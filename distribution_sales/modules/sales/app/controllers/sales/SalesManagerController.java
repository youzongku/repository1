package controllers.sales;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.ALogin;
import controllers.annotation.DivisionMember;
import dto.sales.SaleOrderInfo;
import dto.sales.SalesOrderInfo4FinanceDto;
import dto.sales.audit.AuditByCustomerServiceParam;
import entity.sales.OperateRecord;
import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.base.utils.JsonFormatUtils;
import services.sales.IHttpService;
import services.sales.IManagerImportOrderService;
import services.sales.IManagerOrderService;
import services.sales.IOperateRecordService;
import services.sales.IProductExpirationDateService;
import services.sales.ISaleBaseService;
import services.sales.ISaleDetailsService;
import services.sales.ISaleLockService;
import services.sales.ISaleMainService;
import services.sales.ISaleService;
import services.sales.IUserService;
import util.sales.Constant;
import util.sales.ExportUtil;
import util.sales.JsonCaseUtil;
import util.sales.SaleOrderStatus;
import util.sales.StringUtils;

/**
 * 描述：销售发货订单管理后台控制类
 * 
 * @author hanfs
 *
 */
@Api(value="/销售单管理",description="Sales Management")
public class SalesManagerController extends Controller {
	@Inject
	private ISaleMainService saleMainService;
	@Inject
	private ISaleBaseService saleBaseService;
	@Inject
	private ISaleDetailsService saleDetailsService;
	@Inject
	private IOperateRecordService operateRecordService;
	@Inject
	private ISaleService saleService;
	@Inject
	private IUserService userService;
	@Inject
	private ISaleLockService lockService;
	@Inject
	private IHttpService httpService;
	@Inject
	private IProductExpirationDateService productExpirationDateService;
	@Inject 
	private IManagerOrderService managerOrderService;
	@Inject
	private IManagerImportOrderService managerImportOrderService;

	/**
	 * 获取一个发货单的主信息，base和详情信息
	 * 
	 * @return
	 */
	public Result getSaleOrderByNo() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		JsonNode main = request().body().asJson();
		String salesOrderNo = main.get("salesOrderNo").asText();

		SaleMain sm = saleMainService.getSaleMainOrderByOrderNo(salesOrderNo);
		SaleBase sb = saleBaseService.getSaleBaseByOrderId(sm.getId());
		List<SaleDetail> details = saleDetailsService
				.getAllSaleDetailsByOrderId(sm.getId());

		// 获取商品的详情
		List<String> skus = Lists.newArrayList();
		for (SaleDetail sd : details) {
			if (!skus.contains(sd.getSku())) {
				skus.add(sd.getSku());
			}
		}

		JsonNode productsNode = null;
		try {
			productsNode = httpService.getProducts(sm.getEmail(), skus, sm.getWarehouseId(), sm.getDisMode());
		} catch (IOException e) {
			Logger.info("获取商品详情失败");
			e.printStackTrace();
			resultMap.put("suc", false);
			resultMap.put("msg", "获取商品详情失败");
			return ok(Json.toJson(resultMap));
		}

		Map<String, ObjectNode> sku2Stock = new HashMap<String, ObjectNode>();
		ObjectNode newObject;
		JsonNode productNode;
		for (Iterator<JsonNode> it = productsNode.get("data").get("result").iterator(); it.hasNext();) {
			productNode = it.next();
			newObject = Json.newObject();
			newObject.put("stock", productNode.get("stock").asInt());
			newObject.put("microStock", productNode.get("microStock").asInt());
			newObject
					.put("batchNumber", productNode.get("batchNumber").asInt());
			sku2Stock.put(productNode.get("csku").asText(), newObject);
		}

		resultMap.put("suc", true);
		resultMap.put("saleMain", sm);
		resultMap.put("saleBase", sb);
		resultMap.put("details", details);
		resultMap.put("sku2Stock", sku2Stock);

