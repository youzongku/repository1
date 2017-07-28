package dto.purchase.returnod;

/**
 * 微仓退货：暂时一个退货单只对应一个发货单，可以把两者合并成一个类的数据，方便使用
 * 
 * @author Administrator
 *
 */
public class ReturnOrderInfo {
	// 退货单部分
	private Integer returnOrderId;
	private String returnOrderNo;// 退货单号
	private String email;
	private Double userExpectTotalReturnAmount; // 用户期待的退款金额
	private Double totalReturnAmount;// 退款总金额，根据详情计算出来的
	private Double actualTotalReturnAmount;// 实际退款总金额，审核时可以修改totalReturnAmount
	private Integer status;// 状态
	// 退货单详情部分
	private Integer returnOrderDetailId;
	private String purchaseOrderNo;// 所属采购单单号
	private String sku;
	private Integer warehouseId;
	private Double purchasePrice;
	private Double capfee;
	private String purchaseTime;
	private String expirationDate;
	private Integer returnQty;// 退货数量
	private Integer qty;// 采购数量
	private Integer residueNum;// 剩余数量

	public Integer getReturnOrderId() {
		return returnOrderId;
	}

	public void setReturnOrderId(Integer returnOrderId) {
		this.returnOrderId = returnOrderId;
	}

	public String getReturnOrderNo() {
		return returnOrderNo;
	}

	public void setReturnOrderNo(String returnOrderNo) {
		this.returnOrderNo = returnOrderNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Double getUserExpectTotalReturnAmount() {
		return userExpectTotalReturnAmount;
	}

	public void setUserExpectTotalReturnAmount(Double userExpectTotalReturnAmount) {
		this.userExpectTotalReturnAmount = userExpectTotalReturnAmount;
	}

	public Double getTotalReturnAmount() {
		return totalReturnAmount;
	}

	public void setTotalReturnAmount(Double totalReturnAmount) {
		this.totalReturnAmount = totalReturnAmount;
	}

	public Double getActualTotalReturnAmount() {
		return actualTotalReturnAmount;
	}

	public void setActualTotalReturnAmount(Double actualTotalReturnAmount) {
		this.actualTotalReturnAmount = actualTotalReturnAmount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getReturnOrderDetailId() {
		return returnOrderDetailId;
	}

	public void setReturnOrderDetailId(Integer returnOrderDetailId) {
		this.returnOrderDetailId = returnOrderDetailId;
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

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public Double getCapfee() {
		return capfee;
	}

	public void setCapfee(Double capfee) {
		this.capfee = capfee;
	}

	public String getPurchaseTime() {
		return purchaseTime;
	}

	public void setPurchaseTime(String purchaseTime) {
		this.purchaseTime = purchaseTime;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Integer getReturnQty() {
		return returnQty;
	}

	public void setReturnQty(Integer returnQty) {
		this.returnQty = returnQty;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Integer getResidueNum() {
		return residueNum;
	}

	public void setResidueNum(Integer residueNum) {
		this.residueNum = residueNum;
	}

	@Override
	public String toString() {
		return "ReturnOrderInfo [returnOrderId=" + returnOrderId + ", returnOrderNo=" + returnOrderNo + ", email="
				+ email + ", userExpectTotalReturnAmount=" + userExpectTotalReturnAmount + ", totalReturnAmount="
				+ totalReturnAmount + ", actualTotalReturnAmount=" + actualTotalReturnAmount + ", status=" + status
				+ ", returnOrderDetailId=" + returnOrderDetailId + ", purchaseOrderNo=" + purchaseOrderNo + ", sku="
				+ sku + ", warehouseId=" + warehouseId + ", purchasePrice=" + purchasePrice + ", capfee=" + capfee
				+ ", purchaseTime=" + purchaseTime + ", expirationDate=" + expirationDate + ", returnQty=" + returnQty
				+ ", qty=" + qty + ", residueNum=" + residueNum + "]";
	}

}
