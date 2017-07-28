package services.sales.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;

import dto.JsonResult;
import dto.sales.PostSaleOrderDto;
import dto.sales.PostSalePro;
import dto.sales.SaleBeforeCombineDto;
import dto.sales.SalesPriceDto;
import dto.sales.hb.CombinedSalesPageQueryParam;
import dto.sales.hb.HBDeliveryAuditParam;
import dto.sales.hb.SalesHBDeliveryDto;
import entity.sales.OperateRecord;
import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleInvoice;
import entity.sales.SaleMain;
import entity.sales.hb.SalesHBDelivery;
import entity.sales.hb.SalesHBDeliveryDetail;
import entity.sales.hb.SalesHBDeliveryLog;
import events.sales.CaculateChargeEvent;
import events.sales.ChangePurchaseFeightEvent;
import mapper.sales.OperateRecordMapper;
import mapper.sales.SaleBaseMapper;
import mapper.sales.SaleDetailMapper;
import mapper.sales.SaleInvoiceMapper;
import mapper.sales.SaleMainMapper;
import mapper.sales.hb.SalesHBDeliveryDetailMapper;
import mapper.sales.hb.SalesHBDeliveryLogMapper;
import mapper.sales.hb.SalesHBDeliveryMapper;
import play.Logger;
import play.libs.Json;
import services.base.utils.JsonFormatUtils;
import services.sales.ICombineSaleService;
import services.sales.IHttpService;
import services.sales.IKdnService;
import services.sales.IManagerOrderService;
import services.sales.ISaleInvoiceService;
import services.sales.ISaleLockService;
import services.sales.ISaleService;
import services.sales.ISequenceService;
import session.ISessionService;
import util.sales.IDUtils;
import util.sales.JsonCaseUtil;
import util.sales.Page;
import util.sales.PriceFormatUtil;
import util.sales.SaleOrderStatus;
import util.sales.SalesCombinationStatus;
import util.sales.StringUtils;

/**
 * @author zbc 2017年5月19日 下午5:48:29
 */
public class CombineSaleService implements ICombineSaleService {

	@Inject
	private ISequenceService seqService;
	@Inject
	private SaleMainMapper saleMainMapper;
	@Inject
	private SaleBaseMapper saleBaseMapper;
	@Inject
	private SaleDetailMapper saleDetailMapper;
	@Inject
	private SalesHBDeliveryMapper hbDeliveryMapper;
	@Inject
	private SalesHBDeliveryLogMapper hbDeliveryLogMapper;
	@Inject
	private SalesHBDeliveryDetailMapper hbDeliveryDetailMapper;
	@Inject
	private IHttpService httpService; 
	@Inject
	private ISessionService sessionService;
	@Inject
	private SaleMainService saleMainService;
	@Inject
	private OperateRecordMapper operateRecordMapper;
	@Inject
	private IKdnService kdnService;
	@Inject 
	private ISaleService saleService;
	@Inject
	private EventBus ebus;
	@Inject
	private ISaleLockService saleLockService;
	@Inject
	private IManagerOrderService managerOrderService;
	@Inject
	private ISaleInvoiceService invoiceService;
	@Inject 
	private SaleInvoiceMapper invoiceMapper;
	
	private static final String SEQ_NAME = "COMBINE_SALE_NO";
	
	private  static final byte[] lock = new byte[0];
	
	@Override
	public JsonResult<List<SaleDetail>> getConbinedProDetails(String hbNo) {
		List<SalesHBDeliveryDetail> deliveryDetailList = hbDeliveryDetailMapper.selectByHbNo(hbNo);
		List<SaleDetail> saleDetailList = saleDetailMapper.selectByOrderNoList(Lists.transform(deliveryDetailList, e->e.getSalesOrderNo()));
		return JsonResult.<List<SaleDetail>>newIns().result(true).data(saleDetailList);
	}
	
