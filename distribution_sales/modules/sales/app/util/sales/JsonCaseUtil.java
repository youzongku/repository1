package util.sales;

import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;

public class JsonCaseUtil {

	/**
	 * 获取node的内容
	 * @param node
	 * @param key
	 * @return 没有此key，返回空字符串
	 */
	public static String getStringValue(JsonNode node, String key) {
		return null != node && node.has(key) ? (node.get(key).asText() != "null" ? node.get(key).asText() : "") : "";
	}

	/**
	 * 获取node的内容
	 * @param node
	 * @param key
	 * @return 没有此key，返回0
	 */
	public static Integer getIntegerValue(JsonNode node, String key) {
		return null != node && node.has(key) ? node.get(key).asInt() : 0;
	}

	/**
	 * 获取值
	 * @param money
	 * @return money为null，返回0
	 */
	public static Double getValue(Double money) {
		return money != null ? money : 0;
	}

	/**
	 * 获取node的内容
	 * @param node
	 * @param key
	 * @return 没有此key，返回0
	 */
	public static Double getDoubleValue(JsonNode node, String key) {
		return node.has(key) ? node.get(key).asDouble() : 0;
	}

	/**
	 * 获取node的内容
	 * @param json
	 * @return json为空，返回null
	 */
	public static Date jsonToDate(JsonNode json) {
		return json != null ? new Date(json.asLong()) : null;
	}

	public static Date jsonStrToDate(JsonNode json, String format) {
		Date date = null;
		try {
			date = DateUtils.string2date(json.asText(), format);
		} catch (Exception e) {
			Logger.info("日期转换异常", e);
		}
		return date;
	}

	public static boolean isJsonEmpty(JsonNode json) {
		return json == null || "null".equals(json.asText()) || "".equals(json.toString());
	}

	public static Boolean jsonToBoolean(JsonNode json){
		return isJsonEmpty(json)?null:json.asBoolean();
	}
	public static String jsonToString(JsonNode json) {
		return isJsonEmpty(json) ? null : json.asText();
	}

	public static Integer jsonToInteger(JsonNode json) {
		return isJsonEmpty(json) ? null: json.asInt() ;
	}

	public static Double jsonToDouble(JsonNode json) {
		return isJsonEmpty(json) ? null:json.asDouble() ;
	}
	
	public static  Short JsonToShort(JsonNode json){
		return isJsonEmpty(json) ? null:Short.valueOf(json.asText()) ;
	}
	
	/**
	 * 校验参数完整性方法
	 * 
	 * @author zbc
	 * @since 2017年2月20日 上午9:43:45
	 */
	public static boolean checkParam(JsonNode node, String... fields) {
		if (node != null) {
			for (String p : fields) {
				if (isJsonEmpty(node.get(p))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
