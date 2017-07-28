package entity.dismember;

import java.util.Date;
/**
 * 账期提醒设置
 * @author huangjc
 *
 */
public class ApReminderSetting {
    private Integer id;
    
    private String account;// 分销商账户

    private Boolean enable;

    private Integer daysAgo;

    private Date createDate;

    private String createUser;

    private Date lastUpdateDate;

    private String lastUpdateUser;
    
    public ApReminderSetting(){}
    
    public ApReminderSetting(String account, Boolean enable,Integer daysAgo,String createUser){
    	this.account = account;
    	this.enable = enable;
    	this.daysAgo = daysAgo;
    	this.createUser = createUser;
    }

    public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Integer getDaysAgo() {
        return daysAgo;
    }

    public void setDaysAgo(Integer daysAgo) {
        this.daysAgo = daysAgo;
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

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

	@Override
	public String toString() {
		return "ApReminderSetting [id=" + id + ", enable=" + enable + ", daysAgo=" + daysAgo + ", createDate="
				+ createDate + ", createUser=" + createUser + ", lastUpdateDate=" + lastUpdateDate + ", lastUpdateUser="
				+ lastUpdateUser + "]";
	}
}