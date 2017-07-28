package extensions.sales.timer;

import events.sales.*;
import org.apache.camel.builder.RouteBuilder;

import com.google.common.eventbus.EventBus;

import extensions.InjectorInstance;

/**
 * b2b订单推送到b2c定时器
 * 间隔为：半小时
 *
 * Created by luwj on 2016/3/9.
 */
public class SalesPushTimerTrigger extends RouteBuilder {

    @Override
    public void configure() throws Exception {
    	from("quartz2://executePush?cron=0+*/1+*+*+*+?").bean(this,"triggerEvent");
    	from("quartz2://executeCsConfirm?cron=0+*/2+*+*+*+?").bean(this,"triggerCsAudit");
    	from("quartz2://executeUpstu?cron=59+59+23+*+*+?").bean(this,"triggerEventForUpstu");
    	from("quartz2://executeSaleVolumeCount?cron=0+59+23+*+*+?").bean(this,"triggerSalesVolumeCount");

        //定时检查订单导入
//    	from("quartz2://executeSupplementImportOrderInfo?cron=0+*/2+*+*+*+?").bean(this,"triggerSupplementImportOrderInfo");
    }

    public void triggerEvent(){
        InjectorInstance.getInstance(EventBus.class).post(new SalesPushEvent());
    }
    
    /**
     * 每天晚上12点 更新已发货 订单状态
     */
    public void triggerEventForUpstu(){
        InjectorInstance.getInstance(EventBus.class).post(new SalesStateEvent());
    }
    /**
     * 每2分钟处理自动客服审核
     */
    public void triggerCsAudit(){
    	InjectorInstance.getInstance(EventBus.class).post(new CsConfirmEvent());
    }
    
    /**
     * 每天晚上11:59:00统计商品销量
     */
    public void triggerSalesVolumeCount(){
        InjectorInstance.getInstance(EventBus.class).post(new SalesVolumeCountEvent());
    }

    /**
     * 每2分钟补充导入的订单信息
     */
    public void triggerSupplementImportOrderInfo(){
        InjectorInstance.getInstance(EventBus.class).post(new SupplementImportOrderInfoEvent());
    }
}
