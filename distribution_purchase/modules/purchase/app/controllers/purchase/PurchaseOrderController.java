package controllers.purchase;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.purchase.IPurchaseOrderAuditService;
import services.purchase.IPurchaseOrderDetailService;
import services.purchase.IPurchaseOrderService;
import services.purchase.IUserService;
import utils.purchase.DateUtils;
import utils.purchase.ExportUtil;
import utils.purchase.JsonCaseUtil;
import utils.purchase.ParametersUtil;
import utils.purchase.StringUtils;
import annotation.ALogin;
import annotation.DivisionMember;
import annotation.Login;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import constant.purchase.Constant;
import dto.JsonResult;
import dto.purchase.CancelPurchaseOrderParam;
import dto.purchase.InStorageIterm;
import dto.purchase.ReturnMess;
import dto.purchase.StatisIterm;
import dto.purchase.ViewPurchaseIterm;
import dto.purchase.ViewPurchaseOrder;
import entity.purchase.PurchaseAudit;
import entity.purchase.PurchaseOrderAuditLog;
import forms.purchase.FinanceAuditParam;

/**
 * 描述：分销商采购控制类
 * 
 * @author hanfs
 *
 */
@Api(value="/采购单",description="purchaseOrder")
public class PurchaseOrderController extends Controller {
	@Inject	private IPurchaseOrderService purchaseOrderService;
	@Inject	private IPurchaseOrderDetailService purchaseOrderDetailService;
	@Inject	private IUserService userService;
	@Inject	private IPurchaseOrderAuditService auditService;

