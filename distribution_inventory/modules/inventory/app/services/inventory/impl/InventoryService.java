package services.inventory.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.inventory.APIInventoryDto;
import dto.inventory.APIInventoryDtoDetail;
import dto.inventory.DisInventoryDto;
import dto.inventory.DisProductDto;
import dto.inventory.IvyCheckResDto;
import dto.warehousing.InventoryChangeHistoryDto;
import entity.product.ProductDisprice;
import entity.warehousing.InventoryChangeHistory;
import mapper.inventory.DisInventoryMapper;
import mapper.inventory.DisProductMapper;
import mapper.inventory.WarehouseInventoryMapper;
import mapper.inventory.WarehouseProductMapper;
import mapper.product.ProductDispriceMapper;
import mapper.warehousing.InventoryChangeHistoryMapper;
import play.Logger;
import play.libs.Json;
import services.inventory.IInventoryService;
import services.inventory.IWarehInvenService;
import services.warehousing.IInventoryChangeHistoryService;
import services.warehousing.IMicroGoodsInventoryService;
import utils.inventory.HttpUtil;

public class InventoryService implements IInventoryService {

	@Inject
	DisInventoryMapper disInventoryMapper;
	@Inject
	DisProductMapper disProductMapper;
	@Inject
	WarehouseInventoryMapper warehouseInventoryMapper;
	@Inject
	WarehouseProductMapper warehouseProductMapper;
	@Inject
	IWarehInvenService WarehInvenService;
	@Inject
	IInventoryChangeHistoryService inventoryChangeHistoryService;
	@Inject
	IMicroGoodsInventoryService microGoodService;
	@Inject
	ProductDispriceMapper priceMapper;

	@Inject
	InventoryChangeHistoryMapper inventoryChangeHistoryMapper;
	@Override
	public List<APIInventoryDtoDetail> updateInventoryByOrderInfo(JsonNode main,String email) {
		if(email == null){
			email = main.get("email").textValue();
		}
		//通知云仓，进行库存还原的详细集合
		List<APIInventoryDtoDetail> apiDetails = new ArrayList<APIInventoryDtoDetail>();
		
		JsonNode details = main.get("pros");
		for (JsonNode d : details) {
		
			//通知云仓，进行库存变更的详细
			APIInventoryDtoDetail apiDetail = new APIInventoryDtoDetail();
			apiDetail.setSku(d.get("sku").textValue());
			apiDetail.setProductTitle(d.get("productName").textValue());
			apiDetail.setPurchasePrice(d.get("purchasePrice").asDouble());
			apiDetail.setNum(d.get("qty").asInt());
			apiDetail.setCostprice(d.get("purchasePrice").asDouble());
			apiDetail.setWarehouseId(d.get("warehouseId").asInt());
			apiDetail.setWarehouseName(d.get("warehouseName").asText());
			apiDetail.setDisProfitRate(jsonToDouble(d.get("disProfitRate")));
			apiDetail.setDisProfit(jsonToDouble(d.get("disProfit")));
			apiDetail.setDisVat(jsonToDouble(d.get("disVat")));
			apiDetail.setDisStockFee(jsonToDouble(d.get("disStockFee")));
			apiDetail.setDisShippingType(jsonToString(d.get("disShippingType")));
			apiDetail.setDisOtherCost(jsonToDouble(d.get("disOtherCost")));
			apiDetail.setDisTotalCost(jsonToDouble(d.get("disTotalCost")));
			apiDetail.setDisTransferFee(jsonToDouble(d.get("disTransferFee")));
			apiDetail.setDisListFee(jsonToDouble(d.get("disListFee")));
			apiDetail.setDisPayFee(jsonToDouble(d.get("disPayFee")));
			apiDetail.setDisTradeFee(jsonToDouble(d.get("disTradeFee")));
			apiDetail.setDisPayFee(jsonToDouble(d.get("disPayFee")));
			apiDetail.setDisPostalFee(jsonToDouble(d.get("disPostalFee")));
			apiDetail.setDisImportTar(jsonToDouble(d.get("disImportTar")));
			apiDetail.setDisGst(jsonToDouble(d.get("disGst")));
			apiDetail.setDisInsurance(jsonToDouble(d.get("disInsurance")));
			apiDetail.setDisTotalCost(jsonToDouble(d.get("disTotalCost")));
			apiDetail.setCost(jsonToDouble(d.get("cost")));
			apiDetail.setDisFreight(jsonToDouble(d.get("disFreight")));
			apiDetail.setDisPrice(jsonToDouble(d.get("disPrice")));
			apiDetail.setDisCifPrice(jsonToDouble(d.get("disCifPrice")));
			apiDetail.setDisTotalVat(jsonToDouble(d.get("disTotalVat")));
			apiDetail.setIsgift(d.has("isgift") ? d.get("isgift").asBoolean() : false);
			apiDetail.setCapFee(jsonToDouble(d.get("capFee")));
			//=== add by zbc 查询到仓价  start ===//
			ProductDisprice price = priceMapper.selectByParam(apiDetail.getSku(), apiDetail.getWarehouseId());
			apiDetail.setArriveWarePrice(price !=null?price.getArriveWarePrice():null);
			//=== add by zbc 查询到仓价  end ===//
			apiDetails.add(apiDetail);
		}
		
		/***********************************以下内容将通知云仓，微仓的变化**************************************/
		APIInventoryDto apiMain = new APIInventoryDto();
		
		apiMain.setOrderType(11);
		apiMain.setOrderTitle("采购入库");
		apiMain.setOrderNo(main.get("purchaseNo").textValue());
		apiMain.setDistributorName(email);
		apiMain.setDistributorEmail(email);
		apiMain.setDetailList(apiDetails);
		Logger.info("========微仓入库参数：[{}]========",Json.toJson(apiMain).toString());
		//通知云仓，进行入库操作
		String postRes = HttpUtil.post(Json.toJson(apiMain).toString(), HttpUtil.B2CBASEURL + "/warehousing/micro-inventory");
		
		Logger.info("========微仓入库结果：[{}]========",postRes);
		
		return apiDetails;
		
	}
	
