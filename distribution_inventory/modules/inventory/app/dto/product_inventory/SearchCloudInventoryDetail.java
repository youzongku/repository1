package dto.product_inventory;

/**
 * @author longhuashen
 * @since 2016/12/27
 */
public class SearchCloudInventoryDetail {

    private String sku;

    private Integer num;

    private String msg;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
