package utils.dismember;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import services.base.utils.JsonFormatUtils;

public class JsonCaseUtil {

	public static Date jsonToDate(JsonNode json) {
		return isJsonNotEmpty(json) ? new Date(json.asLong()) : null;
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
	
	public static boolean isJsonNotEmpty(JsonNode json){
		return !isJsonEmpty(json);
	}

	public static String jsonToString(JsonNode json) {
		return isJsonNotEmpty(json)?  json.asText() : null;
	}

	public static BigDecimal jsonToBigDecimal(JsonNode json) {
		return jsonToString(json) != null ? new BigDecimal(jsonToString(json)) : BigDecimal.ZERO;
	}

	public static Integer jsonToInteger(JsonNode json) {
		return isJsonNotEmpty(json) ? json.asInt() : null;
	}

	public static Boolean jsonToBoolean(JsonNode json,String key){
		return isJsonNotEmpty(json.get(key))?json.get(key).asBoolean():null;
	}
	
	public static Double jsonToDouble(JsonNode json) {
		return isJsonNotEmpty(json)? json.asDouble() : null;
	}

	public static <T> T jsonCase(JsonNode json, String key, Class<T> t) {
		return json.get(key) != null && !("null".equals(json.get(key).asText()))
				? JsonFormatUtils.jsonToBean(json.get(key).toString(), t) : null;
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
