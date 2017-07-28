package handlers.inventory;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.elasticsearch.common.collect.Maps;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import dto.product_inventory.InventoryCommonResult;
import dto.product_inventory.InventoryPhysicalResult;
import dto.product_inventory.ProductMicroInventoyResult;
import entity.product_inventory.ProductInventoryDetail;
import entity.product_inventory.ProductInventoryTotal;
import events.product_inventory.PhysicalInventoryEvent;
import mapper.product_inventory.ProductInventoryDetailMapper;
import mapper.product_inventory.ProductInventoryTotalMapper;
import mapper.product_inventory.ProductMicroInventoryInRecordMapper;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import services.product_inventory.IinventorySyncRecordService;

/**
 * 每日统计云仓库存数据
 * 输出相关不合理的sku库存信息
 * @author lenovo
 *
 */
public class PhysicalInventoryHandler {
	
	@Inject
	private IinventorySyncRecordService inventorySyncRecordService;
	
	@Inject
	private ProductInventoryDetailMapper cloudInventoryDetailMapper;
	
	@Inject
	private ProductMicroInventoryInRecordMapper microInventoryInRecordMapper;
	
	@Inject
	private ProductInventoryTotalMapper inventoryTotalMapper;
	
	/**
	 * 
	 * @param event
	 */
	@Subscribe
	public void physicalAllBBCInventory(PhysicalInventoryEvent event){
		Map<String,Object> result=Maps.newHashMap();
		List<InventoryPhysicalResult> success=Lists.newArrayList();
		List<InventoryPhysicalResult> erpLackData=Lists.newArrayList();
		List<InventoryPhysicalResult> fail=Lists.newArrayList();
		try{
			//查询云仓所有商品
			List<ProductInventoryTotal> list = inventoryTotalMapper.getAll();
			//和erp数据进行对比
			for(ProductInventoryTotal tempCloudTotal:list){
				Integer warehouseId=tempCloudTotal.getWarehouseId();
				String sku=tempCloudTotal.getSku();
				try{
					InventoryCommonResult<JsonNode> erpInventoryDataResult = inventorySyncRecordService.getErpExpirationInventoryData(sku, warehouseId, 0);
					if(erpInventoryDataResult.getResultCode().intValue()==1){
						InventoryPhysicalResult tempResult=new InventoryPhysicalResult<>(sku,warehouseId,erpInventoryDataResult.getMsg());
						erpLackData.add(tempResult);
						continue;
					}
					JsonNode erpInventoryData = erpInventoryDataResult.getData();
					
					//解析erp返回数据
					String tempErpDataStr = erpInventoryData.toString();
					InventoryCommonResult<List<ProductInventoryDetail>> erpInventoryDetailResult=inventorySyncRecordService.formatErpInventoryData(tempErpDataStr,sku,warehouseId);
					if(erpInventoryDetailResult.getResultCode().intValue()==1){
						InventoryPhysicalResult tempResult=new InventoryPhysicalResult<>(sku,warehouseId,erpInventoryDetailResult.getMsg());
						fail.add(tempResult);
						continue;
					}
					List<ProductInventoryDetail> erpInventoryDetail=erpInventoryDetailResult.getData();
					
					//erp库存扣减掉微仓囤货，订单占用
					InventoryCommonResult<Map<String, Object>> deductResult=this.deductBBCOccupy(erpInventoryDetail,sku,warehouseId);
					if(deductResult.getResultCode()==1){
						InventoryPhysicalResult<Map<String,Object>> tempResult=new InventoryPhysicalResult<>(sku,warehouseId,deductResult.getMsg());
						tempResult.setData(deductResult.getData());
						fail.add(tempResult);
						continue;
					}
					Map<String, Object> inventoryAfterDeduct = deductResult.getData();
					List<ProductInventoryDetail> inventoryAfterDeductLists = (List<ProductInventoryDetail>) inventoryAfterDeduct.get("detailList");
					//更新云仓数据
					ProductMicroInventoyResult updateCloudInventory = inventorySyncRecordService.updateCloudInventory(inventoryAfterDeductLists);
					if(!updateCloudInventory.getResult()){
						InventoryPhysicalResult<Map<String,Object>> tempResult=new InventoryPhysicalResult<>(sku,warehouseId,updateCloudInventory.getMsg());
						fail.add(tempResult);
					}else{
						InventoryPhysicalResult<Map<String,Object>> tempResult=new InventoryPhysicalResult<>(sku,warehouseId,updateCloudInventory.getMsg());
						success.add(tempResult);
					}
				}catch (Exception e) {
					Logger.info("库存清点[{}],发生异常{}", sku+","+warehouseId,e);
					InventoryPhysicalResult<Map<String,Object>> tempResult=new InventoryPhysicalResult<>(sku,warehouseId,"清点发生异常");
					fail.add(tempResult);
					continue;
				}
			}
			
		}catch (Exception e) {
			Logger.info("库存清点异常{}", e);
		}
		result.put("success", success);
		result.put("erpLackData", erpLackData);
		result.put("fail", fail);
		//将清点结果写入文档
		try {
			this.writeResult(result);
		} catch (Exception e) {
			Logger.info("库存清点将结果[{}]", Json.toJson(result).toString());
			Logger.info("库存清点将结果写入文件发生异常{}", e);
		}
	}

