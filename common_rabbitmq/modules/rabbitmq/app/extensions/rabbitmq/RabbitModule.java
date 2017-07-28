package extensions.rabbitmq;


import handlers.rabbitmq.MQConsumerHandler;

import java.util.List;
import java.util.Set;

import org.apache.camel.builder.RouteBuilder;

import play.Logger;
import service.rabbitmq.IMQSentMsgService;
import service.rabbitmq.IMQConsumerService;
import service.rabbitmq.impl.MQSentMsgService;
import service.rabbitmq.impl.MQConsumerService;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;

import common.rabbitmq.IMQConnection;
import common.rabbitmq.MQConnection;
import extensions.IModule;
import extensions.ModuleSupport;
import extensions.camel.ICamelExtension;
import extensions.common.CommonModule;
import extensions.event.IEventExtension;
import extensions.rabbitmq.camel.MQConsumerTimerTrigger;
import extensions.runtime.IApplication;

/**
 * @author wujirui
 */
public class RabbitModule extends ModuleSupport implements IEventExtension,ICamelExtension {

	@SuppressWarnings("unchecked")
	@Override
	public Set<Class<? extends IModule>> getDependentModules() {
		return Sets.newHashSet(CommonModule.class);
	}

	@Override
	public Module getModule(IApplication application) {
		return new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMQSentMsgService.class).to(MQSentMsgService.class);
				bind(IMQConsumerService.class).to(MQConsumerService.class);
				bind(IMQConnection.class).to(MQConnection.class);
				
				Logger.info("==========RabbitModule configure==========");
			}
		};
	}
 
	@Override
	public void registerListener(EventBus eventBus, Injector injector) {
		eventBus.register(injector.getInstance(MQConsumerHandler.class));
		Logger.info("==========RabbitModule registerListener==========");
	}
	
	@Override
	public List<RouteBuilder> getRouteBuilders() {
		return Lists.newArrayList(new MQConsumerTimerTrigger());
	}
	
}
