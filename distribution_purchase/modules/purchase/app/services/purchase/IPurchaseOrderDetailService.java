package services.purchase;

import java.util.List;
import entity.purchase.PurchaseOrderDetail;

public interface IPurchaseOrderDetailService {
	/**
	 * 描述:批量增加订单详情
	 * @param details
	 * @return
	 */
	public boolean batchSaveDetails(List<PurchaseOrderDetail> details);

	/**
	 * 描述：修改订单详情
	 * @param purchaseOrderDetail
	 * @return
	 */
	public boolean updateDetail(PurchaseOrderDetail purchaseOrderDetail);
	
	/**
	 * 根据单号或id获取订单详情
	 * @param purOrderNo
	 * @return
	 */
	public List<PurchaseOrderDetail> getPurchaseDetailStockInfo(String purOrderNo);

	public List<PurchaseOrderDetail> getGift(Integer id);
}
