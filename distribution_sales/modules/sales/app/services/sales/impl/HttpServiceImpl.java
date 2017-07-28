package services.sales.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;

import dto.SkuWarehouse2Qty;
import play.Logger;
import play.libs.Json;
import services.sales.IHttpService;
import util.sales.DateUtils;
import util.sales.HttpUtil;
import util.sales.IDUtils;

public class HttpServiceImpl implements IHttpService {
	
	@Override
	public JsonNode getTotalArriveWarehousePrice(Map<String, Integer> skuWarehouse2Qty, boolean useCostIfAbsent)
			throws JsonProcessingException, IOException {
		HashMap<String, Object> params = Maps.newHashMap();
		HashMap<String, Object> innerParam = Maps.newHashMap();

		List<SkuWarehouse2Qty> skuWarehouse2QtyList = skuWarehouse2Qty
				.entrySet()
				.stream()
				.map(entry -> {
					String[] skuWarehouse = entry.getKey().split("_");
					return new SkuWarehouse2Qty(skuWarehouse[0], Integer
							.valueOf(skuWarehouse[1]), entry.getValue());
				}).collect(Collectors.toList());

		innerParam.put("skuWarehouse2QtyList", skuWarehouse2QtyList);
		innerParam.put("useCostIfAbsent", useCostIfAbsent);
		params.put("data", innerParam);
		
		Logger.info("getTotalArriveWarehousePrice  post_string--->"
				+ Json.toJson(params));
		String resultString = HttpUtil.post(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/product/api/totalAWPrice");
		Logger.info("getTotalArriveWarehousePrice     --->" + resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode batchGetArriveWarePrice(List<SkuWarehouse2Qty> list) throws JsonProcessingException,
	IOException{
		Map<String,Object> outer = Maps.newHashMap();
		Map<String,Object> inner = Maps.newHashMap();
		inner.put("skuWarehouse2QtyList", list);
		outer.put("data", inner);
		JsonNode paramsNode = Json.toJson(outer);
		Logger.info("批量获取商品到仓价-参数:[{}]",paramsNode);
		String resultString = HttpUtil.post(paramsNode.toString(),
				HttpUtil.B2BBASEURL + "/product/api/AWPrices");
		Logger.info("批量获取商品到仓价-结果:{}",resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode getMicroProductsExpirationDate(String email, List<ObjectNode> skuWarehouseIdNodeList) throws JsonProcessingException,
	IOException {
		Map<String,Object> map = Maps.newHashMap();
		map.put("account", email);
		map.put("skuWarehouseIdArray", skuWarehouseIdNodeList);
		JsonNode paramsNode = Json.toJson(map);
		Logger.info("获取微仓商品到期日期-参数:[{}]",paramsNode);
		String resultString = HttpUtil.post(paramsNode.toString(),
				HttpUtil.B2BBASEURL
						+ "/inventory/micro/getMicroDetailBySkuAndWareId");
		Logger.info("获取微仓商品到期日期-结果:{}",resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode getCloudProductsExpirationDate(List<ObjectNode> skuWarehouseIdNodeList) throws JsonProcessingException,
	IOException{
		Map<String, Object> map = Maps.newHashMap();
		map.put("skuWarehouseIdArray", skuWarehouseIdNodeList);
		JsonNode paramsNode = Json.toJson(map);
		Logger.info("获取云仓商品到期日期-参数:[{}]", paramsNode);
		String resultString = HttpUtil.post(paramsNode.toString(),
				HttpUtil.B2BBASEURL + "/inventory/cloud/getCloudDetail");
		Logger.info("获取云仓商品到期日期-结果:{}", resultString);
		return parseString(resultString);
	}
	
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
//		Logger.info("getProducts:" + resultString);
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
	public JsonNode getCities(Integer proId) throws JsonProcessingException, IOException{
		Map<String, String> param = Maps.newHashMap();
		param.put("proId", proId.toString());
		String resultString = HttpUtil.get(param,
				HttpUtil.B2BBASEURL + "/member/getcities");
		Logger.info("getCities:" + resultString);
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
			Integer countryId, Integer cityId, Integer model) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("warehouseId", warehouseId);
		param.put("shippingCode", shippingCode);
		param.put("orderDetails", skuList);
		param.put("provinceId", provinceId);
		param.put("countryId", countryId);
		param.put("cityId", cityId);
		param.put("model", model);
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


	@Override
	public JsonNode postPurchase(Map<String, Object> pMap) throws JsonProcessingException, IOException {
		String resultString = HttpUtil.post(Json.toJson(pMap).toString(),
				HttpUtil.B2BBASEURL + "/purchase/order");
		Logger.info("postPurchase:" + resultString);
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
				HttpUtil.B2BBASEURL + "/inventory/backstage/queryWarehouse");
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
	public JsonNode backStagePayment(String email,Double amount,String tranNum,Integer applyType,String remark) throws JsonProcessingException, IOException {
		Map<String, Object> payParam = Maps.newHashMap();
		payParam.put("email", email);
		payParam.put("transferAmount", amount);
		payParam.put("transferNumber", tranNum);
		payParam.put("applyType", applyType);
		payParam.put("isBackStage", true);
		payParam.put("applyRemark", remark);
		Logger.info("后台支付参数:{}",Json.toJson(payParam));
		String resultString = HttpUtil.post(Json.toJson(payParam).toString(), 
				HttpUtil.B2BBASEURL + "/member/backStagePayment");
		Logger.info("后台支付结果:{}",resultString);
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
		//change by zbc ＫＡ微仓锁库
//		String res = HttpUtil.post(Json.toJson(param).toString(),
//				HttpUtil.B2BBASEURL + "/inventory/micro/createSaleOrderWithInventory");
		String res = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/product/lock/microlock");
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
		//change by zbc KA云仓锁库 替换 云仓锁库
//		String resultString = HttpUtil.post(Json.toJson(postMap).toString(), HttpUtil.B2BBASEURL+"/inventory/inventoryorder/lockcloudstock");
		String resultString = HttpUtil.post(Json.toJson(postMap).toString(), HttpUtil.B2BBASEURL+"/product/lock/cloudlock");
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
	public JsonNode getShopInfo(Integer shopId) throws JsonProcessingException, IOException {
		Map<String, Integer> param = Maps.newHashMap();
		param.put("sid", shopId);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/member/getstoreById");
		Logger.info("店铺信息:[{}]",resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode contractChargeMatch(List<Map<String, Object>> contractChargeMapList, String salesOrderNo, String payDate)
			throws JsonProcessingException, IOException {
		Map<String,Object> param = Maps.newHashMap();
		param.put("chargeMapList",contractChargeMapList);
		param.put("salesOrderNo", salesOrderNo);
		param.put("payDate", payDate);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/product/charges/match");
		Logger.info("计算合同费用:[{}]",resultString);
		return parseString(resultString);
	}
	public JsonNode getOrgInfo(Integer od) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		if(od != null){
			param.put("od", od+"");
		}
		Logger.info("获取节点下关联的分销商:[{}]",param);
		String resultString = HttpUtil.get(param, HttpUtil.B2BBASEURL + "/member/org/getInfo");
		return parseString(resultString);
	}

	@Override
	public JsonNode getCloudProductsExpirationDate(String email, List<ObjectNode> skuWarehouseIdNodeList)
			throws JsonProcessingException, IOException {
		Map<String, Object> map = Maps.newHashMap();
		map.put("email", email);
		map.put("skuWarehouseIdArray", skuWarehouseIdNodeList);
		JsonNode paramsNode = Json.toJson(map);
		Logger.info("获取云仓商品到期日期-参数:[{}]", paramsNode);
		String resultString = HttpUtil.post(paramsNode.toString(),
				HttpUtil.B2BBASEURL + "/product/lock/cloud");
		Logger.info("获取云仓商品到期日期-结果:{}", resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode getAccount(String email) throws JsonProcessingException, IOException{
		Map<String,String> map = Maps.newHashMap();
		map.put("email", email);
		map.put("isback", "true");
		Logger.info("获取资金账户信息参数{}",map);
		String resultString = HttpUtil.get(map, HttpUtil.B2BBASEURL+"/member/getAccount");
		Logger.info("获取资金账户信息结果{}",resultString);
		return parseString(resultString);
	}
	@Override
	public JsonNode changePurchaseOrderFreight(List<Map<String,Object>> list) throws JsonProcessingException, IOException{
		Logger.info("更新采购单运费参数{}",Json.toJson(list));
		String resultString = HttpUtil.post(Json.toJson(list).toString(), HttpUtil.B2BBASEURL+"/purchase/changeFreight ");
		Logger.info("更新采购单运费结果{}",resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode refund(String disAccount,Object amount,String orderNo) throws JsonProcessingException, IOException{
		Map<String,Object> map = Maps.newHashMap();
		map.put("email", disAccount);
		map.put("transferAmount", amount);
		map.put("transferNumber",orderNo);
		map.put("isBackStage", true);
		map.put("applyType", 4);
		Logger.info("后台退款参数:{}",Json.toJson(map));
		String resultString = HttpUtil.post(Json.toJson(map).toString(), HttpUtil.B2BBASEURL+"/member/freightRefund");
		Logger.info("后台退款结果{}",resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode orderLock(String pNo) throws JsonProcessingException, IOException{
		Map<String,String> map = Maps.newHashMap();
		map.put("od", pNo);
		Logger.info("采购单云仓锁库参数{}",map);
		String resultString = HttpUtil.get(map, HttpUtil.B2BBASEURL+"/purchase/orderLock");
		Logger.info("采购单云仓锁库结果{}",resultString);
		return parseString(resultString);
	}
	
	public JsonNode getContractFeeItems(Map<String, Object> postMap) throws JsonProcessingException, IOException{
		JsonNode paramsNode = Json.toJson(postMap);
		Logger.info("获取合同费用项参数:[{}]", paramsNode);
		String resultString = HttpUtil.post(paramsNode.toString(),
				HttpUtil.B2BBASEURL + "/product/contract/fee/items4cal");
		Logger.info("获取合同费用项-结果:{}", resultString);
		return parseString(resultString);
	}
}
