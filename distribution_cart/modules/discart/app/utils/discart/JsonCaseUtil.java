package utils.discart;

import com.fasterxml.jackson.databind.JsonNode;

import services.base.utils.JsonFormatUtils;

/**
 * @author zbc 2017年3月22日 下午5:52:15
 */
public class JsonCaseUtil {
	
	public static boolean isJsonEmpty(JsonNode json, String key){
		return json.get(key) == null || "null".equals(json.get(key).asText())||"".equals(json.get(key).toString());
	} 
	public static boolean isNotJsonEmpty(JsonNode json, String key){
		return !isJsonEmpty(json, key);
	} 
	
	public static <T> T jsonCase(JsonNode json, String key, Class<T> t) {
		return json.get(key) != null && !("null".equals(json.get(key).asText()))
				? JsonFormatUtils.jsonToBean(json.get(key).toString(), t) : null;
	}

	public static String getStringValue(JsonNode node, String key) {
		return node.has(key) ? node.get(key).asText() != "null" ? node.get(key).asText() : "" : "";
	}
	
	public static Integer getIntegerValue(JsonNode node, String key) {
		return node.has(key) ? node.get(key).asInt() : 0;
	}

	public static Double getDoubleValue(JsonNode node, String key) {
		return node.has(key) ? node.get(key).asDouble() : 0;
	}
	
	
	public static boolean checkParam(JsonNode node, String[] params) {
		if (node != null) {
			for (String p : params) {
				if (isJsonEmpty(node,p)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
