package services.product_inventory.impl;

import com.google.inject.Inject;

import entity.product_inventory.ProductInventoryOrderLock;
import mapper.product_inventory.ProductInventoryOrderLockMapper;
import services.product_inventory.IProductInventoryOrderLockService;

public class ProductInventoryOrderLockService implements IProductInventoryOrderLockService {

	@Inject
	ProductInventoryOrderLockMapper productInventoryOrderLockMapper;
	
	@Override
	public ProductInventoryOrderLock selectByParam(ProductInventoryOrderLock inventoryLock) {
		
		return productInventoryOrderLockMapper.selectByParam(inventoryLock);
	}

	@Override
	public int update(ProductInventoryOrderLock inventoryLock) {
		return productInventoryOrderLockMapper.updateByPrimaryKeySelective(inventoryLock);
	}

}
