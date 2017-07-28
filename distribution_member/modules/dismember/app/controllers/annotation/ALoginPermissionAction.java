package controllers.annotation;

import com.google.inject.Inject;
import dto.dismember.PermissionRes;
import play.Logger;
import play.libs.F;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.dismember.impl.LoginService;

/**
 * 登录拦截 后台用户
 * @author zbc
 * 2017年2月17日 下午5:47:46
 */
public class ALoginPermissionAction extends Action<ALoginPermissionAction> {
	
	@Inject
	private LoginService loginService; 
	
	@Override
	public Promise<Result> call(Context context) throws Throwable {
		// 拦截器  
		try {
			if (loginService.isLogin(2)) {
				return delegate.call(context);
			}
		} catch (Exception e) {
			Logger.error("获取用户异常", e);;
			return F.Promise.pure(ok(Json.toJson(new PermissionRes<>(101, "用户未登陆",null))));
		}
		return F.Promise.pure(ok(Json.toJson(new PermissionRes<>(101, "用户未登陆",null))));
	}
}
