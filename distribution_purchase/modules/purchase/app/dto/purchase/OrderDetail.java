package dto.purchase;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class OrderDetail {
    private Integer id;

    private String orderNo;

    private String sku;
    
    private String productTitle;
    
    private String imgUrl;

    private Integer qty;

    private Double purchasePrice;

    private Integer warehouseId;

    private String warehouseName;
    
    private Integer categoryId;
    
    private String categoryName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Date updateTime;

    private Integer isGift;

    private String expirationDate;

    private Double capfee;
    
	private Double arriveWarePrice;
    
    /**
     * 合同号
     */
    private String contractNo;
    
    /**
     * 清货价
     */
    private Double clearancePrice;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column public.t_order_detail.id
     *
     * @return the value of public.t_order_detail.id
     *
     * @mbggenerated Wed Dec 07 11:01:57 CST 2016
     */
    public Integer getId() {
        return id;
    }

    public Double getClearancePrice() {
		return clearancePrice;
	}

	public void setClearancePrice(Double clearancePrice) {
		this.clearancePrice = clearancePrice;
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

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
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

    public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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

    public Integer getIsGift() {
        return isGift;
    }

    public void setIsGift(Integer isGift) {
        this.isGift = isGift;
    }

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Double getCapfee() {
		return capfee;
	}

	public void setCapfee(Double capfee) {
		this.capfee = capfee;
	}

	public Double getArriveWarePrice() {
		return arriveWarePrice;
	}

	public void setArriveWarePrice(Double arriveWarePrice) {
		this.arriveWarePrice = arriveWarePrice;
	}
	
	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	@Override
	public String toString() {
		return "OrderDetail [id=" + id + ", orderNo=" + orderNo + ", sku=" + sku + ", productTitle=" + productTitle
				+ ", imgUrl=" + imgUrl + ", qty=" + qty + ", purchasePrice=" + purchasePrice + ", warehouseId="
				+ warehouseId + ", warehouseName=" + warehouseName + ", categoryId=" + categoryId + ", categoryName="
				+ categoryName + ", createTime=" + createTime + ", updateTime=" + updateTime + ", isGift=" + isGift
				+ ", expirationDate=" + expirationDate + ", capfee=" + capfee + ", arriveWarePrice=" + arriveWarePrice
				+ ", contractNo=" + contractNo+", clearancePrice=" + clearancePrice+  "]";
	}

}