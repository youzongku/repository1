/**
 * 
 */
package extensions.rabbitmq.camel;

import org.apache.camel.builder.RouteBuilder;

import play.Logger;
import service.rabbitmq.IMQConsumerService;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import events.rabbitmq.MQConsumerEvent;
import extensions.InjectorInstance;

/**
 * @author wujirui
 * 
 */
public class MQConsumerTimerTrigger extends RouteBuilder{

	@Inject
	IMQConsumerService ConsumerService;

	@Override
	public void configure() throws Exception {
		from("quartz2://processReceiveMsg?cron=*/1+*+*+*+*+?").bean(this, "processMQ");
	}

	public void processMQ() {
		Logger.info("In class MQConsumerTimerTrigger --> processReceive-----start");
		InjectorInstance.getInstance(EventBus.class).post(new MQConsumerEvent());
	}

}
