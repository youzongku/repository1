/**
 * 
 */
package service.rabbitmq;

import java.util.Map;

/**
 * @author wujirui
 *
 */
public interface IMQSentMsgService {

	public Map<String, Object> sentMsg(String msg);

}
