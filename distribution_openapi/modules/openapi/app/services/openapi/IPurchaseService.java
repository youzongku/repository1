package services.openapi;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Http.Context;
import play.mvc.Result;

/**
 * @author zbc
 * 2016年8月26日 上午10:42:34
 */
public interface IPurchaseService {

	public Result order(JsonNode node,Context context);

	public Result getOrderPage(JsonNode node,Context context);

	
}
