package services.openapi;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import dto.openapi.Category;
import play.mvc.Http.Context;
import utils.Page;

/**
 * 
 * @author Lzl
 */
public interface IProductService {
	
	public Page getProducts(JsonNode node,Context context);
	
	public String getProductsDetail(JsonNode node,Context context);

	public List<Category> queryCategorys(Integer level);
}
