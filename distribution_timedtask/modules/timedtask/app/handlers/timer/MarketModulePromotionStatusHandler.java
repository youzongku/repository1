package handlers.timer;

import mapper.timer.PromotionActivityMapper;
import play.Logger;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import events.timer.PromotionEndStatusEvent;
import events.timer.PromotionStartStatusEvent;

public class MarketModulePromotionStatusHandler {
	@Inject
	private PromotionActivityMapper promotionActivityMapper;

	@Subscribe
	public void executeUpdateEnd(PromotionStartStatusEvent event) {
		if (SystemEventHandler.run_timed_task) {
			int count = promotionActivityMapper.statusEndTimerTrigger();
			Logger.info(
					"[marketEvent]========	check  outdated promotion activity,[{}] updated	========[marketEvent]",
					count);
		}
	}

	@Subscribe
	public void executeUpdateStart(PromotionEndStatusEvent event) {
		if (SystemEventHandler.run_timed_task) {
			int count = promotionActivityMapper.statusStartTimerTrigger();
			Logger.info(
					"[marketEvent]========	check  active promotion activity,[{}] updated	========[marketEvent]",
					count);
		}
	}
}