	@Override
	public JsonResult<?> combineSaleOrder(String string, String adminAccount) {
		synchronized (lock) {
			try {
					JsonNode json = Json.parse(string);
					String uid = JsonCaseUtil.jsonToString(json.get("uid"));
					String receiver = JsonCaseUtil.jsonToString(json.get("receiver"));
					if(sessionService.get(uid) == null){
						return JsonResult.newIns().result(false).msg("未找到合并发货信息");
					}
					SaleBeforeCombineDto combineDto = JsonFormatUtils.jsonToBean(sessionService.get(uid).toString(), SaleBeforeCombineDto.class);
					if(!combineDto.getReceivers().contains(receiver)){
						return JsonResult.newIns().result(false).msg("收货人不存在");
					}
					//add by zbc 校验发票信息
					JsonResult<SaleInvoice> invoiceResult = invoiceService.checkVaildInvoice(json);
					if(!invoiceResult.getResult()){
						return invoiceResult;
					}
					List<SaleMain> mains = Lists.newArrayList();
					List<SaleBase> bases = Lists.newArrayList();
					SaleMain main = null;
					SaleBase base = null;
					String combineOrderNo =  null;
					String remark = "合并支付:";
					JsonNode lockNode = null;
					for(String orderNo:combineDto.getOrderNos()){
						main = saleMainMapper.selectByOrderNo(orderNo);
						// 校验是否是待付款
						if (main.getStatus() != SaleOrderStatus.WAITING_PAY
								&& main.getStatus() != SaleOrderStatus.WAITING_PAY_BBC_POSTAGE) {
							return JsonResult.newIns().result(false).msg("订单:" + orderNo + "不是待付款，请重新选择订单");
						}
						if(main.getIsCombine()){
							return JsonResult.newIns().result(false).msg("订单:" + orderNo + "已经合并过，请重新选择订单");
						}
						if(combineOrderNo == null){
							combineOrderNo = main.getSalesOrderNo();
						}
						//尝试锁库
						if(main.getPurchaseOrderNo() != null){
							lockNode = httpService.orderLock(main.getPurchaseOrderNo());
							if(!lockNode.get("suc").asBoolean()){
								return JsonResult.newIns().result(false).msg("发货单[" + orderNo + "]:"+lockNode.get("msg"));
							}
						}
						base = saleBaseMapper.selectByOrderId(main.getId());
						//更新收件人
						base.setReceiver(receiver);
						mains.add(main);
						bases.add(base);
						remark += orderNo +",";
					}
					Map<Integer,SaleBase> baseMap = Maps.uniqueIndex(bases, b->b.getSalesOrderId());
					//均摊运费
					capFreight(bases,combineDto.getBbcPostage());
					//获取可支付金额
					JsonNode accountNode = httpService.getAccount(combineDto.getAccount());
					if("true".equals(accountNode.get("isFrozen").asText())){
						return JsonResult.newIns().result(false).msg("分销商:" + combineDto.getAccount() + "账户被冻结，合并发货失败");
					}
					BigDecimal balance = new BigDecimal(accountNode.get("balance").asDouble()).setScale(2,BigDecimal.ROUND_UP);
					if(new BigDecimal(combineDto.getOrderTotalAmount()).compareTo(balance) > 0 ){
						return JsonResult.newIns().result(false).msg("分销商:" + combineDto.getAccount() + "余额不足，支付失败，合并发货失败");
					}
					//生成合并发货单
					// 合并发货单
					SalesHBDelivery delivery = new SalesHBDelivery(IDUtils.getCombineSaleOrderCode(seqService.selectNextValue(SEQ_NAME)), 
							receiver, combineDto, SalesCombinationStatus.WAITING_AUDIT_CS, adminAccount);
					//先支付合并发货单，然后循环更新订单状态
					JsonNode payRes = httpService.backStagePayment(combineDto.getAccount(), combineDto.getOrderTotalAmount(), delivery.getSalesHbNo(), 6,remark.substring(0, remark.length()-1));
					BigDecimal total = null;
					List<SalesHBDeliveryDetail> deliveryDetails = Lists.newArrayList();
					//修改采购单运费集合
					List<Map<String,Object>> changeFreightList = Lists.newArrayList();
					Map<String,Object> changeFreightMap = null;
					// 判断是否支付成功
					if(payRes.get("success").asBoolean()){
						for(SaleMain m :mains){
							base = baseMap.get(m.getId());
							total = saleService.getAmount(m.getPurchaseOrderNo(),base);
							//跟新main信息
							main = new SaleMain();
							if(m.getStatus() == SaleOrderStatus.WAITING_PAY){
								JsonNode finishOrder = httpService.finishedOrder(m.getPurchaseOrderNo(), total.doubleValue());
								Logger.info("更新订单结果[{}]",finishOrder);
							}else if(m.getStatus() == SaleOrderStatus.WAITING_PAY_BBC_POSTAGE){
								main.setPurchasePaymentType("system");
								main.setPurchasePayDate(new Date());
								main.setStatus(SaleOrderStatus.WAITING_AUDIT_BY_CS);
								main.setOrderActualPayment(base.getBbcPostage());
							}
							main.setId(m.getId());
							main.setIsCombine(true);
							main.setCombineOrderNo(combineOrderNo);
							main.setCombineOrderCount(combineDto.getOrderCount());
							saleMainMapper.updateByPrimaryKeySelective(main);
							saleBaseMapper.updateByPrimaryKeySelective(base);
							
							// 合并单详情
							SalesHBDeliveryDetail salesHBDeliveryDetail = new SalesHBDeliveryDetail(delivery.getSalesHbNo(), m.getSalesOrderNo());
							salesHBDeliveryDetail.setSalesOrderId(m.getId());
							salesHBDeliveryDetail.setShopId(base.getShopId());
							salesHBDeliveryDetail.setPurchaseOrderNo(m.getPurchaseOrderNo());
							deliveryDetails.add(salesHBDeliveryDetail);
							
							//更新采购单运费
							if(m.getPurchaseOrderNo() != null){
								changeFreightMap = Maps.newHashMap();
								changeFreightMap.put("pno", m.getPurchaseOrderNo());
								changeFreightMap.put("freight", base.getBbcPostage());
								changeFreightList.add(changeFreightMap);
							}
						}
						hbDeliveryMapper.insertSelective(delivery);
						//保存发票信息
						invoiceService.save(delivery, delivery.getCreateUser(), invoiceResult.getData());
						deliveryDetails.forEach(de->{
							de.setSalesHbId(delivery.getId());
							hbDeliveryDetailMapper.insertSelective(de);
						});
						addHbDeviveryLog(delivery, adminAccount, 1, null);
						//异步执行
						if(changeFreightList.size()>0){
							ebus.post(new ChangePurchaseFeightEvent(changeFreightList));
						}
						return JsonResult.newIns().result(true).msg("合并发货成功，合并发货单号："+delivery.getSalesHbNo());
					}else{
						return JsonResult.newIns().result(false).msg(JsonCaseUtil.jsonToString(payRes.get("info")));
					}
			} catch (Exception e) {
				Logger.info("合并发货异常:{}",e);
				return JsonResult.newIns().result(false).msg("合并发货异常");
			}
		}
	}

