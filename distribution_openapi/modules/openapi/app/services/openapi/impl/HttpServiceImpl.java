package services.openapi.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import play.Logger;
import play.libs.Json;
import play.mvc.Http.Context;
import services.openapi.IHttpService;
import utils.HttpUtil;
import utils.IDUtils;

public class HttpServiceImpl implements IHttpService {

	@Override
	public JsonNode getShopInfo(String email, String shopName, Integer platformId, Context context) throws JsonProcessingException, IOException {
		String resultString = HttpUtil.post(
				"{\"email\":\"" + email + "\",\"type\":\"" + platformId + "\",\"shopName\":\"" + shopName
						+ "\",\"remoteFlag\":\"" + IDUtils.getUUID().substring(0, 6) + "\"}",
				HttpUtil.B2BBASEURL + "/member/checkShopName",context);
		Logger.info("getShopInfo:" + resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode custaccount(String email) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("email", email);
		String resultString = HttpUtil.get(param, HttpUtil.B2BBASEURL + "/member/custaccount",null,null);
		Logger.info("custaccount:" + resultString);
		return parseString(resultString);
	}
	@Override
	public JsonNode viewpurchase(Map<String, Object> param) throws JsonProcessingException, IOException {
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/purchase/viewpurchase",null);
		Logger.info("viewpurchase:" + resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode fgetProducts(String email,List<String> skus, Integer warehouseId,Integer distributionMode) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("email", email);
		param.put("skuList", skus);
		param.put("istatus", 1);
		param.put("warehouseId", warehouseId);
		param.put("model", distributionMode);
		Map<String, Object> params = Maps.newHashMap();
		params.put("data", param);
		String resultString = HttpUtil.post(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/product/api/fgetProducts",null);
		Logger.info("getProducts:" + resultString);
		return parseString(resultString);

	}

	@Override
	public JsonNode getProvinces(String province) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("key", province);
		String resultString = HttpUtil.get(param, HttpUtil.B2BBASEURL + "/member/provinces",null,null);
		Logger.info("getProvinces:" + resultString);
		return parseString(resultString);
	}


	/*
	 * freight = { "warehouseId" : warehouseId, "shippingCode" :
	 * platformOrderBaseInfo.logisticsTypeCode, "orderDetails" : details,
	 * "provinceId" : 17, "countryId" : 44 };
	 * 
	 * (non-Javadoc)
	 * 
	 * @see services.sales.IHttpService#getFreight(java.lang.Integer,
	 * java.lang.String, com.fasterxml.jackson.databind.JsonNode,
	 * java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public JsonNode getFreight(Integer warehouseId, String shippingCode, JsonNode skuList, Integer provinceId,
			Integer countryId) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("warehouseId", warehouseId);
		param.put("shippingCode", shippingCode);
		param.put("orderDetails", skuList);
		param.put("provinceId", provinceId);
		param.put("countryId", countryId);
		String resultString = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/inventory/getFreight",null);
		Logger.info("getFreight:" + resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode getMemberInfo(String email) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("email", email);
		String resultString = HttpUtil.get(param, HttpUtil.B2BBASEURL + "/member/infor",null,null);
		Logger.info("getMemberInfo:" + resultString);
		return parseString(resultString);
	}

	// TODO -------------仓库接口，需要进行对接--------------
/*	@Override
	public JsonNode checkInventory(String email, String salesOrderNo, List<SaleDetail> sd) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("email", email);
		param.put("pros", sd);
		param.put("salesOrderNo", salesOrderNo);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/inventory/ivyChk");
		Logger.info("checkInventory:" + resultString);
		return parseString(resultString);
	}
*/
	// TODO -------------仓库接口，需要进行对接--------------
	/*@Override
	public String deductionStock(String email, List<SaleDetail> sd) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("email", email);
		Map<String, Object> param2 = Maps.newHashMap();
		param2.put("currentDetail", sd);
		param.put("pros", param2);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/inventory/ivyDe");
		Logger.info("deductionStock:" + resultString);
		return resultString;
	}*/

	@Override
	public JsonNode postPurchase(Map<String, Object> pMap,Context context) throws JsonProcessingException, IOException {
		String resultString = HttpUtil.post(Json.toJson(pMap).toString(), HttpUtil.B2BBASEURL + "/purchase/order",context);
		Logger.info("postPurchase:" + resultString);
		return parseString(resultString);
	}

	// TODO -------------仓库接口，需要进行对接--------------
/*	@Override
	public JsonNode updateInventory(String email, List<SaleDetail> updIvyParam, String purchaseNo) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("email", email);
		param.put("pros", updIvyParam);
		param.put("purchaseNo", purchaseNo);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/inventory/updIvy");
		Logger.info("updateInventory:" + resultString);
		return parseString(resultString);
	}*/
	
	
	private JsonNode parseString(String str) throws JsonProcessingException, IOException {
		ObjectMapper obj = new ObjectMapper();
		return obj.readTree(str);
	}

}
