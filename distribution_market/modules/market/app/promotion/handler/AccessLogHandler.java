package promotion.handler;

import com.google.common.eventbus.Subscribe;

import dto.marketing.promotion.AccessLog;
import util.marketing.promotion.HttpUtil;

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
