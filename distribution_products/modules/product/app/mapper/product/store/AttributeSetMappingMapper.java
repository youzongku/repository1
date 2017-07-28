package mapper.product.store;

import entity.product.store.AttributeSetMapping;

public interface AttributeSetMappingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AttributeSetMapping record);

    int insertSelective(AttributeSetMapping record);

    AttributeSetMapping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AttributeSetMapping record);

    int updateByPrimaryKey(AttributeSetMapping record);
}