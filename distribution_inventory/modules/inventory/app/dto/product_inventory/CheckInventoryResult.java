package dto.product_inventory;

import java.util.List;

public class CheckInventoryResult {
	private String sku;
	private Integer warehouseId;
	private Integer erpStock;//erp库存
	private Integer microStockpile;//微仓囤货
	private Integer orderOccupy;//订单占用
	private Integer cloudInventory;//云仓库存
	private List<String> orderNoList;//库存占用的订单号
	private String expirationDate;//商品到期日期
	private String msg;//信息
	
	public CheckInventoryResult() {
		super();
	}
	
	public CheckInventoryResult(String sku, Integer warehouseId, Integer erpStock, Integer microStockpile,
			Integer orderOccupy, Integer cloudInventory, List<String> orderNoList, String expirationDate, String msg) {
		super();
		this.sku = sku;
		this.warehouseId = warehouseId;
		this.erpStock = erpStock;
		this.microStockpile = microStockpile;
		this.orderOccupy = orderOccupy;
		this.cloudInventory = cloudInventory;
		this.orderNoList = orderNoList;
		this.expirationDate = expirationDate;
		this.msg = msg;
	}
	public CheckInventoryResult(String sku, Integer warehouseId, String msg) {
		super();
		this.sku = sku;
		this.warehouseId = warehouseId;
		this.msg = msg;
	}
	public CheckInventoryResult(String sku, Integer warehouseId, Integer erpStock, Integer microStockpile,
			Integer orderOccupy, List<String> orderNoList, String expirationDate, String msg) {
		super();
		this.sku = sku;
		this.warehouseId = warehouseId;
		this.erpStock = erpStock;
		this.microStockpile = microStockpile;
		this.orderOccupy = orderOccupy;
		this.orderNoList = orderNoList;
		this.expirationDate = expirationDate;
		this.msg = msg;
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
	public Integer getErpStock() {
		return erpStock;
	}
	public void setErpStock(Integer erpStock) {
		this.erpStock = erpStock;
	}
	public Integer getMicroStockpile() {
		return microStockpile;
	}
	public void setMicroStockpile(Integer microStockpile) {
		this.microStockpile = microStockpile;
	}
	public Integer getOrderOccupy() {
		return orderOccupy;
	}
	public void setOrderOccupy(Integer orderOccupy) {
		this.orderOccupy = orderOccupy;
	}
	public List<String> getOrderNoList() {
		return orderNoList;
	}
	public void setOrderNoList(List<String> orderNoList) {
		this.orderNoList = orderNoList;
	}
	public Integer getCloudInventory() {
		return cloudInventory;
	}

	public void setCloudInventory(Integer cloudInventory) {
		this.cloudInventory = cloudInventory;
	}

	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
