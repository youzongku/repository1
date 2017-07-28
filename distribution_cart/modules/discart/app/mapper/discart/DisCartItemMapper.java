package mapper.discart;

import entity.discart.DisCartItem;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DisCartItemMapper extends BaseMapper<DisCartItem> {

    /**
     * 通过购物车ID查询购物车列表商品
     * @param cartId
     * @return
     */
    List<DisCartItem> getDisCartItemsByCartId(@Param("cartId")Integer cartId);

    /**
     * 获取购物车中SKU个数
     * @param cartId
     * @return
     */
    Integer getDisCartItemsCountByCartId(@Param("cartId")Integer cartId);

    /**
     * 取得当前购物车中指定SKU指定仓库的产品
     * @param params
     * @return
     */
    DisCartItem getDisCartItemByMultiParam(Map<String, Object> params);
    
    /**
     * 取得指定sku和wd的购物车订单
     * @param params
     * @return
     */
    List<DisCartItem> getCartItembyProductSkuAndWareId(Map<String, Object> params);
    
    /**
     * 清空购物车
     * @param cartId
     * @return
     */
    int deleteDisCartItemsByCartId(@Param("cartId")Integer cartId);
    
    int deleteDisCartItemsByEmail(@Param("email")String email);
    
    int batchInsert(@Param("disCartItemList")List<DisCartItem> disCartItemList);
    
    int deleteByPrimaryKey(int id);
    
    int deleteByIdList(@Param("idList")List<Integer> idList);

    /**
     * 批量更新购物车中商品的选中状态
     * @param cartId
     * @param select
     * @return
     */
    int batchUpdateSelectState(@Param("cartId")Integer cartId, @Param("select")Boolean select);

    int batchUpdateByPrimaryKeySelective(@Param("disCartItemList")List<DisCartItem> disCartItemList);
}