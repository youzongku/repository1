package forms.purchase;

import java.util.List;

public class GetProductsParams {
	private Integer categoryId;
	private Integer istatus;
	private String title;
	private Double maxPrice;
	private Double minPrice;
	private Integer pageSize;
	private Integer currPage;
	private String sku;
	private Integer warehouseId;
	private List<String> skuList;
	private String email;
	private Integer model;
	private Integer typeId;

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getModel() {
		return model;
	}

	public void setModel(Integer model) {
		this.model = model;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getIstatus() {
		return istatus;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public Double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
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

	public List<String> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<String> skuList) {
		this.skuList = skuList;
	}

	@Override
	public String toString() {
		return "GetProductsParams [categoryId=" + categoryId + ", istatus="
				+ istatus + ", title=" + title + ", maxPrice=" + maxPrice
				+ ", minPrice=" + minPrice + ", pageSize=" + pageSize
				+ ", currPage=" + currPage + ", sku=" + sku + ", warehouseId="
				+ warehouseId + ", skuList=" + skuList + ", email=" + email
				+ ", model=" + model + ", typeId=" + typeId + "]";
	}

}
