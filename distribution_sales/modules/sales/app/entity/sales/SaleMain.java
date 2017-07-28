package entity.sales;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wordnik.swagger.annotations.ApiModel;

import services.base.utils.DateFormatUtils;
import util.sales.Constant;
import util.sales.DateUtils;

@ApiModel
public class SaleMain implements Serializable {
	
	private static final long serialVersionUID = 1992748395243065243L;

	private Integer id;

	private String salesOrderNo;// 发货单号

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date orderingDate;// 下单时间

	private String paymentType;// 支付类型

	private String payer;

	private String paymentNo;// 支付流水号

	private String paryerIdcard;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date payDate;// 支付时间

	private String currency;

	private Integer status;// 状态

	public static final int STATUS_FRONT = 1;
	public static final int STATUS_MANAGER = 2;
	private int frontOrManager = STATUS_FRONT;// 前台查询（1）还是后台查询（2），默认是前台查询

	// 是否已通知发货（0：未通知，1：已通知
	private Integer isNotified;

	// 订单产品是否经过库存调拨（0：否，1：是）
	private Integer isAdjusted;

	private String email;

	private String purchaseOrderNo;// 采购单号（缺货采购）

	private String tradeNo;

	private Integer warehouseId;// 仓库

	private String warehouseName;

	private Integer isPushed;// 是否成功推送至ERP（默认0：未推送；1：已推送）

	private Integer distributorType;// 分销商类型（1：普通 2：合营 3：内部）

	private String purchasePaymentNo;// 采购在线支付交易号

	private Date purchasePayDate;// 采购在线支付时间

	private String purchasePaymentType;// 采购在线支付类型

	private String compensationPaymentNo;// 订单补差支付交易号

	private Date compensationPayDate;// 订单补差支付时间

	private String compensationPaymentType;// 订单补差支付类型

	private Double compensationAmount;// 订单补差支付金额

	private Double disPrimeCost; // 分销总成本（云仓扣除的商品数量*采购价+运费+其他费用）

	private Double gst; // 消费税
	private Double vat; // 增值税
	private Double importTar; // 关税
	private Double postalFee; // 行邮税
	private Double taxFee; // 税金

	private Boolean isFetched;// erp接受成功是否(erp 返回标识)

	private String erpReason;// erp同步失败原因

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date confirmReceiptDate;// 确认收货时间 
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date orderSendDate;//发货时间

	private Double platformAmount;// 平台收入，

	private Double clearanceAmount;// 报关金额
	/**
	 * M站推送过来数据，存在 以备以后用到 站点来源，1为分销商站点，2为邀请注册的站点
	 */
	private Integer hostOrigin;

	private Double orderActualPayment;// 订单实际付款金额（分销商支付的金额）

	private String source;// 来源（如BBC,MSITE）

	private String host;// 域名

	private Integer disMode;// 分销模式:1、电商，2、经销商，3、KA直营，4、进口专营

	private Boolean isClose;// 此订单是否已经关闭过

	private String nickName;

	private Integer createdFrom = 30;// 发货单从哪里创建的：1正常（前台，后台，整批出库）；2营销单

	private Double optFee = 0.00;// 操作费默认为0.00

	private Double arrviceTotal;// 到仓总计

	private Double totalCost;// 订单总成本

	private Double profit;// 订单利润

	private Double profitMargin;// 利润值

	private Double contractCharge;// 合同费用

	private Integer erpStatus;// ERP状态

	private Double clearancePriceTotal;// 清货价总计

	private String thirdPartLogisticsTypeCode;// 第三方物流代码

	/** 1: 电商, 2: 经销商, 3: KA直营, 4: 进口专营, 5: VIP */
	private static String[] disModeDescArray = { "", "电商", "经销商", "KA直营", "进口专营", "VIP" };
	/** distributorType： 分销商类型（1：普通分销商, 2：合营分销商, 3：内部分销商） */
	private static String[] distributorTypeStrArray = { "", "普通分销商", "合营分销商", "内部分销商" };

	private Double clearTotalCost;// 清货价总计

	private Double clearProfit;// 清货价利润

	private Double clearProfitMargin;// 清货价利润率

	// 店铺扣点，范围：0~1，下单时，当时的店铺扣点
	private Double shopDeductionPoints;
	// 店铺扣点金额（sdpAmount = orderActualAmount * shopDeductionPoints）
	private Double sdpAmount;

