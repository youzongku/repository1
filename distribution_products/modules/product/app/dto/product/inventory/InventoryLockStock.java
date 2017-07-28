package dto.product.inventory;

import java.io.Serializable;

/**
 * KA锁库
 * @author zbc
 * 2017年4月25日 上午11:36:07
 */
public class InventoryLockStock implements Serializable{
	private static final long serialVersionUID = -7872731393212941000L;
	private String sku;
	private Integer warehouseId;
	private Integer stock;
	
	public InventoryLockStock(){
		
	}
	/**
	 * @param sku
	 * @param wawarehouseId
	 * @param stock
	 */
	public InventoryLockStock(String sku, Integer warehouseId, Integer stock) {
		super();
		this.sku = sku;
		this.warehouseId = warehouseId;
		this.stock = stock;
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
	public Integer getStock() {
		return stock;
	}
	public void setStock(Integer stock) {
		this.stock = stock;
	}
}
