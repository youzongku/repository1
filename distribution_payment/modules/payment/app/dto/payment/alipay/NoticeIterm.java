package dto.payment.alipay;

/**
 * 支付宝回调参数实体
 * Created by luwj on 2015/12/28.
 */
public class NoticeIterm {

    private String[] notify_id;//通知 ID

    private String[] notify_type;//通知类型

    private String[] notify_time;//通知时间

    private String[] sign;//签名

    private String[] sign_type;//签名方式

    private String[] out_trade_no;//外部交易号

    private String[] trade_status;//交易状态

    private String[] trade_no;//交易号

    private String[] currency;//币种

    private String[] total_fee;//交易金额

    public String[] getNotify_id() {
        return notify_id;
    }

    public void setNotify_id(String[] notify_id) {
        this.notify_id = notify_id;
    }

    public String getNotify_id_val(){
        return getNotify_id() == null ? "" : getNotify_id()[0];
    }

    public String[] getNotify_type() {
        return notify_type;
    }

    public void setNotify_type(String[] notify_type) {
        this.notify_type = notify_type;
    }

    public String getNotify_type_val(){
        return getNotify_type() == null ? "" : getNotify_type()[0];
    }

    public String[] getNotify_time() {
        return notify_time;
    }

    public void setNotify_time(String[] notify_time) {
        this.notify_time = notify_time;
    }

    public String getNotify_time_val(){
        return getNotify_time() == null ? "" : getNotify_time()[0];
    }

    public String[] getSign() {
        return sign;
    }

    public void setSign(String[] sign) {
        this.sign = sign;
    }

    public String getSign_val(){
        return getSign() == null ? "" : getSign()[0];
    }

    public String[] getSign_type() {
        return sign_type;
    }

    public void setSign_type(String[] sign_type) {
        this.sign_type = sign_type;
    }

    public String getSign_type_val(){
        return getSign_type() == null ? "" : getSign_type()[0];
    }

    public String[] getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String[] out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getOut_trade_no_val(){
        return getOut_trade_no() == null ? "" : getOut_trade_no()[0];
    }

    public String[] getTrade_status() {
        return trade_status;
    }

    public void setTrade_status(String[] trade_status) {
        this.trade_status = trade_status;
    }

    public String getTrade_status_val(){
        return getTrade_status() == null ? "" : getTrade_status()[0];
    }

    public String[] getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String[] trade_no) {
        this.trade_no = trade_no;
    }

    public String getTrade_no_val(){
        return getTrade_no() == null ? "" : getTrade_no()[0];
    }

    public String[] getCurrency() {
        return currency;
    }

    public void setCurrency(String[] currency) {
        this.currency = currency;
    }

    public String getCurrency_val(){
        return getCurrency() == null ? "" : getCurrency()[0];
    }

    public String[] getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String[] total_fee) {
        this.total_fee = total_fee;
    }

    public String getTotal_fee_val(){
        return getTotal_fee()==null?"":getTotal_fee()[0];
    }
}
