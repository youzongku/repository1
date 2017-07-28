package dto.openapi.enums;


/**
 * @author zbc
 * 2016年8月27日 上午9:17:28
 */
public enum PurchaseOrderFlag {
	
	WAIT_PAY(0),

	CANCEL(2),

    PAY_SUCCESS(1),

    INVALID(3);
	
    private int type;

    private static final long serialVersionUID = 1L;

    private PurchaseOrderFlag(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
