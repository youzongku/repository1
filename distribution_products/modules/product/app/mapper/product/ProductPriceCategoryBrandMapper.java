package mapper.product;

import java.util.List;

import dto.product.ProductPriceFactorDto;
import entity.product.ProductPriceCategoryBrand;

public interface ProductPriceCategoryBrandMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductPriceCategoryBrand record);

    int insertSelective(ProductPriceCategoryBrand record);

    ProductPriceCategoryBrand selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductPriceCategoryBrand record);

    int updateByPrimaryKey(ProductPriceCategoryBrand record);
    
    ProductPriceCategoryBrand select(ProductPriceCategoryBrand record);
    
    List<ProductPriceCategoryBrand> getPageList(ProductPriceFactorDto dto);

    int getPageCount(ProductPriceFactorDto dto);
    
    int batchInsert(List<ProductPriceCategoryBrand> list);
}