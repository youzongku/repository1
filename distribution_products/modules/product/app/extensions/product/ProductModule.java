package extensions.product;

import java.util.List;
import java.util.Set;

import org.apache.camel.builder.RouteBuilder;
import org.elasticsearch.client.Client;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

import component.elasticsearch.EsCommonUtil;
import extensions.IModule;
import extensions.ModuleSupport;
import extensions.camel.ICamelExtension;
import extensions.common.CommonModule;
import extensions.event.IEventExtension;
import extensions.filter.IFilter;
import extensions.filter.IFilterExtension;
import extensions.runtime.IApplication;
import handler.AccessLogHandler;
import handler.CalContractFeeHandler;
import handler.ClearanceProductHandler;
import handler.ExportHandler;
import handler.ProductHandler;
import mapper.banner.BannerInfoMapper;
import mapper.category.CategoryBaseMapper;
import mapper.category.ProductVcategoryMapMapper;
import mapper.category.VirtualCategoryMapper;
import mapper.marketing.DisSpriceActivityMapper;
import mapper.marketing.DisSpriceGoodsMapper;
import mapper.marketing.DisSpricePosterMapper;
import mapper.product.ExportModelMapper;
import mapper.product.ExportSyncResultMapper;
import mapper.product.OperateProductPriceMapper;
import mapper.product.OperateProductPriceRuleMapper;
import mapper.product.ProductBaseLogMapper;
import mapper.product.ProductBaseMapper;
import mapper.product.ProductDispriceMapper;
import mapper.product.ProductImageMapper;
import mapper.product.ProductPriceCategoryBrandMapper;
import mapper.product.ProductPriceFactorMapper;
import mapper.product.ProductPriceRuleMapper;
import mapper.product.ProductTranslateMapper;
import mapper.product.TypeBaseMapper;
import mapper.product.WarehouseMapper;
import mybatis.MyBatisExtension;
import mybatis.MyBatisService;
import play.Logger;
import services.product.IBannerService;
import services.product.IEsProductService;
import services.product.IHttpService;
import services.product.IInventoryLockService;
import services.product.IProductBaseService;
import services.product.IProductDispriceService;
import services.product.IProductEnquiryService;
import services.product.IProductExpirationDateService;
import services.product.IProductVcategoryMapService;
import services.product.ISpriceService;
import services.product.ITypeBaseService;
import services.product.IVirtualCategoryService;
import services.product.impl.BannerService;
import services.product.impl.EsProductService;
import services.product.impl.HttpService;
import services.product.impl.InventoryLockService;
import services.product.impl.ProductBaseService;
import services.product.impl.ProductDisPriceService;
import services.product.impl.ProductExpirationDateService;
import services.product.impl.ProductVcategoryMapService;
import services.product.impl.SpriceService;
import services.product.impl.TypeBaseService;
import services.product.impl.VirtualCategoryService;
import services.product.impl.WebUploaderService;

/**
 * 产品模块预加载配置
 * 
 * @author ye_ziran
 * @since 2015年12月8日 下午2:29:03
 */
public class ProductModule extends ModuleSupport implements MyBatisExtension,
		ICamelExtension, IEventExtension, IFilterExtension {

	
	Client client;
	IProductEnquiryService prodEnquiryService;

	@SuppressWarnings("unchecked")
	@Override
	public Set<Class<? extends IModule>> getDependentModules() {
		return Sets.newHashSet(CommonModule.class,ProductStoreModule.class);
	}

	@Override
	public Module getModule(IApplication application) {
		// 获取es配置
		//final IConfiguration esconfig = application.getConfiguration().getConfig("elasticsearch");

		return new AbstractModule() {
			@Override
			protected void configure() {
				bind(IVirtualCategoryService.class).to(VirtualCategoryService.class);
				bind(IProductVcategoryMapService.class).to(ProductVcategoryMapService.class);
				bind(ISpriceService.class).to(SpriceService.class);
				bind(IProductDispriceService.class).to(ProductDisPriceService.class);
				bind(IBannerService.class).to(BannerService.class);
				bind(ITypeBaseService.class).to(TypeBaseService.class);
				bind(IProductBaseService.class).to(ProductBaseService.class);
				bind(IEsProductService.class).to(EsProductService.class);
				bind(IHttpService.class).to(HttpService.class);
				bind(IProductEnquiryService.class).to(ProductBaseService.class);
				bind(IProductExpirationDateService.class).to(ProductExpirationDateService.class);
				bind(IInventoryLockService.class).to(InventoryLockService.class);
				bind(WebUploaderService.class);
			}
		};
	}

	@Override
	public void processConfiguration(MyBatisService service) {
		service.addMapperClass("product", VirtualCategoryMapper.class);
		service.addMapperClass("product", ProductBaseMapper.class);
		service.addMapperClass("product", ProductBaseLogMapper.class);
		service.addMapperClass("product", ProductTranslateMapper.class);
		service.addMapperClass("product", ProductImageMapper.class);
		service.addMapperClass("product", ProductVcategoryMapMapper.class);
		service.addMapperClass("product", CategoryBaseMapper.class);
		service.addMapperClass("product", DisSpriceActivityMapper.class);
		service.addMapperClass("product", DisSpriceGoodsMapper.class);
		service.addMapperClass("product", DisSpricePosterMapper.class);
		service.addMapperClass("product", OperateProductPriceMapper.class);
		service.addMapperClass("product", OperateProductPriceRuleMapper.class);
		service.addMapperClass("product", ProductDispriceMapper.class);
		service.addMapperClass("product", ProductPriceRuleMapper.class);
		service.addMapperClass("product", WarehouseMapper.class);
		service.addMapperClass("product", BannerInfoMapper.class);
		service.addMapperClass("product", ProductPriceCategoryBrandMapper.class);
		service.addMapperClass("product", ProductPriceFactorMapper.class);
		service.addMapperClass("product", TypeBaseMapper.class);
		service.addMapperClass("product", ExportModelMapper.class);
		service.addMapperClass("product", ExportSyncResultMapper.class);
	}

	@Override
	public List<RouteBuilder> getRouteBuilders() {
	    //return Lists.newArrayList();
		return Lists.newArrayList(new ProductTimerTrigger());
	}

	@Override
	public void registerListener(EventBus eventBus, Injector injector) {
		eventBus.register(injector.getInstance(ProductHandler.class));
		eventBus.register(injector.getInstance(AccessLogHandler.class));
		eventBus.register(injector.getInstance(ClearanceProductHandler.class));
		eventBus.register(injector.getInstance(CalContractFeeHandler.class));
		eventBus.register(injector.getInstance(ExportHandler.class));
	}

	@Override
	public void registerFilter(Multibinder<IFilter> filters) {
		filters.addBinding().to(AccessLogFilter.class);
	}

	@Override
	public void onStart(IApplication app, Injector injector) {
		try {
			IEsProductService esService = injector.getInstance(IEsProductService.class);
			esService.createProductIndex();
			esService.initProductDatas();
		} catch (Exception e) {
			Logger.info("es初始化异常{}",e);
		}
	}

	@Override
	public void onStop(IApplication app, Injector injector) {
		EsCommonUtil.getClient().close();
	}

}
