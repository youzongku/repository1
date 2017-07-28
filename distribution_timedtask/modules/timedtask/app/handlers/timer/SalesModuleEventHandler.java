package handlers.timer;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import entity.timer.enums.TimerExceType;
import events.timer.CsConfirmEvent;
import events.timer.SalesPushEvent;
import events.timer.SalesStateEvent;
import play.Logger;
import service.timer.ISaleService;
import service.timer.ISalesPushToB2CService;

public class SalesModuleEventHandler {

    @Inject
    ISalesPushToB2CService iSalesPushToB2CService;
    @Inject
    ISaleService saleService;

    /**
     * 销售订单推送到b2c
     * @param event
     */
    @Subscribe
    public void executePush(SalesPushEvent event){
    	if(SystemEventHandler.run_timed_task){
    		Logger.info("[saleEvent]========	push order to HK	========[saleEvent]");
            iSalesPushToB2CService.pushSales(TimerExceType.B2B_SALES_2_B2C.name());
    	}
    }
    
    /**
     * 发货单更新状态
     * @param event
     */
    @Subscribe
    public void executeUpstu(SalesStateEvent event){
    	if(SystemEventHandler.run_timed_task){
	        Logger.info("[saleEvent]========	update order status to [Dispatched]	========[saleEvent]");
	        saleService.autoConfirmReceipt();
    	}
    }
    /**
     * 发货单自动客服确认
     * @param event
     */
    @Subscribe
    public void executeCsConfirm(CsConfirmEvent event){
    	if(SystemEventHandler.run_timed_task){
			Logger.info("[saleEvent]========	order auto cs confirm	========[saleEvent]");
			saleService.autoCsConfirm();
    	}
    }
    
}
