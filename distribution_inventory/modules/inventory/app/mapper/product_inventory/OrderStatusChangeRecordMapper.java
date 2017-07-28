package mapper.product_inventory;

import entity.product_inventory.OrderStatusChangeRecord;

public interface OrderStatusChangeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderStatusChangeRecord record);

    int insertSelective(OrderStatusChangeRecord record);

    OrderStatusChangeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderStatusChangeRecord record);

    int updateByPrimaryKey(OrderStatusChangeRecord record);
}