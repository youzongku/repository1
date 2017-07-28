package mapper.product.store;

import entity.product.store.Price;

public interface PriceMapper {
    int insert(Price record);

    int insertSelective(Price record);
}