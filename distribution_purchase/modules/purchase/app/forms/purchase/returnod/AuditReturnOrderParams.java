package forms.purchase.returnod;

import java.util.List;

public class AuditReturnOrderParams {
	private List<String> returnOrderNoList;
	private Integer passed;// 0不通过；1通过
	private Double actualTotalReturnAmount;
	private String auditRemarks;
	private String auditUser;

	public List<String> getReturnOrderNoList() {
		return returnOrderNoList;
	}

	public void setReturnOrderNoList(List<String> returnOrderNoList) {
		this.returnOrderNoList = returnOrderNoList;
	}

	public Integer getPassed() {
		return passed;
	}

	public void setPassed(Integer passed) {
		this.passed = passed;
	}

	public Double getActualTotalReturnAmount() {
		return actualTotalReturnAmount;
	}

	public void setActualTotalReturnAmount(Double actualTotalReturnAmount) {
		this.actualTotalReturnAmount = actualTotalReturnAmount;
	}

	public String getAuditRemarks() {
		return auditRemarks;
	}

	public void setAuditRemarks(String auditRemarks) {
		this.auditRemarks = auditRemarks;
	}

	public String getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}

	@Override
	public String toString() {
		return "AuditReturnOrderParams [returnOrderNoList=" + returnOrderNoList + ", passed=" + passed
				+ ", actualTotalReturnAmount=" + actualTotalReturnAmount + ", auditRemarks=" + auditRemarks
				+ ", auditUser=" + auditUser + "]";
	}

}
