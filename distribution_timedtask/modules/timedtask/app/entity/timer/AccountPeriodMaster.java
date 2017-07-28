package entity.timer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 账期主表实体
 * @author zbc
 * 2017年2月24日 下午5:54:50
 */
public class AccountPeriodMaster implements Serializable{
	
	private static final long serialVersionUID = -1721469187126221123L;

	public static final int PERIOD_TYPE_DATE = 0;
	
	public static final int PERIOD_TYPE_MONTH = 1;
	/**
	 * 固定月结
	 */
	public static final int PERIOD_TYPE_MONTH_STATEMENT = 2;
    /**
     * 账期从表
     */
    private Integer id;

    /**
     * 分销商账号
     */
    private String account;

    /**
     * 账期额度
     */
    private BigDecimal totalLimit;

    /**
     * 责任人 
     */
    private String dutyOfficer;

    /**
     * OA审批号
     */
    private String oaAuditCode;

    /**
     * 合同号
     */
    private String contractNo;

    /**
     * 周期类型:0：天 ，1：自然月
     */
    private Integer periodType;

    /**
     * 周期长度
     */
    private Integer periodLength;

    /**
     * 总还款额度
     */
    private BigDecimal rechargeTotal;

    /**
     * 剩余还款额度
     */
    private BigDecimal rechargeLeft;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createDate;
    
    /**
     * 更新时间
     */
    private Date updateDate;
    
    /**
     * 昵称
     */
    private String nickName;
    
    /**
     * 分销渠道描述
     */
    private String disModeDesc;
    
    /**
     * 分销渠道
     */
    private String disMode;
    
    /**
     * 已用额度
     */
    private BigDecimal usedLimit;
    
    /**
     * 生效子账期数量，用于判断账期是否可以修改
     */
    private Integer effectCount;
    
	public Integer getEffectCount() {
		return effectCount;
	}

	public void setEffectCount(Integer effectCount) {
		this.effectCount = effectCount;
	}

	public BigDecimal getUsedLimit() {
		return usedLimit;
	}

	public void setUsedLimit(BigDecimal usedLimit) {
		this.usedLimit = usedLimit;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getDisModeDesc() {
		return disModeDesc;
	}

	public void setDisModeDesc(String disModeDesc) {
		this.disModeDesc = disModeDesc;
	}

	public String getDisMode() {
		return disMode;
	}

	public void setDisMode(String disMode) {
		this.disMode = disMode;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public BigDecimal getTotalLimit() {
		return totalLimit;
	}

	public void setTotalLimit(BigDecimal totalLimit) {
		this.totalLimit = totalLimit;
	}

	public String getDutyOfficer() {
        return dutyOfficer;
    }

    public void setDutyOfficer(String dutyOfficer) {
        this.dutyOfficer = dutyOfficer;
    }

    public String getOaAuditCode() {
        return oaAuditCode;
    }

    public void setOaAuditCode(String oaAuditCode) {
        this.oaAuditCode = oaAuditCode;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public Integer getPeriodLength() {
        return periodLength;
    }

    public void setPeriodLength(Integer periodLength) {
        this.periodLength = periodLength;
    }

    public BigDecimal getRechargeTotal() {
        return rechargeTotal;
    }

    public void setRechargeTotal(BigDecimal rechargeTotal) {
        this.rechargeTotal = rechargeTotal;
    }

    public BigDecimal getRechargeLeft() {
        return rechargeLeft;
    }

    public void setRechargeLeft(BigDecimal rechargeLeft) {
        this.rechargeLeft = rechargeLeft;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
   
    public String getPeriodDesc(){
    	if(this.periodLength != null && this.periodType != null){
    		return this.periodLength + (this.periodType == PERIOD_TYPE_DATE ?"天":"个自然月");
    	}
    	return null;
    }
    /**
     * 待还款额度
     */
    public BigDecimal getLeftLimit() {
		return totalLimit.subtract(usedLimit);
	}
    
}