package dto.openapi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 保存了线下转账的-数据（把此表当做采购单的附属表，保存了线下转账的数据）
 * @author huangjc
 * @date 2016年12月2日
 */
public class PurchaseAudit implements Serializable {

    private static final long serialVersionUID = -3026856991912062952L;

    private Integer id;//

    private String transferCard;//转账卡号

    private Integer status;//1、待审核   2、审核通过 3、审核不通过

    private String statusName;//非数据库字段，用于页面展示

    private String purchaseNo;//订单号

    private String transferNumber;//交易流水号

    private BigDecimal transferAmount;//转账金额

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date transferTime;//转账时间

    private String screenshotUrl;//截图路径

    private String auditReasons;//审核理由

    private String auditRemark;//审核备注

    private String applyRemark;//申请备注

    private String transferName;//开户名

    private String email;//分销账号

    private Integer recipientCardId;//收款账号ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;//创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;//更新时间
    
    private String transferType;//转账类型（支付宝微信银行卡之类）
    
    private String createUser;//录入人
    
    private BigDecimal orderAmount;//订单金额
    
    private String recipientAccount;//收款账号

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderDate;//下单时间

    private BigDecimal receivedAmount;//实收金额

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receivedTime;//到账时间
    
    private String customerService;//客服账号
    
    public String getCustomerService() {
		return customerService;
	}

	public void setCustomerService(String customerService) {
		this.customerService = customerService;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getRecipientAccount() {
		return recipientAccount;
	}

	public void setRecipientAccount(String recipientAccount) {
		this.recipientAccount = recipientAccount;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransferCard() {
        return transferCard;
    }

    public void setTransferCard(String transferCard) {
        this.transferCard = transferCard;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return !Strings.isNullOrEmpty(statusName) ? statusName :
                status == 1 ? "待审核" :
                status == 2 ? "审核通过" :
                status == 3 ? "审核不通过" : "";
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getPurchaseNo() {
        return purchaseNo;
    }

    public void setPurchaseNo(String purchaseNo) {
        this.purchaseNo = purchaseNo;
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

    public Date getTransferTime() {
        return transferTime;
    }

    public void setTransferTime(Date transferTime) {
        this.transferTime = transferTime;
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

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getApplyRemark() {
        return applyRemark;
    }

    public void setApplyRemark(String applyRemark) {
        this.applyRemark = applyRemark;
    }

    public String getTransferName() {
        return transferName;
    }

    public void setTransferName(String transferName) {
        this.transferName = transferName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getRecipientCardId() {
        return recipientCardId;
    }

    public void setRecipientCardId(Integer recipientCardId) {
        this.recipientCardId = recipientCardId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public Date getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Date receivedTime) {
        this.receivedTime = receivedTime;
	}

	@Override
	public String toString() {
		return "PurchaseAudit [id=" + id + ", transferCard=" + transferCard
				+ ", status=" + status + ", statusName=" + statusName
				+ ", purchaseNo=" + purchaseNo + ", transferNumber="
				+ transferNumber + ", transferAmount=" + transferAmount
				+ ", transferTime=" + transferTime + ", screenshotUrl="
				+ screenshotUrl + ", auditReasons=" + auditReasons
				+ ", auditRemark=" + auditRemark + ", applyRemark="
				+ applyRemark + ", transferName=" + transferName + ", email="
				+ email + ", recipientCardId=" + recipientCardId
				+ ", createDate=" + createDate + ", updateDate=" + updateDate
				+ ", transferType=" + transferType + ", createUser="
				+ createUser + ", orderAmount=" + orderAmount
				+ ", recipientAccount=" + recipientAccount + ", orderDate="
				+ orderDate + ", receivedAmount=" + receivedAmount
				+ ", receivedTime=" + receivedTime + ", customerService="
				+ customerService + "]";
	}
}