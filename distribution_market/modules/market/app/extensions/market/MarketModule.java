package extensions.market;

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
import extensions.market.timer.PromotionStatusTimerTrigger;
import extensions.runtime.IApplication;
import mapper.marketing.promotion.ActivityInformationLogMapper;
import mapper.marketing.promotion.ActivityInstanceMapper;
import mapper.marketing.promotion.ConditionDataSourceMapper;
import mapper.marketing.promotion.ConditionInstanceExtMapper;
import mapper.marketing.promotion.ConditionInstanceMapper;
import mapper.marketing.promotion.ConditionJudgementTypeMapper;
import mapper.marketing.promotion.PrivilegeDataSourceMapper;
import mapper.marketing.promotion.PrivilegeInstanceMapper;
import mapper.marketing.promotion.PrivilegeJudgementTypeMapper;
import mapper.marketing.promotion.PromotionActivityDisModeMapper;
import mapper.marketing.promotion.PromotionActivityMapper;
import mapper.marketing.promotion.PromotionConditionMapper;
import mapper.marketing.promotion.PromotionPrivilegeMapper;
import mapper.marketing.promotion.PromotionTypeMapper;
import mapper.marketing.promotion.RelConditionDataSourceMapper;
import mapper.marketing.promotion.RelConditionJudgementTypeMapper;
import mapper.marketing.promotion.RelPrivilegeDataSourceMapper;
import mapper.marketing.promotion.RelPrivilegeJudgementTypeMapper;
import mapper.marketing.promotion.RelPromotionTypePrivilegeConditionMapper;
import mybatis.MyBatisExtension;
import mybatis.MyBatisService;
import promotion.handler.AccessLogHandler;
import promotion.handler.PromotionStatusHandler;
import services.marketing.promotion.IHttpService;
import services.marketing.promotion.IPromotionService;
import services.marketing.promotion.IPromotionTypeService;
import services.marketing.promotion.IUserService;
import services.marketing.promotion.impl.HttpServiceImpl;
import services.marketing.promotion.impl.PromotionService;
import services.marketing.promotion.impl.PromotionTypeService;
import services.marketing.promotion.impl.UserService;

public class MarketModule extends ModuleSupport implements MyBatisExtension,ICamelExtension,IEventExtension{

	@SuppressWarnings("unchecked")
	public Set<Class<? extends IModule>> getDependentModules() {
        return Sets.newHashSet(CommonModule.class);
    }

    @Override
    public Module getModule(IApplication iApplication) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(IPromotionTypeService.class).to(PromotionTypeService.class);
                bind(IPromotionService.class).to(PromotionService.class);
                bind(IHttpService.class).to(HttpServiceImpl.class);
                bind(IUserService.class).to(UserService.class);
            }
        };
    }


    @Override
    public void processConfiguration(MyBatisService myBatisService) {
    	myBatisService.addMapperClass("market",ActivityInformationLogMapper.class);
    	myBatisService.addMapperClass("market",ActivityInstanceMapper.class);
        myBatisService.addMapperClass("market",ConditionDataSourceMapper.class);
        myBatisService.addMapperClass("market",ConditionInstanceExtMapper.class);
        myBatisService.addMapperClass("market",ConditionInstanceMapper.class);
        myBatisService.addMapperClass("market",ConditionJudgementTypeMapper.class);
        myBatisService.addMapperClass("market",PrivilegeDataSourceMapper.class);
        myBatisService.addMapperClass("market",PrivilegeInstanceMapper.class);
        myBatisService.addMapperClass("market",PrivilegeJudgementTypeMapper.class);
        myBatisService.addMapperClass("market",PromotionActivityDisModeMapper.class);
        myBatisService.addMapperClass("market",PromotionActivityMapper.class);
        myBatisService.addMapperClass("market",PromotionConditionMapper.class);
        myBatisService.addMapperClass("market",PromotionPrivilegeMapper.class);
        myBatisService.addMapperClass("market",PromotionTypeMapper.class);
        myBatisService.addMapperClass("market",RelConditionDataSourceMapper.class);
        myBatisService.addMapperClass("market",RelConditionJudgementTypeMapper.class);
        myBatisService.addMapperClass("market",RelPrivilegeDataSourceMapper.class);
        myBatisService.addMapperClass("market",RelPrivilegeJudgementTypeMapper.class);
        myBatisService.addMapperClass("market",RelPromotionTypePrivilegeConditionMapper.class);
    }

	@Override
	public void registerListener(EventBus eventBus, Injector injector) {
		eventBus.register(injector.getInstance(PromotionStatusHandler.class));
		eventBus.register(injector.getInstance(AccessLogHandler.class));
	}

	@Override
	public List<RouteBuilder> getRouteBuilders() {
	    //return Lists.newArrayList();
		return Lists.newArrayList(new PromotionStatusTimerTrigger());
	}
}
