package handler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import entity.product.ProductDisprice;
import entity.product.Warehouse;
import event.ClearanceProductEvent;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import services.product.IProductBaseService;
import services.product.IProductDispriceService;
import util.product.HttpUtil;

/**
 * 同步商品erp状态、清仓价格
 * @author lenovo
 *
 */
public class ClearanceProductHandler {
	
	@Inject
	private IProductDispriceService dispriceService;
	
	@Inject
	private IProductBaseService productBaseService;
	
	@Subscribe
	public void productClearance(ClearanceProductEvent event){
		try {
			Configuration config = Play.application().configuration().getConfig("erp");
			String apiKey = config.getString("apiKey");
			String host = config.getString("host");
			String uri = config.getString("queryHkProductFlatList");
			String url=host+uri+"?api_key="+apiKey;
			//所有启用中的仓库
			List<Warehouse> availableWarehouseLists= productBaseService.getAvailableWarehouse();
			if(availableWarehouseLists.isEmpty()){
				return;
			}
			List<ProductDisprice> priceWaitUpdateLists=Lists.newArrayList();
			try {
				for(Warehouse warehouse:availableWarehouseLists){
					String warehouseNo = warehouse.getWarehouseNo();
					Map<String,Object> param=Maps.newHashMap();
					param.put("stock_id", warehouseNo);
					param.put("timestamp", 0);
					JsonNode returnJsonNode=null;
					String returnStr=null;
					try {
						returnStr=HttpUtil.post(Json.toJson(param).toString(), url);
						returnJsonNode = Json.parse(returnStr);
					} catch (Exception e) {
						Logger.info("定时任务：获取商品清仓折扣率发生异常{},请求参数{},响应{}", e,Json.toJson(param).toString(),returnStr);
						continue;
					}
					if(returnJsonNode==null ||!returnJsonNode.has("result") || returnJsonNode.get("result").size()<=0){
						continue;
					}
					JsonNode resultJsonNode = returnJsonNode.get("result");
					for(JsonNode node:resultJsonNode){
						String sku = node.get("sku").asText();
						int stockId = node.get("stock_id").asInt();
						int saleStatus = node.get("sale_status").asInt();
						
						List<ProductDisprice> productDispriceList=	dispriceService.getProductDispriceBySkuAndStockId(sku,stockId);
						if(productDispriceList.isEmpty()){
							continue;
						}
						ProductDisprice productDisprice = productDispriceList.get(0);
						int oldSaleStatus= productDisprice.getSaleStatus()!=null?productDisprice.getSaleStatus():-1;
						if(saleStatus==20){
							double clearanceRate = node.get("clear_stock_discount").asDouble();
							double clearanceprice=new BigDecimal(productDisprice.getArriveWarePrice()).multiply(new BigDecimal(clearanceRate)).
									setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							double oldClearanceprice= productDisprice.getClearancePrice()==null?-1f:productDisprice.getClearancePrice().doubleValue();
							if(clearanceprice!=oldClearanceprice || oldSaleStatus!=saleStatus){
								productDisprice.setSaleStatus(saleStatus);
								productDisprice.setClearanceRate(clearanceRate);
								productDisprice.setClearancePrice(clearanceprice);
								priceWaitUpdateLists.add(productDisprice);
							}
						}else{
							if(productDisprice.getSaleStatus()==null||productDisprice.getSaleStatus().intValue()!=saleStatus){
								productDisprice.setSaleStatus(saleStatus);
								priceWaitUpdateLists.add(productDisprice);
							}
						}
					}
				}
			} catch (Exception e) {
				Logger.info("定时任务：获取清货折扣发生异常:{}", e);
			}
			if(!priceWaitUpdateLists.isEmpty()){
				Logger.info("清货价定时任务:更新销售状态数据信息{}", priceWaitUpdateLists.toString());
				int num = dispriceService.updateClearancePrice(priceWaitUpdateLists);
			}
		} catch (Exception e) {
			Logger.info("定时任务：获取清货折扣发生异常:{}", e);
		}
	}
}