	/**
	 * 根据单号获取采购单详情
	 * 
	 * @author 都云涛
	 * @return
	 */
	@ApiOperation(value = "根据单号获取采购单详情", notes = "", nickname = "", httpMethod = "POST")
	@ApiImplicitParams({
	@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
		,defaultValue = "{\"pno\":\"CG201702150000014997\"}"
			) })
	public Result getPurchaseDetail() {
		JsonNode main = request().body().asJson();
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}

		return ok(Json.toJson(purchaseOrderDetailService.getPurchaseDetailStockInfo(main.get("pno").textValue())));
	}

	/**
	 * 下单
	 * 
	 * @return
	 */
	@Login
	public Result orderPurchase() {
		JsonNode node = request().body().asJson();
		Logger.debug(">>>orderPruchase>>>>>node>>" + node.toString());
		return ok(Json.toJson(purchaseOrderService.orderPurchase(node, userService.getDisAccount())));
	}

	/**
	 * DivisionMember区分用户，后台业务员仅查询自己关联的分销商
	 * 
	 * 采购单展示
	 * 
	 * @return
	 */
	@DivisionMember
	public Result viewPurchase() {
		JsonNode node = request().body().asJson();
		if (null == node) {
			return internalServerError();
		}
		
		String account = userService.getAccounts();
		List<String> accounts = null;
		if (!StringUtils.isBlankOrNull(account)) {
			accounts = Arrays.asList(account.split(","));
		}
		Logger.debug(">>>viewPurchase>>>>>{}", node);
		return ok(Json.toJson(purchaseOrderService.queryPurchases(node, accounts)));
	}
	
	
	/**
	 * 新的接口，适配前端使用的jqgrid
	 * @return
	 */
	@DivisionMember
	public Result viewPurchaseNew() {
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		// 转成json
		ObjectNode node = Json.newObject();
		for(Map.Entry<String, String[]> entry : params.entrySet()){
			if(entry.getValue()!=null){
				// 将jqgrid传过来的4个参数重新命名
				if("page".equals(entry.getKey())){
					node.put("pageCount", ParametersUtil.getIntegerParam(params,"page",null));
				}else if("rows".equals(entry.getKey())){
					node.put("pageSize", ParametersUtil.getIntegerParam(params,"rows",null));
				}else if("sidx".equals(entry.getKey())){
					node.put("sort", ParametersUtil.getStringParam(params,"sidx",null));
				}else if("sord".equals(entry.getKey())){
					node.put("filter", ParametersUtil.getStringParam(params,"sord",null));
				}else if("isPro".equals(entry.getKey())){
					node.put("isPro", ParametersUtil.getBooleanParam(params,"isPro",null));
				}else if("isChoose".equals(entry.getKey())){
					node.put("isChoose", ParametersUtil.getBooleanParam(params,"isChoose",null));
				}else{
					node.put(entry.getKey(), entry.getValue()[0]);				
				}
			}
		}
		Logger.info("viewPurchaseNew查询参数：{}",node);
		String account = userService.getAccounts();
		List<String> accounts = null;
		if (!StringUtils.isBlankOrNull(account)) {
			accounts = Arrays.asList(account.split(","));
		}
		Logger.debug(">>>viewPurchase>>>>>{}", node);
		ViewPurchaseIterm purchases = purchaseOrderService.queryPurchases(node, accounts);
		return ok(Json.toJson(purchases));
	}

	/**
	 * 查询一个订单及其明细
	 * @return
	 */
	@ApiOperation(value = "查询一个订单及其明细", notes = "", nickname = "", httpMethod = "POST")
	@ApiImplicitParams({
	@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
		,defaultValue = "{\"orderId\":1}"
			) })
	public Result viewPurchaseOrderDetailById() {
		JsonNode node = request().body().asJson();
		if (null == node) {
			return internalServerError();
		}

		return ok(Json.toJson(purchaseOrderService
				.viewPurchaseOrderDetailById(node.get("orderId").asInt())));
	}

	/**
	 * 更新订单状态
	 * 
	 * @return
	 */
	@Login
	public Result cancelPurchaseOrder() {
		ReturnMess returnMess = new ReturnMess("0", "");
		try {
			JsonNode node = request().body().asJson();
			Logger.debug(">>>cancelPurchaseOrder>>>>>node>>" + node.toString());
			if (node.has("purchaseNo") && node.has("flag")) {
				CancelPurchaseOrderParam param = new CancelPurchaseOrderParam();
				param.setPurchaseNo(node.get("purchaseNo").asText());
				param.setFlag(node.get("flag").asText());
				Double actualAmount = node.has("actualAmount") ? node.get("actualAmount").asDouble() : null;
				param.setActualAmount(actualAmount);
				param.setActualAmount4SO(actualAmount);
				param.setPayType(node.has("payType") ? node.get(
						"payType").asText() : null);
				// 交易时间
				String payDate = null;
				if (node.has("payDate")) {
					payDate = node.get("payDate").asText();
				}
				param.setPayDate(payDate);
				// 交易号
				param.setTradeNo(node.has("tradeNo") ? node.get("tradeNo").asText() : null);
				
				returnMess = purchaseOrderService.cancelPurchaseOrder(param);
			} else {
				returnMess = new ReturnMess("1", "input error!输入参数错误");
			}
		} catch (Exception e) {
			Logger.error("EXCEPTION", e);
			returnMess = new ReturnMess("1", "Exception:异常情况!" + e.getMessage());
		}
		Logger.debug(">>cancelPurchaseOrder>>returnMess>>>>"
				+ Json.toJson(returnMess));
		return ok(Json.toJson(returnMess));
	}
	
	/**
	 * 描述：现金支付接口
	 * 
	 * @return
	 */
	@Login
	public Result payedByCash() {
		JsonNode json = request().body().asJson();
		ReturnMess rm = purchaseOrderService.payedByCash(json);
		return ok(Json.toJson(rm));
	}

	/**
	 * 1.通过采购单号查询微仓、物理仓参数 2.物理库存变更记录操作，以下情况可能会使用到 参数数据格式 { "email":"xxx",
	 * "purchaseNo":"xxx批次号", "purchaseOrderId":"xxx", "status":"xxx", "pros":[
	 * { "sku":"xxx", "qty":"xxx", "warehouseId":"xxx", "purchasePrice":"xxx",
	 * "productName":"xxx" },... ] }
	 *
	 * @return
	 */
	public Result getInfoByPurchaseOrderNo() {
		InStorageIterm iterm = new InStorageIterm();
		JsonNode node = request().body().asJson();
		Logger.debug(">>getInfoByPurchaseOrderNo>node>>" + node.toString());
		if (node.has("purchaseOrderNo") && node.has("flag")) {
			String purchaseOrderNo = node.get("purchaseOrderNo").asText();
			String flag = node.get("flag").asText();
			iterm = purchaseOrderService.getInfoByNo(purchaseOrderNo, flag);
		} else {
			iterm.setReturnMess(new ReturnMess("1", "input error!输入参数错误"));
		}
		if (null == iterm) {
			return ok("");
		}
		return ok(Json.toJson(iterm));
	}

	/**
	 * 采购单的基础信息
	 * 
	 * @return
	 */
	@ApiOperation(value = "采购单的基础信息", notes = "", nickname = "", httpMethod = "POST")
	@ApiImplicitParams({
	@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
		,defaultValue = "{\"purchaseOrderNo\":\"CG201702150000014997\"}"
			) })
	public Result getPurchaseSimpleInfo() {
		JsonNode node = request().body().asJson();
		String purchaseOrderNo = node.get("purchaseOrderNo").asText();

		ObjectNode paramNode = Json.newObject();
		paramNode.put("pageSize", 10);
		paramNode.put("pageCount", 0);
		paramNode.put("seachFlag", purchaseOrderNo);

		return ok(Json.toJson(purchaseOrderService.purchaseSimpleInfo(paramNode, null)));
	}

	/**
	 * 是否有效果支付时间内
	 * 
	 * @return
	 */
	@ApiOperation(value = "是否有效果支付时间内", notes = "", nickname = "", httpMethod = "POST")
	@ApiImplicitParams({
	@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
		,defaultValue = "{\"purchaseOrderNo\":\"CG201702150000014997\"}"
			) })
	public Result isValiPayDate() {
		JsonNode node = request().body().asJson();
		Logger.debug(">>getInfoByPurchaseOrderNo>node>>" + node.toString());
		
		if (node.has("purchaseOrderNo")) {
			String purchaseOrderNo = node.get("purchaseOrderNo").asText();
			ReturnMess rm = purchaseOrderService.isValiPayDate(purchaseOrderNo);
			Logger.debug(">>isValiPayDate>>rm>>" + Json.toJson(rm).toString());
			return ok(Json.toJson(rm));
		}
		
		ReturnMess rm = new ReturnMess("1", "input error!输入参数错误");
		Logger.debug(">>isValiPayDate>>rm>>" + Json.toJson(rm).toString());
		return ok(Json.toJson(rm));
	}

	/**
	 * 统计采购订单（某段时间内）
	 * 
	 * @return
	 */
	public Result statisPurchaseOrder() {
		JsonNode node = request().body().asJson();
		Logger.debug(">>statisPurchaseOrder>node>>" + node.toString());
		if (!node.has("email")) {
			StatisIterm iterm = new StatisIterm();
			iterm.setReturnMess(new ReturnMess("1", "input error!输入参数错误"));
			Logger.debug(">>statisPurchaseOrder>iterm>>" + Json.toJson(iterm).toString());
			return ok(Json.toJson(iterm));
		}
		
		StatisIterm iterm = purchaseOrderService.statisPurchaseOrder(node);
		Logger.debug(">>statisPurchaseOrder>iterm>>" + Json.toJson(iterm).toString());
		return ok(Json.toJson(iterm));
	}

	/**
	 * 通过id查询采购订单
	 * 
	 * @return
	 */
	@ApiOperation(value = "查询采购单", notes = "", nickname = "", httpMethod = "POST")
	@ApiImplicitParams({
	@ApiImplicitParam(name = "body", value = "参数可以是id或pNo，2选1", required = true, paramType = "body" 
		,defaultValue = "{\"pNo\":\"CG201702150000014997\"}"
			) })
	public Result getOrderById() {
		JsonNode node = request().body().asJson();
		Logger.debug(">>>viewPurchase>>>>>node>>" + node.toString());
		if (node.has("id") || node.has("pNo")) {
			ViewPurchaseIterm iterm = purchaseOrderService.getOrderById(node);
			Logger.debug(">>>>getOrderById>>iterm>" + Json.toJson(iterm).toString());
			return ok(Json.toJson(iterm));
		}
		
		ViewPurchaseIterm iterm = new ViewPurchaseIterm();
		iterm.setReturnMess(new ReturnMess("1", "input error!输入参数错误"));
		Logger.debug(">>>>getOrderById>>iterm>" + Json.toJson(iterm).toString());
		return ok(Json.toJson(iterm));
	}

	/**
	 * 导出采购进货单数据
	 * @return
	 */
	public Result export() {
		Map<String, Object> result = Maps.newHashMap();
		Map<String, String[]> map = request().queryString();
		if (null == map) {
			Logger.info("参数错误");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		String[] header = map.get("header");
		if (null == header || header.length <= 0) {
			Logger.info("列头不能为空。");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		
		Logger.info("开始导出采购进货单数据");
		Map<String, Object> params = parseParams(map);
		String account = userService.getAccounts();
		List<String> accounts = null;
		if (!StringUtils.isBlankOrNull(account)) {
			accounts = Arrays.asList(account.split(","));
			params.put("accounts", accounts);
		}
		response().setHeader("Content-disposition",
				"attachment;filename=purchases.xls");
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		List<ViewPurchaseOrder> list = purchaseOrderService
				.getExportList(params);
		Logger.info("导出采购进货单数据条数为 ： " + list.size());
		return ok(ExportUtil.export("purchases.xls", header,
				Constant.PURCHASE_TABLE_MAP, list));
	}

	/**
	 * status seachFlag sorderDate spaydate eorderDate epaydate
	 * 
	 * @param map
	 * @return
	 */
	private Map<String, Object> parseParams(Map<String, String[]> map) {
		Map<String, Object> params = Maps.newHashMap();
		try {
			if (map.containsKey("status") && Integer.parseInt(map.get("status")[0]) != 9) {// 9表示所有订单状态
				params.put("status", Integer.parseInt(map.get("status")[0]));
			}
			params.put("seachFlag", map.containsKey("seachFlag") ? map.get("seachFlag")[0] : null);
			params.put("sorderDate", map.containsKey("sorderDate") ? map.get("sorderDate")[0] : null);
			params.put("spaydate", map.containsKey("spaydate") ? map.get("spaydate")[0] : null);
			params.put("isPro", map.containsKey("isPro") ? Boolean.valueOf(map.get("isPro")[0]) : null);
			params.put("isChoose", map.containsKey("isChoose") ? Boolean.valueOf(map.get("isChoose")[0]) : null);
			String eorderDate = map.containsKey("eorderDate") ? map.get("eorderDate")[0] : null;
			String epaydate = map.containsKey("epaydate") ? map.get("epaydate")[0] : null;
			if (StringUtils.isNotBlankOrNull(eorderDate)) {
				Calendar ca = Calendar.getInstance();
				ca.setTime(DateUtils.string2date(eorderDate, DateUtils.FORMAT_DATE_PAGE));
				ca.add(Calendar.DATE, 1);
				params.put("eorderDate", DateUtils.date2string(ca.getTime(), DateUtils.FORMAT_DATE_PAGE));
			}
			if (StringUtils.isNotBlankOrNull(epaydate)) {
				Calendar ca = Calendar.getInstance();
				ca.setTime(DateUtils.string2date(epaydate, DateUtils.FORMAT_DATE_PAGE));
				ca.add(Calendar.DATE, 1);
				params.put("epaydate", DateUtils.date2string(ca.getTime(), DateUtils.FORMAT_DATE_PAGE));
			}
			return params;
		} catch (Exception e) {
			Logger.error("解析参数错误：" + e);
			return null;
		}
	}

	/**
	 * 查询订单关联的活动
	 * 
	 * @param orderno
	 * @return
	 */
	public Result getActive(String orderno) {
		if (StringUtils.isBlankOrNull(orderno)) {
			return ok("");
		}
		return ok(Json.toJson(purchaseOrderService.getActive(orderno)));
	}

	/**
	 * 添加赠品
	 * 
	 * @return
	 */
	@ALogin
	public Result addGift() {
		JsonNode node = request().body().asJson();
		if (node == null || node.size() <= 0) {
			return ok("");
		}

		Logger.debug(">>>orderPruchase>>>>>node>>" + node.toString());
		InStorageIterm iterm = purchaseOrderService.addGift(node);
		return ok(Json.toJson(iterm));
	}

	/**
	 * 批量更新采购订单
	 */
	@Login
	public Result updateOrders() {
		JsonNode node = request().body().asJson();
		if (node == null || node.size() <= 0 || !node.has("orderid")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "传入参数有误");
			return ok(Json.toJson(result));
		}
		return ok(Json.toJson(purchaseOrderService.updateOrders(node)));
	}

	/**
	 * 测试 处理失效采购单接口
	 * 
	 * @author zbc
	 * @since 2016年8月23日 下午3:49:41
	 */
	public Result dealInvalidOrders() {
		purchaseOrderService.batchInvalid();

		return ok(Json.toJson("ok"));
	}

	/**
	 * 获取采购单操作日志---数据来源于t_purchase_order_audit_logs、t_purchase_gift_op_record
	 * @param purchaseNo
	 * @return
	 */
	public Result orderOperateRecord(String purchaseNo) {
		Map<String, Object> result = Maps.newHashMap();
		if (StringUtils.isBlankOrNull(purchaseNo)) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		result.put("suc", true);
		result.put("msg", purchaseOrderService.orderOperateRecord(purchaseNo));
		return ok(Json.toJson(result));
	}

	/**
	 * 线下转账提交审核-前台
	 * 
	 * @return
	 */
	@Login
	public Result submitAudit() {
		MultipartFormData formData = request().body().asMultipartFormData();
		Map<String, String[]> params = formData.asFormUrlEncoded();
		FilePart file = formData.getFile("image");
		return ok(purchaseOrderService.submitAudit(params, file,
				userService.getDisAccount()));
	}

	/**
	 * 线下转账提交审核-后台
	 * 
	 * @return
	 */
	@ALogin
	public Result backStageAudit() {
		MultipartFormData formData = request().body().asMultipartFormData();
		Map<String, String[]> params = formData.asFormUrlEncoded();
		FilePart file = formData.getFile("image");
		if (params.get("email") == null || params.get("email").length <= 0) {
			return ok("false");
		}
		// email由前台传
		String email = params.get("email")[0];
		return ok(purchaseOrderService.submitAudit(params, file, email));
	}

	/**
	 * 后台查询申请，线下转账的
	 * 
	 * @return
	 */
//	@ALogin
//	@Deprecated
//	public Result getApplyOnLine() {
//		JsonNode node = request().body().asJson();
//		if (node == null || node.size() < 0) {
//			return internalServerError();
//		}
//		String param = node.toString();
//		return ok(Json.toJson(iPurchaseOrderService.queryAudits(param)));
//	}

	/**
	 * 获取审核申请付款截图
	 * 
	 * @Author LSL on 2016-10-24 15:55:40
	 */
	public Result getApplyImage() {
		String id = request().getQueryString("id");
		if (Strings.isNullOrEmpty(id)) {
			return badRequest("参数值不存在");
		}
		PurchaseAudit apply = purchaseOrderService.getTransferApply(Integer
				.valueOf(id));
		if (apply != null
				&& StringUtils.isNotBlankOrNull(apply.getScreenshotUrl())) {
			File file = new File(apply.getScreenshotUrl());
			if (file != null && file.exists()) {
				return ok(file);
			}
		}
		return ok("");
	}

	/**
	 * 总到仓价
	 * 
	 * @return
	 */
	@ALogin
	public Result getTotalArriveWarehousePrice() {
		JsonNode node = request().body().asJson();
		String purchaseOrderNo = node.get("purchaseOrderNo").asText();
		Double totalArriveWarehousePrice = purchaseOrderService
				.getTotalArriveWarehousePrice(purchaseOrderNo);

		HashMap<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("totalArriveWarehousePrice", totalArriveWarehousePrice);
		Logger.info("{}查询到的总到仓价==={}", purchaseOrderNo, newHashMap);
		return ok(Json.toJson(newHashMap));
	}

	/**
	 * 客服确认
	 * 
	 * @return
	 */
	@ALogin
	public Result auditByCustomerService() {
		JsonNode node = request().body().asJson();
		Logger.info("客服确认参数----{}", node);
		return ok(Json.toJson(auditService.auditByCustomerService(node)));
	}

	/**
	 * 财务确认
	 * 
	 * @return
	 */
	@ALogin
	public Result auditByFinance() {
		JsonNode node = request().body().asJson();
		Logger.info("财务审核参数----{}", node);

		FinanceAuditParam param = new FinanceAuditParam();
		param.setAuditUser(userService.getAdminAccount());
		param.setPurchaseOrderNo(node.get("purchaseOrderNo").asText());
		param.setPaid(node.has("paid") ? node.get("paid").asInt() : 1);// 支付信息：未支付0/已支付1
		if (node.has("paymentRemark")) {
			param.setPaymentRemark(node.get("paymentRemark").asText());
		}
		if (node.has("receivedAmount")) {
			param.setReceivedAmount(node.get("receivedAmount").asDouble());
		}
		if (node.has("receivedTime")) {
			param.setReceivedTime(node.get("receivedTime").asText());
		}
		if (param.getPaid() == 0) {
			param.setAuditReason(node.get("auditReason").asText());
		}
		param.setProfitPassed(node.get("profitPassed").asBoolean());
		param.setProfitRemark(node.get("profitRemark").asText());

		Map<String, Object> result = auditService.auditByFinance(param);
		return ok(Json.toJson(result));
	}
	
	public Result getAOrderAllAuditLogs() {
		JsonNode node = request().body().asJson();
		Logger.info("获取一个采购单的所有审核记录----{}", node);
		List<PurchaseOrderAuditLog> logs = auditService.getAOrderAllAuditLogs(node.get("purchaseOrderNo").asText());
		HashMap<Object, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("allAuditLogs", logs);
		return ok(Json.toJson(newHashMap));
	}

	/**
	 * 获取最新的一条财务审核记录
	 * @return
	 */
	public Result getOrderAuditLastestLog() {
		JsonNode node = request().body().asJson();
		Logger.info("获取最新一条审核记录的参数----{}", node);
		PurchaseOrderAuditLog latestLog = auditService
				.getOrderAuditLastestLog(node.get("purchaseOrderNo").asText());
		HashMap<Object, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("latestLog", latestLog);
		return ok(Json.toJson(newHashMap));
	}

	/**
	 * 更新采购单价格
	 * 
	 * @author zbc
	 * @since 2016年12月2日 下午6:07:41
	 */
	@Login
	public Result changeOrderPrice() {
		JsonNode node = request().body().asJson();
		if (node == null) {
			Map<String, Object> res = Maps.newHashMap();
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res));
		}
		
		return ok(Json.toJson(purchaseOrderService.changeOrderPrice(node.toString())));
	}
	
	/**
	 * 根据email更改订单昵称
	 * @author lzl
	 * @since 2016年12月22日下午3:36:59
	 */
	public Result changeNickName() {
		JsonNode node = request().body().asJson();
		if (node == null) {
			ObjectNode result = Json.newObject();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(result.toString());
		}

		String param = node.toString();
		Logger.info("changeNickName---->"+ param);
		return ok(purchaseOrderService.changeNickNameByEmail(param));
	}
	
	
	/**
	 * 订单
	 * @param orderNo
	 * @author zbc
	 * @since 2016年12月26日 下午7:08:27
	 */
	public Result  orderLock(String orderNo){
		return ok(Json.toJson(purchaseOrderService.lock(orderNo)));
	}
	
	/**
	 * 合并发货，修改缺货采购单运费
	 * @author zbc
	 * @since 2017年5月24日 上午11:40:53
	 * @return
	 */
	public Result changeFreight(){
		JsonNode json = request().body().asJson();
		Logger.info("更新运费参数:{}",json);
		if(JsonCaseUtil.checkParam(json,new String[]{})){
			return ok(Json.toJson(purchaseOrderService.changeFreight(json.toString())));
		}
		return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
	}
}

