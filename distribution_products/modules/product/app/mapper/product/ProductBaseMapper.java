package mapper.product;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import component.elasticsearch.ProductLiteDoc;
import dto.ProdcutInventoryDataExportDto;
import dto.product.ProductLite;
import dto.product.ProductSearchParamDto;
import entity.product.ProductBase;
import entity.product.StockEntity;

/**
 * t_product_base
 * 
 * @author ye_ziran
 * @since 2015年12月8日 下午2:51:45
 */
public interface ProductBaseMapper {
	
	/**
	 * 根据搜索条件查询
	 * 
	 * @param paramDto
	 * @param pageNum
	 * @param length
	 * @return
	 * @author ye_ziran
	 * @since 2015年12月8日 下午2:58:09
	 */
	List<ProductBase> queryByParam(@Param("paramDto")ProductSearchParamDto paramDto, Integer pageNum, Integer length);

	ProductBase queryProductBase(@Param("paramDto") ProductSearchParamDto productSearchDto);
	
	List<ProductLite> getProductByListingIDs(
			@Param("list") List<String> listingIDs, int websiteID,
			int languageID);

	/**
	 * @param productSearchDto
	 * @param siteId
	 * @param langId
	 * @return
	 */
	Integer getProductsCount(@Param("param")ProductSearchParamDto productSearchDto, int siteId, int langId);
	
	/**
	 * 根据sku获取图片url
	 * @param sku
	 * @return
	 */
	String getImgUrl(String sku);
	
	List<ProductLite> products(@Param("param")ProductSearchParamDto productSearchDto);
	
	Integer productCount(@Param("param")ProductSearchParamDto productSearchDto);

	List<StockEntity> queryStock(@Param("list")List<Map<String, Object>> stockQuery, @Param("email")String email);

	/**
	 * 去重获取所有商品品牌
	 * @author zbc
	 * @since 2016年8月17日 上午9:59:15
	 */
	List<String> getBrand();

	List<ProductLite> inventoryGoods(@Param("param")ProductSearchParamDto productSearchDto);

	Integer inventoryGoodsCount(@Param("param")ProductSearchParamDto productSearchDto);
	
	/**
	 * 获取商品信息文档初始化需要的商品数据；
	 * <p>
	 * 不适合查询用
	 * 
	 * @return
	 * @author ye_ziran
	 * @since 2017年1月12日 下午12:31:03
	 */
	List<ProductLiteDoc> getProductsInfo();
	
	/**
	 * 获得商品的类目树
	 * 
	 * @return
	 * @author ye_ziran
	 * @since 2017年3月8日 上午10:31:23
	 */
	List<ProductLiteDoc> getProductsCatTree();
	
	/**
	 * 获得商品的虚拟类目树
	 * 
	 * @return
	 * @author ye_ziran
	 * @since 2017年3月8日 上午10:32:19
	 */
	List<ProductLiteDoc> getProductsVirCatTree();

	/**
	 * 根据过期日期导出商品库存信息
	 * @param expiration_begin
	 * @param expiration_end
	 * @return
	 */
	List<ProdcutInventoryDataExportDto> productInventoryDataExport(@Param("beginTime") String expiration_begin, @Param("endTime") String expiration_end);

	
	/**
	 * 
	 * @param sql
	 * @return
	 */
	List<Map> export(@Param("param") String sql);
	
	/**
	 * 更新非卖状态
	 * @param skus
	 * @param salable
	 * @return
	 */
	int updateSalable(@Param("skus")List<String> skus, @Param("salable")Integer salable);

	

	/**
	 * 查询商品和库存信息
	 * @param searchDto
	 * @return
	 */
	List<Map> getProductAndStock(@Param("status")String status, @Param("categoryId")String categoryId, @Param("typeId")String typeId,
			@Param("title")String title, @Param("warehouseId")String warehouseId,
			@Param("currPage")int currPage, @Param("pageSize")int pageSize);
}	
