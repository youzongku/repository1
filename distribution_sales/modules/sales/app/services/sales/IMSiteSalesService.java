package services.sales;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public interface IMSiteSalesService {

	Map<String, Object> order(JsonNode main);

	Map<String, Object> storeOrder(JsonNode main);

}
