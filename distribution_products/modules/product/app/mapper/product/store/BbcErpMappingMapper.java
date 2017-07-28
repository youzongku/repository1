package mapper.product.store;

import entity.product.store.BbcErpMapping;

public interface BbcErpMappingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BbcErpMapping record);

    int insertSelective(BbcErpMapping record);

    BbcErpMapping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BbcErpMapping record);

    int updateByPrimaryKey(BbcErpMapping record);
}