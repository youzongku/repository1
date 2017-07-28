package dto.payment;

/**
 * Created by luwj on 2015/12/22.
 */
public class ReturnMess {
    /**
     * 错误编码 0：成功，1：失败或异常，2：未登录
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorInfo;

    public ReturnMess(){}

    public ReturnMess(String errorCode, String errorInfo) {
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