	@Override
	public List<IvyCheckResDto> disInventoryCheck(String s_main,String email) {
		
		JsonNode main = Json.parse(s_main);
		if(email == null){
			email = (main.has("email")?main.get("email").asText():null);
		}
		List<IvyCheckResDto> res = new ArrayList<IvyCheckResDto>();
		
		JsonNode details = main.get("pros");
		for (JsonNode detail : details) {
			
			String getRes = "";
			JsonNode jn = null;
			if(main.has("totalCheck")){//云仓查询

				Logger.info("========库存检查：[云仓检查]SKU[{}]========",detail.get("sku").textValue());
				Logger.info("========库存检查：[云仓检查]仓库ID[{}]========",detail.get("warehouseId").textValue());
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("sku", detail.get("sku").textValue());
				params.put("warehouseId", detail.get("warehouseId").textValue());
				getRes = HttpUtil.get(params, HttpUtil.B2BBASEURL + "/inventory/warehousing/cloud-inventory");
				jn = Json.parse(getRes).get("list");
			}
			else{//微仓查询
				
				Logger.info("========库存检查：[微仓检查]SKU[{}]========",detail.get("sku").textValue());
				Logger.info("========库存检查：[微仓检查]仓库ID[{}]========",detail.get("warehouseId").asText());
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("sku", detail.get("sku").textValue());
				params.put("mwarehouseId", detail.get("warehouseId").textValue());
				params.put("distributorEmail", email);
				getRes = HttpUtil.get(params, HttpUtil.B2CBASEURL + "/warehousing/micro-inventory");
				jn = Json.parse(getRes).get("data").get("list");
				Logger.info("-------" + jn.toString());
			}
			
			Integer stock = 0;
//			//总仓数量
//			if(jn.size() > 0 && main.has("totalCheck")){
//				stock = jn.get(0).get("cTotalStock").asInt();
//			}
//			else{
//				//云仓数量
//				if(jn.size() > 0){
//					stock = jn.get(0).get("avaliableStock").asInt();
//				}
//			}
			
			IvyCheckResDto i = new IvyCheckResDto();
			Integer giftNum = 0;//微仓赠品数量
			Integer mircNum = 0;//微仓正价商品数量
			Integer totalNum = detail.get("qty").asInt();//商品发货总数
			Integer num = 0;
			for (JsonNode node : jn) {
				if(main.has("totalCheck")) {
					stock += node.get("cTotalStock").asInt();	
					i.setTotoalStock(node.get("cTotalStock").asInt());
				} else {
					num = node.get("avaliableStock").asInt();
					stock += num;
					if(node.has("gift") && node.get("gift").asBoolean()) {
						giftNum = num;
					} else {
						mircNum = num;
					}
				}
			}
			if(stock > 0 && giftNum > 0) {//微仓有货，并且包含了赠品
				if(totalNum > mircNum) {//发货数量大于正价数量
					i.setIsgift(true);
					i.setGiftNum(totalNum - mircNum >= giftNum ? giftNum : totalNum - mircNum);
				}
			}
			
			i.setSku(detail.get("sku").textValue());
			i.setStatus("enough");
			i.setStockOutQty(totalNum);
			i.setSendoutTotalQty(totalNum);
			i.setWarehouseId(detail.get("warehouseId").asInt());
			i.setWarehouseName(detail.has("warehouseName")?detail.get("warehouseName").textValue():"");
			i.setProductName(detail.has("productName")?detail.get("productName").textValue():"");
			i.setPurchasePrice(detail.has("purchasePrice")?detail.get("purchasePrice").asDouble():0);
			i.setProductImg(detail.has("productImg")?detail.get("productImg").textValue():"");
			i.setMarketPrice(detail.has("marketPrice")?detail.get("marketPrice").asDouble():0);
			i.setSalesOrderNo(detail.has("salesOrderNo")?main.get("salesOrderNo").textValue():"");
			
			//微仓中未购入
			if(stock == 0){
				i.setStatus("notExist");
				//待采购的数量
 				i.setStockOutQty(totalNum);
			}else{
				//微仓已购入，但微仓库存不足
				if(stock < totalNum){
					i.setStatus("notEnough");
					//缺货数量
					i.setStockOutQty(totalNum - stock);
				}
			}
			Logger.info("========库存检查：检查结果[{}]========",i.toString());
			res.add(i);
		}
		return res;
	}
	
