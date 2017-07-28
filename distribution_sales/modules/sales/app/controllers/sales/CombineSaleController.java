package controllers.sales;

import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.ALogin;
import dto.JsonResult;
import dto.sales.hb.CombineInfoResult;
import dto.sales.hb.CombinedSalesPageQueryParam;
import dto.sales.hb.HBDeliveryAuditParam;
import entity.sales.SaleDetail;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.sales.ICombineSaleService;
import services.sales.IUserService;
import util.sales.JsonCaseUtil;
import util.sales.SalesCombinationStatus;
import util.sales.StringUtils;

/**
 * 合并发货发货单控制类型
 * @author zbc
 * 2017年5月19日 上午9:40:27
 */
@Api(value="/合并发货单",description="Combine Sale Order")
public class CombineSaleController extends Controller {
	
	@Inject
	private ICombineSaleService combineService;
	@Inject
	private IUserService userService;
	
	/**
	 * 获取合并发货单下的所有商品详情
	 * @return
	 */
	public Result getConbinedProDetails() {
		JsonNode json = request().body().asJson();
		Logger.info("getConbinedProDetails，参数：{}",json);
		if (json==null || StringUtils.isBlankOrNull(json.get("hbNo").asText())) {
			return ok(Json.toJson(JsonResult.newIns().result(false).data("参数错误")));
		}
		String hbNo = json.get("hbNo").asText();
		JsonResult<List<SaleDetail>> result = combineService.getConbinedProDetails(hbNo);
		return ok(Json.toJson(result));
	}
	
	@ApiOperation(
		    value = "查看日志",
		    notes = "hbId:合并单的id",
		    nickname = "",
		    httpMethod = "GET",produces="text/plain"
		    )
	@ApiImplicitParams({
			@ApiImplicitParam(name="hbId",value="合并单的id",required=true,paramType="path",dataType = "Integer")
	})
	public Result getCombinedLogs(Integer hbId){
		Logger.info("获取合并发货单，参数：hbId = {}",hbId);
		if (hbId==null || hbId<1) {
			return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
		}
		return ok(Json.toJson(combineService.getCombinedLogs(hbId)));
	}
	
