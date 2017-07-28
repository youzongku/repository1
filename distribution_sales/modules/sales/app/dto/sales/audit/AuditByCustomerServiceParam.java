package dto.sales.audit;

/**
 * 客服审核传递的参数
 */
public class AuditByCustomerServiceParam {

	private String sno;
	private boolean csAudit;
	private String address;
	private String receiver;
	private String tel;
	private String postCode;
	private String idcard;
	private String csRemark;
	private String auditUser;

	public String getSno() {
		return sno;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}

	public boolean isCsAudit() {
		return csAudit;
	}

	public void setCsAudit(boolean csAudit) {
		this.csAudit = csAudit;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getCsRemark() {
		return csRemark;
	}

	public void setCsRemark(String csRemark) {
		this.csRemark = csRemark;
	}

	public String getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}

	@Override
	public String toString() {
		return "AuditByCustomerServiceParam [sno=" + sno + ", csAudit=" + csAudit + ", address=" + address
				+ ", receiver=" + receiver + ", tel=" + tel + ", postCode=" + postCode + ", idcard=" + idcard
				+ ", csRemark=" + csRemark + ", auditUser=" + auditUser + "]";
	}

}
