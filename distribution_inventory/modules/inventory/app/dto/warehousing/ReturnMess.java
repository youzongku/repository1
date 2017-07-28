package dto.warehousing;

/**
 * Created by luwj on 2016/1/6.
 */
public class ReturnMess {

    private String errorCode;//错误代码

    private String errorInfo;//错误信息

    public ReturnMess() {
    }

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
