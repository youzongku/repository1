package util.sales;

import java.util.Map;

public class ParametersUtil {
	
	public static String getStringParam(Map<String, String[]> node, String field,String d) {
		String[] values = node.get(field);
		return values != null && values.length > 0 ? values[0] : d;
	}
	
	public static Integer getIntegerParam(Map<String, String[]> node, String field,Integer d) {
		String[] values = node.get(field);
		if(values == null || values.length <= 0){
			return d;
		}
		return Integer.parseInt(values[0]);
	}
	
	public static boolean getBooleanParam(Map<String, String[]> node, String field, boolean d) {
		String[] values = node.get(field);
		if(values == null || values.length <= 0){
			return d;
		}
		return Boolean.valueOf(values[0]);
	}
}
