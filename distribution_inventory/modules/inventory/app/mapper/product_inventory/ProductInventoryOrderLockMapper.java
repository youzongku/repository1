package mapper.product_inventory;

import entity.product_inventory.ProductInventoryDetail;
import entity.product_inventory.ProductInventoryOrderLock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductInventoryOrderLockMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductInventoryOrderLock record);

    int insertSelective(ProductInventoryOrderLock record);

    ProductInventoryOrderLock selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductInventoryOrderLock record);

    int updateByPrimaryKey(ProductInventoryOrderLock record);

	ProductInventoryOrderLock selectByParam(ProductInventoryOrderLock inventoryLock);

    List<ProductInventoryOrderLock> listBySkuAndWarehouseId(@Param("sku") String sku, @Param("warehouseId") Integer warehouseid);
    
    List<ProductInventoryOrderLock> listBySkuAndWarehouseIdEffective(@Param("sku") String sku, @Param("warehouseId") Integer warehouseId);

    
	List<ProductInventoryOrderLock> selectInventoryLockListByParams(ProductInventoryOrderLock inventoryOrderLack);

	/**
     * 查询isEffective=-1 或者1 的锁库记录 传入条件为isEffective=1
     * @param inventoryOrderLack
     * @return
     */
	List<ProductInventoryOrderLock> selectInventoryEffectiveLockListByParams(
			ProductInventoryOrderLock inventoryOrderLockParam);

	/**
	 * 查询其他订单锁定带过期日期锁定的sku记录
	 * @param inventoryExpirationOrderLockParam
	 * @return
	 */
	List<ProductInventoryOrderLock> selectOtherOrderInventoryEffectiveLockListByParams(
			ProductInventoryOrderLock inventoryExpirationOrderLockParam);

	/**
	 * 查询未指定过期日期的锁库记录
	 * @param inventoryOrderLockParam
	 * @return
	 */
	List<ProductInventoryOrderLock> selectEffectiveListByParamsAndExpirationDateIsNull(
			ProductInventoryOrderLock inventoryOrderLockParam);

	/**
	 * 删除临时锁定的锁库记录
	 * @param orderNo
	 * @return
	 */
	int deleteByOrderNo(String orderNo);

	/**
	 * 根据云仓详情查询有效锁定的总数量
	 * @param tempInventoryDetail
	 * @return
	 */
	int selectTotalLockNumByInventoryDetail(ProductInventoryDetail tempInventoryDetail);

	/**
	 * 根据sku，warehouseId,状态 查询锁库记录
	 * @param sku
	 * @param warehouseId
	 * @param isEffective
	 * @return
	 */
	List<ProductInventoryOrderLock> getOrderLackBySkuAndWarhouseId(@Param("sku")String sku, @Param("warehouseId")Integer warehouseId, @Param("isEffective")int isEffective);
	
}