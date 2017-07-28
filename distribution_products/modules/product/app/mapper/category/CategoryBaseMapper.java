package mapper.category;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.category.CategorySearchParamDto;
import entity.category.CategoryBase;

public interface CategoryBaseMapper {
    int deleteByPrimaryKey(Integer iid);

    int insert(CategoryBase record);

    int insertSelective(CategoryBase record);

    CategoryBase selectByPrimaryKey(Integer iid);

    int updateByPrimaryKeySelective(CategoryBase record);

    int updateByPrimaryKey(CategoryBase record);

	List<CategoryBase> realCateQuery(@Param("paramDto")CategorySearchParamDto dto);

	/**
	 * @param catIds
	 * @return
	 */
	List<Integer> queryChild(@Param("list")List<Integer> catIds);

	/**
	 * t_product_category_mapper
	 * @param list
	 * @return
	 */
	List<String> getSkusMapper(@Param("list")List<Integer> list);

	/**
	 * 根据类目ID查询类目详细信息
	 * @param catId
	 * @return
	 */
	CategoryBase queryCategory(Integer catId);
	
	/**
	 * 获取系数表中 未初始化的 类目信息
	 * @author zbc
	 * @since 2016年8月20日 上午9:44:39
	 */
	List<CategoryBase> getInitCate();
}