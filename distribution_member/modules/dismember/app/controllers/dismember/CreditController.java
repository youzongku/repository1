package controllers.dismember;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import constant.dismember.Constant;
import dto.dismember.ExportCreditDto;
import entity.dismember.CreditOperationRecord;
import entity.dismember.DisCredit;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.ICreditOperationRecordService;
import services.dismember.ICreditService;
import services.dismember.ILoginService;
import utils.dismember.ExportUtil;
import utils.dismember.HttpUtil;

/**
 * @author hanfs
 * 描述：用户信用额度控制类
 *2016年4月20日
 */
public class CreditController extends Controller {

	@Inject
	private ICreditService creditService;

	@Inject
	private ILoginService loginService;

	@Inject
	private ICreditOperationRecordService creditOperationRecordService;

	/**
	 * 描述：获取所有的用户信用额度信息
	 * 2016年4月20日
	 * @return
	 */
	public Result getAllCredit(){
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if(null == params){
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		Integer currPage = params.containsKey("currPage") ? Integer.valueOf(params.get("currPage")) : 1;
		Integer pageSize = params.containsKey("pageSize") ? Integer.valueOf(params.get("pageSize")) : 10;
		Integer limiteState = (params.containsKey("limiteState")&&!Strings.isNullOrEmpty(params.get("limiteState"))) ? Integer.valueOf(params.get("limiteState")) : null;
		Integer redit = (params.containsKey("redit")&&!Strings.isNullOrEmpty(params.get("redit"))) ? Integer.valueOf(params.get("redit")) : 1;
		String sregdate = params.containsKey("sregdate") ? params.get("sregdate") : "";
		String eregdate = params.containsKey("eregdate") ? params.get("eregdate") : "";
		Map<String, Object> map = Maps.newHashMap();
		map.put("currPage", currPage);
		map.put("pageSize", pageSize);
		map.put("startNum", (currPage - 1) * pageSize);
		map.put("search", params.containsKey("search") ? params.get("search") : "");
		map.put("startTime", sregdate);
		map.put("endTime", eregdate);
		map.put("limiteState", limiteState);
		map.put("redit", redit);
		Integer comsumerType = 0;//分销商类型
		if(params.containsKey("comsumerType")){
			String comsumerTypeStr = utils.dismember.StringUtils.getString(params.get("comsumerType"), false);
			comsumerType = "".equals(comsumerTypeStr)?0:Integer.valueOf(comsumerTypeStr);
		}
		map.put("comsumerType", comsumerType);
		result.put("suc", true);
		result.put("page", creditService.getCreditsByPage(map));
		return ok(Json.toJson(result));
	}
	
	/**
	 * 描述：获取单个信用额度信息
	 * 2016年4月20日
	 * @return
	 */
	public Result getCreditInfo(){
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		JsonNode jsonParam = request().body().asJson();
		if (jsonParam == null || jsonParam.get("id") == null || Strings.isNullOrEmpty(jsonParam.get("id").asText())) {
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
	
		result.put("suc", true);
		result.put("credit", creditService.getCreditInfo(jsonParam.get("id").asInt()));
		return ok(Json.toJson(result));
	}
	/**
	 * 描述：添加用户信用额度
	 * 2016年4月20日
	 * @return
	 */
	public Result addCredit(){
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		JsonNode paramJson = request().body().asJson();
		if (paramJson == null) {
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		Logger.info("jsonPram:"+paramJson.toString());
		try {
			DisCredit credit = Json.fromJson(paramJson, DisCredit.class);
			credit.setCreateuser(loginService.getLoginContext(2).getEmail());
			result = creditService.addCredit(credit);
		} catch (Exception e) {
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
		}
		return ok(Json.toJson(result));
	}
	/**
	 * 描述：更新信用额度信息
	 * 2016年4月20日
	 * @return
	 */
	public Result updateCredit(){
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		JsonNode paramJson = request().body().asJson();
		if (paramJson == null || paramJson.get("id") == null || paramJson.get("creditLimit") == null) {
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		Logger.info("jsonPram:"+paramJson.toString());
		try {
			//当前用户邮箱
			String email = loginService.getLoginContext(2).getEmail();
			DisCredit credit = Json.fromJson(paramJson, DisCredit.class);
			result = creditService.updateCredit(email,credit);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
		}
		return ok(Json.toJson(result));
	}

	/**
	 * 描述：切换用户的激活状态
	 * 2016年5月18日
	 * @return
	 */
	public Result updIsActivated(){
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		JsonNode paramJson = request().body().asJson();
		if (paramJson == null || paramJson.get("id") == null) {
			Logger.info("paramJson->>>>>>>>"+paramJson);
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		Logger.info("jsonPram:"+paramJson.toString());
		try {
			//当前用户邮箱
			String email = loginService.getLoginContext(2).getEmail();
			DisCredit credit = Json.fromJson(paramJson, DisCredit.class);
			result = creditService.changeActivated(email,credit);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
		}
		return ok(Json.toJson(result));
	}

	/**
	 * 描述：审核用户额度
	 * 2016年4月22日
	 * @return
	 */
	public Result checkCredit(){
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		JsonNode paramJson = request().body().asJson();
		if (paramJson == null) {
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		Logger.info("jsonPram:"+paramJson.toString());
		try {
			String  comments = paramJson.get("comments")!=null?paramJson.get("comments").asText():"";
			String userEmail = paramJson.get("email")!=null?paramJson.get("email").asText():"";
			CreditOperationRecord record = new CreditOperationRecord();
			record.setComments(comments);
			record.setOperatorEmail(loginService.getLoginContext(2).getEmail());
			record.setOperatorType(1);
			record.setOperatorResult(1);
			record.setUserEmail(userEmail);
			boolean addRecordFlag = creditOperationRecordService.addOperationRecord(record);
			Logger.info("add credit operate record Result:"+(addRecordFlag?"successful":"failure"));
		} catch (Exception e) {
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
		}
		return ok(Json.toJson(result));
	}

	/**
	 * 描述：获取用户信用额度操作记录
	 * 2016年4月20日
	 * @return
	 */
	public Result getCreditOperRecord(){
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		JsonNode paramJson = request().body().asJson();
		if (paramJson == null) {
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		Logger.info("jsonPram:"+paramJson.toString());
		try {
			String userEmail = paramJson.get("email")!=null?paramJson.get("email").asText():"";
			Integer operateType = (paramJson.get("operateType")!=null&&!Strings.isNullOrEmpty(paramJson.get("operateType").asText()))?paramJson.get("operateType").asInt():null;
			result.put("suc", true);
			result.put("result",creditOperationRecordService.getOperationRecordsByEmail(userEmail,operateType));
			result.put("msg","获取用户额度操作记录成功");
		} catch (Exception e) {
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
		}
		return ok(Json.toJson(result));
	}

	/**
	 * 导出用戶額度
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws IOException
	 */
	public Result exportCredit() throws UnsupportedEncodingException {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			return redirect(HttpUtil.getHostUrl() + "/sysadmin");
		}
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
		
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		response().setHeader("Content-disposition", "attachment;filename="+new String("信用额度".getBytes(), "ISO8859-1")+".xls");
		Integer limiteState = map.containsKey("limiteState")&&!Strings.isNullOrEmpty(map.get("limiteState")[0]) ? Integer.valueOf(map.get("limiteState")[0]) : null;
		Integer redit = map.containsKey("redit")&&!Strings.isNullOrEmpty(map.get("redit")[0]) ? Integer.valueOf(map.get("redit")[0]) : 1;
		String sregdate = map.containsKey("sregdate")&&!Strings.isNullOrEmpty(map.get("sregdate")[0]) ? map.get("sregdate")[0] : "";
		String eregdate = map.containsKey("eregdate")&&!Strings.isNullOrEmpty(map.get("eregdate")[0]) ? map.get("eregdate")[0] : "";
		Map<String, Object> searchMap = Maps.newHashMap();
		searchMap.put("search", map.containsKey("search")&&!Strings.isNullOrEmpty(map.get("search")[0]) ? map.get("search")[0] : "");
		searchMap.put("startTime", sregdate);
		searchMap.put("endTime", eregdate);
		searchMap.put("limiteState", limiteState);
		searchMap.put("redit", redit);
		Integer comsumerType = 0;//分销商类型
		if(map.containsKey("comsumerType")){
			String comsumerTypeStr = utils.dismember.StringUtils.getString(map.get("comsumerType")[0], false);
			comsumerType = "".equals(comsumerTypeStr)?0:Integer.valueOf(comsumerTypeStr);
		}
		searchMap.put("comsumerType", comsumerType);
		List<ExportCreditDto> dtos = this.creditService.getExportCreditData(searchMap);
		Logger.info(loginService.getLoginContext(2).getEmail() + "导出用户额度数据，导出数据条数：" + dtos.size());
		return ok(ExportUtil.export("creditOfUser.xls", header, Constant.EXPORT_CREDIT_MAP, dtos));
	}
	
	/**
	 * 描述：校验用户是否还款
	 * 2016年5月10日
	 * @return
	 */
	public Result checkIsRepayByEmail(){
		return ok(Json.toJson(this.creditService.isRepay(request().body().asJson())));
	}
	
	/**
	 * 描述：根据邮箱删除用户永久额度
	 * 2016年5月10日
	 * @return
	 */
	public Result delCreditByEmail(){
		return ok(Json.toJson(this.creditService.delCreditAndRecordByEmail(request().body().asJson())));
	}
	
	public Result getCreditByEmail(String email){
		if (email == null || email.equals("")) {
			ObjectNode result = Json.newObject();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(result);
		}
	
		return ok(creditService.getCreditByEmail(email));
	}
}
