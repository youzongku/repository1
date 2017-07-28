package handlers.sales;

import java.util.Date;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import events.sales.JdLogisticsEvent;
import events.sales.LogisticsEvent;
import play.Logger;
import services.sales.IJdService;
import services.sales.IPinTwoDuoService;

public class AcceptLogisticsHandler {

	@Inject
	private IPinTwoDuoService pinDd;

	@Inject
	private IJdService jdService;

	@Subscribe
	public void execute(LogisticsEvent event) {
		Logger.info("推送pdd物流信息---【{}】", new Date());
		pinDd.pushPddLogistic(event);
	}

	@Subscribe
	public void executeForJd(JdLogisticsEvent jdLogisticsEvent) {
		Logger.info("推送京东物流信息---【{}】", new Date());
		jdService.pushLogistic(jdLogisticsEvent);
	}
}
