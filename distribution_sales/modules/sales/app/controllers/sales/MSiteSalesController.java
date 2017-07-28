package controllers.sales;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.sales.IMSiteSalesService;

/**
 * 描述：m站销售单控制类
 * 
 * @author zbc
 *
 */
public class MSiteSalesController extends Controller {
	
	@Inject private	IMSiteSalesService  msiteSalesService; 
	
	public Result getOrder(){
		JsonNode main = request().body().asJson();
		Logger.info("M站推送订单参数[{}]",main);
		
		if(main == null){
			Map<String,Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(msiteSalesService.order(main)));
	}
	
	
	/**
	 * 门店零售订单
	 * @return
	 */
	public Result acceptStoreOrder(){
		JsonNode main = request().body().asJson();
		Logger.info("门店推送零售订单参数[{}]",main);
		if(main == null){
			Map<String,Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(msiteSalesService.storeOrder(main)));
	}
	
}