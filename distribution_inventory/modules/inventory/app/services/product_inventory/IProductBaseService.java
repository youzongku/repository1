package services.product_inventory;

import java.util.List;

import entity.product_inventory.ProductBase;


public interface IProductBaseService {
	
	/**
	 * 查询对应销售状态的商品
	 * @return
	 */
	public List<ProductBase> getProductsByStatus(Integer status);
	
	/**
	 * 获取商品标题
	 * @param sku
	 * @return
	 */
	public String getProductTitle(String sku);

	/**
	 * 根据sku获取在售中的商品
	 * @param sku
	 * @param i
	 * @return
	 */
	public List<ProductBase> getProductsInSalesBySku(String sku);

	/**
	 * 更加sku获取商品类别
	 * @param asText
	 * @return
	 */
	public List<Integer> getProductCategoryBySku(String sku);
	
}
