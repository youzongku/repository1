package dto.marketing;

public class AuditParams {

	private Integer passed;// 页面选择，0不通过；1通过
	private String remarks;
	private String marketingOrderNo;
	private String auditUser;
	
	private Integer type;//1表示待初审，2则为待复审参数
	
	/**
	 * 1表示待初审，2则为待复审参数
	 * @return
	 */
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}

	public Integer getPassed() {
		return passed;
	}

	public void setPassed(Integer passed) {
		this.passed = passed;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getMarketingOrderNo() {
		return marketingOrderNo;
	}

	public void setMarketingOrderNo(String marketingOrderNo) {
		this.marketingOrderNo = marketingOrderNo;
	}

}
