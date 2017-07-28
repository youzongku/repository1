package entity.payment.yijipay;

import java.util.Date;

/**
 * 易极付合并同步回执
 * Created by luwj on 2016/4/20.
 */
public class MergeResult {

    private Integer id;

    private String mergePayResult;

    private String notifyTime;

    private String notifyUrl;

    private String orderNo;

    private String partnerId;

    private String protocol;

    private String resultCode;

    private String resultMessage;

    private String returnUrl;

    private String service;

    private String sign;

    private String signType;

    private String success;

    private String tradeDetail;

    private Date createDate;

    private String version;

    /**  接口升级 edit by luwj **/
    private String tradeNo;//交易号

    private String merchOrderNo;//订单号

    private String creatTradeResult;//支付详情

    private String creatResult;//支付结果

    private String tradeAmount;//支付金额

    private String context;//

    /**         end          **/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMergePayResult() {
        return mergePayResult;
    }

    public void setMergePayResult(String mergePayResult) {
        this.mergePayResult = mergePayResult;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
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

    public String getTradeDetail() {
        return tradeDetail;
    }

    public void setTradeDetail(String tradeDetail) {
        this.tradeDetail = tradeDetail;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getMerchOrderNo() {
        return merchOrderNo;
    }

    public void setMerchOrderNo(String merchOrderNo) {
        this.merchOrderNo = merchOrderNo;
    }

    public String getCreatTradeResult() {
        return creatTradeResult;
    }

    public void setCreatTradeResult(String creatTradeResult) {
        this.creatTradeResult = creatTradeResult;
    }

    public String getCreatResult() {
        return creatResult;
    }

    public void setCreatResult(String creatResult) {
        this.creatResult = creatResult;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
