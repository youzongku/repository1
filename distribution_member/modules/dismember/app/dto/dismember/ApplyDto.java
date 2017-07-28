package dto.dismember;

import java.io.Serializable;
import java.math.BigDecimal;

public class ApplyDto implements Serializable{
	
	private static final long serialVersionUID = 2577282742177913443L;

	private Integer id;

	private String name;// 开户名

	private String recipientNum;// 收款账户

	private String transferNumber;// 交易号

	private String transferCard;// 汇款账户

	private BigDecimal transferAmount;// 转账金额

	private String transTime;// 转账时间

	private String auditState;// 初审状态----0:待审核,1:审核不通过,2:审核通过,3:审核异常

	private String receiptCard;// 收款账户

	private String receiptName;// 收款方

	private String email;// 用户名

	private String screenshotUrl;// 截图路径

	private String auditReasons;// 审核理由

	private String reviewState;// 复审状态----1:审核不通过,2:审核通过,3:审核异常,4:待复审

	private String actualTime;// 实际到账日期

	private BigDecimal actualAmount;// 实际到账金额

	private String state;// 分销商我的申请展示状态

	private Integer audit;

	private String auditRemark;// 审核备注

	private String onlineApplyNo;// 在线充值单号

	private String transferType;// 充值渠道
	
	private String reAuditRemark;//复审备注
	
	private String applyRemark;//充值申请备注
	
	private String applyMan;//申请人
	
	private String nickName;//分销商昵称
	
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getReAuditRemark() {
		return reAuditRemark;
	}

	public void setReAuditRemark(String reAuditRemark) {
		this.reAuditRemark = reAuditRemark;
	}

	public String getOnlineApplyNo() {
		return onlineApplyNo;
	}

	public void setOnlineApplyNo(String onlineApplyNo) {
		this.onlineApplyNo = onlineApplyNo;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public String getAuditRemark() {
		return auditRemark;
	}

	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark;
	}

	public Integer getAudit() {
		return audit;
	}

	public void setAudit(Integer audit) {
		this.audit = audit;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public String getActualTime() {
		return actualTime;
	}

	public void setActualTime(String actualTime) {
		this.actualTime = actualTime;
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

	public String getReviewState() {
		return reviewState;
	}

	public void setReviewState(String reviewState) {
		this.reviewState = reviewState;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRecipientNum() {
		return recipientNum;
	}

	public void setRecipientNum(String recipientNum) {
		this.recipientNum = recipientNum;
	}

	public String getTransferNumber() {
		return transferNumber;
	}

	public void setTransferNumber(String transferNumber) {
		this.transferNumber = transferNumber;
	}

	public String getTransferCard() {
		return transferCard;
	}

	public void setTransferCard(String transferCard) {
		this.transferCard = transferCard;
	}

	public String getAuditState() {
		return auditState;
	}

	public void setAuditState(String auditState) {
		this.auditState = auditState;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getApplyRemark() {
		return applyRemark;
	}

	public void setApplyRemark(String applyRemark) {
		this.applyRemark = applyRemark;
	}

	public String getApplyMan() {
		return applyMan;
	}

	public void setApplyMan(String applyMan) {
		this.applyMan = applyMan;
	}

}
