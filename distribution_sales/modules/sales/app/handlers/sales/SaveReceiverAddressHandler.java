package handlers.sales;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import events.sales.SaveReceiverAddressEvent;
import play.Logger;
import services.sales.ISaleReceiverService;

public class SaveReceiverAddressHandler {
	
	@Inject
	private ISaleReceiverService saleReceiverService;

	@Subscribe
	public void saveReceiverAddress(SaveReceiverAddressEvent event) {
		Logger.info("异步保存发货单收货人地址信息，异步事件event：{}", event);
		if (event.getMo()!=null) {
			saleReceiverService.addReceiver(event.getMo());
		} else {
			saleReceiverService.addReceiver(event.getMain(), event.getBase());
		}
	}
}
