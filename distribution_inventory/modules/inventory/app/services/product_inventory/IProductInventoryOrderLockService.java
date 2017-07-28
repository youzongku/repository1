package services.product_inventory;

import entity.product_inventory.ProductInventoryOrderLock;

public interface IProductInventoryOrderLockService {

	ProductInventoryOrderLock selectByParam(ProductInventoryOrderLock inventoryLock);

	int update(ProductInventoryOrderLock inventoryLock);

}
