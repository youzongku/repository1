/**
 * 
 */
package service.rabbitmq.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import play.Logger;
import service.rabbitmq.IMQSentMsgService;

import com.google.inject.Inject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import common.rabbitmq.IMQConnection;

/**
 * @author wujirui
 *
 */
public class MQSentMsgService implements IMQSentMsgService {


	//private final static String QUEUE_NAME = "hello";
	
	private static final String EXCHANGE_NAME = "topic_cart";
	
	@Inject
	private IMQConnection mqConn;
	
	@Override
	public Map<String, Object> sentMsg(String msg) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean result = false;

		Connection connection = null;
		Channel channel = null;
		try {
			connection = mqConn.getConn();
			// 创建一个频道
			channel = connection.createChannel();
			
			channel.exchangeDeclare(EXCHANGE_NAME, "direct", true); //如果消费者已创建，这里可不声明  
			channel.confirmSelect(); 
			channel.addConfirmListener(new ConfirmListener() {
				
				@Override
				public void handleNack(long deliveryTag, boolean multiple)
						throws IOException {
					Logger.info("[handleNack] :" + deliveryTag + "," + multiple);  
				}
				
				@Override
				public void handleAck(long deliveryTag, boolean multiple)
						throws IOException {
					Logger.info("[handleAck] :" + deliveryTag + "," + multiple);  
				}
			});
			//指定一个队列  
			//channel.queueDeclare(QUEUE_NAME, true, true, false, null);
			
			//消息持久化 MessageProperties.PERSISTENT_TEXT_PLAIN  
			String[] routing_keys = new String[] { "tomtop.cart", "tmall.warning", "jd.info", "tomtop.sales" };
			for (String routing_key : routing_keys) {
				channel.basicPublish(EXCHANGE_NAME, routing_key, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
				Logger.info("发送了路由为： " + routing_key + " ,消息内容: " + msg + ".");
			}
			result = true;
			resultMap.put("msg", "消息发送成功");
		} catch (Exception e) {
			result = false;
			resultMap.put("msg", "消息发送失败");
			Logger.error("发送消息出错。" + e);
		}finally{
			try {
				// 关闭频道和连接
				channel.close();
				connection.close();
			} catch (IOException e) {
				Logger.error("关闭连接出错。" + e);
				e.printStackTrace();
			} catch (TimeoutException e) {
				Logger.error("关闭频道超时。" + e);
				e.printStackTrace();
			}
		}
		resultMap.put("result", result);
		return resultMap;
	}
	
	/*@Override
	public Map<String, Object> sentMsg(String msg) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean result = false;

		Connection connection = null;
		Channel channel = null;
		try {
			connection = mqConn.getConn();
			// 创建一个频道
			channel = connection.createChannel();

			//指定一个队列  
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			channel.exchangeDeclare(EXCHANGE_NAME, "topic");
			
			String[] routing_keys = new String[] { "tomtop.product", "tmall.warning", "jd.info", "tomtop.sales" };
			for (String routing_key : routing_keys) {
				channel.basicPublish(EXCHANGE_NAME, routing_key, null, msg.getBytes());
				Logger.info("发送了路由为： " + routing_key + " ,消息内容: " + msg + ".");
			}
			result = true;
			resultMap.put("msg", "消息发送成功");
		} catch (Exception e) {
			result = false;
			resultMap.put("msg", "消息发送失败");
			Logger.error("发送消息出错。" + e);
		}finally{
			try {
				// 关闭频道和连接
				channel.close();
				connection.close();
			} catch (IOException e) {
				Logger.error("关闭连接出错。" + e);
				e.printStackTrace();
			} catch (TimeoutException e) {
				Logger.error("关闭频道超时。" + e);
				e.printStackTrace();
			}
		}
		resultMap.put("result", result);
		return resultMap;
	}*/
	
	

}
