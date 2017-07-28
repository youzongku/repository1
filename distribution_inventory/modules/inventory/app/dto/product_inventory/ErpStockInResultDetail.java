package dto.product_inventory;

/**
 * @author longhuashen
 * @since 2016/12/27
 */
public class ErpStockInResultDetail {

    private String sku;

    private boolean result;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
