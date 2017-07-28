package utils.dismember;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;

import entity.dismember.EmailAccount;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

public class SMSManager {
	
//	private static final String url = "http://sms.253.com/msg/";// 应用地址
//	private static final String account = "N8870432";// 账号
//	private static final String pswd = "YPFyqC9G07cf80";// 密码
	//final String mobile = "13800210021,13800138000";// 手机号码，多个号码使用","分割
	//final String msg = "亲爱的用户，您的验证码是123456，5分钟内有效。";// 短信内容
	private static final String needstatus = "1";// 是否需要状态报告，需要1，不需要0
	private static final String extno = null;// 扩展码

	public static void main(String[] args) throws Exception {
//		send("【通淘国际】亲爱的用户，您的验证码是123456，5分钟内有效。","XXXXXX");
	}
	
	/**
	 * @author hanfs
	 * 描述：短信发送
	 * 2016年4月14日上午9:43:40
	 * @param msg 信息内容
	 * @param mobile 手机号码，多个号码使用","隔开，例如"13800210021,13800138000"
	 * @throws Exception
	 */
	public static void send(String msg,EmailAccount emailAccount, String mobile) throws Exception{
		try {
			//从数据库里配置要发送的短信服务商地址
			String url = emailAccount.getCsmtphostname();
			String account = emailAccount.getCusername();
			String pswd = emailAccount.getCpassword();

			String returnString = batchSend(url, account, pswd, mobile, msg, needstatus, extno);
			System.out.println(returnString);
		} catch (Exception e) {
			// TODO 处理异常
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param url 应用地址，类似于http://ip:port/msg/
	 * @param un 账号
	 * @param pw 密码
	 * @param phone 手机号码，多个号码使用","分割
	 * @param msg 短信内容
	 * @param rd 是否需要状态报告，需要1，不需要0
	 * @return 返回值定义参见HTTP协议文档
	 * @throws Exception
	 */
	public static String batchSend(String url, String un, String pw, String phone, String msg, String rd, String ex)
			throws Exception {
		HttpClient client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
		GetMethod method = new GetMethod();
		try {
			URI base = new URI(url, false);
			method.setURI(new URI(base, "send", false));
			method.setQueryString(new NameValuePair[] { new NameValuePair("un", un), new NameValuePair("pw", pw),
					new NameValuePair("phone", phone), new NameValuePair("rd", rd), new NameValuePair("msg", msg),
					new NameValuePair("ex", ex), });
			int result = client.executeMethod(method);
			if (result == HttpStatus.SC_OK) {
				InputStream in = method.getResponseBodyAsStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				return URLDecoder.decode(baos.toString(), "UTF-8");
			} else {
				throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
			}
		} finally {
			method.releaseConnection();
		}
	}
	
	/**
	 * 
	 * @param url 应用地址，类似于http://ip:port/msg/
	 * @param account 账号
	 * @param pswd 密码
	 * @param mobile 手机号码，多个号码使用","分割
	 * @param msg 短信内容
	 * @param needstatus 是否需要状态报告，需要true，不需要false
	 * @return 返回值定义参见HTTP协议文档
	 * @throws Exception
	 */
	public static String send(String url, String account, String pswd, String mobile, String msg,
			boolean needstatus, String product, String extno) throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod();
		try {
			URI base = new URI(url, false);
			method.setURI(new URI(base, "HttpSendSM", false));
			method.setQueryString(new NameValuePair[] { 
					new NameValuePair("account", account),
					new NameValuePair("pswd", pswd), 
					new NameValuePair("mobile", mobile),
					new NameValuePair("needstatus", String.valueOf(needstatus)), 
					new NameValuePair("msg", msg),
					new NameValuePair("product", product), 
					new NameValuePair("extno", extno), 
				});
			int result = client.executeMethod(method);
			if (result == HttpStatus.SC_OK) {
				InputStream in = method.getResponseBodyAsStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				return URLDecoder.decode(baos.toString(), "UTF-8");
			} else {
				throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
			}
		} finally {
			method.releaseConnection();
		}

	}

	
}
