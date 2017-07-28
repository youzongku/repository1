package controllers.dismember;    

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import constant.dismember.Constant;
import controllers.annotation.ALogin;
import dto.dismember.MemberForm;
import dto.dismember.ResultDto;
import dto.dismember.ReturnMess;
import dto.dismember.result.AdminRecordResult;
import entity.dismember.DisMember;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Result;
import services.base.utils.JsonFormatUtils;
import services.base.utils.MetaUtils;
import services.dismember.ICaptchaService;
import services.dismember.IDisMemberService;
import services.dismember.ILoginService;
import session.ISessionService;
import utils.dismember.HttpUtil;
import utils.dismember.JsonCaseUtil;
import vo.dismember.LoginContext;

/**
 * 
 * 用户Controller
 * @author xuse
 *
 */
@Api(value="/用户模块",description="user module")
public class MemberController extends Controller {

	@Inject
	private IDisMemberService memberService;   
	@Inject
	private ILoginService loginService;
	@Inject
	private ICaptchaService captchaService;
	@Inject
	private ISessionService sessionService;
	
	/**
	 * 根据email查询分销商
	 * @return
	 */
	public Result getDisMemberByEmail(){
		JsonNode jsonNode = request().body().asJson();
		String disEmail = jsonNode.get("disEmail").asText();
		HashMap<String,Object> map = Maps.newHashMap();

		DisMember dismember = memberService.getDismember(disEmail);
		if(dismember==null){
			map.put("suc", false);
			map.put("result", "不存在此分销商");
			return ok(Json.toJson(map));
		}
		
		map.put("suc", true);
		map.put("result", dismember);
		return ok(Json.toJson(map));
	}

	/**
	 * 会员邮箱验证激活
	 *
	 * @return
	 */
	public Result activedEmail(String email, String code) {
		MetaUtils.currentMetaBuilder().setTitle("TOMTOP Supply Chain");
		String host = request().host();
		Map<String, String> result = memberService.activedEmail(email, code, host);
		if(result.get("errorCode").equals("0")){
			return redirect(HttpUtil.getUrl() + "/personal/personal.html?d="+code);
		}

		return ok(Json.toJson(result));
	}

	/**
	 * 重发激活邮件
	 *
	 * @return
	 */
	public Result sendEmailAgain(String email) {
		return ok(memberService.sendEmailAgain(email));
	}

	/**
	 * 发送短信验证码
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result sendMessageForTel() {
		JsonNode json = request().body().asJson();
		Cookie coo = request().cookie("client");
		String sign = coo == null ? "" : coo.value();
		boolean val = memberService.checkSign(sign);
		if(!val) {
			return ok("false");
		}
		
		String result = memberService.sendMessageForTel(json.toString());
		return ok(Json.parse(result));
	}
	
	/**
	 * 接口调用key,
	 * 加密算法：有效期（年月日时分秒使用BBC隔开）
	 * @return
	 */
	public Result clientCode() {
		String code = memberService.getClientCode();
		response().setCookie("client", code);
		return ok("");
	}
	
	/**
	 * 校验短信验证码是否正确
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result checkMessageCaptcha() {
		String captchaTarget = request().body().asJson().path("captcha").asText();
		String captchaSource = (String) sessionService.get("SMS_CAPTCHA_NAME");
		Logger.info("captchaSource-------->{}", Json.toJson(captchaSource));
		Logger.info("captchaTarget-------->{}", Json.toJson(captchaTarget));
		
		boolean result = captchaTarget != null
				&& captchaSource != null
				&& captchaTarget.replaceAll(" ", "").equals(
						captchaSource.replaceAll(" ", ""));
		return ok(Json.toJson(result));
	}

	/**
	 * 检验用户名是否可用
	 * @return
	 */
	public Result verifyUser() {
		Map<String, String> result = Maps.newHashMap();
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("user")) {
			Logger.debug("请求参数不存在");
			result.put("success", "false");
			result.put("message", "请求参数不存在");
			return ok(Json.toJson(result));
		}
		if (!memberService.verifyUser(params.get("user"))) {
			result.put("success", "true");
			result.put("message", "当前用户名可用");
			return ok(Json.toJson(result));	
		}
		
