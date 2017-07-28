package utils.response;

/**
 * 错误的返回
 * 
 * @author huangjc
 * @date 2016年8月26日
 */
public class ErrorResponseResult extends BaseResponseResult {

	private String msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
