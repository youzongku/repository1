package entity.product;

public class StockEntity {
	
	private String sku;//SKU
	
	private Integer warehouseId;//仓库ID
	
	private Integer stock;//云仓库存
	
	private Integer microStock;//微仓库存

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

	public Integer getMicroStock() {
		return microStock;
	}

	public void setMicroStock(Integer microStock) {
		this.microStock = microStock;
	}
	
}
