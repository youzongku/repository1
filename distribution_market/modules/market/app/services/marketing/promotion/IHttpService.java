package services.marketing.promotion;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface IHttpService {

	public JsonNode getProducts(List<String> skus, Integer warehouseId,Integer categoryId,Integer model, String account) throws JsonProcessingException, IOException; 

}
