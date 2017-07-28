package services.product;


import java.util.List;
import java.util.Set;

import dto.category.CategorySearchParamDto;
import dto.product.PageResultDto;
import dto.product.ProductSearchParamDto;

/**
 * 
 * 
 * @author ye_ziran
 * @since 2015年12月17日 上午10:50:00
 */
public interface IProductVcategoryMapService {
	/**
	 * 通过类目id拿到产品list
	 * 
	 * @param catId
	 * @return
	 * @author ye_ziran
	 * @since 2015年12月17日 上午10:43:05
	 */
	PageResultDto getProductList(CategorySearchParamDto paramDto);


	void queryChild(List<Integer> catIds, Set<Integer> all);


	PageResultDto getSkuLists(List<Integer> catIds, ProductSearchParamDto searchDto);
}
