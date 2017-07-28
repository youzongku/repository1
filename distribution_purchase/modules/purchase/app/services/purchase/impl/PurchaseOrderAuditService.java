package services.purchase.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import dto.purchase.InStorageIterm;
import entity.purchase.PurchaseActive;
import entity.purchase.PurchaseAudit;
import entity.purchase.PurchaseOrder;
import entity.purchase.PurchaseOrderAuditLog;
import entity.purchase.PurchaseStockout;
import forms.purchase.CustomerServiceAuditParam;
import forms.purchase.FinanceAuditParam;
import mapper.purchase.PurchaseAuditMapper;
import mapper.purchase.PurchaseOrderAuditLogMapper;
import mapper.purchase.PurchaseOrderMapper;
import mapper.purchase.PurchaseStockoutMapper;
import play.Logger;
import play.libs.Json;
import services.purchase.IHttpService;
import services.purchase.IPurchaseOrderAuditService;
import services.purchase.IPurchaseOrderService;
import services.purchase.IUserService;
import utils.purchase.DateUtils;
import utils.purchase.HttpUtil;
import utils.purchase.JsonCaseUtil;
import utils.purchase.PriceFormatUtil;
import utils.purchase.PurchaseAuditStatus;
import utils.purchase.PurchaseOrderStatus;
import utils.purchase.PurchaseTypes;
import utils.purchase.StringUtils;

/**
 * 采购单审核service实现，关于审核的业务都放在这里
 * 
 * @author huangjc
 * @date 2016年12月1日
 */
public class PurchaseOrderAuditService implements IPurchaseOrderAuditService {
	
	@Inject private IPurchaseOrderService purchaseOrderService;
	@Inject private PurchaseOrderMapper purchaseOrderMapper;
	@Inject private PurchaseStockoutMapper purchaseStockoutMapper;
	@Inject private	PurchaseOrderAuditLogMapper auditLogMapper;
	@Inject private IHttpService httpService;
	@Inject private PurchaseAuditMapper auditMapper;
	@Inject private EventBus ebus;
	@Inject private IUserService userService;
	
	@Override
	public List<PurchaseOrderAuditLog> getAOrderAllAuditLogs(String purchaseOrderNo){
		return auditLogMapper.selectAOrderAllLogsByAuditType(purchaseOrderNo, null);
	}
	
	@Override
	public PurchaseOrderAuditLog getOrderAuditLastestLog(String purchaseOrderNo){
		return auditLogMapper.selectAOrderLastestLog(purchaseOrderNo);
	}

	/**
	 * 判断是否是现金交易 和线下转账
	 * @author zbc
	 * @since 2016年12月16日 下午12:45:26
	 */
	private boolean cashOrOfflinePay(String payType){
		return payType!=null&&(("cash-noline".equals(payType)||"cash".equals(payType)));
	}
	
