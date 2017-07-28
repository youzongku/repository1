package mapper.dismember;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.dismember.ApReminderSetting;

/**
 * 账期提醒设置
 * 
 * @author huangjc
 */
public interface ApReminderSettingMapper {

	int insertSelective(ApReminderSetting record);

	ApReminderSetting selectByPrimaryKey(Integer id);
	
	ApReminderSetting selectByAccount(@Param("account") String account);

	List<ApReminderSetting> selectAll(@Param("enabled")Boolean enabled);

	int updateByPrimaryKeySelective(ApReminderSetting record);

}