/**
 * 
 */
package common.rabbitmq;

import play.Configuration;
import play.Logger;
import play.Play;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import extensions.rabbitmq.RabbitMQ;

/**
 * @author wujirui
 *
 */
public class MQConnection implements IMQConnection {
	
	Connection conn = null;
	
	@Override
	public Connection getConn() {
		Configuration config = Play.application().configuration().getConfig("rabbitmq");
		ConnectionFactory factory = new ConnectionFactory();
		RabbitMQ rabbitMQ=new RabbitMQ();

        rabbitMQ.setHost(config.getString("host"));
        rabbitMQ.setPort(config.getInt("port"));
        rabbitMQ.setVhost(config.getString("vhost"));


        rabbitMQ.setUsername(config.getString("username"));
        rabbitMQ.setPassword(config.getString("password"));


        String autoAckStr = config.getString("autoAck");
        if (autoAckStr != null && !autoAckStr.trim().isEmpty()) {
            rabbitMQ.setAutoAck(Boolean.parseBoolean(autoAckStr));
        }

        String basicQosStr = config.getString("basicQos");
        if (basicQosStr != null && !basicQosStr.trim().isEmpty()) {
            rabbitMQ.setBasicQos(Boolean.parseBoolean(basicQosStr));
        }

        String retriesStr = config.getString("retries");
        if (retriesStr != null && !retriesStr.trim().isEmpty()) {
            rabbitMQ.setRetries(Integer.parseInt(retriesStr));
        }

        String durableStr = config.getString("durable");
        if (durableStr != null && !durableStr.trim().isEmpty()) {
            rabbitMQ.setDurable(Boolean.parseBoolean(durableStr));
        }
        String exchangeType = config.getString("exchangeType");
        if (exchangeType != null && !exchangeType.trim().isEmpty()) {
            rabbitMQ.setExchangeType(exchangeType);
        }
        Logger.info("port ="+rabbitMQ.getPort());
        factory.setPort(rabbitMQ.getPort());
        factory.setUsername(rabbitMQ.getUsername());
        factory.setPassword(rabbitMQ.getPassword());
        factory.setVirtualHost(rabbitMQ.getVhost());

        factory.setHost(rabbitMQ.getHost());
		try {
			conn = factory.newConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}


}
