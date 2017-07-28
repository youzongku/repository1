package services.purchase.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.io.Files;
import com.google.inject.Inject;

import dto.JsonResult;
import dto.purchase.CancelPurchaseOrderParam;
import dto.purchase.CloudLockPro;
import dto.purchase.ImportResultInfo;
import dto.purchase.InStorageDetail;
import dto.purchase.InStorageIterm;
import dto.purchase.InventoryCloudLockDto;
import dto.purchase.OrderDetail;
import dto.purchase.PurchaseOrderDto;
import dto.purchase.ReturnMess;
import dto.purchase.StatisIterm;
import dto.purchase.ViewPurchaseDetail;
import dto.purchase.ViewPurchaseIterm;
import dto.purchase.ViewPurchaseOrder;
import entity.purchase.OrderOperateRecord;
import entity.purchase.PurchaseActive;
import entity.purchase.PurchaseAudit;
import entity.purchase.PurchaseGiftRecord;
import entity.purchase.PurchaseOrder;
import entity.purchase.PurchaseOrderAuditLog;
import entity.purchase.PurchaseOrderDetail;
import entity.purchase.PurchaseOrderInput;
import entity.purchase.PurchaseOrderInputGift;
import entity.purchase.PurchaseOrderInputPro;
import entity.purchase.PurchaseStockout;
import forms.purchase.DeliverDutyPaidGoodsParam;
import forms.purchase.GetProductsParams;
import forms.purchase.InputOrderParam;
import mapper.purchase.PurchaseAuditMapper;
import mapper.purchase.PurchaseGiftRecordMapper;
import mapper.purchase.PurchaseOrderAuditLogMapper;
import mapper.purchase.PurchaseOrderDetailMapper;
import mapper.purchase.PurchaseOrderInputGiftMapper;
import mapper.purchase.PurchaseOrderInputMapper;
import mapper.purchase.PurchaseOrderInputProMapper;
import mapper.purchase.PurchaseOrderMapper;
import mapper.purchase.PurchaseStockoutMapper;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Http.MultipartFormData.FilePart;
import services.purchase.IHttpService;
import services.purchase.IPurchaseOrderDetailService;
import services.purchase.IPurchaseOrderService;
import services.purchase.ISequenceService;
import services.purchase.IUserService;
import utils.purchase.CsvUtils;
import utils.purchase.DateUtils;
import utils.purchase.ExcelUtils;
import utils.purchase.FileUtils;
import utils.purchase.HttpUtil;
import utils.purchase.JsonCaseUtil;
import utils.purchase.PageUtil;
import utils.purchase.ParametersUtil;
import utils.purchase.PriceFormatUtil;
import utils.purchase.PurchaseAuditStatus;
import utils.purchase.PurchaseOrderStatus;
import utils.purchase.PurchaseTypes;
import utils.purchase.StringUtils;

/**
 * Created by luwj on 2015/12/8.
 */
public class PurchaseOrderService implements IPurchaseOrderService {
	/**
	 * 采购单号前缀
	 */
	public static final String CAI_GOU_PREFIX = "CG";
	public static Double DEFAULT_MIN_PROFIT = 5.0;
	private static String filePath = "";

	@Inject
	private PurchaseOrderAuditLogMapper auditLogMapper;
	@Inject
	private EventBus ebus;
	@Inject
	private PurchaseOrderMapper purchaseOrderMapper;
	@Inject
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	@Inject
	private PurchaseOrderDetailMapper purchaseOrderDetailMapper;
	@Inject
	private ISequenceService iSequenceService;
	@Inject
	private PurchaseOrderInputMapper inputMapper;
	@Inject
	private PurchaseOrderInputProMapper inputProMapper;
	@Inject
	private PurchaseOrderInputGiftMapper inputGiftMapper;
	@Inject
	private IHttpService httpService;
	@Inject
	private PurchaseGiftRecordMapper giftRecordMapper;
	@Inject
	private PurchaseAuditMapper auditMapper;
	@Inject
	private PurchaseStockoutMapper purchaseStockoutMapper;
	@Inject
	private IUserService userService;

	@Override
	public Map<String, Object> balancePaymentBackStage(String purchaseOrderNo) {
		Logger.info("后台现金支付，采购单号：{}", purchaseOrderNo);
		HashMap<String, Object> result = Maps.newHashMap();

		// 查询采购单及详情
		PurchaseOrder purchaseOrderParam = new PurchaseOrder();
		purchaseOrderParam.setPurchaseOrderNo(purchaseOrderNo);
		PurchaseOrder order = purchaseOrderMapper.selectOrder(purchaseOrderParam);
		List<PurchaseOrderDetail> detailList = purchaseOrderDetailMapper.getAlldetailsByPurNo(purchaseOrderNo);

		// 检查采购单的状态
		if (order.getStatus() != PurchaseOrderStatus.WAITING_4_PAYMENT) {
			result.put("suc", false);
			result.put("msg", "采购单[" + order.getPurchaseOrderNo() + "]已被支付过！");
			Logger.info("采购单[" + order.getPurchaseOrderNo() + "]已被支付过！");
			return result;
		}

		Map<String, Object> lockMap = lock(purchaseOrderNo);
		if (!"true".equals(lockMap.get("suc").toString())) {
			result.put("suc", false);
			result.put("msg", lockMap.get("msg").toString());
			return result;
		}
		
		// 检查都通过，就进行余额支付
		HashMap<String, Object> balancePaymentParams = Maps.newHashMap();
		balancePaymentParams.put("transferNumber", order.getPurchaseOrderNo());
		balancePaymentParams.put("applyType", "balance");
		balancePaymentParams.put("isBackStage", true);
		// 采购单属于谁，就扣谁的钱
		balancePaymentParams.put("email", order.getEmail());
		// 支付金额（订单金额+运费-优惠）
		double actualAmount = order.calculatePurchaseFinalAmount();
		balancePaymentParams.put("transferAmount", actualAmount);
		balancePaymentParams.put("applyType", 3);// 采购支付，不含运费
		if (order.getBbcPostage() != null && order.getBbcPostage() > 0) {
			balancePaymentParams.put("applyType", 6);// 采购支付，含运费
		}
		Logger.info("余额支付参数：{}" + balancePaymentParams);
		// 余额支付
		String balancePaymentResult = httpService.balancePayment(balancePaymentParams);
		JsonNode balancePaymentResultNode = Json.parse(balancePaymentResult);
		Logger.info("现金支付结果：{}", balancePaymentResultNode);

		// 根据支付结果进行处理
		int resultCode = balancePaymentResultNode.get("code").asInt();
		// 支付成功
		if (resultCode == 4) {
			Logger.info("余额支付成功，即将更新采购单，采购单号：{}", order.getPurchaseOrderNo());
			// 更新订单状态
			CancelPurchaseOrderParam cancelPurchaseOrderParam = new CancelPurchaseOrderParam();
			cancelPurchaseOrderParam.setPurchaseNo(order.getPurchaseOrderNo());
			cancelPurchaseOrderParam.setFlag("PAY_SUCCESS");
			cancelPurchaseOrderParam.setActualAmount(actualAmount);
			// 计算发货单的支付金额
			cancelPurchaseOrderParam.setActualAmount4SO(getPurchaseTotal4SO(order.getBbcPostage(), detailList));
			cancelPurchaseOrderParam.setPayType("system");
			cancelPurchaseOrderParam.setPayDate(DateUtils.date2FullDateTimeString(new Date()));
			// 更新状态
			cancelPurchaseOrder(cancelPurchaseOrderParam);
			result.put("suc", true);
			result.put("msg", "支付成功");
			return result;
		} 
		
		// 支付失败
		if (resultCode == 5) {
			String msg = "当前帐户余额："
					+ balancePaymentResultNode.get("balance").asDouble() + " "
					+ balancePaymentResultNode.get("info").asText();
			Logger.info("支付失败，" + msg);
			result.put("suc", false);
			result.put("msg", msg);
			return result;
		}
		
		// 支付失败
		String msg = balancePaymentResultNode.get("info").asText();
		Logger.info("支付失败，" + msg);
		result.put("suc", false);
		result.put("msg", msg);
		return result;
	}

	@Override
	public Double getTotalArriveWarehousePrice(String purchaseOrderNo) {
		// 采购单
		PurchaseOrder param = new PurchaseOrder();
		param.setPurchaseOrderNo(purchaseOrderNo);
		PurchaseOrder po = purchaseOrderMapper.selectOrder(param);
		// 采购单详情
		PurchaseOrderDetail detailParam = new PurchaseOrderDetail();
		detailParam.setPurchaseId(po.getId());
		List<PurchaseOrderDetail> purchaseDetails = purchaseOrderDetailMapper.getDetails(detailParam);
		return doGetTotalArriveWarehousePrice(purchaseDetails);
	}

