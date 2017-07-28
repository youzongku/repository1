package extensions.sales;

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
import extensions.runtime.IApplication;
import extensions.sales.timer.SalesPushTimerTrigger;
import handlers.sales.*;
import mapper.marketing.*;
import mapper.sales.*;
import mapper.sales.hb.*;
import mybatis.MyBatisExtension;
import mybatis.MyBatisService;
import services.marketing.IMarketingOrderService;
import services.marketing.impl.MarketingOrderService;
import services.sales.*;
import services.sales.impl.*;

/**
 * Created by luwj on 2015/12/15.
 */
public class SalesModule extends ModuleSupport implements MyBatisExtension, ICamelExtension, IEventExtension {

	@SuppressWarnings("unchecked")
	public Set<Class<? extends IModule>> getDependentModules() {
		return Sets.newHashSet(CommonModule.class);
	}

	@Override
	public Module getModule(IApplication iApplication) {
		return new AbstractModule() {
			@Override
			protected void configure() {
				bind(ISaleMainService.class).to(SaleMainService.class);
				bind(ISaleReceiverService.class).to(SaleReceiverService.class);
				bind(ISaleDetailsService.class).to(SaleDetailsService.class);
				bind(ISaleBaseService.class).to(SaleBaseService.class);
				bind(IOperateRecordService.class).to(OperateRecordService.class);
				bind(ISaleService.class).to(SaleService.class);
				bind(ISalesPushToB2CService.class).to(SalesPushToB2CService.class);
				bind(IOrderPackService.class).to(OrderPackService.class);
				bind(ILogisticsTracingService.class).to(LogisticsTracingService.class);
                bind(ITaoBaoOrderService.class).to(TaoBaoOrderService.class);
                bind(ITaoBaoOrderGoodsService.class).to(TaoBaoOrderGoodsService.class);
                bind(ISequenceService.class).to(SequenceService.class);
                bind(IImportOrderService.class).to(ImportOrderService.class);
                bind(IHttpService.class).to(HttpServiceImpl.class);
                bind(IJdService.class).to(JdServiceImpl.class);
                bind(IYZService.class).to(YZService.class);
                bind(IMSiteSalesService.class).to(MSiteSalesService.class);
                bind(ISaleInputService.class).to(SaleInputService.class);
                bind(IUserService.class).to(UserService.class);
                bind(ISaleOrderTaxesService.class).to(SaleOrderTaxesService.class);
                bind(IMarketingOrderService.class).to(MarketingOrderService.class);
                bind(ISaleLockService.class).to(SaleLockService.class);
                bind(IProductExpirationDateService.class).to(ProductExpirationDateService.class);
                bind(IManagerOrderService.class).to(ManagerOrderService.class);
                bind(IPinTwoDuoService.class).to(PinTwoDuoService.class);
                bind(IManagerImportOrderService.class).to(ManagerImportOrderService.class);
                bind(ISaleAfterService.class).to(SaleAfterService.class);
                bind(ICombineSaleService.class).to(CombineSaleService.class);
                bind(IKdnService.class).to(KdnService.class);
                bind(ISalesContractService.class).to(SalesContractService.class);
                bind(ISaleInvoiceService.class).to(SaleInvoiceService.class);
                bind(IAsyncExportService.class).to(AsyncExportService.class);
            }
        };
    }

	@Override
	public void processConfiguration(MyBatisService myBatisService) {
		myBatisService.addMapperClass("b2b_sales", PlatformConfigMapper.class);
		myBatisService.addMapperClass("b2b_sales", ReceiverMapper.class);
		myBatisService.addMapperClass("b2b_sales", SaleBaseMapper.class);
		myBatisService.addMapperClass("b2b_sales", SaleDetailMapper.class);
		myBatisService.addMapperClass("b2b_sales", SaleMainMapper.class);
		myBatisService.addMapperClass("b2b_sales", OperateRecordMapper.class);
		myBatisService.addMapperClass("b2b_sales", OrderPackMapper.class);
		myBatisService.addMapperClass("b2b_sales", LogisticsTracingMapper.class);
		myBatisService.addMapperClass("b2b_sales", TimerRecordMapper.class);
		myBatisService.addMapperClass("b2b_sales", TaoBaoOrderMapper.class);
		myBatisService.addMapperClass("b2b_sales", TaoBaoOrderGoodsMapper.class);

		myBatisService.addMapperClass("b2b_sales", SequenceMapper.class);
		myBatisService.addMapperClass("b2b_sales", PayWarehouseMapper.class);
		myBatisService.addMapperClass("b2b_sales", ImportOrderTemplateFieldMapper.class);
		myBatisService.addMapperClass("b2b_sales", OrderTimeConfigMapper.class);
		myBatisService.addMapperClass("b2b_sales", SaleInputMapper.class);
		myBatisService.addMapperClass("b2b_sales", SaleBufferMapper.class);
		myBatisService.addMapperClass("b2b_sales", AuditRemarkMapper.class);

		myBatisService.addMapperClass("b2b_sales", MarketingOrderMapper.class);
		myBatisService.addMapperClass("b2b_sales", MarketingOrderDetailMapper.class);
		myBatisService.addMapperClass("b2b_sales", MarketingOrderAuditLogMapper.class);

        myBatisService.addMapperClass("b2b_sales", ShOrderMapper.class);
        myBatisService.addMapperClass("b2b_sales", ShOrderDetailMapper.class);
        myBatisService.addMapperClass("b2b_sales", ShAttachmentMapper.class);
        myBatisService.addMapperClass("b2b_sales", ShLogMapper.class);
        myBatisService.addMapperClass("b2b_sales", PddLogisticsMapper.class);
        myBatisService.addMapperClass("b2b_sales", SalesHBDeliveryMapper.class);
        myBatisService.addMapperClass("b2b_sales", SalesHBDeliveryDetailMapper.class);
        myBatisService.addMapperClass("b2b_sales", SalesHBDeliveryLogMapper.class);
        myBatisService.addMapperClass("b2b_sales", KdnOrderMapper.class);
        myBatisService.addMapperClass("b2b_sales", SaleContractFeeMapper.class);
        myBatisService.addMapperClass("b2b_sales", SaleInvoiceMapper.class);
        myBatisService.addMapperClass("b2b_sales", OrderExportTimeConfigMapper.class);
    }

	@Override
	public List<RouteBuilder> getRouteBuilders() {
		// return Lists.newArrayList();
		return Lists.newArrayList(new SalesPushTimerTrigger());
	}

	@Override
	public void registerListener(EventBus eventBus, Injector injector) {
		eventBus.register(injector.getInstance(SalesPushHandler.class));
		eventBus.register(injector.getInstance(AccessLogHandler.class));
		eventBus.register(injector.getInstance(SaleOrderTaxesHandler.class));
		eventBus.register(injector.getInstance(CalculateOptFeeHandler.class));
		eventBus.register(injector.getInstance(AcceptLogisticsHandler.class));
		eventBus.register(injector.getInstance(SalesVolumeCountHandler.class));
		eventBus.register(injector.getInstance(OrderOnlineHandler.class));
		eventBus.register(injector.getInstance(SaveReceiverAddressHandler.class));
	}
}