		return ok(Json.toJson(resultMap));
	}

	/**
	 * 描述：显示所有销售订单
	 * 
	 * @return
	 * @author ye_ziran
	 * @since 2016年1月11日 下午4:01:59
	 */
	/**
	 * DivisionMember区分用户，后台业务员仅查询自己关联的分销商
	 * 
	 * 采购单展示
	 * 
	 * @return
	 */
	@ALogin
	@DivisionMember
	@ApiOperation(value = "发货单展示", notes = "", nickname = "", httpMethod = "POST", response = SaleOrderInfo.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
			,defaultValue = "{\"pageSize\":10,\"pageCount\":1,\"orderStartDate\":\"\",\"orderEndDate\":\"\",\"status\":\"11\",\"warehouseId\":\"\",\"seachSpan\":\"\",\"distributorType\":\"0\",\"sort\":\"asc\",\"sidx\":\"orderingDate\"}"
				) })
	public Result showAllSaleOrder() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("result", false);
			resultMap.put("msg", "数据格式错误");
			return ok(Json.toJson(resultMap));
		}
		Logger.info("showAllSaleOrder in SalesManagerController，参数：{}",json);
		return ok(Json.toJson(saleService.selectSaleOrderListOfBackstage(json, userService.getRelateAccounts())));
	}
	
	@ApiOperation(value = "发货单-财务", notes = "", nickname = "", httpMethod = "POST", response = SalesOrderInfo4FinanceDto.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
				,defaultValue = "{\"pageSize\":10,\"pageCount\":1,\"status\":\"11\",\"seachSpan\":\"\",\"sort\":\"asc\",\"sidx\":\"orderingDate\"}"
				) })
	@ALogin
	@DivisionMember
	public Result showAllSaleOrder4finance() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("result", false);
			resultMap.put("msg", "数据格式错误");
			return ok(Json.toJson(resultMap));
		}
		Logger.info("showAllSaleOrder4finance in SalesManagerController，参数：{}",json);
		return ok(Json.toJson(saleService.selectSaleOrderListOfBackstage4finance(json, userService.getRelateAccounts())));
	}
	
	/**
	 * 查询待客服审核发货单
	 * @author zbc
	 * @since 2017年5月24日 上午9:50:42
	 * @return
	 */
	@ALogin
	@DivisionMember
	@ApiOperation(value = "待客服审核发货单展示", notes = "<b>该接口有后台登录校验</b><br>该接口与原先发货单展示参数返回值一样，只是不需要传参数:statusList<br/>客服确认列表订单查询",httpMethod = "POST", response = SaleOrderInfo.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
			,defaultValue = "{\"pageSize\":10,\"pageCount\":1,\"orderStartDate\":\"\",\"orderEndDate\":\"\",\"warehouseId\":\"\",\"seachSpan\":\"\",\"distributorType\":\"0\",\"sort\":\"asc\",\"sidx\":\"orderingDate\"}"
				) })
	public Result showCsSaleOrder() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("result", false);
			resultMap.put("msg", "数据格式错误");
			return ok(Json.toJson(resultMap));
		}
		/*********add by zbc 指定查看 待客服审核， 未合并 订单 start ***********************/
		((ObjectNode)json).put("status", SaleOrderStatus.WAITING_AUDIT_BY_CS);
		((ObjectNode)json).put("isCombine", false);
		/*********add by zbc 指定查看 待客服审核， 未合并 订单 end ***********************/
		Logger.info("showAllSaleOrder in SalesManagerController，参数：{}",json);
		return ok(Json.toJson(saleService.selectSaleOrderListOfBackstage(json,userService.getRelateAccounts())));
	}
	
	
	/**
	 * 待财务审核列表
	 * @author zbc
	 * @since 2017年5月24日 上午10:28:07
	 * @return
	 */
	@ALogin
	@DivisionMember
	@ApiOperation(value = "待财务审核发货单展示", notes = "<b>该接口有后台登录校验</b><br>该接口与原先发货单展示参数返回值一样，只是不需要传参数:statusList<br/>财务确认列表订单查询",httpMethod = "POST", response = SaleOrderInfo.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
			,defaultValue = "{\"pageSize\":10,\"pageCount\":1,\"orderStartDate\":\"\",\"orderEndDate\":\"\",\"warehouseId\":\"\",\"seachSpan\":\"\",\"distributorType\":\"0\",\"sort\":\"asc\",\"sidx\":\"orderingDate\"}"
				) })
	public Result showFcSaleOrder() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("result", false);
			resultMap.put("msg", "数据格式错误");
			return ok(Json.toJson(resultMap));
		}
		/*********add by zbc 指定查看 待客服审核， 未合并 订单 start ***********************/
		((ObjectNode)json).put("status", SaleOrderStatus.WAITING_AUDIT_BY_FINANCE);
		((ObjectNode)json).put("isCombine", false);
		/*********add by zbc 指定查看 待客服审核， 未合并 订单 end ***********************/
		Logger.info("showAllSaleOrder in SalesManagerController，参数：{}",json);
		return ok(Json.toJson(saleService.selectSaleOrderListOfBackstage(json,userService.getRelateAccounts())));
	}

	/**
	 * 描述：显示所有销售订单---针对H5页面
	 * 
	 * @return
	 * @author liaoZL
	 */
	/**
	 * DivisionMember区分用户，后台业务员仅查询自己关联的分销商
	 */
	@ALogin
	@DivisionMember
	public Result getSalesForTel() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("result", false);
			resultMap.put("msg", "数据格式错误");
			return ok(Json.toJson(resultMap));
		}
		
		String account = userService.getRelateAccounts();
		List<String> accounts = null;
		if (!StringUtils.isBlankOrNull(account)) {
			accounts = Arrays.asList(account.split(","));
		}
		String param = json.toString();
		return ok(Json.toJson(saleService
				.selectSaleOrdersOfBackstageForTel(param, accounts)));
	}

	/**
	 * 描述：后台销售订单导出 2016年5月5日
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public Result exportSaleOrder() throws UnsupportedEncodingException {
		long startTime = System.currentTimeMillis();
		Map<String, Object> result = Maps.newHashMap();
		Map<String, String[]> map = request().queryString();
		if (null == map) {
			Logger.info("参数错误");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		// String[] header = map.get("header");
		// if (null == header || header.length <= 0) {
		// Logger.info("列头不能为空。");
		// result.put("suc", false);
		// result.put("code", "2");
		// return ok(Json.toJson(result));
		// }

		Map<String, String> exportSaleOrderMap = Constant.EXPORT_SALE_ORDER_MAP;
		Set<String> strings = exportSaleOrderMap.keySet();
		Iterator<String> iterator = strings.iterator();
		List<String> newHeader = Lists.newArrayList();
		while (iterator.hasNext()) {
			newHeader.add(iterator.next());
		}
		String[] headerString = new String[newHeader.size()];
		newHeader.toArray(headerString);
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		response().setHeader("Content-disposition",
				"attachment;filename=" + new String("销售订单".getBytes(), "ISO8859-1") + ".xls");
		List<String> exportKeys = saleService.getExportOrderListOfBackstage(map);
		Logger.info("总查询时间:{}",System.currentTimeMillis()-startTime);
//		Logger.info("-----管理员开始导出销售订单，导出数据条数：" + exportData.size());
//		return ok(ExportUtil.export("saleOrder.xls", headerString, Constant.EXPORT_SALE_ORDER_MAP, exportData));
		return ok(saleService.exportSaleOrder("saleOrder.xls", headerString, Constant.EXPORT_SALE_ORDER_MAP, exportKeys));
	}
	
	/**
	 * 后台导入发货单
	 * @return
	 */
	public Result importSalesOrder(){
		MultipartFormData formData = request().body().asMultipartFormData();
		if (formData == null) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("flag", false);
			resultMap.put("msg", "系统错误，上传失败！");
			return ok(Json.toJson(resultMap));
		}
		
		Map<String, String[]> params = formData.asFormUrlEncoded();
		List<FilePart> files = formData.getFiles();
		
		return ok(Json.toJson(managerImportOrderService.importSalesOrder(files, params)));
	}
	
	/**
	 * 下载导入发货单的模板
	 * @return
	 */
	public Result downloadSaleOrderExcelTemplate(){
		String filename = "通淘国际发货单导入模板.xls";
		try {
			response().setHeader("Content-disposition", "attachment;filename="+new String(filename.getBytes("utf-8"),"ISO8859_1"));
		} catch (UnsupportedEncodingException e) {
			Logger.info("营销单导出的excel文件不支持中文名");
			e.printStackTrace();
		}
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		return ok(ExportUtil.exportCloudSaleTemplate(filename));
	}

	/**
	 * 描述：查看订单操作记录
	 * 
	 * @return
	 */
	public Result showOperateRecordOfOrder() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		JsonNode json = request().body().asJson();
		JsonNode dataJson = json.path("orderId");
		if (dataJson == null) {
			resultMap.put("result", false);
			resultMap.put("msg", "传入的销售订单id为空");
			return ok(Json.toJson(resultMap));
		}
		
		Integer orderId = Integer.valueOf(dataJson.asText());
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", orderId);
		// paramMap.put("operateType", 2);
		paramMap.put("operateTypes",
				Lists.newArrayList(new Integer[] { 2, 7, 11, 10 }));
		List<OperateRecord> operateRecords = operateRecordService
				.getOperateRecordByParam(paramMap);
		resultMap.put("result", true);
		resultMap.put("msg", "获得订单操作记录成功");
		resultMap.put("operateRecords", operateRecords);
		resultMap.put("param", orderId);
		return ok(Json.toJson(resultMap));
	}

	/**
	 * 描述：查看原因
	 * 
	 * @return
	 */
	public Result showReason() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		JsonNode json = request().body().asJson();
		JsonNode dataJson = json.path("orderId");
		if (dataJson == null) {
			resultMap.put("result", false);
			resultMap.put("msg", "传入的销售订单id为空");
			return ok(Json.toJson(resultMap));
		}
		
		Integer orderId = Integer.valueOf(dataJson.asText());
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", orderId);
		List<OperateRecord> operateRecords = operateRecordService
				.getOperateRecordByParam(paramMap);
		StringBuilder reason = new StringBuilder("");
		for (OperateRecord operateRecord : operateRecords) {
			Integer operateTyp = operateRecord.getOperateType();
			Integer result = operateRecord.getResult();
			if (result != null && result.equals(0)) {
				if (operateTyp != null
						&& (operateTyp.equals(2) || operateTyp.equals(3))) {
					reason.append(operateRecord.getComment() + ",");
				}
			}
		}
		resultMap.put("result", true);
		resultMap.put("msg", "获得操作失败原因成功");
		resultMap.put("reason",
				reason.subSequence(0, reason.lastIndexOf(",")));
		resultMap.put("param", orderId);
		return ok(Json.toJson(resultMap));
	}

	/**
	 * 描述：显示物流信息
	 * 
	 * @return
	 */
	public Result showDeliveryOfOrder() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", true);
		resultMap.put("msg", "待确认物流反馈信息，之后再完善。。。。。。");
		return ok(Json.toJson(resultMap));
	}

	/**
	 * 描述：批量支付销售订单
	 * 
	 * @return
	 */
	@ALogin
	public Result batchPayForOrder() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		JsonNode json = request().body().asJson();
		Logger.debug(">>>batchPayForOrder>>>json>" + json.toString());
		JsonNode dataJson = json.path("orderIds");
		if (dataJson == null
				|| org.apache.commons.lang3.StringUtils.isBlank(dataJson
				.asText())) {
			resultMap.put("result", false);
			resultMap.put("msg", "请勾选您要审核的订单!");
			return ok(Json.toJson(resultMap).toString());
		}
		
		String orderIdStr = dataJson.asText();
		resultMap = saleMainService.buildBatchPayParam(orderIdStr);
		return ok(Json.toJson(resultMap).toString());
	}

	/**
	 * 描述：批量审核销售订单通过
	 * 
	 * @return
	 */
	@ALogin
	public Result batchVerifyForOrder() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		JsonNode json = request().body().asJson();
		Logger.debug(">>>batchVerifyForOrder>>>>>json>>>" + json.toString());

		JsonNode dataJson = json.path("orderIds");
		if (dataJson == null) {
			resultMap.put("result", false);
			resultMap.put("msg", "传入的销售订单id为空");
			return ok(Json.toJson(resultMap));
		}
		
		String email = json.path("email") == null ? "" : json.path("email").asText();
		String comment = json.get("comment") == null ? "" : json.get("comment").asText();
		String status = json.get("status") == null ? "" : json.get("status").asText();
		String orderIdStr = dataJson.asText();
		List<String> orderIdList = Arrays.asList(orderIdStr.split(","));
		saleMainService.batchAudit(orderIdList, email, comment, status);
		resultMap.put("result", true);
		resultMap.put("msg", "批量设置审核通过成功");
		resultMap.put("orderIds", orderIdStr);
		return ok(Json.toJson(resultMap));
	}

	/**
	 * 单个审核
	 * 
	 * @return
	 */
	@ALogin
	public Result checkSaleMain() {
		JsonNode node = request().body().asJson();
		Logger.info("财务审核参数[{}]", node);
		if (node == null) {
			Map<String, Object> res = new HashMap<String, Object>();
			res.put("result", false);
			res.put("msg", "审核参数错误");
			return ok(Json.toJson(res));
		}
		
		return ok(Json.toJson(saleMainService.check(node.toString(), userService.getAdminAccount())));
	}

	/**
	 * 修改订单金额
	 * 
	 * @author zbc
	 * @since 2016年12月2日 下午5:00:54
	 */
	@ALogin
	public Result changeOrderPrice() {
		JsonNode main = request().body().asJson();
		Logger.info("修改价格参数:[{}]", main);
		if (main == null || (main != null & !main.has("sno"))) {
			Map<String, Object> res = Maps.newHashMap();
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res));
		}
		
		String admin = userService.getAdminAccount();
		Logger.info("修改价格操作人:[{}]", admin);
		return ok(Json.toJson(saleService.changeOrderPrice(main.toString(), admin)));
	}
	
	/**
	 * 客服批量审核
	 * 
	 * @author zbc
	 * @since 2016年12月21日 下午3:30:04
	 */
	@ALogin
	@DivisionMember
	@ApiOperation(value = "客服批量审核", notes = "soInfoList表示发货单的信息，包含以下内容：<br/>"
			+ "sno：发货单单号，必传<br/>address：地址<br/>"
			+ "receiver：收货人<br/>tel：手机号码<br/>postCode：邮编<br/>idcard：身份证号码<br/>"
			+ "<br/>csAudit：是否审核通过，取值为true/false<br/>csRemark：备注"
			+ "", nickname = "", httpMethod = "POST", response = SaleOrderInfo.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
			,defaultValue = "{\"soInfoList\":"
					+ "[{\"sno\":\"XS2017041916143900011108\","
					+ "\"address\":\"建国门内大街\",\"receiver\":\"收货人\",\"tel\":\"13500001111\","
					+ "\"postCode\":\"\",\"idcard\":\"\"}],\"csAudit\":true,\"csRemark\":\"审核备注\"}"
				) })
	public Result batchAuditByCustomerService() {
		/*
		 {
		 	"soInfoList":[
		 		{"sno":"XS2017041916143900011108","address":"建国门内大街","receiver":"收货人",
		 		"tel":"13500001111","postCode":"","idcard":""}
		 	],
		 	"csAudit":true,
		 	"csRemark":"审核备注"
		 }
		 */
		Map<String, Object> res = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		Logger.info("销售发货客服批量审核参数:{}", node);
		if (node == null || !node.has("soInfoList") || !node.has("csAudit") || 
				StringUtils.isBlankOrNull(JsonCaseUtil.jsonToString(node.get("csAudit")))) {
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res));
		}
		
		String auditUser = userService.getAdminAccount();
		List<AuditByCustomerServiceParam> paramList = Lists.newArrayList();
		try {
			paramList = parseAuditByCustomerServiceParamList(node, auditUser);
		} catch (Exception e) {
			Logger.info("客服批量审核，解析参数异常：{}",e);
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res));
		}
		
		// 检查解析后的参数
		for (AuditByCustomerServiceParam param : paramList) {
			// sno是必需的；当为true时，要检验地址和收货人
			if (StringUtils.isBlankOrNull(param.getSno()) || 
					(param.isCsAudit() && 
							(StringUtils.isBlankOrNull(param.getAddress()) 
									|| StringUtils.isBlankOrNull(param.getReceiver()))
							)
					) {
				Logger.info("检查解析后的参数，参数错误");
				res.put("suc", false);
				res.put("msg", "参数错误");
				return ok(Json.toJson(res));
			}
		}
		/*for (AuditByCustomerServiceParam param : paramList) {
			// 当为true时，要检验地址地址长度
			if (param.isCsAudit() && param.getAddress().trim().length() < 5) {
				res.put("suc", false);
				res.put("msg", "详细地址字数不能小于5个");
				return ok(Json.toJson(res));
			}
		}*/
		
		Map<String, Object> failed = Maps.newHashMap();
		Map<String, Object> success = Maps.newHashMap();
		// 循环执行
		for (AuditByCustomerServiceParam param : paramList) {
			Map<String, Object> result = saleService.auditByCustomerService(param, false);
			// 审核不通过，即是suc为false
			if (result!=null && result.size()>0) {
				if (result.get("suc")!=null && !((Boolean)result.get("suc"))) {
					// 审核失败
					Object obj = result.get("msg");
					String msg = "";
					if (obj!=null) {
						msg = (String) obj;
					}
					failed.put(param.getSno(), msg);
				} else if (result.get("suc")!=null && ((Boolean)result.get("suc"))) {
					// 审核成功
					Object obj = result.get("msg");
					String msg = "";
					if (obj!=null) {
						msg = (String) obj;
					}
					success.put(param.getSno(), msg);
				}
			}
		}
		
		res.put("suc", true);
		res.put("failed", failed);
		res.put("success", success);
		return ok(Json.toJson(res));
	}
	
	private List<AuditByCustomerServiceParam> parseAuditByCustomerServiceParamList(JsonNode node, String auditUser) {
		List<AuditByCustomerServiceParam> paramList = Lists.newArrayList();
		boolean csAudit = node.get("csAudit").asBoolean();
		String csRemark = JsonCaseUtil.jsonToString(node.get("csRemark"));
		
		JsonNode soInfoListNode = node.get("soInfoList");
		for (Iterator<JsonNode> it = soInfoListNode.iterator(); it.hasNext(); ) {
			JsonNode nextNode = it.next();
			AuditByCustomerServiceParam param = new AuditByCustomerServiceParam();
			param.setSno(nextNode.get("sno").asText());
			param.setCsAudit(csAudit);// 是否审核通过
			param.setCsRemark(csRemark);
			param.setAuditUser(auditUser);
			
			if (param.isCsAudit()) {// 审核通过才有
				param.setAddress(JsonCaseUtil.jsonToString(nextNode.get("address")));
				param.setReceiver(JsonCaseUtil.jsonToString(nextNode.get("receiver")));
				param.setTel(JsonCaseUtil.jsonToString(nextNode.get("tel")));
				param.setPostCode(JsonCaseUtil.jsonToString(nextNode.get("postCode")));
				param.setIdcard(JsonCaseUtil.jsonToString(nextNode.get("idcard")));
			}
			paramList.add(param);
		}
		return paramList;
	}

	/**
	 * 客服审核
	 * 
	 * @author zbc
	 * @since 2016年12月21日 下午3:30:04
	 */
	@ALogin
	@ApiOperation(value = "客服批量审核", notes = "sno：发货单单号，必传<br/>address：地址<br/>"
			+ "receiver：收货人<br/>tel：手机号码<br/>postCode：邮编<br/>idcard：身份证号码<br/>"
			+ "<br/>csAudit：是否审核通过，取值为true/false<br/>csRemark：备注"
			+ "", nickname = "", httpMethod = "POST", response = SaleOrderInfo.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
			,defaultValue = "{\"address\":\"北京市 北京市 东城区 虚拟仓发货5虚拟仓发货5\",\"receiver\":\"虚拟仓发货5\",\"tel\":\"13589876785\",\"postCode\":\"100000\",\"idcard\":\"\",\"csRemark\":\"不通过啊\",\"csAudit\":false,\"sno\":\"XS2017060714550000011840\"}"
				) })
	public Result auditByCustomerService() {
		JsonNode node = request().body().asJson();
		Logger.info("销售发货客服审核参数:{}", node);
		if (node == null || (node != null & !node.has("sno"))) {
			Map<String, Object> res = Maps.newHashMap();
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res));
		}
		
		AuditByCustomerServiceParam param = parseAuditByCustomerServiceParam(node, userService.getAdminAccount());
		return ok(Json.toJson(saleService.auditByCustomerService(param, false)));
	}
	
	private AuditByCustomerServiceParam parseAuditByCustomerServiceParam(JsonNode node, String auditUser) {
		AuditByCustomerServiceParam param = new AuditByCustomerServiceParam();
		param.setSno(node.get("sno").asText());
		param.setCsAudit(node.get("csAudit").asBoolean());// 是否审核通过
		param.setCsRemark(JsonCaseUtil.jsonToString(node.get("csRemark")));
		param.setAuditUser(auditUser);
		if (param.isCsAudit()) {// 审核通过才有
			param.setAddress(JsonCaseUtil.jsonToString(node.get("address")));
			param.setReceiver(JsonCaseUtil.jsonToString(node.get("receiver")));
			param.setTel(JsonCaseUtil.jsonToString(node.get("tel")));
			param.setPostCode(JsonCaseUtil.jsonToString(node.get("postCode")));
			param.setIdcard(JsonCaseUtil.jsonToString(node.get("idcard")));
		}
		
		return param;
	}

	/**
	 * 财务审核
	 * 
	 * @author zbc
	 * @since 2016年12月21日 下午3:30:44
	 */
	@ALogin
	public Result auditByFinance() {
		JsonNode node = request().body().asJson();
		Logger.info(">>>>>>>>>>>>>>>>finAudit>>>>>>>>>>>>>>>[{}]", request()
				.remoteAddress());
		Logger.info("销售发货财务审核参数:[{}]", node);
		String ip = request().remoteAddress();
		if (node == null) {
			Map<String, Object> res = Maps.newHashMap();
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res));
		}
		
		return ok(Json.toJson(saleService.auditByFinance(node.toString(), ip)));
	}

	/**
	 * 批量财务审核
	 *
	 * @return
	 */
	@ApiOperation(value="财务审核批量确认",httpMethod="POST",notes="财务审核批量确认，带*为必填" ,produces="application/json")
	@ApiImplicitParams({@ApiImplicitParam(name="body",
			required=true,value="可用参数："
			+ "\norderIdList:*订单id列表，\n"
			+ "\nstatus:*财务审核是否通过 3：不通过 6：通过，\n"
			+ "\ncomment:备注",
			paramType="body",dataType="application/json",
			defaultValue = "{\n\"orderIdList\":[1,2],\n\"status\":6,\n\"comment\":\"备注\" \n}")})
	@ALogin
	@BodyParser.Of(BodyParser.Json.class)
	public Result batchAuditByFinance() {
		JsonNode node = request().body().asJson();
		Logger.info("批量销售发货财务审核参数:【{}】", node);
		if (node == null || !node.has("orderIdList") || !node.has("status")) {
			Map<String, Object> res = Maps.newHashMap();
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res));
		}

		return ok(Json.toJson(saleMainService.batchAuditByFinance(node.toString())));
	}

	/**
	 * 云仓发货:可选赠品 不发微仓商品，缺货采购流程与之前无差别
	 * 
	 * @author zbc
	 * @since 2017年1月10日 下午2:41:47
	 */
	@ALogin
	public Result cloudSale() {
		JsonNode main = request().body().asJson();
		Logger.info("云仓发货下单接口请求数据：[{}]", main);
		
		Map<String, Object> result = Maps.newHashMap();
		if (main == null || !main.has("email") || !main.has("warehouseId")
				|| !main.has("skuList")) {
			result.put("code", 101);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		String tradeNo = main.has("tradeNo") ? main.get("tradeNo").textValue(): null;
		// 检查交易号
		if (StringUtils.isNotBlankOrNull(tradeNo)
				&& saleService.checkTradeNo(tradeNo.trim())) {
			result.put("code", 101);
			result.put("msg", "订单交易号重复，请重新输入");
			return ok(Json.toJson(result));
		}
		
		// 修改逻辑 锁库方法
		return ok(Json.toJson(lockService.cOrder(main)));
	}
	
	/**
	 * 财务审核查询订单利润信息
	 * @param sid
	 * @return
	 */
	@ApiOperation(value = "财务审核查询订单利润信息", notes = "SID:订单ID", nickname = "", httpMethod = "GET", produces = "text/plain")
	@ApiImplicitParams({
			@ApiImplicitParam(name="sid",value="订单ID",required=true,paramType="path",dataType = "Integer")
	})
//	@ALogin
	public Result getAmount(Integer sid){
		Logger.info("财务审核查询订单利润信息，参数：{}",sid);
		return ok(Json.toJson(saleService.getAmount(sid)));
	}


	/**
	 * 批量查询财务审核查询订单利润信息
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value="批量查询财务审核订单利润信息",httpMethod="POST",notes="批量查询财务审核查询订单利润信息，带*为必填" ,produces="application/json")
	@ApiImplicitParams({@ApiImplicitParam(name="body",
			required=true,value="可用参数：订单id列表",
			paramType="body",dataType="application/json",
			defaultValue = "[\n" +
					"    1,\n" +
					"    12153\n" +
					"]")})
	@BodyParser.Of(BodyParser.Json.class)
	public Result getListAmounts(){
		Map<String, Object> result = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		if (node == null) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		List list = JsonFormatUtils.jsonToBean(node.toString(), List.class);
		result.put("suc", true);
		result.put("result", Json.toJson(saleService.listAmounts(list)));
		return ok(Json.toJson(result));
	}

	/**
	 * 更新订单状态(erp status)
	 *
	 * @return
	 */
	public Result updateErpStatus() {
		Map<String, Object> result = Maps.newHashMap();
		String hkToken = request().getHeader("token");

		Configuration config = Play.application().configuration().getConfig("safeApi");
		String localToken = config.getString("token");
		if (!hkToken.equals(localToken)) {
			result.put("suc", false);
			result.put("msg", "没有此权限");
			ok(Json.toJson(result));
		}
		JsonNode reqMap = request().body().asJson();
		try {
			Logger.info("更新订单状态请求参数[{}]", reqMap);
			if (reqMap == null || reqMap.size() <= 0) {
				result.put("suc", false);
				result.put("msg", "参数错误");
				return ok(Json.toJson(result));
			}
			Map<String, Object> map = saleMainService.updateErpStatus(reqMap.toString());
			Logger.info("更新订单状态结果：{}", map.toString());
			return ok(Json.toJson(map));
		} catch (Exception e) {
			Logger.info("更新订单状态错误:{}", e);
			result.put("suc", false);
			result.put("msg", "数据格式不正确");
			return ok(Json.toJson(result));
		}
	}
	
	/**
	 * 发货单录入：设置商品的到期日期
	 * 
	 * @return
	 */
	public Result setSelectedProductsExpirationDates(){
		JsonNode main = request().body().asJson();
		Logger.info("获取发货单商品到期日期，参数为：{}", main);
		Map<String, Object> result = productExpirationDateService.setSelectedProductsExpirationDates(main);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 云仓发货：设置商品的到期日期
	 * @return
	 */
	@ApiOperation(value="设置商品的到期日期",notes="<b>修改接口如下</b><br>新增参数：email(分销商账号),其余参数不变<br/>返回值:<br/>成功:{\"suc\":true,\"result\":{}}<br/>失败:{\"suc\":false,\"msg\":\"提示消息\"}<br/>其中result内容不变",httpMethod="POST",produces="application/json;charset=utf-8;")
	@ApiImplicitParams(
		{
		@ApiImplicitParam(name="body",required=true,paramType="body",dataType="string",
				defaultValue="{\n"
						+ "\"email\":\"zhengbc@qq.com\",\n"
						+ "\"selectedProducts\":[\n"
						+ "{\n"
						+ "\"sku\": \"IF942-1\",\n" 
						+ " \"batchNumber\": 1, \n"
						+ " \"title\": \"越南wismo番茄饼干棒22克\", \n"
					    + " \"interBarCode\": \"8936047445493\", \n"
					    + " \"warehouseName\": \"深圳仓\", \n"
					    + " \"warehouseId\": 2024, \n"
					    + " \"stock\": 155148, \n"
					    + " \"price\": 15.4, \n"
					    + " \"qty\": 1, \n"
					    + " \"isgift\": false, \n"
					    + " \"marketPrice\": 15.26, \n"
					    + " \"imgUrl\": \"https://static.tomtop.com.cn/images/I/1/IF942-1/IF942-1-1-80e0-4rJF.jpg\"\n"
						+ "}\n"
						+ "]\n"
						+ "}")}
	)
	public Result setCloudSelectedProductsExpirationDates(){
		JsonNode main = request().body().asJson();
		Logger.info("云仓发货-获取发货单商品到期日期，参数为：{}",main);
		if(JsonCaseUtil.checkParam(main,"email","selectedProducts")){
			return ok(Json.toJson(productExpirationDateService.setCloudSelectedProductsExpirationDates(main)));
		}
		return ok(Json.newObject().put("suc",false).put("msg", "参数错误"));
	}

	/**
	 * 生成客户订单API
	 * 
	 * @return
	 */
//	@ApiPermission
	@BodyParser.Of(BodyParser.Json.class)
	public Result postOrder() {
		JsonNode main = request().body().asJson();
		Logger.info("后台下单接口请求数据：{}", main);
		if (main == null || !main.has("email") || !main.has("warehouseId")
				|| !main.has("skuList")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("code", 101);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		String tradeNo = main.has("tradeNo") ? main.get("tradeNo").textValue() : null;
		//检查交易号
		if(StringUtils.isNotBlankOrNull(tradeNo) && saleService.checkTradeNo(tradeNo.trim())) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("code", 101);
			result.put("msg", "订单交易号重复，请重新输入");
			return ok(Json.toJson(result));
		}
		
		//修改逻辑 锁库方法
		return ok(Json.toJson(managerOrderService.order(main)));
	}

	/**
	 * 获取一段时间之内的销售发货单数量
	 * @return
	 */
	@ALogin
	@DivisionMember
	@BodyParser.Of(BodyParser.Json.class)
	public Result getSalesOrderCount() {
		JsonNode main = request().body().asJson();
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		String account = userService.getRelateAccounts();
		List<String> accounts = null;
		if(!StringUtils.isBlankOrNull(account)) {
			accounts = Arrays.asList(account.split(","));
		}
		String param = main.toString();
		return ok(Json.toJson(saleService.getSalesOrderCount(param,accounts)));
	}
	
	/**
	 * 更新发货单利润信息
	 * @param orderNo
	 * @return
	 */
	@ApiOperation(value = "更新发货单利润信息", notes = "od:发货单号", nickname = "saleOrder", httpMethod = "GET", produces = "text/plain")
	@ApiImplicitParams({
		@ApiImplicitParam(name="od",value="订单号",required=true,paramType="path",dataType = "String")
	})
	public Result caculateProfit(String orderNo) {
		return ok(managerOrderService.caculateProfit(orderNo));
	}
	
}
