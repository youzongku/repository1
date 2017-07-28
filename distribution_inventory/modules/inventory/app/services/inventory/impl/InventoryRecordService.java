package services.inventory.impl;

import java.util.Date;

import mapper.inventory.InventoryChangeRecordDetailMapper;
import mapper.inventory.InventoryChangeRecordMainMapper;
import mapper.inventory.WarehouseInventoryMapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import entity.inventory.InventoryChangeRecordDetail;
import entity.inventory.InventoryChangeRecordMain;
import services.inventory.IInventoryRecordService;

public class InventoryRecordService implements IInventoryRecordService {

	@Inject
	InventoryChangeRecordDetailMapper inventoryChangeRecordDetailMapper;
	
	@Inject
	InventoryChangeRecordMainMapper inventoryChangeRecordMainMapper;
	
	@Inject
	WarehouseInventoryMapper warehouseInventoryMapper;
	
	@Override
	public boolean updateInventoryRecord(JsonNode main) {
		
		boolean res = true;
		
		try {
			
			int status = main.get("status").asInt();
			//主表记录
	    	InventoryChangeRecordMain m = new InventoryChangeRecordMain();
	    	m.setEmail(main.get("email").textValue());
	    	m.setPurchaseOrderId(main.get("purchaseOrderId").textValue());
	    	m.setStatus(status);
	    	m.setCreateDate(new Date());
	    	inventoryChangeRecordMainMapper.insertSelective(m);
	    	
	    	//子表记录
	    	JsonNode details = main.get("pros");
	    	InventoryChangeRecordDetail detail = null;
	    	
	    	if(details != null){
	    		for (JsonNode d : details) {
	        		detail = new InventoryChangeRecordDetail();
	        		detail.setSku(d.get("sku").textValue());
	        		detail.setQty(d.get("qty").asInt());
	        		detail.setWarehouseId(d.get("warehouseId").asInt());
	        		detail.setChangeRecordId(m.getId());
	        		inventoryChangeRecordDetailMapper.insertSelective(detail);
	        		
	        		//冻结库存
	        		if(status == 1){
	        			//添加子表记录的同时，也需要在t_warehouse_inventory中将冻结库存变更
		        		warehouseInventoryMapper.updateFrozenStockInWarehouse(d.get("sku").textValue(), 
		        				d.get("warehouseId").asInt(), 
		        				d.get("qty").asInt());
	        		}
	        		//支付超时，解冻库存
	        		if(status == 2){
	        			warehouseInventoryMapper.updateFrozenStockInWarehouse(d.get("sku").textValue(), 
		        				d.get("warehouseId").asInt(), 
		        				-d.get("qty").asInt());
	        			
	        		}
	        		//状态3（支付成功）会在支付成功的时候一并扣除t_warehouse_inventory的冻结库存，不在此单独操作
	        		
	        		
	    		}
	    	}
		} catch (Exception e) {
			res = false;
			throw e;
		}
		
		return res;
	}

}
