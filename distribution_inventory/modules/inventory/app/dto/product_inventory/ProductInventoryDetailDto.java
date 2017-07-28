package dto.product_inventory;

/**
 * @author longhuashen
 * @since 2016/12/13
 */
public class ProductInventoryDetailDto {

    private String account;

    private String sku;

    private Integer warehouseId;

    private String warehouseName;

    private Integer isGift;

    private Integer expirationDateSort;// 是否需要按照变更时间排序，0：asc，1：desc，null：默认排序

    private Integer moreThan;

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

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Integer getExpirationDateSort() {
        return expirationDateSort;
    }

    public void setExpirationDateSort(Integer expirationDateSort) {
        this.expirationDateSort = expirationDateSort;
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
}
