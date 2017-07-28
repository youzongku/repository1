package controllers.dismember;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import constant.dismember.Constant;
import controllers.annotation.ALogin;
import dto.dismember.ResultDto;
import entity.dismember.DisMember;
import filters.common.CookieTrackingFilter;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.dismember.ICaptchaService;
import services.dismember.IDisMemberService;
import services.dismember.IFindPasswordByEmailService;
import services.dismember.ILoginService;
import session.ISessionService;
import utils.dismember.AESOperator;
import utils.dismember.JsonCaseUtil;
import utils.dismember.MD5Util;
import vo.dismember.LoginContext;
import vo.dismember.LoginContextFactory;

/**
 * Created by LSL on 2015/12/24.
 */
@Api(value = "/用户登陆", description = "login")
public class LoginController extends Controller {

    @Inject   
    private IDisMemberService disMemberService;
    @Inject
    private ILoginService loginService;
    @Inject
    private ISessionService sessionService;
    @Inject
    private ICaptchaService captchaService;
    @Inject
    private IFindPasswordByEmailService  findPasswordByEmailService;

    /**
     * 普通用户(前台用户)注册
     *
     * @return
     */
    public Result register() {
        Map<String, String[]> node = request().body().asFormUrlEncoded();
        Logger.debug("register params-->" + node.toString());
        return ok(disMemberService.register(node));
    }
    
    /**
     * 普通用户(前台用户)手机注册
     *
     * @return
     */
    public Result telRegister() {
    	Map<String, String> params = Form.form().bindFromRequest().data();
    	String host = request().host();
    	params.put("host", host);
        Logger.debug("telRegister params-->" + params.toString());
        return ok(disMemberService.telRegister(params));
    }
    
    /**
     * 普通用户后台注册（手机）
     * @return
     */
    public Result backstageRegister() {
        LoginContext lc = loginService.getLoginContext(2);
        if (lc == null || !lc.isLogin()) {
        	Map<String, Object> result = Maps.newHashMap();
        	result.put("suc", false);
            result.put("msg", "当前用户未登录");
            return ok(Json.toJson(result));
        }
        
        ObjectNode object =(ObjectNode)request().body().asJson();
        Logger.info("backstageResister-->" + object.toString());
    	object.put("registerMan", lc.getEmail());
    	object.put("host", request().host());
    	String param = object.toString();
    	return ok(Json.toJson(disMemberService.backstageRegister(param)));   
    }
    
    /**
     * /member/checkEmail前台处理 检查账号是否被注册
     * /member/sendMessage 发送短信   
     * /member/checkMessageCaptcha //检查短信验证码的正确性
     * {
     * 	email:
     *  captcha:
     *  smsc:
     * }
     * 
     * 验证手机号码账号是否已经被注册，
     * 若否，则发送短信
     *
     * @return
     */
    @SuppressWarnings("unchecked")
	public Result checktelRegister() {
    	Map<String, Object> result = Maps.newHashMap();
    	Map<String, String> params = Form.form().bindFromRequest().data();
    	if (params == null || !params.containsKey("email") || !params.containsKey("captcha")||
    	   !params.containsKey("smsc")	) {
            Logger.debug("请求参数不存在或格式错误");
            result.put("suc", false);
            result.put("msg", "请求参数不存在或格式错误");
            return ok(Json.toJson(result));
        }

    	String captcha = params.get("captcha");
    	String smsc = params.get("smsc");
    	//去除缓存获取短信验证码，修改短信验证码的校验方式 by huchuyin 2016-10-11
    	//String sms = (String) sessionService.get("SMS_CAPTCHA_NAME");
    	Logger.info("smsc-------------->"+smsc);
		//Logger.info("sms---------------->"+sms);
		Logger.info("captcha---------------->"+captcha);
    	//判断验证码是否正确
    	if(!captchaService.verify(captcha)){
    		Logger.info("验证码输入错误");
    		result.put("suc", false);
			result.put("msg", "验证码输入错误");
			return ok(Json.toJson(result));
    	}
    	
    	//校验短信验证码
		String email = params.get("email");
		String checkResult = disMemberService.checkSmsCode(smsc, email, Constant.SMS_TYPE_PHONE_REG);
		Map<String,Object> map = Json.fromJson(Json.parse(checkResult), Map.class);
		boolean suc = (Boolean) map.get("suc");
		if (!suc) {
			Logger.info("短信验证码输入错误");
			result.put("suc", false);
			result.put("msg", map.get("msg"));
			return ok(Json.toJson(result));
		}
		
        return ok(Json.toJson(disMemberService.checktelRegister(params)));
    }

