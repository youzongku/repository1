package utils.payment;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import play.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

public class YjfUtil {

	// 易极付网关地址

	private String gatewayUrl = null;
	// 易极付商户安全码
	private String securityCheckKey = null;

	/**
	 * 易极付工具类 构造方法传参数
	 * 
	 * @param gatewayUrl
	 *            易极付网关地址
	 * @param securityCheckKey
	 *            易极付商户安全码
	 */
	public YjfUtil(String gatewayUrl, String securityCheckKey) {

		this.gatewayUrl = gatewayUrl;
		this.securityCheckKey = securityCheckKey;
	}

	/**
	 * 生成待签名字符串
	 * 
	 * @param data
	 * @return
	 */
	public String signString(Map<String, String> data) {
		StringBuilder sb = new StringBuilder();
		TreeMap<String, String> treeMap = new TreeMap<String, String>(data);
		for (Entry<String, String> entry : treeMap.entrySet()) {
			if (entry.getKey().equals("sign")) {
				//过滤签名参数
				continue;
			}
			if (StringUtils.isBlank(entry.getValue())) {
				//过滤null值和空字符串值
				continue;
			}
			sb.append(entry.getKey()).append("=").append(entry.getValue())
					.append("&");
		}
		sb.deleteCharAt(sb.length() - 1);
		Logger.debug("待签名字符串1:" + sb.toString() + securityCheckKey + "\n");
		return sb.toString() + securityCheckKey;
	}

	/**
	 * 生成待签名字符串
	 * 
	 * @param
	 * @return
	 */
	public String signString(String str) {

		String[] strArray = null;

		if (str.indexOf("&") > -1) {
			strArray = str.split("&");
		} else {
			strArray = str.split(",");
		}

		Logger.debug(""+strArray.length);
		Map<String, String> map = new HashMap<String, String>();

		for (String s : strArray) {
			String[] subStr = s.split("=");

			if (subStr.length < 1) {
				map.put(subStr[0], subStr[1]);
				Logger.debug(subStr[0] + "=" + subStr[1]);
			}

		}

		TreeMap<String, String> treeMap = new TreeMap<String, String>(map);

		StringBuilder sb = new StringBuilder();

		for (Entry<String, String> entry : treeMap.entrySet()) {
			if (entry.getKey().equals("sign")) {
				continue;
			}
			sb.append(entry.getKey()).append("=").append(entry.getValue())
					.append("&");
		}
		// sb.deleteCharAt(sb.length() - 1);

		Logger.debug("待签名字符串2:" + sb.toString() + securityCheckKey + "\n");

		return sb.toString() + securityCheckKey;

	}

	/**
	 * 加密方法
	 * 
	 * @param s
	 * @return
	 */
	public static String MD5(String s) {

		try {
			byte[] toDigest = s.getBytes("UTF-8");
			// 初始化MessageDigest
			MessageDigest md = MessageDigest.getInstance("MD5"); //MD5加密
			// 执行摘要信息.更新字符数组
			md.update(toDigest);
			// 将摘要信息转换为32位的十六进制字符串
			return new String(Hex.encodeHex(md.digest()));
		} catch (Exception e) {
			throw new RuntimeException("签名失败", e);
		}
	}

