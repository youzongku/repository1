package entity.payment.wechat.xml;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import javax.xml.bind.annotation.*;

/**
 * 微信下单报文
 * @author luwj
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(	name = "xml", 
			propOrder = { 	
				"appid", "attach", "body", "deviceInfo", "mchId", 
				"nonceStr", "notifyUrl", "outTradeNo", "spbillCreateIp", 
				"totalFee", "tradeType", "productId", "sign" 
			}, 
			namespace = "WOrderXml"
		)
@XmlRootElement(name = "xml")
public class WOrderXml{

	/**
	 * 公众账号ID
	 */
	@XmlElement(name = "appid")
	@XmlCDATA
	private String appid;
	
	/**
	 * 备用字段
	 */
	@XmlElement(name = "attach")
	@XmlCDATA
	private String attach;
	
	/**
	 * 商品描述
	 */
	@XmlElement(name = "body")
	@XmlCDATA
	private String body;
	
	/**
	 * 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
	 */
	@XmlElement(name = "device_info")
	@XmlCDATA
	private String deviceInfo;
	
	/**
	 * 商户号
	 */
	@XmlElement(name = "mch_id")
	@XmlCDATA
	private String mchId;
	
	/**
	 * 随机字符串
	 */
	@XmlElement(name = "nonce_str")
	@XmlCDATA
	private String nonceStr;
	
	/**
	 * 通知地址
	 */
	@XmlElement(name = "notify_url")
	@XmlCDATA
	private String notifyUrl;
	
	/**
	 * 商品订单号
	 */
	@XmlElement(name = "out_trade_no")
	@XmlCDATA
	private String outTradeNo;
	
	/**
	 * 终端IP  订单生成的机器 IP （需修改）
	 */
	@XmlElement(name = "spbill_create_ip")
	@XmlCDATA
	private String spbillCreateIp;
	
	/**
	 * 总金额 单位为分 不带小数点
	 */
	@XmlElement(name = "total_fee")
	@XmlCDATA
	private String totalFee;
	
	/**
	 * JSAPI、NATIVE、APP
	 */
	@XmlElement(name = "trade_type")
	@XmlCDATA
	private String tradeType;
	
	/**
	 * 此id为二维码中包含的商品ID
	 */
	@XmlElement(name = "product_id")
	@XmlCDATA
	private String productId;
	
	/**
	 * 签名
	 */
	@XmlElement(name = "sign")
	@XmlCDATA
	private String sign;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getSpbillCreateIp() {
		return spbillCreateIp;
	}

	public void setSpbillCreateIp(String spbillCreateIp) {
		this.spbillCreateIp = spbillCreateIp;
	}

	public String getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
}