	@Override
	public String deductDisInventory(JsonNode node,String email) {
		if(email == null){
			email = node.get("email").asText();
		}
		try {
			//库存扣除明细 保存到客户订单中
			saveDeductDisInventoryToSD(node,email);			
		} catch (Exception e) {
			Logger.info("获取库存扣除明细失败，" + e);
		}
		//通知云仓，进行库存还原的详细集合
		List<APIInventoryDtoDetail> apiDetails = new ArrayList<APIInventoryDtoDetail>();
		
		JsonNode pros = node.get("pros").get("currentDetail");
		String saleOrderNo = pros.get(0).get("salesOrderNo").textValue();
		for (JsonNode d : pros) {
			
			//通知云仓，进行库存还原的详细
			APIInventoryDtoDetail apiDetail = new APIInventoryDtoDetail();
			apiDetail.setSku(d.get("sku").textValue());
			apiDetail.setProductTitle(d.get("productName").textValue());
			apiDetail.setNum(d.get("qty").asInt());
			apiDetail.setCostprice(d.get("purchasePrice").asDouble());
			apiDetail.setWarehouseId(d.get("warehouseId").asInt());
			apiDetail.setWarehouseName(d.get("warehouseName").textValue());
			apiDetails.add(apiDetail);
			
		}
		
		APIInventoryDto apiMain = new APIInventoryDto();
		
		apiMain.setOrderType(21);
		apiMain.setOrderTitle("销售出库");
		apiMain.setOrderNo(saleOrderNo);
		apiMain.setDistributorName(email);
		apiMain.setDistributorEmail(email);
		apiMain.setDetailList(apiDetails);
		
		//通知云仓，进行库存扣减
		String postRes = HttpUtil.post(Json.toJson(apiMain).toString(), HttpUtil.B2CBASEURL + "/warehousing/micro-inventory");
		
		
		Logger.info("========库存扣减：扣减结果[{}]========",postRes);
		return postRes;
	}
	/**
	 * 将微仓库存扣除数据保存到客户订单明细中
	 * @param email 
	 */
	public void saveDeductDisInventoryToSD(JsonNode node, String email){
		JsonNode pros = node.get("pros").get("currentDetail");
		String saleOrderNo = pros.get(0).get("salesOrderNo").asText();
		Integer warehouseId = pros.get(0).get("warehouseId").asInt();
		//将出微仓数据保存在客户订单中，后期 返佣有需求
		Map<String,Object>  params = Maps.newHashMap();
		params.put("email",email);
		params.put("warehouseId",warehouseId);	
		List<Map<String,Object>> postDetails =Lists.newArrayList();
		Map<String,Object> postDetail = null;
		int count = 0;
		int qty = 0;
		for (JsonNode d : pros) {
			postDetail = Maps.newHashMap();
			qty = d.get("qty").asInt();
			count += qty;
			postDetail.put("sku", d.get("sku").asText());
			postDetail.put("qty",qty);
			postDetails.add(postDetail);
		}
		if(count >0){
			params.put("details",postDetails);
			Map<String,Object> history = inventoryChangeHistoryService.getPurchasePriceByChangeHistory(Json.toJson(params));
			Map<String,Object> postMap = Maps.newHashMap();
			postMap.put("details", postDetails) ;
			postMap.put("salesOrderNo", saleOrderNo) ;
			postMap.put("history",history);
			String res = HttpUtil.post(Json.toJson(postMap).toString(), HttpUtil.B2BBASEURL+"/sales/saveOutIvyHistory");
			Logger.info("========保存历史数据结果：[{}]========",res);
		}
	}

