package extensions.timer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.camel.builder.RouteBuilder;
import org.redisson.Config;
import org.redisson.MasterSlaveServersConfig;
import org.redisson.Redisson;
import org.redisson.SentinelServersConfig;

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
import extensions.timer.trigger.TimedTaskTrigger;
import handlers.timer.InventoryModuleEventHandler;
import handlers.timer.MarketModulePromotionStatusHandler;
import handlers.timer.MemberModuleEventHandler;
import handlers.timer.ProductModuleEventHandler;
import handlers.timer.PurchaseModuleEventHandler;
import handlers.timer.SalesModuleEventHandler;
import handlers.timer.SystemEventHandler;
import mapper.timer.AccountPeriodMasterMapper;
import mapper.timer.AccountPeriodSlaveMapper;
import mapper.timer.ApBillMapper;
import mapper.timer.ApBillOrderMappingMapper;
import mapper.timer.ApOptRecordMapper;
import mapper.timer.ApkApplyQueueMapper;
import mapper.timer.ApkVersionMapper;
import mapper.timer.ContractQuotationsMapper;
import mapper.timer.DisAccountMapper;
import mapper.timer.DisActiveMapper;
import mapper.timer.DisApplyMapper;
import mapper.timer.DisBillMapper;
import mapper.timer.DisCouponsMapper;
import mapper.timer.DisSpriceActivityMapper;
import mapper.timer.DisSpriceGoodsMapper;
import mapper.timer.InventorySyncRecordMapper;
import mapper.timer.OperateRecordMapper;
import mapper.timer.OrderByApMapper;
import mapper.timer.ProductInventoryBatchDetailMapper;
import mapper.timer.ProductInventoryDetailMapper;
import mapper.timer.ProductInventoryOrderLockMapper;
import mapper.timer.ProductInventoryTotalMapper;
import mapper.timer.PromotionActivityMapper;
import mapper.timer.PurchaseOrderMapper;
import mapper.timer.SaleBaseMapper;
import mapper.timer.SaleDetailMapper;
import mapper.timer.SaleMainMapper;
import mapper.timer.SequenceMapper;
import mapper.timer.ShopSiteMapper;
import mapper.timer.WarehouseMapper;
import mybatis.MyBatisExtension;
import mybatis.MyBatisService;
import play.Configuration;
import play.Logger;
import play.Play;
import service.timer.IAccountPeriodService;
import service.timer.IActiveService;
import service.timer.IApkApplyService;
import service.timer.IDisBillService;
import service.timer.IHttpService;
import service.timer.IProductCloudInventoryService;
import service.timer.IProductInventoryBatchDetailService;
import service.timer.IPurchaseOrderService;
import service.timer.IQuotedService;
import service.timer.ISaleMainService;
import service.timer.ISaleOrderTaxesService;
import service.timer.ISaleService;
import service.timer.ISalesPushToB2CService;
import service.timer.ISequenceService;
import service.timer.ISessionServiceEx;
import service.timer.IWarehInvenService;
import service.timer.IinventorySyncRecordService;
import service.timer.impl.AccountPeriodService;
import service.timer.impl.ActiveService;
import service.timer.impl.ApkApplyService;
import service.timer.impl.DisBillService;
import service.timer.impl.HttpServiceImpl;
import service.timer.impl.InventorySyncRecordService;
import service.timer.impl.ProductCloudInventoryService;
import service.timer.impl.ProductInventoryBatchDetailService;
import service.timer.impl.PurchaseOrderService;
import service.timer.impl.QuotedServiceImpl;
import service.timer.impl.SaleMainService;
import service.timer.impl.SaleOrderTaxesService;
import service.timer.impl.SaleService;
import service.timer.impl.SalesPushToB2CService;
import service.timer.impl.SequenceService;
import service.timer.impl.SessionServiceEx;
import service.timer.impl.WarehInvenService;

/**
 * 定时任务模块
 * 
 * @author Alvin Du
 */
