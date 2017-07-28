package util.marketing.promotion;

import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonCaseUtil {
	
	public static Date jsonToDate(JsonNode json){
		return json != null?new Date(json.asLong()):null;
	}
	
	public static boolean isJsonEmpty(JsonNode json){
		return json == null || "null".equals(json.asText()) || "".equals(json.asText());
	}
	public static String jsonToString(JsonNode json){
		return isJsonEmpty(json)?null:json.asText();
	}
	public static Integer jsonToInteger(JsonNode json){
		return isJsonEmpty(json)?null:json.asInt();
	}
	public static Double jsonToDouble(JsonNode json){
		return isJsonEmpty(json)?null:json.asDouble();
	}
	
	public static Boolean jsonToBoolean(JsonNode json){
		return isJsonEmpty(json)?null:json.asBoolean();
	}
}
