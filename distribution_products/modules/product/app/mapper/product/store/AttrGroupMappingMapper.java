package mapper.product.store;

import entity.product.store.AttrGroupMapping;

public interface AttrGroupMappingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AttrGroupMapping record);

    int insertSelective(AttrGroupMapping record);

    AttrGroupMapping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AttrGroupMapping record);

    int updateByPrimaryKey(AttrGroupMapping record);
}