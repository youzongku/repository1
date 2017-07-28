package mapper.product.store;

import entity.product.store.AttrMultivalue;

public interface AttrMultivalueMapper {
    int deleteByPrimaryKey(String id);

    int insert(AttrMultivalue record);

    int insertSelective(AttrMultivalue record);

    AttrMultivalue selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AttrMultivalue record);

    int updateByPrimaryKey(AttrMultivalue record);
}