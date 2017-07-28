package dto.product_inventory;

import java.util.Date;

/**
 * @author longhuashen
 * @since 2016/12/16
 */
public class ProductMicroInventoryInRecordDto {

    private String account;

    private String sku;

    private Integer warehouseId;

    private Date expirationDate;

    private Integer isGift;

    private Float purchasePrice;

    private Float capfee;

    private Integer moreThan;

    private Integer purchaseTimeSort;//按采购时间排序 0 升序 1 降序

    private String orderNo;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Integer getIsGift() {
        return isGift;
    }

    public void setIsGift(Integer isGift) {
        this.isGift = isGift;
    }

    public Integer getMoreThan() {
        return moreThan;
    }

    public void setMoreThan(Integer moreThan) {
        this.moreThan = moreThan;
    }

    public Integer getPurchaseTimeSort() {
        return purchaseTimeSort;
    }

    public void setPurchaseTimeSort(Integer purchaseTimeSort) {
        this.purchaseTimeSort = purchaseTimeSort;
    }

    public Float getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Float purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Float getCapfee() {
        return capfee;
    }

    public void setCapfee(Float capfee) {
        this.capfee = capfee;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
