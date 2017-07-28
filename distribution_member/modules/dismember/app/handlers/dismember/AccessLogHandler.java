package handlers.dismember;

import com.google.common.eventbus.Subscribe;

import dto.dismember.AccessLog;
import utils.dismember.HttpUtil;

public class AccessLogHandler {

	/**
	 * 打印访问日志
	 * @param event
	 */
	@Subscribe
	public void execute(AccessLog event) {
		HttpUtil.writeInLog(event, "access.log");
	}
	
	
}
