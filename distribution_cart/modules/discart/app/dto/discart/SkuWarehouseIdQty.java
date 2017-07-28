package dto.discart;

public class SkuWarehouseIdQty {
	private String sku;
	private Integer warehouseId;
	private Integer qty;

	public SkuWarehouseIdQty() {
	}

	public SkuWarehouseIdQty(String sku, Integer warehouseId, Integer qty) {
		super();
		this.sku = sku;
		this.warehouseId = warehouseId;
		this.qty = qty;
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

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	@Override
	public String toString() {
		return "SkuWarehouseIdQty [sku=" + sku + ", warehouseId=" + warehouseId + ", qty=" + qty + "]";
	}

}
