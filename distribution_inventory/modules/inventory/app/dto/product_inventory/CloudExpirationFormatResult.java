package dto.product_inventory;

import entity.product_inventory.ProductInventoryDetail;
import utils.inventory.DateUtils;

public class CloudExpirationFormatResult {
	
	private Integer id;
	private String sku;
	private Integer warehouseId;
	private String expirationDate;
	private String warehouseName;
	private Integer stock;
	public CloudExpirationFormatResult(ProductInventoryDetail detail){
		this.id=detail.getId();
		this.sku=detail.getSku();
		this.warehouseName=detail.getWarehouseName();
		this.warehouseId=detail.getWarehouseId();
		this.stock=detail.getStock();
		this.expirationDate=DateUtils.date2string(detail.getExpirationDate(), DateUtils.FORMAT_DATE_PAGE);
	}
	public CloudExpirationFormatResult(String sku, Integer warehouseId, String expirationDate) {
		super();
		this.sku = sku;
		this.warehouseId = warehouseId;
		this.expirationDate = expirationDate;
	}
	public CloudExpirationFormatResult() {
		super();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getStock() {
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