	/**
	 * 获取采购单的总到仓价
	 * @param purchaseDetails
	 * @return
	 */
	private Double doGetTotalArriveWarehousePrice(
			List<PurchaseOrderDetail> purchaseDetails) {
		if (purchaseDetails == null || purchaseDetails.size() == 0){
			return null;
		}

		// 拿到采购单详情sku、warehouseId和qty的对应关系
		Map<String, Integer> skuWarehouse2Qty = Maps.newHashMap();
		String key;
		// 重复的sku、warehouseId，数量累加
		for (PurchaseOrderDetail pod : purchaseDetails) {
			key = String.join("_", pod.getSku(),
					String.valueOf(pod.getWarehouseId()));
			if (skuWarehouse2Qty.get(key) == null) {
				skuWarehouse2Qty.put(key, pod.getQty());
			} else {
				// 数量累加
				skuWarehouse2Qty.put(key, (pod.getQty() + skuWarehouse2Qty.get(key)));
			}
		}
		Logger.info("查询总到仓价参数：" + skuWarehouse2Qty);

		try {
			JsonNode result = httpService.getTotalArriveWarehousePrice(skuWarehouse2Qty);
			if (result.get("suc").asBoolean()) {
				return result.get("result").asDouble();
			}
			return null;
		} catch (IOException e) {
			Logger.info("获取采购单的总到仓价失败");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 是否包含此日期
	 * @param map
	 * @param dateKey 日期key
	 * @return
	 */
	private boolean containsDate(Map<String, Object> map, String dateKey){
		return map.containsKey(dateKey) && map.get(dateKey) != null
				&& StringUtils.isNotBlankOrNull(map.get(dateKey).toString());
	}
	
	@Override
	public ViewPurchaseIterm viewPurchase(Map<String, Object> map) {
		ViewPurchaseIterm iterm = new ViewPurchaseIterm();
		// add by ye_ziran 2016-04-06
		iterm.setRecordPerPage(Integer.parseInt(map.get("pageSize").toString()));
		iterm.setPage(Integer.parseInt(map.get("pageCount").toString()));
		ReturnMess returnMess = new ReturnMess("0", "");
		try {
			if (containsDate(map, "sorderDate")) {
				String eorderDate = map.get("sorderDate").toString();
				Calendar ca = Calendar.getInstance();
				ca.setTime(DateUtils.string2date(eorderDate, DateUtils.FORMAT_DATE_PAGE));
				map.put("sorderDate", DateUtils.date2string(ca.getTime(), DateUtils.FORMAT_DATE_PAGE));
			}
			if (containsDate(map, "eorderDate")) {
				String eorderDate = map.get("eorderDate").toString();
				Calendar ca = Calendar.getInstance();
				ca.setTime(DateUtils.string2date(eorderDate, DateUtils.FORMAT_DATE_PAGE));
				ca.add(Calendar.DATE, 1);
				map.put("eorderDate", DateUtils.date2string(ca.getTime(), DateUtils.FORMAT_DATE_PAGE));
			}
			if (containsDate(map, "spaydate")) {
				String epaydate = map.get("spaydate").toString();
				Calendar ca = Calendar.getInstance();
				ca.setTime(DateUtils.string2date(epaydate, DateUtils.FORMAT_DATE_PAGE));
				map.put("spaydate", DateUtils.date2string(ca.getTime(), DateUtils.FORMAT_DATE_PAGE));
			}
			if (containsDate(map, "epaydate")) {
				String epaydate = map.get("epaydate").toString();
				Calendar ca = Calendar.getInstance();
				ca.setTime(DateUtils.string2date(epaydate, DateUtils.FORMAT_DATE_PAGE));
				ca.add(Calendar.DATE, 1);
				map.put("epaydate", DateUtils.date2string(ca.getTime(), DateUtils.FORMAT_DATE_PAGE));
			}

			// 查询记录数
			int totalCount = purchaseOrderMapper.getPurchaseCount(map);

			List<ViewPurchaseOrder> viewPurchaseOrderList = Lists.newArrayList();
			// 有记录时采取查询具体列表数据
			if (totalCount > 0) {
				iterm.setTotal(totalCount);
				int pageSize = Integer.parseInt(map.get("pageSize").toString());
				int pages = PageUtil.calculateTotalPage(totalCount, pageSize);
				iterm.setPages(pages);

				// 查询列表
				List<PurchaseOrder> orders = purchaseOrderMapper.getPurchaseList(map);

				// 批量查询商品详情
				List<Integer> purchaseIdList = Lists.transform(orders, order -> order.getId());
				List<PurchaseOrderDetail> allPurchaseDetails = purchaseOrderDetailMapper
						.getDetailsByPurchaseIds(purchaseIdList);
				// 采购单id对应详情
				Map<Integer, List<PurchaseOrderDetail>> purchaseId2DetailList = allPurchaseDetails.stream()
						.collect(Collectors.groupingBy(PurchaseOrderDetail::getPurchaseId));

				for (PurchaseOrder purch : orders) {
					ViewPurchaseOrder viewOrder = new ViewPurchaseOrder();
					BeanUtils.copyProperties(purch, viewOrder);
					// 订单折后价 为空则设置为订单总价
					viewOrder.setPurchaseDiscountAmount(purch.getPurchaseDiscountAmount() == null
							? purch.getPurchaseTotalAmount() : purch.getPurchaseDiscountAmount());
					viewOrder.setRemark(purch.getRemarks());
					// 详情转化
					List<ViewPurchaseDetail> viewDetails = Lists.newArrayList();
					List<PurchaseOrderDetail> details = purchaseId2DetailList.get(purch.getId());
					if (details != null) {// 异常订单详情可能为空
						for (PurchaseOrderDetail purchaseDetail : details) {
							ViewPurchaseDetail viewDetail = new ViewPurchaseDetail();
							BeanUtils.copyProperties(purchaseDetail, viewDetail);
							if (viewDetail.getRealPrice() == null) {
								viewDetail.setRealPrice(purchaseDetail.getPurchasePrice());
							}
							viewDetail.setIsClearance(purchaseDetail.getClearancePrice()!=null);
							viewDetails.add(viewDetail);
						}
						viewOrder.setDetails(viewDetails);
						Double proTotal = PriceFormatUtil.toFix2(
								new BigDecimal(viewDetails.stream().mapToDouble(e -> e.getTotalPrices()).sum()));
						viewOrder.setOrderProTotal(proTotal);// 商品总计
						viewOrder.setReducePrice(PriceFormatUtil.toFix2(
								new BigDecimal(proTotal).subtract(new BigDecimal(viewOrder.getPurchaseTotalAmount()))));
					}
					viewPurchaseOrderList.add(viewOrder);
				}
				setTransferOfflineRecords(viewPurchaseOrderList);
				setAuditLogs(viewPurchaseOrderList);
			}
			iterm.setOrders(viewPurchaseOrderList);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("EXCEPTION", e);
			returnMess = new ReturnMess("1", "异常情况");
		}
		iterm.setReturnMess(returnMess);
		return iterm;
	}

	/**
	 * 设置线下转账附属信息（转账金额、截图、转账人等等）
	 * 
	 * @param viewPurchaseOrderList
	 */
	private void setTransferOfflineRecords(
			List<ViewPurchaseOrder> viewPurchaseOrderList) {
		if (viewPurchaseOrderList == null || viewPurchaseOrderList.size() == 0) {
			return;
		}
		// 批量查询采购单-线下转账的记录
		List<String> purchaseOrderNoList_transferOffline = viewPurchaseOrderList.stream()
				.filter(vpo -> "cash-noline".equals(vpo.getPaymentId())).map(ViewPurchaseOrder::getPurchaseOrderNo)
				.collect(Collectors.toList());
		if (purchaseOrderNoList_transferOffline.size() > 0) {
			// 批量查询
			List<PurchaseAudit> auditList = auditMapper
					.selectByPurchaseOrderNoList(purchaseOrderNoList_transferOffline);
			// 设置
			if (auditList != null && auditList.size() > 0) {
				Logger.info("获取到{}个线下转账记录", auditList.size());
				Map<String, PurchaseAudit> auditListByPurchaseNo = auditList.stream()
						.collect(Collectors.toMap(PurchaseAudit::getPurchaseNo, Function.identity()));
				viewPurchaseOrderList.forEach(vpo -> {
					vpo.setPurchaseAudit(auditListByPurchaseNo.get(vpo.getPurchaseOrderNo()));
				});
			}
		}
	}

	/**
	 * 设置采购单审核记录（客服审核&财务审核）
	 * 
	 * @param viewPurchaseOrderList
	 */
	private void setAuditLogs(List<ViewPurchaseOrder> viewPurchaseOrderList) {
		if (viewPurchaseOrderList == null || viewPurchaseOrderList.size() == 0) {
			return;
		}
		// 批量查询审核记录
		List<String> purchaseOrderNoList = viewPurchaseOrderList.stream().map(ViewPurchaseOrder::getPurchaseOrderNo)
				.collect(Collectors.toList());
		// 客服审核的记录
		List<PurchaseOrderAuditLog> csAuditLogs = auditLogMapper.selectAllLogsByAuditType(purchaseOrderNoList,
				PurchaseOrderAuditLog.AUDIT_TYPE_CS);
		if (csAuditLogs != null && csAuditLogs.size() > 0) {
			Logger.info("查询到{}条客服审核记录", csAuditLogs.size());
			csAuditLogs.forEach(log -> {
				log.setAuditDateStr(DateUtils.date2FullDateTimeString(log.getAuditDate()));
			});
			Map<String, List<PurchaseOrderAuditLog>> csAuditLogsByPurchaseNo = csAuditLogs.stream()
					.collect(Collectors.groupingBy(PurchaseOrderAuditLog::getPurchaseNo));
			viewPurchaseOrderList.forEach(vpo -> {
				vpo.setCsAuditLogs(csAuditLogsByPurchaseNo.get(vpo.getPurchaseOrderNo()));
			});
		}

		// 财务审核的记录
		List<PurchaseOrderAuditLog> financeAuditLogs = auditLogMapper.selectAllLogsByAuditType(purchaseOrderNoList,
				PurchaseOrderAuditLog.AUDIT_TYPE_FINANCE);
		if (financeAuditLogs != null && financeAuditLogs.size() > 0) {
			Logger.info("查询到{}条财务审核记录", financeAuditLogs.size());
			financeAuditLogs.forEach(log -> {
				log.setAuditDateStr(DateUtils.date2FullDateTimeString(log.getAuditDate()));
			});
			Map<String, List<PurchaseOrderAuditLog>> financeAuditLogsByPurchaseNo = financeAuditLogs.stream()
					.collect(Collectors.groupingBy(PurchaseOrderAuditLog::getPurchaseNo));
			viewPurchaseOrderList.forEach(vpo -> {
				vpo.setFinanceAuditLogs(financeAuditLogsByPurchaseNo.get(vpo.getPurchaseOrderNo()));
			});
		}
	}

	/**
	 * 处理缺货采购单
	 * 
	 * @param purchaseOrder
	 *            缺货采购单
	 * @param actualAmount
	 *            金额
	 * @param payType
	 *            支付类型
	 * @param payDate
	 * @param tradeNo
	 * @throws Exception
	 */
	private void paySuccess4StockoutPurchaseOrder(PurchaseOrder purchaseOrder,
			Double actualAmount, String payType4SO, String payDate4SO,
			String tradeNo4SO, Double actualAmount4SO) throws Exception {
		// 分销商类型(1：普通分销商，2：合营分销商，3：内部分销商)
		int comsumerType = purchaseOrder.getDistributorType();
		// 获取销售发货单
		Integer saleOrderId = purchaseOrder.getSid();
		// 统一要客服审核
		// change by zbc 内部分销商自动过 其他分销商 校验 订单总价是否低于到仓总价
		if (!isNeedPay(actualAmount)||comsumerType == 3 || !checkNeedAuditOrNot(purchaseOrder)) {
			// 订单状态为完成 电商内部分销商直接完成
			purchaseOrder.setStatus(PurchaseOrderStatus.FINISHED);
			purchaseOrder.setFinanceMoney(isNeedPay(actualAmount)?actualAmount:null);
			Logger.info("缺货采购单{}对应的销售单id={}的状态为待发货", purchaseOrder.getPurchaseOrderNo(), saleOrderId);
			// 为已完成，则要为待发货（出库）
			// 获取采购单详情
			// 先入库后出库
			stockInThenOut(purchaseOrder);
		} else {
			// 享受了优惠/参加了促销/亏本，要进行审核
			purchaseOrder.setStatus(PurchaseOrderStatus.AUDIT_WAINTING_CUSTOMER_SERVICE);
			Logger.info("处理缺货采购单{}", purchaseOrder.getPurchaseOrderNo());
			Logger.info("缺货采购单{}对应的销售单id={}的状态为待审核", purchaseOrder.getPurchaseOrderNo(), saleOrderId);
		}
		// ===============根据缺货采购单状态来判断销售单的状态=========================
		// add by zbc 完成发货单 新接口
		httpService.finishSaleOrder(saleOrderId, 
				actualAmount4SO, 
				purchaseOrder.getStatus() == PurchaseOrderStatus.FINISHED,payDate4SO,tradeNo4SO,payType4SO );
	}

	/**
	 * 判断是否需要支付
	 * @author zbc
	 * @since 2017年6月30日 下午5:08:46
	 * @param actualAmount
	 * @return
	 */
	private boolean isNeedPay(Double actualAmount) {
		return actualAmount!= null&&actualAmount <= 0;
	}

	/**
	 * 支付成功，要更新订单的状态
	 * 
	 * @param purchaseOrder
	 *            采购单
	 * @param actualAmount
	 *            采购单金额
	 * @throws Exception
	 */
	// 分销商类型(1：普通分销商，2：合营分销商，3：内部分销商)
	private void paySuccess4NormalPurchaseOrder(PurchaseOrder purchaseOrder,
			Double actualAmount) throws Exception {
		int comsumerType = purchaseOrder.getDistributorType();
		// change by zbc 内部分销商自动过 其他分销商 校验 订单总价是否低于到仓总价 立即发货自动过
		if (comsumerType == 3 ||purchaseOrder.getImmediateDelivery() || !checkNeedAuditOrNot(purchaseOrder)) {
			try {
				// change by zbc 新更新库存
				httpService.updateStock(purchaseOrder.getPurchaseOrderNo());
			} catch (Exception e) {
				Logger.info("更新微仓库存失败！");
				e.printStackTrace();
			}
			// 订单状态为完成
			purchaseOrder.setStatus(PurchaseOrderStatus.FINISHED);
			purchaseOrder.setFinanceMoney(actualAmount);

			// 判断是否要整批出库
			PurchaseStockout stockout = purchaseStockoutMapper.selectStockout(purchaseOrder.getPurchaseOrderNo(), 0);
			if (stockout != null) {
				stockout(stockout);
			}
		} else {
			// 享受了优惠/参加了促销/亏本，要进行审核
			purchaseOrder.setStatus(PurchaseOrderStatus.AUDIT_WAINTING_CUSTOMER_SERVICE);
		}
	}

	/**
	 * 整批出库
	 * 
	 * @param stockout
	 */
	@Override
	public void stockout(PurchaseStockout stockout) {
		Logger.info("==========采购单完成，去生成整批出库的发货单=stockout========" + stockout);
		// 生成发货单
		String createSaleOrderResult = httpService.createSaleOrder(stockout.getJsonStr());
		JsonNode createSaleOrderResultNode = Json.parse(createSaleOrderResult);
		Logger.info("==========创建发货单的返回值=========" + createSaleOrderResultNode);
		if (createSaleOrderResultNode != null && createSaleOrderResultNode.get("code").asInt() == 108) {
			// 更新
			purchaseStockoutMapper.updateStatusById(stockout.getId(), 1);
		} else {
			purchaseStockoutMapper.updateStatusById(stockout.getId(), 2);
		}
	}

	@Override
	public void stockInThenOut(PurchaseOrder purchaseOrder) throws Exception {
		// change by zbc 新更新库存
		try {
			JsonNode stockOut = httpService.updateStock(purchaseOrder.getPurchaseOrderNo());
			if (stockOut.get("result").asBoolean()) {
				httpService.saveMicroOut(purchaseOrder.getSid(), stockOut.get("microOutList"));
			}
		} catch (Exception e) {
			Logger.info("缺货采购单更新库异常", e);
		}
	}

	@Override
	public ReturnMess cancelPurchaseOrder(CancelPurchaseOrderParam param) {
		String orderNo = param.getPurchaseNo();
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setPurchaseOrderNo(orderNo);
		purchaseOrder = purchaseOrderMapper.selectOrder(purchaseOrder);
		
		if(purchaseOrder==null){
			return new ReturnMess("1", "该订单不存在");
		}
		
		try {
			if ("CANCEL".equals(param.getFlag())) {// 取消
				purchaseOrder.setStatus(PurchaseOrderStatus.CANCEL);
				purchaseOrder.setCancelDate(new Date());// 取消时间
				// add by zbc 释放锁库
				httpService.unLock(orderNo);
			} else if ("PAY_SUCCESS".equals(param.getFlag())) {// 支付成功
				Logger.info("支付成功，更新订单的参数：{}", param);
				// 设置采购单的支付方式
				if (StringUtils.isBlankOrNull(purchaseOrder.getPaymentId())
						&& StringUtils.isNotBlankOrNull(param.getPayType())) {
					purchaseOrder.setPaymentId(param.getPayType());
				}
				// 支付时间
				String payDate = param.getPayDate();
				if (StringUtils.isBlankOrNull(payDate)) {
					Date now = new Date();
					purchaseOrder.setPayDate(now);
					payDate = DateUtils.date2FullDateTimeString(now);
				} else {
					purchaseOrder.setPayDate(DateUtils.string2date(payDate, DateUtils.FORMAT_FULL_DATETIME));
				}
				// 支付流水号
				purchaseOrder.setTransactionId(param.getTradeNo());
				purchaseOrder.setOfflineMoney(param.getActualAmount());
				// add by zbc 永久锁住库存
				httpService.permanentLock(orderNo);
				// 这里区分是正常采购单还是缺货采购单
				if (purchaseOrder.getPurchaseType() != PurchaseTypes.PURCHASE_ORDER_STOCKOUT) {
					// add by zbc 标记活动
					markPro(purchaseOrder);
					// 正常的采购单
					paySuccess4NormalPurchaseOrder(purchaseOrder, param.getActualAmount());
				} else {
					// 缺货采购单
					paySuccess4StockoutPurchaseOrder(purchaseOrder, param.getActualAmount(), param.getPayType(),
							payDate, param.getTradeNo(), param.getActualAmount4SO());
				}
				// 支付成功结束
			} else if ("INVALID".equals(param.getFlag())) {// 失效
				// add by zbc 释放锁库
				httpService.unLock(orderNo);
				purchaseOrder.setStatus(PurchaseOrderStatus.INVALIDATED);
				// add by zbc 缺货采购单失效 还原库存
				if (purchaseOrder.getPurchaseType() == PurchaseTypes.PURCHASE_ORDER_STOCKOUT) {
					invalid(purchaseOrder);
				}
			}
			// 更新订单
			int result = purchaseOrderMapper.updateByPrimaryKeySelective(purchaseOrder);

			if (result <= 0) {
				return new ReturnMess("1", "订单更新操作失败");
			}
			
			// add by zbc 更新优惠码状态
			if (purchaseOrder.getPurchaseType() == PurchaseTypes.PURCHASE_ORDER_NORMAL
					&& purchaseOrder.getCouponsCode() != null) {
				httpService.updateCoupons(null, purchaseOrder.getCouponsCode(), purchaseOrder.getStatus(), null, null,
						null, null, null);
			}
			// end
		} catch (Exception e) {
			Logger.error("Exception:", e);
			return new ReturnMess("1", "Exception!异常");
		}
		return new ReturnMess("0", "");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ReturnMess orderPurchase(JsonNode arr, String email) {
		if (email == null) {
			email = arr.get("email").asText();
		}
		Logger.debug(">>>>>>arr>>>" + arr.toString());
		// 获取用户信息
		JsonNode memberNode = null;
		try {
			memberNode = httpService.getDismemberByEmail(email);
		} catch (Exception e) {
			Logger.info("获取用户信息异常 ", e);
			return new ReturnMess("1", "未查询到用户信息");
		}
		if (memberNode == null || memberNode.get("result") == null) {
			return new ReturnMess("1", "未查询到用户信息");
		}
		JsonNode memberResultNode = memberNode.get("result");
		
		ReturnMess rm = new ReturnMess("1", "批量增加失败");
		// 采购单
		PurchaseOrder order = new PurchaseOrder();
		order.setEmail(email);
		order.setPurchaseOrderNo(iSequenceService.getPurchaseNo(CAI_GOU_PREFIX));// 生成采购单号
		order.setPurchaseDate(new Date());
		Calendar validableDate = Calendar.getInstance();
		validableDate.add(Calendar.DATE, 2);// 有效果支付时间为下订单后2天
		order.setValidablePayDate(validableDate.getTime());// 有效果支付时间点
		order.setNickName(JsonCaseUtil.jsonToString(memberResultNode.get("nickName")));
	    order.setCustomerService(JsonCaseUtil.jsonToString(memberResultNode.get("salesmanErp")));
	    order.setDistributorType(JsonCaseUtil.jsonToInteger(memberResultNode.get("comsumerType")));
	    //add by zbc 保存用户归属 2017-06-21
	    order.setAttributionType(JsonCaseUtil.jsonToInteger(memberResultNode.get("attributionType")));
	    if (arr.has("bbcPostage")) {// 整批出库会有运费
			order.setBbcPostage(JsonCaseUtil.jsonToDouble(arr.get("bbcPostage")));
		}
		order.setPayerIp(StringUtils.getString(arr.path("ip"), true));// 下单人IP
		order.setPayHost("");// 下单域名
		order.setPurchaseTotalAmount(JsonCaseUtil.jsonToDouble(arr.get("totalPrice")));// 订单总金额
		order.setRemarks(arr.has("remarks") ? arr.get("remarks").asText() : null);// 客户备注
		order.setPurchaseType(PurchaseTypes.PURCHASE_ORDER_NORMAL);// 采购类型（1：常规采购，2：缺货采购）
		order.setDeductionAmount(JsonCaseUtil.jsonToDouble(arr.get("deductionAmount")));
		order.setIsPro(arr.get("isPro") != null ? arr.get("isPro").asBoolean() : Boolean.FALSE);
		order.setIsChoose(arr.get("isChoose") != null ? arr.get("isChoose").asBoolean() : Boolean.FALSE);
		order.setInputUser(arr.get("inputUser") != null ? arr.get("inputUser").asText() : arr.get("email").asText());
		order.setBackIn(JsonCaseUtil.jsonToBoolean(arr.get("backIn")));
		order.setOaAuditNo(JsonCaseUtil.jsonToString(arr.get("oaAuditNo")));
		order.setBusenessRemarks(JsonCaseUtil.jsonToString(arr.get("businessRemarks")));
		// 兼容后台完税仓商品出库生成的缺货 by huangjc 2016.12.14
		if (arr.has("purchaseType")) {
		    order.setPurchaseType(JsonCaseUtil.jsonToInteger(arr.get("purchaseType")));			    
		}
		// 都云涛添加，2016-2-22
		if (arr.get("sid") != null) {
			order.setPurchaseType(PurchaseTypes.PURCHASE_ORDER_STOCKOUT);
			order.setSid(arr.get("sid").asInt());
			// 内部分销商的缺货采购单无需支付
			if (order.getDistributorType() != null && order.getDistributorType() == 3) {
				order.setStatus(1);// 已付款
				order.setRemarks("内部分销商缺货采购订单，此单没有经过付款，直接操作微仓");
			}
			// add by zbc 缺货采购单 如果包含运费和物流方式 则支付时 运费需要 展示 算入总计金额
			order.setBbcPostage(JsonCaseUtil.jsonToDouble(arr.get("bbcPostage")));
			order.setLogisticsMode(JsonCaseUtil.jsonToString(arr.get("logisticsMode")));
		}
		
		// add by zbc判断是否包含优惠码
		order.setCouponsAmount(JsonCaseUtil.jsonToDouble(arr.get("couponsAmount")));
		order.setCouponsCode(JsonCaseUtil.jsonToString(arr.get("couponsCode")));
		order.setImmediateDelivery(JsonCaseUtil.jsonToBoolean(arr.get("immediateDelivery")));
		// add by xuse 销售订单缺货采购时支付金额---页面是填入的实际金额 为空则取订单总金额
		order.setSalesAmount(JsonCaseUtil.jsonToDouble(arr.get("salesAmount")));
		try {
			// 如果包含优惠码验证优惠码的的有效性
			if (order.getCouponsCode() != null && order.getPurchaseType() == PurchaseTypes.PURCHASE_ORDER_NORMAL) {
				Map<String, String> coupons = Maps.newHashMap();
				coupons.put("couponsNo", order.getCouponsCode());
				coupons.put("orderAmount", order.getPurchaseTotalAmount().toString());
				String couponsInfoStr = HttpUtil.get(coupons, HttpUtil.B2BBASEURL + "/member/getCouponsInfo");
				ObjectMapper map = new ObjectMapper();
				JsonNode couponsInfo = map.readTree(couponsInfoStr);
				Logger.info("优惠码返回数据:[{}]", couponsInfoStr);
				if ("false".equals(couponsInfo.get("suc").asText())) {
					return new ReturnMess("1", couponsInfo.get("info").asText());
				}
			}
			// 插入采购单
			int result = purchaseOrderMapper.insertSelective(order);
			rm.setOrder(order);
			Logger.debug(">>>order Purchase success?>>>{}", result);
			Logger.info("oderId：{}", order.getId());
			// 采购单明细
			List purchaseOrderDetails = Json.fromJson(arr.path("orderDetail"), List.class);
			List<PurchaseGiftRecord> giftRecords = new ArrayList<PurchaseGiftRecord>();
			PurchaseGiftRecord giftRecord = null;
			for (Object detail : purchaseOrderDetails) {
				Map detailMap = (Map) detail;
				if (detailMap.get("realPrice") == null) {
					detailMap.put("realPrice", detailMap.get("price"));
				}
				detailMap.put("purchaseId", order.getId());
				detailMap.put("purchaseNo", iSequenceService.getBatchNo());
				detailMap.put("inputDate", new Date());
				if (detailMap.get("isBack") != null) {
					detailMap.put("isBack", detailMap.get("isBack"));
				}
				// 记录赠品日志
				if (detailMap.get("isgift") != null && (boolean) detailMap.get("isgift")) {
					giftRecord = new PurchaseGiftRecord();
					giftRecord.setPurchaseId(order.getId());
					giftRecord.setOperateTime(new Date());
					giftRecord.setOperatorEmail(order.getInputUser());
					giftRecord.setQty((Integer) detailMap.get("qty"));
					giftRecord.setSku((String) detailMap.get("sku"));
					giftRecord.setWarehouseId((Integer) detailMap.get("warehouseId"));
					giftRecord.setWarehouseName((String) detailMap.get("warehouseName"));
					giftRecord.setUserEmail(order.getEmail());
					giftRecords.add(giftRecord);
				}
			}
			
			// 在这里，把商品详情转换为实体，不要使用Map
			// 插入采购单详情
			boolean addFlag = purchaseOrderDetailService.batchSaveDetails(purchaseOrderDetails);

			// 后台下采购单，现金支付，且是正常的采购单
			if (arr.has("payType")
					&& "cash".equals(arr.get("payType").asText())
					&& order.getPurchaseType() == PurchaseTypes.PURCHASE_ORDER_NORMAL) {
				order.setPaymentId("cash");
				order.setPayDate(new Date());
				order.setOfflineMoney(arr.get("money").asDouble());
				Logger.info("现金支付，金额为：{}", arr.get("money").asDouble());
				order.setStatus(PurchaseOrderStatus.AUDIT_WAINTING_CUSTOMER_SERVICE);
			} else {
				order.setStatus(PurchaseOrderStatus.WAITING_4_PAYMENT);// 待付款
			}
			
			// 计算总到仓价
			Double tAWPrice = getTotalArriveWarehousePrice(order.getPurchaseOrderNo());
			order.settAWPrice(tAWPrice);

			// 更新订单
			Logger.info("更新采购单：{}", order);
			purchaseOrderMapper.updateByPrimaryKeySelective(order);

			// 更新日志
			giftRecordMapper.batchInsert(giftRecords);

			// 订单总金额
			Double totalAmount = order.getPurchaseTotalAmount();
			// 分销平台运费
			Double bbcfee = order.getBbcPostage() != null ? order.getBbcPostage() : 0;
			// 优惠码面额
			Double couponsAmount = order.getCouponsAmount() != null ? order.getCouponsAmount() : 0;
			
			// 实际支付
			Double actuallyPaid = new Double(0);
			// 常规采购类型 并且包含优惠码 则 更新优惠码信息
			if (order.getCouponsCode() != null) {
				totalAmount += bbcfee;
				if (totalAmount > couponsAmount) {
					actuallyPaid = totalAmount - couponsAmount;
				}
				if (order.getPurchaseType() == PurchaseTypes.PURCHASE_ORDER_NORMAL) {
					httpService.updateCoupons(order.getEmail(),
							order.getCouponsCode(), order.getStatus(), 1,
							actuallyPaid, totalAmount, new Date(),
							order.getPurchaseOrderNo());
				} else {
					httpService.updateCoupons(null, order.getCouponsCode(),
							null, 1, actuallyPaid, totalAmount, null, null);
				}
			}
			
			// 插入采购单详情成功
			if (addFlag) {
				rm = new ReturnMess("0", order.getPurchaseOrderNo());
				// add by xuse 添加订单关联活动信息
				try {
					JsonNode act = arr.get("activities");
					if (act != null) {
						PurchaseActive pa = new PurchaseActive();
						pa.setOrderno(order.getPurchaseOrderNo());
						pa.setActiveid(act.get("activeid").asInt());
						pa.setPriviledgeid(act.get("priviledgeid").asText());
						purchaseOrderMapper.insertActivities(pa);
					}
				} catch (Exception e) {
					Logger.error("保存活动信息错误[{}]", order.getPurchaseOrderNo());
				}
				PurchaseOrderDto dto = new PurchaseOrderDto();
				BeanUtils.copyProperties(order, dto);
				// change by zbc 同步计算均摊价
				caculateCapFee(dto);
				// 锁定库存
				lock(order.getPurchaseOrderNo());
			}
			if (totalAmount > couponsAmount) {
				actuallyPaid = totalAmount - couponsAmount;
			} else {
				// add by zbc 普通采购，订单金额小于等于0自动更新库存
				if (order.getPurchaseType() == PurchaseTypes.PURCHASE_ORDER_NORMAL) {
					cancelPurchaseOrder(new CancelPurchaseOrderParam(
							order.getPurchaseOrderNo(), "PAY_SUCCESS"));
				}
				return new ReturnMess("3", order.getPurchaseOrderNo());
			}
		} catch (Exception e) {
			Logger.error("Exception:", e);
			return new ReturnMess("1", "Exception!异常");
		}
		return rm;
	}

	@Override
	public boolean updatePurchaseOrder(PurchaseOrder purchaseOrder) {
		// add by xuse 现金交易支付类型
		if (purchaseOrder.getStatus() != null && purchaseOrder.getStatus() == 4) {
			purchaseOrder.setPaymentId("cash");
		}
		int updateFlag = purchaseOrderMapper.updateByPrimaryKeySelective(purchaseOrder);
		return updateFlag > 0;
	}

	@Override
	public InStorageIterm getInfoByNo(String purchaseOrderNo, String flag) {
		InStorageIterm iterm = new InStorageIterm();
		
		// 获取采购单
		PurchaseOrder queryPOParam = new PurchaseOrder();
		queryPOParam.setPurchaseOrderNo(purchaseOrderNo);
		PurchaseOrder purchaseOrder = purchaseOrderMapper.selectOrder(queryPOParam);
		
		// 采购单是否存在
		if (null == purchaseOrder) {
			iterm.setReturnMess(null);
			return iterm;
		}
		
		// 获取采购单详情
		PurchaseOrderDetail detail = new PurchaseOrderDetail();
		detail.setPurchaseId(purchaseOrder.getId());
		List<PurchaseOrderDetail> purchaseDetails = purchaseOrderDetailMapper.getDetails(detail);
		
		List<InStorageDetail> pros = Lists.newArrayList();
		if (purchaseDetails.size() > 0) {
			iterm.setCouponsAmount(purchaseOrder.getCouponsAmount());
			iterm.setCouponsCode(purchaseOrder.getCouponsCode());
			iterm.setEmail(purchaseOrder.getEmail());
			Logger.debug(">>>>>>>>{}", purchaseDetails.get(0).getPurchaseNo());
			iterm.setPurchaseNo(purchaseOrder.getPurchaseOrderNo());
			iterm.setPurchaseOrderId(String.valueOf(purchaseOrder.getId()));
			iterm.setPurchaseType(purchaseOrder.getPurchaseType());
			iterm.setSid(purchaseOrder.getSid());
			if ("SUCCESS".equals(flag)) {
				iterm.setStatus("2");// 2：支付完成，扣除库存；
			} else if ("FROZEN".equals(flag)) {
				iterm.setStatus("1");// 1：生成采购单，冻结库存
			} else {
				iterm.setStatus("3");// 3：解冻库存，库存还原，支付超时
			}
		}
		
		for (PurchaseOrderDetail pDetail : purchaseDetails) {
			InStorageDetail inDetail = new InStorageDetail();
			inDetail.setProductName(pDetail.getProductName());
			inDetail.setPurchasePrice(String.valueOf(pDetail.getPurchasePrice()));
			inDetail.setQty(String.valueOf(pDetail.getQty()));
			inDetail.setSku(pDetail.getSku());
			inDetail.setWarehouseId(String.valueOf(pDetail.getWarehouseId()));
			inDetail.setDisProfitRate(pDetail.getDisProfitMargin());
			inDetail.setDisProfit(pDetail.getDisProfit());
			inDetail.setDisVat(pDetail.getDistotalvat());
			inDetail.setDisStockFee(pDetail.getDisStockFee());
			inDetail.setDisShippingType(pDetail.getDisShippingType());
			inDetail.setDisOtherCost(pDetail.getDisOtherCost());
			inDetail.setDisTotalCost(pDetail.getPurchaseCostPrice());
			inDetail.setDisTransferFee(pDetail.getDisTransferFee());
			inDetail.setDisListFee(pDetail.getDislistFee());
			inDetail.setDisTradeFee(pDetail.getDistradeFee());
			inDetail.setDisPayFee(pDetail.getDispayFee());
			inDetail.setDisPostalFee(pDetail.getDispostalFee());
			inDetail.setDisImportTar(pDetail.getDisimportTar());
			inDetail.setDisGst(pDetail.getDisgst());
			inDetail.setDisInsurance(pDetail.getDisinsurance());
			inDetail.setDisTotalVat(pDetail.getDistotalvat());
			inDetail.setCost(pDetail.getCost());
			inDetail.setDisFreight(pDetail.getDisFreight());
			inDetail.setDisPrice(pDetail.getDisPrice());
			inDetail.setDisCifPrice(pDetail.getDisCifPrice());
			inDetail.setWarehouseName(pDetail.getWarehouseName());
			inDetail.setIsgift(pDetail.getIsgift());
			inDetail.setCapFee(pDetail.getCapFee());
			pros.add(inDetail);
		}
		iterm.setPros(pros);
		iterm.setReturnMess(new ReturnMess("0", ""));
		Logger.debug(">>getInfoByNo>iterm>>{}", Json.toJson(iterm).toString());
		return iterm;
	}

	@Override
	public ReturnMess isValiPayDate(String purchaseOrderNo) {
		ReturnMess returnMess = new ReturnMess("0", "");
		try {
			PurchaseOrder purchaseOrder = new PurchaseOrder();
			purchaseOrder.setPurchaseOrderNo(purchaseOrderNo);
			purchaseOrder = purchaseOrderMapper.selectOrder(purchaseOrder);
			Date valiDate = purchaseOrder.getValidablePayDate();
			if (new Date().getTime() > valiDate.getTime()) {// 支付超时
				returnMess = new ReturnMess("3", "该订单支付超时,订单有效支付时间为2天!");
			}
		} catch (Exception e) {
			returnMess = new ReturnMess("1", "Exception!异常");
			Logger.error("Exception!", e);
		}
		return returnMess;
	}

	@Override
	public StatisIterm statisPurchaseOrder(JsonNode node) {
		ReturnMess returnMess = new ReturnMess("0", "");
		StatisIterm iterm = new StatisIterm(0);
		try {
			Map<String, String> map = Maps.newHashMap();
			map.put("email", node.get("email").asText());
			int amount = purchaseOrderMapper.getAmountByDate(map);
			iterm.setAmount(amount);
		} catch (Exception e) {
			returnMess = new ReturnMess("1", "Exception!异常");
			Logger.error("Exception!", e);
		}
		iterm.setReturnMess(returnMess);
		return iterm;
	}

	@Override
	public ViewPurchaseIterm getOrderById(JsonNode node) {
		ViewPurchaseIterm iterm = new ViewPurchaseIterm();
		ReturnMess returnMess = new ReturnMess("0", "");
		try {
			PurchaseOrder purchaseOrder = new PurchaseOrder();
			if (node.has("id")) {
				purchaseOrder.setId(node.get("id").asInt());
			} else if (node.has("pNo")) {
				purchaseOrder.setPurchaseOrderNo(node.get("pNo").asText());
			}
			purchaseOrder = purchaseOrderMapper.selectOrder(purchaseOrder);
			if(purchaseOrder != null){
				List<ViewPurchaseOrder> purchases = Lists.newArrayList();
				ViewPurchaseOrder viewOrder = new ViewPurchaseOrder();
				BeanUtils.copyProperties(purchaseOrder, viewOrder);
				Double total = 0.00;
				if (purchaseOrder != null && purchaseOrder.getOfflineMoney() != null) {
					total = purchaseOrder.getOfflineMoney();
				}
				viewOrder.setTotalOfflineMoney(PriceFormatUtil.toFix2(total));
				viewOrder.setSorderDate(DateUtils.date2FullDateTimeString(viewOrder.getPurchaseDate()));
				if (viewOrder.getPayDate() != null) {
					viewOrder.setSpayDate(DateUtils.date2FullDateTimeString(viewOrder.getPayDate()));
				}
				PurchaseOrderDetail detail = new PurchaseOrderDetail();
				detail.setPurchaseId(purchaseOrder.getId());
				viewOrder.setReducePrice(purchaseOrder.getReducePrice());
				// modify by xuse 分销商前台展示订单总金额---折后分销价不为空则显示折后分销价，否则显示总金额
				viewOrder.setPurchaseTotalAmount(purchaseOrder
						.getPurchaseDiscountAmount() != null ? purchaseOrder
						.getPurchaseDiscountAmount() : purchaseOrder
						.getPurchaseTotalAmount());
				List<PurchaseOrderDetail> purchaseDetails = purchaseOrderDetailMapper
						.getDetails(detail);
				List<ViewPurchaseDetail> details = Lists.newArrayList();
				for (PurchaseOrderDetail PurchaseDetail : purchaseDetails) {
					ViewPurchaseDetail viewDetail = new ViewPurchaseDetail();
					BeanUtils.copyProperties(PurchaseDetail, viewDetail);
					viewDetail.setIsClearance(PurchaseDetail.getClearancePrice()!=null);
					details.add(viewDetail);
				}
				Double proTotal = PriceFormatUtil.toFix2(new BigDecimal(details
						.stream().mapToDouble(e -> e.getTotalPrices()).sum()));
				viewOrder.setOrderProTotal(proTotal);
				if (viewOrder.getPurchaseType() == PurchaseTypes.PURCHASE_ORDER_NORMAL) {
					viewOrder.setReducePrice(PriceFormatUtil.toFix2(new BigDecimal(
							proTotal).subtract(new BigDecimal(viewOrder
							.getPurchaseTotalAmount()))));
				}
				viewOrder.setDetails(details);
				purchases.add(viewOrder);
				iterm.setOrders(purchases);
			}else{
				returnMess = new ReturnMess("1", "订单不存在");
			}
		} catch (Exception e) {
			Logger.error("Exception:", e);
			returnMess = new ReturnMess("1", "Exception!异常");
		}
		iterm.setReturnMess(returnMess);
		return iterm;
	}

	@Override
	public List<ViewPurchaseOrder> getExportList(Map<String, Object> params) {
		List<PurchaseOrder> orders = purchaseOrderMapper
				.getPurchaseList(params);
		List<ViewPurchaseOrder> purchases = Lists.newArrayList();
		for (PurchaseOrder purch : orders) {
			ViewPurchaseOrder viewOrder = new ViewPurchaseOrder();
			BeanUtils.copyProperties(purch, viewOrder);
			viewOrder.setSorderDate(DateUtils.date2FullDateTimeString(viewOrder
					.getPurchaseDate()));
			if (viewOrder.getPayDate() != null)
				viewOrder.setSpayDate(DateUtils
						.date2FullDateTimeString(viewOrder.getPayDate()));

			purchases.add(viewOrder);
		}
		return purchases;
	}

	@Override
	public List<PurchaseActive> getActive(String orderno) {
		List<PurchaseActive> list = purchaseOrderMapper.getActive(orderno);
		if (list == null) {
			return Lists.newArrayList();
		}
		return list;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public InStorageIterm addGift(JsonNode node) {
		ReturnMess returnMess = new ReturnMess("0", "");
		InStorageIterm iterm = new InStorageIterm();
		
		// 查询采购单
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setPurchaseOrderNo(node.get("orderNo").asText());
		purchaseOrder = purchaseOrderMapper.selectOrder(purchaseOrder);
		if (null == purchaseOrder) {
			iterm.setReturnMess(null);
			return iterm;
		}
		
		iterm.setEmail(purchaseOrder.getEmail());
		String pno = purchaseOrder.getPurchaseOrderNo();
		iterm.setPurchaseNo(pno);
		
		try {
			// 采购单明细
			JsonNode purchaseOrderDetail = node.path("pros");
			// 尝试锁库
			Map<String, String> giftLock = checkGiftStock(purchaseOrder,
					purchaseOrderDetail);
			if (!"true".equals(giftLock.get("suc"))) {
				iterm.setReturnMess(new ReturnMess("1", giftLock.get("msg")));
				Logger.debug(">>getInfoByNo>iterm>>{}", Json.toJson(iterm).toString());
				return iterm;
			}
			
			List purchaseOrderDetails = Json.fromJson(purchaseOrderDetail,
					List.class);
			List<PurchaseGiftRecord> giftRecords = new ArrayList<PurchaseGiftRecord>();
			PurchaseGiftRecord giftRecord = null;
			for (Object d : purchaseOrderDetails) {
				Map detailMap = (Map) d;
				detailMap.put("purchaseId", purchaseOrder.getId());
				detailMap.put("purchaseNo", iSequenceService.getBatchNo());
				detailMap.put("inputDate", new Date());
				// 记录赠品日志
				giftRecord = new PurchaseGiftRecord();
				giftRecord.setPurchaseId(purchaseOrder.getId());
				giftRecord.setPurchaseNo(purchaseOrder.getPurchaseOrderNo());
				giftRecord.setOperateTime(new Date());
				giftRecord.setOperatorEmail((String) detailMap.get("operatorEmail"));
				giftRecord.setQty((Integer) detailMap.get("qty"));
				giftRecord.setSku((String) detailMap.get("sku"));
				giftRecord.setWarehouseId((Integer) detailMap.get("warehouseId"));
				giftRecord.setWarehouseName((String) detailMap.get("warehouseName"));
				giftRecord.setUserEmail(purchaseOrder.getEmail());
				giftRecords.add(giftRecord);
			}
			boolean flag = purchaseOrderDetailService.batchSaveDetails(purchaseOrderDetails);
			Logger.info("选增{}", (flag ? "成功" : "失败"));
			if (flag) {
				// 更新
				// 计算总到仓价
				Double tAWPrice = getTotalArriveWarehousePrice(purchaseOrder.getPurchaseOrderNo());
				purchaseOrder.settAWPrice(tAWPrice);
				// 更新订单已选赠品
				purchaseOrder.setIsChoose(true);
				Logger.info(
						"[{}]选赠品更新到仓价:[{}],结果[{}]",
						purchaseOrder.getPurchaseOrderNo(),
						tAWPrice,
						purchaseOrderMapper.updateByPrimaryKeySelective(purchaseOrder) > 0);
				// add by zbc 计算均摊价格
				PurchaseOrderDto dto = new PurchaseOrderDto();
				BeanUtils.copyProperties(purchaseOrder, dto);
				ebus.post(dto);
			}
			// 1、更新是否已选
			purchaseOrderMapper.updateSelect(purchaseOrder.getId());
			// 2.更新日志
			giftRecordMapper.batchInsert(giftRecords);
			// 永久锁定
			httpService.permanentLock(pno);
		} catch (Exception e) {
			returnMess = new ReturnMess("1", "Exception!异常");
			Logger.error("Exception!", e);
		}
		iterm.setReturnMess(returnMess);
		Logger.debug(">>getInfoByNo>iterm>>{}", Json.toJson(iterm).toString());
		return iterm;
	}

	/**
	 * 尝试重新锁库
	 * 
	 * @author zbc
	 * @since 2016年12月28日 下午5:07:47
	 */
	private Map<String, String> checkGiftStock(PurchaseOrder order, JsonNode gitfNode) {
		Map<String, String> res = Maps.newHashMap();
		String suc = "false";
		StringBuilder msgBuilder = new StringBuilder();
		
		List<PurchaseOrderDetail> gifts = Lists.newArrayList();
		List<PurchaseOrderDetail> list = purchaseOrderDetailMapper.getAlldetailsByPurNo(order.getPurchaseOrderNo());
		PurchaseOrderDetail gift = null;
		for (JsonNode g : gitfNode) {
			gift = new PurchaseOrderDetail();
			gift.setSku(g.get("sku").asText());
			gift.setProductName(g.get("title").asText());
			gift.setPurchasePrice(g.get("price").asDouble());
			gift.setMarketPrice(g.get("marketPrice").asDouble());
			gift.setQty(g.get("qty").asInt());
			gift.setWarehouseId(g.get("disStockId").asInt());
			gift.setWarehouseName(g.get("warehouseName").asText());
			gift.setOperatorEmail(g.get("operatorEmail").asText());
			gift.setTotalPrices(g.get("sumPrice").asDouble());
			gift.setRealPrice(g.get("realPrice").asDouble());
			gift.setProductImg(g.get("publicImg").asText());
			gift.setCategoryId(g.get("categoryId").asInt());
			gift.setCategoryName(g.get("categoryName").asText());
			gift.setIsgift(true);
			list.add(gift);
			gifts.add(gift);
		}
		Double amount = new BigDecimal(order.getPurchaseTotalAmount())
				.subtract(new BigDecimal(ParametersUtil.getDoubleValue(order.getCouponsAmount())))
				.doubleValue();
		// 计算均摊价
		caculateDetails(amount, list);
		List<CloudLockPro> lockList = Lists.newArrayList();
		List<CloudLockPro> changeList = Lists.newArrayList();
		// 查询商品价格
		Map<String, Double> priceMap = Maps.newHashMap();
		
		try {
			// 查询到仓价
			getArwPrice(list, priceMap);
			// 构造锁库数据
			getLockDetails(list, priceMap, lockList);
			getLockDetails(gifts, priceMap, changeList);
			InventoryCloudLockDto lockDto = new InventoryCloudLockDto(order.getPurchaseOrderNo(), 
					order.getEmail(), order.getNickName(),
					lockList, changeList);
			// 如果为缺货采购
			if (order.getPurchaseType() == PurchaseTypes.PURCHASE_ORDER_STOCKOUT) {
				JsonNode saleNode = httpService.getSaleMain(order.getSid());
				lockDto.setSaleOrderNo(saleNode.get("salesOrderNo").asText());
			}
			JsonNode resNode = httpService.cloudLock(lockDto);
			Logger.info("云仓锁库结果:[{}]", resNode);
			suc = resNode.get("result").asText();
			msgBuilder.append(JsonCaseUtil.jsonToString(resNode.get("msg")));
			if (!"true".equals(suc)) {
				if (resNode.get("objList") != null
						&& !"null".equals(resNode.get("objList").asText())) {
					for (JsonNode obj : resNode.get("objList")) {
						msgBuilder.append("[").append(obj.get("sku").asText()).append("缺").append(obj.get("stock").asInt()).append("]");
					}
				}
			}
		} catch (Exception e) {
			msgBuilder.delete(0, msgBuilder.length());
			msgBuilder.append("检查库存异常");
			Logger.info("选赠品锁库异常", e);
		}
		res.put("suc", suc);
		res.put("msg", msgBuilder.toString());
		return res;
	}

	/**
	 * 构造锁库数据
	 * 
	 * @author zbc
	 * @since 2016年12月28日 下午4:55:45
	 */
	private void getLockDetails(List<PurchaseOrderDetail> list,
			Map<String, Double> priceMap, List<CloudLockPro> locksList) {
		CloudLockPro lock;
		String key;
		for (PurchaseOrderDetail detail : list) {
			lock = new CloudLockPro(detail);
			key = detail.getSku() + "_" + detail.getWarehouseId();
			// 查询到仓价
			lock.setArriveWarePrice(priceMap.get(key));
			locksList.add(lock);
		}
	}

	/**
	 * 批量设置赠品
	 */
	@Override
	public Map<String, Object> updateOrders(JsonNode node) {
		Map<String, Object> result = Maps.newHashMap();
		List<PurchaseOrderDetail> products = new ArrayList<PurchaseOrderDetail>();
		PurchaseOrderDetail orderDetail = null;
		for (JsonNode obj : node.get("skus")) {
			orderDetail = new PurchaseOrderDetail();
			orderDetail.setQty(obj.get("qty") == null ? null : obj.get("qty").asInt());
			orderDetail.setSku(obj.get("sku") == null ? null : obj.get("sku").asText());
			orderDetail.setWarehouseId(obj.get("warehouseId") == null ? null : obj.get("warehouseId").asInt());
			orderDetail.setIsgift(obj.get("isgift") == null ? null : obj.get("isgift").asBoolean());
			products.add(orderDetail);
		}
		Integer orderId = node.get("orderid").asInt();
		int res = purchaseOrderDetailMapper.batchUpdateDetails(orderId, products);// 先把此订单所有的赠品的数量设为0
		if (res == 0) {
			result.put("suc", false);
			result.put("msg", "批量更新失败");
			return result;
		}
		
		PurchaseOrderDetail pro = new PurchaseOrderDetail();
		List<PurchaseOrderDetail> gifts = null;
		for (PurchaseOrderDetail detail : products) {
			gifts = null;
			pro.setPurchaseId(orderId);
			pro.setWarehouseId(detail.getWarehouseId());
			pro.setIsgift(detail.getIsgift());
			pro.setSku(detail.getSku());
			gifts = purchaseOrderDetailMapper.getDetails(pro);
			pro = gifts.get(0);
			pro.setQty(detail.getQty());// detail表示赠品剩余的数量
			purchaseOrderDetailMapper.updateByPrimaryKeySelective(pro);// 爸剩余的赠品数量加到这个订单其中的一个赠品
		}
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setId(orderId);
		result.put("suc", true);
		result.put("msg", "更新成功");
		return result;
	}

	/**
	 * 处理流程 
	 * 1、 查询过期订单（有效时间小于当前时间，并且状态为未付款：0） 
	 * 2、解除冻结库存（目前没有没做冻结，所以跳过） 
	 * 3、更新订单状态，为已失效 :PurchaseOrderFlag.INVALID.getType() 
	 * 4、如果订单状态为 2 则 更新
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
			if (order.getPurchaseType() == PurchaseTypes.PURCHASE_ORDER_STOCKOUT) {
				salesList.add(order);
			}
			try {
				// change by zbc 解除库存锁
				httpService.unLock(order.getPurchaseOrderNo());
			} catch (Exception e) {
				e.printStackTrace();
			}
			count++;
		}

		if (list.size() > 0) {
			boolean flag = purchaseOrderMapper.batchUpdate(list) > 0;
			Logger.info("批量更新微仓订单状态{}", (flag ? "成功,更新了【" + count + "】条订单" : "失败"));
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

	@Override
	public Map<String, Object> importOrder(File file, String fileName,
			Map<String, String[]> params, String account) {
		// 请求返回结果map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Logger.debug("importOrder params----->{}", Json.toJson(params).toString());
		// 录入人
		String entryUser = params.containsKey("entryUser") ? params.get("entryUser")[0] : null;
		String fileID = params.containsKey("id") ? params.get("id")[0] : null;
		String md5 = params.containsKey(fileID + "_md5") ? params.get(fileID + "_md5")[0] : null;

		// 删除所有该录入人的 待生成订单
		PurchaseOrderInput input = new PurchaseOrderInput();
		input.setInputUser(entryUser);
		input.setInputType(PurchaseOrderInput.INPUT_TYPE_IMPORT);
		inputMapper.deleteInputByParam(input);

		// 文件不存在
		if (file == null) {
			resultMap.put("flag", false);
			resultMap.put("msg", "导入失败，没有选择导入文件或系统错误");
			return resultMap;
		}
		
		try {
			ImportResultInfo resultInfo = new ImportResultInfo(fileName, 0, 0);
			String fileMD5 = DigestUtils.md5Hex(new FileInputStream(file));
			Logger.debug("importOrder    fileMD5----->" + fileMD5);
			if (fileMD5 != null && fileMD5.equals(md5)) {
				// 导入csv文件数据
				if (CsvUtils.isCsvFile(fileName)) {
					// do nothing
				} else if (FileUtils.isExcelFile(fileName)) {
					// 导入excel文件数据
					importOrderExcelFile(resultMap, fileName, file,
							resultInfo, entryUser, account);
				} else {
					resultInfo.getMessages().add("文件导入失败,文件格式不对");
				}
				if (resultMap.get("flag") != null && !Boolean.valueOf(resultMap.get("flag").toString())) {
					return resultMap;
				}
			} else {
				resultInfo.getMessages().add("文件MD5值校验不通过");
			}
			resultMap.put("flag", true);
			resultMap.put("resultInfos", resultInfo);
		} catch (IOException e) {
			e.printStackTrace();
			resultMap.put("flag", false);
			resultMap.put("msg", "系统异常");
		}
		return resultMap;
	}

	/**
	 * 导入excel文件
	 * 
	 * @param resultMap
	 * @param fileName
	 * @param file
	 * @param resultInfo
	 * @param entryUser
	 * @param account
	 */
	private void importOrderExcelFile(Map<String, Object> resultMap,
			String fileName, File file, ImportResultInfo resultInfo,
			String entryUser, String account) {

		try (FileInputStream is = new FileInputStream(file)) {
			Workbook book = WorkbookFactory.create(is);
			Sheet sheet = null;
			Row row = null;
			Cell cell = null;
			String rs = null;
			PurchaseOrderInput input = null;
			PurchaseOrderInputPro pro = null;
			List<PurchaseOrderInputPro> proList = Lists.newArrayList();
			PurchaseOrderInputGift gift = null;
			String cellValue = null;

			for (int m = 0; m < book.getNumberOfSheets(); m++) {
				sheet = book.getSheetAt(m);
				if (sheet == null || sheet.getLastRowNum() == 0)
					continue;
				for (int n = 0; n <= sheet.getLastRowNum(); n++) {
					row = sheet.getRow(n);
					if (n == 0) {
						input = new PurchaseOrderInput();
						cell = row.getCell(1);
						input.setInputUser(entryUser);
						input.setDisAccount(ExcelUtils.gainCellText(cell));
						if (StringUtils.isBlankOrNull(input.getDisAccount())) {
							resultMap.put("flag", false);
							resultMap.put("msg", "模板错误，或者分销商账号为空");
							return;
						}
						if (account != null
								&& !Lists.newArrayList(account.split(","))
										.contains(input.getDisAccount())) {
							resultMap.put("flag", false);
							resultMap.put("msg", "未关联该分销商");
							return;
						}
						JsonNode info = httpService.getMemberInfo(input
								.getDisAccount());
						if (info.size() > 0) {
							input.setDisMode(info.get("distributionMode")
									.asInt());
							input.setDisType(info.get("comsumerType").asInt());
						} else {
							resultMap.put("flag", false);
							resultMap.put("msg", "分销商不存在");
							return;
						}
						// 导入单录入类型2
						input.setInputType(PurchaseOrderInput.INPUT_TYPE_IMPORT);
						inputMapper.insertSelective(input);
					}
					if (n < 3 || row == null || row.getLastCellNum() == 0)
						continue;
					pro = new PurchaseOrderInputPro();
					CellRangeAddress ca = null;
					boolean isFirst = false;
					boolean isLast = false;
					boolean addFlag = false;
					for (int i = 0; i < row.getLastCellNum(); i++) {
						cell = row.getCell(i);
						if (cell == null)
							continue;
						pro.setInputId(input.getId());
						cellValue = ExcelUtils.gainCellText(cell);
						ca = getMergedRegion(sheet, n, i);
						if (ca != null) {
							rs = getMergedRegionValue(sheet, ca);
							isFirst = isFirstORLastRow(ca, n, 1);
							isLast = isFirstORLastRow(ca, n, 2);
						} else {
							if (StringUtils.isBlankOrNull(cellValue)) {
								continue;
							}
						}
						switch (i) {
						case 0:
							pro.setSerialNumber(getInt(cellValue));
							addFlag = true;
							break;
						case 1:
							pro.setSku(cellValue);
							addFlag = true;
							break;
						case 2:
							pro.setCarton(getInt(cellValue));
							addFlag = true;
							break;
						case 3:
							pro.setUnitType(getInt(cellValue));
							addFlag = true;
							break;
						case 4:
							pro.setQty(getInt(cellValue));
							addFlag = true;
							break;
						case 5: {
							// 获取合并给单元格 是否为空
							if (ca != null) {
								// 判断是否为第一行
								if (isFirst) {
									gift = new PurchaseOrderInputGift();
									gift.setSku(rs);
									gift.setInputId(input.getId());
								}
							} else {
								gift = new PurchaseOrderInputGift();
								gift.setSku(cellValue);
								gift.setInputId(input.getId());
							}

						}
							break;
						case 6: {
							if (ca != null) {
								// 判断是否为第一行
								if (isFirst) {
									gift.setCarton(getInt(rs));
								}
							} else {
								gift.setCarton(getInt(cellValue));
							}
						}
							break;
						case 7: {
							if (ca != null) {
								// 判断是否为第一行
								if (isFirst) {
									gift.setUnitType(getInt(rs));
								}
							} else {
								gift.setUnitType(getInt(cellValue));
							}
						}
							break;
						case 8: {
							if (ca != null) {
								// 判断是否为第一行
								if (isFirst) {
									gift.setQty(getInt(rs));
									inputGiftMapper.insertSelective(gift);
								}
								if (isLast) {
									gift = null;
								}
							} else {
								gift.setQty(getInt(cellValue));
								inputGiftMapper.insertSelective(gift);
							}
						}
						}
					}
					if (addFlag) {
						proList.add(pro);
					}
				}
				inputProMapper.insertBatch(proList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getInt(String num) {
		return Double.valueOf(num).intValue();
	}

	/**
	 * 获取合并单元格
	 * 
	 * @param sheet
	 * @param row
	 *            行下标
	 * @param column
	 *            列下标
	 * @return
	 */
	private CellRangeAddress getMergedRegion(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();
			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					return ca;
				}
			}
		}
		return null;
	}

	/**
	 * 获取合并单元格的值
	 * 
	 * @author zbc
	 * @since 2016年8月31日 上午9:37:21
	 */
	private String getMergedRegionValue(Sheet sheet, CellRangeAddress ca) {
		int firstColumn = ca.getFirstColumn();
		int firstRow = ca.getFirstRow();
		Row fRow = sheet.getRow(firstRow);
		Cell fCell = fRow.getCell(firstColumn);
		return ExcelUtils.gainCellText(fCell);
	}

	/**
	 * 判断是否为第一行 或最后一行
	 * 
	 * @author zbc
	 * @since 2016年8月31日 上午9:37:21
	 */
	private boolean isFirstORLastRow(CellRangeAddress ca, int row, Integer type) {
		switch (type) {
		case 1: 
			return row == ca.getFirstRow();
		case 2: 
			return row == ca.getLastRow();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ViewPurchaseIterm queryPurchases(JsonNode node, List<String> accounts) {
		ViewPurchaseIterm iterm = new ViewPurchaseIterm();
		ReturnMess returnMess = new ReturnMess();// 初始默认为0条记录，页数1
		iterm.setTotal(0);
		iterm.setPages(1);
		Integer pageCount = node.has("pageCount") ? node.get("pageCount").asInt() : 1;
		Integer pageSize = node.has("pageSize") ? node.get("pageSize").asInt() : 10;
		Integer status = JsonCaseUtil.geIntegerValue(node, "status");
		// add by huangjc 兼容多个状态的查询
		String statusStr = JsonCaseUtil.getStringValue(node, "statusStr");
		String paytype = JsonCaseUtil.getStringValue(node, "paytype");
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> map = objectMapper.readValue(node.toString(), Map.class);
			int pageNow = (pageCount == 0 ? 0 : (pageCount - 1) * pageSize);
			map.put("pageNow", pageNow);
			map.put("pageCount", pageCount);
			map.put("pageSize", pageSize);
			map.put("accounts", accounts);
			map.put("seachFlag", JsonCaseUtil.getStringValue(node, "seachFlag"));
			map.put("sort", JsonCaseUtil.getStringValue(node, "sort"));
			map.put("filter", JsonCaseUtil.getStringValue(node, "filter"));
			// 对状态做一个转换为list by huangjc 2016.12.2
			List<Integer> statusList = Lists.newArrayList();
			if (StringUtils.isNotBlankOrNull(statusStr)) {
				statusList.addAll(Stream.of(statusStr.split(",")).map(Integer::valueOf).collect(Collectors.toList()));
			}
			if (status != null && status != 9) {
				statusList.add(status);
			}
			map.put("isback", JsonCaseUtil.jsonToBoolean(node.get("isback")));
			map.put("statusList", statusList);
			map.put("paytype", paytype);
			iterm = viewPurchase(map);
		} catch (IOException e) {
			returnMess = new ReturnMess("1", "异常情况");
			iterm.setReturnMess(returnMess);
		}
		return iterm;
	}

	@Override
	public Map<String, Object> getImportOrder(JsonNode node, int inputType) {
		Map<String, Object> res = Maps.newHashMap();
		PurchaseOrderInput input = new PurchaseOrderInput();
		input.setInputUser(node.get("entryUser").asText());
		input.setInputType(inputType);
		List<PurchaseOrderInput> list = inputMapper.selectByParam(input);

		List<PurchaseOrderInputGift> giftList = Lists.newArrayList();
		List<PurchaseOrderInputPro> proList = Lists.newArrayList();
		Map<String, Object> skuWareMap = Maps.newHashMap();
		
		try {
			if (list.size() > 0) {
				input = list.get(0);
				giftList = inputGiftMapper.selectByInputId(input.getId());
				proList = inputProMapper.selectByInputId(input.getId());
				setInputProsGiftsProperties(input.getDisMode(), skuWareMap,
						giftList, proList, input.getDisAccount());
			}
			res.put("suc", true);
			res.put("input", input);
			res.put("gifts", giftList);
			res.put("pros", proList);
			res.put("skuWare", skuWareMap);
		} catch (Exception e) {
			res.put("suc", false);
			res.put("msg", "获取导入采购单异常");
			Logger.info("获取导入采购单信息异常", e);
		}
		return res;
	}

	/**
	 * 设置正价商品和赠品的属性
	 * 
	 * @param mode
	 *            模式
	 * @param skuWareMap
	 * @param giftList
	 *            赠品集合
	 * @param proList
	 *            正价商品集合
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	private void setInputProsGiftsProperties(Integer mode,
			Map<String, Object> skuWareMap,
			List<PurchaseOrderInputGift> giftList,
			List<PurchaseOrderInputPro> proList, String email)
			throws JsonProcessingException, IOException {
		// 正价商品和赠品的sku集合
		Set<String> skuSet = Sets.newHashSet();
		for (PurchaseOrderInputGift gift : giftList) {
			skuSet.add(gift.getSku());
		}
		for (PurchaseOrderInputPro pro : proList) {
			skuSet.add(pro.getSku());
		}

		// 获取商品信息
		GetProductsParams getProductsParams = new GetProductsParams();
		getProductsParams.setSkuList(Lists.newArrayList(skuSet));
		getProductsParams.setModel(mode);
		getProductsParams.setEmail(email);
		JsonNode proNode = httpService.getProducts(getProductsParams);
		List<Map<String, Object>> warehouseNameIds = null;
		Map<String, Object> warehouseIdAndName = null;
		JsonNode product = null;
		if (proNode.get("data") != null
				&& proNode.get("data").get("result") != null) {
			// 设置赠品的属性
			for (PurchaseOrderInputGift gift : giftList) {
				warehouseNameIds = Lists.newArrayList();
				for (Iterator<JsonNode> iterator = proNode.get("data").get("result").iterator(); iterator.hasNext();) {
					product = iterator.next();
					warehouseIdAndName = new HashMap<String, Object>();

					if (gift.getSku().equals(product.get("csku").asText())) {
						warehouseIdAndName.put("warehouseId", product.get("warehouseId").asInt());
						warehouseIdAndName.put("warehouseName", product.get("warehouseName").asText());

						if (gift.getWarehouseId() == null) {
							gift.setWarehouseId(product.get("warehouseId").asInt());
						}
						gift.setWarehouseName(product.get("warehouseName").asText());
						gift.setPrice(getDisPrice(product));
						gift.setTitle(product.get("ctitle").asText());
						gift.setImageUrl(product.get("imageUrl").asText());
						gift.setStock(product.get("stock") != null ? product.get("stock").asInt() : null);
						gift.setExpirationDays(JsonCaseUtil.jsonToInteger(product.path("expirationDays")));

						gift.setProductDetails(Maps.newHashMap());
						warehouseNameIds.add(warehouseIdAndName);
						skuWareMap.put(String.join("_", product.get("csku").asText(),
								String.valueOf(product.get("warehouseId").asInt())), product);
					}
				}
				gift.setWarehouseNameId(warehouseNameIds);
			}

			// 设置正价商品的属性
			for (PurchaseOrderInputPro pro : proList) {
				warehouseNameIds = Lists.newArrayList();

				for (Iterator<JsonNode> iterator = proNode.get("data").get("result").iterator(); iterator.hasNext();) {
					product = iterator.next();
					warehouseIdAndName = new HashMap<String, Object>();

					if (pro.getSku().equals(product.get("csku").asText())) {
						warehouseIdAndName.put("warehouseId", product.get("warehouseId").asInt());
						warehouseIdAndName.put("warehouseName", product.get("warehouseName").asText());

						if (pro.getWarehouseId() == null) {
							pro.setWarehouseId(product.get("warehouseId").asInt());
						}
						pro.setWarehouseName(product.get("warehouseName").asText());
						pro.setStock(product.get("stock") != null ? product.get("stock").asInt() : null);
						pro.setExpirationDays(JsonCaseUtil.jsonToInteger(product.path("expirationDays")));
						pro.setPrice(getDisPrice(product));
						pro.setTitle(product.get("ctitle").asText());
						pro.setImageUrl(product.get("imageUrl").asText());
						pro.setProductDetails(Maps.newHashMap());
						warehouseNameIds.add(warehouseIdAndName);
						skuWareMap.put(String.join("_", product.get("csku").asText(),
								String.valueOf(product.get("warehouseId").asInt())), product);
					}
				}
				pro.setWarehouseNameId(warehouseNameIds);
			}

			inputProMapper.batchUpdateWare(proList);
			inputGiftMapper.batchUpdateWarehouse(giftList);
		}
	}

	@Override
	public Map<String, Object> generInputOder(InputOrderParam param) {
		Map<String, Object> res = Maps.newHashMap();

		// 检查oa审批单号
		if (checkOaAuditNoExists(param.getOaAuditNo())) {
			// 存在，不能使用
			res.put("suc", false);
			res.put("msg", "oa审批号存在，不能重复使用！");
			return res;
		}
		
		int inputId = param.getInputId();
		PurchaseOrderInput input = inputMapper.selectByPrimaryKey(inputId);
		if (input == null) {
			res.put("suc", false);
			res.put("msg", "查询不到录入信息");
			return res;
		}
		
		// 正价商品
		List<PurchaseOrderInputPro> proList = inputProMapper.selectByInputId(input.getId());
		if (proList == null || proList.size() == 0) {
			res.put("suc", false);
			res.put("msg", "没有添加正价商品，不能生成采购单");
			return res;
		}

		// 赠品
		List<PurchaseOrderInputGift> giftList = inputGiftMapper.selectByInputId(input.getId());
		
		try {
			// 设置正价商品和赠品商品详情
			setProductsDetails(giftList, proList, input.getDisMode(), input.getDisAccount());

			// 要下单的商品详情
			List<Map<String, Object>> detailList = extractProductDetails(proList, giftList);

			for (Map<String, Object> map : detailList) {
				if (map != null) {
					map.put("isBack", true);
				}
			}
			
			// 下单参数
			Map<String, Object> orderParam = Maps.newHashMap();
			orderParam.put("email", input.getDisAccount());
			orderParam.put("orderDetail", detailList);// 商品详情
			orderParam.put("distributorType", input.getDisType());
			orderParam.put("totalPrice", PriceFormatUtil.toFix2(calculateTotalPrice(proList)));
			orderParam.put("inputUser", input.getInputUser());
			if (giftList.size() > 0) {
				orderParam.put("isPro", true);
				orderParam.put("isChoose", true);
			}
			orderParam.put("payType", param.getPayType());// 现金支付
			orderParam.put("backIn", true);// 后台录入标识
			orderParam.put("money", param.getMoney());
			orderParam.put("purchaseType", param.getPurchaseType());
			orderParam.put("oaAuditNo", param.getOaAuditNo());// oa审批号
			orderParam.put("businessRemarks", param.getBusinessRemarks());// 业务备注
			if (param.getBbcPostage() != null) {
				orderParam.put("bbcPostage", param.getBbcPostage());// 运费---整批出库会有运费
			}
			
			// 下单
			ReturnMess orderedResult = orderPurchase(Json.toJson(orderParam), null);
			Logger.info("下采购单的结果=={}", orderedResult);

			boolean orderSuccess = "0".equals(orderedResult.getErrorCode()) || "3".equals(orderedResult.getErrorCode());
			if (!orderSuccess) {
				Logger.info("下单失败，结果为：{}", orderedResult);
				res.put("suc", false);
				res.put("msg", "下采购单失败");
				return res;
			}
			
			res.put("suc", true);
			res.put("msg", "下采购单成功");
			res.put("purchaseOrderNo", orderedResult.getErrorInfo());
			// 删除录入信息
			Logger.info("采购单生成成功，删除掉录入的数据，id=" + input.getId());
			inputMapper.deleteInput(input.getId());
			return res;
		} catch (Exception e) {
			Logger.info("录入订单生产异常", e);
			res.put("suc", false);
			res.put("msg", "下单异常");
			return res;
		}
	}

	/**
	 * 检查oa审批单号
	 * 
	 * @param oaAuditNo
	 * @return
	 */
	private boolean checkOaAuditNoExists(String oaAuditNo) {
		// 有才进行检查
		if (StringUtils.isNotBlankOrNull(oaAuditNo)) {
			Integer count = purchaseOrderMapper
					.selectCountByOAAuditNo(oaAuditNo.trim());
			boolean oaAuditNoExists = (count != null && count > 0);
			Logger.info("oa审批单号==oaAuditNo={}===检查结果==={}", oaAuditNo,
					oaAuditNoExists);
			return oaAuditNoExists;
		}
		return false;
	}

	private double getPurchaseTotal4SO(Double bcPostage,
			List<PurchaseOrderDetail> details) {
		// 生成发货单
		// 计算发货单商品的总计+运费
		BigDecimal soTotalAmount = details.stream()
				.filter(detail -> 2024 == detail.getWarehouseId().intValue())
				.map(detail -> new BigDecimal(detail.getTotalPrices()))
				.reduce(new BigDecimal(0), (x, y) -> x.add(y));
		if (bcPostage != null) {
			soTotalAmount = soTotalAmount.add(new BigDecimal(bcPostage));
		}
		return PriceFormatUtil.toFix2(soTotalAmount);
	}

	@Override
	public Map<String, Object> deliverDutyPaidGoods(InputOrderParam param,
			DeliverDutyPaidGoodsParam deliverParam) {
		Map<String, Object> resultGenerInputOder = generInputOder(param);
		Logger.info("下采购单的结果：{}", resultGenerInputOder);
		boolean suc = (boolean) resultGenerInputOder.get("suc");
		// 下单失败
		if (!suc) {
			return resultGenerInputOder;
		}

		// 查询生成的采购单
		String purchaseOrderNo = (String) resultGenerInputOder
				.get("purchaseOrderNo");
		PurchaseOrder poParams = new PurchaseOrder();
		poParams.setPurchaseOrderNo(purchaseOrderNo);
		PurchaseOrder purchaseOrder = purchaseOrderMapper.selectOrder(poParams);
		List<PurchaseOrderDetail> details = purchaseOrderDetailMapper
				.getAlldetailsByPurNo(purchaseOrderNo);

		Map<String, Object> organizedOrderSaleOrderParams = getOrganizedOrderSaleOrderParams(
				purchaseOrder, details, param, deliverParam);
		Logger.info("==========保存整批出库参数organizedOrderSaleOrderParams========={}", organizedOrderSaleOrderParams);

		// 保存标记，说明此采购单要整批出库
		Logger.info("==========保存结果======={}", (savePurchaseStockout(purchaseOrderNo, Json.toJson(organizedOrderSaleOrderParams).toString()) ? "采购" : "失败"));

		return resultGenerInputOder;
	}

	
	private boolean savePurchaseStockout(String purchaseOrderNo, String JsonStr) {
		PurchaseStockout pso = new PurchaseStockout();
		pso.setPurchaseOrderNo(purchaseOrderNo);
		pso.setJsonStr(JsonStr);
		pso.setStatus(0);
		int count = purchaseStockoutMapper.insert(pso);
		return count>0;
	}

	/**
	 * 组织下发货单的参数
	 * 
	 * @param purchaseOrder
	 * @param details
	 * @return
	 */
	private Map<String, Object> getOrganizedOrderSaleOrderParams(
			PurchaseOrder purchaseOrder, List<PurchaseOrderDetail> details,
			InputOrderParam param, DeliverDutyPaidGoodsParam deliverParam) {
		List<ObjectNode> detailList = details
				.stream()
				.filter(detail -> 2024 == detail.getWarehouseId().intValue())
				.map(detail -> {
					ObjectNode newObject = Json.newObject();
					newObject.put("sku", detail.getSku());
					newObject.put("num", detail.getQty());
					newObject.put("productName", detail.getProductName());
					newObject.put("productImg", detail.getProductImg());
					newObject.put("purchasePrice", detail.getPurchasePrice());
					newObject.put("marketPrice", detail.getMarketPrice());
					newObject.put("warehouseId", detail.getWarehouseId());
					newObject.put("warehouseName", detail.getWarehouseName());
					newObject.put("finalSellingPrice", detail.getPurchasePrice());
					return newObject;
				}).collect(Collectors.toList());

		Logger.info("skuList==数量=={}========={}", detailList.size(), detailList);
		Map<String, Object> params = Maps.newHashMap();
		params.put("bbcPostage", deliverParam.getBbcPostage());
		params.put("isNotified", 1);
		params.put("email", purchaseOrder.getEmail());
		params.put("warehouseId", detailList.get(0).get("warehouseId").asInt());
		params.put("warehouseName", detailList.get(0).get("warehouseName").asText());
		params.put("remark", "");
		params.put("address", deliverParam.getAddress());
		params.put("receiver", deliverParam.getReceiver());
		params.put("telphone", deliverParam.getTelephone());
		params.put("idcard", "");
		params.put("postCode", deliverParam.getPostCode());
		params.put("orderer", "");
		params.put("skuList", detailList);
		params.put("provinceId", deliverParam.getProvinceId());
		params.put("LogisticsTypeCode", deliverParam.getShippingCode());// 发货方式
		params.put("logisticsMode", deliverParam.getShippingName());
		params.put("orderActualAmount", 0);
		params.put("createUser", purchaseOrder.getInputUser());
		params.put("isBack", true);
		params.put("orderTotalAmount", getPurchaseTotal4SO(deliverParam.getBbcPostage(), details));
		return params;
	}

	/**
	 * 从正价商品和赠品中拿到商品详情
	 * 
	 * @author zbc
	 * @since 2016年9月1日 下午8:31:05
	 */
	private List<Map<String, Object>> extractProductDetails(
			List<PurchaseOrderInputPro> proList,
			List<PurchaseOrderInputGift> giftList) {
		List<Map<String, Object>> detailList = Lists.newArrayList();
		for (PurchaseOrderInputPro aPro : proList) {
			detailList.add(aPro.getProductDetails());
		}
		for (PurchaseOrderInputGift aGift : giftList) {
			detailList.add(aGift.getProductDetails());
		}
		return detailList;
	}

	/**
	 * 计算正价商品总的分销价
	 * @param proList
	 * @return
	 */
	private BigDecimal calculateTotalPrice(List<PurchaseOrderInputPro> proList) {
		BigDecimal totalPrice = new BigDecimal(0.00);
		if (proList != null && proList.size() > 0) {
			for (PurchaseOrderInputPro aInputPro : proList) {
				totalPrice = totalPrice.add(getOneProPriceTotal(aInputPro));
			}
		}
		return totalPrice;
	}

	/**
	 * 获取一个正价商品的价格：使用的是分销价
	 * @param aPro 一个正价商品
	 * @return 
	 */
	private BigDecimal getOneProPriceTotal(PurchaseOrderInputPro aPro) {
		BigDecimal proPriceTotal = new BigDecimal(0.0);
		if (aPro != null) {
			if (aPro.getUnitType() == PurchaseOrderInputPro.UNIT_TYPE_SINGLE) {
				proPriceTotal = proPriceTotal
						.add(new BigDecimal(aPro.getPrice()).multiply(new BigDecimal(aPro.getQty())));
			} else if (aPro.getUnitType() == PurchaseOrderInputPro.UNIT_TYPE_BOX) {
				// 单位是箱，要换算出总个数
				proPriceTotal = proPriceTotal.add(new BigDecimal(aPro.getPrice())
						.multiply(new BigDecimal(aPro.getQty()).multiply(new BigDecimal(aPro.getCarton()))));
			}
		}
		return proPriceTotal;
	}

	/**
	 * 计算正价商品总价
	 * 
	 * @param proList
	 * @return
	 */
	public BigDecimal getProPriceTotal(List<PurchaseOrderInputPro> proList) {
		BigDecimal proPriceTotal = new BigDecimal(0.0);
		if (proList==null || proList.size() == 0) {
			return proPriceTotal;
		}
		
		for (PurchaseOrderInputPro aPro : proList) {
			if (aPro.getPrice() != null) {
				proPriceTotal = proPriceTotal.add(getOneProPriceTotal(aPro));
			}
		}
		return proPriceTotal;
	}

	/**
	 * 设置正价商品和赠品商品详情
	 * 
	 * @param giftList
	 * @param proList
	 * @param disMode
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public void setProductsDetails(List<PurchaseOrderInputGift> giftList,
			List<PurchaseOrderInputPro> proList, Integer disMode, String email)
			throws JsonProcessingException, IOException {
		Set<String> skuSet = Sets.newHashSet();
		for (PurchaseOrderInputGift gift : giftList) {
			skuSet.add(gift.getSku());
		}
		for (PurchaseOrderInputPro pro : proList) {
			skuSet.add(pro.getSku());
		}

		// 查询商品
		GetProductsParams getProductsParams = new GetProductsParams();
		getProductsParams.setSkuList(Lists.newArrayList(skuSet));
		getProductsParams.setModel(disMode);
		getProductsParams.setEmail(email);
		JsonNode proNode = httpService.getProducts(getProductsParams);
		// 变成Map
		Map<String, JsonNode> skuWarehouseId2ProductNode = getProducts2Map(proNode);

		Map<String, Object> detail = null;
		for (PurchaseOrderInputPro pro : proList) {
			JsonNode productNode = skuWarehouseId2ProductNode.get(String.join("_", pro.getSku(), pro.getWarehouseId().toString()));
			if (productNode != null) {
				detail = Maps.newHashMap();
				Double purchasePrice = getDisPrice(productNode);
				Integer qty = pro.getUnitType() == 1 ? pro.getQty() : pro.getQty() * pro.getCarton();
				pro.setPrice(purchasePrice);
				detail.put("title", JsonCaseUtil.getStringValue(productNode, "ctitle"));
				detail.put("price", purchasePrice);// 采购价
				detail.put("warehouseId", pro.getWarehouseId());
				detail.put("warehouseName", JsonCaseUtil.getStringValue(productNode, "warehouseName"));
				detail.put("sumPrice", PriceFormatUtil.toFix2(purchasePrice * qty));
				detail.put("marketPrice", JsonCaseUtil.getDoubleValue(productNode, "proposalRetailPrice"));
				detail.put("publicImg", JsonCaseUtil.getStringValue(productNode, "imageUrl"));
				detail.put("sku", pro.getSku());
				detail.put("qty", qty);
				detail.put("realPrice", purchasePrice);
				detail.put("disPrice", JsonCaseUtil.getDoubleValue(productNode, "disPrice")); // 分销价
				detail.put("categoryId", JsonCaseUtil.geIntegerValue(productNode, "categoryId"));
				detail.put("categoryName", JsonCaseUtil.getStringValue(productNode, "cname"));
				detail.put("isgift", false);
				detail.put("interBarCode", JsonCaseUtil.getStringValue(productNode, "interBarCode"));
				detail.put("expirationDate", pro.getExpirationDate());// 到期日期
				detail.put("contractNo", JsonCaseUtil.jsonToString(productNode.get("contractNo")));// 合同号
				detail.put("clearancePrice", JsonCaseUtil.jsonToDouble(productNode.get("clearancePrice")));
				pro.setProductDetails(detail);
			}
		}

		for (PurchaseOrderInputGift gift : giftList) {
			JsonNode productNode = skuWarehouseId2ProductNode.get(gift.getSku() + "_" + gift.getWarehouseId().toString());
			if (productNode != null) {
				detail = Maps.newHashMap();
				Double purchasePrice = getDisPrice(productNode);
				Integer qty = gift.getUnitType() == 1 ? gift.getQty() : gift.getQty() * gift.getCarton();
				gift.setPrice(purchasePrice);
				detail.put("title", JsonCaseUtil.getStringValue(productNode, "ctitle"));
				detail.put("price", 0.00);
				detail.put("warehouseId", gift.getWarehouseId());
				detail.put("warehouseName", JsonCaseUtil.getStringValue(productNode, "warehouseName"));
				detail.put("sumPrice", 0.00);
				detail.put("publicImg", JsonCaseUtil.getStringValue(productNode, "imageUrl"));
				detail.put("sku", gift.getSku());
				detail.put("qty", qty);
				detail.put("realPrice", purchasePrice);
				detail.put("marketPrice", JsonCaseUtil.getDoubleValue(productNode, "proposalRetailPrice"));
				detail.put("categoryId", JsonCaseUtil.geIntegerValue(productNode, "categoryId"));
				detail.put("categoryName", JsonCaseUtil.getStringValue(productNode, "cname"));
				detail.put("isgift", true);
				detail.put("interBarCode", JsonCaseUtil.getStringValue(productNode, "interBarCode"));
				detail.put("expirationDate", gift.getExpirationDate());// 到期日期
				detail.put("clearancePrice", JsonCaseUtil.jsonToDouble(productNode.get("clearancePrice")));
				gift.setProductDetails(detail);
			}
		}

	}

	private Map<String, JsonNode> getProducts2Map(JsonNode proNode) {
		Map<String, JsonNode> skuWarehouseId2ProductNode = Maps.newHashMap();
		for (Iterator<JsonNode> it = proNode.get("data").get("result").iterator(); it.hasNext();) {
			JsonNode product = it.next();
			String key = String.join("_", product.get("csku").asText(), product.get("warehouseId").asText());
			skuWarehouseId2ProductNode.put(key, product);
		}
		return skuWarehouseId2ProductNode;
	}

	private Double getDisPrice(JsonNode pro) {
		Double purchase = pro.get("disPrice").asDouble();
		if (pro.get("isSpecial") != null && pro.get("isSpecial").asBoolean()) {
			purchase = pro.get("specialSale").asDouble();
		}
		return PriceFormatUtil.toFix2(purchase);
	}

	@Override
	public Map<String, Object> proUpdate(JsonNode node) {
		Map<String, Object> res = Maps.newHashMap();
		try {
			PurchaseOrderInputPro pro = new PurchaseOrderInputPro();
			pro.setId(node.get("id").asInt());
			pro.setWarehouseId(node.get("warehouseId").asInt());
			inputProMapper.updateByPrimaryKeySelective(pro);
			res.put("suc", true);
			res.put("msg", "更新仓库信息成功");
		} catch (Exception e) {
			Logger.info("更新正价商品仓库信息异常", e);
		}
		return res;
	}

	@Override
	public Map<String, Object> giftUpdate(JsonNode node) {
		Map<String, Object> res = Maps.newHashMap();
		PurchaseOrderInputGift gift = new PurchaseOrderInputGift();
		try {
			gift.setId(node.get("id").asInt());
			gift.setWarehouseId(node.get("warehouseId").asInt());
			inputGiftMapper.updateByPrimaryKeySelective(gift);
			res.put("suc", true);
			res.put("msg", "更新仓库信息成功");
		} catch (Exception e) {
			Logger.info("更新正价商品仓库信息异常", e);
		}
		return res;
	}

	@Override
	public ViewPurchaseOrder viewPurchaseOrderDetailById(int orderId) {
		ViewPurchaseOrder viewOrder = null;
		try {
			// 查询订单
			PurchaseOrder po = purchaseOrderMapper.selectByPrimaryKey(orderId);
			viewOrder = new ViewPurchaseOrder();
			BeanUtils.copyProperties(po, viewOrder);
			// 订单折后价 为空则设置为订单总价
			viewOrder.setPurchaseDiscountAmount(po.getPurchaseDiscountAmount() == null ? po.getPurchaseTotalAmount()
					: po.getPurchaseDiscountAmount());
			viewOrder.setSorderDate(DateUtils.date2FullDateTimeString(viewOrder.getPurchaseDate()));
			viewOrder.setBbcPostage(po.getBbcPostage());
			viewOrder.setLogisticsMode(po.getLogisticsMode());
			viewOrder.setCouponsCode(po.getCouponsCode());
			viewOrder.setCouponsAmount(po.getCouponsAmount());
			viewOrder.setIsPro(po.getIsPro());
			viewOrder.setIsChoose(po.getIsChoose());
			viewOrder.setInputUser(StringUtils.isBlankOrNull(po.getInputUser()) ? po.getEmail() : po.getInputUser());
			if (viewOrder.getPayDate() != null){
				viewOrder.setSpayDate(DateUtils.date2FullDateTimeString(viewOrder.getPayDate()));
			}
			PurchaseOrderDetail detail = new PurchaseOrderDetail();
			detail.setPurchaseId(po.getId());
			// 查询订单详情
			List<PurchaseOrderDetail> purchaseDetails = purchaseOrderDetailMapper.getDetails(detail);
			List<ViewPurchaseDetail> details = Lists.newArrayList();
			for (PurchaseOrderDetail purchaseDetail : purchaseDetails) {
				ViewPurchaseDetail viewDetail = new ViewPurchaseDetail();
				BeanUtils.copyProperties(purchaseDetail, viewDetail);
				if (viewDetail.getRealPrice() == null) {
					viewDetail.setRealPrice(purchaseDetail.getPurchasePrice());
				}
				viewDetail.setIsClearance(purchaseDetail.getClearancePrice()!=null);
				details.add(viewDetail);
			}
			viewOrder.setDetails(details);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("EXCEPTION", e);
		}
		return viewOrder;
	}

	/**
	 * 校验付款截图图片格式与大小 <br>
	 * 格式要为png/jpg/bmp，大小要是2 * 1024 * 1024
	 * 
	 * @param file
	 * @return
	 */
	private boolean checkPicFileFormatAndSize(FilePart file) {
		return (file == null
				|| (file != null
						&& (file.getFilename().endsWith(".png") || file.getFilename().endsWith(".jpg")
								|| file.getFilename().endsWith(".bmp"))
						&& file.getFile().length() <= (2 * 1024 * 1024)));
	}

	@Override
	public String submitAudit(Map<String, String[]> params, FilePart file,
			String email) {
		Map<String, Object> res = Maps.newHashMap();
		
		PurchaseOrder queryPOParams = new PurchaseOrder();
		String pno = ParametersUtil.getString(params.get("purno"));
		queryPOParams.setPurchaseOrderNo(pno);
		PurchaseOrder order = purchaseOrderMapper.selectOrder(queryPOParams);
		
		// 判断登陆email与订单email一致
		if (StringUtils.isBlankOrNull(email) || !email.equals(order.getEmail())) {
			res.put("suc", false);
			res.put("msg", "当前采购单所属分销商与当前登录分销商不一致");
			return Json.toJson(res).toString();
		}
		
		// 判断是否存在待审核的线下转账申请
		if (auditMapper.getNoAuditApplyCount(pno) > 0) {
			res.put("suc", false);
			res.put("msg", "已存在待审核的线下转账申请，不可重复提交");
			return Json.toJson(res).toString();
		}
		
		// 校验付款流水号是否重复
		String transferNumber = ParametersUtil.getString(params.get("transferNumber"));
		if (transferNumber != null && !checktransferNumber(transferNumber)) {
			res.put("suc", false);
			res.put("msg", "付款流水号不能重复");
			return Json.toJson(res).toString();
		}
		
		// 检查截图
		if (!checkPicFileFormatAndSize(file)) {
			res.put("suc", false);
			res.put("msg", "付款截图只支持jpg、bmp、png三种格式，且大小不能大于2MB");
			return Json.toJson(res).toString();
		}
		
		// 转账金额
		BigDecimal transferAmount = ParametersUtil.setBigDecimal(params.get("transferAmount"));
		Double bbcpost = order.getBbcPostage() == null ? 0 : order.getBbcPostage();
		Double coupon = order.getCouponsAmount() == null ? 0 : order.getCouponsAmount();
		// 订单金额
		Double amount = order.getPurchaseTotalAmount();
		BigDecimal orderAmount = 
				new BigDecimal(amount).add(new BigDecimal(bbcpost)).subtract(new BigDecimal(coupon)).setScale(2, BigDecimal.ROUND_HALF_UP) ;
				
		// 付款金额是否大于等于采购单总额
		boolean isTransferAmountOk = transferAmount != null && transferAmount.compareTo(orderAmount) >= 0;
		if(!isTransferAmountOk){
			res.put("suc", false);
			res.put("msg", "付款金额不能小于当前采购单总额");
			return Json.toJson(res).toString();
		}
		
		try {
			// 上传文件
			String filepath = file == null ? "" : uploadFile(file, email);
			PurchaseAudit audit = new PurchaseAudit();
			audit.setPurchaseNo(pno);
			String recipientId = ParametersUtil.getString(params.get("recipientId"));
			audit.setRecipientCardId("".equals(recipientId)? null: Integer.valueOf(recipientId));
			// 待审核
			audit.setStatus(PurchaseAuditStatus.WAINTING_AUDIT);
			audit.setEmail(email);
			audit.setCreateUser(order.getInputUser());
			audit.setOrderAmount(orderAmount);
			audit.setOrderDate(order.getPurchaseDate());
			audit.setScreenshotUrl(filepath);
			audit.setRecipientAccount(ParametersUtil.getString(params.get("receiptAccount")));
			audit.setRecipientName(ParametersUtil.getString(params.get("receiptName")));
			audit.setTransferType(ParametersUtil.getString(params.get("transferType")));
			audit.setTransferCard(ParametersUtil.getString(params.get("transferCard")));
			audit.setTransferName(ParametersUtil.getString(params.get("distributorName")));
			audit.setTransferNumber(transferNumber);
			// 转账金额
			audit.setTransferAmount(transferAmount);
			audit.setApplyRemark(ParametersUtil.getString(params.get("applyRemark")));
			// 转账时间
			String transferTimeStr = ParametersUtil.getString(params.get("transferTime"));
			if (StringUtils.isNotBlankOrNull(transferTimeStr)) {
				audit.setTransferTime(DateUtils.string2date(transferTimeStr, DateUtils.FORMAT_FULL_DATETIME));
			}
			// 保存线下转账记录
			auditMapper.insertSelective(audit);

			order.setTransactionId(audit.getTransferNumber());
			// 支付时间
			if (StringUtils.isBlankOrNull(transferTimeStr)) {
				order.setPayDate(new Date());
			} else {
				order.setPayDate(DateUtils.string2date(transferTimeStr, DateUtils.FORMAT_FULL_DATETIME));
			}
			// 线下转账的
			order.setPaymentId("cash-noline");
			order.setOfflineMoney(transferAmount != null ? transferAmount.doubleValue() : null);
			// 线下转账待客服审核
			order.setStatus(PurchaseOrderStatus.AUDIT_WAINTING_CUSTOMER_SERVICE);
			markPro(order);
			// 计算总到仓价
			Double tAWPrice = getTotalArriveWarehousePrice(order.getPurchaseOrderNo());
			order.settAWPrice(tAWPrice);
			purchaseOrderMapper.updateByPrimaryKeySelective(order);
			res.put("suc", true);
			return Json.toJson(res).toString();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.debug("submitAudit    Exception----->", e);
			res.put("suc", false);
			res.put("msg", "系统异常");
			return Json.toJson(res).toString();
		}
	}

	private boolean checktransferNumber(String transferNumber) {
		try {
			JsonNode resNode = httpService.checktransferNumber(transferNumber);
			return resNode.get("suc").asBoolean();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 上传文件
	 * 
	 * @param file
	 * @param distributor
	 * @return
	 * @throws IOException
	 */
	private String uploadFile(FilePart file, String distributor)
			throws IOException {
		try {
			if (StringUtils.isBlankOrNull(filePath)) {
				Configuration config = Play.application().configuration().getConfig("b2b");
				filePath = config.getString("imagePath");
			}
			String path = filePath + File.separator + distributor;
			File folder = new File(path);
			Logger.info("文件路径：" + path);
			if (!folder.exists()) {
				folder.mkdirs();
				Logger.info("文件路径创建成功：" + path);
			}
			File target = new File(filePath + File.separator + distributor
					+ File.separator + System.currentTimeMillis() + "-"
					+ file.getFilename());
			Logger.info("文件创建" + (target.createNewFile() ? "成功" : "失败"));
			Files.copy(file.getFile(), target);
			return target.getAbsolutePath();
		} catch (Exception e) {
			Logger.error("创建文件夹错误：" + e);
			return "";
		}

	}

	@Override
	public PurchaseAudit getTransferApply(Integer id) {
		return auditMapper.selectByPrimaryKey(id);
	}

	@Override
	public Map<String, Object> changeOrderPrice(String str) {
		Map<String, Object> res = Maps.newHashMap();
		try {
			JsonNode node = Json.parse(str);
			String pno = node.get("pno").asText();
			PurchaseOrder order = new PurchaseOrder();
			order.setPurchaseOrderNo(pno);
			order = purchaseOrderMapper.selectOrder(order);
			Double reducePrice = null;
			boolean flag = false;
			String error = null;
			if (order != null) {
				if (node.has("bbcPostage")) {
					order.setBbcPostage(node.get("bbcPostage").asDouble());
				}
				if (node.has("reducePrice")) {
					List<PurchaseOrderDetail> details = purchaseOrderDetailMapper
							.getAlldetailsByPurNo(pno);
					Double total = details.stream()
							.mapToDouble(e -> e.getTotalPrices()).sum();
					reducePrice = node.get("reducePrice").asDouble();
					order.setPurchaseTotalAmount(PriceFormatUtil
							.toFix2(new BigDecimal(total)
									.subtract(new BigDecimal(reducePrice))));
					order.setReducePrice(reducePrice);
				}
				// 校验金额
				BigDecimal actualPay = new BigDecimal(order.getPurchaseTotalAmount())
						.subtract(new BigDecimal(order.getCouponsAmount() != null ? order.getCouponsAmount() : 0.00));
				if (actualPay.compareTo(BigDecimal.ZERO) > 0) {
					flag = purchaseOrderMapper.updateByPrimaryKeySelective(order) > 0;
					if (flag) {
						// 重新计算均摊价
						PurchaseOrderDto dto = new PurchaseOrderDto();
						BeanUtils.copyProperties(order, dto);
						ebus.post(dto);

						if (order.getCouponsCode() != null) {
							BigDecimal bbcPostage = new BigDecimal(
									ParametersUtil.getDoubleValue(order.getBbcPostage()));
							Double orderAmount = PriceFormatUtil
									.toFix2(new BigDecimal(order.getPurchaseTotalAmount()).add(bbcPostage));
							// 更新优惠码金额
							actualPay = actualPay.add(bbcPostage);
							httpService.updateCoupons(null, order.getCouponsCode(), null, null,
									PriceFormatUtil.toFix2(actualPay), orderAmount, null, null);
						}
					}
				} else {
					error = "订单金额不能为负数";
				}
			}
			res.put("suc", flag);
			res.put("msg", error != null ? error : "更新价格"
					+ (flag ? "成功" : "失败"));
		} catch (Exception e) {
			Logger.info("更新价格异常", e);
			res.put("suc", false);
			res.put("msg", "更新价格异常");
		}
		return res;
	}

	@Override
	public void markPro(PurchaseOrder event) {
		try {
			boolean isPermitted = event != null && StringUtils.isNotBlankOrNull(event.getEmail())
					&& StringUtils.isNotBlankOrNull(event.getPurchaseOrderNo())
					&& event.getPurchaseType() == PurchaseTypes.PURCHASE_ORDER_NORMAL && !event.getBackIn();
			if (!isPermitted) {
				return;
			}
			
			String email = event.getEmail();
			JsonNode user = httpService.getMemberInfo(email);
			Integer model = user.get("distributionMode").asInt();
			Integer type = user.get("comsumerType").asInt();
			boolean isModelPermitted =  (model != 1);
			if (!isModelPermitted) {
				return;
			}
			
			// 非电商模式 标记活动
			Map<String, Object> param = Maps.newHashMap();
			param.put("userMode", model);
			param.put("userAttr", type);
			Double amount = event.getPurchaseTotalAmount()
					+ ParametersUtil.getDoubleValue(event.getBbcPostage())
					- ParametersUtil.getDoubleValue(event.getCouponsAmount());
			param.put("money", amount);
			PurchaseOrderDetail detail = new PurchaseOrderDetail();
			detail.setPurchaseId(event.getId());
			List<PurchaseOrderDetail> details = purchaseOrderDetailMapper
					.getDetails(detail);
			List<Map<String, Object>> list = Lists.newArrayList();
			Map<String, Object> skus = null;
			Integer qty = 0;
			for (PurchaseOrderDetail de : details) {
				skus = Maps.newHashMap();
				skus.put("sku", de.getSku());
				skus.put("commodityCategoryId", de.getCategoryId());
				skus.put("warehouseId", de.getWarehouseId());
				skus.put("totalPrice", de.getTotalPrices());
				skus.put("number", de.getQty());
				qty += de.getQty();
				list.add(skus);
			}
			param.put("commodity", list);
			param.put("totalNumber", qty);
			JsonNode node = httpService.checkActive(param);
			if (node != null && node.size() > 0) {
				PurchaseOrder order = new PurchaseOrder();
				order.setId(event.getId());
				order.setIsPro(true);
				event.setIsPro(true);
				purchaseOrderMapper.updateByPrimaryKeySelective(order);
			}
		} catch (Exception e) {
			Logger.error("标记采购单活动异常：" + e.toString());
		}
	}

	@Override
	public void invalid(PurchaseOrder order) throws JsonProcessingException,
			IOException {
		// 修改失效逻辑
		JsonNode sm =  httpService.getSaleMain(order.getSid());
		String so = JsonCaseUtil.jsonToString(sm.get("salesOrderNo"));
		httpService.cancelSalesOrder(so);
		//关闭发货单
//		updateSalesStatus(order.getSid(), 5);
	}

	@Override
	public Map<String, Object> getMaketAct(String orderNo) {
		Map<String, Object> res = Maps.newHashMap();
		try {
			PurchaseOrder order = purchaseOrderMapper.getOrderByNo(orderNo);
			Map<Integer, JsonNode> actMap = Maps.newHashMap();
			// 含赠品活动集合
			List<Map<String, Object>> actList = Lists.newArrayList();
			if (order != null 
					&& order.getPurchaseType() == PurchaseTypes.PURCHASE_ORDER_NORMAL 
					&& order.getIsPro()
					&& !order.getIsChoose() && !order.getBackIn()) {
				Map<String, Object> param = Maps.newHashMap();
				String email = order.getEmail();
				String admin = userService.getAdminAccount();
				// 分销商 信息
				JsonNode info = httpService.getMemberInfo(email);
				// 获取模式
				Integer model = info.get("distributionMode").asInt();
				// 构造查询优惠参数
				List<PurchaseOrderDetail> details = purchaseOrderDetailMapper.getAlldetailsByPurNo(orderNo);
				List<Map<String, Object>> list = Lists.newArrayList();
				Map<String, Object> skus = null;
				Integer qty = 0;
				for (PurchaseOrderDetail de : details) {
					skus = Maps.newHashMap();
					skus.put("sku", de.getSku());
					skus.put("commodityCategoryId", de.getCategoryId());
					skus.put("warehouseId", de.getWarehouseId());
					skus.put("totalPrice", de.getTotalPrices());
					skus.put("number", de.getQty());
					qty += de.getQty();
					list.add(skus);
				}
				param.put("userMode", model);
				param.put("account", order.getEmail());
				param.put("userAttr", order.getDistributorType());
				param.put("commodity", list);
				param.put("totalNumber", qty);
				param.put("money", order.getPurchaseTotalAmount());
				param.put("paymentTime", order.getPayDate().getTime());// 毫秒值
				JsonNode act = httpService.checkActive(param);
				Integer actId = null;
				Map<String, Object> actIdMap = null;
				for (JsonNode a : act) {
					if (hasGift(a)) {
						actId = a.get("id").asInt();
						actIdMap = Maps.newHashMap();
						actIdMap.put("id", actId);
						actIdMap.put("name", a.get("name").asText());
						actList.add(actIdMap);
						actMap.put(actId, getGiftList(actId, param, admin));
					}
				}
			}
			res.put("suc", true);
			res.put("actList", actList);
			res.put("actMap", actMap);
		} catch (Exception e) {
			res.put("suc", false);
			res.put("msg", "获取优惠数据异常");
			Logger.info("获取优惠数据异常", e);
		}
		return res;
	}

	@Override
	public JsonNode getGiftList(Integer actId, Map<String, Object> param,
			String admin) {
		Map<String, Object> map = Maps.newHashMap();
		List<Map<String, Object>> giftList = Lists.newArrayList();
		String priviledgeid = "";
		// 优惠总数
		Integer total = 0;
		try {
			JsonNode ace = httpService.actExcute(actId, param);
			if (ace.get("suc").asBoolean()) {
				Map<String, Object> giftMap = null;
				List<String> skus = Lists.newArrayList();
				String key = null;
				for (JsonNode pri : ace.get("priList")) {
					priviledgeid += pri.get("id").asText() + ",";
				}
				for (JsonNode g : ace.get("gift")) {
					key = String.join("_", g.get("sku").asText(), String.valueOf(g.get("warehouseId").asInt()));
					total += g.get("num").asInt();
					if (skus.contains(key)) {
						continue;
					}
					skus.add(key);
					giftMap = Maps.newHashMap();
					giftMap.put("title", g.get("cTitle").asText());
					giftMap.put("price", 0.00);
					giftMap.put("marketPrice", g.get("marketPrice").asDouble());
					giftMap.put("qty", 0);
					giftMap.put("warehouseId", g.get("warehouseId").asInt());
					giftMap.put("warehouseName", g.get("warehouseName").asText());
					giftMap.put("sumPrice", 0.00);
					giftMap.put("publicImg", g.get("imgUrl").asText());
					giftMap.put("realPrice", g.get("realPrice").asDouble());
					giftMap.put("sku", g.get("sku").asText());
					giftMap.put("disStockId", g.get("warehouseId").asInt());
					giftMap.put("categoryId", JsonCaseUtil.jsonToInteger(g.get("categoryId")));
					giftMap.put("categoryName", JsonCaseUtil.jsonToString(g.get("categoryName")));
					giftMap.put("operatorEmail", admin);
					giftMap.put("isgift", true);
					giftList.add(giftMap);
				}
			}
		} catch (Exception e) {
			Logger.info("获取赠品信息异常", e);
		}
		map.put("priviledgeid", priviledgeid.substring(0, priviledgeid.lastIndexOf(",")));
		map.put("total", total);
		map.put("giftList", giftList);
		return Json.toJson(map);
	}

	/**
	 * 判断是否 是 有赠品的活动
	 * 
	 * @author zbc
	 * @since 2016年12月14日 下午5:02:31
	 */
	public boolean hasGift(JsonNode act) {
		// 优惠实例
		JsonNode priInst = null;
		boolean hasGift = false;
		for (JsonNode acInt : act.get("fullActInstDtoList")) {
			priInst = acInt.get("fullCondtInstDtoList").get(0).get("fullPvlgInstDto");
			if (priInst != null && (priInst.get("pType").asInt() == 1 || priInst.get("pType").asInt() == 4)) {
				hasGift = true;
			}
		}
		return hasGift;
	}

	/**
	 * 判断是否需要审核
	 * 
	 * @param purchaseOrder
	 * @return
	 */
	@Override
	public boolean checkNeedAuditOrNot(PurchaseOrder purchaseOrder) {
		// 是否享受了优惠/参加了促销
		// 前台：isChoose：参加了活动
		// 后台：getIsPro：true表示参加了活动、false没有参加活动；isChoose：true选择了赠品；false待选赠品
		boolean hasPrivilleges = purchaseOrder.getIsPro() && !purchaseOrder.getIsChoose();
		// 计算总的到仓价
		Double totalArriveWarehousePrice = getTotalArriveWarehousePrice(purchaseOrder.getPurchaseOrderNo());
		// 优惠金额
		Double couponsAmount = purchaseOrder.getCouponsAmount() != null ? purchaseOrder.getCouponsAmount() : 0.00;
		Double purchaseTotalAmount = new BigDecimal(purchaseOrder.getPurchaseTotalAmount())
				.subtract(new BigDecimal(couponsAmount)).doubleValue();
		// 订单金额小于到仓价？true亏本，false不亏本
		boolean loseMoney = purchaseTotalAmount < totalArriveWarehousePrice;
		return (hasPrivilleges || loseMoney);
	}

	@Override
	public ReturnMess payedByCash(JsonNode json) {
		ReturnMess rm = new ReturnMess("1", "更新订单相关信息失败");
		if(null == json) {
			return rm;
		}
		try {
			// 获取订单信息
			JsonNode purchaseOrderJson = json.path("purchaseOrder");
			// 获取订单详情信息
			JsonNode purchaseOrderDetailJson = json.path("purchaseOrderDetail");
			boolean updateOrderFlag = true;
			boolean updateOrderDetailFlag = true;
			PurchaseOrder purchaseOrder = null;
			if (purchaseOrderJson != null) {
				purchaseOrder = Json.fromJson(purchaseOrderJson, PurchaseOrder.class);
				purchaseOrder.setPayDate(new Date());
				// 更新
				updateOrderFlag = updatePurchaseOrder(purchaseOrder);
				// add by zbc 现金交易要标记活动
				markPro(purchaseOrderMapper.selectByPrimaryKey(purchaseOrder.getId()));
				if (!updateOrderFlag) {
					String orderMsg = "更新订单信息失败：" + purchaseOrder.toString();
					Logger.error(orderMsg);
				}
			}
			if (purchaseOrderDetailJson != null && purchaseOrderDetailJson.size() > 0) {
				PurchaseOrderDetail purchaseOrderDetail = Json.fromJson(purchaseOrderDetailJson,
						PurchaseOrderDetail.class);
				updateOrderDetailFlag = purchaseOrderDetailService.updateDetail(purchaseOrderDetail);
				if (!updateOrderDetailFlag) {
					String orderDetailMsg = "更新订单详情信息失败：" + purchaseOrderDetail.toString();
					Logger.error(orderDetailMsg);
				}
			}
			if (updateOrderDetailFlag && updateOrderFlag) {
				rm = new ReturnMess("0",
						"更新订单相关信息成功,order:" + purchaseOrderJson + "   detail:" + purchaseOrderDetailJson);
			}
		} catch (Exception e) {
			Logger.info("更新订单相关信息异常", e);
		}
		return rm;
	}

	/**
	 * 根据email更改订单上的昵称
	 */
	@Override
	public String changeNickNameByEmail(String param) {
		JsonNode json = Json.parse(param);
		ObjectNode result = Json.newObject();
		PurchaseOrder order = new PurchaseOrder();
		order.setEmail(json.get("email").asText());
		order.setNickName(json.get("nickName").asText());
		int flag = purchaseOrderMapper.updateNickNameByEmail(order);
		Logger.info("changeNickNameByEmail----->{}", flag);
		if (flag == 0) {
			result.put("suc", false);
			result.put("msg", "更新失败");
			return result.toString();
		}
		
		result.put("suc", true);
		result.put("data", flag);
		return result.toString();
	}

	/**
	 * 
	 * { "orderNo": "CG201612240935", "account": "854287501@qq.com",
	 * "saleOrderNo": "XS201612221544001", //缺货采购单销售单号，普通采购可以不传 "pros": [ {
	 * "sku": "IF968-1", "qty": 10, "purchasePrice": 55.79, "isGift": 0,
	 * "warehouseId": 2024, "warehouseName": "深圳仓", "imgUrl": "www.google.com",
	 * "productTitle": "泰国日清巧克力味威化饼干100g", "capfee": 55.79 }, { "sku":
	 * "IF968-2", "qty": 10, "purchasePrice": 55.79, "isGift": 0, "warehouseId":
	 * 2024, "warehouseName": "深圳仓", "imgUrl": "www.google.com", "productTitle":
	 * "泰国日清巧克力味威化饼干200g", "capfee": 55.79 } ] }
	 * 
	 * @author zbc
	 * @since 2016年12月26日 下午5:32:18
	 */
	@Override
	public Map<String, Object> lock(String orderNo) {
		Map<String, Object> res = Maps.newHashMap();
		PurchaseOrder order = purchaseOrderMapper.getOrderByNo(orderNo);
		if(order==null){
			res.put("suc", false);
			res.put("msg", "订单不存在");
			return res;
		}
		
		StringBuilder msgBuilder = new StringBuilder();
		List<PurchaseOrderDetail> list = purchaseOrderDetailMapper.getAlldetailsByPurNo(orderNo);
		List<CloudLockPro> lockList = Lists.newArrayList();
		// 查询商品价格
		Map<String, Double> priceMap = Maps.newHashMap();
		
		try {
			// 查询到仓价
			getArwPrice(list, priceMap);
			getLockDetails(list, priceMap, lockList);
			InventoryCloudLockDto lockDto = new InventoryCloudLockDto(orderNo, order.getEmail(), order.getNickName(), lockList, null);
			// 如果为缺货采购
			if (order.getPurchaseType() == PurchaseTypes.PURCHASE_ORDER_STOCKOUT) {
				JsonNode saleNode = httpService.getSaleMain(order.getSid());
				lockDto.setSaleOrderNo( saleNode.get("salesOrderNo").asText());
			}
			JsonNode resNode = httpService.cloudLock(lockDto);
			Logger.info("云仓锁库结果:[{}]", resNode);
			boolean suc = resNode.get("result").asBoolean();
			msgBuilder.append(JsonCaseUtil.jsonToString(resNode.get("msg")));
			if (!suc) {
				for (JsonNode obj : resNode.get("objList")) {
					msgBuilder.append("[").append(obj.get("sku").asText()).append("缺").append(obj.get("stock").asInt()).append("]");
				}
			}
			res.put("suc", suc);
			res.put("msg", msgBuilder.toString());
			return res;
		} catch (Exception e) {
			Logger.info("云仓库存锁定异常", e);
			res.put("suc", false);
			res.put("msg", "云仓库存锁定异常");
			return res;
		}
		
	}

	/**
	 * 获取到仓价
	 * 
	 * @author zbc
	 * @since 2016年12月27日 下午4:59:52
	 */
	private void getArwPrice(List<PurchaseOrderDetail> list,
			Map<String, Double> priceMap) throws JsonProcessingException,
			IOException {
		List<String> skus = Lists.transform(list, f -> f.getSku());
		JsonNode priceNode = httpService.getPriceList(skus);
		if (priceNode.get("suc").asBoolean()) {
			for (JsonNode price : priceNode.get("pages").get("list")) {
				String key = String.join("_", price.get("sku").asText(), String.valueOf(price.get("disStockId").asInt()));
				priceMap.put(key, JsonCaseUtil.jsonToDouble(price.get("arriveWarePrice")));
			}
		}
	}

	@Override
	public List<PurchaseOrderDetail> caculateCapFee(PurchaseOrderDto event) {
		/*
		 * 满减、折扣活动 ① 计算订单不参加促销活动金额； ② 计算订单参加促销活动金额； ③
		 * 均摊价：商品采购价（订单生成时当前分销商采购该商品的价格）*订单参加促销活动金额/订单不参加促销活动金额。
		 */
		PurchaseOrderDetail detail = new PurchaseOrderDetail();
		detail.setPurchaseId(event.getId());
		List<PurchaseOrderDetail> details = purchaseOrderDetailMapper
				.getDetails(detail);
		// 目前均摊价要 赠品也需要均摊
		Double amount = new BigDecimal(event.getPurchaseTotalAmount())
				.subtract(new BigDecimal(ParametersUtil.getDoubleValue(event.getCouponsAmount()))).doubleValue();
		caculateDetails(amount, details);
		purchaseOrderDetailMapper.batchUpdateCapFee(details);
		return details;
	}

	@Override
	public void caculateDetails(Double amount, List<PurchaseOrderDetail> details) {
		BigDecimal proTotal = PriceFormatUtil.setScale2(details.stream().map(de -> {
			return new BigDecimal(de.getRealPrice()).multiply(new BigDecimal(de.getQty()));
		}).reduce(new BigDecimal(0), (x, y) -> x.add(y)));

		// 商品数量
		int proCount = details.size();
		// 均摊价格总计
		Double capTatol = 0.00;
		for (PurchaseOrderDetail de : details) {
			// 正价商品 假设更新均摊价格到disprice 后续只需要批量更新详情就可以
			proCount--;
			// 如果支付价格 小于等于0 均摊价格则为零
			if (amount <= 0) {
				de.setCapFee(0.00);
			} else {
				if (proCount <= 0) {
					de.setCapFee(PriceFormatUtil.toFix2(new BigDecimal(amount).subtract(new BigDecimal(capTatol))
							.divide(new BigDecimal(de.getQty()), 10, BigDecimal.ROUND_HALF_UP)));
				} else {
					de.setCapFee(PriceFormatUtil.toFix2(new BigDecimal(de.getRealPrice())
							.divide(proTotal, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(amount))));
					capTatol += PriceFormatUtil
							.toFix2(new BigDecimal(de.getCapFee()).multiply(new BigDecimal(de.getQty())));
				}
			}
		}
	}

	/* change by zbc 
	 * KA 锁库逻辑修改
	 * @Override
	public void changeInventoryCafee(PurchaseOrderDto event,
			List<PurchaseOrderDetail> details) {
		try {
			// 查询商品价格
			Map<String, Double> priceMap = Maps.newHashMap();
			getArwPrice(details, priceMap);
			//TODO 修改均摊价
			List<CloudLockPro> lockList = Lists.newArrayList();
			// 查询商品价格
			getLockDetails(details, priceMap, lockList);
			Map<String, Object> postMap = Maps.newHashMap();
			postMap.put("orderNo", event.getPurchaseOrderNo());
			postMap.put("account", event.getEmail());
			postMap.put("pros", lockList);
			postMap.put("accountName", event.getNickName());
			httpService.changeInventoryCafee(postMap);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("更新云仓锁库信息（caFee）异常。" + e);
		}
	}*/
	
	private static String getKey(Object... obj){
		StringBuilder builder = new StringBuilder();
		for(int i = 0;i< obj.length;i++){
			if(i == 0){
				builder.append(obj[i]!= null?obj[i]:"--");
			}else{
				builder.append("||").append(obj[i]!= null?obj[i]:"--");
			}
		}
		return builder.toString();
	}

	@Override
	public void changeInventoryCafee(PurchaseOrderDto event,
			List<PurchaseOrderDetail> details) {
		try {
			Map<String,Double> capFeeMap = Maps.newHashMap();
			details.forEach(d->{
				capFeeMap.put(getKey(d.getSku(),d.getWarehouseId(),d.getIsgift(),d.getExpirationDate()), d.getCapFee());
			});
			ObjectMapper map = new ObjectMapper();
			List<OrderDetail> ds = map.readValue(httpService.getOrderDetails(event.getPurchaseOrderNo()).toString(), new TypeReference<List<OrderDetail>>() {});
			List<CloudLockPro> lockList = Lists.transform(ds, d->{
				return new CloudLockPro(d,capFeeMap.get
						(getKey(d.getSku(),d.getWarehouseId(),(d.getIsGift() != null&& d.getIsGift() == 1),d.getExpirationDate())));
			});
			// 查询商品价格
			Map<String, Object> postMap = Maps.newHashMap();
			postMap.put("orderNo", event.getPurchaseOrderNo());
			postMap.put("account", event.getEmail());
			postMap.put("pros", lockList);
			postMap.put("accountName", event.getNickName());
			httpService.changeInventoryCafee(postMap);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("更新云仓锁库信息（caFee）异常。" + e);
		}
	}

	@Override
	public String savePurchaseStockout(String string) {
		boolean suc = false;
		try {
			JsonNode json = Json.parse(string);
			String pNo = JsonCaseUtil.jsonToString(json.get("pno"));
			String jsonStr = JsonCaseUtil.jsonToString(json.get("jsonStr"));
			suc = savePurchaseStockout(pNo, jsonStr);
		} catch (Exception e) {
			Logger.info("保存采购单发货信息数据异常:{}",e);
		}
		return Json.newObject().put("suc", suc).put("msg", "保存采购单发货信息数据"+(suc?"成功":"失败")).toString();
	}
	@Override
	public JsonResult<?> changeFreight(String string) {
		try {
			JsonNode json = Json.parse(string);
			String pno = null;
			PurchaseOrder order = null;
			for(JsonNode item:json){
				pno = JsonCaseUtil.jsonToString(item.get("pno"));
				order = purchaseOrderMapper.getOrderByNo(pno);
				order.setBbcPostage(JsonCaseUtil.jsonToDouble(item.get("freight")));
				purchaseOrderMapper.updateByPrimaryKeySelective(order);
			}
			return JsonResult.newIns().result(true).msg("更新运费成功");
		} catch (Exception e) {
			Logger.info("更新运费异常:{}",e);
			return JsonResult.newIns().result(false).msg("更新运费异常");
		}
	}
	
	@Override
	public List<OrderOperateRecord> orderOperateRecord(String purchaseNo) {
		return auditLogMapper.getOperateRecord(purchaseNo);
	}

	@Override
	public ViewPurchaseOrder purchaseSimpleInfo(JsonNode paramNode, List<String> accounts) {
		ViewPurchaseIterm viewPurchaseIterm = queryPurchases(paramNode, accounts);
		return viewPurchaseIterm == null || viewPurchaseIterm.getOrders().size() == 0 ? new ViewPurchaseOrder() : viewPurchaseIterm.getOrders().get(0);
	}
}
