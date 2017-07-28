package dto.payment.alipay;

/**
 * 支付参数实体
 * Created by luwj on 2015/12/22.
 */
public class ReqParam {

    private String order_id;//订单id

    private String vhost;//域名

    private String service;//接口名称

    private String partner;//境外商户

    private String notify_url;//通知 URL

    private String return_url;//返回 URL

    private String sign;//签名

    private String sign_type;//签名方式

    private String subject;//商品名称

    private String _input_charset;//编码

    private String body;//商品描述

    private String out_trade_no;//外部交易号

    private String currency;//币种

    private String total_fee;//交易金额

    private String rmb_fee;//交易人民币金额

    private String supplier;//供货方

    private String order_gmt_create;//请求创建时间

    private String order_valid_time;//订单有效时间

    private String timeout_rule;//交易超时规则

    private String specified_pay_channel;//网银前置

    private String seller_id;//子商户 ID

    private String seller_name;//子商户名称

    private String seller_industry;//子商户行业
    
    private String postflag;
    
    private String payment_type;

    public String getPostflag() {
		return postflag;
	}

	public void setPostflag(String postflag) {
		this.postflag = postflag;
	}

	public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String get_input_charset() {
        return _input_charset;
    }

    public void set_input_charset(String _input_charset) {
        this._input_charset = _input_charset;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getRmb_fee() {
        return rmb_fee;
    }

    public void setRmb_fee(String rmb_fee) {
        this.rmb_fee = rmb_fee;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getOrder_gmt_create() {
        return order_gmt_create;
    }

    public void setOrder_gmt_create(String order_gmt_create) {
        this.order_gmt_create = order_gmt_create;
    }

    public String getOrder_valid_time() {
        return order_valid_time;
    }

    public void setOrder_valid_time(String order_valid_time) {
        this.order_valid_time = order_valid_time;
    }

    public String getTimeout_rule() {
        return timeout_rule;
    }

    public void setTimeout_rule(String timeout_rule) {
        this.timeout_rule = timeout_rule;
    }

    public String getSpecified_pay_channel() {
        return specified_pay_channel;
    }

    public void setSpecified_pay_channel(String specified_pay_channel) {
        this.specified_pay_channel = specified_pay_channel;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getSeller_industry() {
        return seller_industry;
    }

    public void setSeller_industry(String seller_industry) {
        this.seller_industry = seller_industry;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }
}
