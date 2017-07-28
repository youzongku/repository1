package services.openapi.impl;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import dto.openapi.ViewPurchaseDetail;
import dto.openapi.ViewPurchaseDetailDto;
import dto.openapi.ViewPurchaseOrder;
import dto.openapi.ViewPurchaseOrderDto;
import dto.openapi.enums.PurchaseOrderFlag;
import play.Logger;
import play.libs.Json;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.openapi.IHttpService;
import services.openapi.ILoginService;
import services.openapi.IPurchaseService;
import utils.Page;
import utils.response.ResponseResultUtil;

/**
 * @author zbc
 * 2016年8月26日 上午10:44:03
 */
public class PurchaseService implements IPurchaseService {
	
	@Inject
	IHttpService httpService;
	
	@Inject
	ILoginService loginService;
	
	private static DecimalFormat f = new DecimalFormat("###0.00"); 
	static {
    	f.setRoundingMode(RoundingMode.HALF_UP);     	
    }
	/** 
	 *
     * 可能有赠品
     * 根据参数获取 商品信息  检查库存
	 * 分销商
	 */
	@Override
	public Result order(JsonNode orderNode,Context context) {
		Map<String,Object> res = Maps.newHashMap();
		try {//分销商账号
			JsonNode login = loginService.currentUser(orderNode.get("ltc").asText());
			String email = login.get("email").asText();
			Integer distributorType = login.get("comsumerType").asInt();
			//分销商模式
			Integer distributionMode = login.get("distributionMode").asInt();
			List<String> skus = Lists.newArrayList();
			Map<String,Integer> skuNumMap = Maps.newHashMap();
			String key = null;
			Set<String> proSet = Sets.newHashSet();
			for(JsonNode pro:orderNode.get("details")){
				skus.add(pro.get("sku").asText());
				key = pro.get("sku").asText() + "_"+ pro.get("wareId").asInt();
				proSet.add(key);
				skuNumMap.put(key, pro.get("qty").asInt());
			}
			JsonNode productStrNode = null;
			JsonNode pros = null;
			try {
				// 获取商品
				productStrNode = httpService.fgetProducts(null,skus, null,distributionMode);
				if (productStrNode == null || productStrNode.get("data").get("result").size() <= 0) {
					return ResponseResultUtil.newErrorJson(102,"未查询到商品信息"); 
				}
				pros = productStrNode.get("data").get("result");
				boolean enough = true;
				String msg = "";
				Integer stock = null; 
				Lists.newArrayList(proSet);
				int batchNumber = 1;
				Integer qty = 0; 
				boolean  batchFlag = true;
				String batchMsg = "";
				
				for (JsonNode pro : pros) {
					stock = pro.get("stock").asInt();
					key = pro.get("csku").asText() + "_" + pro.get("warehouseId").asInt();
					qty = skuNumMap.get(key);
					batchNumber = pro.get("batchNumber") !=null?pro.get("batchNumber").asInt():1;
					if(qty != null){
						if(qty < batchNumber){
							batchFlag = false;
							batchMsg += "["+pro.get("csku").asText()+"]不满足起批量["+batchNumber+"],";
						}
						qty += skuNumMap.get(key + "_"+ true) != null?skuNumMap.get(key + "_"+ true):0;
						if(qty > stock){
							enough = false;
							msg += "["+key+"],";
						}
					}
					proSet.remove(key);
				}
				
				if(!enough){
					return ResponseResultUtil.newErrorJson(103, msg+ "库存不足无法下单");
				}
				if(!batchFlag){
					return ResponseResultUtil.newErrorJson(106, batchMsg+ "无法下单");
				}
				if(proSet.size() > 0){
					return ResponseResultUtil.newErrorJson(104, proSet.toString()+ "商品不存在，无法下单");
				}
				
			} catch (Exception e) {
				Logger.info("getProducts:" + e);
				return ResponseResultUtil.newErrorJson(102,"未查询到商品信息"); 
			}
			List<Map<String,Object>> detailList  = Lists.newArrayList();
			Double totalPrice = getDetailList(detailList,pros,distributionMode,orderNode.get("details"));
			Map<String,Object> orderParma = Maps.newHashMap();
			orderParma.put("email",email);
			orderParma.put("orderDetail", detailList);
			orderParma.put("distributorType",distributorType);
			orderParma.put("totalPrice", totalPrice);
			JsonNode purJson = httpService.postPurchase(orderParma,context);
			
			res.put("orderNo", purJson.get("errorInfo").asText());
		}catch (Exception e) {
			Logger.info("采购单下单异常",e);
			return ResponseResultUtil.newErrorJson(105,"下单异常");
		}
		return ResponseResultUtil.newSuccessJson(Json.toJson(res).toString());
	}
	
	
	private Double getDetailList(List<Map<String,Object>> list,JsonNode proNode,Integer mode,JsonNode orderNode) {
		Double totalPrice = 0.00;
		Double purchase  = null;
		Double sumPrice  = null;
		Map<String,Object> detail = null;
		int qty = 0;
		boolean isgift = false;
		for(JsonNode opro:orderNode){
			for(JsonNode pro:proNode){
				if(opro.get("sku").equals(pro.get("csku")) && opro.get("wareId").equals(pro.get("warehouseId")) ){
					detail = Maps.newHashMap();
					qty = opro.get("qty").asInt();
					purchase = pro.get("disPrice").asDouble();
					detail.put("itemId", "");
					detail.put("title", getStringValue(pro, "ctitle"));
					//add by zbc 赠品实际价格	
					detail.put("realPrice", purchase);
					detail.put("sku", getStringValue(pro, "csku"));
					detail.put("qty", qty);
					detail.put("publicImg", getStringValue(pro, "imageUrl"));
					detail.put("warehouseId", getIntegerValue(pro, "warehouseId"));
					detail.put("warehouseName", getStringValue(pro, "warehouseName"));
					detail.put("categoryId",getDoubleValue(pro,"categoryId"));
					detail.put("price",isgift?0.00:purchase);
					detail.put("marketPrice",isgift?0.00:getDoubleValue(pro, "localPrice"));
					detail.put("disPrice", isgift?0.00:getDoubleValue(pro,"disPrice")); // 分销价
					sumPrice = Double
							.parseDouble(f.format((isgift?0.00:purchase)* qty));
					totalPrice = totalPrice + sumPrice;
					detail.put("sumPrice", sumPrice);
					list.add(detail);
				}
			}
		}
		
		return Double
				.parseDouble(f.format(totalPrice));
	}
	
