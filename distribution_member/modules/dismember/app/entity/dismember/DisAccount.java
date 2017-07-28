package entity.dismember;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author luwj
 *
 */
public class DisAccount implements Serializable {
	
	private static final long serialVersionUID = 6237139431947888139L;

	private Integer id;

    private String payPass;//支付密码

    private BigDecimal balance;//余额

    private BigDecimal frozenAmount;//暂冻结金额

    private Boolean frozen;//是否冻结

    private String createUser;

    private Date createDate;
    
    private Date updateDate;

    private String email;//用户邮箱
    
    private LoginHistory historys;//最近一次登陆历史
    
    /**输入错误次数*/
    private Integer inputErrorNumTimes;
    /**禁用时间*/
    private Date disableTime;
    
    private Double periodFrozen;//账期冻结金额：当账期剩余到达该金额时冻结账户
    
    public Double getPeriodFrozen() {
		return periodFrozen;
	}

	public void setPeriodFrozen(Double periodFrozen) {
		this.periodFrozen = periodFrozen;
	}

	public LoginHistory getHistorys() {
		return historys;
	}

	public void setHistorys(LoginHistory historys) {
		this.historys = historys;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPayPass() {
        return payPass;
    }

    public void setPayPass(String payPass) {
        this.payPass = payPass;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(BigDecimal frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    public Boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(Boolean frozen) {
		this.frozen = frozen;
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

    public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

	public Integer getInputErrorNumTimes() {
		return inputErrorNumTimes;
	}

	public void setInputErrorNumTimes(Integer inputErrorNumTimes) {
		this.inputErrorNumTimes = inputErrorNumTimes;
	}

	public Date getDisableTime() {
		return disableTime;
	}

	public void setDisableTime(Date disableTime) {
		this.disableTime = disableTime;
	}
    
}