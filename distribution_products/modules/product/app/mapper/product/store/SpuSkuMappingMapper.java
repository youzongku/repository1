package mapper.product.store;

import entity.product.store.SpuSkuMapping;

public interface SpuSkuMappingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SpuSkuMapping record);

    int insertSelective(SpuSkuMapping record);

    SpuSkuMapping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SpuSkuMapping record);

    int updateByPrimaryKey(SpuSkuMapping record);
}