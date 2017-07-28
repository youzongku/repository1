package dto.product_inventory;

/**
 * @author longhuashen
 * @since 2016/12/16
 */
public class SaleOrderDto {

    /**
     * sku
     */
    private String sku;

    /**
     * 数量
     */
    private int qty;

    public SaleOrderDto(String sku, int qty) {
        this.sku = sku;
        this.qty = qty;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
