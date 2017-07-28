package mapper.product.store;

import entity.product.store.SkuEntity;

public interface SkuEntityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SkuEntity record);

    int insertSelective(SkuEntity record);

    SkuEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SkuEntity record);

    int updateByPrimaryKey(SkuEntity record);
}