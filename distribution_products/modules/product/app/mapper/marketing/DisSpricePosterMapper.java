package mapper.marketing;

import entity.marketing.DisSpricePoster;

import java.util.List;
import java.util.Map;

public interface DisSpricePosterMapper extends BaseMapper<DisSpricePoster> {

    /**
     * 获取指定活动下的所有海报图片
     */
    List<DisSpricePoster> findPosterByCondition(Map<String,Object> queryMap);

}