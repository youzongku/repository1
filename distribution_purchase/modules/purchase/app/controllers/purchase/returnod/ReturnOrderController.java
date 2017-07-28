package controllers.purchase.returnod;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import annotation.ALogin;
import annotation.DivisionMember;
import annotation.Login;
import dto.purchase.returnod.CalculateReturnAmountResult;
import dto.purchase.returnod.ReturnOrderDto;
import entity.purchase.returnod.ReturnOrder;
import entity.purchase.returnod.ReturnOrderDetail;
import forms.purchase.Page;
import forms.purchase.returnod.AuditReturnOrderParams;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.purchase.IUserService;
import services.purchase.returnod.IReturnOrderService;
import utils.purchase.RegExpValidatorUtils;
import utils.purchase.StringUtils;

@Api(value="/returnOrder",description="微仓退货")
public class ReturnOrderController extends Controller {

	@Inject private IReturnOrderService returnOrderService;
	@Inject private IUserService userService;
	
	@Login
	public Result calculateExpectReturnAmount() {
		/*
		 {"sku":"","warehouseId":2024,"capfee":0.1,returnQty":1,"expirationDate":"2012-01-12"}
		 */
		JsonNode node = request().body().asJson();
		ReturnOrderDetail detailParams = parseParams(node);
		CalculateReturnAmountResult calculateReturnAmountResult = returnOrderService.calculateExpectReturnAmount(detailParams);
		return ok(Json.toJson(calculateReturnAmountResult));
	}

