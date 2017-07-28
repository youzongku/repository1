package entity.contract;

import java.util.Date;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * 合同表
 * @author Administrator
 *
 */
@ApiModel
public class Contract {
	
    private Integer id;

    private String contractNo;//合同号

    private String account;//分销商账号

    private String phone;//分销商电话

    private Integer distributionMode;//分销商渠道

    private Integer distributionType;//分销商类型

    private String distributionName;//分销商名称

    private Date contractStart;//合同开始时间
    
    private Date contractEnd;//合同结束时间
    
    private String bussinessErp;//业务员erp账号

    private Date createTime;//创建时间

    private Date updateTime;//更新时间

    private String createUser;//创建人
    
    private Integer status;//合同状态

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getDistributionMode() {
        return distributionMode;
    }

    public void setDistributionMode(Integer distributionMode) {
        this.distributionMode = distributionMode;
    }

    public Integer getDistributionType() {
        return distributionType;
    }

    public void setDistributionType(Integer distributionType) {
        this.distributionType = distributionType;
    }

    public String getDistributionName() {
        return distributionName;
    }

    public void setDistributionName(String distributionName) {
        this.distributionName = distributionName;
    }

    public Date getContractStart() {
        return contractStart;
    }

    public void setContractStart(Date contractStart) {
        this.contractStart = contractStart;
    }

    public Date getContractEnd() {
        return contractEnd;
    }

    public void setContractEnd(Date contractEnd) {
        this.contractEnd = contractEnd;
    }

    public String getBussinessErp() {
        return bussinessErp;
    }

    public void setBussinessErp(String bussinessErp) {
        this.bussinessErp = bussinessErp;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}