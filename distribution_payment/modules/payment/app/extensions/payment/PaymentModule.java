package extensions.payment;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

import extensions.IModule;
import extensions.ModuleSupport;
import extensions.common.CommonModule;
import extensions.event.IEventExtension;
import extensions.filter.IFilter;
import extensions.filter.IFilterExtension;
import extensions.runtime.IApplication;
import handlers.payment.AccessLogHandler;
import mapper.payment.alipay.AlipayConfigMapper;
import mapper.payment.alipay.AlipayOrderMapper;
import mapper.payment.alipay.AlipayRefundMapper;
import mapper.payment.alipay.AlipayResultMapper;
import mapper.payment.alipay.PaymentRecordMapper;
import mapper.payment.shengpay.ShengAccountBalanceMapper;
import mapper.payment.shengpay.ShengConfigMapper;
import mapper.payment.shengpay.ShengOrderMapper;
import mapper.payment.shengpay.ShengResultMapper;
import mapper.payment.wechat.WechatConfigMapper;
import mapper.payment.wechat.WechatOrderMapper;
import mapper.payment.wechat.WechatPayResultMapper;
import mapper.payment.wechat.WechatRefundResultMapper;
import mapper.payment.yijipay.*;
import mybatis.MyBatisExtension;
import mybatis.MyBatisService;
import services.payment.IAlipayService;
import services.payment.IDealInventoryService;
import services.payment.IShengPayService;
import services.payment.IWechatService;
import services.payment.IYijipayService;
import services.payment.impl.AlipayService;
import services.payment.impl.DealInventoryServiceImpl;
import services.payment.impl.ShengPayService;
import services.payment.impl.WechatService;
import services.payment.impl.YijipayService;

import java.util.Set;

public class PaymentModule extends ModuleSupport implements MyBatisExtension,IEventExtension,IFilterExtension{

	@SuppressWarnings("unchecked")
	public Set<Class<? extends IModule>> getDependentModules() {
        return Sets.newHashSet(CommonModule.class);
    }

    @Override
    public Module getModule(IApplication iApplication) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(IAlipayService.class).to(AlipayService.class);
                bind(IWechatService.class).to(WechatService.class);
                bind(IYijipayService.class).to(YijipayService.class);
                bind(IShengPayService.class).to(ShengPayService.class);
                bind(IDealInventoryService.class).to(DealInventoryServiceImpl.class);
            }
        };
    }


    @Override
    public void processConfiguration(MyBatisService myBatisService) {
        myBatisService.addMapperClass("payment", AlipayConfigMapper.class);
        myBatisService.addMapperClass("payment", AlipayOrderMapper.class);
        myBatisService.addMapperClass("payment", AlipayRefundMapper.class);
        myBatisService.addMapperClass("payment", AlipayResultMapper.class);
        myBatisService.addMapperClass("payment", WechatConfigMapper.class);
        myBatisService.addMapperClass("payment", WechatOrderMapper.class);
        myBatisService.addMapperClass("payment", WechatPayResultMapper.class);
        myBatisService.addMapperClass("payment", WechatRefundResultMapper.class);
        myBatisService.addMapperClass("payment", YijiConfigMapper.class);
        myBatisService.addMapperClass("payment", YijiOrderMapper.class);
        myBatisService.addMapperClass("payment", YijiResultMapper.class);
        myBatisService.addMapperClass("payment", MergeResultMapper.class);
        myBatisService.addMapperClass("payment", ShengConfigMapper.class);
        myBatisService.addMapperClass("payment", ShengOrderMapper.class);
        myBatisService.addMapperClass("payment", ShengResultMapper.class);
        myBatisService.addMapperClass("payment", ShengAccountBalanceMapper.class);
        myBatisService.addMapperClass("payment", PaymentRecordMapper.class);
        myBatisService.addMapperClass("payment", CheckedRealNameMapper.class);
    }

	@Override
	public void registerFilter(Multibinder<IFilter> filters) {
		filters.addBinding().to(AccessLogFilter.class);
	}

	@Override
	public void registerListener(EventBus eventBus, Injector injector) {
		eventBus.register(injector.getInstance(AccessLogHandler.class));
	}
}
