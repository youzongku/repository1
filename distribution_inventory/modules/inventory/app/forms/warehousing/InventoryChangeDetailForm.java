package forms.warehousing;

import java.io.Serializable;

/**
 * 
 * 
 * @author ye_ziran
 * @since 2016年3月3日 下午3:48:52
 */
public class InventoryChangeDetailForm implements Serializable{
	private static final long serialVersionUID = -1579934146807398836L;
	private String sku;
	private String productTitle;// 产品标题
	private Integer productCategoryId;// 产品类目id，只记录到大类
	private String productCategoryName;// 类目名称
	private Double costprice;// 成本价
	private Integer warehouseId;
	private String warehouseNo;
	private String warehouseName;
	private Integer num;// 变更数量

	/********************** 为获取出入库的采购价格，记录出入库明细的分销价格体系 **********************/

	private Double disProfitRate;// 分销利润率

	private Double disProfit;// 分销毛利润

	private Double disVat;// 分销增值税

	private Double disStockFee;// 分销操作费

	private String disShippingType;// 分销物流方式

	private Double disOtherCost;// 分销其他费用

	private Double disTotalCost;// 分销总成本

	private Double disTransferFee;// 分销转仓费

	private Double disListFee;// 分销登录费

	private Double disTradeFee;// 分销平台 交易费

	private Double disPayFee;// 分销支付费

	private Double disPostalFee;// 分销行邮税

	private Double disImportTar;// 分销进口关税

	private Double disGst;// 分销消费税

	private Double disInsurance;// 分销保险费

	private Double disTotalVat;// 分销增值税

	private Double cost;// 裸采购价

	private Double disFreight;// 分销物流费

	private Double disPrice;// 分销价

	private Double disCifPrice;// CIF价格

	private Double purchasePrice;// 商品采购价格

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

	public Integer getProductCategoryId() {
		return productCategoryId;
	}

	public void setProductCategoryId(Integer productCategoryId) {
		this.productCategoryId = productCategoryId;
	}

	public String getProductCategoryName() {
		return productCategoryName;
	}

	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseNo() {
		return warehouseNo;
	}

	public void setWarehouseNo(String warehouseNo) {
		this.warehouseNo = warehouseNo;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Double getCostprice() {
		return costprice;
	}

	public void setCostprice(Double costprice) {
		this.costprice = costprice;
	}

	public Double getDisProfitRate() {
		return disProfitRate;
	}

	public void setDisProfitRate(Double disProfitRate) {
		this.disProfitRate = disProfitRate;
	}

	public Double getDisProfit() {
		return disProfit;
	}

	public void setDisProfit(Double disProfit) {
		this.disProfit = disProfit;
	}

	public Double getDisVat() {
		return disVat;
	}

	public void setDisVat(Double disVat) {
		this.disVat = disVat;
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

	public Double getDisTotalCost() {
		return disTotalCost;
	}

	public void setDisTotalCost(Double disTotalCost) {
		this.disTotalCost = disTotalCost;
	}

	public Double getDisTransferFee() {
		return disTransferFee;
	}

	public void setDisTransferFee(Double disTransferFee) {
		this.disTransferFee = disTransferFee;
	}

	public Double getDisListFee() {
		return disListFee;
	}

	public void setDisListFee(Double disListFee) {
		this.disListFee = disListFee;
	}

	public Double getDisTradeFee() {
		return disTradeFee;
	}

	public void setDisTradeFee(Double disTradeFee) {
		this.disTradeFee = disTradeFee;
	}

	public Double getDisPayFee() {
		return disPayFee;
	}

	public void setDisPayFee(Double disPayFee) {
		this.disPayFee = disPayFee;
	}

	public Double getDisPostalFee() {
		return disPostalFee;
	}

	public void setDisPostalFee(Double disPostalFee) {
		this.disPostalFee = disPostalFee;
	}

	public Double getDisImportTar() {
		return disImportTar;
	}

	public void setDisImportTar(Double disImportTar) {
		this.disImportTar = disImportTar;
	}

	public Double getDisGst() {
		return disGst;
	}

	public void setDisGst(Double disGst) {
		this.disGst = disGst;
	}

	public Double getDisInsurance() {
		return disInsurance;
	}

	public void setDisInsurance(Double disInsurance) {
		this.disInsurance = disInsurance;
	}

	public Double getDisTotalVat() {
		return disTotalVat;
	}

	public void setDisTotalVat(Double disTotalVat) {
		this.disTotalVat = disTotalVat;
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

	public Double getDisPrice() {
		return disPrice;
	}

	public void setDisPrice(Double disPrice) {
		this.disPrice = disPrice;
	}

	public Double getDisCifPrice() {
		return disCifPrice;
	}

	public void setDisCifPrice(Double disCifPrice) {
		this.disCifPrice = disCifPrice;
	}

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	@Override
	public String toString() {
		return "InventoryChangeDetailForm [sku=" + sku + ", productTitle="
				+ productTitle + ", productCategoryId=" + productCategoryId
				+ ", productCategoryName=" + productCategoryName
				+ ", costprice=" + costprice + ", warehouseId=" + warehouseId
				+ ", warehouseNo=" + warehouseNo + ", warehouseName="
				+ warehouseName + ", num=" + num + ", disProfitRate="
				+ disProfitRate + ", disProfit=" + disProfit + ", disVat="
				+ disVat + ", disStockFee=" + disStockFee
				+ ", disShippingType=" + disShippingType + ", disOtherCost="
				+ disOtherCost + ", disTotalCost=" + disTotalCost
				+ ", disTransferFee=" + disTransferFee + ", disListFee="
				+ disListFee + ", disTradeFee=" + disTradeFee + ", disPayFee="
				+ disPayFee + ", disPostalFee=" + disPostalFee
				+ ", disImportTar=" + disImportTar + ", disGst=" + disGst
				+ ", disInsurance=" + disInsurance + ", disTotalVat="
				+ disTotalVat + ", cost=" + cost + ", disFreight=" + disFreight
				+ ", disPrice=" + disPrice + ", disCifPrice=" + disCifPrice
				+ ", purchasePrice=" + purchasePrice + "]";
	}

}
