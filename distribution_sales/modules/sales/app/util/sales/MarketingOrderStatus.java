package util.sales;
/**
 * 营销单状态
 * <p>录入->待初审状态1
 * 			初审通过->复审状态2
						复审通过->审核通过3
						复审不通过->审核不通过4
			初审不通过->审核不通过4
 * @author huangjc
 * @date 2016年12月27日
 */
public final class MarketingOrderStatus {
	private MarketingOrderStatus(){}
	/**
	 * 1待初审
	 */
	public static final int WAITING_AUDIT_FIRSTLY = 1;
	/**
	 * 2待复审
	 */
	public static final int WAITING_AUDIT_SECONDLY = 2;
	/**
	 * 3审核通过（2待复审通过后就是3审核通过）
	 */
	public static final int AUDIT_PASSED = 3;
	/**
	 * 4审核不通过（1待初审和2待复审，审核不通过就是4审核不通过，要查看是1待初审不通过，还是2待复审不通过，就要看日志了）
	 */
	public static final int AUDIT_NOT_PASSED = 4;
}
