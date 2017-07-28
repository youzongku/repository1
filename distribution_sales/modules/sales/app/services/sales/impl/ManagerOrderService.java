package services.sales.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import dto.JsonResult;
import dto.sales.CommonExportDto;
import dto.sales.ProductSalesVolume;
import dto.sales.ProductSalesVolumeSearchDto;
import dto.sales.SaleLockDto;
import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleInvoice;
import entity.sales.SaleMain;
import events.sales.AutoPayEvent;
import mapper.sales.SaleBaseMapper;
import mapper.sales.SaleDetailMapper;
import mapper.sales.SaleMainMapper;
import play.Logger;
import play.libs.Json;
import services.sales.IHttpService;
import services.sales.IManagerOrderService;
import services.sales.ISaleInvoiceService;
import services.sales.ISaleLockService;
import services.sales.ISaleService;
import services.sales.ISalesContractService;
import util.sales.CommonExportUtils;
import util.sales.DateUtils;
import util.sales.JsonCaseUtil;
import util.sales.PriceFormatUtil;
import util.sales.SaleOrderStatus;
import util.sales.StringUtils;

/**
 * 后台下发货单的service接口，跟前台下单的接口分开
 * 
 * @author huangjc
 * @since 2017年3月7日
 */
public class ManagerOrderService implements IManagerOrderService {
	
	@Inject private IHttpService httpService;
	@Inject private ISaleService saleService;
	@Inject private SaleMainMapper saleMainMapper;
	@Inject private SaleBaseMapper saleBaseMapper;
	@Inject private SaleDetailMapper saleDetailMapper;
	@Inject private EventBus ebus;
	@Inject private ISalesContractService contractSerivce;
	@Inject private ISaleInvoiceService invoiceService;
	@Inject private ISaleLockService lockService;

