package entity.contract;

import java.util.Date;

import util.product.DateUtils;

/**
 * 费用管理是实体
 * @author zbc
 * 2017年3月25日 上午11:26:11
 */
public class ContractCost {
	
	/**
	 * 1：未开始    2：已开始   3：已结束
	 */
	public static final int  HAVE_NOT_STARTED = 1;
	
	public static final int  HAS_BEGUN = 2;
	
	public static final int FINISHED = 3;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 合同号
     */
    private String contractNo;

    /**
     * 费用类型id
     */
    private Integer costTypeId;

    /**
     * 费用率
     */
    private Double scaleOfCharges;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String createUser;
    
    /**
     * 备注信息
     */
    private String remark;
    
    private String type;
    
    private String formula;
    
    /**
     * 合同费用状态(1、未开始 2、已开始 3、已结束)
     */
    private Integer status;
	
	public ContractCost(){
    	
    }
    /**
	 * @param contractNo
	 * @param costTypeId
	 * @param scaleOfCharges
	 * @param startTime
	 * @param endTime
	 * @param createUser
	 * @param remark
	 */
	public ContractCost( String contractNo, Integer costTypeId, Double scaleOfCharges, Date startTime,
			Date endTime, String createUser,String remark) {
		super();
		this.contractNo = contractNo;
		this.costTypeId = costTypeId;
		this.scaleOfCharges = scaleOfCharges;
		this.startTime = startTime;
		this.endTime = endTime;
		this.createUser = createUser;
		this.remark = remark;
	}

	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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

    public Integer getCostTypeId() {
        return costTypeId;
    }

    public void setCostTypeId(Integer costTypeId) {
        this.costTypeId = costTypeId;
    }

    public Double getScaleOfCharges() {
        return scaleOfCharges;
    }

    public void setScaleOfCharges(Double scaleOfCharges) {
        this.scaleOfCharges = scaleOfCharges;
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
    
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateTimeStr(){
    	return DateUtils.date2string(updateTime, DateUtils.FORMAT_FULL_DATETIME);
    }
    
    public String getCreateTimeStr(){
    	return DateUtils.date2string(createTime, DateUtils.FORMAT_FULL_DATETIME);
    }
    
    public String getEndTimeStr() {
    	return DateUtils.date2string(endTime, DateUtils.FORMAT_FULL_DATETIME);
    }
    
    public String getStartTimeStr(){
    	return DateUtils.date2string(startTime, DateUtils.FORMAT_FULL_DATETIME);
    }
    
}