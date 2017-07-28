package annotation;

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
import com.google.inject.Inject;

import play.Logger;
import play.libs.F;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.base.utils.StringUtils;
import session.ISessionService;
import utils.HttpUtil;
import utils.response.ResponseResultUtil;

public class LoginPermissionAction extends Action<LoginPermissionAction> {

	@Inject
	private ISessionService sessionServce; 
	
	@Override
	public Promise<Result> call(Context context) throws Throwable {
		// 登陆判断
		try {
			if(context.request().body() != null && !context.request().body().asJson().has("ltc")) {
				return F.Promise.pure(ResponseResultUtil.newErrorJson(109, "缺失参数【ltc】"));
			}
			String res = get(Maps.newHashMap(), HttpUtil.B2BBASEURL + "/member/isnulogin?" + System.currentTimeMillis(),
					context);
			JsonNode node = Json.parse(res);
			if (node != null && node.get("suc").asBoolean()) {
				// TODO 临时处理，解决后台调用 sessionServce 设置不了session 问题
				context.args.put("TT_LTC", context.request().body().asJson().get("ltc").asText());
				sessionServce.set("user", node.get("msg").toString(),context);
				Logger.info("msg[{}]",sessionServce.get("user"));
				return delegate.call(context);
			}
		} catch (Exception e) {
			Logger.error("获取用户异常", e);;
			return F.Promise.pure(ResponseResultUtil.newErrorJson(110, "用户未登陆"));
		}
		return F.Promise.pure(ResponseResultUtil.newErrorJson(110, "用户未登陆"));
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
		JsonNode node = context.request().body().asJson();
		if (node == null || !node.has("ltc")) {
			return context.request().getQueryString("ltc");
		} else {
			return node.get("ltc").asText();
		}
	}

}
