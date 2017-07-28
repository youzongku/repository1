package services.product_inventory;

import dto.inventory.ProductCloudInventoryResult;
import dto.inventory.SearchSkuProductCloudInventoryDto;
import dto.product_inventory.CloudAndMicroInventoryDto;
import dto.product_inventory.CloudAndMicroInventoryResult;
import dto.product_inventory.CloudExpirationFormatResult;
import dto.product_inventory.ErpStockInResult;
import dto.product_inventory.InventoryDetailDistributeDto;
import dto.product_inventory.SearchCloudInventoryResult;
import entity.product_inventory.ProductInventoryBatchDetail;
import entity.product_inventory.ProductInventoryDetail;
import entity.product_inventory.ProductInventoryOrderLock;
import entity.product_inventory.ProductInventoryTotal;

import java.util.List;
import java.util.Map;

/**
 * @author longhuashen
 * @since 2016/12/5
 */
public interface IProductCloudInventoryService {

    ErpStockInResult erpStockInDetail(List<ProductInventoryBatchDetail> productInventoryBatchDetailList);

    SearchCloudInventoryResult list(SearchSkuProductCloudInventoryDto searchSkuProductCloudInventoryDto);
    
    ProductCloudInventoryResult inventoryLocks(List<ProductInventoryOrderLock> params);
    
    int insertSelective(ProductInventoryTotal record);
    
    int deleteBySkuAndWarehouseId(String sku,Integer warehouseId);

    List<CloudAndMicroInventoryResult> searchProductCloudAndMicroInventory(CloudAndMicroInventoryDto cloudAndMicroInventoryDto);

    /**
     * 查询某个sku云仓
     *
     * @param s_main
     * @return
     */
    InventoryDetailDistributeDto getCloudInventoryDetail(String s_main);

	/**
	 * 查看erp库存
	 * @author zbc
	 * @since 2017年1月12日 上午10:44:49
	 */
	Map<String, Object> erpStock(String s_main);

	/**
	 * 根据仓库Id获取该仓库中所有商品
	 * @param warehouseId
	 * @return
	 */
	List<ProductInventoryDetail> getExternalWarehouseInventoryDetail(Integer warehouseId);

	/**
	 * 更新仓库库存数据(暂时外部仓库福州1仓使用)
	 * @param productInventoryDetailParam
	 * @return
	 */
	int updateExternalWearhouseProductInventory(ProductInventoryDetail productInventoryDetailParam);

	/**
	 * 查询指定sku，warehouseId 在云仓中的明细 
	 * @param reqParam
	 * @return
	 */
	List<CloudExpirationFormatResult> searchProductCloudInventory(List<ProductInventoryDetail> reqParam);

	/**
	 * 修改杭州仓2012所有商品库存为指定数量
	 * @param param
	 * @return
	 */
	ProductCloudInventoryResult setCloudStockByWarehouseId(String param);

	/**
	 * 查询云仓总仓中商品
	 * @param pit
	 * @return
	 */
	ProductInventoryTotal getProductBySkuAndWarehouseId(ProductInventoryTotal pit);

	/**
	 * 更新总仓信息
	 * @param totalResult
	 * @return
	 */
	int updateProductTotal(ProductInventoryTotal totalResult);

	/**
	 * 查询库存分布数据
	 * @param sku
	 * @param warehouseId
	 * @return
	 */
	String getInventoryDispersion(String sku, Integer warehouseId);
}
