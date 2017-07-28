package entity.purchase.returnod;

import java.util.Date;

import utils.purchase.DateUtils;

/**
 * 采购退货单日志
 * 
 * @author huangjc
 * @date 2017年2月13日
 */
public class ReturnOrderLog {
	private Integer id;
	private String returnOrderNo;
	private Integer status;
	private Date createTime;
	private String createUser;
	private String auditRemarks;// 审核备注

	public ReturnOrderLog() {
	}

	public ReturnOrderLog(String returnOrderNo, Integer status,
			String createUser, String auditRemarks) {
		super();
		this.returnOrderNo = returnOrderNo;
		this.status = status;
		this.createUser = createUser;
		this.auditRemarks = auditRemarks;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getReturnOrderNo() {
		return returnOrderNo;
	}

	public void setReturnOrderNo(String returnOrderNo) {
		this.returnOrderNo = returnOrderNo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}
	
	public String getCreateTimeStr() {
		return DateUtils.date2FullDateTimeString(createTime);
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getAuditRemarks() {
		return auditRemarks;
	}

	public void setAuditRemarks(String auditRemarks) {
		this.auditRemarks = auditRemarks;
	}

	@Override
	public String toString() {
		return "ReturnOrderLog [id=" + id + ", returnOrderNo=" + returnOrderNo
				+ ", status=" + status + ", createTime=" + createTime
				+ ", createUser=" + createUser + ", auditRemarks="
				+ auditRemarks + "]";
	}

}
