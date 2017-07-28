package mapper.product;

import java.util.List;

import entity.product.ProductPriceRule;

/**
 * @author zbc
 * 2016年7月28日 上午11:50:58
 */
public interface ProductPriceRuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductPriceRule record);

    int insertSelective(ProductPriceRule record);

    ProductPriceRule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductPriceRule record);

    int updateByPrimaryKey(ProductPriceRule record);

	List<ProductPriceRule> selectAll();

}