	/**
	 * 生成自动提交表单
	 * 
	 * @param data
	 * @return
	 */
	public String form(Map<String, String> data) {

		StringBuilder sb = new StringBuilder();

		sb.append("<meta charset=\"UTF-8\">\n");

		sb.append("<form id=\"gatewayform\" action=\"" + gatewayUrl
				+ "\" method=\"post\">\n");

		// URLEncoder.encode(entry.getValue(),"UTF-8") 中文字符做UrlEncode

		for (Entry<String, String> entry : data.entrySet()) {
			try {
				sb.append(String.format(
						"<input type=\"hidden\" name=\"%s\" value=\"%s\" />\n",
						entry.getKey(),
						URLEncoder.encode(entry.getValue(), "UTF-8")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		sb.append("</form>\n");

		sb.append("<script type=\"text/javascript\">\n");
		sb.append("window.onload = function() {document.getElementById('gatewayform').submit();}\n");
		sb.append("</script>\n");

		System.out.println(sb.toString() + "\n\n\n");
		return sb.toString();

	}

	/***
	 * 生成请求链接
	 * 
	 * @param data
	 * @return
	 */
	public String forLink(Map<String, String> data) {

		StringBuilder sb = new StringBuilder();

		sb.append(gatewayUrl + "?");

		for (Entry<String, String> entry : data.entrySet()) {
			try {
				// sb.append(String.format("%s=%s&",entry.getKey(),URLEncoder.encode(entry.getValue(),"UTF-8")));

				sb.append(String.format("%s=%s&", entry.getKey(),
						entry.getValue()));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 去掉最后一个字符"&"
		sb.deleteCharAt(sb.length() - 1);

		Logger.debug("无编码访问链接:\n" + sb.toString() + "\n");

		return sb.toString();

	}


	/***
	 * ' 提交表单
	 *
	 * @param data
	 * @throws Exception
	 */
	public String postString(Map<String, String> data) {
		HttpURLConnection con = null;
		OutputStream out = null;
		BufferedReader bufferedReader = null;
		InputStreamReader inRe = null;
		try {
			URL url = new URL(gatewayUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setAllowUserInteraction(true);
			out = con.getOutputStream();

			StringBuilder sb = new StringBuilder();
			for (Entry<String, String> entry : data.entrySet()) {
				if(StringUtils.isNotBlank(entry.getValue()))
					sb.append(entry.getKey() + "="
						+ URLEncoder.encode(entry.getValue(), "UTF-8") + "&");
			}
			sb.deleteCharAt(sb.length() - 1);
			out.write(sb.toString().getBytes());
			int responseCode = con.getResponseCode();
			if (responseCode == 200) {
				Logger.debug("链接成功!");
			} else {
				Logger.debug("链接异常!  链接状态码:" + responseCode);
			}
			// 定义BufferedReader拼接结果字符串
			StringBuffer responseResult = new StringBuffer();

			inRe = new InputStreamReader(con.getInputStream(),"UTF-8");
			// 定义BufferedReader输入流来读取URL的ResponseData
			bufferedReader = new BufferedReader(inRe);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				responseResult.append(line);
			}
			Logger.debug("执行结果:" + responseResult.toString() + "\n");
			return responseResult.toString();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("Exception>>:",e);
			throw new RuntimeException("提交表单失败");
		}finally {//关闭流
			IDUtils.closeStream(inRe, bufferedReader, out);
			if(con != null){
				con.disconnect();
			}
		}

	}

	public static String httpclient(String gatewayUrl, Map<String, String> data) {
		String responseBody = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			// Secure Protocol implementation.
			SSLContext ctx = SSLContext.getInstance("SSL");
			// Implementation of a trust manager for X509 certificates
			X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs,
											   String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
											   String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);

			ClientConnectionManager ccm = httpclient.getConnectionManager();
			// register https protocol in httpclient's scheme registry
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 443, ssf));

			HttpPost httpPost = new HttpPost(gatewayUrl);
			HttpParams params = httpclient.getParams();

			// 创建名/值组列表 (entry.getKey(), entry.getValue()
			List<BasicNameValuePair> parameters = new ArrayList<>();

			// 遍历添加map内容
			for (Entry<String, String> entry : data.entrySet()) {
				parameters.add(new BasicNameValuePair(entry.getKey(),
						URLEncoder.encode(entry.getValue(), "UTF-8")));
			}

			// 创建UrlEncodedFormEntity对象
			UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(
					parameters);
			httpPost.setEntity(formEntiry);

			httpPost.setParams(params);

			Logger.debug("REQUEST:" + httpPost.getURI());

			ResponseHandler responseHandler = new BasicResponseHandler();

			responseBody = (String) httpclient.execute(httpPost, responseHandler);

			System.out.println(responseBody);

			// Create a response handler

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return responseBody;
	}
}
