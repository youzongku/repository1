package forms.purchase;

/**
 * 客服审核参数
 * 
 * @author huangjc
 * @date 2016年12月2日
 */
public class CustomerServiceAuditParam {
	private String auditUser;

	private String purchaseOrderNo;

	private boolean passed;

	private String remark;

	@Override
	public String toString() {
		return "CustomerServiceAuditParam [auditUser=" + auditUser
				+ ", purchaseOrderNo=" + purchaseOrderNo + ", passed=" + passed
				+ ", remark=" + remark + "]";
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

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
