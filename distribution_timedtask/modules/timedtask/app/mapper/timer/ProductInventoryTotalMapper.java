package mapper.timer;

import org.apache.ibatis.annotations.Param;

import entity.timer.ProductInventoryTotal;


public interface ProductInventoryTotalMapper {

    int insertSelective(ProductInventoryTotal record);

    int updateByPrimaryKeySelective(ProductInventoryTotal record);

    ProductInventoryTotal selectBySkuAndWarehouseId(@Param("sku") String sku, @Param("warehouseId")Integer id);

    ProductInventoryTotal selectByParam(ProductInventoryTotal param);
   
}