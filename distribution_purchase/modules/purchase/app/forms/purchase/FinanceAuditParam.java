package forms.purchase;

/**
 * 封装财务审核的参数
 * 
 * @author huangjc
 * @date 2016年12月2日
 */
public class FinanceAuditParam {
	// 审核人
	private String auditUser;
	// 订单号
	private String purchaseOrderNo;
	// 支付信息：未支付0/已支付1
	private int paid;

	// 审核原因
	private String auditReason;

	// 支付信息备注
	private String paymentRemark;

	// 利润是否通过
	private boolean profitPassed;
	// 利润备注
	private String profitRemark;

	private double receivedAmount;

	private String receivedTime;

	public String getAuditReason() {
		return auditReason;
	}

	public void setAuditReason(String auditReason) {
		this.auditReason = auditReason;
	}

	public double getReceivedAmount() {
		if (paid == 0)
			return 0.0;
		return receivedAmount;
	}

	public void setReceivedAmount(double receivedAmount) {
		this.receivedAmount = receivedAmount;
	}

	public String getReceivedTime() {
		return receivedTime;
	}

	public void setReceivedTime(String receivedTime) {
		this.receivedTime = receivedTime;
	}

	public String getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public int getPaid() {
		return paid;
	}

	public void setPaid(int paid) {
		this.paid = paid;
	}

	public String getPaymentRemark() {
		return paymentRemark;
	}

	public void setPaymentRemark(String paymentRemark) {
		this.paymentRemark = paymentRemark;
	}

	public boolean isProfitPassed() {
		return profitPassed;
	}

	public void setProfitPassed(boolean profitPassed) {
		this.profitPassed = profitPassed;
	}

	public String getProfitRemark() {
		return profitRemark;
	}

	public void setProfitRemark(String profitRemark) {
		this.profitRemark = profitRemark;
	}

	@Override
	public String toString() {
		return "FinanceAuditParam [auditUser=" + auditUser
				+ ", purchaseOrderNo=" + purchaseOrderNo + ", paid=" + paid
				+ ", auditReason=" + auditReason + ", paymentRemark="
				+ paymentRemark + ", profitPassed=" + profitPassed
				+ ", profitRemark=" + profitRemark + ", receivedAmount="
				+ receivedAmount + ", receivedTime=" + receivedTime + "]";
	}

}
