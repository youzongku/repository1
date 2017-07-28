package services.openapi.impl;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import play.Logger;
import play.libs.Json;
import play.mvc.Http.Context;
import services.openapi.ILoginService;
import session.ISessionService;
import utils.HttpUtil;

public class LoginService implements ILoginService {
	
	private static String USER_KEY = "user";
	
	@Inject
	private ISessionService sessionService;

	@Override
	public Map<String, Object> login(JsonNode node,Context context) {
		Map<String, Object> result = Maps.newHashMap();
		Map<String, String> param = Maps.newHashMap();
		param.put("email", node.get("email").asText());
		param.put("passWord", node.get("passWord").asText());
		try {
			String response = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/member/login",context);
			parseResult(response,result);
		} catch (Exception e) {
			Logger.error("登陆异常", e);
			result.put("success", false);
			result.put("message", "登陆失败");
		}
		return result;
	}

	private void parseResult(String response,Map<String, Object> result) {
		JsonNode node = Json.parse(response);
		if(node == null || node.size() <= 0 || node.get("errorCode").asInt() != 0) {
			result.put("success", false);
			result.put("message", node != null && node.has("errorInfo") ? node.get("errorInfo").asText() : "登陆失败");
		} else {
			result.put("success", true);
			result.put("message", "登陆成功");
			result.put("ltc_key", node.get("ltc_key").asText());
		}
	}

	@Override
	public JsonNode currentUser(String ltc) {
		JsonNode user = null;
		try {
			Context.current().args.put("TT_LTC", ltc);
			user = Json.parse(sessionService.get(USER_KEY,Context.current()).toString());
		} catch (Exception e) {
			Logger.error("获取用户信息异常",e);
		}
		return user;
	}

	@Override
	public void logout(Context current,String ltc) {
		HttpUtil.get(Maps.newHashMap(), HttpUtil.B2BBASEURL + "/member/logout",current,ltc);
	}

}
