package services.sales;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public interface IProductExpirationDateService {
	// 发货单录入选择商品到期日期
	Map<String,Object> setSelectedProductsExpirationDates(JsonNode node);

	// 云仓发货选择商品到期日期
	Map<String,Object> setCloudSelectedProductsExpirationDates(JsonNode node);
	
	// 营销单录入选择商品到期日期
	Map<String, Object> setMOSelectedProductsExpirationDates(JsonNode node);
}
