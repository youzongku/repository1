package promotion.handler;

import mapper.marketing.promotion.PromotionActivityMapper;
import play.Logger;
import promotion.events.PromotionEndStatusEvent;
import promotion.events.PromotionStartStatusEvent;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class PromotionStatusHandler {
	@Inject
	private PromotionActivityMapper promotionActivityMapper;

	@Subscribe
	public void executeUpdateEnd(PromotionStartStatusEvent event) {
		int count = promotionActivityMapper.statusEndTimerTrigger();
		Logger.info("statusEndTimerTrigger-查询是否有过期促销活动，更新的数量为："+count);
	}

	@Subscribe
	public void executeUpdateStart(PromotionEndStatusEvent event) {
		int count = promotionActivityMapper.statusStartTimerTrigger();
		Logger.info("statusStartTimerTrigger-查询是否有正在活动期的促销活动，更新的数量为："+count);
	}
}
