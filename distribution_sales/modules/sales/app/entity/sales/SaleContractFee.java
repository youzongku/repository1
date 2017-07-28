package entity.sales;

import java.util.Date;

import util.sales.Constant;
import util.sales.IDUtils;

/**
 * 发货单(合同)费用实体
 * @author zbc
 * 2017年5月12日 下午3:52:02
 */
public class SaleContractFee {
	
	/**
	 * uid
	 */
	private String uid; 

    /**
     * 发货单号
     */
    private String salesOrderNo;

    /**
     * 合同号
     */
    private String contractNo;

    /**
     * 属性key值
     */
    private String attrKey;

    /**
     * 属性名称
     */
    private String attrName;

    /**
     * 属性值
     */
    private String value;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 是否删除标识 
     */
    private Boolean isDelete;
    
    /**
     * 费用项id
     */
    private Integer feeId;
    
	public SaleContractFee(){
    	
    }
    
    public SaleContractFee(String salesOrderNo, String contractNo) {
		super();
		this.uid = IDUtils.getUid();
		this.salesOrderNo = salesOrderNo;
		this.contractNo = contractNo;
	}
    
    public SaleContractFee(String salesOrderNo, String contractNo,String attrName,String attrKey) {
		super();
		this.uid = IDUtils.getUid();
		this.salesOrderNo = salesOrderNo;
		this.contractNo = contractNo;
		this.attrName = getName(attrName,attrKey);
		this.attrKey = attrKey;
	}

    public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getFeeId() {
		return feeId;
	}

	public void setFeeId(Integer feeId) {
		this.feeId = feeId;
	}

	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

    public String getSalesOrderNo() {
        return salesOrderNo;
    }

    public void setSalesOrderNo(String salesOrderNo) {
        this.salesOrderNo = salesOrderNo;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getAttrKey() {
        return attrKey;
    }

    public void setAttrKey(String attrKey) {
        this.attrKey = attrKey;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
    
    public String getName(String typeName,String key){
		StringBuilder build = new StringBuilder();
		if(typeName != null){
			build.append(typeName);
		}
		String keyName = Constant.CONTRACT_FEE_MAP.get(key);
		if(keyName != null){
			build.append(keyName);
		}
		return build.toString();
	}
}