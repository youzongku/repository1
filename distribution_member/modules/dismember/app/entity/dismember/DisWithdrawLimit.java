package entity.dismember;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DisWithdrawLimit implements Serializable {

    private static final long serialVersionUID = 2587771911383480062L;

    /**主键ID*/
    private Integer id;
    
    /**每月最多提现次数*/
    private Integer permonthTimes;

    /**单次提现最低限额*/
    private BigDecimal pertimeLeast;

    /**分销商邮箱账号*/
    private String distributorEmail;
    
    /**创建人*/
    private String createUser;

    /**创建时间*/
    private Date createTime;
    
    /**更新人*/
    private String updateUser;
    
    /**更新时间*/
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPermonthTimes() {
        return permonthTimes;
    }

    public void setPermonthTimes(Integer permonthTimes) {
        this.permonthTimes = permonthTimes;
    }

    public BigDecimal getPertimeLeast() {
        return pertimeLeast;
    }

    public void setPertimeLeast(BigDecimal pertimeLeast) {
        this.pertimeLeast = pertimeLeast;
    }

    public String getDistributorEmail() {
        return distributorEmail;
    }

    public void setDistributorEmail(String distributorEmail) {
        this.distributorEmail = distributorEmail;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
    
}