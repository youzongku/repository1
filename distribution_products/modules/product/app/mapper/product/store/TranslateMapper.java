package mapper.product.store;

import entity.product.store.Translate;

public interface TranslateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Translate record);

    int insertSelective(Translate record);

    Translate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Translate record);

    int updateByPrimaryKey(Translate record);
}