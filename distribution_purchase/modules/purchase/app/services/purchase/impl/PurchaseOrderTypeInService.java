package services.purchase.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mapper.purchase.PurchaseOrderInputGiftMapper;
import mapper.purchase.PurchaseOrderInputMapper;
import mapper.purchase.PurchaseOrderInputProMapper;

import org.springframework.beans.BeanUtils;

import play.Logger;
import play.libs.Json;
import services.purchase.IHttpService;
import services.purchase.IPurchaseOrderTypeInService;
import utils.purchase.JsonCaseUtil;
import utils.purchase.PriceFormatUtil;
import utils.purchase.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import dto.purchase.ExpirationDateDto;
import dto.purchase.PurchaseOrderInputDto;
import entity.purchase.PurchaseOrderInput;
import entity.purchase.PurchaseOrderInputGift;
import entity.purchase.PurchaseOrderInputPro;
import forms.purchase.GetProductsParams;

public class PurchaseOrderTypeInService implements IPurchaseOrderTypeInService {
	@Inject private PurchaseOrderInputMapper purchaseOrderInputMapper;
	@Inject private PurchaseOrderInputProMapper purchaseOrderInputProMapper;
	@Inject private PurchaseOrderInputGiftMapper purchaseOrderInputGiftMapper;
	@Inject private IHttpService httpService;

	@Override
	public boolean addOrUpdateMain(PurchaseOrderInput input) {
		int count=0;
		if(input.getId()==null){
			// 新增之前，要把旧的删掉，防止重复
			purchaseOrderInputMapper.deleteInputByParam(input);
			count=purchaseOrderInputMapper.insert(input);
		}else{
			count=purchaseOrderInputMapper.updateByPrimaryKeySelective(input);
		}
		return count==1;
	}
	
	@Override
	public void addProducts(Integer inputId, boolean needExpirationDate,
			List<PurchaseOrderInputPro> inputProList) {
		PurchaseOrderInput input = purchaseOrderInputMapper.selectByPrimaryKey(inputId);
		// 新增正价商品，此时还没有赠品，要设置主表id
		for (PurchaseOrderInputPro pro : inputProList) {
			pro.setInputId(inputId);
			pro.setNeedExpirationDate(needExpirationDate);
		}

		// 数据库里的记录
		List<PurchaseOrderInputPro> inputProListExisted = purchaseOrderInputProMapper
				.selectByInputId(inputId);

		if (inputProListExisted != null && inputProListExisted.size() > 0) {
			if(needExpirationDate){
				// 设置到期日期
				inputProList = setInputProExpirationDate(input.getDisAccount(),inputProList);
			}
			// true的部分：在数据库中有记录，但是没有赠品，可以进行合并，即数量要增加
			// false的部分：在数据库中没有有记录/或有记录，但是有赠品，需要直接插入到数据库中
			Map<Boolean, List<PurchaseOrderInputPro>> noGiftsToInputPros = inputProList.stream()
					.collect(Collectors.partitioningBy(aPro -> checkProExisted(inputProListExisted, aPro)));
			List<PurchaseOrderInputPro> toMergeInputProList = noGiftsToInputPros
					.get(Boolean.TRUE);
			List<PurchaseOrderInputPro> toInsertInputProList = noGiftsToInputPros
					.get(Boolean.FALSE);
			// 合并部分
			if (toMergeInputProList != null && toMergeInputProList.size() > 0) {
				// 设置要更新的正价商品的数量
				List<PurchaseOrderInputPro> toUpdateInputProListExisted = new ArrayList<>();
				for (PurchaseOrderInputPro aToUpdateInputPro : toMergeInputProList) {
					// 从数据库记录里拿到要更新的记录，修改数量
					PurchaseOrderInputPro inputProExisted = getInputProFromExistedList(inputProListExisted,
							aToUpdateInputPro);
					if (inputProExisted != null) {
						int newQty = aToUpdateInputPro.getQty() + inputProExisted.getQty();
						inputProExisted.setQty(newQty);
						toUpdateInputProListExisted.add(inputProExisted);
					}
				}
				Logger.info("更新部分的正价商品为：{}",toUpdateInputProListExisted);
				purchaseOrderInputProMapper.batchUpdateProQtyAndNeedExpirationDate(toUpdateInputProListExisted);
			}
			// 新增部分
			if (toInsertInputProList != null && toInsertInputProList.size() > 0) {
				Logger.info("新增部分的正价商品为：{}",toInsertInputProList);
				purchaseOrderInputProMapper.insertBatch(toInsertInputProList);
			}
		} else {
			if(needExpirationDate){
				// 设置到期日期
				inputProList = setInputProExpirationDate(input.getDisAccount(),inputProList);
			}
			Logger.info("新增部分的正价商品为：{}",inputProList);
			// 全部插入
			purchaseOrderInputProMapper.insertBatch(inputProList);
		}
	}

