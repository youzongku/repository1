package service.timer.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import entity.timer.HistoryOrderData;
import entity.timer.SaleDetail;
import play.Logger;
import play.libs.Json;
import service.timer.IHttpService;
import util.timer.DateUtils;
import util.timer.HttpUtil;
import util.timer.IDUtils;

public class HttpServiceImpl implements IHttpService {
	
	@Override
	public JsonNode getOptFee(int warehouseId,int qty)
			throws JsonProcessingException, IOException{
		Map<String, String> param = Maps.newHashMap();
		param.put("warehouseId", String.valueOf(warehouseId));
		param.put("qty", String.valueOf(qty));
		String resultString = HttpUtil.get(param,
				HttpUtil.B2CBASEURL + "/warehousing/getoptfee");
		Logger.info("获取操作费结果为：" + resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode getDismemberByEmail(String email)
			throws JsonProcessingException, IOException {
		HashMap<String, Object> params = Maps.newHashMap();
		params.put("disEmail", email);
		String resultString = HttpUtil.post(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/member/dismember");
		Logger.info("getDismemberByEmail的结果    --->" + resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode getShopInfo(String email, String shopName, Integer platformId) throws JsonProcessingException, IOException {
		String resultString = HttpUtil.post(
				"{\"email\":\"" + email + "\",\"type\":\"" + platformId + "\",\"shopName\":\"" + shopName
						+ "\",\"remoteFlag\":\"" + IDUtils.getUUID().substring(0, 6) + "\"}",
				HttpUtil.B2BBASEURL + "/member/checkShopName");
		Logger.info("getShopInfo:" + resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode custaccount(String email) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("email", email);
		String resultString = HttpUtil.get(param,
				HttpUtil.B2BBASEURL + "/member/custaccount");
		Logger.info("custaccount:" + resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode getProducts(String email,List<String> skus, Integer warehouseId,Integer distributionMode) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("email", email);
		param.put("istatus", 1);
		param.put("skuList", skus);
		param.put("warehouseId", warehouseId);
		param.put("model", distributionMode);
		Map<String, Object> params = Maps.newHashMap();
		params.put("data", param);
		String resultString = HttpUtil.post(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/product/api/getProducts");
		Logger.info("getProducts:" + resultString);
		return parseString(resultString);

	}
	
	@Override
	public JsonNode getProvinces(String province) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("key", province);
		String resultString = HttpUtil.get(param,
				HttpUtil.B2BBASEURL + "/member/provinces");
		Logger.info("getProvinces:" + resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode getShoppingMethod(Integer warehouseId) throws JsonProcessingException, IOException {
		String getRes = HttpUtil.get(Maps.newHashMap(),
				HttpUtil.B2CBASEURL + "/sysadmin/warehouse/"+warehouseId+"/shippings");
		Logger.info("getShoppingMethod:" + getRes);
		return parseString(getRes);
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
				HttpUtil.B2BBASEURL + "/inventory/getFreight");
		Logger.info("getFreight:" + resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode getMemberInfo(String email) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("email", email);
		String resultString = HttpUtil.get(param, 
				HttpUtil.B2BBASEURL + "/member/infor");
		Logger.info("getMemberInfo:" + resultString);
		return parseString(resultString);
	}

	// TODO -------------仓库接口，需要进行对接--------------
	@Override
	public JsonNode checkInventory(String email, String salesOrderNo, List<SaleDetail> sd,Boolean totalCheck) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("email", email);
		param.put("pros", sd);
		param.put("salesOrderNo", salesOrderNo);
		if(totalCheck != null && totalCheck) {
			param.put("totalCheck", 1);
		}
		String resultString = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/inventory/ivyChk");
		Logger.info("checkInventory:" + resultString);
		return parseString(resultString);
	}

	// TODO -------------仓库接口，需要进行对接--------------
	@Override
	public String deductionStock(String email, List<SaleDetail> sd) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("email", email);
		Map<String, Object> param2 = Maps.newHashMap();
		param2.put("currentDetail", sd);
		param.put("pros", param2);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), 
				HttpUtil.B2BBASEURL + "/inventory/ivyDe");
		Logger.info("deductionStock:" + resultString);
		return resultString;
	}

	@Override
	public JsonNode postPurchase(Map<String, Object> pMap) throws JsonProcessingException, IOException {
		String resultString = HttpUtil.post(Json.toJson(pMap).toString(),
				HttpUtil.B2BBASEURL + "/purchase/order");
		Logger.info("postPurchase:" + resultString);
		return parseString(resultString);
	}

	// TODO -------------仓库接口，需要进行对接--------------
	@Override
	public JsonNode updateInventory(String email, List<SaleDetail> updIvyParam, String purchaseNo) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("email", email);
		param.put("pros", updIvyParam);
		param.put("purchaseNo", purchaseNo);
		Logger.info("入库参数：[{}]",Json.toJson(param).toString());
		String resultString = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/inventory/updIvy");
		Logger.info("updateInventory:" + resultString);
		return parseString(resultString);
	}
	
	
	private JsonNode parseString(String str) throws JsonProcessingException, IOException {
		ObjectMapper obj = new ObjectMapper();
		return obj.readTree(str);
	}

	@Override
	public JsonNode getWarehouse(Integer wid) throws JsonProcessingException, IOException {
		HashMap<String, String> params = Maps.newHashMap();
		if(wid !=null){
			params.put("wid", String.valueOf(wid));
		}
		String resultString = HttpUtil.get(params,
				HttpUtil.B2BBASEURL + "/inventory/queryWarehouse");
		return parseString(resultString);
	}

	@Override
	public JsonNode getPurchaseDetailList(String purchaseOrderNo) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("pno", purchaseOrderNo);
		String resultString = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/purchase/getDeByNo");
		Logger.info("updateInventory:" + resultString);
		return parseString(resultString);
	}

	// TODO -------------仓库接口，需要进行对接--------------
	@Override
	public JsonNode restoreMicroStock(String email,List<SaleDetail> detailList)
			throws JsonProcessingException, IOException {
		HashMap<Object,Object> param = Maps.newHashMap();
		HashMap<Object,Object> inner = Maps.newHashMap();
		param.put("email", email);
		param.put("pros", inner);
		inner.put("historyDetail", detailList);
		String resultString = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/inventory/resSto");
		return parseString(resultString);
	}

	@Override
	public JsonNode cancelPurchaseOrder(String purchaseOrderNo)
			throws JsonProcessingException, IOException {
	    HashMap<String,Object> param = Maps.newHashMap();
		param.put("purchaseNo", purchaseOrderNo);
		param.put("flag", "CANCEL");
		String resultString = HttpUtil.post(Json.toJson(param).toString(), 
				HttpUtil.B2BBASEURL + "/purchase/cancel");
		return parseString(resultString);
	}

	@Override
	public JsonNode getPurchaseOrder(String purchaseOrderNo)
			throws JsonProcessingException, IOException {
		HashMap<String,Object> param = Maps.newHashMap();
		param.put("purchaseOrderNo", purchaseOrderNo);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), 
				HttpUtil.B2BBASEURL + "/purchase/simpleInfo");
		return parseString(resultString);
	}

	@Override
	public JsonNode changeOrderPrice(JsonNode node) throws JsonProcessingException, IOException {
		String resultString = HttpUtil.post(node.toString(),
				HttpUtil.B2BBASEURL + "/purchase/changeOrderPrice");
		return parseString(resultString);
	}

	@Override
	public JsonNode backStagePayment(String email,Double amount,String tranNum,Integer applyType) throws JsonProcessingException, IOException {
		Map<String, Object> payParam = Maps.newHashMap();
		payParam.put("email", email);
		payParam.put("transferAmount", amount);
		payParam.put("transferNumber", tranNum);
		payParam.put("applyType", applyType);
		payParam.put("isBackStage", true);
		String resultString = HttpUtil.post(Json.toJson(payParam).toString(), 
				HttpUtil.B2BBASEURL + "/member/backStagePayment");
		return parseString(resultString);
	}
	
	public JsonNode finishedOrder(String purchaseNo, Double total) throws JsonProcessingException, IOException{
		Map<String, Object> param = Maps.newHashMap();
		param.put("purchaseNo", purchaseNo);
		param.put("flag", "PAY_SUCCESS");
		param.put("actualAmount", total);
		param.put("payType", "system");
		param.put("payDate", DateUtils.date2string(new Date(),DateUtils.FORMAT_FULL_DATETIME));
		Logger.info("完成采购单参数:[{}]",Json.toJson(param));
		String response_string = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/purchase/cancel");
		Logger.info("完成采购单结果:[{}]",response_string);
		return parseString(response_string);
	}

	@Override
	public JsonNode getPurByNo(String orderNo) throws JsonProcessingException, IOException {
		HashMap<String,Object> param = Maps.newHashMap();
		param.put("pNo", orderNo);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), 
				HttpUtil.B2BBASEURL + "/purchase/getOrderById");
		return parseString(resultString);
	}
	
	@Override
	public JsonNode getByNo(String orderNo)throws JsonProcessingException, IOException {
		HashMap<String,Object> param = Maps.newHashMap();
		param.put("purchaseOrderNo",orderNo);
		param.put("flag", "SUCCESS");
		String res = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/purchase/getByNo");
		return parseString(res);
	}

	@Override
	public JsonNode microLock(Map<String, Object> param) throws JsonProcessingException, IOException {
		Logger.info("微仓锁库参数:[{}]",Json.toJson(param));
		String res = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/inventory/micro/createSaleOrderWithInventory");
		Logger.info("微仓锁库结果:[{}]",res);
		return parseString(res);
	}
	
	@Override  
	public JsonNode updateStock(String orderNo) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("orderNo", orderNo);
		Logger.info("释放锁库，更新库存参数:[{}]",param);
		String resultString = HttpUtil.get(param, 
				HttpUtil.B2BBASEURL + "/inventory/inventoryorder/updatestock");
		Logger.info("释放锁库，更新库存结果:[{}]",resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode restoreMicro(String sod) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("orderNo", sod);
		Logger.info("还原微仓，更新库存参数:[{}]",param);
		String resultString = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/inventory/micro/cancleSaleOrderWithInventory");
		Logger.info("还原微仓，更新库存结果:[{}]",resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode restoreCloud(String pod) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("orderNo", pod);
		Logger.info("还原云仓，更新库存参数:[{}]",param);
		String resultString = HttpUtil.get(param,
				HttpUtil.B2BBASEURL + "/inventory/micro/restorecloudfrommicro");
		Logger.info("还原云仓，更新库存结果:[{}]",resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode getPriceList(List<String> skus) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("skuList", skus);
		Logger.info("获取价格参数[{}]",param);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/product/fixprice/read");
		Logger.info("获取价格结果:[{}]",resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode cloudLock(Map<String, Object> postMap) throws JsonProcessingException, IOException {
		Logger.info("云仓锁库参数:[{}]",Json.toJson(postMap));
		String resultString = HttpUtil.post(Json.toJson(postMap).toString(), HttpUtil.B2BBASEURL+"/inventory/inventoryorder/lockcloudstock");
		Logger.info("云仓锁库结果:{}",resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode unLock(String orderNo) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("orderNo", orderNo);
		Logger.info("释放锁库参数",param);
		String resultString = HttpUtil.get(param, HttpUtil.B2BBASEURL + "/inventory/inventoryorder/purchasecancel");
		Logger.info("释放锁库结果:[{}]",resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode historyOrderClosed(HistoryOrderData order) throws JsonProcessingException, IOException{
		Logger.info("旧版临时还原微仓参数",order);
		String resultString = HttpUtil.post(Json.toJson(order).toString(), HttpUtil.B2BBASEURL+"/inventory/inventoryorder/historyOrderClosed");
		Logger.info("旧版临时还原微仓结果:[{}]",resultString);
		return parseString(resultString);
	}


	
}
