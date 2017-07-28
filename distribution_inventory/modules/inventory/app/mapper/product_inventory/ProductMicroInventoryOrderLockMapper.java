package mapper.product_inventory;


import entity.product_inventory.ProductMicroInventoryDetail;

import java.util.List;

import dto.product_inventory.ProductMicroInventoryOrderLockDto;
import entity.product_inventory.ProductMicroInventoryOrderLock;

public interface ProductMicroInventoryOrderLockMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductMicroInventoryOrderLock record);

    int insertSelective(ProductMicroInventoryOrderLock record);

    ProductMicroInventoryOrderLock selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductMicroInventoryOrderLock record);

    int updateByPrimaryKey(ProductMicroInventoryOrderLock record);

	ProductMicroInventoryOrderLock selectByMicroDetail(ProductMicroInventoryDetail microInventory);

    List<ProductMicroInventoryOrderLock> query(ProductMicroInventoryOrderLockDto productMicroInventoryOrderLockCriteria);

	List<ProductMicroInventoryOrderLock> selectListsByParams(ProductMicroInventoryOrderLock microInventoryLock);
}