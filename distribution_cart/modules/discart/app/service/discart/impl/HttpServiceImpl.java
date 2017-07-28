package service.discart.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import dto.discart.OrderDetail;
import dto.discart.ProSearch;
import play.Logger;
import play.libs.Json;
import service.discart.IHttpService;
import utils.discart.HttpUtil;

public class HttpServiceImpl implements IHttpService {
	
	@Override
	public String getProStock(List<String> skus) throws Exception {
		HashMap<String,Object> param = Maps.newHashMap();
		param.put("skus", skus);
		Logger.info("查询库存参数:[{}]",param);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/inventory/cloud/searchProductCloudAndMicroInventory");
		Logger.info("查询库存结果:[{}]",resultString);
		return resultString;
	}
	
	@Override
	public JsonNode fgetProducts(ProSearch search) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("email", search.getEmail());
		param.put("istatus", 1);
		param.put("skuList", search.getSkuList());
		param.put("warehouseId", search.getWarehouseId());
		param.put("model", search.getModel());
		Map<String, Object> params = Maps.newHashMap();
		params.put("data", param);
		String resultString = HttpUtil.post(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/product/api/fgetProducts");
		return parseString(resultString);
	}
	
	@Override
	public JsonNode getProducts(ProSearch search) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("email", search.getEmail());
		param.put("istatus", 1);
		param.put("skuList", search.getSkuList());
		param.put("warehouseId", search.getWarehouseId());
		param.put("model", search.getModel());
		Map<String, Object> params = Maps.newHashMap();
		params.put("data", param);
		String resultString = HttpUtil.post(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/product/api/getProducts");
		return parseString(resultString);
	}
	
	private JsonNode parseString(String str) {
		ObjectMapper obj = new ObjectMapper();
		try {
			return obj.readTree(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public JsonNode postPurchase(Integer distributorType,String email,List<OrderDetail> orderDetail,
			Double totalPrice,Double couponsAmount,String couponsCode,String remarks,Boolean isChoose,
			String logisticsMode,Double bbcPostage,String logisticsTypeCode,Boolean immediateDelivery) throws JsonProcessingException, IOException {
		Map<String,Object> pMap = Maps.newHashMap();
		pMap.put("distributorType", distributorType);
		pMap.put("email", email);
		pMap.put("orderDetail", orderDetail);
		pMap.put("totalPrice",totalPrice);
		if(couponsAmount != null && couponsCode != null){
			pMap.put("couponsAmount", couponsAmount);
			pMap.put("couponsCode", couponsCode);
		}
		pMap.put("remarks", remarks);
		pMap.put("isChoose", isChoose);
		pMap.put("immediateDelivery", immediateDelivery);
		//add by zbc 立即结算发货要支付运费
		if(logisticsMode != null && bbcPostage != null){
			pMap.put("bbcPostage", bbcPostage);
			pMap.put("logisticsMode", logisticsMode);
			pMap.put("logisticsTypeCode", logisticsTypeCode);
		}
		Logger.info("cartPurchaseOrder:{}",Json.toJson(pMap));
		String resultString = HttpUtil.post(Json.toJson(pMap).toString(),
				HttpUtil.B2BBASEURL + "/purchase/order");
		Logger.info("postPurchase:" + resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode savePurchaseSaleOut(String pno,String jsonStr){
		Map<String,Object> pMap = Maps.newHashMap();
		pMap.put("pno", pno);
		pMap.put("jsonStr", jsonStr);
		Logger.info("保存采购发货信息:{}",Json.toJson(pMap));
		String resultString = HttpUtil.post(Json.toJson(pMap).toString(),
				HttpUtil.B2BBASEURL + "/purchase/savePurchaseSaleOut");
		Logger.info("保存采购发货结果:{}",resultString);
		return parseString(resultString);
		
	}
	
	@Override
	public JsonNode getFreight(Integer warehouseId, String shippingCode, JsonNode skuList, Integer provinceId,
			Integer cityId) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("warehouseId", warehouseId);
		param.put("shippingCode", shippingCode);
		param.put("orderDetails", skuList);
		param.put("provinceId", provinceId);
		param.put("countryId", 44);
		param.put("cityId", cityId);
		String resultString = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/inventory/getFreight");
		Logger.info("getFreight:" + resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode getCouponsInfo(Double orderAmount,String couponsNo) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("orderAmount", orderAmount.toString());
		param.put("couponsNo", couponsNo);
		Logger.info("获取优惠信息参数:{}",Json.toJson(param));
		String resultString = HttpUtil.get(param, HttpUtil.B2BBASEURL + "/member/getCouponsInfo");
		Logger.info("获取优惠信息结果:" + resultString);
		return parseString(resultString);
	}
	
}