	private Integer orderchannel;// 订单渠道：默认为1， 4进口虚拟仓

	private Date csConfirmDate;// 客服确认时间（多次确认，保存最新的）

	// 是否由财务驳回，此字段只有在发货单财务审核才有用（只有审核不通过才为true，其余为false）
	private boolean rejectedByFinance = false;

	/**
	 * 订单预估平摊费用
	 */
	private Double estimatedCatfeeInOrder;
	/**
	 * 订单实际平摊费用
	 */
	private Double realCatfeeInOrder;
	
	/**
	 * 是否合并发货标识:true 已合并  false 未合并
	 */
	private Boolean isCombine;

	/**
	 * 合并发货单主单号
	 */
	private String  combineOrderNo;
	
	/**
	 * 合并发货单数量
	 */
	private Integer combineOrderCount;
	
	private String erpOrderNo;//订单在erp的单号
	
	/**
	 * 用户归属
	 */
	private Integer attributionType;
	
	/**
	 * 是否需要开发票(true:需要，false：不需要)
	 */
	private Boolean isNeedInvoice;

	public Date getConfirmReceiptDate() {
		return confirmReceiptDate;
	}

	public void setConfirmReceiptDate(Date confirmReceiptDate) {
		this.confirmReceiptDate = confirmReceiptDate;
	}

	public Date getOrderSendDate() {
		return orderSendDate;
	}

	public void setOrderSendDate(Date orderSendDate) {
		this.orderSendDate = orderSendDate;
	}

	public Boolean getIsNeedInvoice() {
		return isNeedInvoice;
	}

	public void setIsNeedInvoice(Boolean isNeedInvoice) {
		this.isNeedInvoice = isNeedInvoice;
	}

	public Integer getAttributionType() {
		return attributionType;
	}

	public void setAttributionType(Integer attributionType) {
		this.attributionType = attributionType;
	}

	/**
	 * 是否是虚拟仓发货单
	 * @return true虚拟仓发货单
	 */
	public boolean isVirtualHouseOrder() {
		if (orderchannel == null || orderchannel != 4) {
			return false;
		}
		return true;
	}
	
	public String getErpOrderNo() {
		return erpOrderNo;
	}
	public void setErpOrderNo(String erpOrderNo) {
		this.erpOrderNo = erpOrderNo;
	}

	public Integer getCombineOrderCount() {
		return combineOrderCount;
	}

	public void setCombineOrderCount(Integer combineOrderCount) {
		this.combineOrderCount = combineOrderCount;
	}

	public Boolean getIsCombine() {
		return isCombine;
	}

	public void setIsCombine(Boolean isCombine) {
		this.isCombine = isCombine;
	}

	public String getCombineOrderNo() {
		return combineOrderNo;
	}

	public void setCombineOrderNo(String combineOrderNo) {
		this.combineOrderNo = combineOrderNo;
	}

	private Short isPackageMail;// 是否包邮

	public Double getEstimatedCatfeeInOrder() {
		return estimatedCatfeeInOrder;
	}

	public void setEstimatedCatfeeInOrder(Double estimatedCatfeeInOrder) {
		this.estimatedCatfeeInOrder = estimatedCatfeeInOrder;
	}

	public Double getRealCatfeeInOrder() {
		return realCatfeeInOrder;
	}

	public void setRealCatfeeInOrder(Double realCatfeeInOrder) {
		this.realCatfeeInOrder = realCatfeeInOrder;
	}

	public boolean isRejectedByFinance() {
		return rejectedByFinance;
	}

	public void setRejectedByFinance(boolean rejectedByFinance) {
		this.rejectedByFinance = rejectedByFinance;
	}

	public Date getCsConfirmDate() {
		return csConfirmDate;
	}

	public String getCsConfirmDateStr() {
		if (csConfirmDate != null) {
			return DateUtils.date2string(csConfirmDate, DateUtils.FORMAT_FULL_DATETIME);
		}
		return null;
	}

	public void setCsConfirmDate(Date csConfirmDate) {
		this.csConfirmDate = csConfirmDate;
	}

	public Double getClearTotalCost() {
		return clearTotalCost;
	}

	public void setClearTotalCost(Double clearTotalCost) {
		this.clearTotalCost = clearTotalCost;
	}

	public Double getClearProfit() {
		return clearProfit;
	}

