package extensions.dismember;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import dto.dismember.AccessLog;
import extensions.filter.FilterExecutionChain;
import extensions.filter.IFilter;
import play.libs.F.Promise;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.dismember.impl.LoginService;

/**
 * 拦截器
 * @author zbc
 * 2016年10月31日 下午3:05:17
 */
@Singleton
public class AccessLogFilter implements IFilter {

	@Inject
	private LoginService loginService; 
	
	@Inject
	private EventBus ebus;
	
	@Override
	public Promise<Result> call(Context context, FilterExecutionChain chain) throws Throwable {
		String email =  null;
		if(loginService.isLogin(1)){
			email = loginService.getLoginContext(1).getEmail();
		}else if(loginService.isLogin(2)){
			email = loginService.getLoginContext(2).getEmail();
		}
		if(email != null){
			logPrint(context,email);
		}
		context.response().setContentType("application/json;charset=utf-8");
		return chain.executeNext(context);
	}

	@Override
	public int priority() {
		return 0;
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
