package entity.payment.yijipay;

import java.util.Date;

/**
 * 易极付配置信息实体类
 * Created by luwj on 2016/3/25.
 */
public class YijiConfig {

    private Integer id;

    /**
     * 服务商地址
     */
    private String yijiUrl;

    /**
     * HTTP-POST方式请求
     * :httpPost
     */
    private String postProtocol;

    /**
     * HTTP-Get方式请求
     * :httpGet
     */
    private String getProtocol;

    /**
     * 批量支付服务代码
     */
    private String tradeService;

    /**
     * 批量支付服务版本
     */
    private String version;

    /**
     * 商户ID
     */
    private String partnerId;

    /**
     * 签名方式:MD5
     */
    private String signType;

    /**
     * 页面跳转返回URL
     */
    private String returnUrl;

    /**
     * 异步通知URL
     */
    private String notifyUrl;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 单个支付服务代码
     */
    private String singlePayService;

    /**
     * 单个支付服务版本
     */
    private String singlePayVersion;

    /**
     * 配置标识or用途
     */
    private String mark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getYijiUrl() {
        return yijiUrl;
    }

    public void setYijiUrl(String yijiUrl) {
        this.yijiUrl = yijiUrl;
    }

    public String getPostProtocol() {
        return postProtocol;
    }

    public void setPostProtocol(String postProtocol) {
        this.postProtocol = postProtocol;
    }

    public String getGetProtocol() {
        return getProtocol;
    }

    public void setGetProtocol(String getProtocol) {
        this.getProtocol = getProtocol;
    }

    public String getTradeService() {
        return tradeService;
    }

    public void setTradeService(String tradeService) {
        this.tradeService = tradeService;
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

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getSinglePayService() {
        return singlePayService;
    }

    public void setSinglePayService(String singlePayService) {
        this.singlePayService = singlePayService;
    }

    public String getSinglePayVersion() {
        return singlePayVersion;
    }

    public void setSinglePayVersion(String singlePayVersion) {
        this.singlePayVersion = singlePayVersion;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
