package utils.inventory;

import com.fasterxml.jackson.databind.JsonNode;

import services.base.utils.JsonFormatUtils;

/**
 * @author zbc
 * 2017年3月21日 下午4:26:19
 */
public class JsonCaseUtil {
	public static <T> T jsonCase(JsonNode json,String key,Class<T> t){
		return isNotEmpty(json,key)?
				JsonFormatUtils.jsonToBean(json.get(key).toString(), t):null; 
	}
	public static boolean isNotEmpty(JsonNode json,String key){
		return json.get(key)!= null&&!("null".equals(json.get(key).asText()))&&!("".equals(json.get(key).asText()));
	}
}
