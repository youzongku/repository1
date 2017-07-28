package mapper.product.store;

import entity.product.store.Image;

public interface ImageMapper {
    int insert(Image record);

    int insertSelective(Image record);
}