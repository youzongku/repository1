package dto.purchase;

import java.io.Serializable;

import entity.purchase.enums.Warehouse;

/**
 * Created by luwj on 2015/12/8. TODO 修改该实体类是，需要同步openapi 同名实体类
 */
public class ViewPurchaseDetail implements Serializable {
	private static final long serialVersionUID = -2879582703542221240L;

	private String productImg;// 商品图片
	private String productName;// 商品名称
	private String interBarCode;// 国际条码
	private String sku;// sku
	private Double purchasePrice;// 采购价
	private Double marketPrice;// 市场价
	private Integer qty;// 数量
	private Double weight;// 单个sku的重量
	private Integer purchaseId;// 采购单id
	private Integer warehouseId;// 仓库
	private String warehouseName;// 仓库名称
	private Double totalPrices;// 总价
	private Double afterDiscountPrice;// 商品折后价
	private Double disProfitMargin;// 商品分销利润率
	private Double discountProfitMargin;// 商品折后利润率
	private Double purchaseCostPrice;// 商品成本价
	private Double disProfit; // 分销毛利润
	private Double disStockFee; // 分销操作费
	private String disShippingType; // 分销物流方式
	private Double disOtherCost; // 分销其他费用
	private Double disTransferFee; // 分销转仓费
	private Double dislistFee;// 分销登录费
	private Double distradeFee;// 分销平台 交易费
	private Double dispayFee;// 分销支付费
	private Double dispostalFee;// 分销行邮税
	private Double disimportTar;// 分销进口关税
	private Double disgst;// 分销消费税
	private Double disinsurance;// 分销保险费
	private Double distotalvat;// 分销增值税
	private Double disCifPrice; // 分销cif价格
	private Double cost; // 裸采购价
	private Double disFreight;// 分销物流费
	private Integer disStockId;// 分销仓库id
	private Double disPrice;// 分销价
	private Integer categoryId;// 商品所属类目
	private Boolean isgift;// 是否为赠品，默认为false
	private Double realPrice;// 实际价格
	private Double capFee;// 均摊价格
	private String expirationDate;// 到期日期
	private Boolean isClearance;// 是否为清货商品
	/**
	 * 累计退款金额
	 */
	private Double totalReturnAmount;
	/**
	 * 累计退货个数
	 */
	private Integer totalReturnQties;

	public Double getTotalReturnAmount() {
		return totalReturnAmount;
	}

	public void setTotalReturnAmount(Double totalReturnAmount) {
		this.totalReturnAmount = totalReturnAmount;
	}

	public Integer getTotalReturnQties() {
		return totalReturnQties;
	}

	public void setTotalReturnQties(Integer totalReturnQties) {
		this.totalReturnQties = totalReturnQties;
	}

	public Boolean getIsClearance() {
		return isClearance;
	}

