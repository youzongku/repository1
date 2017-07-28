package entity.purchase.returnod;

import java.util.Date;

import utils.purchase.DateUtils;

public class ReturnAmountCoefficientLog {
	private Integer id;
	private Integer coefficientId;
	private String sku;
	private Integer warehouseId;
	private String logValue;
	private Date createTime;
	private String createUser;
	
	public ReturnAmountCoefficientLog(){}
	
	public ReturnAmountCoefficientLog(Integer coefficientId, String sku,
			Integer warehouseId, String logValue, String createUser) {
		super();
		this.coefficientId = coefficientId;
		this.sku = sku;
		this.warehouseId = warehouseId;
		this.logValue = logValue;
		this.createUser = createUser;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCoefficientId() {
		return coefficientId;
	}

	public void setCoefficientId(Integer coefficientId) {
		this.coefficientId = coefficientId;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getLogValue() {
		return logValue;
	}

	public String getCreateTimeStr() {
		return DateUtils.date2FullDateTimeString(createTime);
	}

	public void setLogValue(String logValue) {
		this.logValue = logValue;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	@Override
	public String toString() {
		return "ReturnAmountCoefficientLog [id=" + id + ", coefficientId="
				+ coefficientId + ", sku=" + sku + ", warehouseId="
				+ warehouseId + ", logValue=" + logValue + ", createTime="
				+ createTime + ", createUser=" + createUser + "]";
	}

}
