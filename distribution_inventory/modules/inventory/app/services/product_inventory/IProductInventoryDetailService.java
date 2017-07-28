package services.product_inventory;

import entity.product_inventory.ProductInventoryDetail;

public interface IProductInventoryDetailService {
	
	public int deleteBySkuAndWarehouseId(String sku,Integer warehouseId);
	
	public int saveOrUpdate(ProductInventoryDetail pid);
	
	public ProductInventoryDetail selectDetail(ProductInventoryDetail record);

}
