package entity.dismember;

import java.util.Date;
/**
 * 账期提醒设置日志
 * 
 * @author huangjc
 */
public class ApReminderSettingLog {
    private Integer id;

    private Integer reminderSettingId;

    private Boolean enable;

    private Integer daysAgo;

    private Date createDate;

    private String createUser;
    
    private Boolean setSuc;// 设置是否成功
    
    public ApReminderSettingLog(Integer reminderSettingId,Boolean enable,Integer daysAgo,String createUser,Boolean setSuc){
    	this.reminderSettingId = reminderSettingId;
    	this.enable = enable;
    	this.daysAgo = daysAgo;
    	this.createUser = createUser;
    	this.setSuc = setSuc;
    }
    
    public ApReminderSettingLog(){}

    public Boolean getSetSuc() {
		return setSuc;
	}

	public void setSetSuc(Boolean setSuc) {
		this.setSuc = setSuc;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReminderSettingId() {
		return reminderSettingId;
	}

	public void setReminderSettingId(Integer reminderSettingId) {
		this.reminderSettingId = reminderSettingId;
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
}