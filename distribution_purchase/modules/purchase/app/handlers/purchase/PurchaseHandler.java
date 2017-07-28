package handlers.purchase;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import events.purchase.PurchaseInvalidEvent;
import play.Logger;
import services.purchase.IPurchaseOrderService;

/**
 * 定时任务
 * @author zbc
 * 2016年8月24日 下午3:44:38
 */
public class PurchaseHandler {

    @Inject
    IPurchaseOrderService purchaseOrderService;
    
    /**
     * 微仓订单自动 失效，自动更新
     * @param event
     */
    @Subscribe
    public void executeInvalid(PurchaseInvalidEvent event){
        Logger.debug(">>>每天12点 定时更新微仓订单状态 >>>");
        purchaseOrderService.batchInvalid();
    }
    
}