	@Override
	public Map<String, Object> order(JsonNode main) {
		Map<String, Object> result = Maps.newHashMap();
		// add by zbc 校验发票信息
		JsonResult<SaleInvoice> invoiceResult = invoiceService.checkVaildInvoice(main);
		if(!invoiceResult.getResult()){
			return resultMsg(110, invoiceResult.getMsg(), result,null);
		}
		
		String email = main.get("email").textValue();// 分销账号
		Integer warehouseId = main.get("warehouseId").asInt();// 仓库ID
		JsonNode skuObj = main.get("skuList");
		
		Set<String> skuSet = Sets.newHashSet();// 防止sku重复
		Map<String, Double> finalPriceMap = Maps.newHashMap(); //final price
		Map<String, Integer> numMap = Maps.newHashMap();
		Map<String, Set<String>> skuWarehouseId2expirationDateSet = Maps.newHashMap();
		String sku, expirationDate, key, skuWarehouseIdKey;
		if (skuObj.isArray()) {
			JsonNode jsonNode = null;
			for (Iterator<JsonNode> it = skuObj.iterator();it.hasNext();) {
				jsonNode = (JsonNode) it.next();
				sku = jsonNode.get("sku").asText();
				expirationDate = JsonCaseUtil.jsonToString(jsonNode.get("expirationDate"));
				key = getKey(sku, warehouseId, expirationDate);
				skuWarehouseIdKey = getKey(sku, warehouseId, null);
				
				skuSet.add(sku);
				numMap.put(key, jsonNode.get("num").asInt());
				finalPriceMap.put(key, jsonNode.get("finalSellingPrice").asDouble());
				// 到期日期
				Set<String> set = skuWarehouseId2expirationDateSet.get(skuWarehouseIdKey);
				if(set==null){
					set = Sets.newHashSet();
					skuWarehouseId2expirationDateSet.put(skuWarehouseIdKey, set);
				}
				set.add(expirationDate);
			}
		} else {
			sku = skuObj.get("sku").asText();
			expirationDate = skuObj.get("expirationDate").asText();
			key = getKey(sku, warehouseId, expirationDate);
			skuWarehouseIdKey = getKey(sku, warehouseId, null);
			
			skuSet.add(sku);
			numMap.put(key, skuObj.get("num").asInt());
			finalPriceMap.put(key, skuObj.get("finalSellingPrice").asDouble());
			// 到期日期
			Set<String> set = skuWarehouseId2expirationDateSet.get(skuWarehouseIdKey);
			if(set==null){
				set = Sets.newHashSet();
				skuWarehouseId2expirationDateSet.put(skuWarehouseIdKey, set);
			}
			set.add(expirationDate);
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
			productStrNode = httpService.getProducts(email, Lists.newArrayList(skuSet), warehouseId, distributionMode);
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
		List<SaleDetail> saleDetails = null;
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
			//包邮信息 2017-5-27
			sm.setIsPackageMail(JsonCaseUtil.JsonToShort(memberNode.get("isPackageMail")));	
			// 插入订单base
			sb = saleService.parseSaleBase(sm, main, null, custStrNode, skuObj);
			if(null == sb) {
				Logger.error("获取运费失败");
				return resultMsg(109, "获取运费失败", result, null);
			}
			// 生产订单前查询库存是否充足 再来决定是否下单

			// 构造详情
			saleDetails = parseSaleDetail(sm, productStrNode, numMap, memberNode, finalPriceMap, skuWarehouseId2expirationDateSet);
			// 微仓锁库
			checkNode = saleOut(sm,saleDetails,null,false);
			switch (checkNode.get("type").asText()) {
			case "4":
				return resultMsg(107, "锁库异常", result,null);
			case "3":
				return resultMsg(107, "云仓微仓库存不足", result, null);
				default:
					break;
			}
			// 插入主表信息
			sm.setThirdPartLogisticsTypeCode(sb.getThirdPartLogisticsTypeCode());

			saleMainMapper.insertSelective(sm);
			// 插入详情
			for(SaleDetail de:saleDetails){
				de.setSalesOrderId(sm.getId());
				saleDetailMapper.insertSelective(de);
			}
			sb.setSalesOrderId(sm.getId());
			// 插入base
			saleBaseMapper.insertSelective(sb);
			// 更新优惠码信息
			saleService.syncCoupons(sm.getId());
			//add by zbc 保存发票信息
			invoiceService.save(sm,sb.getCreateUser(),invoiceResult.getData());
		} catch (Exception e) {
			lockService.deleteSaleOrder(sm);
			Logger.info("生成订单异常{}",e);
			return resultMsg(106, "生成订单失败", result,null);
		}
		try {
			// 计算操作费
			saleService.calculateOptFee(sm,sb,saleDetails);
			parseCheckNode(email, sm, sb, checkNode, saleDetails);
//			// 计算税金
//			if (sm != null) {
//				Logger.info("即将计算税金");
//				ebus.post(sm);
//			}
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
			return resultMsg(108, "生成发货单"+sm.getSalesOrderNo()+"成功！", result,sm.getStatus());
		} catch (Exception e) {
			Logger.error("库存检查失败:{}",e);
			return resultMsg(107, "库存检查失败", result,null);
		}
	}
	
