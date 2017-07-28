package extensions.timer.trigger;

import org.apache.camel.builder.RouteBuilder;

import com.google.common.eventbus.EventBus;

import events.timer.AccountPerioEvent;
import events.timer.ActOpenEvent;
import events.timer.ActiveStateEvent;
import events.timer.ApkApplyEvent;
import events.timer.ContractEvent;
import events.timer.CsConfirmEvent;
import events.timer.ExternalWarehouseInventorySynchroizationEvent;
import events.timer.InventorySynchronizationEvent;
import events.timer.ProductReleaseCloudLockEvent;
import events.timer.PromotionEndStatusEvent;
import events.timer.PromotionStartStatusEvent;
import events.timer.PurchaseInvalidEvent;
import events.timer.SalesPushEvent;
import events.timer.SalesStateEvent;
import events.timer.TimedTaskRunningStatusEvent;
import events.timer.WithdrawApplyEvent;
import extensions.InjectorInstance;

public class TimedTaskTrigger extends RouteBuilder {

    @Override
    public void configure() throws Exception {
    	
    	/***************************定时任务模块运行状态同步*******************************/
    	from("quartz2://executeRunningStatusSync?cron=0+*/1+*+*+*+?").bean(this,"triggerRunningStatusSyncEvent");
    	
    	/***************************销售模块的相关定时任务*******************************/
    	from("quartz2://executePush?cron=0+*/1+*+*+*+?").bean(this,"triggerSalesPushEvent");
    	from("quartz2://executeUpstu?cron=59+59+23+*+*+?").bean(this,"triggerUpdateOrderStatusEvent");
    	from("quartz2://executeCsConfirm?cron=0+*/2+*+*+*+?").bean(this,"triggerCsAuditEvent");
    	/*************************************************************************/
    	
    	/***************************采购模块的相关定时任务*******************************/
    	from("quartz2://executeInvalid?cron=59+59+23+*+*+?").bean(this,"triggerPurchaseInvalidEvent");
    	/*************************************************************************/
   
    	/***************************商品模块的相关定时任务*******************************/
        from("quartz2://openAct?cron=59+59+23+*+*+?").bean(this, "triggerActOpenEvent");
        from("quartz2://openEsRefresh?cron=0+*/30+*+*+*+?").bean(this, "refreshEs");
        from("quartz2://autoOpenContract?cron=1+0+0+*+*+?").bean(this, "autoOpenContract");
    	/*************************************************************************/

    	/***************************营销模块的相关定时任务*******************************/
		from("quartz2://executeUpdateEnd?cron=0+*/1+*+*+*+?").bean(this,
				"triggerEventForUpdateEnd");
		from("quartz2://executeUpdateStart?cron=0+*/1+*+*+*+?").bean(this,
				"triggerEventForUpdateStart");
    	/*************************************************************************/

		/***************************用户模块的相关定时任务*******************************/
		from("quartz2://executeActiveState?cron=59+59+23+*+*+?").bean(this, "triggerActiveStateEvent");
        from("quartz2://sendWithdrawApplyToMsite?cron=0+0+*/1+*+*+?").bean(this, "withdrawApplyEvent");
        from("quartz2://apkApplyCheck?cron=0+*/1+*+*+*+?").bean(this, "apkApplyCheckEvent");
        from("quartz2://dealAccountPeriod?cron=0+0+0+*+*+?").bean(this, "accountPerioEvent");
		
		/***************************库存模块的相关定时任务*******************************/
        from("quartz2://executeExternalWarehouseInventorySynchroization?cron=0+*/2+*+*+*+?").bean(this, "triggerExternalWarehouseInventorySynchroizationEvent");
        from("quartz2://executeInventorySynchronization?cron=0+*/2+*+*+*+?").bean(this, "triggerInventorySynchronizationEvent");
        from("quartz2://executeProductReleaseCloudLockEvent?cron=0+*/1+*+*+*+?").bean(this, "triggerProductReleaseCloudLockEvent");
    }
    /**
     * 每1分钟同步订单到HK
     */
    public void triggerSalesPushEvent(){
        InjectorInstance.getInstance(EventBus.class).post(new SalesPushEvent());
    }
    /**
     * 每天晚上12点 更新已发货 订单状态
     */
    public void triggerUpdateOrderStatusEvent(){
        InjectorInstance.getInstance(EventBus.class).post(new SalesStateEvent());
    }
    /**
     * 每2分钟处理自动客服审核
     */
    public void triggerCsAuditEvent(){
    	InjectorInstance.getInstance(EventBus.class).post(new CsConfirmEvent());
    }
    /**
     * 采购单超时处理
     */
    public void triggerPurchaseInvalidEvent(){
        InjectorInstance.getInstance(EventBus.class).post(new PurchaseInvalidEvent());
    }
    /**
     * 活动启动
     */
    public void triggerActOpenEvent(){
        InjectorInstance.getInstance(EventBus.class).post(new ActOpenEvent());
    }
    public void autoOpenContract(){
        InjectorInstance.getInstance(EventBus.class).post(new ContractEvent());
    }
    
	/**
	 * 活动更新
	 */
	public void triggerEventForUpdateStart() {
		InjectorInstance.getInstance(EventBus.class).post(
				new PromotionStartStatusEvent());
	}
	public void triggerEventForUpdateEnd() {
		InjectorInstance.getInstance(EventBus.class).post(
				new PromotionEndStatusEvent());
	}
	
	/**
	 * 用户模块
	 */
    public void triggerActiveStateEvent(){
        InjectorInstance.getInstance(EventBus.class).post(new ActiveStateEvent());
    }
    
    public void withdrawApplyEvent(){
		InjectorInstance.getInstance(EventBus.class).post(new WithdrawApplyEvent());
    }
    
    public void apkApplyCheckEvent(){
		InjectorInstance.getInstance(EventBus.class).post(new ApkApplyEvent());
    }
    public void accountPerioEvent(){
    	InjectorInstance.getInstance(EventBus.class).post(new AccountPerioEvent());
    }
    /**
     * 库存模块
     */
    public void triggerInventorySynchronizationEvent(){
        InjectorInstance.getInstance(EventBus.class).post(new InventorySynchronizationEvent());
    }
    public void triggerExternalWarehouseInventorySynchroizationEvent(){
    	InjectorInstance.getInstance(EventBus.class).post(new ExternalWarehouseInventorySynchroizationEvent());
    }
    public void triggerProductReleaseCloudLockEvent(){
        InjectorInstance.getInstance(EventBus.class).post(new ProductReleaseCloudLockEvent());
    }
    
    /**
     * 系统状态同步
     */
    public void triggerRunningStatusSyncEvent(){
        InjectorInstance.getInstance(EventBus.class).post(new TimedTaskRunningStatusEvent());
    }
}