	public void setClearProfit(Double clearProfit) {
		this.clearProfit = clearProfit;
	}

	public Double getClearProfitMargin() {
		return clearProfitMargin;
	}

	public void setClearProfitMargin(Double clearProfitMargin) {
		this.clearProfitMargin = clearProfitMargin;
	}

	public Double getClearancePriceTotal() {
		return clearancePriceTotal;
	}

	public void setClearancePriceTotal(Double clearancePriceTotal) {
		this.clearancePriceTotal = clearancePriceTotal;
	}

	public Double getArrviceTotal() {
		return arrviceTotal;
	}

	public void setArrviceTotal(Double arrviceTotal) {
		this.arrviceTotal = arrviceTotal;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Double getProfit() {
		return profit;
	}

	public void setProfit(Double profit) {
		this.profit = profit;
	}

	public Double getProfitMargin() {
		return profitMargin;
	}

	public void setProfitMargin(Double profitMargin) {
		this.profitMargin = profitMargin;
	}

	public Double getContractCharge() {
		return contractCharge;
	}

	public void setContractCharge(Double contractCharge) {
		this.contractCharge = contractCharge;
	}

	public Integer getOrderchannel() {
		return orderchannel;
	}

	public void setOrderchannel(Integer orderchannel) {
		this.orderchannel = orderchannel;
	}

	public String getDisModeDesc() {
		String disModeDesc = "";
		if (disMode != null) {
			int index = disMode;
			if (index < disModeDescArray.length) {
				disModeDesc = disModeDescArray[index];
			}
		}
		return disModeDesc;
	}

	public Double getOptFee() {
		return optFee;
	}

	public void setOptFee(Double optFee) {
		this.optFee = optFee;
	}

	public Integer getCreatedFrom() {
		return createdFrom;
	}

	public void setCreatedFrom(Integer createdFrom) {
		this.createdFrom = createdFrom;
	}

	/**
	 * 是否有采购单
	 * 
	 * @return
	 */
	public boolean hasPurchaseOrder() {
		return (purchaseOrderNo != null && purchaseOrderNo.trim().length() > 0);
	}

	public void setFrontOrManager(int frontOrManager) {
		this.frontOrManager = frontOrManager;
	}

	public Double getGst() {
		return gst;
	}

	public void setGst(Double gst) {
		this.gst = gst;
	}

	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}

	public Double getImportTar() {
		return importTar;
	}

	public void setImportTar(Double importTar) {
		this.importTar = importTar;
	}

	public Double getPostalFee() {
		return postalFee;
	}

	public void setPostalFee(Double postalFee) {
		this.postalFee = postalFee;
	}

	public Double getTaxFee() {
		return taxFee;
	}

	public void setTaxFee(Double taxFee) {
		this.taxFee = taxFee;
	}

	public Integer getDisMode() {
		return disMode;
	}