	/**
	 * 保存历史
	 * @param sm
	 * @param json
	 */
	private void saveHistory(SaleMain sm, JsonNode json) {
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
	
	private Map<String, Object> getDetail(JsonNode node, String salesOrderNo, Map<String, SaleDetail> sdMap) {
		Map<String, Object> detail = Maps.newHashMap();
		SaleDetail sd = sdMap.get(getKey(node.get("sku").asText(),
				node.get("warehouseId").asInt()));
		Integer qty = node.get("qty").asInt();
		detail.put("itemId", "");
		detail.put("title", sd.getProductName());
		detail.put("interBarCode", sd.getInterBarCode());
		detail.put("price", sd.getPurchasePrice());
		detail.put("warehouseId", sd.getWarehouseId());
		detail.put("warehouseName", sd.getWarehouseName());
		detail.put("sumPrice", PriceFormatUtil.toFix2(new BigDecimal(sd.getPurchasePrice()*qty)));
		detail.put("marketPrice", sd.getMarketPrice());
		detail.put("publicImg", sd.getProductImg());
		detail.put("sku", node.get("sku").asText());
		detail.put("salesOrderNo", salesOrderNo);
		detail.put("qty",qty);
		detail.put("categoryId",sd.getCategoryId());
		detail.put("categoryName", sd.getCategoryName());
		detail.put("disStockId", sd.getWarehouseId());// 仓库id
		detail.put("disPrice", sd.getDisPrice()); // 分销价
//		detail.put("expirationDate", DateUtils.date2string(sd.getExpirationDate(), DateUtils.FORMAT_DATE_PAGE));
		detail.put("expirationDate", JsonCaseUtil.jsonToString(node.get("expirationDate")));
		detail.put("contractNo",sd.getContractNo());
		detail.put("clearancePrice",sd.getClearancePrice());
		return detail;
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
		/*Map<String, SaleDetail> saleDetailMap = Maps.uniqueIndex(sd, s -> {
					return getKey(s.getSku(), s.getWarehouseId(), DateUtils
							.date2string(s.getExpirationDate(),
									DateUtils.FORMAT_DATE_PAGE));
		});*/
		Map<String, SaleDetail> saleDetailMap = Maps.newHashMap();
		for(SaleDetail d:sd){
			saleDetailMap.put(getKey(d.getSku(), d.getWarehouseId()), d);
		}
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
			String key;
			for(JsonNode pur:purchases){
				key = getKey(pur.get("sku").asText(), pur.get("warehouseId")
						.asInt());
				purchaseDetail.add(getDetail(pur, sm.getSalesOrderNo(), saleDetailMap));
				totalPrice = totalPrice.add(new BigDecimal(pur.get("qty")
						.asInt()).multiply(new BigDecimal(saleDetailMap
						.get(key).getPurchasePrice())));
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
				lockService.finishSaleOrder(finishJson.toString());
				return;
			}
		}
		sm.setStatus(saleStatus);
		saleMainMapper.updateByPrimaryKeySelective(sm);
	}
	
