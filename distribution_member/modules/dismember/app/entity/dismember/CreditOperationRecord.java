package entity.dismember;

import java.io.Serializable;
import java.util.Date;

import services.base.utils.DateFormatUtils;

public class CreditOperationRecord implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;   //id
	
	private String userEmail; //被操作者的邮箱
	
	private String operatorEmail; //操作人邮箱
	
	private String comments;//操作细节 
	
	private Date operatorTime; //操作时间
	
	private String operatorTimeStr;//操作时间字符串
	
	private Integer operatorType;//操作类型（1：审核 ）
	
	private Integer operatorResult;//操作结果（0：失败，1：成功）
	
	private Integer credit;//

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getOperatorEmail() {
		return operatorEmail;
	}

	public void setOperatorEmail(String operatorEmail) {
		this.operatorEmail = operatorEmail;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public void setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
	}

	public Integer getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(Integer operatorType) {
		this.operatorType = operatorType;
	}

	public Integer getOperatorResult() {
		return operatorResult;
	}

	public void setOperatorResult(Integer operatorResult) {
		this.operatorResult = operatorResult;
	}

	public String getOperatorTimeStr() {
		if (this.operatorTime != null) {
			return DateFormatUtils.getStrFromYYYYMMDDHHMMSS(operatorTime);
		}
		return operatorTimeStr;
	}

	public void setOperatorTimeStr(String operatorTimeStr) {
		this.operatorTimeStr = operatorTimeStr;
	}
	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}

	@Override
	public String toString() {
		return "CreditOperationRecord [id=" + id + ", userEmail=" + userEmail + ", operatorEmail=" + operatorEmail
				+ ", comments=" + comments + ", operatorTime=" + operatorTime + ", operatorType=" + operatorType
				+ ", operatorResult=" + operatorResult + ", credit=" + credit + "]";
	}
	
}