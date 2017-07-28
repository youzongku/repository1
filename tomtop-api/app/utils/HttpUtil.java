package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

/**
 * Http请求的工具类
 * <p>
 *      note:使用HttpClinetPool,获取和关闭HttpClient
 *
 * </p>
 *
 *
 *
 * @author  ye_ziran
 * @since	2016-03-31
 */
public class HttpUtil {

	public static final String TT_LTC = "TT_LTC";
	public static final String TT_STC = "TT_STC";


	//cookie失效时间，2小时
	private static final int COOKIE_EXPIRE_TIME = 2 * 60 * 60;
	private static HttpClientPool pool = null;

	static {
		pool = HttpClientPool.instance();
	}

	/**
	 *
	 *
	 * @return
	 *
	 *
	 */
	public static CookieStore getCookieStore(){
		//在其他终端同时打开了tomtop的网站，也会有TT_LTC的cookie，
		//如果存在，就把cookie带到这一次的请求中
		Http.Cookie cookieInRequest =  Http.Context.current().request().cookie("TT_LTC");
		CookieStore cookieStore = new BasicCookieStore();
		if(cookieInRequest != null){
			Cookie clientCookie = new BasicClientCookie("TT_LTC", cookieInRequest.value());
			cookieStore.addCookie(clientCookie);
		}
		return cookieStore;
	}

	/**
	 * 创建StringEntity
	 * @param params
	 * @return
	 *
	 */
	public static HttpEntity buildStringEntity(JsonNode params){
		return new StringEntity(params.toString(), ContentType.APPLICATION_JSON);
	}

	/**
	 * 创建StringEntity
	 * @param params
	 * @return
	 */
	public static HttpEntity buildStringEntity(Map<String, String> params){
		Set<String> keySet = params.keySet();
		ObjectNode on = Json.newObject();
		for(String key : keySet) {
			on.put(key, params.get(key));
		}
		return buildStringEntity(on);
	}

	/**
	 *
	 * 以post方式请求
	 *
	 * @param url
	 * @param reqEntity
	 * @return
	 * @throws IOException
	 *
	 * @author  ye_ziran
	 * @since	2016-03-25
	 */
	public static String post(String url, HttpEntity reqEntity){
		return post(url, reqEntity, null);
	}
	public static String post(String url, Map<String,String> params){
		return post(url, buildStringEntity(params), null);
	}
	public static String post(String url, JsonNode params){
		return post(url, buildStringEntity(params), null);
	}



	/**
	 *
	 * 以post方式请求
	 *
	 * @param url
	 * @param reqEntity
	 * @param header
	 * @return 请求接口后，返回的字符串
	 *
	 * @author  ye_ziran
	 * @since	2016-03-25
	 */
	public static String post(String url, HttpEntity reqEntity, Header header)  {
		String reponseStr = null;
		Logger.debug("post url {}", url);

		CloseableHttpClient httpclient = pool.get();
		CloseableHttpResponse response = null;
		try {

			HttpUriRequest reqUri = RequestBuilder.post().setUri(new URI(url)).setEntity(reqEntity).build();
			response = httpclient.execute(reqUri);

			if(response != null) {
				Logger.debug("request url: {} , reponse.statusline : {}", url, response.getStatusLine());
				HttpEntity entity = response.getEntity();
				reponseStr = EntityUtils.toString(entity);
			}else{
				Logger.debug("请求超时，URL={}, statusline : {}", url, response.getStatusLine());
			}

		} catch (URISyntaxException e) {
			Logger.error("无效的URI：{}, errorMsg:{}", url, e.getMessage());
		} catch (ClientProtocolException e) {
			Logger.error("客户端协议错误：{}, errorMsg:{}", url, e.getMessage());
		} catch (UnsupportedEncodingException e) {
			Logger.error("无效的编码格式, errorMsg:{}",  e.getMessage());
		} catch (IOException e) {
			Logger.error("httpclient.execute错误, errorMsg:{}", e.getMessage());
		} finally {
			pool.close(httpclient);
		}
		return reponseStr;
	}

	/**
	 * 以get方式请求
	 * @param url
	 * @return
	 */
	public static String get(String url) {
		String reponseStr = null;
		Logger.debug("post url {}", url);

		CloseableHttpClient httpclient = pool.get();
		try{

			HttpGet getMethod = new HttpGet(url);
			HttpResponse response = httpclient.execute(getMethod);
			HttpEntity entity = response.getEntity();
			reponseStr = EntityUtils.toString(entity);
		}catch(Exception e){
			Logger.error("HttpUtil.post error, msg={}", e.getMessage());
		} finally {
			pool.close(httpclient);
		}
		return reponseStr;
	}

}
