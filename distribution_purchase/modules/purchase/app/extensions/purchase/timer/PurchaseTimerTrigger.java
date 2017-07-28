package extensions.purchase.timer;

import org.apache.camel.builder.RouteBuilder;

import com.google.common.eventbus.EventBus;

import events.purchase.PurchaseInvalidEvent;
import extensions.InjectorInstance;

/**
 * 定时器 自动更新 失效微仓订单自动更新
 * 时间间隔为 1 天
 * @author zbc
 * 2016年8月24日 下午3:49:41
 */
public class PurchaseTimerTrigger extends RouteBuilder {

    @Override
    public void configure() throws Exception {
    	from("quartz2://executeInvalid?cron=59+59+23+*+*+?").bean(this,"triggerEvent");
    }
    public void triggerEvent(){
        InjectorInstance.getInstance(EventBus.class).post(new PurchaseInvalidEvent());
    }

}
