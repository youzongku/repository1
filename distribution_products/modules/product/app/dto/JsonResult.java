package dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 统一json格式返回对象
 * <p>可以使用builder模式
 * <p>或JsonResult.newIns()
 * <p>可直接作为返回值，也可以将返回值设为String,再将该对象转换为jsonString 
 * @author ouyangyaxiong
 * 2016年11月28日
 *
 * @param <T>
 */
@JsonSerialize()
@ApiModel
public class JsonResult<T> {

	@ApiModelProperty("处理结果")
	private Boolean result = false;
	
	/**
	 * 成功或失败的消息
	 */
	@ApiModelProperty("成功或失败的消息")
	private String msg;
	
	/**
	 * 错误代码
	 */
	@ApiModelProperty("错误代码")
	private Integer errCode;
	
	/**
	 * 多组错误消息,{'err1':'msg1','err2':'msg2',...}
	 */
	@ApiModelProperty("多个错误描述")
	private Map<Object, Object> errors /*= new HashMap<Object, Object>()*/;
	
	/**
	 * 数据
	 */
	@ApiModelProperty("数据")
	private T data;

	public static class JsonResultBuilder<T> {
		private Boolean result = false;
		private String msg;//成功或失败的消息，可以为空
		private Integer errCode;//错误代码，可空
		private Map<Object, Object> errors = new HashMap<Object, Object>();
		private T data;
		public JsonResultBuilder<T> result(Boolean result){
			this.result = result;
			return this;
		}
		public JsonResultBuilder<T> errCode(Integer errCode){
			this.errCode = errCode;
			return this;
		}
		public JsonResultBuilder<T> msg(String msg){
			this.msg = msg;
			return this;
		}
		public JsonResultBuilder<T> errors(Map<Object, Object> errors){
			this.errors = errors;
			return this;
		}
		public JsonResultBuilder<T> data(T data){
			this.data = data;
			return this;
		}
		public JsonResult<T> build(){
			return new JsonResult(this);
		}
	}
	
	protected JsonResult(JsonResultBuilder<T> builder) {
		this.result = builder.result;
		this.errCode = builder.errCode;
		this.errors = builder.errors;
		this.msg = builder.msg;
		this.data = builder.data;
	}
	
	protected JsonResult() {
	}

	/**
	 * 简化方式new一个新实例
	 * @return
	 * @author ouyangyaxiong
	 * 2016年11月28日 上午11:33:46
	 *
	 */
	public static <T> JsonResult<T> newIns(){
		return new JsonResult<T>();
	} 
	public JsonResult<T> result(Boolean result){
		this.result = result;
		return this;
	}
	public JsonResult<T> errCode(Integer errCode){
		this.errCode = errCode;
		return this;
	}
	public JsonResult<T> msg(String msg){
		this.msg = msg;
		return this;
	}
	public JsonResult<T> errors(Map<Object, Object> errors){
		this.errors = errors;
		return this;
	}
	public JsonResult<T> data(T data){
		this.data = data;
		return this;
	}
	
	public Boolean getResult() {
		return result;
	}

	public String getMsg() {
		return msg;
	}

	public Integer getErrCode() {
		return errCode;
	}

	public Map<Object, Object> getErrors() {
		return errors;
	}

	public T getData() {
		return data;
	}

	@Override
	public String toString() {
		return "JsonResult [result=" + result + ", msg=" + msg + ", errCode=" + errCode + ", errors=" + errors
				+ ", data=" + data + "]";
	}
}
