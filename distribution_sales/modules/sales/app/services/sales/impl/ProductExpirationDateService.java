package services.sales.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import play.Logger;
import play.libs.Json;
import services.sales.IHttpService;
import services.sales.IProductExpirationDateService;
import util.sales.JsonCaseUtil;
import util.sales.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import dto.sales.ExpirationDateDto;
import dto.sales.SelectedProduct;

public class ProductExpirationDateService implements IProductExpirationDateService {
	@Inject
	private IHttpService httpService;

	@Override
	public Map<String, Object> setMOSelectedProductsExpirationDates(JsonNode node) {
		List<SelectedProduct> cloudInventoryProducts = parseParams(node.get("selectedProducts"));
		// 设置商品到期日期
		List<SelectedProduct> result = setCloudInventoryProductsExpirationDate(node.get("email").asText(),
				cloudInventoryProducts);

		Logger.info("营销单录入设置云仓商品到期日期最终结果：{}", result);

		Map<String, Object> map = Maps.newHashMap();
		map.put("suc", true);
		map.put("result", result);
		return map;
	}

	@Override
	public Map<String, Object> setCloudSelectedProductsExpirationDates(JsonNode node) {
		String email = node.get("email").asText();
		List<SelectedProduct> cloudInventoryProducts = parseParams(node.get("selectedProducts"));
		// 设置商品到期日期
		List<SelectedProduct> result = setCloudInventoryProductsExpirationDate(email, cloudInventoryProducts);

		Logger.info("云仓发货设置云仓商品到期日期最终结果：{}", result);

		Map<String, Object> map = Maps.newHashMap();
		map.put("suc", true);
		map.put("result", result);
		return map;
	}

	@Override
	public Map<String, Object> setSelectedProductsExpirationDates(JsonNode node) {
		String email = node.get("email").asText();
		List<SelectedProduct> selectedProducts = parseParams(node.get("selectedProducts"));

		// 都去查询微仓和云仓库存
		List<SelectedProduct> skusWithSubStock = setCloudInventoryProductsExpirationDate(email, selectedProducts);
		List<SelectedProduct> skusWithSubMicroStock = setMicroInventoryProductsExpirationDate(email, selectedProducts);

		Logger.info("云仓商品经过设置到期日期后：{}", skusWithSubStock);
		Logger.info("微仓商品经过设置到期日期后：{}", skusWithSubMicroStock);
		Map<String, SelectedProduct> spMap = new TreeMap<>();
		// 先把云仓的放进去
		for (SelectedProduct sp : skusWithSubStock) {
			spMap.put(getKey(sp.getSku(), sp.getWarehouseId(), sp.getExpirationDate()), sp);
		}

		String key;
		SelectedProduct spVal;
		for (SelectedProduct sp : skusWithSubMicroStock) {
			key = getKey(sp.getSku(), sp.getWarehouseId(), sp.getExpirationDate());
			spVal = spMap.get(key);
			if (spVal != null) {
				spVal.setSubMicroStock(sp.getSubMicroStock());
			} else {
				spMap.put(key, sp);
			}
		}

		List<SelectedProduct> result = Lists.newArrayList(spMap.values());
		Collections.sort(result, (sp1, sp2) -> {
			return compareSelectedProducts(sp1, sp2);
		});
		;
		Logger.info("云仓商品到期日期+微仓商品到期日期最终有：{}", result);

		// Map<Boolean, List<SelectedProduct>> micro2cloud =
		// selectedProducts.stream().collect(Collectors.partitioningBy(sp->(sp.getMicroStock()>0&&sp.getFromMicroInventory())));
		// List<SelectedProduct> microInventoryProducts =
		// micro2cloud.get(Boolean.TRUE);// 微仓商品
		// List<SelectedProduct> cloudInventoryProducts =
		// micro2cloud.get(Boolean.FALSE);// 云仓商品

		// 设置商品到期日期
		// List<SelectedProduct> result = Lists.newArrayList();
		// result.addAll(setMicroInventoryProductsExpirationDate(email,
		// microInventoryProducts));
		// result.addAll(setCloudInventoryProductsExpirationDate(cloudInventoryProducts));

		Map<String, Object> map = Maps.newHashMap();
		map.put("suc", true);
		map.put("result", result);
		return map;
	}