	@Override
	public Map<String,Object> auditByCustomerService(JsonNode node) {
		HashMap<String, Object> res = Maps.newHashMap();
		try {
			String orderNo = node.get("purchaseOrderNo").asText();
			CustomerServiceAuditParam param = new CustomerServiceAuditParam();
			param.setPassed(node.get("passed").asInt() == 0 ? false : true);
			param.setRemark(JsonCaseUtil.jsonToString(node.get("remark")));
			param.setPurchaseOrderNo(orderNo);
			String admin = userService.getAdminAccount();
			param.setAuditUser(admin);
			PurchaseOrder po = purchaseOrderMapper.getOrderByNo(orderNo);
			if(po.getStatus() != PurchaseOrderStatus.AUDIT_WAINTING_CUSTOMER_SERVICE){
				res.put("suc", false);
				res.put("msg", "该订单不是待客服确认状态,不能进行客服确认,请刷新页面!");
				return res;
			}
			
			String msg = null;
			if (param.isPassed()) {
				msg = "审核";
				// 更新订单数据 
				//查看是否有赠品
				if(node.has("giftMap")){
					JsonNode giftMap = node.get("giftMap");
					JsonNode giftList = giftMap.get("giftList");
					
					//============更新订单数据==========//
					Map<String,Object> addGift = Maps.newHashMap();
					addGift.put("pros", giftList);
					addGift.put("orderNo", orderNo);
					InStorageIterm item = purchaseOrderService.addGift(Json.toJson(addGift));
					
					if(!"0".equals(item.getReturnMess().getErrorCode())){
						res.put("suc", false);
						res.put("msg", item.getReturnMess().getErrorInfo());
						return res;
					}
					
					//=============保存选增记录===============//
					PurchaseActive pa = new PurchaseActive();
					pa.setOrderno(orderNo);
					pa.setActiveid(giftMap.get("actId").asInt());
					pa.setPriviledgeid(giftMap.get("priviledgeid").asText());
					purchaseOrderMapper.insertActivities(pa);
					httpService.syncActiveLog(pa, admin, po.getPurchaseTotalAmount(), orderNo);
				}
				
				po = purchaseOrderMapper.getOrderByNo(orderNo);
				if(po.getIsPro()){
					po.setIsChoose(true);
				}
				
				// 线下支付  现金交易  判断是否亏损  是:财务审核 否 直接完成   确认通过
				if (cashOrOfflinePay(po.getPaymentId()) || purchaseOrderService.checkNeedAuditOrNot(po)) {
					po.setStatus(PurchaseOrderStatus.AUDIT_WAINTING_FINANCE);
				} else {
					//============更新云仓库存==========//
					po.setStatus(PurchaseOrderStatus.FINISHED);
					po.setFinanceMoney(po.getOfflineMoney());
					purchaseOrderService.getInfoByNo(orderNo, "SUCCESS");
					try {
						// change by zbc 新更新库存 
						httpService.updateStock(po.getPurchaseOrderNo());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					// 判断是否要整批出库
					PurchaseStockout stockout = purchaseStockoutMapper.selectStockout(po.getPurchaseOrderNo(), 0);
					if(stockout!=null){
						purchaseOrderService.stockout(stockout);
					}
				}
				// 状态变为待财务审核
				po.setRemark(param.getRemark());
				purchaseOrderMapper.updateByPrimaryKeySelective(po);
				// 记录日志
				saveAuditLog(param.getPurchaseOrderNo(), po.getStatus(), param.getAuditUser(),
						Json.toJson(param).toString(), PurchaseOrderAuditLog.AUDIT_TYPE_CS);
			} else {
				msg = "关闭订单";
				// 确认关闭
				// 状态变为已取消
				po.setStatus(PurchaseOrderStatus.CANCEL);
				po.setRemark(param.getRemark());
				purchaseOrderMapper.updateByPrimaryKeySelective(po);
				// 余额和线上第三方有退钱
				boolean canReturnMoney = !"cash-noline".equals(po.getPaymentId())
						&& !"cash".equals(po.getPaymentId());
				if (canReturnMoney) {
					BigDecimal moneyReturn = new BigDecimal(0);
					if(po.getOfflineMoney()!=null && po.getOfflineMoney()>0){
						moneyReturn = moneyReturn.add(new BigDecimal(po.getOfflineMoney()));
					}
					// 处理缺货采购单
					if (po.getPurchaseType() == 2) {
						// 支付的钱退回余额，销售单变为已取消
						if(po.getBbcPostage()!=null && po.getBbcPostage()>0){
							moneyReturn = moneyReturn.add(new BigDecimal(po.getBbcPostage()));
						}
						try {
							httpService.restoreMicro(po.getSid(),param.getAuditUser());
						} catch (IOException e) {
							Logger.info("取消销售单失败，销售单id");
							e.printStackTrace();
						}
					}else{
						moneyReturn = moneyReturn.add(new BigDecimal(po.getOfflineMoney()));
					}
					if(moneyReturn.doubleValue()>0){
						try {
							httpService.offlineTransferExtraMoney2Balance(po.getEmail(), po.getPurchaseOrderNo(), po.getOfflineMoney());
						} catch (IOException e) {
							Logger.info("客服审核关闭，退钱失败！采购单号：{}",po.getPurchaseOrderNo());
							e.printStackTrace();
						}
					}
				}
				//解除库存锁定
				httpService.unLock(param.getPurchaseOrderNo());
				// 记录日志
				saveAuditLog(param.getPurchaseOrderNo(), PurchaseOrderStatus.CANCEL, param.getAuditUser(),
						Json.toJson(param).toString(), PurchaseOrderAuditLog.AUDIT_TYPE_CS);
			}
			res.put("suc", true);
			res.put("msg", msg+"成功");
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.info("客服审核异常",e);
			res.put("suc", false);
			res.put("msg", "客服审核异常");
			return res;
		}
	}

	@Override
	public Map<String, Object> auditByFinance(FinanceAuditParam param) {
		// 支付&利润通过
		boolean passed = param.getPaid() != 0 && param.isProfitPassed();
		String orderNo = param.getPurchaseOrderNo();
		PurchaseOrder purchaseOrder = purchaseOrderMapper.getOrderByNo(orderNo);
		if(purchaseOrder.getStatus() != PurchaseOrderStatus.AUDIT_WAINTING_FINANCE){
			HashMap<String, Object> resultMap = Maps.newHashMap();
			resultMap.put("suc", false);
			resultMap.put("msg", "该订单不是待财务确认状态，不能进行财务确认,请刷新页面!");
			return resultMap;
		}
		// 查询线下转账记录
		PurchaseAudit purchaseAudit = auditMapper.selectByPurchaseOrderNo(orderNo);
		// 是否是线下转账
		boolean isTransferOffline = purchaseAudit != null;
		if (!passed) {
			Logger.info("财务审核不通过");

			// 线下转账
			if (isTransferOffline) {
				purchaseAudit.setStatus(PurchaseAuditStatus.WAINTING_AUDIT);
				purchaseAudit.setAuditRemark(param.getProfitRemark());
				purchaseAudit.setAuditReasons(param.getAuditReason());
				auditMapper.updateByPrimaryKeySelective(purchaseAudit);
			}

			// 打回待客服确认
			PurchaseOrder po = new PurchaseOrder();
			po.setPurchaseOrderNo(param.getPurchaseOrderNo());
			// 财务审核不通过，即是待客服确认
			po.setStatus(PurchaseOrderStatus.AUDIT_WAINTING_CUSTOMER_SERVICE);
			po.setRejectedByFinance(true);
			po.setRemark(param.getProfitRemark());
			po.setReason(param.getAuditReason());
			purchaseOrderMapper.updateSelective(po);

			// 记录日志
			saveAuditLog(param.getPurchaseOrderNo(),
					PurchaseOrderStatus.AUDIT_WAINTING_CUSTOMER_SERVICE,
					param.getAuditUser(), Json.toJson(param).toString(), PurchaseOrderAuditLog.AUDIT_TYPE_FINANCE);

			HashMap<String, Object> resultMap = Maps.newHashMap();
			resultMap.put("suc", true);
			resultMap.put("msg", "财务确认不通过");
			return resultMap;
		}

		// 查询采购单
		// 财务审核通过，线下转账和现金交易的逻辑不一样
		purchaseOrder.setRejectedByFinance(false);
		if (isTransferOffline || "cash-noline".equals(purchaseOrder.getPaymentId())) {// 线下转账
			Logger.info("审核通过-线下转账--{}",purchaseOrder.getPurchaseOrderNo());
			
			double transferAmount = PriceFormatUtil.toFix2(purchaseAudit.getTransferAmount());
			Logger.info("审核通过-线下转账--金额对比：receivedAmount={}，transferAmount={}",param.getReceivedAmount(),transferAmount);
			if (param.getReceivedAmount() < transferAmount) {
				Logger.info("实收金额必须大于等于付款金额");
				HashMap<String, Object> resultMap = Maps.newHashMap();
				resultMap.put("suc", false);
				resultMap.put("msg", "实收金额必须大于等于付款金额");
				return resultMap;
			}
			
			// 判断金额是否够欠的钱和订单的金额
			if (!checkMoneyEnough4DebtAndOrder(purchaseOrder,
					param.getReceivedAmount())) {
				HashMap<String, Object> resultMap = Maps.newHashMap();
				resultMap.put("suc", false);
				resultMap.put("msg", "有欠款，请先还欠款");
				return resultMap;
			}
			// 确认通过
			return auditPassedByFinance_transferOffline(param, purchaseAudit, purchaseOrder);
		} 
		
		if("cash".equals(purchaseOrder.getPaymentId())) {// 现金交易
			Logger.info("审核通过-现金交易--{}",purchaseOrder.getPurchaseOrderNo());
			Logger.info("审核通过-线下转账--金额对比：receivedAmount={}，offlineMoney={}",param.getReceivedAmount(),purchaseOrder.getOfflineMoney());
			if (param.getReceivedAmount() < purchaseOrder.getOfflineMoney()) {
				Logger.info("实收金额必须大于等于付款金额");
				HashMap<String, Object> resultMap = Maps.newHashMap();
				resultMap.put("suc", false);
				resultMap.put("msg", "实收金额必须大于等于付款金额");
				return resultMap;
			}
		}
		
		return auditPassedByFinance(param, purchaseOrder);
	}
	
	/**
	 * 检查钱是否够订单金额和负债
	 * @param purchaseOrder 采购单
	 * @param receivedAmount 输入的金额
	 * @return
	 */
	private boolean checkMoneyEnough4DebtAndOrder(PurchaseOrder purchaseOrder,
			double receivedAmount) {
		Logger.info("检查钱是否够订单金额和负债");
		try {
			String debtResult = httpService.getDebt(purchaseOrder.getEmail());
			JsonNode jsonNode = Json.parse(debtResult);
			// 有欠款才要判断
			double debt = jsonNode.get("debt").asDouble();
			if (jsonNode.get("suc").asBoolean() && debt > 0) {
				double purchaseFinalAmount = purchaseOrder.calculatePurchaseFinalAmount();
				Logger.info("实收金额：{}，欠款金额：{}，订单金额：{}", receivedAmount, debt,
						purchaseFinalAmount);
				// 欠款和订单金额
				double debtAndOrderAmount = PriceFormatUtil.toFix2((new BigDecimal(debt)
						.add(new BigDecimal(purchaseFinalAmount))));
				if (receivedAmount < debtAndOrderAmount) {
					// 金额不够欠款和订单金额
					Logger.info("金额不够欠款和订单金额");
					return false;
				}
				Logger.info("金额够欠款和订单金额");
				return true;
			}
		} catch (IOException e) {
			Logger.info("获取欠款失败，email = {}", purchaseOrder.getEmail());
			e.printStackTrace();
		}
		Logger.info("没有欠款");
		return true;
	}
	
	/**
	 * 财务审核通过-现金交易和线上支付的
	 * 
	 * @param param
	 * @param purchaseAudit
	 * @return
	 */
	private Map<String, Object> auditPassedByFinance(
			FinanceAuditParam param, PurchaseOrder order) {
		Logger.info("财务审核通过");
		HashMap<String, Object> newHashMap = Maps.newHashMap();

		// 支付成功，更改采购单状态
		// update采购单状态为已完成
		order.setStatus(PurchaseOrderStatus.FINISHED);
		order.setFinanceMoney(param.getReceivedAmount());
		order.setRemark(param.getProfitRemark());
		try {
			if(param.getReceivedTime()!=null){
				order.setPayDate(DateUtils.string2date(param.getReceivedTime(), DateUtils.FORMAT_FULL_DATETIME));
			}
		} catch (ParseException e) {
			Logger.info("到账时间类型转换失败！");
			e.printStackTrace();
		}
		purchaseOrderMapper.updateByPrimaryKeySelective(order);
		
		try {
			// 处理缺货采购单
			if(order.getPurchaseType()==PurchaseTypes.PURCHASE_ORDER_STOCKOUT){
				Logger.info("缺货采购单{}-更新微仓库存",order.getPurchaseOrderNo());
				processStockoutPurchaseOrder(order, null);
			}else{
				// change by zbc 新更新库存 
				httpService.updateStock(order.getPurchaseOrderNo());
			}
		} catch (Exception e) {
			Logger.info("更新微仓库存失败！");
			e.printStackTrace();
		}
		// 判断是否要整批出库
		PurchaseStockout stockout = purchaseStockoutMapper.selectStockout(order.getPurchaseOrderNo(), 0);
		if(stockout!=null){
			purchaseOrderService.stockout(stockout);
		}
		// add by xuse 标记有赠品的订单
		ebus.post(order);
		// 记录日志
		saveAuditLog(param.getPurchaseOrderNo(), PurchaseOrderStatus.FINISHED,
				param.getAuditUser(), Json.toJson(param).toString(), PurchaseOrderAuditLog.AUDIT_TYPE_FINANCE);

		newHashMap.put("suc", true);
		newHashMap.put("msg", "财务确认通过");
		return newHashMap;
	}

	/**
	 * 财务审核通过-线下交易
	 * 
	 * @param param
	 * @param purchaseAudit
	 * @return
	 */
	private Map<String, Object> auditPassedByFinance_transferOffline(
			FinanceAuditParam param, PurchaseAudit purchaseAudit, PurchaseOrder order) {
		Logger.info("财务审核通过");
		HashMap<String, Object> newHashMap = Maps.newHashMap();
		
		purchaseAudit.setReceivedAmount(new BigDecimal(param.getReceivedAmount()));
		try {
			if(StringUtils.isNotBlankOrNull(param.getReceivedTime())){
				purchaseAudit.setReceivedTime(
						DateUtils.string2date(param.getReceivedTime(), DateUtils.FORMAT_FULL_DATETIME));
			}
		} catch (ParseException e) {
			Logger.info("到账时间类型转换失败！");
			e.printStackTrace();
		}
		purchaseAudit.setAuditRemark(param.getProfitRemark());
		purchaseAudit.setUpdateDate(new Date());
		// 线下转账记录状态为审核通过
		purchaseAudit.setStatus(PurchaseAuditStatus.AUDIT_PASSED);
		auditMapper.updateByPrimaryKeySelective(purchaseAudit);

		// 调用member模块接口，生成充值记录，交易记录，并用余额支付采购单
		JsonNode request = Json.toJson(purchaseAudit);
		// 操作人
		((ObjectNode) request).put("operator", param.getAuditUser());
		String response_data = HttpUtil.post(request.toString(),
				HttpUtil.B2BBASEURL + "/member/addOfflineApply");
		Logger.debug("auditApply    response_data----->" + response_data);
		// 解析返回值
		JsonNode response = Json.parse(response_data);

		if (response.get("suc").asBoolean()) {
			if (response.get("pay").asBoolean()) {
				// 支付成功，更改采购单状态
				String payTime = response.get("payTime").asText();
				// update采购单状态为已完成
				order.setStatus(PurchaseOrderStatus.FINISHED);
				try {
					order.setPayDate(DateUtils.string2date(payTime, DateUtils.FORMAT_FULL_DATETIME));
				} catch (ParseException e) {
					Logger.info("支付时间【payTime=" + payTime + "】类型转换失败！");
					e.printStackTrace();
				}
				order.setFinanceMoney(param.getReceivedAmount());
				order.setRemark(param.getProfitRemark());
				purchaseOrderMapper.updateByPrimaryKeySelective(order);
				
				try {
					// 处理缺货采购单
					if(order.getPurchaseType()==PurchaseTypes.PURCHASE_ORDER_STOCKOUT){
						Logger.info("缺货采购单{}-更新微仓库存",order.getPurchaseOrderNo());
						processStockoutPurchaseOrder(order, null);
					}else{
						try {
							// change by zbc 新更新库存 
							httpService.updateStock(order.getPurchaseOrderNo());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					Logger.info("更新微仓库存失败！");
					e.printStackTrace();
				}
				// 判断是否要整批出库
				PurchaseStockout stockout = purchaseStockoutMapper.selectStockout(order.getPurchaseOrderNo(), 0);
				if(stockout!=null){
					purchaseOrderService.stockout(stockout);
				}
				// 记录日志
				saveAuditLog(param.getPurchaseOrderNo(),
						PurchaseOrderStatus.FINISHED, param.getAuditUser(),
						Json.toJson(param).toString(), PurchaseOrderAuditLog.AUDIT_TYPE_FINANCE);
			}
		}

		newHashMap.put("suc", true);
		newHashMap.put("msg", "财务确认通过");
		return newHashMap;
	}

	/**
	 * 记录日志
	 * 
	 * @param purchaseOrderNo
	 * @param status
	 * @param auditUser
	 * @param jsonValue
	 */
	private void saveAuditLog(String purchaseOrderNo, int status,
			String auditUser, String jsonValue, int auditType) {
		PurchaseOrderAuditLog auditLog = new PurchaseOrderAuditLog();
		auditLog.setStatus(status);
		auditLog.setPurchaseNo(purchaseOrderNo);
		auditLog.setAuditUser(auditUser);
		auditLog.setJsonValue(jsonValue);
		auditLog.setAuditType(auditType);
		auditLogMapper.insertOrderAuditLog(auditLog);
	}
	
	/**
	 * 审核通过处理缺货采购单
	 * @param purchaseOrder
	 * @param actualAmount
	 * @throws Exception
	 */
	private void processStockoutPurchaseOrder(PurchaseOrder purchaseOrder,
			Double actualAmount) throws Exception{
		if(purchaseOrder.getPurchaseType()==2){
			Logger.info("处理缺货采购单{}",purchaseOrder.getPurchaseOrderNo());
			
			JsonNode dismemberByEmail = httpService
					.getDismemberByEmail(purchaseOrder.getEmail());
			
			if (!dismemberByEmail.get("suc").asBoolean()) {
				Logger.info("更新采购单状态时，查询分销商失败！采购单号：{}", purchaseOrder.getPurchaseOrderNo());
			}
			// 获取销售发货单
			Integer saleOrderId = purchaseOrder.getSid();
			// 缺货采购单状态
			Integer statusPurchaseOrder = purchaseOrder.getStatus();
			// 缺货采购单待审核，销售单也要为待审核
			if (statusPurchaseOrder == PurchaseOrderStatus.FINISHED) {
				Logger.info("缺货采购单{}对应的销售单id={}的状态为待发货",purchaseOrder.getPurchaseOrderNo(),saleOrderId);
				// 先入库后出库
				purchaseOrderService.stockInThenOut(purchaseOrder);
				// add by zbc 完成发货单
				httpService.finishSaleOrder(saleOrderId, actualAmount, true,
						DateUtils.date2string(purchaseOrder.getPayDate(), DateUtils.FORMAT_FULL_DATETIME),purchaseOrder.getTransactionId(),purchaseOrder.getPaymentId());
			}
		}
	}
}
