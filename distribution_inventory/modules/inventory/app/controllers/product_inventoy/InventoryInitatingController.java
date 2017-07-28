package controllers.product_inventoy;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

import controllers.annotation.GetRequestIp;
import dto.product_inventory.InitProductInventoryResult;
import dto.product_inventory.InventoryCommonResult;
import dto.product_inventory.ProductMicroInventoyResult;
import entity.product_inventory.InventorySyncRecord;
import entity.product_inventory.OrderMicroInventoryDeductRecord;
import entity.product_inventory.ProductBase;
import entity.product_inventory.ProductInventoryDetail;
import entity.product_inventory.ProductInventoryTotal;
import entity.warehousing.Warehouse;
import events.product_inventory.CheckInventoryEvent;
import events.product_inventory.PhysicalInventoryEvent;
import extensions.InjectorInstance;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.inventory.IWarehInvenService;
import services.product_inventory.IProductBaseService;
import services.product_inventory.IProductCloudInventoryService;
import services.product_inventory.IProductInventoryDetailService;
import services.product_inventory.IinventorySyncRecordService;
import utils.inventory.DateUtils;
import utils.inventory.ErpApiUtil;
import utils.inventory.HttpUtil;
import utils.inventory.JsonCaseUtil;
import utils.inventory.Types;

@GetRequestIp
public class InventoryInitatingController extends Controller{

	@Inject
	private IProductBaseService productBaseService;
	
	@Inject
	private IinventorySyncRecordService inventorySyncRecordService;
	
	@Inject
	private IWarehInvenService warehInvenService;
	
	@Inject
	private IProductInventoryDetailService productInventoryDetailService;
	
	@Inject
	private IProductCloudInventoryService productCloudInventoryService;
	
