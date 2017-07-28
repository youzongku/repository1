package controllers.sales;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.sales.IManagerOrderService;

public class ProductController extends Controller {
	@Inject
	IManagerOrderService managerOrderservice;
	
	public Result productSalesVolumeCount(){
		Map<String,Object> result=Maps.newHashMap();
		JsonNode reqParam = request().body().asJson();
		String resultStr=managerOrderservice.productSalesVolumeSearch(reqParam.toString());
		JsonNode resultData = Json.parse(resultStr);
		result.put("data", resultData);
		return ok(Json.toJson(result));
	}
}
