package dto.warehousing;

import java.util.List;

public class GoodsInventorySearchDto {

	private Integer id;

    private String sku;

    private Integer warehouseId;//仓库id

    private String warehouseName;//仓库名
    
    private String key;//关键字，sku或商品名称
    
    private Integer categoryI;//类目id
    
    private List<String> skuList;//多个sku批量查询

	private int pageNo;
	
	private int pageSize;
	
	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getCategoryI() {
		return categoryI;
	}

	public void setCategoryI(Integer categoryI) {
		this.categoryI = categoryI;
	}

	public List<String> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<String> skuList) {
		this.skuList = skuList;
	}

	@Override
	public String toString() {
		return "GoodsInventorySearchDto [id=" + id + ", sku=" + sku + ", warehouseId=" + warehouseId
				+ ", warehouseName=" + warehouseName + ", key=" + key + ", categoryI=" + categoryI + ", skuList="
				+ skuList + ", pageNo=" + pageNo + ", pageSize=" + pageSize + "]";
	}
}
