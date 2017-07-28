package dto.purchase;

import entity.purchase.PurchaseAudit;

import java.io.Serializable;

/**
 * Created by LSL on 2016/11/24.
 */
public class PurchaseAuditDto extends PurchaseAudit implements Serializable {

    private static final long serialVersionUID = -7695069530923011340L;

    private ViewPurchaseOrder vpOrder;

    public ViewPurchaseOrder getVpOrder() {
        return vpOrder;
    }

    public void setVpOrder(ViewPurchaseOrder vpOrder) {
        this.vpOrder = vpOrder;
    }
}
