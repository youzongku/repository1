package controllers.sales;

import java.util.Map;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.sales.IJdService;
import services.sales.IUserService;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import controllers.annotation.Login;

/**
 * 拉取京东订单
 * 
 * @author xuse
 *
 */
public class JdOrderController extends Controller {

	@Inject	private IJdService jdService;
	@Inject private	IUserService userService;

	/**
	 * TODO 目前为测试 后面改为店铺对象
	 * 
	 * @param { "SERVER_URL":"https://api.jd.com/routerjson",
	 *        "access_token":"473958e0-fe21-4e58-9783-060f0ddc4250",
	 *        "app_key":"74EDCE89B12DC7A62C9D405107852A9C",
	 *        "app_secret":"211ee70b38f3486db00b96b954b50e85",
	 *        "email":"18206698184", "start_date":"2016-5-25",
	 *        "end_date":"2016-6-24", "shop_name":"店铺名称" "shopId": }
	 * 
	 * @return
	 */
	@Login
	public Result pull() {
		// 同步 防止多人拉取 一个店铺 订单
		// 不同账号 拉取一个店铺订单
		// 店铺id 判断是否正在同步订单，如果是return ,拉取完毕 可以在此拉取

		JsonNode main = request().body().asJson();
		Logger.info("拉取京东订单参数:[{}]", main);
		Map<String, Object> result = Maps.newHashMap();
		if (main == null || !main.has("shopId")) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		result = jdService.pullOrder(main, userService.getDisAccount());

		return ok(Json.toJson(result));
	}
}
