package entity.payment.wechat.xml;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import javax.xml.bind.annotation.*;


/**
 * 微信查询返回报文
 * @author luwj
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(	name = "xml",
			propOrder = {
					"returnCode", "returnMsg", "appid", "mchId", "nonceStr", "sign",
					"resultCode", "errCode", "errCodeDes", "deviceInfo", "openid",
					"isSubscribe", "tradeType", "tradeState", "bankType", "totalFee",
					"feeType", "cashFee", "cashFeeType", "couponFee", "couponCount",
					"transactionId", "outTradeNo", "attach", "timeEnd", "tradeStateDesc"
			},
			namespace = "WQueryReturnXml"
		)
@XmlRootElement(name = "xml")
public class WQueryReturnXml{

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
	 * 设备号
	 */
	@XmlElement(name = "device_info")
	@XmlCDATA
	private String deviceInfo;
	
	/**
	 * 用户标识
	 */
	@XmlElement(name = "openid")
	@XmlCDATA
	private String openid;
	
	/**
	 * 是否关注公众账号
	 */
	@XmlElement(name = "is_subscribe")
	@XmlCDATA
	private String isSubscribe;
	
	/**
	 * 交易类型
	 */
	@XmlElement(name = "trade_type")
	@XmlCDATA
	private String tradeType;
	
	/**
	 * 交易状态
	 */
	@XmlElement(name = "trade_state")
	@XmlCDATA
	private String tradeState;
	
	/**
	 * 付款银行
	 */
	@XmlElement(name = "bank_type")
	@XmlCDATA
	private String bankType;
	
	/**
	 * 总金额
	 */
	@XmlElement(name = "total_fee")
	@XmlCDATA
	private String totalFee;
	
	/**
	 * 货币种类
	 */
	@XmlElement(name = "fee_type")
	@XmlCDATA
	private String feeType;
	
	/**
	 * 现金支付金额
	 */
	@XmlElement(name = "cash_fee")
	@XmlCDATA
	private String cashFee;
	
	/**
	 * 现金支付货币类型
	 */
	@XmlElement(name = "cash_fee_type")
	@XmlCDATA
	private String cashFeeType;
	
	/**
	 * 代金券或立减优惠金额
	 */
	@XmlElement(name = "coupon_fee")
	@XmlCDATA
	private String couponFee;
	
	/**
	 * 代金券或立减优惠使用数量
	 */
	@XmlElement(name = "coupon_count")
	@XmlCDATA
	private String couponCount;
	
	/**
	 * 微信支付订单号
	 */
	@XmlElement(name = "transaction_id")
	@XmlCDATA
	private String transactionId;
	
	/**
	 * 商户订单号
	 */
	@XmlElement(name = "out_trade_no")
	@XmlCDATA
	private String outTradeNo;
	
	/**
	 * 附加数据
	 */
	@XmlElement(name = "attach")
	@XmlCDATA
	private String attach;
	
	/**
	 * 支付完成时间
	 */
	@XmlElement(name = "time_end")
	@XmlCDATA
	private String timeEnd;
	
	/**
	 * 交易状态描述
	 */
	@XmlElement(name = "trade_state_desc")
	@XmlCDATA
	private String tradeStateDesc;

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

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getIsSubscribe() {
		return isSubscribe;
	}

	public void setIsSubscribe(String isSubscribe) {
		this.isSubscribe = isSubscribe;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getTradeState() {
		return tradeState;
	}

	public void setTradeState(String tradeState) {
		this.tradeState = tradeState;
	}

	public String getBankType() {
		return bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public String getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getCashFee() {
		return cashFee;
	}

	public void setCashFee(String cashFee) {
		this.cashFee = cashFee;
	}

	public String getCashFeeType() {
		return cashFeeType;
	}

	public void setCashFeeType(String cashFeeType) {
		this.cashFeeType = cashFeeType;
	}

	public String getCouponFee() {
		return couponFee;
	}

	public void setCouponFee(String couponFee) {
		this.couponFee = couponFee;
	}

	public String getCouponCount() {
		return couponCount;
	}

	public void setCouponCount(String couponCount) {
		this.couponCount = couponCount;
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

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(String timeEnd) {
		this.timeEnd = timeEnd;
	}

	public String getTradeStateDesc() {
		return tradeStateDesc;
	}

	public void setTradeStateDesc(String tradeStateDesc) {
		this.tradeStateDesc = tradeStateDesc;
	}
	
}
