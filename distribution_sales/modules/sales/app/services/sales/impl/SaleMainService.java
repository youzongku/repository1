package services.sales.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import dto.sales.ErpStatusDto;
import dto.sales.SaleAuditDto;
import dto.sales.SaleInventoryDetail;
import dto.sales.SaleInventoryDto;
import dto.sales.SalesToB2cDetail;
import dto.sales.UpdateErpStatusResultDto;
import entity.sales.AuditRemark;
import entity.sales.OperateRecord;
import entity.sales.OrderPack;
import entity.sales.PayWarehouse;
import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;
import events.sales.ClosedOrderEvent;
import mapper.sales.AuditRemarkMapper;
import mapper.sales.PayWarehouseMapper;
import mapper.sales.SaleBaseMapper;
import mapper.sales.SaleDetailMapper;
import mapper.sales.SaleMainMapper;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import services.sales.IHttpService;
import services.sales.IKdnService;
import services.sales.ISaleLockService;
import services.sales.ISaleMainService;
import services.sales.ISequenceService;
import services.sales.IUserService;
import util.sales.Constant;
import util.sales.DateUtils;
import util.sales.HttpUtil;
import util.sales.IDUtils;
import util.sales.JsonCaseUtil;
import util.sales.MD5Util;
import util.sales.PriceFormatUtil;
import util.sales.SaleOrderStatus;

public class SaleMainService implements ISaleMainService {

	@Inject
	SaleMainMapper saleMainMapper;
	@Inject
	SaleBaseMapper saleBaseMapper;
	@Inject
	ISequenceService sequenceService;
	@Inject
	SaleDetailMapper saleDetailMapper;
	@Inject
	PayWarehouseMapper payWarehouseMapper;
	@Inject
	OperateRecordService operateRecordService;
	@Inject
	IHttpService httpService;
	@Inject
	IUserService userService;
	@Inject
	AuditRemarkMapper auditMapper;

	@Inject
	private IKdnService kdnService;
	
	@Inject
	private EventBus ebus;
	
	@Inject
	private ISaleLockService lockService;

	/**
	 * 获取sku要还原微仓的数量：sku->qty
	 * @param saleMain
	 * @param saleDetailList
	 * @return
	 */
	@SuppressWarnings("unused")
	private Map<String,Integer> calculateSkuToQtyMicroRestore(SaleMain saleMain, List<SaleDetail> saleDetailList){
		// 微仓最终要还原的
		Map<String, Integer> skuToQtyMicroRestore = Maps.newHashMap();

		if (saleMain.hasPurchaseOrder()) {
			Logger.info("此销售单" + saleMain.getSalesOrderNo() + "有缺货采购，采购单为："
					+ saleMain.getPurchaseOrderNo());
			// 有采购单，2种情况：
			// 1、全是新采购单；2、部分是采购单，部分是微仓订单

			String purchaseOrderNo = saleMain.getPurchaseOrderNo();
			// 采购单详情
			JsonNode purchaseOrderListNode;
			try {
				purchaseOrderListNode = httpService
						.getPurchaseDetailList(purchaseOrderNo);
			} catch (IOException e) {
				Logger.info("获取采购单失败，采购单号为：" + purchaseOrderNo);
				throw new RuntimeException(e);
			}
			// 将采购单详情转换为sku=qty的map
			Map<String, Integer> skuToQtyPurchase = convertSkuToQty(purchaseOrderListNode);

			Map<String, Integer> skuToQtyMicroRestore_temp = saleDetailList
					.stream().collect(
							Collectors.toMap(SaleDetail::getSku, sd -> {
								// 计算微仓需要还原的数量
									int qtySale = sd.getQty();// 销售单sku数量
									// 采购单采购的数量
									Integer qtyPurchase = skuToQtyPurchase
											.get(sd.getSku());
									// 存在可能：销售单sku不需要进行缺货采购，获取到的数量就是null
									if (qtyPurchase == null) {
										qtyPurchase = 0;
									}
									return qtySale - qtyPurchase.intValue();
								}));
			// 过滤掉value为0的
			skuToQtyMicroRestore
					.putAll(filterValueEqZeroOrNull(skuToQtyMicroRestore_temp));
		} else {
			// 全是微仓的
			skuToQtyMicroRestore.putAll(saleDetailList.stream().collect(
					Collectors.toMap(SaleDetail::getSku, SaleDetail::getQty)));
		}
		return skuToQtyMicroRestore;
	}
	
	/**
	 * 关闭订单内部类
	 * @author zbc
	 * 2016年12月16日 下午2:52:31
	 */
	class Cancel{
		private Boolean purClose;//是否关闭采购单
		private BigDecimal tram;//退款金额
		
		public Cancel(Boolean purClose, BigDecimal tram) {
			this.purClose = purClose;
			this.tram = tram;
		}

		public Boolean getPurClose() {
			return purClose;
		}

		public void setPurClose(Boolean purClose) {
			this.purClose = purClose;
		}

		public BigDecimal getTram() {
			return tram;
		}

		public void setTram(BigDecimal tram) {
			this.tram = tram;
		}
		
	}
	