	private List<SaleDetail> parseSaleDetail(SaleMain sm,
			JsonNode productStrNode, Map<String, Integer> numMap,
			JsonNode memberNode, Map<String, Double> finalPriceMap,
			Map<String, Set<String>> skuWarehouseId2expirationDateSet) {
		
		List<SaleDetail> sds = Lists.newArrayList();
		// 保存销售发货单商品详细表
		SaleDetail sd = null;
		//根据分销商模式 计算分销
		Map<String,JsonNode> skuWarehouseId2ProductNode = Maps.newHashMap();
		for (Iterator<JsonNode> it = productStrNode.get("data").get("result").iterator();it.hasNext();) {
			JsonNode productNode = it.next();
			skuWarehouseId2ProductNode.put(productNode.get("csku").textValue()+""+productNode.get("warehouseId").asText(), productNode);
		}
		
		for(Map.Entry<String,JsonNode> entry : skuWarehouseId2ProductNode.entrySet()){
			JsonNode productNode = entry.getValue();
			sd = new SaleDetail();
			sd.setProductName(productNode.get("ctitle").textValue());
			sd.setInterBarCode(productNode.has("interBarCode")?productNode.get("interBarCode").asText():null);
			sd.setSku(productNode.get("csku").textValue());
			sd.setPurchasePrice(productNode.get("disPrice").asDouble());// 分销价
			sd.setWarehouseId(productNode.get("warehouseId").asInt());
			sd.setWarehouseName(productNode.get("warehouseName").textValue());
			sd.setProductImg(productNode.get("imageUrl").textValue());
			sd.setSalesOrderNo(sm.getSalesOrderNo());
			sd.setIsDeducted(0);
			sd.setGstRate(productNode.get("gstRate").asDouble());// 消费税税率
			sd.setVatRate(productNode.get("vatRate").asDouble());// 增值税税率
			sd.setImportTarRate(productNode.get("importTarRate").asDouble());// 关税税率
			sd.setPostalFeeRate(productNode.get("postalFeeRate").asDouble());// 行邮税税率
			sd.setLogisticFee(productNode.get("logisticFee").asDouble());// 头程运费
			sd.setCategoryId(JsonCaseUtil.jsonToInteger(productNode.get("categoryId")));
			sd.setCategoryName(JsonCaseUtil.jsonToString(productNode.get("cname")));
			sd.setContractNo(JsonCaseUtil.jsonToString(productNode.get("contractNo")));
			sd.setClearancePrice(JsonCaseUtil.jsonToDouble(productNode.get("clearancePrice")));
			sds.add(sd);
		}
		
		// 处理到期日期
		List<SaleDetail> finalSds = Lists.newArrayList();
		try {
			for(SaleDetail aSaleDetail : sds){
				// 根据到期日期来拆分：一个sku可能会变成多条记录
				Set<String> set = skuWarehouseId2expirationDateSet.get(getKey(aSaleDetail.getSku(),aSaleDetail.getWarehouseId(),null));
				if(set!=null && set.size()>0){
					for(String expirationDate : set){
						sd = new SaleDetail();
						BeanUtils.copyProperties(aSaleDetail, sd);
						sd.setExpirationDate(DateUtils.string2date(expirationDate, DateUtils.FORMAT_DATE_PAGE));
						finalSds.add(sd);
					}
				}
			}
		} catch (ParseException e) {
			Logger.info("到期日期转换出错");
			e.printStackTrace();
		}
		
		String key;
		for(SaleDetail aSaleDetail : finalSds){
			key = getKey(aSaleDetail.getSku(),aSaleDetail.getWarehouseId(),aSaleDetail.getExpirationDateStr());
			aSaleDetail.setQty(numMap.get(key));
			// 最终售价上下浮不能超过分销价的 50%
			Double finalSellingPrice = finalPriceMap == null ? sd.getPurchasePrice() : finalPriceMap.get(key);
			// 设置其他平台最终售价
			if (sm.getDistributorType() != 3 && sm.getWarehouseId() != 2024) {
				aSaleDetail.setFinalSellingPrice(finalSellingPrice > sd.getPurchasePrice() * 1.5 ? sd.getPurchasePrice() * 1.5
						: finalSellingPrice < sd.getPurchasePrice() * 0.5
								? PriceFormatUtil.toFix2(new BigDecimal(sd.getPurchasePrice() * 0.5)): finalSellingPrice);
			} else {
				aSaleDetail.setFinalSellingPrice(finalSellingPrice);
			}
		}
		
		return finalSds;
	}
	
	private Map<String, Object> resultMsg(Integer code, String msg, Map<String, Object> result,Integer status) {
		result.put("code", code);
		result.put("msg", msg);
		result.put("status", status);
		return result;
	}

