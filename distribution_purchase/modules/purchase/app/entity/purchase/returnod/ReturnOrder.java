package entity.purchase.returnod;

import java.util.Date;

/**
 * 采购退货单
 * 
 * @author huangjc
 * @date 2017年2月13日
 */
public class ReturnOrder {
	private Integer id;
	private String returnOrderNo;// 退货单号
	private String email;
	private String nickName;
	private String salesman;
	private Double userExpectTotalReturnAmount; // 用户期待的退款金额
	private Double totalReturnAmount;// 退款总金额，根据详情计算出来的
	private Double actualTotalReturnAmount;// 实际退款总金额，审核时可以修改totalReturnAmount
	private Integer status;// 状态
	private Date applicationTime;// 申请时间
	private String remarks;// 备注
	private String auditRemarks;// 审核备注

	private Date createTime;
	private String createUser;
	private Date lastUpdateTime;
	private String lastUpdateUser;

	public Double getUserExpectTotalReturnAmount() {
		return userExpectTotalReturnAmount;
	}

	public void setUserExpectTotalReturnAmount(
			Double userExpectTotalReturnAmount) {
		this.userExpectTotalReturnAmount = userExpectTotalReturnAmount;
	}

	public String getAuditRemarks() {
		return auditRemarks;
	}

	public void setAuditRemarks(String auditRemarks) {
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

	public Double getActualTotalReturnAmount() {
		return actualTotalReturnAmount;
	}

	public void setActualTotalReturnAmount(Double actualTotalReturnAmount) {
		this.actualTotalReturnAmount = actualTotalReturnAmount;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getSalesman() {
		return salesman;
	}

	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}

	public Double getTotalReturnAmount() {
		return totalReturnAmount;
	}

	public void setTotalReturnAmount(Double totalReturnAmount) {
		this.totalReturnAmount = totalReturnAmount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getApplicationTime() {
		return applicationTime;
	}

	public void setApplicationTime(Date applicationTime) {
		this.applicationTime = applicationTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getCreateTime() {
		return createTime;
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

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	@Override
	public String toString() {
		return "ReturnOrder [id=" + id + ", returnOrderNo=" + returnOrderNo
				+ ", email=" + email + ", nickName=" + nickName + ", salesman="
				+ salesman + ", userExpectTotalReturnAmount="
				+ userExpectTotalReturnAmount + ", totalReturnAmount="
				+ totalReturnAmount + ", actualTotalReturnAmount="
				+ actualTotalReturnAmount + ", status=" + status
				+ ", applicationTime=" + applicationTime + ", remarks="
				+ remarks + ", auditRemarks=" + auditRemarks + ", createTime="
				+ createTime + ", createUser=" + createUser
				+ ", lastUpdateTime=" + lastUpdateTime + ", lastUpdateUser="
				+ lastUpdateUser + "]";
	}

}
