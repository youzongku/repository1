package handlers.dismember;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import entity.ap.ApOrderDetail;
import events.dismember.BackUpApOrderDetailEvent;
import mapper.ap.ApOrderDetailMapper;
import play.Logger;
import services.dismember.IHttpService;
import utils.dismember.JsonCaseUtil;
/**
 * 备份账期支付的订单
 */
public class BackUpApOrderDetailHandler {
	@Inject
	private IHttpService httpService;
	@Inject
	private ApOrderDetailMapper apOrderDetailMapper;
	
	@Subscribe
	public void dealAccountPeriod(BackUpApOrderDetailEvent event){
		Logger.info("异步备份账期支付的订单:[{}]",event.getOrderNo());
		backUpApOrder(event.getApOrderId(), event.getOrderNo());
	}
	
	private void backUpApOrder(Integer apOrderId, String orderNo) {
		if (StringUtils.isEmpty(orderNo)) {
			Logger.info("异步备份账期支付的订单，单号为空，停止备份");
			return;
		}
		if (CollectionUtils.isNotEmpty(apOrderDetailMapper.selectByOrderNo(orderNo))) {
			Logger.info("异步备份账期支付的订单，订单已同步过，停止备份");
			return;
		}
		
		List<ApOrderDetail> apOrderDetailList = null;
		if (orderNo.startsWith("CG")) {
			apOrderDetailList = getPurchaseOrderDetails(apOrderId, orderNo);
		} else if (orderNo.startsWith("XS")) {
			apOrderDetailList = getSalesOrderDetails(apOrderId, orderNo);
		} else if (orderNo.startsWith("HBXS")) {// 合并发货单
			apOrderDetailList = getCombinedSalesOrderProsDetails(apOrderId, orderNo);
		} 
		
		if (CollectionUtils.isEmpty(apOrderDetailList)) {
			Logger.info("获取到的订单[{}]的详情为空,不进行保存操作");
			return;
		}
		
		// 保存详情
		int line = apOrderDetailMapper.insertBatch(apOrderDetailList);
		Logger.info("保存订单[{}]的详情{}", orderNo, line > 0 ? "成功" : "失败");
	}
	
	private List<ApOrderDetail> getPurchaseOrderDetails(Integer apOrderId, String purchaseOrderNo){
		List<ApOrderDetail> apOrderDetailList = Lists.newArrayList();
		JsonNode purchaseOrderNode = null;
		try {
			purchaseOrderNode = httpService.getPurchaseOrder(purchaseOrderNo);
		} catch (IOException e) {
			Logger.info("账期支付订单，获取采购单[{}]异常，{}", purchaseOrderNo, e);
		}
		if (purchaseOrderNode==null) {
			Logger.info("异步备份账期支付的订单，获取采购单[{}]失败，停止备份",purchaseOrderNo);
			return apOrderDetailList;
		}
		
		for (Iterator<JsonNode> it = purchaseOrderNode.get("details").iterator(); it.hasNext(); ) {
			JsonNode next = it.next();
			String sku = next.get("sku").asText();
			String productName = next.get("productName").asText();
			int qty = next.get("qty").asInt();
			int warehouseId = next.get("warehouseId").asInt();
			String warehouseName = next.get("warehouseName").asText();
			apOrderDetailList.add(new ApOrderDetail(apOrderId, purchaseOrderNo, sku, productName, qty, warehouseId, warehouseName));
		}
		return apOrderDetailList;
	}
	
	private List<ApOrderDetail> getSalesOrderDetails(Integer apOrderId, String salesOrderNo){
		List<ApOrderDetail> apOrderDetailList = Lists.newArrayList();
		JsonNode salesOrderNode = null;
		try {
			salesOrderNode = httpService.getSalesOrder(salesOrderNo);
		} catch (IOException e) {
			Logger.info("账期支付订单，获取发货单[{}]异常，{}", salesOrderNo, e);
		}
		if (salesOrderNode==null || !salesOrderNode.get("suc").asBoolean()) {
			Logger.info("异步备份账期支付的订单，获取发货单[{}]失败，停止备份",salesOrderNo);
			return apOrderDetailList;
		}
		
		for (Iterator<JsonNode> it = salesOrderNode.get("details").iterator(); it.hasNext(); ) {
			JsonNode next = it.next();
			String sku = next.get("sku").asText();
			String productName = next.get("productName").asText();
			int qty = next.get("qty").asInt();
			int warehouseId = next.get("warehouseId").asInt();
			String warehouseName = next.get("warehouseName").asText();
			apOrderDetailList.add(new ApOrderDetail(apOrderId, salesOrderNo, sku, productName, qty, warehouseId, warehouseName));
		}
		return apOrderDetailList;
	}
	
	private List<ApOrderDetail> getCombinedSalesOrderProsDetails(Integer apOrderId, String hbNo){
		List<ApOrderDetail> apOrderDetailList = Lists.newArrayList();
		JsonNode salesOrderNode = null;
		try {
			salesOrderNode = httpService.getCombinedSalesOrder(hbNo);
		} catch (IOException e) {
			Logger.info("账期支付订单，获取合并发货单商品详情[{}]异常，{}", hbNo, e);
		}
		if (salesOrderNode==null || !salesOrderNode.get("result").asBoolean()) {
			Logger.info("异步备份账期支付的订单，获取合并发货单商品详情[{}]失败，停止备份",hbNo);
			return apOrderDetailList;
		}
		
		for (Iterator<JsonNode> it = salesOrderNode.get("data").iterator(); it.hasNext(); ) {
			JsonNode next = it.next();
			String sku = next.get("sku").asText();
			String productName = next.get("productName").asText();
			int qty = next.get("qty").asInt();
			int warehouseId = next.get("warehouseId").asInt();
			String warehouseName = next.get("warehouseName").asText();
			String salesOrderNo = JsonCaseUtil.jsonToString(next.get("salesOrderNo"));
			ApOrderDetail apOrderDetail = new ApOrderDetail(apOrderId, hbNo, sku, productName, qty, warehouseId, warehouseName);
			apOrderDetail.setSalesOrderNo(salesOrderNo);
			apOrderDetailList.add(apOrderDetail);
		}
		return apOrderDetailList;
	}
}
