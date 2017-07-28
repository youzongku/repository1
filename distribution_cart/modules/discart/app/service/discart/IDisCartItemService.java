package service.discart;

import java.util.Map;

import dto.discart.CartInfoDto;
import dto.discart.ProductSelectedStateParam;

/**
 * Created by LSL on 2015/12/7.
 */
public interface IDisCartItemService {

    /**
     * 获取当前用户可用购物车列表商品数据
     * @param cartId
     * @param discount 用户对应的折扣值
     * @return
     */
	CartInfoDto getDisCartData(String dismember);

    /**
     * 从购物车中移除指定商品
     * @param email 当前用户
     * @param cartItemId 商品id
     * @return
     */
    Map<String, Object> deleteDisCartItemById(String email, String cartItemId);
    
    /**
     * 根据email删除购物车中所有数据
     * @param email
     * @return
     */
    String deleteDisCartItemByEmail(String email);

    /**
     * 更新购物车中商品的选中状态
     * @return
     */
    Map<String, Object> batchUpdateSelectStateOfCartItem(ProductSelectedStateParam param);
    
	/**
	 * 更新采购数量，更新前校验云仓库存
	 * @author zbc
	 * @since 2017年1月5日 下午4:57:47
	 */
	public Map<String, Object> updatePurchaseQties(String email, Integer itemId, Integer qty);

    /**
     * 获取指定用户购物车的商品数（sku种类数，并非个数）
     * @param email
     * @return
     */
    public Integer getItemsCount(String email);

}
