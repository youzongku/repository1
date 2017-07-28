package handlers.sales;

import java.io.IOException;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import entity.sales.enums.TimerExceType;
import events.sales.CaculateChargeEvent;
import events.sales.ChangePurchaseFeightEvent;
import events.sales.CsConfirmEvent;
import events.sales.SalesPushEvent;
import events.sales.SalesStateEvent;
import play.Logger;
import services.sales.IHttpService;
import services.sales.IManagerOrderService;
import services.sales.ISaleService;
import services.sales.ISalesPushToB2CService;

/**
 * Created by luwj on 2016/3/9.
 */
public class SalesPushHandler {

    @Inject
    private ISalesPushToB2CService iSalesPushToB2CService;
    @Inject
    private ISaleService saleService;
    @Inject
    private IManagerOrderService managerOrderService;
    @Inject
    private IHttpService httpService;

    /**
     * 销售订单推送到b2c
     * @param event
     */
    @Subscribe
    public void executePush(SalesPushEvent event){
        Logger.debug(">>>execute b2b sales 2 b2c >>>");
        iSalesPushToB2CService.pushSales(TimerExceType.B2B_SALES_2_B2C.name());
    }
    
    
    /**
     * 发货单更新状态
     * @param event
     */
    @Subscribe
    public void executeUpstu(SalesStateEvent event){
        Logger.debug(">>>每天12点 更新已发货订单状态 >>>");
        saleService.autoConfirmReceipt();
    }
    /**
     * 发货单自动客服确认
     * @param event
     */
    @Subscribe
    public void executeCsConfirm(CsConfirmEvent event){
    	Logger.debug("定时任务自动扫描确认订单");
    	saleService.autoCsConfirm();
    }
    
    /**
     * 财务审核费用信息更新
     * 合同总费用 = 合同扣点(10万*0.12=12000) + 满返（10万*0.03=3000）=15000元
	 * 订单总成本 = 运费 + 操作费 + 合同总费用 + 到仓价总计
	 * 利润 = 订单毛收入（订单总额）- 订单总成本
	 * 利润率 = 利润 ÷ 订单毛收入
     * @param event
     */
    @Subscribe
    public void executeCaculateProfit(CaculateChargeEvent event){
    	Logger.debug("异步计算财务审核费用信息");
    	if(event.getSid() != null){
    		managerOrderService.match(event.getSid());
    	}else if(event.getOrderNos() != null){
    		for(String orderNo:event.getOrderNos()){
    			managerOrderService.caculateProfit(orderNo);
    		}
    	}
    }
    
    /**
     * 异步更新缺货采购单运费
     * @author zbc
     * @since 2017年5月24日 上午11:48:28
     * @param event
     */
    @Subscribe
    public void changePurchaseFeight(ChangePurchaseFeightEvent event){
    	Logger.info("异步更新缺货采购单运费");
    	try {
			httpService.changePurchaseOrderFreight(event.getChangeFreightList());
		} catch (IOException e) {
			Logger.info("异步更新缺货采购单运费异常{}",e);
		}
    }
}
