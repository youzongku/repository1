package forms;

/**
 * @author ye_ziran
 * @since 2016/3/24 17:50
 */
public class ReturnMessageForm {
    private boolean res;
    private String msg;
    private String data;

    public boolean isRes() {
        return res;
    }

    public void setRes(boolean res) {
        this.res = res;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
