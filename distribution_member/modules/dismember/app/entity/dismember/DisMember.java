package entity.dismember;

import java.io.Serializable;
import java.util.Date;

import utils.dismember.DateUtils;

public class DisMember implements Serializable {

	private static final long serialVersionUID = -587700855575849757L;

	private Integer id;

	private String userName;

	private String passWord;

	private String nickName;

	private String realName;

	private Integer gender;

	private String birthday;

	private String email;

	private String telphone;

	private String profile;

	private String headImg;

	private Boolean isActived;

	private Date createDate;

	private Date lastUpdateDate;

	private Integer roleId;//角色Id
	
	private String role;//角色名称

	private Integer rankId;

	private Date lastLoginDate;

	private String workNo;// 工号

	private Integer customizeDiscount;// 定制折扣

	private Boolean isCustomized;// 是否定制

	private Integer comsumerType;// 分销商类型(1：普通分销商，2：合营分销商，3：内部分销商)

	// 用户折扣 该字段只用于获得用户的折扣值（由定制折扣和等级折扣综合获得）
	private Double discount;

	// SimpleDateFormat转换成时间格式不对，下面2个字段只是用于前台显示

	private String create;

	private String login;

	private String registerInviteCode;// 注册邀请码

	private String selfInviteCode;// 用户自己的 邀请码（注册时系统自动生成）

	private String erpAccount;// erp账号

	private Integer distributionMode;// 分销商 模式(1、电商；2、经销商；3、商超；4、进口专营)

	private String distributionModeDesc;

	/** 创建人 */
	private String createUser;
	/** 是否有附加权限 */
	private Boolean ifAddPermision;
	/** 栏目ID */
	private String menuIds;
	/** 前台是否有附加权限按钮值 */
	private String addUserGetMenu;
	/** 是否删除 */
	private Boolean isDelete;

	// add By liaozl
	private Boolean isBackRegister;// 是否为后台注册

	private String registerMan;// 注册人

	private String salesmanErp;// 业务人员erp账号（由后台人员注册选择业务人员时，此业务员的erp账号）
	
	// add by zbc 
	private String branchName;//组织架构分部名称(即节点)
	
	/**
	 * add by zbc 是否禁用(是:true，否：false)
	 */
	private Boolean isDisabled;//

	private String userCode;//客户编码

	private Short isPackageMail;//是否包邮
	
	private Integer attributionType;//用户归属类型(1:线上，2`:为线下)
	
	public Integer getAttributionType() {
		return attributionType;
	}

	public void setAttributionType(Integer attributionType) {
		this.attributionType = attributionType;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getDistributionModeDesc() {
		return distributionModeDesc;
	}

	public void setDistributionModeDesc(String distributionModeDesc) {
		this.distributionModeDesc = distributionModeDesc;
	}

	public Integer getDistributionMode() {
		return distributionMode;
	}

	public void setDistributionMode(Integer distributionMode) {
		this.distributionMode = distributionMode;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public void setCreate(String create) {
		if (null == createDate) {
			this.create = "";
		} else {
			this.create = create;
		}
	}

	public void setLogin(String login) {
		if (null == lastLoginDate) {
			this.login = "";
		} else {
			this.login = login;
		}
	}

	public String getCreate() {
		return create;
	}

	public String getLogin() {
		return login;
	}

	public String getWorkNo() {
		return workNo;
	}

	public void setWorkNo(String workNo) {
		this.workNo = workNo;
	}

	public Integer getCustomizeDiscount() {
		return customizeDiscount;
	}

	public void setCustomizeDiscount(Integer customizeDiscount) {
		this.customizeDiscount = customizeDiscount;
	}

	public Boolean getIsCustomized() {
		return isCustomized;
	}

	public void setIsCustomized(Boolean isCustomized) {
		this.isCustomized = isCustomized;
	}

	public Integer getComsumerType() {
		return comsumerType;
	}

	public void setComsumerType(Integer comsumerType) {
		this.comsumerType = comsumerType;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
		this.setLogin(DateUtils.date2string(lastLoginDate, DateUtils.FORMAT_DATETIME));
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getRankId() {
		return rankId;
	}

	public void setRankId(Integer rankId) {
		this.rankId = rankId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public Boolean getIsActived() {
		return isActived;
	}

	public void setIsActived(Boolean isActived) {
		this.isActived = isActived;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
		this.setCreate(DateUtils.date2string(createDate, DateUtils.FORMAT_DATETIME));
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getRegisterInviteCode() {
		return registerInviteCode;
	}

	public void setRegisterInviteCode(String registerInviteCode) {
		this.registerInviteCode = registerInviteCode;
	}

	public String getSelfInviteCode() {
		return selfInviteCode;
	}

	public void setSelfInviteCode(String selfInviteCode) {
		this.selfInviteCode = selfInviteCode;
	}

	public String getErpAccount() {
		return erpAccount;
	}

	public void setErpAccount(String erpAccount) {
		this.erpAccount = erpAccount;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Boolean getIfAddPermision() {
		return ifAddPermision;
	}

	public void setIfAddPermision(Boolean ifAddPermision) {
		this.ifAddPermision = ifAddPermision;
	}

	public String getMenuIds() {
		return menuIds;
	}

	public void setMenuIds(String menuIds) {
		this.menuIds = menuIds;
	}

	public String getAddUserGetMenu() {
		return addUserGetMenu;
	}

	public void setAddUserGetMenu(String addUserGetMenu) {
		this.addUserGetMenu = addUserGetMenu;
	}

	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	public Boolean getIsBackRegister() {
		return isBackRegister;
	}

	public void setIsBackRegister(Boolean isBackRegister) {
		this.isBackRegister = isBackRegister;
	}

	public String getRegisterMan() {
		return registerMan;
	}

	public void setRegisterMan(String registerMan) {
		this.registerMan = registerMan;
	}

	public String getSalesmanErp() {
		return salesmanErp;
	}

	public void setSalesmanErp(String salesmanErp) {
		this.salesmanErp = salesmanErp;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public Short getIsPackageMail() {
		return isPackageMail;
	}

	public void setIsPackageMail(Short isPackageMail) {
		this.isPackageMail = isPackageMail;
	}
}