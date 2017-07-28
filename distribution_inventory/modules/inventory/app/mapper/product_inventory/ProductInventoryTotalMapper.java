package mapper.product_inventory;

import java.util.List;

import entity.product_inventory.ProductInventoryTotal;
import entity.product_inventory.ProductMicroInventoryTotal;

import org.apache.ibatis.annotations.Param;

import dto.product_inventory.ProductInventoryEnquiryRequest;
import dto.product_inventory.ProductInventoryEnquiryResult;

public interface ProductInventoryTotalMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductInventoryTotal record);

    int insertSelective(ProductInventoryTotal record);

    ProductInventoryTotal selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductInventoryTotal record);

    int updateByPrimaryKey(ProductInventoryTotal record);

    ProductInventoryTotal selectBySkuAndWarehouseId(@Param("sku") String sku, @Param("warehouseId")Integer id);

    ProductInventoryTotal selectByParam(ProductInventoryTotal param);

	int deductTotalNumByParam(ProductInventoryTotal param);
	
	int deleteBySkuAndWarehouseId(@Param("sku") String sku,@Param("warehouseId") Integer warehouseId);

    List<ProductInventoryTotal> query(ProductInventoryTotal inventoryTotalParam);
    
    /**
	 * 查询所有云仓库存
	 * 
	 * @param param
	 * @return
	 * @author ye_ziran
	 * @since 2017年1月6日 下午5:48:00
	 */
	List<ProductInventoryEnquiryResult> cloudInventory(ProductInventoryEnquiryRequest param);

	List<ProductInventoryTotal> selectByWarehouseId(@Param("warehouseId")Integer warehouseId);

	List<ProductInventoryTotal> getAll();
}