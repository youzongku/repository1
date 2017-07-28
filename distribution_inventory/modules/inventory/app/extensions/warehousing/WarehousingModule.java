package extensions.warehousing;

import java.util.Set;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

import extensions.IModule;
import extensions.ModuleSupport;
import extensions.common.CommonModule;
import extensions.runtime.IApplication;
import mapper.product.ProductDispriceMapper;
import mapper.product_inventory.ProductBaseMapper;
import mapper.warehousing.ErpPushInvenDetailMapper;
import mapper.warehousing.ErpPushInvenRecodeMapper;
import mapper.warehousing.GoodsInventoryMapper;
import mapper.warehousing.InventoryChangeHistoryMapper;
import mapper.warehousing.MicroGoodsInventoryMapper;
import mapper.warehousing.MicroWarehouseMapper;
import mapper.warehousing.WarehouseMapper;
import mybatis.MyBatisExtension;
import mybatis.MyBatisService;
import services.inventory.IUserService;
import services.inventory.impl.UserService;
import services.product_inventory.IProductBaseService;
import services.product_inventory.impl.ProductBaseService;
import services.warehousing.IGoodsInventoryService;
import services.warehousing.IInventoryChangeHistoryService;
import services.warehousing.IMicroGoodsInventoryService;
import services.warehousing.IMicroWarehouseService;
import services.warehousing.IWarehInvenService;
import services.warehousing.IWarehouseService;
import services.warehousing.impl.GoodsInventoryService;
import services.warehousing.impl.InventoryChangeHistoryService;
import services.warehousing.impl.MicroGoodsInventoryService;
import services.warehousing.impl.MicroWarehouseService;
import services.warehousing.impl.WarehInvenService;
import services.warehousing.impl.WarehouseService;

public class WarehousingModule extends ModuleSupport implements MyBatisExtension{
	@SuppressWarnings("unchecked")
	@Override
	public Set<Class<? extends IModule>> getDependentModules() {
		return Sets.newHashSet(CommonModule.class);
	}

	@Override
	public Module getModule(IApplication application) {
		return new AbstractModule() {
			@Override
			protected void configure() {
				bind(IWarehInvenService.class).to(WarehInvenService.class);
				bind(IInventoryChangeHistoryService.class).to(InventoryChangeHistoryService.class);
				bind(IMicroWarehouseService.class).to(MicroWarehouseService.class);
				bind(IMicroGoodsInventoryService.class).to(MicroGoodsInventoryService.class);
				bind(IWarehouseService.class).to(WarehouseService.class);
				bind(IUserService.class).to(UserService.class);
				bind(IGoodsInventoryService.class).to(GoodsInventoryService.class);
				bind(IProductBaseService.class).to(ProductBaseService.class);
			}
		};
	}

	@Override
	public void processConfiguration(MyBatisService service) {
		service.addMapperClass("warehousing", GoodsInventoryMapper.class);
		service.addMapperClass("warehousing", WarehouseMapper.class);
		service.addMapperClass("warehousing", InventoryChangeHistoryMapper.class);
		service.addMapperClass("warehousing", MicroWarehouseMapper.class);
		service.addMapperClass("warehousing", MicroGoodsInventoryMapper.class);
		service.addMapperClass("warehousing", ErpPushInvenDetailMapper.class);
		service.addMapperClass("warehousing", ErpPushInvenRecodeMapper.class);
		service.addMapperClass("warehousing", ProductDispriceMapper.class);
		service.addMapperClass("warehousing", ProductBaseMapper.class);
	}
}
