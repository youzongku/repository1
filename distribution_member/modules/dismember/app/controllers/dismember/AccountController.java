package controllers.dismember;

import java.util.HashMap;
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
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import constant.dismember.Constant;
import controllers.annotation.ALogin;
import controllers.annotation.Login;
import dto.dismember.ResultDto;
import entity.dismember.AccountPeriodSlave;
import entity.dismember.DisAccount;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IAccountPeriodService;
import services.dismember.IDisAccountService;
import services.dismember.IDisMemberService;
import services.dismember.ILoginService;
import utils.dismember.HttpUtil;
import utils.dismember.JsonCaseUtil;
import vo.dismember.LoginContext;

/**
 * Created by luwj on 2015/11/25.
 */
@Api(value = "/账户模块", description = "account")
public class AccountController extends Controller {

	@Inject
	private IDisMemberService memberService;

	@Inject
	private ILoginService loginService;

	@Inject
	private IDisAccountService accountService;

	@Inject
	private IAccountPeriodService apPeriodService;

	/**
	 * 获取指定用户资金账户信息
	 * 
	 * @return
	 */
	 @ApiOperation(
	    value = "Authenticates the user.",
	    notes = "Authenticates the user with a given password. Valid usernames are `user-1` to `user-10`. The password is `pass`",
	    nickname = "auth",
	    httpMethod = "GET")
	 @ApiImplicitParam(name = "body", value = "JSON body must contain a username and password.", required = false,
		        dataType = "application/json", paramType = "body")
	 @ApiResponses(
		{@ApiResponse(code = 200, message = "Username and password is valid. Returns a token. The token contains the encrypted username."),
		@ApiResponse(code = 303, message = "Access-Control-Allow-Origin."),
		@ApiResponse(code = 400, message = "Username or password not supplied."),
		@ApiResponse(code = 401, message = "Password is incorrect."),
		@ApiResponse(code = 401, message = "Username doesn't exist.")}
	 )
	public Result getAccount() {
		if (!loginService.isLogin(1)) {
			return redirect(HttpUtil.getUrl() + "/personal/login.html");
		}
		return ok(Json.toJson(accountService.getAccountInfo(loginService.getLoginContext(1).getEmail())));
	}

	/**
	 * 根据邮箱检查资金账户是否被冻结
	 * 
	 * @return
	 */
	@Login
	public Result checkFrozen() {
		Map<String, Object> result = Maps.newHashMap();
		String email = loginService.getLoginContext(1).getEmail();
		DisAccount account = accountService.checkFrozen(email);
		AccountPeriodSlave acPeriond = apPeriodService.getCurAp(email);
		Map<String, String> accountinfo = memberService.getInfo(email);  
		String nick = accountinfo.get("nickName");
		result.put("acPeriond", acPeriond);
		result.put("result", account);
		result.put("nick", nick == null ? "" : nick);
		return ok(Json.toJson(new ResultDto<>(100,null,result)));
	}

