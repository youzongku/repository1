package controllers.dismember;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IDisShopService;
import services.dismember.ILoginService;
import utils.dismember.HttpUtil;
import utils.dismember.StringUtils;

/**
 * 
 * 
 * @author xuse
 *
 */
public class AccessTokenController extends Controller {

	@Inject
	private ILoginService loginService;

	@Inject
	private IDisShopService disShopService;

	/**
	 * @param clientid
	 * @param accesskey
	 * @param shopId
	 * @return
	 */
	public Result getCodeUrl() {
		if (!loginService.isLogin(1)) {
			return redirect(HttpUtil.getUrl() + "/personal/login.html");
		}
		
		JsonNode node = request().body().asJson();
		if (node == null) {
			Map<String, Object> res = Maps.newHashMap();
			res.put("success", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res));
		}
		
		return ok(Json.toJson(disShopService.query(node)));
	}

	/**
	 * 回调地址
	 * 
	 * @param code
	 *            授权码
	 * @param state
	 *            店铺ID
	 * @return
	 */
	public Result callbackSuccess(String code, String state) {
		Logger.info("code:" + code + ",state:" + state);
		if (StringUtils.isBlankOrNull(code) || StringUtils.isBlankOrNull(state)) {
			Logger.info("授权失败。");
			return internalServerError("授权失败。");
		}
		if (!disShopService.getAccessToken(code, state)) {
			return internalServerError("授权失败。");
		}
		return redirect(HttpUtil.getUrl()+"/personal/personal.html");
	}

	/**
	 * 回调地址
	 * 
	 * @return
	 */
	public Result callbackError() {
		return internalServerError("授权失败。");
	}

}