	private String getStringValue(JsonNode node, String key) {
		return node.get(key) != null?("null".equals(node.get(key).asText())?null:node.get(key).asText()):null;
	}

	private Integer getIntegerValue(JsonNode node, String key) {
		return node.get(key) != null ? node.get(key).asInt() : null;
	}

	private Double getDoubleValue(JsonNode node, String key) {
		return node.get(key) != null ? node.get(key).asDouble() : null;
	}


	
	@Override
	public Result getOrderPage(JsonNode node,Context context) {
		try {
			JsonNode login = loginService.currentUser(node.get("ltc").asText());
			Integer pageSize = node.get("pageSize") != null?node.get("pageSize").asInt():10;
			Integer pageCount = node.get("currPage") !=null?node.get("currPage").asInt():1;
			if(pageSize > 100){
				return ResponseResultUtil.newErrorJson(101, "最大页数不能超过100");
			}
			String email = login.get("email").asText();
			Map<String,Object> postParam = Maps.newHashMap();
			postParam.put("email",email);
			postParam.put("pageSize",pageSize);
			postParam.put("pageCount", pageCount);
			postParam.put("status",node.get("status") != null?PurchaseOrderFlag.valueOf(node.get("status").asText()).getType():null);
			postParam.put("seachFlag",node.get("title") != null?node.get("title").asText():null);
			postParam.put("sorderDate", node.get("sorderDate") != null?node.get("sorderDate").asText():null);
			postParam.put("eorderDate", node.get("eorderDate") != null?node.get("eorderDate").asText():null);
			postParam.put("spaydate", node.get("spaydate") != null?node.get("spaydate").asText():null);
			postParam.put("epaydate", node.get("epaydate") != null?node.get("epaydate").asText():null);
			
			JsonNode  res = httpService.viewpurchase(postParam);
			JsonNode orders = res.get("orders");
			Integer total = res.get("total").asInt();
			List<ViewPurchaseOrder> list = Lists.newArrayList();
			ObjectMapper map = new ObjectMapper();
			list = map.readValue(orders.toString(), new TypeReference<List<ViewPurchaseOrder>>() {});
			List<ViewPurchaseOrderDto> dtoList = Lists.newArrayList();
			ViewPurchaseOrderDto dto = null;
			ViewPurchaseDetailDto detailDto = null;
			List<ViewPurchaseDetailDto> detailList = null; 
			//字段隐藏
			for(ViewPurchaseOrder or:list){
				dto = new ViewPurchaseOrderDto();
				BeanUtils.copyProperties(or, dto);
				detailList = Lists.newArrayList();
				for(ViewPurchaseDetail detail:or.getDetails()){
					detailDto = new ViewPurchaseDetailDto();
					BeanUtils.copyProperties(detail, detailDto);
					detailList.add(detailDto);
				}
				dto.setDetails(detailList);
				dtoList.add(dto);
			}
			return ResponseResultUtil.newSuccessJson(new Page<ViewPurchaseOrderDto>(pageCount, pageSize, total, dtoList));
		} catch (Exception e) {
			Logger.info("查询采购单异常",e);
			return ResponseResultUtil.newErrorJson(103, "查询采购单异常");
		}
	}
	
}