	/**
	 * 保存指定用户的资金账户支付密码
	 * @return
	 */
	public Result savePayPassword() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(1)) {
			Logger.info("当前用户未登录");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		
		Map<String, String> params = Form.form().bindFromRequest().data();
		params.put("email", loginService.getLoginContext(1).getEmail());
		if (accountService.updateDisAccount(params)) {
			result.put("suc", true);
			return ok(Json.toJson(result));
		}
		
		result.put("suc", false);
		result.put("msg", "保存支付密码失败");
		return ok(Json.toJson(result));
	}
	
	/**
	 *重置支付密码
	 *@return 
	 */
	@SuppressWarnings("unchecked")
	public Result resetPayPassword(){
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(1)) {
			Logger.info("当前用户未登录");
			result.put("suc", 6);
			result.put("msg", "当前用户未登录");
			return ok(Json.toJson(result));
		}
		
		Map<String, String> params = Form.form().bindFromRequest().data();
		//captcha 图片验证码，code 邮箱验证码，cell 新手机号，smsc 短信验证码，email 当前用户邮箱
		if (params == null || !params.containsKey("password")) {
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", 3);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		//修改邮箱不可由前台传入，修改为从登录信息获取 by huchuyin 2016-10-11
		//String email = params.get("email");
		LoginContext lc = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_PERSONAL);
		String email = lc.getEmail();
		params.put("email", email);
		// add by zbc 
		boolean emailFlag = email.contains("@");
		Logger.info("resetPayPassword------>{}", params.toString());
		
		// ========================邮箱=============================
		if(emailFlag){//手机用户可以不通过邮箱
			if(!params.containsKey("code")){
				Logger.info("请求参数不存在或格式错误");
				result.put("suc", 3);
				result.put("msg", "请求参数不存在或格式错误");
				return ok(Json.toJson(result));
			}
			
			//校验邮箱验证码
			boolean isPermitted = memberService.verifyEmailCode(params.get("email"), params.get("code"));
			if (!isPermitted) {
				result.put("suc", 5);
				result.put("msg", "邮箱验证码错误");
				return ok(Json.toJson(result));
			}
			
			// 重置密码
			return ok(Json.toJson(accountService.resetPayPassword(params)));
		}
		
		// ========================手机=============================
		//校验手机号码与短信验证码
		String smsc = params.get("smsc");
		String cell = params.get("cell");
		if(StringUtils.isBlank(smsc) || StringUtils.isBlank(cell)) {
			result.put("suc", 3);
			result.put("msg", "手机号码与验证码参数错误");
			return ok(Json.toJson(result));
		}
		
		String resultStr = memberService.checkSmsCode(smsc, cell, Constant.SMS_TYPE_SET_PAY_PWD);
		Map<String,Object> map = Json.fromJson(Json.parse(resultStr),Map.class);
		if(!(Boolean) map.get("suc")) {
			result.put("suc", 5);
			result.put("msg", (String)map.get("msg"));
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(accountService.resetPayPassword(params)));
	}
	
	/**
	 * 获取指定用户资金账户信息
	 * 
	 * @return
	 */
	public Result getAccountByEmail(String email) {
		if (!loginService.isLogin(2)&&(request().getQueryString("isback") == null || !"true".equals(request().getQueryString("isback")))) {
			return redirect(HttpUtil.getUrl() + "/personal/login.html");
		}
		
		return ok(Json.toJson(accountService.getAccountInfo(email)));
	}
	
	/**
	 * 获取用户账户信息，判断是否显示验证码
	 * @return
	 * @author huchuyin
	 * @date 2016年10月7日 下午5:40:19
	 */
	public Result getDisAccountInfo() {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		//获取登录用户信息
		LoginContext lc = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_PERSONAL);
		if(lc == null) {
			resultMap.put("suc", false);
			resultMap.put("code", 2);
			resultMap.put("msg", "当前用户未登录");
			return ok(Json.toJson(resultMap));
		}
		
		//查询登录用户的账户信息
		DisAccount account = accountService.getAccountByEmail(lc.getEmail());
		resultMap.put("suc", true);
		resultMap.put("msg", account);
		return ok(Json.toJson(resultMap));
	}
	
	/**
	 * 得到所有的账号信息
	 * @author lzl
	 * @since 2016年12月7日上午11:50:40
	 */
