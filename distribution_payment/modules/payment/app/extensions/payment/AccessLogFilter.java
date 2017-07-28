package extensions.payment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import dto.payment.AccessLog;
import extensions.filter.FilterExecutionChain;
import extensions.filter.IFilter;
import play.Logger;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.base.utils.StringUtils;
import utils.payment.HttpUtil;

/**
 * 拦截器
 * @author zbc
 * 2016年10月31日 下午3:05:17
 */
@Singleton
public class AccessLogFilter implements IFilter {

	@Inject
	private EventBus ebus;
	
	@Override
	public Promise<Result> call(Context context, FilterExecutionChain chain) throws Throwable {
		try {
			//判断是否前台登录，如果是打印信息
			String res = get(Maps.newHashMap(), HttpUtil.getHostUrl() + "/member/isnulogin?" + System.currentTimeMillis(),
					context);
			JsonNode node = Json.parse(res);
			if (node != null && node.get("suc").asBoolean()) {
				// add by zbc 
				logPrint(context, node.get("msg").get("email").asText());
			}
		} catch (Exception e) {
		}
		return chain.executeNext(context);
	}

	@Override
	public int priority() {
		return 0;
	}
	
	
	private String get(Map<String, String> params, String url, Context context) {
		String responString = "";
		GetMethod get = new GetMethod(url);
		HttpClient client = new HttpClient();
		String ltc = context != null ? cookies(context) : "";
		if(!StringUtils.isEmpty(ltc)) {
			get.setRequestHeader("Cookie", "TT_LTC=" + ltc);			
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
			Logger.error("", e);
		} catch (IOException e) {
			Logger.error("", e);
		} finally {
			// 释放连接
			get.releaseConnection();
		}
		return responString;
	}

	private String cookies(Context context) {
		String cookie = "";
		try {
			cookie = context._requestHeader().cookies().get("TT_LTC").get().copy$default$2();
		} catch (Exception e) {
			Logger.info("未获取到登录cookie");
		}
		return cookie;
	}
	
	/**
	 * 日志打印方法
	 * @author zbc
	 * @param jsonNode 
	 * @param context 
	 * @since 2016年10月31日 上午10:02:23
	 */
	private void logPrint(Context context, String user){
		AccessLog log = new AccessLog();
		log.setAccessUser(user);
		log.setHost(context.request().host());
		log.setAccessIP(context.request().remoteAddress());
		log.setAccessTime(System.currentTimeMillis());
		log.setAccessInterface(context.request().path());
		ebus.post(log);
	}
}
