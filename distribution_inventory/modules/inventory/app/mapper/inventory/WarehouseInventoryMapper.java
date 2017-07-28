package mapper.inventory;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.inventory.PostB2CData;
import entity.inventory.WarehouseInventory;

public interface WarehouseInventoryMapper extends BaseMapper<WarehouseInventory> {

	/**
     * 更新指定微仓仓库指定商品的库存（同时扣除掉相应的冻结库存）
     * @param email
     * @param sku
     * @param warehouseid
     * @param qty 变化的数量，正数是添加，负数是减少
     * @return
     */
    int updateStockAndFrozenInWarehouse(String sku,int warehouseid,int qty);
    
    /**
     * 单纯变更指定sku在指定仓库中的库存信息
     * @param sku
     * @param warehouseid
     * @param qty
     * @return
     */
    int updateStockInWarehouse(String sku,int warehouseid,int qty);
    
    /**
     * 更新物理仓指定产品的冻结信息
     * @param sku
     * @param warehouseid
     * @param frozenQty 正数：增加冻结数量，负数：减少冻结数量
     * @return
     */
    int updateFrozenStockInWarehouse(String sku,int warehouseid,int frozenQty);

	Integer queryInventoryCount(@Param("param")PostB2CData search);

	List<WarehouseInventory> queryInventory(@Param("param")PostB2CData search);

	List<WarehouseInventory> checkInventory(@Param("param")WarehouseInventory search);
	
}