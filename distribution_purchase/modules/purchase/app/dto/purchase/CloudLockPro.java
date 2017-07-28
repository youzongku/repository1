package dto.purchase;

import java.io.Serializable;

import entity.purchase.PurchaseOrderDetail;

/**
 * 云仓锁库实体
 * 
 * @author zbc 2016年12月26日 下午5:38:47
 */
/**
 * @author zbc 2017年3月24日 上午10:49:22
 */
public class CloudLockPro implements Serializable {

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
	 * 仓库名称
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
	private String categoryName;

	/**
	 * 合同号
	 */
	private String contractNo;

	/**
	 * 到期日期
	 */
	private String expirationDate;
	
	/**
	 * 清货价
	 */
	private Double clearancePrice;

	public CloudLockPro(PurchaseOrderDetail detail) {
		this.sku = detail.getSku();
		this.qty = detail.getQty();
		this.purchasePrice = detail.getPurchasePrice();
		this.isGift = detail.getIsgift() ? 1 : 0;
		this.warehouseId = detail.getWarehouseId();
		this.warehouseName = detail.getWarehouseName();
		this.imgUrl = detail.getProductImg();
		this.productTitle = detail.getProductName();
		this.capfee = detail.getCapFee();
		this.categoryId = detail.getCategoryId();
		this.categoryName = detail.getCategoryName();
		this.contractNo = detail.getContractNo();
		this.expirationDate = detail.getExpirationDate();
		this.clearancePrice = detail.getClearancePrice();
	}
	
	public CloudLockPro(OrderDetail detial,Double capFee){
		this.sku = detial.getSku();
		this.qty = detial.getQty();
		this.purchasePrice = detial.getPurchasePrice();
		this.isGift = detial.getIsGift();
		this.warehouseId = detial.getWarehouseId();
		this.warehouseName = detial.getWarehouseName();
		this.imgUrl = detial.getImgUrl();
		this.productTitle = detial.getProductTitle();
		this.capfee = capFee;
		this.arriveWarePrice = detial.getArriveWarePrice();
		this.categoryId = detial.getCategoryId();
		this.categoryName = detial.getCategoryName();
		this.contractNo = detial.getContractNo();
		this.expirationDate = detial.getExpirationDate();
		this.clearancePrice = detial.getClearancePrice();
	}

	public Double getClearancePrice() {
		return clearancePrice;
	}

	public void setClearancePrice(Double clearancePrice) {
		this.clearancePrice = clearancePrice;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
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

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public String toString() {
		return "CloudLockPros [sku=" + sku + ", qty=" + qty
				+ ", purchasePrice=" + purchasePrice + ", isGift=" + isGift
				+ ", warehouseId=" + warehouseId + ", warehouseName="
				+ warehouseName + ", imgUrl=" + imgUrl + ", productTitle="
				+ productTitle + ", capfee=" + capfee + ", arriveWarePrice="
				+ arriveWarePrice + ", categoryId=" + categoryId
				+ ", categoryName=" + categoryName + ", expirationDate="
				+ expirationDate + "]";
	}

}
