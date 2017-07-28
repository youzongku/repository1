package controllers.inventory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import controllers.annotation.ALogin;
import controllers.annotation.Login;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.inventory.IInventoryService;
import services.inventory.IUserService;
import services.product_inventory.IProductMicroInventoryService;
import services.product_inventory.impl.ProductMicroInventoryService;
import utils.inventory.HttpUtil;

import java.util.Map;

/**
 * 物理库存，微仓库存变动
 * @author Alvin Du
 */
public class InventoryManager extends Controller{

	@Inject
	IInventoryService inventoryService;
	

 	@Inject
	IProductMicroInventoryService productMicroInventoryService;

	@Inject
	IUserService userService;
    
    /**
	 * 生成发货单之前，微仓库存检查（检查指定分销商，指定商品在指定地域的微仓库存）
	 * 参数数据格式
     * {
     *     "email":"xxx",
     *     "pros":[
     *     			{
     *     				"sku":"xxx",
     *     				"qty":"xxx要购买的数量",
     *     				"warehouseId":"xxx",
     *     			},...
     *     		  ]
     *     "totalCheck"：1，本参数可选，当传入本参数，将检查SKU的云仓库存，若不传，将检查SKU的微仓库存
     * }
	 * @return 当前分销商指定仓库各个指定sku商品的库存情况
	 */
    @ALogin
	@BodyParser.Of(BodyParser.Json.class)
	public Result disInventoryCheck(){
		
		JsonNode main = request().body().asJson();
		
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		String s_main = main.toString();
		
		
		return ok(Json.toJson(inventoryService.disInventoryCheck(s_main,null)));
	}
	
	/**
	 * 生成发货单，扣除分销商相应的微仓库存
	 * 参数数据格式
     * {
     *     "email":"xxx",
     *     "pros":[
     *     			{
     *     				"sku":"xxx",
     *     				"qty":"xxx负数是减少，正数是增加，考虑后续退货的情况",
     *     				"warehouseId":"xxx",
     *     			},...
     *     		  ]
     * }
	 * @return 
	 */
    @ALogin
	@BodyParser.Of(BodyParser.Json.class)
	public Result deductDisInventory(){
		JsonNode main = request().body().asJson();
		Logger.info("微仓库存扣减参数:[{}]",main);
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		return ok(Json.toJson(inventoryService.deductDisInventory(main, null)));
	}
}
 