package entity.product_inventory;

import java.util.Date;

public class ProductMicroInventoryTotal {
    private Integer id;

    private String sku;

    private Integer warehouseId;

    private String warehouseName;

    private Integer stock;

    private String productTitle;

    private Date updateTime;

    private Date createTime;
    
    private String account;
    
    private Integer categoryId;
    
    private String categoryName;

//    private Integer residueNum;
    
    public ProductMicroInventoryTotal() {
		super();
	}
    
    public ProductMicroInventoryTotal(ProductMicroInventoryInRecord microInRecord) {
		super();
		this.sku = microInRecord.getSku();
		this.warehouseId = microInRecord.getWarehouseId();
		this.account = microInRecord.getAccount();
    }

    @Override
    public String toString() {
        return "ProductMicroInventoryTotal [id=" + id + ", sku=" + sku + ", warehouseId=" + warehouseId
                + ", warehouseName=" + warehouseName + ", stock=" + stock + ", productTitle=" + productTitle
                + ", updateTime=" + updateTime + ", createTime=" + createTime + ", account=" + account + ", categoryId="
                + categoryId + ", categoryName=" + categoryName + "]";
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
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


//    public Integer getResidueNum() {
//        return residueNum;
//    }
//
//    public void setResidueNum(Integer residueNum) {
//        this.residueNum = residueNum;
//    }
}