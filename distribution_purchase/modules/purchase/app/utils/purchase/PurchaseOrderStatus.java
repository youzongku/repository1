package utils.purchase;
/**
 * 采购单状态常量：0待付款，1已付款，2已取消，3已失效，4待审核, 5审核不通过
 * @author huangjc
 * @date 2016年12月1日
 */
public class PurchaseOrderStatus {
	/**0、代付款*/
	public static final int WAITING_4_PAYMENT = 0;
	/**1、已完成（财务审核通过）-原先的已付款*/
	public static final int FINISHED = 1;
	/**2、已取消*/
	public static final int CANCEL = 2;
	/**3、已失效*/
	public static final int INVALIDATED = 3;
	/**4、待客服审核，确认通过就到了6（待财务审核）*/
	public static final int AUDIT_WAINTING_CUSTOMER_SERVICE = 4;
	/**6、此状态有两个含义（客服审核通过&待财务审核）*/
	public static final int AUDIT_WAINTING_FINANCE = 6;
	
}
