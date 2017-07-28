package utils.payment;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dto.payment.AccessLog;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Http.Context;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpUtil {

	public static String getToken(){
		String token = "";
		Configuration config = Play.application().configuration().getConfig("safeApi");
		if(config != null){
			token = config.getString("token");
		}
		return token;
	}
	public static String getHostUrl() {
		Configuration config = Play.application().configuration()
				.getConfig("BBC");
		return config.getString("HOST");
		
	}
	
	public static final String BBC_HOST = "https://www.tomtop.com.cn";
	

	/**
	 * 基于进口项目的公共POST方法
	 * 
	 * @param requestBody
	 *            json 请求体
	 * @param urlName
	 *            请求路径，直接从根开始写即可，例如“/sales/demo”
	 * @return
	 */
	public static String post(String requestBody, String urlName) {
		Logger.debug(">>>>urlName>>>"+urlName);
		String responString = "";
		Configuration config = Play.application().configuration()
				.getConfig("BBC");
		String url = config.getString("HOST") + urlName;
		Logger.debug(">>>>url>>>>"+url);
		PostMethod post = new PostMethod(url);
		HttpClient client = new HttpClient();
		String token = getToken();
		try {
			if(!StringUtils.isEmpty(token)){
				post.setRequestHeader("token", token);
			}
			post.setRequestEntity(new StringRequestEntity(requestBody, "",
					"utf-8"));
			post.setRequestHeader("content-type",
					"application/json;charset=utf-8");
			// 发送http请求

			client.executeMethod(post);
			// 打印返回的信息
			responString = post.getResponseBodyAsString();
			Logger.debug(">>>responString>>>"+responString);
		} catch (HttpException e) {
			Logger.error("HttpException",e);
			e.printStackTrace();
		} catch (IOException e) {
			Logger.error("IOException",e);
			e.printStackTrace();
		}
		finally {
			// 释放连接
			post.releaseConnection();
		}

		return responString;
	}
	
	/**
	 * 基于进口项目的公共GET方法
	 * 
	 * @param params
	 *            查询参数数据字典
	 * @param url
	 *            请求路径,直接从根开始写即可，例如“/sale/demo”
	 * @return
	 */
	public static String get(Map<String, String> params, String url) {
		Logger.debug(">>>>urlName>>>"+url);
		String responString = "";
		Configuration config = Play.application().configuration()
				.getConfig("BBC");
		url = config.getString("HOST") + url;
		Logger.debug(">>>>url>>>>"+url);
		GetMethod get = new GetMethod(url);
		String token = getToken();
		if(!StringUtils.isEmpty(token))
			get.setRequestHeader("token", token);
		HttpClient client = new HttpClient();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		params.forEach((t, u) -> nvps.add(new NameValuePair(t, u)));
		
		NameValuePair[] nvpArr = new NameValuePair[params.size()];
		
		nvpArr = nvps.toArray(nvpArr);
		
		try {
			//参数拼接
			get.setQueryString(nvpArr);
			get.setRequestHeader("content-type",
					"application/json;charset=utf-8");
			// 发送http请求
			client.executeMethod(get);
			// 打印返回的信息
			responString = get.getResponseBodyAsString();

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			// 释放连接
			get.releaseConnection();
		}

		return responString;
	}

	/**
	 * GET方式HTTP请求
	 * @param params 请求参数
	 * @param headers 请求头信息
	 * @param url 请求地址
	 * @Author LSL on 2016-10-06 16:06:29
	 */
	public static String httpGET(Map<String, String> params,
		Map<String, String> headers, String url) {
		String response = "{}";
		GetMethod get = null;
		try {
			Logger.debug("httpGET    [preview url]----->" + url);
			url = Play.application().configuration().getConfig("BBC").getString("HOST") + url;
			Logger.debug("httpGET    [really url]----->" + url);
			get = new GetMethod(url);
			HttpClient client = new HttpClient();
			NameValuePair[] nvps = new NameValuePair[params.size()];
			List<NameValuePair> nvpList = Lists.newArrayList();
			params.forEach((name, value) -> nvpList.add(new NameValuePair(name, value)));
			nvps = nvpList.toArray(nvps);
			get.setQueryString(nvps);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				get.setRequestHeader(entry.getKey(), entry.getValue());
			}
			client.executeMethod(get);
			response = get.getResponseBodyAsString();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("httpGET    Exception----->", e);
		} finally {
			if (get != null) {
				get.releaseConnection();
			}
		}
		Logger.debug("httpGET    response----->" + response);
		return response;
	}

	/**
	 * 获取登陆用户信息
	 * @Author LSL on 2016-10-06 16:13:35
	 */
	public static String getUser() {
		String url = "/member/isnulogin?" + System.currentTimeMillis();
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String> headers = Maps.newHashMap();
		String[] cookies = Context.current().request().headers().get("Cookie");
		headers.put("Cookie", cookies[0]);
		Map<String, String[]> headerMap = Context.current().request().headers();
		for (Map.Entry<String, String[]> entry : headerMap.entrySet()) {
			headers.put(entry.getKey(), entry.getValue()[0]);
		}
		headers.remove("Accept-Encoding");
		headers.put("Content-Type", "application/json;charset=utf-8");
		headers.put("Accept", "application/json;q=0.9,*/*;q=0.8");
		return httpGET(params, headers, url);
	}
	public static void  writeInLog(AccessLog log,String fileName){
		FileOutputStream out = null;
		try {
			Configuration config = Play.application().configuration().getConfig("ALog");
			String filePath = config.getString("logDir");
			String dir = log.getAccessInterface().split("/")[1];
			filePath = filePath + File.separator + dir;
			File path = new File(filePath);
			if (!path.isDirectory() && !path.exists()) {
				path.mkdirs();
			}
			String name = filePath + File.separator+ fileName;
			File file = new File(name);
			if(!file.exists()){
				file.createNewFile();
			}
			out =new FileOutputStream(file,true);
			out.write((Json.toJson(log).toString()+"\r\n").getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally {
			try {
				if (out!=null) {
					out.flush();
					out.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * https GET 请求
	 * @param headers : 头信息
	 * @param url : 地址，带参数
     * @return : String
     */
	public static String httpsGet(Map<String, String> headers, String url){
		String returnStr = "";
		CloseableHttpClient client = null;
		try {
			HttpGet https = new HttpGet(url);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				https.setHeader(entry.getKey(), entry.getValue());
			}
			SSLContext sslc = SSLContexts.createDefault();
			sslc.init(null, new TrustManager[] {new ExampleX509TrustManager()}, null);
			SSLConnectionSocketFactory sslcsf = new SSLConnectionSocketFactory(sslc,
					new String[] {"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"},
					null, new ExampleHostnameVerifier());
			client = HttpClients.custom().setSSLSocketFactory(sslcsf).build();
			// 打印返回的信息
			CloseableHttpResponse response = client.execute(https);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				returnStr = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
			} else {
				System.out.println("requestGET    GET请求执行失败，HTTP状态码：" + statusCode);
			}
		} catch (Exception e) {
			Logger.error(">>httpsGet>>Exception>",e);
			e.printStackTrace();
		} finally {
			try {
				if(client != null)
					client.close();
			}catch (Exception e){
				Logger.error(">>httpsGet>>Exception>",e);
			}
		}
		return returnStr;
	}

	/**
	 * https POST 请求
	 * @param params : 请求参数
	 * @param headers : 头信息
	 * @param url : 地址
     * @return : String
     */
	public static String httpsPost(String params, Map<String, String> headers, String url){
		String returnStr = "";
		CloseableHttpClient httpClient = null;
		try {
			HttpPost https = new HttpPost(url);
			Logger.info("httpsPost    [executing request]----->" + https.getRequestLine());
			StringEntity entity = new StringEntity(params, Consts.UTF_8);
			entity.setContentEncoding(Charsets.UTF_8.name());
			https.setEntity(entity);
			for (String key : headers.keySet()) {
				https.setHeader(key, headers.get(key));
			}
			SSLContext sslc = SSLContexts.createDefault();
			sslc.init(null, new TrustManager[]{new ExampleX509TrustManager()}, null);
			SSLConnectionSocketFactory sslcsf = new SSLConnectionSocketFactory(sslc,
					new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"},
					null, new ExampleHostnameVerifier());
			httpClient = HttpClients.custom().setSSLSocketFactory(sslcsf).build();
			CloseableHttpResponse response = httpClient.execute(https);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				returnStr = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
			} else {
				Logger.info("requestPOST    POST请求执行失败，HTTP状态码：" + statusCode);
			}
			response.close();
		} catch (Exception e) {
			Logger.error("httpsPost    执行POST请求发生异常----->", e);
		} finally {
			try {
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				Logger.error("requestPOST    关闭HTTPS客户端发生异常----->", e);
			}
		}
		return returnStr;
	}

	public static void main(String[] args) {
		String returnStr = "";
		CloseableHttpClient httpClient = null;
		try {
			String url = "https://middle.b2b.com.cn/sales/getBase";
			Map<String, String> header = Maps.newHashMap();
			header.put("content-type", "application/json;charset=utf-8");
			header.put("token", "cb44ad0f-f796-454b-8452-9aaddb71d97e");
			String params = "{\"orderId\": 12304}";
			returnStr = HttpUtil.httpsPost(params, header, url);
		}catch (Exception e){
			System.out.println(">>e>>>>>"+e);
		}
		System.out.println(">>>returnStr>>>"+returnStr);
	}
}
