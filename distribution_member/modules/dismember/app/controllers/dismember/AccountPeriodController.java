package controllers.dismember;

import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import constant.dismember.Constant;
import controllers.annotation.ALogin;
import dto.dismember.ApReminderSettingParam;
import dto.dismember.ResultDto;
import entity.dismember.ApReminderSetting;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IAccountPeriodService;
import services.dismember.impl.LoginService;
import utils.dismember.JsonCaseUtil;
import vo.dismember.LoginContext;

/**
 * 账期管理控制类
 * @author zbc
 * 2017年2月17日 下午4:40:44
 */
@Api(value = "/账期模块", description = "account period")
public class AccountPeriodController extends Controller {
	
	@Inject
	private IAccountPeriodService apService;
	@Inject
	private LoginService loginService;
	
	@ApiOperation(
		    value = "刷数据使用-获取账期支付的订单的详情",
		    notes = "",
		    nickname = "",
		    httpMethod = "GET",produces="text/plain"
		    )
	@ApiImplicitParams({
//		@ApiImplicitParam(name="id",value="子账期ID",required=true,paramType="path",dataType = "Integer")
	})
	public Result brushApOrderDetail() {
		ResultDto<?> result = apService.brushApOrderDetail();
		return ok(Json.toJson(result));
	}
	
	/**
	 * 
	 * 重新定义账期开始时间逻辑 (业绩周期开始时间 就是开始时间，下一账期开始时间 上一账期的业绩周期结束时间的隔天) 
	 * 扩展账期周期类型的按天计算的功能（业绩周期固定为一个月）
	 * 保存账期记录
	 * @author zbc
	 * @since 2017年2月17日 下午4:43:48
	 *  * @param 
	 * 
	  {
		  "master": {
		    "account": "zhengbc@qq.com", 
		    "totalLimit": 5000, 
		    "oaAuditCode": "OA123456", 
		    "contractNo": "HT123456", 
		    "dutyOfficer": "责任人", 
		    "periodType": 1, 
		    "periodLength": 1
		  }, 
		  "slave": {
		    "startTime": "2017-02-25",
		    "redLineDays": 7, 
		    "performanceEndTime": "2017-02-27"
		  }
		}
	 * 
	 */
	@ALogin
	public Result addAccountPeriod() {
		JsonNode node = request().body().asJson();
		Logger.info("保存账期，参数:{}",node);
		if (JsonCaseUtil.checkParam(node, new String[] { "master","slave"})) {
			return ok(Json.toJson(apService.addAccountPeriod(node.toString())));
		}
		
		return ok(Json.toJson(new ResultDto<>(102, "参数错误", null)));
	}
	
	/**
	 * 查询下账期参数
	 * @author zbc
	 * @since 2017年3月3日 下午5:52:44
	 */
	public Result nextSlaveMsg(Integer id){
		Logger.info("查询下账期参数，参数: id = {}",id);
		if (id==null || id<1) {
			return ok(Json.toJson(new ResultDto<>(false ,"参数错误")));
		}
		return ok(Json.toJson(apService.nextSlave(id)));
	}
	
	/**
	 * 
	 * 1 待还款，已完结，已逾期 （账户未冻结）并且 未开启下一期
	 * 开始时间 为 上个业绩周期结束时间 不能修改
	 * 完结才可以开启下一期
	 * {
		  "id": 2, 
		  "redLineDays": 8, 
		  "performanceEndTime": "2017-02-27"
		}
	 * 开启下一账期
	 * @author zbc
	 * @since 2017年2月18日 上午11:22:16
	 */
	@ALogin
	public Result nextSlave(){
		JsonNode node = request().body().asJson();
		Logger.info("开启下一账期，参数: {}",node);
		if(JsonCaseUtil.checkParam(node, new String[]{
				"id","redLineDays","performanceEndTime"})){
			return ok(Json.toJson(apService.nextSlave(node.toString(),loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK).getEmail())));
		}

