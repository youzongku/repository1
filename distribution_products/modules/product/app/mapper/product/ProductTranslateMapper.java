package mapper.product;

import org.apache.ibatis.annotations.Param;

import dto.product.ProductSearchParamDto;
import entity.product.ProductTranslate;

public interface ProductTranslateMapper {
    int deleteByPrimaryKey(Integer iid);

    int insert(ProductTranslate record);

    int insertSelective(ProductTranslate record);

    ProductTranslate selectByPrimaryKey(Integer iid);

    int updateByPrimaryKeySelective(ProductTranslate record);

    int updateByPrimaryKey(ProductTranslate record);

	ProductTranslate queryProductTranslate(@Param("paramDto") ProductSearchParamDto paramDto);
}