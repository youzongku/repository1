package services.warehousing;

import java.util.List;
import java.util.Map;

import dto.warehousing.GoodsInventoryListDto;
import dto.warehousing.GoodsInventorySearchDto;

import forms.warehousing.InventoryChangeForm;
import util.warehousing.Page;

public interface IGoodsInventoryService {
	/**
	 * 保存（新增或更新）
	 * 
	 * @author ouyangyaxiong
	 * @data 上午11:03:25
	 * @return
	 */
	public Map<String, Object> save(GoodsInventorySearchDto param);

	/**
	 * 查询
	 * 
	 * @author ouyangyaxiong
	 * @data 上午11:03:14
	 * @param mWareouse
	 * @return
	 */
	public Page<GoodsInventoryListDto> query(GoodsInventorySearchDto param);
	
	/**
	 * 云仓库存查询
	 * 
	 * @param param
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月9日 下午3:23:52
	 */
	public Page<GoodsInventoryListDto> cloudInventoryQuery(GoodsInventorySearchDto param);

	public Map<String, Object> delete(GoodsInventorySearchDto param);

	/**
	 * 更新库存数量
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

	/**
	 * 订单关闭库存恢复
	 * @param warehouseId 仓库ID
	 * @param csku 商品sku
	 * @param iqty 数量
	 * @return 
	 */
	public Map<String, String> restoreStockOfeOrderClosed(Integer warehouseId, String csku, Integer iqty);

}
