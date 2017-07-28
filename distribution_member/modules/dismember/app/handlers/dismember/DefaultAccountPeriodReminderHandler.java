package handlers.dismember;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.google.common.eventbus.Subscribe;

import entity.dismember.ApReminderSetting;
import entity.dismember.ApReminderSettingLog;
import events.dismember.DefaultAccountPeriodReminderEvent;
import mapper.dismember.ApReminderSettingLogMapper;
import mapper.dismember.ApReminderSettingMapper;
import play.Logger;

/**
 * 默认账期提醒事件处理
 * 
 * @author huangjc
 */
public class DefaultAccountPeriodReminderHandler {

	@Inject
	private ApReminderSettingMapper reminderSettingMapper;
	@Inject
	private ApReminderSettingLogMapper reminderSettingLogMapper;
	
	/**
	 * 计算税金
	 * @param event
	 */
	@Subscribe
	public void addDefaultReminderSetting(DefaultAccountPeriodReminderEvent event) {
		String account = event.getAccount();
		if (StringUtils.isEmpty(account)) {
			return;
		}
		
		ApReminderSetting setting = reminderSettingMapper.selectByAccount(account);
		if (setting==null) {
			// 新增一个
			ApReminderSetting newSetting = new ApReminderSetting();
			newSetting.setAccount(account);
			newSetting.setEnable(true);
			newSetting.setDaysAgo(3);
			newSetting.setCreateUser(event.getCreateUser());
			int line = reminderSettingMapper.insertSelective(newSetting);
			boolean suc = line==1;
			Logger.info("异步为{}添加默认账期提醒{}，创建账期的人是{}，配置是{}",account,(suc?"成功":"失败"),event.getCreateUser(),newSetting);
			
			addReminderSettingLog(newSetting, suc);
		}
	}
	
	private void addReminderSettingLog(ApReminderSetting rs, boolean suc){
		String createUser = rs.getLastUpdateUser();
		if (StringUtils.isEmpty(createUser)) {
			createUser = rs.getCreateUser();
		}
		ApReminderSettingLog log = new ApReminderSettingLog(rs.getId(), rs.getEnable(), rs.getDaysAgo(), createUser,
				suc);
		reminderSettingLogMapper.insert(log);
	}
	
}
