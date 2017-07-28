package entity.warehousing;

import java.util.Date;

/**
 * 仓库变更记录实体
 * 
 * @author ouyangyaxiong
 *
 */
public class InventoryChangeHistory implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;

	private Integer warehouseId;// 仓库编码

	private String warehouseName;// 仓库名

	private String sku;

	private String productName;

	private Integer num;// 变更数量

	private Integer type;// 变更类型 1：入；0：出

	private Date changeTime; // 变更时间

	private String operator; // 变更操作者

	private boolean isEffective;// 是否生效

	private String orderNo;// 订单编号

	private Integer orderType;// 记录变更类型（比如采购入库，还原入库等等，对应不同的数字值）

	private String disMemberEmail;// 分销商邮箱

	private Integer mwarehouseId;// 微仓id

	private String mwarehouseName;// 微仓名称

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
	
	private Double capFee;//均摊价格
	
	private Double arriveWarePrice;// 到仓价
	
	private Boolean isgift;//是否为赠品

	public Double getArriveWarePrice() {
		return arriveWarePrice;
	}

	public void setArriveWarePrice(Double arriveWarePrice) {
		this.arriveWarePrice = arriveWarePrice;
	}

	public Double getCapFee() {
		return capFee;
	}

	public void setCapFee(Double capFee) {
		this.capFee = capFee;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public boolean isEffective() {
		return isEffective;
	}

	public void setEffective(boolean isEffective) {
		this.isEffective = isEffective;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getDisMemberEmail() {
		return disMemberEmail;
	}

	public void setDisMemberEmail(String disMemberEmail) {
		this.disMemberEmail = disMemberEmail;
	}

	public Integer getMwarehouseId() {
		return mwarehouseId;
	}

	public void setMwarehouseId(Integer mwarehouseId) {
		this.mwarehouseId = mwarehouseId;
	}

	public String getMwarehouseName() {
		return mwarehouseName;
	}

	public void setMwarehouseName(String mwarehouseName) {
		this.mwarehouseName = mwarehouseName;
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

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	
	public Boolean getIsgift() {
		return isgift;
	}

	public void setIsgift(Boolean isgift) {
		this.isgift = isgift;
	}

	@Override
	public String toString() {
		return "InventoryChangeHistory [id=" + id + ", warehouseId="
				+ warehouseId + ", warehouseName=" + warehouseName + ", sku="
				+ sku + ", productName=" + productName + ", num=" + num
				+ ", type=" + type + ", changeTime=" + changeTime
				+ ", operator=" + operator + ", isEffective=" + isEffective
				+ ", orderNo=" + orderNo + ", orderType=" + orderType
				+ ", disMemberEmail=" + disMemberEmail + ", mwarehouseId="
				+ mwarehouseId + ", mwarehouseName=" + mwarehouseName
				+ ", disProfitRate=" + disProfitRate + ", disProfit="
				+ disProfit + ", disVat=" + disVat + ", disStockFee="
				+ disStockFee + ", disShippingType=" + disShippingType
				+ ", disOtherCost=" + disOtherCost + ", disTotalCost="
				+ disTotalCost + ", disTransferFee=" + disTransferFee
				+ ", disListFee=" + disListFee + ", disTradeFee=" + disTradeFee
				+ ", disPayFee=" + disPayFee + ", disPostalFee=" + disPostalFee
				+ ", disImportTar=" + disImportTar + ", disGst=" + disGst
				+ ", disInsurance=" + disInsurance + ", disTotalVat="
				+ disTotalVat + ", cost=" + cost + ", disFreight=" + disFreight
				+ ", disPrice=" + disPrice + ", disCifPrice=" + disCifPrice
				+ ", purchasePrice=" + purchasePrice + ", capFee=" + capFee + "]";
	}

}
