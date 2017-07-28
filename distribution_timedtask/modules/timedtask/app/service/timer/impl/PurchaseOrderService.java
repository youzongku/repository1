package service.timer.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import mapper.timer.PurchaseOrderMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import entity.timer.Constant;
import entity.timer.PurchaseOrder;
import entity.timer.PurchaseOrderStatus;
import events.timer.PhpEvents;
import play.Logger;
import play.libs.Json;
import service.timer.IHttpService;
import service.timer.IPurchaseOrderService;
import util.timer.HttpUtil;

public class PurchaseOrderService implements IPurchaseOrderService {

	@Inject
	private IHttpService httpService;
	
	@Inject
	private PurchaseOrderMapper purchaseOrderMapper;
	
	@Inject
	private EventBus ebus;
	
	// TODO -------------仓库接口，需要进行对接--------------
	/**
	 * 处理流程 1、 查询过期订单（有效时间小于当前时间，并且状态为未付款：0） 2、解除冻结库存（目前没有没做冻结，所以跳过） 3、更新订单状态
	 * ，为已失效 :PurchaseOrderFlag.INVALID.getType() 4、如果订单状态为 2 则 更新
	 * 销售单状态/sales/updStu {"id":getByNoRes.sid,"status":5} 返回 值 updStuRes
	 * 5、还原库存/inventory/resSto {"pros":{"historyDetail":updStuRes},"email":em}
	 * logger.info(返回值)
	 */
	@Override
	public void batchInvalid() {
		List<PurchaseOrder> list = purchaseOrderMapper.getInvalidOrders();
		List<PurchaseOrder> salesList = Lists.newArrayList();
		int count = 0;
		for (PurchaseOrder order : list) {
			order.setStatus(PurchaseOrderStatus.INVALIDATED);
			if (order.getPurchaseType() == 2) {
				salesList.add(order);
			}
			try {
				// change by zbc 解除库存锁
				httpService.unLock(order.getPurchaseOrderNo());
				syncLogs(order, Constant.UPD);
			} catch (Exception e) {
				e.printStackTrace();
			}
			count++;
		}

		if (list.size() > 0) {
			boolean flag = purchaseOrderMapper.batchUpdate(list) > 0;
			Logger.info("批量更新微仓订单状态"
					+ (flag ? "成功,更新了【" + count + "】条订单" : "失败"));
			try {
				if (flag) {
					for (PurchaseOrder order : salesList) {
						invalid(order);
					}
				}
			} catch (Exception e) {
				Logger.info("类型转换异常", e);
			}

		}
	}
	
	public void invalid(PurchaseOrder order) throws JsonProcessingException, IOException {
		updateSalesStatus(order.getSid(), 5);
	}
	
	public String updateSalesStatus(Integer id, Integer status) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("status", status);
		Logger.info("updateSalesStatus     post_string--->"
				+ Json.toJson(params));
		String response_string = HttpUtil.post(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/sales/updStu");
		Logger.info("updateSalesStatus     response_string--->"
				+ response_string);
		return response_string;
	}
	
	/**
	 * 异步记录日志
	 * @param purchaseOrder
	 * @param type
	 */
	public void syncLogs(PurchaseOrder purchaseOrder, String type) {
		Map<String, Object> logs = Maps.newHashMap();
		logs.put("orderId", purchaseOrder.getId());
		ebus.post(new PhpEvents(Constant.PURCHASE_KEY, logs, type));
	}
}
