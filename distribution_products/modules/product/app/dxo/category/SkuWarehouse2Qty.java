package dxo.category;

public class SkuWarehouse2Qty {
	private String sku;
	private int warehouseId;
	private int qty;
	
	public SkuWarehouse2Qty(String sku, int warehouseId) {
		super();
		this.sku = sku;
		this.warehouseId = warehouseId;
	}

	public SkuWarehouse2Qty(String sku, int warehouseId, int qty) {
		this(sku, warehouseId);
		this.qty = qty;
	}

	public int getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}
}
