package services.product;

import java.util.List;
import java.util.Map;
import java.util.Set;

import dto.CommonExportDto;
import dto.ProdcutInventoryDataExportDto;
import dto.product.PageResultDto;
import dto.product.ProductLite;
import dto.product.ProductSearchParamDto;
import entity.category.CategoryBase;
import entity.product.ExportSyncResult;
import entity.product.ProductBase;
import entity.product.ProductBaseLog;
import entity.product.Warehouse;

public interface IProductBaseService{
	
	/**
	 * 根据sku或者listingid查询product
	 * @param productSearchDto
	 * @param siteId
	 * @param langId
	 * @return
	 */
	public List<ProductLite> getProducts(ProductSearchParamDto productSearchDto, int siteId, int langId);
	
	/**
	 * 根据搜索条件过滤商品
	 * 
	 * @param productSearchDto
	 * @return
	 * @author ye_ziran
	 * @since 2017年1月10日 下午5:11:30
	 */
	public List<ProductLite> getProductInfo(ProductSearchParamDto productSearchDto);
	

	/**
	 * 查询商品基本信息（t_product_base）
	 * 
	 * @author ouyangyaxiong
	 * @param searchDto
	 * @return
	 * @since 2015年12月9日
	 */
	public ProductBase getProductBase(ProductSearchParamDto searchDto);

	/**
	 * 取得商品详细信息，给商品详情页提供数据
	 * 
	 * @author ouyangyaxiong
	 * @param productSearchDto
	 * @param categoryName 
	 * @param category_sku_mapping 
	 * @return
	 * @since 2015年12月9日
	 */
	public Map<String, Object> getProductDetail(ProductSearchParamDto productSearchDto, Map<Integer, List<String>> category_sku_mapping, Map<Integer, CategoryBase> categoryName);
	
	public String getImgUrl(String sku);

	List<String> getBrand();
	/**
	 * B2B查询产品入口
	 * @param searchDto
	 * @param categoryName 
	 * @param category_sku_mapping 
	 * @return
	 */
	public PageResultDto products(ProductSearchParamDto searchDto);

	public PageResultDto inventoryGoods(ProductSearchParamDto searchDto, Map<Integer, List<String>> category_sku_mapping,
			Map<Integer, CategoryBase> categoryName);

	public List<Warehouse> selectAllWare();

	/**
	 * 缓存类目
	 * @author zbc
	 * @since 2016年9月8日 下午4:31:42
	 */
	public void loadCategory();

	/**
	 * 缓存仓库信息
	 * @author zbc
	 * @since 2016年9月8日 下午4:37:10
	 */
	public void loadwarehouse();

	/**
	 * 清除缓存
	 * @author zbc
	 * @since 2016年9月8日 下午4:44:06
	 */
	public void reloadCategory(boolean cateName, boolean skuMapping, boolean wareMap);

	/**
	 * 导出商品库存信息
	 * @param expiration_begin
	 * @param expiration_end
	 * @return
	 */
	public List<ProdcutInventoryDataExportDto> productInventoryDataExport(String expiration_begin,
			String expiration_end);

	/**
	 * 获得function信息
	 * @param functionId  function_id,函数名
	 * @return
	 */
	public CommonExportDto getExportModelByFunctionId(String functionId);

	/**
	 * 执行函数，获得返回的map集合结果
	 * @param param
	 * @return
	 */
	public List<Map> productInventoryDataExportTest(String sql);

	/**
	 * 获得启用中的仓库
	 * @return
	 */
	public List<Warehouse> getAvailableWarehouse();

	/**
	 * 设置非卖状态
	 * @param skus 要设置非卖的skus字符串
	 * @param salable 非卖状态（0不可卖，1可卖）
	 * @param optUser 操作人
	 * @return
	 */
	public Map<String, Object> setSalable(Set<String> skuSet, int salable, String optUser);
	
	/**
	 * 获取非卖品设置的日志
	 * @param sku
	 * @return
	 */
	public List<ProductBaseLog> getSalableSetLogs(String sku);

	/**
	 * 获取商品信息及商品库存信息
	 * @param nodeStr
	 * @return
	 */
	public String getProductAndStock(String nodeStr);

	/**
	 * 
	 * @param reqNodeStr
	 * @return
	 */
	public String createProductAndStockFile(String reqNodeStr);

	public ExportSyncResult getProductAndStockFileResult(String operator);

	public void deleteExportResultByOperator(String operator);
}