	/**
	 * 均摊运费
	 * @author zbc
	 * @since 2017年5月22日 上午11:39:01
	 * @param bases 
	 * @param bbcPostage
	 */
	private void capFreight(List<SaleBase> bases, Double bbcPostage) {
		BigDecimal totalPostage  = new BigDecimal(bases.stream().mapToDouble(b->{
			return JsonCaseUtil.getValue(b.getBbcPostage());
		}).sum());
		BigDecimal leftPostage = new BigDecimal(bbcPostage);
		BigDecimal curPostage = null;
		SaleBase base  = null;
		for(int i=0;i<bases.size();i++){
			base = bases.get(i);
			if(i < bases.size() - 1){
				if(JsonCaseUtil.getValue(base.getBbcPostage()) == 0 || totalPostage.compareTo(BigDecimal.ZERO) == 0){
					curPostage = BigDecimal.ZERO;
				}else{
					curPostage = new BigDecimal(JsonCaseUtil.getValue(base.getBbcPostage()))
							.divide(totalPostage, 10, BigDecimal.ROUND_UP).multiply(new BigDecimal(bbcPostage));	
				}
				leftPostage = leftPostage.subtract(curPostage);
			}else{
				curPostage = leftPostage;
			}
			base.setBbcPostage(PriceFormatUtil.toFix2(curPostage));
			//修改前运费也要这么算
			base.setOriginalFreight(PriceFormatUtil.toFix2(curPostage));
		}
	}

	/*
	 *  1、维护销售单支付金额字段 2、维护采购单完成标识
	 */
	@Override
	public JsonResult<?> getToCombineInfo(String string, String operator,String relateAccounts) {
		synchronized (lock) {
			try {
				JsonNode json = Json.parse(string);
				List<String> orderNos = null;
				if(json.get("isAll") != null&&json.get("isAll").asBoolean()){
					JsonNode pageJson = json.get("pageSearch");
					if(!JsonCaseUtil.checkParam(pageJson,"status")){
						return JsonResult.newIns().result(false).msg("全选搜索结果参数错误");
					}
					orderNos = Lists.transform(getMains(relateAccounts, pageJson), e->{return e.getSalesOrderNo();});
				}else{
					if(json.get("orderNos") != null&&json.get("orderNos").isArray()){
						ObjectMapper map = new ObjectMapper();
						orderNos = map.readValue(json.get("orderNos").toString(), new TypeReference<List<String>>() {
						});	
					}
				}
				if (orderNos == null || orderNos.size() < 2) {
					return JsonResult.newIns().result(false).msg("发货单数量必须大于等于2");
				}
				//合并发货单参数
				SaleBeforeCombineDto combineDto = null; 
				SaleMain main = null;
				SaleBase base = null;
				String key = null;
				//收货人集合
				Set<String> receivers = Sets.newHashSet();
				List<SaleDetail> details = Lists.newArrayList();
				//待付款总金额
				BigDecimal orderTotalAmount = BigDecimal.ZERO;
				// 1.根据发货单号，查询
				BigDecimal originalTotalBbcPostage = new BigDecimal(0);
				for (String orderNo : orderNos) {
					main = saleMainMapper.selectByOrderNo(orderNo);
					if (main == null) {
						return JsonResult.newIns().result(false).msg("订单:" + orderNo + "不存在,请重新选择发货订单");
					}
					// 校验是否是待付款
					if (main.getStatus() != SaleOrderStatus.WAITING_PAY
							&& main.getStatus() != SaleOrderStatus.WAITING_PAY_BBC_POSTAGE) {
						return JsonResult.newIns().result(false).msg("订单:" + orderNo + "不是待付款,请重新选择发货订单");
					}
					// 校验是否已经合并过
					if (main.getIsCombine()) {
						return JsonResult.newIns().result(false).msg("订单:" + orderNo + "不能重复合并,请重新选择发货订单");
					}
					base = saleBaseMapper.selectByOrderId(main.getId());
					//校验是否有优惠码，如果有不能合并，应为优惠码有门槛，如果低于门槛金额就不能合并，所以一律不能合并
					if(StringUtils.isNotBlankOrNull(base.getCouponsCode())){
						return JsonResult.newIns().result(false).msg("订单:" + orderNo + "使用了优惠码，不能进行合并操作");
					}
					// 校验是否能够合并
					if (key != null && !key.equals(getSameKey(main, base))) {
						return JsonResult.newIns().result(false)
								.msg("订单:" + orderNo + "不能合并," + "合并发货单必须保证:分销商，仓库，收货地址，联系方式，物流方式一致");
					}
					key = getSameKey(main, base);
					originalTotalBbcPostage = originalTotalBbcPostage.add(new BigDecimal(JsonCaseUtil.getValue(base.getBbcPostage())));
					saleService.getAmount(main.getPurchaseOrderNo(), base);
					orderTotalAmount = orderTotalAmount.add(
							new BigDecimal(JsonCaseUtil.getValue(base.getOrderTotalAmount())));
					details.addAll(saleDetailMapper.selectByOrderId(main.getId()));
					if(combineDto == null){
						combineDto = new SaleBeforeCombineDto(main,base);
					}
					receivers.add(base.getReceiver());
				}
				combineDto.setOriginalTotalBbcPostage(PriceFormatUtil.toFix2(originalTotalBbcPostage));
				//设置品类数量
				combineDto.setKindCount(details.stream().map(e->e.getSku()).collect(Collectors.toSet()).size());
				//设置商品数量
				combineDto.setTotalQty(details.stream().mapToInt(e->e.getQty()).sum());
				combineDto.setOrderNos(orderNos);
				combineDto.setReceivers(receivers);
				String[] addressArray = combineDto.getAddress().split(" ");
				Integer proId = getProvinceId(addressArray);
				Integer cityId = getCityId(proId,addressArray[1]);
				//获取省id 市id 计算运费
				JsonNode res = httpService.getFreight(combineDto.getWarehouseId(), combineDto.getLogisticsTypeCode(),getSkuObj(details), proId, 44, cityId, main.getDisMode());
				if(!res.get("result").asBoolean()){
					return JsonResult.newIns().result(false).msg(JsonCaseUtil.jsonToString(res.get("msg")));
				}
				//获取运费
				combineDto.setBbcPostage(res.get("msg").asDouble());
				combineDto.setOrderTotalAmount(PriceFormatUtil.toFix2(orderTotalAmount.add(new BigDecimal(JsonCaseUtil.getValue(combineDto.getBbcPostage())))));
				//保存到redis
				sessionService.set(combineDto.getUid(), Json.toJson(combineDto).toString());
				return JsonResult.newIns().result(true).data(combineDto);
			} catch (Exception e) {
				Logger.info("获取合并发货信息异常:{}", e);
				return JsonResult.newIns().result(false).msg("获取合并发货信息异常");
			}
		}
	}

