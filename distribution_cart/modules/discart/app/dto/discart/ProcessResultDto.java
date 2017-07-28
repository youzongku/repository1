package dto.discart;

import java.util.List;
import java.util.Map;

/**
 * 购物车模块通用返回值
 * Created by duyuntao on 2017/5/27.
 */
public class ProcessResultDto {

    /**
     * 必选值，返回结果校验使用
     */
    private Boolean success;

    /**
     * 可选值，提示性信息
     */
    private String message;

    /**
     * 必选值，处理之后的返回结果
     */
    private Map<?,?> datas;

    public ProcessResultDto() {
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<?, ?> getDatas() {
        return datas;
    }

    public void setDatas(Map<?, ?> datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "ProcessResultDto{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", datas=" + datas +
                '}';
    }
}
