package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang3.StringUtils;

import dto.openapi.AccessLog;
import play.Configuration;
import play.Play;
import play.libs.Json;
import play.mvc.Http.Context;

public class HttpUtil {

	public static String B2BBASEURL = "";
	public static String TOKEN = "";

	static {
			Configuration config = Play.application().configuration();
			Configuration url = config.getConfig("b2b");
			Configuration token = config.getConfig("safeApi");
			TOKEN = token.getString("token");
			B2BBASEURL = url.getString("b2bBaseUrl");
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
	public static String post(String requestBody, String url, Context context) {
		String responString = "";
		PostMethod post = new PostMethod(url);
		HttpClient client = new HttpClient();
		if (StringUtils.isNotEmpty(TOKEN)) {
			post.setRequestHeader("token", TOKEN);
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

	/**
	 * 基于进口项目的公共GET方法
	 * 
	 * @param params
	 *            查询参数数据字典
	 * @param url
	 *            请求路径,直接从根开始写即可，例如“/sale/demo”
	 * @return
	 */
	public static String get(Map<String, String> params, String url, Context context, String ltc) {
		String responString = "";
		GetMethod get = new GetMethod(url);
		HttpClient client = new HttpClient();
		if (StringUtils.isNotEmpty(TOKEN)) {
			get.setRequestHeader("token", TOKEN);
		}
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
			// 参数拼接
			get.setQueryString(nvpArr);
			get.setRequestHeader("content-type", "application/json;charset=utf-8");
			// 发送http请求
			client.executeMethod(get);
			// 打印返回的信息
			responString = new String(get.getResponseBody(), "UTF-8");

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			get.releaseConnection();
			// ((SimpleHttpConnectionManager)httpClient.getHttpConnectionManager()).shutdown();
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
			if(!file.exists()){
				file.createNewFile();
			}
			out =new FileOutputStream(file,true);
			out.write((Json.toJson(log).toString()+"\r\n").getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(out != null){
					out.flush();
					out.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
