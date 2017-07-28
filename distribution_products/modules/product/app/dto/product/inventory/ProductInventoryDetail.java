package dto.product.inventory;

import java.text.ParseException;
import java.util.Date;

import dto.product.CloudExpirationFormatResult;
import util.product.DateUtils;

/**
 * 库存模块实体
 * @author zbc
 * 2017年4月21日 下午3:41:33
 */
public class ProductInventoryDetail {
	
    private Integer id;

    private String sku;

    private Integer stock;

    private Integer warehouseId;

    private String warehouseName;

    private Date updateTime;

    private Date createTime;

    private Date expirationDate;
    
    private String productName;

    
    /**
	 * @param id
	 * @param sku
	 * @param stock
	 * @param warehouseId
	 * @param warehouseName
	 * @param updateTime
	 * @param createTime
	 * @param expirationDate
	 */
	public ProductInventoryDetail(CloudExpirationFormatResult cloud,Integer stock) {
		this.sku = cloud.getSku();
		this.stock = stock;
		this.warehouseId = cloud.getWarehouseId();
		this.warehouseName = cloud.getWarehouseName();
		try {
			this.expirationDate = DateUtils.string2date(cloud.getExpirationDate(), DateUtils.FORMAT_DATE_PAGE);
		} catch (ParseException e) {
			
		}
	}
	
	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}


	public ProductInventoryDetail(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public ProductInventoryDetail() {
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
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
}