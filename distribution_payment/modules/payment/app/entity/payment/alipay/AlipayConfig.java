package entity.payment.alipay;

import java.util.Date;

/**
 * 支付宝配置实体
 *
 * @author luwj
 *
 */
public class AlipayConfig implements java.io.Serializable{

    private static final long serialVersionUID = -2286891565370056518L;

    private Integer id;

    private String partner;// 商户在支付宝的用户 ID

    private String notifyUrl;// 通知 URL

    private String returnUrl;// 返回 URL

    private String key;// 密钥
    /**
     * 支付宝外汇地区代码 :GBP 英镑 HKD 港币 USD 美元 CHF 瑞士法郎 SGD 新加坡元 SEK 瑞典克朗 DKK 丹麦克朗 NOK
     * 挪威克朗 JPY 日元 CAD 加拿大元 AUD 澳大利亚元 EUR 欧元 NZD 新西兰元 THB 泰铢
     */
    private String exchangeRate;

    private String gatewayUrl;// 支付宝网关url

    private Integer types;//类型,1:国际支付宝,2:国内支付宝

    private String createUser;

    private Date createDate;

    private String lastUpdateUser;

    private Date lastUpdateDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public Integer getTypes() {
        return types;
    }

    public void setTypes(Integer types) {
        this.types = types;
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

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}