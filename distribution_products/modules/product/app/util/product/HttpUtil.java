package util.product;

import java.io.BufferedReader;
import java.io.Closeable;
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
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import dto.AccessLog;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Http.Context;

public class HttpUtil {

	public static String B2BBASEURL = "";
	static {
		if (StringUtils.isEmpty(B2BBASEURL)) {
			Configuration config = Play.application().configuration().getConfig("b2b");
			B2BBASEURL = config.getString("b2bBaseUrl");
		}
		
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
		HttpClient client = new HttpClient();
		String token = getToken();
		try {
			if (StringUtils.isNotEmpty(token)) {
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

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			// 释放连接
			post.releaseConnection();
		}

		return responString;
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
			if(!file.isFile()&&!file.exists()){
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
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		params.forEach(new BiConsumer<String, String>() {
			@Override
			public void accept(String t, String u) {
				// TODO Auto-generated method stub
				nvps.add(new NameValuePair(t, u));
			}
		});
		
		NameValuePair[] nvpArr = new NameValuePair[params.size()];
		
		nvpArr = nvps.toArray(nvpArr);
		HttpClient client = new HttpClient();
		try {
			String token = getToken();
			if(StringUtils.isNotEmpty(token)){
				get.setRequestHeader("token", token);
			}
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
	
	public static String post(String requestBody, String url, Context context) {
		String responString = "";
		PostMethod post = new PostMethod(url);
		HttpClient client = new HttpClient();
		String longTermCookie = context != null ? cookies(context,null) : "";
		if (StringUtils.isNotEmpty(longTermCookie)) {
			post.setRequestHeader("Cookie", "TT_LTC=" + longTermCookie);
		}
		String token = getToken();
		if (StringUtils.isNotEmpty(token)) {
			post.setRequestHeader("token", token);
		}
		try {
			post.setRequestEntity(new StringRequestEntity(requestBody, "", "utf-8"));
			post.setRequestHeader("content-type", "application/json;charset=utf-8");
			// 发送http请求
			client.executeMethod(post);
			// 打印返回的信息
			responString = new String(post.getResponseBody(), "UTF-8");

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			post.releaseConnection();
			// ((SimpleHttpConnectionManager)httpClient.getHttpConnectionManager()).shutdown();
		}

		return responString;
	}
	
	private static String cookies(Context context, String ltc) {
		if (context.request().cookie("TT_LTC") == null) {
			JsonNode node = context.request().body().asJson();
			if (node == null || !node.has("ltc")) {
				return ltc;
			} else {
				return node.get("ltc").asText();
			}
		}
		return context.request().cookie("TT_LTC").value();
	}
	
	public static String getToken(){
		String token = "";
		Configuration config = Play.application().configuration().getConfig("safeApi");
		if(config != null){
			token = config.getString("token");
		}
		return token;
	}
	/**
	 * 关闭流
	 * @param streams
	 */
	public static void closeStream(Closeable... streams) {
		for(Closeable stream : streams) {
			if(stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
					Logger.error("HttpUtil closeStream IOException", e);
				}
			}
		}
	}

	
	/**
	 * 执行一个HTTP POST请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的HTML
	 * @throws Exception
	 */
	public static String doPost(String url, Map<String, Object> params, String charset, boolean pretty)
			throws Exception {
		StringBuffer response = new StringBuffer();
		PostMethod method = new PostMethod(url);
		HttpClient client = new HttpClient();
		// 设置Http Post数据
		if (params != null) {
			method.setRequestHeader("content-type", "application/x-www-form-urlencoded;charset=utf-8"); 
			NameValuePair[] pare = new NameValuePair[]{new NameValuePair("data",Json.toJson(params) + "")}; 
			method.setRequestBody(pare);
		}
		try {
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
		} catch (IOException e) {
			throw new Exception("执行HTTP Post请求" + url + "时，发生异常！" + e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}
}

