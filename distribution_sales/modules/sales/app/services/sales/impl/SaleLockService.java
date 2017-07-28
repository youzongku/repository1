package services.sales.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import dto.JsonResult;
import dto.sales.SaleLockDto;
import dto.sales.SaleOrderAllDetails;
import dto.sales.TaoBaoGoodsSearchDto;
import entity.marketing.MarketingOrder;
import entity.platform.order.template.TaoBaoOrderGoods;
import entity.sales.OperateRecord;
import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleInvoice;
import entity.sales.SaleMain;
import events.sales.AutoPayEvent;
import events.sales.CalculateOptFeeEvent;
import mapper.marketing.MarketingOrderMapper;
import mapper.sales.SaleBaseMapper;
import mapper.sales.SaleDetailMapper;
import mapper.sales.SaleMainMapper;
import mapper.sales.TaoBaoOrderGoodsMapper;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import services.base.utils.JsonFormatUtils;
import services.sales.IHttpService;
import services.sales.IKdnService;
import services.sales.IManagerOrderService;
import services.sales.IOperateRecordService;
import services.sales.ISaleInvoiceService;
import services.sales.ISaleLockService;
import services.sales.ISaleMainService;
import services.sales.ISaleService;
import services.sales.ISequenceService;
import services.sales.ITaoBaoOrderGoodsService;
import services.sales.IUserService;
import util.sales.AddressUtils;
import util.sales.Constant;
import util.sales.DateUtils;
import util.sales.HttpUtil;
import util.sales.IDUtils;
import util.sales.JsonCaseUtil;
import util.sales.MD5Util;
import util.sales.PriceFormatUtil;
import util.sales.SaleOrderStatus;
import util.sales.StringUtils;

/**
 * 销售发货锁库 service 实现
 * @author zbc
 * 2016年12月27日 上午10:48:39
 */
public class SaleLockService implements ISaleLockService {
	
	@Inject private IHttpService httpService;
	@Inject private ISaleService saleService;
	@Inject private SaleMainMapper saleMainMapper;
	@Inject private SaleBaseMapper saleBaseMapper;
	@Inject private SaleDetailMapper saleDetailMapper;
	@Inject private EventBus ebus;
	@Inject private IOperateRecordService operateRecordService;
	@Inject private IUserService userService;
	@Inject private ISaleMainService mainSerivce;
	@Inject private ISequenceService sequenceService;
	@Inject private TaoBaoOrderGoodsMapper taoBaoOrderGoodsMapper;
	@Inject private ITaoBaoOrderGoodsService tbGoodsService;
	@Inject private ISaleMainService saleMainService;
	@Inject private MarketingOrderMapper  marketingOrderMapper;
	@Inject private IManagerOrderService managerOrderService;
	@Inject private ISaleInvoiceService invoiceService;
	@Inject private IKdnService kdnService;
	
	private static final byte[] lock = new byte[0];
	
	@Override
	public Map<String, Object> order(JsonNode main) {
		Map<String, Object> result = Maps.newHashMap();
		List<String> skus = Lists.newArrayList();
		// add by zbc 校验发票信息
		JsonResult<SaleInvoice> invoiceResult = invoiceService.checkVaildInvoice(main);
		if(!invoiceResult.getResult()){
			return resultMsg(110, invoiceResult.getMsg(), result,null);
		}
		// 分销账号
		String email = main.get("email").textValue();
		// 仓库ID
		Integer warehouseId = main.get("warehouseId").asInt();
		JsonNode skuObj = main.get("skuList");
		// final price
		Map<String, Double> finalPriceMap = Maps.newHashMap(); 
		Map<String, Integer> skuNumMap = Maps.newHashMap();
		
		if (skuObj.isArray()) {
			String sku;
			JsonNode jsonNode;
			for (Iterator<JsonNode> it = skuObj.iterator();it.hasNext();) {
				jsonNode = (JsonNode) it.next();
				sku = jsonNode.get("sku").asText();
				skus.add(sku);
				skuNumMap.put(sku, jsonNode.get("num").asInt());
				finalPriceMap.put(sku, jsonNode.get("finalSellingPrice").asDouble());
			}
		} else {
			String sku = skuObj.get("sku").asText();
			skus.add(sku);
			skuNumMap.put(sku, skuObj.get("num").asInt());
			finalPriceMap.put(sku, skuObj.get("finalSellingPrice").asDouble());
		}
		
		JsonNode shopNode = null;
		try {
			// 获取店铺
			if(main.get("shopId") != null){
				shopNode = httpService.getShopInfo(main.get("shopId").asInt());
			}
		} catch (Exception e) {
			Logger.error("getShopInfo:" + e);
			return resultMsg(102, "未查询到店铺信息", result,null);
		}
		// 店铺扣点
		Double shopDeductionPoints = null;
		if (shopNode != null && shopNode.get("deductionPoints") != null
				&& email.equals(shopNode.get("email").asText())) {// 要是本人的店铺
			shopDeductionPoints = shopNode.get("deductionPoints").asDouble();
		}
		JsonNode memberNode = null;
		try {
			// 获取用户详情
			memberNode = httpService.getMemberInfo(email);
			if (memberNode == null || !memberNode.has("comsumerType")) {
				return resultMsg(105, "未查询到用户信息", result,null);
			}
		} catch (Exception e) {
			Logger.error("getMemberInfo:" + e);
			return resultMsg(105, "未查询到用户信息", result,null);
		}

		Integer distributionMode = memberNode.get("distributionMode").asInt();
		String nickName = (memberNode.get("nickName") == null ||
				memberNode.get("nickName").asText().equals("null")) ? null : memberNode.get("nickName").asText();

		JsonNode productStrNode = null;
		try {
			// 获取商品
			productStrNode = httpService.getProducts(email, skus, warehouseId, distributionMode);
			if (productStrNode == null || productStrNode.get("data").get("result").size() <= 0) {
				return resultMsg(104, "未查询到商品信息", result,null);
			}
		} catch (Exception e) {
			Logger.error("getProducts:" + e);
			e.printStackTrace();
			return resultMsg(104, "未查询到商品信息", result,null);
		}

		JsonNode custStrNode = null;
		try {
			// 获取客服账号
			custStrNode = httpService.custaccount(email);
			if (null == custStrNode || StringUtils.isBlankOrNull(custStrNode.get("account").asText())) {
				return resultMsg(103, "未关联客服账号", result,null);
			}
		} catch (Exception e) {
			Logger.error("custaccount:" + e);
			return resultMsg(103, "未关联客服账号", result,null);
		}

		SaleBase sb = null;
		SaleMain sm = null;
		List<SaleDetail> sdList = null;
		JsonNode checkNode = null;
		try {
			// 插入订单Main
			sm = saleService.parseSaleMain(main);
			if(null == sm) {
				Logger.error("解析Main参数失败。");
				return resultMsg(109, "获取运费失败", result,null);
			}
			Integer distributorType = memberNode.get("comsumerType").asInt();
			sm.setDistributorType(distributorType);
			sm.setSource("BBC");
			sm.setDisMode(distributionMode);
			sm.setNickName(nickName);
			//add by zbc 设置用户归属  
			sm.setAttributionType(JsonCaseUtil.jsonToInteger(memberNode.get("attributionType")));
			//2017-5-27 用户是否包邮
			sm.setIsPackageMail(JsonCaseUtil.JsonToShort(memberNode.get("isPackageMail")));
			// 插入订单base
			sb = saleService.parseSaleBase(sm, main, shopNode, custStrNode, skuObj);
			if(null == sb) {
				Logger.error("获取运费失败");
				return resultMsg(109, "获取运费失败", result,null);
			}

			//update by longhs 2017-4-13
			sm.setShopDeductionPoints(shopDeductionPoints == null ? 0 : shopDeductionPoints);
			double sdpAmount = 0d;
			if (shopDeductionPoints != null && sb.getOrderActualAmount() != null) {
				sdpAmount = PriceFormatUtil
						.toFix2(new BigDecimal(sb.getOrderActualAmount()).multiply(new BigDecimal(shopDeductionPoints)));
			}
			sm.setSdpAmount(sdpAmount);
			sm.setThirdPartLogisticsTypeCode(sb.getThirdPartLogisticsTypeCode());
			// 生产订单前查询库存是否充足 再来决定是否下单
			
			// 构造详情
			sdList = parseSaleDetail(sm, productStrNode, skuNumMap, memberNode, finalPriceMap);
			// 微仓锁库
			checkNode = saleOut(sm,sdList,null,false);
			switch (checkNode.get("type").asText()) {
			case "4":
				return resultMsg(107, "锁库异常", result,null);
			case "3":
				return resultMsg(107, "云仓微仓库存不足", result,null);
			default:
				break;
			}
			saleMainMapper.insertSelective(sm);
			// 插入详情
			for(SaleDetail de:sdList){
				de.setSalesOrderId(sm.getId());
				if(de.getFinalSellingPrice() == null) {
					de.setFinalSellingPrice(de.getPurchasePrice());					
				}
				saleDetailMapper.insertSelective(de);
			}
			sb.setSalesOrderId(sm.getId());
			saleBaseMapper.insertSelective(sb);
			// 更新优惠码信息
			saleService.syncCoupons(sm.getId());
			//add by zbc 保存发票信息
			invoiceService.save(sm,sb.getCreateUser(),invoiceResult.getData());
		} catch (Exception e) {
			deleteSaleOrder(sm);
			Logger.info("生成订单异常[{}]", e);
			return resultMsg(106, "生成订单失败", result,null);
		}
		try {
			// 计算操作费
			saleService.calculateOptFee(sm,sb,sdList);
			parseCheckNode(email, sm, sb, checkNode, sdList);
			// 是否自动支付
			if (main.has("isPay") && main.get("isPay").asBoolean()) {// 表示是后台选择用余额支付的操作
				Logger.info("后台自动扣款：" + sm.getSalesOrderNo());
				AutoPayEvent pay = new AutoPayEvent();
				pay.setMain(sm);
				pay.setBase(sb);
				pay.setDetails(sdList);
				pay.setFreight(sb.getBbcPostage());
				pay.setPurchaseNo(sm.getPurchaseOrderNo());
				ebus.post(pay);
			}
			Map<String, Object> resultMsg = resultMsg(108, "生成发货单"+sm.getSalesOrderNo()+"成功！", result,sm.getStatus());
			//前台下单成功支付需要以下参数（add by xuse）
			resultMsg.put("orderno", sm.getSalesOrderNo());//销售单号
			resultMsg.put("sid", sm.getId());//销售单ID
			resultMsg.put("purno", sm.getPurchaseOrderNo());//采购单号
			return resultMsg;
		} catch (Exception e) {
			Logger.error("库存检查失败:" + e);
			return resultMsg(107, "库存检查失败", result,null);
		}
	}
	
