package services.sales.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import dto.sales.ExportSaleLogistic;
import dto.sales.ExportSaleOrderInfo;
import dto.sales.SaleOrderInfo;
import dto.sales.SaleOrderListDto;
import dto.sales.SalesOrderInfo4FinanceDto;
import dto.sales.SalesPriceDto;
import dto.sales.ShopDto;
import dto.sales.ViewSaleOrder;
import dto.sales.ViewSaleOrderDetail;
import dto.sales.audit.AuditByCustomerServiceParam;
import entity.sales.AuditRemark;
import entity.sales.OperateRecord;
import entity.sales.SaleBase;
import entity.sales.SaleBuffer;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;
import entity.sales.ShOrder;
import events.sales.CaculateChargeEvent;
import events.sales.SaveReceiverAddressEvent;
import mapper.sales.AuditRemarkMapper;
import mapper.sales.OperateRecordMapper;
import mapper.sales.SaleBaseMapper;
import mapper.sales.SaleBufferMapper;
import mapper.sales.SaleDetailMapper;
import mapper.sales.SaleMainMapper;
import mapper.sales.ShOrderMapper;
import pager.sales.Pager;
import play.Logger;
import play.libs.Json;
import services.sales.IHttpService;
import services.sales.IKdnService;
import services.sales.IManagerOrderService;
import services.sales.ISaleService;
import services.sales.ISequenceService;
import services.sales.IUserService;
import session.ISessionService;
import util.sales.Constant;
import util.sales.DateUtils;
import util.sales.ExportUtil;
import util.sales.HttpUtil;
import util.sales.IDUtils;
import util.sales.JsonCaseUtil;
import util.sales.PageUtil;
import util.sales.PriceFormatUtil;
import util.sales.SaleOrderStatus;
import util.sales.SalesStatusUtil;
import util.sales.StringUtils;

public class SaleService implements ISaleService {

	@Inject
	private SaleMainMapper saleMainMapper;
	@Inject
	private SaleBaseMapper saleBaseMapper;
	@Inject
	private SaleDetailMapper saleDetailMapper;
	@Inject
	private OperateRecordMapper operateRecordMapper;
	@Inject
	private SaleBufferMapper saleBufferMapper;
	@Inject
	private ISequenceService sequenceService;
	@Inject
	private IHttpService httpService;
	@Inject
	private SaleMainService saleMainService;
	@Inject
	private IUserService userService;
	@Inject
	private EventBus ebus;
	@Inject
	private AuditRemarkMapper remarkMapper;
	@Inject
	private ShOrderMapper shOrderMapper;
	@Inject
	private IManagerOrderService managerOrderService;
	@Inject
	private ISessionService  sessionService;

	@Inject
	private IKdnService kdnService;

	
	//处理中
	private static boolean IS_PROCESSING = false;

