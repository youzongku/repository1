package extensions.openapi;

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
import handlers.openapi.AccessLogHandler;
import services.openapi.IHttpService;
import services.openapi.IInventoryService;
import services.openapi.ILoginService;
import services.openapi.IProductService;
import services.openapi.IPurchaseService;
import services.openapi.ISaleService;
import services.openapi.impl.HttpServiceImpl;
import services.openapi.impl.InventoryService;
import services.openapi.impl.LoginService;
import services.openapi.impl.ProductService;
import services.openapi.impl.PurchaseService;
import services.openapi.impl.SaleService;

public class ApiModule extends ModuleSupport implements IEventExtension{

	@SuppressWarnings("unchecked")
	public Set<Class<? extends IModule>> getDependentModules() {
        return Sets.newHashSet(CommonModule.class);
    }
	
	@Override
	public Module getModule(IApplication arg0) {
		return new AbstractModule() {
			@Override
			protected void configure() {
				bind(ILoginService.class).to(LoginService.class);
				bind(IInventoryService.class).to(InventoryService.class);
				bind(IPurchaseService.class).to(PurchaseService.class);
				bind(IHttpService.class).to(HttpServiceImpl.class);
				bind(IProductService.class).to(ProductService.class);
				bind(ISaleService.class).to(SaleService.class);
			}
		};
	}

	@Override
	public void registerListener(EventBus eventBus, Injector injector) {
		eventBus.register(injector.getInstance(AccessLogHandler.class));
	}

}
