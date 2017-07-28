package dto.dismember;

import java.io.Serializable;
import java.math.BigDecimal;

public class AccountDto implements Serializable {

	private static final long serialVersionUID = 5512210554869606383L;

	private Integer id;
	
	private String email;//邮箱

    private String nick;//昵称
    
    private String realName;//真实姓名
    
    private String telphone;//电话
    
    private Integer comsumerType;//分销商类型(1：普通分销商，2：合营分销商，3：内部分销商)
    
    private Integer distributionMode;//分销商 模式(1,电商 2，经销商 3 ,商超)
    
    private String comsumerTypeName;//分销商类型名称（由comsumerType确定取值）
    
    private String distributionModeDesc;//分销商模式，描述
    
    private BigDecimal balance;//余额

    private BigDecimal frozenAmount;//暂冻结金额

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public Integer getComsumerType() {
		return comsumerType;
	}

	public void setComsumerType(Integer comsumerType) {
		this.comsumerType = comsumerType;
	}

	public Integer getDistributionMode() {
		return distributionMode;
	}

	public void setDistributionMode(Integer distributionMode) {
		this.distributionMode = distributionMode;
	}

	public String getComsumerTypeName() {
		return comsumerTypeName;
	}

	public void setComsumerTypeName(Integer comsumerType) {
		switch (comsumerType) {
		case 1:
			this.comsumerTypeName = "普通分销商";
			break;
		case 2:
			this.comsumerTypeName = "合营分销商";
			break;
		case 3:
			this.comsumerTypeName = "内部分销商";
			break;
		default:
			this.comsumerTypeName = "普通分销商";
			break;
		}
	}

	public String getDistributionModeDesc() {
		return distributionModeDesc;
	}

	public void setDistributionModeDesc(Integer distributionMode) {
		switch (distributionMode) {
		case 1:
			this.distributionModeDesc = "电商";
			break;
		case 2:
			this.distributionModeDesc = "经销商";
			break;
		case 3:
			this.distributionModeDesc = "KA直营";
			break;
		case 4:
			this.distributionModeDesc = "进口专营";
			break;
		case 5:
			this.distributionModeDesc = "VIP";
			break;
		default:
			this.distributionModeDesc = "电商";
			break;
		}
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
    
    
}
