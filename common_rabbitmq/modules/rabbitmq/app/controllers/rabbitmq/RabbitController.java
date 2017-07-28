/**
 * 
 */
package controllers.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import service.rabbitmq.IMQSentMsgService;

import com.google.inject.Inject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import common.rabbitmq.IMQConnection;

/**
 * @author wujirui
 *
 */
public class RabbitController extends Controller {

	@Inject
	private IMQConnection mqConn;

	@Inject
	private IMQSentMsgService service;

	/**
	 * 发送消息
	 * 
	 * @return
	 */
	public Result sentMsg() {
		Map<String, String> dataMap = Form.form().bindFromRequest().data();
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String message = dataMap.get("msg");
		boolean result = false;

		Connection connection = null;
		try {
			connection = mqConn.getConn();
			// 创建一个频道
			Channel channel = connection.createChannel();
			channel.queueDeclare("hello", false, false, false, null);
			// 往队列中发出一条消息
			channel.basicPublish("", "hello", null, message.getBytes());
			Logger.info("Sent message " + message);
			// 关闭频道和连接
			channel.close();
			connection.close();
			result = true;
			resultMap.put("msg", "消息发送成功");
		} catch (Exception e) {
			result = false;
			resultMap.put("msg", "消息发送失败");
			Logger.error("发送消息出错。" + e);
		}

		resultMap.put("result", result);
		return ok(Json.toJson(resultMap));
	}

	/**
	 * 接收消息
	 * 
	 * @return
	 */
	public Result receiveMsg() {
    	Map<String,Object> resultMap = new HashMap<String,Object>();
    	
    	boolean result = false;

    	try {
			Connection connection = mqConn.getConn();
			Channel channel = connection.createChannel();
			
			// 声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
			channel.queueDeclare("hello", false, false, false, null);
			// 创建队列消费者
			QueueingConsumer consumer = new QueueingConsumer(channel);
			// 指定消费队列
			channel.basicConsume("hello", true, consumer);
			//while (true) {
				// nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				System.out.println(" [x] Received '" + message + "'");
				if (message != null) {
					   result = true;
					   resultMap.put("msg", "Message is:"+message);
				   }else{
					   result = false;
					   resultMap.put("msg", "No Message.");
				   }
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
    	resultMap.put("result", result);
    	return ok(Json.toJson(resultMap));
    }

	/**
	 * 发送消息
	 * 
	 * @return
	 */
	public Result sentMsgBySer() {
		Map<String, String> dataMap = Form.form().bindFromRequest().data();
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String message = dataMap.get("msg");
		boolean result = false;

		resultMap = service.sentMsg(message);
		return ok(Json.toJson(resultMap));
	}

}
