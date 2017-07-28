/**
 * 
 */
package services.openapi;


import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import dto.openapi.Warehouse;
import play.mvc.Http.Context;
import utils.Page;

/**
 * @author Lzl
 *
 */
public interface IInventoryService {

	public Page getMicroStorage (JsonNode node,Context context);
	
	public Page getCloudStorage (String sku, Integer wid);

	public List<Warehouse> queryWarehouse(String wid);

}
