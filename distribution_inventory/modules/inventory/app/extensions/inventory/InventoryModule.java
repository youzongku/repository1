package extensions.inventory;

import java.util.List;
import java.util.Set;

import org.apache.camel.builder.RouteBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;

import component.elasticsearch.IInventoryComponent;
import component.elasticsearch.InventoryComponent;
import extensions.IModule;
import extensions.ModuleSupport;
import extensions.camel.ICamelExtension;
import extensions.common.CommonModule;
import extensions.event.IEventExtension;
import extensions.runtime.IApplication;
import handlers.inventory.AccessLogHandler;
import handlers.inventory.CheckInventoryHandler;
import handlers.inventory.ExternalWarehouseInventorySynchronizingHandler;
import handlers.inventory.InventorySynchronizingHandler;
import handlers.inventory.PhysicalInventoryHandler;
import handlers.product_inventory.ProductReleaseCloudLockHandler;
import mapper.product_inventory.*;
import mybatis.MyBatisExtension;
import mybatis.MyBatisService;

import mapper.inventory.DisInventoryMapper;
import mapper.inventory.DisProductMapper;
import mapper.inventory.InventoryChangeRecordDetailMapper;
import mapper.inventory.InventoryChangeRecordMainMapper;
import mapper.inventory.RefundDetailMapper;
import mapper.inventory.RefundMapper;
import mapper.inventory.WarehouseInventoryMapper;
import mapper.inventory.WarehouseMapper;
import mapper.inventory.WarehouseProductMapper;
import mapper.product_inventory.OrderDetailMapper;
import mapper.product_inventory.OrderInventoryDeductRecordMapper;
import mapper.product_inventory.OrderMapper;
import mapper.product_inventory.OrderMicroInventoryDeductRecordMapper;
import mapper.product_inventory.OrderStatusChangeRecordMapper;
import mapper.product_inventory.ProductInventoryBatchDetailMapper;
import mapper.product_inventory.ProductInventoryDetailMapper;
import mapper.product_inventory.ProductInventoryInRecordMapper;
import mapper.product_inventory.ProductInventoryOrderLockMapper;
import mapper.product_inventory.ProductInventoryTotalMapper;
import mapper.product_inventory.ProductMicroInventoryDetailMapper;
import mapper.product_inventory.ProductMicroInventoryInRecordMapper;
import mapper.product_inventory.ProductMicroInventoryOrderLockMapper;
import mapper.product_inventory.ProductMicroInventoryTotalMapper;
import services.inventory.IInventoryRecordService;
import services.inventory.IInventoryService;
import services.inventory.IWarehInvenService;
import services.inventory.impl.InventoryRecordService;
import services.inventory.impl.InventoryService;
import services.inventory.impl.WarehInvenService;
import services.product_inventory.*;
import services.product_inventory.impl.*;
import timer.product_inventory.ProductInventoryTrigger;
import timer.product_inventory.ProductReleaseCloudLockTrigger;
import services.product_inventory.IOrderService;
import services.product_inventory.IProductCloudInventoryService;
import services.product_inventory.IProductInventoryOrderLockService;
import services.product_inventory.IProductInventoryService;
import services.product_inventory.IProductMicroInventoryService;
import services.product_inventory.impl.OrderService;
import services.product_inventory.impl.ProductCloudInventoryService;
import services.product_inventory.impl.ProductInventoryOrderLockService;
import services.product_inventory.impl.ProductInventoryService;
import services.product_inventory.impl.ProductMicroInventoryService;

public class InventoryModule extends ModuleSupport implements MyBatisExtension,
		IEventExtension, ICamelExtension {

	@SuppressWarnings("unchecked")
	public Set<Class<? extends IModule>> getDependentModules() {
		return Sets.newHashSet(CommonModule.class);
	}

	@Override
	public Module getModule(IApplication iApplication) {
		return new AbstractModule() {
			@Override
			protected void configure() {
				bind(IInventoryService.class).to(InventoryService.class);
				bind(IInventoryRecordService.class).to(
						InventoryRecordService.class);
				bind(IWarehInvenService.class).to(WarehInvenService.class);
				bind(IProductCloudInventoryService.class).to(
						ProductCloudInventoryService.class);
				bind(IOrderService.class).to(OrderService.class);
				bind(IProductInventoryOrderLockService.class).to(
						ProductInventoryOrderLockService.class);
				bind(IProductInventoryService.class).to(
						ProductInventoryService.class);
				bind(IProductMicroInventoryService.class).to(
						ProductMicroInventoryService.class);
				bind(IinventorySyncRecordService.class).to(
						InventorySyncRecordService.class);
				bind(IProductInventoryDetailService.class).to(
						ProductInventoryDetailService.class);
				bind(IProductInventoryBatchDetailService.class).to(
						ProductInventoryBatchDetailService.class);

				bind(IInventoryComponent.class).to(InventoryComponent.class);
			}
		};
	}

	@Override
	public void processConfiguration(MyBatisService myBatisService) {
		myBatisService
				.addMapperClass("b2b_inventory", DisInventoryMapper.class);
		myBatisService.addMapperClass("b2b_inventory", DisProductMapper.class);
		myBatisService.addMapperClass("b2b_inventory", RefundMapper.class);
		myBatisService
				.addMapperClass("b2b_inventory", RefundDetailMapper.class);
		myBatisService.addMapperClass("b2b_inventory", WarehouseMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				WarehouseInventoryMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				WarehouseProductMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				InventoryChangeRecordMainMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				InventoryChangeRecordDetailMapper.class);

		myBatisService.addMapperClass("b2b_inventory", OrderDetailMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				OrderInventoryDeductRecordMapper.class);
		myBatisService.addMapperClass("b2b_inventory", OrderMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				OrderMicroInventoryDeductRecordMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				ProductMicroInventoryOrderLockMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				ProductInventoryInRecordMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				OrderStatusChangeRecordMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				ProductInventoryBatchDetailMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				ProductInventoryOrderLockMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				ProductInventoryTotalMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				ProductMicroInventoryDetailMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				ProductMicroInventoryInRecordMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				ProductMicroInventoryTotalMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				ProductInventoryDetailMapper.class);
		myBatisService.addMapperClass("b2b_inventory",
				InventorySyncRecordMapper.class);
	}

	@Override
	public void registerListener(EventBus eventBus, Injector injector) {
		eventBus.register(injector.getInstance(AccessLogHandler.class));
		eventBus.register(injector
				.getInstance(InventorySynchronizingHandler.class));
		eventBus.register(injector
				.getInstance(ProductReleaseCloudLockHandler.class));
		eventBus.register(injector
						.getInstance(ExternalWarehouseInventorySynchronizingHandler.class));
		eventBus.register(injector
				.getInstance(PhysicalInventoryHandler.class));
		eventBus.register(injector
				.getInstance(CheckInventoryHandler.class));
	}

	@Override
	public List<RouteBuilder> getRouteBuilders() {
	    //return Lists.newArrayList();
		return Lists.newArrayList(new ProductReleaseCloudLockTrigger(),
				new ProductInventoryTrigger());
	}
}
