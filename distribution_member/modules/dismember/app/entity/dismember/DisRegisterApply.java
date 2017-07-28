package entity.dismember;

import java.util.Date;
import java.util.List;

public class DisRegisterApply {
    private Integer id;

    private String account;//申请人账号

    private String registerMan;//注册人(分为前台注册和后台注册)

    private Integer status;//申请状态

    private Date registerDate;//注册日期

    private Date createDate;//申请日期

    private Date updateDate;//更新日期

    private String auditRemark;//审核备注

    private String auditReason;//审核理由

    private Boolean isBackRegister;//是否为后台注册
    
    private String passWord;//注册申请时填写的密码
    
	private Integer salesmanId;//业务员id
    
    private String applyRemark;//申请备注
    
    private String auditMan;//审核人
    
    private String registerInviteCode;//注册邀请码
    
    private List<DisApplyFile> files;//申请时提交的文件
    
    private String statusDesc;//审核状态字符串
    
    private String createDateDesc;
    
    private String registerDateDesc;
    
    private String updateDateDesc;

	private Integer provinceCode;//省编码

	private Integer cityCode;///市编码

	private Integer areaCode;//区编码

	private Integer distributionMode;//分销渠道

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

	public String getRegisterMan() {
		return registerMan;
	}

	public void setRegisterMan(String registerMan) {
		this.registerMan = registerMan;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getAuditRemark() {
		return auditRemark;
	}

	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark;
	}

	public String getAuditReason() {
		return auditReason;
	}

	public void setAuditReason(String auditReason) {
		this.auditReason = auditReason;
	}

	public Boolean getIsBackRegister() {
		return isBackRegister;
	}

	public void setIsBackRegister(Boolean isBackRegister) {
		this.isBackRegister = isBackRegister;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public Integer getSalesmanId() {
		return salesmanId;
	}

	public void setSalesmanId(Integer salesmanId) {
		this.salesmanId = salesmanId;
	}

	public String getApplyRemark() {
		return applyRemark;
	}

	public void setApplyRemark(String applyRemark) {
		this.applyRemark = applyRemark;
	}

	public String getAuditMan() {
		return auditMan;
	}

	public void setAuditMan(String auditMan) {
		this.auditMan = auditMan;
	}

	public List<DisApplyFile> getFiles() {
		return files;
	}

	public void setFiles(List<DisApplyFile> files) {
		this.files = files;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(Integer status) {
		if (status == 0) {
			this.statusDesc = "待审核";
		} else if (status == 1) {
			this.statusDesc = "审核不通过";
		} else if (status == 2){
			this.statusDesc = "审核通过";
		} else {
			this.statusDesc = "已取消";
		}
	}

	public String getCreateDateDesc() {
		return createDateDesc;
	}

	public void setCreateDateDesc(String createDateDesc) {
		this.createDateDesc = createDateDesc;
	}

	public String getRegisterDateDesc() {
		return registerDateDesc;
	}

	public void setRegisterDateDesc(String registerDateDesc) {
		this.registerDateDesc = registerDateDesc;
	}

	public String getRegisterInviteCode() {
		return registerInviteCode;
	}

	public void setRegisterInviteCode(String registerInviteCode) {
		this.registerInviteCode = registerInviteCode;
	}

	public String getUpdateDateDesc() {
		return updateDateDesc;
	}

	public void setUpdateDateDesc(String updateDateDesc) {
		this.updateDateDesc = updateDateDesc;
	}

	public Integer getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(Integer provinceCode) {
		this.provinceCode = provinceCode;
	}

	public Integer getCityCode() {
		return cityCode;
	}

	public void setCityCode(Integer cityCode) {
		this.cityCode = cityCode;
	}

	public Integer getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(Integer areaCode) {
		this.areaCode = areaCode;
	}

	public Integer getDistributionMode() {
		return distributionMode;
	}

	public void setDistributionMode(Integer distributionMode) {
		this.distributionMode = distributionMode;
	}
}