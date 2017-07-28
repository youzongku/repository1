package entity.timer;

import java.util.Date;

public class ApkApplyQueue {
	private Integer id;

	private String account;

	private String identifier;

	private Date applyTime;

	private Boolean isSuccess;

	private Boolean hasError;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public Boolean getHasError() {
		return hasError;
	}

	public void setHasError(Boolean hasError) {
		this.hasError = hasError;
	}

	@Override
	public String toString() {
		return "ApkApplyQueue [id=" + id + ", account=" + account
				+ ", identifier=" + identifier + ", applyTime=" + applyTime
				+ ", isSuccess=" + isSuccess + ", hasError=" + hasError + "]";
	}

}