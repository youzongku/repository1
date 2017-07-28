package dto.purchase;
/**
 * 封装sku、warehouseId、qty
 * @author huangjc
 * @date 2016年12月13日
 */
public class SkuWarehouse2Qty {
	private String sku;
	private int warehouseId;
	private int qty;

	public SkuWarehouse2Qty(String sku, int warehouseId, int qty) {
		super();
		this.sku = sku;
		this.warehouseId = warehouseId;
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

	@Override
	public String toString() {
		return "SkuWarehouse2Qty [sku=" + sku + ", warehouseId=" + warehouseId
				+ ", qty=" + qty + "]";
	}

}
