package mapper.dismember;

import java.util.List;

import entity.dismember.LoginHistory;

/**
 *
 * @author luwj
 *
 */
public interface LoginHistoryMapper extends BaseMapper<LoginHistory> {

    /**
     * 获取最近登录记录
     * @param eamil
     * @return
     */
    List<LoginHistory> getRecentHistory(String eamil);
}