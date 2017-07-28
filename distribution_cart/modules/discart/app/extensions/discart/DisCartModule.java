package extensions.discart;

import java.util.Set;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;

import extensions.IModule;
import extensions.ModuleSupport;
import extensions.common.CommonModule;
import extensions.event.IEventExtension;
import extensions.runtime.IApplication;
import handlers.discart.AccessLogHandler;
import mapper.discart.DisCartItemMapper;
import mapper.discart.DisCartMapper;
import mybatis.MyBatisExtension;
import mybatis.MyBatisService;
import service.discart.IDisCartItemService;
import service.discart.IDisCartService;
import service.discart.IHttpService;
import service.discart.IUserService;
import service.discart.impl.DisCartItemService;
import service.discart.impl.DisCartService;
import service.discart.impl.HttpServiceImpl;
import service.discart.impl.UserService;

public class DisCartModule extends ModuleSupport implements MyBatisExtension,IEventExtension{

	@SuppressWarnings("unchecked")
	public Set<Class<? extends IModule>> getDependentModules() {
        return Sets.newHashSet(CommonModule.class);
    }

    @Override
    public Module getModule(IApplication iApplication) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(IDisCartService.class).to(DisCartService.class);
                bind(IDisCartItemService.class).to(DisCartItemService.class);
                bind(IUserService.class).to(UserService.class);
                bind(IHttpService.class).to(HttpServiceImpl.class);
            }
        };
    }


    @Override
    public void processConfiguration(MyBatisService myBatisService) {
        myBatisService.addMapperClass("discart",DisCartMapper.class);
        myBatisService.addMapperClass("discart",DisCartItemMapper.class);
    }


	@Override
	public void registerListener(EventBus eventBus, Injector injector) {
		eventBus.register(injector.getInstance(AccessLogHandler.class));
	}
}
