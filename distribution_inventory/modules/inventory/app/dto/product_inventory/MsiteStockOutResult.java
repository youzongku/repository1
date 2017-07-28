package dto.product_inventory;

import java.util.Map;

/**
 * @author longhuashen
 * @since 2016/12/27
 */
public class MsiteStockOutResult {

    //1 正常 2 数据异常 3 系统异常
    private int type;

    private Map details;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Map getDetails() {
        return details;
    }

    public void setDetails(Map details) {
        this.details = details;
    }
}