	/**
	 * 设置云仓商品到期日期
	 * 
	 * @param cloudInventoryProducts
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<SelectedProduct> setCloudInventoryProductsExpirationDate(String email,
			List<SelectedProduct> cloudInventoryProducts) {
		if (cloudInventoryProducts == null || cloudInventoryProducts.size() == 0) {
			return Collections.EMPTY_LIST;
		}
		Logger.info("即将设置到期日期的云仓商品：{}", cloudInventoryProducts);
		List<ObjectNode> skuWarehouseIdNodeList = Lists.newArrayList();
		for (SelectedProduct sp : cloudInventoryProducts) {
			skuWarehouseIdNodeList
					.add(Json.newObject().put("sku", sp.getSku()).put("warehouseId", sp.getWarehouseId()));
		}
		JsonNode resultNode = null;
		try {
			/***** change by zbc KA锁库，替换查询接口 start ******/
			// resultNode =
			// httpService.getCloudProductsExpirationDate(skuWarehouseIdNodeList);
			resultNode = httpService.getCloudProductsExpirationDate(email, skuWarehouseIdNodeList);
			/***** change by zbc KA锁库，替换查询接口 start ******/
		} catch (IOException e) {
			Logger.info("获取云仓商品到期日期失败");
			e.printStackTrace();
			return Collections.EMPTY_LIST;
		}

		Map<String, Set<ExpirationDateDto>> skuWarehouseId2ExpirationDateDtoSet = skuWarehouseId2ExpirationDateDtoSet(
				resultNode);
		if (skuWarehouseId2ExpirationDateDtoSet.size() == 0) {
			Logger.info("没有云仓商品到期日期");
			return Collections.EMPTY_LIST;
		}
		
		// 设置商品保质期
		setProductsExpirationDays(cloudInventoryProducts);

		List<SelectedProduct> result = Lists.newArrayList();
		SelectedProduct newSp;
		for (SelectedProduct sp : cloudInventoryProducts) {
			String skuWarehouseId = getKey(sp.getSku(), sp.getWarehouseId(), null);
			Set<ExpirationDateDto> expirationDateDtoSet = skuWarehouseId2ExpirationDateDtoSet.get(skuWarehouseId);
			if (expirationDateDtoSet != null && expirationDateDtoSet.size() > 0) {
				for (ExpirationDateDto edDto : expirationDateDtoSet) {
					newSp = new SelectedProduct();
					BeanUtils.copyProperties(sp, newSp);
					newSp.setSubStock(edDto.getSubStock());
					newSp.setExpirationDate(edDto.getExpirationDate());
					result.add(newSp);
				}
			}
		}
		return result;
	}

	/**
	 * 设置微仓商品到期日期
	 * 
	 * @param email
	 * @param microInventoryProducts
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<SelectedProduct> setMicroInventoryProductsExpirationDate(String email,
			List<SelectedProduct> microInventoryProducts) {
		Logger.info("即将设置到期日期的微仓商品：{}", microInventoryProducts);
		if (microInventoryProducts == null || microInventoryProducts.size() == 0) {
			return Collections.EMPTY_LIST;
		}
		List<ObjectNode> skuWarehouseIdNodeList = Lists.newArrayList();
		for (SelectedProduct sp : microInventoryProducts) {
			ObjectNode newObject = Json.newObject();
			newObject.put("sku", sp.getSku());
			newObject.put("warehouseId", sp.getWarehouseId());
			skuWarehouseIdNodeList.add(newObject);
		}
		JsonNode resultNode = null;
		try {
			resultNode = httpService.getMicroProductsExpirationDate(email, skuWarehouseIdNodeList);
		} catch (IOException e) {
			Logger.info("获取微仓商品到期日期失败");
			e.printStackTrace();
			return Collections.EMPTY_LIST;
		}

		Map<String, Set<ExpirationDateDto>> skuWarehouseId2ExpirationDateDtoSet = skuWarehouseId2ExpirationDateDtoSet(
				resultNode);
		if (skuWarehouseId2ExpirationDateDtoSet.size() == 0) {
			Logger.info("没有微仓商品到期日期");
			return Collections.EMPTY_LIST;
		}
		
		// 设置商品保质期
		setProductsExpirationDays(microInventoryProducts);

		List<SelectedProduct> result = Lists.newArrayList();
		SelectedProduct newSp;
		for (SelectedProduct sp : microInventoryProducts) {
			String skuWarehouseId = getKey(sp.getSku(), sp.getWarehouseId(), null);
			Set<ExpirationDateDto> expirationDateDtoSet = skuWarehouseId2ExpirationDateDtoSet.get(skuWarehouseId);
			if (expirationDateDtoSet != null && expirationDateDtoSet.size() > 0) {
				for (ExpirationDateDto edDto : expirationDateDtoSet) {
					newSp = new SelectedProduct();
					BeanUtils.copyProperties(sp, newSp);
					newSp.setSubMicroStock(edDto.getSubStock());
					newSp.setExpirationDate(edDto.getExpirationDate());
					result.add(newSp);
				}
			}
		}
		return result;
	}

	private Map<String, Set<ExpirationDateDto>> skuWarehouseId2ExpirationDateDtoSet(JsonNode expirationDateNode) {
		Map<String, Set<ExpirationDateDto>> map = Maps.newHashMap();
		Set<ExpirationDateDto> expirationDtoSet;
		String skuWarehouseId;
		for (Iterator<JsonNode> it = expirationDateNode.iterator(); it.hasNext();) {
			JsonNode nextNode = it.next();
			skuWarehouseId = getKey(nextNode.get("sku").asText(), nextNode.get("warehouseId").asInt(), null);
			expirationDtoSet = map.get(skuWarehouseId);
			if (expirationDtoSet == null) {
				expirationDtoSet = Sets.newHashSet();
				map.put(skuWarehouseId, expirationDtoSet);
			}
			expirationDtoSet
					.add(new ExpirationDateDto(nextNode.get("sku").asText(), nextNode.get("warehouseId").asInt(),
							nextNode.get("expirationDate").asText(), nextNode.get("stock").asInt()));
		}
		return map;
	}

	private List<SelectedProduct> parseParams(JsonNode node) {
		List<SelectedProduct> selectedProducts = Lists.newArrayList();
		SelectedProduct sp;
		for (Iterator<JsonNode> it = node.iterator(); it.hasNext();) {
			JsonNode nextNode = it.next();

			sp = new SelectedProduct();
			sp.setSku(nextNode.get("sku").asText());
			sp.setBatchNumber(nextNode.get("batchNumber").asInt());
			sp.setTitle(nextNode.get("title").asText());
			sp.setInterBarCode(nextNode.get("interBarCode").asText());
			sp.setWarehouseName(nextNode.get("warehouseName").asText());
			sp.setWarehouseId(nextNode.get("warehouseId").asInt());
			sp.setStock(nextNode.get("stock").asInt());
			sp.setMicroStock(nextNode.has("microStock") ? nextNode.get("microStock").asInt() : 0);
			sp.setQty(nextNode.get("qty").asInt());
			sp.setPrice(nextNode.get("price").asDouble());
			sp.setMarketPrice(nextNode.has("marketPrice") ? nextNode.get("marketPrice").asDouble() : null);
			sp.setImgUrl(nextNode.get("imgUrl").asText());
			sp.setIsgift(nextNode.has("isgift") ? nextNode.get("isgift").asBoolean() : null);
			selectedProducts.add(sp);
		}
		return selectedProducts;
	}

	/**
	 * 根据sku、仓库、过期日期排序
	 * 
	 * @param sp1
	 * @param sp2
	 * @return
	 */
	private int compareSelectedProducts(SelectedProduct sp1, SelectedProduct sp2) {
		int result = sp1.getSku().compareTo(sp2.getSku());
		if (result == 0) {// 如果sku一样，比较仓库
			result = sp1.getWarehouseId().compareTo(sp2.getWarehouseId());
			if (result == 0) {// 仓库一样，比较到期日期
				if (StringUtils.isNotBlankOrNull(sp1.getExpirationDate())) {
					result = sp1.getExpirationDate().compareTo(sp2.getExpirationDate());
				} else if (StringUtils.isNotBlankOrNull(sp1.getExpirationDate())) {
					result = sp2.getExpirationDate().compareTo(sp1.getExpirationDate());
				} else {// 如果两个的到期一样，就说明sku、仓库、到期时间一样，两个是同一样的商品
					result = 0;
				}
			}
		}
		return result;
	}
	
	/**
	 * 设置商品的保质期
	 * @param products
	 */
	private void setProductsExpirationDays(List<SelectedProduct> products) {
		// 检查是否有必要设置保质期
		if (CollectionUtils.isEmpty(products) || 
				products.stream().filter(e->e.getExpirationDays()==null).count()<1) {
			return;
		}
		
		JsonNode dataNode = null;
		try {
			dataNode = httpService.getProducts(null, Lists.transform(products, e->e.getSku()), products.get(0).getWarehouseId(), null);
		} catch (IOException e) {
			Logger.info("设置商品的保质期失败, {}", e);
		}
		if (dataNode != null) {
			HashMap<String, Integer> sku2ExpirationDays = Maps.newHashMap();
			for(Iterator<JsonNode> it = dataNode.path("data").path("result").iterator();it.hasNext();){
				JsonNode nextNode = it.next();
				sku2ExpirationDays.put(nextNode.get("csku").asText(), JsonCaseUtil.getIntegerValue(nextNode, "expirationDays"));
			}
			if (sku2ExpirationDays.size()>0) {
				for (SelectedProduct pro : products) {
					pro.setExpirationDays(sku2ExpirationDays.get(pro.getSku()));
				}
			}
		}
	}

	private String getKey(String sku, Integer warehouseId, String expirationDate) {
		String key = sku + "_" + warehouseId.toString();
		if (StringUtils.isNotBlankOrNull(expirationDate)) {
			key = key + "_" + expirationDate;
		}
		return key;
	}
}
