package dto.sales;

/**
 * @author longhuashen
 * @since 2017/3/6
 */
public class ShOrderDto {

    private Integer id;

    private String productName;

    private String sku;

    private String productImg;

    private Integer qty;

    private Integer warehouseId;

    private String warehouseName;

    private Integer status;

    private String createTime;

    private String email;

    private Double demandAmount;

    private String qaDesc;

    /**
     * 售后单号
     */
    private String orderNo;

    private String saleOrderNo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Double getDemandAmount() {
        return demandAmount;
    }

    public void setDemandAmount(Double demandAmount) {
        this.demandAmount = demandAmount;
    }

    public String getQaDesc() {
        return qaDesc;
    }

    public void setQaDesc(String qaDesc) {
        this.qaDesc = qaDesc;
    }

    public String getSaleOrderNo() {
        return saleOrderNo;
    }

    public void setSaleOrderNo(String saleOrderNo) {
        this.saleOrderNo = saleOrderNo;
    }

    @Override
    public String toString() {
        return "ShOrderDto{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", sku='" + sku + '\'' +
                ", productImg='" + productImg + '\'' +
                ", qty=" + qty +
                ", warehouseId=" + warehouseId +
                ", warehouseName='" + warehouseName + '\'' +
                ", status=" + status +
                ", createTime='" + createTime + '\'' +
                ", email='" + email + '\'' +
                ", orderNo='" + orderNo + '\'' +
                '}';
    }
}
