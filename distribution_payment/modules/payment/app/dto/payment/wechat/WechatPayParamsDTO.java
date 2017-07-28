package dto.payment.wechat;

/**
 * 微信支付返回json参数的dto
 * <p>
 * 具体文档参考<a href="https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1">微信支付文档</a>
 *
 * @author ye_ziran
 * @since 2015年7月21日 下午12:00:10
 */
public class WechatPayParamsDTO {

	private String appId;// 微信分配的公众账号ID（企业号corpid即为此appId）
	private String codeUrl;// 二维码地址
	private String deviceInfo;// 终端提交方式，web或者app
	private String fProposalCode;// 订单号
	private String mchId;// 商户号
	private String openId;// 用户标识
	private String prepayId;// 支付码
	private String payState;// 支付状态
	private String signType;// 签名类型
	private String totalFee;// 总价
	private String tradetype;// 支付方式，扫码或者js支付
	private String errorCode;// 返回状态码
	private String errorInfo;// 返回状态信息
	private int iid;//表t_order iid;

	/**
	 * 微信分配的公众账号ID（企业号corpid即为此appId）
	 *
	 * @return
	 * @author ye_ziran
	 * @since 2015年7月21日 下午12:13:01
	 */
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * 二维码链接
	 * <p>
	 * trade_type为NATIVE是有返回，可将该参数值生成二维码展示出来进行扫码支付
	 *
	 * @return
	 * @author ye_ziran
	 * @since 2015年7月21日 下午12:13:08
	 */
	public String getCodeUrl() {
		return codeUrl;
	}

	public void setCodeUrl(String codeUrl) {
		this.codeUrl = codeUrl;
	}

	/**
	 * 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
	 *
	 * @return
	 * @author ye_ziran
	 * @since 2015年7月21日 下午12:13:24
	 */
	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	/**
	 * 订单号，由商户自定义，原样返回
	 *
	 * @return
	 * @author ye_ziran
	 * @since 2015年7月21日 下午12:13:42
	 */
	public String getfProposalCode() {
		return fProposalCode;
	}

	public void setfProposalCode(String fProposalCode) {
		this.fProposalCode = fProposalCode;
	}

	/**
	 * 微信支付分配的商户号
	 *
	 * @return
	 * @author ye_ziran
	 * @since 2015年7月21日 下午12:15:14
	 */
	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	/**
	 * 用户标识
	 * <p>
	 * trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。下单前需要调用【网页授权获取用户信息】
	 * 接口获取到用户的Openid。
	 * 企业号请使用【企业号OAuth2.0接口】获取企业号内成员userid，再调用【企业号userid转openid接口】进行转换。
	 *
	 * @return
	 * @author ye_ziran
	 * @since 2015年7月21日 下午12:19:07
	 */
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	/**
	 * 预支付交易会话标识
	 * <p>
	 * 微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时
	 *
	 * @return
	 * @author ye_ziran
	 * @since 2015年7月21日 下午12:22:27
	 */
	public String getPrepayId() {
		return prepayId;
	}

	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}

	/**
	 * 
	 *
	 * @return
	 * @author ye_ziran
	 * @since 2015年7月21日 下午2:19:40
	 */
	public String getPayState() {
		return payState;
	}

	public void setPayState(String payState) {
		this.payState = payState;
	}

	/**
	 * 签名类型
	 *
	 * @return
	 * @author ye_ziran
	 * @since 2015年7月21日 下午2:25:45
	 */
	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	/**
	 * 总价，用户自定义字段
	 *
	 * @return
	 * @author ye_ziran
	 * @since 2015年7月21日 下午12:22:10
	 */
	public String getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}

	/**
	 * 交易类型
	 * <p>
	 * 取值如下：JSAPI，NATIVE，APP，WAP,详细说明见参数规定
	 *
	 * @return
	 * @author ye_ziran
	 * @since 2015年7月21日 下午2:26:15
	 */
	public String getTradetype() {
		return tradetype;
	}

	public void setTradetype(String tradetype) {
		this.tradetype = tradetype;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public int getIid() {
		return iid;
	}

	public void setIid(int iid) {
		this.iid = iid;
	}
	
}
