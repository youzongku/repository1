package dto.product_inventory;

import java.util.List;

/**
 * @author longhuashen
 * @since 2016/12/16
 */
public class CreateSaleOrderResult {

    //1 发货成功 2 缺货 3 云仓锁不了 4 系统错误
    private int type;

    private List<ShipingDto> successOrLocks;

    private List<PurchaseDto> purchases;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<ShipingDto> getSuccessOrLocks() {
        return successOrLocks;
    }

    public void setSuccessOrLocks(List<ShipingDto> successOrLocks) {
        this.successOrLocks = successOrLocks;
    }

    public List<PurchaseDto> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<PurchaseDto> purchases) {
        this.purchases = purchases;
    }
}
