/**
 * 
 */
package handlers.rabbitmq;

import java.util.Date;

import play.Logger;
import service.rabbitmq.IMQConsumerService;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import events.rabbitmq.MQConsumerEvent;

/**
 * @author wujirui
 *
 */
public class MQConsumerHandler {
	
	@Inject
	IMQConsumerService consumerService;

	@Subscribe
	public void getMessage(MQConsumerEvent event) {
		Logger.info("In MQConsumerHandler --> execute getMessage timer>>"+new Date());
		try {
			consumerService.receive();
		} catch (Exception e) {
			Logger.error("Error", e);
		}
	}

}
