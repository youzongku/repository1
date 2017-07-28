package forms.marketing.promotion;

import java.util.List;

public class ProductSearchForm {
	private Integer warehouseId;
	private Double minPrice;
	private Double maxPrice;
	private Integer curr;
	private Integer pageSize;
	private String categoryId;
	private String searchText;
	private List<String> iids;

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public Integer getCurr() {
		return curr;
	}

	public void setCurr(Integer curr) {
		this.curr = curr;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public List<String> getIids() {
		return iids;
	}

	public void setIids(List<String> iids) {
		this.iids = iids;
	}

	@Override
	public String toString() {
		return "ProductSearchForm [warehouseId=" + warehouseId + ", minPrice="
				+ minPrice + ", maxPrice=" + maxPrice + ", curr=" + curr
				+ ", pageSize=" + pageSize + ", categoryId=" + categoryId
				+ ", searchText=" + searchText + ", iids=" + iids + "]";
	}

}