	private Integer getProvinceId(String[] addressArray) throws JsonProcessingException, IOException {
		return httpService.getProvinces(addressArray[0]).get("id").asInt();
	}

	private List<SaleMain> getMains(String relateAccount, JsonNode pageJson) {
		List<String> accounts = null;
		if (!StringUtils.isBlankOrNull(relateAccount)) {
			accounts = Arrays.asList(relateAccount.split(","));
		}
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("statusList", splitStatusStr(StringUtils.getStringBlank(pageJson.path("status").asText(), true)));
		paramMap.put("orderStartDate", JsonCaseUtil.jsonToString(pageJson.path("orderStartDate")));
		paramMap.put("orderEndDate", JsonCaseUtil.jsonToString(pageJson.path("orderEndDate")));
		paramMap.put("noticeStartDate", JsonCaseUtil.jsonToString(pageJson.path("noticeStartDate")));
		paramMap.put("noticeEndDate", JsonCaseUtil.jsonToString(pageJson.path("noticeEndDate")));
		paramMap.put("seachSpan", JsonCaseUtil.jsonToString(pageJson.path("seachSpan")));
		paramMap.put("warehouseId", JsonCaseUtil.jsonToInteger(pageJson.path("warehouseId")));
		paramMap.put("distributorType", JsonCaseUtil.jsonToInteger(pageJson.path("distributorType")));
		paramMap.put("disMode", JsonCaseUtil.jsonToInteger(pageJson.path("disMode")));
		paramMap.put("accounts", accounts);
		paramMap.put("sort", JsonCaseUtil.jsonToString(pageJson.path("sort")));
		paramMap.put("filter", JsonCaseUtil.jsonToString(pageJson.path("sidx")));
		paramMap.put("source", JsonCaseUtil.jsonToString(pageJson.path("source")));
		if(pageJson.has("email")){
			paramMap.put("email", JsonCaseUtil.jsonToString(pageJson.path("email")));
		}
		return saleMainMapper.selectAllSaleMain(paramMap);
	}
	
	private static List<Integer> splitStatusStr(String statusStr){
		List<Integer> statusList = new ArrayList<>();
		if(statusStr!=null && statusStr.trim().length()>0){
			// 过滤掉status为0
			statusList = Stream.of(statusStr.split(","))
					.map(Integer::valueOf).filter(status -> status.intValue() != 0)
					.collect(Collectors.toList());
		}
		return statusList;
	}
	
	/**
	 * 获取计算运费商品数据
	 * @author zbc
	 * @since 2017年5月23日 上午11:03:07
	 * @param details
	 * @return
	 */
	private JsonNode getSkuObj(List<SaleDetail> details) {
		Map<String,Integer> skuNumMap =  Maps.newHashMap();
		for(SaleDetail de:details){
			if(skuNumMap.get(de.getSku()) != null){
				skuNumMap.put(de.getSku(), skuNumMap.get(de.getSku())+de.getQty());
			}else{
				skuNumMap.put(de.getSku(), de.getQty());
			}
		}
		List<Map<String,Object>> skuList = Lists.newArrayList();
		Map<String,Object> skuMap = null;
		for(Map.Entry<String, Integer> entry : skuNumMap.entrySet()){
			skuMap = Maps.newHashMap();
			skuMap.put("sku", entry.getKey());
			skuMap.put("num", entry.getValue());
			skuList.add(skuMap); 
		}
		return Json.toJson(skuList);
	}

	/**
	 * 
	 * 获取城市id
	 * @author zbc
	 * @since 2017年5月23日 上午11:03:23
	 * @param proId
	 * @param cityName
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	private Integer getCityId(Integer proId,String cityName) throws JsonProcessingException, IOException {
		Map<String,Integer> cityMap = Maps.newHashMap();
		JsonNode cityNode =  httpService.getCities(proId);
		if(cityNode.get("success").asBoolean()){
			for(JsonNode cNode:cityNode.get("cities")){
				cityMap.put(cNode.get("cityName").asText(), cNode.get("id").asInt());
			}
		}
		return cityMap.get(cityName);
	}

	private String getSameKey(SaleMain main, SaleBase base) {
		StringBuilder builder = new StringBuilder();
		builder.append(main.getEmail())// 分销商账号
				.append("_").append(main.getWarehouseId())// 仓库id
				.append("_").append(base.getAddress())// 收货地址
				.append("_").append(base.getTel())// 收货人联系方式
				.append("_").append(base.getLogisticsTypeCode());// 物流方式
		return builder.toString();
	}

	@Override
	public Page<SalesHBDelivery> getSalesHBDeliveryPage(CombinedSalesPageQueryParam query) {
		int totalRows = hbDeliveryMapper.selectCountByQueryParam(query);
		List<SalesHBDelivery> items = Lists.newArrayList();
		if (totalRows>0) {
			items = hbDeliveryMapper.selectByQueryParam(query);
		}
		
		Page<SalesHBDelivery> page = new Page<SalesHBDelivery>(query.getCurrPage(),query.getPageSize(),totalRows,items);
		return page;
	}

	/**
	 * 检查合并单的状态
	 * @param devivery
	 * @param result
	 * @return
	 */
	private boolean checkCSDelivery(SalesHBDelivery devivery, JsonResult<?> result) {
		if (devivery==null) {
			result.result(false).msg("合并单不存在");
			return false;
		}
		
		if (SalesCombinationStatus.WAITING_AUDIT_CS!=devivery.getStatus().intValue()) {
			result.result(false).msg("合并单不处于待客服审核状态");
			return false;
		}
		
		return true;
	}

