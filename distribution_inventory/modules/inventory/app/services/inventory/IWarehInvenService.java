package services.inventory;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import entity.inventory.WarehouseInventory;


/**
 *
 * 存储b2c推送仓库信息、商品库存信息
 */
public interface IWarehInvenService {

    /**
     * 存储仓库信息（全量接口）
     * @param node
     * @return
     */
    public Map<String, Object> saveWarehouse(Map<String, String[]> node);

    /**
     * 存储商品库存信息（增量接口）
     * @param node
     * @return
     */
    public Map<String, Object> saveInvenInfo(Map<String, String[]> node);

	/**
	 * 
	 * @param wid 仓库id
	 * @param isBack 是否是后台查询，可为null
	 * @param onlyVirtualHouse 是否只要虚拟仓的，可为null
	 * @return
	 */
	public List<entity.warehousing.Warehouse> queryWarehouse(Integer wid, Boolean isBack, Boolean onlyVirtualHouse);

	/**
	 * @param node 
	 * @return
	 */
	public Map<String,Object> queryInventory(Map<String, String[]> node);

	public WarehouseInventory inventory(JsonNode node);

}
