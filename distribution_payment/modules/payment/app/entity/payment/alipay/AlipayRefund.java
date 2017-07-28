package entity.payment.alipay;

import java.util.Date;

/**
 * 支付宝退款记录实体
 *
 * @author luwj
 *
 */
public class AlipayRefund implements java.io.Serializable {

    private static final long serialVersionUID = -442003936150330548L;

    private Integer id;

    private String outReturnNo;// 外部退款号

    private String outTradeNo;// 原外部交易号

    private String returnAmount;// 退款金额(外币)

    private String returnRmbAmount;// 退款金额(人民币)

    private String currency;// 币种

    private String gmtReturn;// 退款时间

    private String reason;// 退款原因

    private String isSuccess;// 成功:T,失败:F

    private String error;// 失败原因

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

    public String getOutReturnNo() {
        return outReturnNo;
    }

    public void setOutReturnNo(String outReturnNo) {
        this.outReturnNo = outReturnNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(String returnAmount) {
        this.returnAmount = returnAmount;
    }

    public String getReturnRmbAmount() {
        return returnRmbAmount;
    }

    public void setReturnRmbAmount(String returnRmbAmount) {
        this.returnRmbAmount = returnRmbAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getGmtReturn() {
        return gmtReturn;
    }

    public void setGmtReturn(String gmtReturn) {
        this.gmtReturn = gmtReturn;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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