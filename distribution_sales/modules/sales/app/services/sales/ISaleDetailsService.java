package services.sales;

import java.util.List;

import entity.sales.SaleDetail;

public interface ISaleDetailsService {
	
	/**
	 * 查询订单详情历史
	 * @param orderId
	 * @return
	 */
//	List<SaleDetail> getHistorySaleDetailsByOrderId(Integer orderId);

	boolean saveBatchSaleDetails(List<SaleDetail> saleDetails);
	
	boolean updateBatchSaleDetail(List<SaleDetail> salesDetails);
	
	/**
	 * 获取销售发货单所有的订单详情
	 * @param orderId
	 * @return
	 */
	List<SaleDetail> getAllSaleDetailsByOrderId(Integer orderId);
	
	
	/**
	 * 将客户订单详情表是否处于售后的标识修改为1（售后进行中）
	 * @return
	 */
	int updateIsAfterSaleTo1(String saleNo,String sku);

}
