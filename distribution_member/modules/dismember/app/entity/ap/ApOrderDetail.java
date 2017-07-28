package entity.ap;

/**
 * 账期支付的订单详情
 */
public class ApOrderDetail {
	private Integer id;
	private Integer apOrderId;
	private String orderNo;
	// 当orderNo是合并发货单单号时，salesOrderNo有值
	private String salesOrderNo;
	private String sku;
	private String productName;
	private Integer qty;
	private Integer warehouseId;
	private String warehouseName;

	public ApOrderDetail() {
	}

	public ApOrderDetail(Integer apOrderId, String orderNo, String sku, String productName, Integer qty,
			Integer warehouseId, String warehouseName) {
		super();
		this.apOrderId = apOrderId;
		this.orderNo = orderNo;
		this.sku = sku;
		this.productName = productName;
		this.qty = qty;
		this.warehouseId = warehouseId;
		this.warehouseName = warehouseName;
	}

	public String getSalesOrderNo() {
		return salesOrderNo;
	}

	public void setSalesOrderNo(String salesOrderNo) {
		this.salesOrderNo = salesOrderNo;
	}

	public Integer getApOrderId() {
		return apOrderId;
	}

	public void setApOrderId(Integer apOrderId) {
		this.apOrderId = apOrderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	@Override
	public String toString() {
		return "ApOrderDetail [id=" + id + ", apOrderId=" + apOrderId + ", orderNo=" + orderNo + ", salesOrderNo="
				+ salesOrderNo + ", sku=" + sku + ", productName=" + productName + ", qty=" + qty + ", warehouseId="
				+ warehouseId + ", warehouseName=" + warehouseName + "]";
	}

}
