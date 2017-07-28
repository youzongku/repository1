package entity.warehousing;

import java.util.Date;

/**
 * 微仓信息表实体
 * <p>
 * 对应表t_micro_warehouse
 * 
 * @author ye_ziran
 * @since 2016年3月2日 下午4:52:34
 */
public class MicroWarehouse {
	
	private Integer id;
	private String warehouseName;//微仓名称
	private Integer distributorId;//分销商ID
	private String distributorEmail;//分销商email
	private String distributorName;//分销商名称
	private Date createTime;//创建时间
	private String createTimeBegin;//创建时间，yyyy-MM-dd
	private String createTimeEnd;//创建时间，yyyy-MM-dd
	private String operator;//操作人
	private String createBy;//创建人邮箱
	private Date lastUpdate;
	private String lastUpdateBegin;//最后修改时间,yyyy-MM-dd
	private String lastUpdateEnd;//最后修改时间,yyyy-MM-dd
	private String updateBy;//修改人邮箱
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public Integer getDistributorId() {
		return distributorId;
	}
	public void setDistributorId(Integer distributorId) {
		this.distributorId = distributorId;
	}
	public String getDistributorEmail() {
		return distributorEmail;
	}
	public void setDistributorEmail(String distributorEmail) {
		this.distributorEmail = distributorEmail;
	}
	public String getDistributorName() {
		return distributorName;
	}
	public void setDistributorName(String distributorName) {
		this.distributorName = distributorName;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public String getCreateTimeBegin() {
		return createTimeBegin;
	}
	public void setCreateTimeBegin(String createTimeBegin) {
		this.createTimeBegin = createTimeBegin;
	}
	public String getCreateTimeEnd() {
		return createTimeEnd;
	}
	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}
	public String getLastUpdateBegin() {
		return lastUpdateBegin;
	}
	public void setLastUpdateBegin(String lastUpdateBegin) {
		this.lastUpdateBegin = lastUpdateBegin;
	}
	public String getLastUpdateEnd() {
		return lastUpdateEnd;
	}
	public void setLastUpdateEnd(String lastUpdateEnd) {
		this.lastUpdateEnd = lastUpdateEnd;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	@Override
	public String toString() {
		return "MicroWarehouse [id=" + id + ", warehouseName=" + warehouseName + ", distributorId=" + distributorId
				+ ", distributorEmail=" + distributorEmail + ", distributorName=" + distributorName + ", createTimeBegin="
				+ createTimeBegin + ", createBy=" + createBy + ", lastUpdateBegin=" + lastUpdateBegin + ", updateBy=" + updateBy + "]";
	}
	
}
