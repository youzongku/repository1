package entity.payment.yijipay;

import java.util.Date;

public class YijiResult {
    private Integer id;

    private String partnerId;

    private String outOrderNo;

    private String orderNo;

    private String notifyUrl;

    private String returnUrl;

    private String success;

    private String protocol;

    private String service;

    private String version;

    private String notifyTime;

    private String signType;

    private String sign;

    private String resultCode;

    private String resultMessage;

    private String tradeNo;

    private String tradeStatus;

    private String tradeType;

    private String accountDay;

    private String tradeAmount;

    private Date createDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getOutOrderNo() {
        return outOrderNo;
    }

    public void setOutOrderNo(String outOrderNo) {
        this.outOrderNo = outOrderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
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

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getAccountDay() {
        return accountDay;
    }

    public void setAccountDay(String accountDay) {
        this.accountDay = accountDay;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

	@Override
	public String toString() {
		return "YijiResult [id=" + id + ", partnerId=" + partnerId + ", outOrderNo=" + outOrderNo + ", orderNo="
				+ orderNo + ", notifyUrl=" + notifyUrl + ", returnUrl=" + returnUrl + ", success=" + success
				+ ", protocol=" + protocol + ", service=" + service + ", version=" + version + ", notifyTime="
				+ notifyTime + ", signType=" + signType + ", sign=" + sign + ", resultCode=" + resultCode
				+ ", resultMessage=" + resultMessage + ", tradeNo=" + tradeNo + ", tradeStatus=" + tradeStatus
				+ ", tradeType=" + tradeType + ", accountDay=" + accountDay + ", tradeAmount=" + tradeAmount
				+ ", createDate=" + createDate + "]";
	}
    
}