	@Override
	public Map<String,Object> cancelOrder(JsonNode main) {
		Map<String,Object> res = Maps.newHashMap();
		// change by zbc  正在实现
		try {
			SaleMain saleMain = saleMainMapper.selectByOrderNo(main.get("so").asText());
			// 检查是否可以进行操作
			List<Integer> cancelStatusList = Arrays.asList(1,103,4);
			if(cancelStatusList.indexOf(saleMain.getStatus()) == -1){
				res.put("suc", false);
				res.put("msg", "不能进行取消操作，请刷新页面查看订单最新状态");
				return res;
			}
			
			String email = JsonCaseUtil.jsonToString(main.get("em")) != null?
					JsonCaseUtil.jsonToString(main.get("em")):userService.getDisAccount();
			if(!(email!=null&&email.equals(saleMain.getEmail()))){
				res.put("suc", false);
				res.put("msg", "非本人不能操作该订单");
				return res;
			}
			//校验是否已关闭
			if(saleMain.getIsClose()){
				res.put("suc", false);
				res.put("msg", "该订单已关闭，不能再进行取消操作");
				return res;
			}
			//将订单更新为已关闭 防止重复关闭
			saleMain.setIsClose(true);
			saleMainMapper.updateByPrimaryKey(saleMain);
			//判断是否要关闭采购单
			Cancel can = getTram(saleMain);
			JsonNode purNode = null;
			String pNo = saleMain.getPurchaseOrderNo();
			//内部分销商还回云仓
			if(saleMain.getDistributorType() == 3 &&pNo !=null){
				purNode = httpService.getByNo(pNo);
			}
			boolean suc = false;
			ObjectNode backRes = backMoneyAndStock(can.getTram(), saleMain, purNode,5,"分销商用户");
			if(backRes.get("suc").asBoolean()){
				suc = true;
				//关闭采购单
				if(can.getPurClose()){
					Logger.info("关闭采购单结果:[{}]",httpService.cancelPurchaseOrder(pNo));
				}
			}
			res.put("suc", suc);
			res.put("msg", "关闭订单"+(suc?"成功":"失败"));
		} catch (Exception e) {
			res.put("suc", false);
			res.put("msg", "关闭订单异常");
			Logger.info("关闭订单异常",e);
		}
		return res;
	}
	
	private Cancel getTram(SaleMain saleMain) {
		boolean purClose = false;
		BigDecimal tram = null;
		try {
			//计算订单金额
			SaleBase base = saleBaseMapper.selectByOrderId(saleMain.getId());
			String pNo = saleMain.getPurchaseOrderNo();
			if(saleMain.getDistributorType() != 3){
				if(pNo != null){
					JsonNode  purOrder = httpService.getPurByNo(pNo);
					if("0".equals(purOrder.get("returnMess").get("errorCode").asText())){
						JsonNode order = purOrder.get("orders").get(0);
						Integer poStatus = order.get("status").asInt();
						BigDecimal couponsAmount = new BigDecimal(getValue(JsonCaseUtil.jsonToDouble(order.get("couponsAmount"))));
						BigDecimal bbcPostage = new BigDecimal(getValue(JsonCaseUtil.jsonToDouble(order.get("bbcPostage"))));
						BigDecimal purchaseTotalAmount = new BigDecimal(getValue(JsonCaseUtil.jsonToDouble(order.get("purchaseTotalAmount"))));
						//已完成订单要退运费  若有优惠码 优惠金额大于运费不退钱 , 
						if(poStatus == 1){
							tram = purchaseTotalAmount.subtract(couponsAmount).compareTo(BigDecimal.ZERO)>0?
									bbcPostage:purchaseTotalAmount.subtract(couponsAmount).add(bbcPostage);
						//待客服审核  待财务审核  说明金额未扣 退钱 
						//TODO 目前不支持线下转账，所以不需要判断 支付方式，是否已经有过交易
						}else if(poStatus == 4 || poStatus == 6 ){
							tram =purchaseTotalAmount.subtract(couponsAmount).add(bbcPostage);
							purClose = true;
						}else if(poStatus == 0){
							purClose = true;
						}
					}
				}else{
					if(saleMain.getStatus() != 103){
						tram = new BigDecimal(getValue(base.getBbcPostage()))
								.subtract(new BigDecimal(base.getCouponsAmount()));
					}
				}
			}
		} catch (Exception e) {
			Logger.info("获取退款金额异常",e);
		}
		tram = tram!= null && tram.compareTo(BigDecimal.ZERO)>0?PriceFormatUtil.setScale2(tram):null;
		return new Cancel(purClose, tram);
	}

	public Double getValue(Double money){
		return money!=null?money:0;
	}
	/**
	 * 过滤掉value为null或不大于0的entry
	 * @param skuToQtyMicroRestoreArg
	 * @return
	 */
	private Map<String,Integer> filterValueEqZeroOrNull(Map<String, Integer> skuToQtyMicroRestoreArg){
		Map<String, Integer> skuToQtyMicroRestore = Maps.newHashMap();
		skuToQtyMicroRestoreArg.forEach((sku,qty)->{
			if(qty !=null && qty.intValue() > 0){
				skuToQtyMicroRestore.put(sku, qty);
			}
		});
		return skuToQtyMicroRestore;
	}
	
	/**
	 * 采购单：sku与数量的映射
	 * @param purchaseOrderListNode
	 * @return
	 */
	private Map<String,Integer> convertSkuToQty(JsonNode purchaseOrderListNode){
		HashMap<String,Integer> skuToQty = Maps.newHashMap();
		if(purchaseOrderListNode==null) return skuToQty;
		for(Iterator<JsonNode> purchaseOrderIt = purchaseOrderListNode.iterator();
				purchaseOrderIt.hasNext();){
			JsonNode purchaseOrderNode = purchaseOrderIt.next();
			skuToQty.put(purchaseOrderNode.get("sku").asText(),
					purchaseOrderNode.get("qty").asInt());
		}
		return skuToQty;
	}

