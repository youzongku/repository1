package entity.timer;

import java.io.Serializable;
import java.util.Date;

import services.base.utils.DateFormatUtils;
import util.timer.Constant;

public class SaleMain implements Serializable {
	private static final long serialVersionUID = 1992748395243065243L;

	private Integer id;

	private String salesOrderNo;

	private Date orderingDate;

	private String orderingDateStr;

	private String paymentType;

	private String payer;

	private String paymentNo;

	private String paryerIdcard;

	private Date payDate;

	private String payDateStr;

	private String currency;

	private Integer status;

	public static final int STATUS_FRONT = 1;
	public static final int STATUS_MANAGER = 2;
	private int frontOrManager = STATUS_FRONT;// 前台查询（1）还是后台查询（2），默认是前台查询
	private String statusDesc;

	// 是否已通知发货（0：未通知，1：已通知
	private Integer isNotified;

	// 订单产品是否经过库存调拨（0：否，1：是）
	private Integer isAdjusted;

	private String email;

	private String purchaseOrderNo;

	private String tradeNo;

	private Integer warehouseId;

	private String warehouseName;

	private Integer isPushed;

	private Integer distributorType;// 分销商类型（1：普通 2：合营 3：内部）

	private String distributorTypeStr;

	private String purchasePaymentNo;// 采购在线支付交易号

	private Date purchasePayDate;// 采购在线支付时间

	private String purchasePaymentType;// 采购在线支付类型

	private String purchasePayDateStr;

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

	private String compensationPayDateStr;

	private Boolean isFetched;// erp接受成功是否(erp 返回标识)

	private String erpReason;// erp同步失败原因

	private Date cinfirmReceiptDate;// 确认收货时间

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

	private String disModeDesc;

	private String nickName;

	private Integer createdFrom = 30;// 发货单从哪里创建的：1正常（前台，后台，整批出库）；2营销单

	private Double optFee = 0.00;// 操作费默认为0.00

	public String getDisModeDesc() {
		if (disMode != null) {
			switch (disMode) {
			case 1:
				return "电商";
			case 2:
				return "经销商";
			case 3:
				return "KA直营";
			case 4:
				return "进口专营";
			default:
				break;
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
		if (compensationPayDate != null) {
			return DateFormatUtils
					.getStrFromYYYYMMDDHHMMSS(compensationPayDate);
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

	public Date getCinfirmReceiptDate() {
		return cinfirmReceiptDate;
	}

	public void setCinfirmReceiptDate(Date cinfirmReceiptDate) {
		this.cinfirmReceiptDate = cinfirmReceiptDate;
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
		if (purchasePayDate != null) {
			return DateFormatUtils.getStrFromYYYYMMDDHHMMSS(purchasePayDate);
		}
		return purchasePayDateStr;
	}

	public String getOrderingDateStr() {
		return DateFormatUtils.getStrFromYYYYMMDDHHMMSS(orderingDate);
	}

	public void setOrderingDateStr(String orderingDateStr) {
		this.orderingDateStr = DateFormatUtils
				.getStrFromYYYYMMDDHHMMSS(orderingDate);
	}

	public String getPayDateStr() {
		if (payDate != null) {
			return DateFormatUtils.getStrFromYYYYMMDDHHMMSS(payDate);
		}
		return payDateStr;
	}

	public void setPayDateStr(String payDateStr) {
		if (payDate != null) {
			this.payDateStr = DateFormatUtils.getStrFromYYYYMMDDHHMMSS(payDate);
		}
		this.payDateStr = payDateStr;
	}

	/**
	 * 分销商类型 1、普通分销商 2、合营分销商 3、内部分销商
	 * 
	 * @author zbc
	 * @return
	 */
	public String getDistributorTypeStr() {
		if (distributorType != null) {
			switch (distributorType) {
			case 1:
				distributorTypeStr = "普通分销商";
				break;
			case 2:
				distributorTypeStr = "合营分销商";
				break;
			case 3:
				distributorTypeStr = "内部分销商";
				break;
			default:
				break;
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

	@Override
	public String toString() {
		return "SaleMain [id=" + id + ", salesOrderNo=" + salesOrderNo
				+ ", orderingDate=" + orderingDate + ", orderingDateStr="
				+ orderingDateStr + ", paymentType=" + paymentType + ", payer="
				+ payer + ", paymentNo=" + paymentNo + ", paryerIdcard="
				+ paryerIdcard + ", payDate=" + payDate + ", payDateStr="
				+ payDateStr + ", currency=" + currency + ", status=" + status
				+ ", frontOrManager=" + frontOrManager + ", statusDesc="
				+ statusDesc + ", isNotified=" + isNotified + ", isAdjusted="
				+ isAdjusted + ", email=" + email + ", purchaseOrderNo="
				+ purchaseOrderNo + ", tradeNo=" + tradeNo + ", warehouseId="
				+ warehouseId + ", warehouseName=" + warehouseName
				+ ", isPushed=" + isPushed + ", distributorType="
				+ distributorType + ", distributorTypeStr="
				+ distributorTypeStr + ", purchasePaymentNo="
				+ purchasePaymentNo + ", purchasePayDate=" + purchasePayDate
				+ ", purchasePaymentType=" + purchasePaymentType
				+ ", purchasePayDateStr=" + purchasePayDateStr
				+ ", compensationPaymentNo=" + compensationPaymentNo
				+ ", compensationPayDate=" + compensationPayDate
				+ ", compensationPaymentType=" + compensationPaymentType
				+ ", compensationAmount=" + compensationAmount
				+ ", disPrimeCost=" + disPrimeCost + ", gst=" + gst + ", vat="
				+ vat + ", importTar=" + importTar + ", postalFee=" + postalFee
				+ ", taxFee=" + taxFee + ", compensationPayDateStr="
				+ compensationPayDateStr + ", isFetched=" + isFetched
				+ ", erpReason=" + erpReason + ", cinfirmReceiptDate="
				+ cinfirmReceiptDate + ", platformAmount=" + platformAmount
				+ ", clearanceAmount=" + clearanceAmount + ", hostOrigin="
				+ hostOrigin + ", orderActualPayment=" + orderActualPayment
				+ ", source=" + source + ", host=" + host + ", disMode="
				+ disMode + ", isClose=" + isClose + ", disModeDesc="
				+ disModeDesc + ", nickName=" + nickName + ", createdFrom="
				+ createdFrom + ", optFee=" + optFee + "]";
	}

}