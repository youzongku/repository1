package dto.dismember;

import java.io.Serializable;

/**
 * Created by luwj on 2015/11/25.
 */
public class MemberForm implements Serializable {
    private static final long serialVersionUID = -3601911701258823562L;
    private Integer id;
    private String userName;//用户名
    private String passWord;//登录密码
    private String nickName;//呢称
    private String realName;//姓名
    private Integer gender;//性别
    private String birthday;//生日
    private String day;
    private String month;
    private String year;
    private String email;//邮箱
    private String telphone;//手机
    private String profile;//简介
    private String headImg;//头像
    private String registerInviteCode;//注册邀请码
    private String selfInviteCode;//用户自身邀请码
    private String erpAccount;//erp账号
    private Integer distributionMode;//分销商 模式(1,电商 2，经销商 3 ,商超)
    public Integer getDistributionMode() {
		return distributionMode;
	}

	public void setDistributionMode(Integer distributionMode) {
		this.distributionMode = distributionMode;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

	public String getRegisterInviteCode() {
		return registerInviteCode;
	}

	public void setRegisterInviteCode(String registerInviteCode) {
		this.registerInviteCode = registerInviteCode;
	}

	public String getSelfInviteCode() {
		return selfInviteCode;
	}

	public void setSelfInviteCode(String selfInviteCode) {
		this.selfInviteCode = selfInviteCode;
	}

	public String getErpAccount() {
		return erpAccount;
	}

	public void setErpAccount(String erpAccount) {
		this.erpAccount = erpAccount;
	}
}
