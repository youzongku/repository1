package utils.payment;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.util.SortedMap;
import java.util.UUID;

/**
 * 微信支付工具类
 * @author luwj
 *
 */
public class WechatUtil {

	public static final String SUCCESS = "SUCCESS";
	
	/**
	 * 微信支付异步返回业务结果码：成功
	 */
	public static final String RETURN_CODE_SUCCESS = "SUCCESS";
	
	/**
	 * 微信支付异步返回业务结果码：失败
	 */
	public static final String RETURN_CODE_FAIL = "FAIL";
	
	/**
	 * 微信支付异步返回业务结果信息:ok
	 */
	public static final String RETURN_MSG_OK = "OK";
	
	/**
	 * 微信支付订单状态：0,下单
	 */
	public static final String WECHAT_PRO_STATUS_0 = "0";
	
	/**
	 * 微信支付订单状态：1,支付完成
	 */
	public static final String WECHAT_PRO_STATUS_1 = "1";
	
	/**
	 * 微信支付订单状态：2,未下单的支付结果 
	 */
	public static final String WECHAT_PRO_STATUS_2 = "2";
	
	/**
	 * 微信支付交易结果：成功
	 */
	public static final String TRADE_STATE_SUCCESS = "SUCCESS";
	
	/**
	 * 微信appid
	 */
	public static final String APPID = "wx0bf0701322c263d8";
	
	/**
	 * 微信secret
	 */
	public static final String APPSECRET = "419977c0077ba7190a3cee7273e12752";
	
	/**
	 * 商户号
	 */
	public static final String MCH_ID = "1252040101";
	
	/**
	 * API密钥
	 */
	public static final String KEY = "weinxinpayATtomtopFrom2004TO2015";
	
	/**
	 * 终端IP  订单生成的机器 IP
	 */
	public static final String SPBILl_CREATE_IP = "192.168.12.13";
	
	/**
	 * 编码方式
	 */
	public static final String SIGN_TYPE = "MD5";
	
	/**
	 * 通知地址 （接收微信支付成功通知）
	 */
	public static final String NOTIFY_URL = "http://tomtophnc.3322.org:9000/payhk/PayControl";
	
	/**
	 * 统一下单地址
	 */
	public static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	
	/**
	 * 长链接转短链接地址
	 */
	public static final String LONG_SHORT_URL = "https://api.weixin.qq.com/cgi-bin/shorturl?access_token=";
	
	/**
	 * 获取access_token地址
	 */
	public static final String GET_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=";
	
	/**
	 * 微信支付结果查询地址
	 */
	public static final String ORDER_QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
	
	/**
	 * 微信申请退款地址
	 */
	public static final String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	
	/**
	 *  调用支付方式:NATIVE
	 */
	public static final String TRADE_TYPE_NATIVE = "NATIVE";

	/**
	 * 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
	 */
	public static final String DEVICE_INFO_WEB = "WEB";
	
	/**
	 * 金额单位：元转分
	 */
	public static final String CONVESION_0 = "0";
	
	/**
	 * 金额单位：分转元
	 */
	public static final String CONVESION_1 = "1";
	
	/**
	 * 解析返回xml报文
	 * @param root
	 * @return
	 */
	public static String resovleXml(Element root,String name){
		NodeList lists = root.getElementsByTagName(name);
		if(lists.getLength() > 0 && lists.item(0).getFirstChild() != null)
			return lists.item(0).getFirstChild().getNodeValue();
		else
			return "";
	}
	
	/**
	 * 封装签名窜Map
	 * @param name
	 * @param str
	 * @param map
	 * @return
	 */
	public static SortedMap<Object,Object> putInMap(String name, String str, SortedMap<Object,Object> map){
		if(StringUtils.isNotBlank(str))
			map.put(name, str);
		return map;
	}
	
	   /**
     * 生成随机数
     * @return
     * @author luwj
     *2015年7月9日下午7:13:36
     */
    public static String create_nonce_str() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成当前时间戮
     * @return
     * @author luwj
     *2015年7月9日下午7:13:54
     */
    public static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
    
    /**
     * 金额单位换算（元--分）
     * @return
     */
	public static String moneyUnitConversion(String data , String name){
    	if(StringUtils.isNotBlank(data)){
	    	if(WechatUtil.CONVESION_0.equals(name)){//金额单位：元转分,去掉小数点后面数字
	    		data = new BigDecimal(data).multiply(new BigDecimal(100.00)).toString();
				if(data.contains(".")){
					data = data.substring(0, data.lastIndexOf("."));
				}
	    	}else{//金额单位：分转元
	    		data = new BigDecimal(data).divide(new BigDecimal(100.00)).toString();
	    	}
    	}else{
    		data = "";
    	}
    	return data;
    }
}
