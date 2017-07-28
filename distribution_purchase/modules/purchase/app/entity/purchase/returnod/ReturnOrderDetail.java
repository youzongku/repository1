package entity.purchase.returnod;

import java.math.BigDecimal;

import utils.purchase.PriceFormatUtil;

/**
 * 采购退货单明细
 * 
 * @author huangjc
 * @date 2017年2月13日
 */
public class ReturnOrderDetail {
	private Integer id;
	private Integer returnOrderId;// 退货单id
	private String returnOrderNo;// 退货单号
	private String purchaseOrderNo;// 所属采购单单号
	private String productTitle;// 商品名称
	private String imgUrl;
	private String sku;
	private Integer warehouseId;
	private String warehouseName;
	private Double purchasePrice;
	private Double capfee;
	private String purchaseTime;
	private String expirationDate;
	private Integer returnQty;// 退货数量
	private Integer qty;// 采购数量
	private Integer residueNum;// 剩余数量
	private Double subTotalReturnAmount;// 商品的退款金额小计
	private Integer inRecordId;
	private Boolean isGift;// 是否是赠品（true赠品；false正价商品）

	private Integer daySpace;// 距离到期日期天数
	private Double coefficient;// 退款系数

	/**
	 * 均摊价小计
	 * 
	 * @return
	 */
	public Double getCapfeeSubtotal() {
		if (capfee == null || returnQty == null || returnQty == 0) {
			return 0.00;
		}

		BigDecimal capfeeSubtotal = new BigDecimal(capfee).multiply(new BigDecimal(returnQty));
		return PriceFormatUtil.toFix2(capfeeSubtotal);
	}

	/**
	 * 采购价小计
	 * 
	 * @return
	 */
	public Double getPurchasePriceSubtotal() {
		if (purchasePrice == null || returnQty == null || returnQty == 0) {
			return 0.00;
		}

		BigDecimal purchasePriceSubtotal = new BigDecimal(purchasePrice).multiply(new BigDecimal(returnQty));
		return PriceFormatUtil.toFix2(purchasePriceSubtotal);
	}

	public Integer getDaySpace() {
		return daySpace;
	}

	public void setDaySpace(Integer daySpace) {
		this.daySpace = daySpace;
	}

	public Double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getReturnOrderId() {
		return returnOrderId;
	}

	public void setReturnOrderId(Integer returnOrderId) {
		this.returnOrderId = returnOrderId;
	}

	public String getReturnOrderNo() {
		return returnOrderNo;
	}

	public void setReturnOrderNo(String returnOrderNo) {
		this.returnOrderNo = returnOrderNo;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
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

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
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

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public Double getCapfee() {
		return capfee;
	}

	public void setCapfee(Double capfee) {
		this.capfee = capfee;
	}

	public String getPurchaseTime() {
		return purchaseTime;
	}

	public void setPurchaseTime(String purchaseTime) {
		this.purchaseTime = purchaseTime;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Integer getReturnQty() {
		return returnQty;
	}

	public void setReturnQty(Integer returnQty) {
		this.returnQty = returnQty;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Integer getResidueNum() {
		return residueNum;
	}

	public void setResidueNum(Integer residueNum) {
		this.residueNum = residueNum;
	}

	public Double getSubTotalReturnAmount() {
		// 小计金额为空，说明没有coefficient，导致calculateSubTotalReturnAmount方法没有被执行
		if (subTotalReturnAmount == null) {
			BigDecimal subTotal = new BigDecimal(capfee).multiply(new BigDecimal(returnQty));
			if (coefficient != null) {
				subTotal = subTotal.multiply(new BigDecimal(coefficient));
			}
			subTotalReturnAmount = PriceFormatUtil.toFix2(subTotal);
		}
		return subTotalReturnAmount;
	}

	public void setSubTotalReturnAmount(Double subTotalReturnAmount) {
		this.subTotalReturnAmount = subTotalReturnAmount;
	}

	public Integer getInRecordId() {
		return inRecordId;
	}

	public void setInRecordId(Integer inRecordId) {
		this.inRecordId = inRecordId;
	}

	public Boolean getIsGift() {
		return isGift;
	}

	public void setIsGift(Boolean isGift) {
		this.isGift = isGift;
	}

	/**
	 * 计算此sku的退款金额
	 * 
	 * @param coefficient
	 *            退款系数，可以为null，当为null时，就是全额退款，范围：[0~1]
	 * @return
	 */
	public Double calculateSubTotalReturnAmount(Double coefficient) {
		BigDecimal subTotal = new BigDecimal(capfee).multiply(new BigDecimal(returnQty));
		if (coefficient != null) {
			subTotal = subTotal.multiply(new BigDecimal(coefficient));
		}
		this.subTotalReturnAmount = PriceFormatUtil.toFix2(subTotal);

		return this.subTotalReturnAmount;
	}

	@Override
	public String toString() {
		return "ReturnOrderDetail [id=" + id + ", returnOrderId=" + returnOrderId + ", returnOrderNo=" + returnOrderNo
				+ ", purchaseOrderNo=" + purchaseOrderNo + ", productTitle=" + productTitle + ", imgUrl=" + imgUrl
				+ ", sku=" + sku + ", warehouseId=" + warehouseId + ", warehouseName=" + warehouseName
				+ ", purchasePrice=" + purchasePrice + ", capfee=" + capfee + ", purchaseTime=" + purchaseTime
				+ ", expirationDate=" + expirationDate + ", returnQty=" + returnQty + ", qty=" + qty + ", residueNum="
				+ residueNum + ", subTotalReturnAmount=" + subTotalReturnAmount + ", inRecordId=" + inRecordId
				+ ", isGift=" + isGift + ", daySpace=" + daySpace + ", coefficient=" + coefficient + "]";
	}

}