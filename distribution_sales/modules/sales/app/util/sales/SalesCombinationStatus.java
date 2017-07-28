package util.sales;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 销售单合并发货状态
 */
public class SalesCombinationStatus {
	/**
	 * 待客服审核 1
	 */
	public static final int WAITING_AUDIT_CS = 1;
	/**
	 * 待财务审核 2
	 */
	public static final int WAITING_AUDIT_FINANCE = 2;
	/**
	 * 已关闭 3
	 */
	public static final int CLOSED = 3;
	/**
	 * 待发货 4
	 */
	public static final int WAITING_DELIVERY = 4;

	private static Map<Integer, String> statusMsgMap = Maps.newHashMap();
	static {
		statusMsgMap.put(1, "待客服审核");
		statusMsgMap.put(2, "待财务审核");
		statusMsgMap.put(3, "已关闭");
		statusMsgMap.put(4, "待发货");
	}
	
	public static String getStatusMsg(int status) {
		return statusMsgMap.get(status);
	}
}