		result.put("success", "false");
		result.put("message", "当前用户名已存在");
		return ok(Json.toJson(result));
	}

	/**
	 * 获取当前登录用户的资料信息
	 *
	 * @return
	 */
	public Result getInformation(String email) {
		if(StringUtils.isNotEmpty(email)){
			return ok(Json.toJson(memberService.getInfo(email)));
		}
		if (!loginService.isLogin(1)) {
			return ok(Json.toJson(new ReturnMess("2", "当前用户未登录")));
		}
		
		return ok(Json.toJson(memberService.getInfo(loginService.getLoginContext(1).getEmail())));
	}

	/**
	 * 更新用户信息
	 *
	 * @return
	 */
	public Result updateInfo() {
		sessionService.remove("SMS_CAPTCHA_NAME");
		if (!loginService.isLogin(1)) {
			ReturnMess returnMess = new ReturnMess("2", "当前用户未登录");
			return ok(Json.toJson(returnMess));
		}
		Form<MemberForm> updateMember = Form.form(MemberForm.class).bindFromRequest();
		if (updateMember.hasErrors()) {
			return ok("error");
		}
		
		MemberForm form = updateMember.get();
		if (form.getMonth() != null && form.getDay() != null && form.getYear() != null) {
			String month = Integer.valueOf(form.getMonth()) < 10 ? "0" + form.getMonth() : form.getMonth();
			String day = Integer.valueOf(form.getDay()) < 10 ? "0" + form.getDay() : form.getDay();
			String date = form.getYear() + "-" + month + "-" + day;
			Logger.debug(">>updateInfo>>>>>date>>>{}", date);
			form.setBirthday(date);
		}
		Logger.debug(">>updateInfo>>>>>form>>>{}", Json.toJson(form).toString());
		return ok(memberService.updateInfo(form));
	}

	/**
	 * 获取所有已激活的前台或后台用户
	 * 注：必须指定角色
	 * @return
	 */
	public Result getUsers() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if(null == params || !params.containsKey("role")){
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		Logger.info("参数为：{}",params);
		
		Integer currPage = params.containsKey("currPage") ? Integer.valueOf(params.get("currPage")) : 1;
		Integer pageSize = params.containsKey("pageSize") ? Integer.valueOf(params.get("pageSize")) : 10;
		String sregdate = params.containsKey("sregdate") ? params.get("sregdate") : "";
		String eregdate = params.containsKey("eregdate") ? params.get("eregdate") : "";
		String slogdate = params.containsKey("slogdate") ? params.get("slogdate") : "";
		String elogdate = params.containsKey("elogdate") ? params.get("elogdate") : "";
		Integer isPackageMail = params.containsKey("isPackageMail") ? Integer.valueOf(params.get("isPackageMail")) : 0;
		String userCode = params.containsKey("userCode") ? params.get("userCode") : "";

		eregdate = Strings.isNullOrEmpty(eregdate) ? eregdate : new DateTime(eregdate).plusDays(1).toString("yyyy-MM-dd");
		elogdate = Strings.isNullOrEmpty(elogdate) ? elogdate : new DateTime(elogdate).plusDays(1).toString("yyyy-MM-dd");
		Map<String, Object> map = Maps.newHashMap();
		map.put("currPage", currPage);
		map.put("pageSize", pageSize);
		map.put("startNum", (currPage - 1) * pageSize);
		map.put("roleId", Integer.valueOf(params.get("role")));
		map.put("rankId", params.containsKey("rank") && !Strings.isNullOrEmpty(params.get("rank")) ? Integer.valueOf(params.get("rank")) : null);
		map.put("search", params.containsKey("search") ? params.get("search") : "");
		map.put("sregdate", sregdate);
		map.put("eregdate", eregdate);
		map.put("slogdate", slogdate);
		map.put("elogdate", elogdate);
		map.put("workNo", params.get("workNo"));//后台用户工号或者姓名
		map.put("sort", params.get("sort"));
		map.put("filter", params.get("filter"));
		map.put("isPackageMail", isPackageMail);
		map.put("userCode", userCode);
		Integer comsumerType = 0;//分销商类型
		if(params.containsKey("comsumerType")){
			String comsumerTypeStr = utils.dismember.StringUtils.getString(params.get("comsumerType"), false);
			comsumerType = "".equals(comsumerTypeStr)?0:Integer.valueOf(comsumerTypeStr);
		}
		map.put("comsumerType", comsumerType);
		Integer distributionMode = 0;//分销商类型
		if(params.containsKey("distributionMode")){
			String distributionModeStr = utils.dismember.StringUtils.getString(params.get("distributionMode"), false);
			distributionMode = "".equals(distributionModeStr)?0:Integer.valueOf(distributionModeStr);
		}
		map.put("distributionMode",distributionMode);
		//增加创建人维度查询，且针对后台用户，需添加标识查询 by huchuyin 2016-9-13
		map.put("fromFlag", params.get("fromFlag"));
		//获取登录信息
		LoginContext lc = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK);
		map.put("createUser",lc.getEmail());
		//获取用户信息
		DisMember disMember = memberService.getMemberById(Integer.valueOf(lc.getUserID()));
		//设置当前用户角色ID
		map.put("curRoleId",disMember.getRoleId());
		Logger.info("createUser，fromFlag，curRoleId-------------->"+map.get("createUser")+","+map.get("fromFlag")+","+map.get("curRoleId"));
		//End by huchuyin 2016-9-13
		result.put("suc", true);
		result.put("page", memberService.getAdminsByPage(map));
		return ok(Json.toJson(result));
	}
	
	/**
	 * 根据code获取邮箱验证成功记录
	 * @return
	 */
	public Result getEmailVerifySuccessRecord(){
		Map<String, String> params = Form.form().bindFromRequest().data();
		if(null == params || StringUtils.isEmpty(params.get("d"))){
			return ok("");
		}
		
		return ok(Json.toJson(memberService.getEmailVerifySuccessRecord(params)));
	}

	/**
	 * 获取管理员用户登录历史信息
	 * @return
	 */
	public Result getAdminLoginHistory() {
		Map<String, Object> result = Maps.newHashMap();
		LoginContext lc = loginService.getLoginContext(2);
		if (lc == null) {
			Logger.info("当前用户未登录");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		
		result.put("suc", true);
		result.put("info", memberService.getAdminLoginHistory(lc.getEmail()));
		return ok(Json.toJson(result));
	}

	/**
	 * 为修改分销商支付密码而发送邮件
	 * @return
	 */
	public Result changePayPassword(){
		if (!loginService.isLogin(1)) {
			Logger.info("当前用户未登录或登录超时");
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "当前用户未登录或登录超时");
			return ok(Json.toJson(result));
		}
		
		Map<String, String> params = Form.form().bindFromRequest().data();
		Logger.info("applyModifyCellphone params-->{}", params);
		if (params == null || !params.containsKey("email") || !params.containsKey("captcha")) {
			Logger.info("请求参数不存在或格式错误");
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		if (!captchaService.verify(params.get("captcha"))) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "验证码输入错误");
			ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(memberService.changePayPasswordByEmail(params)));
	}

	/**
	 * 重新发送修改支付密码认证邮件
	 * @return
	 */
	public Result rsendEmailForchangePaypassword(){
		if (!loginService.isLogin(1)) {
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", false);
			result.put("msg", "当前用户未登录或登录超时");
			return ok(Json.toJson(result));
		}
		
		Map<String, String> params = Form.form().bindFromRequest().data();
		Logger.info("applyModifyCellphone params-->" + params);
		if (params == null || !params.containsKey("email")) {
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(memberService.changePayPasswordByEmail(params)));
	}
	
	/**
	 * 检查支付密码修改邮件url有效 时期
	 * @return
	 */
	public Result checkPaypasswordEmailHours(){
		if (!loginService.isLogin(1)) {
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", 0);
			result.put("msg", "当前用户未登录或登录超时");
			return ok(Json.toJson(result));
		}
		
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("email") || !params.containsKey("code")) {
			Logger.info("checkPaypasswordEmailHours params-->" + params);
			Logger.info("请求参数不存在或格式错误");
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", 1);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(memberService.checkedPayPwdEmailHours(params)));
	}

	/**
	 * 申请修改手机号
	 * @return
	 */
	public Result applyModifyCellphone() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(1)) {
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("email") || !params.containsKey("captcha")) {
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		Logger.info("applyModifyCellphone params-->" + params.toString());
		String captcha = params.get("captcha");
		if (!captchaService.verify(captcha)) {
			Logger.info("验证码输入错误");
			result.put("suc", false);
			result.put("msg", "验证码输入错误");
			return ok(Json.toJson(result));
		}
		
		String email = params.get("email");
		if (!email.equals(loginService.getLoginContext(1).getEmail())) {
			Logger.info("输入的邮箱与当前登录用户不匹配");
			result.put("suc", false);
			result.put("msg", "输入的邮箱与当前登录用户不匹配");
			return ok(Json.toJson(result));
		}
		
		boolean flag = memberService.sendModifyCellphoneVerifyEmail(email);
		if (!flag) {
			result.put("suc", false);
			result.put("msg", "发送邮件失败，请稍后重试");
			return ok(Json.toJson(result));
		}
		
		result.put("suc", true);
		result.put("email", email);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 手机用户申请修改手机号码
	 * @return
	 */
	public Result applyModifyCellphone2() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(1)) {
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("email") || !params.containsKey("captcha")) {
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		Logger.info("applyModifyCellphone params-->" + params.toString());
		String captcha = params.get("captcha");
		if (!captchaService.verify(captcha)) {
			Logger.info("验证码输入错误");
			result.put("suc", false);
			result.put("msg", "验证码输入错误");
			return ok(Json.toJson(result));
		}
		
		String email = params.get("email");
		if (!email.equals(loginService.getLoginContext(1).getEmail())) {
			Logger.info("输入的邮箱与当前登录用户不匹配");
			result.put("suc", false);
			result.put("msg", "输入的邮箱与当前登录用户不匹配");
			return ok(Json.toJson(result));
		}
		
		result.put("suc", true);
		result.put("email", email);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 重新发送修改手机验证邮件
	 * @return
	 */
	public Result repeatSendModifyCellphoneVerifyEmail() {
		ObjectNode result = JsonNodeFactory.instance.objectNode();
		if (!loginService.isLogin(1)) {
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", false);
			result.put("code", "2");
			return ok(result);
		}

		JsonNode params = request().body().asJson();
		if (params.isNull() || !params.has("email")) {
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(result);
		}
		
		Logger.info("repeatSendModifyCellphoneVerifyEmail params-->" + params.toString());
		boolean flag = memberService.sendModifyCellphoneVerifyEmail(params.get("email").asText());
		if (!flag) {
			result.put("suc", false);
			result.put("msg", "重发邮件失败，请稍后重试");
			return ok(result);
		}
		
		result.put("suc", true);
		return ok(result);
	}
	
	/**
	 * 修改手机 验证手机验证码
	 * 
	 */
	public Result checkModifyCode(){
		Map<String, Object> result = Maps.newHashMap();
		Map<String, String> params = Form.form().bindFromRequest().data();
		if( !params.containsKey("smsc")
				|| !params.containsKey("cell")){
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		//修改验证码方式 by huchuyin 2016-10-10
		String smsc = params.get("smsc");
		String cell = params.get("cell");
		String resultStr = memberService.checkSmsCode(smsc, cell, Constant.SMS_TYPE_SET_PAY_PWD);
		Logger.info(this.getClass().getName() + " checkModifyCode resultStr" + resultStr);
		return ok(Json.parse(resultStr));
	}	

	/**
	 * 修改重置手机号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Result resetCellphone() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(Constant.LOGIN_FROM_MARK_PERSONAL)) {
			Logger.info("当前用户未登录");
			result.put("suc", false);
			result.put("msg", "当前用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		// captcha 图片验证码，code 邮箱验证码，cell 新手机号，smsc 短信验证码，email 当前用户邮箱
		if (params == null || !params.containsKey("cell") || !params.containsKey("smsc")
				|| !params.containsKey("captcha") || !params.containsKey("email")) {
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		//修改邮箱不可由前台传入，修改为从登录信息获取 by huchuyin 2016-10-11
		//String email = params.get("email");
		LoginContext lc = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_PERSONAL);
		String email = lc.getEmail();
		params.put("email", email);
		boolean emailFlag = email.contains("@");
		Logger.info("resetCellphone params-->" + params.toString());
		// 校验图片验证码
		String captcha = params.get("captcha");
		if (captchaService.verify(captcha)) {
			// 校验邮箱验证码
			if (emailFlag) {
				if (params.containsKey("code")) {
					String code = params.get("code");
					if (memberService.verifyEmailCode(email, code)) {
						// 校验短信验证码
						String smsc = params.get("smsc");
						//String sms = (String) sessionService.get("SMS_CAPTCHA_NAME", Context.current());
						Logger.info("smsc-------------->" + smsc);
						//Logger.info("sms---------------->" + sms);
						//校验手机号码与短信验证码
						String cell = params.get("cell");
						String resultStr = memberService.checkSmsCode(smsc, cell, Constant.SMS_TYPE_CHANGE_PHONE);
						Map<String,Object> checkMap = Json.fromJson(Json.parse(resultStr),Map.class);
						if ((Boolean) checkMap.get("suc")) {
							// 所有校验通过，更新手机号
							//String cell = params.get("cell");
							Map<String, String> user = memberService.getInfo(email);
							MemberForm form = new MemberForm();
							form.setId(Integer.valueOf(user.get("id")));
							form.setTelphone(cell);
							JsonNode node = memberService.updateInfo(form);
							if ("0".equals(node.get("errorCode").asText())) {
								result.put("suc", true);
							} else {
								Logger.info("保存新手机号失败");
								result.put("suc", false);
								result.put("msg", "保存新手机号失败");
							}
						} else {
							Logger.info("短信验证码输入错误");
							result.put("suc", false);
							result.put("msg", (String)checkMap.get("msg"));
						}
					} else {
						Logger.info("邮箱验证码输入错误");
						result.put("suc", false);
						result.put("msg", "邮箱验证码输入错误");
					}
				} else {
					Logger.info("请求参数不存在或格式错误");
					result.put("suc", false);
					result.put("msg", "请求参数不存在或格式错误");
				}
			} else {
				// 校验短信验证码
				String smsc = params.get("smsc");
				//String sms = (String) sessionService.get("SMS_CAPTCHA_NAME", Context.current());
				Logger.info("smsc-------------->" + smsc);
				//Logger.info("sms---------------->" + sms);
				String cell = params.get("cell");
				String resultStr = memberService.checkSmsCode(smsc, cell, Constant.SMS_TYPE_CHANGE_PHONE);
				Map<String,Object> checkMap = Json.fromJson(Json.parse(resultStr),Map.class);
				if ((Boolean)checkMap.get("suc")) {
					// 所有校验通过，更新手机号
					//String cell = params.get("cell");
					Map<String, String> user = memberService.getInfo(email);
					MemberForm form = new MemberForm();
					form.setId(Integer.valueOf(user.get("id")));
					form.setTelphone(cell);
					JsonNode node = memberService.updateInfo(form);
					if ("0".equals(node.get("errorCode").asText())) {
						result.put("suc", true);
					} else {
						Logger.info("保存新手机号失败");
						result.put("suc", false);
						result.put("msg", "保存新手机号失败");
					}
				}else{
					Logger.info("短信验证码输入错误");
					result.put("suc", false);
					result.put("msg", (String)checkMap.get("msg"));
				}
			}
		} else {
			Logger.info("图片验证码输入错误");
			result.put("suc", false);
			result.put("msg", "图片验证码输入错误");
		}
		return ok(Json.toJson(result));
	}

	/**
	 * 获取指定用户的资料信息
	 * @return
	 */
	public Result gainInfoByEmail() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			Logger.info("当前用户未登录");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		
		JsonNode jsonParam = request().body().asJson();
		if (jsonParam == null || jsonParam.get("email") == null || Strings.isNullOrEmpty(jsonParam.get("email").asText())) {
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(memberService.getCreditInfo(jsonParam.get("email").asText())));
	}

	/**
	 * 内部使用，更新后台用户密码
	 * 
	 * @return
	 */
	public Result resetCipher(Integer userId) {
		if (!loginService.isLogin(2)) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("msg", "用户未登陆");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(memberService.updateCipher(loginService.getLoginContext(2).getEmail(), userId)));
	}

	/**
	 * 添加后台用户
	 * 
	 * @return
	 */
	public Result addBackstageUser() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			result.put("success", false);
			result.put("code", "2");
			result.put("msg", "用户未登陆");
			return ok(Json.toJson(result));
		}
		
		Logger.info(this.getClass().getName()+" addBackstageUser user==="+loginService.getLoginContext(2).getEmail());
		JsonNode node = request().body().asJson();
		Logger.info(this.getClass().getName()+" addBackstageUser node==="+node.toString());
		DisMember member = JsonFormatUtils.jsonToBean(node.toString(), DisMember.class);
		if (null == member) {
			result.put("success", false);
			result.put("msg", "请填写完整参数。");// 参数错误
			return ok(Json.toJson(result));
		}
		
		member.setIfAddPermision("1".equals(member.getAddUserGetMenu()));
		return ok(Json.toJson(memberService.insertUser(member, "user", loginService.getLoginContext(2).getEmail())));
	}

	/**
	 * 删除后台用户
	 * 
	 * @return
	 */
	public Result deleteBackstageUser(Integer id) {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			result.put("success", false);
			result.put("code", "2");
			result.put("msg", "用户未登陆");
			return ok(Json.toJson(result));
		}
		
		Logger.info(this.getClass().getName() + " deleteBackstageUser user===" + loginService.getLoginContext(2).getEmail());
		return ok(Json.toJson(memberService.deleteUser(id)));
	}


	/**
	 * 更改密码
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Result resetPwd() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			Logger.info("当前用户未登录");
			result.put("success", false);
			return ok(Json.toJson(result));
		}
		JsonNode json = request().body().asJson();
		Map<String, String>  params = JsonFormatUtils.jsonToBean(json.toString(), Map.class);
		if (params == null) {
			result.put("success", true);
			result.put("code", 1);// 缺失参数
			return ok(Json.toJson(result));
		}
		
		params.put("email", loginService.getLoginContext(2).getEmail());
		return ok(Json.toJson(memberService.resetPwd(params)));
	}
	/**
	 * 描述：后台修改分销商信息
	 * 2016年3月18日
	 * @return
	 */
	@ApiOperation(value="后台修改分销商类型",httpMethod="POST",notes="<b>该接口有后台登录校验</b><br>修改原来接口返回值",response=ResultDto.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="param",dataType="string",paramType="body",required=true,
				value= "\"id\":用户id，必传<br/>"
					   +"\"comsumerType\":分销商类型，必传<br/>"
					   +"\"distributionMode\":分销商渠道， 必传<br/>"
					   +"\"attributionType\":用户归属，必传<br/>",
				defaultValue="{\n"
						+ "\"id\":627,\n"
						+ "\"comsumerType\":2,\n"
						+ "\"distributionMode\":1,\n"
						+ "\"attributionType\":1\n"
						+ "}")
	})
	@ALogin
	public Result updateComsumerInfoInBack() {
		JsonNode node = request().body().asJson();
		Logger.info("修改分销商类型参数:{}",node);
		if(!JsonCaseUtil.checkParam(node, "id","comsumerType","distributionMode","attributionType")){
			return ok(Json.toJson(new ResultDto<>(false, "参数错误")));
		}
		return ok(Json.toJson(memberService.updateComsumerInfo(node.toString(),loginService.getLoginContext(2).getEmail())));
	}

	/**
	 * 获取注册时的客服人员《根据邀请码》
	 * @return
	 */
	public Result getCustAccount(String email){
		if (!loginService.isLogin(1) && StringUtils.isEmpty(email)) {
			return redirect(HttpUtil.getUrl() + "/personal/login.html");
		}
		
		if(loginService.getLoginContext(1) != null && StringUtils.isEmpty(email)) {
			email = loginService.getLoginContext(1).getEmail();
		}
		return ok(Json.toJson(memberService.getCustomerServiceAccount(email)));
	}

	/**
	 * 获取分销商模式
	 * @author zbc
	 * @since 2016年8月19日 上午9:47:58
	 */
	public Result getMode(){
		return ok(Json.toJson(memberService.getMode()));
	}
	/**
	 * 禁用后台用户
	 * @author zbc
	 * @since 2017年4月28日 上午11:45:21
	 * @param
	 */
	@ApiOperation(value="禁/启用后台用户",httpMethod="GET",response=ResultDto.class,
			notes="<b>该接口有登录校验</b><br/>isDisabled:是否被禁用<br/>true:禁用状态，可进行启用操作,false:启用状态,可进行禁用操作")
	@ApiImplicitParams({ 
			@ApiImplicitParam(name="em",value="后台账号",dataType="string",paramType="path",required=true,defaultValue="test") 
	})
	@ALogin
	public Result adminDisable(String email){
		return ok(Json.toJson(memberService.disableUser(email, loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK).getEmail())));
	}
	
	/**
	 * 后台用户修改日志接口
	 * @author zbc
	 * @since 2017年5月2日 上午9:34:13
	 */
	@ApiOperation(value="查看后台用户操作日志",httpMethod="GET",response=AdminRecordResult.class,
			notes="")
	@ApiImplicitParams({ 
			@ApiImplicitParam(name="id",value="后台账号id",dataType="integer",paramType="path",required=true,defaultValue="281") 
	})
	public Result adminRecord(Integer id){
		return ok(Json.toJson(memberService.adminRecord(id)));
	}

	/**
	 * 设置分销商客户编码
	 *
	 * {
	 *	 "id":2,
	 *	 "provinceCode":1,
	 *	 "cityCode": 2,
	 *	 "areaCode":3
	 * }
	 * @return
	 */
	@ApiOperation(value="设置客户编码",httpMethod="POST",notes="设置客户编码，带*为必填" ,produces="application/json")
	@ApiImplicitParams({@ApiImplicitParam(name="body",
			required=true,value="可用参数："
			+ "\nid:*id，\n"
			+ "\nprovinceCode:*省编码，\n"
			+ "\ncityCode:*市编码，\n"
			+ "\nareaCode:*区编码\n",
			paramType="body",dataType="application/json",
			defaultValue = "{\n\"id\":\"1\",\n\"provinceCode\":\"1\",\n\"cityCode\":\"2\",\n\"areaCode\":\"3\"\n}")})
	public Result setMemberUserCode(){
		JsonNode jsonNode = request().body().asJson();
		HashMap<String,Object> result = Maps.newHashMap();

		if (!loginService.isLogin(2)) {
			result.put("success", false);
			result.put("code", "2");
			result.put("msg", "用户未登陆");
			return ok(Json.toJson(result));
		}

		if (null == jsonNode || jsonNode.size() == 0) {
			return internalServerError("Expecting Json data");
		}

		if (!jsonNode.has("id") || !jsonNode.has("provinceCode") || !jsonNode.has("cityCode") || !jsonNode.has("areaCode")) {
			result.put("suc", false);
			result.put("result", "参数错误!");
			return ok(Json.toJson(result));
		}

		return ok(Json.toJson(memberService.setMemberUserCode(jsonNode, loginService.getLoginContext(2).getEmail())));
	}

	@ApiOperation(value="单个设置运费",httpMethod="POST",notes="单个设置运费，带*为必填" ,produces="application/json")
	@ApiImplicitParams({@ApiImplicitParam(name="body",
			required=true,value="可用参数："
			+ "\nid:*用户id，\n"
			+ "\nisPackageMail:*是否包邮 1:包邮 2.不包邮，\n"
			+ "\nremark:备注",
			paramType="body",dataType="application/json",
			defaultValue = "{\n\"id\":1,\n\"isPackageMail\":1,\n\"remark\":\"remark\" \n}")})
	public Result setIsPackageMail() {

		if (!loginService.isLogin(2)) {
			Map<String, Object> result = new HashMap<>();
			Logger.info("当前用户未登录");
			result.put("suc", false);
			result.put("msg", "当前用户未登录");
			return ok(Json.toJson(result));
		}

		JsonNode json = request().body().asJson();
		Logger.info("单个设置是否包邮----{}", json);

		if (json == null || !json.has("id") || !json.has("isPackageMail")) {
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("result", false);
			resultMap.put("msg", "参数错误");
			return ok(Json.toJson(resultMap));
		}

		return ok(Json.toJson(memberService.setIsPackageMail(json, loginService.getLoginContext(2).getEmail())));
	}

	@ApiOperation(value="批量设置运费",httpMethod="POST",notes="批量设置运费，带*为必填" ,produces="application/json")
	@ApiImplicitParams({@ApiImplicitParam(name="body",
			required=true,value="可用参数："
			+ "\nmemberIdList:*用户id列表，\n"
			+ "\nisPackageMail:*是否包邮 1:包邮 2.不包邮，\n"
			+ "\nremark:备注",
			paramType="body",dataType="application/json",
			defaultValue = "{\n\"memberIdList\":[1,2],\n\"isPackageMail\":1,\n\"remark\":\"remark\" \n}")})
	public Result batchSetIsPackageMail() {

		if (!loginService.isLogin(2)) {
			Map<String, Object> result = new HashMap<>();
			Logger.info("当前用户未登录");
			result.put("suc", false);
			result.put("msg", "当前用户未登录");
			return ok(Json.toJson(result));
		}

		Map<String, Object> res = Maps.newHashMap();
		JsonNode jsonNode = request().body().asJson();
		Logger.info("批量设置运费参数:{}", jsonNode);
		if (jsonNode == null || !jsonNode.has("memberIdList") || !jsonNode.has("isPackageMail")) {
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res));
		}

		return ok(Json.toJson(memberService.batchSetIsPackageMail(jsonNode, loginService.getLoginContext(2).getEmail())));
	}
}