	/**
	 * 参数：
		{
			"uid":"1fcdde74eea14f339b12807eadb3c94e",
			"receiver":"啦啦啦"
		}
	 * 合并发货单
	 * @author zbc
	 * @since 2017年5月19日 上午9:42:32
	 * @return
	 */
	@ApiOperation(value="确认单合并发货",notes="<b>该接口有后台登陆校验</b>",
			httpMethod="POST",response=JsonResult.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="body",value=""
				+ "uid：合并发货信息uid,必传<br/>"
				+ "receiver:收货人信息,必传<br/>"
				+ "isNeedInvoice:是否开发票(true:是，false：否)非必传<br/>"
				+ "invoiceType:发票类型(1:个人，2：公司),isNeedInvoice为true 时必传<br/>"
				+ "invoiceTitle:发票抬头 invoiceType 为2 时必传",
				required=true,paramType="body",dataType="body",
				defaultValue=	"{\n"+
								"\"uid\":\"1fcdde74eea14f339b12807eadb3c94e\",\n"+
								"\"receiver\":\"啦啦啦\"\n"+
								"}"
		)
	})
	@ALogin
	public Result combine(){
		JsonNode json = request().body().asJson();
		String admin =  userService.getAdminAccount();
		Logger.info("{}合并发货参数{}",admin,json);
		if(JsonCaseUtil.checkParam(json,"uid","receiver")){
			return ok(Json.toJson(combineService.combineSaleOrder(json.toString(),admin)));
		}
		return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
	}
	
	/**
	 * 
	 {
	 	"orderNos":
	 	[
	 		"XS001",
	 		"XS002"
	 	]
	 }
	 * 获取合并信息
	 * 合并条件:
	 *     1、 待付款，并且未合并过
	 *     2、 仓库ID            必须相同
	 *     3 、收货地址，联系方式    必须相同
	 *     4、 物流方式                     必须相同
	 *     5、分销商                          必须相同	
	 * @author zbc
	 * @since 2017年5月19日 下午5:42:34
	 * @return
	 */
	@ApiOperation(value="获取合并发货信息",httpMethod="POST",notes="<b>该接口有后台登录校验</b><br/>选择订单，获取合并发货信息",response=CombineInfoResult.class)
	@ApiImplicitParams(
		{
			@ApiImplicitParam(name="body",required=true,
					value="\"orderNos\":发货单号数组  isAll 不传或者不为true时 必传<br/>"
					+ "\"isAll\":全选所有搜索结果 布尔型，非必传<br/>"
					+ "\"pageSearch\": 查询参数，isAll 为true时，必传，数据与分页查询一样",dataType="body",paramType="body",
					defaultValue="{\n"+
						 	"\"orderNos\":\n"+
						 	"[\n"+
						 		"\"XS2017052209502400011374\",\n"+
						 		"\"XS2017052209491000011373\"\n"+
						 	"]\n"+
						 "}")
		}
	)
	@ALogin
	public Result toCombineInfo(){
		JsonNode json = request().body().asJson();
		String admin = userService.getAdminAccount();
		Logger.info("{}合并发货参数:{}",admin,json);
		// 关联分销商
		if(JsonCaseUtil.checkParam(json)){
			return ok(Json.toJson(combineService.getToCombineInfo(json.toString(),admin,userService.getRelateAccounts())));
		}
		return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
	}
	
	/**
	 * 分页查询，合并发货列表
	 * @return
	 */
	@ApiOperation(value="分页查询，合并发货列表",notes="", httpMethod="POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name="body",required=true,paramType="body",dataType="string",
					value="<b>新增参数</b><br>\"sidx\":排序字段（可选:\"create_time\"）<br/>\"sord\":排序规则（可选\"asc\",\"desc\"）",
					defaultValue="{\n"
							+ "\"pageSize\":10,\n"
							+ "\"currPage\":1,\n"
							+ "\"status\":1,\n"
							+ "\"consumerType\":1,\n"
							+ "\"distributionMode\":1,\n"
							+ "\"warehouseId\":2024,\n"
							+ "\"combinedStartTime\":\"2017-05-01\",\n"
							+ "\"combinedEndTime\":\"2017-07-10\",\n"
							+ "\"searchText\":\"003003@qq.com\"\n"
							+ "}")}
		)
	@ALogin
	public Result hbDeliveryList() {
		JsonNode node = request().body().asJson();
		Logger.info("分页查询，合并发货列表，参数：{}",node);
		if (node==null || !checkPageQueryParam(node)) {
			return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
		}
		CombinedSalesPageQueryParam query = parsePageQueryParam(node);
		return ok(Json.toJson(combineService.getSalesHBDeliveryPage(query)));
	}
	
	/**
	 * 分页查询，合并发货列表，待客服审核的
	 * @return
	 */
	@ApiOperation(value="分页查询，合并发货列表，待客服审核的",notes="", httpMethod="POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name="body",required=true,paramType="body",dataType="string",
					value="<b>新增参数</b><br>\"sidx\":排序字段（可选:\"create_time\"）<br/>\"sord\":排序规则（可选\"asc\",\"desc\"）",
					defaultValue="{\n"
							+ "\"pageSize\":10,\n"
							+ "\"currPage\":1,\n"
							+ "\"consumerType\":1,\n"
							+ "\"distributionMode\":1,\n"
							+ "\"warehouseId\":2024,\n"
							+ "\"combinedStartTime\":\"2017-05-01\",\n"
							+ "\"combinedEndTime\":\"2017-07-10\",\n"
							+ "\"searchText\":\"003003@qq.com\"\n"
							+ "}")}
		)
	@ALogin
	public Result csAuditHbDeliveryList() {
		JsonNode node = request().body().asJson();
		Logger.info("分页查询，合并发货列表，待客服审核的，参数：{}",node);
		if (node==null || !checkPageQueryParam(node)) {
			return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
		}
		CombinedSalesPageQueryParam query = parsePageQueryParam(node);
		query.setStatus(SalesCombinationStatus.WAITING_AUDIT_CS);
		return ok(Json.toJson(combineService.getSalesHBDeliveryPage(query)));
	}
	
	/**
	 * 分页查询，合并发货列表，待财务审核的
	 * @return
	 */
	@ApiOperation(value="分页查询，合并发货列表，待财务审核的",notes="", httpMethod="POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name="body",required=true,paramType="body",dataType="string",
					value="<b>新增参数</b><br>\"sidx\":排序字段（可选:\"create_time\"）<br/>\"sord\":排序规则（可选\"asc\",\"desc\"）",
					defaultValue="{\n"
							+ "\"pageSize\":10,\n"
							+ "\"currPage\":1,\n"
							+ "\"consumerType\":1,\n"
							+ "\"distributionMode\":1,\n"
							+ "\"warehouseId\":2024,\n"
							+ "\"combinedStartTime\":\"2017-05-01\",\n"
							+ "\"combinedEndTime\":\"2017-07-10\",\n"
							+ "\"searchText\":\"003003@qq.com\"\n"
							+ "}")
			})
	@ALogin
	public Result financeAuditHbDeliveryList() {
		JsonNode node = request().body().asJson();
		Logger.info("分页查询，合并发货列表，待财务审核的，参数：{}",node);
		if (node==null || !checkPageQueryParam(node)) {
			return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
		}
		CombinedSalesPageQueryParam query = parsePageQueryParam(node);
		query.setStatus(SalesCombinationStatus.WAITING_AUDIT_FINANCE);
		return ok(Json.toJson(combineService.getSalesHBDeliveryPage(query)));
	}
	
	private boolean checkPageQueryParam(JsonNode node) {
		if (!node.has("pageSize") 
				|| node.get("pageSize").asInt()<1
				|| !node.has("currPage")
				|| node.get("currPage").asInt()<1) {
			return false;
		}
		
		return true;
	}
	
	private CombinedSalesPageQueryParam parsePageQueryParam(JsonNode node){
		CombinedSalesPageQueryParam query = new CombinedSalesPageQueryParam();
		query.setPageSize(JsonCaseUtil.jsonToInteger(node.get("pageSize")));
		query.setCurrPage(JsonCaseUtil.jsonToInteger(node.get("currPage")));
		query.setStatus(JsonCaseUtil.jsonToInteger(node.get("status")));
		query.setConsumerType(JsonCaseUtil.jsonToInteger(node.get("consumerType")));
		query.setDistributionMode(JsonCaseUtil.jsonToInteger(node.get("distributionMode")));
		query.setWarehouseId(JsonCaseUtil.jsonToInteger(node.get("warehouseId")));
		query.setCombinedStartTime(JsonCaseUtil.jsonToString(node.get("combinedStartTime")));
		query.setCombinedEndTime(JsonCaseUtil.jsonToString(node.get("combinedEndTime")));
		query.setSearchText(JsonCaseUtil.jsonToString(node.get("searchText")));
		query.setSord(JsonCaseUtil.jsonToString(node.get("sord")));
		query.setSidx(JsonCaseUtil.jsonToString(node.get("sidx")));
		return query;
	}
	
	private HBDeliveryAuditParam parseAuditParam(JsonNode node) {
		String salesHbNo = JsonCaseUtil.jsonToString(node.get("salesHbNo"));
		boolean passed = node.get("passed").asBoolean();
		String remarks = JsonCaseUtil.jsonToString(node.get("remarks"));
		
		HBDeliveryAuditParam param = new HBDeliveryAuditParam(salesHbNo, passed, remarks);
		
		if (passed) {
			param.setReceiver(JsonCaseUtil.jsonToString(node.get("receiver")));
			param.setProvinceName(JsonCaseUtil.jsonToString(node.get("provinceName")));
			param.setCityName(JsonCaseUtil.jsonToString(node.get("cityName")));
			param.setAreaName(JsonCaseUtil.jsonToString(node.get("areaName")));
			param.setAddrDetail(JsonCaseUtil.jsonToString(node.get("addrDetail")));
			param.setTel(JsonCaseUtil.jsonToString(node.get("tel")));
			param.setPostCode(JsonCaseUtil.jsonToString(node.get("postCode")));
		}
		
		return param;
	}
	
	private boolean auditCommonParamsIllegal(JsonNode node) {
		return node==null || !node.has("salesHbNo")
				|| StringUtils.isBlankOrNull(JsonCaseUtil.jsonToString(node.get("salesHbNo")))
				|| !JsonCaseUtil.jsonToString(node.get("salesHbNo")).startsWith("HBXS")
				|| !node.has("passed")
				|| StringUtils.isBlankOrNull(JsonCaseUtil.jsonToString(node.get("passed")));
	}
	
	/**
	 * 客服审核
	 * @return
	 */
	@ApiOperation(value="客服审核",notes="参数分为两种："
			+ "1、当审核不通过时，需传以下参数<br>"
			+ "salesHbNo：合并单单号，必传<br>"
			+ "passed：是否通过，false，必传<br>"
			+ "remarks：备注<br><br>"
			+ "2、当审核通过时，需传以下参数<br>"
			+ "salesHbNo：合并单单号，必传<br>"
			+ "passed：是否通过，true，必传<br>"
			+ "remarks：备注<br>"
			+ "receiver：收货人，必传<br/>"
			+ "provinceName：省，必传<br/>"
			+ "cityName：市，必传<br/>"
			+ "areaName：区，必传<br/>"
			+ "addrDetail：详细地址，必传<br/>"
			+ "tel：收货人电话，必传<br/>"
			+ "postCode：邮编", httpMethod="POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name="body",required=true,paramType="body",dataType="string",
					defaultValue="{\n"
							+ "\"salesHbNo\":\"HBXS2017052218150000000003\",\n"
							+ "\"passed\":true,\n"
							+ "\"receiver\":\"收货人\",\n"
							+ "\"provinceName\":\"北京市\",\n"   
							+ "\"cityName\":\"北京市\",\n"
							+ "\"areaName\":\"东城区\",\n"
							+ "\"addrDetail\":\"深圳华南城\",\n"
							+ "\"tel\":\"15768859658\",\n"
							+ "\"remarks\":\"这里是备注\"\n"
							+ "}")}
		)
	@ALogin
	public Result auditByCustomerService() {
		JsonNode node = request().body().asJson();
		Logger.info("合并发货，客服审核，参数：{}",node);
		if (auditCommonParamsIllegal(node) || (
				!node.has("receiver") || StringUtils.isBlankOrNull(node.get("receiver").asText())
				|| !node.has("provinceName") || StringUtils.isBlankOrNull(node.get("provinceName").asText())
				|| !node.has("cityName") || StringUtils.isBlankOrNull(node.get("cityName").asText())
				|| !node.has("areaName") || StringUtils.isBlankOrNull(node.get("areaName").asText())
				|| !node.has("addrDetail") || StringUtils.isBlankOrNull(node.get("addrDetail").asText())
				|| !node.has("tel") || StringUtils.isBlankOrNull(node.get("tel").asText())
				)) {
			return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
		}
		
		HBDeliveryAuditParam auditParam = parseAuditParam(node);
		auditParam.setAuditUser(userService.getAdminAccount());
		JsonResult<?> result = combineService.auditByCustomerService(auditParam);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 获取财务审核时看到的统计数据
	 * @param hbId
	 * @return
	 */
	@ApiOperation(
		    value = "查看详情",
		    notes = "毛收入 totalPlatformamount<br>"
		    		+ "到仓价总计 totalArrvicetotal<br>到仓价利润 totalProfit<br>"
		    		+ "到仓价利润率 totalProfitmargin<br>到仓价总成本 totalTotalcost<br>"
		    		+ "清货价总计 totalClearancepricetotal<br>清货价利润 totalClearprofit<br>"
		    		+ "清货价利润率 totalClearprofitmargin<br>清货价总成本 totalCleartotalcost<br>"
		    		+ "实际运费 originalTotalBbcPostage<br>操作费 totalOptfee<br>"
		    		+ "合同费用 totalContractcharge<br>店铺扣点 totalSdpamount",
		    nickname = "",
		    httpMethod = "GET",produces="text/plain"
		    )
	@ApiImplicitParams({
			@ApiImplicitParam(name="hbId",value="合并单的id",required=true,paramType="path",dataType = "Integer")
	})
	public Result info2auditByFinance(Integer hbId) {
		Logger.info("获取合并发货单，参数：hbId = {}",hbId);
		if (hbId==null || hbId<1) {
			return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
		}
		return ok(Json.toJson(combineService.info2auditByFinance(hbId)));
	}
	
	/**
	 * 财务审核
	 * @return
	 */
	@ApiOperation(value="财务审核",notes="salesHbNo：合并单单号，必传<br>"
			+ "passed：是否通过，true/false，必传<br>"
			+ "remarks：备注", httpMethod="POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name="body",required=true,paramType="body",dataType="string",
					defaultValue="{\n"
							+ "\"salesHbNo\":\"HBXS2017052218150000000003\",\n"
							+ "\"passed\":true,\n"
							+ "\"remarks\":\"这里是备注\"\n"
							+ "}")}
		)
	@ALogin
	public Result auditByFinance() {
		JsonNode node = request().body().asJson();
		Logger.info("合并发货，财务审核，参数：{}",node);
		if (auditCommonParamsIllegal(node)) {
			return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
		}
		HBDeliveryAuditParam auditParam = parseAuditParam(node);
		auditParam.setAuditUser(userService.getAdminAccount());
		JsonResult<?> result = combineService.auditByFinance(auditParam);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 利润计算
	 * 1、获取所有发货单信息
	 * 2、将成本，毛收入累加
	 * 3、重新计算利润值
	 * @author zbc
	 * @since 2017年5月23日 下午12:15:25
	 * @return
	 */
	@ApiOperation(value="计算利润值",httpMethod="GET",notes="计算合并发货单 所有订单的利润信息",response=JsonResult.class)
	@ApiImplicitParams(
		{@ApiImplicitParam(name="od",required=true,dataType="string",value="合并发货单号",paramType="path",defaultValue="HBXS2017052309401200000006")}
	)
	public Result calculation(String od){
		return ok(Json.toJson(combineService.calculation(od)));
	}
	
	
	@ApiOperation(
		    value = "查看详情",
		    notes = "hbId:合并单的id",
		    nickname = "",
		    httpMethod = "GET",produces="text/plain"
		    )
	@ApiImplicitParams({
			@ApiImplicitParam(name="hbId",value="合并单的id",required=true,paramType="path",dataType = "Integer")
	})
	public Result getACombination(Integer hbId) {
		Logger.info("获取合并发货单，参数：hbId = {}",hbId);
		if (hbId==null || hbId<1) {
			return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
		}
		return ok(Json.toJson(combineService.getACombination(hbId)));
	}
	
	/**
	 * 已关闭合并发货单
	 * 可以批量下单
	 * @author zbc
	 * @since 2017年5月23日 下午5:06:15
	 * @return
	 */
	@ApiOperation(value="批量复制订单",httpMethod="POST",notes="批量复制合并发货单下的所有发货单",response=JsonResult.class)
	@ApiImplicitParams(
		{
			@ApiImplicitParam(name="body",required=true,value="od:合并发货单号,必传<br/> orderNos:发货单数组,必传",
					defaultValue="{\n"
							+ "\"od\":\"HBXS2017052309401200000006\",\n"
							+"\"orderNos\":\n"
							+"[\n"
							+"\"XS2017052309391500011406\",\n"
							+"\"XS2017052309394600011407\"\n"
							+"]\n"
							+ "}",
							dataType="string",paramType="body")
		}
	)
	@ALogin
	public Result batchOrder(){
		JsonNode json = request().body().asJson();
		String admin = userService.getAdminAccount();
		Logger.info("{}合并发货单批量复制:{}",admin,json);
		if(JsonCaseUtil.checkParam(json,"od","orderNos")){
			return ok(Json.toJson(combineService.batchOrder(admin,json.toString())));
		}
		return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
		
		
	
	}
}
