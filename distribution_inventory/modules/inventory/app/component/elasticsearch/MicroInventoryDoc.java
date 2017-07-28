package component.elasticsearch;

/**
 * 
 * 商品保存在es里的实体，不包含库存
 * @author huangjc
 * @date 2016年11月28日
 */
public class MicroInventoryDoc {
	
	@MappingType(type = "integer")
	private Integer id;
	@MappingType
	private String csku;
	@MappingType
	private String warehouseName;
	@MappingType(type = "integer")
	private Integer warehouseId;
	@MappingType(type = "integer")
	private Integer stockNum;
	@MappingType
	private String disAccount;
	
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
	public String getDisAccount() {
		return disAccount;
	}
	public void setDisAccount(String disAccount) {
		this.disAccount = disAccount;
	}
	@Override
	public String toString() {
		return "MicroInventoryDoc [id=" + id + ", sku=" + csku + ", warehouseName=" + warehouseName + ", warehouseId="
				+ warehouseId + ", stockNum=" + stockNum + ", disAccount=" + disAccount + "]";
	}
}