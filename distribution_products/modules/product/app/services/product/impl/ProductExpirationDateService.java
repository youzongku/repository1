package services.product.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import dto.product.ExpirationDateDto;
import dto.product.ProductLite;
import play.Logger;
import play.libs.Json;
import services.product.IHttpService;
import services.product.IProductExpirationDateService;
import util.product.KeyUtil;

public class ProductExpirationDateService implements IProductExpirationDateService {
	@Inject private IHttpService httpService;
	
	@Override
	public Map<String, List<ProductLite>> setCloudSelectedProductsExpirationDates(List<ProductLite> proList){
		List<Map<String,Object>> list = Lists.newArrayList();
		for (ProductLite pl : proList) {
			Map<String,Object> map = Maps.newHashMap();
			map.put("sku", pl.getCsku());
			map.put("warehouseId", pl.getWarehouseId());
			list.add(map);
		}
		
		// 设置商品到期日期
		Map<String, List<ProductLite>> result = setCloudInventoryProductsExpirationDate(proList, list);
		Logger.info("云仓发货设置云仓商品到期日期最终结果：{}",result);
		return result;
	}

	/**
	 * 设置云仓商品到期日期
	 * 
	 * @param cloudInventoryProducts
	 * @return
	 */
	private Map<String, List<ProductLite>> setCloudInventoryProductsExpirationDate(List<ProductLite> proList, List<Map<String,Object>> list) {
		if(list==null || list.size()==0){
			return Maps.newHashMap();
		}
		Logger.info("即将设置到期日期的云仓商品：{}",list);
		List<ObjectNode> skuWarehouseIdNodeList = Lists.newArrayList();
		for(Map<String,Object> map : list){
			ObjectNode newObject = Json.newObject();
			newObject.put("sku", (String)map.get("sku"));
			newObject.put("warehouseId", (Integer)map.get("warehouseId"));
			skuWarehouseIdNodeList.add(newObject);
		}
		JsonNode resultNode = null;
		try {
			resultNode = httpService.getCloudProductsExpirationDate(skuWarehouseIdNodeList);
		} catch (IOException e) {
			Logger.info("获取云仓商品到期日期失败");
			e.printStackTrace();
			return Maps.newHashMap();
		}
		
		Map<String, Set<ExpirationDateDto>> skuWarehouseId2ExpirationDateDtoSet = skuWarehouseId2ExpirationDateDtoSet(resultNode);
		if(skuWarehouseId2ExpirationDateDtoSet.size()==0){
			Logger.info("没有云仓商品到期日期");
			return Maps.newHashMap();
		}
		
		// 循环处理
		Map<String, List<ProductLite>> result = Maps.newHashMap();
		ProductLite newPL;
		for(ProductLite pl : proList){
			String skuWarehouseId = KeyUtil.getKey(pl.getCsku(),pl.getWarehouseId());
			Set<ExpirationDateDto> expirationDateDtoSet = skuWarehouseId2ExpirationDateDtoSet.get(skuWarehouseId);
			// 按照到期日期进行拆分
			if(expirationDateDtoSet!=null && expirationDateDtoSet.size()>0){
				List<ProductLite> proListWithExpirationDate = Lists.newArrayList();
				for(ExpirationDateDto edDto : expirationDateDtoSet){
					newPL = new ProductLite();
					BeanUtils.copyProperties(pl, newPL);
					// 重新覆盖库存
					newPL.setStock(edDto.getSubStock());
					// 到期日期
					newPL.setExpirationDate(edDto.getExpirationDate());
					proListWithExpirationDate.add(newPL);
				}
				result.put(skuWarehouseId, proListWithExpirationDate);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param expirationDateNode
	 * @return {skuWarehouseId = Set&lt;ExpirationDateDto&gt;, ...}
	 */
	private Map<String,Set<ExpirationDateDto>> skuWarehouseId2ExpirationDateDtoSet(
			JsonNode expirationDateNode) {
		Map<String,Set<ExpirationDateDto>> map = Maps.newHashMap();
		Set<ExpirationDateDto> expirationDtoSet;
		String skuWarehouseId;
		for (Iterator<JsonNode> it = expirationDateNode.iterator(); it
				.hasNext();) {
			JsonNode nextNode = it.next();
			skuWarehouseId = KeyUtil.getKey(nextNode.get("sku").asText(),nextNode.get("warehouseId").asInt());
			expirationDtoSet = map.get(skuWarehouseId);
			if(expirationDtoSet==null){
				expirationDtoSet = Sets.newHashSet();
				map.put(skuWarehouseId, expirationDtoSet);
			}
			expirationDtoSet.add(new ExpirationDateDto(nextNode.get("sku")
					.asText(), nextNode.get("warehouseId").asInt(), nextNode
					.get("expirationDate").asText(), nextNode.get("stock")
					.asInt()));
		}
		return map;
	}

}
