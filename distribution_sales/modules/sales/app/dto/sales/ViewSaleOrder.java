package dto.sales;

import java.util.Date;
import java.util.List;

import services.base.utils.DateFormatUtils;

/**
 * 对外 客户订单展示
 * 
 * @author zbc 2016年9月6日 上午10:16:57
 */
public class ViewSaleOrder {
	private Integer id;

	private String salesOrderNo;

	private Date orderingDate;

	private String paymentType;

	private String payer;

	private String paymentNo;

	private String paryerIdcard;

	private Date payDate;

	// 状态描述，1：待采购，2：待通知发货，3：待审核，4：审核不通过，5：已取消，6：审核通过
	private String statusDesc;

	private String status;

	private String email;

	private String purchaseOrderNo;

	private String tradeNo;

	private Integer warehouseId;

	private String warehouseName;

	private String purchasePaymentNo;// 采购在线支付交易号

	private Date purchasePayDate;// 采购在线支付时间

	private String purchasePaymentType;// 采购在线支付类型

	private Date confirmReceiptDate;// 确认收货时间

	private Double orderActualPayment;// 订单实际付款金额（分销商支付的金额）

	private Double orderActualAmount;// 实付款

	private String platformOrderNo;

	private Double orderTotalAmount;

	private String address;

	private String receiver;

	private String tel;

	private String idcard;

	private String postCode;

	private String orderer;// 订购人姓名

	private String ordererIDCard;// 订购人身份证

	private String ordererTel;// 订购人手机号

	private String ordererPostcode;// 订购人邮编

	private String customerservice;// 客服账号

	private String logisticsTypeCode;// 物流方式代码

	private Double bbcPostage;// 分销平台运费

	private String logisticsMode; // 物流方式

	private String couponsCode;// 优惠码

	private Double couponsAmount;// 优惠金额

	private List<ViewSaleOrderDetail> details;// 商品详情

	public Double getOrderActualAmount() {
		return orderActualAmount;
	}

	public void setOrderActualAmount(Double orderActualAmount) {
		this.orderActualAmount = orderActualAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ViewSaleOrderDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ViewSaleOrderDetail> details) {
		this.details = details;
	}

	public String getOrderingDateStr() {
		return DateFormatUtils.getStrFromYYYYMMDDHHMMSS(orderingDate);
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

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
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

	public Date getConfirmReceiptDate() {
		return confirmReceiptDate;
	}

	public void setConfirmReceiptDate(Date confirmReceiptDate) {
		this.confirmReceiptDate = confirmReceiptDate;
	}

	public Double getOrderActualPayment() {
		return orderActualPayment;
	}

	public void setOrderActualPayment(Double orderActualPayment) {
		this.orderActualPayment = orderActualPayment;
	}

	public String getPlatformOrderNo() {
		return platformOrderNo;
	}

	public void setPlatformOrderNo(String platformOrderNo) {
		this.platformOrderNo = platformOrderNo;
	}

	public Double getOrderTotalAmount() {
		return orderTotalAmount;
	}

	public void setOrderTotalAmount(Double orderTotalAmount) {
		this.orderTotalAmount = orderTotalAmount;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getOrderer() {
		return orderer;
	}

	public void setOrderer(String orderer) {
		this.orderer = orderer;
	}

	public String getOrdererIDCard() {
		return ordererIDCard;
	}

	public void setOrdererIDCard(String ordererIDCard) {
		this.ordererIDCard = ordererIDCard;
	}

	public String getOrdererTel() {
		return ordererTel;
	}

	public void setOrdererTel(String ordererTel) {
		this.ordererTel = ordererTel;
	}

	public String getOrdererPostcode() {
		return ordererPostcode;
	}

	public void setOrdererPostcode(String ordererPostcode) {
		this.ordererPostcode = ordererPostcode;
	}

	public String getCustomerservice() {
		return customerservice;
	}

	public void setCustomerservice(String customerservice) {
		this.customerservice = customerservice;
	}

	public String getLogisticsTypeCode() {
		return logisticsTypeCode;
	}

	public void setLogisticsTypeCode(String logisticsTypeCode) {
		this.logisticsTypeCode = logisticsTypeCode;
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

}
