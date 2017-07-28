package mapper.inventory;

import entity.inventory.WarehouseProduct;

public interface WarehouseProductMapper extends BaseMapper<WarehouseProduct> {

	int updateTotalStock(String csku,int qty);
	
}