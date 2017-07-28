package dto.product;

/**
 * 商品 对应每个到期日期的库存
 * @author zbc
 * 2017年4月19日 上午11:04:13
 */
public class CloudExpirationFormatResult {
	
	private Integer id;
	private String sku;
	private Integer warehouseId;
	private String expirationDate;
	private String warehouseName;
	private Integer stock;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getStock() {
		if(stock == null || stock < 0){
			return 0;
		}
		return stock;
	}
	public void setStock(Integer stock) {
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
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	@Override
	public String toString() {
		return "CloudExpirationFormatResult [sku=" + sku + ", warehouseId=" + warehouseId + ", expirationDate="
				+ expirationDate + ", warehouseName=" + warehouseName + "]";
	}
	
}
