package utils.purchase;

/**
 * PurchaseAudit的状态
 * @author huangjc
 * @date 2016年12月1日
 */
public class PurchaseAuditStatus {
	
	// '1、待审核   2、审核通过 3、审核不通过';
	
	/**1、待审核*/
	public static final int WAINTING_AUDIT = 1;
	/**2、审核通过*/
	public static final int AUDIT_PASSED = 2;
	/**3、审核不通过*/
	public static final int AUDIT_NOT_PASSED = 3;

}
