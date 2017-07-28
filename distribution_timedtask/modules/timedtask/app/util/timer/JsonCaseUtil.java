package util.timer;

import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;

public class JsonCaseUtil {
	
	public static Date jsonToDate(JsonNode json){
		return json != null?new Date(json.asLong()):null;
	}
	
	public static Date jsonStrToDate(JsonNode json,String format){
		Date date =  null;
		try {
			date = DateUtils.string2date(json.asText(), format);
		} catch (Exception e) {
			Logger.info("日期转换异常",e);
		}
		return date;
	}
	public static boolean isJsonEmpty(JsonNode json){
		return json == null || "null".equals(json.asText()) || "".equals(json.asText());
	}
	public static String jsonToString(JsonNode json){
		return json != null?("null".equals(json.asText())?null:json.asText()):null;
	}
	public static Integer jsonToInteger(JsonNode json){
		return json != null?json.asInt():null;
	}
	public static Double jsonToDouble(JsonNode json){
		return json != null?json.asDouble():null;
	}
}
