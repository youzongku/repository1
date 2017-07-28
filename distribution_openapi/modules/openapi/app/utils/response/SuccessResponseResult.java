package utils.response;

/**
 * 成功的返回
 * 
 * @author huangjc
 * @date 2016年8月26日
 */
public class SuccessResponseResult extends BaseResponseResult {
	private Object response;

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}

}
