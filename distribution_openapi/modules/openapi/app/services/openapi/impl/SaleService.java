package services.openapi.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.Context;
import services.openapi.IHttpService;
import services.openapi.ILoginService;
import services.openapi.ISaleService;
import utils.HttpUtil;
import utils.SalesStatusUtil;
import utils.StringUtils;
import utils.response.ResponseResultUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class SaleService implements ISaleService {

	@Inject
	private IHttpService httpService;
	@Inject
	private ILoginService loginService;

	@Override
	public String getCustomerOrderPage(JsonNode main, Context context) {
		ObjectNode paramNode = Json.newObject();

		if (!main.has("status"))
			paramNode.put("status", "");
		else
			paramNode.put("status",
					SalesStatusUtil.get(main.get("status").asText()));

		if (!main.has("orderStartDate"))
			paramNode.put("orderStartDate", "");
		else
			paramNode
					.put("orderStartDate", main.get("orderStartDate").asText());

		if (!main.has("orderEndDate"))
			paramNode.put("orderEndDate", "");
		else
			paramNode.put("orderEndDate", main.get("orderEndDate").asText());

		if (!main.has("noticeStartDate"))
			paramNode.put("noticeStartDate", "");
		else
			paramNode.put("noticeStartDate", main.get("noticeStartDate")
					.asText());

		if (!main.has("noticeEndDate"))
			paramNode.put("noticeEndDate", "");
		else
			paramNode.put("noticeEndDate", main.get("noticeEndDate").asText());

		if (!main.has("seachSpan"))
			paramNode.put("seachSpan", "");
		else
			paramNode.put("seachSpan", main.get("seachSpan").asText());

		if (!main.has("warehouseId"))
			paramNode.put("warehouseId", "");
		else
			paramNode.put("warehouseId", main.get("warehouseId").asText());

		JsonNode currentUserNode = loginService.currentUser(main.get("ltc").asText());
		Logger.info("Logined user is " + currentUserNode.get("email").asText());
		paramNode.put("email", currentUserNode.get("email").asText());

		// 查询
		String url = HttpUtil.B2BBASEURL + "/sales/open/showAllOrder";
		String postResult = HttpUtil.post(paramNode.toString(), url, context);

		return postResult;
	}

	@Override
	public Result createNewOrder(JsonNode main, Context context) {
		// 预先做一部分的数据校验
		List<String> skus = Lists.newArrayList();
		// 店铺名称
		String shopName = main.get("shopName").textValue();
		// 分销账号
		JsonNode currentUserNode = loginService.currentUser(main.get("ltc").asText());
		String email = currentUserNode.get("email").asText();
		// 分销商模式
		Integer distributionMode = currentUserNode.get("distributionMode").asInt();
		Logger.info("createNewOrder当前登录用户是：" + email);
		ObjectNode nodeObj = (ObjectNode) main;
		nodeObj.put("email", email);
		nodeObj.put("disMode", distributionMode);

		// 仓库ID
		Integer warehouseId = main.get("warehouseId").asInt();
		JsonNode skuObj = main.get("skuList");// [{sku:"",num:1},{}]'

		Set<String> proSet = Sets.newHashSet();
		Map<String, Integer> sukNumMap = Maps.newHashMap();
		if (skuObj.isArray()) {
			Iterator<JsonNode> it = skuObj.iterator();
			JsonNode jsonNode = null;
			while (it.hasNext()) {
				jsonNode = (JsonNode) it.next();
				skus.add(jsonNode.get("sku").asText());
				sukNumMap.put(jsonNode.get("sku").asText(), jsonNode.get("num").asInt());
				proSet.add(jsonNode.get("sku").asText() + "_" + warehouseId.intValue());
			}
		} else {
			skus.add(skuObj.get("sku").asText());
			sukNumMap.put(skuObj.get("sku").asText(), skuObj.get("num").asInt());
			proSet.add(skuObj.get("sku").asText() + "_" + warehouseId.intValue());
		}

		JsonNode productStrNode = null;
		try {
			// 获取商品
			productStrNode = httpService.fgetProducts(email, skus, warehouseId, distributionMode);
			if (productStrNode == null
					|| productStrNode.get("data").get("result").size() <= 0) {
				return ResponseResultUtil.newErrorJson(104, "未查询到商品信息");
			}
			JsonNode pros = productStrNode.get("data").get("result");
			
			// 查询到的商品数量比下单的商品数量小，可能是部分商品无效或存在非卖品
			int realProCount = pros.size();
			if(skus.size() > realProCount){
				return ResponseResultUtil.newErrorJson(109, "部分商品无效或存在非卖品");
			}
			
			boolean stockEnough = true;
			String msg = "";
			for (JsonNode pro : pros) {
				Integer stock = pro.get("stock").asInt()
						+ (pro.get("microStock") != null ? pro.get("microStock").asInt() : 0);
				if (sukNumMap.get(pro.get("csku").asText()) > stock) {
					stockEnough = false;
					msg += "[" + pro.get("csku").asText() + "],";
				}
				// 移除
				proSet.remove(pro.get("csku").asText() + "_" + pro.get("warehouseId").asInt());
			}
			if (!stockEnough) {
				return ResponseResultUtil.newErrorJson(110, msg + "库存不足无法下单");
			}
			if(proSet.size() > 0){
				return ResponseResultUtil.newErrorJson(109, proSet.toString()+ "商品不存在，无法下单");
			}
		} catch (Exception e) {
			Logger.error("getProducts:" + e);
			return ResponseResultUtil.newErrorJson(104, "未查询到商品信息");
		}

		// 组织好客户订单信息
		JsonNode shopStrNode = null;
		try {
			// 获取店铺
			shopStrNode = httpService.getShopInfo(email, shopName, 12, context);
			if (shopStrNode == null || !shopStrNode.get("suc").asBoolean()) {
				return ResponseResultUtil.newErrorJson(102, "未查询到店铺信息");
			}
		} catch (Exception e) {
			Logger.error("getShopInfo:" + e);
			return ResponseResultUtil.newErrorJson(102, "未查询到店铺信息");
		}

		JsonNode custStrNode = null;
		try {
			// 获取客服账号
			custStrNode = httpService.custaccount(email);
			if (null == custStrNode
					|| StringUtils.isBlankOrNull(custStrNode.get("account")
							.asText())) {
				return ResponseResultUtil.newErrorJson(103, "未关联客服账号");
			}
		} catch (Exception e) {
			Logger.error("custaccount:" + e);
			return ResponseResultUtil.newErrorJson(103, "未关联客服账号");
		}

		JsonNode memberNode = null;
		try {
			// 获取用户详情
			memberNode = httpService.getMemberInfo(email);
			if (memberNode == null || !memberNode.has("comsumerType")) {
				return ResponseResultUtil.newErrorJson(105, "未查询到用户信息");
			}
		} catch (Exception e) {
			Logger.error("getMemberInfo:" + e);
			return ResponseResultUtil.newErrorJson(105, "未查询到用户信息");
		}

		// 下单
		String param = main.toString();
		String url = HttpUtil.B2BBASEURL + "/sales/postOrder4OpenApi";
		String postResult = HttpUtil.post(param, url, Context.current());
		return processOrderResult(postResult);
	}

	/**
	 * 处理下单返回结果
	 * 
	 * @param postResult
	 * @return
	 */
	private Result processOrderResult(String postResult) {
		// code:101,status:参数错误
		// code:102,status:未查询到店铺信息
		// code:103,status:未关联客服账号
		// code:104,status:未查询到商品信息
		// code:105,status:未查询到用户信息
		// code:106,status:生成订单失败
		// code:107,status:库存检查失败
		// code:108,msg:XS2016061316110100000832
		Logger.info("postResult=========" + postResult);
		JsonNode resultNode = Json.parse(postResult);
		int code = resultNode.get("code").asInt();
		if (code == 108) {
			// 结果：{"orderNo":"订单号"}
			String jsonString = "{\"orderNo\":\""
					+ resultNode.get("msg").asText() + "\"}";
			return ResponseResultUtil.newSuccessJson(jsonString);
		} else {
			return ResponseResultUtil.newErrorJson(code, resultNode.get("msg")
					.asText());
		}
	}

}
