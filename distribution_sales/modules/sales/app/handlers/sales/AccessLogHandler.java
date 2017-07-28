package handlers.sales;

import com.google.common.eventbus.Subscribe;

import dto.sales.AccessLog;
import util.sales.HttpUtil;

public class AccessLogHandler {

	/**
	 * 打印日志
	 * @param event
	 */
	@Subscribe
	public void execute(AccessLog event) {
		HttpUtil.writeInLog(event, "access.log");
	}
}
