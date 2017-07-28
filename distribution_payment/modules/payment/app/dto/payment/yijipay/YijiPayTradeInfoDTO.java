package dto.payment.yijipay;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by luwj on 2017/5/27.
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class YijiPayTradeInfoDTO {

    /**
     * 单笔交易外部订单号  必填(1-64)
     */
    private String merchOrderNo;

    /**
     * 交易名称  可选(0-64)
     */
    private String tradeName;

    /**
     * 卖家的易极付会员id  必填(0-20)
     */
    private String sellerUserId;

    /**
     * 交易金额  必填
     */
    private String tradeAmount;

    /**
     * 交易币种  必填
     * CNY:人民币 USD:美元 JPY:日元 HKD:港币 GBP:英镑
     */
    private String currency;

    /**
     * 商品类型码  可选(0-64)
     */
    private String goodsTypeCode;

    /**
     * 商品类型名称  可选(0-64)
     */
    private String goodsTypeName;

    /**
     * 商品名称  必填(0-64)
     */
    private String goodsName;

    /**
     * 备注  可选(0-128)
     */
    private String memo;

    /**
     * 分润信息  可选
     * 分润最多支持10笔,单笔分润格式:userId1~amount1~memo1^userId2~amount2~memo2
     */
    private String shareProfits;

    /**
     * 卖家机构名  可选
     */
    private String sellerOrgName;

    /**
     * 未付款订单自动关闭时间  可选
     * 默认10天，单位分钟,最小为1
     */
    private Integer autoCloseDuration;

    public String getMerchOrderNo() {
        return merchOrderNo;
    }

    public void setMerchOrderNo(String merchOrderNo) {
        this.merchOrderNo = merchOrderNo;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(String sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getGoodsTypeCode() {
        return goodsTypeCode;
    }

    public void setGoodsTypeCode(String goodsTypeCode) {
        this.goodsTypeCode = goodsTypeCode;
    }

    public String getGoodsTypeName() {
        return goodsTypeName;
    }

    public void setGoodsTypeName(String goodsTypeName) {
        this.goodsTypeName = goodsTypeName;
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

    public String getShareProfits() {
        return shareProfits;
    }

    public void setShareProfits(String shareProfits) {
        this.shareProfits = shareProfits;
    }

    public String getSellerOrgName() {
        return sellerOrgName;
    }

    public void setSellerOrgName(String sellerOrgName) {
        this.sellerOrgName = sellerOrgName;
    }

    public Integer getAutoCloseDuration() {
        return autoCloseDuration;
    }

    public void setAutoCloseDuration(Integer autoCloseDuration) {
        this.autoCloseDuration = autoCloseDuration;
    }
}
