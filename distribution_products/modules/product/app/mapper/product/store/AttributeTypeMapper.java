package mapper.product.store;

import java.util.List;

import entity.product.store.AttributeType;

public interface AttributeTypeMapper {
    int insert(AttributeType record);

    int insertSelective(AttributeType record);

	List<AttributeType> selectAll();
}