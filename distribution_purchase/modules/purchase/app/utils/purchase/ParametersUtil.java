package utils.purchase;

import java.math.BigDecimal;
import java.util.Map;

public class ParametersUtil {
	
	public static BigDecimal setBigDecimal(String[] strings) {
		return strings != null && strings.length > 0 ? new BigDecimal(
				strings[0]) : null;
	}
	
	public static String getString(String[] strings) {
		return strings != null && strings.length > 0 ? strings[0] : "";
	}
	
	public static String getStringParam(Map<String, String[]> node, String field, String d) {
		String[] values = node.get(field);
		return values != null && values.length > 0 ? values[0] : d;
	}
	
	public static Integer getIntegerParam(Map<String, String[]> node, String field, Integer d) {
		String[] values = node.get(field);
		if(values == null || values.length <= 0){
			return d;
		}
		return Integer.parseInt(values[0]);
	}
	
	public static Boolean getBooleanParam(Map<String, String[]> node, String field, Boolean b) {
		String[] values = node.get(field);
		if(values == null || values.length <= 0){
			return b;
		}
		return Boolean.valueOf(values[0]);
	}
	
	/**
	 * 如果b为null，则返回默认值0
	 * @param b
	 * @return
	 */
	public static double getDoubleValue(Double b) {
		return b == null ? 0 : b;
	}
}
