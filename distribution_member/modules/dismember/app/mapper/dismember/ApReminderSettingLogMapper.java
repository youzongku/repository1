package mapper.dismember;

import org.apache.ibatis.annotations.Param;

import entity.dismember.ApReminderSettingLog;
/**
 * 账期提醒设置日志
 * 
 * @author huangjc
 */
public interface ApReminderSettingLogMapper {

    int insert(ApReminderSettingLog record);

    /**
     * 根据配置id来查询日志
     * @param reminderSettingId
     * @return
     */
    ApReminderSettingLog selectByRSId(@Param("reminderSettingId")Integer reminderSettingId);

}