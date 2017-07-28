package services.product;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dto.product.ProductDispriceDto;
import dto.product.ProductDispriceSearch;
import dxo.category.SkuWarehouse2Qty;
import entity.category.CategoryBase;
import entity.product.OperateProductPrice;
import entity.product.ProductDisprice;
import entity.product.ProductPriceRule;
import entity.product.Warehouse;

/**
 * @author zbc
 * 2016年7月28日 下午2:27:32
 */
public interface IProductDispriceService {
	
	/**
	 * 计算总的到仓价
	 * @param sku2QtyEntryList (sku=qty)集合
	 * @param useCostIfAbsent 是否使用裸采价替换空的到仓价
	 * @return
	 */
	public Double calculateTotalArriveWarePrice(List<SkuWarehouse2Qty> skuWarehouse2QtyList, boolean useCostIfAbsent);
	
	/**
	 * 批量获取sku的到仓价
	 * 
	 * @param skuWarehouse2QtyList 要查到仓价的sku集合
	 * @return [{sku:"IF001", warehouseId:2024, arriveWarePrice:0.1}]
	 */
	public List<Map<String,Object>> batchGetArriveWarePrice(List<SkuWarehouse2Qty> skuWarehouse2QtyList);

	/**
	 * 获取商品价格信息
	 * @author zbc
	 * @param node 
	 * @param warehouse_id_mapping 
	 * @param category_sku_mapping 
	 * @param categoryName 
	 * @since 2016年7月28日 下午2:28:45
	 */
	public Map<String, Object> read(JsonNode node, Map<String, Warehouse> warehouse_id_mapping, Map<Integer, List<String>> category_sku_mapping, Map<Integer, CategoryBase> categoryName);

	/**
	 * 根据类目id，获取所有sku
	 * @author zbc
	 * @since 2016年7月29日 上午10:31:27
	 */
	List<String> getSkuList(Integer cateId);

	/**
	 * 获取价格设置数据
	 * @author zbc
	 * @since 2016年7月29日 下午12:24:47
	 */
	public List<ProductPriceRule> readrule();

	/**
	 * 修改计算系数
	 * @author zbc
	 * @param categoryName 
	 * @param category_sku_mapping 
	 * @param warehouse_id_mapping 
	 * @since 2016年7月29日 下午2:21:18
	 */
	public Map<String, Object> updaterule(JsonNode node, Map<String, Warehouse> warehouse_id_mapping, Map<Integer, List<String>> category_sku_mapping, Map<Integer, CategoryBase> categoryName);

	public Map<String, Object> getDisprice(Integer id,Map<Integer, List<String>> category_sku_mapping);

	/**
	 * 更新单个商品价格
	 * @author zbc
	 * @param type 
	 * @since 2016年7月30日 下午2:59:35
	 */
	public Map<String, Object> setDisprice(JsonNode node, String type);

	public List<OperateProductPrice> readPriceLog(Integer priceid, String type);

	/**
	 * 批量设置价格
	 * @author zbc
	 * @since 2016年8月1日 下午4:46:19
	 */
	public Map<String, Object> batchSetPrice(JsonNode node, Map<String, Warehouse> warehouse_id_mapping,
			Map<Integer, List<String>> category_sku_mapping, Map<Integer, CategoryBase> categoryName,String type);

	/**
	 * 分页查询价格操作日志
	 * @author zbc
	 * @since 2016年8月1日 下午8:47:27
	 */
	public Map<String, Object> readRecord(JsonNode node, Map<Integer, List<String>> category_sku_mapping);

	/**
	 * 查询默认价格设置 操作记录
	 * @author zbc
	 * @since 2016年8月4日 下午2:40:08
	 */
	public Map<String, Object> readRuleLog(JsonNode node);

	/**
	 * b2c 通知价格更新
	 * @author zbc
	 * @param categoryName 
	 * @param category_sku_mapping 
	 * @param warehouse_id_mapping 
	 * @since 2016年8月4日 下午8:06:30
	 */
	public Map<String, Object> b2cUpdatePrice(JsonNode node, Map<String, Warehouse> warehouse_id_mapping, Map<Integer, List<String>> category_sku_mapping, Map<Integer, CategoryBase> categoryName);

	/**
	 * 设置价格系数
	 * @author zbc
	 * @since 2016年8月16日 下午3:26:08
	 */
	public Map<String, Object> addPriceFactor(JsonNode node, Map<String, Warehouse> warehouse_id_mapping,
			Map<Integer, List<String>> category_sku_mapping, Map<Integer, CategoryBase> categoryName);

	public Map<String, Object> getPriceFactorList(JsonNode node);

	public Map<String, Object> initCateData();

	public String setTypeForProducts(String param);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月28日 下午4:37:43
	 */
	public Double runJS(Map<String, String> subs, String ruleValue);

	List<ProductDispriceDto> getExportProductDisPrice(ProductDispriceSearch searchDto, Map<String, Warehouse> warehouse_id_mapping,
													  Map<Integer, List<String>> category_sku_mapping,
													  Map<Integer, CategoryBase> categoryName);

	/**
	 * @author zbc
	 * @since 2017年4月11日 上午11:38:09
	 */
	public Map<String, Object> pageSearchClearancePrice(String string);

	/**
	 * 根据sku和仓库id获取商品价格信息
	 * @param sku
	 * @param stockId
	 * @return
	 */
	public List<ProductDisprice> getProductDispriceBySkuAndStockId(String sku, int stockId);

	/**
	 * 更新商品清货价格
	 * @param priceWaitUpdateLists
	 * @return
	 */
	public int updateClearancePrice(List<ProductDisprice> priceWaitUpdateLists);
}