	@Override
	public List<SaleMain> getAllSaleMain(Map<String,Object> paramMap) {
		return saleMainMapper.selectAllSaleMain(paramMap);
	}

	@Override
	public boolean batchUpdateVerify(List<String> orderIds) {
		List<Integer> ids = new ArrayList<Integer>();
		for (String id  : orderIds) {
			ids.add(Integer.valueOf(id));
		}
		if (!ids.isEmpty()) {
			return saleMainMapper.batchUpdateVerify(ids)>0;
		}
		return true;
	}

	@Override
	public SaleMain getSaleMainOrderByOrderNo(String orderNo) {
		return saleMainMapper.selectByOrderNo(orderNo);
	}

	@Override
	public SaleMain getSaleMainOrderByID(Integer id) {
		return saleMainMapper.selectByPrimaryKey(id);
	}
	
	@Override
	public SaleMain getSaleMainByIdAndAccounts(Map<String, Object> params){
		return saleMainMapper.selectByIdAndAccounts(params);
	}

	/**
	 * 构建批量支付参数
	 * @param ids
	 * @return
	 */
	public Map<String, Object> buildBatchPayParam(String ids){
		Map<String, Object> resultMap = Maps.newHashMap();
		ObjectMapper obj = new ObjectMapper();
		ArrayNode lists = obj.createArrayNode();
//		List<Map<String,String>> lists = Lists.newArrayList();
		List<String> orderIdList = Arrays.asList(ids.split(","));
		int isSameWarehouse = 0;
		String salesNo = "";
		try {
			for (String id : orderIdList) {
				Map<String,String> saleMap = saleMainMapper.getSalesById(Integer.parseInt(id));
				Logger.debug(">>>>>saleMap>>>>>"+saleMap.toString());
				if (!saleMap.isEmpty()) {
					salesNo = saleMap.get("sales_order_no");
					if(saleMap.containsKey("orderer") && saleMap.containsKey("orderer_idcard")
							&& StringUtils.isNotBlank(saleMap.get("orderer"))
							&&StringUtils.isNotBlank(saleMap.get("orderer_idcard"))){
						//校验订购人身份信息
						ObjectNode reqParam = obj.createObjectNode();
						reqParam.put("realName",saleMap.get("orderer"));
						reqParam.put("certNo",saleMap.get("orderer_idcard"));
						String url = HttpUtil.B2BBASEURL + "/payment/realNameQuery";
						String retStr = HttpUtil.post(reqParam.toString(),url);
						JsonNode retNode = Json.parse(retStr);
						Logger.debug(">>>retNode>>>>"+retNode.toString());
						String partnerId = "";
						if("0".equals(retNode.get("errorCode").asText())){//实名校验通过
							partnerId = retNode.get("errorInfo").toString();
						}else {
							isSameWarehouse = 1;
							break;
						}
						PayWarehouse record = payWarehouseMapper.getRecord(String.valueOf(saleMap.get("warehouse_id")));
						if (record == null) {
							isSameWarehouse = 2;
							break;
						}
						ObjectNode node = obj.createObjectNode();
						node.put("outOrderNo",salesNo);
						node.put("tradeAmount", String.valueOf(saleMap.get("order_actual_amount")));
						node.put("sellerUserId",partnerId);
						List<SalesToB2cDetail> details = saleDetailMapper.getDetails(Integer.parseInt(id));
						ArrayNode array = obj.createArrayNode();
						for (SalesToB2cDetail detail : details) {
							ObjectNode batchDetail = obj.createObjectNode();
							batchDetail.put("name", detail.getCtitle());
							array.add(batchDetail);
						}
						node.putPOJO("goodsClauses", array);
						lists.add(node);
					}else {
						isSameWarehouse = 4;
					}
				}else {
					isSameWarehouse = 3;
				}
			}
		}catch (Exception e){
			Logger.error(">>Exception>>>>"+e);
			e.printStackTrace();
			resultMap.put("result", false);
			resultMap.put("msg", "系统异常");
			return resultMap;
		}
		
		// 使用查表法来简化代码
		String[] errorMsgArray = {
				"批量设置审核通过成功",
				"订购人信息实名校验不通过，请确认！订单号："+salesNo,
				"存在可直接审核的订单，请确认！订单号："+salesNo,
				"销售订单数据异常，请确认！订单号："+salesNo,
				"销售订单数据订购人信息异常，请确认！订单号："+salesNo
		};
		if (isSameWarehouse == 1 || isSameWarehouse == 2
				|| isSameWarehouse == 3 || isSameWarehouse == 4) {
			resultMap.put("result", false);
			resultMap.put("msg", errorMsgArray[isSameWarehouse]);
			return resultMap;
		}
		
		resultMap.put("result", true);
		resultMap.put("msg", errorMsgArray[0]);
		resultMap.put("builds", lists.toString());
		return resultMap;
	}