//	@BodyParser.Of(BodyParser.Json.class)
	public Result getAllAccount() {
		//获取登录用户信息
		if(!loginService.isLogin(2)) {
			ObjectNode resultMap = Json.newObject();
			resultMap.put("suc", false);
			resultMap.put("msg", "当前用户未登录");
			return ok(Json.toJson(resultMap.toString()));
		}
		
		Map<String, String[]> asFormUrlEncoded = request().body().asFormUrlEncoded();
		//查询登录用户的账户信息
		String res = accountService.getAllAccount(loginService.getLoginContext(2).getEmail(), asFormUrlEncoded);
		return ok(res);
	}
	
	/**
	 * 根据email核减账户余额
	 * @author lzl
	 * @since 2016年12月14日上午10:48:13
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result reduceAccountByEmail() {
		//获取登录用户信息
		if(!loginService.isLogin(2)) {
			ObjectNode resultMap = Json.newObject();
			resultMap.put("suc", false);
			resultMap.put("msg", "当前用户未登录");
			return ok(Json.toJson(resultMap.toString()));
		}
		
		JsonNode node = request().body().asJson();
		if (node == null || !node.has("email") || !node.has("reduceAmount")) {
			ObjectNode resultMap = Json.newObject();
			resultMap.put("suc", false);
			resultMap.put("msg", "参数错误");
			return ok(Json.toJson(resultMap.toString()));
		}
		
		String param =  node.toString();
		Logger.info("reduceAccountByEmail---->{}", param);
		//查询登录用户的账户信息
		String res = accountService.reduceAccountByEmalil(loginService.getLoginContext(2).getEmail(), param);
		return ok(res);
	}
	
	/**
	 * 根据账户id得到历史核减记录
	 * @author lzl
	 * @since 2016年12月14日下午2:24:04
	 */
	public Result getAccountHistory(Integer accountId) {
		LoginContext lc = loginService.getLoginContext(2);
		if (lc == null || !lc.isLogin()) {
			ObjectNode result = Json.newObject();
			result.put("suc", false);
        	result.put("msg", "用户未登录");
        	return ok(result.toString());
		}
		
		return ok(accountService.getReduceAccountHistory(accountId));
	}
	
	/**
	 * 激活账户，解除账户冻结
	 * @author zbc
	 * @since 2017年3月7日 下午5:04:20
	 */
	@ALogin
	public Result unfreeze(String em){
		return ok(Json.toJson(accountService.unfreeze(em)));
	}
	
	/**
	 * 修改分销商昵称
	 * @author zbc
	 * @since 2017年3月29日 下午3:26:57
	 */
	@ALogin
	public Result changeNickName(){
		JsonNode json =request().body().asJson();
		if(!JsonCaseUtil.checkParam(json, new String[]{"em","nickName"})){
			return ok(Json.toJson(new ResultDto<>(false, "参数错误")));
		}
		
		return ok(Json.toJson(accountService.changeNickName(json.toString())));
	}
	
	/**
	 * 账期冻结金额：当账期剩余到达该金额时冻结账户
	 * @author zbc
	 * @since 2017年3月29日 下午3:26:57
	 */
	@ApiOperation(value = "账期冻结金额：当账期剩余到达该金额时冻结账户", notes = "Query Period Frozen", nickname = "Query Period Frozen", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name="email",value="用户账号",required=true,paramType="query",dataType = "string") })
	@ALogin
	public Result queryPeriodFrozen(String email){
		if(StringUtils.isEmpty(email)){
			return ok(Json.toJson(0));
		}
		return ok(Json.toJson(accountService.queryPeriodFrozen(email)));
	}
	
	/**
	 * 设置账期冻结金额，当账期剩余到达该金额时冻结账户
	 * @author zbc
	 * @since 2017年3月29日 下午3:26:57
	 */
	@ApiOperation(value = "设置账期冻结金额", notes = "Set Period Frozen", nickname = "Set Period Frozen", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", value = "JSON body must contain a account and amount.", required = false, dataType = "application/json", paramType = "body")})
	@ALogin
	public Result setPeriodFrozen(){
		JsonNode json =request().body().asJson();
		if(json == null){
			return ok(Json.toJson(new ResultDto<>(false,"非法参数，请重新确认后提交。")));
		}
		return ok(Json.toJson(accountService.setPeriodFrozen(json.toString())));
	}
	
	
}
