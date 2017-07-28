package mapper.product;

import entity.product.ProductGroupPrice;

public interface ProductGroupPriceMapper {
    int deleteByPrimaryKey(Integer iid);

    int insert(ProductGroupPrice record);

    int insertSelective(ProductGroupPrice record);

    ProductGroupPrice selectByPrimaryKey(Integer iid);

    int updateByPrimaryKeySelective(ProductGroupPrice record);

    int updateByPrimaryKey(ProductGroupPrice record);
}