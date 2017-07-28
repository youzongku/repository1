package entity.dismember;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import utils.dismember.DateUtils;

/**
 * 登录历史记录实体
 * Created by luwj on 2015/11/24.
 */
public class LoginHistory implements Serializable{

    private static final long serialVersionUID = 1L;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
    private Date dtimestamp;

    private String cemail;

    private Integer iwebsiteid;

    private String cltc;

    private String cstc;

    private String cclientip;
    
    private String lastLoginTime;//仅页面展示用

    public String getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		if(null == lastLoginTime){
			this.lastLoginTime = "";
		}else{
			this.lastLoginTime = lastLoginTime;	
		}
	}

    public Date getDtimestamp() {
        return dtimestamp;
    }

    public void setDtimestamp(Date dtimestamp) {
        this.dtimestamp = dtimestamp;
        this.setLastLoginTime(DateUtils.date2FullDateTimeString(dtimestamp));
    }

    public String getCemail() {
        return cemail;
    }

    public void setCemail(String cemail) {
        this.cemail = cemail;
    }

    public Integer getIwebsiteid() {
        return iwebsiteid;
    }

    public void setIwebsiteid(Integer iwebsiteid) {
        this.iwebsiteid = iwebsiteid;
    }

    public String getCltc() {
        return cltc;
    }

    public void setCltc(String cltc) {
        this.cltc = cltc;
    }

    public String getCstc() {
        return cstc;
    }

    public void setCstc(String cstc) {
        this.cstc = cstc;
    }

    public String getCclientip() {
        return cclientip;
    }

    public void setCclientip(String cclientip) {
        this.cclientip = cclientip;
    }
}