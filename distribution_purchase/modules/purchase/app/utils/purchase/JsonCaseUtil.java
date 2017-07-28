package utils.purchase;

import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;

public class JsonCaseUtil {
	
	public static Date jsonToDate(JsonNode json) {
		return json != null ? new Date(json.asLong()) : null;
	}

	public static boolean isJsonEmpty(JsonNode json) {
		return json == null || "null".equals(json.asText()) || "".equals(json.toString());
	}

	public static String jsonToString(JsonNode json) {
		return isJsonEmpty(json)? null : json.asText();
	}
	
	public static String getStringValue(JsonNode node, String key) {
		return node.has(key) && node.get(key) != null ? 
				("null".equals(node.get(key).asText()) ? null : node.get(key).asText().trim()) : null;
	}

	/**
	 * 
	 * @param json
	 * @return json为null，返回值为null
	 */
	public static Integer jsonToInteger(JsonNode json) {
		return isJsonEmpty(json)  ? null : json.asInt();
	}
	
	/**
	 * json为null，返回值为null
	 * @param node
	 * @param key
	 * @return
	 */
	public static Integer geIntegerValue(JsonNode node, String key) {
		return (node.has(key) && !Strings.isNullOrEmpty(node.get(key).asText())) ? node.get(key).asInt() : null;
	}

	public static Boolean jsonToBoolean(JsonNode json) {
		return isJsonEmpty(json) ? null : json.asBoolean();
	}

	public static Double jsonToDouble(JsonNode json) {
		return isJsonEmpty(json) ? null :json.asDouble();
	}
	
	public static Double getDoubleValue(JsonNode node, String key) {
		return node.get(key) != null ? node.get(key).asDouble() : null;
	}
	
	/**
	 * 校验参数完整性方法
	 * 
	 * @author zbc
	 * @since 2017年2月20日 上午9:43:45
	 */
	public static boolean checkParam(JsonNode node, String[] params) {
		if (node != null) {
			for (String p : params) {
				if (isJsonEmpty(node.get(p))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
