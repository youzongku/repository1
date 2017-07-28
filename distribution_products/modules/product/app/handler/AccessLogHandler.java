package handler;

import com.google.common.eventbus.Subscribe;

import dto.AccessLog;

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
