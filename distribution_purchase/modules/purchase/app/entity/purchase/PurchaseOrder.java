package entity.purchase;

import utils.purchase.PriceFormatUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购单实体
 * 
 * @author luwj
 *
 */
public class PurchaseOrder implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String email;
	private String purchaseOrderNo;// 采购单编号
	private String cartId;// 购物车编号
	private Integer status;// 采购单状态：0待付款，1已付款，2已取消
	private String paymentId;// 付款方式
	private String currency;// 结算货币
	private String transactionId;// 交易号
	private String payerIp;// 下单人ip
	private Date payDate;// 付款时间
	private String payHost;
	private String remarks;// 备注信息
	private Double purchaseTotalAmount;// 订单总金额
	private Date validablePayDate;// 支付时效点
	private Date purchaseDate;// 下单时间
	private String payerIdcard;// 支付人身份证号码
	private String payerName;// 支付人姓名
	private Integer purchaseType;// 采购类型（1：常规采购，2：缺货采购）
	private Integer sid;// 若当前采购订单是缺货采购，那么本属性应该对应缺货的发货单，否则本属性则为空
	private Double purchaseDiscountAmount;// 订单折后金额
	private Double discount;// 订单折扣
	private Double salesAmount;// 销售单缺货采购实付金额
	private Date cancelDate;// 取消时间
	private Double bbcPostage;// 分销平台运费
	private String logisticsMode;// 物流方式
	private String couponsCode;// 优惠码
	private Double couponsAmount;// 优惠码面额
	private Integer distributorType; // 分销商类型（1：普通 2：合营 3：内部）
	private Double deductionAmount;// 促销减免金额
	private Boolean isPro;// 是否存在活动
	private Boolean isChoose;// 是否已选优惠
	private String inputUser;// 录入人
	private Double offlineMoney;// 现金交易支付金额
	private Double financeMoney;// 财务实收金额
	private String remark;// 财务备注
	private String reason;// 审核理由
	private String customerService;// 客服账号
	private Double reducePrice;// 减价金额
	private Double tAWPrice; // 总的到仓价
	private String oaAuditNo;// oa审批单号，唯一的
	private String busenessRemarks;// 业务备注
	private Boolean backIn;// 后台录入标识
	private String nickName;// 分销商昵称
	// 是否由财务驳回，此字段只有在发货单财务审核才有用（只有审核不通过才为true，其余为false）
	private Boolean rejectedByFinance = false;
	private Boolean immediateDelivery;
	private Integer attributionType;//用户归属(1:线上,2:线下)
	
	public Integer getAttributionType() {
		return attributionType;
	}

	public void setAttributionType(Integer attributionType) {
		this.attributionType = attributionType;
	}

	public Boolean getImmediateDelivery() {
		return immediateDelivery;
	}

	public void setImmediateDelivery(Boolean immediateDelivery) {
		this.immediateDelivery = immediateDelivery;
	}

	public Boolean isRejectedByFinance() {
		return rejectedByFinance;
	}

	public void setRejectedByFinance(Boolean rejectedByFinance) {
		this.rejectedByFinance = rejectedByFinance;
	}

	public Boolean getBackIn() {
		return backIn;
	}

	public void setBackIn(Boolean backIn) {
		this.backIn = backIn;
	}

	public String getBusenessRemarks() {
		return busenessRemarks;
	}

	public void setBusenessRemarks(String busenessRemarks) {
		this.busenessRemarks = busenessRemarks;
	}

	public String getOaAuditNo() {
		return oaAuditNo;
	}

	public void setOaAuditNo(String oaAuditNo) {
		this.oaAuditNo = oaAuditNo;
	}

	/**
	 * 获取订单最终金额（订单金额+加运费-减优惠），保留两位小数
	 * 
	 * @param purchaseOrder
	 * @return
	 */
	public double calculatePurchaseFinalAmount() {
		// 订单金额
		BigDecimal finalAmount = new BigDecimal(purchaseTotalAmount);
		// 加运费
		if (bbcPostage != null) {
			finalAmount = finalAmount.add(new BigDecimal(bbcPostage));
		}
		// 减优惠
		if (couponsAmount != null) {
			finalAmount = finalAmount.subtract(new BigDecimal(couponsAmount));
		}

		double doubleValue = PriceFormatUtil.toFix2(finalAmount);
		if (doubleValue < 0) {
			return 0.00;
		}
		// 保留两位小数
		return doubleValue;
	}

	public Double gettAWPrice() {
		return tAWPrice;
	}

	public void settAWPrice(Double tAWPrice) {
		this.tAWPrice = tAWPrice;
	}

	public Double getReducePrice() {
		return reducePrice;
	}

	public void setReducePrice(Double reducePrice) {
		this.reducePrice = reducePrice;
	}

	public String getCustomerService() {
		return customerService;
	}

	public void setCustomerService(String customerService) {
		this.customerService = customerService;
	}

	public String getInputUser() {
		return inputUser;
	}

	public void setInputUser(String inputUser) {
		this.inputUser = inputUser;
	}

	public Double getDeductionAmount() {
		return deductionAmount;
	}

	public void setDeductionAmount(Double deductionAmount) {
		this.deductionAmount = deductionAmount;
	}

	public Integer getDistributorType() {
		return distributorType;
	}

	public void setDistributorType(Integer distributorType) {
		this.distributorType = distributorType;
	}

	public String getCouponsCode() {
		return couponsCode;
	}

	public void setCouponsCode(String couponsCode) {
		this.couponsCode = couponsCode;
	}

	public Double getCouponsAmount() {
		return couponsAmount;
	}

	public void setCouponsAmount(Double couponsAmount) {
		this.couponsAmount = couponsAmount;
	}

	public Double getBbcPostage() {
		return bbcPostage;
	}

	public void setBbcPostage(Double bbcPostage) {
		this.bbcPostage = bbcPostage;
	}

	public String getLogisticsMode() {
		return logisticsMode;
	}

	public void setLogisticsMode(String logisticsMode) {
		this.logisticsMode = logisticsMode;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public Double getSalesAmount() {
		return salesAmount;
	}

	public void setSalesAmount(Double salesAmount) {
		this.salesAmount = salesAmount;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public String getCartId() {
		return cartId;
	}

	public void setCartId(String cartId) {
		this.cartId = cartId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getPayerIp() {
		return payerIp;
	}

	public void setPayerIp(String payerIp) {
		this.payerIp = payerIp;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public String getPayHost() {
		return payHost;
	}

	public void setPayHost(String payHost) {
		this.payHost = payHost;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Double getPurchaseTotalAmount() {
		if (purchaseTotalAmount == null) {
			return purchaseTotalAmount;
		}
		return PriceFormatUtil.toFix2(purchaseTotalAmount);
	}

	public void setPurchaseTotalAmount(Double purchaseTotalAmount) {
		this.purchaseTotalAmount = purchaseTotalAmount;
	}

	public Date getValidablePayDate() {
		return validablePayDate;
	}

	public void setValidablePayDate(Date validablePayDate) {
		this.validablePayDate = validablePayDate;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getPayerIdcard() {
		return payerIdcard;
	}

	public void setPayerIdcard(String payerIdcard) {
		this.payerIdcard = payerIdcard;
	}

	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	public Integer getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(Integer purchaseType) {
		this.purchaseType = purchaseType;
	}

	public Double getPurchaseDiscountAmount() {
		return purchaseDiscountAmount;
	}

	public void setPurchaseDiscountAmount(Double purchaseDiscountAmount) {
		this.purchaseDiscountAmount = purchaseDiscountAmount;
	}

	public Boolean getIsPro() {
		return isPro;
	}

	public void setIsPro(Boolean isPro) {
		this.isPro = isPro;
	}

	public Boolean getIsChoose() {
		return isChoose;
	}

	public void setIsChoose(Boolean isChoose) {
		this.isChoose = isChoose;
	}

	public Double getOfflineMoney() {
		return offlineMoney;
	}

	public void setOfflineMoney(Double offlineMoney) {
		this.offlineMoney = offlineMoney;
	}

	public Double getFinanceMoney() {
		return financeMoney;
	}

	public void setFinanceMoney(Double financeMoney) {
		this.financeMoney = financeMoney;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Override
	public String toString() {
		return "PurchaseOrder [id=" + id + ", email=" + email + ", purchaseOrderNo=" + purchaseOrderNo + ", cartId="
				+ cartId + ", status=" + status + ", paymentId=" + paymentId + ", currency=" + currency
				+ ", transactionId=" + transactionId + ", payerIp=" + payerIp + ", payDate=" + payDate + ", payHost="
				+ payHost + ", remarks=" + remarks + ", purchaseTotalAmount=" + purchaseTotalAmount
				+ ", validablePayDate=" + validablePayDate + ", purchaseDate=" + purchaseDate + ", payerIdcard="
				+ payerIdcard + ", payerName=" + payerName + ", purchaseType=" + purchaseType + ", sid=" + sid
				+ ", purchaseDiscountAmount=" + purchaseDiscountAmount + ", discount=" + discount + ", salesAmount="
				+ salesAmount + ", cancelDate=" + cancelDate + ", bbcPostage=" + bbcPostage + ", logisticsMode="
				+ logisticsMode + ", couponsCode=" + couponsCode + ", couponsAmount=" + couponsAmount
				+ ", distributorType=" + distributorType + ", deductionAmount=" + deductionAmount + ", isPro=" + isPro
				+ ", isChoose=" + isChoose + ", inputUser=" + inputUser + ", offlineMoney=" + offlineMoney
				+ ", financeMoney=" + financeMoney + ", remark=" + remark + ", reason=" + reason + ", customerService="
				+ customerService + ", reducePrice=" + reducePrice + ", tAWPrice=" + tAWPrice + ", oaAuditNo="
				+ oaAuditNo + ", busenessRemarks=" + busenessRemarks + ", backIn=" + backIn + ", nickName=" + nickName
				+ ", rejectedByFinance=" + rejectedByFinance + "]";
	}

}