package entity.timer;

/**
 * @author longhuashen
 * @since 2017/1/4
 */
public class CloudAndMicroInventoryResult {

    private String sku;

    private Integer warehouseId;

    private Integer cloudInventory;

    private Integer microInventory;

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

    public Integer getCloudInventory() {
        return cloudInventory;
    }

    public void setCloudInventory(Integer cloudInventory) {
        this.cloudInventory = cloudInventory;
    }

    public Integer getMicroInventory() {
        return microInventory;
    }

    public void setMicroInventory(Integer microInventory) {
        this.microInventory = microInventory;
    }
}
