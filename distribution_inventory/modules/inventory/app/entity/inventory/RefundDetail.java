package entity.inventory;

public class RefundDetail {
    private Integer id;

    private String purchaseNo;

    private String sku;

    private Double refundPrice;

    private Integer refundQty;

    private Double refundTotalAmount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPurchaseNo() {
        return purchaseNo;
    }

    public void setPurchaseNo(String purchaseNo) {
        this.purchaseNo = purchaseNo;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getRefundPrice() {
        return refundPrice;
    }

    public void setRefundPrice(Double refundPrice) {
        this.refundPrice = refundPrice;
    }

    public Integer getRefundQty() {
        return refundQty;
    }

    public void setRefundQty(Integer refundQty) {
        this.refundQty = refundQty;
    }

    public Double getRefundTotalAmount() {
        return refundTotalAmount;
    }

    public void setRefundTotalAmount(Double refundTotalAmount) {
        this.refundTotalAmount = refundTotalAmount;
    }
}