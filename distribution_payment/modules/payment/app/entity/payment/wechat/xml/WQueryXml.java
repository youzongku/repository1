package entity.payment.wechat.xml;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import javax.xml.bind.annotation.*;

/**
 * 微信查询报文
 * @author luwj
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(	name = "xml",
			propOrder = {
				"appid", "mchId", "transactionId", "outTradeNo",
				"nonceStr", "sign"
			},
			namespace = "WQueryXml"
		)
@XmlRootElement(name = "xml")
public class WQueryXml{

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
	 * 微信订单号
	 */
	@XmlElement(name = "transaction_id")
	@XmlCDATA
	private String transactionId;
	
	/**
	 * 商户订单号(当没提供transaction_id时需要传这个)
	 */
	@XmlElement(name = "out_trade_no")
	@XmlCDATA
	private String outTradeNo;
	
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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
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
}
