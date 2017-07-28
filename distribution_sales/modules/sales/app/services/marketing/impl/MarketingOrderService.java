package services.marketing.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import dto.SkuWarehouse2Qty;
import dto.marketing.AuditParams;
import dto.marketing.CloudLockPros;
import dto.marketing.MarketingOrderDto;
import dto.marketing.QueryMarketingOrderParams;
import dto.marketing.ViewMarketingOrderDetail;
import dto.marketing.ViewMarketingOrderDto;
import dto.sales.ExportMarketOrderInfo;
import entity.marketing.MarketingOrder;
import entity.marketing.MarketingOrderAuditLog;
import entity.marketing.MarketingOrderDetail;
import entity.sales.OperateRecord;
import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;
import events.sales.CalculateOptFeeEvent;
import events.sales.GenerateSaleOrderEvent;
import events.sales.SaveReceiverAddressEvent;
import mapper.marketing.MarketingOrderAuditLogMapper;
import mapper.marketing.MarketingOrderDetailMapper;
import mapper.marketing.MarketingOrderMapper;
import mapper.sales.OperateRecordMapper;
import mapper.sales.SaleBaseMapper;
import mapper.sales.SaleDetailMapper;
import mapper.sales.SaleMainMapper;
import pager.sales.Pager;
import play.Logger;
import play.libs.Json;
import services.marketing.IMarketingOrderService;
import services.sales.IHttpService;
import services.sales.IKdnService;
import services.sales.ISaleLockService;
import services.sales.ISequenceService;
import services.sales.IUserService;
import util.sales.CollectionUtils;
import util.sales.DateUtils;
import util.sales.ExportMarketingOrderUtil;
import util.sales.IDUtils;
import util.sales.JsonCaseUtil;
import util.sales.MarketingOrderStatus;
import util.sales.ParametersUtil;
import util.sales.PriceFormatUtil;
import util.sales.SaleOrderStatus;
import util.sales.StringUtils;

/**
 * 营销单service实现
 * 
 * @author huangjc
 * @since 2017年3月10日
 */
public class MarketingOrderService implements IMarketingOrderService {
	@Inject
	private MarketingOrderMapper marketingOrderMapper;
	@Inject
	private MarketingOrderDetailMapper marketingOrderDetailMapper;
	@Inject
	private ISequenceService sequenceService;
	@Inject
	private MarketingOrderAuditLogMapper logMapper;
	@Inject
	private IHttpService httpService;
	@Inject
	private EventBus ebus;
	@Inject
	private SaleMainMapper saleMainMapper;
	@Inject
	private SaleBaseMapper saleBaseMapper;
	@Inject
	private SaleDetailMapper saleDetailMapper;
	@Inject
	private ISaleLockService lockService;

	@Inject
	private IUserService userService;

	@Inject
	private IKdnService kdnService;
	
	@Inject
	private OperateRecordMapper operateRecordMapper;

	@Override
	public MarketingOrderAuditLog getAuditFirstlyLatestLog(String marketingOrderNo) {
		return logMapper.selectAOrderLatestLog(marketingOrderNo, 1);
	}

	@Override
	public List<MarketingOrderAuditLog> getAllAuditLogs4AMo(String marketingOrderNo) {
		return logMapper.selectAllLogs(marketingOrderNo, null);
	}

	/**
	 * 校验参数
	 * 
	 * @param main
	 * @param result
	 */
	private Map<String, Object> validate(JsonNode main) {
		Map<String, Object> result = Maps.newHashMap();
		// do nothing for now
		return result;
	}

	private List<MarketingOrderDetail> parseDetails(JsonNode main) {
		List<MarketingOrderDetail> detailList = Lists.newArrayList();
		MarketingOrderDetail detail;
		JsonNode nextNode;
		for (Iterator<JsonNode> it = main.get("details").iterator(); it.hasNext();) {
			nextNode = it.next();
			detail = new MarketingOrderDetail();
			detail.setDisPrice(nextNode.get("disPrice").asDouble());
			detail.setProductName(nextNode.get("productName").asText());
			detail.setInterBarCode(nextNode.get("interBarCode").asText());
			detail.setProductImg(nextNode.get("productImg").asText());
			detail.setSku(nextNode.get("sku").asText());
			detail.setQty(nextNode.get("qty").asInt());
			detail.setWarehouseId(nextNode.get("warehouseId").asInt());
			detail.setWarehouseName(nextNode.get("warehouseName").asText());
			detail.setCategoryId(JsonCaseUtil.jsonToInteger(nextNode.get("categoryId")));
			detail.setCategoryName(JsonCaseUtil.jsonToString(nextNode.get("categoryName")));
			detail.setExpirationDate(nextNode.has("expirationDate") ? nextNode.get("expirationDate").asText() : null);
			detailList.add(detail);
		}
		return detailList;
	}

