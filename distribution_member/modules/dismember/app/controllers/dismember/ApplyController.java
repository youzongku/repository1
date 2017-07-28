package controllers.dismember;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import constant.dismember.Constant;
import controllers.annotation.ALogin;
import entity.dismember.DisApply;
import entity.dismember.DisEmailVerify;
import entity.dismember.DisWithdrawAccount;
import entity.dismember.ShopSite;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.base.utils.JsonFormatUtils;
import services.dismember.IApplyService;
import services.dismember.IDisAccountService;
import services.dismember.ILoginService;
import services.dismember.IWAccountService;
import utils.dismember.DateUtils;
import utils.dismember.HttpUtil;
import utils.dismember.MD5Util;
import vo.dismember.LoginContext;

/**
 * 充值提现申请/交易记录
 * 
 * @author xuse
 *
 */
public class ApplyController extends Controller {

	@Inject
	private IApplyService applyService;

	@Inject
	private ILoginService loginService;

	@Inject
	private IWAccountService iwAccountService;

	@Inject
	private IDisAccountService disAccountService;
	
	/**
	 * 提交申请
	 * 
	 * @return
	 */
	public Result sendApply() {
		if (!loginService.isLogin(1) && !loginService.isLogin(2)) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}
		
		MultipartFormData formData = request().body().asMultipartFormData();
    	Map<String, String[]> params = formData.asFormUrlEncoded();
    	FilePart file = formData.getFile("image");
    	String email = "";
    	if (params.containsKey("isBackStage") && params.get("isBackStage")[0].equals("true")){
    		email = loginService.getLoginContext(2).getEmail();
    	} else {
    		email = loginService.getLoginContext(1).getEmail();
    	}
		return ok(Json.toJson(applyService.sendApply(file,params,email)));
	}
	
	/**
	 * 添加在线充值申请
	 * 
	 * @return
	 */
	public Result sendOnlineApply() {
		if (!loginService.isLogin(1)) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}
		
		Form<DisApply> form = Form.form(DisApply.class).bindFromRequest();
		DisApply apply = form.get();
		if (null == apply || apply.getTransferAmount() == null) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("code", 1);// 参数错误
			return ok(Json.toJson(result));
		}

		apply.setEmail(loginService.getLoginContext(1).getEmail());
		Logger.info("分销商【" + apply.getEmail() + "】使用【"
				+ apply.getTransferType() + "】在线充值，金额为："
				+ apply.getTransferAmount());
		return ok(Json.toJson(applyService.sendOnlineApply(apply)));
	}

	/**
	 * 在线充值成功回调
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result onlinePaySuccessCallback() {
		JsonNode jsonode = request().body().asJson();
		if (null == jsonode || jsonode.get("onlineApplyNo") == null
				|| jsonode.get("transferNumber") == null) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("code", 1);// 参数错误
			return ok(Json.toJson(result));
		}

		Logger.info("在线充值成功回调函数执行，在线充值单号:["
				+ jsonode.get("onlineApplyNo").textValue() + "],充值金额["
				+ jsonode.get("transferNumber").asDouble() + "]");

		// 额度更新
		DisApply apply = JsonFormatUtils.jsonToBean(jsonode.toString(),DisApply.class);
		// 状态更新
		return ok(Json.toJson(applyService.updateApply(apply, "")));
	}

	/**
	 * 修改申请----管理员审核
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result audit() {
		if (!loginService.isLogin(2)) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}
		JsonNode jsonode = request().body().asJson();
		if (null == jsonode || jsonode.get("id") == null) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("code", 1);// 参数错误
			return ok(Json.toJson(result));
		}
		
		DisApply apply = JsonFormatUtils.jsonToBean(jsonode.toString(), DisApply.class);
		return ok(Json.toJson(applyService.updateApply(apply, loginService.getLoginContext(2).getEmail())));
	}


	/**
	 * 查询我的申请记录
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result queryApply() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(1) && !loginService.isLogin(2)) {
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}
		
		LoginContext lc = loginService.getLoginContext(1);
		Map<String, Object> param = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		if (node.has("type")) {
			param.put("email", lc.getEmail());// 为空则是后台管理员登陆，查询所有申请
		}
		param.put("pageSize", utils.dismember.StringUtils.getIntegerParam(node, "pageSize", 10));
		param.put("currPage", utils.dismember.StringUtils.getIntegerParam(node, "currPage", 1));
		// 初审状态 ---0:待审核,1:审核不通过,2:审核通过,3:待确认
		param.put("auditState",utils.dismember.StringUtils.getIntegerParam(node, "auditState", null));
		// 复审状态 ---1:审核不通过,2:审核通过,3:待确认,4:待复审
		param.put("reviewState",utils.dismember.StringUtils.getIntegerParam(node, "reviewState", null));
		param.put("disState", utils.dismember.StringUtils.getIntegerParam(node, "disState", null));
		param.put("auditOrreview",utils.dismember.StringUtils.getStringParam(node, "reviewState", null));
		// 取分销商填写的转账时间
		if (utils.dismember.StringUtils.checkParam(node,"time")) {
			Calendar ca = Calendar.getInstance();
			ca.add(Calendar.MONTH, -utils.dismember.StringUtils.getIntegerParam(node, "time", null));
			param.put("time", DateUtils.date2FullDateTimeString(ca.getTime()));
		}
		param.put("receiptMode",utils.dismember.StringUtils.getIntegerParam(node, "receiptMode", null));
		param.put("search",utils.dismember.StringUtils.getStringParam(node, "search", null));
		
		if (utils.dismember.StringUtils.checkParam(node,"isOnline")) {
			param.put("isOnline", true);
		}
		
		param.put("applyType", utils.dismember.StringUtils.getStringParam(node, "applyType", null));
		param.put("onlineApplyNo", utils.dismember.StringUtils.getStringParam(node, "onlineApplyNo", null));
		param.put("sort",utils.dismember.StringUtils.getStringParam(node, "sidx", null));
		param.put("filter",utils.dismember.StringUtils.getStringParam(node, "sord", null));
		result.put("success", true);
		result.put("result", applyService.queryApply(param));
		return ok(Json.toJson(result));
	}

	/**
	 * 查询提现记录
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result queryWithdraw() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(1) && !loginService.isLogin(2)) {
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}
		JsonNode node = request().body().asJson();
		if (node == null) {
            result.put("success", false);
            result.put("msg", "请求参数为空");
            return ok(Json.toJson(result));
		}

		JSONObject params = JSON.parseObject(node.toString());
        LoginContext lc = loginService.getLoginContext(1);
        if(lc != null && node.has("type")) {
        	//若为前台用户登录，则只能查询自己提交的数据，将邮箱参数传进
        	params.put("distributorEmail", lc.getEmail());
        }
		result.put("success", true);
		result.put("result", applyService.getWithdrawRecord(params));
		return ok(Json.toJson(result));
	}

	/**
	 * 验证支付密码是否正确
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result checkPayPwd() {
		if (!loginService.isLogin(1) && !loginService.isLogin(2)) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}
		JsonNode node = request().body().asJson();
		if (!node.has("captcha") || StringUtils.isBlank(node.get("captcha").asText())) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		LoginContext lc = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_PERSONAL);
		return ok(disAccountService.checkPayPwd(node.get("captcha").asText(), lc, node.get("payCaptcha").asText()));
	}

	/**
	 * 提现申请
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result applyWithdraw() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(1) && !loginService.isLogin(2)) {
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}
		
		JsonNode node = request().body().asJson();
		if(node==null){
			result.put("suc", false);
			result.put("msg", "参数错误");
			Logger.debug(">>>applyWithdraw>>>result>>>"+result.toString());
			return ok(Json.toJson(result));
		}
		
		String msg = "";
		boolean suc = true;
		Integer insert = applyService.applyWithdraw(node.toString());
		switch (insert){
			case 1 : 
				msg = "提现申请已提交，通淘客服会尽快为您处理。<br>您可在提现申请列表里跟踪提现状态！<br>如有疑问，请联系客服";
				suc = true;
				break;
			case 2: 
				msg = "已超过当月可提现次数";
				suc = false;
				break;
			case 7: 
				msg = "单次提现不得低于最低限额";
				suc = false;
				break;
			case 3 : 
				msg = "参数错误";
				suc = false;
				break;
			case 5 : 
				msg = "正在处理中，请稍后...";
				suc = false;
				break;
			case 4 : 
				msg = "帐户异常或已被冻结";
				suc = false;
				break;
			case 6 : 
				msg = "余额不足";
				suc = false;
				break;
			case 8 : 
				msg = "提现的银行卡为无效卡，请选择有效的银行卡进行提现";
				suc = false;
				break;
			case 10 : 
				msg = "提现申请已提交，将提现至m站后台可结算金额。若有疑问，请联系客服。";
				suc = true;
				break;
			default: msg = "申请失败";
				suc = false;
				break;
		}
		result.put("suc", suc);
		result.put("msg", msg);
		Logger.debug(">>>applyWithdraw>>>result>>>"+result.toString());
		return ok(Json.toJson(result));
	}

	/**
	 * 校验提现帐号是否存在
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result existWithdrawNo() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(1) && !loginService.isLogin(2)) {
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}
		
		JsonNode param = request().body().asJson();
		if(param==null){
			result.put("suc", false);
			result.put("msg", "参数错误");
			Logger.debug(">>>existWithdrawNo>>>result>>>{}", result.toString());
			return ok(Json.toJson(result));
		}
		
		JSONObject map = iwAccountService.existWAccountNo(JSON.parseObject(param.toString()));
		Integer exist = map.getInteger("exist");
		String account = map.getString("account");
		String msg = "";
		boolean suc = true;
		switch (exist){
			case 5 : 
				msg = "正在处理中，请稍后...";
				suc = false;
				break;
			case 3 : 
				msg = "参数错误";
				suc = false;
				break;
			case 4 : 
				msg = "帐号已经绑定";
				suc = false;
				break;
			case 0 : 
				break;
			case 6 : 
				msg = "操作过于频繁，如果你依然未收到邮件，请于明日再进行操作！";
				suc = false;
				break;
			case 7 : 
				msg = "操作过于频繁，如果你依然未收到短信，请于明日再进行操作！";
				suc = false;
				break;
			case 9 : 
				msg = "9";
				break;
			case 8 : 
				msg = "8";
				break;
		}
		result.put("suc", suc);
		result.put("msg", msg);
		result.put("account", account);
		Logger.debug(">>>existWithdrawNo>>>result>>>"+result.toString());
		return ok(Json.toJson(result));
	}

	/**
	 * 添加提现帐号
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result addWithdrawNo() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(1) && !loginService.isLogin(2)) {
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}
		
		JsonNode node = request().body().asJson();
		if(node==null){
			result.put("suc", false);
			result.put("msg", "参数错误");
			Logger.debug(">>>addWithdrawNo>>>result>>>{}", result.toString());
			return ok(Json.toJson(result));
		}
		
		String msg = "";
		boolean suc = true;
		Integer count = iwAccountService.addWAccountNo(node.toString());
		switch (count){
			case 3 : msg = "参数错误";
				suc = false;
				break;
			case 4 : msg = "该帐号已经绑定";
				suc = false;
				break;
			case 5 : msg = "正在处理中，请稍后...";
				suc = false;
				break;
			case 6 : msg = "短信验证码错误";
				suc = false;
				break;
			case 7 : msg = "短信验证码已失效，请重新获取";
				suc = false;
				break;
			case 8 : msg = "短信验证码已失效，请重新获取";
				suc = false;
				break;
			case 1 : break;
			default: msg = "申请失败";
				suc = false;
				break;
		}
		result.put("suc", suc);
		result.put("msg", msg);
		Logger.debug(">>>addWithdrawNo>>>result>>>{}", result.toString());
		return ok(Json.toJson(result));
	}

	/**
	 * 提现激活邮件添加绑定帐号
	 * @return
     */
	public Result wbActiveEmail(){
		String param = request().getQueryString("data");
		Integer count = iwAccountService.activateWBEmail(param);
		return redirect(HttpUtil.getUrl() + "/personal/activ_email.html?msg="+count);
	}

	/**
	 * 余额支付接口
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result balancePayment() {
		if (!loginService.isLogin(1)) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("code", 1);// 未登录
			return ok(Json.toJson(result));
		}
		
		JsonNode jsonode = request().body().asJson();
		if (null == jsonode || !jsonode.has("transferAmount")
				|| !jsonode.has("transferNumber") || !jsonode.has("applyType")
				|| !jsonode.has("paycode")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("info", "参数缺失");// 参数错误
			result.put("code", 2);
			return ok(Json.toJson(result));
		}
		
		DisApply apply = JsonFormatUtils.jsonToBean(jsonode.toString(),
				DisApply.class);
		apply.setEmail(loginService.getLoginContext(1).getEmail());
		apply.setPassword(MD5Util.MD5Encode(apply.getPaycode(),
				MD5Util.CHARSET_UTF_8));
		return ok(Json.toJson(applyService._payment(apply)));
	}
	
	/**
	 * 后台现金支付接口，不需要支付密码
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result backStagePayment() {
		JsonNode jsonode = request().body().asJson();
		Logger.info("后台支付参数:[{}]",jsonode);
		if (null == jsonode || !jsonode.has("transferAmount")
				|| !jsonode.has("transferNumber") || !jsonode.has("applyType")
				|| !jsonode.has("isBackStage") || !jsonode.has("email")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("info", "参数缺失");// 参数错误
			result.put("code", 2);
			return ok(Json.toJson(result));
		}
		DisApply apply = JsonFormatUtils.jsonToBean(jsonode.toString(), DisApply.class);
		return ok(Json.toJson(applyService._payment(apply)));
	}

	/**
	 * 
	 * 运费退款接口
	 * 
	 * @author zbc
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result freightRefund() {
		JsonNode jsonode = request().body().asJson();
		if (!loginService.isLogin(1) &&
				(jsonode == null || !jsonode.has("isBackStage"))) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("code", 1);// 未登录
			return ok(Json.toJson(result));
		}
		if (null == jsonode || !jsonode.has("transferAmount")
				|| !jsonode.has("transferNumber") || !jsonode.has("applyType")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("info", "参数缺失");// 参数错误
			result.put("code", 2);
			return ok(Json.toJson(result));
		}
		
		DisApply apply = JsonFormatUtils.jsonToBean(jsonode.toString(), DisApply.class);
		if (apply.getIsBackStage() == null) {
			apply.setPassword(MD5Util.MD5Encode(apply.getPaycode(), MD5Util.CHARSET_UTF_8));
		}
		return ok(Json.toJson(applyService.freightRefund(apply)));
	}

	/**
	 * 查询申请操作记录
	 * 
	 * @param d
	 * @return
	 */
	public Result queryOperations(Integer d) {
		if (!loginService.isLogin(2)) {
			return redirect(HttpUtil.getHostUrl() + "/sysadmin");
		}
		
		return ok(Json.toJson(applyService.queryOperations(d)));
	}

	/**
	 * 批量审批初审状态为审核通过的申请
	 * 
	 * @return
	 */
	public Result batchAudit() {
		if (!loginService.isLogin(2)) {
			return redirect(HttpUtil.getHostUrl() + "/sysadmin");
		}
		
		JsonNode json = request().body().asJson();
		List<Integer> ids = new ArrayList<>();
		for (Iterator<JsonNode> it = json.get("list").iterator();it.hasNext();) {
			ids.add(it.next().asInt());
		}
		
		return ok(Json.toJson(applyService.batchAudit(ids, loginService.getLoginContext(2).getEmail())));
	}

	/**
	 * 申请开通移动客户端
	 * 
	 * @return
	 */
	public Result applyOpenMobile() {
		if (!loginService.isLogin(1)) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");// 未登录
			return ok(Json.toJson(result));
		}
		Form<ShopSite> form = Form.form(ShopSite.class).bindFromRequest();
		if (null == form) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		ShopSite shopSite = form.get();
		return ok(Json.toJson(applyService.applyOpenMobile(shopSite)));
	}

	/**
	 * 得到移动客户端相关信息
	 */
	public Result getMobileApplyInfo() {
		if (!loginService.isLogin(1)) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("code", "1");// 未登录
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params != null && !(params.size() > 0)) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("code", "2");
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(applyService.getMobileApplyInfo(params)));
	}

	/**
	 * 修改移动客户端的相关信息
	 */
	public Result changeMobileClient() {
		JsonNode json = request().body().asJson();
		ShopSite shopSite = JsonFormatUtils.jsonToBean(json.get("data")
				.toString(), ShopSite.class);
		Logger.info("changeMobileClient------------->{}", Json.toJson(json.get("data")).toString());
		if (null == shopSite) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(applyService.modifyMobileInfo(shopSite)));
	}
	
	public Result getMoneyFromMsite(){
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		return ok(Json.toJson(applyService.saveMoneyFromMsite(params)));
	}

    /**
     * 审核提现申请，保存审核结果。
     * @Author LSL on 2016-09-20 12:32:11
     */
    public Result auditWithdraw() {
        LoginContext lc = loginService.getLoginContext(2);
        if (lc == null) {
        	JSONObject result = new JSONObject();
        	result.put("suc", false);
            result.put("code", 2);
            result.put("msg", "当前用户未登录或登录超时");
            return ok(result.toString());
        }
        JsonNode node = request().body().asJson();
        if (node == null) {
        	JSONObject result = new JSONObject();
            result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(result.toString());
        }
        
        JSONObject params = JSON.parseObject(node.toString());
        params.put("operator", lc.getEmail());
        return ok(applyService.auditWithdraw(params));
    }
    
    /**
     * 获取绑定的银行卡列表
     * @return
     * @author huchuyin
     * @date 2016年9月22日 上午11:34:54
     */
    public Result getBindBankCardList() {
    	LoginContext lc = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_PERSONAL);
        if (lc == null) {
        	Map<String,Object> resultMap = Maps.newHashMap();
        	resultMap.put("suc", false);
        	resultMap.put("code", 2);
        	resultMap.put("msg", "当前用户未登录或登录超时");
            return ok(Json.toJson(resultMap).toString());
        }
        
        DisWithdrawAccount account = new DisWithdrawAccount();
        account.setDistributorEmail(lc.getEmail());
        account.setIsBind(Constant.FLAG_BIND_BANK);
        return ok(applyService.getBindBankCardList(account));
    }
    
    /**
     * 解除绑定银行卡
     * @return
     * @author huchuyin
     * @date 2016年9月22日 上午11:45:36
     */
    public Result delBindBankCard() {
    	LoginContext lc = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_PERSONAL);
        if (lc == null) {
        	Map<String,Object> resultMap = Maps.newHashMap();
        	resultMap.put("suc", false);
        	resultMap.put("code", 2);
        	resultMap.put("msg", "当前用户未登录或登录超时");
            return ok(Json.toJson(resultMap).toString());
        }
        
        JsonNode node = request().body().asJson();
        if(node.isNull() || !node.has("id")) {
        	Map<String,Object> resultMap = Maps.newHashMap();
        	resultMap.put("suc", false);
        	resultMap.put("msg", "参数错误");
        	return ok(Json.toJson(resultMap).toString());
        }
        
        return ok(applyService.delBindBankCard(node.get("id").asInt()));
    }

    /**
     * 保存提现限制
     * @Author LSL on 2016-09-22 16:36:08
     */
	public Result saveWLimit() {
        LoginContext lc = loginService.getLoginContext(2);
        if (lc == null) {
        	JSONObject result = new JSONObject();
        	result.put("suc", false);
            result.put("code", 2);
            result.put("msg", "当前用户未登录或登录超时");
            return ok(result.toString());
        }
        JsonNode node = request().body().asJson();
        if (node == null) {
        	JSONObject result = new JSONObject();
            result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(result.toString());
        }
        
        JSONObject params = JSON.parseObject(node.toString());
        params.put("operator", lc.getEmail());
        return ok(applyService.saveWLimit(params));
	}

    /**
     * 获取通用提现限制
     * @Author LSL on 2016-09-22 17:11:39
     */
    public Result getCommonWLimit() {
        if (!loginService.isLogin(1) && !loginService.isLogin(2)) {
        	JSONObject result = new JSONObject();
        	result.put("suc", false);
            result.put("code", 2);
            result.put("msg", "当前用户未登录或登录超时");
            return ok(result.toString());
        }
        
        return ok(applyService.getCommonWLimit());
    }
    
    /**
     * 重新发送邮件
     * @return
     * @author huchuyin
     * @date 2016年9月24日 上午11:09:28
     */
    public Result reSendMail() {
    	LoginContext lc = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_PERSONAL);
        if (lc == null) {
        	Map<String,Object> resultMap = Maps.newHashMap();
        	resultMap.put("suc", false);
        	resultMap.put("code", 2);
        	resultMap.put("msg", "当前用户未登录或登录超时");
            return ok(Json.toJson(resultMap).toString());
        }
        
        DisEmailVerify emailVerify = new DisEmailVerify();
        emailVerify.setCemail(lc.getEmail());
        emailVerify.setSendType(3);
        return ok(iwAccountService.reSendEmail(emailVerify));
    }

	/**
	 * 绑定银行帐户重发短信
	 * @return
     */
	public Result reSendTelFor(){
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(1) && !loginService.isLogin(2)) {
			result.put("suc", false);
			result.put("msg", "2");// 未登录
			return ok(Json.toJson(result));
		}
		
		JsonNode node = request().body().asJson();
		boolean parametersCheckResult = node.has("types") && node.has("withdrawAccount");
		if(!parametersCheckResult) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		String msg = "";
		boolean suc = true;
		try {
			String withdrawAccount = node.get("withdrawAccount").asText();
			int types = node.get("types").asInt();
			int count = iwAccountService.sendTelCode(withdrawAccount,types);
			switch (count) {
			case 7:
				suc = false;
				msg = "操作过于频繁，如果你依然未收到短信，请于明日再进行操作！";
				break;
			}
		}catch (Exception e){
			e.printStackTrace();
			suc = false;
			msg = "正在处理中，请稍后...";
		}
		result.put("suc", suc);
		result.put("msg", msg);
		return ok(Json.toJson(result));
	}

	/**
	 * 新增审核通过的线下充值申请记录
	 * @Author LSL on 2016-10-21 14:55:22
	 */
	public Result addOfflineApply() {
		JsonNode node = request().body().asJson();
		if(node == null || node.size() == 0) {
			ObjectNode result = Json.newObject();
			result.put("suc", false);
			result.put("msg", "请求参数为空或不存在");
			return ok(result);
		}
		
		return ok(applyService.addOfflineApply(node.toString()));
	}
	
	public Result screenUrl(Integer id){
		if (id == null) {
			ObjectNode result = Json.newObject();
			result.put("suc", false);
			result.put("msg", "传入参数有误");
			return ok(Json.toJson(result));
		}
		
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		File file = applyService.getApplyFile(id);
		if(file == null || file != null &&!file.exists()){
			return ok(Json.toJson(""));
		}

		try {
			response().setHeader("Content-disposition", "attachment;filename=" + new String(file.getName().getBytes(), "ISO8859-1"));
		} catch (Exception e) {
			Logger.info(e.getMessage());
		}
		return ok(file);
	}
	
	/**
	 * 校验交易流水号是否重复
	 * @author zbc
	 * @since 2017年1月16日 上午10:02:58
	 */
	public Result checktranNo(String tno){
		return ok(applyService.checktranNo(tno));
	}
	
	
	/**
	 * 后台欢迎页面展示待初审，待复审，提现总数
	 * @return
	 */
	@ALogin
	public Result backstageWelcome() {
		String email = loginService.getLoginContext(2).getEmail();
		return ok(Json.toJson(applyService.backstageWelcome(email)));
	}
    
}
