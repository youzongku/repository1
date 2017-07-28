package dto.payment.yijipay;

/**
 * Created by luwj on 2016/3/25.
 * edit by luwj on 2017/05/17.
 * 易极付即时到帐请求参数
 */
public class YijiPayParamsDTO extends YijiPayPubReqDTO{

    /**
     * 网关地址
     */
    private String actionUrl;

    /****************************  跳转聚合支付  ***************************************/

    /**
     * 交易号  可选(10-64)
     * tradeNo为空时，tradeAmount、goodsName、sellerUserId不能为空
     */
    private String tradeNo;

    /**
     * 买家(易极付)用户id  可选(0-64)
     */
    private String buyerUserId;

    /**
     * 终端类型  可选
     * PC:电脑平台
     * MOBILE:移动平台(默认)
     */
    private String userTerminalType;

    /**
     * 交易名称  可选
     */
    private String tradeName;

    /**
     * 商品类型  可选(0-64)
     */
    private String goodsType;

    /**
     * 商品名称  可选(0-64)
     * tradeNo为空时，tradeAmount、goodsName、sellerUserId不能为空
     */
    private String goodsName;

    /**
     * 备注 可选(0-128)
     */
    private String memo;

    /**
     * 卖家会员号  可选(20)
     * tradeNo为空时，tradeAmount、goodsName、sellerUserId不能为空
     */
    private String sellerUserId;

    /**
     * 交易金额  可选
     * tradeNo为空时，tradeAmount、goodsName、sellerUserId不能为空
     */
    private Double tradeAmount;

    /**
     * 公众号用户标示  可选
     */
    private String openid;

    /**
     * 收费扩展维度  可选
     * 外部商户传入的收费扩展字段，比如当做Vip标识，用户对特殊角色进行收费，0:非VIP，1:VIP
     */
    private String chargeExtends;

    /**
     * 未付款自动关闭时间  可选
     * 未付款自动关闭时间，单位为分钟，默认10天
     */
    private Integer autoCloseDuration;

    /**
     * 请求mac地址  可选(0-48)
     * 示例:14-DD-A9-DE-FF-87
     */
    private String macAddress;

    /**
     * 用户客户端ip  可选
     * 示例:192.168.45.23
     */
    private String userEndIp;

    /**
     * 支付类型  可选-移动专属参数
     * 默认:PAYMENT_TYPE_SUPER
     * PAYMENT_TYPE_SUPER:聚合支付（移动支付）
     * PAYMENT_TYPE_YJ:易手富支付（移动支付）
     * PAYMENT_TYPE_WECHAT:微信支付（移动支付）
     * BALANCE:余额支付（PC支付）
     * QUICKPAY:快捷支付（PC支付）
     * ONLINEBANK:网银支付（PC支付）
     * THIRDSCANPAY:扫码支付（PC支付）
     * OFFLINEPAY:线下支付（PC支付）
     */
    private String paymentType;

    /**
     * 会员类型  可选-移动专属参数
     * MEMBER_TYPE_YIJI:易极付会员
     * MEMBER_TYPE_PATERN:商户会员
     * MEMBER_TYPE_CARD:收单
     */
    private String memberType;

    /**
     * 用户姓名  可选-移动专属参数
     */
    private String name;

    /**
     * 用户姓名是否固定  可选-移动专属参数
     * 示例: true
     */
    private String stable;

    /**
     * 手机号码  可选-移动专属参数
     */
    private String mobileNo;

    /**
     * 手机号码是否固定  可选-移动专属参数
     * 示例: true
     */
    private String mobileNoStable;

    /**
     * 用户银行卡号  可选-移动专属参数
     */
    private String cardNo;

    /**
     * 用户银行卡号是否固定  可选-移动专属参数
     * 示例: true
     */
    private String cardNoStable;

    /**
     * 身份证号码  可选-移动专属参数
     */
    private String certNo;

    /**
     * 身份证号码是否固定  可选-移动专属参数
     * 示例: true
     */
    private String certNoStable;

    /**
     * 银行简称  可选-PC专属参数
     * PC专属参数，微信扫码请传入WEIXIN，支付宝扫码请传入ALIPAY
     */
    private String bankCode;

    /**
     * 对公对私  可选-PC专属参数
     * CORPORATE:对公
     * PERSONAL:对私
     */
    private String personalCorporateType;

    /**
     * 银行卡类型  可选-PC专属参数
     * CREDIT:CREDIT
     * DEBIT:DEBIT
     */
    private String cardType;

    /**
     * 分润信息  可选
     * 分润最多支持10笔,单笔分润格式:userId1~amount1~memo1^userId2~amount2~memo2
     */
    private String shareProfits;

    /**
     * 分润方式  可选
     * M:从商户中间户分润
     * S:从卖家分润
     */
    private String shareMethod;

    /**
     * 平台商下商户的易极付ID  可选
     * 仅限特殊场景使用（套号模式），如没有切勿传递该字段
     */
    private String sellerMerchantId;

    /**
     * 客户号  可选
     */
    private String customerNo;

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public String getUserTerminalType() {
        return userTerminalType;
    }

    public void setUserTerminalType(String userTerminalType) {
        this.userTerminalType = userTerminalType;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(String sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    public Double getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(Double tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getChargeExtends() {
        return chargeExtends;
    }

    public void setChargeExtends(String chargeExtends) {
        this.chargeExtends = chargeExtends;
    }

    public Integer getAutoCloseDuration() {
        return autoCloseDuration;
    }

    public void setAutoCloseDuration(Integer autoCloseDuration) {
        this.autoCloseDuration = autoCloseDuration;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getUserEndIp() {
        return userEndIp;
    }

    public void setUserEndIp(String userEndIp) {
        this.userEndIp = userEndIp;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStable() {
        return stable;
    }

    public void setStable(String stable) {
        this.stable = stable;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getMobileNoStable() {
        return mobileNoStable;
    }

    public void setMobileNoStable(String mobileNoStable) {
        this.mobileNoStable = mobileNoStable;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardNoStable() {
        return cardNoStable;
    }

    public void setCardNoStable(String cardNoStable) {
        this.cardNoStable = cardNoStable;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getCertNoStable() {
        return certNoStable;
    }

    public void setCertNoStable(String certNoStable) {
        this.certNoStable = certNoStable;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getPersonalCorporateType() {
        return personalCorporateType;
    }

    public void setPersonalCorporateType(String personalCorporateType) {
        this.personalCorporateType = personalCorporateType;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getShareProfits() {
        return shareProfits;
    }

    public void setShareProfits(String shareProfits) {
        this.shareProfits = shareProfits;
    }

    public String getShareMethod() {
        return shareMethod;
    }

    public void setShareMethod(String shareMethod) {
        this.shareMethod = shareMethod;
    }

    public String getSellerMerchantId() {
        return sellerMerchantId;
    }

    public void setSellerMerchantId(String sellerMerchantId) {
        this.sellerMerchantId = sellerMerchantId;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }
}
