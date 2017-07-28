package dto.purchase.returnod;

import entity.purchase.PurchaseOrderDetail;

/**
 * 采购单商品退货情况
 * 
 * @author Administrator
 *
 */
public class PoReturnInfo extends PurchaseOrderDetail {
	private static final long serialVersionUID = 1L;
	/**
	 * 累计退款金额
	 */
	private Double totalReturnAmount;
	/**
	 * 累计退货个数
	 */
	private Integer totalReturnQties;

	public Double getTotalReturnAmount() {
		return totalReturnAmount;
	}

	public void setTotalReturnAmount(Double totalReturnAmount) {
		this.totalReturnAmount = totalReturnAmount;
	}

	public Integer getTotalReturnQties() {
		return totalReturnQties;
	}

	public void setTotalReturnQties(Integer totalReturnQties) {
		this.totalReturnQties = totalReturnQties;
	}

}
