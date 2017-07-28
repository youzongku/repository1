package extensions.market.timer;

import org.apache.camel.builder.RouteBuilder;

import promotion.events.PromotionEndStatusEvent;
import promotion.events.PromotionStartStatusEvent;

import com.google.common.eventbus.EventBus;

import extensions.InjectorInstance;

public class PromotionStatusTimerTrigger extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("quartz2://executeUpdateEnd?cron=0+*/1+*+*+*+?").bean(this,
				"triggerEventForUpdateEnd");
		from("quartz2://executeUpdateStart?cron=0+*/1+*+*+*+?").bean(this,
				"triggerEventForUpdateStart");
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
	
}