    /**
     * 普通用户(前台用户)登录
     * @return
     */
	@ApiOperation(value = "普通用户(前台用户)登录", notes = "Authenticates the user with a given password. Valid usernames are `user-1` to `user-10`. The password is `pass`", nickname = "login", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", value = "JSON body must contain a username and password.", required = false, dataType = "application/json", paramType = "body", 
					defaultValue = "{\n\"email\":\"zhengbc@qq.com\",\n\"passWord\":\"123456a\"\n}") })
    public Result login() {
		JsonNode node = request().body().asJson();
		String host = request().host();
		Integer times = sessionService.get("login_times") == null ? 0
				: Integer.valueOf(sessionService.get("login_times").toString()) + 1;
		Map<String, String> result = disMemberService.login(node, host, times);
		String code = result.get("errorCode");
		if ("0".equals(code)) {
			// 保存登录状态
			loginService.setLoginContext(1,
					LoginContextFactory.initLC(result.get("id"), result.get("username"), result.get("email"),result.get("distributionmode"),Integer.valueOf(result.get("distributionType"))));
			result.put("ltc_key", CookieTrackingFilter.getLongTermCookie(Context.current()));
			sessionService.set("login_times", 0);
		} else {
			sessionService.set("login_times", times);
			// 清除登录状态
			loginService.clearLoginContext(1);
		}
		return ok(Json.toJson(result));
	}
	
	/**
     * 提供给php使用的免密接口
     * @return
     */
	public Result login4php() {
		JsonNode node = request().body().asJson();
		String host = request().host();
		String email = node.has("email")?node.get("email").asText():null;
		String phpLoginToken = node.has("phpLoginToken")?node.get("phpLoginToken").asText():null;
		Logger.debug("php前台免密登录参数：email={}-----phpLoginToken={}-----host={}",email,phpLoginToken,host);
		
		Map<String, Object> result = disMemberService.loginWithoutPwd(email, phpLoginToken, host);
		
		String code = (String) result.get("errorCode");
		String ltc = CookieTrackingFilter.getLongTermCookie(Context.current());
		if ("0".equals(code)) {
			DisMember disMember = (DisMember) result.get("memberInfo");
			// 保存登录状态
			loginService.setLoginContext(1,
					LoginContextFactory.initLC(String.valueOf(disMember.getId()), disMember.getUserName(),
							disMember.getEmail(), String.valueOf(disMember.getDistributionMode()),
							disMember.getComsumerType()));
			sessionService.set(1 + ltc, Json.toJson(disMember).toString());
			result.put("ltc_key", ltc);
		} else {
			// 清除登录状态
			loginService.clearLoginContext(1);
			sessionService.remove(1 + ltc);
		}
		return ok(Json.toJson(result));
	}

    /**
     * 普通用户(前台用户)退出
     * @return
     */
    public Result logout() {
        //清除登录状态
        loginService.clearLoginContext(1);
        return ok();
    }

    /**
     * 管理员用户(后台用户)登录
     * @return
     */
    @ApiOperation(value = "管理员(后台用户)登录", notes = "", nickname = "adminLogin", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", value = "JSON body must contain a user and cipher.", required = false, dataType = "application/json", paramType = "body", 
					defaultValue = "{\n\"user\":\"superadmin\",\n\"cipher\":\"q1q1q1\"\n}") })
    public Result adminLogin() {
        Map<String, String> result = Maps.newHashMap();
        Map<String, String> params = Form.form().bindFromRequest().data();
        if (params == null || !params.containsKey("user") || !params.containsKey("cipher")) {
            Logger.debug("请求参数不存在或格式错误");
            result.put("success", "false");
            result.put("message", "请求参数不存在或格式错误");
            return ok(Json.toJson(result));
        }
        
        params.put("host", request().host());
        Logger.debug("adminLogin params-->" + params.toString());
        result = disMemberService.adminLogin(params);
        if ("true".equals(result.get("success"))) {
            //保存登录状态
			loginService.setLoginContext(2, LoginContextFactory.initLC(result.get("id"), result.get("username"),
					result.get("email"), result.get("distributionmode"), null));
        } else {
            //清除登录状态
            loginService.clearLoginContext(2);
            sessionService.remove(2 + CookieTrackingFilter.getLongTermCookie(Context.current()));
        }
        return ok(Json.toJson(result));
    }
    
    public Result adminLogin4php() {
    	JsonNode node = request().body().asJson();
		String host = request().host();
		String email = node.has("email")?node.get("email").asText():null;
		String phpLoginToken = node.has("phpLoginToken")?node.get("phpLoginToken").asText():null;
    	
        Logger.debug("php后台免密登录参数：email={}-----phpLoginToken={}-----host={}",email,phpLoginToken,host);
        Map<String, Object> result = disMemberService.adminLoginWithoutPwd(email,phpLoginToken,host);
        String code = (String) result.get("errorCode");
        String ltc = CookieTrackingFilter.getLongTermCookie(Context.current());
		if ("0".equals(code)) {
			DisMember disMember = (DisMember) result.get("memberInfo");
			// 保存登录状态
			loginService.setLoginContext(2,
					LoginContextFactory.initLC(String.valueOf(disMember.getId()), disMember.getUserName(),
							disMember.getEmail(), String.valueOf(disMember.getDistributionMode()), null));
			result.put("ltc_key", ltc);
			sessionService.set(2 + ltc, Json.toJson(disMember).toString());
        } else {
			// 清除登录状态
			loginService.clearLoginContext(2);
			sessionService.remove(2 + ltc);
        }
		
        return ok(Json.toJson(result));
    }

    /**
     * 管理员用户(后台用户)退出
     * @return
     */
    public Result adminLogout() {
        //清除登录状态
        loginService.clearLoginContext(2);
        return ok();
    }

    /**
     * 普通用户(前台用户)是否登录
     * @return
     */
    public Result isNormalUserLogin() {
        Map<String, Object> result = Maps.newHashMap();
        LoginContext lc = loginService.getLoginContext(1);
        if (lc == null || !lc.isLogin()) {
            result.put("suc", false);
            result.put("msg", "当前用户未登录");
            return ok(Json.toJson(result));
        }
        
        result.put("user", lc);
        result.put("suc", true);
        result.put("msg", disMemberService.getMember(lc.getEmail()));
        return ok(Json.toJson(result));
    }

    /**
     * 管理员用户(后台用户)是否登录
     * @return
     */
    public Result isAdminUserLogin() {
        Map<String, Object> result = Maps.newHashMap();
        LoginContext lc = loginService.getLoginContext(2);
        if (lc == null || !lc.isLogin()) {
            result.put("suc", false);
            result.put("msg", "当前用户未登录");
            return ok(Json.toJson(result));
        }
        
        result.put("user", lc);
        result.put("suc", true);
        result.put("msg", "当前用户已登录");
        return ok(Json.toJson(result));
    }
  /**
   * 手机账户找回密码 验证码验证
   * 以及账号验证
   */
   @BodyParser.Of(BodyParser.Json.class)
   public Result checkTelFindPWD(){
       Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("email")
				|| !params.containsKey("captcha")) {
			Logger.debug("请求参数不存在或格式错误");
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
	   
	   String captcha = params.get("captcha");
	   //判断验证码是否正确
		if (!captchaService.verify(captcha)) {
			Logger.info("验证码输入错误");
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "验证码输入错误");
			return ok(Json.toJson(result));
		}
   	   
	   return ok(Json.toJson(disMemberService.checkTelFindPWD(params)));
   }
   
   /**
    * 手机账户找回密码 验证账号，验证手机验证码，验证手机号是否匹配
    * 
    */
	@SuppressWarnings("unchecked")
	public Result sendCheckCode() {
		Map<String, Object> result = Maps.newHashMap();
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("email") || !params.containsKey("smsc")
				|| !params.containsKey("cell")) {
			Logger.debug("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		//去除缓存获取短信验证码，修改短信验证码的校验方式 by huchuyin 2016-10-11
		String smsc = params.get("smsc");
		//String sms = (String) sessionService.get("SMS_CAPTCHA_NAME");
		Logger.info("smsc-------------->" + smsc);
		//Logger.info("sms---------------->" + sms);
		//校验短信验证码
		String cell = params.get("cell");
		String checkResult = disMemberService.checkSmsCode(smsc, cell, Constant.SMS_TYPE_FIND_PWD);
		Map<String,Object> map = Json.fromJson(Json.parse(checkResult), Map.class);
		boolean smsCodeSuc = (Boolean) map.get("suc");
		if(!smsCodeSuc){
			Logger.info("短信验证码输入错误");
			result.put("suc", false);
			result.put("msg", (String)map.get("msg"));
			return ok(Json.toJson(result));
		}
		
		Map<String, Object> sendCheckCodeResult = disMemberService.sendCheckCode(params);
		if ((Boolean) sendCheckCodeResult.get("suc")) {
			// 若返回结果正确，则将UUID放置到缓存中
			sessionService.remove("UUID_CODE_CAPTCHA_NAME");
			String code = (String) sendCheckCodeResult.get("code");
			sessionService.set("UUID_CODE_CAPTCHA_NAME", code);
		}

		return ok(Json.toJson(sendCheckCodeResult));
	}
	
	/**
    * 重置密码验证手机 验证码
    * password 
    * email
    * smsc
    */
	@SuppressWarnings("unchecked")
	public Result resetPWDbyTEL() {
		Map<String, Object> result = Maps.newHashMap();
		Map<String, String> params = Form.form().bindFromRequest().data();
		if(params == null || !params.containsKey("email")||!params.containsKey("smsc")
				|| !params.containsKey("password")
				|| !params.containsKey("cell")){
			Logger.debug("请求参数不存在或格式错误");
			result.put("success", false);
			result.put("info", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		//解密邮箱，验证码与获取UUID by huchuyin 2016-10-10
		String smsc = AESOperator.getInstance().decrypt(params.get("smsc"));;
		String email = AESOperator.getInstance().decrypt(params.get("email"));
		String cell = AESOperator.getInstance().decrypt(params.get("cell"));
		String code = params.get("code");
		String password = params.get("password");
		//String sms = (String) sessionService.get("SMS_CAPTCHA_NAME");
		String codeUUID = (String) sessionService.get("UUID_CODE_CAPTCHA_NAME");
		Logger.info("smsc-------------->" + smsc);
		//Logger.info("sms---------------->" + sms);
		//密码必须为6-20个字符，且至少包含数字、大小写字母等三种或以上字符 by huchuyin 2016-10-6
		if(StringUtils.isEmpty(password)
				|| (password.length() < 6 || password.length() > 20)
				|| !utils.dismember.StringUtils.containsLetterNum(password)) {
			result.put("success",false);
			result.put("info", "密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符！");
			return ok(Json.toJson(result));	
		}
		//End by huchuyin 2016-10-6
		if(!code.equalsIgnoreCase(codeUUID)) {
			Logger.info(this.getClass().getName()+" resetPWDbyTEL UUID Error!");
			result.put("success", false);
			result.put("info", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));	
		}

		//修改手机号码与短信验证码校验 by huchuyin 2016-10-11
		String checkResult = disMemberService.checkSmsCode(smsc, cell, Constant.SMS_TYPE_FIND_PWD);
		Map<String,Object> checkMap = Json.fromJson(Json.parse(checkResult),Map.class);
		boolean suc = (Boolean) checkMap.get("suc");
		if(!suc){
			Logger.info("短信验证码输入错误");
			result.put("success", false);
			result.put("info", (String) checkMap.get("msg"));
			return ok(Json.toJson(result));
		}
		
		Map<String, Object> resetPasswordResult = findPasswordByEmailService.resetPassword(email, null, MD5Util.MD5Encode(password, "utf-8"));
		//若修改密码成功，则清空UUID缓存 by huchuyin 2016-10-10
		if((Boolean) resetPasswordResult.get("success")) {
			sessionService.remove("UUID_CODE_CAPTCHA_NAME");
		}
		return ok(Json.toJson(resetPasswordResult));	
	}
	
	public Result loginTimes() {
		return ok(sessionService.get("login_times") + "");
	}
	
	/**
	 * 前台注册申请成为经销商
	 * @return
	 */
	public Result registerApply() {
		MultipartFormData formData = request().body().asMultipartFormData();
    	Map<String, String[]> params = formData.asFormUrlEncoded();
    	List<FilePart> files = formData.getFiles();
    	if (params == null || files == null || !params.containsKey("password")
    			|| !params.containsKey("account")) {
    		ObjectNode result = Json.newObject();
    		result.put("suc", false);
    		result.put("msg", "参数错误");
    		return ok(Json.toJson(result.toString()));
    	}
    	
    	Logger.info("password------->{}", params.get("password")[0]);
    	return ok(Json.toJson(disMemberService.registerApply(params,files)));  
    }
	
	/**
	 * 得到注册申请的数据
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getApplys(){
		if (!loginService.isLogin(2)) {
			Logger.info("当前用户未登录或登录超时");
			ObjectNode result = Json.newObject();
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result.toString()));
		}
		JsonNode node = request().body().asJson();
		if(null == node){
			Logger.info("请求参数不存在或格式错误");
			ObjectNode result = Json.newObject();
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result.toString()));
		}
		
		Logger.info("getApplys----->{}", Json.toJson(node).toString());
		String param = node.toString();
		return ok(disMemberService.getRegisterApplys(param));
	}
	
	/**
	 * 得到指定注册申请的详情
	 * 
	 * @return
	 */
	public Result showDetail(Integer id) {
		if (!loginService.isLogin(2)) {
			ObjectNode result = Json.newObject();
			result.put("suc", false);
			result.put("msg", "用户未登陆");
			return ok(Json.toJson(result));
		}
		
		return ok(disMemberService.getDetail(id));
	}
	
	/**
	 * 审核注册申请
	 * @return
	 */
	@ApiOperation(value="注册审核",httpMethod="POST",notes="<b>该接口有后台登录校验</b>分销商账号注册省审核",response=ResultDto.class)
	@ApiImplicitParams({
		@ApiImplicitParam(
			name="prama",required=true,value="\"id\":申请id,必传<br/>\"status\":审核状态 (1:审核不通过,2:审核通过),必传<br/>"
					+ "\"attributionType\":用户归属类型,status为2时必传<br/>"
					+ "\"remark\":审核备注,必传<br/>"
					+ "\"reason\":审核不通过理由,status为1时 必传<br/>"
					+ "\"salesmanId\":业务员id,status为2是必传",
					defaultValue="{\n\"id\":258,\n\"salesmanId\":87,\n\"status\":2,\n\"remark\":\"sdsf\",\n\"attributionType\":1\n}",
					paramType="body",dataType="string"
		)
	})
	@ALogin
	@BodyParser.Of(BodyParser.Json.class)
	public Result auditApply() {
		JsonNode params = request().body().asJson();
		if (!JsonCaseUtil.checkParam(params, "id","status","remark")) {
    		return ok(Json.toJson(new ResultDto<>(false, "传入参数有误")));
    	}
    	Logger.info("auditApply------->{}", params);
    	String node = params.toString();
    	return ok(Json.toJson(disMemberService.auditApply(node,loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK).getEmail())));  
    }
	
	/**
	 * 根据id查询注册申请时上传的文件
	 * @param id
	 * @return
	 */
	public Result getAuditFile(Integer id) {
		if (id == null) {
			ObjectNode result = Json.newObject();
			result.put("suc", false);
			result.put("msg", "传入参数有误");
			return ok(Json.toJson(result));
		}
		
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		File file = disMemberService.getAuditFile(id);
		try {
			response().setHeader("Content-disposition", "attachment;filename=" + new String(file.getName().getBytes(), "ISO8859-1"));
		} catch (Exception e) {
			Logger.info(e.getMessage());
		}
		if(!file.exists()){
			return ok(Json.toJson(""));
		}
		return ok(file);
	}
	
	/**
	 * 注册申请待审核变为注册成普通用户
	 */
	public Result becomeOrdinaryUser(Integer id) {
		String host = request().host();
		return ok(Json.toJson(disMemberService.becomeOrdinaryUser(id,host)));
	}
	
	/**
	 * 后台申请成为经销商
	 * @return
	 */
	public Result backRegisterApply() {
		LoginContext lc = loginService.getLoginContext(2);
        if (lc == null || !lc.isLogin()) {
        	ObjectNode result = Json.newObject();
        	result.put("suc", false);
        	result.put("msg", "用户未登录");
        	return ok(Json.toJson(result.toString()));
        }
        
		MultipartFormData formData = request().body().asMultipartFormData();
    	Map<String, String[]> params = formData.asFormUrlEncoded();
    	List<FilePart> files = formData.getFiles();
    	if (params == null || files == null || !params.containsKey("password")
    			|| !params.containsKey("account")) {
    		ObjectNode result = Json.newObject();
    		result.put("suc", false);
    		result.put("msg", "参数错误");
    		return ok(Json.toJson(result.toString()));
    	}
    	
    	Logger.info("password------->{}", params.get("password")[0]);
    	return ok(Json.toJson(disMemberService.backRegisterApply(params,files,lc.getEmail())));  
    } 
	
	/**
	 * 修改注册申请文件
	 * @author lzl
	 * @since 2016年11月22日下午2:45:40
	 */
	public Result modifyApplyFiles(){
		LoginContext lc = loginService.getLoginContext(2);
		if (lc == null || !lc.isLogin()) {
			ObjectNode result = Json.newObject();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(result.toString());
		}
		
		MultipartFormData formData = request().body().asMultipartFormData();
		Map<String, String[]> params = formData.asFormUrlEncoded();
		List<FilePart> files = formData.getFiles();
		if (params == null || files == null || !params.containsKey("applyId")) {
			ObjectNode result = Json.newObject();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(result.toString());
		}

		return ok(disMemberService.modifyApplyFiles(params, files,
				lc.getEmail()));
	}
	
	/**
	 * 根据注册申请成为经销商的申请id得到文件修改的历史记录
	 * @author lzl
	 * @since 2016年11月23日下午5:20:09
	 */
	public Result applyFileHistory(Integer applyId) {
		LoginContext lc = loginService.getLoginContext(2);
		if (lc == null || !lc.isLogin()) {
			ObjectNode result = Json.newObject();
			result.put("suc", false);
        	result.put("msg", "用户未登录");
        	return ok(result.toString());
		}
		
		return ok(disMemberService.getApplyFileHistory(applyId));
	}
}