	/**
	 * 前台申请退货
	 * @return
	 */
	@Login
	public Result applyReturnOrder() {
		JsonNode node = request().body().asJson();
		Logger.info("微仓申请退货的参数：{}",node);
//		if(node.get("isGift").asInt()==1){
//			Map<String, Object> result = Maps.newHashMap();
//			result.put("suc", false);
//			result.put("msg", "赠品不能退货申请");
//			return ok(Json.toJson(result));
//		}
		// 0正价商品；1赠品
		if(!node.has("isGift") || (node.get("isGift").asInt()!=0 && node.get("isGift").asInt()!=1)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "没有指明要退货的商品是正价商品还是赠品");
			return ok(Json.toJson(result));
		}
		ReturnOrderDetail detailParams = parseParams(node);
		// 用户填的退款金额
		Double userExpectTotalReturnAmount = node.has("userExpectTotalReturnAmount") ? node.get("userExpectTotalReturnAmount").asDouble() : null;
		ReturnOrder ro = new ReturnOrder();
		ro.setRemarks(node.get("remarks").asText());
		ro.setEmail(userService.getDisAccount());
		ro.setNickName(Json.parse(userService.getDismember()).get("nickName").asText());
		ro.setUserExpectTotalReturnAmount(userExpectTotalReturnAmount);
		Map<String, Object> result = returnOrderService.applyReturnOrder(ro, detailParams);
		return ok(Json.toJson(result));
	}

	private ReturnOrderDetail parseParams(JsonNode node){
		ReturnOrderDetail detail = new ReturnOrderDetail();
		detail.setPurchaseOrderNo(node.has("purchaseOrderNo")?node.get("purchaseOrderNo").asText():null);
		detail.setProductTitle(node.has("productTitle")?node.get("productTitle").asText():null);
		detail.setImgUrl(node.has("imgUrl")?node.get("imgUrl").asText():null);
		detail.setSku(node.get("sku").asText());
		detail.setWarehouseId(node.get("warehouseId").asInt());
		detail.setWarehouseName(node.has("warehouseName")?node.get("warehouseName").asText():null);
		detail.setPurchasePrice(node.has("purchasePrice")?node.get("purchasePrice").asDouble():null);
		detail.setCapfee(node.get("capfee").asDouble());
		detail.setPurchaseTime(node.has("purchaseTime")?node.get("purchaseTime").asText():null);
		detail.setExpirationDate(node.get("expirationDate").asText());
		detail.setReturnQty(node.get("returnQty").asInt());
		detail.setQty(node.has("qty")?node.get("qty").asInt():null);
		detail.setResidueNum(node.has("residueNum")?node.get("residueNum").asInt():null);
		detail.setInRecordId(node.has("inRecordId")?node.get("inRecordId").asInt():null);
		detail.setIsGift(node.has("isGift") && node.get("isGift").asInt()==1?true:false);// 0正价商品；1赠品
		return detail;
	}

	/**
	 * 用户取消退货申请单
	 * @return
	 */
	@Login
	public Result cancelReturnOrderApplication() {
		JsonNode node = request().body().asJson();
		Map<String, Object> result = returnOrderService.cancelReturnOrderApplication(userService.getDisAccount(), node.get("returnOrderNo").asText());
		return ok(Json.toJson(result));
	}

	/**
	 * 查看退货单
	 * @return
	 */
	public Result getReturnOrder() {
		JsonNode node = request().body().asJson();
		Map<String, Object> result = Maps.newHashMap();
		result.put("returnOrderDto",returnOrderService.getReturnOrder(node.get("returnOrderNo").asText()));
		return ok(Json.toJson(result));
	}

	/**
	 * 前台获取退货单列表
	 *
	 * @return
	 */
	@Login
	@DivisionMember
	public Result getReturnOrders() {
		JsonNode node = request().body().asJson();
		Logger.info("前台查询查询退货单的参数：{}",node);
		Page<ReturnOrderDto> page = returnOrderService.getReturnOrdersByPage(userService.getDisAccount(),node);
//		Logger.info("查询退货单的结果：{}",page);
		return ok(Json.toJson(page));
	}

	/**
	 * 后台获取退货单列表
	 *
	 * @return
	 */
	@ALogin
	@DivisionMember
	public Result getReturnOrdersBackstage() {
		JsonNode node = request().body().asJson();
		Logger.info("后台查询退货单的参数：{}",node);
		Page<ReturnOrderDto> page = returnOrderService.getReturnOrdersByPage(null,node);
		return ok(Json.toJson(page));
	}
	
	/**
	 * 全选搜索结果所有，统计被选中项的退款金额合计
	 * @return
	 */
	@ApiOperation(value = "合同费用管理列表", notes = "", nickname = "", httpMethod = "POST")
	@ApiImplicitParams({
	@ApiImplicitParam(name = "body", value = "全选搜索结果所有，统计被选中项的退款金额合计", required = true, paramType = "body" 
		,defaultValue = "{\"categoryId\":4680,\"searchText\":\"\",\"typeId\":\"\",\"warehouseId\":2024}"
			) })
	public Result getTotalUserExpectReturnAmount4MatchedConditions() {
		JsonNode node = request().body().asJson();
		Logger.info("后台查询退货单的参数：{}",node);
		Map<String, Object> result = Maps.newHashMap();
		if (node==null) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result)); 
		}
		
		Double total = returnOrderService.getTotalUserExpectReturnAmount4MatchedConditions(node);
		result.put("suc", true);
		result.put("result", total);
		return ok(Json.toJson(result)); 
	}

	/**
	 * 后台获取待审核退货单列表
	 *
	 * @return
	 */
	@ALogin
	@DivisionMember
	public Result get2BeAuditedReturnOrdersBackstage() {
		JsonNode node = request().body().asJson();
		Logger.info("后台查询退货单的参数：{}",node);
		Page<ReturnOrderDto> page = returnOrderService.get2BeAuditedReturnOrders(null, node);
		return ok(Json.toJson(page));
	}

	/**
	 * 审核退货单
	 * @return
	 */
	@DivisionMember
	@ALogin
	public Result audit(){
		JsonNode node = request().body().asJson();
		boolean lackOfParams = (node == null
				|| !node.has("returnOrderNo")
				|| StringUtils.isBlankOrNull(node.get("returnOrderNo").asText())
				|| !node.has("passed"));
		// 检查基本参数
		if(lackOfParams){
			return ok(Json.toJson(checkAuditParamErrorResult(null)));
		}

		// passed值检查
		int passed = node.get("passed").asInt();
		if(passed!=0 && passed!=1){
			return ok(Json.toJson(checkAuditParamErrorResult(null)));
		}

		// 审核通过才有金额
		if (passed == 1) {
			if (!node.has("actualTotalReturnAmount")) {
				return ok(Json.toJson(checkAuditParamErrorResult(null)));
			}
			// 金额格式检查
			String actualTotalReturnAmount = node.get("actualTotalReturnAmount").asText();
			if (!RegExpValidatorUtils.isMoney(actualTotalReturnAmount)) {
				return ok(Json.toJson(checkAuditParamErrorResult(null)));
			}
		}
		
		// 审核备注长度检查
		String auditRemarks = null;
		if (node.has("auditRemarks")) {
			auditRemarks = node.get("auditRemarks").asText();
			if(StringUtils.lengthBiggerThan(auditRemarks,500)){
				return ok(Json.toJson(checkAuditParamErrorResult("审核备注不能超过500字符")));
			}
		}

		Logger.info("审核退货单的参数：{}",node);

		AuditReturnOrderParams params = new AuditReturnOrderParams();
		params.setReturnOrderNoList(Lists.newArrayList(node.get("returnOrderNo").asText()));
		params.setAuditUser(userService.getAdminAccount());
		params.setPassed(passed);
		params.setAuditRemarks(auditRemarks);
		if(passed==1){
			params.setActualTotalReturnAmount(node.get("actualTotalReturnAmount").asDouble());
		}
		Map<String, Object> result = returnOrderService.audit(params);
		return ok(Json.toJson(result));
	}

	/**
	 * 批量审核退货单
	 * @return
	 */
	@DivisionMember
	@ALogin
	public Result batchAudit(){
		JsonNode node = request().body().asJson();
		Logger.info("批量审核退货单的参数：{}",node);
		boolean lackOfParams = (node == null
				|| !node.has("returnOrderNos")
				|| !node.has("passed"));
		// 检查基本参数
		if(lackOfParams){
			return ok(Json.toJson(checkAuditParamErrorResult(null)));
		}

		// passed值检查
		int passed = node.get("passed").asInt();
		if(passed!=0 && passed!=1){
			return ok(Json.toJson(checkAuditParamErrorResult(null)));
		}
		
		// 审核备注长度检查
		String auditRemarks = null;
		if(node.has("auditRemarks")){
			auditRemarks = node.get("auditRemarks").asText();
			if(StringUtils.lengthBiggerThan(auditRemarks,500)){
				return ok(Json.toJson(checkAuditParamErrorResult("审核备注不能超过500字符")));
			}
		}

		AuditReturnOrderParams params = new AuditReturnOrderParams();
		Set<String> returnOrderNoSet = Sets.newHashSet();
		for(Iterator<JsonNode> it = node.get("returnOrderNos").iterator();it.hasNext();){
			returnOrderNoSet.add(it.next().asText().trim());
		}
		params.setReturnOrderNoList(Lists.newArrayList(returnOrderNoSet));
		params.setAuditUser(userService.getAdminAccount());
		params.setPassed(node.get("passed").asInt());
		params.setAuditRemarks(auditRemarks);
		// 批量审核没有金额，以用户填的为主
//		params.setActualTotalReturnAmount(node.get("actualTotalReturnAmount").asDouble());
		Map<String, Object> result = returnOrderService.batchAudit(params);
		return ok(Json.toJson(result));
	}

	/**
	 * 
	 * @param msg 为null时，默认msg为“参数错误”
	 * @return
	 */
	private Map<String, Object> checkAuditParamErrorResult(String msg){
		Map<String, Object> result = Maps.newHashMap();
		result.put("suc", false);
		result.put("msg", StringUtils.isBlankOrNull(msg)?"参数错误":msg);
		return result;
	}
	
}
