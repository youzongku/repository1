package utils.purchase;
/**
 * 退货单状态：待审核1；审核通过2；审核未通过3；取消4
 * @author huangjc
 * @date 2017年2月20日
 */
public class ReturnOrderStatus {
	/**
	 * 待审核1
	 */
	public static final Integer AUDIT_WAITING = 1;
	/**
	 * 审核通过2
	 */
	public static final Integer AUDIT_PASSED = 2;
	/**
	 * 审核未通过3
	 */
	public static final Integer AUDIT_NOT_PASSED = 3;
	/**
	 * 取消4
	 */
	public static final Integer CANCELED = 4;
}
