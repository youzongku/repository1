package mapper.timer;

import org.apache.ibatis.annotations.Param;

import entity.timer.InventorySyncRecord;

public interface InventorySyncRecordMapper {
    
    public InventorySyncRecord selectBySkuAndWarehouseId(@Param("sku")String sku,@Param("warehouseId")Integer warehouseId);

}