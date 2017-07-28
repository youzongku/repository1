package forms.order;

import play.data.validation.Constraints;

/**
 * 销售订单确认详细form
 *
 * @author ye_ziran
 * @since 2016/3/30 10:51
 */
public class SalesConfirmDetailForm {

    @Constraints.Required
    private String sku;
    @Constraints.Required
    private Integer qty;
    @Constraints.Required
    private Integer warehouseId;
    @Constraints.Required
    private double actualPrice;

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

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }
}
