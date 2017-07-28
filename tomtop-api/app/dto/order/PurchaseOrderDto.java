package dto.order;

import utils.PriceFormatUtil;

import java.util.Date;

/**
 * @author ye_ziran
 * @since 2016/3/23 15:15
 */
public class PurchaseOrderDto {

    private Integer id;

    private String email;

    private String purchaseOrderNo;//采购单编号

    private String cartId;//购物车编号

    private Integer status;//采购单状态：0待付款，1已付款，2已取消

    private String paymentId;//付款方式

    private String currency;//结算货币

    private String transactionId;//交易号

    private String payerIp;//下单人ip

    private Date payDate;//付款时间

    private String payHost;

    private String remarks;//备注信息

    private Double purchaseTotalAmount;//订单总金额

    private Date validablePayDate;//支付时效点

    private Date purchaseDate;//下单时间

    private String payerIdcard;//支付人身份证号码

    private String payerName;//支付人姓名

    private Integer purchaseType;//采购类型（1：常规采购，2：缺货采购）

    private Integer sid;//若当前采购订单是缺货采购，那么本属性应该对应缺货的发货单，否则本属性则为空

    private Double purchaseDiscountAmount;//订单折后金额

    private Double discount;//订单折扣

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
        return PriceFormatUtil.keepTwoDecimalDouble(purchaseTotalAmount);
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

}
