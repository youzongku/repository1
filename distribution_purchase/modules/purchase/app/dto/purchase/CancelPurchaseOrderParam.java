package dto.purchase;


public class CancelPurchaseOrderParam {
	// 采购单号
	private String purchaseNo;
	// 操作标识
	private String flag;
	// 支付类型
	private String payType;
	// 支付金额
	private Double actualAmount;
	// 发货单的支付金额
	private Double actualAmount4SO;
	// 支付时间
	private String payDate;
	// 流水号
	private String tradeNo;
	
	public CancelPurchaseOrderParam(){
		
	}
	/**
	 * @param purchaseNo
	 * @param flag
	 */
	public CancelPurchaseOrderParam(String purchaseNo, String flag) {
		this.purchaseNo = purchaseNo;
		this.flag = flag;
	}

	public Double getActualAmount4SO() {
		return actualAmount4SO;
	}

	public void setActualAmount4SO(Double actualAmount4SO) {
		this.actualAmount4SO = actualAmount4SO;
	}

	public String getPayDate() {
		return payDate;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getPurchaseNo() {
		return purchaseNo;
	}

	public void setPurchaseNo(String purchaseNo) {
		this.purchaseNo = purchaseNo;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Double getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(Double actualAmount) {
		this.actualAmount = actualAmount;
	}

	@Override
	public String toString() {
		return "CancelPurchaseOrderParam [purchaseNo=" + purchaseNo + ", flag="
				+ flag + ", payType=" + payType + ", actualAmount="
				+ actualAmount + ", actualAmount4SO=" + actualAmount4SO
				+ ", payDate=" + payDate + ", tradeNo=" + tradeNo + "]";
	}

}
