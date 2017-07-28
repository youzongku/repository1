/**
 * 
 */
package common.rabbitmq;

import com.rabbitmq.client.Connection;

/**
 * @author wujirui
 *
 */
public interface IMQConnection {
	
	public Connection getConn();
	
}