	private JsonNode saleOut(SaleMain main,List<SaleDetail> list,String marketNo,Boolean lockCloud) {
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
	
	/**
	 * 用参数构建一个key
	 * 
	 * @param sku
	 * @param warehouseId
	 * @param expirationDate
	 * @return sku[_warehouseId][_expirationDate]
	 */
	private String getKey(String sku, Integer warehouseId, String expirationDate){
		String key = sku;
		if(warehouseId!=null){
			key = key+"_"+warehouseId.toString();
		}
		if(StringUtils.isNotBlankOrNull(expirationDate)){
			key = key+"_"+expirationDate;
		}
		return key;
	}
	
	private String getKey(String sku, Integer warehouseId){
		String key = sku;
		if(warehouseId!=null){
			key = key+"_"+warehouseId.toString();
		}
		return key;
	}

	/**
	 * arrvicetotal
	 * bbcpostage
	 * optfee
	 * totalcost
	 * profit
	 * profitmargin
	 * contractCharge
	 *合同总费用 = 合同扣点(10万*0.12=12000) + 满返（10万*0.03=3000）=15000元
	 *订单总成本 = (修改前)运费 + 操作费 + 合同总费用 + 到仓价总计
	 *利润 = 订单毛收入（订单总额）- 订单总成本
	 *利润率 = 利润 ÷ 订单毛收入
	 *清货价总成本=清货价总计+实际运费+操作费+合同费用+店铺扣点
	 *清货价利润=毛收入-清货价总成本
     *清货价利润率=清货价利润/毛收入
	 */
	@Override
	public String match(Integer sid) {
		//计算合同费用
		SaleMain  main = (SaleMain)contractSerivce.caculate(sid).getData();
		SaleBase base = saleBaseMapper.selectByOrderId(sid);
		// 计算费用项
		//判断是否存在清货价
		boolean isExistClearPrice = saleDetailMapper.getHistoryByOrderId(sid).stream().
				filter(sd -> sd.getClearancePrice() != null&&sd.getClearancePrice()>0.0).findAny().isPresent();
		//change by zbc 订单合同费用 取订单预估费用 2017-06-06
		BigDecimal contractCharge = main.getEstimatedCatfeeInOrder() != null ? 
				new BigDecimal(main.getEstimatedCatfeeInOrder()): BigDecimal.ZERO;
		Map<String,Object> map = saleDetailMapper.getAmount(sid);
		if(null == map) {
		    return "";
		}
		BigDecimal bbcpostage = map.get("bbcpostage") != null?new BigDecimal(map.get("bbcpostage")+""):BigDecimal.ZERO;
		BigDecimal platformamount =  new BigDecimal(map.get("platformamount")+"");
		BigDecimal arrvicetotal = new BigDecimal(map.get("arrvicetotal")+"");
		BigDecimal optfee = map.get("optfee") != null?new BigDecimal(map.get("optfee")+""):BigDecimal.ZERO;
		BigDecimal clearanceamount = main.getWarehouseId() == 2024?
				BigDecimal.ZERO:map.get("clearanceamount") != null?
						new BigDecimal(map.get("clearanceamount") + ""):BigDecimal.ZERO;
		//清货价总计
		BigDecimal 	clearancepricetotal = new BigDecimal(map.get("clearancepricetotal") + "");
		BigDecimal sdpAmount  = main.getSdpAmount() != null?
				new BigDecimal(main.getSdpAmount()):BigDecimal.ZERO;
		//新增字段存放计算结果
		main = new SaleMain();
		if("3".equals(map.get("distributortype").toString())){
			platformamount = new BigDecimal(base.getOrderActualAmount());
		}else{
			platformamount = platformamount.add(bbcpostage);
		}
		if(isExistClearPrice){
			//清货价总成本
			BigDecimal  cleartotalcost = clearancepricetotal.add(
					map.get("originalfreight") != null? new BigDecimal(map.get("originalfreight") +""):BigDecimal.ZERO 
					).add(optfee).add(sdpAmount).add(contractCharge);;	
			//清货价利润值
			BigDecimal clearprofit = platformamount.subtract(cleartotalcost).setScale(2, BigDecimal.ROUND_HALF_UP);
			//清货价利润率
			BigDecimal clearprofitmargin = platformamount.compareTo(BigDecimal.ZERO) > 0?
					clearprofit.divide(platformamount,5):
					BigDecimal.ZERO;
			main.setClearTotalCost(cleartotalcost.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			main.setClearProfit(clearprofit.doubleValue());
			main.setClearProfitMargin(clearprofitmargin.doubleValue());
			main.setClearancePriceTotal(clearancepricetotal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		}
		//到仓价总成本 
		BigDecimal totalcost =  arrvicetotal.add(
		map.get("originalfreight") != null? new BigDecimal(map.get("originalfreight") +""):BigDecimal.ZERO 
		).add(optfee).add(sdpAmount).add(contractCharge);
		
		BigDecimal profit = platformamount.subtract(totalcost).setScale(2, BigDecimal.ROUND_HALF_UP);
		//到仓价利润率
		BigDecimal profitmargin = platformamount.compareTo(BigDecimal.ZERO) > 0?
				profit.divide(platformamount,5):
				BigDecimal.ZERO;
		main.setId(sid);
		main.setClearanceAmount(clearanceamount.doubleValue());
		main.setArrviceTotal(arrvicetotal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		main.setTotalCost(totalcost.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		main.setProfit(profit.doubleValue());
		main.setPlatformAmount(platformamount.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		main.setProfitMargin(profitmargin.doubleValue());
		if(contractCharge.compareTo(BigDecimal.ZERO)>0){
			main.setContractCharge(contractCharge.doubleValue());	
		}
		//防止更新到状态
		main.setStatus(null);
		saleMainMapper.updateByPrimaryKeySelective(main);
		return "success";
	}

	@Override
	public String caculateProfit(String orderNo) {
		try {
			SaleMain main = saleMainMapper.selectByOrderNo(orderNo);
			if(main == null){
				return "ORDER NOT EXIST";
			}
			match(main.getId());
			return "SUCCESS";
		} catch (Exception e) {
			Logger.info("发货单计算利润值异常"+e);
			return "ERROR";
		}
		
	}
	@SuppressWarnings("rawtypes")
	@Override
	public String commonExport(String reqStr) {
		Map<String, Object> result = Maps.newHashMap();
		JsonNode reqNode = Json.parse(reqStr);
		String functionId = reqNode.get("functionId").asText();
		CommonExportDto exportModel = saleMainMapper.getExprotDtoByFunctionId(functionId);
		if (exportModel == null) {
			result.put("result", "1");
			result.put("msg", "查询不到导出信息");
			return Json.toJson(result).toString();
		}
		try {
			String sql = " ";
			String function = exportModel.getFunctionId();
			sql = sql + function;
			String functionParam = exportModel.getFunctionParam();
			Map<Integer, String> paramKeyMap = Maps.newHashMap();
			JsonNode paramNode = Json.parse(functionParam);
			if (paramNode != null && paramNode.size() > 0) {
				for (JsonNode node : paramNode) {
					int index = node.get("index").asInt();
					String key = node.get("key").asText();
					paramKeyMap.put(index, key);
				}
			}
			sql = sql + " (";
			if (paramKeyMap.size() > 0) {
				for (int i = 0; i < paramKeyMap.size(); i++) {
					String key = paramKeyMap.get(i);
					if (reqNode.get(key) != null && !"null".equals(reqNode.get(key).asText())) {
						sql = sql + "'" + reqNode.get(key).asText() + "'" + ",";
					} else {
						sql = sql + "''" + ",";
					}
				}
				sql = sql.substring(0, sql.length() - 1);
			}
			sql = sql + ")";
			
			List<Map> sqlResult = saleMainMapper.getExportDataBySqlFunction(sql);
			String functionResult = exportModel.getFunctionResult();
			Map<Integer, String> sqlResultKeyMap = Maps.newHashMap();
			JsonNode resultNode = Json.parse(functionResult);
			if (resultNode != null && resultNode.size() > 0) {
				for (JsonNode node : resultNode) {
					int index = node.get("index").asInt();
					String key = node.get("key").asText();
					sqlResultKeyMap.put(index, key);
				}
			}
			String excelRows = exportModel.getExcelRows();
			Map<Integer, String> excelRowKeyMap = Maps.newHashMap();
			JsonNode excelRowsNode = Json.parse(excelRows);
			if (excelRowsNode != null && excelRowsNode.size() > 0) {
				for (JsonNode node : excelRowsNode) {
					int index = node.get("index").asInt();
					String rowName = node.get("rowName").asText();
					excelRowKeyMap.put(index, rowName);
				}
			}

			String excelWidth = exportModel.getExcelWidth();
			Map<Integer, Integer> excelWidthMap = Maps.newHashMap();
			JsonNode excelWidthNode = null;
			if (excelWidth != null) {
				excelWidthNode = Json.parse(excelWidth);
			}

			if (excelWidthNode != null && excelWidthNode.size() > 0) {
				for (JsonNode node : excelWidthNode) {
					int index = node.get("index").asInt();
					int width = node.get("width").asInt();
					excelWidthMap.put(index, width);
				}
			}

			String mergeKey = exportModel.getMergeKey();

			String rowsMerge = exportModel.getRowsMerge();
			List<Integer> rowsMergeLists = Lists.newArrayList();
			JsonNode rowsMergeNode = null;
			if (rowsMerge != null) {
				rowsMergeNode = Json.parse(rowsMerge);
			}
			if (rowsMergeNode != null && rowsMergeNode.size() > 0) {
				for (JsonNode node : rowsMergeNode) {
					Integer rowMerge = node.asInt();
					rowsMergeLists.add(rowMerge);
				}
			}

			String fileName = exportModel.getFileName();
			String excelTitle = exportModel.getExcelTitle();

			String tempFileName = CommonExportUtils.getFile(sqlResult, sqlResultKeyMap, excelRowKeyMap, fileName,
					excelTitle, excelWidthMap, rowsMergeLists, mergeKey);
			result.put("result", "0");
			result.put("fileName", fileName);
			result.put("temFileName", tempFileName);
		} catch (Exception e) {
			Logger.info("导出功能发生异常{}", e);
			result.put("result", "1");
			result.put("msg", "导出功能发生异常");
		}
		return Json.toJson(result).toString();
	}

	@Override
	public String productSalesVolumeSearch(String reqParam) {
		JsonNode node = Json.parse(reqParam);
		Map<String,Object> result=Maps.newHashMap();
		ProductSalesVolumeSearchDto volumeSearchDto=new ProductSalesVolumeSearchDto();
		volumeSearchDto.setBeginDate(JsonCaseUtil.getStringValue(node, "beginDate"));
		volumeSearchDto.setEndDate(JsonCaseUtil.getStringValue(node, "endDate"));
		volumeSearchDto.setIstatus(JsonCaseUtil.getStringValue(node, "istatus"));
		volumeSearchDto.setBack(
				JsonCaseUtil.isJsonEmpty(node) || node.get("isBack") == null ? null : node.get("isBack").asBoolean());
		volumeSearchDto.setTypeId(JsonCaseUtil.getStringValue(node, "typeId"));
		volumeSearchDto.setCategoryId(JsonCaseUtil.getStringValue(node, "categoryId"));
		volumeSearchDto.setCurrPage(JsonCaseUtil.getStringValue(node, "currPage"));
		volumeSearchDto.setPageSize(JsonCaseUtil.getStringValue(node, "pageSize"));
		volumeSearchDto.setTitle(JsonCaseUtil.getStringValue(node, "title"));
		List<ProductSalesVolume> dataLists = saleMainMapper.getProductInfo(volumeSearchDto);
		int total= saleMainMapper.productInfoTotal(volumeSearchDto);
		int pageSize = Integer.valueOf(volumeSearchDto.getPageSize());
		int totalPage=0;
		if(total%pageSize>0){
			totalPage=total/pageSize+1;
		}else{
			totalPage=total/pageSize;
		}
		result.put("result", dataLists);
		result.put("pageSize", pageSize);
		result.put("totalPage", totalPage);
		result.put("currPage", volumeSearchDto.getCurrPage());
		result.put("rows", total);
		return Json.toJson(result).toString();
	}
}
