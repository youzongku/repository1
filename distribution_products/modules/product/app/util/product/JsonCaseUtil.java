package util.product;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;

public class JsonCaseUtil {

	public static Date jsonToDate(JsonNode json) {
		return json != null ? new Date(json.asLong()) : null;
	}

	public static Date jsonStrToDate(JsonNode json, String format) {
		if (null == json) {
			return null;
		}
		Date date = null;
		try {
			if (StringUtils.isNotEmpty(json.asText())) {
				date = DateUtils.string2date(json.asText(), format);
			}
		} catch (Exception e) {
			Logger.info("日期转换异常", e);
		}
		return date;
	}

	public static boolean isJsonEmpty(JsonNode json) {
		return json == null || "null".equals(json.asText()) || "".equals(json.asText());
	}

	public static String jsonToString(JsonNode json) {
		return json != null ? ("null".equals(json.asText()) ? null : json.asText()) : null;
	}

	public static Integer jsonToInteger(JsonNode json) {
		return !isJsonEmpty(json)? json.asInt() : null;
	}
	public static Double jsonToDouble(JsonNode json) {
		return json != null ? json.asDouble() : null;
	}

	public static String getString(JsonNode node, String field, String def) {
		return node != null && node.has(field) && StringUtils.isNotEmpty(node.get(field).asText())
				? node.get(field).asText() : def;
	}
	
	public static String getString(String[] strings) {
		return strings != null && strings.length > 0 ? "".equals(strings[0]) ? null : strings[0] : null;
	}
	
	public static Integer getInteger(String[] strings) {
		return strings != null && strings.length > 0 ? "".equals(strings[0]) ? null : Integer.valueOf(strings[0]) : null;
	}
	
	/**
	 * 校验参数完整性方法
	 * @author zbc
	 * @since 2017年2月20日 上午9:43:45
	 */
	public static boolean checkParam(JsonNode node, String[] params) {
		if (node != null) {
			for (String p : params) {
				if (node.get(p) == null) {
					return false;
				}
			}
			return true;
		} 
		return false;
	}
	
}