	public void setDisMode(Integer disMode) {
		this.disMode = disMode;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Double getDisPrimeCost() {
		return disPrimeCost;
	}

	public void setDisPrimeCost(Double disPrimeCost) {
		this.disPrimeCost = disPrimeCost;
	}

	public Double getCompensationAmount() {
		return compensationAmount;
	}

	public void setCompensationAmount(Double compensationAmount) {
		this.compensationAmount = compensationAmount;
	}

	public String getCompensationPaymentNo() {
		return compensationPaymentNo;
	}

	public void setCompensationPaymentNo(String compensationPaymentNo) {
		this.compensationPaymentNo = compensationPaymentNo;
	}

	public Date getCompensationPayDate() {
		return compensationPayDate;
	}

	public void setCompensationPayDate(Date compensationPayDate) {
		this.compensationPayDate = compensationPayDate;
	}

	public String getCompensationPaymentType() {
		return compensationPaymentType;
	}

	public void setCompensationPaymentType(String compensationPaymentType) {
		this.compensationPaymentType = compensationPaymentType;
	}

	public String getCompensationPayDateStr() {
		String compensationPayDateStr = "";
		if (compensationPayDate != null) {
			compensationPayDateStr = DateFormatUtils.getStrFromYYYYMMDDHHMMSS(compensationPayDate);
		}
		return compensationPayDateStr;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Double getOrderActualPayment() {
		return orderActualPayment;
	}

	public void setOrderActualPayment(Double orderActualPayment) {
		this.orderActualPayment = orderActualPayment;
	}

	public Integer getHostOrigin() {
		return hostOrigin;
	}

	public void setHostOrigin(Integer hostOrigin) {
		this.hostOrigin = hostOrigin;
	}

	public Double getPlatformAmount() {
		return platformAmount;
	}

	public void setPlatformAmount(Double platformAmount) {
		this.platformAmount = platformAmount;
	}

	public Double getClearanceAmount() {
		return clearanceAmount;
	}

	public void setClearanceAmount(Double clearanceAmount) {
		this.clearanceAmount = clearanceAmount;
	}


	public Boolean getIsFetched() {
		return isFetched;
	}

	public void setIsFetched(Boolean isFetched) {
		this.isFetched = isFetched;
	}

	public String getErpReason() {
		return erpReason;
	}

	public void setErpReason(String erpReason) {
		this.erpReason = erpReason;
	}

	public String getPurchasePaymentNo() {
		return purchasePaymentNo;
	}

	public void setPurchasePaymentNo(String purchasePaymentNo) {
		this.purchasePaymentNo = purchasePaymentNo;
	}

	public Date getPurchasePayDate() {
		return purchasePayDate;
	}

	public void setPurchasePayDate(Date purchasePayDate) {
		this.purchasePayDate = purchasePayDate;
	}

	public String getPurchasePaymentType() {
		return purchasePaymentType;
	}

	public void setPurchasePaymentType(String purchasePaymentType) {
		this.purchasePaymentType = purchasePaymentType;
	}

	public Integer getDistributorType() {
		return distributorType;
	}

	public void setDistributorType(Integer distributorType) {
		this.distributorType = distributorType;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSalesOrderNo() {
		return salesOrderNo;
	}

	public void setSalesOrderNo(String salesOrderNo) {
		this.salesOrderNo = salesOrderNo;
	}

	public Date getOrderingDate() {
		return orderingDate;
	}

	public void setOrderingDate(Date orderingDate) {
		this.orderingDate = orderingDate;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	public String getParyerIdcard() {
		return paryerIdcard;
	}

	public void setParyerIdcard(String paryerIdcard) {
		this.paryerIdcard = paryerIdcard;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getStatus() {
		return status;
	}

	/**
	 * 订单状态标识： 1 ---> 待采购， 2 ---> 待通知发货， 3 ---> 待审核， 4 ---> 审核不通过， 5 ---> 已取消， 6
	 * ---> 审核通过， 7 ---> 待发货， 8 ---> 发货失败， 9 ---> 待收货， 10 ---> 已收货
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getIsNotified() {
		return isNotified;
	}

	public void setIsNotified(Integer isNotified) {
		this.isNotified = isNotified;
	}

	public Integer getIsAdjusted() {
		return isAdjusted;
	}

	public void setIsAdjusted(Integer isAdjusted) {
		this.isAdjusted = isAdjusted;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPurchasePayDateStr() {
		String purchasePayDateStr = "";
		if (purchasePayDate != null) {
			purchasePayDateStr = DateFormatUtils.getStrFromYYYYMMDDHHMMSS(purchasePayDate);
		}
		return purchasePayDateStr;
	}

	/**
	 * 分销商类型 1、普通分销商 2、合营分销商 3、内部分销商
	 * 
	 * @author zbc
	 * @return
	 */
	public String getDistributorTypeStr() {
		String distributorTypeStr = "";
		if (distributorType != null) {
			int index = distributorType;
			if (index <= distributorTypeStrArray.length) {
				distributorTypeStr = distributorTypeStrArray[index];
			}
		}
		return distributorTypeStr;
	}

	/**
	 * 状态描述，1：待采购，2：待通知发货，3：待审核，4：审核不通过，5：已取消，6：审核通过
	 * 
	 * @return
	 * @author ye_ziran
	 * @since 2016年1月8日 下午3:09:41
	 */
	public String getStatusDesc() {
		String statusDesc = "";
		if (frontOrManager == STATUS_FRONT) {
			statusDesc = Constant.SALES_ORDER_STATE_FRONT.get(this.status);
		} else if (frontOrManager == STATUS_MANAGER) {
			statusDesc = Constant.SALES_ORDER_STATE_MANAGER.get(this.status);
		}
		return statusDesc;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
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

	public Integer getIsPushed() {
		return isPushed;
	}

	public void setIsPushed(Integer isPushed) {
		this.isPushed = isPushed;
	}

	public Boolean getIsClose() {
		return isClose;
	}

	public void setIsClose(Boolean isClose) {
		this.isClose = isClose;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getErpStatus() {
		return erpStatus;
	}

	public void setErpStatus(Integer erpStatus) {
		this.erpStatus = erpStatus;
	}

	public String getThirdPartLogisticsTypeCode() {
		return thirdPartLogisticsTypeCode;
	}

	public void setThirdPartLogisticsTypeCode(String thirdPartLogisticsTypeCode) {
		this.thirdPartLogisticsTypeCode = thirdPartLogisticsTypeCode;
	}

	public Double getSdpAmount() {
		return sdpAmount;
	}

	public void setSdpAmount(Double sdpAmount) {
		this.sdpAmount = sdpAmount;
	}

	public Double getShopDeductionPoints() {
		return shopDeductionPoints;
	}

	public void setShopDeductionPoints(Double shopDeductionPoints) {
		this.shopDeductionPoints = shopDeductionPoints;
	}

	public Short getIsPackageMail() {
		return isPackageMail;
	}

	public void setIsPackageMail(Short isPackageMail) {
		this.isPackageMail = isPackageMail;
	}

	@Override
	public String toString() {
		return "SaleMain [id=" + id + ", salesOrderNo=" + salesOrderNo + ", orderingDate=" + orderingDate
				+ ", paymentType=" + paymentType + ", payer=" + payer + ", paymentNo=" + paymentNo + ", paryerIdcard="
				+ paryerIdcard + ", payDate=" + payDate + ", currency=" + currency + ", status=" + status
				+ ", frontOrManager=" + frontOrManager + ", isNotified=" + isNotified + ", isAdjusted=" + isAdjusted
				+ ", email=" + email + ", purchaseOrderNo=" + purchaseOrderNo + ", tradeNo=" + tradeNo
				+ ", warehouseId=" + warehouseId + ", warehouseName=" + warehouseName + ", isPushed=" + isPushed
				+ ", distributorType=" + distributorType + ", purchasePaymentNo=" + purchasePaymentNo
				+ ", purchasePayDate=" + purchasePayDate + ", purchasePaymentType=" + purchasePaymentType
				+ ", compensationPaymentNo=" + compensationPaymentNo + ", compensationPayDate=" + compensationPayDate
				+ ", compensationPaymentType=" + compensationPaymentType + ", compensationAmount=" + compensationAmount
				+ ", disPrimeCost=" + disPrimeCost + ", gst=" + gst + ", vat=" + vat + ", importTar=" + importTar
				+ ", postalFee=" + postalFee + ", taxFee=" + taxFee + ", isFetched=" + isFetched + ", erpReason="
				+ erpReason + ", confirmReceiptDate=" + confirmReceiptDate + ", platformAmount=" + platformAmount
				+ ", clearanceAmount=" + clearanceAmount + ", hostOrigin=" + hostOrigin + ", orderActualPayment="
				+ orderActualPayment + ", source=" + source + ", host=" + host + ", disMode=" + disMode + ", isClose="
				+ isClose + ", nickName=" + nickName + ", createdFrom=" + createdFrom + ", optFee=" + optFee
				+ ", arrviceTotal=" + arrviceTotal + ", totalCost=" + totalCost + ", profit=" + profit
				+ ", profitMargin=" + profitMargin + ", contractCharge=" + contractCharge + ", erpStatus=" + erpStatus
				+ ", clearancePriceTotal=" + clearancePriceTotal + ", thirdPartLogisticsTypeCode="
				+ thirdPartLogisticsTypeCode + ", clearTotalCost=" + clearTotalCost + ", clearProfit=" + clearProfit
				+ ", clearProfitMargin=" + clearProfitMargin + ", shopDeductionPoints=" + shopDeductionPoints
				+ ", sdpAmount=" + sdpAmount + ", orderchannel=" + orderchannel + ", csConfirmDate=" + csConfirmDate
				+ ", rejectedByFinance=" + rejectedByFinance + ", estimatedCatfeeInOrder=" + estimatedCatfeeInOrder
				+ ", realCatfeeInOrder=" + realCatfeeInOrder + ", isPackageMail=" + isPackageMail + "]";
	}

}