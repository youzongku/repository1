/**
 * 
 */
package service.rabbitmq.impl;

import java.util.concurrent.TimeUnit;

import play.Logger;
import service.rabbitmq.IMQConsumerService;

import com.google.inject.Inject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

import common.rabbitmq.IMQConnection;

/**
 * @author wujirui
 *
 */
public class MQConsumerService implements IMQConsumerService {

	private final static String QUEUE_NAME = "hello";

	private static final String EXCHANGE_NAME = "topic_cart";

	@Inject
	private IMQConnection mqConn;


	@Override
	public void receive() {
		Logger.info("Execute method receive");

		// 创建队列消费者
		QueueingConsumer consumer;
		try {
			Connection connection = mqConn.getConn();
			Channel channel = connection.createChannel();
			Logger.info("connection======" + connection);
			
			boolean durable = true;  

			//声明交换机  
	        channel.exchangeDeclare(EXCHANGE_NAME, "direct", durable); //按照routingKey过滤  
	        
			//声明队列  
	        String queueName = channel.queueDeclare(QUEUE_NAME, true, true, false, null).getQueue();  
	        

			// 接收所有与tomtop相关的消息
			channel.queueBind(queueName, EXCHANGE_NAME, "tomtop.cart");  

			consumer = new QueueingConsumer(channel);
			
			boolean ack = false;;        //打开应签机制
			
			// 指定消费队列
	        channel.basicConsume(QUEUE_NAME, ack, consumer);

			Logger.info("Waiting for messages................");

			while (true) {
				Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				String routingKey = delivery.getEnvelope().getRoutingKey();

				Logger.info("Received 接收到 " + routingKey + "的消息,msg = " + message + ".");
				Logger.info(" Handle message");  
				//TimeUnit.SECONDS.sleep(3); //mock handle message  
	            //发送应答
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false); //确定该消息已成功消费  

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*@Override
	public void receive() {
		Logger.info("Execute method receive");

		// 创建队列消费者
		QueueingConsumer consumer;
		try {
			Connection connection = mqConn.getConn();
			Channel channel = connection.createChannel();
			Logger.info("connection=" + connection);

			// 指定一个队列
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			// 声明转发器
			//channel.exchangeDeclare(EXCHANGE_NAME, "topic");

			consumer = new QueueingConsumer(channel);
			
			boolean ack = false;;        //打开应签机制
			
			// 指定消费队列
	        channel.basicConsume(QUEUE_NAME, ack, consumer);

			// 接收所有与tomtop相关的消息
			channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "tomtop.*");

			Logger.info("Waiting for messages................");

			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				String routingKey = delivery.getEnvelope().getRoutingKey();

				Logger.info("Received 我只接收 = " + routingKey + "的消息,msg = " + message + ".");

	            //发送应答
	            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

}
