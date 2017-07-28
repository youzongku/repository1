package mapper.product;

import entity.product.InventoryHistory;

public interface InventoryHistoryMapper {
    int deleteByPrimaryKey(Integer iid);

    int insert(InventoryHistory record);

    int insertSelective(InventoryHistory record);

    InventoryHistory selectByPrimaryKey(Integer iid);

    int updateByPrimaryKeySelective(InventoryHistory record);

    int updateByPrimaryKey(InventoryHistory record);
}