	private Map<String, Object> resultMsg(Integer code, String msg, Map<String, Object> result,Integer status) {
		result.put("code", code);
		result.put("msg", msg);
		result.put("status", status);
		return result;
	}
	
	/**
	 * 异步计算操作费
	 * @param sm
	 * @param sb
	 * @param details
	 */
	private void asyncCalculateOptFee(SaleMain sm, SaleBase sb, List<SaleDetail> details){
		CalculateOptFeeEvent calculateOptFeeEvent = new CalculateOptFeeEvent();
		calculateOptFeeEvent.setBase(sb);
		calculateOptFeeEvent.setMain(sm);
		calculateOptFeeEvent.setDetails(details);
		ebus.post(calculateOptFeeEvent);
	}
	
	/**
	 * 
	 * {
		  "type": 1,//1 充足 ,2 缺货
		  "successOrLocks": [
		    {
		      "account": "854287501@qq.com", 
		      "sku": "IF968-1", 
		      "title": "泰国日清巧克力味威化饼干100g", 
		      "imgUrl": "www.google.com", 
		      "qty": 10, 
		      "purchasePrice": 55.79, 
		      "capfee": 55.79, 
		      "warehouseId": 2024, 
		      "warehouseName": "深圳仓", 
		      "expirationDate": "2017-04-02", 
		      "arriveWarePrice":55.6,
		      "purchaseNo": "CG201612240931", 
		      "isGift": 0
		    }, 
		    {
		      "account": "854287501@qq.com", 
		      "sku": "IF968-2", 
		      "title": "泰国日清巧克力味威化饼干200g", 
		      "imgUrl": "www.google.com", 
		      "qty": 8, 
		      "purchasePrice": 55.79, 
		      "arriveWarePrice":55.6,
		      "capfee": 55.79, 
		      "warehouseId": 2024, 
		      "warehouseName": "深圳仓", 
		      "expirationDate": "2018-04-02", 
		      "purchaseNo": "CG201612240931", 
		      "isGift": 0
		    }
		  ], 
		  "purchases": null
		}
	 * @author zbc
	 * @since 2016年12月27日 下午12:24:52
	 */
	private void parseCheckNode(String email, SaleMain sm, SaleBase sb, JsonNode checkNode,
			List<SaleDetail> sd) throws JsonProcessingException, IOException {
		//  解析锁库参数
		String type = checkNode.get("type").asText();
		Integer  distributorType = sm.getDistributorType();
		JsonNode successOrLocks = null;
		JsonNode purchases = null;
		Map<String, SaleDetail> sdMap = Maps.uniqueIndex(sd, s -> s.getSku());
		if("1".equals(type)){
			// 微仓充足
			successOrLocks = checkNode.get("successOrLocks");
		}else if("2".equals(type)){
			//缺货
			successOrLocks = "null".equals(checkNode.get("successOrLocks").asText())?null:checkNode.get("successOrLocks");
			purchases = checkNode.get("purchases");
		}
		List<Map<String, Object>> giftList = Lists.newArrayList();
		if(successOrLocks != null){
			//标记赠品
			Map<String, Object> gift = null;
			for (Iterator<JsonNode> node = successOrLocks.iterator();node.hasNext();) {
				JsonNode jsonNode = (JsonNode) node.next();
				if("1".equals(jsonNode.get("isGift").asText())) {
					gift = Maps.newHashMap();
					gift.put("salesOrderNo", sm.getSalesOrderNo());
					gift.put("isgift", true);
					gift.put("giftNum", jsonNode.get("qty").asInt());
					gift.put("sku", jsonNode.get("sku").asInt());
					giftList.add(gift);
				}
				//插入历史数据
				saveHistory(sm,jsonNode);
			}
		}
		
		Integer saleStatus = SaleOrderStatus.WAITING_PAY;
		//需要支付的金额
		BigDecimal tram = null;
		// 需要采购的sku
		List<Map<String, Object>> purchaseDetail = Lists.newArrayList();
		if(purchases != null &&!"null".equals(purchases.asText())&& purchases.size()>0){
			BigDecimal totalPrice = BigDecimal.ZERO;// 总价
			for(JsonNode pur:purchases){
				String sku = pur.get("sku").asText();
				purchaseDetail.add(getDetail(pur, sm.getSalesOrderNo(), sdMap));
				totalPrice = totalPrice.add(new BigDecimal(pur.get("qty").asInt()).multiply(new BigDecimal(sdMap.get(sku).getPurchasePrice())));
			}
			Map<String, Object> pMap = Maps.newHashMap();
			pMap.put("sid", sm.getId());
			pMap.put("distributorType", distributorType);
			pMap.put("email", email);
			pMap.put("orderDetail", purchaseDetail);
			pMap.put("totalPrice", PriceFormatUtil.toFix2(totalPrice));
			pMap.put("bbcPostage", sb.getBbcPostage());
			pMap.put("couponsAmount", sb.getCouponsAmount());
			pMap.put("couponsCode", sb.getCouponsCode());
			pMap.put("logisticsMode", sb.getLogisticsMode());
			JsonNode purchaseNode = httpService.postPurchase(pMap);
			String purNo = purchaseNode.get("errorInfo").asText();
			tram = saleService.getAmount(purNo,sb);
			sm.setPurchaseOrderNo(purNo);
			saleStatus = SaleOrderStatus.WAITING_PAY;
			//内部直接扣库存 或者待支付费用  小于等于0
 			if (distributorType == 3||tram.compareTo(BigDecimal.ZERO)<= 0) {
				//完成订单，更新库存
			    httpService.finishedOrder(purNo, tram.doubleValue());
			    return;
			}
		}else{
			tram = saleService.getAmount(null,sb);
			saleStatus = SaleOrderStatus.WAITING_PAY_BBC_POSTAGE;// 待支付运费
			if (distributorType == 3||tram.compareTo(BigDecimal.ZERO)<= 0) {
				JsonNode finishJson = Json.newObject().
						put("isComplete", true).
						put("id", sm.getId());
				finishSaleOrder(finishJson.toString());
				return;
			}
		}
		sm.setStatus(saleStatus);
		saleMainService.updateByPrimaryKeySelective(sm);
	}
	
	@Override
	public Map<String,Object> saveMicroOutHistory(Integer id, String str) {
		Map<String,Object> res = Maps.newHashMap();
		try {
			SaleMain sm = saleMainMapper.selectByPrimaryKey(id);
			if(sm==null){
				res.put("suc", false);
				res.put("msg", "该发货单不存在");
				return res;
			}
			
			JsonNode mircoOut = Json.parse(str);
			for(JsonNode out:mircoOut){
				saveHistory(sm, out);
			}
//			saleService.syncLogs(sm, Constant.UPDATE);
			res.put("suc", true);
			res.put("msg", "插入历史数据成功");
			return res;
		} catch (Exception e) {
			Logger.info("插入历史数据异常",e);
			res.put("suc", false);
			res.put("msg", "插入历史数据异常");
			return res;
		}
	}

	/**
	 * 保存历史数据
	 * @author zbc
	 * @since 2016年12月27日 下午2:27:16
	 */
	@Override
	public void saveHistory(SaleMain sm, JsonNode json) {
		SaleDetail saleDetai = new SaleDetail();
		saleDetai.setSku(json.get("sku").asText());
		saleDetai.setQty(json.get("qty").asInt());
		saleDetai.setPurchaseOrderNo(json.get("purchaseNo").asText());// 采购单号
		saleDetai.setProductName(JsonCaseUtil.jsonToString(json.get("productTitle")));
		saleDetai.setProductImg(JsonCaseUtil.jsonToString(json.get("imgUrl")));
		saleDetai.setPurchasePrice(JsonCaseUtil.jsonToDouble(json.get("purchasePrice")));
		saleDetai.setWarehouseId(JsonCaseUtil.jsonToInteger(json.get("warehouseId")));
		saleDetai.setWarehouseName(JsonCaseUtil.jsonToString(json.get("warehouseName")));
		saleDetai.setSalesOrderId(sm.getId());
		saleDetai.setSalesOrderNo(sm.getSalesOrderNo());
		saleDetai.setIsDeducted(1);
		//到仓价
		saleDetai.setArriveWarePrice(JsonCaseUtil.jsonToDouble(json.get("arriveWarePrice")));
		//均摊价
		saleDetai.setCapFee(JsonCaseUtil.jsonToDouble(json.get("capfee")));
		saleDetai.setIsDeductedHistory(1);
		saleDetai.setIsgift("1".equals(json.get("isGift").asText()));
		//到期日期
		saleDetai.setExpirationDate(JsonCaseUtil.jsonStrToDate(json.get("expirationDate"),DateUtils.FORMAT_DATE_PAGE));
		saleDetai.setContractNo(JsonCaseUtil.jsonToString(json.get("contractNo")));
		saleDetai.setClearancePrice(JsonCaseUtil.jsonToDouble(json.get("clearancePrice")));
		SaleDetail detail = saleDetailMapper.getDetailBySkuAndOrderNo(saleDetai);
		if(detail != null){
			saleDetai.setFinalSellingPrice(detail.getFinalSellingPrice());
			saleDetai.setProductName(detail.getProductName());
			saleDetai.setInterBarCode(detail.getInterBarCode());
		}
		//插入数据
		saleDetailMapper.insertSelective(saleDetai);
	}

	private Double getValue(Double money){
		return money!=null?money:0;
	}
	
