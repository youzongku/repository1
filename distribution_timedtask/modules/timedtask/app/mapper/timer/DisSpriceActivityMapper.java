package mapper.timer;


import java.util.List;

import entity.timer.DisSpriceActivity;

public interface DisSpriceActivityMapper extends BaseMapper<DisSpriceActivity> {

    /**
     * 查询未启用或已开启的所有活动
     */
    List<DisSpriceActivity> findUnusedOrOpenedActivity();

}