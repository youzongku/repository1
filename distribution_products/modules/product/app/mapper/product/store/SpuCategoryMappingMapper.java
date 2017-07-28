package mapper.product.store;

import entity.product.store.SpuCategoryMapping;

public interface SpuCategoryMappingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SpuCategoryMapping record);

    int insertSelective(SpuCategoryMapping record);

    SpuCategoryMapping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SpuCategoryMapping record);

    int updateByPrimaryKey(SpuCategoryMapping record);
}