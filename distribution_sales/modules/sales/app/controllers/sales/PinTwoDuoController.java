package controllers.sales;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import controllers.annotation.Login;
import events.sales.PddSyncEvent;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.sales.IJdService;
import services.sales.IUserService;
import util.sales.JsonCaseUtil;

/**
 * 
 * 1，拉取订单接口 2，订单发货通知拼多多 拼多多订单拉取控制类
 * 
 * @author zbc 2017年2月28日 下午6:08:02
 */
public class PinTwoDuoController extends Controller {

	
	@Inject
	private EventBus eBus;
	
	@Inject
	private IUserService userService;
	
	@Inject
	private IJdService jdService;

	@Login
	public Result pull() {
		JsonNode main = request().body().asJson();
		Logger.info("拉取拼多多订单参数:[{}]", main);
		Map<String, Object> result = Maps.newHashMap();
		if (main == null || !main.has("shopId")) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		String email = userService.getDisAccount();
		boolean isSync = jdService.syncOrder(result, JsonCaseUtil.getIntegerValue(main, "shopId"), email);
		if (isSync) {
			result.put("suc", false);
			result.put("msg", "该店铺正在同步订单");
			return ok(Json.toJson(result));
		}
		eBus.post(new PddSyncEvent(main,email));
		result.put("suc", true);
		result.put("msg", "正在同步订单，请稍后查询。");
		return ok(Json.toJson(result));
	}

}
