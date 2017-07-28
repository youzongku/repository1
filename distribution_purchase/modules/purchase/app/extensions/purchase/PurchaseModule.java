package extensions.purchase;

import java.util.List;
import java.util.Set;

import org.apache.camel.builder.RouteBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;

import extensions.IModule;
import extensions.ModuleSupport;
import extensions.camel.ICamelExtension;
import extensions.common.CommonModule;
import extensions.event.IEventExtension;
import extensions.purchase.timer.PurchaseTimerTrigger;
import extensions.runtime.IApplication;
import handlers.purchase.AccessLogHandler;
import handlers.purchase.PurchaseActiveHandler;
import handlers.purchase.PurchaseHandler;
import mapper.purchase.DisQuotationMapper;
import mapper.purchase.PurchaseAuditMapper;
import mapper.purchase.PurchaseGiftRecordMapper;
import mapper.purchase.PurchaseOrderAuditLogMapper;
import mapper.purchase.PurchaseOrderDetailMapper;
import mapper.purchase.PurchaseOrderInputGiftMapper;
import mapper.purchase.PurchaseOrderInputMapper;
import mapper.purchase.PurchaseOrderInputProMapper;
import mapper.purchase.PurchaseOrderMapper;
import mapper.purchase.PurchaseRecordMapper;
import mapper.purchase.PurchaseStockoutMapper;
import mapper.purchase.SequenceMapper;
import mapper.purchase.returnod.ReturnAmountCoefficientLogMapper;
import mapper.purchase.returnod.ReturnAmountCoefficientMapper;
import mapper.purchase.returnod.ReturnOrderDetailMapper;
import mapper.purchase.returnod.ReturnOrderLogMapper;
import mapper.purchase.returnod.ReturnOrderMapper;
import mybatis.MyBatisExtension;
import mybatis.MyBatisService;
import services.purchase.IDisQuotationService;
import services.purchase.IHttpService;
import services.purchase.IPurchaseOrderAuditService;
import services.purchase.IPurchaseOrderDetailService;
import services.purchase.IPurchaseOrderManagerService;
import services.purchase.IPurchaseOrderService;
import services.purchase.IPurchaseOrderTypeInService;
import services.purchase.ISequenceService;
import services.purchase.IUserService;
import services.purchase.impl.DisQuotationService;
import services.purchase.impl.HttpServiceImpl;
import services.purchase.impl.PurchaseOrderAuditService;
import services.purchase.impl.PurchaseOrderDetailService;
import services.purchase.impl.PurchaseOrderManagerService;
import services.purchase.impl.PurchaseOrderService;
import services.purchase.impl.PurchaseOrderTypeInService;
import services.purchase.impl.SequenceService;
import services.purchase.impl.UserService;
import services.purchase.returnod.IReturnAmountCoefficientService;
import services.purchase.returnod.IReturnOrderService;
import services.purchase.returnod.impl.ReturnAmountCoefficientService;
import services.purchase.returnod.impl.ReturnOrderService;

public class PurchaseModule extends ModuleSupport implements MyBatisExtension, ICamelExtension, IEventExtension{

	@SuppressWarnings("unchecked")
	public Set<Class<? extends IModule>> getDependentModules() {
        return Sets.newHashSet(CommonModule.class);
    }

    @Override
    public Module getModule(IApplication iApplication) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(IPurchaseOrderService.class).to(PurchaseOrderService.class);
                bind(IPurchaseOrderDetailService.class).to(PurchaseOrderDetailService.class);
                bind(ISequenceService.class).to(SequenceService.class);
                bind(IPurchaseOrderTypeInService.class).to(PurchaseOrderTypeInService.class);
                bind(IHttpService.class).to(HttpServiceImpl.class);
                bind(IUserService.class).to(UserService.class);
                bind(IDisQuotationService.class).to(DisQuotationService.class);
                bind(IPurchaseOrderAuditService.class).to(PurchaseOrderAuditService.class);
                bind(IReturnOrderService.class).to(ReturnOrderService.class);
                bind(IReturnAmountCoefficientService.class).to(ReturnAmountCoefficientService.class);
                bind(IPurchaseOrderManagerService.class).to(PurchaseOrderManagerService.class);
            }
        };
    }

    @Override
    public void processConfiguration(MyBatisService myBatisService) {
        myBatisService.addMapperClass("purchase", PurchaseOrderMapper.class);
        myBatisService.addMapperClass("purchase", PurchaseOrderDetailMapper.class);
        myBatisService.addMapperClass("purchase", SequenceMapper.class);
        myBatisService.addMapperClass("purchase", PurchaseRecordMapper.class);
        myBatisService.addMapperClass("purchase", PurchaseOrderInputMapper.class);
        myBatisService.addMapperClass("purchase", PurchaseOrderInputProMapper.class);
        myBatisService.addMapperClass("purchase", PurchaseOrderInputGiftMapper.class);
        myBatisService.addMapperClass("purchase", PurchaseGiftRecordMapper.class);
        myBatisService.addMapperClass("purchase", PurchaseAuditMapper.class);
        myBatisService.addMapperClass("purchase", DisQuotationMapper.class);
        myBatisService.addMapperClass("purchase", PurchaseOrderAuditLogMapper.class);
        myBatisService.addMapperClass("purchase", PurchaseStockoutMapper.class);
        myBatisService.addMapperClass("purchase", ReturnOrderMapper.class);
        myBatisService.addMapperClass("purchase", ReturnOrderDetailMapper.class);
        myBatisService.addMapperClass("purchase", ReturnOrderLogMapper.class);
        myBatisService.addMapperClass("purchase", ReturnAmountCoefficientMapper.class);
        myBatisService.addMapperClass("purchase", ReturnAmountCoefficientLogMapper.class);
    }
    
    @Override
    public List<RouteBuilder> getRouteBuilders() {
        //return Lists.newArrayList();
        return Lists.newArrayList(new PurchaseTimerTrigger());
    }

    @Override
    public void registerListener(EventBus eventBus, Injector injector) {
        eventBus.register(injector.getInstance(PurchaseHandler.class));
        eventBus.register(injector.getInstance(PurchaseActiveHandler.class));
        eventBus.register(injector.getInstance(AccessLogHandler.class));
    }
}
