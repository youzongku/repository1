package handlers.openapi;

import com.google.common.eventbus.Subscribe;

import dto.openapi.AccessLog;
import utils.HttpUtil;

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
