package util.timer;
/**
 * 销售单状态
 * @author huangjc
 * @date 2016年12月20日
 */
public class SaleOrderStatus {
	// 待付款分为：待支付货款、待支付运费
	/**1待支付货款*/
	public static final int WAITING_PAY = 1;
	/**103待支付运费*/
	public static final int WAITING_PAY_BBC_POSTAGE = 103;

	// 待客户确认
	/**2待客户确认*/
	public static final int WAITING_CONFIRM_BY_CUSTOMER = 2;
	
	// 待审核分为：待客服确认、待财务确认、待二次支付
	/**3待客服确认*/
	public static final int WAITING_AUDIT_BY_CS = 3;
	/**11待财务确认*/
	public static final int WAITING_AUDIT_BY_FINANCE = 11;
	/**12待二次支付*/
	public static final int WAITING_PAY_SECONDLY = 12;
	
	// 待发货分为：待发货、已推送hk、已推送erp
	/**6待发货*/
	public static final int WAITING_DELIVERY_SIX = 6;
	/**7待发货*/
	public static final int WAITING_DELIVERY_SERVEN = 7;
	/**13已推送hk*/
	public static final int PUSHED_2_HK = 13;
	/**104已推送erp*/
	public static final int PUSHED_2_ERP = 104;
	
	// 待收货
	/**9待收货*/
	public static final int WAITING_RECEIVEMENT = 9;
	
	// 已完成
	/**106已完成*/
	public static final int WAITING_FINISHED = 106;
	
	// 订单关闭分为：审核不通过、用户关闭、客服关闭、erp关闭
	/**4审核不通过*/
	public static final int AUDIT_NOT_PASSED = 4;
	/**5用户关闭*/
	public static final int CLOSED_BY_CUSTOMER = 5;
	/**14客服关闭，其实就是审核不通过*/
//	public static final int CLOSED_BY_CS = 14;
	/**20erp关闭*/
	public static final int CLOSED_BY_ERP = 20;
	
	/**10已收货*/
	public static final int RECEIVED = 10;
}
