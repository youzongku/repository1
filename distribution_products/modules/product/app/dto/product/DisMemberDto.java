package dto.product;

import java.io.Serializable;

/**
 * 用户信息实体类
 * @author zbc
 * 2017年3月25日 下午4:22:09
 */
public class DisMemberDto  implements Serializable{

	private static final long serialVersionUID = -3721842279853229855L;
	
	private String nickName;

	private String email;

	private Integer comsumerType;// 分销商类型(1：普通分销商，2：合营分销商，3：内部分销商)

	private String erpAccount;// erp账号

	private Integer distributionMode;// 分销商 模式(1、电商；2、经销商；3、商超；4、进口专营)

	private String distributionModeDesc;

	private String salesmanErp;// 业务人员erp账号（由后台人员注册选择业务人员时，此业务员的erp账号）

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getComsumerType() {
		return comsumerType;
	}

	public void setComsumerType(Integer comsumerType) {
		this.comsumerType = comsumerType;
	}

	public String getErpAccount() {
		return erpAccount;
	}

	public void setErpAccount(String erpAccount) {
		this.erpAccount = erpAccount;
	}

	public Integer getDistributionMode() {
		return distributionMode;
	}

	public void setDistributionMode(Integer distributionMode) {
		this.distributionMode = distributionMode;
	}

	public String getDistributionModeDesc() {
		return distributionModeDesc;
	}

	public void setDistributionModeDesc(String distributionModeDesc) {
		this.distributionModeDesc = distributionModeDesc;
	}

	public String getSalesmanErp() {
		return salesmanErp;
	}

	public void setSalesmanErp(String salesmanErp) {
		this.salesmanErp = salesmanErp;
	}
	
}
