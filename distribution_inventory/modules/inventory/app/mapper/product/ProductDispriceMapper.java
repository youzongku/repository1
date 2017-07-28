package mapper.product;

import org.apache.ibatis.annotations.Param;

import entity.product.ProductDisprice;

public interface ProductDispriceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductDisprice record);

    int insertSelective(ProductDisprice record);

    ProductDisprice selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductDisprice record);

    int updateByPrimaryKey(ProductDisprice record);
    
    /**
     * 根据sku和 仓库id 获取商品信息
     * @param sku
     * @param warehouseId 
     * @author zbc
     * @since 2016年11月16日 下午3:14:12
     */
    ProductDisprice selectByParam(@Param("sku")String sku,@Param("wareId")Integer wareId);
    
    
}