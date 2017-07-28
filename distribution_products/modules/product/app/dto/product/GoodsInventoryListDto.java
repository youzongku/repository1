package dto.product;

import java.util.Date;

public class GoodsInventoryListDto implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
    private String sku;
    private Integer warehouseId;
    private String warehouseName;
    private Integer totalStock;
    private Integer frozenStock;
    private Integer availableStock;
    private Double costprice;
    private Date lastUpdated;
    private String productTitle;
    private String categoryI;
    private String categoryII;
    private String categoryIII;
    private String categoryIName;
    private String categoryIIName;
    private String categoryIIIName;
    private Integer mTotalStock;
    private Integer cTotalStock;
    private String cProductTitle;
    private String cCategoryName;
    private String iCategoryId;
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
		this.sku = sku;
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
		this.warehouseName = warehouseName;
	}
	public Integer getTotalStock() {
		return totalStock;
	}
	public void setTotalStock(Integer totalStock) {
		this.totalStock = totalStock;
	}
	public Integer getFrozenStock() {
		return frozenStock;
	}
	public void setFrozenStock(Integer frozenStock) {
		this.frozenStock = frozenStock;
	}
	public Integer getAvailableStock() {
		return availableStock;
	}
	public void setAvailableStock(Integer availableStock) {
		this.availableStock = availableStock;
	}
	public Double getCostprice() {
		return costprice;
	}
	public void setCostprice(Double costprice) {
		this.costprice = costprice;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getProductTitle() {
		return productTitle;
	}
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
	public String getCategoryI() {
		return categoryI;
	}
	public void setCategoryI(String categoryI) {
		this.categoryI = categoryI;
	}
	public String getCategoryII() {
		return categoryII;
	}
	public void setCategoryII(String categoryII) {
		this.categoryII = categoryII;
	}
	public String getCategoryIII() {
		return categoryIII;
	}
	public void setCategoryIII(String categoryIII) {
		this.categoryIII = categoryIII;
	}
	public String getCategoryIName() {
		return categoryIName;
	}
	public void setCategoryIName(String categoryIName) {
		this.categoryIName = categoryIName;
	}
	public String getCategoryIIName() {
		return categoryIIName;
	}
	public void setCategoryIIName(String categoryIIName) {
		this.categoryIIName = categoryIIName;
	}
	public String getCategoryIIIName() {
		return categoryIIIName;
	}
	public void setCategoryIIIName(String categoryIIIName) {
		this.categoryIIIName = categoryIIIName;
	}
	public Integer getmTotalStock() {
		return mTotalStock;
	}
	public void setmTotalStock(Integer mTotalStock) {
		this.mTotalStock = mTotalStock;
	}
	public Integer getcTotalStock() {
		return cTotalStock;
	}
	public void setcTotalStock(Integer cTotalStock) {
		this.cTotalStock = cTotalStock;
	}
	public String getcProductTitle() {
		return cProductTitle;
	}
	public void setcProductTitle(String cProductTitle) {
		this.cProductTitle = cProductTitle;
	}
	public String getcCategoryName() {
		return cCategoryName;
	}
	public void setcCategoryName(String cCategoryName) {
		this.cCategoryName = cCategoryName;
	}
	public String getiCategoryId() {
		return iCategoryId;
	}
	public void setiCategoryId(String iCategoryId) {
		this.iCategoryId = iCategoryId;
	}

}
