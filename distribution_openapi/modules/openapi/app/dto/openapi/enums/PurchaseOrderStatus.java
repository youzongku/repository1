package dto.openapi.enums;

import java.io.Serializable;

/**
 * Created by luwj on 2015/12/9.
 */
public enum PurchaseOrderStatus implements Serializable {

    待付款(0),

    已付款(1),

    已取消(2),

    已失效(3),
    
    待审核(4),
	
	审核不通过(5);

    private int type;

    private static final long serialVersionUID = 1L;

    private PurchaseOrderStatus(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
