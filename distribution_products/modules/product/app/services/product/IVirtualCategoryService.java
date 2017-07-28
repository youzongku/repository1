package services.product;

import java.util.List;
import java.util.Map;
import java.util.Set;

import dto.category.CategorySearchParamDto;
import dto.category.VirCategoryDto;
import dto.product.PageResultDto;
import dto.product.ProductSearchParamDto;
import entity.category.CategoryBase;
import entity.category.VirtualCategory;
import forms.category.VirtualCategoryForm;

/**
 * 虚拟类目service
 * 
 * @author ye_ziran
 * @since 2015年12月8日 下午6:03:02
 */
public interface IVirtualCategoryService {
	
	/**
	 * 根据搜索条件查询数据
	 * 
	 * @param paramDto
	 * @return
	 * @author ye_ziran
	 * @since 2015年12月8日 下午6:26:29
	 */
	List<VirtualCategoryForm> query(CategorySearchParamDto paramDto);

	/**
	 * 查询真实类目
	 * @param dto
	 * @return
	 */
	List<CategoryBase> realCateQuery(CategorySearchParamDto dto);

	void queryChild(List<Integer> list, Set<Integer> all);

	List<String> getSkuLists(List<Integer> list);

	VirtualCategory categoryDetail(Integer vcId);

	List<VirtualCategoryForm> queryAll(CategorySearchParamDto dto);

	List<VirtualCategoryForm> queryParent(Integer vcId);
	
	void initAll();

	void emptyAll();

	/**
	 * 获取虚拟类目 首页虚拟类目下的商品 
	 * @author zbc
	 * @since 2016年9月7日 下午4:27:09
	 */
	List<Map<String, Object>> getProInfo(CategorySearchParamDto cateDto);

	/**
	 * 获取类目下的所有sku
	 * @author zbc
	 * @since 2016年10月17日 上午9:19:39
	 */
	List<String> getSkuList(Integer catId);

	/**
	 *
	 * @author zbc
	 * @since 2017年3月9日 上午11:43:43
	 */
	PageResultDto getSkuLists(ProductSearchParamDto searchDto);
	
	/**
	 * 根据parentId找到所有子节点，并从下一级开始算1级
	 * 
	 * @param parentId
	 * @return
	 * @author ye_ziran
	 * @since 2017年3月28日 下午5:03:48
	 */
	List<VirCategoryDto> getAllSubsByParentId(Integer parentId);
}
