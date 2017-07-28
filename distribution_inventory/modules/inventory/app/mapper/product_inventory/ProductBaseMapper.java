package mapper.product_inventory;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.product_inventory.ProductBase;

public interface ProductBaseMapper {
	
    int deleteByPrimaryKey(Integer iid);

    int insert(ProductBase record);

    int insertSelective(ProductBase record);

    ProductBase selectByPrimaryKey(Integer iid);

    int updateByPrimaryKeySelective(ProductBase record);

    int updateByPrimaryKey(ProductBase record);
    
    /**
     * 根据商品状态查找商品
     * @param status
     * @return
     */
    public List<ProductBase> selectByStatus(Integer status);
    
    /**
     * 获取商品标题
     * @param sku
     * @return
     */
    public String getProductTitle(String sku);

    /**
     * 更加sku查询在售的商品
     * @param sku
     * @param i
     * @return
     */
	List<ProductBase> getProductsInSalesBySku(@Param("sku")String sku);

	/**
	 * 根据sku查询类别
	 * @param sku
	 * @return
	 */
	List<Integer> getCategoryBySku(@Param("sku")String sku);
    
}