		return ok(Json.toJson(new ResultDto<>(102,"参数错误",null)));
	}
	
	
	/**
	 * 1，更新主账期 ，前提是只有一个子账期，并且未生效，注意修改周期长度是，对业绩结束周期的约束时候符合
		参数：	
		 {
		   "id":6,
		   "totalLimit":5000,
		   "oaAuditCode": "OA123456", 
		   "contractNo": "HT123456", 
		   "dutyOfficer": "责任人", 
		   "periodType": 0, 
		   "periodLength": 7
		 }
	 * @author zbc
	 * @since 2017年2月17日 下午4:57:11
	 */
	@ALogin
	public Result modifyMaster(){
		JsonNode node = request().body().asJson();
		Logger.info("更新主账期modifyMaster，参数: {}",node);
		Logger.info("account periond master modify:[{}]",node);
		if (JsonCaseUtil.checkParam(node, new String[] { "id", "totalLimit", "oaAuditCode", "contractNo", "dutyOfficer",
				"periodType", "periodLength" })) {
			return ok(Json.toJson(apService.updateMaster(node.toString())));
		}

		return ok(Json.toJson(new ResultDto<>(102, "参数错误", null)));
	}
	
	/**
	 * 2，更新子账期，只能修改开始时间，红线天数，业绩周期结束时间，其他时间都是计算出来的
	          参数：
	    {
	    	"id":5,
	    	"startTime": "2017-02-25", 
		    "redLineDays": 7, 
		    "performanceEndTime": "2017-02-27"
	    } 
	 * @author zbc
	 * @since 2017年2月26日 上午11:35:00
	 */
	@ALogin
	public Result modifySlave(){
		JsonNode node = request().body().asJson();
		Logger.info("更新子账期modifySlave，参数: {}",node);
		if (JsonCaseUtil.checkParam(node, new String[] { "id", "startTime", "redLineDays", "performanceEndTime" })) {
			return ok(Json.toJson(apService.updateSlave(node.toString())));
		}
		
		return ok(Json.toJson(new ResultDto<>(102, "参数错误", null)));
	}
	
	
	
	/**
	 * 获取账期记录信息(单条记录查询)
	 * @author zbc
	 * @since 2017年2月17日 下午4:59:41
	 */
	@ALogin
	public Result readMasterOne(Integer id){
		Logger.info("获取账期记录信息(单条记录查询)，参数: id = {}",id);
		if (id==null || id<1) {
			return ok(Json.toJson(new ResultDto<>(false ,"参数错误")));
		}
		return ok(Json.toJson(apService.readMasterOne(id)));
	}
	
	/**
	 * 单个查询子账期
	 * @author zbc
	 * @since 2017年3月3日 下午2:49:56
	 */
	@ALogin
	public Result readSlaveOne(Integer id){
		Logger.info("单个查询子账期，参数: id = {}",id);
		if (id==null || id<1) {
			return ok(Json.toJson(new ResultDto<>(false ,"参数错误")));
		}
		return ok(Json.toJson(apService.readSlaveOne(id)));
	}
	
	/**
	 * 分页查询账期信息
	 * @author zbc
	 * @since 2017年2月17日 下午5:59:34
	 */
	@ALogin
	public Result readMasterList(){
		JsonNode node = request().body().asJson();
		Logger.info("分页查询账期信息readMasterList，参数: {}",node);
		Logger.info("account periond master read list master:[{}]", node);
		if (node == null) {
			return ok(Json.toJson(new ResultDto<>(102, "参数错误", null)));
		}

		return ok(Json.toJson(apService.readMasterList(node.toString())));
	}
	
	@ApiOperation(value="查看账期明细",httpMethod="POST",notes="<b>该接口有后台登录校验<b/><br/>新增参数，返回值不变")
	@ApiImplicitParams({
		@ApiImplicitParam(name="body",required=true,dataType="string",paramType="body",
				value="新增参数\"isUnfinished\":是否展示未完结账期，布尔类型",
				defaultValue="{\n\"masterId\":9,\n\"currPage\":1,\n\"pageSize\":10,\n\"std\":\"\",\n\"state\":\"\",\n\"sort\":\"\",\n\"filter\":\"asc\"\n}")
	})
	@ALogin
	public Result readSlaveList(){
		JsonNode node = request().body().asJson();
		Logger.info("readSlaveList，参数: {}",node);
		if (node == null) {
			return ok(Json.toJson(new ResultDto<>(102, "参数错误", null)));
		}
		
		return ok(Json.toJson(apService.readSlaveList(node.toString())));
	}
	/**
	 * 手动调用自动任务逻辑：
	 * 1，订单次日生效操作
	 * @author zbc
	 * @since 2016年11月20日 下午6:29:31
	 */
	public Result deal(){
		return ok(Json.toJson(apService.dealAccountPeriod()));
	}
	
	/**
	 * 查看账期操作日志
	 * @author zbc
	 * @since 2017年2月27日 下午12:20:37
	 */
	@ALogin
	public Result masterRecord(Integer id){
		Logger.info("查看账期操作日志masterRecord，参数: id = {}",id);
		if (id==null || id<1) {
			return ok(Json.toJson(new ResultDto<>(false ,"参数错误")));
		}
		return ok(Json.toJson(apService.readMasterRecord(id)));
	}
	
	/**
	 * 查看子账期操作日志
	 * @author zbc
	 * @since 2017年2月27日 下午12:21:18
	 */
	@ALogin
	public Result slaveRecord(Integer id){
		Logger.info("查看子账期操作日志slaveRecord，参数: id = {}",id);
		if (id==null || id<1) {
			return ok(Json.toJson(new ResultDto<>(false ,"参数错误")));
		}
		return ok(Json.toJson(apService.readSlaveRecord(id)));
	}
	
	@ALogin
	public Result getStartDate(Integer id){
		Logger.info("getStartDate，参数: id = {}",id);
		if (id==null || id<1) {
			return ok(Json.toJson(new ResultDto<>(false ,"参数错误")));
		}
		return ok(Json.toJson(apService.getStartDate(id)));
	}
	/**
	 * 禁用账期
	 * @author zbc
	 * @since 2017年3月4日 上午10:03:48
	 */
	@ALogin
	public Result stopSlave(Integer id){
		Logger.info("禁用账期，参数: id = {}",id);
		if (id==null || id<1) {
			return ok(Json.toJson(new ResultDto<>(false ,"参数错误")));
		}
		return ok(Json.toJson(apService.disabled(id)));
	}
	/**
	 * 分页查询
	 * @author zbc
	 * @since 2017年3月4日 下午3:30:44
	 */
	@ALogin
	public Result readOrderList(){
		JsonNode node = request().body().asJson();
		Logger.info("分页查询readOrderList，参数: {}",node);
		if(node == null){
			return ok(Json.toJson(new ResultDto<>(false, "参数错误")));
		}

		return ok(Json.toJson(apService.readOrderList(node.toString())));
	}
	
	/**
	 * 生成账单数据
	 * {
	 	"orderIds":[1,2],
	 	"id":5
	  	}
	 * @author zbc
	 * @since 2017年3月5日 上午10:11:19
	 */
	@ALogin
	public Result generBill(){
		JsonNode node = request().body().asJson();
		Logger.info("生成账单数据，参数: {}",node);
		if(node == null){
			return ok(Json.toJson(new ResultDto<>(false, "参数错误")));
		}
		
		return ok(Json.toJson(apService.generBill(node.toString(),loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK).getEmail(),false)));
	}
	
	/**
	 * 查看应还款项
	 * @author zbc
	 * @since 2017年3月6日 下午5:37:12
	 */
	@ApiOperation(
		    value = "后台用户查看账单",
		    notes = "id:子账期ID",
		    nickname = "Read Bill",
		    httpMethod = "GET",produces="text/plain"
		    )
	@ApiImplicitParams({
			@ApiImplicitParam(name="id",value="子账期ID",required=true,paramType="path",dataType = "Integer")
	})
	@ALogin
	public Result readBill(Integer id) {
		Logger.info("查看应还款项readBill，参数: id = {}",id);
		if (id==null || id<1) {
			return ok(Json.toJson(new ResultDto<>(false ,"参数错误")));
		}
		return ok(Json.toJson(apService.readBill(id)));
	}
	
	/**
	 * 重新生成应还款项
	 * @author zbc
	 * @since 2017年3月6日 下午5:36:52
	 */
	@ALogin
	public Result delBill(Integer id){
		Logger.info("重新生成应还款项delBill，参数: id = {}",id);
		if (id==null || id<1) {
			return ok(Json.toJson(new ResultDto<>(false ,"参数错误")));
		}
		return ok(Json.toJson(apService.delBill(id)));
	}
	
	@ApiOperation(value="账期核销",httpMethod="POST",
			notes="<b>该接口有后台登陆校验</b><br>替换原先/ap/b/:id/cof接口,可以进行强制核销",
			response=ResultDto.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="parma",required=true,dataType="string",paramType="body",
				value="\"id\":子账期id,必传<br/>\"isForce\":是否强制核销 (true/false) 非必传<br/>\"remark\":强制核销原因,如果 isForce 为 true时必传"
				,defaultValue="{\n\"id\":6\n}")
	})
	@ALogin
	public Result chargeOff(){
		JsonNode json = request().body().asJson();
		Logger.info("chargeOff，参数:{}",json);
		if (JsonCaseUtil.checkParam(json, new String[]{"id"})) {
			return ok(Json.toJson(apService.chargeOff(json.toString(),loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK).getEmail())));
		}
		return ok(Json.toJson(new ResultDto<>(false ,"参数错误")));
	}
	
	/**
	 * 账期短信提醒设置
	 * @return
	 */
	@ApiOperation(value = "账期短信提醒设置", notes = "距离合同账期还有几天，就要发送短信提醒", nickname = "", httpMethod = "POST")
	@ApiImplicitParam(name = "body", value = "", required = false,
		        dataType = "application/json", paramType = "body")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
			,defaultValue = "{\"account\":\"001001@qq.com\", \"enable\":true, \"daysAgo\":1}"
				) })
	@ALogin
	public Result setReminderSetting(){
		Map<String, Object> result = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		Logger.info("账期短信提醒设置，参数: {}",node);
		if (node == null 
				|| !node.has("account") 
				|| StringUtils.isEmpty(node.get("account").asText())
				|| !node.has("enable") ) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
	
		Logger.info("账期短信提醒设置，参数：{}",node);
		boolean enable = node.get("enable").asBoolean();
		Integer daysAgo = null;
		if (enable) {// 启用时才判断天数
			if (!node.has("daysAgo")) {
				result.put("suc", false);
				result.put("msg", "请设置天数");
				return ok(Json.toJson(result));
			}
			daysAgo = node.get("daysAgo").asInt();
			if (daysAgo<1) {
				result.put("suc", false);
				result.put("msg", "天数不能小于1天");
				return ok(Json.toJson(result));
			}
		}
		
		String account = node.get("account").asText();
		LoginContext lc = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK);
		ApReminderSettingParam rsParam = new ApReminderSettingParam(account, enable, daysAgo, lc.getEmail());
		return ok(Json.toJson(apService.setReminderSetting(rsParam)));
	}
	
	/**
	 * 获取账期短信提醒设置
	 * @return
	 */
	@ApiOperation(value = "获取账期短信提醒设置", notes = "距离合同账期还有几天，就要发送短信提醒", nickname = "", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
			,defaultValue = "{\"account\":\"001001@qq.com\"}"
				) })
	@ALogin
	public Result getReminderSetting(){
		JsonNode node = request().body().asJson();
		Logger.info("获取账期短信提醒设置，参数: {}",node);
		if (node == null 
				|| !node.has("account") 
				|| StringUtils.isEmpty(node.get("account").asText())) {
			return ok(Json.toJson(new ResultDto<>(false, "参数错误")));
		}
		
		Logger.info("获取账期提醒配置，参数：{}",node);
		ApReminderSetting spSetting = apService.getReminderSetting(node.get("account").asText());
		Map<String, Object> result = Maps.newHashMap();
		result.put("suc", true);
		result.put("spSetting", spSetting);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 
	 * {
		  "id": 6, 
		  "totalLimit": 5000, 
		  "oaAuditCode": "OA123456", 
		  "contractNo": "HT123456", 
		  "remarks": "XXXXXX"
		}
	 * 调账主账期额度
	 * 1、修改后额度必须大于等于原来的额度
	 * 2、备注必填 1-100
	 * 可改字段:整个账期额度 
		       OA审批单号
		             账期合同编号
		             
	 * @author zbc
	 * @since 2017年6月8日 上午9:10:16
	 * @return
	 */
	@ApiOperation(value="账期额度调整",httpMethod="POST",notes="",response=ResultDto.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="body",required=true,dataType="string",paramType="body",
				value="<b>该接口有后台登陆校验</b>"
				+ "<br/>\"id\":主账期id 必传<br/>\"totalLimit\":账期额度,必传"
				+ "<br/>\"oaAuditCode\":OA审批单号,必传"
				+ "<br/>\"contractNo\": 账期合同编号,非必传"
				+ "<br/>\"remarks\": 修改备注,必传",
				defaultValue=
					"{\n"+
					  "\"id\": 6, \n"+
					  "\"totalLimit\": 5000, \n"+
					  "\"oaAuditCode\": \"OA123456\", \n"+
					  "\"contractNo\": \"HT123456\", \n"+
					  "\"remarks\": \"XXXXXX\"\n"+
					"}")
	})
	@ALogin
	public Result adjust(){
		JsonNode json = request().body().asJson();
		String admin = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK).getEmail();
		Logger.info("{}调整额度参数:{}",admin,json);
		if(JsonCaseUtil.checkParam(json, new String[]{"id","totalLimit","oaAuditCode","remarks"})){
			return ok(Json.toJson(apService.adjust(json.toString(),admin)));
		}
		return ok(Json.toJson(new ResultDto<>(102, "参数错误", null)));
	}
}
