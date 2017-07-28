package dto.product;


import entity.product.ProductPriceCategoryBrand;

public class ProductPriceFactorDto extends ProductPriceCategoryBrand{

	
	private String key;//模糊搜索关键字
	
	private Integer pageSize;//页面size
	
	private Integer currPage;//当前页

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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

}
