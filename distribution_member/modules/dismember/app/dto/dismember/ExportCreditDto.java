package dto.dismember;

import java.io.Serializable;
import java.math.BigDecimal;

import entity.dismember.DisCredit;
import entity.dismember.DisMember;
import services.base.utils.DateFormatUtils;

/**
 * @author hanfs
 * 描述：导出用户信用额度业务实体类
 *2016年4月21日
 */
public class ExportCreditDto implements Serializable {

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	private String comsumerType;// 分销商类型

	private String userEmail;// 用户名

	private String userName;// 姓名

	private String tel;// 手机号

	private BigDecimal creditLimit;// 信用额度

	private BigDecimal usedAmount;// 已使用额度

	private String createuser;// 额度申请人（责任人）

	private String limitState;// 额度状态

	private String isFinished;// 是否还款

	private String startTime;// 开始时间

	private String endTime;// 失效时间

	private String redit;// 额度类型

	public String getComsumerType() {
		return comsumerType;
	}

	public void setComsumerType(String comsumerType) {
		this.comsumerType = comsumerType;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public BigDecimal getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(BigDecimal creditLimit) {
		this.creditLimit = creditLimit;
	}

	public BigDecimal getUsedAmount() {
		return usedAmount;
	}

	public void setUsedAmount(BigDecimal usedAmount) {
		this.usedAmount = usedAmount;
	}

	public String getCreateuser() {
		return createuser;
	}

	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}

	public String getLimitState() {
		return limitState;
	}

	public void setLimitState(String limitState) {
		this.limitState = limitState;
	}

	public String getIsFinished() {
		return isFinished;
	}

	public void setIsFinished(String isFinished) {
		this.isFinished = isFinished;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getRedit() {
		return redit;
	}

	public void setRedit(String redit) {
		this.redit = redit;
	}
	public void copyPropertyByCredit(DisCredit credit){
		if (credit !=null) {
			this.creditLimit = credit.getCreditLimit();
			this.usedAmount = credit.getUsedAmount();
			this.createuser = credit.getCreateuser();
			String status = "";
			switch (credit.getLimitState()) {
			case 1:
				status = "待使用";
				break;
			case 2:
				status = "使用中";
				break;
			case 3:
				status = "已失效";
				break;
			default:
				break;
			}
			this.limitState = status;
			if (credit.getIsFinished()) {
				this.isFinished = "是";
			}else{
				this.isFinished = "否";
			}
			this.startTime = DateFormatUtils.getStrFromYYYYMMDDHHMMSS(credit.getStartTime());
			if (credit.getRedit()!=null && credit.getRedit()>1) {
				this.redit = "永久额度";
			}else{
				this.redit = "临时额度";
				this.endTime = DateFormatUtils.getStrFromYYYYMMDDHHMMSS(credit.getEndTime());
			}
		}
	}

	public void copyPorpertyByMember(DisMember member) {
		if (member != null) {
			String type = "";
			switch (member.getComsumerType()) {
			case 1:
				type = "普通分销商";
				break;
			case 2:
				type = "合营分销商";
				break;
			case 3:
				type = "内部分销商";
				break;
			default:
				break;
			}
			this.comsumerType = type;
			this.userEmail = member.getEmail();
			this.userName = member.getRealName();
			this.tel = member.getTelphone();
		}
	}
}
