package handlers.timer;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import events.timer.PurchaseInvalidEvent;
import play.Logger;
import service.timer.IPurchaseOrderService;

/**
 * 定时任务
 * @author zbc
 * 2016年8月24日 下午3:44:38
 */
public class PurchaseModuleEventHandler {

    @Inject
    IPurchaseOrderService purchaseOrderService;
    
    /**
     * 微仓订单自动 失效，自动更新
     * @param event
     */
    @Subscribe
    public void executeInvalid(PurchaseInvalidEvent event){
    	if(SystemEventHandler.run_timed_task){
	    	Logger.info("[purchaseEvent]========	purchase order timeout at every 24:00	========[purchaseEvent]");
	        purchaseOrderService.batchInvalid();
    	}
    }
    
}
