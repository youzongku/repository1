package controllers.sales;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.ApiPermission;
import controllers.annotation.Login;
import dto.JsonResult;
import dto.sales.ExportSaleLogistic;
import dto.sales.SaleOrderInfo;
import entity.sales.OperateRecord;
import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.sales.IOperateRecordService;
import services.sales.ISaleBaseService;
import services.sales.ISaleDetailsService;
import services.sales.ISaleInvoiceService;
import services.sales.ISaleLockService;
import services.sales.ISaleMainService;
import services.sales.ISaleService;
import services.sales.IUserService;
import util.sales.Constant;
import util.sales.ExportUtil;
import util.sales.IDUtils;
import util.sales.JsonCaseUtil;
import util.sales.SaleOrderStatus;
import util.sales.StringUtils;

/**
 * 描述：客户端销售发货控制类
 * 
 * @author hanfs
 *
 */
@Api(value="/销售单模块",description="Sales Order")
public class SalesController extends Controller {
	@Inject private ISaleService saleService;
	@Inject private ISaleMainService saleMainService;
	@Inject private ISaleBaseService saleBaseService;
	@Inject private ISaleDetailsService saleDetailsService;
	@Inject private IOperateRecordService operateRecordService;
	@Inject private IUserService userService;
	@Inject private ISaleLockService lockService;
	@Inject private ISaleInvoiceService invoiceService;
	
	
	/**
	 * 更新销售发货订单主表的虚拟支付信息
	 * 
	 * 销售订单号  支付时间，支付方式，支付交易号，支付金额
	 * 参数格式：
	 * {
	 * 		saleOrderNo:xxx,
	 * 		payDate:xxx,
	 * 		payNo:xxx,
	 * 		payType:xxx,
	 * 		payer:xxx,
	 * 		paryerIdcard:xxx,
	 * 		currency:xxx,
	 * 		status:xxx
	 * }
	 * @return
	 */
	@Login
	@BodyParser.Of(BodyParser.Json.class)
	public Result updSalesOrderVirtualPayInfo() {
		JsonNode main = request().body().asJson();
		Logger.debug("在线支付审核回调:[{}]",main);
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		return ok(Json.toJson(saleMainService.updSalesOrderVirtualPayInfo(main.toString(),request().remoteAddress())));
	}
	
	/**
	 * 获取一段时间之内的销售发货单数量
	 * @return
	 */
	@Login
	@BodyParser.Of(BodyParser.Json.class)
	public Result getSalesOrderCount() {
		JsonNode main = request().body().asJson();
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		String param = main.toString();
		return ok(Json.toJson(saleService.getSalesOrderCount(param,null)));
	}
	
	/**
	 * 根据orderId显示订单详细列表
	 * 
	 * @return
	 * @author ye_ziran
	 * @since 2016年1月11日 上午9:32:15
	 */
	public Result showSalesOrderDetail() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> param = Maps.newHashMap();
		JsonNode json = request().body().asJson();
		Integer orderId = json.get("orderId").asInt();
		SaleOrderInfo saleOrderInfo = new SaleOrderInfo();
		param.put("id", orderId);
		SaleMain saleMain = saleMainService.getSaleMainByIdAndAccounts(param);
		if (saleMain == null ) {
			resultMap.put("suc", false);
			resultMap.put("msg", "没有查询到指定详情");
			return ok(Json.toJson(resultMap));
		}
		
