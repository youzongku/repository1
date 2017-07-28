package services.sales;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import events.sales.LogisticsEvent;

/**
 * @author zbc
 * 2017年2月28日 下午6:19:14
 */
public interface IPinTwoDuoService {

	/**
	 * 
	 * @author zbc
	 * @since 2017年2月28日 下午6:23:32
	 */
	public Map<String, Object> pullOrder(JsonNode main,String email);

	/**
	 * 推送拼多多物流
	 * @param event
	 */
	public void pushPddLogistic(LogisticsEvent event);

}
