package constant;

/**
 * @author longhuashen
 * @since 2017/6/19
 *
 * 定义一些常量
 */
public class InventoryConstant {

    /**********************************销售单出库的返回值***************************************/
    //正常出库扣减
    public static final int SALE_ORDER_SUCCESS = 1;

    //缺货
    public static final int SALE_ORDER_OUT_OF_STOCK = 2;

    //云仓库存也不足
    public static final int SALE_ORDER_OUT_OF_CLOUD_STOCK = 3;

    //系统异常
    public static final int SALE_ORDER_ERROR = 4;

    //直接去锁云仓成功
    public static final int SALE_ORDER_LOCK_CLOUD_SUCCESS = 5;


    /**********************************保存的订单状态***************************************/

    //待付款
    public static final int INVENTORY_ORDER_STATUS_PENDING_PAYMENT = 0;

    //采购单已完成
    public static final int INVENTORY_ORDER_STATUS_PURCHASE_ORDER_SUCCESS = 1;

    //采购单已取消
    public static final int INVENTORY_ORDER_STATUS_PURCHASE_ORDER_CANCLE = 2;

    //销售单待采购
    public static final int INVENTORY_ORDER_STATUS_SALE_ORDER_TO_PURCHASE = 3;

    //销售单库存扣减成功
    public static final int INVENTORY_ORDER_STATUS_SUCCESS = 4;

    //销售单已取消
    public static final int INVENTORY_ORDER_STATUS_SALE_ORDER_CANCLE = 5;
}
