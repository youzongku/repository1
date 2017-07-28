package controllers.inventory;

import java.util.Map;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.inventory.IWarehInvenService;

public class WarehInvenContoller extends Controller{
	
	@Inject
    IWarehInvenService iWarehInvenService;
	/**
     * 仓库信息接口(接收b2c推送仓库信息)
     * @return
     */
    public Result receWarehouses(){
        Map<String, String[]> node = request().body().asFormUrlEncoded();
        if(null == node){
        	return internalServerError("未获取到参数。");
        }
        Logger.info(">>>receWarehouses>>node>>" + node.toString());
        return ok(Json.toJson(iWarehInvenService.saveWarehouse(node)));
    }

    /**
     * 商品库存信息接口(接收b2c推送的库存信息)
     * @return
     */
    public Result receProducts(){
    	Map<String, String[]> node = request().body().asFormUrlEncoded();
        if(null == node){
        	return internalServerError("未获取到参数。");
        }
        Logger.info(">>>receProducts>>node>>" + node.toString());
        return ok(Json.toJson(iWarehInvenService.saveInvenInfo(node)));
    }
    
    
    /**
     * 前台获取所有仓库，需要过滤虚拟仓
     * @return
     */
//    @Login
    public Result queryWarehouse(Integer wid){
    	return ok(Json.toJson(iWarehInvenService.queryWarehouse(wid,false,null)));
    }
    
    /**
     * 后台获取所有仓库
     * @return
     */
    public Result backstageWarehouse(Integer wid){
    	return ok(Json.toJson(iWarehInvenService.queryWarehouse(wid,true,null)));
    }
    
    /**
     * 后台查询所有虚拟仓
     * @return
     */
    public Result virtualWarehouses() {
    	return ok(Json.toJson(iWarehInvenService.queryWarehouse(null,true,true)));
    }
    
    /**
     * 
     * 该接口已经作废----查询库存信息应该调用仓储系统接口
     * 根据sku和仓库id获取库存
     * @return
     */
    public Result queryInventory(){
    	Map<String, String[]> node = request().body().asFormUrlEncoded();
    	Logger.info(">>>queryInventory>>node>>" + node);
    	return ok(Json.toJson(iWarehInvenService.queryInventory(node)));
    }
    
    /**
     * 
     * 该接口已经作废----查询库存信息应该调用仓储系统接口
     * 根据sku和仓库id获取库存
     * @return
     */
    @BodyParser.Of(BodyParser.Json.class)
    public Result inventory(){
    	JsonNode node = request().body().asJson();
    	Logger.info(">>>inventory>>node>>" + node);
    	return ok(Json.toJson(iWarehInvenService.inventory(node)));
    }

}