	@Override
	public int getSalesOrderCount(String param, List<String> accounts) {
		JsonNode main = Json.parse(param);
		Calendar cal = Calendar.getInstance();
		//时间参数上，天数或者月份数选一个，首选天数
		if (main.has("months")) {
			int months = main.get("months").asInt();
			cal.add(Calendar.MONTH, -months);
		} else if (main.has("days")) {
			int days = main.get("days").asInt();
			cal.add(Calendar.DAY_OF_MONTH, -days);
		}
		String from = main.has("status") ? null : DateUtils.date2string(cal.getTime(), DateUtils.XLSX_DATE_TIME_FORMAT);
		String to = main.has("status") ? null : DateUtils.date2string(new Date(), DateUtils.XLSX_DATE_TIME_FORMAT);
		String email = main.has("email") ? main.get("email").textValue() : null;
		Integer status = main.has("status") ? main.get("status").asInt() : null;
		int i = saleMainMapper.selectSalesOrderCount(from, to, email, status, accounts);
		return i;
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

	@Override
	public Pager<SaleOrderListDto> selectSaleOrderListDto(JsonNode main,String email) {

		Logger.info("selectSaleOrderListDto查询参数：main={}，email={}",main,email);
		String statusStr = main.get("status").asText();
		List<Integer> statusList = splitStatusStr(statusStr);
		int pageSize = main.get("pageSize").asInt();
		int currPage = main.get("currPage").asInt();
		String desc = main.get("desc").textValue();
		if(StringUtils.isBlankOrNull(email)){
			email = main.get("email").textValue();
		}
		List<SaleOrderListDto> saleOrderList = saleMainMapper.selectSaleOrderListDto(statusList,desc,email,pageSize,currPage);

		// 批量查询订单详情
		if(saleOrderList!=null && saleOrderList.size()>0){
			List<Integer> orderIdList = saleOrderList.stream().map(SaleOrderListDto::getId).collect(Collectors.toList());
			List<SaleDetail> saleDetailList = saleDetailMapper.selectByOrderIdList(orderIdList);
			// 根据订单id来分组
			Map<Integer, List<SaleDetail>> saleDetailListByOrderId = saleDetailList.stream().collect(Collectors.groupingBy(SaleDetail::getSalesOrderId));
			// 为每个订单设置订单详情
			for (SaleOrderListDto order : saleOrderList) {
				// 表明是前端查询的
				order.setFrontOrManager(SaleMain.STATUS_FRONT);

				if(order.getConfirmReceiptDate() != null && order.getStatus() == 10) {
					DateTime d1 = new DateTime(order.getConfirmReceiptDate());
					order.setShFlag(Days.daysBetween(d1, new DateTime()).getDays() > 7 ? false : true);
				} else {
					order.setShFlag(false);
				}

				List<SaleDetail> saleDetails = saleDetailListByOrderId.get(order.getId());
				// 防止单没有详情
				saleDetails = CollectionUtils.isEmpty(saleDetails) ? Lists.newArrayList() : saleDetails;
				
				for (SaleDetail saleDetail : saleDetails) {
					saleDetail.setCount(0);// 首先设置为0
					
					ShOrder shOrder = shOrderMapper.selectEffectiveShOrderByDetailOrderId(saleDetail.getId());
					if(shOrder != null) {
						saleDetail.setShOrderId(shOrder.getId());
						saleDetail.setStatus(shOrder.getStatus());
						saleDetail.setCount(1);
					} else {
						saleDetail.setCount(0);
					}
				}
				order.setSaleDetails(saleDetails);
			}
		}
		return new Pager<SaleOrderListDto>(saleOrderList, currPage, pageSize,saleMainMapper.selectSaleOrderListDtoCount(statusList, desc,email));
	}

	@Override
	public List<SaleDetail> selectSaleOrderDetail(JsonNode main) {
		return saleDetailMapper.selectByOrderId(main.get("orderId").asInt());
	}

	@Override
	public Map<String,Object> selectSaleOrderListOfBackstage(JsonNode json,String relateAccount) {
		// 关联分销商
		List<String> accounts = null;
		if (!StringUtils.isBlankOrNull(relateAccount)) {
			accounts = Arrays.asList(relateAccount.split(","));
		}
		Map<String,Object> paramMap = new HashMap<String,Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<SaleOrderInfo> saleOrderInfos = new ArrayList<SaleOrderInfo>();
		int total = 0, pages = 0, pageCount = 1, pageSize = 10;
        if (json.get("pageSize")!=null && json.get("pageCount")!=null) {
        	pageCount = json.get("pageCount").asInt();
        	pageSize = json.get("pageSize").asInt();
		}
        paramMap.put("currPage", pageCount);
        paramMap.put("pageSize", pageSize);
        // 状态拆分
		paramMap.put("statusList", splitStatusStr(StringUtils.getStringBlank(json.path("status").asText(), true)));
		paramMap.put("orderStartDate", JsonCaseUtil.jsonToString(json.path("orderStartDate")));
		paramMap.put("orderEndDate", JsonCaseUtil.jsonToString(json.path("orderEndDate")));
		paramMap.put("noticeStartDate", JsonCaseUtil.jsonToString(json.path("noticeStartDate")));
		paramMap.put("noticeEndDate", JsonCaseUtil.jsonToString(json.path("noticeEndDate")));
		paramMap.put("seachSpan", JsonCaseUtil.jsonToString(json.path("seachSpan")));
		paramMap.put("warehouseId", JsonCaseUtil.jsonToInteger(json.path("warehouseId")));
		paramMap.put("distributorType", JsonCaseUtil.jsonToInteger(json.path("distributorType")));
		paramMap.put("disMode", JsonCaseUtil.jsonToInteger(json.path("disMode")));
		paramMap.put("accounts", accounts);
		paramMap.put("sort", JsonCaseUtil.jsonToString(json.path("sort")));
		paramMap.put("filter", JsonCaseUtil.jsonToString(json.path("sidx")));
		paramMap.put("source", JsonCaseUtil.jsonToString(json.path("source")));
		paramMap.put("isCombine", JsonCaseUtil.jsonToBoolean(json.get("isCombine")));
		if(json.has("email")){
			paramMap.put("email", JsonCaseUtil.jsonToString(json.path("email")));
		}
		
		Logger.info("selectSaleOrderListOfBackstage参数，paramMap：{}",paramMap);
		List<SaleMain> allSaleMain = saleMainMapper.selectAllSaleMain(paramMap);
		//获取销售订单其他信息
		if (allSaleMain!=null && allSaleMain.size()>0) {
			List<Integer> orderIdList = allSaleMain.stream().map(SaleMain::getId).collect(Collectors.toList());
			// 查询base
			List<SaleBase> saleBaseList = saleBaseMapper.selectByOrderIdList(orderIdList);
			// 按照order id分组
			Map<Integer, SaleBase> orderId2SaleBase = saleBaseList.stream().collect(Collectors.toMap(SaleBase::getSalesOrderId,Function.identity()));
			// 查询操作记录
			List<OperateRecord> operateRecordList = operateRecordMapper.selectByOrderIdList(orderIdList);
			// 按照order id分组
			Map<Integer, List<OperateRecord>> operateRecordsByOrderId = operateRecordList.stream().collect(Collectors.groupingBy(OperateRecord::getOrderId));
			
			for (SaleMain saleMain : allSaleMain) {
				// 表明是后台查询的
				saleMain.setFrontOrManager(SaleMain.STATUS_MANAGER);
				
				// 操作记录
				List<OperateRecord> opRecordList = operateRecordsByOrderId.get(saleMain.getId());
				if(opRecordList==null){// 如果为null，给一个空list
					opRecordList = new ArrayList<>();
				}
				SaleOrderInfo saleOrderInfo = new SaleOrderInfo();
				// 基础信息
				saleOrderInfo.setSaleBase(orderId2SaleBase.get(saleMain.getId()));
				saleOrderInfo.setSaleMain(saleMain);
				saleOrderInfo.setOpRecordList(opRecordList);
				saleOrderInfos.add(saleOrderInfo);
			}
			
		}
		
		total = this.saleMainMapper.selectAllSaleMainCount(paramMap);
		pages = PageUtil.calculateTotalPage(total,pageSize);
		resultMap.put("result", true);
		resultMap.put("total", total);
		resultMap.put("pages",pages);
		resultMap.put("pageCount", pageCount);
		resultMap.put("msg", (saleOrderInfos!=null && saleOrderInfos.size()>0)?"成功获取销售订单":"没有销售订单");
		resultMap.put("saleOrderInfos", saleOrderInfos);
		return resultMap;
	}
	
	@Override
	public Map<String,Object> selectSaleOrderListOfBackstage4finance(JsonNode json,String relateAccount) {
		// 关联分销商
		List<String> accounts = null;
		if (!StringUtils.isBlankOrNull(relateAccount)) {
			accounts = Arrays.asList(relateAccount.split(","));
		}
		Map<String,Object> paramMap = new HashMap<String,Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		int total = 0, pages = 0, pageCount = 1, pageSize = 10;
        if (json.get("pageSize")!=null && json.get("pageCount")!=null) {
        	pageCount = json.get("pageCount").asInt();
        	pageSize = json.get("pageSize").asInt();
		}
        paramMap.put("currPage", pageCount);
        paramMap.put("pageSize", pageSize);
        // 状态拆分
		paramMap.put("statusList", splitStatusStr(StringUtils.getStringBlank(json.path("status").asText(), true)));
		paramMap.put("seachSpan", JsonCaseUtil.jsonToString(json.path("seachSpan")));
		paramMap.put("accounts", accounts);
		paramMap.put("sort", JsonCaseUtil.jsonToString(json.path("sort")));
		paramMap.put("filter", JsonCaseUtil.jsonToString(json.path("sidx")));
		
		Logger.info("selectSaleOrderListOfBackstage4finance参数，paramMap：{}",paramMap);
		List<SalesOrderInfo4FinanceDto> dtoList = saleMainMapper.selectAllSaleMain4finance(paramMap);
		if (CollectionUtils.isNotEmpty(dtoList)) {
			total = saleMainMapper.selectAllSaleMainCount(paramMap);
			pages = PageUtil.calculateTotalPage(total,pageSize);
		}
		resultMap.put("result", true);
		resultMap.put("total", total);
		resultMap.put("pages",pages);
		resultMap.put("pageCount", pageCount);
		resultMap.put("msg", (dtoList!=null && dtoList.size()>0)?"成功获取销售订单":"没有销售订单");
		resultMap.put("dtoList", dtoList);
		return resultMap;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Map<String, Object> selectSaleOrdersOfBackstageForTel(String param,List<String> accounts) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		Map<String, Object> resultMap = Maps.newHashMap();
		JsonNode json = Json.parse(param);
		List<ObjectNode> saleOrderInfos = new ArrayList<ObjectNode>();
		int total = 0;
		int pages = 0;
		int currPage = 1;
        int pageSize = 10;
        if (json.get("pageSize")!=null && json.get("currPage")!=null) {
        	currPage = json.get("currPage").asInt();
        	pageSize = json.get("pageSize").asInt();
		}
        paramMap.put("currPage", currPage);
        paramMap.put("pageSize", pageSize);
        // 状态处理
        String statusStr = StringUtils.getStringBlank(json.get("status").asText(), true);
        List<Integer> statusList = splitStatusStr(statusStr);
		paramMap.put("statusList", statusList);
		paramMap.put("orderStartDate", StringUtils.getStringBlank(json.path("orderStartDate").asText(), true));
		paramMap.put("orderEndDate", StringUtils.getStringBlank(json.path("orderEndDate").asText(), true));
		paramMap.put("noticeStartDate", StringUtils.getStringBlank(json.path("noticeStartDate").asText(), true));
		paramMap.put("noticeEndDate", StringUtils.getStringBlank(json.path("noticeEndDate").asText(), true));
		paramMap.put("seachSpan", StringUtils.getStringBlank(json.path("seachSpan").asText(), true));
		paramMap.put("warehouseId", StringUtils.getStringBlank(json.path("warehouseId").asText(), true));
		paramMap.put("distributorType", StringUtils.getStringBlank(json.path("distributorType").asText(), true));
		paramMap.put("accounts", accounts);
		if(json.has("email")){
			paramMap.put("email", StringUtils.getStringBlank(json.path("email").asText(), true));
		}
		
		List<SaleMain> allSaleMain = this.saleMainMapper.selectAllSaleMain(paramMap);
		//获取销售订单其他信息
		ObjectNode map = null;
		//List<SaleDetail> details = null;
		if (allSaleMain!=null && allSaleMain.size()>0) {
			List<Integer> orderIdList = allSaleMain.stream().map(SaleMain::getId).collect(Collectors.toList());
			// 批量查询订单详情
			List<SaleDetail> saleDetailList = saleDetailMapper.selectByOrderIdList(orderIdList);
			// 按照订单id来分组
			Map<Integer, List<SaleDetail>> saleDetailListByOrderId = saleDetailList.stream().collect(Collectors.groupingBy(SaleDetail::getSalesOrderId));
			
			for (SaleMain saleMain : allSaleMain) {
				// 后台查询
				saleMain.setFrontOrManager(SaleMain.STATUS_MANAGER);
				map = Json.newObject();
				map.put("id", saleMain.getId());
				map.put("salesOrderNo", saleMain.getSalesOrderNo());
				map.put("status", saleMain.getStatus());
				map.put("statusDesc", saleMain.getStatusDesc());
				// 根据订单id拿到订单的订单详情，然后拿第一个
				map.put("details", Json.toJson(saleDetailListByOrderId.get(saleMain.getId()).get(0)));
				saleOrderInfos.add(map);
			}
		}
		total = saleMainMapper.selectAllSaleMainCount(paramMap);
		pages = PageUtil.calculateTotalPage(total,pageSize);
		resultMap.put("result", true);
		resultMap.put("total", total);
		resultMap.put("pages",pages);
		resultMap.put("currPage", currPage);
		resultMap.put("msg", (saleOrderInfos!=null && saleOrderInfos.size()>0)?"成功获取销售订单":"没有销售订单");
		resultMap.put("saleOrderInfos", saleOrderInfos);
		return resultMap;
	}

	@Override
	public List<String> getExportOrderListOfBackstage(Map<String, String[]> map) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		String account = userService.getRelateAccounts();
        List<String> accounts = null;
        if(!StringUtils.isBlankOrNull(account)) {
        	accounts = Arrays.asList(account.split(","));
        	paramMap.put("accounts", accounts);
        }
		String status = StringUtils.getStringParam(map, "status", "");
		
		String orderStartDate = StringUtils.getStringParam(map, "orderStartDate", "");
		String orderEndDate = StringUtils.getStringParam(map, "orderEndDate","");
		String warehouseId = StringUtils.getStringParam(map, "warehouseId","");
		String seachSpan = StringUtils.getStringParam(map, "seachSpan","");
		String distributorType = StringUtils.getStringParam(map, "distributorType","0");
		String source = StringUtils.getStringParam(map, "source","");
		paramMap.put("statusList", splitStatusStr(StringUtils.getStringBlank(status, true)));
		paramMap.put("warehouseId", warehouseId);
		paramMap.put("orderStartDate", orderStartDate);
		paramMap.put("orderEndDate", orderEndDate);
		paramMap.put("seachSpan", seachSpan);
		paramMap.put("distributorType", distributorType);
		paramMap.put("source", source);
		Logger.info("getExportOrderListOfBackstage in service，参数：{}",paramMap);
		Map<Integer,ShopDto> shopMap = getShop();
		int pageSize = 5000;
		//循环查询
		int size = 1;
		paramMap.put("pageSize", pageSize);
		int currPage = 1;
		List<SaleMain> currSaleMain = null;
		List<String> keys = Lists.newArrayList();
		String key = null;
		long startTime;
		while (true) {
			paramMap.put("currPage",currPage);
			startTime = System.currentTimeMillis();
			currSaleMain = saleMainMapper.selectAllSaleMain(paramMap);
			Logger.info("查询时间:{}",System.currentTimeMillis()- startTime);
			size = currSaleMain.size();
			if(size == 0 ){
				break;
			}
			key = getExportOrderInfo(currSaleMain,shopMap);
			if(key != null){
				keys.add(key);
			}
			if(size< 5000 ){
				break;
			}
			currPage++; 
		}
		return keys;
	}
	
	private  Map<Integer,ShopDto> getShop(){
		ObjectMapper obj = new ObjectMapper();
        //获取所有店铺信息
        Map<Integer,ShopDto> shopMap = Maps.newHashMap();
        try {
        	 String shopRes = HttpUtil.get(Maps.newHashMap(),HttpUtil.B2BBASEURL+"/member/getallstore");
        	 List<ShopDto> shops =  obj.readValue(shopRes, new TypeReference<List<ShopDto>>(){});
        	 shopMap = shops.stream().collect(
     	            Collectors.toMap(ShopDto::getId, (p) -> p)); 
        } catch (Exception e) {
			Logger.error("获取店铺信息错误",e);
		}
        return shopMap;
	}
	