	/**
	 * 批量审核
	 * @param ids  订单id
	 * @param email  审核人
	 * @param comment  审核不通过原因
	 * @param status   审核状态
	 * @return
	 */
	public void batchAudit(List<String> ids,String email,String comment,String status){
		for(String id : ids) {
			SaleMain sm = getSaleMainOrderByID(Integer.valueOf(id));
			if(sm == null){
				sm = new SaleMain();
			}else if("6".equals(status) && Strings.isNullOrEmpty(sm.getPaymentNo())){
				//无需实际支付时审核通过，手动生成支付信息
				sm.setPaymentNo(IDUtils.getPayNo());
				sm.setPayDate(new Date());
				sm.setPaymentType("system");
				sm.setCurrency("CNY");
			}
			sm.setStatus(Integer.valueOf(status));
			boolean updateSaleMainFlag = updateByPrimaryKeySelective(sm);
			Logger.debug(">>>updateSaleMainFlag>>>>>>" + updateSaleMainFlag);
			//判断是否已有审核通过的操作日志
			OperateRecord record = new OperateRecord();
			record.setOrderId(sm.getId());
			record.setOperateType(2);
			record.setResult(Integer.valueOf(status) == 6 ? 1 : 0);
			List<OperateRecord> records = operateRecordService.findOperateRecordByCondition(record);
			if (records == null || records.size() == 0) {
				boolean saveOperateRecordFlag = lockService.saveRecord(sm.getId(), 
						record.getOperateType(), record.getResult(), comment, email);
				Logger.info("saveOperateRecord_Flag--->" + saveOperateRecordFlag);
			}
		}
	}

