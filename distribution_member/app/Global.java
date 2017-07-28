import com.google.common.collect.FluentIterable;
import controllers.Home;
import extensions.IModule;
import extensions.ModularGlobal;
import play.Logger;
import play.Play;
import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class Global extends ModularGlobal {

	public Global() throws Exception {
		super(getModules());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends EssentialFilter> Class<T>[] filters() {
		return new Class[] { GzipFilter.class };
	}

	@Override
	public Promise<Result> onHandlerNotFound(RequestHeader request) {
		return Home.notFoundPromiseResult();
	}

	@Override
	public Promise<Result> onError(RequestHeader request, Throwable t) {
		if (Play.isDev()) {
			return null;
		}
		return Home.errorPromiseResult(request, t);
	}
	@Override
	public Promise<Result> onBadRequest(RequestHeader var1, String var2) {
		return Home.notFoundPromiseResult();
	}

	@SuppressWarnings("unchecked")
	protected static List<Class<? extends IModule>> getModules()
			throws Exception {
		Properties p = new Properties();
		InputStream is = null;
		try{//加入try-catch块，保证文件流的关闭	by ye_ziran 20150728
			String moduleconf = System.getProperty("module.config");
			if (moduleconf != null) {
				Logger.info("Loading from Module Configuration File: {}",
						moduleconf);
				is = new FileInputStream(moduleconf);
			} else {
				// default module config
				is = Global.class.getResourceAsStream("modules.properties");
			}
			p.load(is);
		} catch (Exception e){
			Logger.error("");
		} finally{
			if(is != null){
				is.close();
			}
		}
		List<?> clazzes = FluentIterable.from(p.keySet())
				.filter(k -> k.toString().startsWith("module."))
				.transform(k -> p.getProperty(k.toString()))
				.transform(clazz -> loadClass(clazz)).filter(c -> c != null)
				.toList();
		return (List<Class<? extends IModule>>) clazzes;
	}

	@SuppressWarnings("unchecked")
	protected static Class<? extends IModule> loadClass(String clazz) {
		try {
			return (Class<? extends IModule>) Class.forName(clazz);
		} catch (Exception e) {
			Logger.error("Module class error: " + clazz, e);
			return null;
		}
	}

}
