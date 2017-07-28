package controllers.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.openapi.ILoginService;
import utils.response.ResponseResultUtil;

/**
 * 
 * 登陆
 * @author xuse
 *
 */
public class LoginController extends Controller {
	
	@Inject
	private ILoginService loginService;
	
	/**
	 * @return
	 */
	public Result login(){
		JsonNode node = request().body().asJson();
		if(node == null || node.size() <= 0 ) {
			return ResponseResultUtil.newErrorJson(101, "参数错误。");
		}
		return ResponseResultUtil.newSuccessJson(loginService.login(node,Context.current()));
	}
	
	/**
	 * 退出登录
	 * @return
	 */
	public Result logout(String ltc) {
		loginService.logout(Context.current(),ltc);
		return ok(Json.toJson(true));
	}

}