public class TimerModule extends ModuleSupport implements MyBatisExtension,
		ICamelExtension, IEventExtension {

	@SuppressWarnings("unchecked")
	public Set<Class<? extends IModule>> getDependentModules() {
		return Sets.newHashSet(CommonModule.class);
	}

	@Override
	public Module getModule(IApplication iApplication) {
		return new AbstractModule() {
			@Override
			protected void configure() {
				Configuration sessionConfig = Play.application().configuration().getConfig("session");
		        if (sessionConfig == null) {
		        	
		            Logger.warn("you need to config redis to notify app status");
		            
		        }
		        else{
		        	
		        	final String type = sessionConfig.getString("server_type");
			        final Integer databaseNum = sessionConfig.getInt("database", 0);
			        final List<Object> servers = new LinkedList<Object>();
			        try {
			            servers.addAll(sessionConfig.getList("server_address"));
			        }
			        catch (Exception e) {
			            servers.addAll(Arrays.asList(sessionConfig.getString("server_address").split(",")));
			        }
			        final Long timeout = sessionConfig.getLong("timeout", 3600L);
			        Logger.info("Session Redis(Ex) Server Type: {}", new Object[] { type });
			        Logger.info("Session Redis(Ex) Server Address: {}", new Object[] { servers });
			        Logger.info("Session Redis(Ex) Database: {}", new Object[] { databaseNum });
			        Logger.info("Session Redis(Ex) Expiry: {} sec", new Object[] { timeout });
			        final Config config = new Config();
			        if (type == null || "single".equals(type)) {
			            config.useSingleServer().setAddress(servers.get(0).toString()).setDatabase((int)databaseNum);
			        }
			        else if ("master_slave".equals(type)) {
			            final MasterSlaveServersConfig msconfig = config.useMasterSlaveConnection();
			            boolean masterSet = false;
			            for (final Object a : servers) {
			                if (!masterSet) {
			                    msconfig.setMasterAddress(a.toString());
			                    masterSet = true;
			                }
			                else {
			                    msconfig.addSlaveAddress(new String[] { a.toString() });
			                }
			            }
			            msconfig.setDatabase((int)databaseNum);
			        }
			        else {
			            if (!"sentinel".equals(type)) {
			                throw new RuntimeException("Redis config error, session.server_type must be one of ['single', 'master_slave', 'sentinel']");
			            }
			            final SentinelServersConfig sconfig = config.useSentinelConnection();
			            sconfig.setMasterName("master");
			            for (final Object a2 : servers) {
			                sconfig.addSentinelAddress(new String[] { a2.toString() });
			            }
			            sconfig.setDatabase((int)databaseNum);
			        }
			        
			        final Redisson redisson = Redisson.create(config);
			        final SessionServiceEx sessionServiceEx = new SessionServiceEx(redisson);
			        bind(ISessionServiceEx.class).toInstance(sessionServiceEx);
			        
		        }
		        
				
				/******************* sales **********************/
				bind(ISaleMainService.class).to(SaleMainService.class);
				bind(ISaleService.class).to(SaleService.class);
				bind(ISalesPushToB2CService.class).to(
						SalesPushToB2CService.class);
				bind(IHttpService.class).to(HttpServiceImpl.class);
				bind(ISaleOrderTaxesService.class).to(
						SaleOrderTaxesService.class);

				/******************* purchase **********************/
				bind(IPurchaseOrderService.class)
						.to(PurchaseOrderService.class);

				/******************* member **********************/
				bind(IDisBillService.class).to(DisBillService.class);
				bind(IActiveService.class).to(ActiveService.class);
				bind(ISequenceService.class).to(SequenceService.class);
				bind(IApkApplyService.class).to(ApkApplyService.class);

				bind(IQuotedService.class).to(QuotedServiceImpl.class);
				bind(IAccountPeriodService.class).to(AccountPeriodService.class);
				
				/******************* inventory **********************/
				bind(IWarehInvenService.class).to(WarehInvenService.class);
				bind(IProductCloudInventoryService.class).to(
						ProductCloudInventoryService.class);
				bind(IinventorySyncRecordService.class).to(
						InventorySyncRecordService.class);
				bind(IProductInventoryBatchDetailService.class).to(
						ProductInventoryBatchDetailService.class);

			}
		};
	}

	@Override
	public void processConfiguration(MyBatisService myBatisService) {
		
		
		/******************* sales **********************/
		myBatisService.addMapperClass("b2b_sales", SaleBaseMapper.class);
		myBatisService.addMapperClass("b2b_sales", SaleDetailMapper.class);
		myBatisService.addMapperClass("b2b_sales", SaleMainMapper.class);
		myBatisService.addMapperClass("b2b_sales", OperateRecordMapper.class);

		/******************* purchase **********************/
		myBatisService.addMapperClass("purchase", PurchaseOrderMapper.class);

		/******************* product **********************/
		myBatisService.addMapperClass("product", DisSpriceActivityMapper.class);
		myBatisService.addMapperClass("product", DisSpriceGoodsMapper.class);
		myBatisService.addMapperClass("product", WarehouseMapper.class);
		myBatisService.addMapperClass("b2b_product", ContractQuotationsMapper.class);

		/******************* market **********************/
		myBatisService.addMapperClass("market", PromotionActivityMapper.class);

		/******************* member **********************/
		myBatisService.addMapperClass("dismember", DisAccountMapper.class);
		myBatisService.addMapperClass("dismember", DisApplyMapper.class);
		myBatisService.addMapperClass("dismember", DisBillMapper.class);
		myBatisService.addMapperClass("dismember", DisActiveMapper.class);// 优惠活动表
		myBatisService.addMapperClass("dismember", DisCouponsMapper.class); // 优惠码表
		myBatisService.addMapperClass("dismember", ShopSiteMapper.class);
		myBatisService.addMapperClass("dismember", SequenceMapper.class);
		myBatisService.addMapperClass("dismember", ApkApplyQueueMapper.class);// apk打包申请
		myBatisService.addMapperClass("dismember", ApkVersionMapper.class);// apk版本升级
		myBatisService.addMapperClass("dismember", AccountPeriodSlaveMapper.class);// 账期
		myBatisService.addMapperClass("dismember", AccountPeriodMasterMapper.class);// 账期
		myBatisService.addMapperClass("dismember", ApBillMapper.class);// 账期
		myBatisService.addMapperClass("dismember", ApBillOrderMappingMapper.class);// 账期
		myBatisService.addMapperClass("dismember", ApOptRecordMapper.class);// 账期
		myBatisService.addMapperClass("dismember", OrderByApMapper.class);// 账期
		myBatisService.addMapperClass("b2b_inventory",ProductInventoryBatchDetailMapper.class);
		myBatisService.addMapperClass("b2b_inventory",ProductInventoryOrderLockMapper.class);
		myBatisService.addMapperClass("b2b_inventory",ProductInventoryTotalMapper.class);
		myBatisService.addMapperClass("b2b_inventory",ProductInventoryDetailMapper.class);
		myBatisService.addMapperClass("b2b_inventory",InventorySyncRecordMapper.class);

	}

	@Override
	public List<RouteBuilder> getRouteBuilders() {
		return Lists.newArrayList(new TimedTaskTrigger());
	}

	@Override
	public void registerListener(EventBus eventBus, Injector injector) {
		// 系统事件
		eventBus.register(injector.getInstance(SystemEventHandler.class));
		// 采购模块事件
		eventBus.register(injector.getInstance(PurchaseModuleEventHandler.class));
		// 销售模块事件
		eventBus.register(injector.getInstance(SalesModuleEventHandler.class));
		// 商品模块事件
		eventBus.register(injector.getInstance(ProductModuleEventHandler.class));
		// 营销模块事件
		eventBus.register(injector.getInstance(MarketModulePromotionStatusHandler.class));
		// 用户模块事件
		eventBus.register(injector.getInstance(MemberModuleEventHandler.class));
		// 库存模块事件
		eventBus.register(injector.getInstance(InventoryModuleEventHandler.class));
	}
}
