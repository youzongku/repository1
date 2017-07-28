package entity.timer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 分销商账单(账户资金明细)表实体
 */
public class DisBill implements Serializable {

    private static final long serialVersionUID = -401355755667623691L;
    
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private Integer id;

    private Integer accountId;

    private BigDecimal amount;
    
    private BigDecimal balance;//余额

    private String purpose;// 交易类型：1、充值 2、提现 3、采购支付 4、退款 5、支付运费 6、采购支付含运费
    
    private String purposeStr;

    private Integer targetAccount;

    private String remark;

    private Date createDate;

    private String serialNumber;
    
    private String paymentType;//支付类型：支付宝，微信，线下
    
    private Integer applyId;//申请单号
    
    private String create;//仅用于前台显示时间
    
    private String sourceCard;//交易卡号或者账户
    
    /**0余额*/
    public static final int SOURCES_BALANCE = 0;
    /**1临时*/
    public static final int SOURCES_TEMPORARY = 1;
    /**2永久*/
    public static final int SOURCES_PERMANNENT = 2;
    public static final int SOURCES_ALL = 3;
    private Integer sources;//资金来源（0：余额  1：临时额度 2：永久额度）;
    
    private BigDecimal creditLimitBalance; //额度金额
    
    private String email;//分销账号，用户支付成功后需要产生交易记录
    
    private String tradeNo;//交易流水号
  
	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigDecimal getCreditLimitBalance() {
		return creditLimitBalance;
	}

	public void setCreditLimitBalance(BigDecimal creditLimitBalance) {
		this.creditLimitBalance = creditLimitBalance;
	}

	public String getPurposeStr() {
		return purposeStr;
	}

	public void setPurposeStr(String purposeStr) {
		this.purposeStr = purposeStr;
	}

	public DisBill() {
		super();
	}
    
	public DisBill(Integer accountId, BigDecimal amount, String purpose, String serialNumber, String paymentType,
			Integer applyId, String sourceCard) {
		super();
		this.accountId = accountId;
		this.amount = amount;
		this.purpose = purpose;
		this.serialNumber = serialNumber;
		this.paymentType = paymentType;
		this.applyId = applyId;
		this.sourceCard = sourceCard;
	}

	public Integer getSources() {
		return sources;
	}

	public void setSources(Integer sources) {
		this.sources = sources;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getSourceCard() {
		return sourceCard;
	}

	public void setSourceCard(String sourceCard) {
		this.sourceCard = sourceCard;
	}

	public String getCreate() {
		return create;
	}

	public void setCreate(String create) {
		if(null == createDate){
			this.create = "";
		}else{
			this.create = create;			
		}
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Integer getApplyId() {
		return applyId;
	}

	public void setApplyId(Integer applyId) {
		this.applyId = applyId;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Integer getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(Integer targetAccount) {
        this.targetAccount = targetAccount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
        this.setCreate(sdf.format(createDate));
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

	@Override
	public String toString() {
		return "DisBill [金额 ：" + amount + ", 交易类型 ：" + purpose
				+ ", 备注 ：" + remark + ", 创建时间 ：" + createDate
				+ ", 流水号 ：" + serialNumber + ", 支付类型 ：" + paymentType + ", 申请Id ：" + applyId +", 交易流水号 ：" + tradeNo + "]";
	}
}