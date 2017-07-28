package entity.product_inventory;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import play.Logger;

public class ProductMicroInventoryDetail implements Serializable  {
  
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8441242550796590575L;

	private Integer id;

    private String sku;

    private String productTitle;
    
    private Integer stock;

    private Integer lockStock;

    private String account;
    
    private String accountName;

    private Integer warehouseId;

    private String warehouseName;

    private String containerNumber;

    private String storageLocation;

    private Date updateTime;

    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
    private Date expirationDate;

    private Float purchasePrice;
    
    private Float capfee;

    private Short isGift;

    public ProductMicroInventoryDetail() {
		super();
	}
    
    public ProductMicroInventoryDetail(ProductMicroInventoryInRecord microInRecord) {
		super();
		this.sku = microInRecord.getSku();
		this.productTitle = microInRecord.getProductTitle();
		this.account = microInRecord.getAccount();
		this.warehouseId = microInRecord.getWarehouseId();
		this.expirationDate = microInRecord.getExpirationDate();
		this.purchasePrice = microInRecord.getPurchasePrice();
		this.capfee = microInRecord.getCapfee();
		this.isGift = microInRecord.getIsGift();
	}
    
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

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getLockStock() {
		return lockStock;
	}

	public void setLockStock(Integer lockStock) {
		this.lockStock = lockStock;
	}

	public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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

	public Short getIsGift() {
        return isGift;
    }

    public void setIsGift(Short isGift) {
        this.isGift = isGift;
    }

	@Override
	public String toString() {
		return "ProductMicroInventoryDetail [id=" + id + ", sku=" + sku + ", productTitle=" + productTitle + ", stock="
				+ stock + ", lockStock=" + lockStock + ", account=" + account + ", accountName=" + accountName
				+ ", warehouseId=" + warehouseId + ", warehouseName=" + warehouseName + ", containerNumber="
				+ containerNumber + ", storageLocation=" + storageLocation + ", updateTime=" + updateTime
				+ ", createTime=" + createTime + ", expirationDate=" + expirationDate + ", purchasePrice="
				+ purchasePrice + ", capfee=" + capfee + ", isGift=" + isGift + "]";
	}

    public ProductMicroInventoryDetail sum(ProductMicroInventoryDetail t) {
        Logger.error("---------->t:{}", t);
        this.stock = (this.getStock() - this.getLockStock()) + t.getStock() - t.getLockStock();
        return this;
    }
}