package dto.product;

public class ContractDto {

	private Integer id;

    private String contractNo;//合同号

    private String account;//分销商账号

    private String phone;//分销商电话

    private String distributionMode;//分销商渠道
    
    private Integer model;

    private String distributionType;//分销商类型

    private String distributionName;//分销商名称

    private String contractStart;//合同开始时间
    
    private String contractEnd;//合同结束时间
    
    private String bussinessErp;//业务员erp账号

//    private String createTime;//创建时间
//
//    private String updateTime;//更新时间

    private String createUser;//创建人
    
	public Integer getModel() {
		return model;
	}

	public void setModel(Integer model) {
		this.model = model;
	}

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

	public String getDistributionMode() {
		return distributionMode;
	}

	public void setDistributionMode(String distributionMode) {
		this.distributionMode = distributionMode;
	}

	public String getDistributionType() {
		return distributionType;
	}

	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
	}

	public String getDistributionName() {
		return distributionName;
	}

	public void setDistributionName(String distributionName) {
		this.distributionName = distributionName;
	}

	public String getContractStart() {
		return contractStart;
	}

	public void setContractStart(String contractStart) {
		this.contractStart = contractStart;
	}

	public String getContractEnd() {
		return contractEnd;
	}

	public void setContractEnd(String contractEnd) {
		this.contractEnd = contractEnd;
	}

	public String getBussinessErp() {
		return bussinessErp;
	}

	public void setBussinessErp(String bussinessErp) {
		this.bussinessErp = bussinessErp;
	}

//	public String getCreateTime() {
//		return createTime;
//	}
//
//	public void setCreateTime(String createTime) {
//		this.createTime = createTime;
//	}
//
//	public String getUpdateTime() {
//		return updateTime;
//	}
//
//	public void setUpdateTime(String updateTime) {
//		this.updateTime = updateTime;
//	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
    
}
