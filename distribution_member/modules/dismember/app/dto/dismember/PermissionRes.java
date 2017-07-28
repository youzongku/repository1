package dto.dismember;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModelProperty;


public class PermissionRes<T> implements Serializable {
	
	private static final long serialVersionUID = 5566639758036530846L;
	
	@ApiModelProperty("是否成功:100为成功，其他失败")
	private Integer code = 100;//是否成功
	
	@ApiModelProperty("提示信息")
	private String msg;//提示信息
	
	@ApiModelProperty("返回对象")
	private T obj;//返回对象

	public T getObj() {
		return obj;
	}
	public void setObj(T obj) {
		this.obj = obj;
	}
	public PermissionRes(Integer code, String msg,T obj) {
		super();
		this.code = code;
		this.msg = msg;
		this.obj = obj;
	}
	public PermissionRes() {
		
	}
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
