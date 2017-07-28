package dto.product.inventory;

import java.util.Date;

/**
 * @author longhuashen
 * @since 2016/12/20
 *
 * 待采购
 */
public class PurchaseDto {

    private int warehouseId;

    private String sku;

    private Integer qty;

    private String expirationDate;

    public PurchaseDto() {
     
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

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }
}
