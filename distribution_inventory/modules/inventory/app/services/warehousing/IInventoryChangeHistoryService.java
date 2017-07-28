package services.warehousing;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.fasterxml.jackson.databind.JsonNode;

import dto.warehousing.InventoryChangeHistoryDto;
import dto.warehousing.ReturnMess;
import dto.warehousing.WarehouseChangeRecordDto;
import entity.warehousing.InventoryChangeHistory;
import forms.warehousing.InventoryChangeHistoryForm;
import util.warehousing.Page;

public interface IInventoryChangeHistoryService {
	
	public int save(InventoryChangeHistory history);
	
	public ReturnMess delete(Integer id);
	
	public List<InventoryChangeHistory> get(InventoryChangeHistoryDto dto);
	
	/**
	 * 获取待出库商品在入库时的采购价格
	 * @param node JSON参数
	 * 形如：
	   <pre>
	    {
		    "email": "001001@qq.com",
		    "warehouseId": "2029",
		    "details": [
		        {
		            "sku": "IM81",
		            "qty": 5
		        }
		    ]
		}
		</pre>
	 * @return 每个SKU都会返回一个JSON数组，数组包含对应数量所覆盖的当时的采购价格和分销价格体系
	 */
	public Map<String, Object> getPurchasePriceByChangeHistory(JsonNode node);
	
	Page<InventoryChangeHistoryForm> queryByParam(@Param("param") WarehouseChangeRecordDto dto);
}
