package dto.dismember;

import java.io.Serializable;

import constant.dismember.Constant;

/**
 * Created by LSL on 2015/12/22.
 */
public class AdminDto implements Serializable {   

    private static final long serialVersionUID = -1617198436004044807L;

    private Integer id;

    private String nick;

    private String loginName;

    private String realName;

    private Boolean bactived;

    private String role;

    private String createUser;

    private String createTime;

    private Integer rankId;//等级ID

    private String rankName;//等级名称

    private String discount;//折扣，单位为%
    
    private String customizeDiscount;//定制折扣，单位为%
    
    private Integer comsumerType;//分销商类型(1：普通分销商，2：合营分销商，3：内部分销商)
    
    private String workNo;//工号
    
    private Boolean isCustomized;//是否定制折扣
    
    private String telphone;//电话
    
    private String email;//邮箱
    
    private String login;//最后登录时间
    
    private Integer roleId;//角色ID
    
    private String comsumerTypeName;//分销商类型名称（由comsumerType确定取值）
    
    private String registerInviteCode;//注册邀请码
    
    private String selfInviteCode;//用户自身邀请码
    
    private String erpAccount;//erp账号
    
    private String distributionModeDesc;//分销商模式，描述

    private Integer distributionMode;//分销商 模式(1,电商 2，经销商 3 ,商超)
    
    /**是否有附加权限*/
    private Boolean ifAddPermision;
    /**栏目ID*/
    private String menuIds;
    
    //add By liaozl
    private Boolean isBackRegister;//是否为后台注册
    
    private String registerMan;//注册人
    
    private String salesmanErp;//业务人员erp账号（由后台人员注册选择业务人员时，此业务员的erp账号）
    
    /**
     * 是否冻结
     */
    private Boolean isFrozen;
    
    /**
     * 是否被禁用(后台账号专用)
     */
    private Boolean isDisabled;

	private String userCode;//客户编码

	private Short isPackageMail;//客户编码
	
	private Integer attributionType;//用户归属(1:线上,2:线下)

	public Integer getAttributionType() {
		return attributionType;
	}

	public void setAttributionType(Integer attributionType) {
		this.attributionType = attributionType;
	}

	public String getAttributionTypeDesc(){
		return Constant.USER_ATTR_TYPE_MAP.get(this.attributionType);
	}
	
	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public Boolean getIsFrozen() {
		return isFrozen;
	}

	public void setIsFrozen(Boolean isFrozen) {
		this.isFrozen = isFrozen;
	}

	public Integer getDistributionMode() {
		return distributionMode;
	}

	public void setDistributionMode(Integer distributionMode) {
		this.distributionMode = distributionMode;
	}
    
    public void setDistributionModeDesc(String distributionModeDesc) {
		this.distributionModeDesc = distributionModeDesc;
	}

	public String getDistributionModeDesc() {
    	
		return distributionModeDesc;
	}
    
    public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getCustomizeDiscount() {
		return customizeDiscount;
	}

	public void setCustomizeDiscount(String customizeDiscount) {
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

	public String getComsumerTypeName() {
		this.setComsumerTypeName(null);
		return this.comsumerTypeName;
	}

	public void setComsumerTypeName(String comsumerTypeName) {
		if (comsumerTypeName != null && !"".equals(comsumerTypeName)) {
			this.comsumerTypeName = comsumerTypeName;
		}
		else{
			switch (this.comsumerType) {
			case 1:
				this.comsumerTypeName = "普通分销商";
				break;
			case 2:
				this.comsumerTypeName = "合营分销商";
				break;
			case 3:
				this.comsumerTypeName = "内部分销商";
				break;
			default:
				this.comsumerTypeName = "普通分销商";
				break;
			}
		}
	}

	public String getWorkNo() {
		return workNo;
	}

	public void setWorkNo(String workNo) {
		this.workNo = workNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Boolean getBactived() {
        return bactived;
    }

    public void setBactived(Boolean bactived) {
        this.bactived = bactived;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getRankId() {
        return rankId;
    }

    public void setRankId(Integer rankId) {
        this.rankId = rankId;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
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