		List<SaleDetail> orderDetails = saleDetailsService.getAllSaleDetailsByOrderId(orderId);
		SaleBase saleBase = saleBaseService.getSaleBaseByOrderId(orderId);
		saleOrderInfo.setSaleDetails(orderDetails);
		saleOrderInfo.setSaleBase(saleBase);
		saleMain.setFrontOrManager(SaleMain.STATUS_MANAGER);
		saleOrderInfo.setSaleMain(saleMain);
		resultMap.put("result", true);
		resultMap.put("saleOrderInfo", saleOrderInfo);
		return ok(Json.toJson(resultMap));
	}

	/**
	 * 查询销售订单主要信息
	 * 参数参考
	 * {
		    "status":"xxx",
		    "desc":"订单号或者收件人",
		    "email":"xxx"
	   }
	 * @return
	 */
	@Login
	public Result getSaleOrderList() {
		JsonNode main = request().body().asJson();
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		String email =userService.getDisAccount();
		return ok(Json.toJson(saleService.selectSaleOrderListDto(main,email)));
	}
	
	/**
	 * 查询销售订单商品详细信息
	 * 参数格式参考
	 * {
	 *     orderId:"xxx";
	 * }
	 * @return
	 */
	@Login
	public Result getSaleOrderDetailList() {
		JsonNode main = request().body().asJson();
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		return ok(Json.toJson(saleService.selectSaleOrderDetail(main)));
	}

	/**
	 * 取消要做校验     
	 * 校验是否本人操作 策略:
	 * 1 登录拦截    用户使用
	 * 2 传email 异常处理时可用	
	 * 
	 * 校验 可直接把订单已关闭标识 更新为true
	 * 后续调用判断已关闭标识 如果为false 才给更新
	 * /sales/undoClose?so=XXX
	 * 预留接口，强行更改关闭状态下的订单为 未关闭
	 * 还原库存统一还会微仓
	 * 还原规则
	 * 内部分销商如果 缺货采购单 已扣库存 还回云仓
	 * 外部分销商如果 缺货采购单 已扣库存还回微仓  并返还运费
	 * 待付款: 直接取消订单   并还原库存(如果有扣则还原)
	 * 待支付运费 : 还原库存  外部分销商退运费
	 * 待确认: 
	 *     采购单已完成:还原库存  退运费
	 *     采购单待确认: 云仓库存未扣，微仓未入，退钱
	 * 审核不通过:全部还原，逻辑同上 （还原规则） 
	 * @return
	 */
	@Login
	public Result cancelSalesOrder(){
		JsonNode main = request().body().asJson();
		Logger.info("关闭订单参数:[{}]",main);
		Map<String,Object> res = Maps.newHashMap();
		if(main == null){
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res));
		}
		String account = JsonCaseUtil.jsonToString(main.get("em")) != null?
				JsonCaseUtil.jsonToString(main.get("em")):userService.getDisAccount();
		return ok(Json.toJson(lockService.cancelOrder(main,account)));
	}
	
	/**
	 * 门店订单关闭：内部实现与BBC一致
	 * @return
	 */
	@ApiPermission
	public Result cancelStoreOrder(){
		JsonNode main = request().body().asJson();
		Logger.info("门店关闭订单参数:[{}]",main);
		Map<String,Object> res = Maps.newHashMap();
		if(main == null){
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res));
		}
		return ok(Json.toJson(lockService.cancelOrder(main,null)));
	}
	
	/**
	 * 更新发货单状态<br>
	 * 此接口即将废除，不建议使用  by huangjc 2016.12.20
	 * @return
	 */
	@Login
	public Result updateSalesOrderStatus() {
		JsonNode main = request().body().asJson();
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		Logger.info("<<<<<<<<<<<<<更新状态参数:[{}]>>>>>>>",main);
		
		SaleMain sm = new SaleMain();
		sm.setId(main.get("id").asInt());
		// 设置销售订单状态
		sm.setStatus(main.get("status").asInt());
		
		//当设置发货单状态为待采购，无论通知发货与否，待采购的订单一定是未通知发货的状态
        if(main.has("actualPay")){
			sm.setOrderActualPayment(main.get("actualPay").asDouble());
		}
        
        // 状态判断，不同的状态做不同的操作 add by huangjc 2016.12.6
		if(sm.getStatus() == SaleOrderStatus.WAITING_PAY){
			sm.setIsNotified(0);
		}else if(sm.getStatus() == SaleOrderStatus.CLOSED_BY_CUSTOMER){
			//如果要将状态变更为“取消”，那么返回该订单的发货详情，库存还原
			//添加取消订单的操作记录
			OperateRecord  or = new OperateRecord();
			or.setOperateType(4);
			or.setResult(1);
			or.setComment("取消订单");
			or.setOrderId(sm.getId());
			or.setEmail("分销商用户");
			boolean saveFlag = operateRecordService.saveOperateRecord(or);
			Logger.info("销售订单状态改为取消添加操作记录结果："+(saveFlag?"添加成功":"添加失败"));
			
			boolean res = saleMainService.updateByPrimaryKeySelective(sm);
			Logger.info("更改客户订单id为[{}]的状态标识为：[{}],执行结果为：{}",sm.getId(),sm.getStatus(),res);
			//add by xuse 更新优惠码信息
			saleMainService.updateCouponsState(sm);
			return ok(Json.toJson(saleDetailsService.getAllSaleDetailsByOrderId(sm.getId())));
		}else if(sm.getStatus() == SaleOrderStatus.WAITING_DELIVERY_SIX) {
			sm.setPaymentNo(IDUtils.getPayNo());
			sm.setPayDate(new Date());
			sm.setPaymentType("system");
			sm.setCurrency("CNY");
		}else if(sm.getStatus() == SaleOrderStatus.RECEIVED){
            //确认收货时间
			sm.setConfirmReceiptDate(new Date());
		}

		boolean res = saleMainService.updateByPrimaryKeySelective(sm);
		
		//add by xuse 更新优惠码信息
		saleMainService.updateCouponsState(sm);
		Logger.info("更改客户订单id为[{}]的状态标识为：[{}],执行结果为：{}",sm.getId(),sm.getStatus(),res);
		return ok(Json.toJson(res));
	}
	
	/***************拆分updateSalesOrderStatus方法start**********************/
	/**
	 * 取消通知发货，订单变为待客户确认
	 * @return
	 */
	public Result cancelNotification() {
		// sid:1, isNotified:1
		JsonNode main = request().body().asJson();
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		HashMap<String, Object> result = Maps.newHashMap();
		SaleMain saleMainOrderByID = saleMainService.getSaleMainOrderByID(main.get("id").asInt());
		// 状态为3才能取消通知发货
		if(saleMainOrderByID.getStatus()!=SaleOrderStatus.WAITING_AUDIT_BY_CS){
			result.put("suc", false);
			result.put("msg", "不能进行取消通知发货操作，请刷新页面查看订单最新状态");
			return ok(Json.toJson(result));
		}
		
		SaleMain sm = new SaleMain();
		sm.setId(main.get("id").asInt());
		sm.setEmail(saleMainOrderByID.getEmail());
		sm.setStatus(SaleOrderStatus.WAITING_CONFIRM_BY_CUSTOMER);
		boolean res = saleMainService.updateByPrimaryKeySelective(sm);
		
		//添加通知发货的操作记录
		saveOperateRecord(sm.getId(), 9, "取消通知发货", "分销商用户");
		
		//add by xuse 更新优惠码信息
		saleMainService.updateCouponsState(sm);
		Logger.info("更改客户订单id为[{}]的状态标识为：[{}],执行结果为：{}",sm.getId(),sm.getStatus(),res);
		
		result.put("suc", true);
		result.put("msg", "取消通知发货成功");
		return ok(Json.toJson(result));
	}
	
	/**
	 * 通知发货，发货单变为待客服审核
	 * @return
	 */
	public Result informShipping() {
		JsonNode main = request().body().asJson();
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		Logger.info("<<<<<<<<<<<<<更新状态参数:[{}]>>>>>>>",main);
		
		SaleMain sm = new SaleMain();
		sm.setId(main.get("id").asInt());
		sm.setStatus(SaleOrderStatus.WAITING_AUDIT_BY_CS);
//		sm.setIsAdjusted(1);
		boolean res = saleMainService.updateByPrimaryKeySelective(sm);
		
		//添加通知发货的操作记录
		saveOperateRecord(sm.getId(), 3, "通知发货", "分销商用户");
		
		//add by xuse 更新优惠码信息
		saleMainService.updateCouponsState(sm);
		Logger.info("更改客户订单id为[{}]的状态标识为：[{}],执行结果为：{}",sm.getId(),sm.getStatus(),res);
		return ok(Json.toJson(res));
	}
	
	/**
	 * 确认收货 
	 * @return
	 */
	public Result confirmReceivement() {
		JsonNode main = request().body().asJson();
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		Logger.info("<<<<<<<<<<<<<更新状态参数:[{}]>>>>>>>",main);
		
		SaleMain sm = new SaleMain();
		sm.setId(main.get("id").asInt());
		sm.setStatus(SaleOrderStatus.RECEIVED);
		sm.setConfirmReceiptDate(new Date());
		boolean res = saleMainService.updateByPrimaryKeySelective(sm);
		
		//添加操作记录
		saveOperateRecord(sm.getId(), 6, "确认收货", "分销商用户");
        
		//add by xuse 更新优惠码信息
		saleMainService.updateCouponsState(sm);
		Logger.info("更改客户订单id为[{}]的状态标识为：[{}],执行结果为：{}",sm.getId(),sm.getStatus(),res);
		return ok(Json.toJson(res));
	}
	
	/**
	 * 待支付运费
	 * @return
	 */
	public Result toPayFreight() {
		JsonNode main = request().body().asJson();
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		Logger.info("<<<<<<<<<<<<<更新状态参数:[{}]>>>>>>>",main);
		
		SaleMain sm = new SaleMain();
		sm.setId(main.get("id").asInt());
		sm.setStatus(103);
		sm.setIsAdjusted(1);
		boolean res = saleMainService.updateByPrimaryKeySelective(sm);
		
		//add by xuse 更新优惠码信息
		saleMainService.updateCouponsState(sm);
		Logger.info("更改客户订单id为[{}]的状态标识为：[{}],执行结果为：{}",sm.getId(),sm.getStatus(),res);
		return ok(Json.toJson(res));
	}
	
	/**
	 * 添加通知发货的操作记录
	 */
	private void saveOperateRecord(int sid, int optType, String comment, String email){
		OperateRecord or = new OperateRecord();
		or.setOperateType(optType);
		or.setResult(1);
		or.setComment(comment);
		or.setOrderId(sid);
		or.setEmail(email);
		boolean saveFlag = operateRecordService.saveOperateRecord(or);
		Logger.info("添加操作记录结果："+(saveFlag?"添加成功":"添加失败"));
	}
	/***************拆分updateSalesOrderStatus方法end**********************/
	
	/**
	 * 获取发货单基本信息
	 * @return
	 */
	@Login
	public Result getSaleOrderBase(){
		JsonNode main = request().body().asJson();
		Logger.info("orderId:[{}]",main);
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		return ok(Json.toJson(saleBaseService.getSaleBaseByOrderId(main.get("orderId").asInt())));
	}
	
	/**
	 * 获取发货单主表信息-用主键id或订单号
	 * @return
	 */
	public Result getSaleOrderMain(){
		JsonNode main = request().body().asJson();
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		if(main.has("orderId")){
			return ok(Json.toJson(saleMainService.getSaleMainOrderByID(main.get("orderId").asInt())));
		}
		
		return ok(Json.toJson(saleMainService.getSaleMainOrderByOrderNo(main.get("orderNo").textValue())));
	}
	
	/**
	 * 获取销售订单商品的采购信息
	 * @return
	 */
	public Result getPurchaseInfo(){
		JsonNode main = request().body().asJson();
		int orderId = main.get("orderId").asInt();
		return ok(Json.toJson(saleService.getPurchaseInfo(orderId)));
	}
	
	/**
	 * 获取客户订单主表信息
	 * 参数参考
		{
			orderNo:123
		}
	 * @return
	 */
	@Login
	@BodyParser.Of(BodyParser.Json.class)
	public Result getSaleMain(){
		JsonNode main = request().body().asJson();
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		return ok(Json.toJson(saleMainService.getSaleMainOrderByOrderNo(main.get("orderNo").textValue())));
	}
	
	/**
	 * 获取定购人信息
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@Login
	public Result getOrderer(){
		String salesOrderNo = request().getQueryString("salesOrderNo");
		Logger.debug(">>>>>salesOrderNo>>>>"+salesOrderNo);
		Map map = saleBaseService.getOrderer(salesOrderNo);
		if(map == null) {
			return ok("");			
		}
		return ok(Json.toJson(map).toString());
	}
	
	/**
	 * 生成客户订单API
	 * 
	 * @return
	 */