	private Map<String, Object> getDetail(JsonNode node, String salesOrderNo, Map<String, SaleDetail> sdMap) {
		Map<String, Object> detail = Maps.newHashMap();
		String sku = node.get("sku").asText();
		SaleDetail sd = sdMap.get(sku);
		Integer qty =  node.get("qty").asInt();
		detail.put("itemId", "");
		detail.put("title", sd.getProductName());
		detail.put("interBarCode", sd.getInterBarCode());
		detail.put("price", sd.getPurchasePrice());
		detail.put("warehouseId", sd.getWarehouseId());
		detail.put("warehouseName", sd.getWarehouseName());
		detail.put("sumPrice", PriceFormatUtil.toFix2(new BigDecimal(sd.getPurchasePrice()*qty)));
		detail.put("marketPrice", sd.getMarketPrice());
		detail.put("publicImg", sd.getProductImg());
		detail.put("sku", sku);
		detail.put("salesOrderNo", salesOrderNo);
		detail.put("qty",qty);
		detail.put("expirationDate", JsonCaseUtil.jsonToString(node.get("expirationDate")));
		detail.put("categoryId",sd.getCategoryId());
		detail.put("categoryName", sd.getCategoryName());
		detail.put("disStockId", sd.getWarehouseId());// 仓库id
		detail.put("disPrice", sd.getDisPrice()); // 分销价
		detail.put("contractNo", sd.getContractNo()); // 合同号
		detail.put("clearancePrice", sd.getClearancePrice()); // 清货价
		return detail;
	}

	/**
	 * 发货锁定微仓：
	 * 
	 {
	    "orderNo": "XS201612221544004",
	    "warehouseId": 2024,
	    "warehouseName": "深圳仓",
	    "account": "854287501@qq.com",
		"purchaseNo":"XXXX",
		"lockCloud":true,
	    "pros": [
	        {
	            "sku": "IF968-1",
	            "qty": 10,
	            "productTitle":"泰国日清巧克力味威化饼干100g",
	            "imgUrl": "www.LOL.com"
	        },
	        {
	            "sku": "IF968-2",
	            "qty": 8,
	            "productTitle":"泰国日清巧克力味威化饼干300g",
	            "imgUrl": "www.qq.com"
	        }
	    ]
	}
	 */
	@Override
	public JsonNode saleOut(SaleMain main, List<SaleDetail> list, String marketNo, Boolean lockCloud) {
		JsonNode saleOut = null;
		try {
			Map<String,Object> postMap = Maps.newHashMap();
			postMap.put("orderNo", main.getSalesOrderNo());
			postMap.put("warehouseId", main.getWarehouseId());
			postMap.put("warehouseName", main.getWarehouseName());
			postMap.put("account", main.getEmail());
			postMap.put("lockCloud", lockCloud);
			//指定采购单出库:营销单
			if(marketNo != null){
				postMap.put("purchaseNo", marketNo);
			}
			List<SaleLockDto> lockList = Lists.newArrayList();
			for(SaleDetail detail:list){
				lockList.add(new SaleLockDto(detail));
			}
			postMap.put("pros", lockList);
			saleOut = httpService.microLock(postMap);
		} catch (Exception e) {
			Logger.info("微仓锁库异常",e);
		}
		return saleOut;
	}
	
