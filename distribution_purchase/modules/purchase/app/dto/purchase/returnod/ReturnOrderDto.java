package dto.purchase.returnod;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import entity.purchase.returnod.ReturnOrderDetail;
import entity.purchase.returnod.ReturnOrderLog;
import utils.purchase.DateUtils;
import utils.purchase.PriceFormatUtil;

public class ReturnOrderDto {
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

	private List<ReturnOrderDetail> details;
	private List<ReturnOrderLog> logs;
	
	/**
	 * 均摊价小计
	 * @return
	 */
	public Double getCapfeeSubtotal(){
		if(details==null || details.size()==0){
			return 0.00;
		}
		
		BigDecimal capfeeSubtotal = new BigDecimal(0.00);
		for(ReturnOrderDetail detail : details){
			if(detail.getCapfee()!=null && detail.getReturnQty()!=null && detail.getReturnQty()!=0){
				capfeeSubtotal = capfeeSubtotal.add(new BigDecimal(detail.getCapfee()).multiply(new BigDecimal(detail.getReturnQty())));
			}
		}
		return PriceFormatUtil.toFix2(capfeeSubtotal);	
	}
	
	/**
	 * 采购价小计
	 * @return
	 */
	public Double getPurchasePriceSubtotal(){
		if(details==null || details.size()==0){
			return 0.00;
		}
		
		BigDecimal purchasePriceSubtotal = new BigDecimal(0.00);
		for(ReturnOrderDetail detail : details){
			if(detail.getPurchasePrice()!=null && detail.getReturnQty()!=null && detail.getReturnQty()!=0){
				purchasePriceSubtotal = purchasePriceSubtotal.add(new BigDecimal(detail.getPurchasePrice()).multiply(new BigDecimal(detail.getReturnQty())));
			}
		}
		return PriceFormatUtil.toFix2(purchasePriceSubtotal);
	}

	public List<ReturnOrderLog> getLogs() {
		return logs;
	}

	public void setLogs(List<ReturnOrderLog> logs) {
		this.logs = logs;
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

	public Double getActualTotalReturnAmount() {
		return actualTotalReturnAmount;
	}

	public void setActualTotalReturnAmount(Double actualTotalReturnAmount) {
		this.actualTotalReturnAmount = actualTotalReturnAmount;
	}

	public Integer getStatus() {
		return status;
	}

	public String getStatusStr() {
		switch (status) {
		case 1:
			return "待审核";
		case 2:
			return "已退款";
		case 3:
			return "审核不通过";
		case 4:
			return "已取消";
		default:
			return "未知状态";
		}
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getApplicationTime() {
		return applicationTime;
	}

	public String getApplicationTimeStr() {
		if (applicationTime == null)
			return "";
		String applicationTimeStr = DateUtils
				.date2FullDateTimeString(applicationTime);
		return applicationTimeStr;
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

	public List<ReturnOrderDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ReturnOrderDetail> details) {
		this.details = details;
	}

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

	@Override
	public String toString() {
		return "ReturnOrderDto [id=" + id + ", returnOrderNo=" + returnOrderNo
				+ ", email=" + email + ", nickName=" + nickName + ", salesman="
				+ salesman + ", userExpectTotalReturnAmount="
				+ userExpectTotalReturnAmount + ", totalReturnAmount="
				+ totalReturnAmount + ", actualTotalReturnAmount="
				+ actualTotalReturnAmount + ", status=" + status
				+ ", applicationTime=" + applicationTime + ", remarks="
				+ remarks + ", auditRemarks=" + auditRemarks + ", createTime="
				+ createTime + ", createUser=" + createUser
				+ ", lastUpdateTime=" + lastUpdateTime + ", lastUpdateUser="
				+ lastUpdateUser + ", details=" + details + ", logs=" + logs
				+ "]";
	}

}
