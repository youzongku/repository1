package entity.timer;

import java.util.List;

/**
 * @author longhuashen
 * @since 2016/12/19
 */
public class SearchCloudInventoryResult {

    //1 正常 2 系统异常
    private int type;

    private List<SearchCloudInventoryDetail> details;

    private String msg;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SearchCloudInventoryDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SearchCloudInventoryDetail> details) {
        this.details = details;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
