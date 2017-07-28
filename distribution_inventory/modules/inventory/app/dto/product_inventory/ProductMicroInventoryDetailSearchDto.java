package dto.product_inventory;

import entity.product_inventory.ProductMicroInventoryDetail;

import java.util.Date;
import java.util.List;

public class ProductMicroInventoryDetailSearchDto {
	private List<String> skus;
	private List<ProductMicroInventoryDetail> skuAndWarehouseIds;
	private Integer warehouseId;
	private String account;
	private Integer pageSize;
	private Integer currPage;
	private String title;
	private Integer avaliableStock;
	private String key;
	private Integer categoryId;
	private String categoryName;
	private Date expirationDate;
	private Date expirationDateEnd;

	private String sidx;//jqGrid相关的排序关键字
	private String sord;//jqGrid相关排序方式

	private String sku;

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

	public List<String> getSkus() {
		return skus;
	}

	public void setSkus(List<String> skus) {
		this.skus = skus;
	}

	public List<ProductMicroInventoryDetail> getSkuAndWarehouseIds() {
		return skuAndWarehouseIds;
	}

	public void setSkuAndWarehouseIds(List<ProductMicroInventoryDetail> skuAndWarehouseIds) {
		this.skuAndWarehouseIds = skuAndWarehouseIds;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getCurrPage() {
		return currPage;
	}

	public void setCurrPage(Integer currPage) {
		this.currPage = currPage;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getAvaliableStock() {
		return avaliableStock;
	}

	public void setAvaliableStock(Integer avaliableStock) {
		this.avaliableStock = avaliableStock;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Date getExpirationDateEnd() {
		return expirationDateEnd;
	}

	public void setExpirationDateEnd(Date expirationDateEnd) {
		this.expirationDateEnd = expirationDateEnd;
	}

	public String getSidx() {
		return sidx;
	}

	public void setSidx(String sidx) {
		this.sidx = sidx;
	}

	public String getSord() {
		return sord;
	}

	public void setSord(String sord) {
		this.sord = sord;
	}


	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;


	}

	@Override
	public String toString() {
		return "ProductMicroInventoryDetailSearchDto{" +
				"skus=" + skus +
				", skuAndWarehouseIds=" + skuAndWarehouseIds +
				", warehouseId=" + warehouseId +
				", account='" + account + '\'' +
				", pageSize=" + pageSize +
				", currPage=" + currPage +
				", title='" + title + '\'' +
				", avaliableStock=" + avaliableStock +
				", key='" + key + '\'' +
				", categoryId=" + categoryId +
				", categoryName='" + categoryName + '\'' +
				", expirationDate=" + expirationDate +
				", expirationDateEnd=" + expirationDateEnd +
				", sidx='" + sidx + '\'' +
				", sord='" + sord + '\'' +
				", sku='" + sku + '\'' +
				'}';
	}
}