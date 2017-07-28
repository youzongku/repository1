package dto.dismember;

public class ApReminderSettingParam {
	private String account;
	
	private Boolean enable;

	private Integer daysAgo;

	private String optUser;// 操作人

	public ApReminderSettingParam(String account, Boolean enable, Integer daysAgo, String optUser) {
		super();
		this.account = account;
		this.enable = enable;
		this.daysAgo = daysAgo;
		this.optUser = optUser;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
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

	public String getOptUser() {
		return optUser;
	}

	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}

	@Override
	public String toString() {
		return "ApReminderSettingParam [enable=" + enable + ", daysAgo=" + daysAgo + ", optUser=" + optUser + "]";
	}

}
