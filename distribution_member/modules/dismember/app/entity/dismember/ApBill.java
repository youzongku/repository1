package entity.dismember;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import services.base.utils.DateFormatUtils;

/**
 * 账期账单实体
 * @author zbc
 * 2017年3月5日 上午10:33:44
 */
public class ApBill implements Serializable{

	private static final long serialVersionUID = -692523517295213011L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * 账单总额
     */
    private BigDecimal totalAmount;

    /**
     * 应还金额
     */
    private BigDecimal arearAmount;

    /**
     * 分销账号
     */
    private String account;

    /**
     * 子账期id
     */
    private Integer apId;

    /**
     * 总已还金额
     */
    private BigDecimal rechargeLeft;
    
    /**
     * 核销人
     */
    private String verificationUser;
    
    /**
     * 核销时间
     */
    private Date verificationDate;
    
    /**
     * 创建时间
     */
    private Date createDate;
    
    /**
     * 创建人
     */
    private String createUser;
    
    /**
     * 是否进行核销操作标识
     */
    private Boolean isChargeOff;
    
    /**
     * 是否强制核销
     */
    private Boolean isForce;

    /**
     * 强制核销备注
     */
    private String remark;
    
    public ApBill(){
    	
    }
    public ApBill(AccountPeriodSlave slave,BigDecimal totalAmount, BigDecimal arearAmount,String createUser) {
		super();
		this.totalAmount = totalAmount;
		this.arearAmount = arearAmount;
		this.account = slave.getAccount();
		this.apId = slave.getId();
		this.rechargeLeft = slave.getRechargeLeft();
		this.createUser = createUser;
	}

	public Boolean getIsForce() {
		return isForce;
	}
	public void setIsForce(Boolean isForce) {
		this.isForce = isForce;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Boolean getIsChargeOff() {
		return isChargeOff;
	}

	public void setIsChargeOff(Boolean isChargeOff) {
		this.isChargeOff = isChargeOff;
	}

	public String getVerificationUser() {
		return verificationUser;
	}

	public void setVerificationUser(String verificationUser) {
		this.verificationUser = verificationUser;
	}

	public Date getVerificationDate() {
		return verificationDate;
	}

	public void setVerificationDate(Date verificationDate) {
		this.verificationDate = verificationDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getArearAmount() {
        return arearAmount;
    }

    public void setArearAmount(BigDecimal arearAmount) {
        this.arearAmount = arearAmount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getApId() {
        return apId;
    }

    public void setApId(Integer apId) {
        this.apId = apId;
    }

    public BigDecimal getRechargeLeft() {
        return rechargeLeft;
    }

    public void setRechargeLeft(BigDecimal rechargeLeft) {
        this.rechargeLeft = rechargeLeft;
    }
    
    public String getCreateDateStr() {
		return DateFormatUtils.getStrFromYYYYMMDDHHMMSS(createDate);
	}
	
	public String getVerificationDateStr() {
		return DateFormatUtils.getStrFromYYYYMMDDHHMMSS(verificationDate);
	}
}