package service.timer;

import java.util.List;

import entity.timer.ErpStockInResult;
import entity.timer.ProductInventoryBatchDetail;
import entity.timer.ProductInventoryDetail;

public interface IProductCloudInventoryService {

    ErpStockInResult erpStockInDetail(List<ProductInventoryBatchDetail> productInventoryBatchDetailList);

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
}
