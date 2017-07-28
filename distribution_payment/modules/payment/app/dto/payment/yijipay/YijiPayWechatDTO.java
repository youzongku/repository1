package dto.payment.yijipay;

import dto.payment.ReturnMess;

/**
 * Created by luwj on 2016/8/3.
 */
public class YijiPayWechatDTO {

    private ReturnMess returnMess;

    private String protocol;

    private String service;

    private String version;

    private String partnerId;

    private String sellerUserId;

    private String orderNo;

    private String merchOrderNo;

    private String signType;

    private String returnUrl;

    private String notifyUrl;

    private String tradeName;

    private String outOrderNo;

    private String sellerCardNo;

    private String tradeMemo;

    private String tradeAmount;

    private String profitClauses;

    private String currency;

    private String payFrom;

    private String mutableType;

    private String chargeExtend;

    private String uiStyle;

    private String goodsClauses;

    private String actionUrl;

    private String sign;

    public ReturnMess getReturnMess() {
        return returnMess;
    }

    public void setReturnMess(ReturnMess returnMess) {
        this.returnMess = returnMess;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(String sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getOutOrderNo() {
        return outOrderNo;
    }

    public void setOutOrderNo(String outOrderNo) {
        this.outOrderNo = outOrderNo;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getUiStyle() {
        return uiStyle;
    }

    public void setUiStyle(String uiStyle) {
        this.uiStyle = uiStyle;
    }

    public String getGoodsClauses() {
        return goodsClauses;
    }

    public void setGoodsClauses(String goodsClauses) {
        this.goodsClauses = goodsClauses;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getMerchOrderNo() {
        return merchOrderNo;
    }

    public void setMerchOrderNo(String merchOrderNo) {
        this.merchOrderNo = merchOrderNo;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getSellerCardNo() {
        return sellerCardNo;
    }

    public void setSellerCardNo(String sellerCardNo) {
        this.sellerCardNo = sellerCardNo;
    }

    public String getTradeMemo() {
        return tradeMemo;
    }

    public void setTradeMemo(String tradeMemo) {
        this.tradeMemo = tradeMemo;
    }

    public String getProfitClauses() {
        return profitClauses;
    }

    public void setProfitClauses(String profitClauses) {
        this.profitClauses = profitClauses;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPayFrom() {
        return payFrom;
    }

    public void setPayFrom(String payFrom) {
        this.payFrom = payFrom;
    }

    public String getMutableType() {
        return mutableType;
    }

    public void setMutableType(String mutableType) {
        this.mutableType = mutableType;
    }

    public String getChargeExtend() {
        return chargeExtend;
    }

    public void setChargeExtend(String chargeExtend) {
        this.chargeExtend = chargeExtend;
    }
}
