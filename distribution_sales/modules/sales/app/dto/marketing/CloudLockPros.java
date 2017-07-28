package dto.marketing;

import java.io.Serializable;

import entity.marketing.MarketingOrderDetail;

/**
 * 营销单-云仓锁库实体
 * @author zbc
 * 2016年12月26日 下午5:38:47
 */
public class CloudLockPros implements Serializable {

	private static final long serialVersionUID = 2781669986022110873L;

	/**
	 * 商品编码
	 */
	private String sku;
	
    /**
     * 数量
     */
    private Integer qty;
    
    /**
     * 采购价
     */
    private Double purchasePrice;
    
    /**
     * 是否是赠品
     */
    private Integer isGift;
    
    /**
     * 仓库id
     */
    private Integer warehouseId;
    
    /**
     *仓库名称 
     */
    private String warehouseName;
    
    /**
     * 图片链接
     */
    private String imgUrl;
    
    /**
     * 商品标题
     */
    private String productTitle;
    
    /**
     * 均摊价
     */
    private Double capfee;
    
    /**
     * 到仓价
     */
    private Double arriveWarePrice;
    
    /**
     * 类目id
     */
    private Integer categoryId;
    
    /**
     * 类目名称
     */
    private String  categoryName;
    
    /**
     * 到期日期
     */
    private String expirationDate;

	public CloudLockPros(MarketingOrderDetail detail) {
		this.sku = detail.getSku();
		this.qty = detail.getQty();
		this.purchasePrice = detail.getDisPrice();
		this.isGift = 0;
		this.warehouseId = detail.getWarehouseId();
		this.warehouseName = detail.getWarehouseName();
		this.imgUrl = detail.getProductImg();
		this.productTitle = detail.getProductName();
		this.capfee = 0.00;
		this.categoryId = detail.getCategoryId();
		this.categoryName = detail.getCategoryName();
		this.expirationDate = detail.getExpirationDate();
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

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public Integer getIsGift() {
		return isGift;
	}

	public void setIsGift(Integer isGift) {
		this.isGift = isGift;
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

	public Double getCapfee() {
		return capfee;
	}

	public void setCapfee(Double capfee) {
		this.capfee = capfee;
	}

	@Override
	public String toString() {
		return " {\"sku\":\"" + sku + "\",\"qty\":" + qty + ",\"purchasePrice\":" + purchasePrice + ",\"isGift\":" + isGift
				+ ",\"warehouseId\":" + warehouseId + ",\"warehouseName\":\"" + warehouseName + "\",\"imgUrl\":\"" + imgUrl
				+ "\",\"productTitle\":\"" + productTitle + "\",\"capfee\":" + capfee + "\",\"expirationDate\":" + expirationDate + "}";
	}
	
}
