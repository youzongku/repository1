package dto.marketing.promotion;

import java.io.Serializable;

public class PermissionRes implements Serializable {
	
	private static final long serialVersionUID = 5566639758036530846L;
	
	private Integer code = 100;//是否成功
	
	private String msg;//提示信息
	
	private Object obj;//返回对象

	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	public PermissionRes(Integer code, String msg,Object obj) {
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
