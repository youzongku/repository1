package entity.contract;

import java.time.LocalDateTime;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import dto.contract.fee.BaseContractFeeValue;
import dto.contract.fee.FeeRate;
import dto.contract.fee.FeeValue;
import play.libs.Json;
import util.product.DateUtils;


public class ContractFeeItem {
	
	private Integer id;
	private Integer feeTypeId;
	private String feeTypeName;
	private Integer feeType;
	private String contractNo;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date startTime;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date endTime;
	private String content;
	private Boolean deleted;

	private String createUser;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;

	private String lastUpdateUser;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date updateTime;

	private String remarks;
	
	public BaseContractFeeValue getContractFeeValue() {
		if (StringUtils.isBlank(content)) {
			return null;
		}
		
		if (feeType==1) {// 固定费用值
			return Json.fromJson(Json.parse(content), FeeValue.class);
		} else if (feeType==2) {// 固定费用率
			return Json.fromJson(Json.parse(content), FeeRate.class);
		} else {
			return null;
		}
	}

	public String getStatusMsg() {
		String msg = "";
		if (notStart()) {
			msg = "未开始";
		} else if (working()) {
			msg = "已开始";
		} else if (finied()) {
			msg = "已结束";
		}
		return msg;
	}
	
	public int getStatus() {
		if (notStart()) {
			return 1;// 未开始
		}
		if (working()) {
			return 2;// 已开始
		} 
		if (finied()) {
			return 3;// 已结束
		}
		
		return -1;// 未知状态
	}

	/**
	 * 提前结束了？
	 * 
	 * @return
	 */
	public boolean finishedAheadOfTime() {
		return LocalDateTime.now().isAfter(DateUtils.toLocalDateTime(endTime));
	}

	/**
	 * 结束了？
	 * 
	 * @return
	 */
	public boolean finied() {
		return finishedAheadOfTime();
	}

	/**
	 * 未开始
	 * 
	 * @return
	 */
	public boolean notStart() {
		if (!finied()) {
			LocalDateTime today = LocalDateTime.now();
			LocalDateTime startDate = DateUtils.toLocalDateTime(startTime);
			return today.isBefore(startDate);
		}
		return false;
	}

	/**
	 * 使用中
	 * 
	 * @return
	 */
	public boolean working() {
		// 不处于未开始状态，也不处于结束状态，那就是处于使用中状态
		return !notStart() && !finied();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFeeTypeId() {
		return feeTypeId;
	}

	public void setFeeTypeId(Integer feeTypeId) {
		this.feeTypeId = feeTypeId;
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

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}