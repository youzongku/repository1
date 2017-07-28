package component.elasticsearch;

/**
 * 商品保存在es里的实体，不包含库存
 * @author huangjc
 * @date 2016年11月28日
 */
public class CloudInventoryDoc {
	
	@MappingType(type = "integer")
	private Integer id;
	@MappingType
	private String csku;
	@MappingType
	private String warehouseName;
	@MappingType(type = "integer")
	private Integer warehouseId;// 仓库ID
	@MappingType(type = "integer")
	private Integer stockNum;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCsku() {
		return csku;
	}
	public void setCsku(String csku) {
		this.csku = csku;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public Integer getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
	public Integer getStockNum() {
		return stockNum;
	}
	public void setStockNum(Integer stockNum) {
		this.stockNum = stockNum;
	}
	@Override
	public String toString() {
		return "CloudInventoryDoc [id=" + id + ", csku=" + csku + ", warehouseName=" + warehouseName + ", warehouseId="
				+ warehouseId + ", stockNum=" + stockNum + "]";
	}
	
}