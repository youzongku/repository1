package utils.response;

/**
 * 响应对象
 * 
 * @author huangjc
 * @since 2016年6月15日 下午3:01:33
 */
public abstract class BaseResponseResult {
	public static final int SUCCESS_CODE = 100;

	private int code = SUCCESS_CODE;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