	/*@Override
	public List<ExportSaleOrderInfo> getExportOrderListOfBackstage(Map<String, String[]> map) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		String account = userService.getRelateAccounts();
        List<String> accounts = null;
        if(!StringUtils.isBlankOrNull(account)) {
        	accounts = Arrays.asList(account.split(","));
        	paramMap.put("accounts", accounts);
        }
		String status = StringUtils.getStringParam(map, "status", "");
		
		String orderStartDate = StringUtils.getStringParam(map, "orderStartDate", "");
		String orderEndDate = StringUtils.getStringParam(map, "orderEndDate","");
		String warehouseId = StringUtils.getStringParam(map, "warehouseId","");
		String seachSpan = StringUtils.getStringParam(map, "seachSpan","");
		String distributorType = StringUtils.getStringParam(map, "distributorType","0");
		String source = StringUtils.getStringParam(map, "source","");
		paramMap.put("statusList", splitStatusStr(StringUtils.getStringBlank(status, true)));
		paramMap.put("warehouseId", warehouseId);
		paramMap.put("orderStartDate", orderStartDate);
		paramMap.put("orderEndDate", orderEndDate);
		paramMap.put("seachSpan", seachSpan);
		paramMap.put("distributorType", distributorType);
		paramMap.put("source", source);
		Logger.info("getExportOrderListOfBackstage in service，参数：{}",paramMap);
		List<SaleMain> allSaleMain = saleMainMapper.selectAllSaleMain(paramMap);
		List<ExportSaleOrderInfo> list = getExportOrderInfo(allSaleMain);
		if(null == list) {
			return Lists.newArrayList();
		}
		return list;
	}*/
	
	/**
	 * 描述：通过主销售单获取导出订单数据
	 * 2016年4月15日
	 * @param allSaleMain
	 * @return
	 */
	/*private List<ExportSaleOrderInfo> getExportOrderInfo(List<SaleMain> allSaleMain){
		List<ExportSaleOrderInfo> exportSaleOrderInfoList = new ArrayList<ExportSaleOrderInfo>();
		Logger.info("查询时间Start：" + new Date());
		ObjectMapper obj = new ObjectMapper();
        //获取所有店铺信息
        Map<Integer,ShopDto> shopMap = Maps.newHashMap();
        try {
        	 String shopRes = HttpUtil.get(Maps.newHashMap(),HttpUtil.B2BBASEURL+"/member/getallstore");
        	 List<ShopDto> shops =  obj.readValue(shopRes, new TypeReference<List<ShopDto>>(){});
        	 shopMap = shops.stream().collect(
     	            Collectors.toMap(ShopDto::getId, (p) -> p)); 
        } catch (Exception e) {
			Logger.error("获取店铺信息错误",e);
		}
        ShopDto shop = null;
        List<Integer> mainIds = Lists.transform(allSaleMain, main -> main.getId());
        if(mainIds == null || mainIds.size() <= 0) {
        	return exportSaleOrderInfoList;
        }
        List<List<Integer>> midsList = util.sales.CollectionUtils.createList(mainIds, 100);
        Map<Integer, SaleMain> id2SaleMain = Maps.uniqueIndex(allSaleMain, saleMain -> saleMain.getId());
        //base信息  分段取值
        List<SaleBase> bases = Lists.newArrayList();
		List<SaleDetail> saleDetails = Lists.newArrayList();
		List<SaleDetail> effectiveDetails = null;
		List<SaleBase> subBases = null;
        for (List<Integer> ids : midsList) {
        	effectiveDetails = saleDetailMapper.selectEffectiveDetails(ids);
			subBases = saleBaseMapper.selectBases(ids);
			saleDetails.addAll(effectiveDetails);
        	bases.addAll(subBases);
		}
        Map<Integer, SaleBase> mainId2SaleBase = Maps.uniqueIndex(bases, b -> b.getSalesOrderId());
        SaleBase saleBase = null;
        SaleMain saleMain = null;
        ExportSaleOrderInfo exportOrder = null;
        for (SaleDetail sd : saleDetails) {
        	saleBase = mainId2SaleBase.get(sd.getSalesOrderId());
        	saleMain = id2SaleMain.get(sd.getSalesOrderId());
        	if(saleBase == null || saleMain == null) {
        		 continue; 		
        	}
        	saleMain.setFrontOrManager(SaleMain.STATUS_MANAGER);
        	exportOrder = new ExportSaleOrderInfo();
        	exportOrder.setEmail(saleMain.getEmail());
        	exportOrder.setPlatformAmount(saleMain.getPlatformAmount());
        	exportOrder.setNickName(saleMain.getNickName());
			exportOrder.setBbcPostage(saleBase.getBbcPostage());
			exportOrder.setSaleOrderNO(saleMain.getSalesOrderNo());
			exportOrder.setOrderId(saleMain.getId());
			exportOrder.setPlatformOrderNo(saleBase.getPlatformOrderNo());
			exportOrder.setBuyerId(saleBase.getBuyerID());
			exportOrder.setReceiver(saleBase.getReceiver());
			exportOrder.setTel(saleBase.getTel());
			exportOrder.setAddress(saleBase.getAddress());
			exportOrder.setSku(sd.getSku());
			exportOrder.setProductName(sd.getProductName());
			exportOrder.setQty(sd.getQty());
			exportOrder.setStatus(saleMain.getStatusDesc());
			exportOrder.setOrderActualAmount(saleBase.getOrderActualAmount());
//			exportOrder.setPayAccount("");付款账户暂时没有 后期补充
			exportOrder.setOrderActualPayAmount(saleMain.getOrderActualPayment());
//			exportOrder.setCost(sd.getCost());
			exportOrder.setReceiverIDcard(saleBase.getIdcard());
//			exportOrder.setDisAccount(saleMain.getEmail());
			exportOrder.setDistributorType(Constant.DIS_TYPE_AMP.get(saleMain.getDistributorType()));
			exportOrder.setWarehouseName(saleMain.getWarehouseName());
			exportOrder.setTradeNo(saleMain.getTradeNo());
			exportOrder.setOrderDateStr(saleMain.getOrderingDateStr());
			exportOrder.setFinalSellingPrice(
					sd.getFinalSellingPrice() != null ? PriceFormatUtil.toFix2(sd.getFinalSellingPrice())
							: PriceFormatUtil.toFix2(sd.getPurchasePrice()));
			exportOrder.setExpirationDate(DateUtils.date2string(sd.getExpirationDate(), "yyyy-MM-dd"));
			//获得店铺id
			Integer shopId = saleBase.getShopId();
			shop = shopMap.get(shopId);
			exportOrder.setShopName(shop != null ? shopMap.get(shopId).getShopName() : "");
			exportOrder.setRemark(saleBase.getRemark());
			exportOrder.setErp(saleBase.getCustomerservice());
			exportOrder.setDistributionModelStr(saleMain.getDisMode() == null ? "" : getDistributionModelStr(saleMain.getDisMode()));
			exportOrder.setCapfee(sd.getCapFee() == null ? PriceFormatUtil.toFix2(sd.getPurchasePrice())
					: PriceFormatUtil.toFix2(sd.getCapFee()));
			exportSaleOrderInfoList.add(exportOrder);
		}
        Logger.info("查询时间END：" + new Date());
		return exportSaleOrderInfoList;
	}*/
	
	
	private String getExportOrderInfo(List<SaleMain> allSaleMain,Map<Integer,ShopDto> shopMap){
		ArrayList<ExportSaleOrderInfo> exportSaleOrderInfoList = Lists.newArrayList();
		Logger.info("查询时间Start：" + new Date());
        List<Integer> mainIds = Lists.transform(allSaleMain, main -> main.getId());
        if(mainIds == null || mainIds.size() <= 0) {
        	return null;
        }
        Map<Integer, SaleMain> id2SaleMain = Maps.uniqueIndex(allSaleMain, saleMain -> saleMain.getId());
        
        List<List<Integer>> midsList = util.sales.CollectionUtils.createList(mainIds, 100);
        //base信息  分段取值
        List<SaleBase> bases = Lists.newArrayList();
		List<SaleDetail> saleDetails = Lists.newArrayList();
		List<SaleDetail> effectiveDetails = null;
		List<SaleBase> subBases = null;
        for (List<Integer> ids : midsList) {
        	effectiveDetails = saleDetailMapper.selectEffectiveDetails(ids);
			subBases = saleBaseMapper.selectBases(ids);
			saleDetails.addAll(effectiveDetails);
        	bases.addAll(subBases);
		}
        Map<Integer, SaleBase> mainId2SaleBase = Maps.uniqueIndex(bases, b -> b.getSalesOrderId());
        ShopDto shop  = null;
        //base信息  分段取值
		SaleBase saleBase = null;
        SaleMain saleMain = null;
        ExportSaleOrderInfo exportOrder = null;
        for (SaleDetail sd : saleDetails) {
        	saleBase = mainId2SaleBase.get(sd.getSalesOrderId());
        	saleMain = id2SaleMain.get(sd.getSalesOrderId());
        	if(saleBase == null || saleMain == null) {
        		 continue; 		
        	}
        	saleMain.setFrontOrManager(SaleMain.STATUS_MANAGER);
        	exportOrder = new ExportSaleOrderInfo();
        	exportOrder.setEmail(saleMain.getEmail());
        	exportOrder.setErpOrderNo(saleMain.getErpOrderNo());
        	exportOrder.setNickName(saleMain.getNickName());
			exportOrder.setBbcPostage(saleBase.getBbcPostage());
			exportOrder.setSaleOrderNO(saleMain.getSalesOrderNo());
			exportOrder.setOrderId(saleMain.getId());
			exportOrder.setPlatformOrderNo(saleBase.getPlatformOrderNo());
			exportOrder.setBuyerId(saleBase.getBuyerID());
			exportOrder.setReceiver(saleBase.getReceiver());
			exportOrder.setTel(saleBase.getTel());
			exportOrder.setAddress(saleBase.getAddress());
			exportOrder.setSku(sd.getSku());
			exportOrder.setProductName(sd.getProductName());
			exportOrder.setQty(sd.getQty());
			exportOrder.setStatus(saleMain.getStatusDesc());
			exportOrder.setOrderActualAmount(saleBase.getOrderActualAmount());
//			exportOrder.setPayAccount("");付款账户暂时没有 后期补充
			exportOrder.setOrderActualPayAmount(saleMain.getOrderActualPayment());
			exportOrder.setPlatformAmount(saleMain.getPlatformAmount());
//			exportOrder.setCost(sd.getCost());
			exportOrder.setReceiverIDcard(saleBase.getIdcard());
//			exportOrder.setDisAccount(saleMain.getEmail());
			exportOrder.setDistributorType(Constant.DIS_TYPE_AMP.get(saleMain.getDistributorType()));
			exportOrder.setWarehouseName(saleMain.getWarehouseName());
			exportOrder.setTradeNo(saleMain.getTradeNo());
			exportOrder.setOrderDateStr(saleMain.getOrderingDate() + "");
			exportOrder.setFinalSellingPrice(
					sd.getFinalSellingPrice() != null ? PriceFormatUtil.toFix2(sd.getFinalSellingPrice())
							: PriceFormatUtil.toFix2(sd.getPurchasePrice()));
			exportOrder.setExpirationDate(DateUtils.date2string(sd.getExpirationDate(), "yyyy-MM-dd"));
			//获得店铺id
			Integer shopId = saleBase.getShopId();
			shop = shopMap.get(shopId);
			exportOrder.setShopName(shop != null ? shopMap.get(shopId).getShopName() : "");
			exportOrder.setRemark(saleBase.getRemark());
			exportOrder.setErp(saleBase.getCustomerservice());
			exportOrder.setDistributionModelStr(saleMain.getDisMode() == null ? "" : getDistributionModelStr(saleMain.getDisMode()));
			exportOrder.setCapfee(sd.getCapFee() == null ? PriceFormatUtil.toFix2(sd.getPurchasePrice())
					: PriceFormatUtil.toFix2(sd.getCapFee()));
			exportSaleOrderInfoList.add(exportOrder);
		}
		String key = null;
	    Logger.info("查询时间END：" + new Date());
		if(exportSaleOrderInfoList.size()>0){
			key = IDUtils.getUUID();
			sessionService.set(key, exportSaleOrderInfoList);
		}
		return key;
	}

