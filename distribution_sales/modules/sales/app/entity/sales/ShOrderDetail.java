package entity.sales;

import java.math.BigDecimal;
import java.util.Date;

import util.sales.DateUtils;

public class ShOrderDetail {
    private Integer id;
    // 售后单id
    private Integer shOrderId;
    // 售后单单号
    private String shOrderNo;
    // 商品对应的采购单号
    private String purchaseOrderNo;
    // 数量
    private Object sku;
    // 退货数量
    private Integer qty;

    private Integer warehoseid;

    private String warehouseName;

    private String productImg;

    private String productName;

    private Double purchasePrice;
    // 均摊价
    private Double capfee;
    /**
     * 实际退款均摊：实际退款金额，按比例均摊到商品上的，实时计算出来的，在数据库表没有对应字段
     */
    private Double actualAmountCapfee;
    // 到仓价
    private Double arriveWarePrice;

    private Date createTime;

    private Date updateTime;
    // 到期时间
    private Date expirationDate;
    // 采购时间
    private String interBarCode;
    
    /**
     * 均摊价小计：capfee * qty
     * @return
     */
	public BigDecimal capfeeSubtotal() {
		if (capfee==null || qty==null || qty==0) {
			return new BigDecimal(0);
		}
		return new BigDecimal(capfee).multiply(new BigDecimal(qty));
	}

    /**
     * 实际退款均摊：实际退款金额，按比例均摊到商品上的，实时计算出来的，在数据库表没有对应字段
     * @return
     */
    public Double getActualAmountCapfee() {
		return actualAmountCapfee;
	}
    /**
     * 实际退款均摊：实际退款金额，按比例均摊到商品上的，实时计算出来的，在数据库表没有对应字段
     * @param actualAmountCapfee
     */
	public void setActualAmountCapfee(Double actualAmountCapfee) {
		this.actualAmountCapfee = actualAmountCapfee;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShOrderId() {
        return shOrderId;
    }

    public void setShOrderId(Integer shOrderId) {
        this.shOrderId = shOrderId;
    }

    public String getShOrderNo() {
        return shOrderNo;
    }

    public void setShOrderNo(String shOrderNo) {
        this.shOrderNo = shOrderNo == null ? null : shOrderNo.trim();
    }

    public String getPurchaseOrderNo() {
        return purchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        this.purchaseOrderNo = purchaseOrderNo == null ? null : purchaseOrderNo.trim();
    }

    public Object getSku() {
        return sku;
    }

    public void setSku(Object sku) {
        this.sku = sku;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getWarehoseid() {
        return warehoseid;
    }

    public void setWarehoseid(Integer warehoseid) {
        this.warehoseid = warehoseid;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName == null ? null : warehouseName.trim();
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg == null ? null : productImg.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getCapfee() {
        return capfee;
    }

    public void setCapfee(Double capfee) {
        this.capfee = capfee;
    }

    public Double getArriveWarePrice() {
        return arriveWarePrice;
    }

    public void setArriveWarePrice(Double arriveWarePrice) {
        this.arriveWarePrice = arriveWarePrice;
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

    public Date getExpirationDate() {
        return expirationDate;
    }
    
    public String getExpirationDateStr() {
    	if (expirationDate!=null) {
    		return DateUtils.date2string(expirationDate, DateUtils.FORMAT_DATE_PAGE);
		}
        return "";
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getInterBarCode() {
        return interBarCode;
    }

    public void setInterBarCode(String interBarCode) {
        this.interBarCode = interBarCode == null ? null : interBarCode.trim();
    }
}