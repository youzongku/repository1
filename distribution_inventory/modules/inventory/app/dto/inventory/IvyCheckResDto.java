package dto.inventory;

public class IvyCheckResDto {

	/**
	 * 已检查的sku
	 */
	private String sku;
	/**
	 * 已检查的sku的微仓状态 <li>enough：微仓充足</li> <li>notEnough：微仓不充足</li> <li>
	 * notExist：微仓中不存在</li>
	 */
	private String status;
	/**
	 * 微仓中缺少的库存数量
	 */
	private int stockOutQty;

	/**
	 * 发货的总数量
	 */
	private int sendoutTotalQty;

	/**
	 * 所选仓库id
	 */
	private int warehouseId;

	private String warehouseName;

	private String productName;

	private double purchasePrice;

	private double marketPrice;

	private String productImg;

	private String salesOrderNo;
	
	private Boolean isgift;
	
	private Integer giftNum;
	
	private Integer totoalStock;
	
	public Boolean getIsgift() {
		return isgift;
	}

	public void setIsgift(Boolean isgift) {
		this.isgift = isgift;
	}

	public Integer getGiftNum() {
		return giftNum;
	}

	public void setGiftNum(Integer giftNum) {
		this.giftNum = giftNum;
	}

	public IvyCheckResDto() {
		super();
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getStatus() {
		return status;
	}

	/**
	 * 已检查的sku的微仓状态 <li>enough：微仓充足</li> <li>notEnough：微仓不充足</li> <li>
	 * notExist：微仓中不存在</li>
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public int getStockOutQty() {
		return stockOutQty;
	}

	public void setStockOutQty(int stockOutQty) {
		this.stockOutQty = stockOutQty;
	}

	public int getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getProductImg() {
		return productImg;
	}

	public void setProductImg(String productImg) {
		this.productImg = productImg;
	}

	public double getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(double marketPrice) {
		this.marketPrice = marketPrice;
	}

	public String getSalesOrderNo() {
		return salesOrderNo;
	}

	public void setSalesOrderNo(String salesOrderNo) {
		this.salesOrderNo = salesOrderNo;
	}

	public int getSendoutTotalQty() {
		return sendoutTotalQty;
	}

	public void setSendoutTotalQty(int sendoutTotalQty) {
		this.sendoutTotalQty = sendoutTotalQty;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Integer getTotoalStock() {
		return totoalStock;
	}

	public void setTotoalStock(Integer totoalStock) {
		this.totoalStock = totoalStock;
	}

	@Override
	public String toString() {
		return "IvyCheckResDto [sku=" + sku + ", status=" + status
				+ ", stockOutQty=" + stockOutQty + ", sendoutTotalQty="
				+ sendoutTotalQty + ", warehouseId=" + warehouseId
				+ ", warehouseName=" + warehouseName + ", productName="
				+ productName + ", purchasePrice=" + purchasePrice
				+ ", marketPrice=" + marketPrice + ", productImg=" + productImg
				+ ", salesOrderNo=" + salesOrderNo + "]";
	}

}
