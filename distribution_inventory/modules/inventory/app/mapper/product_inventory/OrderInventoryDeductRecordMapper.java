package mapper.product_inventory;

import entity.product_inventory.OrderInventoryDeductRecord;

import java.util.List;

public interface OrderInventoryDeductRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderInventoryDeductRecord record);

    int insertSelective(OrderInventoryDeductRecord record);

    OrderInventoryDeductRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderInventoryDeductRecord record);

    int updateByPrimaryKey(OrderInventoryDeductRecord record);

    List<OrderInventoryDeductRecord> listByOrderNo(String orderNo);

    /**
     * 历史订单关闭还原微仓扣减云仓出库记录查询
     * @param inventoryDeductRecord
     * @return
     */
	OrderInventoryDeductRecord selectForDeductByHistoryOrderClosed(OrderInventoryDeductRecord inventoryDeductRecord);

	/**
	 * 根据订单号删除云仓出库记录
	 * @param orderNo
	 */
	int deleteByOrderNo(String orderNo);
}