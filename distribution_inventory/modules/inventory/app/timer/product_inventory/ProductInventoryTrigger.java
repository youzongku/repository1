package timer.product_inventory;


import org.apache.camel.builder.RouteBuilder;

import com.google.common.eventbus.EventBus;

import events.product_inventory.CheckInventoryEvent;
import events.product_inventory.ExternalWarehouseInventorySynchroizationEvent;
import events.product_inventory.InventorySynchronizationEvent;
import events.product_inventory.PhysicalInventoryEvent;
import extensions.InjectorInstance;

/**
 * 定时执行更新活动状态
 * @author Administrator
 *
 */
public class ProductInventoryTrigger extends RouteBuilder {
	
	    @Override
	    public void configure() throws Exception {
//	        from("quartz2://execute?cron=59+59+23+*+*+?").bean(this, "triggerEvent");
//	        from("quartz2://sendWithdrawApplyToMsite?cron=0+0+*/1+*+*+?").bean(this, "withdrawApplyEvent");
	    	from("quartz2://executeExternalWarehouseInventorySynchroization?cron=0+*/2+*+*+*+?").bean(this, "triggerExternalWarehouseInventorySynchroizationEvent");
	        from("quartz2://executeInventorySynchronization?cron=0+*/2+*+*+*+?").bean(this, "triggerInventorySynchronizationEvent");
	        from("quartz2://physicalAllBBCInventory?cron=0+0+2+*+*+?").bean(this, "triggerPhysicalAllBBCInventoryEvent");
	        from("quartz2://checkAllBBCInventory?cron=0+0+3+*+*+?").bean(this, "triggerCheckAllBBCInventoryEvent");
	        
	    }

	    public void triggerInventorySynchronizationEvent(){
	        InjectorInstance.getInstance(EventBus.class).post(new InventorySynchronizationEvent());
	    }
	    
	    public void triggerExternalWarehouseInventorySynchroizationEvent(){
	    	InjectorInstance.getInstance(EventBus.class).post(new ExternalWarehouseInventorySynchroizationEvent());
	    }
	    
	    /**
	     * 盘点BBC库存，会同步库存
	     */
	    public void triggerPhysicalAllBBCInventoryEvent(){
	    	InjectorInstance.getInstance(EventBus.class).post(new PhysicalInventoryEvent());
	    }
	    
	    /**
	     * 清点BBC库存，输出excel文件
	     */
	    public void triggerCheckAllBBCInventoryEvent(){
	    	InjectorInstance.getInstance(EventBus.class).post(new CheckInventoryEvent());
	    }
}
