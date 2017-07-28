package controllers.openapi;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import annotation.Login;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.openapi.IInventoryService;
import utils.Page;
import utils.response.ResponseResultUtil;

/**
 * @author Lzl
 */
public class InventoryController extends Controller {
	
	@Inject
	IInventoryService inventoryService;
	
	/**
	 * param:
	 * {"pageSize":10,"currPage":1,"productTitle":"IM22"}
	 * @return
	 */
	@Login
	public Result getMicroWarehuoseStorage(){
		JsonNode node = request().body().asJson();
		if(node == null || node.size() <= 0) {
			return ResponseResultUtil.newErrorJson(101, "参数错误，未获取指定参数。");
		}
		Page result = inventoryService.getMicroStorage(node,Context.current());
		if (result == null) {
			return ResponseResultUtil.newErrorJson(104, "未查询到指定商品微仓库存信息");
		}
		return ResponseResultUtil.newSuccessJson(result);
	}
	
	/**
	 * getCloudWarehuoseInfo?sku=IF639&warehouseId=2024
	 * @param sku:可选 
	 * @param warehouseid：可选
	 * @return
	 */
	@Login
	public Result getCloudWarehuoseStorage(String sku,Integer warehouseid){
		Page node = inventoryService.getCloudStorage(sku,warehouseid);
		if (node == null) {
			return ResponseResultUtil.newErrorJson(104, "没有查询到相关云仓库存");
		}
		return ResponseResultUtil.newSuccessJson(node);
	}
	
	/**
     * 获取所有仓库
     * @return
     */
    @Login
    public Result queryWarehouse(){
    	JsonNode node = request().body().asJson();
    	String wid = null;
    	if(node != null && node.has("wid")) {
    		wid = node.get("wid").asText(null);
    	}
    	return ResponseResultUtil.newSuccessJson(inventoryService.queryWarehouse(wid));
    }

}
