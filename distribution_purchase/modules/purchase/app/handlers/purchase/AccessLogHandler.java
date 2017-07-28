package handlers.purchase;

import com.google.common.eventbus.Subscribe;

import dto.purchase.AccessLog;
import utils.purchase.HttpUtil;

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
