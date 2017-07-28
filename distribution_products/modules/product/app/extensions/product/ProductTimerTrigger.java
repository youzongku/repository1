package extensions.product;

import org.apache.camel.builder.RouteBuilder;

import com.google.common.eventbus.EventBus;

import event.ActOpenEvent;
import event.ClearanceProductEvent;
import event.EsRefreshEvent;
import event.HomePageRefreshEvent;
import event.ContractEvent;
import extensions.InjectorInstance;

/**
 * 商品模块自动任务
 * Created by LSL on 2016/7/7.
 */
public class ProductTimerTrigger extends RouteBuilder {
	
    @Override
    public void configure() throws Exception {
        from("quartz2://openAct?cron=59+59+23+*+*+?").bean(this, "triggerEvent");
        from("quartz2://openEsRefresh?cron=0+*/30+*+*+*+?").bean(this, "refreshEs");
        from("quartz2://autoOpenContract?cron=1+0+0+*+*+?").bean(this, "autoOpenContract");
        from("quartz2://clearancePrice?cron=0+*/10+*+*+*+?").bean(this, "clearancePrice");
    }

    public void triggerEvent(){
        InjectorInstance.getInstance(EventBus.class).post(new ActOpenEvent());
    }
    
    public void refreshEs(){
    	InjectorInstance.getInstance(EventBus.class).post(new EsRefreshEvent());
    }
    
    public void autoOpenContract(){
        InjectorInstance.getInstance(EventBus.class).post(new ContractEvent());
    }
    
    public void refreshHomepage(){
    	InjectorInstance.getInstance(EventBus.class).post(new HomePageRefreshEvent());
    }
    
    public void clearancePrice(){
        InjectorInstance.getInstance(EventBus.class).post(new ClearanceProductEvent());
    }

}