	private List<SaleDetail> parseSaleDetail(SaleMain sm, JsonNode productStrNode, Map<String, Integer> skuNumMap,
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
			sd.setInterBarCode(jsonNode.has("interBarCode")?jsonNode.get("interBarCode").asText():null);
			sd.setSku(sku);
			purchase = jsonNode.get("disPrice").asDouble();
			sd.setPurchasePrice(purchase);// 分销价
			sd.setDisPrice(purchase);
			sd.setQty(skuNumMap.get(sku));
			sd.setWarehouseId(warehouseId);
			sd.setWarehouseName(jsonNode.get("warehouseName").textValue());
			sd.setProductImg(jsonNode.get("imageUrl").textValue());
			sd.setSalesOrderNo(sm.getSalesOrderNo());
			sd.setIsDeducted(0);

			sd.setGstRate(jsonNode.get("gstRate").asDouble());// 消费税税率
			sd.setVatRate(jsonNode.get("vatRate").asDouble());// 增值税税率
			sd.setImportTarRate(jsonNode.get("importTarRate").asDouble());// 关税税率
			sd.setPostalFeeRate(jsonNode.get("postalFeeRate").asDouble());// 行邮税税率
			sd.setLogisticFee(jsonNode.get("logisticFee").asDouble());// 头程运费
			sd.setCategoryId(JsonCaseUtil.jsonToInteger(jsonNode.get("categoryId")));
			sd.setCategoryName(JsonCaseUtil.jsonToString(jsonNode.get("cname")));
			sd.setContractNo(JsonCaseUtil.jsonToString(jsonNode.get("contractNo")));
			sd.setClearancePrice(JsonCaseUtil.jsonToDouble(jsonNode.get("clearancePrice")));
			// 最终售价上下浮不能超过分销价的 50%
			Double finalSellingPrice = finalPriceMap == null ? purchase : finalPriceMap.get(sku);
			// 设置其他平台最终售价
			if (sm.getDistributorType() != 3 && sm.getWarehouseId() != 2024) {
				sd.setFinalSellingPrice(finalSellingPrice > purchase * 1.5 ? purchase * 1.5
						: finalSellingPrice < purchase * 0.5
								? PriceFormatUtil.toFix2(new BigDecimal(purchase * 0.5)): finalSellingPrice);
			} else {
				sd.setFinalSellingPrice(finalSellingPrice);
			}
			sds.add(sd);
		}
		return sds;
	}
	
	/**
	 * 
	 * @author zbc
	 * @since 2016年12月28日 上午10:15:02
	 */
	@Override
	public Map<String, Object> cancelOrder(JsonNode main,String disAccount) {
		//add by zbc  追加同步锁，防止重复调用
		synchronized (lock) {
			Map<String,Object> res = Maps.newHashMap();
			// change by zbc  正在实现
			try {
				SaleMain saleMain = saleMainMapper.selectByOrderNo(main.get("so").asText());
				Logger.info("cancelOrder，取消发货单------>saleMain：{}",saleMain);
				// 检查是否可以进行操作  
				List<Integer> cancelStatusList = Arrays.asList(
						SaleOrderStatus.WAITING_PAY,
						SaleOrderStatus.WAITING_CONFIRM_BY_CUSTOMER,
						SaleOrderStatus.WAITING_PAY_BBC_POSTAGE,
						SaleOrderStatus.AUDIT_NOT_PASSED,
						SaleOrderStatus.WAITING_AUDIT_BY_CS,
						SaleOrderStatus.WAITING_AUDIT_BY_FINANCE);
				if(cancelStatusList.indexOf(saleMain.getStatus()) == -1){
					res.put("suc", false);
					res.put("msg", "不能进行取消操作，请刷新页面查看订单最新状态");
					return res;
				}
				if(disAccount != null){
					if(!disAccount.equals(saleMain.getEmail())){
						res.put("suc", false);
						res.put("msg", "非本人不能操作该订单");
						return res;
					}
					if(saleMain.getIsCombine()){
						res.put("suc", false);
						res.put("msg", "该发货单已经被合并，不能取消");
						return res;
					}
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
				Logger.info("获取到的退款金额为：{}",can);
				String pNo = saleMain.getPurchaseOrderNo();
				//内部分销商还回云仓 虚拟仓发货退回到云仓
				boolean restoCloud = (saleMain.getDistributorType() == 3  || saleMain.getIsCombine()||saleMain.isVirtualHouseOrder())&&pNo !=null;
				ObjectNode backRes = backMoneyAndStock(can.getTram(), saleMain, restoCloud,5,disAccount != null?disAccount:"system");
				boolean suc = backRes.get("suc").asBoolean();
				if(suc){
					Boolean purClose = JsonCaseUtil.jsonToBoolean(main.get("purClose")) ;
					//关闭采购单 如果标识不关闭，则不进行关闭
					if((purClose == null|| !purClose) && can.getPurClose()){
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
	}
	/**
	 * @param traAm       退款金额
	 * @param main        发货单
	 * @param restoCloud        是否退还云仓
	 * @param status      订单状态
	 * @param operator    操作人
	 * 退钱退库存
	 * @author lzl
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @since 2016年11月16日下午6:27:58
	 */
	public ObjectNode backMoneyAndStock(BigDecimal traAm,SaleMain main, boolean	restoCloud,Integer status,String operator) throws JsonProcessingException, IOException{
		ObjectNode result = Json.newObject();
		if (traAm != null) {//退款
			JsonNode refundNode = httpService.refund(main.getEmail(), traAm, main.getSalesOrderNo());
			if (refundNode.get("code").asInt() == 4) {//说明退款成功
				result = backStock(main,restoCloud,status,operator);
			} else {
				result.put("suc", false);
				result.put("msg", "订单"+main.getSalesOrderNo()+"退款失败");
			}
		} else {
			result = backStock(main,restoCloud,status,operator);
		}
		return result;
	}
	/**
	 * 库存还原
	 * @author lzl
	 * @since 2016年11月18日下午2:03:31
	 */
	@SuppressWarnings("deprecation")
	private ObjectNode backStock(SaleMain main, boolean restoCloud,Integer status,String operator){
		ObjectNode result = Json.newObject();
		try {
			boolean flag = false;
			List<SaleDetail> historDetails = saleDetailMapper.getHistoryByOrderId(main.getId());//查询销售发货详情历史记录，并且进行库存还原（还原微仓）
			if (historDetails.size() > 0) {
				JsonNode resto  = httpService.restoreMicro(main.getSalesOrderNo());
				Logger.info("backStock微仓还原-------->[{}]",resto);
				flag = resto.get("result").asBoolean();
			}else{
				flag = true;
			}
			if (flag) {
				if (restoCloud) {//说明存在缺货采购,还原云仓
				    Logger.info("backStock云仓还原-------->[{}]", httpService.restoreCloud(main.getPurchaseOrderNo()));
				}
				// 1、添加取消订单的操作记录
				boolean saveResultOperateRecord = saveRecord(main.getId(), 
						status!=SaleOrderStatus.CLOSED_BY_ERP?4:11, 1, 
						status==SaleOrderStatus.CLOSED_BY_CUSTOMER?
						"取消订单":(status==SaleOrderStatus.CLOSED_BY_ERP?"关闭订单":"取消订单")
						, operator);
				Logger.info("销售订单状态改为取消添加操作记录结果："
						+ (saveResultOperateRecord ? "添加成功" : "添加失败"));
				main.setStatus(status);
				main.setIsClose(true);
				saleMainService.updateByPrimaryKeySelective(main);//更新状态
				mainSerivce.updateCouponsState(main);
				result.put("suc", true);
				result.put("data", Json.toJson(main));
			} else {
				result.put("suc", false);
				result.put("msg", "更新云仓库存失败");
			}
		} catch (Exception e) {
			result.put("suc", false);
			result.put("msg", "还原库存异常");
			Logger.info("还原库存异常",e);
		}
		
		return result;
	}
	
	/**
	 * 判断是否要关闭采购单以及计算要退款的金额
	 * @param saleMain
	 * @return
	 */
	private Cancel getTram(SaleMain saleMain) {
		boolean purClose = false;
		BigDecimal tram = null;
		try {
			//计算订单金额
			SaleBase base = saleBaseMapper.selectByOrderId(saleMain.getId());
			String pNo = saleMain.getPurchaseOrderNo();
			// 虚拟仓发货要退钱，先看是否是虚拟仓发货单，再看distributorType，顺序不能乱
			if(saleMain.isVirtualHouseOrder() || saleMain.getDistributorType() != 3){
				if(StringUtils.isNotBlankOrNull(pNo)){
					JsonNode purOrder = httpService.getPurByNo(pNo);
					if("0".equals(purOrder.get("returnMess").get("errorCode").asText())){
						JsonNode order = purOrder.get("orders").get(0);
						Integer poStatus = order.get("status").asInt();
						BigDecimal couponsAmount = new BigDecimal(getValue(JsonCaseUtil.jsonToDouble(order.get("couponsAmount"))));
						BigDecimal bbcPostage = new BigDecimal(getValue(JsonCaseUtil.jsonToDouble(order.get("bbcPostage"))));
						BigDecimal purchaseTotalAmount = new BigDecimal(getValue(JsonCaseUtil.jsonToDouble(order.get("purchaseTotalAmount"))));
						//已完成订单要退运费  若有优惠码 优惠金额大于运费不退钱 , 
						if(poStatus == 1){
							//合并发货单要退钱 关闭采购单 还云仓
							if(saleMain.getIsCombine() || saleMain.isVirtualHouseOrder()){
								tram =purchaseTotalAmount.subtract(couponsAmount).add(bbcPostage);
								purClose = true;
							}else{
								tram = purchaseTotalAmount.subtract(couponsAmount).compareTo(BigDecimal.ZERO)>0?
										bbcPostage:purchaseTotalAmount.subtract(couponsAmount).add(bbcPostage);
							}
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
					if(saleMain.getStatus() != SaleOrderStatus.WAITING_PAY_BBC_POSTAGE){
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

		@Override
		public String toString() {
			return "Cancel [purClose=" + purClose + ", tram=" + tram + "]";
		}
		
	}
	
	private Map<String,Object> resultMess(boolean red,String msg,Map<String,Object> res){
		res.put("suc", red);
		res.put("msg",msg);
		return res;
	}
	
	@Override
	public Map<String,Object> saveSaleOrder(JsonNode main) {
		Map<String,Object> resMap = Maps.newHashMap();
		boolean suc = false;
		String msg = null;

		//校验
		Map<String,Object> checkMap = checkSaveSaleOrder(main);
		boolean flag = (boolean) checkMap.get("suc");
		if (!flag) {
			return checkMap;
		}
		JsonResult<SaleInvoice> invoiceResult = invoiceService.checkVaildInvoice(main);
		if(!invoiceResult.getResult()){
			return resultMess(false, invoiceResult.getMsg(), resMap);
		}
		try {
			String tradeNo = JsonCaseUtil.jsonToString(main.get("tradeNo"));
			if(StringUtils.isNotBlankOrNull(tradeNo) && saleService.checkTradeNo(tradeNo.trim())) {
				return resultMess(false,main.get("base").get("platformOrderNo").asText()+"订单交易号重复",resMap);
			}
			String user = userService.getDismember();
			ObjectNode resultNode = Json.newObject();
			JsonNode userNode = Json.parse(user);
			String email = JsonCaseUtil.getStringValue(userNode, "email");
			String nickName = JsonCaseUtil.getStringValue(userNode,"nickName");
			Integer distributionMode = userNode.get("distributionMode").asInt();
			Integer comsumerType = userNode.get("comsumerType").asInt();
			Short isPackageMail = JsonCaseUtil.JsonToShort(userNode.get("isPackageMail"));
			// 返回结果
			SaleOrderAllDetails res = new SaleOrderAllDetails();
			// 保存销售发货单主表
			SaleMain sm = new SaleMain();
			//来源
			sm.setSource("BBC");
			sm.setSalesOrderNo(IDUtils.getSalesCode(sequenceService.selectNextValue("SALE_NO")));
			sm.setOrderingDate(new Date());
			sm.setEmail(email);
			sm.setNickName(nickName);
			sm.setDisMode(distributionMode);
			sm.setIsAdjusted(0);
			//add by zbc 插入分销商类型
			sm.setDistributorType(comsumerType);
			/**************** 目前因为不允许跨仓下单，所以在主表上维护仓库信息 *******************/
			sm.setTradeNo(tradeNo);
			sm.setWarehouseId(main.get("warehouseId").asInt());
			sm.setWarehouseName(main.get("warehouseName").textValue());

			//2017-6-9
			sm.setIsPackageMail(isPackageMail);
			/***********************************/
			
			// 保存销售发货单基本信息表
			JsonNode base = main.get("base");
			SaleBase sb = JsonFormatUtils.jsonToBean(base.toString(), SaleBase.class);
			String platformOrderNo = sb.getPlatformOrderNo();
			//TODO 尝试锁库  校验库存
			List<SaleDetail> sds = goodsDetails(main, email, distributionMode, sm, sb);
			// 微仓锁库
			JsonNode checkNode = saleOut(sm,sds,null,false);
			switch (checkNode.get("type").asText()) {
			case "4":
				return resultMess(false,"锁库异常",resMap);
			case "3":
				return resultMess(false,"云仓微仓库存不足",resMap);
			default:
				break;
			}
			saleMainMapper.insertSelective(sm);
			for(SaleDetail sd:sds){
				sd.setSalesOrderId(sm.getId());
			}
			res.setMainId(sm.getId());
			sb.setSalesOrderId(sm.getId());

			if(isPackageMail != null && isPackageMail == 1) {//包邮
				sb.setBbcPostage(0d);
			}
			saleBaseMapper.insertSelective(sb);
			// 保存销售发货单商品详细表
			saleDetailMapper.batchInsertSelective(sds);
			invoiceService.save(sm,sb.getCreateUser(), invoiceResult.getData());
			try {
				// TODO 校验是否生成缺货采购
				parseCheckNode(email, sm, sb, checkNode,sds);
			} catch (Exception e) {
				Logger.info("检查库存异常",e);
			}
			res.setCurrentDetail(sds);
			//删除已生成详情
			tbGoodsService.batchDeleteOrderGoods(new TaoBaoGoodsSearchDto(Lists.transform(sds, d->d.getSku()), platformOrderNo, email,Lists.newArrayList(platformOrderNo), sm.getWarehouseId().toString()));
			// 计算操作费
			asyncCalculateOptFee(sm,sb,sds);
			suc= true;
			resultNode.put("suc", true);
		} catch (Exception e) {
			suc =false;
			msg = "生成订单异常";
			Logger.info("生成订单异常",e);
		}
		return resultMess(suc, msg, resMap);
	}

	private Map<String, Object> checkSaveSaleOrder(JsonNode main) {
		Map<String, Object> resultMap = Maps.newHashMap();
		JsonNode base = main.get("base");
		if (base.get("receiver") instanceof NullNode || org.apache.commons.lang3.StringUtils.isBlank(base.get("receiver").asText())) {
			resultMap.put("suc", false);
			resultMap.put("msg","收货人姓名不能为空！");
			return resultMap;
		}

		if (base.get("address") instanceof NullNode || StringUtils.isBlankOrNull(base.get("address").asText()) || !AddressUtils.isAdjustAddress(base.get("address").asText())) {
			resultMap.put("suc", false);
			resultMap.put("msg","地址填写错误！");
			return resultMap;
		}

		if (base.get("tel") instanceof NullNode || org.apache.commons.lang3.StringUtils.isBlank(base.get("tel").asText())) {
			resultMap.put("suc", false);
			resultMap.put("msg","收货人联系方式不能为空！");
			return resultMap;
		}

		if (main.get("tradeNo") instanceof NullNode || org.apache.commons.lang3.StringUtils.isBlank(main.get("tradeNo").asText())) {
			resultMap.put("suc", false);
			resultMap.put("msg","交易单号不能为空！");
			return resultMap;
		}

		if (base.get("logisticsTypeCode") instanceof NullNode || org.apache.commons.lang3.StringUtils.isBlank(base.get("logisticsTypeCode").asText())) {
			resultMap.put("suc", false);
			resultMap.put("msg","必须选定物流方式！");
			return resultMap;
		}

		if (base.get("shopId") instanceof NullNode || org.apache.commons.lang3.StringUtils.isBlank(base.get("shopId").asText())) {
			resultMap.put("suc", false);
			resultMap.put("msg","必须选定店铺！");
			return resultMap;
		}

		if (main.get("warehouseId") instanceof NullNode || org.apache.commons.lang3.StringUtils.isBlank(main.get("warehouseId").asText())) {
			resultMap.put("suc", false);
			resultMap.put("msg","必须要选定仓库！");
			return resultMap;
		}

		resultMap.put("suc", true);
		return resultMap;
	}

	private List<SaleDetail> goodsDetails(JsonNode main, String email, Integer distributionMode, SaleMain sm,
										  SaleBase sb) {
		List<SaleDetail> sds = Lists.newArrayList();
		TaoBaoGoodsSearchDto search = new TaoBaoGoodsSearchDto();
		search.setOrderNo(sb.getPlatformOrderNo());
		search.setEmail(email);
		search.setWarehouseId(main.get("warehouseId").asText());
		List<TaoBaoOrderGoods> taoBaoGoods = taoBaoOrderGoodsMapper.getGoodsByParam(search);
		if (taoBaoGoods != null) {
			SaleDetail sd = null;
			try {
				JsonNode productNode = httpService.getProducts(email, Lists.transform(taoBaoGoods, i -> i.getSku()), main.get("warehouseId").asInt(), distributionMode);
				Double disprice = null;
				Map<String,JsonNode> proMap = Maps.newHashMap();
				for (JsonNode product : productNode.get("data").get("result")) {
					String key = product.get("csku").asText();
					proMap.put(key, product);
				}
				for (TaoBaoOrderGoods taoBaoGood : taoBaoGoods) {
					JsonNode product = proMap.get(taoBaoGood.getSku());
					if(product != null){
						sd = new SaleDetail();
						disprice = product.get("disPrice").asDouble();
						if (product.get("isSpecial") != null && !"null".equals(product.get("isSpecial").asText())) {
							disprice = product.get("specialSale").asDouble();
						}
						sd.setProductName(product.get("ctitle").textValue());
						sd.setPurchasePrice(disprice);
						sd.setSku(product.get("csku").textValue());
						sd.setWarehouseId(product.get("warehouseId").asInt());
						sd.setWarehouseName(product.get("warehouseName").textValue());
						sd.setProductImg(product.get("imageUrl").textValue());
						sd.setSalesOrderNo(sm.getSalesOrderNo());
						sd.setIsDeducted(1);
						sd.setDisPrice(disprice);
						sd.setFinalSellingPrice(null == taoBaoGood.getPrice() ? disprice : taoBaoGood.getPrice());
						sd.setQty(taoBaoGood.getAmount());
						sd.setInterBarCode(product.get("interBarCode").textValue());
						sd.setContractNo(JsonCaseUtil.jsonToString(product.get("contractNo")));
						sd.setClearancePrice(JsonCaseUtil.jsonToDouble(product.get("clearancePrice")));
						sds.add(sd);
					}
				}
			} catch (IOException e) {
				Logger.info(e.getMessage());
			}
		}
		return sds;
	}

	@Override
	public Map<String, Object> order4OpenApi(JsonNode main,boolean store) {

		Map<String, Object> result = Maps.newHashMap();
		List<String> skus = Lists.newArrayList();
		// 店铺名称
		String shopName = StringUtils.getStringValue(main, "shopName");
		// 分销账号
		String email = main.get("email").textValue();
		// 仓库ID
		Integer warehouseId = main.get("warehouseId").asInt();

		JsonNode skuObj = main.get("skuList");// [{sku:"",num:1},{}]'
		
		Map<String,Double> toTalCostMap = Maps.newHashMap(); 
		Map<String, Integer> skuNumMap = Maps.newHashMap();
		if (skuObj.isArray()) {
			JsonNode jsonNode;
			String sku;
			for (Iterator<JsonNode> it = skuObj.iterator();it.hasNext();) {
				jsonNode = (JsonNode) it.next();
				sku = jsonNode.get("sku").asText();
				skus.add(sku);
				skuNumMap.put(sku, jsonNode.get("num").asInt());
			}
		} else {
			String sku = skuObj.get("sku").asText();
			skus.add(sku);
			skuNumMap.put(sku, skuObj.get("num").asInt());
		}
		
		JsonNode memberNode = null;
		try {
			// 获取用户详情
			memberNode = httpService.getMemberInfo(email);
		} catch (Exception e) {
			Logger.error("getMemberInfo:" + e);
			return resultMsg(105, "未查询到用户信息", result,null);
		}
		Integer distributionMode = memberNode.get("distributionMode").asInt();
		
		JsonNode productStrNode = null;
		try {
			// 获取商品
			productStrNode = httpService.getProducts(email, skus, warehouseId, distributionMode);
		} catch (Exception e) {
			Logger.error("getProducts:" + e);
			return resultMsg(104, "未查询到商品信息", result,null);
		}
		// 给获取运费detail 赋值
		for(JsonNode skudetail:skuObj){
			((ObjectNode)skudetail).put("costPrice", toTalCostMap.get(skudetail.get("sku").asText()));
		}
		
		// 组织好客户订单信息
		JsonNode shopNode = null;
		try {
			// 获取店铺
			if(main.has("shopId")) {
				shopNode = httpService.getShopInfo(main.get("shopId").asInt());
			} else {
				shopNode = httpService.getShopInfo(email, shopName, 12);				
			}
		} catch (Exception e) {
			Logger.error("getShopInfo:" + e);
			return resultMsg(102, "未查询到店铺信息", result,null);
		}
		// 店铺扣点
		Double shopDeductionPoints = null;
		if (shopNode != null && shopNode.get("deductionPoints") != null
				&& email.equals(shopNode.get("email").asText())) {// 要是本人的店铺
			shopDeductionPoints = shopNode.get("deductionPoints").asDouble();
		}
		
		JsonNode custStrNode = null;
		try {
			// 获取客服账号
			custStrNode = httpService.custaccount(email);
			if(StringUtils.isBlankOrNull(JsonCaseUtil.jsonToString(custStrNode.get("account")))) {
			    return resultMsg(103, "未关联客服账号", result,null);
			}
		} catch (Exception e) {
			Logger.error("custaccount:" + e);
			return resultMsg(103, "未关联客服账号", result,null);
		}
		SaleBase sb = null;
		SaleMain sm = null;
		List<SaleDetail> sdList = null;
		JsonNode checkNode = null;
		try {
			// 插入订单Main
			sm = new SaleMain();
			// 来源
			sm.setSource("BBC");
			String orderNo = "";
			if(!store) {
				orderNo = IDUtils.getSalesCode(sequenceService.selectNextValue("SALE_NO"));
			} else {
				orderNo = generateMdOrderNo(shopNode);
			}
			if(StringUtils.isBlankOrNull(orderNo)) {
				return resultMsg(110, "店铺编号为空。", result,null);
			}
			sm.setSalesOrderNo(orderNo);
			sm.setOrderingDate(new Date());
			sm.setStatus(SaleOrderStatus.WAITING_PAY);
			sm.setEmail(email);
			sm.setIsAdjusted(0);
			/**************** 目前因为不允许跨仓下单，所以在主表上维护仓库信息 *******************/
			sm.setTradeNo(JsonCaseUtil.jsonToString(main.get("tradeNo")));// 交易号
			sm.setWarehouseId(JsonCaseUtil.jsonToInteger(main.get("warehouseId")));
			sm.setWarehouseName(JsonCaseUtil.jsonToString(main.get("warehouseName")));
			Integer distributorType = memberNode.get("comsumerType").asInt();
			String nickName = (memberNode.get("nickName") == null || 
					memberNode.get("nickName").asText().equals("null")) ? null : memberNode.get("nickName").asText();
			sm.setNickName(nickName);
			sm.setDistributorType(distributorType);
			sm.setDisMode(distributionMode);
			//add by zbc 设置用户归属  
			sm.setAttributionType(JsonCaseUtil.jsonToInteger(memberNode.get("attributionType")));
			//2017-5-27 用户是否包邮
			sm.setIsPackageMail(JsonCaseUtil.JsonToShort(memberNode.get("isPackageMail")));
			// 插入订单base
			sb = saleService.parseSaleBase4OpenApi(sm, main, shopNode, custStrNode, skuObj);
			if(null == sb) {
				Logger.error("获取运费失败");
				return resultMsg(109, "获取运费失败", result,null);
			}
			// 设置店铺扣点
//			sb.setShopDeductionPoints(shopDeductionPoints);
			//update by longhs 2017-4-13
			sm.setShopDeductionPoints(shopDeductionPoints == null ? 0 : shopDeductionPoints);
			sm.setThirdPartLogisticsTypeCode(sb.getThirdPartLogisticsTypeCode());
			double sdpAmount = 0d;
			if (shopDeductionPoints != null && sb.getOrderActualAmount() != null) {
				sdpAmount = PriceFormatUtil
						.toFix2(new BigDecimal(sb.getOrderActualAmount()).multiply(new BigDecimal(shopDeductionPoints)));
			}
			sm.setSdpAmount(sdpAmount);

			// 插入订单详情
			sdList = parseSaleDetail(sm, productStrNode, skuNumMap, memberNode,null);
			// 微仓锁库
			checkNode = saleOut(sm,sdList,null,false);
			switch (checkNode.get("type").asText()) {
			case "4":
				return resultMsg(107, "锁库异常", result,null);
			case "3":
				return resultMsg(107, "云仓微仓库存不足", result,null);
			default:
				break;
			}
			saleMainMapper.insertSelective(sm);
			BigDecimal actulAmount = new BigDecimal(0);
			for(SaleDetail d:sdList){
				d.setSalesOrderId(sm.getId());
				actulAmount = actulAmount.add(new BigDecimal(d.getQty() * d.getPurchasePrice()));
				saleDetailMapper.insertSelective(d);
			}
			//门店实付款为商品金额+运费
			if(store && null != sb.getBbcPostage()) {
			    actulAmount = actulAmount.add(new BigDecimal(sb.getBbcPostage()));
			}
			if(sb.getOrderActualAmount() == null || sb.getOrderActualAmount() == 0) {
				sb.setOrderActualAmount(actulAmount.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			sb.setSalesOrderId(sm.getId());
			saleBaseMapper.insertSelective(sb);
			// 计算税金
			if(sm!=null && sm.getId()!=null && sm.getSalesOrderNo()!=null){
				ebus.post(sm);
			}
		} catch (Exception e) {
			deleteSaleOrder(sm);
			Logger.error("生成订单失败:" + e);
			return resultMsg(106, "生成订单失败", result,null);
		}
		try {
			parseCheckNode(email, sm, sb, checkNode, sdList);
			// 计算操作费
			asyncCalculateOptFee(sm, sb, sdList);
//			saleService.syncLogs(sm, Constant.CREATE);
			return resultMsg(108, sm.getSalesOrderNo(), result,sm.getStatus());
		} catch (Exception e) {
			Logger.error("库存检查失败:" + e);
			return resultMsg(107, "库存检查失败", result,null);
		}
	}

	private String generateMdOrderNo(JsonNode shopStrNode) {
		String shopNo = StringUtils.getStringValue(shopStrNode, "shopNo");
		if(StringUtils.isBlankOrNull(shopNo)) {
			return null;
		}
		return shopNo + DateUtils.date2string(new Date(), DateUtils.FORMAT_DATE_DB)
				+ IDUtils.paddingLeft(sequenceService.selectNextValue("STORE_ORDER_NO"), 5);
	}

	@Override
	public Map<String, Object> remicsto(String str) {
		Map<String,Object> res = Maps.newHashMap();
		boolean suc = false;
		String msg = null;
		try {
			JsonNode json = Json.parse(str);
			SaleMain sm = saleMainMapper.selectByPrimaryKey(json.get("sid").asInt());
			if(sm==null){
				res.put("suc", false);
				res.put("msg", "该订单不存在");
				return res;
			}
			
			httpService.restoreMicro(sm.getSalesOrderNo());
			sm.setStatus(SaleOrderStatus.CLOSED_BY_CUSTOMER);
			suc = saleMainService.updateByPrimaryKeySelective(sm);
			if(suc){
				//如果要将状态变更为“取消”，那么返回该订单的发货详情，库存还原
				//添加取消订单的操作记录
				Logger.info("销售订单状态改为取消添加操作记录结果："+(saveRecord(sm.getId(), 4, 1, "取消订单", json.get("op").asText())?"添加成功":"添加失败"));
				Logger.info("更改客户订单id为[{}]的状态标识为：[{}],执行结果为：{}",sm.getId(),sm.getStatus(),res);
				saleMainService.updateCouponsState(sm);
			}
		} catch (Exception e) {
			suc = false;
			msg = "客服关闭缺货采购单异常";
			Logger.info("客服关闭缺货采购单异常",e);
		}
		res.put("suc", suc);
		res.put("msg", msg);
		return res;
	}

	@Override
	public String closeSalesFromB2C(String param) {
		//add by zbc  追加同步锁，防止重复调用
		synchronized (lock) {
			ObjectNode result = Json.newObject();
			try {
				JsonNode node = Json.parse(param);
				SaleMain main = saleMainMapper.selectByOrderNo(node.get("saleOrderNo").asText());
				boolean flag = main != null && !main.getIsClose();
				if (!flag) {
					return result.put("suc", false).put("msg", "订单不存在或订单已经关闭过").toString();
				}
				if (main.getSalesOrderNo().startsWith("MS-")) {// 表示是来自于M站的订单,就需要传递到M站去处理库存和金额
					syncMisteOrder(result, main);
					return result.toString();
				}
				Integer distributeType = main.getDistributorType();
				SaleBase base = saleBaseMapper.selectByOrderId(main.getId());
				BigDecimal traAm = new BigDecimal(getValue(base.getBbcPostage()))
						.subtract(new BigDecimal(getValue(base.getCouponsAmount())));
				String pNo = main.getPurchaseOrderNo();
				//合并发货单要退钱 和 还原云仓
				if(main.getIsCombine()&& pNo != null){
					traAm = saleService.getAmount(pNo, base);
				}
				//还原云仓标识
				boolean restoCloud = (distributeType == 3 ||main.getIsCombine()||main.isVirtualHouseOrder())&& pNo != null;
				traAm = distributeType == 3 || main.getCreatedFrom() == 50 ? null
						: (traAm.compareTo(BigDecimal.ZERO) > 0 ? PriceFormatUtil.setScale2(traAm) : null);
				result = backMoneyAndStock(traAm, main, restoCloud, 20, "system");
				// 营销单要还原云仓
				if (main.getCreatedFrom() == 50) {
					MarketingOrder mo = marketingOrderMapper.getBySno(main.getSalesOrderNo());
					if (mo != null) {
						try {
							Logger.info("营销单云仓还原-------->[{}]", httpService.restoreCloud(mo.getMarketingOrderNo()));
						} catch (Exception e) {
							Logger.info("营销单云仓还原异常", e);
						}
					}
				}
				return result.toString();
			} catch (Exception e) {
				Logger.info("erp关闭订单异常{}",e);
				return result.put("suc", false).put("msg", "erp关闭订单异常").toString();
			} 
		}
	}
	
	@SuppressWarnings("deprecation")
	private void syncMisteOrder(ObjectNode result, SaleMain main) {
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
		if (StringUtils.isNotBlankOrNull(res) && Json.parse(res).get("result").asBoolean()) {
			SaleMain sm = new SaleMain();
			sm.setStatus(20);
			sm.setId(main.getId());
			sm.setIsClose(true);
			saleMainService.updateByPrimaryKeySelective(sm);
			result.put("suc", true);
			result.put("data", Json.toJson(main));
		} else {
			result.put("suc", false);
			result.put("msg", "订单" + main.getSalesOrderNo() + "关闭失败");
		}
	}
	
	@Override
	public Map<String, Object> orderCreatedFromPurchaseOrder(JsonNode main) {

		Map<String, Object> result = Maps.newHashMap();
		List<String> skus = Lists.newArrayList();
		String email = main.get("email").textValue();// 分销账号
		Integer warehouseId = main.get("warehouseId").asInt();// 仓库ID
		JsonNode skuObj = main.get("skuList");
		
		Map<String,Double> finalPriceMap = Maps.newHashMap();// final price 
		Map<String, Integer> skuNumMap = Maps.newHashMap();
		if (skuObj.isArray()) {
			JsonNode jsonNode = null;
			String sku;
			for (Iterator<JsonNode> it = skuObj.iterator();it.hasNext();) {
				jsonNode = (JsonNode) it.next();
				sku = jsonNode.get("sku").asText();
				skus.add(sku);
				// 这里要判断sku重复的情况
				if(skuNumMap.get(sku)!=null){
					skuNumMap.put(sku, (jsonNode.get("num").asInt() + skuNumMap.get(sku)));
				}else{
					skuNumMap.put(sku, jsonNode.get("num").asInt());
				}
				finalPriceMap.put(sku, jsonNode.get("finalSellingPrice").asDouble());
			}
		} else {
			String sku = skuObj.get("sku").asText();
			skus.add(sku);
			// 这里要判断sku重复的情况
			if(skuNumMap.get(sku)!=null){
				skuNumMap.put(sku, (skuObj.get("num").asInt() + skuNumMap.get(sku)));
			}else{
				skuNumMap.put(sku, skuObj.get("num").asInt());
			}
			finalPriceMap.put(sku, skuObj.get("finalSellingPrice").asDouble());
		}
		
		JsonNode memberNode = null;
		try {
			// 获取用户详情
			memberNode = httpService.getMemberInfo(email);
			if (memberNode == null || !memberNode.has("comsumerType")) {
				return resultMsg(105, "未查询到用户信息", result,null);
			}
		} catch (Exception e) {
			Logger.error("getMemberInfo:" + e);
			return resultMsg(105, "未查询到用户信息", result,null);
		}
		Integer distributionMode = memberNode.get("distributionMode").asInt();
		String nickName = (memberNode.get("nickName") == null || 
				memberNode.get("nickName").asText().equals("null")) ? null : memberNode.get("nickName").asText();
		
		JsonNode productStrNode = null;
		try {
			// 获取商品
			productStrNode = httpService.getProducts(email,skus, warehouseId,distributionMode);
			if (productStrNode == null || productStrNode.get("data").get("result").size() <= 0) {
				return resultMsg(104, "未查询到商品信息", result,null);
			}
			JsonNode pros = productStrNode.get("data").get("result");
			boolean enough = true;
			String msg = "";
			for(JsonNode pro:pros){
				Integer stock = pro.get("stock").asInt() + (pro.get("microStock")!=null?pro.get("microStock").asInt():0);
				if(skuNumMap.get(pro.get("csku").asText()) > stock){
					enough = false;
					msg += "["+pro.get("csku").asText()+"],";
				}
			}
			if(!enough){
				return resultMsg(110, msg+"库存不足无法下单", result,null);
			}
		} catch (Exception e) {
			Logger.error("getProducts:{}",e);
			return resultMsg(104, "未查询到商品信息", result,null);
		}
		
		JsonNode custStrNode = null;
		try {
			// 获取客服账号
			custStrNode = httpService.custaccount(email);
			if (null == custStrNode || StringUtils.isBlankOrNull(custStrNode.get("account").asText())) {
				return resultMsg(103, "未关联客服账号", result,null);
			}
		} catch (Exception e) {
			Logger.error("custaccount:" + e);
			return resultMsg(103, "未关联客服账号", result,null);
		}
		
		SaleBase sb = null;
		SaleMain sm = null;
		List<SaleDetail> sdList = null;
		JsonNode checkNode = null;
		try {
			// 插入订单Main
			sm = saleService.parseSaleMain(main);
			if(null == sm) {
				Logger.error("解析Main参数失败。");
				return resultMsg(109, "获取运费失败", result,null);
			}
			sm.setStatus(SaleOrderStatus.WAITING_AUDIT_BY_FINANCE);//直接财务审核
			Integer distributorType = memberNode.get("comsumerType").asInt();
			sm.setDistributorType(distributorType);
			sm.setSource("BBC");
			sm.setDisMode(distributionMode);
			sm.setNickName(nickName);
			//add by zbc 设置用户归属  
			sm.setAttributionType(JsonCaseUtil.jsonToInteger(memberNode.get("attributionType")));
			//2017-5-27 用户是否包邮
			sm.setIsPackageMail(JsonCaseUtil.JsonToShort(memberNode.get("isPackageMail")));
			
			// 插入订单base
			sdList = parseSaleDetail(sm, productStrNode, skuNumMap, memberNode,finalPriceMap);
			//指定商品
			checkNode = saleOut(sm,sdList,JsonCaseUtil.jsonToString(main.get("purchaseOrderNo")),false);
			switch (checkNode.get("type").asText()) {
			case "4":
				return resultMsg(107, "锁库异常", result,null);
			case "3":
				return resultMsg(107, "云仓微仓库存不足", result,null);
			default:
				break;
			}
			sb = saleService.parseSaleBase(sm, main, null, custStrNode, skuObj);
			sm.setThirdPartLogisticsTypeCode(sb.getThirdPartLogisticsTypeCode());
			// add by zbc 
			sm.setIsPushed(1);
			saleMainMapper.insertSelective(sm);
			sb.setSalesOrderId(sm.getId());
			saleBaseMapper.insertSelective(sb);
			// 插入订单详情
			for(SaleDetail d:sdList){
				d.setSalesOrderId(sm.getId());
				saleDetailMapper.insertSelective(d);
			}
		} catch (Exception e) {
			deleteSaleOrder(sm);
			Logger.info("生成订单异常:{}",e);
			return resultMsg(106, "生成订单失败", result,null);
		}
		try {
			// 处理检查结果
			parseCheckNode2(email, sm, sb, checkNode, sdList);
//			// 计算税金
//			if (sm != null) {
//				Logger.info("即将计算税金");
//				ebus.post(sm);
//			}
			//同步计算操作费
			saleService.calculateOptFee(sm, sb, sdList);
			//同步计算费用信息
			managerOrderService.match(sm.getId());
			// add by zbc 
			sm.setIsPushed(0);
			saleMainMapper.updateByPrimaryKeySelective(sm);
			result = resultMsg(108, "生成发货单成功！", result, sm.getStatus());
			return result;
		} catch (Exception e) {
			Logger.error("库存检查失败:" + e);
			return resultMsg(107, "库存检查失败", result,null);
		}
	}
	
	private void parseCheckNode2(String email, SaleMain sm, SaleBase sb, JsonNode checkNode,
			List<SaleDetail> sd) throws JsonProcessingException, IOException {
		//  解析锁库参数
		String type = checkNode.get("type").asText();
		JsonNode successOrLocks = null;
		// 微仓充足
		if("1".equals(type)){
			successOrLocks = checkNode.get("successOrLocks");
		}
		List<Map<String, Object>> giftList = Lists.newArrayList();
		if(successOrLocks != null){
			//标记赠品
			Map<String, Object> gift;
			for (Iterator<JsonNode> node = successOrLocks.iterator();node.hasNext();) {
				JsonNode jsonNode = (JsonNode) node.next();
				if("1".equals(jsonNode.get("isGift").asText())) {
					gift = Maps.newHashMap();
					gift.put("salesOrderNo", sm.getSalesOrderNo());
					gift.put("isgift", true);
					gift.put("giftNum", jsonNode.get("qty").asInt());
					gift.put("sku", jsonNode.get("sku").asInt());
					giftList.add(gift);
				}
				//插入历史数据
				saveHistory(sm,jsonNode);
			}
		}
	}

	@Override
	public Map<String, Object> cOrder(JsonNode main) {
		Map<String, Object> result = Maps.newHashMap();
		// add by zbc 校验发票信息
		JsonResult<SaleInvoice> invoiceResult = invoiceService.checkVaildInvoice(main);
		if(!invoiceResult.getResult()){
			return resultMsg(110, invoiceResult.getMsg(), result,null);
		}
		// 分销账号
		String email = main.get("email").textValue();
		// 仓库ID
		Integer warehouseId = main.get("warehouseId").asInt();
		JsonNode skuObj = main.get("skuList");
		// 以 sku_warehouseId_isgift_expirationDate为key
		Map<String,JsonNode> detailNodeMap = Maps.newHashMap();
		Map<String,Integer> skuTotalMap =  Maps.newHashMap();
		Set<String> skuSet = Sets.newHashSet();
		if (skuObj.isArray()) {
			for (Iterator<JsonNode> it = skuObj.iterator();it.hasNext();) {
				JsonNode nextNode = it.next();
				skuSet.add(nextNode.get("sku").asText());
				exeProMap(detailNodeMap, skuTotalMap, nextNode);
			}
		} else {
			skuSet.add(skuObj.get("sku").asText());
			exeProMap(detailNodeMap, skuTotalMap, skuObj);
		}
		
		JsonNode memberNode = null;
		try {
			// 获取用户详情
			memberNode = httpService.getMemberInfo(email);
			if (memberNode == null || !memberNode.has("comsumerType")) {
				return resultMsg(105, "未查询到用户信息", result,null);
			}
		} catch (Exception e) {
			Logger.error("getMemberInfo:" + e);
			return resultMsg(105, "未查询到用户信息", result,null);
		}
		
		JsonNode productStrNode = null;
		Integer distributionMode = memberNode.get("distributionMode").asInt();
		String nickName = (memberNode.get("nickName") == null || 
				memberNode.get("nickName").asText().equals("null")) ? null : memberNode.get("nickName").asText();
		try {
			// 获取商品
			productStrNode = httpService.getProducts(email,Lists.newArrayList(skuSet), warehouseId,distributionMode);
		} catch (Exception e) {
			Logger.error("getProducts:" + e);
			return resultMsg(104, "未查询到商品信息", result,null);
		}
		if (productStrNode == null || productStrNode.get("data").get("result").size() <= 0) {
			return resultMsg(104, "未查询到商品信息", result,null);
		}
		
		// 库存校验
		String msg = "";
		for(JsonNode pro:productStrNode.get("data").get("result")){
			if(skuTotalMap.get(pro.get("csku").asText()) > pro.get("stock").asInt()){
				msg += "["+pro.get("csku").asText()+"],";
			}
		}
		if(StringUtils.isNotBlankOrNull(msg)){// 有提示哪个sku不足，说明库存不足
			return resultMsg(110, msg+"云仓库存不足", result,null);
		}

		JsonNode custStrNode = null;
		try {
			// 获取客服账号
			custStrNode = httpService.custaccount(email);
		} catch (Exception e) {
			Logger.error("custaccount:" + e);
			return resultMsg(103, "未关联客服账号", result,null);
		}
		if (null == custStrNode || StringUtils.isBlankOrNull(custStrNode.get("account").asText())) {
			return resultMsg(103, "未关联客服账号", result,null);
		}
		
		SaleBase sb = null;
		SaleMain sm = null;
		List<SaleDetail> saleDetails = null;
		try {
			// 插入订单Main
			sm = saleService.parseSaleMain(main);
			if(null == sm) {
				Logger.error("解析Main参数失败。");
				return resultMsg(109, "获取运费失败", result,null);
			}
			Integer distributorType = memberNode.get("comsumerType").asInt();
			sm.setDistributorType(distributorType);
			sm.setSource("BBC");
			sm.setDisMode(distributionMode);
			sm.setNickName(nickName);
			sm.setStatus(SaleOrderStatus.WAITING_PAY);
			//add by zbc 设置用户归属  
			sm.setAttributionType(JsonCaseUtil.jsonToInteger(memberNode.get("attributionType")));
			//2017-5-27 用户是否包邮
			sm.setIsPackageMail(JsonCaseUtil.JsonToShort(memberNode.get("isPackageMail")));
			// 插入订单base
			sb = saleService.parseSaleBase(sm, main, null, custStrNode, skuObj);
			if(null == sb) {
				Logger.error("获取运费失败");
				return resultMsg(109, "获取运费失败", result,null);
			}
			// 生产订单前查询库存是否充足 再来决定是否下单
			// 构造详情
			saleDetails = pareCloudSaleDetail(sm, productStrNode,memberNode,detailNodeMap);
			JsonNode checkNode = saleOut(sm,saleDetails,null,true);
			switch (checkNode.get("type").asText()) {
			case "4":
				return resultMsg(107, "锁库异常", result,null);
			case "3":
				return resultMsg(107, "云仓微仓库存不足", result,null);
			default:
				break;
			}
			// 不做  微仓锁库
			saleMainMapper.insertSelective(sm);
			// 插入详情
			for(SaleDetail de:saleDetails){
				de.setSalesOrderId(sm.getId());
				saleDetailMapper.insertSelective(de);
			}
			sb.setSalesOrderId(sm.getId());
			saleBaseMapper.insertSelective(sb);
			//add by zbc 保存发票信息
			invoiceService.save(sm,sb.getCreateUser(),invoiceResult.getData());
		} catch (Exception e) {
			// add by zbc  异常处理 删除 main 表信息
			deleteSaleOrder(sm);
			Logger.info("生产发货单异常:{}",e);
			return resultMsg(106, "生成订单失败", result,null);
		}
		try {
			// 生成缺货采购单
			parsePurchase(sm, sb,saleDetails);
			// 是否自动支付
			if (main.has("isPay") && main.get("isPay").asBoolean()) {// 表示是后台选择用余额支付的操作
				Logger.info("后台自动扣款：" + sm.getSalesOrderNo());
				AutoPayEvent pay = new AutoPayEvent();
				pay.setMain(sm);
				pay.setBase(sb);
				pay.setDetails(saleDetails);
				pay.setFreight(sb.getBbcPostage());
				pay.setPurchaseNo(sm.getPurchaseOrderNo());
				ebus.post(pay);
			}
			
			// 计算操作费
			asyncCalculateOptFee(sm,sb,saleDetails);
			return resultMsg(108, sm.getSalesOrderNo(), result,sm.getStatus());
		} catch (Exception e) {
			Logger.error("生成缺货采购单异常" + e);
			return resultMsg(107, "生成缺货采购单异常", result,null);
		}
	}

	@Override
	public void deleteSaleOrder(SaleMain sm) {
		if(sm != null && sm.getId() != null){
			saleMainMapper.deleteByPrimaryKey(sm.getId());
			saleDetailMapper.deleteByMainOrderId(sm.getId());
		}
	}

	private List<SaleDetail> pareCloudSaleDetail(SaleMain sm, JsonNode productStrNode, JsonNode memberNode,
			Map<String, JsonNode> detailNodeMap) {
		List<SaleDetail> sds = Lists.newArrayList();
		// 保存销售发货单商品详细表
		SaleDetail sd = null;
		// 根据分销商模式 计算分销
		// 分销价
		Double purchase = 0.0;
		//仓库id
		Integer warehouseId = null;
		String sku = "";
		JsonNode products = productStrNode.get("data").get("result");
		JsonNode detailNode = null;
		JsonNode product = null;
		Boolean isGift = false;
		try {
			for(Iterator<Map.Entry<String, JsonNode>> sit = detailNodeMap.entrySet().iterator();sit.hasNext();){
				detailNode = sit.next().getValue();
				sku = detailNode.get("sku").asText();
				product = getProNode(sku,products);
				isGift = detailNode.get("isgift").asBoolean();
				if(product != null){
				    warehouseId = product.get("warehouseId").asInt();
					sd = new SaleDetail();
					sd.setProductName(product.get("ctitle").textValue());
					sd.setInterBarCode(product.has("interBarCode")?product.get("interBarCode").textValue():null);
					purchase = isGift?0.00:product.get("disPrice").asDouble();
					sd.setSku(sku);
					sd.setPurchasePrice(purchase);// 分销价
					sd.setDisPrice(purchase);
					sd.setQty(detailNode.get("num").asInt());
					sd.setWarehouseId(warehouseId);
					sd.setDisPrice(product.get("disPrice").asDouble());
					sd.setWarehouseName(product.get("warehouseName").textValue());
					sd.setProductImg(product.get("imageUrl").textValue());
					sd.setSalesOrderNo(sm.getSalesOrderNo());
					sd.setIsDeducted(0);
					sd.setGstRate(product.get("gstRate").asDouble());// 消费税税率
					sd.setVatRate(product.get("vatRate").asDouble());// 增值税税率
					sd.setImportTarRate(product.get("importTarRate").asDouble());// 关税税率
					sd.setPostalFeeRate(product.get("postalFeeRate").asDouble());// 行邮税税率
					sd.setLogisticFee(product.get("logisticFee").asDouble());// 头程运费
					sd.setCategoryId(JsonCaseUtil.jsonToInteger(product.get("categoryId")));
					sd.setCategoryName(JsonCaseUtil.jsonToString(product.get("cname")));
					sd.setIsgift(isGift);
					sd.setContractNo(JsonCaseUtil.jsonToString(product.get("contractNo")));
					sd.setClearancePrice(JsonCaseUtil.jsonToDouble(product.get("clearancePrice")));
					sd.setExpirationDate(DateUtils.string2date(
							detailNode.get("expirationDate").asText(),
							DateUtils.FORMAT_DATE_PAGE));
					// 最终售价上下浮不能超过分销价的 50%
					Double finalSellingPrice = 0.00;
					if(detailNode.has("finalSellingPrice")){
						finalSellingPrice = JsonCaseUtil.jsonToDouble(detailNode.get("finalSellingPrice"));
					}else{
						// 使用分销价当做最终价格
						finalSellingPrice = product.get("disPrice").asDouble();
					}
					// 设置其他平台最终售价
					if (sm.getDistributorType() != 3 && sm.getWarehouseId() != 2024) {
						sd.setFinalSellingPrice(finalSellingPrice > purchase * 1.5 ? purchase * 1.5
								: finalSellingPrice < purchase * 0.5
										? PriceFormatUtil.toFix2(new BigDecimal(purchase * 0.5)): finalSellingPrice);
					} else {
						sd.setFinalSellingPrice(finalSellingPrice);
					}
					sds.add(sd);
				}
			}
		} catch (ParseException e) {
			Logger.info("云仓发货-商品详情到期日期格式化失败");
			e.printStackTrace();
		}
		return sds;
	}

	private JsonNode getProNode(String sku, JsonNode products) {
		JsonNode proNode = null; 
		String csku = null;
		for (Iterator<JsonNode> it = products.iterator();it.hasNext();) {
			JsonNode jsonNode = (JsonNode) it.next();
			csku = jsonNode.get("csku").textValue();
			if(csku.equals(sku)){
				proNode = jsonNode;
			}
		}
		return proNode;
	}

	/**
	 * 云仓发货使用的
	 * 
	 * @param detailNodeMap
	 * @param skuTotalMap
	 * @param jsonNode
	 */
	private void exeProMap(Map<String, JsonNode> detailNodeMap,
			Map<String, Integer> skuTotalMap, JsonNode jsonNode) {
		String sku = jsonNode.get("sku").asText();
		String key = jsonNode.get("key").asText();
		// 这里要判断sku重复的情况
		Integer num = skuTotalMap.get(sku);
		if(num!=null){
			skuTotalMap.put(sku, (jsonNode.get("num").asInt() + num));
		}else{
			skuTotalMap.put(sku, jsonNode.get("num").asInt());
		}
		detailNodeMap.put(key, jsonNode);
	}

	private void parsePurchase(SaleMain sm, SaleBase sb, List<SaleDetail> sd) throws JsonProcessingException, IOException {
		List<Map<String, Object>> purchaseDetail = Lists.newArrayList();
		BigDecimal totalPrice = BigDecimal.ZERO;
		String salesOrderNo =  sm.getSalesOrderNo();
		BigDecimal purchasePrice,sumPrice;
		for(SaleDetail d:sd){
			Map<String, Object> detail = Maps.newHashMap();
			Integer qty = d.getQty();
			boolean isgift = d.getIsgift()!= null &&d.getIsgift();
			purchasePrice = isgift?BigDecimal.ZERO:new BigDecimal(d.getPurchasePrice());
			sumPrice = PriceFormatUtil.setScale2(purchasePrice.multiply(new BigDecimal(qty)));
			totalPrice = totalPrice.add(sumPrice);
			detail.put("itemId", "");
			detail.put("title", d.getProductName());
			detail.put("price", purchasePrice);
			detail.put("warehouseId", d.getWarehouseId());
			detail.put("warehouseName", d.getWarehouseName());
			detail.put("sumPrice", sumPrice);
			detail.put("marketPrice", d.getMarketPrice());
			detail.put("publicImg", d.getProductImg());
			detail.put("sku", d.getSku());
			detail.put("salesOrderNo", salesOrderNo);
			detail.put("qty",qty);
			detail.put("categoryId",d.getCategoryId());
			detail.put("categoryName", d.getCategoryName());
			detail.put("disStockId", d.getWarehouseId());// 仓库id
			detail.put("disPrice", d.getDisPrice()); // 分销价
			detail.put("realPrice", d.getDisPrice());
			detail.put("isgift", isgift);
			detail.put("interBarCode", d.getInterBarCode());// 国际条码
			detail.put("expirationDate", d.getExpirationDateStr());// 到期日期
			detail.put("contractNo", d.getContractNo());
			detail.put("clearancePrice", d.getClearancePrice());
			purchaseDetail.add(detail);
		}
		Map<String, Object> pMap = Maps.newHashMap();
		pMap.put("sid", sm.getId());// 缺货采购，绑定发货单
		pMap.put("distributorType", sm.getDistributorType());
		pMap.put("email", sm.getEmail());
		pMap.put("orderDetail", purchaseDetail);
		pMap.put("totalPrice", PriceFormatUtil.setScale2(totalPrice));
		pMap.put("bbcPostage", sb.getBbcPostage());
		pMap.put("logisticsMode", sb.getLogisticsMode());
		JsonNode purchaseNode = httpService.postPurchase(pMap);
		String purNo = purchaseNode.get("errorInfo").asText();
		sm.setPurchaseOrderNo(purNo);
		saleMainService.updateByPrimaryKeySelective(sm);
	}

	/**	  电商
		  内部  是： 财务审核 
			    否： 
				    亏本 
					    是 :财务审核，
						否 :直接过
		非电商：保持原有逻辑	*/
	@Override
	public JsonResult<?> finishSaleOrder(String string) {
		try {
			JsonNode json = Json.parse(string);
			Integer id = JsonCaseUtil.jsonToInteger(json.get("id"));
			SaleMain sm  = saleMainMapper.selectByPrimaryKey(id);
			if(sm == null ){
				return JsonResult.newIns().result(true).msg("发货单不存在");
			}
			sm.setPurchasePaymentType(JsonCaseUtil.jsonToString(json.get("payType")));
			sm.setPurchasePayDate(JsonCaseUtil.jsonStrToDate(json.get("payDate"), DateUtils.FORMAT_FULL_DATETIME));
			sm.setPurchasePaymentNo(JsonCaseUtil.jsonToString(json.get("payNo")));
			sm.setOrderActualPayment(JsonCaseUtil.jsonToDouble(json.get("actualPay")));
			//用于校验采购单是否完成
			boolean isComplete = JsonCaseUtil.jsonToBoolean(json.get("isComplete"));
			if(sm.getDisMode() != null && sm.getDisMode() == 1){
				//缺货采购单未完成需要审核，已完成判断是否亏本
				if(isComplete){
					// 提前更新支付时间，防止计算合同价报错
					saleMainMapper.updateByPrimaryKeySelective(sm);
					//同步更新利润信息
					managerOrderService.match(id);
					//内部分销商 亏本订单需要财务审核
					if(sm.getDistributorType() == 3 ||saleService.noProfit(id)){
						sm.setStatus(SaleOrderStatus.WAITING_AUDIT_BY_FINANCE);
					}else{
						sm.setStatus(SaleOrderStatus.WAITING_DELIVERY_SIX);
					}
				}else{
					sm.setStatus(SaleOrderStatus.WAITING_AUDIT_BY_CS);
				}
			}else{
				//非电商 默认待客服审核
				sm.setStatus(SaleOrderStatus.WAITING_AUDIT_BY_CS);
			}
			//待发货订单设置生成交易号
			if(sm.getStatus() == SaleOrderStatus.WAITING_DELIVERY_SIX){
				sm.setPaymentNo(IDUtils.getPayNo());
				sm.setPayDate(new Date());
				sm.setPaymentType("system");
				sm.setCurrency("CNY");
				sm.setIsPushed(1);
				// add by zbc 保存操作日志
				saveRecord(sm.getId(),2,1,"系统自动通过","system");
			}
			saleMainMapper.updateByPrimaryKeySelective(sm);
			//请求快递鸟 生成电子面单
			if(sm.getStatus() == SaleOrderStatus.WAITING_DELIVERY_SIX){
				kdnService.requestOrderOnline(sm);
			}
			// 更新优惠码信息
			saleMainService.updateCouponsState(sm);
			Logger.info("【"+sm.getSalesOrderNo()+"】发货单完成,状态更新为:{}",Constant.SALES_ORDER_STATE_MANAGER.get(sm.getStatus()));
			return JsonResult.newIns().result(true).msg("完成发货单成功");
		} catch (Exception e) {
			Logger.info("完成发货单异常:{}",e);
			return JsonResult.newIns().result(true).msg("完成发货单异常");
		}
	}

	@Override
	public boolean saveRecord(Integer id,Integer operateType,Integer result,String comment,String operator) {
		OperateRecord record = new OperateRecord();
		record.setOrderId(id);
		record.setOperateType(operateType);
		record.setResult(result);
		record.setComment(comment);
		record.setEmail(operator);
		return operateRecordService.saveOperateRecord(record);
	}
	
}
