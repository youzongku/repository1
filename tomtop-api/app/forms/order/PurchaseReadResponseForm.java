package forms.order;

import java.util.Date;
import java.util.List;

/**
 *
 * 采购单查询响应form;
 *
 * <p>
 *     form是用来裁剪从服务器接收的数据，利用现有的ObjectMapper工具，将jsonNode映射成form，
 *     并且只保留form中有的相同的字段
 * </p>
 *
 * @author ye_ziran
 * @since 2016/4/6 15:06
 */
public class PurchaseReadResponseForm {

    private String email;//用户
    private String purchaseOrderNo;//采购单号
    private Integer status;//采购单状态：0待付款，1已付款，2已取消
    private Date purchaseDate;//下单时间
    //private String sorderDate;//下单时间(String)
    private String statusMes;//订单状态：待付款，已付款，已取消
    private Double purchaseTotalAmount;//订单总金额
    private Date payDate;//付款时间
    //private String spayDate;//付款时间(String)
    private Double purchaseDiscountAmount;//订单折后金额
    private List<PurchaseOrderDetailResponseForm> details;
    private Double discount;//订单折扣
    private Integer sid;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPurchaseOrderNo() {
        return purchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        this.purchaseOrderNo = purchaseOrderNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getStatusMes() {
        return statusMes;
    }

    public void setStatusMes(String statusMes) {
        this.statusMes = statusMes;
    }

    public Double getPurchaseTotalAmount() {
        return purchaseTotalAmount;
    }

    public void setPurchaseTotalAmount(Double purchaseTotalAmount) {
        this.purchaseTotalAmount = purchaseTotalAmount;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public Double getPurchaseDiscountAmount() {
        return purchaseDiscountAmount;
    }

    public void setPurchaseDiscountAmount(Double purchaseDiscountAmount) {
        this.purchaseDiscountAmount = purchaseDiscountAmount;
    }

    public List<PurchaseOrderDetailResponseForm> getDetails() {
        return details;
    }

    public void setDetails(List<PurchaseOrderDetailResponseForm> details) {
        this.details = details;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }
}
