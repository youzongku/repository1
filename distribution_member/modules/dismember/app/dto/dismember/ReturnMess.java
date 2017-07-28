package dto.dismember;

/**
 * Created by Administrator on 2015/12/11.
 */
public class ReturnMess {
    private String errorCode;

    private String errorInfo;

    public ReturnMess(){

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
