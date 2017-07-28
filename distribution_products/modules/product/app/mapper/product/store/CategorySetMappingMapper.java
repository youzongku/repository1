package mapper.product.store;

import entity.product.store.CategorySetMapping;

public interface CategorySetMappingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CategorySetMapping record);

    int insertSelective(CategorySetMapping record);

    CategorySetMapping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CategorySetMapping record);

    int updateByPrimaryKey(CategorySetMapping record);
}