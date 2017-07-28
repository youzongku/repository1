package service.timer;

import entity.timer.InventorySyncRecord;

public interface IinventorySyncRecordService {
	
	public InventorySyncRecord selectBySkuAndWarehouseId(String sku,Integer warehouseId);
	
}
