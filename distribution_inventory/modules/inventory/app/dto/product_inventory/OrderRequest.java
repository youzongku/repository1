package dto.product_inventory;

public class OrderRequest {
    @Override
	public String toString() {
		return "OrderRequest [sku=" + sku + ", productTitle=" + productTitle + ", imgUrl=" + imgUrl + ", qty=" + qty
				+ ", purchasePrice=" + purchasePrice + ", warehouseId=" + warehouseId + ", warehouseName="
				+ warehouseName + ", isGift=" + isGift + ", capfee=" + capfee + ", arriveWarePrice=" + arriveWarePrice
				+ ", categoryId=" + categoryId + ", categoryName=" + categoryName + ", expirationDate=" + expirationDate
				+ ", contractNo=" + contractNo + ", clearancePrice=" + clearancePrice
				+ "]";
	}

	private String sku;
    
    private String productTitle;
    
    private String imgUrl;

    private Integer qty;

    private Float purchasePrice;

    private Integer warehouseId;

    private String warehouseName;

    private Short isGift;

    private Float capfee;
    
    private Float arriveWarePrice;
    
    private Integer categoryId;
    
    private String categoryName;
    
    private String expirationDate;
    
    private String contractNo;
    
    private Double clearancePrice;

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Double getClearancePrice() {
		return clearancePrice;
	}

	public void setClearancePrice(Double clearancePrice) {
		this.clearancePrice = clearancePrice;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
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

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
    
}
