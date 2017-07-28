package mapper.timer;

import java.util.List;

import entity.timer.PurchaseOrder;

public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {
	
	/**
	 * 获取失效的采购单
	 * @author zbc
	 * @since 2016年8月23日 下午3:48:21
	 */
	public List<PurchaseOrder> getInvalidOrders();
	
	/**
	 * 批量更新微仓订单状态
	 * @author zbc
	 * @since 2016年8月24日 上午11:09:57
	 */
	public int batchUpdate(List<PurchaseOrder> list);
	
}