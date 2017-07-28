package dto.discart;

import java.io.Serializable;

/**
 * 采购发货 商品详情
 * @author zbc
 * 2017年5月25日 下午7:46:51
 */
public class PurchaseSaleDetailDto implements Serializable {

	private static final long serialVersionUID = 1522781596127187772L;
	
	private String sku;//商品编码
	private Integer num;//商品数量
	private String productName;//商品名称
	private String productImg;//商品图片
	private Double purchasePrice;//分销价
	private Double marketPrice;//市场零售价
	private Integer warehouseId;//仓库id
	private String warehouseName;//仓库名称
	private Double finalSellingPrice;//最终售价
	
	public PurchaseSaleDetailDto(OrderDetail detail) {
		super();
		this.sku = detail.getSku();
		this.num = detail.getQty();
		this.productName = detail.getTitle();
		this.productImg = detail.getPublicImg();
		this.purchasePrice = detail.getPrice();
		this.marketPrice = detail.getMarketPrice();
		this.warehouseId = detail.getWarehouseId();
		this.warehouseName = detail.getWarehouseName();
		this.finalSellingPrice = detail.getPrice();
	}
	public PurchaseSaleDetailDto() {
		super();
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductImg() {
		return productImg;
	}
	public void setProductImg(String productImg) {
		this.productImg = productImg;
	}
	public Double getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public Double getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(Double marketPrice) {
		this.marketPrice = marketPrice;
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
	public Double getFinalSellingPrice() {
		return finalSellingPrice;
	}
	public void setFinalSellingPrice(Double finalSellingPrice) {
		this.finalSellingPrice = finalSellingPrice;
	}

}
