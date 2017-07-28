package mapper.product_inventory;

import entity.product_inventory.ProductInventoryInRecord;

public interface ProductInventoryInRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductInventoryInRecord record);

    int insertSelective(ProductInventoryInRecord record);

    ProductInventoryInRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductInventoryInRecord record);

    int updateByPrimaryKey(ProductInventoryInRecord record);
}