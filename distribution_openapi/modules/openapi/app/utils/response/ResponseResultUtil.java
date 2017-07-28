package utils.response;

import play.libs.Json;
import play.mvc.Result;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 返回值工具类
 * 
 * @author huangjc
 * @date 2016年8月26日
 */
public final class ResponseResultUtil {
	/**
	 * 创建一个成功的返回值
	 * 
	 * @param result
	 *            可以是查询的数据、操作成功后的信息等
	 * @return
	 */
	public static SuccessResponseResult newSuccessResponseResult(Object result) {
		SuccessResponseResult responseResult = new SuccessResponseResult();
		responseResult.setResponse(result);
		return responseResult;
	}

	/**
	 * 创建一个成功的返回值，并转成一个json
	 * 
	 * @param result
	 *            可以是查询的数据、操作成功后的信息等
	 * @return
	 */
	public static Result newSuccessJson(Object result) {
		return play.mvc.Results.ok(Json.toJson(ResponseResultUtil
				.newSuccessResponseResult(result)));
	}

	/**
	 * 创建一个成功的返回值，并转成一个json
	 * 
	 * @param result json格式的字符串
	 * @return
	 */
	public static Result newSuccessJson(String jsonString) {
		ObjectNode newObject = Json.newObject();
		newObject.put("code", BaseResponseResult.SUCCESS_CODE);
		if(jsonString==null || jsonString.length()==0){
			newObject.put("response", "");
		}else{
			newObject.put("response", Json.parse(jsonString));
		}
		return play.mvc.Results.ok(newObject);
	}

	/**
	 * 创建一个失败的返回值
	 * 
	 * @param errCode
	 *            错误码
	 * @param errMsg
	 *            错误信息
	 * @return
	 */
	public static BaseResponseResult newErrorResponseResult(int code, String msg) {
		ErrorResponseResult responseResult = new ErrorResponseResult();
		responseResult.setCode(code);
		responseResult.setMsg(msg);
		return responseResult;
	}

	/**
	 * 创建一个失败的返回值，并转成一个json
	 * 
	 * @param errCode
	 *            错误码
	 * @param errMsg
	 *            错误信息
	 * @return
	 */
	public static Result newErrorJson(int errCode, String errMsg) {
		return play.mvc.Results.ok(Json.toJson(ResponseResultUtil
				.newErrorResponseResult(errCode, errMsg)));
	}
}
