package dto.product_inventory;

public class HistoryDateOrderDetail {
    private String sku;
    
    private String imgUrl;
    
    private String productTitle;

    private Integer qty;

    private Float purchasePrice;
    
    private Float capfee;
    
    private Float arriveWarePrice;

    private Integer warehouseId;

    private String warehouseName;

    private Short isGift;
    
    private String orderNo;

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Float getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Float purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public Float getCapfee() {
		return capfee;
	}

	public void setCapfee(Float capfee) {
		this.capfee = capfee;
	}

	public Float getArriveWarePrice() {
		return arriveWarePrice;
	}

	public void setArriveWarePrice(Float arriveWarePrice) {
		this.arriveWarePrice = arriveWarePrice;
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

	public Short getIsGift() {
		return isGift;
	}

	public void setIsGift(Short isGift) {
		this.isGift = isGift;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Override
	public String toString() {
		return "HistoryDateOrderDetail [sku=" + sku + ", imgUrl=" + imgUrl + ", productTitle=" + productTitle + ", qty="
				+ qty + ", purchasePrice=" + purchasePrice + ", capfee=" + capfee + ", arriveWarePrice="
				+ arriveWarePrice + ", warehouseId=" + warehouseId + ", warehouseName=" + warehouseName + ", isGift="
				+ isGift + ", orderNo=" + orderNo + "]";
	}
    
    
}
