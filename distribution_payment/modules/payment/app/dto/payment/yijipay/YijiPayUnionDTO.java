package dto.payment.yijipay;

import dto.payment.ReturnMess;

import java.util.List;

/**
 * Created by luwj on 2016/4/18.
 * 易极付聚合支付请求参数
 */
public class YijiPayUnionDTO extends YijiPayPubReqDTO {

    /**
     * 网关地址
     */
    private String actionUrl;

    /**
     * 外部会员id  可选(1-64)
     */
    private String outUserId;

    /**
     * 买家(易极付)用户id  可选(20)
     */
    private String buyerUserId;

    /**
     * 	买家真实姓名  可选(1-64)
     */
    private String buyerRealName;

    /**
     * 交易信息  必填
     * [{"merchOrderNo":"234531545","tradeName":"及时到账","sellerUserId":"20165148968752486415","tradeAmount":"84.51","currency":"CNY","goodsTypeCode":"5146854","goodsTypeName":"计算机","goodsName":"笔记本电脑","memo":"备注","shareProfits":"2012658947952258175214~88.66~分润^45631541323241~65.21~fenrun","sellerOrgName":"猪八戒科技有限公司","autoCloseDuration":"14400"}]
     */
    private List<YijiPayTradeInfoDTO> tradeInfo;

    /**
     * 支付类型  必填
     * BALANCE:余额支付（仅供PC使用）
     QUICKPAY:快捷支付（仅供PC使用）
     ONLINEBANK:网银支付（仅供PC使用）
     THIRDSCANPAY:扫码支付(仅支持单笔，仅供PC使用)
     OFFLINEPAY:线下支付（仅供PC使用）
     PAYMENT_TYPE_SUPER:聚合支付（仅供MOBILE使用）
     PAYMENT_TYPE_YJ:易手富支付（仅供MOBILE使用）
     PAYMENT_TYPE_WECHAT:微信支付（仅供MOBILE使用）
     PAYMENT_TYPE_UPMP:银联支付（仅供MOBILE使用）
     */
    private String paymentType;

    /**
     * 终端类型  可选
     * PC:电脑平台
     MOBILE:移动平台
     */
    private String userTerminalType;

    /**
     * 买家机构名  可选
     */
    private String buyerOrgName;

    /**
     * 场景识别行为  可选(0-256)
     */
    private String behavior;

    /**
     * 保证金主交易号  可选
     * 将保证金用于抵扣部分货款，仅限有保证金业务使用
     */
    private String mainTradeNo;

    /**
     * 客户号  可选
     * 仅民生银行企业版填写
     */
    private String customerNo;

    private ReturnMess returnMess;

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getOutUserId() {
        return outUserId;
    }

    public void setOutUserId(String outUserId) {
        this.outUserId = outUserId;
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public String getBuyerRealName() {
        return buyerRealName;
    }

    public void setBuyerRealName(String buyerRealName) {
        this.buyerRealName = buyerRealName;
    }

    public List<YijiPayTradeInfoDTO> getTradeInfo() {
        return tradeInfo;
    }

    public void setTradeInfo(List<YijiPayTradeInfoDTO> tradeInfo) {
        this.tradeInfo = tradeInfo;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getUserTerminalType() {
        return userTerminalType;
    }

    public void setUserTerminalType(String userTerminalType) {
        this.userTerminalType = userTerminalType;
    }

    public String getBuyerOrgName() {
        return buyerOrgName;
    }

    public void setBuyerOrgName(String buyerOrgName) {
        this.buyerOrgName = buyerOrgName;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public String getMainTradeNo() {
        return mainTradeNo;
    }

    public void setMainTradeNo(String mainTradeNo) {
        this.mainTradeNo = mainTradeNo;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public ReturnMess getReturnMess() {
        return returnMess;
    }

    public void setReturnMess(ReturnMess returnMess) {
        this.returnMess = returnMess;
    }
}
