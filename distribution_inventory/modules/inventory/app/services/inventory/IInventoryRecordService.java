package services.inventory;

import com.fasterxml.jackson.databind.JsonNode;

public interface IInventoryRecordService {
	
	/**
	 * 物理库存变更记录更新
	 * @param node
	 */
	boolean updateInventoryRecord(JsonNode node);

}
