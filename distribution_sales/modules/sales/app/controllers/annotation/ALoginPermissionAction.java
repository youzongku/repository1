package controllers.annotation;

import java.io.Serializable;

import com.google.common.base.Strings;
import com.google.inject.Inject;

import dto.sales.PermissionRes;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.F;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.base.utils.StringUtils;
import session.ISessionService;

/**
 * 拦截器，
 * 1、接口权限  判断 token=test
 * 2、登录拦截 前台用户
 * 3、登录拦截 后台用户
 * @author zbc
 * 2016年10月7日 上午9:01:49
 */
public class ALoginPermissionAction extends Action<ALoginPermissionAction> {
	
	@Inject
	private ISessionService sessionService;
	
	@Override
	public Promise<Result> call(Context context) throws Throwable {
		// 拦截器
		try {
			String token = context.request().getHeader("token");
			// 权限判断
			if (!Strings.isNullOrEmpty(token)) {
				Configuration config = Play.application().configuration().getConfig("safeApi");
				String localToken = config.getString("token");
				if (token.equals(localToken)) {
					return delegate.call(context);
				}
			}
			// 后台台登录判断
			String res = admin(context);
			if (!StringUtils.isEmpty(res)) {
				sessionService.set("admin", res);
				return delegate.call(context);
			}
		} catch (Exception e) {
			Logger.error("获取用户异常", e);
			;
			return F.Promise.pure(ok(Json.toJson(new PermissionRes(101, "用户未登陆", null))));
		}
		return F.Promise.pure(ok(Json.toJson(new PermissionRes(101, "用户未登陆", null))));
	}
	
	private String admin(Context context) {
		String ltc = context != null ? cookies(context) : "";
		Serializable serializable = sessionService.get(2 + ltc);
		if (serializable == null) {
			return null;
		}
		return serializable.toString();
	}
	
	/*private String get(Map<String, String> params, String url, Context context) {
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
	}*/

	private String cookies(Context context) {
		String cookie = "";
		try {
			cookie = context._requestHeader().cookies().get("TT_LTC").get().copy$default$2();
		} catch (Exception e) {
			Logger.info("未获取到登录cookie");
		}
		return cookie;
	}
	
}
