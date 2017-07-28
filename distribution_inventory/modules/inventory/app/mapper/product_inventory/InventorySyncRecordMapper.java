package mapper.product_inventory;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.product_inventory.InventorySyncRecord;

public interface InventorySyncRecordMapper {
	
    public int deleteByPrimaryKey(Integer id);

    public int insert(InventorySyncRecord record);

    public int insertSelective(InventorySyncRecord record);

    public InventorySyncRecord selectByPrimaryKey(Integer id);
    
    public InventorySyncRecord selectBySkuAndWarehouseId(@Param("sku")String sku,@Param("warehouseId")Integer warehouseId);

    public int updateByPrimaryKeySelective(InventorySyncRecord record);

    public int updateByPrimaryKey(InventorySyncRecord record);

	public List<InventorySyncRecord> selectBySku(String sku);
    
}