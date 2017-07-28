package services.product_inventory;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import dto.inventory.ProductCloudInventoryResult;
import dto.product_inventory.HistoryOrderData;
import entity.product_inventory.Order;
import entity.product_inventory.OrderDetail;
import entity.product_inventory.ProductMicroInventoryInRecord;
import entity.product_inventory.ProductMicroInventoryOrderLock;

public interface IOrderService {

	Order getOrder(Order order);

	void saveOrder(Order order);

	void saveOrderDetail(OrderDetail orderDetail);

	OrderDetail selectByParam(OrderDetail orderDetail);

	/**
	 * 未支付订单处理
	 * @param order
	 * @param orderDetailList
	 * @return
	 */
	ProductCloudInventoryResult orderLockCloudDispose(Order order, List<OrderDetail> orderDetailList);

	/**
	 * 采购单更新云仓和微仓库存
	 * @param order
	 * @param orderDetailList
	 * @return
	 */
	ProductCloudInventoryResult updateStockByPurchaseOrder(Order order, List<OrderDetail> orderDetailList);

	ProductCloudInventoryResult orderCanceled(Order order, List<OrderDetail> orderDetailList);

	/**
	 * 根据采购订单号搜索采购订单详情集合
	 * @param orderDetailParam
	 * @return
	 */
	List<OrderDetail> getOrderListForPurchase(OrderDetail orderDetailParam);

	/**
	 * 根据销售单号查询微仓锁库记录
	 * @param microLockParam
	 * @return
	 */
	List<ProductMicroInventoryOrderLock> selectMicroLockListForMicroOut(ProductMicroInventoryOrderLock microLockParam);

	/**
	 * 根据采购单号查询入仓记录
	 * @param microInRecordParam
	 * @return
	 */
	List<ProductMicroInventoryInRecord> selectMicroInRecordForMicroOut(
			ProductMicroInventoryInRecord microInRecordParam);

	ProductCloudInventoryResult microOutOfSalesOrder(List<ProductMicroInventoryOrderLock> microLackList,
			List<ProductMicroInventoryInRecord> microInRecordList, String saleOrderNo);

	/**
	 * 检查商品在云仓总仓中扣除锁定个数后的数量是否满足订单的增量，当所有商品都满足时就锁定，不满足返回缺少个数
	 * @param proChangedList
	 */
	List<Object> checkInventoryTotalNum(Order order, List<OrderDetail> orderDetailList);

	
	void deleteAllOrderDetailDateByOrderNo(OrderDetail orderDetailParam);

	/**
	 * 采购单发生改变重新生成采购单详情
	 * @param order
	 * @param orderDetailReList
	 * @return
	 */
	ProductCloudInventoryResult reSaveOrderDetail(Order order, List<OrderDetail> orderDetailReList);

	/**
	 * 将该订单锁定的库存更新为不可失效
	 * @param orderResult
	 * @return
	 */
	ProductCloudInventoryResult changeLockToEffective(Order orderResult);

	/**
	 * 关闭原有系统订单数据进行构造微仓入库记录
	 * @param historyOrderData
	 * @return
	 */
	ProductCloudInventoryResult historyOrderDataClosed(HistoryOrderData historyOrderData);

	/**
	 * 修改订单详情信息
	 * @param order
	 * @param orderDetailList
	 * @return
	 */
	ProductCloudInventoryResult changeOrderDetailInfo(Order order, List<OrderDetail> orderDetailList);

	//void initResidueNum();

	/**
	 * 获取采购单信息
	 *
	 * @param main
	 * @return
	 */
	List<OrderDetail> getOrderDetailBySkuAndSaleOrderNo(String main);

	ProductMicroInventoryInRecord getPurchaseByPurchaseOrderNo(String s_main);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月22日 上午10:55:46
	 */
	 List<OrderDetail> getOrderDetails(String orderNo);
}
