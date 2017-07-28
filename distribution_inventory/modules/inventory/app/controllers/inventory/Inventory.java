package controllers.inventory;

import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import component.elasticsearch.IInventoryComponent;
import controllers.annotation.Login;
import entity.product_inventory.ProductMicroInventoryInRecord;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.inventory.IInventoryService;
import services.inventory.IUserService;
import services.product_inventory.IProductMicroInventoryService;
/**
 * 物理库存，微仓库存变动
 * @author Alvin Du
 */
public class Inventory extends Controller{

	@Inject
	IInventoryService inventoryService;
	
	@Inject
	IProductMicroInventoryService productMicroInventoryService;
	
	@Inject
	IUserService userService;
	
	@Inject
	IInventoryComponent inventoryComp;
	
	@Login
	@BodyParser.Of(BodyParser.Json.class)
    public Result getFreight(){
    	JsonNode main = request().body().asJson();
    	if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
    	return ok(inventoryService.getFreight(main));
    }
	
	
	/**
	 * 根据仓库ID获取可用物流方式
	 * @return
	 */
	@Login
	@BodyParser.Of(BodyParser.Json.class)
    public Result getShippingMethodByWarehouseId(Integer wid){
    	return ok(Json.toJson(inventoryService.getShippingMethodByWarehouse(wid)));
    }
	
	/**
     * 采购订单支付完毕，更新微仓，物理仓信息
     * 参数数据格式
     * {
     *     "email":"xxx",
     *     "purchaseNo":"xxx批次号",
     *     "pros":[
     *     			{
     *     				"sku":"xxx",
     *     				"qty":"xxx",
     *     				"warehouseId":"xxx",
     *     				"purchasePrice":"xxx",
     *     				"productName":"xxx"
     *     			},...
     *     		  ]
     * }
     * @return 更新的产品数量
     */
	@Login
    @BodyParser.Of(BodyParser.Json.class)
    public Result updateWarehouseInventoryByOrderInfo(){
    	
    	JsonNode main = request().body().asJson();
    	
    	if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
    	String email = userService.getDisAccount();
    	return ok(Json.toJson(inventoryService.updateInventoryByOrderInfo(main, email)));
    	
    }
    
    /**
     * 根据指定的参数，冻结t_warehouse_inventory中的库存
     * 			  [
	 *     			{
	 *     				"sku":"xxx",
	 *     				"qty":"xxx",（正数是冻结一定的数量，负数是解冻一定的数量）
	 *     				"warehouseId":"xxx"
	 *     			},...
	 *     		  ]
     * @return
     */
    public Result updateWarehouseInventoryStockByOrderInfo(){
    	
    	JsonNode main = request().body().asJson();
    	
    	if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
    	
    	return ok(Json.toJson(inventoryService.frozenStock(main)));
    	
    }
    
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
    @Login
	@BodyParser.Of(BodyParser.Json.class)
	public Result disInventoryCheck(){
		
		JsonNode main = request().body().asJson();
		
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		String s_main = main.toString();
		String email = userService.getDisAccount();
		return ok(Json.toJson(inventoryService.disInventoryCheck(s_main, email)));
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
    @Login
	@BodyParser.Of(BodyParser.Json.class)
	public Result deductDisInventory(){
		JsonNode main = request().body().asJson();
		Logger.info("微仓库存扣减参数:[{}]",main);
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		String email = userService.getDisAccount();
		return ok(Json.toJson(inventoryService.deductDisInventory(main, email)));
	}
	
	/**
	 * 获取指定分销商的微仓产品信息
	 * 
	 * {
		    "email": "123@qq.com",
		    "warehouseId":"" 本参数为可选参数，填写的话会查询指定仓库的信息
		}
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getDisProduct(){
		
		JsonNode main = request().body().asJson();
		
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		return ok(Json.toJson(inventoryService.getDisProduct(main)));
	}
	
	/**
	 * 获取指定分销商的微仓产品信息
	 * xu_shengen
	 * {
		    "email": "123@qq.com",
		    "warehouseId":"" 本参数为可选参数，填写的话会查询指定仓库的信息
		}
	*/
	@Login
	@BodyParser.Of(BodyParser.Json.class)
	public Result initDisproduct(){
		
		JsonNode main = request().body().asJson();
		
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		String email = userService.getDisAccount();
		
		return ok(Json.toJson(inventoryService.initDisproduct(main, email)));
	}
	
	/**
	 * 获取分销商所有商品所在的微仓
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result selectAllStock(){
		
		JsonNode main = request().body().asJson();
		
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		return ok(Json.toJson(inventoryService.selectAllStock(main)));
	}
	
	
	/**
	 * 获取微仓产品信息及其库存信息
	 * {
	 *     "email":"xxx",
	 *     "sku":"xxx"
	 * }
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getDisProductAndStockInfo(){
		
		JsonNode main = request().body().asJson();
		
		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		
		return ok(Json.toJson(inventoryService.getDisProductAndStockInfo(main)));
	}


	/**
	 * 展示微仓变更历史
	 */
	@BodyParser.Of(BodyParser.Json.class)
	@Login
	public Result getDisProductMicroChangeHistory() {
		JsonNode main = request().body().asJson();

		Logger.info("查询微仓变更历史:[{}]",main);

		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}

		String email = userService.getDisAccount();
		String s_main = main.toString();
		//设置日期格式
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(fmt);
		Json.setObjectMapper(mapper);
		return ok(Json.toJson(inventoryService.getDisProductMicroChangeHistory(s_main, email)));
	}
	
	/**
	 * 展示微仓入仓记录
	 */
	@BodyParser.Of(BodyParser.Json.class)
	@Login
	public Result getDisProductMicroDetail() {
		JsonNode main = request().body().asJson();

		Logger.info("查询微仓明细:[{}]",main);

		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}

		String email = userService.getDisAccount();
		String s_main = main.toString();
		//设置日期格式
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(fmt);
		Json.setObjectMapper(mapper);
		List<ProductMicroInventoryInRecord> resultList= productMicroInventoryService.getProductMicroInventoryDetail(s_main,email);
		return ok(Json.toJson(resultList));
		//return ok(Json.toJson(inventoryService.getDisProductMicroChangeHistory(s_main, email)));
	}
	/**
	 * 库存初始化接口
	 * 
	 * <ul>
	 * <li>初始化库存索引
	 * <li>调product的服务接口，初始化商品
	 * 
	 * @return
	 * @author ye_ziran
	 * @since 2017年1月4日 下午5:02:47
	 */
	public Result esInit(){
		inventoryComp.elasticSearchInit();
		return ok();
	}
}
 