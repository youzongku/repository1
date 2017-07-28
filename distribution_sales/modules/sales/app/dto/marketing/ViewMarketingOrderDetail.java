package dto.marketing;
/**
 * 过滤掉了MarketingOrderDetail里的到仓价
 * 
 * @author huangjc
 * @since 2017年3月10日
 */
public class ViewMarketingOrderDetail {
	private Integer id;
	private Integer marketingOrderId;
	private String marketingOrderNo;
	private String productName;
	private String interBarCode;// 国际条码
	private String productImg;
	private String sku;
	private Integer qty;
	private Double disPrice;
	private Integer warehouseId;
	private String warehouseName;
	private String expirationDate;
	private Integer categoryId;
	private String categoryName;

	private Double arriveWarePrice;//到仓价

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMarketingOrderId() {
		return marketingOrderId;
	}

	public void setMarketingOrderId(Integer marketingOrderId) {
		this.marketingOrderId = marketingOrderId;
	}

	public String getMarketingOrderNo() {
		return marketingOrderNo;
	}

	public void setMarketingOrderNo(String marketingOrderNo) {
		this.marketingOrderNo = marketingOrderNo;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getInterBarCode() {
		return interBarCode;
	}

	public void setInterBarCode(String interBarCode) {
		this.interBarCode = interBarCode;
	}

	public String getProductImg() {
		return productImg;
	}

	public void setProductImg(String productImg) {
		this.productImg = productImg;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Double getDisPrice() {
		return disPrice;
	}

	public void setDisPrice(Double disPrice) {
		this.disPrice = disPrice;
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

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Double getArriveWarePrice() {
		return arriveWarePrice;
	}

	public void setArriveWarePrice(Double arriveWarePrice) {
		this.arriveWarePrice = arriveWarePrice;
	}

	@Override
	public String toString() {
		return "ViewMarketingOrderDetail [id=" + id + ", marketingOrderId="
				+ marketingOrderId + ", marketingOrderNo=" + marketingOrderNo
				+ ", productName=" + productName + ", interBarCode="
				+ interBarCode + ", productImg=" + productImg + ", sku=" + sku
				+ ", qty=" + qty + ", disPrice=" + disPrice + ", warehouseId="
				+ warehouseId + ", warehouseName=" + warehouseName
				+ ", expirationDate=" + expirationDate + ", categoryId="
				+ categoryId + ", categoryName=" + categoryName + "]";
	}

}
