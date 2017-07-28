package dto.openapi;

import java.util.Date;

/**
 * 采购单审核记录
 * 
 * @author huangjc
 * @date 2016年12月2日
 */
public class PurchaseOrderAuditLog {
	
	// 客服审核
	public static final int AUDIT_TYPE_CS = 1;
	// 财务审核
	public static final int AUDIT_TYPE_FINANCE = 2;

	private Integer id;
	private Integer status;
	private String purchaseNo;
	private String jsonValue;
	private String auditUser;
	private Date auditDate;
	/** 审核类型：1客服审核；2财务审核 */
	private Integer auditType;

	private String auditDateStr;//审核日期时间字符串
	
	public Integer getAuditType() {
		return auditType;
	}

	public void setAuditType(Integer auditType) {
		this.auditType = auditType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPurchaseNo() {
		return purchaseNo;
	}

	public void setPurchaseNo(String purchaseNo) {
		this.purchaseNo = purchaseNo;
	}

	public String getJsonValue() {
		return jsonValue;
	}

	public void setJsonValue(String jsonValue) {
		this.jsonValue = jsonValue;
	}

	public String getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}

	public Date getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}

	public String getAuditDateStr() {
		return auditDateStr;
	}

	public void setAuditDateStr(String auditDateStr) {
		this.auditDateStr = auditDateStr;
	}

	@Override
	public String toString() {
		return "PurchaseOrderAuditLogs [id=" + id + ", status=" + status
				+ ", purchaseNo=" + purchaseNo + ", jsonValue=" + jsonValue
				+ ", auditUser=" + auditUser + ", auditDate=" + auditDate + "]";
	}

}
