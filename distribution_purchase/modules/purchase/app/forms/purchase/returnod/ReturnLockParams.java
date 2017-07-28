package forms.purchase.returnod;

/**
 * 微仓退货锁定微仓要的参数
 * 
 * @author huangjc
 * @date 2017年2月16日
 */
public class ReturnLockParams {
	private String returnOrderNo;
	private String purchaseOrderNo;
	private String sku;
	private Integer warehouseId;
	private Integer returnQty;
	private String account;
	private Integer inRecordId;

	public String getReturnOrderNo() {
		return returnOrderNo;
	}

	public void setReturnOrderNo(String returnOrderNo) {
		this.returnOrderNo = returnOrderNo;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
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

	public Integer getReturnQty() {
		return returnQty;
	}

	public void setReturnQty(Integer returnQty) {
		this.returnQty = returnQty;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getInRecordId() {
		return inRecordId;
	}

	public void setInRecordId(Integer inRecordId) {
		this.inRecordId = inRecordId;
	}

	@Override
	public String toString() {
		return "ReturnLockParams [returnOrderNo=" + returnOrderNo
				+ ", purchaseOrderNo=" + purchaseOrderNo + ", sku=" + sku
				+ ", warehouseId=" + warehouseId + ", returnQty=" + returnQty
				+ ", account=" + account + ", inRecordId=" + inRecordId + "]";
	}

}
