package controllers.warehousing;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.warehousing.IInventoryChangeHistoryService;

public class InventoryChangeHistoryController extends Controller {

	@Inject
	IInventoryChangeHistoryService inventoryChangeHistoryService;
	
	/**
	 * 参数格式
	 * {
	 * 		email:xxx,
	 * 		warehouseId:xxx,
	 * 		details:[
	 * 			{sku:xxx,qty:xxx},
	 * 			{sku:xxx,qty:xxx}...
	 * 		]
	 * }
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getPurchasePriceByChangeHistory(){
		JsonNode node = request().body().asJson();
		
		if(node == null || node.size() == 0){
			return ok(Json.toJson("参数不可为空"));
		}
		else{
			Logger.info("参数内容为：{}",node.toString());
		}
		
		return ok(Json.toJson(inventoryChangeHistoryService.getPurchasePriceByChangeHistory(node)));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result insert(){
		JsonNode node = request().body().asJson();
		Logger.info("-----"+node.toString());
		return ok(Json.toJson("sd"));
	}
}