	/**
	 * 初始化SKU在ERP的可用数到BBC商品库
	 * @param event
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public Result syncingInitInventory() {
		
		//获取所有在售的SKU
		List<ProductBase> ProductBases = productBaseService.getProductsByStatus(1);
		
		//待同步集合,该数据需返回
		Set<String> skusNeedSync = new HashSet<String>();
		//已同步集合，该数据需返回
		List<String> skusSynced = new ArrayList<String>();
		//库存不含到期日期的数据，单独列出,，需要返回结果，与ERP协调，该数据需返回
		List<String> datasHaveErrors = new ArrayList<String>();
		//含有待发数的SKU集合，意味着这些SKU此次没有进行初始化
		List<String> skuWithWaitDeliveryCount = new ArrayList<String>();
		
		//所有启用的仓库
		List<Warehouse> validableWarehouses = warehInvenService
				.queryWarehouse(null,true,null);
		Logger.info("所有启用仓库{}", validableWarehouses.toString());
		for (Warehouse warehouse : validableWarehouses) {
			//#1  遍历所有SKU， 获取所有上次同步结果
			for (ProductBase productBase : ProductBases) {
				
				InventorySyncRecord isr = inventorySyncRecordService.selectBySkuAndWarehouseId(productBase.getCsku(),Integer.valueOf(warehouse.getWarehouseId()));
				//同步记录不存在，需要初始化记录
				if(isr == null){
					//添加到待同步
					skusNeedSync.add(productBase.getCsku());
				}
				//同步记录存在，但是前次初始化时存在“待发数”，导致库存不准确，所以未初始化，那么此次进行初始化
				if(isr != null && isr.getIsActive() == 0){
					//添加都待同步
					skusNeedSync.add(productBase.getCsku());
				}
				//同步记录存在，并且已经同步成功，那么不需要初始化
				if(isr != null && isr.getIsActive() == 1){
					skusSynced.add(warehouse.getWarehouseId() + ":" + productBase.getCsku());
				}
			}
		}
		Logger.info("============在售SKU总数[{}]============", ProductBases.size());
		Logger.info("============待同步SKU总数[{}]============", skusNeedSync.size());
		for (Warehouse warehouse : validableWarehouses) {
			//根据仓库，查询一组SKU的货箱，获取到期日期数据
			int warehouseId=Integer.parseInt(warehouse.getWarehouseId());
			
			JsonNode resultNode=null;
			//两个外部仓库（福州1仓和杭州1仓）直接查询商品库
			if(warehouseId==2012||warehouseId==2062){
				resultNode = this.externalWarehouseInitInventory(skusNeedSync,warehouse.getWarehouseId());
			}else{//其他仓库先查询带有过期日期接口，如果没有再查询商品接口(0表示需要查)
				resultNode=this.getProductsInventoryDatasFormErp(skusNeedSync,warehouse.getWarehouseId(),0);
			}
			Logger.info("ERP商品库存数据信息-------------->{}", resultNode.toString());
			
			
			//获取每个SKU在查询仓库下的库存明细
			if(resultNode.get("result").size() > 0){
				JsonNode skuStocks = resultNode.get("result");
				for (JsonNode skuStock : skuStocks) {
					//如果这个SKU有待发数，说明这个SKU暂时不能初始化库存，跳过这个SKU
					if(skuStock.get("wait_delivery_count").asInt() > 0){
						skuWithWaitDeliveryCount.add(warehouse.getWarehouseId() + ":" + skuStock.get("sku").asText());
						continue;
					}
					else{//该SKU没有待发数，那么可以同步
						//#1 清除既存的数据
						productInventoryDetailService.deleteBySkuAndWarehouseId(skuStock.get("sku").asText(),Integer.valueOf(warehouse.getWarehouseId()));
						productCloudInventoryService.deleteBySkuAndWarehouseId(skuStock.get("sku").asText(),Integer.valueOf(warehouse.getWarehouseId()));
						
						JsonNode stockByExpirationDates = skuStock.get("expiration");
						
						//总计数
						int tempSum = 0;
						
						//遍历每一个详情数据的库存信息
						for (JsonNode stockByDate : stockByExpirationDates) {
							//#2 添加到云仓明细表
							ProductInventoryDetail newProductInventoryDetail = new ProductInventoryDetail();
							 //到期日期校验
				            if(StringUtils.isNotBlank(stockByDate.get("productionDate").asText())){
			            		try {
									newProductInventoryDetail.setExpirationDate(DateUtils.string2date(stockByDate.get("productionDate").asText(), DateUtils.FORMAT_DATE_PAGE));
				            	} catch (ParseException e) {
				            		newProductInventoryDetail.setExpirationDate(ErpApiUtil.EXPIRATION_DATE);
				            		
									datasHaveErrors.add(warehouse.getWarehouseId() + ":" + skuStock.get("sku").asText());
					            	Logger.info("============SKU[{}],含有异常格式到期日期的库存！设置2116-2-7过期！============", skuStock.get("sku").asText());
									e.printStackTrace();
								}
				            }else{
			            		newProductInventoryDetail.setExpirationDate(ErpApiUtil.EXPIRATION_DATE);
				            	
				            	datasHaveErrors.add(warehouse.getWarehouseId() + ":" + skuStock.get("sku").asText());
				            	Logger.info("============SKU[{}],含有不包含到期日期的库存！设置2116-2-7过期！============", skuStock.get("sku").asText());
				            }
				            
				            newProductInventoryDetail.setSku(skuStock.get("sku").asText());
							newProductInventoryDetail.setWarehouseId(Integer.valueOf(warehouse.getWarehouseId()));
							
							Date now = new Date();
							tempSum += stockByDate.get("leftCount").asInt();
							
							//验证是否存在记录
							ProductInventoryDetail dbDetail = productInventoryDetailService.selectDetail(newProductInventoryDetail);
							if(dbDetail != null){
								//记录存在，添加库存数据
								
								Logger.info("============SKU[{}],返回了多条含有相同到期日期的库存，主动进行合并处理！============", skuStock.get("sku").asText());
								
								dbDetail.setStock(dbDetail.getStock() + stockByDate.get("leftCount").asInt());
								dbDetail.setUpdateTime(now);
								productInventoryDetailService.saveOrUpdate(dbDetail);
								
							}else{
								
					            newProductInventoryDetail.setStock(stockByDate.get("leftCount").asInt());
					            newProductInventoryDetail.setWarehouseName(warehouse.getWarehouseName());
					            newProductInventoryDetail.setCreateTime(now);
					            newProductInventoryDetail.setUpdateTime(now);
					            
					            productInventoryDetailService.saveOrUpdate(newProductInventoryDetail);
								
							}
						}
						
						//#3 将合计数添加到总计表
						ProductInventoryTotal pit = new ProductInventoryTotal();
		                pit.setSku(skuStock.get("sku").asText());
		                pit.setWarehouseId(Integer.valueOf(warehouse.getWarehouseId()));
		                pit.setWarehouseName(warehouse.getWarehouseName());
		                pit.setStock(tempSum);
		                pit.setProductName(productBaseService.getProductTitle(skuStock.get("sku").asText()));
		                Date now = new Date();
		                pit.setCreateTime(now);
		                pit.setUpdateTime(now);
		                productCloudInventoryService.insertSelective(pit);
						
						//#4 生成初始化记录
		                InventorySyncRecord isr = inventorySyncRecordService.selectBySkuAndWarehouseId(skuStock.get("sku").asText(),Integer.valueOf(warehouse.getWarehouseId()));
		                if(isr != null){
		                	isr.setLastSyncingTime(new Date());
							inventorySyncRecordService.updateSelective(isr);
		                }else{
		                	isr = new InventorySyncRecord();
		                	isr.setIsActive(1);
		                	isr.setLastSyncingTime(new Date());
		                	isr.setSku(skuStock.get("sku").asText());
		                	isr.setWarehouseId(Integer.valueOf(warehouse.getWarehouseId()));
							inventorySyncRecordService.insertSelective(isr);
		                }
						
					}
				}
			}
		}
		
		Map<String, Object> resultDatas = new HashMap<String, Object>();
		
		resultDatas.put("withoutProductDateSku", datasHaveErrors);//sku中含有不包含到期日期数据的库存信息的数据，格式“仓库ID：SKU”
		resultDatas.put("validableWarehousesQty", validableWarehouses.size());//目前开启的仓库数量
		resultDatas.put("skuTotalCounts", ProductBases.size());//SKU总数
		resultDatas.put("skuWithWaitDeliveryCount",skuWithWaitDeliveryCount);//含有待发数的SKU，格式“仓库ID：SKU”
		resultDatas.put("syncedSkuThisTime", skusNeedSync);//此次同步的SKU，格式“SKU”
		resultDatas.put("syncedSkuQtyThisTime", skusNeedSync.size());//此次同步的SKU个数
		resultDatas.put("skusSyncedBefore", skusSynced);//此前已同步过的SKU，格式“仓库ID：SKU”
		Logger.info("云仓初始化结果------------------->{}", resultDatas.toString());
		return ok(Json.toJson(resultDatas));
	}

	private JsonNode getProductsInventoryDatasFormErp(Set<String> skusNeedSync, String warehouseId,int type) {
		List<JsonNode> productInventoryFromErpDatas=Lists.newArrayList();
		Map<String,Object> result=Maps.newHashMap();
		List<String> requestDataFromErpExpiration=Lists.newArrayList();
		List<String> requestDataFromProudctFlat=Lists.newArrayList();
		for(String sku:skusNeedSync){
			Map<String, Object> paramMap = new HashMap<String, Object>();
			List<String> skus=Lists.newArrayList();
			skus.add(sku);
			paramMap.put("skus", skus);
			paramMap.put("stock_id", warehouseId);
			paramMap.put("timestamp", 0);
			
			String resultStrBoxInfo = HttpUtil.post(
					Json.toJson(paramMap).toString(),
					ErpApiUtil.ERP_HOST + ErpApiUtil.STOCK_INIT_API + "?api_key="
							+ ErpApiUtil.ERP_API_KEY);
			JsonNode resultNode = Json.parse(resultStrBoxInfo);
			if(resultNode.get("result")==null||resultNode.get("result").size()<=0){//查询到期日期接口没有该sku数据
				if(type==1){//当type为1时不查商品接口
					continue;
				}
				Set<String> skuParam=new HashSet<>();
				skuParam.add(sku);
				resultNode=this.externalWarehouseInitInventory(skuParam, warehouseId);
				if(resultNode.get("result")==null||resultNode.get("result").size()<=0){
					continue;
				}else {
					productInventoryFromErpDatas.add(resultNode.get("result").get(0));
					String requParam=sku;
					requestDataFromProudctFlat.add(requParam);
				}
			}else{
				productInventoryFromErpDatas.add(resultNode.get("result").get(0));
				String requParam=sku;
				requestDataFromErpExpiration.add(requParam);
			}
		}
		Logger.info("云仓初始化数据请求Expiration参数 warehouseId[{}],skuList[{}]", warehouseId,requestDataFromErpExpiration.toString());
		Logger.info("云仓初始化数据请求ProductFlatList参数warehouseId[{}],skuList[{}]", warehouseId,requestDataFromProudctFlat.toString());
		result.put("result", productInventoryFromErpDatas);
		return Json.toJson(result);
	}

	/**
	 * 外部仓库处理
	 * @param paramMap
	 * @return
	 */
	private JsonNode externalWarehouseInitInventory(Set<String> skusNeedSync, String warehouseId) {
		//Logger.info("商品库数据仓库id[{}]",warehouseId);
		Map<String,Object> externalDatas=Maps.newHashMap();
		List<Map<String,Object>> resultDatas=Lists.newArrayList();
		//查询仓库参数
		for(String sku:skusNeedSync){
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("sku", sku);
			paramMap.put("stock_id", warehouseId);
			paramMap.put("timestamp", 0);
			String resultStrBoxInfo = HttpUtil.post(
					Json.toJson(paramMap).toString(),
					ErpApiUtil.ERP_HOST + ErpApiUtil.EXTERNAL_WAREHOUSE_API + "?api_key="
							+ ErpApiUtil.ERP_API_KEY);
			JsonNode resultNode = Json.parse(resultStrBoxInfo);
			if(resultNode.get("result").size() > 0){
				JsonNode skuStock = resultNode.get("result").get(0);
				int wareId = skuStock.get("stock_id").asInt();
				Map<String,Object> expirationAndStock=Maps.newHashMap();
				List<Map<String,Object>> expirationDatas=Lists.newArrayList();
				expirationAndStock.put("productionDate", ErpApiUtil.EXPIRATION_STR);
				if(wareId==2012){
					if(ErpApiUtil.EXTERNAL_WAREHOUSE_STOCK!=-1){//有配置杭州仓2012的初始化数量
						expirationAndStock.put("leftCount", ErpApiUtil.EXTERNAL_WAREHOUSE_STOCK);
					}else{//没有配置杭州仓2012的初始化数量
						if(skuStock.get("available_stock_count")!=null){
							expirationAndStock.put("leftCount", skuStock.get("available_stock_count").asInt());
						}else{
							expirationAndStock.put("leftCount", 0);
						}
						
					}
				}else if(wareId==2062){
					if(skuStock.get("available_stock_count")!=null){
						expirationAndStock.put("leftCount", skuStock.get("available_stock_count").asInt());
					}else{
						expirationAndStock.put("leftCount", 0);
					}
				}else{
					expirationAndStock.put("leftCount", skuStock.get("available_stock_count").asInt());
				}
				expirationDatas.add(expirationAndStock);
				
				Map<String,Object> tempDate=Maps.newHashMap();
				tempDate.put("wait_delivery_count", 0);	
				tempDate.put("sku", skuStock.get("sku").asText());
				tempDate.put("expiration", expirationDatas);
				
				resultDatas.add(tempDate);
			}
		}
		externalDatas.put("result", resultDatas);
		return Json.toJson(externalDatas);
	}
	
