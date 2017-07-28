package dto.payment.yijipay;

/**
 * 查询易极付实名认证返回结果实体
 * Created by luwj on 2016/4/21.
 */
public class YijiPayRealNameDTO {

    private String partnerId;

    private String success;

    private String protocol;

    private String service;

    private String version;

    private String orderNo;

    private String signType;

    private String sign;

    private String resultCode;

    private String resultMessage;

    private String realNameQueryResult;

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
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

    public String getRealNameQueryResult() {
        return realNameQueryResult;
    }

    public void setRealNameQueryResult(String realNameQueryResult) {
        this.realNameQueryResult = realNameQueryResult;
    }
}
