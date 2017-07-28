package dto.sales.hb;
/**
 * 发货单合并审核参数
 */
public class HBDeliveryAuditParam {

	// 合并单单号
	private String salesHbNo;

	// 是否通过（false不通过，true通过）
	private boolean passed;

	private String remarks;
	
	private String auditUser;
	
	// passed为true的情况下，才有以下内容
	private String receiver;
	private String provinceName;
	private String cityName;
	private String areaName;
	private String addrDetail;// 详细地址
	private String tel;// 收货人电话
	private String postCode;// 邮编
	
	public HBDeliveryAuditParam(String salesHbNo, boolean passed, String remarks) {
		super();
		this.salesHbNo = salesHbNo;
		this.passed = passed;
		this.remarks = remarks;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAddrDetail() {
		return addrDetail;
	}

	public void setAddrDetail(String addrDetail) {
		this.addrDetail = addrDetail;
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

	public String getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}

	public String getSalesHbNo() {
		return salesHbNo;
	}

	public void setSalesHbNo(String salesHbNo) {
		this.salesHbNo = salesHbNo;
	}

	public boolean getPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return "HBDeliveryAuditParam [salesHbNo=" + salesHbNo + ", passed=" + passed + ", remarks=" + remarks
				+ ", auditUser=" + auditUser + "]";
	}

}