	public Result inintNewProduct(){
		//是否开启 category 为Tooarts(8892) Tomfeel(8678) 特殊处理功能(将仓库7保存为2024)
		Configuration config = Play.application().configuration().getConfig("inventory");
		int specialDispose = config.getInt("TooartsAndTomFeel")==null?0:config.getInt("TooartsAndTomFeel");
		String spcialCategory=config.getString("specialDisposeCategory");
		String[] split = spcialCategory.split("_");
		List<String> spcialCategoryList=Arrays.asList(split);
		Integer specialWarehouseId=2024;
		String spceialWarehouseName="深圳仓";
		int specialFlag=0;
		
		Map<String,Object> result = Maps.newHashMap();
		List<String> skuWithWaitDeliveryCount = new ArrayList<String>();
		List<String> datasHaveErrors = new ArrayList<String>();
		JsonNode reqParam = request().body().asJson();
		Logger.info("初始化指定商品----->[{}]", reqParam);
		
		if(reqParam==null||!reqParam.has("sku")||!reqParam.has("warehouseId")||!reqParam.has("operator")){
			result.put("result", 1);
			result.put("msg", "请校验参数是否正确!");
			return ok(Json.toJson(result));
		}
		int checkOrderFlag=1;//是否校验处于流转中的订单
		if(reqParam.has("checkOrderFlag")){
			checkOrderFlag=reqParam.get("checkOrderFlag").asInt();
		}
		int warehouseId = reqParam.get("warehouseId").asInt();
		String operator=reqParam.get("operator").asText();
		String warehouseName="";
		List<Warehouse> queryWarehouse = warehInvenService.queryWarehouse(warehouseId,true,null);
		if(queryWarehouse!=null && queryWarehouse.size()>0){
			warehouseName = queryWarehouse.get(0).getWarehouseName();
		}
		String sku = reqParam.get("sku").asText();
		//判断是同步过
		InventorySyncRecord checkSyncRecord = inventorySyncRecordService.selectBySkuAndWarehouseId(sku,warehouseId);
		if(checkSyncRecord!=null){
			result.put("result", 1);
			result.put("msg", "该sku已存在同步记录,请检查！");
			return ok(Json.toJson(result));
		}
		try {
			Set<String> skusNeedSync = new HashSet<String>();
			skusNeedSync.add(sku);
			JsonNode resultNode=null;
			if(warehouseId==2012||warehouseId==2062){
				resultNode= this.externalWarehouseInitInventory(skusNeedSync,warehouseId+"");
			}else{
				Set<String> skus=new HashSet<>();
				skus.add(sku);
				resultNode = this.getProductsInventoryDatasFormErp(skus, warehouseId+"",0);
			}
			Logger.info("初始化指定商品erp返回数据:{}",resultNode.toString());
			if(resultNode.get("result").size() > 0){
				JsonNode skuStocks = resultNode.get("result");
				for (JsonNode skuStock : skuStocks) {
					
					//如果这个SKU有待发数，说明这个SKU暂时不能初始化库存，跳过这个SKU
					if(skuStock.get("wait_delivery_count").asInt() > 0){
						skuWithWaitDeliveryCount.add(warehouseId + ":" + skuStock.get("sku").asText());
						continue;
					}
					else{
						if(checkOrderFlag==1){//检查是否有已微仓出库但没有流转至HK的订单
							InitProductInventoryResult checkOrderResult = inventorySyncRecordService.checkOrderExistHKBySku(sku,warehouseId);
							if(!checkOrderResult.isResult()){
								result.put("result", 1);
								result.put("msg", checkOrderResult.getMsg());
								return ok(Json.toJson(result));
							}
						}
						//校验微仓库存囤货数量,
						String s_skuStock = skuStock.toString();
						InitProductInventoryResult checkMicroInventoryResult  = inventorySyncRecordService.checkMicroInventoryStockpile(s_skuStock,Integer.valueOf(warehouseId));
						if(!checkMicroInventoryResult .isResult()){
							result.put("result", 1);
							result.put("msg", checkMicroInventoryResult .getMsg());
							return ok(Json.toJson(result));
						}
						//过期日期对应的微仓囤货数量
						Map<String, Integer> expirationWithNum = checkMicroInventoryResult .getExpirationWithNum();
						//该SKU没有待发数，微仓库存囤货校验通过那么可以同步，同步数量为erp库存数量-微仓囤货数量
						//#1 清除既存的数据
						productInventoryDetailService.deleteBySkuAndWarehouseId(skuStock.get("sku").asText(),Integer.valueOf(warehouseId));
						productCloudInventoryService.deleteBySkuAndWarehouseId(skuStock.get("sku").asText(),Integer.valueOf(warehouseId));
						
						JsonNode stockByExpirationDates = skuStock.get("expiration");
						
						//总计数
						int tempSum = 0;
						
						//遍历每一个详情数据的库存信息
						for (JsonNode stockByDate : stockByExpirationDates) {
							//#2 添加到云仓明细表
							ProductInventoryDetail newProductInventoryDetail = new ProductInventoryDetail();
							 //到期日期校验
				            if(StringUtils.isNotBlank(stockByDate.get("productionDate").asText())){
			            		try {
									newProductInventoryDetail.setExpirationDate(DateUtils.string2date(stockByDate.get("productionDate").asText(), DateUtils.FORMAT_DATE_PAGE));
				            	} catch (ParseException e) {
				            		newProductInventoryDetail.setExpirationDate(ErpApiUtil.EXPIRATION_DATE);
									datasHaveErrors.add(warehouseId + ":" + skuStock.get("sku").asText());
					            	Logger.info("============SKU[{}],含有异常格式到期日期的库存！设置2116-2-7过期！============", skuStock.get("sku").asText());
									e.printStackTrace();
								}
				            }else{
			            		newProductInventoryDetail.setExpirationDate(ErpApiUtil.EXPIRATION_DATE);
				            	datasHaveErrors.add(warehouseId + ":" + skuStock.get("sku").asText());
				            	Logger.info("============SKU[{}],含有不包含到期日期的库存！设置2116-2-7年以后过期！============", skuStock.get("sku").asText());
				            }
				            if(specialDispose==1){//开启了特殊处理
				            	//验证该sku是否是 category 为Tooarts(8892)或者 Tomfeel(8678)
				            	List<Integer> categoryList=Lists.newArrayList();
				            	categoryList=productBaseService.getProductCategoryBySku(skuStock.get("sku").asText());
				            	for(int index=0;index<categoryList.size();index++){
				            		  String category = categoryList.get(index).toString();
				            		if(spcialCategoryList.contains(category)){//属于特殊商品
				            			if(warehouseId==7){//还属于仓库7(SZ1)
				            				specialFlag=1;
				            				//清除掉仓库id=2024，sku=XXX的商品
				            				productInventoryDetailService.deleteBySkuAndWarehouseId(skuStock.get("sku").asText(),2024);
				    						productCloudInventoryService.deleteBySkuAndWarehouseId(skuStock.get("sku").asText(),2024);
				            			}
				            		}
				            	}
				            }
				            newProductInventoryDetail.setSku(skuStock.get("sku").asText());
				            if(specialFlag==1){
				            	newProductInventoryDetail.setWarehouseId(specialWarehouseId);
				            }else{
				            	newProductInventoryDetail.setWarehouseId(warehouseId);
				            }
							
				            int erpStock = stockByDate.get("leftCount").asInt();
				            String tempDate = DateUtils.date2string(newProductInventoryDetail.getExpirationDate(), DateUtils.FORMAT_DATE_PAGE);
				            int microStockpileNumWithExpirationdate = expirationWithNum.get(tempDate)==null?0:expirationWithNum.get(tempDate);
				            int syncNum=erpStock-microStockpileNumWithExpirationdate;//同步的数量
							Date now = new Date();
							tempSum +=syncNum ;
							
							//验证是否存在记录
							ProductInventoryDetail dbDetail = productInventoryDetailService.selectDetail(newProductInventoryDetail);
							if(dbDetail != null){
								//记录存在，添加库存数据
								
								Logger.info("============SKU[{}],返回了多条含有相同到期日期的库存，主动进行合并处理！============", skuStock.get("sku").asText());
								
								dbDetail.setStock(dbDetail.getStock() + syncNum);
								dbDetail.setUpdateTime(now);
								productInventoryDetailService.saveOrUpdate(dbDetail);
								
							}else{
								
					            newProductInventoryDetail.setStock(syncNum);
					            if(specialFlag==1){
					            	newProductInventoryDetail.setWarehouseName(spceialWarehouseName);
					            }else{
					            	newProductInventoryDetail.setWarehouseName(warehouseName);
					            }
					            newProductInventoryDetail.setCreateTime(now);
					            newProductInventoryDetail.setUpdateTime(now);
					            
					            productInventoryDetailService.saveOrUpdate(newProductInventoryDetail);
								
							}
						}
						
						//#3 将合计数添加到总计表
						ProductInventoryTotal pit = new ProductInventoryTotal();
			            pit.setSku(skuStock.get("sku").asText());
			            if(specialFlag==1){
			            	pit.setWarehouseId(specialWarehouseId);
				            pit.setWarehouseName(spceialWarehouseName);
			            }else{
			            	pit.setWarehouseId(Integer.valueOf(warehouseId));
				            pit.setWarehouseName(warehouseName);
			            }
			            pit.setStock(tempSum);
			            pit.setProductName(productBaseService.getProductTitle(skuStock.get("sku").asText()));
			            Date now = new Date();
			            pit.setCreateTime(now);
			            pit.setUpdateTime(now);
			            ProductInventoryTotal totalResult= productCloudInventoryService.getProductBySkuAndWarehouseId(pit);
			            if(totalResult !=null){
			            	totalResult.setStock(totalResult.getStock()+pit.getStock());
			            	totalResult.setUpdateTime(new Date());
			            	productCloudInventoryService.updateProductTotal(totalResult);
			            }else{
			            	 productCloudInventoryService.insertSelective(pit);
			            }
			           
						
						//#4 生成初始化记录
			            InventorySyncRecord isr = inventorySyncRecordService.selectBySkuAndWarehouseId(skuStock.get("sku").asText(),warehouseId);
			            if(isr != null){
			            	isr.setOperator(operator);
			            	isr.setSyncingNum(tempSum);
			            	isr.setLastSyncingTime(new Date());
							inventorySyncRecordService.updateSelective(isr);
			            }else{
			            	isr = new InventorySyncRecord();
			            	isr.setOperator(operator);
			            	isr.setSyncingNum(tempSum);
			            	isr.setIsActive(1);
			            	isr.setLastSyncingTime(new Date());
			            	isr.setSku(skuStock.get("sku").asText());
			            	isr.setWarehouseId(Integer.valueOf(warehouseId));
							inventorySyncRecordService.insertSelective(isr);
			            }
					}
				}
			}else{
				result.put("result", 1);
				result.put("msg", "该商品在此仓库无相关数据");
				return ok(Json.toJson(result));
			}
			result.put("result", 0);
			result.put("msg", "新商品初始化成功！");
			if(skuWithWaitDeliveryCount.size()>0){
				result.put("result", 1);
				result.put("msg", "商品存在代发数，无法进行初始化！");
			}
		} catch (Exception e) {
			Logger.info("inintNewProductError[{}]--------->", e);
			result.put("result", 1);
			result.put("msg", "新商品初始化发生异常！");
		}
		return ok(Json.toJson(result));
	}
	
