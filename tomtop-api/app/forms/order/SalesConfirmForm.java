package forms.order;

import play.data.validation.Constraints;

import java.util.List;

/**
 * 销售订单确认数据表单
 *
 * note: tp = three-part
 *
 * @author ye_ziran
 * @since 2016/3/30 10:51
 */
public class SalesConfirmForm {

    @Constraints.Required
    private String memberEmail;//分销商邮箱
    @Constraints.Required
    private boolean isNotify;//是否通知发货
    @Constraints.Required
    private String tpTradeNo;//第三方交易号
    @Constraints.Required
    private String tpOrderNo;//第三方订单号
    @Constraints.Required
    private double tpOrderFreight;//第三方订单运费
    @Constraints.Required
    private double tpOrderActureAmount;//第三方实际交易额
    @Constraints.Required
    private Integer shopId;//店铺id，必须在系统中存在
    @Constraints.Required
    private Integer addrId;//地址id，必须在系统中存在

    private String tpOrderDate;//第三方订单生成时间
    private String remark;//备注

    @Constraints.Required
    private List<SalesConfirmDetailForm> orderDetails;

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setIsNotify(boolean isNotify) {
        this.isNotify = isNotify;
    }

    public String getTpTradeNo() {
        return tpTradeNo;
    }

    public void setTpTradeNo(String tpTradeNo) {
        this.tpTradeNo = tpTradeNo;
    }

    public String getTpOrderNo() {
        return tpOrderNo;
    }

    public void setTpOrderNo(String tpOrderNo) {
        this.tpOrderNo = tpOrderNo;
    }

    public double getTpOrderFreight() {
        return tpOrderFreight;
    }

    public void setTpOrderFreight(double tpOrderFreight) {
        this.tpOrderFreight = tpOrderFreight;
    }

    public double getTpOrderActureAmount() {
        return tpOrderActureAmount;
    }

    public void setTpOrderActureAmount(double tpOrderActureAmount) {
        this.tpOrderActureAmount = tpOrderActureAmount;
    }

    public String getTpOrderDate() {
        return tpOrderDate;
    }

    public void setTpOrderDate(String tpOrderDate) {
        this.tpOrderDate = tpOrderDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Integer getAddrId() {
        return addrId;
    }

    public void setAddrId(Integer addrId) {
        this.addrId = addrId;
    }

    public List<SalesConfirmDetailForm> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<SalesConfirmDetailForm> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
