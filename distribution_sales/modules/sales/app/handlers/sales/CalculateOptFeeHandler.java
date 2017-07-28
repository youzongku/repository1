package handlers.sales;

import play.Logger;
import services.sales.ISaleService;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import events.sales.CalculateOptFeeEvent;
/**
 * 计算发货单操作费
 * @author huangjc
 * @date 2017年1月12日
 */
public class CalculateOptFeeHandler {
	@Inject
	ISaleService saleService;
	/**
	 * 营销单审核通过生成发货单
	 * @param event
	 */
	@Subscribe
	public void generateOrder(CalculateOptFeeEvent event) {
		Logger.info("计算发货单的操作费：" + event.getMain().getSalesOrderNo());
		saleService.calculateOptFee(event.getMain(), event.getBase(), event.getDetails());
	}
}
