package util.marketing.promotion;

/**
 * 响应对象
 * 
 * @author huangjc
 * @since 2016年6月15日 下午3:01:33
 */
public class ResponseObject {
	// 如果为false，则设置errorMessage；为true，则设置resultObject
	private boolean success;
	private Object resultObject;
	private String errorMessage;
	
	/**
	 * 构造一个错误响应对象
	 * 
	 * @param errorMessage
	 * @return
	 */
	public static ResponseObject newErrorResponseObject(String errorMessage) {
		ResponseObject res = new ResponseObject();
		res.setSuccess(false);
		res.setErrorMessage(errorMessage);
		return res;
	}

	/**
	 * 构造一个成功响应对象
	 * 
	 * @param resultObject
	 * @return
	 */
	public static ResponseObject newSuccessResponseObject(Object resultObject) {
		ResponseObject res = new ResponseObject();
		res.setSuccess(true);
		res.setResultObject(resultObject);
		return res;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Object getResultObject() {
		return resultObject;
	}

	public void setResultObject(Object resultObject) {
		this.resultObject = resultObject;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
