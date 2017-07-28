package mapper.product.store;

import entity.product.store.SetAttrMultivalueMapping;

public interface SetAttrMultivalueMappingMapper {
    int insert(SetAttrMultivalueMapping record);

    int insertSelective(SetAttrMultivalueMapping record);
}