	@Override
	public List<DisInventoryDto> getDisProduct(JsonNode node) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("email", node.get("email").textValue());
		param.put("warehouseId", node.get("warehouseId") == null ? 0 : node.get("warehouseId").asInt());
		param.put("qty", node.get("qty") == null ? 0 : node.get("qty").asInt());
		return disProductMapper.selectDisproductByWareIdAndEmail(param);
	}

	@Override
	public String getDisProductAndStockInfo(JsonNode node) {
		
 		String wid  = node.has("wid")?node.get("wid").textValue():"";
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("sku", node.get("sku").textValue());
		params.put("mwarehouseId", wid);
		params.put("distributorEmail", node.get("email").textValue());
		
		Logger.info("========微仓查询：参数sku：[{}]========", node.get("sku").textValue());
		Logger.info("========微仓查询：仓库id：[{}]========", wid);
		Logger.info("========微仓查询：email：[{}]========", node.get("email").textValue());
		
		String getRes = HttpUtil.get(params, HttpUtil.B2CBASEURL + "/warehousing/micro-inventory");
		Logger.info("========微仓查询：查询结果[{}]========", getRes);
		return getRes;
	}

	@Override
	public String initDisproduct(JsonNode node,String email) {
		Map<String, String> param = Maps.newHashMap();
		if(email == null){
			email = node.get("email").textValue();
		}
		Integer pageSize = !node.has("pageSize") ? null : node.get("pageSize").asInt();
		Integer currPage = !node.has("currPage") ? null : node.get("currPage").asInt();
		if(node.has("warehouseId") && !"".equals(node.get("warehouseId").textValue())){
			param.put("warehouseId",node.get("warehouseId").textValue());
		}
		
		if(node.has("productTitle") && !"".equals(node.get("productTitle").textValue())){
			param.put("key",node.get("productTitle").textValue());
		}
		
		if(node.has("productCategoryId") && !"".equals(node.get("productCategoryId").textValue())){
			param.put("productCategoryId",node.get("productCategoryId").textValue());
		}
		
		if(node.has("avaliableStock") && !"".equals(node.get("avaliableStock").textValue())){
			param.put("avaliableStock",node.get("avaliableStock").textValue());
		}
		if(node.has("sku") && !"".equals(node.get("sku").textValue())){
			param.put("sku",node.get("sku").textValue());
		}
		if(node.has("warehouseId") && !"".equals(node.get("warehouseId").textValue())){
			param.put("warehouseId",node.get("warehouseId").textValue());
		}
		param.put("pageSize", pageSize!=null?String.valueOf(pageSize):null);
		param.put("pageNo",currPage!=null? String.valueOf(currPage):null);
		param.put("distributorEmail", email);
		 
		
		return microGoodService.b2bQuery(Json.toJson(param)).toString();
		/*//微仓查询
		String getRes = HttpUtil.get(param, HttpUtil.B2CBASEURL + "/warehousing/micro-inventory");
 		return getRes;*/
	}

	@Override
	public List<DisProductDto> selectAllStock(JsonNode node) {
		return disProductMapper.selectAllStock(node.get("email").textValue());
	}

	@Override
	public int frozenStock(JsonNode node) {
		int updCount = 0;
		for (JsonNode skuToBeFrozen : node) {
			warehouseInventoryMapper.updateFrozenStockInWarehouse(skuToBeFrozen.get("sku").textValue(), 
																  skuToBeFrozen.get("warehouseId").asInt(), 
																  skuToBeFrozen.get("qty").asInt());
			updCount++;
		}
		return updCount;
	}

	@Override
	public String getShippingMethodByWarehouse(Integer wid) {
		Logger.info("========物流方式查询：查询参数[{}]========", wid);
		//String getRes = HttpUtil.get(Maps.newHashMap(), HttpUtil.B2CBASEURL + "/checkout/shipping-methods/"+wid);
		String getRes = HttpUtil.get(Maps.newHashMap(), HttpUtil.B2CBASEURL + "/sysadmin/warehouse/" + wid + "/shippings");
		Logger.info("========物流方式查询：查询结果[{}]========",getRes);
		return getRes;
	}

	@Override
	public String getFreight(JsonNode node) {
		Logger.info("========运费查询：查询参数[{}]========", node.toString());
		String getRes = HttpUtil.post(node.toString(), HttpUtil.B2CBASEURL + "/checkout/orders/freight");
		Logger.info("========运费查询：查询结果[{}]========",getRes);


//		JsonNode freightNode = null;
//		try {
//			ObjectMapper obj = new ObjectMapper();
//			freightNode = obj.readTree(getRes);
//		} catch (IOException e) {
//			Logger.error("----------------解析运费查询接口返回值错误：{}", e);
//		}
//
//		if (node.has("model")) {
//			int model = node.get("model").asInt();//用户渠道
//			if (model == 1 && freightNode.has("result") && freightNode.get("result").asBoolean()) { //获取操作费
//				int warehouseId = node.get("warehouseId").asInt();//仓库Id
//				JsonNode detailNode = node.get("orderDetails");
//
//				int qty = 0;
//				if (detailNode != null) {
//					for (JsonNode d : detailNode) {
//						qty += d.get("num").asInt();
//					}
//				}
//
//				Map<String, String> param = Maps.newHashMap();
//				param.put("warehouseId", String.valueOf(warehouseId));
//				param.put("qty", String.valueOf(qty));
//				String resultString = HttpUtil.get(param,
//						HttpUtil.B2CBASEURL + "/warehousing/getoptfee");
//				Logger.info("获取操作费结果为：" + resultString);
//
//
//				JsonNode jsonNode = null;
//				try {
//					ObjectMapper obj = new ObjectMapper();
//					jsonNode = obj.readTree(resultString);
//				} catch (IOException e) {
//					Logger.error("----------------解析获取操作费接口返回数据错误：{}", e);
//				}
//
//				double fee = 0d;//操作费
//				double freight = freightNode.get("msg").asDouble();
//
//				if (jsonNode.has("result") && jsonNode.get("result").asBoolean()) {
//					fee = jsonNode.get("optfee").asDouble();
//				} else {
//					String msg = "{\n" +
//							"  \"result\": false,\n" +
//							"  \"msg\": "+ jsonNode.get("msg").asText() + "\n" +
//							"}";
//					return msg;
//				}
//
//				String msg = "{\n" +
//						"  \"result\": true,\n" +
//						"  \"msg\": "+ Math.round(freight + fee) + "\n" +
//						"}";
//				return msg;
//			}
//		}
//
//		if (freightNode.get("result").asBoolean()) {
//			String msg = "{\n" +
//					"  \"result\": true,\n" +
//					"  \"msg\": "+ Math.round(freightNode.get("msg").asDouble()) + "\n" +
//					"}";
//			return msg;
//		}
		return getRes;
	}
	
	@Override
	public List<InventoryChangeHistory> getDisProductMicroChangeHistory(String main, String email) {
		JsonNode jsonNode = Json.parse(main);
		int total = jsonNode.get("total").asInt();
		InventoryChangeHistoryDto dto = new InventoryChangeHistoryDto();
		dto.setSku(jsonNode.get("sku").asText());
		dto.setOperator(email);
		dto.setType(1);
		dto.setMorderType("11");
		dto.setChangeTimeDesc(1);
		List<InventoryChangeHistory> inventoryChangeHistories = inventoryChangeHistoryMapper.selectByParam(dto);
		List<InventoryChangeHistory> results = Lists.newArrayList();
		int reduceNum = total;
		for (int i = 0; i < inventoryChangeHistories.size(); i++) {
			InventoryChangeHistory inventoryChangeHistory = inventoryChangeHistories.get(i);
			int inventoryChangeHistoryNum = inventoryChangeHistory.getNum();
			if (inventoryChangeHistoryNum >= reduceNum) {
				inventoryChangeHistory.setNum(reduceNum);
				results.add(inventoryChangeHistory);
				break;
			} else {
				reduceNum = reduceNum - inventoryChangeHistoryNum;
				results.add(inventoryChangeHistory);
			}
		}
		return Lists.reverse(results);
	}

	public Double jsonToDouble(JsonNode node){
		return node == null?null:node.asDouble();
	}
	public String jsonToString(JsonNode node){
		return node == null?null:node.textValue();
	}

}
