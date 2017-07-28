package services.warehousing;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import entity.warehousing.MicroGoodsInventory;
import forms.warehousing.InventoryChangeForm;
import forms.warehousing.MicroGoodsInventoryForm;
import util.warehousing.Page;

/**
 * 微仓库存service
 * 
 * @author ye_ziran
 * @since 2016年3月3日 上午11:41:22
 */
public interface IMicroGoodsInventoryService {
	
	/**
	 * 插入数据
	 * 
	 * @param mInventory
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月2日 下午5:16:18
	 */
	public int insert(MicroGoodsInventory mInventory);
	
	/**
	 * 修改数据
	 * 
	 * @param mInventory
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月2日 下午5:16:25
	 */
	public int update(MicroGoodsInventory mInventory);
	
	/**
	 * 删除数据
	 * 
	 * @param mInventory
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月2日 下午5:16:32
	 */
	public int delete(MicroGoodsInventory mInventory);
	
	/**
	 * 查询数据
	 * 
	 * @param mInventory			实体(暂且不考虑连表查询，所以用实体做查询条件)
	 * @param pageNum				第几页
	 * @param length				每页长度
	 * 
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月2日 下午5:16:40
	 */
	public Page<MicroGoodsInventory> query(MicroGoodsInventoryForm mInventory, Integer pageNum, Integer length);
	
	/**
	 * 更新微仓库存数量
	 * <p>
	 * inventoryForm.type 出入库类型，1入0出
	 * 
	 * 
	 * @param inventoryForm
	 * @return List 更新的结果集
	 * @author ye_ziran
	 * @since 2016年3月9日 上午10:04:22
	 */
	public List<Map<String, Object>> updateStock(InventoryChangeForm inventoryForm);
	
	
	public JsonNode b2bQuery(JsonNode node);
}