	private void writeResult(Map<String, Object> result) throws Exception {
		String resultStr = Json.toJson(result).toString();
		Configuration conf = Play.application().configuration()
				.getConfig("inventory");
		LocalDate today=LocalDate.now();
		String fileName=today+"库存清点结果.json";
		String path = conf.getString("stockCheckFilePath");
		String filePath=path+File.separator+fileName;
		File file = new File(filePath);  
        if (!file.exists()) {  
            File dir = new File(file.getParent());  
            dir.mkdirs();  
            file.createNewFile();  
        }
        byte bytes[]=new byte[1024];   
        bytes=resultStr.getBytes();  
        int b=bytes.length;
        FileOutputStream fos=new FileOutputStream(file); 
        fos.write(bytes,0,b); 
        fos.flush();
        fos.close();
	}

	/**
	 * 扣减掉BBC已销售的库存
	 * @param erpInventoryDetail
	 * @param sku
	 * @param warehouseId
	 * @return
	 */
	private InventoryCommonResult<Map<String,Object>> deductBBCOccupy(
			List<ProductInventoryDetail> erpInventoryDetail, String sku, Integer warehouseId) {
		List<String> orderLists=Lists.newArrayList();
		//erp数据中某到期日期可能不够
		int waitDeductNum=0;
		//BBC总占用
		int BBCSalesTotal=0;
		for(ProductInventoryDetail tempErpDetail: erpInventoryDetail){
			Date expirationDate = tempErpDetail.getExpirationDate();
			tempErpDetail.setSku(sku);
			tempErpDetail.setWarehouseId(warehouseId);
			
			//微仓囤货数量
			ProductInventoryDetail paramDetail=new ProductInventoryDetail(sku, warehouseId, expirationDate);
			int microStockpile = microInventoryInRecordMapper.getTotalMicroInventoryStockpileByExpiration(paramDetail);
			//流转订单占用数量
			ProductMicroInventoyResult orderOccupyNumResult = inventorySyncRecordService.getOrderOccupyNum(tempErpDetail);
			if(!orderOccupyNumResult.getResult()){
				return new InventoryCommonResult<>(1,orderOccupyNumResult.getMsg());
			}
			int orderOccupyNum=(int) orderOccupyNumResult.getEntity();
			
			orderLists=orderOccupyNumResult.getEntityList();
			int bbcSalesTotal=orderOccupyNum+microStockpile+waitDeductNum;
			BBCSalesTotal+=bbcSalesTotal;
			if(tempErpDetail.getStock()<bbcSalesTotal){//erp该到期日期数量数量不足待发数扣减
				waitDeductNum=bbcSalesTotal-tempErpDetail.getStock();
				tempErpDetail.setStock(0);
			}else{
				tempErpDetail.setStock(tempErpDetail.getStock()-bbcSalesTotal);
				waitDeductNum=0;
			}
		}
		if(waitDeductNum>0){
			Map<String,Object> tempMap=Maps.newHashMap();
			tempMap.put("microStockpile", BBCSalesTotal);
			tempMap.put("orderLists", orderLists);
			return new InventoryCommonResult<Map<String,Object>>(1,"BBC已销售数量大于erp库存数量",tempMap);
		}else{
			Map<String,Object> tempMap=Maps.newHashMap();
			tempMap.put("detailList", erpInventoryDetail);
			return new InventoryCommonResult<Map<String,Object>>(0,tempMap);
		}
	}
}
