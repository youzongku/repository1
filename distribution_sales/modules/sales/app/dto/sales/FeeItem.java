package dto.sales;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class FeeItem implements Serializable{

	private static final long serialVersionUID = -3806843197540220786L;
	
	private Integer id;//主键id 
	private Integer feeTypeId;//费用项id
	private String feeTypeName;//如返点
	private Integer feeType;//1：固定值2：费用率 
	private String contractNo;//合同号
	
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date startTime;
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date endTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
	private String createUser;//创建人
	private String lastUpdateUser;//创建人
	private FeeValue contractFeeValue;//费用参数
	private List<RelatedSku> relatedSkus;//关联商品
	private String remarks;
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getFeeTypeId() {
		return feeTypeId;
	}

	public void setFeeTypeId(Integer feeTypeId) {
		this.feeTypeId = feeTypeId;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public FeeItem() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public FeeValue getContractFeeValue() {
		return contractFeeValue;
	}

	public void setContractFeeValue(FeeValue contractFeeValue) {
		this.contractFeeValue = contractFeeValue;
	}

	public List<RelatedSku> getRelatedSkus() {
		return relatedSkus;
	}

	public void setRelatedSkus(List<RelatedSku> relatedSkus) {
		this.relatedSkus = relatedSkus;
	}
}
