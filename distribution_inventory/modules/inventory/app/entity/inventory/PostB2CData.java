package entity.inventory;

import java.util.List;



public class PostB2CData {
	
	//product模块查询商品库存信息接口参数
	private List<String> skus;
	
	private Integer warehouseId;
	
	private Integer currPage;
	
	private Integer pageSize;
	
	private Double maxPrice;
	
	private Double minPrice;
	
	
	//b2c推送库存信息
	private List<B2CGoodsInventory> goods;//商品库存信息
	
	
	private List<B2CWarehouse> houses;//仓库信息
	
	
	//b2c仓库信息
	private List<B2CWarehouse> list;//仓库信息
	private Integer totalCount;
	private Integer pageNo;
	private Integer totalPages;
	
	private String sku;
	
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}
	
	public List<B2CWarehouse> getList() {
		return list;
	}

	public void setList(List<B2CWarehouse> list) {
		this.list = list;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
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

	public List<String> getSkus() {
		return skus;
	}

	public void setSkus(List<String> skus) {
		this.skus = skus;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Integer getCurrPage() {
		return currPage;
	}

	public void setCurrPage(Integer currPage) {
		this.currPage = currPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public PostB2CData() {
	}

	public List<B2CGoodsInventory> getGoods() {
		return goods;
	}

	public void setGoods(List<B2CGoodsInventory> goods) {
		this.goods = goods;
	}

	public List<B2CWarehouse> getHouses() {
		return houses;
	}

	public void setHouses(List<B2CWarehouse> houses) {
		this.houses = houses;
	}

}