	@Override
	public void updateCouponsState(SaleMain sm) {
		Map<String, String> sale = saleMainMapper.getSalesById(sm.getId());
		if (null != sale && util.sales.StringUtils.isNotBlankOrNull(sale.get("couponscode"))) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("orderStatus", sm.getStatus());// 订单状态
			params.put("couponsNo", sale.get("couponscode"));// 优惠码
			if(sm.getStatus() == 5 || sm.getStatus() == 100) {
				params.put("istatus", 3);
			}
			String response = HttpUtil.post(Json.toJson(params).toString(),
					HttpUtil.B2BBASEURL + "/member/updateCoupons");
			Logger.info("updateCouponsState : " + response);
		}
	}

	@Override
	public void pushStatusToStory(SaleMain sm) {
		try {
			Integer id = sm.getId();
			if(id != null && StringUtils.isEmpty(sm.getSalesOrderNo())){
				sm = saleMainMapper.selectByPrimaryKey(id);
			}
			String orderNo = sm.getSalesOrderNo();
			Integer status = sm.getStatus();
			if(orderNo.startsWith("MD")){//如果是门店销售单，同步状态
				Map<String,Object> param = Maps.newHashMap();
			   	param.put("order_number", orderNo);
			   	param.put("status",status);
			   	Configuration config = Play.application().configuration()
	                    .getConfig("store");
	            String url =  config.getString("2Store") + "/requirement/item/status";
	            Logger.info("门店订单更新状态参数：" + Json.toJson(param));
	            String res = HttpUtil.post(Json.toJson(param).toString(), url);
	            Logger.info("更新门店销售单状态:"+res);
			}
		} catch (Exception e) {
			Logger.error("更新门店销售单状态异常", e);
		}
	}
	
	@Override
	public void batchPushStatusToStory(List<SaleMain> list) {
		try {
			Map<String,Object> param = null;
			List<Map<String,Object>> paramList = Lists.newArrayList();
			for(SaleMain sm:list){
				param = Maps.newHashMap();
			 	param.put("order_number", sm.getSalesOrderNo());
			   	param.put("status",sm.getStatus());
			   	paramList.add(param);
			}
		   	Configuration config = Play.application().configuration()
                    .getConfig("store");
            String url =  config.getString("2Store") + "/requirement/item/statuslist";
            String res = HttpUtil.post(Json.toJson(paramList).toString(), url);
            Logger.info("更新门店销售单状态:"+res);
		} catch (Exception e) {
			Logger.error("更新门店销售单状态异常", e);
		}
	}
	
	@Override
	public void batchPushStatusToMSite(List<OrderPack> list) {
		// TODO Auto-generated method stub
		try {
		   	Configuration config = Play.application().configuration()
                    .getConfig("MSite");
            String url =  config.getString("mSite") + "/service/bbc/orderPack";
            
            //构建传递给M站的json数据，包含集合list订单信息和一个校验数据key
            String key=Json.toJson(list).toString()+"msite";
            Map<String,Object> MSiteMap=Maps.newHashMap();
            MSiteMap.put("key", MD5Util.MD5Encode(key, "UTF-8"));
            MSiteMap.put("data", list);
            
            String res = HttpUtil.post(Json.toJson(MSiteMap).toString(), url);
            Logger.info("更新M站销售单状站态:"+res);
		} catch (Exception e) {
			Logger.error("更新M销售单状态异常", e);
		}
	}

	@Override
	public Map<String, Object> updateSalesStatus(JsonNode main) {
		Map<String,Object> result = Maps.newHashMap();
		SaleMain sm = saleMainMapper.selectByOrderNo(main.get("saleOrderNo").asText());
		if(sm != null){
			sm.setStatus(main.get("status").asInt());
			boolean flag = updateByPrimaryKeySelective(sm);
			result.put("suc", flag);
			result.put("msg",flag?"更新状态成功":"更新状态失败");
			
			Logger.info("更改客户订单:[{}]的前端状态标识为：[{}]，后台状态标识为：[{}]，" + "执行结果为：{}",
					sm.getSalesOrderNo(), Constant.SALES_ORDER_STATE_FRONT.get(sm
							.getStatus()), Constant.SALES_ORDER_STATE_MANAGER.get(sm
							.getStatus()), flag);
		}else{
			result.put("suc",false);
			result.put("msg","找不到该订单");
		}
		return result;
	}

	@Override
	public Map<String, Object> erpCatchResult(JsonNode main) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			SaleMain sm = new SaleMain();
			List<String> failOrder = Lists.newArrayList();
			int low = 0;
			for(JsonNode  m:main){
				boolean isFetched = m.get("isFetched").asBoolean();
				String  orderNo = m.get("orderNo").asText();
				String erpOrderNo=m.has("erpOrderNo")?m.get("erpOrderNo").asText():null;
				sm = saleMainMapper.selectByOrderNo(orderNo);
				if(sm != null){
					sm.setIsFetched(isFetched);
					sm.setErpReason(m.has("reason")?m.get("reason").asText():null);
					sm.setErpOrderNo(erpOrderNo);
					updateByPrimaryKeySelective(sm);
					low++;
				}
				if(!isFetched){
					failOrder.add(orderNo);
				}
			}
			Logger.info("更新订单:[{}]条，erp抓取失败订单:[{}]",low,failOrder);
			result.put("suc", true);
			result.put("msg", "保存数据成功");
		} catch (Exception e) {
			result.put("suc", false);
			result.put("msg", "保存数据失败");
			Logger.error("获取ERP抓取结果失败",e);
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String closeSalesFromB2C(String param) {
		ObjectNode result = Json.newObject();
		JsonNode node = Json.parse(param);
		SaleMain main = saleMainMapper.selectByOrderNo(node.get("saleOrderNo").asText());
		boolean flag = main != null && !main.getIsClose();
		if(!flag){
			result.put("suc", false);
			result.put("msg", "订单不存在或订单已经关闭过");
			return result.toString();
		}

		if (main.getSalesOrderNo().startsWith("MS-")) {// 表示是来自于M站的订单,就需要传递到M站去处理库存和金额
			String res = "";
			Map<String, String> mParam = new HashMap<String, String>();
			mParam.put("orderNo", main.getSalesOrderNo());
			String key = "orderNo=" + main.getSalesOrderNo();
			key = MD5Util.MD5Encode(key + "msite", MD5Util.CHARSET_UTF_8);
			mParam.put("key", key);
			Configuration config = Play.application().configuration().getConfig("MSite");
			String url = config.getString("mSite") + "/service/bbc/cancel-order";
			res = HttpUtil.post(Json.toJson(mParam).toString(), url);
			Logger.info("closeSalesFromB2C---->" + res);
			if (StringUtils.isNotEmpty(res) && Json.parse(res).get("result").asBoolean()) {
				SaleMain sm = new SaleMain();
				sm.setStatus(20);
				sm.setId(main.getId());
				sm.setEmail(main.getEmail());
				sm.setIsClose(true);
				updateByPrimaryKeySelective(sm);// 更新状态
				result.put("suc", true);
				result.put("data", Json.toJson(main));
				return result.toString();
			}
			
			result.put("suc", false);
			result.put("msg", "订单" + main.getSalesOrderNo() + "关闭失败");
			return result.toString();
		}
		Integer distributeType = main.getDistributorType();
		SaleBase base = saleBaseMapper.selectByOrderId(main.getId());
		BigDecimal traAm = new BigDecimal(base.getBbcPostage() == null ? 0 : base.getBbcPostage());
		if (distributeType != 3) {
			if (base.getCouponsAmount() != null && base.getCouponsAmount() > 0) {
				BigDecimal coupons = new BigDecimal(base.getCouponsAmount());
				traAm = traAm.subtract(coupons);
			}
			if (traAm.compareTo(BigDecimal.ZERO) > 0) {// 运费大于优惠金额，最终需要退 【运费
														// - 优惠金额】
				result = backMoneyAndStock(PriceFormatUtil.setScale2(traAm), main, null,20,"system");
			} else {
				result = backMoneyAndStock(null, main, null,20,"system");
			}
		} else {
			String pNo = main.getPurchaseOrderNo();
			JsonNode purchaseNode  = null;
			if(main.getPurchaseOrderNo() != null){
				try {
					purchaseNode =  httpService.getByNo(pNo);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			result = backMoneyAndStock(null, main, purchaseNode,20,"system");
		}
		return result.toString();
	}
	
	/**
	 * 退钱退库存
	 * @author lzl
	 * @since 2016年11月16日下午6:27:58
	 */
	public ObjectNode backMoneyAndStock(BigDecimal traAm,SaleMain main, JsonNode purchaseNode,Integer status,String operator){
		ObjectNode result = Json.newObject();
		if (traAm != null) {//退款
			JsonNode refundNode = null;
			try {
				refundNode = httpService.refund(main.getEmail(), traAm, main.getSalesOrderNo());
			} catch (Exception e) {
				e.printStackTrace();
			} 
			if (refundNode == null || refundNode.get("code").asInt() != 4){// 退款失败
				return result.put("suc", false).put("msg", "订单"+main.getSalesOrderNo()+"退款失败");
			}
		}
		return backStock(main, purchaseNode, status, operator);
	}
	
	/**
	 * 库存还原
	 * @author lzl
	 * @since 2016年11月18日下午2:03:31
	 */
	@SuppressWarnings("deprecation")
	public ObjectNode backStock(SaleMain main, JsonNode purchaseNode,Integer status,String operator){
		ObjectNode result = Json.newObject();
		String postRes = "";
		List<SaleDetail> historDetails = saleDetailMapper.getHistoryByOrderId(main.getId());//查询销售发货详情历史记录，并且进行库存还原（还原微仓）
		List<SaleInventoryDetail> inventoryDetails = new ArrayList<SaleInventoryDetail>();
		SaleInventoryDetail inventoryDetail = null;
		boolean flag = false;
		if (historDetails.size() > 0) {
			for (SaleDetail detail : historDetails){
				inventoryDetail = new SaleInventoryDetail();
				inventoryDetail.setSku(detail.getSku());
				inventoryDetail.setProductTitle(detail.getProductName());
				inventoryDetail.setNum(detail.getQty());
				inventoryDetail.setIsgift(detail.getIsgift());
				inventoryDetail.setCostprice(detail.getPurchasePrice());
				inventoryDetail.setWarehouseId(detail.getWarehouseId());
				inventoryDetail.setWarehouseName(detail.getWarehouseName());
				inventoryDetails.add(inventoryDetail);
			}
			SaleInventoryDto inventoryDto = new SaleInventoryDto();
			inventoryDto.setOrderType(13);
			inventoryDto.setOrderTitle("还原入库");
			inventoryDto.setOrderNo(main.getSalesOrderNo());
			inventoryDto.setDistributorName(main.getEmail());
			inventoryDto.setDistributorEmail(main.getEmail());
			inventoryDto.setWareType(1);
			inventoryDto.setDetailList(inventoryDetails);
			postRes = HttpUtil.post(Json.toJson(inventoryDto).toString(), HttpUtil.B2CBASEURL+"/warehousing/restorestock-erpclosed");
			Logger.info("backStock微仓还原-------->" + postRes);
			flag = !postRes.equals("") && Json.parse(postRes).get("result").asBoolean();
		}else{
			flag = true;
		}
		if (flag) {
			if (purchaseNode != null && purchaseNode.get("pros").size() > 0) {//说明存在缺货采购,还原云仓
				JsonNode shortDetails = purchaseNode.get("pros");//缺货采购单里的订单详情
				inventoryDetails = new ArrayList<SaleInventoryDetail>();
			    inventoryDetail = null;
			    for (JsonNode node : shortDetails) {
			    	inventoryDetail = new SaleInventoryDetail();
			    	inventoryDetail.setSku(node.get("sku").textValue());
			    	inventoryDetail.setProductTitle(node.get("productName").textValue());
			    	inventoryDetail.setNum(node.get("qty").asInt());
			    	inventoryDetail.setCostprice(node.get("purchasePrice").asDouble());
			    	inventoryDetail.setWarehouseId(node.get("warehouseId").asInt());
			    	inventoryDetail.setWarehouseName(node.get("warehouseName").textValue());
			    	inventoryDetails.add(inventoryDetail);
			    }
			    SaleInventoryDto cloudDto = new SaleInventoryDto();
			    cloudDto.setOrderType(29);
			    cloudDto.setOrderTitle("其他出库");
			    cloudDto.setOrderNo(purchaseNode.get("purchaseNo").asText());
			    cloudDto.setDistributorName(purchaseNode.get("email").asText());
			    cloudDto.setDistributorEmail(purchaseNode.get("email").asText());
			    cloudDto.setWareType(2);
			    cloudDto.setDetailList(inventoryDetails);
			    postRes = HttpUtil.post(Json.toJson(cloudDto).toString(), HttpUtil.B2CBASEURL+"/warehousing/restorestock-erpclosed");
			    Logger.info("backStock云仓还原-------->" + postRes);
			}
			// 1、添加取消订单的操作记录
			boolean saveResultOperateRecord = lockService.saveRecord(main.getId(),
					status!=SaleOrderStatus.CLOSED_BY_ERP?4:11, 1, 
							status==SaleOrderStatus.CLOSED_BY_CUSTOMER?"取消订单":
								(status==SaleOrderStatus.CLOSED_BY_ERP?"关闭订单":"取消订单"), operator);
			Logger.info("销售订单状态改为取消添加操作记录结果："
					+ (saveResultOperateRecord ? "添加成功" : "添加失败"));
			main.setStatus(status);
			main.setIsClose(true);
			updateByPrimaryKeySelective(main);//更新状态
			this.updateCouponsState(main);
			result.put("suc", true);
			result.put("data", Json.toJson(main));
		} else {
			result.put("suc", false);
			result.put("msg", "更新云仓库存失败");
		}
		return result;
	}

	@Override
	public String undoClose(String orderNo) {
		Logger.info("取消关闭订单:[{}]",orderNo);
		SaleMain main = saleMainMapper.selectByOrderNo(orderNo);
		if(main == null){
			return "order not exsit";
		}
		main.setIsClose(false);
		return updateByPrimaryKeySelective(main) ? "success" : "false";
	}

	@Override
	public Map<String, Object> check(String node, String optUser) {
		Map<String,Object> res = Maps.newHashMap();
		try {
			JsonNode json = Json.parse(node);
			Integer orderId = JsonCaseUtil.jsonToInteger(json.get("orderId"));
			
			SaleMain sm = getSaleMainOrderByID(orderId);
			if (sm==null) {
				res.put("result", false);
				res.put("msg", "发货单不存在");
				return res;
			}
			if(sm.getStatus() != SaleOrderStatus.WAITING_AUDIT_BY_FINANCE){
				res.put("result", false);
				res.put("msg", "该订单不是待财务确认状态，不能进行财务确认,请刷新页面!");
				return res;
			}
			
			Integer status = JsonCaseUtil.jsonToInteger(json.get("status")); 
			String comment = JsonCaseUtil.jsonToString(json.get("comment")); 
			
			// 审核通过
			if (status == SaleOrderStatus.WAITING_DELIVERY_SIX) {
				if (Strings.isNullOrEmpty(sm.getPaymentNo())) {
					//无需实际支付时审核通过，手动生成支付信息
					sm.setPaymentNo(IDUtils.getPayNo());
					sm.setPayDate(new Date());
					sm.setPaymentType("system");
					sm.setCurrency("CNY");
					sm.setRejectedByFinance(false);
				}

				//暂时不推送 走完快递鸟电子面单流程再推送
				sm.setIsPushed(1);

			} else if (status == SaleOrderStatus.WAITING_AUDIT_BY_CS) {// 审核不通过
				// 如果是打回客服审核，设置标识
				sm.setRejectedByFinance(true);
			}
			
			sm.setStatus(Integer.valueOf(status));
			
			Logger.info("checkSaleMain    SaleMain--->" + Json.toJson(sm).toString());
			boolean result = updateByPrimaryKeySelective(sm);

			if (status == SaleOrderStatus.WAITING_DELIVERY_SIX) {
				kdnService.requestOrderOnline(sm);
			}

			if (!result) {
				res.put("result", false);
				res.put("msg", "更新销售订单状态失败");
				return res;
			}

			//更新优惠券状态
			sm.setId(orderId);
			updateCouponsState(sm);
			//判断是否已有审核通过的操作日志
			Logger.info("saveOperateRecord_Flag--->" + lockService.saveRecord(sm.getId(), 2,
					status == SaleOrderStatus.WAITING_DELIVERY_SIX ? 1 : 0, comment, optUser));
			res.put("result", true);
			res.put("msg", "更新销售订单状态成功");
			return res;
		} catch (Exception e) {
			res.put("result", false);
			res.put("msg", "更新订单异常");
			return res;
		}
		
	}

	@Override
	public boolean updSalesOrderVirtualPayInfo(String str,String ip) {
		boolean flag = false;
		try {
			JsonNode main = Json.parse(str);
			SaleMain sm = getSaleMainOrderByOrderNo(main.get("saleOrderNo").textValue());
			if(sm != null && Strings.isNullOrEmpty(sm.getPaymentNo())){
				sm.setCurrency(main.get("currency").textValue());
				try {
					sm.setPayDate(
							DateUtils.string2date(main.get("payDate").textValue(), DateUtils.FORMAT_FULL_DATETIME));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				sm.setPaymentNo(main.get("payNo").textValue());
				sm.setPaymentType(main.get("payType").textValue());
				sm.setStatus(SaleOrderStatus.WAITING_DELIVERY_SIX);//虚拟支付之后，默认变为“审核通过”
				if(main.has("payer")) {
					sm.setPayer(main.get("payer").textValue());
				}
				if(main.has("paryerIdcard")) {
					sm.setParyerIdcard(main.get("paryerIdcard").textValue());
				}
				//add by zbc 保存在数据库中的备注
				AuditRemark queryParam = new AuditRemark();
				queryParam.setOrderId(sm.getId());
				queryParam.setIp(ip);
				AuditRemark remark = auditMapper.select(queryParam);
				//判断是否已有审核通过的操作日志
				OperateRecord record = new OperateRecord();
				record.setOrderId(sm.getId());
				record.setOperateType(2);
				record.setResult(1);
				if(remark != null ){
					record.setComment(remark.getRemark());
					record.setEmail(remark.getOperator());
				}else{
					record.setEmail("admin");
				}
				boolean saveOpt = operateRecordService.saveOperateRecord(record);
				Logger.info("saveOperateRecord_Flag--->" + saveOpt);
				Logger.debug(">>>>>>sm>>" + Json.toJson(sm).toString());
				//2017-5-20 请求生成电子面单
				sm.setIsPushed(1);
				flag = updateByPrimaryKeySelective(sm);
				Logger.info("updateSaleMainOrder_Flag--->" + flag);
				kdnService.requestOrderOnline(sm);
			}
		} catch (Exception e) {
			Logger.info("二次支付审核异常",e);
		}
		return flag;
	}

	/**
	 * 根据email更改订单上的昵称
	 */
	@Override
	public String changeNickNameByEmail(String param) {
		JsonNode json = Json.parse(param);
		ObjectNode result = Json.newObject();
		SaleMain sale = new SaleMain();
		sale.setEmail(json.get("email").asText());
		sale.setNickName(json.get("nickName").asText());
	    int flag = saleMainMapper.updateNickNameByEmail(sale);
	    Logger.info("changeNickNameByEmail----->" +flag);
	    if (flag == 0) {
	    	result.put("suc", false);
	    	result.put("msg", "更新失败");
	    	return result.toString();
	    }
	    
	    result.put("suc", true);
    	result.put("data", flag);
		return result.toString();
	}

	@Override
	public Map<String, Object> updateErpStatus(String reqStr) {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<UpdateErpStatusResultDto> list = Lists.newArrayList();
		JsonNode node = Json.parse(reqStr);

		if(node == null){
			resultMap.put("suc", false);
			resultMap.put("msg", "传入数据为空");
			return resultMap;
		}

		if(node.isArray()){
			Iterator<JsonNode> nodeIterator = node.iterator();
			while (nodeIterator.hasNext()) {
				ErpStatusDto erpStatusDto = Json.fromJson(nodeIterator.next(), ErpStatusDto.class);
				if(!StringUtils.isEmpty(erpStatusDto.getOrderNo())) {
					UpdateErpStatusResultDto updateErpStatusResultDto = new UpdateErpStatusResultDto();
					SaleMain saleMain = saleMainMapper.selectByOrderNo(erpStatusDto.getOrderNo());
					if (saleMain != null) {
						saleMain.setErpStatus(erpStatusDto.getStatus());
						updateErpStatusResultDto.setFlag(updateByPrimaryKeySelective(saleMain));
						updateErpStatusResultDto.setOrderNo(erpStatusDto.getOrderNo());
						list.add(updateErpStatusResultDto);
					} else {
						updateErpStatusResultDto.setOrderNo(erpStatusDto.getOrderNo());
						updateErpStatusResultDto.setFlag(false);
						list.add(updateErpStatusResultDto);
					}
				}
			}
			resultMap.put("suc", true);
			resultMap.put("results", list);
			return resultMap;
		} else {
			resultMap.put("suc", false);
			resultMap.put("msg", "传入数据有误");
			return resultMap;
		}
	}

	@Override
	public boolean updateByPrimaryKeySelective(SaleMain main) {
		if (null != main && StringUtils.isNotEmpty(main.getSalesOrderNo()) && main.getSalesOrderNo().startsWith("MD")
				&& main.getStatus() != null) {
			pushStatusToStory(main);
		}
		boolean index = saleMainMapper.updateByPrimaryKeySelective(main) > 0;
		//add by xuse 审核不通过直接关闭订单
		if (index && main.getStatus() == SaleOrderStatus.AUDIT_NOT_PASSED) {
			// 关闭发货单
			ebus.post(new ClosedOrderEvent(main.getSalesOrderNo(), main.getEmail()));
		}
		return index;
	}

	@Override
	public Map<String, Object> batchAuditByFinance(String node) {
		Map<String,Object> res = Maps.newHashMap();

		List<SaleAuditDto> failList = Lists.newArrayList();
		try {
			JsonNode jsonNode = Json.parse(node);

			int status = jsonNode.get("status").asInt();
			JsonNode orderIdListNode = jsonNode.get("orderIdList");
			String comment = jsonNode.has("comment") ? jsonNode.get("comment").asText().trim() : "";

			String email = userService.getAdminAccount(); //operator

			if (orderIdListNode.size() < 1) {
				res.put("result", false);
				res.put("msg", "更新订单异常");
				return res;
			}

			for (Iterator<JsonNode> it = orderIdListNode.iterator(); it.hasNext(); ) {
				JsonNode nextNode = it.next();

				Integer orderId = nextNode.asInt();
				SaleMain saleMain = getSaleMainOrderByID(orderId);
				if (saleMain != null && saleMain.getWarehouseId() != 2050 && saleMain.getWarehouseId() != 2029) {//保税仓需二次支付，过滤掉
					if(saleMain.getStatus() != SaleOrderStatus.WAITING_AUDIT_BY_FINANCE){
						SaleAuditDto saleAuditDto = new SaleAuditDto();
						saleAuditDto.setId(orderId);
						saleAuditDto.setResult("该订单不是待财务确认状态，不能进行财务确认,请刷新页面!");
						failList.add(saleAuditDto);
					}

					// 审核通过
					if (status == SaleOrderStatus.WAITING_DELIVERY_SIX) {
						if (Strings.isNullOrEmpty(saleMain.getPaymentNo())) {
							//无需实际支付时审核通过，手动生成支付信息
							saleMain.setPaymentNo(IDUtils.getPayNo());
							saleMain.setPayDate(new Date());
							saleMain.setPaymentType("system");
							saleMain.setCurrency("CNY");
							saleMain.setRejectedByFinance(false);
						}

					} else if (status == SaleOrderStatus.WAITING_AUDIT_BY_CS) {// 审核不通过
						// 如果是打回客服审核，设置标识
						saleMain.setRejectedByFinance(true);
					}

					saleMain.setEmail(email);
					saleMain.setStatus(Integer.valueOf(status));

					//快递鸟电子面单
					if (status == SaleOrderStatus.WAITING_DELIVERY_SIX) {
						saleMain.setIsPushed(1);
					}

					Logger.info("checkSaleMain    SaleMain--->" + Json.toJson(saleMain).toString());
					boolean result = updateByPrimaryKeySelective(saleMain);

					if (status == SaleOrderStatus.WAITING_DELIVERY_SIX) {
						kdnService.requestOrderOnline(saleMain);
					}
					if (!result) {
						SaleAuditDto saleAuditDto = new SaleAuditDto();
						saleAuditDto.setId(orderId);
						saleAuditDto.setResult("更新销售订单状态失败");
						failList.add(saleAuditDto);
					}


					//更新优惠券状态
					saleMain.setId(orderId);
					updateCouponsState(saleMain);
					//判断是否已有审核通过的操作日志
					boolean savevflag = lockService.saveRecord(saleMain.getId(), 2, status == 6 ? 1 : 0, comment, email);
					Logger.info("saveOperateRecord_Flag--->" + savevflag);
				} else {
					Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>batchAuditByFinance 发货单不存在或发货单属于保税仓 orderId：【{}】", orderId);
					SaleAuditDto saleAuditDto = new SaleAuditDto();
					saleAuditDto.setId(orderId);
					saleAuditDto.setResult("发货单不存在");
					failList.add(saleAuditDto);
				}
			}

			Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>批量财务审核失败信息：【{}】", failList);
			Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>批量财务审核个数：【{}】， 成功个数：【{}】， 失败个数：【{}】",
					orderIdListNode.size(), orderIdListNode.size() - failList.size(), failList.size());

			res.put("result", true);
			res.put("msg", "更新销售订单状态成功");
			return res;
		} catch (Exception e) {
			Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>批量财务审核失败信息：【{}】", failList);
			Logger.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>批量财务审核失败：【{}】", e);
			res.put("result", false);
			res.put("msg", "更新订单异常");
			return res;
		}
	}
}
