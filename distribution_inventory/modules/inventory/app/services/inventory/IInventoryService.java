package services.inventory;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import dto.inventory.APIInventoryDtoDetail;
import dto.inventory.DisInventoryDto;
import dto.inventory.DisProductDto;
import dto.inventory.IvyCheckResDto;
import entity.warehousing.InventoryChangeHistory;

/**
 * 库存业务服务接口
 * @author Alvin Du
 *
 */
public interface IInventoryService {
	/**
	 * 根据物流方式id和目的地省份id获取sku集合的运费
	 * @param node
	 * @return
	 */
	String getFreight(JsonNode node);
	
	
	/**
	 * 根据仓库ID获取对应可选的物流
	 * @param node
	 * @return
	 */
	String getShippingMethodByWarehouse(Integer wid);
	
	
	/**
	 * 冻结或者解冻物理仓仓库中的库存信息
	 * @param node
	 * @return
	 */
	int frozenStock(JsonNode node);

	/**
	 * 采购订单支付完毕，更新微仓，物理仓信息
	 * @param email 
	 * @return 返回更新的产品数量
	 */
	List<APIInventoryDtoDetail> updateInventoryByOrderInfo(JsonNode node, String email);
	
	/**
	 * 检查指定分销商指定的微仓库存中，产品的库存
	 * @param node
	 * @return 检查结果
	 */
	List<IvyCheckResDto> disInventoryCheck(String node,String email);
	
	/**
	 * 扣除分销商指定微仓中指定产品的库存
	 * @param node
	 * @param context 
	 * @return
	 */
	String deductDisInventory(JsonNode node, String email);
	
	/**
	 * 获取指定分销商的微仓产品信息
	 * @param node
	 * @return
	 */
	List<DisInventoryDto> getDisProduct(JsonNode node);
	
	/**
	 * 获取指定分销商指定产品的产品信息和微仓信息
	 * @param sku
	 * @param email
	 * @return
	 */
	String getDisProductAndStockInfo(JsonNode node);

	/**
	 * 获取微仓库存信息
	 * @param node
	 * @param email 
	 * @return
	 */
	String initDisproduct(JsonNode node, String email);

	/**
	 * 获取分销商所有商品所在的微仓
	 * @param main
	 * @return
	 */
	List<DisProductDto> selectAllStock(JsonNode main);

	List<InventoryChangeHistory> getDisProductMicroChangeHistory(String main, String email);
}
