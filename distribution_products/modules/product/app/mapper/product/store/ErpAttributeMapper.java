package mapper.product.store;

import entity.product.store.ErpAttribute;

public interface ErpAttributeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ErpAttribute record);

    int insertSelective(ErpAttribute record);

    ErpAttribute selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ErpAttribute record);

    int updateByPrimaryKey(ErpAttribute record);
}