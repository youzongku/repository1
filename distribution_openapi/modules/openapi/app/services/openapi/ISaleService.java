package services.openapi;

import play.mvc.Http.Context;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

public interface ISaleService {
	public String getCustomerOrderPage(JsonNode main, Context context);
	
	public Result createNewOrder(JsonNode main, Context context);
}
