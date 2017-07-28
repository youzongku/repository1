package dto.contract.fee;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * 合同费用项日志
 */
public class ContractFeeItemLogDto {
	private Integer feeItemId;

	private String feeTypeName;

	private Integer feeType;

	private BaseContractFeeValue feeValueOriginal;

	private BaseContractFeeValue feeValueNew;

	private Integer optType;

	private String optUser;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date optTime;

	public Integer getFeeItemId() {
		return feeItemId;
	}

	public void setFeeItemId(Integer feeItemId) {
		this.feeItemId = feeItemId;
	}

	public String getFeeTypeName() {
		return feeTypeName;
	}

	public void setFeeTypeName(String feeTypeName) {
		this.feeTypeName = feeTypeName;
	}

	public Integer getFeeType() {
		return feeType;
	}

	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}

	public BaseContractFeeValue getFeeValueOriginal() {
		return feeValueOriginal;
	}

	public void setFeeValueOriginal(BaseContractFeeValue feeValueOriginal) {
		this.feeValueOriginal = feeValueOriginal;
	}

	public BaseContractFeeValue getFeeValueNew() {
		return feeValueNew;
	}

	public void setFeeValueNew(BaseContractFeeValue feeValueNew) {
		this.feeValueNew = feeValueNew;
	}

	public Integer getOptType() {
		return optType;
	}

	public void setOptType(Integer optType) {
		this.optType = optType;
	}

	public String getOptUser() {
		return optUser;
	}

	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}

	public Date getOptTime() {
		return optTime;
	}

	public void setOptTime(Date optTime) {
		this.optTime = optTime;
	}

}
