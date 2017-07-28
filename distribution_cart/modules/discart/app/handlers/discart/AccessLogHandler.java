package handlers.discart;

import com.google.common.eventbus.Subscribe;

import dto.discart.AccessLog;

public class AccessLogHandler {
	/**
	 * 打印日志
	 * @param event
	 */
	@Subscribe
	public void execute(AccessLog event) {
//		HttpUtil.writeInLog(event, "access.log");
	}
}
