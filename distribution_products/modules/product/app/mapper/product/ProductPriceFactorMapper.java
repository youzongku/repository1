package mapper.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.product.ProductPriceFactor;

public interface ProductPriceFactorMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductPriceFactor record);

    int insertSelective(ProductPriceFactor record);

    ProductPriceFactor selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductPriceFactor record);

    int updateByPrimaryKey(ProductPriceFactor record);

	int batchInsert(List<ProductPriceFactor> list);
	
	int batchUpdate(List<ProductPriceFactor> list);
	
	List<ProductPriceFactor> getBybBrandId(@Param("CategoryBrandId") Integer id); 
	
	List<ProductPriceFactor> getByPriceIds(List<Integer> ids); 

	
}