	/**
	 * 获取分销渠道
	 *
	 * @param disModel
	 * @return
	 */
	private String getDistributionModelStr(Integer disModel) {
		//1、电商，2、经销商，3、KA直营，4、进口专营
		switch (disModel) {
			case  1:
				return "电商";
			case 2:
				return "经销商";
			case 3:
				return "KA直营";
			case 4:
				return "进口专营";
			default:
				return "";
		}
	}


	/** 
	 * 增加判断优惠码是否可用
	 * (non-Javadoc)
	 * @see services.sales.ISaleService#(com.fasterxml.jackson.databind.JsonNode)
	 */
	@Override
	public JsonNode checkCoupons(String couponsCode, Double amount) {
		try {
			Map<String, String> params = Maps.newHashMap();
			params.put("couponsNo", couponsCode);
			params.put("orderAmount", amount + "");
			String res = HttpUtil.get(params, HttpUtil.B2BBASEURL + "/member/getCouponsInfo");
			Logger.info("检查优惠码状态：" + res);
			ObjectMapper obj = new ObjectMapper();
			return obj.readTree(res);
		} catch (Exception e) {
			Logger.error(e + "");
			return null;
		}
	}


	/**
	 * 获取需要支付的金额
	 * @author zbc
	 * @since 2016年12月20日 上午10:02:48
	 */
	@Override
	public BigDecimal getAmount(String pNo,SaleBase base) throws JsonProcessingException, IOException {
		BigDecimal tram,couponsAmount,bbcPostage;
		if(pNo != null){
			BigDecimal purchaseTotalAmount;
			if(base.getOrderTotalAmount() != null && base.getOrderTotalAmount() > 0){
				purchaseTotalAmount = new BigDecimal(base.getOrderTotalAmount()); 
				couponsAmount = new BigDecimal(JsonCaseUtil.getValue(base.getCouponsAmount()));
				bbcPostage = new BigDecimal(JsonCaseUtil.getValue(base.getBbcPostage()));
			}else{
				JsonNode  purOrder = httpService.getPurByNo(pNo);
				JsonNode order = purOrder.get("orders").get(0);
				couponsAmount = new BigDecimal(JsonCaseUtil.getValue(JsonCaseUtil.jsonToDouble(order.get("couponsAmount"))));
				bbcPostage = new BigDecimal(JsonCaseUtil.getValue(JsonCaseUtil.jsonToDouble(order.get("bbcPostage"))));
				purchaseTotalAmount = new BigDecimal(JsonCaseUtil.getValue(JsonCaseUtil.jsonToDouble(order.get("purchaseTotalAmount"))));
				//保存支付金额
				base.setOrderTotalAmount(purchaseTotalAmount.doubleValue());
				saleBaseMapper.updateByPrimaryKeySelective(base);
			}
			tram = purchaseTotalAmount.subtract(couponsAmount).add(bbcPostage);
		}else{
			couponsAmount = new BigDecimal(JsonCaseUtil.getValue(base.getCouponsAmount()));
			bbcPostage = new BigDecimal(JsonCaseUtil.getValue(base.getBbcPostage()));
			tram = bbcPostage.subtract(couponsAmount);
		}
		return  new BigDecimal(PriceFormatUtil.toFix2(tram));
	}

	@Override
	public List<SaleDetail> parseSaleDetail(SaleMain sm, JsonNode productStrNode, Map<String, Integer> skuNumMap,
			JsonNode memberNode, Map<String, Double> finalPriceMap) {
		List<SaleDetail> sds = Lists.newArrayList();

		// 保存销售发货单商品详细表
		SaleDetail sd = null;
		//根据分销商模式 计算分销
		// 分销价
		Double purchase = 0.0;
		//仓库id
		Integer warehouseId = null;
		String sku = "";
		JsonNode products = productStrNode.get("data").get("result");
		for (Iterator<JsonNode> it = products.iterator();it.hasNext();) {
			JsonNode jsonNode = (JsonNode) it.next();
			sku = jsonNode.get("csku").textValue();
		    warehouseId = jsonNode.get("warehouseId").asInt();
			sd = new SaleDetail();
			sd.setProductName(jsonNode.get("ctitle").textValue());
			purchase = jsonNode.get("disPrice").asDouble();
			
			sd.setPurchasePrice(purchase);// 分销价
			sd.setDisPrice(purchase);
			sd.setSku(sku);
			sd.setQty(skuNumMap.get(sku));
			sd.setWarehouseId(warehouseId);
			sd.setWarehouseName(jsonNode.get("warehouseName").textValue());
			sd.setSalesOrderId(sm.getId());
			sd.setProductImg(jsonNode.get("imageUrl").textValue());
			sd.setSalesOrderNo(sm.getSalesOrderNo());
			sd.setIsDeducted(0);

			sd.setGstRate(jsonNode.get("gstRate").asDouble());// 消费税税率
			sd.setVatRate(jsonNode.get("vatRate").asDouble());// 增值税税率
			sd.setImportTarRate(jsonNode.get("importTarRate").asDouble());// 关税税率
			sd.setPostalFeeRate(jsonNode.get("postalFeeRate").asDouble());// 行邮税税率
			sd.setLogisticFee(jsonNode.get("logisticFee").asDouble());// 头程运费
			
			// 最终售价上下浮不能超过分销价的 50%
			Double finalSellingPrice = finalPriceMap == null ? purchase : finalPriceMap.get(sku);
			// 设置其他平台最终售价
			if (sm.getDistributorType() != 3 && sm.getWarehouseId() != 2024) {
				sd.setFinalSellingPrice(finalSellingPrice > purchase * 1.5 ? purchase * 1.5
						: finalSellingPrice < purchase * 0.5
								? PriceFormatUtil.toFix2(purchase * 0.5) : finalSellingPrice);
			} else {
				sd.setFinalSellingPrice(finalSellingPrice);
			}
			saleDetailMapper.insertSelective(sd);
			sds.add(sd);
		}
		return sds;
	}
	
