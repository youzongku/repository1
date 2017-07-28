package entity.payment.shengpay;

import java.util.Date;

public class ShengConfig {
    private Integer id;

    private String wsUrl;

    private String verifyUrl;

    private String balanceQueryUrl;

    private String merchantNo;

    private String payer;

    private String receipt;

    private String version;

    private String accountType;

    private String signType;

    private String signCharset;

    private String ckey;

    private String currencyType;

    private String invokeIp;

    private String verifyCharset;

    private String userIp;

    private String machineName;

    private Date createDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWsUrl() {
        return wsUrl;
    }

    public void setWsUrl(String wsUrl) {
        this.wsUrl = wsUrl;
    }

    public String getVerifyUrl() {
        return verifyUrl;
    }

    public void setVerifyUrl(String verifyUrl) {
        this.verifyUrl = verifyUrl;
    }

    public String getBalanceQueryUrl() {
        return balanceQueryUrl;
    }

    public void setBalanceQueryUrl(String balanceQueryUrl) {
        this.balanceQueryUrl = balanceQueryUrl;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getSignCharset() {
        return signCharset;
    }

    public void setSignCharset(String signCharset) {
        this.signCharset = signCharset;
    }

    public String getCkey() {
        return ckey;
    }

    public void setCkey(String ckey) {
        this.ckey = ckey;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getInvokeIp() {
        return invokeIp;
    }

    public void setInvokeIp(String invokeIp) {
        this.invokeIp = invokeIp;
    }

    public String getVerifyCharset() {
        return verifyCharset;
    }

    public void setVerifyCharset(String verifyCharset) {
        this.verifyCharset = verifyCharset;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}