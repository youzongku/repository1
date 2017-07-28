package service.timer.impl;

import mapper.timer.InventorySyncRecordMapper;
import service.timer.IinventorySyncRecordService;

import com.google.inject.Inject;

import entity.timer.InventorySyncRecord;

public class InventorySyncRecordService implements IinventorySyncRecordService {

	@Inject
	private InventorySyncRecordMapper inventorySyncRecordMapper;
	
	@Override
	public InventorySyncRecord selectBySkuAndWarehouseId(String sku,Integer warehouseId) {
		return inventorySyncRecordMapper.selectBySkuAndWarehouseId(sku,warehouseId);
	}
	

}
