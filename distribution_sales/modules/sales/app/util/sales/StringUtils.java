package util.sales;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public class StringUtils {
    /**
     * @param obj
     * @param isNull(true:对象为空时返回null false:对象为空时返回"")
     * @return
     * @author hanfs
     */
    public static String getString(Object obj, boolean isNull) {
        if (obj != null) {
            return obj.toString();
        }
        if (isNull) {
            return null;
        }
        return "";
    }

    public static boolean isBlankOrNull(Object obj) {
        if (obj == null || "".equals(obj.toString())) {
            return true;
        }
        return false;
    }

    public static boolean isNotBlankOrNull(Object obj) {
        return !isBlankOrNull(obj);
    }

    /**
     * 1、obj不为null，且obj的字符串内容不为""，就返回内容obj的字符串内容；<br>
     * 2、如果isBlank为true，那么就返回null，为false就返回""
     * @param obj
     * @param isBlank
     * @return
     */
    public static String getStringBlank(Object obj, boolean isBlank) {
        if (obj != null && !"".equals(obj.toString())) {
            return obj.toString();
        }
        if (isBlank) {
            return null;
        }
        return "";
    }
    
    public static String getStringValue(JsonNode node, String key) {
		return node.has(key) ? node.get(key).asText() != "null" ? node.get(key).asText() : "" : "";
	}
    
    public static String getStringParam(Map<String, String[]> node, String field,String d) {
		String[] values = node.get(field);
		return values != null && values.length > 0 && isNotBlankOrNull(values[0]) ? values[0] : d;
	}
    
	public static String getKey(Object... obj) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < obj.length; i++) {
			if (i > 0) {
				builder.append("_");
			}
			builder.append(obj[i] != null ? obj[i] : "--");
		}
		return builder.toString();
	}
}
