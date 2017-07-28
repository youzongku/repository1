package forms.order;

import play.data.validation.Constraints;

/**
 *
 *
 * @author ye_ziran
 * @since 2016/3/23 15:04
 */
public class PurchaseConfirmDetailForm {

    @Constraints.Required
    private String sku;
    @Constraints.Required
    private String warehouseId;
    @Constraints.Required
    private Integer qty;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
