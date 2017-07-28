package entity.product_inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class ProductInventoryBatchDetail {
	
	@JsonIgnore
    private Integer id;

    private String sku;

    private String productName;

    private Integer containerStockChange;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss:SSS", locale = "zh", timezone = "GMT+8")
    private Date updateDate;

    private String containerNumber;

    private String storageLocation;
    
    private String warehouseName;
    
    private Integer warehouseId;
    
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
    private Date expirationDate;

    private String identifier;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku == null ? null : sku.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public Integer getContainerStockChange() {
        return containerStockChange;
    }

    public void setContainerStockChange(Integer containerStockChange) {
        this.containerStockChange = containerStockChange;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }


    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
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

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName == null ? null : warehouseName.trim();
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public String toString() {
		return "ProductInventoryBatchDetail [id=" + id + ", sku=" + sku
				+ ", productName=" + productName + ", containerStockChange="
				+ containerStockChange + ", remark=" + remark + ", updateDate="
				+ updateDate + ", containerNumber=" + containerNumber
				+ ", storageLocation=" + storageLocation + ", warehouseName="
				+ warehouseName + ", warehouseId=" + warehouseId
				+ ", expirationDate=" + expirationDate + ", identifier="
				+ identifier + "]";
	}
	
	
    
    
    
}