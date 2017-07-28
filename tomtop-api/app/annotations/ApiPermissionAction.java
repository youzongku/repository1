package annotations;

import forms.ReturnMessageForm;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ApiPermissionAction extends Action<ApiPermission> {

	public static final String TT_LTC = "TT_LTC";

	@Override
	public CompletionStage<Result> call(Context context) {
		if(isLogin(context)) {
			return delegate.call(context);
		}else {
			ReturnMessageForm form = new ReturnMessageForm();
			form.setRes(false);
			form.setMsg("you have no permission, please sign in first.");
			String data = "{ href : '" + context.request().host() + "/v1/api/member/login"+"'}";
			form.setData(data);
			return CompletableFuture.completedFuture(unauthorized(Json.toJson(form)));
		}
	}


	//是否登录
	public boolean isLogin(Context context){
		boolean res = false;
		Http.Cookie cookie = context.request().cookie(TT_LTC);
		if(cookie != null){
			//登录后会以cookie的值作为session的key存在服务端
			String ltcValue = context.session().get(cookie.value());
			if(ltcValue != null ){
				res = true;
			}
		}

		return res;
	}



	


}
