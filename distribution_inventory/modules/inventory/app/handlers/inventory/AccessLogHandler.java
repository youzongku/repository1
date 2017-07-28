package handlers.inventory;

import com.google.common.eventbus.Subscribe;

import dto.inventory.AccessLog;
import utils.inventory.HttpUtil;

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
