package util.warehousing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import entity.product_inventory.ProductInventoryBatchDetail;
import play.libs.Json;

/**
 * 所有传入数据相关的工具，关键：传入参数必须是json格式
 * 
 * <ul>
 * 	<li>传入参数data,必须是json格式
 * 	<li>返回是否/解析data
 * </ul>
 *
 *json格式<br/>
 *<![CDATA[
 * {
 * 		dist_code : 0755,//接口code
 * 		data:{
 * 			memberID : xxx,		
 * 			memberEmail :　xxx,
 * 			groupID : xxx,
 * 			payload : xxx,
 * 			ltc : xxx,
 * 			stc : xxx,
 * 			currencyCode : xxx,
 * 			countryCode : xxx,
 * 			
 * 		}
 * }
 * ]]>
 * 
 * @author ye_ziran
 * @since 2015年11月2日 下午5:25:32
 */
public class DataUtil {
	
	private static ObjectMapper om = new ObjectMapper();
	
	static{
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	/**
	 * 将待转换字符串转换成目标实例，忽略无法识别的字段
	 * @param clz 目标类型
	 * @param json 待转换字符串
	 * @return 目标类型实例
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T fromJson(Class<T> clz,String json) throws JsonParseException, JsonMappingException, IOException{
		T t = om.readValue(json, clz);
		return t;
	}
	
	/**
	 * 是否登录
	 * 
	 * @param data
	 * @return
	 * @author ye_ziran
	 * @since 2015年11月2日 下午5:53:26
	 */
	@JsonGetter
	public static boolean isLogin(String data){
		JsonNode jsonNode = getData(data);
		String memberId = jsonNode.get("memberID").asText();
		return !StringUtils.isEmpty(memberId);
	}
	
	/**
	 * 根据属性名，返回属性值
	 * 
	 * @param data
	 * @param propName			属性名
	 * @return
	 * @author ye_ziran
	 * @since 2015年11月2日 下午6:11:41
	 */
	@JsonGetter
	public static String get(String data, String propName){
		JsonNode jsonNode = getData(data);
		return jsonNode.get(propName).asText();
	}
	
	/**
	 * TODO:data格式验证，格式见type注解
	 * 
	 * @param data		传入格式
	 * @return JsonNode 错误信息;err_code为0时，没有错误信息
	 * @author ye_ziran
	 * @since 2015年11月2日 下午6:14:57
	 */
	public static JsonNode valid(String data){
		Map<String, Object> mjson = new HashMap<String, Object>();
		mjson.put("err_code", "1");
		mjson.put("xxx", "This prop is required");
		return Json.toJson(mjson);
	}
	
	/**
	 * 统一从data取数据
	 * 
	 * @param data
	 * @return
	 * @author ye_ziran
	 * @since 2015年11月3日 下午5:43:21
	 */
	@JsonGetter
	public static JsonNode getData(String data){
		return Json.parse(data).get("data");
	}
	
	/**
	 * 格式化数据，返回统一的格式
	 * 
	 * @param obj			
	 * @param errMsgs		错误描述
	 * @return
	 * @author ye_ziran
	 * @since 2015年11月4日 上午11:04:38
	 */
	public static JsonNode formatData(Object obj, Object[] errMsgs){
		Map<String, Object> formatJson = new HashMap<>();
		int errCode = 0;
		StringBuilder errMsg = null;
		if(obj == null ){
			errCode  = 1;
		}
		if(errCode > 0){
			errMsg = new StringBuilder(2<<8);
			for (Object object : errMsgs) {
				errMsg.append(object.toString()).append(","); 
			}
		}
		formatJson.put("data", obj);
		formatJson.put("errMsg", errMsg);
		formatJson.put("errCode", errCode);
		return Json.toJson(formatJson);
	}
}