	@Override
	public JsonResult<?> auditByCustomerService(HBDeliveryAuditParam auditParam) {
		//  待完善，客服审核
		// 合并单
		SalesHBDelivery devivery = hbDeliveryMapper.selectByHbNo(auditParam.getSalesHbNo());
		
		JsonResult<?> jsonResult = JsonResult.newIns();
		if (!checkCSDelivery(devivery, jsonResult)) {
			return jsonResult;
		}
		
		// 查询详情
		List<SalesHBDeliveryDetail> detailList = hbDeliveryDetailMapper.selectByHbId(devivery.getId());
		
		// 拿到所有的发货单
		List<String> salesOrderIdList = Lists.transform(detailList, e->e.getSalesOrderNo());
		List<SaleMain> saleMainList = saleMainMapper.selectByOrderNoList(salesOrderIdList);
		
		// 缺货采购的进行判断
		if (saleMainList.stream().filter(e->StringUtils.isNotBlankOrNull(e.getSalesOrderNo())).count()>0) {
			try {
				Map<Object, Object> errors = Maps.newHashMap();
				for (SaleMain saleMain : saleMainList) {
					String pno = saleMain.getPurchaseOrderNo();
					if (StringUtils.isNotBlankOrNull(pno)) {
						JsonNode purNode = httpService.getPurchaseOrder(pno);
						if (purNode.get("status").asInt() != 1) {
							errors.put(saleMain.getSalesOrderNo(), "对应的缺货采购单【" + pno + "】未完成。");
						}
					}
				}
				if (errors.size()>0) {
					return JsonResult.newIns().result(false).errors(errors);
				}
			} catch (Exception e) {
				Logger.info("合并发货单客服审核异常，{}",e);
				return JsonResult.newIns().result(false).msg("客服审核异常");
			}			
		}
		
		if (auditParam.getPassed()) {
			// 客服审核通过
			return passedByCustomerService(auditParam, devivery, saleMainList);
		}
		
		// 客服关闭
		return closedByCustomerService(auditParam, devivery, saleMainList);
	}
	/**
	 * 客服审核通过
	 * @return
	 */
	private JsonResult<?> passedByCustomerService(HBDeliveryAuditParam auditParam, SalesHBDelivery devivery, List<SaleMain> saleMainList) {
		String address = String.join(" ", auditParam.getProvinceName(), auditParam.getCityName(),
				auditParam.getAreaName(), auditParam.getAddrDetail());
		for (SaleMain saleMain : saleMainList) {
			// add by zbc 异步计算财务审核费用信息
			ebus.post(new CaculateChargeEvent(saleMain.getId()));
			// 待财务审核
			saleMain.setStatus(SaleOrderStatus.WAITING_AUDIT_BY_FINANCE);
			
			Logger.info("合并发货：发货单{}客服审核通过", saleMain.getSalesOrderNo());
			
			// 客服确认时间
			saleMain.setCsConfirmDate(new Date());
			saleMainService.updateByPrimaryKeySelective(saleMain);
			
			// 修改订单信息
			SaleBase base = saleBaseMapper.selectByOrderId(saleMain.getId());
			base.setAddress(address);
			base.setReceiver(auditParam.getReceiver());
			base.setTel(auditParam.getTel());
			base.setPostCode(auditParam.getPostCode());
			saleBaseMapper.updateByPrimaryKeySelective(base);
			
			// 操作日志
			addSalesOperateRecord(saleMain.getId(), 10, 1, auditParam.getAuditUser(), auditParam.getRemarks());
		}
		{//计算异步计算利润值
			List<String> orderNos = Lists.transform(hbDeliveryDetailMapper.selectByHbId(devivery.getId()), d->d.getSalesOrderNo());
			ebus.post(new CaculateChargeEvent(orderNos));
		}
		//change by zbc  修改地址
		devivery.setAddress(address);
		devivery.setReceiver(auditParam.getReceiver());
		devivery.setTelephone(auditParam.getTel());
		// 合并单待财务审核
		devivery.setStatus(SalesCombinationStatus.WAITING_AUDIT_FINANCE);
		devivery.setLastUpdateUser(auditParam.getAuditUser());
		hbDeliveryMapper.updateById(devivery);
		// 记录日志
		addHbDeviveryLog(devivery, auditParam.getAuditUser(), 2, auditParam.getRemarks());
		return JsonResult.newIns().result(true).msg("客服确认通过成功");
	}
	
	
	/**
	 * 客服关闭，商品要退回，钱要退回
	 * @return
	 */
	private JsonResult<?> closedByCustomerService(HBDeliveryAuditParam auditParam, SalesHBDelivery devivery, List<SaleMain> saleMainList) {
		try {
			for (SaleMain saleMain : saleMainList) {
				
				Logger.info("合并发货：发货单{}客服关闭", saleMain.getSalesOrderNo());
				
				// 客服关闭
				saleMain.setStatus(SaleOrderStatus.AUDIT_NOT_PASSED);
				// 客服确认时间
				saleMain.setCsConfirmDate(new Date());
				saleMainMapper.updateByPrimaryKeySelective(saleMain);
				
				// 关闭发货单
				ObjectNode newObject = Json.newObject();
				newObject.put("so", saleMain.getSalesOrderNo());
				saleLockService.cancelOrder(newObject,null);
				
				// 操作日志
				addSalesOperateRecord(saleMain.getId(), 10, 0, auditParam.getAuditUser(), auditParam.getRemarks());
			}
		} catch (Exception e) {
			Logger.info("合并发货：客服关闭异常，{}", e);
			return JsonResult.newIns().result(false).msg("客服关闭失败");
		}
		
		// 合并单关闭
		devivery.setStatus(SalesCombinationStatus.CLOSED);
		devivery.setLastUpdateUser(auditParam.getAuditUser());
		hbDeliveryMapper.updateById(devivery);
		// 记录日志
		addHbDeviveryLog(devivery, auditParam.getAuditUser(), 2, auditParam.getRemarks());
		
		return JsonResult.newIns().result(true).msg("客服关闭成功");
	}
	
