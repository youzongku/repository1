package util.sales;

import java.util.Map;

import com.google.common.collect.Maps;

public final class DismemberModeTypeUtil {
	private DismemberModeTypeUtil() {
	}

	/**
	 * 分销商类型
	 */
	public static Map<Integer, String> DISTRIBUTIONTYPE = Maps.newHashMap();
	/**
	 * 分销商模式
	 */
	public static Map<Integer, String> DISTRIBUTIONMODE = Maps.newHashMap();

	static {
		DISTRIBUTIONTYPE.put(1, "普通分销商");
		DISTRIBUTIONTYPE.put(2, "合营分销商");
		DISTRIBUTIONTYPE.put(3, "内部分销商");

		DISTRIBUTIONMODE.put(1, "电商");
		DISTRIBUTIONMODE.put(2, "经销商");
		DISTRIBUTIONMODE.put(3, "KA直营");
		DISTRIBUTIONMODE.put(4, "进口专营");
		DISTRIBUTIONMODE.put(5, "VIP");
	}
	
	/**
	 * 分销商类型
	 */
	public static String getTypeName(int type) {
		return DISTRIBUTIONTYPE.get(type);
	}
	
	/**
	 * 分销商模式
	 */
	public static String getModeName(int mode) {
		return DISTRIBUTIONMODE.get(mode);
	}
}
