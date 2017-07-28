package services.purchase.impl;

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

import dto.purchase.InventoryCloudLockDto;
import dto.purchase.SkuWarehouse2Qty;
import entity.purchase.PurchaseActive;
import forms.purchase.GetProductsParams;
import forms.purchase.returnod.ReturnLockParams;
import play.Logger;
import play.libs.Json;
import services.purchase.IHttpService;
import utils.purchase.HttpUtil;
import utils.purchase.PurchaseOrderStatus;

public class HttpServiceImpl implements IHttpService {
	
	@Override
	public JsonNode getMicroInRecord(String purchaseOrderNo) throws JsonProcessingException,
	IOException {
		Map<String, Object> map = Maps.newHashMap();
		map.put("purchaseOrderNo", purchaseOrderNo);
		JsonNode paramsNode = Json.toJson(map);
		Logger.info("根据采购单号获取微仓入库记录-参数:[{}]", paramsNode);
		String resultString = HttpUtil.post(paramsNode.toString(),
				HttpUtil.B2BBASEURL + "/inventory/micro/getMicroInRecordByPurchaseOrderNo");
		Logger.info("根据采购单号获取微仓入库记录-结果:{}", resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode getReturnAmountCapfee4Sku(String purchaseOrderNo) throws JsonProcessingException,
	IOException {
		Map<String, Object> map = Maps.newHashMap();
		map.put("purchaseOrderNo", purchaseOrderNo);
		JsonNode paramsNode = Json.toJson(map);
		Logger.info("获取发货单售后退款均摊-参数:[{}]", paramsNode);
		String resultString = HttpUtil.post(paramsNode.toString(),
				HttpUtil.B2BBASEURL + "/sales/actualAmountCapfee");
		Logger.info("获取发货单售后退款均摊-结果:{}", resultString);
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
	public JsonNode returnLock(ReturnLockParams params) throws JsonProcessingException,
	IOException{
		JsonNode paramsNode = Json.toJson(params);
		Logger.info("微仓退货-微仓锁库参数:[{}]",paramsNode);
		String resultString = HttpUtil.post(paramsNode.toString(), HttpUtil.B2BBASEURL+"/inventory/micro/returnLock");
		Logger.info("微仓退货-微仓锁库结果:{}",resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode returnToCloudInventory(List<String> returnOrderNoList) throws JsonProcessingException,
	IOException{
		Map<String, Object> param = Maps.newHashMap();
		param.put("returnOrderNoArray", returnOrderNoList);
		Logger.info("微仓退货-微仓库存退还云仓参数:[{}]",param);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/inventory/micro/returnToCloudInventory");
		Logger.info("微仓退货-微仓库存退还云仓结果:[{}]",resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode releaseReturnLock(List<String> returnOrderNoList) throws JsonProcessingException,
	IOException{
		Map<String, Object> param = Maps.newHashMap();
		param.put("returnOrderNoArray", returnOrderNoList);
		Logger.info("微仓退货-释放微仓库存锁参数:[{}]",param);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/inventory/micro/releaseReturnLock");
		Logger.info("微仓退货-释放微仓库存锁结果:[{}]",resultString);
		return parseString(resultString);
	}
	
	@Override
	public String getFreight(Map<String,Object> params){
		Logger.info("getFreight     post_string--->"
				+ Json.toJson(params));
		String response_string = HttpUtil.post(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/inventory/getFreight");
		Logger.info("getFreight     response_string--->"
				+ response_string);
		return response_string;
	}
	
	@Override
	public String createSaleOrder(String params){
		String response_string = HttpUtil.post(params,
				HttpUtil.B2BBASEURL + "/sales/postOrderFPO");
		return response_string;
	}
	
	@Override
	public String balancePayment(Map<String,Object> params){
		Logger.info("balancePayment     post_string--->"
				+ Json.toJson(params));
		String response_string = HttpUtil.post(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/member/backStagePayment");
		Logger.info("balancePayment     response_string--->"
				+ response_string);
		return response_string;
	}
	
	
	@Override
	public String getDebt(String email) throws JsonProcessingException, IOException {
		Map<String, String> params = Maps.newHashMap();
		params.put("email", email);

		Logger.info("getDebt     post_string--->" + Json.toJson(params));
		String response_string = HttpUtil.get(params, HttpUtil.B2BBASEURL
				+ "/member/getDebtByEmail");
		Logger.info("getDebt     response_string--->" + response_string);
		return response_string;
	}
	
	@Override
	public JsonNode offlineTransferExtraMoney2Balance(String email,String purchaseOrderNo,Double money)
			throws JsonProcessingException, IOException {
		Map<String, Object> map = Maps.newHashMap();
		map.put("email", email);
		map.put("transferAmount", money);
		map.put("transferNumber", purchaseOrderNo);
		map.put("isBackStage", true);
		map.put("applyType", 4);
		JsonNode refundNode = Json.parse(HttpUtil.post(Json.toJson(map)
				.toString(), HttpUtil.B2BBASEURL + "/member/freightRefund"));
		
		ObjectNode result = Json.newObject();
		if (refundNode.get("code").asInt() != 4) {// 说明退款失败
			result.put("suc", false);
			result.put("msg", "订单" + purchaseOrderNo
					+ "退款失败");
		} else {
			result.put("suc", true);
			result.put("msg", "订单" + purchaseOrderNo
					+ "退款成功");
		}
		return result;
	}
	
	@Override
	public JsonNode refund2Balance4ReturnOrder(String email,String returnOrderNo,Double money) throws JsonProcessingException,
	IOException{
		Map<String, Object> map = Maps.newHashMap();
		map.put("email", email);
		map.put("transferAmount", money);
		map.put("transferNumber", returnOrderNo);
		map.put("isBackStage", true);
		map.put("applyType", 4);
		Logger.info("微仓退货，将款退回到余额里-参数：{}",map);
		JsonNode refundNode = Json.parse(HttpUtil.post(Json.toJson(map)
				.toString(), HttpUtil.B2BBASEURL + "/member/freightRefund"));
		Logger.info("微仓退货，将款退回到余额里-结果：{}",refundNode);
		return refundNode;
	}
	
	@Override
	public JsonNode finishSaleOrder(Integer sid,Double actualPay,boolean isComplete,String payDate, String payNo, String payType) throws JsonProcessingException, IOException{
		Map<String,Object> param = Maps.newHashMap();
		param.put("id", sid);
		param.put("actualPay", actualPay);
		param.put("isComplete", isComplete);
		param.put("payDate", payDate);
		param.put("payNo", payNo);
		param.put("payType", payType);
		Logger.info("完成发货单参数:{}",param);
		JsonNode resultNode = Json.parse(HttpUtil.post(Json.toJson(param)
				.toString(), HttpUtil.B2BBASEURL + "/sales/finishSaleOrder"));
		Logger.info("完成发货单结果:{}",resultNode);
		return resultNode;
	} 
	
	
	@Override
	public JsonNode getProducts(GetProductsParams params) throws JsonProcessingException,
			IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("title", params.getTitle());
		param.put("maxPrice", params.getMaxPrice());
		param.put("minPrice", params.getMinPrice());
		param.put("categoryId", params.getCategoryId());
		param.put("email", params.getEmail());
		param.put("skuList", params.getSkuList());
		param.put("sku", params.getSku());
		param.put("warehouseId", params.getWarehouseId());
		param.put("istatus", params.getIstatus()!=null?params.getIstatus():1);
		param.put("model", params.getModel());
		param.put("pageSize", params.getPageSize());
		param.put("currPage", params.getCurrPage());
		param.put("typeId", params.getTypeId());
		Map<String, Object> dataParams = Maps.newHashMap();
		dataParams.put("data", param);
		String resultString = HttpUtil.post(Json.toJson(dataParams).toString(),
				HttpUtil.B2BBASEURL + "/product/api/getProducts");
		return parseString(resultString);

	}

	@Override
	public JsonNode getMemberInfo(String email) throws JsonProcessingException,
			IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("email", email);
		String resultString = HttpUtil.get(param, HttpUtil.B2BBASEURL
				+ "/member/infor");
		return parseString(resultString);
	}

	private JsonNode parseString(String str) throws JsonProcessingException,
			IOException {
		ObjectMapper obj = new ObjectMapper();
		return obj.readTree(str);
	}


	@Override
	public JsonNode checkActive(Map<String, Object> param)
			throws JsonProcessingException, IOException {
		Logger.info("checkActive     post_string--->" + Json.toJson(param));
		String resultString = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/market/pro/act/check");
		Logger.info("checkActive     --->" + resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode getDismemberByEmail(String email)
			throws JsonProcessingException, IOException {
		HashMap<String, Object> params = Maps.newHashMap();
		params.put("disEmail", email);
		Logger.info("getDismemberByEmail     post_string--->"
				+ Json.toJson(params));
		String resultString = HttpUtil.post(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/member/dismember");
		Logger.info("getDismemberByEmail     --->" + resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode getTotalArriveWarehousePrice(Map<String, Integer> skuWarehouse2Qty)
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
		params.put("data", innerParam);
		
		Logger.info("getTotalArriveWarehousePrice  post_string--->"
				+ Json.toJson(params));
		String resultString = HttpUtil.post(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/product/api/totalAWPrice");
		Logger.info("getTotalArriveWarehousePrice     --->" + resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode updateCoupons(String user,String code, Integer orderStatus, Integer istatus,
			Double actuallyPay,Double orderAmount,Date usageTime,String orderNo)
			throws JsonProcessingException, IOException{
		Map<String, Object> couponsMap = Maps.newHashMap();
		couponsMap.put("couponsNo",code);
		couponsMap.put("orderStatus", orderStatus);
		couponsMap.put("istatus", istatus);
		if (orderStatus != null&&(orderStatus.equals(PurchaseOrderStatus.CANCEL)|| orderStatus.equals(PurchaseOrderStatus.INVALIDATED))) {
			couponsMap.put("istatus", 3);
		}
		couponsMap.put("user", user);
		couponsMap.put("usageTime", usageTime);
		couponsMap.put("orderNo", orderNo);
		couponsMap.put("orderAmount", orderAmount);
		couponsMap.put("actuallyPaid", actuallyPay);
		Logger.info("优惠码参数[{}]",couponsMap);
		// 当支付成功则把 优惠码改为已使用
		String resultString = HttpUtil.post(Json.toJson(couponsMap).toString(), 
				HttpUtil.B2BBASEURL+ "/member/updateCoupons");
		
		Logger.info("========优惠码更新结果[{}]========" ,resultString);
		return parseString(resultString);
	}
	
	
	@Override
	public JsonNode syncActiveLog(PurchaseActive act,String email,Double total,String orderno) 
			throws JsonProcessingException, IOException{
		Map<String, Object> params = Maps.newHashMap();
		params.put("userName", email);
		params.put("orderNumber", orderno);
		params.put("purchasePrice",total);
		params.put("proActId",act.getActiveid());
		params.put("isPayment",true);
		params.put("pvlgInstId",act.getPriviledgeid());
		Logger.info("同步保存促销活动日志参数:[{}]",params);
		String resultString = HttpUtil.post(Json.toJson(params).toString(), HttpUtil.B2BBASEURL+"/market/pro/act/savelog");
		Logger.info("同步保存促销活动日志结果:{}",resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode actExcute(Integer actId, Map<String, Object> param)
			throws JsonProcessingException, IOException{
		Map<String,Object> postMap = Maps.newHashMap();
		postMap.put("actId", actId);
		postMap.put("pros", param);
		Logger.info("获取赠品信息参数:[{}]",postMap);
		String resultString = HttpUtil.post(Json.toJson(postMap).toString(), HttpUtil.B2BBASEURL+"/market/pro/act/backexcute");
		Logger.info("获取赠品信息结果:{}",resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode cloudLock(InventoryCloudLockDto lockDto) throws JsonProcessingException, IOException {
		Logger.info("云仓锁库参数:[{}]",Json.toJson(lockDto));
		//change by zbc KA锁库修改
//		String resultString = HttpUtil.post(Json.toJson(postMap).toString(), HttpUtil.B2BBASEURL+"/inventory/inventoryorder/lockcloudstock");
		String resultString = HttpUtil.post(Json.toJson(lockDto).toString(), HttpUtil.B2BBASEURL+"/product/lock/cloudlock");
		Logger.info("云仓锁库结果:{}",resultString);
		return parseString(resultString);
	}
	@Override
	public JsonNode changeInventoryCafee(Map<String, Object> postMap) throws JsonProcessingException, IOException {
		Logger.info("更新云仓锁库信息:[{}]",Json.toJson(postMap));
		String resultString = HttpUtil.post(Json.toJson(postMap).toString(), HttpUtil.B2BBASEURL+"/inventory/inventoryorder/changeOrderDetail");
		Logger.info("更新云仓锁库信息结果:{}",resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode permanentLock(String orderNo)throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("orderNo", orderNo);
		Logger.info("永久锁定库存参数[{}]",param);
		String resultString = HttpUtil.get(param, HttpUtil.B2BBASEURL + "/inventory/inventoryorder/updatelockstatus");
		Logger.info("永久锁定库存结果:[{}]" ,resultString);
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
	public JsonNode updateStock(String orderNo) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("orderNo", orderNo);
		Logger.info("微仓入库，更新库存参数:[{}]",param);
		String resultString = HttpUtil.get(param, HttpUtil.B2BBASEURL + "/inventory/inventoryorder/updatestock");
		Logger.info("微仓入库，更新库存结果:[{}]",resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode getSaleMain(Integer sid) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("orderId", sid);
		Logger.info("获取发货单信息参数:[{}]",param);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/sales/getMain");
		Logger.info("获取发货单信息结果:[{}]",resultString);
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
	public JsonNode saveMicroOut(Integer sid, JsonNode mircoOut)throws JsonProcessingException, IOException{
		Map<String, Object> param = Maps.newHashMap();
		param.put("sid", sid);
		param.put("mircoOut", mircoOut);
		Logger.info("保存微仓出库历史数据参数:[{}]",param);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/sales/saveMicroOut");
		Logger.info("保存微仓出库历史数据结果:[{}]",resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode restoreMicro(Integer sid,String operater) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("sid", sid);
		param.put("op", operater);
		Logger.info("关闭缺货采购单，微仓还原参数:[{}]",param);
		String resultString = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/sales/remicsto");
		Logger.info("关闭缺货采购单，微仓还原结果:[{}]",resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode checktransferNumber(String transferNumber) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("tno", transferNumber);
		Logger.info("校验付款流水号参数:[{}]",param);
		String resultString = HttpUtil.get(param, HttpUtil.B2BBASEURL + "/member/checktranNo");
		Logger.info("校验付款流水号结果:[{}]",resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode custaccount(String email) throws JsonProcessingException,
			IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("email", email);
		String resultString = HttpUtil.get(param,
				HttpUtil.B2BBASEURL + "/member/custaccount");
		Logger.info("custaccount:" + resultString);
		return parseString(resultString);
	}
	@Override
	public JsonNode getOrderDetails(String orderNo) throws JsonProcessingException, IOException {
		Map<String,String> params = Maps.newHashMap();
		params.put("orderNo", orderNo);
		String resultString = HttpUtil.get(params,HttpUtil.B2BBASEURL+"/inventory/inventoryorder/getOrderDetails");
		Logger.info("orderDetails:[{}]",resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode cancelSalesOrder(String so) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("so", so);
		//不进行采购单关闭操作 防止死循环
		param.put("purClose", false);
		Logger.info("采购单失效关闭发货单参数:[{}]",param);
		String resultString = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/sales/cancelso");
		Logger.info("采购单失效关闭发货单结果:[{}]",resultString);
		return parseString(resultString);
	}
	
}