	public void setIsClearance(Boolean isClearance) {
		this.isClearance = isClearance;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Double getCapFee() {
		return capFee;
	}

	public void setCapFee(Double capFee) {
		this.capFee = capFee;
	}

	public Double getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(Double realPrice) {
		this.realPrice = realPrice;
	}

	public Double getDiscountProfitMargin() {
		return discountProfitMargin;
	}

	public void setDiscountProfitMargin(Double discountProfitMargin) {
		this.discountProfitMargin = discountProfitMargin;
	}

	public String getProductImg() {
		return productImg;
	}

	public void setProductImg(String productImg) {
		this.productImg = productImg;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
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

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Integer getPurchaseId() {
		return purchaseId;
	}

	public void setPurchaseId(Integer purchaseId) {
		this.purchaseId = purchaseId;
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
		switch (warehouseId) {
		case 1:
			this.warehouseName = Warehouse.广州仓.name();
			break;
		case 2:
			this.warehouseName = Warehouse.深圳仓.name();
			break;
		case 3:
			this.warehouseName = Warehouse.郑州仓.name();
			break;
		default:
			this.warehouseName = warehouseName;
		}
	}

	public Double getTotalPrices() {
		return totalPrices;
	}

	public void setTotalPrices(Double totalPrices) {
		this.totalPrices = totalPrices;
	}

	public Double getAfterDiscountPrice() {
		return afterDiscountPrice;
	}

	public void setAfterDiscountPrice(Double afterDiscountPrice) {
		this.afterDiscountPrice = afterDiscountPrice;
	}

	public Double getDisProfitMargin() {
		return disProfitMargin;
	}

	public void setDisProfitMargin(Double disProfitMargin) {
		this.disProfitMargin = disProfitMargin;
	}

	public Double getPurchaseCostPrice() {
		return purchaseCostPrice;
	}

	public void setPurchaseCostPrice(Double purchaseCostPrice) {
		this.purchaseCostPrice = purchaseCostPrice;
	}

	public Double getDisProfit() {
		return disProfit;
	}

	public void setDisProfit(Double disProfit) {
		this.disProfit = disProfit;
	}

	public Double getDisStockFee() {
		return disStockFee;
	}

	public void setDisStockFee(Double disStockFee) {
		this.disStockFee = disStockFee;
	}

	public String getDisShippingType() {
		return disShippingType;
	}

	public void setDisShippingType(String disShippingType) {
		this.disShippingType = disShippingType;
	}

	public Double getDisOtherCost() {
		return disOtherCost;
	}

	public void setDisOtherCost(Double disOtherCost) {
		this.disOtherCost = disOtherCost;
	}

	public Double getDisTransferFee() {
		return disTransferFee;
	}

	public void setDisTransferFee(Double disTransferFee) {
		this.disTransferFee = disTransferFee;
	}

	public Double getDislistFee() {
		return dislistFee;
	}

	public void setDislistFee(Double dislistFee) {
		this.dislistFee = dislistFee;
	}

	public Double getDistradeFee() {
		return distradeFee;
	}

	public void setDistradeFee(Double distradeFee) {
		this.distradeFee = distradeFee;
	}

	public Double getDispayFee() {
		return dispayFee;
	}

	public void setDispayFee(Double dispayFee) {
		this.dispayFee = dispayFee;
	}

	public Double getDispostalFee() {
		return dispostalFee;
	}

	public void setDispostalFee(Double dispostalFee) {
		this.dispostalFee = dispostalFee;
	}

	public Double getDisimportTar() {
		return disimportTar;
	}

	public void setDisimportTar(Double disimportTar) {
		this.disimportTar = disimportTar;
	}

	public Double getDisgst() {
		return disgst;
	}

	public void setDisgst(Double disgst) {
		this.disgst = disgst;
	}

	public Double getDisinsurance() {
		return disinsurance;
	}

	public void setDisinsurance(Double disinsurance) {
		this.disinsurance = disinsurance;
	}

	public Double getDistotalvat() {
		return distotalvat;
	}

	public void setDistotalvat(Double distotalvat) {
		this.distotalvat = distotalvat;
	}

	public Double getDisCifPrice() {
		return disCifPrice;
	}

	public void setDisCifPrice(Double disCifPrice) {
		this.disCifPrice = disCifPrice;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
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

	public Boolean getIsgift() {
		return isgift;
	}

	public void setIsgift(Boolean isgift) {
		this.isgift = isgift;
	}

	public String getInterBarCode() {
		return interBarCode;
	}

	public void setInterBarCode(String interBarCode) {
		this.interBarCode = interBarCode;
	}

	// 复写equals和hashCode方法，类目&sku&仓库&商品名称一样的，就说明是一样的商品
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoryId == null) ? 0 : categoryId.hashCode());
		result = prime * result + ((productName == null) ? 0 : productName.hashCode());
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		result = prime * result + ((warehouseId == null) ? 0 : warehouseId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViewPurchaseDetail other = (ViewPurchaseDetail) obj;
		if (categoryId == null) {
			if (other.categoryId != null)
				return false;
		} else if (!categoryId.equals(other.categoryId))
			return false;
		if (productName == null) {
			if (other.productName != null)
				return false;
		} else if (!productName.equals(other.productName))
			return false;
		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;
		if (warehouseId == null) {
			if (other.warehouseId != null)
				return false;
		} else if (!warehouseId.equals(other.warehouseId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ViewPurchaseDetail [productImg=" + productImg + ", productName=" + productName + ", interBarCode="
				+ interBarCode + ", sku=" + sku + ", purchasePrice=" + purchasePrice + ", marketPrice=" + marketPrice
				+ ", qty=" + qty + ", weight=" + weight + ", purchaseId=" + purchaseId + ", warehouseId=" + warehouseId
				+ ", warehouseName=" + warehouseName + ", totalPrices=" + totalPrices + ", afterDiscountPrice="
				+ afterDiscountPrice + ", disProfitMargin=" + disProfitMargin + ", discountProfitMargin="
				+ discountProfitMargin + ", purchaseCostPrice=" + purchaseCostPrice + ", disProfit=" + disProfit
				+ ", disStockFee=" + disStockFee + ", disShippingType=" + disShippingType + ", disOtherCost="
				+ disOtherCost + ", disTransferFee=" + disTransferFee + ", dislistFee=" + dislistFee + ", distradeFee="
				+ distradeFee + ", dispayFee=" + dispayFee + ", dispostalFee=" + dispostalFee + ", disimportTar="
				+ disimportTar + ", disgst=" + disgst + ", disinsurance=" + disinsurance + ", distotalvat="
				+ distotalvat + ", disCifPrice=" + disCifPrice + ", cost=" + cost + ", disFreight=" + disFreight
				+ ", disStockId=" + disStockId + ", disPrice=" + disPrice + ", categoryId=" + categoryId + ", isgift="
				+ isgift + ", realPrice=" + realPrice + ", capFee=" + capFee + ", expirationDate=" + expirationDate
				+ ", isClearance=" + isClearance + ", totalReturnAmount=" + totalReturnAmount + ", totalReturnQties="
				+ totalReturnQties + "]";
	}

}
