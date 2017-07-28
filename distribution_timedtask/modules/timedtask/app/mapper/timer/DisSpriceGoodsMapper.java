package mapper.timer;

import java.util.List;
import java.util.Map;

import entity.timer.DisSpriceGoods;

public interface DisSpriceGoodsMapper extends BaseMapper<DisSpriceGoods> {

    /**
     * 查询指定活动下的所有关联商品
     */
    List<DisSpriceGoods> findGoodsByCondition(Map queryMap);

    

}