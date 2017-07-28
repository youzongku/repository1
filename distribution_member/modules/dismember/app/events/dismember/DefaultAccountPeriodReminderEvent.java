package events.dismember;

/**
 * 默认账期提醒事件
 * 
 * @author huangjc
 */
public class DefaultAccountPeriodReminderEvent {

	// 分销商账户
	private String account;
	// 创建人
	private String createUser;

	public DefaultAccountPeriodReminderEvent() {
	}

	public DefaultAccountPeriodReminderEvent(String account, String createUser) {
		super();
		this.account = account;
		this.createUser = createUser;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

}
