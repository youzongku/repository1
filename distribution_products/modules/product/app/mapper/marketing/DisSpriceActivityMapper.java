package mapper.marketing;

import dto.marketing.ActivityDTO;
import entity.marketing.DisSpriceActivity;

import java.util.List;

public interface DisSpriceActivityMapper extends BaseMapper<DisSpriceActivity> {

    /**
     * 多条件分页获取特价活动
     */
    List<DisSpriceActivity> findActivityByCondition(ActivityDTO dto);

    /**
     * 多条件分页获取特价活动记录条数
     */
    int findActivityCount(ActivityDTO dto);

    /**
     * 查询未启用或已开启的所有活动
     */
    List<DisSpriceActivity> findUnusedOrOpenedActivity();

}