package dto.discart;

import java.io.Serializable;

/**
 * Created by LSL on 2015/12/8.
 */
public class DisCartDto implements Serializable {

	private static final long serialVersionUID = 4973726704660421767L;

	private Integer itemId;// 购物车单元ID

	private String title;// 商品名称

	private String image;// 商品图片url

	private String sku;// 商品sku

	private String storageName;// 所选仓库

	private Integer warehouseId;

	private Double marketPrice;// 市场零售价

	private Double sumprice;// 单价X数量

	private Integer qty;// 购买数量

	private Integer isOrder;// 是否提交为订单（1：已提交，0：未提交）

	private Double purchaseCostPrice;// 分销成本价

	private Boolean selected;// 是否选中

	private Double disFreight;// 分销物流费

	private Integer disStockId;// 分销仓库id

	private Double disPrice;// 分销价

	private Integer batchnum;// 起批量

	// add by lzl增加属性
	private Integer categoryId;// 所属类目ID

	private Double fweight;// 重量

	private Integer istatus;// 商品状态(1在售，2停售，3下架)

	private Integer salable;// 非卖状态（0不可卖，1可卖）

	private String categoryName;// 类目名称

	private String interBarCode;

	/**
	 * 合同号
	 */
	private String contractNo;

	/**
	 * 清货价
	 */
	private Double clearancePrice;
	
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

	public String getInterBarCode() {
		return interBarCode;
	}

	public void setInterBarCode(String interBarCode) {
		this.interBarCode = interBarCode;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Integer getBatchnum() {
		return batchnum;
	}

	public void setBatchnum(Integer batchnum) {
		this.batchnum = batchnum;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getTitle() {
		return title;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getStorageName() {
		return storageName;
	}

	public void setStorageName(String storageName) {
		this.storageName = storageName;
	}

	public Double getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(Double marketPrice) {
		this.marketPrice = marketPrice;
	}

	public Double getSumprice() {
		return sumprice;
	}

	public void setSumprice(Double sumprice) {
		this.sumprice = sumprice;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Integer getIsOrder() {
		return isOrder;
	}

	public void setIsOrder(Integer isOrder) {
		this.isOrder = isOrder;
	}

	public Double getPurchaseCostPrice() {
		return purchaseCostPrice;
	}

	public void setPurchaseCostPrice(Double purchaseCostPrice) {
		this.purchaseCostPrice = purchaseCostPrice;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Double getDisFreight() {
		return disFreight;
	}

	public void setDisFreight(Double disFreight) {
		this.disFreight = disFreight;
	}

	public Integer getDisStockId() {
		return disStockId;
	}

	public void setDisStockId(Integer disStockId) {
		this.disStockId = disStockId;
	}

	public Double getDisPrice() {
		return disPrice;
	}

	public void setDisPrice(Double disPrice) {
		this.disPrice = disPrice;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Double getFweight() {
		return fweight;
	}

	public void setFweight(Double fweight) {
		this.fweight = fweight;
	}

	public Integer getIstatus() {
		return istatus;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
	}

	public Integer getSalable() {
		return salable;
	}

	public void setSalable(Integer salable) {
		this.salable = salable;
	}

}
