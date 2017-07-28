package dto.product_inventory;

/**
 * @author longhuashen
 * @since 2016/12/12
 */
public class ProductMicroInventoryOrderLockDto {

    private String sku;

    private Integer warehouseId;

    private String account;

    private Integer isEffective;

    private String orderNo;

    private Integer isGift;

    private Integer microInRecordId;//入仓记录的主键

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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(Integer isEffective) {
        this.isEffective = isEffective;
    }

    public Integer getIsGift() {
        return isGift;
    }

    public void setIsGift(Integer isGift) {
        this.isGift = isGift;
    }

    public Integer getMicroInRecordId() {
        return microInRecordId;
    }

    public void setMicroInRecordId(Integer microInRecordId) {
        this.microInRecordId = microInRecordId;
    }
}
