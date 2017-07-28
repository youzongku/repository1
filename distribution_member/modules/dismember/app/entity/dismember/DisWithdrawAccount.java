package entity.dismember;

import java.io.Serializable;
import java.util.Date;

public class DisWithdrawAccount implements Serializable {

    private static final long serialVersionUID = -8514996716658356417L;

    private Integer id;

    private String withdrawAccount;

    private String accountUser;

    private String accountUnit;

    private String distributorEmail;

    private Date createTime;

    private Integer isBind;//0:解绑, 1:绑定

    private Integer accountType;//0:银行卡, 1:支付宝, 2:M站

    private String accountProvince;//开户行所在省

    private String accountCity;//开户行所在市
    
    /**是否有效*/
    private Boolean ifEffective;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWithdrawAccount() {
        return withdrawAccount;
    }

    public void setWithdrawAccount(String withdrawAccount) {
        this.withdrawAccount = withdrawAccount;
    }

    public String getAccountUser() {
        return accountUser;
    }

    public void setAccountUser(String accountUser) {
        this.accountUser = accountUser;
    }

    public String getAccountUnit() {
        return accountUnit;
    }

    public void setAccountUnit(String accountUnit) {
        this.accountUnit = accountUnit;
    }

    public String getDistributorEmail() {
        return distributorEmail;
    }

    public void setDistributorEmail(String distributorEmail) {
        this.distributorEmail = distributorEmail;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getIsBind() {
        return isBind;
    }

    public void setIsBind(Integer isBind) {
        this.isBind = isBind;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getAccountProvince() {
        return accountProvince;
    }

    public void setAccountProvince(String accountProvince) {
        this.accountProvince = accountProvince;
    }

    public String getAccountCity() {
        return accountCity;
    }

    public void setAccountCity(String accountCity) {
        this.accountCity = accountCity;
    }

	public Boolean getIfEffective() {
		return ifEffective;
	}

	public void setIfEffective(Boolean ifEffective) {
		this.ifEffective = ifEffective;
	}
    
}