	/**
	 * 数据库记录里拿到数据（sku&仓库id&到期日期相同，且数据库里的这条记录没有赠品）
	 * 
	 * @param inputProListExisted
	 * @param newInputPro
	 * @return 没有，返回null
	 */
	private PurchaseOrderInputPro getInputProFromExistedList(
			List<PurchaseOrderInputPro> inputProListExisted,
			PurchaseOrderInputPro newInputPro) {
		List<PurchaseOrderInputPro> list = inputProListExisted.stream().filter(aPro -> {
			// 判断sku、仓库和到期日期是否一致
			return checkProductEquals(newInputPro.getSku(), newInputPro.getWarehouseId(),
					newInputPro.getExpirationDate(), aPro.getSku(), aPro.getWarehouseId(), aPro.getExpirationDate());
		}).collect(Collectors.toList());

		if (list.size() == 1) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * 检查newInputPro是否在数据库中有记录（sku&仓库id相同，且数据库里的这条记录没有赠品）
	 * 
	 * @param inputProListExisted
	 *            数据库中的记录
	 * @param newInputPro
	 * @return
	 */
	private boolean checkProExisted(
			List<PurchaseOrderInputPro> inputProListExisted,
			PurchaseOrderInputPro newInputPro) {
		if (inputProListExisted != null && inputProListExisted.size() > 0) {
			return inputProListExisted.stream().anyMatch(aPro -> {
				// 判断sku、仓库和到期日期是否一致
				return checkProductEquals(newInputPro.getSku(),
						newInputPro.getWarehouseId(),
						newInputPro.getExpirationDate(), aPro.getSku(),
						aPro.getWarehouseId(), aPro.getExpirationDate());
			});
		}
		return false;
	}
	
	private JsonNode getInputProExpirationDates(String disAccount,List<PurchaseOrderInputPro> inputProList){
		// 批量查询到期日期
		List<ObjectNode> skuWarehouseIdNodeList = Lists.newArrayList();
		ObjectNode newObject;
		for (PurchaseOrderInputPro aPro : inputProList) {
			newObject = Json.newObject();
			newObject.put("sku", aPro.getSku());
			newObject.put("warehouseId", aPro.getWarehouseId());
			skuWarehouseIdNodeList.add(newObject);
		}
		return getExpirationDates(disAccount,skuWarehouseIdNodeList);
	}

	// 设置到期日期
	private List<PurchaseOrderInputPro> setInputProExpirationDate(String disAccount,List<PurchaseOrderInputPro> inputProList) {
		JsonNode expirationDateNode = getInputProExpirationDates(disAccount,inputProList);
		Map<String, Set<String>> skuWarehouseId2ExpirationDates = convertExpirationResult2Map(expirationDateNode);

		List<PurchaseOrderInputPro> newProList = Lists.newArrayList();
		PurchaseOrderInputPro pro;
		String skuWarehouseId;
		for (PurchaseOrderInputPro aPro : inputProList) {
			skuWarehouseId = String.join("_", aPro.getSku(), String.valueOf(aPro.getWarehouseId()));
			// 获取到对应的到期日期集合
			Set<String> expirationDateSet = skuWarehouseId2ExpirationDates.get(skuWarehouseId);
			if(expirationDateSet==null){
				// 此正价商品没有对应的到期日期
				continue;
			}
			for (String expirationDate : expirationDateSet) {
				pro = new PurchaseOrderInputPro();
				BeanUtils.copyProperties(aPro, pro);
				pro.setExpirationDate(expirationDate);
				newProList.add(pro);
			}
		}
		return newProList;
	}
	
	private JsonNode getExpirationDates(String disAccount,List<ObjectNode> skuWarehouseIdNodeList){
		JsonNode expirationDateNode;
		try {
			expirationDateNode = httpService.getCloudProductsExpirationDate(disAccount,skuWarehouseIdNodeList);
		} catch (IOException e) {
			Logger.info("获取商品到期日期失败，参数为：skuWarehouseIdNodeList={}", skuWarehouseIdNodeList);
			throw new RuntimeException(e);
		}
		return expirationDateNode;
	}

	@Override
	public void addGifts(Integer inputId, boolean needExpirationDate, List<PurchaseOrderInputGift> inputGiftList) {
		PurchaseOrderInput input = purchaseOrderInputMapper.selectByPrimaryKey(inputId);
		// 新增赠品，要设置主表id
		for (PurchaseOrderInputGift gift : inputGiftList) {
			gift.setInputId(inputId);
			gift.setNeedExpirationDate(needExpirationDate);
		}

		// 数据库里的记录
		List<PurchaseOrderInputGift> inputGiftListExisted = purchaseOrderInputGiftMapper
				.selectByInputId(inputId);

		if (inputGiftListExisted != null && inputGiftListExisted.size() > 0) {
			if(needExpirationDate){
				// 设置到期日期
				inputGiftList = setInputGiftExpirationDate(input.getDisAccount(),inputGiftList);
			}
			// 根据在数据库中是否存在来分组
			Map<Boolean, List<PurchaseOrderInputGift>> giftExistedOrNot = inputGiftList.stream()
					.collect(Collectors.partitioningBy(aGift -> checkGiftExists(inputGiftListExisted, aGift)));
			// true存在，false不存在
			List<PurchaseOrderInputGift> toMergeInputGiftList = giftExistedOrNot.get(Boolean.TRUE);
			List<PurchaseOrderInputGift> toInsertInputGiftList = giftExistedOrNot.get(Boolean.FALSE);

			if (toMergeInputGiftList != null && toMergeInputGiftList.size() > 0) {
				// 设置要更新的正价商品的数量
				List<PurchaseOrderInputGift> toUpdateInputGiftListExisted = new ArrayList<>();
				for (PurchaseOrderInputGift aToUpdateInputGift : toMergeInputGiftList) {
					// 从数据库记录里拿到要更新的记录，修改数量
					PurchaseOrderInputGift inputGiftExisted = getInputGiftFromExistedList(inputGiftListExisted,
							aToUpdateInputGift);
					if (inputGiftExisted != null) {
						inputGiftExisted.setQty(aToUpdateInputGift.getQty() + inputGiftExisted.getQty());
						toUpdateInputGiftListExisted.add(inputGiftExisted);
					}
				}
				Logger.info("赠品部分更新：{}",toUpdateInputGiftListExisted);
				purchaseOrderInputGiftMapper
						.batchUpdateGiftQtyAndNeedExpirationDate(toUpdateInputGiftListExisted);
			}
			if (toInsertInputGiftList != null
					&& toInsertInputGiftList.size() > 0) {
				if(needExpirationDate){
					// 设置到期日期
					toInsertInputGiftList = setInputGiftExpirationDate(input.getDisAccount(),toInsertInputGiftList);
				}
				Logger.info("赠品部分新增：{}",toInsertInputGiftList);
				purchaseOrderInputGiftMapper.insertBatch(toInsertInputGiftList);
			}
		} else {
			if(needExpirationDate){
				// 设置到期日期
				inputGiftList = setInputGiftExpirationDate(input.getDisAccount(),inputGiftList);
			}
			Logger.info("赠品全部新增：{}",inputGiftList);
			purchaseOrderInputGiftMapper.insertBatch(inputGiftList);
		}
	}

	/**
	 * 数据库记录里拿到数据（sku&仓库id&到期日期相同，且数据库里的这条记录没有赠品）
	 * 
	 * @param inputGiftListExisted
	 * @param newInputGift
	 * @return 没有，返回null
	 */
	private PurchaseOrderInputGift getInputGiftFromExistedList(
			List<PurchaseOrderInputGift> inputGiftListExisted,
			PurchaseOrderInputGift newInputGift) {
		List<PurchaseOrderInputGift> list = inputGiftListExisted.stream().filter(aGift -> {
			// 判断sku、仓库和到期日期是否一致
			return checkProductEquals(newInputGift.getSku(), newInputGift.getWarehouseId(),
					newInputGift.getExpirationDate(), aGift.getSku(), aGift.getWarehouseId(),
					aGift.getExpirationDate());
		}).collect(Collectors.toList());

		if (list.size() == 1) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * 检查赠品是否在列表中存在
	 * 
	 * @param giftList
	 * @param newInputGift
	 * @return
	 */
	private boolean checkGiftExists(List<PurchaseOrderInputGift> giftList,
			PurchaseOrderInputGift newInputGift) {
		if (giftList != null && giftList.size() > 0) {
			return giftList.stream().anyMatch(aGift -> {
				return checkProductEquals(newInputGift.getSku(), newInputGift.getWarehouseId(),
						newInputGift.getExpirationDate(), aGift.getSku(), aGift.getWarehouseId(),
						aGift.getExpirationDate());
			});
		}
		return false;
	}
	
	private JsonNode getInputGiftExpirationDates(String disAccount,List<PurchaseOrderInputGift> inputGiftList){
		// 批量查询到期日期
		List<ObjectNode> skuWarehouseIdNodeList = Lists.newArrayList();
		ObjectNode newObject;
		for (PurchaseOrderInputGift aGift : inputGiftList) {
			newObject = Json.newObject();
			newObject.put("sku", aGift.getSku());
			newObject.put("warehouseId", aGift.getWarehouseId());
			skuWarehouseIdNodeList.add(newObject);
		}
		return getExpirationDates(disAccount,skuWarehouseIdNodeList);
	}

	// 设置赠品到期日期
	private List<PurchaseOrderInputGift> setInputGiftExpirationDate(String disAccount,List<PurchaseOrderInputGift> inputGiftList) {
		JsonNode expirationDateNode = getInputGiftExpirationDates(disAccount,inputGiftList);
		Map<String, Set<String>> skuWarehouseId2ExpirationDates = convertExpirationResult2Map(expirationDateNode);
		List<PurchaseOrderInputGift> newGiftList = Lists.newArrayList();
		PurchaseOrderInputGift gift;
		String skuWarehouseId;
		for (PurchaseOrderInputGift aGift : inputGiftList) {
			skuWarehouseId = String.join("_", aGift.getSku(), String.valueOf(aGift.getWarehouseId()));
			// 获取到对应的到期日期集合
			Set<String> expirationDateSet = skuWarehouseId2ExpirationDates.get(skuWarehouseId);
			if (expirationDateSet == null) {
				// 此赠品没有对应的到期日期
				continue;
			}
			for (String expirationDate : expirationDateSet) {
				gift = new PurchaseOrderInputGift();
				BeanUtils.copyProperties(aGift, gift);
				gift.setExpirationDate(expirationDate);
				newGiftList.add(gift);
			}
		}
		return newGiftList;
	}

	/**
	 * 检查两个商品是否一致：sku1等于sku2 && warehouseId1等于warehouseId2 &&
	 * expirationDate1等于expirationDate2
	 * 
	 * @param sku1 商品1 sku
	 * @param warehouseId1 商品1仓库id
	 * @param expirationDate1 商品1到期日期
	 * @param sku2 商品2 sku
	 * @param warehouseId2 商品2仓库id
	 * @param expirationDate2 商品2到期日期
	 * @return
	 */
	private boolean checkProductEquals(String sku1, int warehouseId1,
			String expirationDate1, String sku2, int warehouseId2,
			String expirationDate2) {
		Logger.info("sku1={},warehouseId1={},expirationDate1={},sku2={},warehouseId2={},expirationDate2={},",
				sku1,warehouseId1,expirationDate1,sku2,warehouseId2,expirationDate2);
		return (StringUtils.isNotBlankOrNull(sku1) && sku1.equals(sku2))
				&& ((expirationDate1 == null && expirationDate2 == null)
						|| (StringUtils.isNotBlankOrNull(expirationDate1) && expirationDate1.equals(expirationDate2)))
				&& warehouseId1 == warehouseId2;
	}

	/**
	 * 一个sku、warehouseId对应多个到期日期。key是[sku_warehouseId]
	 * 
	 * @param expirationDateNode
	 * @return
	 */
	private Map<String, Set<String>> convertExpirationResult2Map(
			JsonNode expirationDateNode) {
		Map<String, Set<String>> skuWarehouseId2ExpirationDates = Maps.newHashMap();
		String key;
		Set<String> values;
		for (Iterator<JsonNode> it = expirationDateNode.iterator(); it.hasNext();) {
			JsonNode nextNode = it.next();
			key = String.join("_", nextNode.get("sku").asText(), String.valueOf(nextNode.get("warehouseId").asInt()));
			values = skuWarehouseId2ExpirationDates.get(key);
			if (values == null) {
				values = new HashSet<String>();
				skuWarehouseId2ExpirationDates.put(key, values);
			}
			values.add(nextNode.get("expirationDate").asText());

		}
		return skuWarehouseId2ExpirationDates;
	}
	
	@Override
	public void deleteProducts(String ids) {
		// 正价商品id
		List<Integer> proIdList = Arrays.asList(ids.split(",")).stream().map(Integer::valueOf)
				.collect(Collectors.toList());
		// 删除正品信息
		purchaseOrderInputProMapper.deleteByIdList(proIdList);
	}

	@Override
	public void deleteGifts(String ids) {
		List<Integer> giftIdList = Arrays.asList(ids.split(",")).stream().map(Integer::valueOf)
				.collect(Collectors.toList());
		// 删除赠品
		purchaseOrderInputGiftMapper.deleteByIdList(giftIdList);
	}
	
	@Override
	public void deleteAllProsAndGiftsByInputId(int inputId) {
		// 删除正品信息
		purchaseOrderInputProMapper.deleteByInputId(inputId);
		// 删除赠品
		purchaseOrderInputGiftMapper.deleteByInputId(inputId);
	}
	
	private Double getDisPrice(JsonNode pro) {
		Double purchase = pro.get("disPrice").asDouble();
		if (pro.get("isSpecial") != null && pro.get("isSpecial").asBoolean()) {
			purchase = pro.get("specialSale").asDouble();
		}
		return PriceFormatUtil.toFix2(purchase);
	}

	private Double getDisPriceForKa(JsonNode pro) {
		Double purchase = pro.get("disPrice").asDouble();
		if (pro.get("isSpecial") != null && pro.get("isSpecial").asBoolean()) {
			purchase = pro.get("specialSale").asDouble();
		}
		return PriceFormatUtil.toFix4(purchase);
	}

	@Override
	public int updateProQty(int proId, int qty) {
		return purchaseOrderInputProMapper.updateProQty(proId, qty);
	}
	
	@Override
	public int updateGiftQty(int giftId, int qty) {
		return purchaseOrderInputGiftMapper.updateGiftQty(giftId, qty);
	}
	
	@Override
	public PurchaseOrderInputDto getPurchaseOrderInputDto(int inputId) {
		// 主表数据
		PurchaseOrderInput orderInput = purchaseOrderInputMapper
				.selectByPrimaryKey(inputId);

		PurchaseOrderInputDto dto = commonGetPurchaseOrderInputDto(orderInput);
		return dto;
	}

	@Override
	public PurchaseOrderInputDto getPurchaseOrderInputDto(String inputUser) {
		List<PurchaseOrderInput> orderInputList = purchaseOrderInputMapper
				.selectByInputType(PurchaseOrderInput.INPUT_TYPE_TYPE_IN, inputUser);
		// 主表数据
		if (orderInputList.size() > 0) {
			PurchaseOrderInput orderInput = orderInputList.get(0);
			PurchaseOrderInputDto dto = commonGetPurchaseOrderInputDto(orderInput);
			return dto;
		}
		return null;
	}

	/**
	 * 获取录入的数据，没有对应关系的
	 * 
	 * @param orderInput
	 *            主表信息
	 * @return
	 */
	private PurchaseOrderInputDto commonGetPurchaseOrderInputDto(
			PurchaseOrderInput orderInput) {
		if(orderInput==null){
			return null;
		}
		// 正价商品
		List<PurchaseOrderInputPro> inputProList = purchaseOrderInputProMapper.selectByInputId(orderInput.getId());
		// 赠品
		List<PurchaseOrderInputGift> giftList = purchaseOrderInputGiftMapper.selectByInputId(orderInput.getId());

		setProAndGiftOtherPropertiesValue(orderInput.getDisAccount(),orderInput.getDisMode(), inputProList, giftList);

		PurchaseOrderInputDto dto = new PurchaseOrderInputDto();
		dto.setDisAccount(orderInput.getDisAccount());
		dto.setInputId(orderInput.getId());
		dto.setInputUser(orderInput.getInputUser());
		dto.setDisMode(orderInput.getDisMode());
		dto.setInputType(orderInput.getInputType());
		dto.setProList(inputProList);// 正价商品
		dto.setGiftList(giftList);// 赠品

		return dto;
	}
	
	private Map<String,Set<ExpirationDateDto>> skuWarehouseId2ExpirationDateDtoSet(
			JsonNode expirationDateNode) {
		Map<String,Set<ExpirationDateDto>> map = Maps.newHashMap();
		Set<ExpirationDateDto> expirationDtoSet;
		for (Iterator<JsonNode> it = expirationDateNode.iterator(); it.hasNext();) {
			JsonNode nextNode = it.next();
			String key = String.join("_", nextNode.get("sku").asText(),
					String.valueOf(nextNode.get("warehouseId").asInt()));
			expirationDtoSet = map.get(key);
			if (expirationDtoSet == null) {
				expirationDtoSet = Sets.newHashSet();
				map.put(key, expirationDtoSet);
			}
			expirationDtoSet
					.add(new ExpirationDateDto(nextNode.get("sku").asText(), nextNode.get("warehouseId").asInt(),
							nextNode.get("expirationDate").asText(), nextNode.get("stock").asInt()));
		}
		return map;
	}
	
	/**
	 * 设置正价商品的部分属性的属性值
	 * 
	 * @param inputProList
	 * @param disMode
	 */
	private void setProAndGiftOtherPropertiesValue(
			String disAccount,
			int disMode,
			List<PurchaseOrderInputPro> inputProList,
			List<PurchaseOrderInputGift> inputGiftList) {
		if((inputProList==null || inputProList.size()==0) && (inputGiftList==null || inputGiftList.size()==0)){
			return;
		}
		
		// 1、查询商品信息
		Map<Integer,Set<String>> warehouseId2Skus = Maps.newHashMap();
		GetProductsParams getProductsParams;
		for (PurchaseOrderInputPro pro : inputProList) {
			if(warehouseId2Skus.get(pro.getWarehouseId())==null){
				warehouseId2Skus.put(pro.getWarehouseId(), Sets.newHashSet());
			}
			warehouseId2Skus.get(pro.getWarehouseId()).add(pro.getSku());
		}
		for (PurchaseOrderInputGift gift : inputGiftList) {
			if(warehouseId2Skus.get(gift.getWarehouseId())==null){
				warehouseId2Skus.put(gift.getWarehouseId(), Sets.newHashSet());
			}
			warehouseId2Skus.get(gift.getWarehouseId()).add(gift.getSku());
		}
		
		Map<String,JsonNode> skuWarehouseId2JsonNode = Maps.newHashMap();
		JsonNode dataNode;
		try {
			for(Map.Entry<Integer,Set<String>> entry : warehouseId2Skus.entrySet()){
				getProductsParams = new GetProductsParams();
			 	getProductsParams.setSkuList(Lists.newArrayList(entry.getValue()));
				getProductsParams.setModel(disMode);
				getProductsParams.setWarehouseId(entry.getKey());
				dataNode = httpService.getProducts(getProductsParams);
				if (dataNode != null) {
					for(Iterator<JsonNode> it = dataNode.path("data").path("result").iterator();it.hasNext();){
						JsonNode nextNode = it.next();
						String key = String.join("_", nextNode.get("csku").asText(),nextNode.get("warehouseId").asText());
						skuWarehouseId2JsonNode.put(key, nextNode);
					}
				}
			}
		} catch (IOException e) {
			Logger.info("获取商品价格、仓库等信息失败");
			throw new RuntimeException(e);
		}
		
		// 没有获取到
		if(skuWarehouseId2JsonNode.size()==0){
			return;
		}
		
		// 检查是否有需要设置到期日期的商品
		long needExpirationDateCount = 0;
		needExpirationDateCount += inputProList.stream().filter(aPro->aPro.getNeedExpirationDate()).count();
		needExpirationDateCount += inputGiftList.stream().filter(aGift->aGift.getNeedExpirationDate()).count();
		Map<String,Set<ExpirationDateDto>> skuWarehouseId2ExpirationDateDtoSet = Maps.newHashMap();
		if(needExpirationDateCount > 0 ){
			// 过滤相同的sku_warehouseId
			Set<String> skuWarehouseIdSet = Sets.newHashSet();
			for (PurchaseOrderInputPro aPro : inputProList) {
				skuWarehouseIdSet.add(String.join("_", aPro.getSku(),aPro.getWarehouseId().toString()));
			}
			for (PurchaseOrderInputGift aGift : inputGiftList) {
				skuWarehouseIdSet.add(String.join("_", aGift.getSku(),aGift.getWarehouseId().toString()));
			}
			
			List<ObjectNode> skuWarehouseIdNodeList = Lists.newArrayList();
			ObjectNode newObject;
			String[] skuWarehouseIdArray;
			for (String skuWarehouseId : skuWarehouseIdSet) {
				newObject = Json.newObject();
				skuWarehouseIdArray = skuWarehouseId.split("_");
				newObject.put("sku", skuWarehouseIdArray[0]);
				newObject.put("warehouseId", skuWarehouseIdArray[1]);
				skuWarehouseIdNodeList.add(newObject);
			}
			// 批量获取到期日期
			skuWarehouseId2ExpirationDateDtoSet.putAll(skuWarehouseId2ExpirationDateDtoSet(getExpirationDates(disAccount,skuWarehouseIdNodeList)));
		}
		
		// 3、设置正价商品/赠品额外商品信息
		if (inputProList.size() > 0) {
			for (PurchaseOrderInputPro pro : inputProList) {
				String key = String.join("_", pro.getSku(), pro.getWarehouseId().toString());
				JsonNode productNode = skuWarehouseId2JsonNode.get(key);
				if(productNode!=null){
					pro.setTitle(productNode.path("ctitle").asText());
					pro.setStock(productNode.path("stock").asInt());
					pro.setWarehouseName(productNode.path("warehouseName").asText());
					pro.setBatchNumber(productNode.path("batchNumber").asInt());
					pro.setInterBarCode(productNode.path("interBarCode").asText());
					pro.setExpirationDays(JsonCaseUtil.jsonToInteger(productNode.path("expirationDays")));
					// 要判断是否有特价
					Double disPrice;
					if (disMode == 3) {//ka分销商
						disPrice = getDisPriceForKa(productNode);
					} else {
						disPrice = getDisPrice(productNode);
					}
					pro.setPrice(disPrice);
					pro.setImageUrl(productNode.path("imageUrl").asText());
				}
				
				if(pro.getNeedExpirationDate()){
					// 到期日期
					pro.setExpirationDateDtoSet(skuWarehouseId2ExpirationDateDtoSet.get(key));
				}
			}
		}
		
		if (inputGiftList.size() > 0) {
			for (PurchaseOrderInputGift gift : inputGiftList) {
				String key = String.join("_", gift.getSku(), gift.getWarehouseId().toString());
				JsonNode productNode = skuWarehouseId2JsonNode.get(key);
				if(productNode!=null){
					gift.setTitle(productNode.path("ctitle").asText());
					gift.setStock(productNode.path("stock").asInt());
					gift.setWarehouseName(productNode.path("warehouseName").asText());
					gift.setBatchNumber(productNode.path("batchNumber").asInt());
					gift.setInterBarCode(productNode.path("interBarCode").asText());
					gift.setExpirationDays(JsonCaseUtil.jsonToInteger(productNode.path("expirationDays")));
					// 要判断是否有特价
					Double disPrice = getDisPrice(productNode);
					gift.setPrice(disPrice);
					gift.setImageUrl(productNode.path("imageUrl").asText());
				}
				
				if(gift.getNeedExpirationDate()){
					// 到期日期
					gift.setExpirationDateDtoSet(skuWarehouseId2ExpirationDateDtoSet.get(key));
				}
			}
		}
	}

	@Override
	public PurchaseOrderInput getPurchaseOrderInput(String inputUser) {
		List<PurchaseOrderInput> orderInputList = purchaseOrderInputMapper
				.selectByInputType(PurchaseOrderInput.INPUT_TYPE_TYPE_IN,
						inputUser);
		// 主表数据
		if (orderInputList.size() > 0) {
			return orderInputList.get(0);
		}
		return null;
	}
	
	@Override
	public PurchaseOrderInput getPurchaseOrderInput(int inputId) {
		return purchaseOrderInputMapper.selectByPrimaryKey(inputId);
	}

	@Override
	public List<PurchaseOrderInputPro> getCheckedInputPros(int inputId) {
		return purchaseOrderInputProMapper.selectByChecked(inputId, true);
	}

	@Override
	public void updateChecked(int inputId, String proIds) {
		// 先将inputId下的所有正价商品的checked设置为false，然后再设置为true
		purchaseOrderInputProMapper.updateChecked(inputId, null, false);

		if (StringUtils.isNotBlankOrNull(proIds)) {
			List<Integer> proIdList = Stream.of(proIds.split(",")).map(Integer::valueOf).collect(Collectors.toList());
			purchaseOrderInputProMapper.updateChecked(inputId, proIdList, true);
		}
	}
}
