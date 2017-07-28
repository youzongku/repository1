package vo.dismember;

import java.io.Serializable;

/**
 * 用于存储用户登录状态
 * Created by LSL on 2015/12/23.
 */
public class LoginContext implements Serializable {

    private static final long serialVersionUID = 1835007269590433302L;

    private String userID;//用户ID

    private String username;//用户名

    private String email;//用户邮箱
    
    private String distributionmode;//用户经营模式

    private String ltc;//LongTermCookie

    private String stc;//ShortTermCookie
    
    private Integer distributionType;
    
    
    public Integer getDistributionType() {
		return distributionType;
	}

	public void setDistributionType(Integer distributionType) {
		this.distributionType = distributionType;
	}

	/**
     * 用户是否登录
     * @return
     */
    public boolean isLogin() {
        return userID == null ? false : true;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLtc() {
        return ltc;
    }

    public void setLtc(String ltc) {
        this.ltc = ltc;
    }

    public String getStc() {
        return stc;
    }

    public void setStc(String stc) {
        this.stc = stc;
    }

	public String getDistributionmode() {
		return distributionmode;
	}

	public void setDistributionmode(String distributionmode) {
		this.distributionmode = distributionmode;
	}
}
