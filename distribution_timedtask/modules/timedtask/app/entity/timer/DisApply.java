package entity.timer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisApply implements Serializable {

	private static final long serialVersionUID = 1821071935594800371L;

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private Integer id;

	private String distributorName;// 开户名

	private String transferCard;// 转账卡号

	private Date transferTime;// 转账时间

	private Date actualDate;// 实际到账日期，date类型

	private String transferNumber;// 交易号

	private BigDecimal transferAmount;// 转账金额

	private String distributorTelphone;// 电话号码

	private Integer auditState;// 申请状态----0:待审核,1:审核不通过,2:审核通过,3:审核异常

	private String transferType;// 转账类型（线下，微信，支付宝）

	private String transferDesc;// 转账描述

	private Date createdate;// 申请创建时间

	private Date updatedate;// 申请更新时间

	private Boolean isConfirm;// 确认提交申请（默认true）

	private String email;// 用户邮箱
	/**
	 * 申请类型:
	 * 1：充值
	 * 2：提现
	 * 3：采购支付
	 * 4：退款
	 * 5：运费支付
	 * 6：采购支付含运费
	 */
	private String applyType;

	private String transTime;// 转换为transferTime

	private Integer recipientId;// 转账目标卡号ID

	private BigDecimal actualAmount;// 实际到账金额

	private Integer reviewState;// 复审状态----1:审核不通过,2:审核通过,3:审核异常,4:待复审

	private String screenshotUrl;// 截图路径

	private String auditReasons;// 审核理由

	private String receiptCard;// 收款账户

	private String receiptName;// 收款方

	private String actualTime;// 实际到账日期 actualDate的String表示

	private String password;// 用于临时存储支付密码密文(MD5加密)

	private String paycode;// 用于临时存储支付密码明文

	private String auditRemark;// 审核备注

	private String onlineApplyNo;// 在线充值单号
	
	//add by lzl
	private String isBackStage;//是否是后台操作
	
	private String reAuditRemark;//复审备注
	
	private String applyRemark;//充值申请备注

	//add by luwj
	private BigDecimal withdrawAmount;//提现金额

	private Integer withdrawAccountId;//提现账号ID
	
	/**当月第一天*/
	private String curMonthFirstDay;
	/**当月最后一天*/
	private String curMonthLastDay;
	/**手续费*/
	private BigDecimal counterFee;
	/**支付密码验证码*/
	private String payCaptcha;
	
	private String applyMan;//充值申请人

	public String getReAuditRemark() {
		return reAuditRemark;
	}

	public void setReAuditRemark(String reAuditRemark) {
		this.reAuditRemark = reAuditRemark;
	}

	public String getApplyRemark() {
		return applyRemark;
	}

	public void setApplyRemark(String applyRemark) {
		this.applyRemark = applyRemark;
	}

	public String getAuditRemark() {
		return auditRemark;
	}

	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark;
	}

	public String getPaycode() {
		return paycode;
	}

	public void setPaycode(String paycode) {
		this.paycode = paycode;
	}

	public String getActualTime() {
		return actualTime;
	}

	public void setActualTime(String actualTime) throws ParseException {
		this.actualTime = actualTime;
		this.actualDate = sdf.parse(actualTime);
	}

	public Integer getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(Integer recipientId) {
		this.recipientId = recipientId;
	}

	public String getTransTime() {
		return transTime;
	}

	public Date getActualDate() {
		return actualDate;
	}

	public void setActualDate(Date actualDate) {
		this.actualDate = actualDate;
	}

	public void setTransTime(String transTime) throws ParseException {
		this.transTime = transTime;
		this.transferTime = sdf.parse(transTime);
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDistributorName() {
		return distributorName;
	}

	public void setDistributorName(String distributorName) {
		this.distributorName = distributorName;
	}

	public String getTransferCard() {
		return transferCard;
	}

	public void setTransferCard(String transferCard) {
		this.transferCard = transferCard;
	}

	public Date getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(Date transferTime) {
		this.transferTime = transferTime;
	}

	public String getTransferNumber() {
		return transferNumber;
	}

	public void setTransferNumber(String transferNumber) {
		this.transferNumber = transferNumber;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	public String getDistributorTelphone() {
		return distributorTelphone;
	}

	public void setDistributorTelphone(String distributorTelphone) {
		this.distributorTelphone = distributorTelphone;
	}

	public Integer getAuditState() {
		return auditState;
	}

	public void setAuditState(Integer auditState) {
		this.auditState = auditState;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public String getTransferDesc() {
		return transferDesc;
	}

	public void setTransferDesc(String transferDesc) {
		this.transferDesc = transferDesc;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public Date getUpdatedate() {
		return updatedate;
	}

	public void setUpdatedate(Date updatedate) {
		this.updatedate = updatedate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public Boolean getIsConfirm() {
		return isConfirm;
	}

	public void setIsConfirm(Boolean isConfirm) {
		this.isConfirm = isConfirm;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public Integer getReviewState() {
		return reviewState;
	}

	public void setReviewState(Integer reviewState) {
		this.reviewState = reviewState;
	}

	public String getScreenshotUrl() {
		return screenshotUrl;
	}

	public void setScreenshotUrl(String screenshotUrl) {
		this.screenshotUrl = screenshotUrl;
	}

	public String getAuditReasons() {
		return auditReasons;
	}

	public void setAuditReasons(String auditReasons) {
		this.auditReasons = auditReasons;
	}

	public String getReceiptCard() {
		return receiptCard;
	}

	public void setReceiptCard(String receiptCard) {
		this.receiptCard = receiptCard;
	}

	public String getReceiptName() {
		return receiptName;
	}

	public void setReceiptName(String receiptName) {
		this.receiptName = receiptName;
	}

	public String getOnlineApplyNo() {
		return onlineApplyNo;
	}

	public void setOnlineApplyNo(String onlineApplyNo) {
		this.onlineApplyNo = onlineApplyNo;
	}

	public String getIsBackStage() {
		return isBackStage;
	}

	public void setIsBackStage(String isBackStage) {
		this.isBackStage = isBackStage;
	}

	public BigDecimal getWithdrawAmount() {
		return withdrawAmount;
	}

	public void setWithdrawAmount(BigDecimal withdrawAmount) {
		this.withdrawAmount = withdrawAmount;
	}

	public Integer getWithdrawAccountId() {
		return withdrawAccountId;
	}

	public void setWithdrawAccountId(Integer withdrawAccountId) {
		this.withdrawAccountId = withdrawAccountId;
	}

	@Override
	public String toString() {
		return "DisApply [id=" + id + ", distributorName=" + distributorName
				+ ", transferCard=" + transferCard + ", transferTime="
				+ transferTime + ", actualDate=" + actualDate
				+ ", transferNumber=" + transferNumber + ", transferAmount="
				+ transferAmount + ", distributorTelphone="
				+ distributorTelphone + ", auditState=" + auditState
				+ ", transferType=" + transferType + ", transferDesc="
				+ transferDesc + ", createdate=" + createdate + ", updatedate="
				+ updatedate + ", isConfirm=" + isConfirm + ", email=" + email
				+ ", applyType=" + applyType + ", transTime=" + transTime
				+ ", recipientId=" + recipientId + ", actualAmount="
				+ actualAmount + ", reviewState=" + reviewState
				+ ", screenshotUrl=" + screenshotUrl + ", auditReasons="
				+ auditReasons + ", receiptCard=" + receiptCard
				+ ", receiptName=" + receiptName + ", actualTime=" + actualTime
				+ ", password=" + password + ", paycode=" + paycode
				+ ", auditRemark=" + auditRemark + ", onlineApplyNo="
				+ onlineApplyNo + "]";
	}

	public String getCurMonthFirstDay() {
		return curMonthFirstDay;
	}

	public void setCurMonthFirstDay(String curMonthFirstDay) {
		this.curMonthFirstDay = curMonthFirstDay;
	}

	public String getCurMonthLastDay() {
		return curMonthLastDay;
	}

	public void setCurMonthLastDay(String curMonthLastDay) {
		this.curMonthLastDay = curMonthLastDay;
	}

	public BigDecimal getCounterFee() {
		return counterFee;
	}

	public void setCounterFee(BigDecimal counterFee) {
		this.counterFee = counterFee;
	}

	public String getPayCaptcha() {
		return payCaptcha;
	}

	public void setPayCaptcha(String payCaptcha) {
		this.payCaptcha = payCaptcha;
	}

	public String getApplyMan() {
		return applyMan;
	}

	public void setApplyMan(String applyMan) {
		this.applyMan = applyMan;
	}
}