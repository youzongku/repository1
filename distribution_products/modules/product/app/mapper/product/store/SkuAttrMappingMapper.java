package mapper.product.store;

import entity.product.store.SkuAttrMapping;

public interface SkuAttrMappingMapper {
    int deleteByPrimaryKey(String id);

    int insert(SkuAttrMapping record);

    int insertSelective(SkuAttrMapping record);

    SkuAttrMapping selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SkuAttrMapping record);

    int updateByPrimaryKey(SkuAttrMapping record);
}