	/**
	 * @param sm
	 * @param main
	 * @param shopStrNode
	 * @param custStrNode
	 * @param skuObj
	 * @return
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	@Override
	public SaleBase parseSaleBase(SaleMain sm, JsonNode main, JsonNode shopStrNode, JsonNode custStrNode,
			JsonNode skuObj) throws JsonProcessingException, IOException {
		SaleBase sb = new SaleBase();
		// 获取物流信息、运费信息
		//获取运费
		if(main.has("LogisticsTypeCode") && main.has("provinceId")) {
			//第三方物流code
			if("BBC-TPL".equals(main.get("LogisticsTypeCode").asText())&&main.has("thirdPostfee")){
				sb.setBbcPostage(JsonCaseUtil.jsonToDouble(main.get("thirdPostfee")));
				sb.setOriginalFreight(JsonCaseUtil.jsonToDouble(main.get("thirdPostfee")));
			}else{
				JsonNode res = httpService.getFreight(sm.getWarehouseId(), JsonCaseUtil.getStringValue(main, "LogisticsTypeCode"), skuObj, main.get("provinceId").asInt(), 44, main.has("cityId") ? main.get("cityId").asInt() : null, main.has("model") ? main.get("model").asInt() : null);
				String thirdPartLogisticsTypeCode= res.get("externalCode")==null?null:res.get("externalCode").asText();
				sb.setLogisticsTypeCode(main.get("LogisticsTypeCode").asText());
				sb.setThirdPartLogisticsTypeCode(thirdPartLogisticsTypeCode);
				sb.setBbcPostage(res.get("msg").asDouble());
				sb.setOriginalFreight(res.get("msg").asDouble());
			}
			sb.setLogisticsMode(main.get("logisticsMode").asText());
		}else{
			// 兼容拷贝发货单，因为发货单中没有保存provinceId
			sb.setLogisticsTypeCode(JsonCaseUtil.getStringValue(main, "LogisticsTypeCode"));
			sb.setBbcPostage(JsonCaseUtil.getDoubleValue(main, "bbcPostage"));
			sb.setOriginalFreight(JsonCaseUtil.getDoubleValue(main, "bbcPostage"));
			sb.setLogisticsMode(JsonCaseUtil.getStringValue(main, "logisticsMode"));
		}

		if (sm.getIsPackageMail().intValue() == 1) {//包邮
			sb.setBbcPostage(0d);
		}

		sb.setPlatformOrderNo(JsonCaseUtil.getStringValue(main,"platformOrderNo"));// 平台单号
		sb.setOrderActualAmount(JsonCaseUtil.jsonToDouble(main.get("orderActualAmount")));// 实付款
		//前台不传该字段		
		sb.setOrderTotalAmount(JsonCaseUtil.jsonToDouble(main.get("orderTotalAmount")));// 订单总金额
		sb.setOrderPostage(JsonCaseUtil.getDoubleValue(main, "orderPostage"));// 运费
		sb.setShopId(main.has("shopId") ? main.get("shopId").asInt() : null);// 店铺ID
		sb.setCollectAccount(JsonCaseUtil.getStringValue(main, "collectAccount"));// 收款账号
		sb.setCouponsAmount(JsonCaseUtil.getDoubleValue(main, "couponsAmount"));// 优惠码金额
		sb.setCouponsCode(JsonCaseUtil.getStringValue(main, "couponsCode"));// 优惠码
		
		if(null != shopStrNode) {
			sb.setPlatformName(JsonCaseUtil.getStringValue(shopStrNode, "shopName"));
			sb.setPlatformType(JsonCaseUtil.getIntegerValue(shopStrNode, "platformId"));			
		}
		// xuse 关联客服账号
		sb.setCustomerservice(custStrNode.get("account").textValue());

		String address = "";
		if (main.has("address")) {
			// 格式为空格隔开
			address = main.get("address").textValue();
		} 
		String receiver = "";
		if (main.has("receiver")) {
			receiver = main.get("receiver").textValue();
		} 
		String tel = "";
		if (main.has("telphone")) {
			tel = main.get("telphone").textValue();
		}
		String idcard = "";
		if (main.has("idcard")) {
			idcard = main.get("idcard").textValue();
		}
		String postCode = "";
		if (main.has("postCode")) {
			postCode = main.get("postCode").textValue();
		} 
		sb.setAddress(address);// 详细地址
		sb.setReceiver(receiver);// 收件人
		sb.setTel(tel == "null" ? null : tel);// 收件人电话
		sb.setIdcard(idcard == "null" ? null : idcard);// 收件人身份证
		sb.setPostCode(postCode == "null" ? null : postCode);// 收货地址邮编
		if (main.has("orderer")) {
			sb.setOrderer(main.get("orderer").textValue());// 订购人姓名
		}
		if (main.has("ordererIDCard")) {
			sb.setOrdererIDCard(main.get("ordererIDCard").textValue());// 订购人身份证
		}
		if (main.has("ordererTel")) {
			sb.setOrdererTel(main.get("ordererTel").textValue());// 订购人电话
		}
		if (main.has("buyerID")) {
			sb.setBuyerID(main.get("buyerID").textValue());// 客户昵称
		}
		if (main.has("isBack")) {
			sb.setIsBack(main.get("isBack").asBoolean());
		}
		if (main.has("createUser")) {
			sb.setCreateUser(main.get("createUser").asText());
		}
		try {
			// 顾客下单时间
			if (main.has("orderingDate") && !main.get("orderingDate").textValue().equals("")) {
				sb.setOrderingDate(
						DateUtils.string2date(main.get("orderingDate").textValue(), DateUtils.FORMAT_FULL_DATETIME));
			} else {
				sb.setOrderingDate(new Date());
			}
		} catch (ParseException e) {
			Logger.error(e + "");
		}
		sb.setRemark(JsonCaseUtil.getStringValue(main, "remark"));
		
		// 异步保存收货人地址
		ebus.post(new SaveReceiverAddressEvent(sm, sb));
		
		return sb;
	}
	
	/**
	 * 与parseSaleBase方法的区别是：没有平台单号platformOrderNo
	 * @param sm
	 * @param main
	 * @param shopStrNode
	 * @param custStrNode
	 * @param skuObj
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	@Override
	public SaleBase parseSaleBase4OpenApi(SaleMain sm, JsonNode main, JsonNode shopStrNode, JsonNode custStrNode,
										  JsonNode skuObj) throws JsonProcessingException, IOException {
		SaleBase sb = new SaleBase();
		Integer proId = null;
		Integer cityId = null;
		String receiver;
		Map<String, String> shoppingNames = Maps.newHashMap();
		// 获取物流信息、运费信息
		// 1、根据仓库获取所有物流方式
		JsonNode methodNode = httpService.getShoppingMethod(sm.getWarehouseId());
		if (methodNode == null) {
			return null;
		}
		JsonNode shop = shopStrNode.has("data") ? shopStrNode.get("data") : shopStrNode;
		String address = "";
		if (main.has("address")) {
			// 格式为空格隔开
			address = main.get("address").textValue();
			// 获取省份ID
			proId = main.has("provinceId") ? JsonCaseUtil.getIntegerValue(main, "provinceId") : httpService.getProvinces(address.split(" ")[0]).get("id").asInt();
			cityId = main.has("cityId") ? main.get("cityId").asInt() : null;
		} else {
			address = JsonCaseUtil.getStringValue(shop, "provinceName") + " " + JsonCaseUtil.getStringValue(shop, "cityName") + " "
					+ JsonCaseUtil.getStringValue(shop, "areaName") + " " + JsonCaseUtil.getStringValue(shop, "addr");
			proId = JsonCaseUtil.getIntegerValue(shop, "provinceId");
			cityId = JsonCaseUtil.getIntegerValue(shop,"cityId");
		}
		Iterator<JsonNode> it = methodNode.iterator();
		boolean hasDefault = false;
		JsonNode node = null;
        while (it.hasNext()) {
            node = (JsonNode) it.next();
            if (node.get("default").asBoolean()) {
                hasDefault = true;
                break;
            }
        }
		// 获取默认的物流
		if ((main.has("LogisticsTypeCode")) || (hasDefault && node != null)) {
			String code = main.has("LogisticsTypeCode") ? JsonCaseUtil.getStringValue(main, "LogisticsTypeCode") : node.get("methodCode").asText();
			String methodName = main.has("logisticsMode") ? JsonCaseUtil.getStringValue(main, "logisticsMode") : node.get("methodName").asText();
			Integer model = main.has("model") ? main.get("model").asInt() : null;
			JsonNode res = httpService.getFreight(sm.getWarehouseId(), code, skuObj,proId,44,cityId, model);
			String thirdPartLogisticsTypeCode= res.get("externalCode")==null?null:res.get("externalCode").asText();
			sb.setThirdPartLogisticsTypeCode(thirdPartLogisticsTypeCode);
			sb.setLogisticsTypeCode(code);
			sb.setBbcPostage(res.get("msg").asDouble());
			sb.setOriginalFreight(res.get("msg").asDouble());
			sb.setLogisticsMode(methodName);
		} else {
			//没默认的话就取最便宜的
			ArrayList<Entry<String, Double>> shoppingInfo = postShoppingMethod(skuObj, proId, cityId,sm.getWarehouseId(), shoppingNames, methodNode);
			if (shoppingInfo != null && shoppingInfo.size() > 0) {
				Double bbcPostage = shoppingInfo.get(0).getValue();
				String code = shoppingInfo.get(0).getKey();
				
				for(JsonNode tmepNode :methodNode){//匹配运费最低的物流在第三方中的代码
					String methCode = tmepNode.get("methodCode").asText();
					if(methCode.equals("code")){
						String thirdPartLogisticsTypeCode= tmepNode.get("externalCode")==null?null:tmepNode.get("externalCode").asText();
						sb.setThirdPartLogisticsTypeCode(thirdPartLogisticsTypeCode);
					}
				}
				
				if (bbcPostage <= 0 && shoppingInfo.size() >= 2) {
					bbcPostage = shoppingInfo.get(1).getValue();
					code = shoppingInfo.get(1).getKey();
				}
				sb.setLogisticsTypeCode(code);
				sb.setBbcPostage(bbcPostage);
				sb.setOriginalFreight(bbcPostage);

				sb.setLogisticsMode(shoppingNames.get(code));
			} else {
				return null;
			}
		}

		if (sm.getIsPackageMail().intValue() == 1) {//包邮
			sb.setBbcPostage(0d);
		}

		sb.setOrderActualAmount(JsonCaseUtil.getDoubleValue(main, "orderActualAmount"));// 实付款
		sb.setOrderPostage(0.0);// 运费
		sb.setCustomerservice(JsonCaseUtil.getStringValue(custStrNode, "account"));// 客服账号
		
		if(null != shop) {
			sb.setShopId(JsonCaseUtil.getIntegerValue(shop, "id"));// 店铺ID
			sb.setCollectAccount(JsonCaseUtil.getStringValue(shop, "shroffAccountNumber"));// 收款账号
			sb.setPlatformName(JsonCaseUtil.getStringValue(shop, "shopName"));
			sb.setPlatformType(JsonCaseUtil.getIntegerValue(shop, "platformId"));			
		}
		if (main.has("receiver")) {
			receiver = main.get("receiver").textValue();
		} else {
			receiver = shop.get("keeperName").asText();
		}
		String tel = "";
		if (main.has("telphone")) {
			tel = main.get("telphone").textValue();
		} else {
			tel = shop.get("telphone").asText();
		}
		String idcard = "";
		if (main.has("idcard")) {
			idcard = main.get("idcard").textValue();
		} else {
			idcard = shop.get("idcard").asText();
		}
		String postCode = "";
		if (main.has("postCode")) {
			postCode = main.get("postCode").textValue();
		} else {
			postCode = shop.get("zipCode").asText();
		}
		sb.setAddress(address);// 详细地址
		sb.setReceiver(receiver);// 收件人
		sb.setTel(tel == "null" ? null : tel);// 收件人电话
		sb.setIdcard(idcard == "null" ? null : idcard);// 收件人身份证
		sb.setPostCode(postCode == "null" ? null : postCode);// 收货地址邮编
		if (main.has("orderer")) {
			sb.setOrderer(main.get("orderer").textValue());// 订购人姓名
		}
		if (main.has("ordererIDCard")) {
			sb.setOrdererIDCard(main.get("ordererIDCard").textValue());// 订购人身份证
		}
		if (main.has("ordererTel")) {
			sb.setOrdererTel(main.get("ordererTel").textValue());// 订购人电话
		}
		if (main.has("buyerID")) {
			sb.setBuyerID(main.get("buyerID").textValue());// 客户昵称
		}
		try {
			// 顾客下单时间
			if (main.has("orderingDate") && !main.get("orderingDate").textValue().equals("")) {
				sb.setOrderingDate(
						DateUtils.string2date(main.get("orderingDate").textValue(), DateUtils.FORMAT_FULL_DATETIME));
			} else {
				sb.setOrderingDate(new Date());
			}
		} catch (ParseException e) {
			Logger.error(e+"");
		}
		sb.setRemark(JsonCaseUtil.getStringValue(main, "remark"));
		return sb;
	}
	
	@Override
	public ArrayList<Entry<String, Double>> postShoppingMethod(JsonNode skuObj, Integer proId,Integer cityId, Integer warehouseId,
			Map<String, String> freightName, JsonNode methodNode) throws JsonProcessingException, IOException {
		Map<String, Object> freight = Maps.newHashMap();
		JsonNode res = null;
		JsonNode jsonNode = null;
		String code = "";
		Double fee = 0.0;  
		if (methodNode.isArray()) {
			for (Iterator<JsonNode> it = methodNode.iterator();it.hasNext();) {
				jsonNode = (JsonNode) it.next();
				code = jsonNode.get("methodCode").asText();

				Integer model = jsonNode.has("model") ? jsonNode.get("model").asInt() : null;
				res = httpService.getFreight(warehouseId, code, skuObj, proId,44,cityId, model);
				if(parseString(res.asText()).get("result").asBoolean()) {
					fee = parseString(res.asText()).get("msg").asDouble();

					freight.put(code, fee);
					freightName.put(code, jsonNode.get("methodName").asText());
				}
			}
		} else {
			code = methodNode.get("methodCode").asText();

			Integer model = methodNode.has("model") ? methodNode.get("model").asInt() : null;
			res = httpService.getFreight(warehouseId, code, skuObj, proId, cityId, 44, model);
			if(parseString(res.asText()).get("result").asBoolean()) {
				fee =parseString(res.asText()).get("msg").asDouble();
				freight.put(code, fee);
				freightName.put(code, methodNode.get("methodName").asText());
			}
		}
		Logger.info("物流与运费：" + Json.toJson(freight));
		return sortMap(freight);
	}

	@Override
	public SaleMain parseSaleMain(JsonNode main) {
		SaleMain sm = new SaleMain();
		// 发货单
		sm.setSalesOrderNo(IDUtils.getSalesCode(sequenceService.selectNextValue("SALE_NO")));
		if (main.has("isVirtualWarehouse") && main.get("isVirtualWarehouse").asBoolean()) {
			sm.setOrderchannel(4);// 订单渠道：4进口虚拟仓
		}
		
		sm.setOrderingDate(new Date());
		sm.setStatus(SaleOrderStatus.WAITING_AUDIT_BY_CS);// 待审核
		sm.setEmail(main.get("email").textValue());
		sm.setIsAdjusted(0);
		/**************** 目前因为不允许跨仓下单，所以在主表上维护仓库信息 *******************/
		sm.setTradeNo(JsonCaseUtil.getStringValue(main, "tradeNo").trim());// 交易号
		sm.setWarehouseId(JsonCaseUtil.getIntegerValue(main, "warehouseId"));
		sm.setWarehouseName(JsonCaseUtil.getStringValue(main,"warehouseName"));
		return sm;
	}

	private JsonNode parseString(String str) throws JsonProcessingException, IOException {
		ObjectMapper obj = new ObjectMapper();
		return obj.readTree(str);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList<Entry<String, Double>> sortMap(Map oldMap) {
		ArrayList<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(oldMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<java.lang.String, Double> arg0, Entry<java.lang.String, Double> arg1) {
				return arg0.getValue().compareTo(arg1.getValue());
			}
		});
		return list;
	}
	
	
	/**
	 * 系统自动确认收货
	 * 完税   深圳仓：2024 									   10天
	 * 保税   南沙保税仓：2029，杭州保税仓:2012   福州仓 2050           15天
	 * 直邮   英国：6，美国：73  澳大利亚：70                        45天
	 */
	@Override
	public void autoConfirmReceipt() {
		List<SaleMain> list = saleMainMapper.getShippedSales();
		List<String> orderlist =Lists.newArrayList();
		//全部更新状态为 确认发货
		for(SaleMain sale :list){
			sale.setStatus(SaleOrderStatus.RECEIVED);
			sale.setConfirmReceiptDate(new Date());
			orderlist.add(sale.getSalesOrderNo());
		}
		boolean flag = saleMainMapper.batchUpdateStatus(list)>0;
		Logger.info("自动确认收货订单:[{}],更新结果:[{}]",orderlist,flag);
	}
	
	@Override
	public List<ExportSaleLogistic> selectSaleOrederLogistics(Map<String, Object> params) {
		return saleMainMapper.selectSaleOrederLogistics(params);
	}

	@Override
	public boolean checkTradeNo(String tradeNo) {
		return saleMainMapper.countTradeNo(tradeNo) >= 1;
	}

	@Override
	public Map<String, Object> openQuery(JsonNode json) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<ViewSaleOrder> list = new ArrayList<ViewSaleOrder>();
		int total = 0;
		int pages = 0;
		int pageCount = 1;
        int pageSize = 10;
        if (json.get("pageSize")!=null && json.get("pageCount")!=null) {
        	pageCount = json.get("pageCount").asInt();
        	pageSize = json.get("pageSize").asInt();
		}
        paramMap.put("currPage", pageCount);
        paramMap.put("pageSize", pageSize);
		paramMap.put("status", StringUtils.getStringBlank(json.path("status").asText(), true));
		paramMap.put("orderStartDate", StringUtils.getStringBlank(json.path("orderStartDate").asText(), true));
		paramMap.put("orderEndDate", StringUtils.getStringBlank(json.path("orderEndDate").asText(), true));
		paramMap.put("noticeStartDate", StringUtils.getStringBlank(json.path("noticeStartDate").asText(), true));
		paramMap.put("noticeEndDate", StringUtils.getStringBlank(json.path("noticeEndDate").asText(), true));
		paramMap.put("seachSpan", StringUtils.getStringBlank(json.path("seachSpan").asText(), true));
		paramMap.put("warehouseId", StringUtils.getStringBlank(json.path("warehouseId").asText(), true));
		paramMap.put("distributorType", StringUtils.getStringBlank(json.path("distributorType").asText(), true));
		if(json.has("email")){
			paramMap.put("email", StringUtils.getStringBlank(json.path("email").asText(), true));
		}
		List<SaleMain> allSaleMain = this.saleMainMapper.selectAllSaleMain(paramMap);
		ViewSaleOrder view = null;
		ViewSaleOrderDetail detail = null;
		List<ViewSaleOrderDetail> details = null;
		//获取销售订单其他信息
		if (allSaleMain!=null && allSaleMain.size()>0) {
			for (SaleMain saleMain : allSaleMain) {
				view = new ViewSaleOrder();
				details = Lists.newArrayList();
				SaleBase saleBase = saleBaseMapper.selectByOrderId(saleMain.getId());
				BeanUtils.copyProperties(saleBase, view);
				BeanUtils.copyProperties(saleMain, view);
				view.setStatus(SalesStatusUtil.getKey(saleMain.getStatus()+""));
				List<SaleDetail> detailList =  saleDetailMapper.selectByOrderId(saleMain.getId());
				for(SaleDetail d:detailList){
					detail = new ViewSaleOrderDetail();
					BeanUtils.copyProperties(d,detail);
					details.add(detail);
				}
				view.setDetails(details);
				list.add(view);
			}
		}
		total = this.saleMainMapper.selectAllSaleMainCount(paramMap);
		pages = PageUtil.calculateTotalPage(total,pageSize);
		resultMap.put("result", true);
		resultMap.put("total", total);
		resultMap.put("pages",pages);
		resultMap.put("pageCount", pageCount);
		resultMap.put("msg", (list!=null && list.size()>0)?"成功获取销售订单":"没有销售订单");
		resultMap.put("saleOrderInfos", list);
		return resultMap;
	}
	
	@Override
	public String saveBufferMemory(String params){
		ObjectNode result = Json.newObject();
		JsonNode node = Json.parse(params);
	    String data = node.get("data").toString();
	    SaleBuffer buffer = new SaleBuffer();
	    buffer.setEmail(userService.getDisAccount());
	    //清空此用户上次保存的缓存
	    saleBufferMapper.deleteByEmail(buffer);
	    buffer.setDataDetail(data);
	    buffer.setCreateDate(new Date());
	    int flag = saleBufferMapper.insertSelective(buffer);
	    Logger.info("saveBufferMemory------->" + flag);
	    if (flag == 0) {
	    	result.put("suc", false);
	    	result.put("msg", "保存缓存失败");
	    	return result.toString();
	    }
	    result.put("suc", false);
	    result.put("msg", "保存缓存成功");
		return result.toString();
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getBufferMemory() {
		ObjectNode result = Json.newObject();
		List<SaleBuffer> buffers = saleBufferMapper.selectByEmail(userService.getDisAccount());
		if (buffers == null || buffers.size() <= 0) {
			result.put("suc", false);
			result.put("msg", "没有查到缓存信息");
			return result.toString();
		}
		result.put("suc", true);
		result.put("data", Json.toJson(buffers.get(0).getDataDetail()));
		return result.toString();
	}

	@Override
	public String clearBufferMemory() {
		ObjectNode result = Json.newObject();
		SaleBuffer buffer = new SaleBuffer();
		buffer.setEmail(userService.getDisAccount());
		int flag = saleBufferMapper.deleteByEmail(buffer);
		result.put("data", flag);
		return result.toString();
	}
	
	/**
	 * 获取待付款金额
	 * @param newHashMap
	 * @param
	 * @return
	 */
	private BigDecimal getAmountToBePaid(HashMap<String, Object> newHashMap, SaleMain saleMain, SaleBase saleBase) {
		// 待付款
		BigDecimal amountToBePaid = new BigDecimal(0);

		BigDecimal orderTotalAmount = new BigDecimal(0);
		BigDecimal bbcPostage = new BigDecimal(0);
		BigDecimal couponsAcount = new BigDecimal(0);
		
		if (saleBase.getOrderTotalAmount() != null) {
			// 发货单支付金额+运费-优惠
			orderTotalAmount = new BigDecimal(saleBase.getOrderTotalAmount());
			bbcPostage = new BigDecimal(saleBase.getBbcPostage() != null ? saleBase.getBbcPostage() : 0);
			couponsAcount = new BigDecimal(saleBase.getCouponsAmount() != null ? saleBase.getBbcPostage() : 0);
		} else {
			// 看是否有缺货采购
			if (saleMain.hasPurchaseOrder()) {
				JsonNode purchaseOrder = null;
				try {
					// 查询缺货采购的单
					purchaseOrder = httpService.getPurchaseOrder(saleMain.getPurchaseOrderNo());
				} catch (IOException e) {
					Logger.info("获取采购单失败，采购单号为：{}",
							saleMain.getPurchaseOrderNo());
					throw new RuntimeException(e);
				}
				newHashMap.put("purchaseOrder", purchaseOrder);

				// 待付款状态的才有待支付金额
				if (purchaseOrder.get("status").asInt() == 0) {
					orderTotalAmount = new BigDecimal(purchaseOrder.get("purchaseTotalAmount").asDouble());
					bbcPostage = new BigDecimal(purchaseOrder.get("bbcPostage").asDouble());
					// 优惠
					couponsAcount = new BigDecimal(purchaseOrder.get("couponsAmount").asDouble());
				}
			}
		}
		
		// 订单金额+运费-优惠
		amountToBePaid = amountToBePaid.add(orderTotalAmount).add(bbcPostage);
		if (couponsAcount != null) {
			if (amountToBePaid.doubleValue() > couponsAcount.doubleValue()) {
				amountToBePaid = amountToBePaid.subtract(couponsAcount);
			} else {
				amountToBePaid = new BigDecimal(0);
			}
		}
		
		return amountToBePaid;
	}

	@Override
	public Map<String, Object> getPurchaseInfo(int orderId) {
		HashMap<String, Object> newHashMap = Maps.newHashMap();
		
		SaleMain saleMain = saleMainMapper.selectByPrimaryKey(orderId);
		SaleBase saleBase = saleBaseMapper.selectByOrderId(orderId);

		// 商品的采购信息
		List<SaleDetail> historySaleDetailList = saleDetailMapper.selectHistoryByOrderId(orderId);
		BigDecimal totalPurchasePrice = historySaleDetailList.stream().map(hsd->{
			return new BigDecimal(hsd.getCapFee() != null?hsd.getCapFee():hsd.getPurchasePrice())
				.multiply(new BigDecimal(hsd.getQty()));
		}).reduce(new BigDecimal(0), (x,y)->x.add(y));
		// 待支付金额
		BigDecimal amountToBePaid = (saleMain.getStatus()==1 || saleMain.getStatus()==103) ? getAmountToBePaid(newHashMap, saleMain, saleBase) : new BigDecimal(0);
		// 采购总计
		BigDecimal purchaseAmountTotal = totalPurchasePrice.setScale(2, BigDecimal.ROUND_HALF_UP);

		List<SaleDetail> resultHistorySaleDetailList = Lists.newArrayList();
		if (!CollectionUtils.isEmpty(historySaleDetailList)) {
			for (SaleDetail saleDetail : historySaleDetailList) {
				if (saleDetail.getPurchasePrice() != null) {
					saleDetail.setPurchasePrice(new BigDecimal(saleDetail.getPurchasePrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				}

				if (saleDetail.getCapFee() != null) {
					saleDetail.setCapFee(new BigDecimal(saleDetail.getCapFee()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				}
				resultHistorySaleDetailList.add(saleDetail);
			}
		}

		newHashMap.put("purchaseAmountTotal", purchaseAmountTotal);
		newHashMap.put("amountToBePaid", amountToBePaid.doubleValue());
		newHashMap.put("historySaleDetailList", resultHistorySaleDetailList);
		return newHashMap;
	}

	@Override
	public String syncCoupons(Integer mainId) {
		Map<String,Object> res = Maps.newHashMap();
		SaleBase base = saleBaseMapper.selectByOrderId(mainId);
		SaleMain sm = saleMainMapper.selectByPrimaryKey(mainId);
		if(base != null){
			String couponsNo =  base.getCouponsCode();
			if(couponsNo!= null){
				BigDecimal totalAmount = new BigDecimal(base.getBbcPostage() != null?base.getBbcPostage():0);
				BigDecimal couponsAmount = new BigDecimal(base.getCouponsAmount());
				BigDecimal actuallyPaid = totalAmount.subtract(couponsAmount);
				if(actuallyPaid.compareTo(BigDecimal.ZERO)<0){
					actuallyPaid = BigDecimal.ZERO;
				}
				Map<String, Object> couponsMap = Maps.newHashMap();
				couponsMap.put("orderAmount", totalAmount.doubleValue());
				couponsMap.put("couponsNo", couponsNo);
				couponsMap.put("actuallyPaid", actuallyPaid.doubleValue());
				couponsMap.put("user", sm.getEmail());
				couponsMap.put("istatus", 1);
				couponsMap.put("orderNo", sm.getSalesOrderNo());
				couponsMap.put("orderStatus", sm.getStatus());
				couponsMap.put("usageTime", new Date());
				String result = HttpUtil.post(Json.toJson(couponsMap).toString(),
						HttpUtil.B2BBASEURL + "/member/updateCoupons");
				Logger.info("========优惠码更新结果[{}]========", result);
			}
		}else{
			res.put("suc", false);
			res.put("msg", "该订单不存在");
		}
		return Json.toJson(res).toString();
	}

	/* 
	 * 
	 * yyyy-mm-dd hh:mm:ss xxx（管理员）修改运费为xx.xx元，整单优惠为xx.xx元
	 * 校验金额不能小于 0
	 * TODO 1 如果不缺货，更新运费
	 *      2 如果缺货，更新运费，已经更新采购单 运费 订单金额
	 *      3 记录操作日志
	 */
	@Override
	public Map<String, Object> changeOrderPrice(String str,String admin) {
		Map<String,Object> res = Maps.newHashMap();
		JsonNode node  = null;
		try {
			node = Json.parse(str);
			String sno = node.get("sno").asText();
			SaleMain main = saleMainMapper.selectByOrderNo(sno);
			//操作日志描述
			String desc = "修改";
			Double bbcPostage = null;
			Double reducePrice = null;
			boolean flag = false;
			String error = null;
			SaleBase base = null;
			if(main != null){
				base = saleBaseMapper.selectByOrderId(main.getId());
				if(node.has("bbcPostage") && StringUtils.isNotBlankOrNull(node.get("bbcPostage"))){
					bbcPostage = node.get("bbcPostage").asDouble();
					base.setBbcPostage(bbcPostage);
					desc += "运费为:"+ PriceFormatUtil.toFix2(bbcPostage) +"元";
				}
				if(main.getPurchaseOrderNo() != null){
					if(node.has("reducePrice") && StringUtils.isNotBlankOrNull(node.get("reducePrice"))) {
						reducePrice = node.get("reducePrice").asDouble();
						if(desc.length()>2){
							desc +=",";
						}
						desc += "整单优惠为"+PriceFormatUtil.toFix2(reducePrice)+"元";
					}
					((ObjectNode)node).put("pno",main.getPurchaseOrderNo());
					JsonNode postRes = httpService.changeOrderPrice(node);
					Logger.info("更新价格结果[{}]",postRes);
					if(postRes.get("suc").asBoolean()){
						flag = true;
					}else{
						error = postRes.get("msg").asText();
					}
				}else{
					BigDecimal actualPay = new BigDecimal(base.getBbcPostage()).
							subtract(new BigDecimal(base.getCouponsAmount() !=null?base.getCouponsAmount():0.00));
					if(actualPay.compareTo(BigDecimal.ZERO)<0){
						error = "待付款金额不能为负数";
					}else{
						flag =  true;
						if(base.getCouponsCode() != null){
							Map<String,Object> postMap = Maps.newHashMap();
							postMap.put("couponsNo", base.getCouponsCode());
							postMap.put("orderAmount", new BigDecimal(bbcPostage));
							postMap.put("actuallyPaid", actualPay.setScale(2, BigDecimal.ROUND_HALF_UP));
							String result = HttpUtil.post(Json.toJson(postMap).toString(),HttpUtil.B2BBASEURL+"/member/updateCoupons");
							Logger.info("更新优惠码信息:[{}]",result);
						}
					}
				}
			}
			
			if(flag){
				OperateRecord  or = new OperateRecord();
				or.setOperateType(7);
				or.setResult(1);
				or.setComment(desc);
				or.setOrderId(main.getId());
				or.setEmail(admin);
				operateRecordMapper.insert(or);
				//更新运费
				//更新采购金额
				if(StringUtils.isNotBlankOrNull(main.getPurchaseOrderNo())){
					JsonNode  purOrder = httpService.getPurByNo(main.getPurchaseOrderNo());
					JsonNode order = purOrder.get("orders").get(0);
					BigDecimal purchaseTotalAmount = new BigDecimal(JsonCaseUtil.getValue(JsonCaseUtil.jsonToDouble(order.get("purchaseTotalAmount"))));
					//保存支付金额
					base.setOrderTotalAmount(purchaseTotalAmount.doubleValue());
				}
				saleBaseMapper.updateByPrimaryKeySelective(base);
			}
			res.put("suc",flag);
			res.put("msg",error != null?error:("更新价格"+(flag?"成功":"失败")));
		} catch (Exception e) {
			Logger.info("修改价格异常",e);
			res.put("suc", false);
			res.put("msg", "修改价格异常");
		}
		return res;
	}


	@Override
	public Map<String, Object> auditByCustomerService(AuditByCustomerServiceParam param, boolean isauto) {
		Map<String, Object> res = Maps.newHashMap();
		SaleMain saleMain = saleMainMapper.selectByOrderNo(param.getSno());
		if (saleMain == null) {
			res.put("suc", false);
			res.put("msg", "该订单不存在");
			return res;
		}
		if(saleMain.getIsCombine()){
			res.put("suc", false);
			res.put("msg", "已合并发货单不能进行客服审核");
			return res;
		}
		try {
			// 缺货采购
			String pno = saleMain.getPurchaseOrderNo();
			if (StringUtils.isNotBlankOrNull(pno)) {
				JsonNode purNode = httpService.getPurchaseOrder(pno);
				if (purNode.get("status").asInt() != 1) {
					String msg = "销售单【" + saleMain.getSalesOrderNo() + "】对应的缺货采购单【" + pno + "】未完成。";
					Logger.info(msg);
					res.put("suc", false);
					res.put("msg", msg);
					return res;
				}
			}
			// 状态确认
			if (saleMain.getStatus() != SaleOrderStatus.WAITING_AUDIT_BY_CS) {
				res.put("suc", false);
				res.put("msg", "该订单不是待客服确认状态,不能进行客服确认,请刷新页面!");
				return res;
			}
			
			String msg;
			OperateRecord autoConfirm = null;
			Integer warehouseId = saleMain.getWarehouseId();
			if (param.isCsAudit()) {
				msg = "客服确认";
				// add by xuse
				if (noProfit(saleMain.getId())) {
					if (isauto) {// isauto为true时，表明系统自动审核，亏本时要手动审核
						Logger.info(saleMain.getSalesOrderNo() + "订单亏本，需要手动审核。");
						return Maps.newHashMap();
					}
					
					// add by zbc 异步计算财务审核费用信息
					ebus.post(new CaculateChargeEvent(saleMain.getId()));
					// 待财务审核
					saleMain.setStatus(SaleOrderStatus.WAITING_AUDIT_BY_FINANCE);
				} else {
					if (saleMain.getDisMode() > 1 || (warehouseId == 2029 || warehouseId == 2050)
							|| saleMain.getDistributorType() == 3) {
						if (saleMain.getDisMode() > 1 && isauto) {
							Logger.info(saleMain.getSalesOrderNo() + "需要手动审核。");
							return Maps.newHashMap();
						}
						// add by zbc 异步计算财务审核费用信息
						ebus.post(new CaculateChargeEvent(saleMain.getId()));
						// 待财务审核
						saleMain.setStatus(SaleOrderStatus.WAITING_AUDIT_BY_FINANCE);
					} else {
						// add by zbc 异步计算财务审核费用信息
						ebus.post(new CaculateChargeEvent(saleMain.getId()));
						autoConfirm = new OperateRecord();
						autoConfirm.setOrderId(saleMain.getId());
						autoConfirm.setOperateType(2);
						autoConfirm.setResult(1);
						autoConfirm.setComment("订单符合审核条件，自动通过财务审核。");
						autoConfirm.setEmail("system");
						saleMain.setStatus(SaleOrderStatus.WAITING_DELIVERY_SIX);
						// 无需实际支付时审核通过，手动生成支付信息
						saleMain.setPaymentNo(IDUtils.getPayNo());
						saleMain.setPayDate(new Date());
						saleMain.setPaymentType("system");
						saleMain.setCurrency("CNY");

						//生成电子面单 2017-5-18
						saleMain.setIsPushed(1);
					}
				}
				Logger.info(param.getSno() + "客服审核通过");
				// 修改订单信息
				SaleBase base = saleBaseMapper.selectByOrderId(saleMain.getId());
				base.setAddress(param.getAddress());
				base.setReceiver(param.getReceiver());
				base.setTel(param.getTel());
				base.setPostCode(param.getPostCode());
				base.setIdcard(param.getIdcard());
				saleBaseMapper.updateByPrimaryKeySelective(base);
			} else {
				msg = "客服关闭";
				// 客服关闭
				saleMain.setStatus(SaleOrderStatus.AUDIT_NOT_PASSED);
			}
			// 客服确认时间
			saleMain.setCsConfirmDate(new Date());
			boolean flag = saleMainService.updateByPrimaryKeySelective(saleMain);
			if (saleMain.getStatus() == SaleOrderStatus.WAITING_DELIVERY_SIX) {
				kdnService.requestOrderOnline(saleMain);
			}

			saleMainService.updateCouponsState(saleMain);
			// 判断是否已有审核通过的操作日志
			OperateRecord record = new OperateRecord();
			record.setOrderId(saleMain.getId());
			record.setOperateType(10);
			Integer result = saleMain.getStatus().equals(SaleOrderStatus.AUDIT_NOT_PASSED) ? 0 : 1;
			record.setResult(result);
			record.setComment(param.getCsRemark());
			record.setEmail(param.getAuditUser());
			operateRecordMapper.insertSelective(record);
			if (null != autoConfirm) {
				Logger.info(saleMain.getSalesOrderNo() + "系统自动通过财务审核状态。 ");
				operateRecordMapper.insertSelective(autoConfirm);
			}
			res.put("suc", flag);
			res.put("msg", msg + (flag ? "成功" : "失败"));
			return res;
		} catch (Exception e) {
			res.put("suc", false);
			res.put("msg", "客服审核异常");
			Logger.info("客服审核异常", e);
			return res;
		}
	}

	/**
	 * 不盈利？
	 * @param id
	 * @return 不盈利返回true，盈利订单返回false
	 */
	@Override
	public boolean noProfit(Integer id) {
		SalesPriceDto amount = getAmount(id);
		if(amount == null) {
			return true;
		}
		if(null != amount.getProfit() && amount.getProfit() >= 0) {
			return false;
		}
		return true;
	}

	@Override
	public Map<String, Object> auditByFinance(String string,String ip) {
		Map<String,Object> res = Maps.newHashMap();
		try {
			JsonNode node = Json.parse(string);
			String admin = userService.getAdminAccount();
			AuditRemark audit = new AuditRemark();
			audit.setOrderId(JsonCaseUtil.jsonToInteger(node.get("orderId")));
			audit.setIp(ip);
			audit.setOperator(admin);
			audit.setRemark(JsonCaseUtil.jsonToString(node.get("comment")));
			//强行待发货
			audit.setStatus(SaleOrderStatus.WAITING_DELIVERY_SIX);
			SaleMain main = saleMainMapper.selectByPrimaryKey(audit.getOrderId());
			if(main.getStatus() != SaleOrderStatus.WAITING_AUDIT_BY_FINANCE){
				res.put("suc", false);
				res.put("msg", "该订单不是待财务确认状态，不能进行财务确认,请刷新页面!");
				return res;
			}
			
			//保存信息
			remarkMapper.insertSelective(audit);
			res.put("suc", true);
			return res;
		} catch (Exception e) {
			res.put("suc", false);
			res.put("msg", "财务审核异常");
			Logger.info("财务审核异常",e);
			return res;
		}
	}

	@Override
	public void calculateOptFee(SaleMain sm, SaleBase sb, List<SaleDetail> details){
		try {
			if(details!=null && details.size()>0){
				Integer qtyTotal = details.stream().map(SaleDetail::getQty).reduce(0,(x,y)->x+y);
				JsonNode optFeeNode=null;
				try {
					Logger.info("获取操作费的参数：warehouseId={}，qty={}",sm.getWarehouseId(), qtyTotal);
					// 从hk获取操作费
					optFeeNode = httpService.getOptFee(sm.getWarehouseId(), qtyTotal);
				} catch (IOException e) {
					Logger.info("获取操作费失败，发货单单号为：{}",sm.getSalesOrderNo());
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				
				if(optFeeNode==null || !optFeeNode.get("result").asBoolean()){
					Logger.info("获取操作费失败，发货单单号为：{}",sm.getSalesOrderNo());
					return;
				}
				
				double optFee = optFeeNode.get("optfee").asDouble();
				saleMainMapper.updateOptFeeByOrderNo(sm.getSalesOrderNo(), optFee);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	public SalesPriceDto getAmount(Integer sid) {
		SaleMain sm = saleMainMapper.selectByPrimaryKey(sid);
		if (sm==null) {
			Logger.info("获取财务审核数据，不存在id为 {} 的发货单",sid);
			return new SalesPriceDto();
		}
		return saleMainMapper.getCharges(sid);
	}

	@Override
	public void autoCsConfirm() {
		if(!IS_PROCESSING) {
			IS_PROCESSING = true;//处理中
			// 查询1小时前支并且状态为客服审核的订单
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.MINUTE, -60);
			List<SaleMain> sms = saleMainMapper.getAutoConfirmOrders(DateUtils.date2string(cal.getTime(),DateUtils.FORMAT_FULL_DATETIME));
			// 循环确认订单
			if (CollectionUtils.isNotEmpty(sms)) {
				Logger.info("本次共【" + sms.size() + "】个订单需自动确认。");
				for (SaleMain saleMain : sms) {
					try {
						managerOrderService.match(saleMain.getId());
						
						AuditByCustomerServiceParam param = new AuditByCustomerServiceParam();
						param.setSno(saleMain.getSalesOrderNo());
						param.setCsAudit(true);// 审核通过
						param.setAuditUser("system");
						auditByCustomerService(param, true);
					} catch (Exception e) {
						Logger.error(saleMain.getSalesOrderNo() + "客服审核自动通过任务失败。" + e);
						continue;
					}
				}
			}
			IS_PROCESSING = false;//处理完
		} else {
			Logger.info("上次任务还在执行中，本次任务跳过。");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public  File exportSaleOrder(String fileName, String[] header,
			Map<String, String> headerMapping, List<String> exportKeys) {
		Logger.info("导出时间start：" + new Date());
		HSSFWorkbook workBook = new HSSFWorkbook();// 创建一个工作簿
		HSSFSheet sheet = workBook.createSheet(fileName.substring(0, fileName.indexOf(".")));// 创建工作表
		HSSFRow headRow = sheet.createRow(0); // 创建第一栏，抬头栏
		HSSFCell cell = null;
		for (int j = 0; j < header.length; j++) {
			cell = headRow.createCell(j);// 创建抬头栏单元格
			// 设置单元格格式
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			sheet.setColumnWidth(j, 6000);
			HSSFCellStyle style = workBook.createCellStyle();
			HSSFFont font = workBook.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			short color = HSSFColor.RED.index;
			font.setColor(color);
			style.setFont(font);
			// 将数据填入单元格
			cell.setCellStyle(style);
			if (header[j] != null) {
				cell.setCellValue(headerMapping.get(header[j]));
			} else {
				cell.setCellValue("-");
			}
		}
		int rowNum = 0;
		for(String key:exportKeys){
			rowNum = ExportUtil.insertData(header, ((ArrayList<ExportSaleOrderInfo>)sessionService.get(key)), sheet,rowNum);
			sessionService.remove(key);
		}
		// 生成文件
		return ExportUtil.createExcel(fileName, workBook);
	}
	

	@Override
	public List<SalesPriceDto> listAmounts(List<Integer> idList) {
		if (CollectionUtils.isEmpty(idList)) {
			return Collections.emptyList();
		}
		return saleMainMapper.getListCharges(idList);
	}

}