	/**
	 * 检查合并单的状态
	 * @param devivery
	 * @param result
	 * @return
	 */
	private boolean checkFinanceDelivery(SalesHBDelivery devivery, JsonResult<?> result) {
		if (devivery==null) {
			result.result(false).msg("合并单不存在");
			return false;
		}
		
		if (SalesCombinationStatus.WAITING_AUDIT_FINANCE!=devivery.getStatus().intValue()) {
			result.result(false).msg("合并单不处于待财务审核状态");
			return false;
		}
		
		return true;
	}
	
	@Override
	public JsonResult<?> auditByFinance(HBDeliveryAuditParam auditParam) {
		//  待完善，财务审核
		
		// 合并单
		SalesHBDelivery devivery = hbDeliveryMapper.selectByHbNo(auditParam.getSalesHbNo());

		JsonResult<?> jsonResult = JsonResult.newIns();
		if (!checkFinanceDelivery(devivery, jsonResult)) {
			return jsonResult;
		}

		// 查询详情
		List<SalesHBDeliveryDetail> detailList = hbDeliveryDetailMapper.selectByHbId(devivery.getId());

		// 拿到所有的发货单
		List<String> salesOrderIdList = Lists.transform(detailList, e -> e.getSalesOrderNo());
		List<SaleMain> saleMainList = saleMainMapper.selectByOrderNoList(salesOrderIdList);
		
		if (auditParam.getPassed()) {
			// 客服审核通过
			return passedByFinance(auditParam, devivery, saleMainList);
		}
		
		// 客服关闭
		return notPassedByFinance(auditParam, devivery, saleMainList);
	}
	/**
	 * 财务审核通过
	 * @return
	 */
	private JsonResult<?> passedByFinance(HBDeliveryAuditParam auditParam, SalesHBDelivery devivery, List<SaleMain> saleMainList) {
		try {
			for (SaleMain saleMain : saleMainList) {
				if (Strings.isNullOrEmpty(saleMain.getPaymentNo())) {
					//无需实际支付时审核通过，手动生成支付信息
					saleMain.setPaymentNo(IDUtils.getPayNo());
					saleMain.setPayDate(new Date());
					saleMain.setPaymentType("system");
					saleMain.setCurrency("CNY");
					saleMain.setRejectedByFinance(false);
				}
				
				Logger.info("合并发货：发货单{}财务审核通过", saleMain.getSalesOrderNo());
				
				//暂时不推送 走完快递鸟电子面单流程再推送
				saleMain.setIsPushed(1);
				saleMain.setStatus(SaleOrderStatus.WAITING_DELIVERY_SIX);
				// 更新发货单
				saleMainMapper.updateByPrimaryKeySelective(saleMain);
				
				kdnService.requestOrderOnline(saleMain);
				
				// 操作日志
				addSalesOperateRecord(saleMain.getId(), 2, 1, auditParam.getAuditUser(), auditParam.getRemarks());
			}
		} catch (Exception e) {
			Logger.info("合并发货：客服确认通过异常，{}", e);
			return JsonResult.newIns().result(false).msg("客服确认通过失败");
		}
		
		// 合并单待发货
		devivery.setStatus(SalesCombinationStatus.WAITING_DELIVERY);
		devivery.setLastUpdateUser(auditParam.getAuditUser());
		hbDeliveryMapper.updateById(devivery);
		// 记录日志
		addHbDeviveryLog(devivery, auditParam.getAuditUser(), 3, auditParam.getRemarks());
		
		return JsonResult.newIns().result(true).msg("财务确认通过成功");
	}
	/**
	 * 财务审核不通过
	 * @return
	 */
	private JsonResult<?> notPassedByFinance(HBDeliveryAuditParam auditParam, SalesHBDelivery devivery, List<SaleMain> saleMainList) {
		for (SaleMain saleMain : saleMainList) {
			
			Logger.info("合并发货：发货单{}财务审核不通过，打回客服审核", saleMain.getSalesOrderNo());
			
			// 审核不通过，打回到客服审核
			saleMain.setStatus(SaleOrderStatus.WAITING_AUDIT_BY_CS);
			saleMain.setRejectedByFinance(true);
			saleMainMapper.updateByPrimaryKeySelective(saleMain);
			
			// 操作日志
			addSalesOperateRecord(saleMain.getId(), 2, 0, auditParam.getAuditUser(), auditParam.getRemarks());
		}
		
		// 合并单打回客服审核
		devivery.setStatus(SalesCombinationStatus.WAITING_AUDIT_CS);
		devivery.setLastUpdateUser(auditParam.getAuditUser());
		hbDeliveryMapper.updateById(devivery);
		// 记录日志
		addHbDeviveryLog(devivery, auditParam.getAuditUser(), 3, auditParam.getRemarks());

		return JsonResult.newIns().result(true).msg("财务确认不通过成功");
	}
	
