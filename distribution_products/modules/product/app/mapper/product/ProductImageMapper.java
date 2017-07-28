package mapper.product;

import java.util.List;

import dto.product.ProductSearchParamDto;
import entity.product.ProductImage;

public interface ProductImageMapper {
    int deleteByPrimaryKey(Integer iid);

    int insert(ProductImage record);

    int insertSelective(ProductImage record);

    ProductImage selectByPrimaryKey(Integer iid);

    int updateByPrimaryKeySelective(ProductImage record);

    int updateByPrimaryKey(ProductImage record);
    
    /**
     * 根据sku查商品图片，返回该商品所有图片路径
     * 
     * @author ouyangyaxiong
     * @param paramDto
     * @return
     * @since 2015年12月10日
     */
    List<ProductImage> queryProductImgs(ProductSearchParamDto paramDto);
    
    /**
     * 根据sku或listingid查商品 主图（baseimage） 图片路径（一张）
     * 
     * @author ouyangyaxiong
     * @param paramDto
     * @return
     * @since 2015年12月10日
     */
    String queryProductImg(ProductSearchParamDto paramDto);
}