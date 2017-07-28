package controllers.annotation;

import play.Logger;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class RequestIpFilter extends Action<RequestIpFilter> {

	@Override
	public Promise<Result> call(Context context) throws Throwable {
		String remoteAddress = context.request().remoteAddress();
		String uri = context.request().uri();
		Logger.info("RequestIp------>>>[{}],uri---->>>[{}]", remoteAddress,uri);
		return delegate.call(context);
	}

}
