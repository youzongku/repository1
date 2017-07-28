
package utils.payment;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;


/**
 * 支付宝静态配置工具类
 * @author luwj
 *
 */
public class AlipayUtil {
	
	/**
	 * http
	 */
	public static final String HTTP_ADD = "http://";
	
	/**
	 * 网关支付接口名称
	 */
	public static final String GATEWAY_SERVICE = "create_direct_pay_by_user";
	
	/**
	 * 获取汇率接口名称
	 */
	public static final String FOREX_RATE_SERVICE = "forex_rate_file";
	
	/**
	 * 校验通知合法性接口名称
	 */
	public static final String VERIFY_SERVICE = "notify_verify";

	/**
	 * 支付宝批量付款接口
	 */
	public static final String BATCH_TRANS_NOTIFY = "batch_trans_notify";
	
	/**
	 * 支付宝单笔退款接口
	 */
	public static final String FOREX_REFUND = "forex_refund";
	
	/**
	 * 编码
	 */
	public static final String SIGN_TYPE = "MD5";
	
	/**
	 * 字符编码格式 目前支持 gbk 或 utf-8
	 */
	public static final String INPUT_CHARSET = "utf-8";
	
	/**
	 * 字符编码格式  gbk
	 */
	public static final String INPUT_GBK = "GBK";
	
	public static final String INPUT_ISO= "ISO-8859-1";
	
	/**
	 * 请求方式:GET
	 */
	public static final String METHOD_GET = "GET";
	 
	/**
	 * 请求方式：POST
	 */
	public static final String METHOD_POST = "POST";
	
	/**
	 * true
	 */
	public static final String TRUE = "true";
	
	/**
	 * 支付宝支付状态:交易完成
	 */
	public static final String TRADE_STATUS = "TRADE_SUCCESS";
	
	/**
	 * 币种：人民币
	 */
	public static final String HKD = "HKD";
	
	/**
	 * 供货商
	 */
	public static final String SUPPLIER = "通淘国际";
	
	/** 
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }
	
	/** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);//排序
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }
    
    /**
	 * 字符窜编码
	 * @param param 字符窜
	 * @return
	 */
	public static String encode(String param){
		String str = "";
		if(StringUtils.isNotBlank(param)){
			try {
				str = new String(param.getBytes("ISO-8859-1"),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return str;
	}
	
	 /**
     * MAP类型数组转换成NameValuePair类型
     * @param properties  MAP类型数组
     * @return NameValuePair类型数组
     */
	public static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
        }

        return nameValuePair;
    }
	
	/**
	 * String转Xml
	 * @param returnMess
	 * @return
	 * @throws Exception
	 */
	public static Element getRootElementByString(String returnMess) throws Exception{
		InputStream inputStream = null;
		Element root = null;
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			//解析返回xml报文 
			DocumentBuilder builder = dbf.newDocumentBuilder();
			inputStream = new ByteArrayInputStream(returnMess.trim().getBytes("UTF-8"));
		    Document doc = builder.parse(inputStream);
		    root = doc.getDocumentElement(); // 获取根元素
		} finally{
			if(inputStream != null)
				inputStream.close();
		}
		return root;
	}

	/**
	 * 校验数据是否为空  不为空则取0角标值
	 * @param strs
	 * @return
	 */
	public static String checkNotifyParam(String[] strs){
		return strs == null?"":strs[0];
	}

	/**
	 * 将javabean转为map类型，然后返回一个map类型的值
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> beanToMap(Object obj) {
		Map<String, Object> params = new HashMap<String, Object>(0);
		try {
			PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
			PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				if (!StringUtils.equals(name, "class")) {
					params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}
}

