package handlers.dismember;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;
import events.dismember.AccountPerioEvent;
import play.Logger;
import services.dismember.IAccountPeriodService;

/**
 * @author zbc
 * 2016年11月20日 下午5:30:00
 */
public class AccountPeriodHandler {
	
	@Inject
	private IAccountPeriodService apService;

	/**
	 * 更新账期状态：
	 *  1 未生效 次日生效
	 * @author zbc
	 * @since 2016年11月20日 下午5:31:39
	 */
	@Subscribe
	public void dealAccountPeriod(AccountPerioEvent event ){
		Logger.info("凌晨更新账期状态:[{}]",apService.dealAccountPeriod());
	}
	
}
