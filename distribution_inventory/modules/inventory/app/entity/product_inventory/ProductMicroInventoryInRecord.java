package entity.product_inventory;

import java.util.Date;

public class ProductMicroInventoryInRecord {
    private Integer id;
    
    private String account;

    private String sku;
    
    private String imgUrl;
    
    private String productTitle;

    private Integer qty;

    private Float purchasePrice;
    
    private Float capfee;
    
    private Float arriveWarePrice;

    private Integer warehouseId;

    private String warehouseName;

    private Short isGift;

    private Integer orderDetailId;
    
    private String orderNo;

    private String containerNumber;

    private String storageLocation;

    private Date expirationDate;
    
    private Date purchaseTime;
    
    private Integer residueNum;

    private Date createTime;

    private Date updateTime;
    
    /**
     * 合同号
     */
    private String contractNo;
    
    /**
     * 清货价
     */
    private Double clearancePrice;
    
    public Double getClearancePrice() {
		return clearancePrice;
	}

	public void setClearancePrice(Double clearancePrice) {
		this.clearancePrice = clearancePrice;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku == null ? null : sku.trim();
    }

    public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Float getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Float purchasePrice) {
        this.purchasePrice = purchasePrice;
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

    public Short getIsGift() {
        return isGift;
    }

    public void setIsGift(Short isGift) {
        this.isGift = isGift;
    }

    public Integer getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

    public Date getPurchaseTime() {
		return purchaseTime;
	}

	public void setPurchaseTime(Date purchaseTime) {
		this.purchaseTime = purchaseTime;
	}

	public Float getCapfee() {
		return capfee;
	}

	public void setCapfee(Float capfee) {
		this.capfee = capfee;
	}

    public Float getArriveWarePrice() {
		return arriveWarePrice;
	}

	public void setArriveWarePrice(Float arriveWarePrice) {
		this.arriveWarePrice = arriveWarePrice;
	}

	public Integer getResidueNum() {
		return residueNum;
	}

	public void setResidueNum(Integer residueNum) {
		this.residueNum = residueNum;
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

	@Override
	public String toString() {
		return "ProductMicroInventoryInRecord [id=" + id + ", account=" + account + ", sku=" + sku + ", imgUrl="
				+ imgUrl + ", productTitle=" + productTitle + ", qty=" + qty + ", purchasePrice=" + purchasePrice
				+ ", capfee=" + capfee + ", arriveWarePrice=" + arriveWarePrice + ", warehouseId=" + warehouseId
				+ ", warehouseName=" + warehouseName + ", isGift=" + isGift + ", orderDetailId=" + orderDetailId
				+ ", orderNo=" + orderNo + ", containerNumber=" + containerNumber + ", storageLocation="
				+ storageLocation + ", expirationDate=" + expirationDate + ", purchaseTime=" + purchaseTime
				+ ", residueNum=" + residueNum + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
	}
    
}