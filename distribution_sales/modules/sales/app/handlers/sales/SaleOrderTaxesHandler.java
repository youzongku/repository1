package handlers.sales;

import java.util.Map;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import entity.sales.SaleMain;
import events.sales.AsyncExportEvent;
import events.sales.AutoPayEvent;
import events.sales.ClosedOrderEvent;
import events.sales.GenerateSaleOrderEvent;
import events.sales.PddSyncEvent;
import play.Logger;
import play.libs.Json;
import services.marketing.IMarketingOrderService;
import services.sales.IAsyncExportService;
import services.sales.IPinTwoDuoService;
import services.sales.ISaleLockService;
import services.sales.ISaleOrderTaxesService;
/**
 * 下单计算税金
 * @author huangjc
 * @date 2016年11月1日
 */
public class SaleOrderTaxesHandler {
	
	@Inject
	ISaleOrderTaxesService saleOrderTaxesService;
	@Inject
	private IMarketingOrderService marketOrderService;
	@Inject
	private ISaleLockService lockService;
	@Inject
	private IPinTwoDuoService pinTwoDuoService;
	@Inject
	private IAsyncExportService asyncExportService;
	

	/**
	 * 计算税金
	 * @param event
	 */
	@Subscribe
	public void calculateTaxes(SaleMain saleMain) {
		saleOrderTaxesService.calculateTaxes(saleMain.getId(), saleMain.getSalesOrderNo());
	}
	
	/**
	 * 余额自动支付
	 * @param event
	 */
	@Subscribe
	public void autoPay(AutoPayEvent event) {
		saleOrderTaxesService.autoPay(event);
	}
	
	/**
	 * 营销单审核通过生成发货单
	 * @param event
	 */
	@Subscribe
	public void generateOrder(GenerateSaleOrderEvent event) {
		Logger.info("营销单审核通过生成发货单：" + event.getMarketOrder().getMarketingOrderNo());
		marketOrderService.generateOrder(event);
	}
	
	/**
	 * 订单客服审核不通过关闭订单
	 * @param event
	 */
	@Subscribe
	public void closeOrder(ClosedOrderEvent event) {
		Logger.info("销售单客服审核不通过关闭订单：" + event.getSo());
		Map<String, Object> cancelOrder = lockService.cancelOrder(Json.toJson(event),null);
		Logger.info("关闭订单返回值：" + cancelOrder);
	}
	
	/**
	 * 拼多多异步同步订单
	 * @param event
	 */
	@Subscribe
	public void pddSync(PddSyncEvent event) {
		Logger.info("拼多多异步同步订单.");
		pinTwoDuoService.pullOrder(event.getMain(),event.getEmail());
	}
	
	@Subscribe
	public void inertExcel(AsyncExportEvent event){
		asyncExportService.insert(event.getMap(), event.getAsyncExportDto(), event.getHeaderString(), event.getFieldsMap());
	}
	
	
}
