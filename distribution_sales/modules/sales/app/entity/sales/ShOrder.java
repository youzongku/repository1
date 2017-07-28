package entity.sales;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
/**
 * 售后单
 * @author huangjc
 * @date 2017年3月23日
 */
public class ShOrder {
    private Integer id;
    // 售后单单号
    private String shOrderNo;
    // 发货单单号
    private String xsOrderNo;
    // 状态
    private Integer status;
    // 分销商用户
    private String email;
    // 分销商模式
    private Integer disMode;
    // 分销商名称
    private String disName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    // 业务员erp账号
    private String businessErp;
    // 退货单总金额
    private Double orderAmount;
    // 客户要求退款金额
    private Double demandAmount;
    // 实际退款金额
    private Double actualAmount;
    // 问题描述
    private String qaDesc;
    // 要求退货数量
    private int demandQty;

    private int warehouseId;

    private String warehouseName;

    private String productImg;

    private String productName;

    private String sku;
    // 是否寄回商品
    private Integer isProductReturn;
    // 快递公司
    private String company;
    // 快递公司
    private String expressCode;
    // 确定销售单号下的sku
    private Integer detailOrderId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date financeConfirmTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendProductTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receivedProductTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShOrderNo() {
        return shOrderNo;
    }

    public void setShOrderNo(String shOrderNo) {
        this.shOrderNo = shOrderNo == null ? null : shOrderNo.trim();
    }

    public String getXsOrderNo() {
        return xsOrderNo;
    }

    public void setXsOrderNo(String xsOrderNo) {
        this.xsOrderNo = xsOrderNo == null ? null : xsOrderNo.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Integer getDisMode() {
        return disMode;
    }

    public void setDisMode(Integer disMode) {
        this.disMode = disMode;
    }

    public String getDisName() {
        return disName;
    }

    public void setDisName(String disName) {
        this.disName = disName == null ? null : disName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getBusinessErp() {
        return businessErp;
    }

    public void setBusinessErp(String businessErp) {
        this.businessErp = businessErp == null ? null : businessErp.trim();
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Double getDemandAmount() {
        return demandAmount;
    }

    public void setDemandAmount(Double demandAmount) {
        this.demandAmount = demandAmount;
    }

    public Double getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(Double actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getQaDesc() {
        return qaDesc;
    }

    public void setQaDesc(String qaDesc) {
        this.qaDesc = qaDesc == null ? null : qaDesc.trim();
    }

    public int getDemandQty() {
        return demandQty;
    }

    public void setDemandQty(int demandQty) {
        this.demandQty = demandQty;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
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

    public Integer getIsProductReturn() {
        return isProductReturn;
    }

    public void setIsProductReturn(Integer isProductReturn) {
        this.isProductReturn = isProductReturn;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }

    public Date getFinanceConfirmTime() {
        return financeConfirmTime;
    }

    public void setFinanceConfirmTime(Date financeConfirmTime) {
        this.financeConfirmTime = financeConfirmTime;
    }

    public Date getSendProductTime() {
        return sendProductTime;
    }

    public void setSendProductTime(Date sendProductTime) {
        this.sendProductTime = sendProductTime;
    }

    public Date getReceivedProductTime() {
        return receivedProductTime;
    }

    public void setReceivedProductTime(Date receivedProductTime) {
        this.receivedProductTime = receivedProductTime;
    }

    public Integer getDetailOrderId() {
        return detailOrderId;
    }

    public void setDetailOrderId(Integer detailOrderId) {
        this.detailOrderId = detailOrderId;
    }
}