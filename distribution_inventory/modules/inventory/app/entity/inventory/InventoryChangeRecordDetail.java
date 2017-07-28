package entity.inventory;

public class InventoryChangeRecordDetail {
    private Integer id;

    private String sku;

    private Integer qty;

    private Integer changeRecordId;

    private Integer warehouseId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getChangeRecordId() {
        return changeRecordId;
    }

    public void setChangeRecordId(Integer changeRecordId) {
        this.changeRecordId = changeRecordId;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }
}