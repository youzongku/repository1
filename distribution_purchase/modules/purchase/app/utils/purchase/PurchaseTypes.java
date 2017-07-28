package utils.purchase;
/**
 * 采购单类型常量：1：正常采购单；2：缺货采购单
 * @author huangjc
 * @date 2016年12月14日
 */
public final class PurchaseTypes {
	private PurchaseTypes() {
	}

	/**
	 * 1、正常采购单
	 */
	public static final int PURCHASE_ORDER_NORMAL = 1;
	/**
	 * 2、缺货采购单
	 */
	public static final int PURCHASE_ORDER_STOCKOUT = 2;
}