	/**
	 * 操作日志
	 * @param smId
	 * @param optType
	 * @param result
	 * @param auditUser
	 * @param auditRemark
	 */
	private void addSalesOperateRecord(int smId, int optType, int result, String auditUser, String auditRemark) {
		OperateRecord record = new OperateRecord();
		record.setOrderId(smId);
		record.setOperateType(optType);
		record.setResult(result);
		record.setComment(auditRemark);
		record.setEmail(auditUser);
		operateRecordMapper.insert(record);
	}
	/**
	 * 记录合并日志
	 * @param devivery
	 * @param optUser
	 */
	private void addHbDeviveryLog(SalesHBDelivery devivery, String optUser, int optType, String remarks) {
		SalesHBDeliveryLog log = new SalesHBDeliveryLog();
		log.setSalesHbId(devivery.getId());
		log.setSalesHbNo(devivery.getSalesHbNo());
		log.setStatus(devivery.getStatus());
		log.setOptUser(optUser);
		log.setOptType(optType);
		log.setRemarks(remarks);
		hbDeliveryLogMapper.insertSelective(log);
	}

	@Override
	public JsonResult<SalesHBDeliveryDto> getACombination(int hbId) {
		SalesHBDelivery devivery = hbDeliveryMapper.selectByPrimaryKey(hbId);
		SalesHBDeliveryDto dto = new SalesHBDeliveryDto();
		BeanUtils.copyProperties(devivery, dto);
		// 查询详情
		List<SalesHBDeliveryDetail> detailList = hbDeliveryDetailMapper.selectByHbId(hbId);
		setGrossIncomeAndProfit(detailList);
		dto.setDetailList(detailList);
		return JsonResult.<SalesHBDeliveryDto>newIns().result(true).data(dto);
	}

	/**
	 * 设置毛收入和毛利润
	 * @param detailList
	 */
	private void setGrossIncomeAndProfit(List<SalesHBDeliveryDetail> detailList) {
		if (CollectionUtils.isEmpty(detailList)) {
			return;
		}
		// 发货单单号 => 发货单
		ImmutableMap<String, SaleMain> sno2SaleMain = Maps.uniqueIndex(saleMainMapper.selectByOrderNoList(Lists.transform(detailList, e->e.getSalesOrderNo())), e->e.getSalesOrderNo());
		if (sno2SaleMain==null || sno2SaleMain.size()==0) {
			return;
		}
		for (SalesHBDeliveryDetail detail : detailList) {
			SaleMain sm = sno2SaleMain.get(detail.getSalesOrderNo());
			if (sm!=null) {
				detail.setGrossIncome(sm.getPlatformAmount());
				detail.setGrossProfit(sm.getProfit());
			}
		}
	}

	@Override
	public JsonResult<List<SalesHBDeliveryLog>> getCombinedLogs(Integer hbId) {
		List<SalesHBDeliveryLog> logs = hbDeliveryLogMapper.selectBySalesHbId(hbId);
		return JsonResult.<List<SalesHBDeliveryLog>>newIns().result(true).data(logs);
	}

	@Override
	public JsonResult<?> calculation(String od) {
		List<SalesHBDeliveryDetail> details = hbDeliveryDetailMapper.selectByHbNo(od);
		details.forEach(d->{
			//计算利润
			managerOrderService.match(saleMainMapper.selectByOrderNo(d.getSalesOrderNo()).getId());
		});
		return JsonResult.newIns().result(true).msg("计算利润成功");
	}

	@Override
	public JsonResult<?> batchOrder(String admin, String jsonStr) {
		try {
			JsonNode json  = Json.parse(jsonStr);
			String od = JsonCaseUtil.jsonToString(json.get("od"));
			ObjectMapper map = new ObjectMapper();
			List<String> orderNos = map.readValue(json.get("orderNos").toString(), new TypeReference<List<String>>() {});
			if(orderNos.size() <= 0){
				return JsonResult.newIns().result(false).msg("请选择发货单");
			}
			SalesHBDelivery delivery = hbDeliveryMapper.selectByHbNo(od);
			if(delivery == null){
				return JsonResult.newIns().result(false).msg("合并发货单不存在");
			}
			List<SalesHBDeliveryDetail> details = hbDeliveryDetailMapper.selectByHbNo(od);
			details = details.stream().filter(de->{
				return orderNos.indexOf(de.getSalesOrderNo()) > -1; 
			}).collect(Collectors.toList());
			if(orderNos.size() > details.size()){
				return JsonResult.newIns().result(false).msg("选择正确的发货单");
			}
			List<PostSaleOrderDto> postOrders = Lists.newArrayList();
			PostSaleOrderDto dto = null;
			SaleMain main = null;
			SaleBase base = null;
			String[] addressArray = delivery.getAddress().split(" ");
			Integer provinceId = getProvinceId(addressArray);
			Integer cityId = getCityId(provinceId,addressArray[1]);
			SaleInvoice invoice = null;
			for(SalesHBDeliveryDetail detail :details){
				main = saleMainMapper.selectByOrderNo(detail.getSalesOrderNo());
				base = saleBaseMapper.selectByOrderId(main.getId());
				invoice = invoiceMapper.selectByOrderNo(main.getSalesOrderNo());
				dto = new PostSaleOrderDto(invoice,main, base, admin, true,provinceId, cityId,Lists.transform(saleDetailMapper.selectByOrderId(main.getId()), de->{
					return new PostSalePro(de);
				}));
				postOrders.add(dto);
			}
			Logger.info("后台批量下单参数:{}",Json.toJson(postOrders));
			postOrders.forEach(post->{
				saleLockService.order(((ObjectNode) Json.toJson(post)).put("LogisticsTypeCode",post.getLogisticsTypeCode()));
			});
			return JsonResult.newIns().result(true).msg("批量生成订单成功");
		} catch (Exception e) {
			Logger.info("批量生成:{}",e);
			return JsonResult.newIns().result(false).msg("批量生成订单异常");
		}
	}

