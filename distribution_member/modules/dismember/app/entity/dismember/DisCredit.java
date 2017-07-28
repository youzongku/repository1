package entity.dismember;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import services.base.utils.DateFormatUtils;

/**
 * 
 * change by zbc 
 * 信用额度实体 -> 账期实体
 * 新旧数据适应
 * 2017年2月17日 下午2:45:05
 */
public class DisCredit implements Serializable{

	private static final long serialVersionUID = -4039010642639869228L;
	
	/**
	 * 主键
	 */
	private Integer id;

    /**
     * 分销商账号
     */
    private String email;

    /**
     * 账期额度（旧：信用额度）
     */
    private BigDecimal creditLimit;

    /**
     * 已用账期额度(旧：已使用额度)
     */
    private BigDecimal usedAmount;

    /**
     * 创建人
     */
    private String createuser;

    /**
     * 账期额度状态 : (旧：1：待使用 2：使用中 3：已失效)
     * 1  未生效
     * 2  可使用
     * 3  待还款 
     * 4  已逾期 （账户冻结）
	 * 5      禁用中 （无法透支）
	 * 6      已完结  
     */
    private Integer limitState;

    /**
     * (旧:是否已还款)
     */
    private Boolean isFinished;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;
    
    /**
     * oa审批号
     */
    private String auditCode;

    /**
     * 现在处理掉临时额度，永久额度作为账期处理（旧：额度类型）
     */
    private Integer redit;//额度类型 （1：临时额度，2：永久额度，见CreditTypes）;

    /**
     * 创建时间
     */
    private Date createdate;

    /**
     * 更新时间
     */
    private Date updatedate;
    
    /**
     * (旧:额度总额)
     */
    private BigDecimal totalCreditLimit;
    
    /**
     * (旧:已使用额度总额)
     */
    private BigDecimal totalUsedAmount; 
    
    /**
     * 
     * (旧)该额度是否激活
     *  true 激活
     *  false 禁用 
     */
    private Boolean isActivated;
    

    
    public Boolean getIsActivated() {
		return isActivated;
	}

	public void setIsActivated(Boolean isActivated) {
		this.isActivated = isActivated;
	}

	public BigDecimal getTotalCreditLimit() {
		return totalCreditLimit;
	}

	public void setTotalCreditLimit(BigDecimal totalCreditLimit) {
		this.totalCreditLimit = totalCreditLimit;
	}

	public BigDecimal getTotalUsedAmount() {
		return totalUsedAmount;
	}

	public void setTotalUsedAmount(BigDecimal totalUsedAmount) {
		this.totalUsedAmount = totalUsedAmount;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(BigDecimal usedAmount) {
        this.usedAmount = usedAmount;
    }

    public String getCreateuser() {
        return createuser;
    }

    public void setCreateuser(String createuser) {
        this.createuser = createuser;
    }

    public Integer getLimitState() {
        return limitState;
    }

    public void setLimitState(Integer limitState) {
        this.limitState = limitState;
    }

    public Boolean getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(Boolean isFinished) {
        this.isFinished = isFinished;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getAuditCode() {
        return auditCode;
    }

    public void setAuditCode(String auditCode) {
        this.auditCode = auditCode;
    }

    public Integer getRedit() {
        return redit;
    }

    public void setRedit(Integer redit) {
        this.redit = redit;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

	public Date getUpdatedate() {
		return updatedate;
	}

	public void setUpdatedate(Date updatedate) {
		this.updatedate = updatedate;
	}

	public String getStartTimeStr() {
		return formartDate(startTime);
	}

	public String getEndTimeStr() {
		return formartDate(endTime);
	}

	private static String formartDate(Date date){
		return date != null?DateFormatUtils.getStrFromYYYYMMDDHHMMSS(date):null;
	}
}