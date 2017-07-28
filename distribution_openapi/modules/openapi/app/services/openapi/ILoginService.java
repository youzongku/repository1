package services.openapi;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Http.Context;

public interface ILoginService {
	
	/**
	 * 用户登录
	 * @return
	 */
	public Map<String, Object> login(JsonNode node,Context context);
	
	
	/**
	 * 根据当前请求，获取登陆信息，包含用户的所有信息
	 * @param string 
	 * @param context
	 * @return
	 */
	public JsonNode currentUser(String string);


	/**
	 * 退出登录
	 * @param current
	 * @param ltc 
	 */
	public void logout(Context current, String ltc);

}
