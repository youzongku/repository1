package mapper.product;

import entity.product.ProductRuleDetail;

public interface ProductRuleDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductRuleDetail record);

    int insertSelective(ProductRuleDetail record);

    ProductRuleDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductRuleDetail record);

    int updateByPrimaryKey(ProductRuleDetail record);
}