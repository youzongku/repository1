package mapper.product.store;

import entity.product.store.AttributeSet;

public interface AttributeSetMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AttributeSet record);

    int insertSelective(AttributeSet record);

    AttributeSet selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AttributeSet record);

    int updateByPrimaryKey(AttributeSet record);
}