package dto.product_inventory;

public class ReturnProductData {
	private Integer inRecordId;// 入库记录id
	private String purchaseOrderNo;// 采购单号
	private String returnOrderNo;// 退货单号
	private Integer returnQty;// 数量
	private String sku;
	private Integer warehouseId;// 仓库id
	private String account;// 账号
	public Integer getInRecordId() {
		return inRecordId;
	}
	public void setInRecordId(Integer inRecordId) {
		this.inRecordId = inRecordId;
	}
	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}
	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}
	public String getReturnOrderNo() {
		return returnOrderNo;
	}
	public void setReturnOrderNo(String returnOrderNo) {
		this.returnOrderNo = returnOrderNo;
	}
	public Integer getReturnQty() {
		return returnQty;
	}
	public void setReturnQty(Integer returnQty) {
		this.returnQty = returnQty;
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
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	@Override
	public String toString() {
		return "ReturnProductData [inRecordId=" + inRecordId + ", purchaseOrderNo=" + purchaseOrderNo
				+ ", returnOrderNo=" + returnOrderNo + ", returnQty=" + returnQty + ", sku=" + sku + ", warehouseId="
				+ warehouseId + ", account=" + account + "]";
	}
	
}
