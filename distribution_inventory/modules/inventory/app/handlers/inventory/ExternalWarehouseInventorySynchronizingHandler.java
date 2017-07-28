package handlers.inventory;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import services.product_inventory.IProductCloudInventoryService;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

import entity.product_inventory.ProductInventoryDetail;
import events.product_inventory.ExternalWarehouseInventorySynchroizationEvent;
import utils.inventory.DateUtils;
import utils.inventory.HttpUtil;

public class ExternalWarehouseInventorySynchronizingHandler {

	@Inject
	private IProductCloudInventoryService productCloudInventoryService;

	private static String ERP_API_KEY = "";
	private static String ERP_HOST = "";
	private static String EXTERNAL_WAREHOUSE_API="";
	
	static {
		if (ERP_API_KEY.equals("")) {
			Configuration config = Play.application().configuration()
					.getConfig("erp");
			ERP_API_KEY = config.getString("apiKey");
		}
		if(EXTERNAL_WAREHOUSE_API.equals("")){
			Configuration config = Play.application().configuration().getConfig("erp");
			EXTERNAL_WAREHOUSE_API = config.getString("externalWarehouseApi");
		}
		if (ERP_HOST.equals("")) {
			Configuration config = Play.application().configuration()
					.getConfig("erp");
			ERP_HOST = config.getString("host");
		}
	}

	/**
	 * 外部仓库(福州1仓)库存获取
	 * @param event
	 */
	@Subscribe
	public void executeExternalWarehouseInventorySynchroization(ExternalWarehouseInventorySynchroizationEvent event){
		List<Map<String,Object>> dataUpdateForLog=Lists.newArrayList();
		//在总仓明细中获取当前福州1仓的商品数据
		Integer warehouseId=2062;
		List<ProductInventoryDetail> externalInventoryDatas=  productCloudInventoryService.getExternalWarehouseInventoryDetail(warehouseId);
		if(externalInventoryDatas==null || externalInventoryDatas.size()<=0){
			return ;
		}
		for(int i=0;i<externalInventoryDatas.size();i++){
			ProductInventoryDetail productInventoryDetailParam = externalInventoryDatas.get(i);
			Map<String,Object> reqParam=Maps.newHashMap();
			reqParam.put("sku", productInventoryDetailParam.getSku());
			reqParam.put("stock_id", productInventoryDetailParam.getWarehouseId());
			reqParam.put("timestamp", 0);
			String resultStrBoxInfo = HttpUtil.post(Json.toJson(reqParam).toString(),
					ERP_HOST + EXTERNAL_WAREHOUSE_API + "?api_key="
							+ ERP_API_KEY);
			JsonNode resultData=null;
			try {
				JsonNode externalWarehouseDataNode = Json.parse(resultStrBoxInfo);
				resultData = externalWarehouseDataNode.get("result");
			} catch (Exception e) {
				Logger.info("外部仓---福州仓库存同步发生异常======时间[{}]", DateUtils.date2string(new Date(), DateUtils.FORMAT_FULL_DATETIME_MI));
				Logger.info("异常信息：{}",e);
				break;
			}
			if(resultData==null || resultData.size()<=0){
				continue;
			}
			JsonNode productInventoryData = resultData.get(0);
			int availableStockNum = productInventoryData.get("available_stock_count").asInt();
			if(availableStockNum!=productInventoryDetailParam.getStock()){
				Map<String,Object> dataForLog=Maps.newHashMap();
				dataForLog.put("expirationDate", DateUtils.date2string(productInventoryDetailParam.getExpirationDate(), "yyyy-MM-dd"));
				dataForLog.put("sku", productInventoryDetailParam.getSku());
				dataForLog.put("oldstockNum", productInventoryDetailParam.getStock());
				
				productInventoryDetailParam.setStock(availableStockNum);
				int j= productCloudInventoryService.updateExternalWearhouseProductInventory(productInventoryDetailParam);
				dataForLog.put("nowStockNum", availableStockNum);
				dataUpdateForLog.add(dataForLog);
			}
		}
		if(dataUpdateForLog.size()>0){
			Logger.info("外部仓库库存变更前后信息[{}]",dataUpdateForLog.toString());
		}
	}
}
