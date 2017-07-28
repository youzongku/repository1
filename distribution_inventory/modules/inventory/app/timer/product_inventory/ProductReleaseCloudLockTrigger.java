package timer.product_inventory;

import com.google.common.eventbus.EventBus;

import events.product_inventory.ProductReleaseCloudLockEvent;
import extensions.InjectorInstance;

import org.apache.camel.builder.RouteBuilder;

import play.Configuration;
import play.Play;

/**
 * @author longhuashen
 * @since 2016/12/29
 */
public class ProductReleaseCloudLockTrigger extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("quartz2://start?cron=0+*/1+*+*+*+?").bean(this, "triggerEvent");
    }

    public void triggerEvent(){
        InjectorInstance.getInstance(EventBus.class).post(new ProductReleaseCloudLockEvent());
    }
}
