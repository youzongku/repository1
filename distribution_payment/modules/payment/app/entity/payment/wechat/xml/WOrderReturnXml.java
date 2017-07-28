package entity.payment.wechat.xml;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import javax.xml.bind.annotation.*;

/**
 * 下单返回报文
 * 
 * @author luwj
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(	name = "xml", 
			propOrder = { 
				"returnCode", "returnMsg", "appid", "mchId", "deviceInfo", 
				"nonceStr", "sign", "resultCode", "errCode", "errCodeDes",
				"prepayId", "tradeType", "codeUrl" 
			}, 
			namespace = "WOrderReturnXml"
		)
@XmlRootElement(name = "xml")
public class WOrderReturnXml{

	/**
	 * 返回状态码
	 */
	@XmlElement(name = "return_code")
	@XmlCDATA
	private String returnCode;
	
	/**
	 * 返回信息
	 */
	@XmlElement(name = "return_msg")
	@XmlCDATA
	private String returnMsg;
	
	/**
	 * 公众账号ID
	 */
	@XmlElement(name = "appid")
	@XmlCDATA
	private String appid;
	
	/**
	 * 商户号
	 */
	@XmlElement(name = "mch_id")
	@XmlCDATA
	private String mchId;
	
	/**
	 * 设备号
	 */
	@XmlElement(name = "device_info")
	@XmlCDATA
	private String deviceInfo;
	
	/**
	 * 随机字符串
	 */
	@XmlElement(name = "nonce_str")
	@XmlCDATA
	private String nonceStr;
	
	/**
	 * 签名
	 */
	@XmlElement(name = "sign")
	@XmlCDATA
	private String sign;
	
	/**
	 * 业务结果
	 */
	@XmlElement(name = "result_code")
	@XmlCDATA
	private String resultCode;
	
	/**
	 * 错误代码
	 */
	@XmlElement(name = "err_code")
	@XmlCDATA
	private String errCode;
	
	/**
	 * 错误代码描述
	 */
	@XmlElement(name = "err_code_des")
	@XmlCDATA
	private String errCodeDes;
	
	/**
	 * 预支付交易会话标识
	 */
	@XmlElement(name = "prepayId")
	@XmlCDATA
	private String prepay_id;
	
	/**
	 * 交易类型:JSAPI，NATIVE，APP
	 */
	@XmlElement(name = "trade_type")
	@XmlCDATA
	private String tradeType;
	
	/**
	 * 二维码链接(trade_type为NATIVE是有返回)
	 */
	@XmlElement(name = "code_url")
	@XmlCDATA
	private String codeUrl;

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getPrepay_id() {
		return prepay_id;
	}

	public void setPrepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getCodeUrl() {
		return codeUrl;
	}

	public void setCodeUrl(String codeUrl) {
		this.codeUrl = codeUrl;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrCodeDes() {
		return errCodeDes;
	}

	public void setErrCodeDes(String errCodeDes) {
		this.errCodeDes = errCodeDes;
	}

}
