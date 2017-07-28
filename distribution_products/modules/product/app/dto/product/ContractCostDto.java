package dto.product;

import entity.contract.ContractCost;
import util.product.Constant;

/**
 * @author zbc
 * 2017年3月27日 上午11:54:48
 */
public class ContractCostDto extends  ContractCost{

	private String account;//分销商账号

    private String phone;//分销商电话
   
    private Integer disType;//分销商类型

    private String distributionMode;//分销商渠道
    
    private Integer model;//渠道
    
    private String disName;//分销商名称
  
	public String getDisName() {
		return disName;
	}

	public void setDisName(String disName) {
		this.disName = disName;
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

	public Integer getModel() {
		return model;
	}

	public void setModel(Integer model) {
		this.model = model;
	}
	
	public Integer getDisType() {
		return disType;
	}

	public void setDisType(Integer disType) {
		this.disType = disType;
	}

	public String getDistributionType() {
		return Constant.DISTRIBUTIONTYPE.get(disType);
	}

	public String getDistributionName() {
		return Constant.DISTRIBUTIONMODE.get(model);
	}
	
    public String getStatusDesc(){
    	return Constant.CONTRACT_COST_STATUS.get(getStatus());
    }
}
