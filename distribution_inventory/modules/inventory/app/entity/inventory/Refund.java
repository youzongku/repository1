package entity.inventory;

import java.util.Date;

public class Refund {
    private Integer id;

    private String email;

    private Date refundDate;

    private Double refundTotalAmount;

    private String refundNo;

    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    public Double getRefundTotalAmount() {
        return refundTotalAmount;
    }

    public void setRefundTotalAmount(Double refundTotalAmount) {
        this.refundTotalAmount = refundTotalAmount;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}