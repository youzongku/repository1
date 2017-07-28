package mapper.category;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.category.CategorySearchParamDto;
import dto.product.ProductListInfoDto;
import entity.category.ProductVcategoryMapper;

/**
 * 产品-虚拟类目关系对应表
 * t_product_vcatetory_map
 * 
 * @since 2015年12月16日 下午6:17:15
 */
public interface ProductVcategoryMapMapper {
	
	/**
	 * 通过类目id拿到产品list
	 * 
	 * @param catId
	 * @return
	 * @since 2015年9月24日 上午11:33:04
	 */
	List<ProductListInfoDto> getProductList(@Param("paramDto")CategorySearchParamDto paramDto);

	/**
	 * 根据虚拟类目ID查询该类目下的所有sku
	 * @param list
	 * @return
	 */
	List<String> getSkuLists(@Param("list")List<Integer> list);
	/**
	 * @param paramDto
	 * @return
	 */
	Integer getProductListTotal(@Param("paramDto")CategorySearchParamDto paramDto);
	
	/**
	 * 获取类目下的商品信息
	 * @author zbc
	 * @since 2016年9月8日 下午2:56:26
	 */
	List<ProductVcategoryMapper> getProVcategory(@Param("list")List<Integer> list);
	
}
