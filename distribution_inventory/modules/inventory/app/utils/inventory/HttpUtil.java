package utils.inventory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dto.inventory.AccessLog;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;

public class HttpUtil {
	
	
	
	private static Log log = LogFactory.getLog(HttpUtil.class);

	public static String B2CBASEURL = "";
	public static String B2BBASEURL = "";

	static {
		if (B2CBASEURL.equals("")) {
			Configuration config = Play.application().configuration().getConfig("b2c");
			B2CBASEURL = config.getString("b2cBaseUrl");
		}
		
		if (B2BBASEURL.equals("")) {
			Configuration config = Play.application().configuration().getConfig("b2b");
			B2BBASEURL = config.getString("b2bBaseUrl");
		}
	}

	/**
	 * 执行一个HTTP GET请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param queryString
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的HTML
	 */
	public static String doGet(String url, String queryString, String charset, boolean pretty) {
		StringBuffer response = new StringBuffer();

		HttpMethod method = new GetMethod(url);
		HttpClient client = new HttpClient();
		try {
			if (StringUtils.isNotBlank(queryString))
				// 对get请求参数做了http请求默认编码，好像没有任何问题，汉字编码后，就成为%式样的字符串
				method.setQueryString(URIUtil.encodeQuery(queryString));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(), charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (URIException e) {
			log.error("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！", e);
		} catch (IOException e) {
			log.error("执行HTTP Get请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}
	/**
	 * 基于进口项目的公共POST方法
	 * 
	 * @param requestBody
	 *            json格式请求体
	 * @param url
	 *            请求路径,直接从根开始写即可，例如“/sale/demo”
	 * @return
	 */
	public static String post(String requestBody, String url) {
		String responString = "";
		PostMethod post = new PostMethod(url);
		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(60000);//连接超时
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(60000);//读取超时
		try {
			String token = getToken();
			if(StringUtils.isNotEmpty(token)){
				post.setRequestHeader("token",token);
			}
			post.setRequestEntity(new StringRequestEntity(requestBody, "",
					"utf-8"));
			post.setRequestHeader("content-type",
					"application/json;charset=utf-8");
			
			// 发送http请求
			httpClient.executeMethod(post);
			// 打印返回的信息
			responString = new String(post.getResponseBody(),"UTF-8");

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			// 释放连接
			post.releaseConnection();
			//((SimpleHttpConnectionManager)httpClient.getHttpConnectionManager()).shutdown();
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
		String responString = "";
		GetMethod get = new GetMethod(url);
		//get.setRequestHeader("Connection", "close");
		HttpClient httpClient = new HttpClient();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		params.forEach(new BiConsumer<String, String>() {
			@Override
			public void accept(String t, String u) {
				nvps.add(new NameValuePair(t, u));
			}
		});
		
		NameValuePair[] nvpArr = new NameValuePair[params.size()];
		
		nvpArr = nvps.toArray(nvpArr);
		
		try {
			String token = getToken();
			if(StringUtils.isNotEmpty(token)){
				get.setRequestHeader("token",token);
			}
			//参数拼接
			get.setQueryString(nvpArr);
			get.setRequestHeader("content-type",
					"application/json;charset=utf-8");
			// 发送http请求
			httpClient.executeMethod(get);
			// 打印返回的信息
			responString = new String(get.getResponseBody(),"UTF-8");

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			// 释放连接
			get.releaseConnection();
			//((SimpleHttpConnectionManager)httpClient.getHttpConnectionManager()).shutdown(); 
		}

		return responString;
	}
	
	public static String getToken(){
		String token = "";
		Configuration config = Play.application().configuration().getConfig("safeApi");
		if(config != null){
			token = config.getString("token");
		}
		return token;
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
				if(out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
