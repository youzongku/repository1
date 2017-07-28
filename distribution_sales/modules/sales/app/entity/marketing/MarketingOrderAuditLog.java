package entity.marketing;

import java.util.Date;

import util.sales.DateUtils;

public class MarketingOrderAuditLog {
    private Integer id;

    private Integer status;// 状态

    private String marketingOrderNo;// 营销单单号

    private Integer passed;// 是否通过

    private String remarks;// 备注

    private String auditUser;// 审核人

    private Date auditDate;
    private String auditDateStr;

    private Integer auditType;// 审核类型：1初审；2复审

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

    public String getMarketingOrderNo() {
        return marketingOrderNo;
    }

    public void setMarketingOrderNo(String marketingOrderNo) {
        this.marketingOrderNo = marketingOrderNo;
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

    public String getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(String auditUser) {
        this.auditUser = auditUser;
    }

    public Date getAuditDate() {
        return auditDate;
    }
    
    public String getAuditDateStr() {
    	return DateUtils.date2string(auditDate, DateUtils.FORMAT_FULL_DATETIME);
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public Integer getAuditType() {
        return auditType;
    }

    public void setAuditType(Integer auditType) {
        this.auditType = auditType;
    }
}