package mapper.timer;

import entity.timer.ProductInventoryOrderLock;

import java.util.List;

public interface ProductInventoryOrderLockMapper {

    int updateByPrimaryKeySelective(ProductInventoryOrderLock record);
    
	List<ProductInventoryOrderLock> selectInventoryLockListByParams(ProductInventoryOrderLock inventoryOrderLack);

}