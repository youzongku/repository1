package handlers.payment;

import com.google.common.eventbus.Subscribe;

import dto.payment.AccessLog;
import utils.payment.HttpUtil;

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
