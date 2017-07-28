package mapper.marketing;

import entity.marketing.DisSpriceGoods;

import java.util.List;
import java.util.Map;

public interface DisSpriceGoodsMapper extends BaseMapper<DisSpriceGoods> {

    /**
     * 查询指定活动下的所有关联商品
     */
    List<DisSpriceGoods> findGoodsByCondition(Map<String,Object> queryMap);

    /**
     * 查询指定条件下的单个关联商品
     */
    DisSpriceGoods getGoodsByCondition(Map<String,Object> queryMap);

    /**
     * 获取已开启活动的指定SKU和仓库ID的商品数量
     */
    int getOpendActGoodsCount(Map<String,Object> queryMap);

    /**
     * 获取未启用或已开启活动的指定SKU和仓库ID的商品数量
     */
    int getRelatedActGoodsCount(Map<String,Object> queryMap);

	/**
	 * 获取参与活动的SKU
	 * @param skus
	 * @return
	 */
	List<DisSpriceGoods> findActivitySkus(List<String> skus);

}