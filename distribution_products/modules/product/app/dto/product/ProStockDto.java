package dto.product;

/**
 * 新商品库存实体
 * @author zbc
 * 2017年1月4日 下午4:25:01
 */
public class ProStockDto {
	/**
	 * 商品sku
	 */
	private String sku;
	/**
	 * 仓库id
	 */
	private Integer warehouseId;
	/**
	 * 云仓库存
	 */
	private Integer cloudInventory;
	/**
	 * 微仓库存
	 */
	private Integer microInventory;
	
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
	public Integer getCloudInventory() {
		return cloudInventory;
	}
	public void setCloudInventory(Integer cloudInventory) {
		this.cloudInventory = cloudInventory;
	}
	public Integer getMicroInventory() {
		return microInventory;
	}
	public void setMicroInventory(Integer microInventory) {
		this.microInventory = microInventory;
	}

	@Override
	public String toString() {
		return "ProStockDto{" +
				"microInventory=" + microInventory +
				", cloudInventory=" + cloudInventory +
				", warehouseId=" + warehouseId +
				", sku='" + sku + '\'' +
				'}';
	}
}
