package entity.timer;

import java.util.List;

/**
 * @author longhuashen
 * @since 2016/12/27
 */
public class ErpStockInResult {

    private boolean result;

    private List<ErpStockInResultDetail> details;

    private String msg;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<ErpStockInResultDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ErpStockInResultDetail> details) {
        this.details = details;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
