package vo.payment;

import entity.payment.yijipay.YijiResult;

/**
 * Created by LSL on 2016/4/18.
 */
public class YijiResultVO extends YijiResult {

    private String inlet;

    private String merchOrderNo;

    private String bizNo;

    private String creatTradeResult;

    private String fastPayStatus;

    private String merchantOrderNo;

    private String context;

    public String getInlet() {
        return inlet;
    }

    public void setInlet(String inlet) {
        this.inlet = inlet;
    }

    public String getMerchOrderNo() {
        return merchOrderNo;
    }

    public void setMerchOrderNo(String merchOrderNo) {
        this.merchOrderNo = merchOrderNo;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public String getCreatTradeResult() {
        return creatTradeResult;
    }

    public void setCreatTradeResult(String creatTradeResult) {
        this.creatTradeResult = creatTradeResult;
    }

    public String getFastPayStatus() {
        return fastPayStatus;
    }

    public void setFastPayStatus(String fastPayStatus) {
        this.fastPayStatus = fastPayStatus;
    }

    public String getMerchantOrderNo() {
        return merchantOrderNo;
    }

    public void setMerchantOrderNo(String merchantOrderNo) {
        this.merchantOrderNo = merchantOrderNo;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
