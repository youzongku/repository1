package handlers.timer;

import java.util.List;
import java.util.Map;

import mapper.timer.DisSpriceActivityMapper;
import mapper.timer.DisSpriceGoodsMapper;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTimeUtils;

import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import entity.timer.DisSpriceActivity;
import entity.timer.DisSpriceGoods;
import events.timer.ActOpenEvent;
import events.timer.ContractEvent;
import play.Logger;
import service.timer.IQuotedService;

public class ProductModuleEventHandler {

	@Inject
	private DisSpriceActivityMapper activityMapper;
	@Inject
	private DisSpriceGoodsMapper goodsMapper;
	@Inject
	private IQuotedService quotedService;

	@Subscribe
	public void autoOpenContract(ContractEvent event) {
		if (SystemEventHandler.run_timed_task) {
			Logger.info("[productEvent]========	auto start quoted price 	========[productEvent]");
			quotedService.autoOpenNotStartQuoted();
		}
	}

	/**
	 * 活动开启自动任务
	 */
	@Subscribe
	public void openAct(ActOpenEvent event) {
		if (SystemEventHandler.run_timed_task) {
			Logger.info("[productEvent]========	open activity start	========[productEvent]");
			List<DisSpriceActivity> list = activityMapper
					.findUnusedOrOpenedActivity();
			if (CollectionUtils.isNotEmpty(list)) {
				long now;
				int line;
				Map queryMap = Maps.newHashMap();
				for (DisSpriceActivity item : list) {
					now = DateTimeUtils.currentTimeMillis();
					if (item.getStartTime().getTime() <= now
							&& item.getEndTime().getTime() > now) {
						item.setActivityStatus(2);// 启用中
					} else if (item.getEndTime().getTime() <= now) {
						item.setActivityStatus(3);// 已结束
					}
					queryMap.put("activityId", item.getId());
					List<DisSpriceGoods> goods = goodsMapper
							.findGoodsByCondition(queryMap);
					Logger.debug("openAct    [update activity status]id----->"
							+ item.getId());
					if (CollectionUtils.isNotEmpty(goods)) {
						line = activityMapper.updateByPrimaryKeySelective(item);
						Logger.info(
								"[productEvent]========	openAct    [update activity status]status----->[{}]	========[productEvent]",
								item.getActivityStatus());
						Logger.info(
								"[productEvent]========	openAct    [update activity status]line----->[{}]	========[productEvent]",
								line);
					} else {
						Logger.info("[productEvent]========	openAct    [current activity has not goods.]	========[productEvent]");
					}
				}
			}
			Logger.info("[productEvent]========	open activity end	========[productEvent]");
		}
	}
}
