package entity.product_inventory;

import java.util.Date;

public class InventorySyncRecord {
	private Integer id;

	private Integer isActive;

	private Date lastSyncingTime;

	private String sku;

	private Integer warehouseId;
	
	private String operator;
	
	private Integer syncingNum;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Date getLastSyncingTime() {
		return lastSyncingTime;
	}

	public void setLastSyncingTime(Date lastSyncingTime) {
		this.lastSyncingTime = lastSyncingTime;
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

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Integer getSyncingNum() {
		return syncingNum;
	}

	public void setSyncingNum(Integer syncingNum) {
		this.syncingNum = syncingNum;
	}

}