	private MarketingOrder parseMarketingOrder(JsonNode main, String loginAccount) {
		MarketingOrder mo = new MarketingOrder();
		mo.setEmail(main.get("email").asText());
		mo.setNickName(main.get("nickName").asText());
		mo.setTotalAmount(main.get("totalAmount").asDouble());
		mo.setProvinceId(main.get("provinceId").asInt());
		mo.setCityId(main.get("cityId").asInt());
		mo.setRegionId(main.get("regionId").asInt());
		mo.setProvinceName(main.get("provinceName").asText());
		mo.setCityName(main.get("cityName").asText());
		mo.setRegionName(main.get("regionName").asText());
		mo.setAddressDetail(main.get("addressDetail").asText());
		mo.setReceiver(main.get("receiver").asText());
		mo.setReceiverTel(main.get("receiverTel").asText());
		mo.setReceiverPostcode(main.get("receiverPostcode").asText());
		mo.setLogisticsMode(main.get("logisticsMode").asText());
		mo.setLogisticsTypeCode(main.get("logisticsTypeCode").asText());
		mo.setBbcPostage(main.get("bbcPostage").asDouble());
		mo.setBusinessRemark(main.get("businessRemark").asText());
		mo.setCreateUser(loginAccount);
		try {
			mo.setSalesman(httpService.custaccount(mo.getEmail()).get("account").textValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mo;
	}

	@Override
	public Map<String, Object> createMarketingOrder(String mainStr, String loginAccount) {

		JsonNode main = Json.parse(mainStr);
		Map<String, Object> validate = validate(main);
		if (validate.size() > 0) {
			return validate;
		}

		Map<String, Object> result = Maps.newHashMap();
		MarketingOrder mo = parseMarketingOrder(main, loginAccount);
		mo.setStatus(MarketingOrderStatus.WAITING_AUDIT_FIRSTLY);
		List<MarketingOrderDetail> detailList = parseDetails(main);

		// 获取分销商
		JsonNode disMemberNode = null;
		try {
			disMemberNode = httpService.getDismemberByEmail(mo.getEmail());
		} catch (IOException e) {
			result.put("suc", false);
			result.put("msg", "获取分销商失败");
			return result;
		}
		if (disMemberNode == null || !disMemberNode.get("suc").asBoolean()) {
			result.put("suc", false);
			result.put("msg", "不存在此分销商");
			return result;
		}

		batchSetArriveWarePrice(detailList);
		setTotalArriveWarePrice(mo, detailList, true);

		// 内部分销商才可以做营销单
		// comsumerType 分销商类型(1：普通分销商，2：合营分销商，3：内部分销商)
		// distributionMode 分销商 模式(1、电商；2、经销商；3、商超；4、进口专营；5、VIP)
		int comsumerType = disMemberNode.get("result").get("comsumerType").asInt();
		int distributionMode = disMemberNode.get("result").get("distributionMode").asInt();
		String nickName = disMemberNode.get("result").get("nickName").asText();

		// 设置分销商类型与模式
		mo.setDisMode(distributionMode);
		mo.setDistributorType(comsumerType);
		mo.setNickName(nickName);
		mo.setStatus(MarketingOrderStatus.WAITING_AUDIT_FIRSTLY);
		mo.setMarketingOrderNo(IDUtils.getMarketingOrderCode(sequenceService.selectNextValue("MARKETING_ORDER_NO")));
		int insertMoResult = marketingOrderMapper.insert(mo);
		Logger.info((insertMoResult == 1 ? "保存营销单成功！{}" : "保存营销单失败！{}"), mo);

		for (MarketingOrderDetail mod : detailList) {
			mod.setMarketingOrderId(mo.getId());
			mod.setMarketingOrderNo(mo.getMarketingOrderNo());
		}
		int insertBatchResult = marketingOrderDetailMapper.insertBatch(detailList);
		Logger.info((insertBatchResult == detailList.size() ? "保存营销单明细成功！{}" : "保存营销单明细失败！{}"), detailList);

		// 异步保存收货人地址
		ebus.post(new SaveReceiverAddressEvent(mo));
		
		result.put("suc", true);
		result.put("msg", "创建营销单成功，单号为：" + mo.getMarketingOrderNo());
		return result;
	}

	/**
	 * 设置营销单总到仓价
	 * @param mo 营销单
	 * @param detailList 营销单详情
	 * @param useCostIfAbsent 是否可以使用裸采价替换空的到仓价
	 */
	private void setTotalArriveWarePrice(MarketingOrder mo, List<MarketingOrderDetail> detailList, boolean useCostIfAbsent) {
		if (mo==null || detailList==null || detailList.size()==0) {
			Logger.info("设置营销单的总到仓价，由于营销单不存在/营销单详情为空，设置失败");
			return;
		}
		Logger.info("设置营销单的总到仓价，{}使用裸采价替换空的到仓价", (useCostIfAbsent?"可以":"不可以"));
		
		BigDecimal tawPrice = new BigDecimal(0);
		for (MarketingOrderDetail mod : detailList) {
			// 使用到仓价，如果到仓价为空，要看能不能使用裸采价替换计算
			if (mod.getArriveWarePrice()!=null && mod.getArriveWarePrice()>0) {
				tawPrice = tawPrice.add(
						new BigDecimal(mod.getArriveWarePrice()).multiply(new BigDecimal(mod.getQty()))
						);
			} else {
				// 是否可以使用裸采价替换计算
				if (useCostIfAbsent && mod.getCost()!=null && mod.getCost()>0) {
					tawPrice = tawPrice.add(
							new BigDecimal(mod.getCost()).multiply(new BigDecimal(mod.getQty()))
							);
				}
			}
		}
		
		mo.settAWPrice(PriceFormatUtil.toFix2(tawPrice));
	}

	/**
	 * 设置到仓价和裸采价
	 * 
	 * @param detailList
	 */
	private void batchSetArriveWarePrice(List<MarketingOrderDetail> detailList) {
		if (detailList == null || detailList.size() == 0) {
			Logger.info("要设置到仓价的营销单详情为空");
			return;
		}

		// 因为存在到期日期，会有重复的sku，要过滤掉
		HashMap<String, SkuWarehouse2Qty> skuWarehouseId2Param = Maps.newHashMap();
		for (MarketingOrderDetail mod : detailList) {
			skuWarehouseId2Param.put(getKey(mod.getSku(), mod.getWarehouseId()),
					new SkuWarehouse2Qty(mod.getSku(), mod.getWarehouseId()));
		}
		List<SkuWarehouse2Qty> list = Lists.newArrayList();
		list.addAll(skuWarehouseId2Param.values());
		JsonNode resultNode = null;
		try {
			// 查询到仓价
			resultNode = httpService.batchGetArriveWarePrice(list);
		} catch (IOException e) {
			Logger.info("营销单详情批量获取到仓价失败");
			e.printStackTrace();
		}

		if (resultNode == null || !resultNode.get("suc").asBoolean()) {
			Logger.info("营销单详情批量获取到仓价失败");
			return;
		}
		Map<String, JsonNode> skuWarehouseId2ArriveWarePrice = Maps.newHashMap();
		for (Iterator<JsonNode> it = resultNode.get("result").iterator(); it.hasNext();) {
			JsonNode nextNode = it.next();
			skuWarehouseId2ArriveWarePrice
					.put(getKey(nextNode.get("sku").asText(), nextNode.get("warehouseId").asInt()), nextNode);
		}

		if (skuWarehouseId2ArriveWarePrice.size() == 0) {
			Logger.info("营销单详情批量获取到的到仓价为空");
			return;
		}

		// 设置到仓价
		for (MarketingOrderDetail mod : detailList) {
			JsonNode node = skuWarehouseId2ArriveWarePrice.get(getKey(mod.getSku(), mod.getWarehouseId()));
			mod.setArriveWarePrice(node.get("arriveWarePrice").asDouble());
			mod.setCost(node.get("cost").asDouble());
		}
	}

	private String getKey(String sku, Integer warehouseId) {
		return sku + "_" + warehouseId.toString();
	}

	@Override
	public List<MarketingOrderDto> getMarketingOrderList(QueryMarketingOrderParams params) {
		// 不需要分页
		params.setCurrPage(null);
		params.setPageSize(null);
		// 查询营销单
		List<MarketingOrder> moList = marketingOrderMapper.getMarketingOrderList(params);
		List<MarketingOrderDto> moDtoList = Lists.newArrayListWithCapacity(moList.size());
		if (moList.size() > 0) {
			// 批量查询详情
			List<List<Integer>> midsLists = CollectionUtils.createList(Lists.transform(moList, mo -> mo.getId()), 150);
			List<MarketingOrderDetail> allDetails = Lists.newArrayList();
			for (List<Integer> ids : midsLists) {
				allDetails.addAll(marketingOrderDetailMapper.selectByMoIdList(ids));
			}
			Map<Integer, List<MarketingOrderDetail>> detailsByMoId = allDetails.stream()
					.collect(Collectors.groupingBy(MarketingOrderDetail::getMarketingOrderId));

//			try {
				// 转换
				MarketingOrderDto moDto;
				for (MarketingOrder mo : moList) {
					moDto = new MarketingOrderDto();
					BeanUtils.copyProperties(mo, moDto);
					moDtoList.add(moDto);
					// 设置详情
					moDto.setDetailList(detailsByMoId.get(mo.getId()));
					
					// TODO 旧数据没有总到仓价，要重新获取
//					if (moDto.gettAWPrice()==null) {
//						Map<String, Integer> skuWarehouse2Qty = Maps.newHashMap();
//						JsonNode tawPriceNode = httpService.getTotalArriveWarehousePrice(skuWarehouse2Qty, true);
//						if (tawPriceNode!=null && tawPriceNode.get("suc").asBoolean()) {
//							moDto.settAWPrice(tawPriceNode.get("result").asDouble());
//						}
//					}
				}
//			} catch (IOException e) {
//				Logger.info("为营销单旧数据获取总到仓价出错：{}", e);
//				return Lists.newArrayList();
//			}
		}
		return moDtoList;
	}

	@Override
	public Pager<ViewMarketingOrderDto> getMarketingOrderPage(QueryMarketingOrderParams param) {
		Integer pageSize = param.getPageSize();
		Integer currPage = param.getCurrPage();
		Integer totalCount = 0;
		// 查询营销单
		List<MarketingOrder> moList = marketingOrderMapper.getMarketingOrderList(param);
		List<ViewMarketingOrderDto> moDtoList = Lists.newArrayListWithCapacity(moList.size());
		if (moList.size() > 0) {
			// 批量查询详情
			List<Integer> moIdList = Lists.transform(moList, mo -> mo.getId());
			List<MarketingOrderDetail> allDetails = marketingOrderDetailMapper.selectByMoIdList(moIdList);
			// 详情转换
			List<ViewMarketingOrderDetail> allViewDetails = Lists.newArrayListWithCapacity(allDetails.size());
			ViewMarketingOrderDetail viewDetail;
			for (MarketingOrderDetail mod : allDetails) {
				viewDetail = new ViewMarketingOrderDetail();
				BeanUtils.copyProperties(mod, viewDetail);
				allViewDetails.add(viewDetail);
			}

			Map<Integer, List<ViewMarketingOrderDetail>> detailsByMoId = allViewDetails.stream()
					.collect(Collectors.groupingBy(ViewMarketingOrderDetail::getMarketingOrderId));

			// 转换
			ViewMarketingOrderDto moDto;
			for (MarketingOrder mo : moList) {
				moDto = new ViewMarketingOrderDto();
				BeanUtils.copyProperties(mo, moDto);
				moDtoList.add(moDto);
				// 设置详情
				moDto.setDetailList(detailsByMoId.get(mo.getId()));
			}
			// 查询记录数
			totalCount = marketingOrderMapper.getMarketingOrderCount(param);
		}
		return new Pager<ViewMarketingOrderDto>(moDtoList, currPage, pageSize, totalCount);

	}

	@Override
	public Map<String, Object> audit(AuditParams params) {
		Map<String, Object> result = Maps.newHashMap();
		// 查询营销单
		MarketingOrder mo = marketingOrderMapper.selectByMarketingOrderNo(params.getMarketingOrderNo());

		if (Objects.isNull(mo)) {
			result.put("suc", false);
			result.put("msg", "不存在单号为" + params.getMarketingOrderNo() + "的营销单");
			return result;
		}

		List<MarketingOrderDetail> details = marketingOrderDetailMapper.selectByMoIdList(Arrays.asList(mo.getId()));
		if (details.size() <= 0) {
			result.put("suc", false);
			result.put("msg", "网络异常，请稍后重试！");
			return result;
		}

		String orderNo = mo.getMarketingOrderNo();
		int status = 0;
		if (params.getPassed() != 0) {// 审核通过
			Map<String, Object> lockMap = lock(orderNo);
			if (!"true".equals(lockMap.get("suc").toString())) {
				result.put("suc", false);
				result.put("msg", lockMap.get("msg").toString());
				return result;
			}

			// 初审：审核通过就是变为待复审；复审审核通过就是 审核通过
			status = params.getType() == 1 ? MarketingOrderStatus.WAITING_AUDIT_SECONDLY
					: MarketingOrderStatus.AUDIT_PASSED;
		} else {// 审核不通过
			status = MarketingOrderStatus.AUDIT_NOT_PASSED;
			// 释放锁库
			try {
				httpService.unLock(orderNo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int updateCount = marketingOrderMapper.updateStatusById(mo.getId(), status, params.getAuditUser());
		if (updateCount != 1) {
			result.put("suc", false);
			result.put("msg", "审核失败");
			return result;
		}

		// 更新营销单成功
		// 记录日志
		saveAuditLog(status, mo.getMarketingOrderNo(), params.getPassed(), params.getRemarks(), params.getAuditUser(),
				params.getType());
		// 是否通过，通过的话，要生成发货单
		if (status == MarketingOrderStatus.AUDIT_PASSED) {
			// 查询更新后的营销单
			MarketingOrder newMO = marketingOrderMapper.selectByMarketingOrderNo(params.getMarketingOrderNo());
			// 异步创建发货单
			ebus.post(new GenerateSaleOrderEvent(params.getAuditUser(), newMO, details));
		}
		result.put("suc", true);
		result.put("msg", "审核成功");
		return result;
	}

	/**
	 * 保存日志
	 * 
	 * @param status
	 * @param marketingOrderNo
	 * @param passed
	 * @param remarks
	 * @param auditUser
	 * @param auditType
	 *            1初审，2复审
	 */
	private void saveAuditLog(Integer status, String marketingOrderNo, Integer passed, String remarks, String auditUser,
			Integer auditType) {
		MarketingOrderAuditLog log = new MarketingOrderAuditLog();
		log.setStatus(status);
		log.setMarketingOrderNo(marketingOrderNo);
		log.setPassed(passed);
		log.setRemarks(remarks);
		log.setAuditUser(auditUser);
		log.setAuditDate(new Date());
		log.setAuditType(auditType);
		int count = logMapper.insert(log);
		if (count == 1) {
			Logger.info("记录审核日志成功，{}", log);
		} else {
			Logger.info("记录审核日志失败，{}", log);
		}
	}

	@Override
	public void generateOrder(GenerateSaleOrderEvent event) {
		// 营销单
		MarketingOrder mo = event.getMarketOrder();
		try {
			JsonNode res = httpService.updateStock(mo.getMarketingOrderNo());
			// 将销售单号更新到营销单
			MarketingOrder record = new MarketingOrder();
			String saleNo = null;
			record.setId(mo.getId());
			Logger.info("营销单云仓出库:{}",res);
			if(res.get("result").asBoolean()){
				saleNo = saveOrder(event);
				record.setSalesOrderNo(saleNo);
				Logger.info("营销单:{}的发货单:{}",mo,saleNo);
			}else{
				//如果微仓入库失败，则把营销单更新为待复审
				record.setStatus(MarketingOrderStatus.WAITING_AUDIT_SECONDLY);
			}
			marketingOrderMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			Logger.info("云仓入库异常", e);
		}
	}

	private String saveOrder(GenerateSaleOrderEvent event) {
		// 营销单
		MarketingOrder mo = event.getMarketOrder();
		// 营销单详情
		List<MarketingOrderDetail> details = event.getDetails();
		SaleMain sm = new SaleMain();
		String orderNo = IDUtils.getSalesCode(sequenceService.selectNextValue("SALE_NO"));
		sm.setSalesOrderNo(orderNo);
		sm.setOrderingDate(new Date());
		sm.setStatus(SaleOrderStatus.WAITING_DELIVERY_SIX);// 待发货
		sm.setPaymentNo(IDUtils.getPayNo());
		sm.setCreatedFrom(50);// 营销单
		sm.setPayDate(new Date());
		sm.setPaymentType("system");
		sm.setCurrency("CNY");
		sm.setSource("BBC");
		sm.setEmail(mo.getEmail());
		sm.setIsAdjusted(0);
		sm.setTradeNo(System.currentTimeMillis() + "");// 交易号
		sm.setWarehouseId(details.get(0).getWarehouseId());
		sm.setWarehouseName(details.get(0).getWarehouseName());
		sm.setNickName(mo.getNickName());
		sm.setDisMode(mo.getDisMode());
		sm.setDistributorType(mo.getDistributorType());
		// SALE_BASE
		SaleBase sb = new SaleBase();
		sb.setAddress(
				String.join(" ", mo.getProvinceName(), mo.getCityName(), mo.getRegionName(), mo.getAddressDetail()));
		sb.setLogisticsMode(mo.getLogisticsMode());
		sb.setOrderActualAmount(0.0);
		sb.setBbcPostage(mo.getBbcPostage());
		sb.setLogisticsTypeCode(mo.getLogisticsTypeCode());
		sb.setOrderTotalAmount(mo.getTotalAmount());
		sb.setCustomerservice(mo.getSalesman());
		sb.setReceiver(mo.getReceiver());
		sb.setIdcard("");
		sb.setTel(mo.getReceiverTel());
		sb.setPostCode(mo.getReceiverPostcode());
		sb.setCreateUser(mo.getCreateUser());
		sb.setIsBack(true);
		sb.setOrderingDate(new Date());
		sb.setRemark(mo.getBusinessRemark());
		//为了防止订单数据构造未完成就推送到erp，先把订单更新为已推送
		sm.setIsPushed(1);
		saleMainMapper.insertSelective(sm);

		OperateRecord autoConfirm = new OperateRecord();
		autoConfirm.setOrderId(sm.getId());
		autoConfirm.setOperateType(2);
		autoConfirm.setResult(1);
		autoConfirm.setComment("营销单审核通过后自动发货。");
		autoConfirm.setEmail("system");
		operateRecordMapper.insertSelective(autoConfirm);
		
		sb.setSalesOrderId(sm.getId());
		saleBaseMapper.insertSelective(sb);
		// SALE_DETAIL
		List<SaleDetail> sds = Lists.newArrayList();
		// 保存销售发货单商品详细表
		SaleDetail sd = null;
		try {
			for (MarketingOrderDetail moDetail : details) {
				sd = new SaleDetail();
				sd.setProductName(moDetail.getProductName());
				sd.setInterBarCode(moDetail.getInterBarCode());
				sd.setPurchasePrice(moDetail.getDisPrice());// 分销价
				sd.setFinalSellingPrice(moDetail.getDisPrice());
				sd.setSku(moDetail.getSku());
				sd.setQty(moDetail.getQty());
				sd.setWarehouseId(moDetail.getWarehouseId());
				sd.setWarehouseName(moDetail.getWarehouseName());
				sd.setSalesOrderId(sm.getId());
				sd.setProductImg(moDetail.getProductImg());
				sd.setSalesOrderNo(sm.getSalesOrderNo());
				sd.setIsDeducted(1);
				sd.setIsgift(false);
				if (StringUtils.isNotBlankOrNull(moDetail.getExpirationDate())) {
					sd.setExpirationDate(
							DateUtils.string2date(moDetail.getExpirationDate(), DateUtils.FORMAT_DATE_PAGE));
				}
				saleDetailMapper.insertSelective(sd);
				sds.add(sd);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// 计算操作费
		asyncCalculateOptFee(sm, sb, sds);

		// change by zbc
		JsonNode checkNode = lockService.saleOut(sm, sds, mo.getMarketingOrderNo(), false);
		JsonNode successOrLocks = null;
		String type = checkNode.get("type").asText();
		if ("1".equals(type)) {
			successOrLocks = checkNode.get("successOrLocks");
			// 缺货
		}
		if (successOrLocks != null) {
			Iterator<JsonNode> node = successOrLocks.iterator();
			// 标记赠品
			while (node.hasNext()) {
				JsonNode jsonNode = (JsonNode) node.next();
				// 插入历史数据
				lockService.saveHistory(sm, jsonNode);
			}
		}
		//构造数据完成后，把订单更新为未推送
		kdnService.requestOrderOnline(sm);
		sm.setIsPushed(0);
		saleMainMapper.updateByPrimaryKeySelective(sm);
		Logger.info("营销单出库:[{}]", checkNode);
		return orderNo;
	}

	/**
	 * 尝试锁库，锁库成功
	 * 
	 * @author zbc
	 * @since 2017年1月5日 上午9:17:20
	 */
	@Override
	public Map<String, Object> lock(String orderNo) {
		Map<String, Object> res = Maps.newHashMap();

		MarketingOrder order = marketingOrderMapper.selectByMarketingOrderNo(orderNo);
		if (order == null) {
			res.put("suc", false);
			res.put("msg", "订单不存在");
			return res;
		}

		List<MarketingOrderDetail> list = marketingOrderDetailMapper.getDetailsByNo(orderNo);
		Map<String, Object> postMap = Maps.newHashMap();
		List<CloudLockPros> lockList = Lists.newArrayList();
		// 查询商品价格
		Map<String, Double> priceMap = Maps.newHashMap();

		try {
			// 查询到仓价
			getArwPrice(list, priceMap);
			getLockDetails(list, priceMap, lockList);
			postMap.put("accountName", order.getNickName());
			postMap.put("orderNo", orderNo);
			postMap.put("account", order.getEmail());
			postMap.put("pros", lockList);
			// 如果为缺货采购
			JsonNode resNode = httpService.cloudLock(postMap);
			Logger.info("云仓锁库结果:[{}]", resNode);
			boolean suc = resNode.get("result").asBoolean();

			String msg = JsonCaseUtil.jsonToString(resNode.get("msg"));
			if (!suc) {
				for (JsonNode obj : resNode.get("objList")) {
					msg += "[" + obj.get("sku").asText() + "缺" + obj.get("stock").asInt() + "]";
				}
			}
			res.put("suc", suc);
			res.put("msg", msg);
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
	private void getArwPrice(List<MarketingOrderDetail> list, Map<String, Double> priceMap)
			throws JsonProcessingException, IOException {
		List<String> skus = Lists.transform(list, f -> f.getSku());
		JsonNode priceNode = httpService.getPriceList(skus);
		if (priceNode.get("suc").asBoolean()) {
			for (JsonNode price : priceNode.get("pages").get("list")) {
				String key = price.get("sku").asText() + "_" + price.get("disStockId").asInt();
				priceMap.put(key, JsonCaseUtil.jsonToDouble(price.get("arriveWarePrice")));
			}
		}
	}

	/**
	 * 构造锁库数据
	 * 
	 * @author zbc
	 * @since 2016年12月28日 下午4:55:45
	 */
	private void getLockDetails(List<MarketingOrderDetail> list, Map<String, Double> priceMap,
			List<CloudLockPros> locksList) {
		CloudLockPros lock;
		for (MarketingOrderDetail detail : list) {
			lock = new CloudLockPros(detail);
			String key = detail.getSku() + "_" + detail.getWarehouseId();
			// 查询到仓价
			lock.setArriveWarePrice(priceMap.get(key));
			locksList.add(lock);
		}
	}

	/**
	 * 异步计算操作费
	 * 
	 * @param sm
	 * @param sb
	 * @param details
	 */
	private void asyncCalculateOptFee(SaleMain sm, SaleBase sb, List<SaleDetail> details) {
		CalculateOptFeeEvent calculateOptFeeEvent = new CalculateOptFeeEvent();
		calculateOptFeeEvent.setBase(sb);
		calculateOptFeeEvent.setMain(sm);
		calculateOptFeeEvent.setDetails(details);
		ebus.post(calculateOptFeeEvent);
	}

	@Override
	public File smExport(String fileName, Map<String, String[]> map) {
		List<MarketingOrderDto> moDtoList = null;
		Integer orgId = ParametersUtil.getIntegerParam(map, "orgId", null);
		try {
			JsonNode orgNode = httpService.getOrgInfo(orgId);
			Iterator<JsonNode> it = orgNode.iterator();
			String accouts = "";
			JsonNode node = null;
			Map<String, String> branchMap = Maps.newHashMap();
			while (it.hasNext()) {
				node = it.next();
				String email = JsonCaseUtil.jsonToString(node.get("email"));
				accouts += email + ",";
				branchMap.put(email, JsonCaseUtil.jsonToString(node.get("branchName")));
			}
			QueryMarketingOrderParams params = parseQueryMoParams(map, accouts.substring(0, accouts.length() - 1));
			moDtoList = getMarketingOrderList(params);
			moDtoList.forEach(e -> {
				e.setBranchName(branchMap.get(e.getEmail()));
			});
		} catch (IOException e) {
			e.printStackTrace();
			moDtoList = Lists.newArrayList();
		}
		Logger.info("导出营销单数据条数为 ： " + moDtoList.size());
		return ExportMarketingOrderUtil.saleManExport(fileName, moDtoList);
	}

	@Override
	public Pager<ViewMarketingOrderDto> query(Map<String, String[]> map) {
		// 只能查看关联的分销商的营销单
		String accounts = userService.getRelateAccounts();
		QueryMarketingOrderParams params = parseQueryMoParams(map, accounts);
		return getMarketingOrderPage(params);
	}

	private QueryMarketingOrderParams parseQueryMoParams(Map<String, String[]> map, String accounts) {
		QueryMarketingOrderParams params = new QueryMarketingOrderParams();
		if (map != null && map.size() > 0) {
			params.setPageSize(ParametersUtil.getIntegerParam(map, "rows", 10));
			params.setCurrPage(ParametersUtil.getIntegerParam(map, "page", 1));
			params.setStartDate(ParametersUtil.getStringParam(map, "startDate", null));
			params.setEndDate(ParametersUtil.getStringParam(map, "endDate", null));
			params.setDistributorType(ParametersUtil.getIntegerParam(map, "distributorType", null));
			params.setStatus(ParametersUtil.getIntegerParam(map, "status", null));
			params.setSearchText(ParametersUtil.getStringParam(map, "searchText", null));
			// 排序的参数
			params.setFilter(ParametersUtil.getStringParam(map, "sord", null));
			params.setSort(ParametersUtil.getStringParam(map, "sidx", null));
		}

		if (StringUtils.isNotBlankOrNull(accounts)) {
			List<String> accountList = null;
			if (!StringUtils.isBlankOrNull(accounts)) {
				accountList = Arrays.asList(accounts.split(","));
			}
			params.setRelatedMembers(accountList);
		}

		return params;
	}

	@Override
	public File exportMoList(String filename, Map<String, String[]> map) {
		String accounts = userService.getRelateAccounts();
		QueryMarketingOrderParams params = parseQueryMoParams(map, accounts);
		List<ExportMarketOrderInfo> exportList = getExportInfo(params);
		Logger.info("导出营销单数据条数为 ： " + exportList.size());
		return ExportMarketingOrderUtil.export(filename, exportList);
	}

	private List<ExportMarketOrderInfo> getExportInfo(QueryMarketingOrderParams params) {
		params.setCurrPage(null);
		params.setPageSize(null);
		// 查询营销单
		List<MarketingOrder> moList = marketingOrderMapper.getMarketingOrderList(params);
		List<ExportMarketOrderInfo> exportList = Lists.newArrayList();

		if (moList.size() > 0) {
			Map<Integer, MarketingOrder> moMap = Maps.uniqueIndex(moList, MarketingOrder::getId);
			// 批量查询详情
			List<List<Integer>> midsLists = CollectionUtils.createList(Lists.transform(moList, mo -> mo.getId()), 150);
			List<MarketingOrderDetail> allDetails = Lists.newArrayList();
			for (List<Integer> ids : midsLists) {
				allDetails.addAll(marketingOrderDetailMapper.selectByMoIdList(ids));
			}
			Map<Integer, List<MarketingOrderDetail>> detailsByMoId = allDetails.stream()
					.collect(Collectors.groupingBy(MarketingOrderDetail::getMarketingOrderId));
			ExportMarketOrderInfo info;
			for (MarketingOrderDetail detail : allDetails) {
				info = new ExportMarketOrderInfo();
				BeanUtils.copyProperties(detail, info);
				BeanUtils.copyProperties(moMap.get(detail.getMarketingOrderId()), info);
				info.setTotalQty(
						detailsByMoId.get(detail.getMarketingOrderId()).stream().mapToInt(e -> e.getQty()).sum());
				exportList.add(info);
			}
		}
		return exportList;
	}

	@Override
	public Pager<ViewMarketingOrderDto> smMoList(Map<String, String[]> map) {
		Pager<ViewMarketingOrderDto> pages;
		Integer orgId = ParametersUtil.getIntegerParam(map, "orgId", null);
		try {
			JsonNode orgNode = httpService.getOrgInfo(orgId);
			Iterator<JsonNode> it = orgNode.iterator();
			StringBuilder accoutsBuilder = new StringBuilder("");
			JsonNode node = null;
			Map<String, String> branchMap = Maps.newHashMap();
			while (it.hasNext()) {
				node = it.next();
				String email = JsonCaseUtil.jsonToString(node.get("email"));
				accoutsBuilder.append(email).append(",");
				branchMap.put(email, JsonCaseUtil.jsonToString(node.get("branchName")));
			}
			String accouts = accoutsBuilder.toString();
			QueryMarketingOrderParams params = parseQueryMoParams(map, accouts.substring(0, accouts.length() - 1));
			pages = getMarketingOrderPage(params);
			pages.getDatas().forEach(e -> {
				e.setBranchName(branchMap.get(e.getEmail()));
			});
		} catch (IOException e) {
			e.printStackTrace();
			pages = new Pager<>(Lists.newArrayList(), 1, 10, 0);
		}
		return pages;
	}
}