//	@ApiPermission
	@ApiOperation(value = "发货单下单", notes = "", nickname = "saleOrder", httpMethod = "POST")
	@ApiImplicitParams({
	@ApiImplicitParam(name = "body", value = "", required = true, dataType = "dto.sales.PostSaleOrderDto", paramType = "body" 
		,defaultValue = 
		"{"+
			  "\n\"email\": \"zhengbc@qq.com\", "+
			  "\n\"remark\": \"\", "+
			  "\n\"address\": \"北京市 北京市 东城区 sdsfsdf\", "+
			  "\n\"receiver\": \"sdfsf\", "+
			  "\n\"shopId\": 100, "+
			  "\n\"telphone\": \"18206698184\", "+
			  "\n\"postCode\": \"100000\","+ 
			  "\n\"LogisticsTypeCode\": \"JYT\","+ 
			  "\n\"logisticsMode\": \"圆通快递\", "+ 
			  "\n\"createUser\": \"superadmin\", "+ 
			  "\n\"provinceId\": \"1\", "+ 
			  "\n\"warehouseName\": \"深圳仓\", "+ 
			  "\n\"warehouseId\": \"2024\", "+ 
			  "\n\"skuList\": ["+ 
			    "\n{"+ 
			     "\n \"sku\": \"IF965-2\", "+ 
			     "\n \"warehouseId\": \"2024\", "+ 
			     "\n \"expirationDate\": \"\", "+ 
			     "\n \"num\": \"1\", "+ 
			     " \n\"finalSellingPrice\": \"16.53\""+ 
			   " \n}"+ 
			  "\n]"+ 
			"\n}"
			) })
	@BodyParser.Of(BodyParser.Json.class)
	public Result postOrder() {
		JsonNode main = request().body().asJson();
		Logger.info("下单接口请求数据：{}", main);
		Map<String, Object> result = Maps.newHashMap();
		// 一定要有店铺id，涉及到店铺扣点
		if (!JsonCaseUtil.checkParam(main, "email","warehouseId","skuList","shopId")) {
			result.put("code", 101);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		String couponsCode = !main.has("couponsCode") ? null : main.get("couponsCode").textValue();
		//检查优惠码
		if(StringUtils.isNotBlankOrNull(couponsCode)) {
			Logger.info("检查优惠码=={}",couponsCode);
			Double amount = !main.has("purchaseTotal") ? null : main.get("purchaseTotal").asDouble();
			JsonNode node = saleService.checkCoupons(couponsCode,amount);
			if(node == null || !node.get("suc").asBoolean()) {
				return ok(Json.toJson(node));
			}
			Logger.info("检查优惠码结果：{}",node);
			// 优惠码审核通过了，获取到优惠金额
			double couponsAmount = node.get("active").get("couponsCost").asDouble();
			// 保存优惠金额
			((ObjectNode)main).put("couponsAmount", couponsAmount);
		}
		
		String tradeNo = JsonCaseUtil.jsonToString(main.get("tradeNo"));
		//检查交易号
		if(StringUtils.isNotBlankOrNull(tradeNo) && saleService.checkTradeNo(tradeNo.trim())) {
			result.put("code", 101);
			result.put("msg", "订单交易号重复，请重新输入");
			return ok(Json.toJson(result));
		}
		//修改逻辑 锁库方法
		return ok(Json.toJson(lockService.order(main)));
	}
	
	/**
	 * 提供给后台用的（完税仓商品出库）
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result postOrderFromPurchaseOrder() {
		JsonNode main = request().body().asJson();
		Map<String, Object> result = Maps.newHashMap();
		if (main == null || !main.has("email") || !main.has("warehouseId")
				|| !main.has("skuList")) {
			result.put("code", 101);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		Logger.info("后台下单接口请求数据：" + main.toString());
		return ok(Json.toJson(lockService.orderCreatedFromPurchaseOrder(main)));
	}
	
	/**
	 * 生成客户订单API，提供给开放接口使用
	 * 
	 * @return
	 */
//	@ApiPermission
	@BodyParser.Of(BodyParser.Json.class)
	public Result postOrder4OpenApi() {
		JsonNode main = request().body().asJson();
		Logger.info("开放接口请求数据：" + main.toString());
//		return ok(Json.toJson(saleService.order4OpenApi(main)));
		return ok(Json.toJson(lockService.order4OpenApi(main, false)));
	}
	/**
	 * 生成客户订单API，提供给门店接口使用
	 * 
	 * @return
	 */
	@ApiPermission
	@BodyParser.Of(BodyParser.Json.class)
	public Result postOrder4Store() {
		JsonNode main = request().body().asJson();
		Logger.info("门店接口请求数据：" + main.toString());
		return ok(Json.toJson(lockService.order4OpenApi(main,true)));
	}
	/**
	 * 根据销售订单号 更新订单状态
	 * 

	 * @param saleOrderNo
	 * @param status
	 * @return
	 */
    @Login
	public Result upSalesStuByOrderNo(){
		JsonNode main = request().body().asJson();
		Logger.info("ERP同步客户订单状态:[{}]",main);
		if(main == null||!main.has("saleOrderNo")||!main.has("status")){
			Map<String,Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg","参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(saleMainService.updateSalesStatus(main)));
	}
	
	/**
	 * 
	 * [
	    {
	        "isFetched": "true",
	        "orderNo": "XS201603230924243",
	        "erpOrderNo":"No.232020170516-54131779-X2"
	    },
	    {
	        "isFetched": "false",
	        "orderNo": "XS201603230855457",
	        "reason": "啦啦",
	        "erpOrderNo":"No.232020170516-54131779-X1"
	    }
		]
	 * erp抓取结果保存
	 * @return
	 */
	public Result  erpFetchResult(){
		JsonNode main = request().body().asJson();
		Logger.info("ERP抓取订单返回结果:[{}]",main);
		if(main == null){
			Map<String,Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg","参数错误");
			return ok(Json.toJson(result));
		}

		return ok(Json.toJson(saleMainService.erpCatchResult(main)));
	}
	
	/**
	 * 导出物流信息
	 */
	public Result exportLogistics(){
		Map<String, String[]> map = request().queryString();
		if (null == map || map.get("email") == null) {
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("参数错误");
			result.put("suc", false);
			result.put("code", "1");
			return ok(Json.toJson(result));
		}
		String[] header = map.get("header");
		try {
			response().setHeader("Content-disposition",
					"attachment;filename=" + new String("物流信息".getBytes(), "ISO8859-1") + ".xls");
		} catch (Exception e) {
			Logger.error("导出物流信息失败",e);
		}
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		Map<String, Object> params = Maps.newHashMap();
		params.put("email", map.get("email")[0]);
		params.put("currPage", map.get("currPage") == null ? null : Integer.parseInt(map.get("currPage")[0]));
		params.put("pageSize", map.get("pageSize") == null ? null : Integer.parseInt(map.get("pageSize")[0]));
		params.put("status", map.get("status") == null ? null : Integer.parseInt(map.get("status")[0]));
		List<ExportSaleLogistic> list = saleService.selectSaleOrederLogistics(params);
		Logger.info("物流导出，导出数据条数：" + list.size());
		for (ExportSaleLogistic logistic : list) {
			if (logistic.getTrackingNumber() == null || logistic.getTrackingNumber().equals("")) {
				logistic.setTrackingNumber(logistic.getLocalTrackNumber());
			}
			if (logistic.getShippingName() == null || logistic.getShippingName().equals("")) {
				logistic.setShippingName(logistic.getLogisticsMode());
			}
		}
		return ok(ExportUtil.export("logistics.xls", header, Constant.EXOPRT_LOGISTICS_INFO, list));
	}
	
	
	/**
	 * 为开放接口提供查询
	 * @author zbc
	 * @since 2016年9月6日 上午10:34:51
	 */
	public Result openShowOrder(){
		JsonNode main = request().body().asJson();
		if (main == null || main.size() == 0) {
			return ok("参数错误");
		}
		
		return ok(Json.toJson(saleService.openQuery(main)));
	}
	
	/**
	 * 前台销售发货页面
	 * @return
	 */
	@Login
	public Result saveBufferMemory() {
		JsonNode main = request().body().asJson();
		if (main == null || main.size() == 0) {
			ObjectNode result  = Json.newObject();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result.toString()));
		}
		
		String params = main.toString();
		return ok(Json.toJson(saleService.saveBufferMemory(params)));
	}
	
	/**
	 * 获取当前登录分销商在销售发货页面的缓存信息
	 * @return
	 */
	@Login
	public Result getBufferMemory() {
		return ok(Json.toJson(saleService.getBufferMemory()));
	}
	
	/**
	 * 清除缓存信息
	 * @return
	 */
	@Login
	public Result clearBufferMemory() {
		return ok(Json.toJson(saleService.clearBufferMemory()));
	}
	
	/**
	 * 业务背景： ERP订单关闭推送到HK后，再推送到BBC还原库存（库存哪里来回哪里去），退钱到余额
	 * 此接口：从HK获取关闭的订单信息，根据订单信息，退还余额以及还原库存
	 * @author lzl
	 * @since 2016年11月16日下午4:05:01
	 */
	public Result closeSalesFromB2C(){
	    JsonNode main = request().body().asJson();
	    Logger.info("HK关闭订单参数:[{}]",main);
	    if (main == null || main.size() == 0) {
	    	ObjectNode result = Json.newObject();
	    	result.put("suc", false);
	    	result.put("msg", "参数错误");
	    	return ok(Json.toJson(result.toString()));
	    }
	    
	    String param = main.toString();
	    return ok(lockService.closeSalesFromB2C(param));
	}

	/**
	 *  TODO 目前 支持微仓不缺货！同步优惠码信息
	 * @author zbc
	 * @since 2016年11月24日 上午10:31:46
	 */
	public Result syncCoupons(Integer mainId){
		return ok(saleService.syncCoupons(mainId));
	}
	
	/**
	 * 取消关闭订单状态
	 * @author zbc
	 * @since 2016年12月15日 下午8:01:16
	 */
	@ApiOperation(
	    value = "取消关闭订单状态",
	    notes = "重置发货单关闭状态",
	    httpMethod = "GET"
	    )
	@ApiImplicitParams({
		@ApiImplicitParam(name="so",value="订单号",required=true,paramType="query",dataType = "String")
	})
	public Result undoClose(String orderNo){
		return ok(saleMainService.undoClose(orderNo));
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
		
		Logger.info("changeNickName---->"+ node.toString());
		String param = node.toString();
		return ok(saleMainService.changeNickNameByEmail(param));
	}
	
	
	/**
	 * 新 保存微仓入库历史数据
	 * @author zbc
	 * @since 2016年12月27日 下午5:38:09
	 */
	public Result saveMicroOut(){
		JsonNode node = request().body().asJson();
		Map<String,Object> res = Maps.newHashMap();
		Logger.info("保存入库历史数据:[{}]",node);
		if(!node.has("sid")||!node.has("mircoOut")){
			res.put("suc", false);
			res.put("msg", "参数错误");
		}else{
			res = lockService.saveMicroOutHistory(node.get("sid").asInt(),node.get("mircoOut").toString());
		}
		return ok(Json.toJson(res));
	}
	
	
	/**
	 * 后台调用
	 * 关闭发货单还原微仓库存
	 * @author zbc
	 * @since 2017年1月6日 上午9:29:07
	 */
	public Result remicsto(){
		JsonNode node = request().body().asJson();
		Logger.info("关闭发货单还原微仓库存:[{}]",node);
		if(!node.has("sid")||!node.has("op")){
			Map<String,Object> res = Maps.newHashMap();
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res));
		}
		
		return ok(Json.toJson(lockService.remicsto(node.toString())));
	}
	
	/**
	 * 支付完成发货单
	 * @author zbc
	 * @since 2017年6月21日 下午4:32:12
	 * @return
	 */
	@Login
	public Result finishSaleOrder(){
		JsonNode json = request().body().asJson();
		if(!JsonCaseUtil.checkParam(json, "id","isComplete")){
			return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
		}
		return ok(Json.toJson(lockService.finishSaleOrder(json.toString())));
	}
	
	@ApiOperation(value="根据单号查询发票信息",httpMethod="GET",
			notes="<b>当订单 isNeedInvoice为true 是调用该接口</b><br/>"
					+ " data.invoiceType:发票类型（1为个人，2为公司）<br/>"
					+ "data.invoiceTitle:发票抬头",response=JsonResult.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="so",required=true,dataType="string",paramType="path",defaultValue="xx",value="单号")
	})
	public Result getInvoice(String so){
		return ok(Json.toJson(invoiceService.getInvoice(so)));
	}
	
}