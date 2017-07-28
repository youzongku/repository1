package mapper.product_inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dto.product_inventory.ProductInventoryDetailDto;
import entity.product_inventory.ProductInventoryDetail;

public interface ProductInventoryDetailMapper {
    int deleteByPrimaryKey(Integer id);
    
    int deleteBySkuAndWarehouseId(@Param("sku")String sku,@Param("warehouseId")Integer warehouseId);

    int insert(ProductInventoryDetail record);

    int insertSelective(ProductInventoryDetail record);

    ProductInventoryDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductInventoryDetail record);

    int updateByPrimaryKey(ProductInventoryDetail record);

    ProductInventoryDetail query(ProductInventoryDetailDto productInventoryDetailDto);

	ProductInventoryDetail selectByParam(ProductInventoryDetail param);

	List<ProductInventoryDetail> selectinventoryDetailListByParam(ProductInventoryDetail inventoryDetailParam);

	List<ProductInventoryDetail> list(ProductInventoryDetail inventoryDetailParam);

	List<ProductInventoryDetail> selectInventoryDetailBySkuAndWarehouseId(@Param("sku")String sku, @Param("warehouseId")Integer warehouseId);

	int getTotalNumBySkuAndWarehouseId(ProductInventoryDetail detailParam);

	/**
	 * 查找云仓明细库存小于0的
	 * @return
	 */
	List<ProductInventoryDetail> selectCloudInventoryStockMinus();

	/**
	 * 云仓明细数据和云仓总仓不一致的
	 * @return
	 */
	List<Map> selectCloudInventoryTotalUnequalDetail();
}