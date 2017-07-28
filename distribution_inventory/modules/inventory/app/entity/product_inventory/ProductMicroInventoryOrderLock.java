package entity.product_inventory;

import java.util.Date;

public class ProductMicroInventoryOrderLock {
    private Integer id;

    private String orderNo;

    private String sku;

    private Integer stockLocked;

    private Integer warehouseId;

    private String warehouseName;

    private Short isEffective;

    private String containerNumber;

    private String storageLocation;

    private Date expirationDate;

    private Date createTime;

    private Date updateTime;
    
    private String account;
    
    private Short isGift;
    
    private Float purchasePrice;
    
    private Float capfee;

    private Integer microInRecordId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

	public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku == null ? null : sku.trim();
    }

    public Integer getStockLocked() {
        return stockLocked;
    }

    public void setStockLocked(Integer stockLocked) {
        this.stockLocked = stockLocked;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName == null ? null : warehouseName.trim();
    }

    public Short getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(Short isEffective) {
        this.isEffective = isEffective;
    }

    public String getContainerNumber() {
        return containerNumber;
    }

    public void setContainerNumber(String containerNumber) {
        this.containerNumber = containerNumber == null ? null : containerNumber.trim();
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation == null ? null : storageLocation.trim();
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Short getIsGift() {
		return isGift;
	}

	public void setIsGift(Short isGift) {
		this.isGift = isGift;
	}

	public Float getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Float purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

    public Float getCapfee() {
        return capfee;
    }

    public void setCapfee(Float capfee) {
        this.capfee = capfee;
    }

    public Integer getMicroInRecordId() {
        return microInRecordId;
    }

    public void setMicroInRecordId(Integer microInRecordId) {
        this.microInRecordId = microInRecordId;
    }

    @Override
    public String toString() {
        return "ProductMicroInventoryOrderLock{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", sku='" + sku + '\'' +
                ", stockLocked=" + stockLocked +
                ", warehouseId=" + warehouseId +
                ", warehouseName='" + warehouseName + '\'' +
                ", isEffective=" + isEffective +
                ", containerNumber='" + containerNumber + '\'' +
                ", storageLocation='" + storageLocation + '\'' +
                ", expirationDate=" + expirationDate +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", account='" + account + '\'' +
                ", isGift=" + isGift +
                ", purchasePrice=" + purchasePrice +
                ", capfee=" + capfee +
                ", microInRecordId=" + microInRecordId +
                '}';
    }
}