	public Result checkInitRecord(String sku){
		Map<String,Object> result=Maps.newHashMap();
		List<ProductBase> productList = productBaseService.getProductsInSalesBySku(sku);
		if(productList==null||productList.size()<=0){
			result.put("result", 1);
			result.put("msg", "该sku在出售的商品中查询不到！");
			return ok(Json.toJson(result));
		}
		ProductBase productBase = productList.get(0);
		
		List<Warehouse> warehouseList = warehInvenService
				.queryWarehouse(null,true,null);
		
		List<InventorySyncRecord> syncData=Lists.newArrayList();
	
		for(int i=0;i<warehouseList.size();i++){
			Warehouse warehouse = warehouseList.get(i);
			InventorySyncRecord isr = inventorySyncRecordService.selectBySkuAndWarehouseId(productBase.getCsku(),Integer.valueOf(warehouse.getWarehouseId()));
			if(isr!=null){
				syncData.add(isr);
			}else{
				InventorySyncRecord iSyncRecord=new InventorySyncRecord();
				iSyncRecord.setSku(productBase.getCsku());
				iSyncRecord.setWarehouseId(Integer.valueOf(warehouse.getWarehouseId()));
				syncData.add(iSyncRecord);
			}
		}
		result.put("result", 0);
		result.put("syncdata", syncData);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 云仓库存存在问题的sku查找：
	 * 云仓库存小于0;
	 * 云仓明细与总数对应不上;
	 * @return
	 */
	public Result cloudInventoryCheck(){
		String resultStr = inventorySyncRecordService.cloudInventoryCheck();
		JsonNode resultNode = Json.parse(resultStr);
		return ok(resultNode);
	}
	
	/**
	 * 查询BBC库存比erp多的商品数据
	 * @return
	 */
	public Result checkBBCInventoryCompareToErp(){
		Map<String,Object> result=Maps.newHashMap();
		List<Map<String,Object>> checkFailedList=Lists.newArrayList();
		List<Map<String,Object>> checkSuccessList=Lists.newArrayList();
		List<Map<String,Object>> abileSyncList=Lists.newArrayList();
		List<Map<String,Object>> erpUnequalBBC=Lists.newArrayList();
		List<Map<String,Object>> allDataList=Lists.newArrayList();
		String wareId = request().getQueryString("warehouseId");
		List<Warehouse> validableWarehouses=Lists.newArrayList();
		if(wareId!=null){
			Warehouse warehouse=new Warehouse();
			warehouse.setWarehouseId(wareId+"");
			validableWarehouses.add(warehouse);
		}else{
			//所有启用仓库
			validableWarehouses = warehInvenService
					.queryWarehouse(null, null, null);
		}
		for (Warehouse warehouse : validableWarehouses) {
			Integer warehouseId = Integer.valueOf(warehouse.getWarehouseId());
			//查询该仓库下所有商品
			List<ProductInventoryTotal> cloudInventoryLists=inventorySyncRecordService.getCloudInventoryByWarehouseId(warehouseId);
			for(ProductInventoryTotal tempTotal:cloudInventoryLists){
				String sku=tempTotal.getSku();
				Map<String,Object> tempMap=Maps.newHashMap();
				tempMap.put("sku", sku);
				tempMap.put("warehouseId", warehouseId);
				Set<String> skusNeedSync = new HashSet<String>();
				skusNeedSync.add(tempTotal.getSku());
				JsonNode erpDataNode=null;
				if(warehouseId==2012||warehouseId==2062){
					erpDataNode = this.externalWarehouseInitInventory(skusNeedSync,warehouse.getWarehouseId());
				}else{//其他仓库先查询带有过期日期接口 只查过期日期接口
					erpDataNode=this.getProductsInventoryDatasFormErp(skusNeedSync,warehouse.getWarehouseId(),1);
				}
				Logger.info("ERP商品库存数据信息-------------->{}", erpDataNode.toString());
				//查询erp可用数
				ProductMicroInventoyResult erpDataFormatResult= this.erpDataFormat(erpDataNode,tempTotal.getSku(),warehouseId);
				if(!erpDataFormatResult.getResult()){
					Logger.info("erp查询可用数失败：原因{}", erpDataFormatResult.getMsg());
					checkFailedList.add(tempMap);
					continue;
				}
				int erpLeftCount=0;
				for(Object tempObj: erpDataFormatResult.getEntityList()){
					ProductInventoryDetail d=(ProductInventoryDetail) tempObj;
					erpLeftCount+=d.getStock();
				}
				//不指定到期日期商品在微仓中的囤货
				int microStockpileTotal = inventorySyncRecordService.getMicroStockpileTotal(tempTotal.getSku(), warehouseId);
				
				//审核中订单占用
				ProductMicroInventoyResult orderOccupyResult=inventorySyncRecordService.getOrderOccupyNumBySkuAndWarehouseId(tempTotal.getSku(),warehouseId);
				if(!orderOccupyResult.getResult()){
					Logger.info("查询流转中订单占用库存失败：原因{}", orderOccupyResult.getMsg());
					checkFailedList.add(tempMap);
					continue;
				}
				int orderOccupyNum= (int) orderOccupyResult.getEntity();
				//已销售订单数量
				int salesNum=orderOccupyNum+microStockpileTotal;
				Map<String,Object> allDataTemp=Maps.newHashMap();
				allDataTemp.put("sku", sku);
				allDataTemp.put("warehouseId", warehouseId);
				allDataTemp.put("microStockpile", microStockpileTotal);
				allDataTemp.put("orderOccupy", orderOccupyNum);
				allDataTemp.put("erpData", erpLeftCount);
				allDataTemp.put("cloudStock", tempTotal.getStock());
				allDataList.add(allDataTemp);
				if(salesNum>erpLeftCount){
					Map<String,Object> succesTemp=Maps.newHashMap();
					succesTemp.put("sku", sku);
					succesTemp.put("warehouseId", warehouseId);
					checkSuccessList.add(succesTemp);
				}else if(salesNum+tempTotal.getStock() != erpLeftCount){
					Map<String,Object> unequalTemp=Maps.newHashMap();
					unequalTemp.put("sku", sku);
					unequalTemp.put("warehouseId", warehouseId);
					erpUnequalBBC.add(unequalTemp);
				}else{
					Map<String,Object> abileTemp=Maps.newHashMap();
					abileTemp.put("sku", sku);
					abileTemp.put("warehouseId", warehouseId);
					abileSyncList.add(tempMap);
				}
			}
		}
		result.put("checkFailedList", checkFailedList);
		result.put("checkSuccessList", checkSuccessList);
		result.put("erpUnequalBBC", erpUnequalBBC);
		result.put("abileSyncList", abileSyncList);
		result.put("allDataList", allDataList);
		Logger.info("库存清点结果{}", Json.toJson(result).toString());
		return ok(Json.toJson(result));
	}
	
	/**
	 * 将云仓库存设置为0
	 * @return
	 */
	public Result setCloudInventory(){
		Map<String,Object> result=Maps.newHashMap();
		JsonNode reqParam = request().body().asJson();
		if(reqParam==null || reqParam.size()<=0){
			result.put("result", 1);
			result.put("msg", "参数信息为空");
			return ok(Json.toJson(result));
		}
		String reqStr=reqParam.toString();
		String resultStr=inventorySyncRecordService.setCloudInventory(reqStr);
		JsonNode resultNode = Json.parse(resultStr);
		return ok(resultNode);
	}

	/**
	 * BBC库存和erp库存对比
	 * @return
	 */
	public Result cloudInventorySyncing(){
		Map<String,Object> result=Maps.newHashMap();
		JsonNode paramNode = request().body().asJson();
		List<ProductMicroInventoyResult> failedLists=Lists.newArrayList();
		List<ProductMicroInventoyResult> successLists=Lists.newArrayList();
		if(paramNode==null || paramNode.size()<=0){
			result.put("result", 1);
			result.put("msg", "参数为空");
			return ok(Json.toJson(result));
		}
		for(JsonNode tempNode:paramNode){
			String sku=JsonCaseUtil.jsonCase(tempNode, "sku", Types.STR);
			int warehouseId=JsonCaseUtil.jsonCase(tempNode, "warehouseId", Types.INT);
			Set<String> skusNeedSync = new HashSet<String>();
			skusNeedSync.add(sku);
			JsonNode resultNode=null;
			if(warehouseId==2012||warehouseId==2062){
				resultNode= this.externalWarehouseInitInventory(skusNeedSync,warehouseId+"");
			}else{
				Set<String> skus=new HashSet<>();
				skus.add(sku);
				resultNode = this.getProductsInventoryDatasFormErp(skus, warehouseId+"",1);
			}
			Logger.info("sku={},warehouseId={},erp返回数据:{}",sku,warehouseId,resultNode.toString());
			ProductMicroInventoyResult erpDataFormatResult= this.erpDataFormat(resultNode,sku,warehouseId);
			if(!erpDataFormatResult.getResult()){
				failedLists.add(erpDataFormatResult);
				continue;
			}
			ProductMicroInventoyResult comparisonResult=this.erpComparisonToBBC(erpDataFormatResult.getEntityList());
			if(!comparisonResult.getResult()){
				failedLists.add(comparisonResult);
				continue;
			}
			//更新云仓数据
			List inventoryDetailDataLists = comparisonResult.getEntityList();
			ProductMicroInventoyResult inventoryUpdateResult=inventorySyncRecordService.updateCloudInventory(inventoryDetailDataLists);
			if(!inventoryUpdateResult.getResult()){
				failedLists.add(inventoryUpdateResult);
			}else{
				successLists.add(inventoryUpdateResult);
			}
		}
		result.put("failedLists", failedLists);
		result.put("successLists", successLists);
		JsonNode resultJson = Json.toJson(result);
		Logger.info("同步erp库存结果", resultJson.toString());
		return ok(Json.toJson(result));
	}

	private ProductMicroInventoyResult erpComparisonToBBC(List inventoryDetailLists) {
		//比较结果
		List<ProductInventoryDetail> resultLists=Lists.newArrayList();
		for(Object tempData:inventoryDetailLists){
			ProductInventoryDetail erpDataDetail=(ProductInventoryDetail)tempData;
			List<Warehouse> queryWarehouse = warehInvenService.queryWarehouse(erpDataDetail.getWarehouseId(), null, null);
			erpDataDetail.setWarehouseName(queryWarehouse.get(0).getWarehouseName());
			Integer erpStock = erpDataDetail.getStock();
			String sku = erpDataDetail.getSku();
			Integer warehouseId = erpDataDetail.getWarehouseId();
			Date expirationDate = erpDataDetail.getExpirationDate();
			ProductInventoryDetail paramDetail=new ProductInventoryDetail(sku,warehouseId,expirationDate);
			//BBC云仓库存
			ProductInventoryDetail inventoryDetailResult  = inventorySyncRecordService.getInventoryDetailByParam(paramDetail);
			if(inventoryDetailResult == null){
				resultLists.add(erpDataDetail);
				continue;
			}
			Integer inventoryStock = inventoryDetailResult.getStock();
			//BBC微仓囤货
			int microStockpileTotal = inventorySyncRecordService.getMicroStockpileTotalWithExpirdationDate(paramDetail);
			//审核中订单占用
			ProductMicroInventoyResult orderOccupyResult=inventorySyncRecordService.getOrderOccupyNum(paramDetail);
			if(!orderOccupyResult.getResult()){
				return orderOccupyResult;
			}
			int orderOccupyNum= (int) orderOccupyResult.getEntity();
			//已售出库存
			int BBCStock=microStockpileTotal+orderOccupyNum;
			if(erpStock>=BBCStock){
				erpDataDetail.setStock(erpStock-BBCStock);
				resultLists.add(erpDataDetail);
			}else{
				return new ProductMicroInventoyResult(false,"sku="+sku+",warehouseId="+warehouseId+",expirationDate="+DateUtils.date2string(expirationDate, DateUtils.FORMAT_DATE_PAGE)+",BBC已销售数量大于erp可用数量",null,null);
			}
		}
		return new ProductMicroInventoyResult(true,null,null,resultLists);
	}

	private ProductMicroInventoyResult erpDataFormat(JsonNode resultNode,String sku, Integer warehouseId) {
		if(resultNode==null ||!resultNode.has("result") || resultNode.get("result").size() <= 0){
			return new ProductMicroInventoyResult(false,"erp无sku="+sku+",warehouseId="+warehouseId+"相关数据",null,null);
		}
		//待发数信息所涉及的商品日期
		List<OrderMicroInventoryDeductRecord> microDeductRecordLists=Lists.newArrayList();
		//erp可用数据信息
		List<ProductInventoryDetail> inventoryDetailList=Lists.newArrayList();
		
		JsonNode erpInventoryNode = resultNode.get("result");
		for(JsonNode tempNode:erpInventoryNode){
			int waitDeliveryCount = tempNode.get("wait_delivery_count").asInt();
			
			if(waitDeliveryCount>0){//有代发数
				JsonNode orderNoNodes = tempNode.get("itemIds");
				if(orderNoNodes==null || orderNoNodes.size()<=0){
					Map<String,Object> tempMap=Maps.newHashMap();
					tempMap.put("sku", sku);
					tempMap.put("warehouseId", warehouseId);
					return new ProductMicroInventoyResult(false,"erp数据缺少sku="+sku+",warehouseId="+warehouseId+"待发数相关订单信息",tempMap,null);
				}
				List<String> orderNoLists=Lists.newArrayList();
				for(JsonNode tempOrderNode:orderNoNodes){
					String orderNo = tempOrderNode.asText();
					orderNoLists.add(orderNo);
				}
				microDeductRecordLists = inventorySyncRecordService.getMicroDeductRecordByOrderNoAndSku(orderNoLists, sku,null);
				int countNum=0;
				if(microDeductRecordLists==null || microDeductRecordLists.size()<=0){
					Map<String,Object> tempMap=Maps.newHashMap();
					tempMap.put("sku", sku);
					tempMap.put("warehouseId", warehouseId);
					return new ProductMicroInventoyResult(false,"微仓出库记录查询不到sku="+sku+",warehouseId="+warehouseId+"待发数相关的订单信息",tempMap,null);
				}else{
					for(OrderMicroInventoryDeductRecord tempRecord:microDeductRecordLists){
						countNum+=tempRecord.getQty();
					}
				}
				if(countNum != waitDeliveryCount){//微仓出库数量与代发数不相等
					Map<String,Object> tempMap=Maps.newHashMap();
					tempMap.put("sku", sku);
					tempMap.put("warehouseId", warehouseId);
					return new ProductMicroInventoyResult(false,"sku="+sku+",warehouseId="+warehouseId+"微仓出库记录订单数量与待发数不匹配",tempMap,null);
				}
			}
			
			JsonNode expirationDataNode = tempNode.get("expiration");
			if(expirationDataNode==null ||expirationDataNode.size()<=0){
				Map<String,Object> tempMap=Maps.newHashMap();
				tempMap.put("sku", sku);
				tempMap.put("warehouseId", warehouseId);
				return new ProductMicroInventoyResult(false,"sku="+sku+",warehouseId="+warehouseId+",erp缺少商品到期日期信息",tempMap,null);
			}
			
			for(JsonNode tempExpirationNode:expirationDataNode){
				ProductInventoryDetail inventoryDetail=new ProductInventoryDetail();
				String expirationStr = tempExpirationNode.get("productionDate").asText(ErpApiUtil.EXPIRATION_STR);
				int leftCount = tempExpirationNode.get("leftCount").asInt(0);
				Date expirationDate=null;
				try {
					expirationDate=DateUtils.string2date(expirationStr, DateUtils.FORMAT_DATE_PAGE);
				} catch (ParseException e) {
					Logger.info("============SKU[{}],含有异常格式到期日期的库存！设置2116-02-07过期！============", sku);
					expirationDate=ErpApiUtil.EXPIRATION_DATE;
				}
				inventoryDetail.setSku(sku);
				inventoryDetail.setWarehouseId(warehouseId);
				inventoryDetail.setExpirationDate(expirationDate);
				inventoryDetail.setStock(leftCount);
				inventoryDetailList.add(inventoryDetail);
			}
		}
		Map<Date,Integer> waitDetailMap=Maps.newHashMap();
		if(!microDeductRecordLists.isEmpty()){
			for(OrderMicroInventoryDeductRecord tempRecord:microDeductRecordLists){
				Date dateKey = tempRecord.getExpirationDate();
				Integer qty = tempRecord.getQty();
				if(waitDetailMap.get(dateKey)!=null){
					waitDetailMap.put(dateKey, waitDetailMap.get(dateKey)+qty);
				}else{
					waitDetailMap.put(dateKey, qty);
				}
			}
		}
		
		for(ProductInventoryDetail tempDetail:inventoryDetailList){
			Date dateKey = tempDetail.getExpirationDate();
			int orderSalesNum=0;
			if(waitDetailMap.get(dateKey)!=null){
				orderSalesNum=waitDetailMap.get(dateKey);
			}
			if(tempDetail.getStock()<orderSalesNum){
				return new ProductMicroInventoyResult(false,"sku="+sku+",warehouseId="+warehouseId+"expirationDate="+DateUtils.date2string(dateKey, DateUtils.FORMAT_DATE_PAGE)+",微仓出库记录订单销售数量与待发数不匹配",null,null);
			}
			tempDetail.setStock(tempDetail.getStock()-orderSalesNum);
		}
		return new ProductMicroInventoyResult(true,null,null,inventoryDetailList);
	}
	
	public Result physicalInventoryStock(){
		//触发盘点任务
		InjectorInstance.getInstance(EventBus.class).post(new PhysicalInventoryEvent());
		return ok("库存盘点任务运行中...稍后请查看日志输出盘点信息！");
	}
	
	public Result checkInventoryStock(){
		//触发盘点任务
		InjectorInstance.getInstance(EventBus.class).post(new CheckInventoryEvent());
		return ok("库存清点任务运行中...稍后请查看日志输出盘点信息！");
	}
	
	public Result getInventoryPhysicalResult(){
		String time = request().getQueryString("time");
		Map<String,Object> result=Maps.newHashMap();
		InventoryCommonResult<String> downLoadPhysicalResult = inventorySyncRecordService.downLoadPhysicalResult(time);
		if(downLoadPhysicalResult.getResultCode()==1){
			result.put("suc", 1);
			result.put("msg", downLoadPhysicalResult.getMsg());
			return ok(Json.toJson(result));
		}
		String data = downLoadPhysicalResult.getData();
		JsonNode resultNode= Json.parse(data);
		result.put("suc", 0);
		result.put("data", resultNode);
		response().setHeader("Access-Control-Allow-Origin", "*");
		return ok(Json.toJson(result));
	}
	
	public Result downloadInventoryCheckResult(){
		String time = request().getQueryString("time");
		Map<String,Object> result=Maps.newHashMap();
		try{
			byte[] downLoadPhysicalResult = inventorySyncRecordService.downLoadCheckResult(time);
			response().setHeader("Content-disposition", "attachment;filename="+time+".xls");
			response().setContentType("application/octet-stream");
			return ok(downLoadPhysicalResult);
		}catch (Exception e) {
			Logger.info("下载库存清点报告发生异常{}", e);
			return ok("下载库存清点报告发生异常!");
		}
	}
}