	@Override
	public JsonResult<Map<String, Object>> info2auditByFinance(int hbId) {
		SalesHBDelivery hbDelivery = hbDeliveryMapper.selectByPrimaryKey(hbId);
		if (hbDelivery==null) {
			return JsonResult.<Map<String, Object>>newIns().result(false).msg("获取财务审核信息失败");
		}
		List<SalesHBDeliveryDetail> details = hbDeliveryDetailMapper.selectByHbId(hbId);
		List<String> snoList = Lists.transform(details, e->e.getSalesOrderNo());
		
		List<SaleMain> saleMainList = saleMainMapper.selectByOrderNoList(snoList);
		List<Integer> sidList = Lists.transform(saleMainList, e->e.getId());

		List<SalesPriceDto> salesPriceDtoList = Lists.newArrayList();
		for (Integer sid : sidList) {
			salesPriceDtoList.add(saleMainMapper.getCharges(sid));
		}
		
		BigDecimal totalPlatformamount = new BigDecimal(0);// 毛收入
		
		BigDecimal totalArrvicetotal = new BigDecimal(0);// 到仓价总计
		BigDecimal totalProfit = new BigDecimal(0);// 到仓价利润
		BigDecimal totalTotalcost = new BigDecimal(0);// 到仓价总成本
		
		BigDecimal totalClearancepricetotal = new BigDecimal(0);// 清货价总计
		BigDecimal totalClearprofit = new BigDecimal(0);// 清货价利润
		BigDecimal totalCleartotalcost = new BigDecimal(0);// 清货价总成本
		
		BigDecimal totalOptfee = new BigDecimal(0);// 操作费
		BigDecimal totalContractcharge = new BigDecimal(0);// 合同费用
		BigDecimal totalSdpamount = new BigDecimal(0);// 店铺扣点
		//判断是否有到仓价
		boolean hasClearanceprice = salesPriceDtoList.stream().filter(ele->{
			return ele.getClearancepricetotal() != null&&ele.getClearancepricetotal()>0;
		}).findAny().isPresent();
		for (SalesPriceDto spDto : salesPriceDtoList) {
			// 毛收入 platformamount
			totalPlatformamount = totalPlatformamount.add(getBigDecimal(spDto.getPlatformamount()));
			// 到仓价总计 arrvicetotal
			totalArrvicetotal = totalArrvicetotal.add(getBigDecimal(spDto.getArrvicetotal()));
			// 到仓价利润 profit
			totalProfit = totalProfit.add(getBigDecimal(spDto.getProfit()));
			// 到仓价总成本 totalcost
			totalTotalcost = totalTotalcost.add(getBigDecimal(spDto.getTotalcost()));
			// 清货价总计 clearancepricetotal
			totalClearancepricetotal = totalClearancepricetotal.add(
					getBigDecimal(spDto.getClearancepricetotal()).compareTo(BigDecimal.ZERO)>0?
							getBigDecimal(spDto.getClearancepricetotal()):getBigDecimal(spDto.getArrvicetotal())
					);
			// 操作费 optfee
			totalOptfee = totalOptfee.add(getBigDecimal(spDto.getOptfee()));
			// 合同费用 contractcharge
			totalContractcharge = totalContractcharge.add(getBigDecimal(spDto.getContractcharge()));
			// 店铺扣点 sdpamount
			totalSdpamount = totalSdpamount.add(getBigDecimal(spDto.getSdpamount()));
		}
		
		Map<String, Object> infos = Maps.newHashMap();
		
		infos.put("totalPlatformamount", PriceFormatUtil.toFix2(totalPlatformamount));// 毛收入
		infos.put("totalArrvicetotal", PriceFormatUtil.toFix2(totalArrvicetotal));// 到仓价总计
		infos.put("totalProfit", PriceFormatUtil.toFix2(totalProfit));// 到仓价利润
		if (totalPlatformamount.doubleValue()!=0) {
			infos.put("totalProfitmargin", PriceFormatUtil.toFix2(totalProfit.divide(totalPlatformamount, 2, RoundingMode.HALF_UP)));// 到仓价利润率
		} else {
			infos.put("totalProfitmargin", 0.00);// 到仓价利润率
		}
		infos.put("totalTotalcost", PriceFormatUtil.toFix2(totalTotalcost));// 到仓价总成本
		if(hasClearanceprice){
			//清货总成本  = 清货价总计+运费+合同费用+店铺扣点
			totalCleartotalcost =  totalClearancepricetotal.add(getBigDecimal(hbDelivery.getTotalBbcPostage()).add(totalOptfee).add(totalSdpamount));
			infos.put("totalClearancepricetotal", PriceFormatUtil.toFix2(totalClearancepricetotal));// 清货价总计
			// 清货价利润 clearprofit
			totalClearprofit = totalPlatformamount.subtract(totalCleartotalcost);
			infos.put("totalClearprofit", PriceFormatUtil.toFix2(totalClearprofit));// 清货价利润
			if (totalPlatformamount.doubleValue()!=0) {
				infos.put("totalClearprofitmargin", PriceFormatUtil.toFix2(totalClearprofit.divide(totalPlatformamount, 2, RoundingMode.HALF_UP)));// 清货价利润率
			} else {
				infos.put("totalClearprofitmargin", 0.00);// 清货价利润率
			}
			infos.put("totalCleartotalcost", PriceFormatUtil.toFix2(totalCleartotalcost));// 清货价总成本
		}
		infos.put("originalTotalBbcPostage", hbDelivery.getTotalBbcPostage());// 实际运费
		infos.put("totalOptfee", PriceFormatUtil.toFix2(totalOptfee));// 操作费
		infos.put("totalContractcharge", PriceFormatUtil.toFix2(totalContractcharge));// 合同费用
		infos.put("totalSdpamount", PriceFormatUtil.toFix2(totalSdpamount));// 店铺扣点
		return JsonResult.<Map<String, Object>>newIns().result(true).msg("获取财务审核信息成功").data(infos);
	}
	
	private BigDecimal getBigDecimal(Double value) {
		if (value==null) {
			return new BigDecimal(0.00);
		}
		return new BigDecimal(value);
	}
	
}
