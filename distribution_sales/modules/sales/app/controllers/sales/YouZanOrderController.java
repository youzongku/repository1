package controllers.sales;

import java.util.Map;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.sales.IUserService;
import services.sales.IYZService;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import controllers.annotation.Login;

public class YouZanOrderController extends Controller{
	
	@Inject private IYZService yzService;
	@Inject private IUserService userService;
	
	@Login
	public Result pull() {
		JsonNode main = request().body().asJson();
		Map<String, Object> result = Maps.newHashMap();
		Logger.info("拉取有赞订单参数[{}]", main);
		if (main == null || !main.has("start_created") || !main.has("end_created") || !main.has("shopId")) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		result = yzService.pullOrder(main, userService.getDisAccount());
		return ok(Json.toJson(result));
	}
}
