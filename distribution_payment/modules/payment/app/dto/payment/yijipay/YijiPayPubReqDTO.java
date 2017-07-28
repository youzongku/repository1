package dto.payment.yijipay;

/**
 * Created by luwj on 2017/5/27.
 * 易极付公共请求参数
 */
public class YijiPayPubReqDTO {

    /**
     * 请求订单号  必填
     * 长度：20-40字符。
     */
    private String orderNo;

    /**
     * 报文协议格式。 可选
     * HTTP-FORM-JOSN(默认)
     */
    private String protocol;

    /**
     * 接口服务代码 必填
     */
    private String service;

    /**
     * 服务版本 默认1.0 可选
     */
    private String version;

    /**
     * 商户ID  必填
     */
    private String partnerId;

    /**
     * 签名方式  可选
     * 目前支持MD5，请填写“MD5”必须大写
     */
    private String signType;

    /**
     * 签名  必填
     */
    private String sign;

    /**
     * 交易订单号  可选
     * 交易类业务的交易订单号，交易类业务如未特殊说明，都根据该订单号支持幂等。
     */
    private String merchOrderNo;

    /**
     * 会话参数  可选
     * 调用端的API调用会话参数，请求参数任何合法值，在响应时会回传给调用端。
     */
    private String context;

    /**
     * 页面跳转返回URL  可选
     */
    private String returnUrl;

    /**
     * 异步通知URL  可选
     */
    private String notifyUrl;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public String getMerchOrderNo() {
        return merchOrderNo;
    }

    public void setMerchOrderNo(String merchOrderNo) {
        this.merchOrderNo = merchOrderNo;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
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
}
