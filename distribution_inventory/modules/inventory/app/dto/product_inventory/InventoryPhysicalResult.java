package dto.product_inventory;

/**
 * 库存盘点结果
 * @author lenovo
 *
 */
public class InventoryPhysicalResult<T> {
	private String sku;
	private Integer warehouseId;
	private String msg;
	private T  data;
	
	public InventoryPhysicalResult(String sku, Integer warehouseId, String msg) {
		super();
		this.sku = sku;
		this.warehouseId = warehouseId;
		this.msg = msg;
	}
	
	public InventoryPhysicalResult() {
		super();
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
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "InventoryPhysicalResult [sku=" + sku + ", warehouseId=" + warehouseId + ", msg=" + msg + ", data="
				+ data + "]";
	}
}
