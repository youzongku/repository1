package dto.product_inventory;

/**
 * @author longhuashen
 * @since 2016/12/26
 */
public class CancleSaleOrderResult {

     private boolean result;

     private String msg;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
