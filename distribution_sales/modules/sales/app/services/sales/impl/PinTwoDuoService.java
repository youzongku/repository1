package services.sales.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.eventbus.EventBus;
import events.sales.ImportOrderSyncEvent;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import entity.platform.order.template.TaoBaoOrder;
import entity.platform.order.template.TaoBaoOrderGoods;
import entity.sales.OrderPack;
import entity.sales.PddLogistics;
import entity.sales.SaleBase;
import entity.sales.SaleMain;
import events.sales.LogisticsEvent;
import mapper.sales.PddLogisticsMapper;
import mapper.sales.TaoBaoOrderGoodsMapper;
import mapper.sales.TaoBaoOrderMapper;
import play.Logger;
import play.libs.Json;
import services.sales.IHttpService;
import services.sales.IJdService;
import services.sales.IPinTwoDuoService;
import services.sales.IYZService;
import util.sales.DateUtils;
import util.sales.JsonCaseUtil;
import util.sales.MD5Util;

/**
 * @author zbc 2017年2月28日 下午6:19:52
 */
public class PinTwoDuoService implements IPinTwoDuoService {

	private static String SERVERURL = "http://open.yangkeduo.com/api/router";
	private static String ORDER_LIST = "pdd.order.number.list.get";// 订单列表查询
	private static String SIMPLE_ORDER = "pdd.order.information.get";// 单个订单查询
	private static String LOGISTICS = "pdd.logistics.online.send";
	private static String DATA_TYPE = "JSON";
	// private static String MALL_ID = "35945";
	// private static String SECRET = "12884EEE-D588-47AD-98E9-FE5C6B796162";
	private static String MALL_ID = "";
	private static String SECRET = "";
	private static String ORDER_STATUS = "1";

	@Inject
	private TaoBaoOrderGoodsMapper goodsMapper;

	@Inject
	private TaoBaoOrderMapper orderMapper;

	@Inject
	private IJdService jdService;
	
	@Inject
	private IHttpService httpService;
	
	@Inject
	private IYZService yzService;

	@Inject
	private EventBus eventBus;

	@Inject
	private PddLogisticsMapper pddLogisticsMapper;

	private static Cache<String, String> cache = CacheBuilder
			.newBuilder()
			.expireAfterWrite(7200, TimeUnit.SECONDS)
			.maximumSize(10)
			.build();

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> pullOrder(JsonNode main,String email) {
		Map<String, Object> result = Maps.newHashMap();
		Integer shopId = main.get("shopId").asInt();

		JsonNode shopNode = jdService.checkShop(shopId);
		if (!jdService.isRightful(shopNode, "clientsecret", "clientid", "shopName")) {
			Logger.info("PDD店铺信息【{}】", shopNode);
			result.put("suc", false);
			result.put("msg", "店铺信息不完整，请重新确认。");
			return result;
		}
		SECRET = JsonCaseUtil.jsonToString(shopNode.get("clientsecret"));
		MALL_ID = JsonCaseUtil.jsonToString(shopNode.get("clientid"));
		String shopName = JsonCaseUtil.jsonToString(shopNode.get("shopName"));
		List<TaoBaoOrder> orderList = Lists.newArrayList();
		List<TaoBaoOrderGoods> goodList = Lists.newArrayList();
		Map<String, Object> orderListMap = getOrderList(shopName,email);
		if (!(boolean) orderListMap.get("suc")) {
			jdService.cancelSync(shopId);
			return orderListMap;
		} else {
			orderList = (List<TaoBaoOrder>) orderListMap.get("orderList");
			goodList = (List<TaoBaoOrderGoods>) orderListMap.get("good");
		}
		if (orderList.size() == 0) {
			result.put("suc", false);
			result.put("msg", "没有拉取到订单数据");
			jdService.cancelSync(shopId);
			return result;
		}
		// 批量插入订单
		orderMapper.batchInsert(orderList);
		// 批量插入商品
		goodsMapper.batchInsert(goodList);

		//补全信息
		eventBus.post(new ImportOrderSyncEvent(email));

		result.put("suc", true);
		result.put("msg", "拉取拼多多订单成功,共插入【" + orderList.size() + "】条");
		jdService.cancelSync(shopId);
		return result;
	}

	/**
	 * 
	 * @author zbc
	 * @param email 
	 * @since 2017年3月1日 上午11:52:38
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getOrderList(String shopName, String email) {
		Map<String, Object> queryMap = queryPddOrderList();
		if (!(boolean) queryMap.get("suc")) {
			return queryMap;
		} else {
			Logger.info("共拉取订单【{}】个", queryMap.get("count"));
			Set<String> orderNos = (Set<String>) queryMap.get("respose");
			// 生成订单
			return generateOrders(orderNos, shopName,email);
		}
	}

	private Map<String, Object> generateOrders(Set<String> orderNos, String shopName, String email) {
		Map<String, Object> orderListMap = Maps.newHashMap();
		JsonNode orderDetail = null;
		List<TaoBaoOrder> orderList = Lists.newArrayList();
		List<TaoBaoOrderGoods> goodList = Lists.newArrayList();
		for (String orderNo : orderNos) {
			try {
				TaoBaoOrder _order = new TaoBaoOrder();
				// 分销商账号
				_order.setEmail(email);
				// 订单编号
				_order.setOrderNo(orderNo);
				// 交易号JsonCaseUtil.jsonToString(orderInfo.get("pay_no"))
				_order.setPaymentNo(orderNo);
				TaoBaoOrder exitOrder = orderMapper.selectBygroube(_order);
				if (exitOrder != null) {
					Logger.info("该订单:[{}]已经存在", _order.getOrderNo());
					// 跳过循环
					continue;
				}
				orderDetail = queryPddOrderDetail(orderNo);
				JsonNode orderInfo = orderDetail.get("order_info_get_response").get("order_info");
				// 店铺名称 TODO 需要店铺名称
				_order.setShopName(shopName);
				//平台类型
				_order.setPlateform(14);
				// ************************ 收货人信息
				// ******************************//
				// 收货信息
				// 收货人姓名
				_order.setReceiverName(JsonCaseUtil.jsonToString(orderInfo.get("receiver_name")));
				// 收货人电话
				_order.setReceiverTelephone(JsonCaseUtil.jsonToString(orderInfo.get("receiver_phone")));
				// 收货人手机号码
				_order.setReceiverPhone(_order.getReceiverTelephone());
				// 收货人地址，记得省 市 地区 详细地址隔开，否则订单推送会失败
				_order.setAddress(JsonCaseUtil.jsonToString(orderInfo.get("province")) + " "
						+ JsonCaseUtil.jsonToString(orderInfo.get("city")) + " "
						+ JsonCaseUtil.jsonToString(orderInfo.get("town")) + " "
						+ JsonCaseUtil.jsonToString(orderInfo.get("address")));
				// ************************* 订单信息
				// ************************************//*
				// 订单金额
				_order.setOrderTotal(JsonCaseUtil.jsonToDouble(orderInfo.get("pay_amount")));
				// 下单账号
				// order.setBuyerAccount(buyerMap.get(order.getOrderNo()));
				// 订单状态
				_order.setOrderStatus("待发货");
				// 订单备注
				_order.setBuyerMessage(JsonCaseUtil.jsonToString(orderInfo.get("remark")));
				// 运费金额
				_order.setLogisticsCost(JsonCaseUtil.jsonToDouble(orderInfo.get("postage")));
				try {
					// 下单时间
					_order.setPaymentDate(
							JsonCaseUtil.jsonStrToDate(orderInfo.get("created_time"), DateUtils.FORMAT_FULL_DATETIME));
				} catch (Exception e) {
					Logger.info("时间格式转换异常", e);
				}
				_order.setUpdateDate(new Date());
				// 判断是否缺少sku
				boolean shortSku = false;
				String sku = null, title = null;
				// 插入商品数据
				List<TaoBaoOrderGoods> itemGoodList = Lists.newArrayList();
				for (JsonNode itemGood : orderInfo.get("item_list")) {
					TaoBaoOrderGoods good = new TaoBaoOrderGoods();
					sku = JsonCaseUtil.jsonToString(itemGood.get("outer_id"));
					title = JsonCaseUtil.jsonToString(itemGood.get("goods_name"));
					if (StringUtils.isEmpty(sku)) {
						shortSku = true;
						Logger.info(title + "缺少外部sku:" + JsonCaseUtil.jsonToString(itemGood.get("sku_id")));
						break;
					}
					good.setSku(sku);
					good.setGoodsTitle(title);
					good.setOrderNo(_order.getOrderNo());
					good.setAmount(JsonCaseUtil.jsonToInteger(itemGood.get("goods_count")));
					good.setPrice(JsonCaseUtil.jsonToDouble(itemGood.get("goods_price")));
					good.setEmail(email);
					itemGoodList.add(good);
				}
				if (!shortSku) {
					goodList.addAll(itemGoodList);
					orderList.add(_order);
				}
			} catch (Exception e) {
				Logger.info("获取订单异常" + e);
			}
		}
		orderListMap.put("suc", true);
		orderListMap.put("orderList", orderList);
		orderListMap.put("good", goodList);
		return orderListMap;
	}

	private static JsonNode queryPddOrderDetail(String orderNo)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Long timeStamp = System.currentTimeMillis();
		Map<String, String> paramMap = Maps.newHashMap();
		paramMap.put("type", SIMPLE_ORDER);
		paramMap.put("mall_id", MALL_ID);
		paramMap.put("data_type", DATA_TYPE);
		paramMap.put("timestamp", timeStamp.toString());
		paramMap.put("order_sn", orderNo);
		paramMap.put("sign", generateSign(paramMap));
		String url = generateUrl(paramMap);
		Logger.info("拉取拼多多平台订单详情请求URL" + url);
		String respone = doPost(url, paramMap, MD5Util.CHARSET_UTF_8, true);
		Logger.info("拼多多订单详情拉取返回值：" + respone);
		return Json.parse(respone);
	}

	private Map<String, Object> queryPddOrderList() {
		Map<String, Object> result = Maps.newHashMap();
		Boolean suc = false;
		String info = null;
		try {
			Integer page = 1;//页数
			Integer page_size = 100;
			Set<String> allOrderSet = Sets.newHashSet();
			allOrderNo(page,page_size,allOrderSet);
			suc = true;
			info = "拉取订单成功";
			result.put("respose", allOrderSet);
			result.put("count", allOrderSet.size());
		} catch (Exception e) {
			Logger.error("订单拉取失败：" + new Date() + " : " + e);
			result.put("suc", false);
			info = "拉取订单异常";
			return null;
		}
		result.put("suc", suc);
		result.put("msg", info);
		return result;
	}

	private Set<String> allOrderNo(Integer page, Integer page_size, Set<String> allOrderSet)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String res = postOrderList(page, page_size);
		// 将返回结果转换为json对象
		JsonNode responseJson = Json.parse(res);
		if (responseJson.has("error_response")) {
			JsonNode errorJson = responseJson.get("error_response");
			Logger.info("拼多多订单拉取出错:[{}]", errorJson);
		} else {
			JsonNode noLists = responseJson.get("order_sn_list_get_response");
			Integer count = JsonCaseUtil.getIntegerValue(noLists, "total_count");
			Set<String> pageSet = Sets.newHashSet();
			pageSet = parseOrderNo(noLists.get("order_sn_list"));
			allOrderSet.addAll(pageSet);
			if (count < page_size) {
				return allOrderSet;
			} else {
				page++;
				allOrderNo(page, page_size, allOrderSet);
			}
		}
		return allOrderSet;
	}

	private Set<String> parseOrderNo(JsonNode jsonNode) {
		Set<String> pageSet = Sets.newHashSet();
		for (JsonNode node : jsonNode) {
			pageSet.add(JsonCaseUtil.getStringValue(node, "order_sn"));
		}
		return pageSet;
	}

	private String postOrderList(Integer page, Integer page_size)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Long timeStamp = System.currentTimeMillis();
		Map<String, String> paramMap = Maps.newHashMap();
		paramMap.put("type", ORDER_LIST);
		paramMap.put("mall_id", MALL_ID);
		paramMap.put("data_type", DATA_TYPE);
		paramMap.put("order_status", ORDER_STATUS);
		paramMap.put("timestamp", timeStamp.toString());
		paramMap.put("page", page.toString());
		paramMap.put("page_size", page_size.toString());
		paramMap.put("sign", generateSign(paramMap));
		String url = generateUrl(paramMap);
		Logger.info("拉取拼多多平台订单请求URL" + url);
		String response = doPost(url, paramMap, MD5Util.CHARSET_UTF_8, true);
		Logger.info("拼多多订单拉取返回值：" + response);
		return response;
	}

	private static String generateUrl(Map<String, String> paramMap) {
		String url = SERVERURL + "?";
		for (String key : paramMap.keySet()) {
			url += key + "=" + paramMap.get(key).toString() + "&";
		}
		if (url.endsWith("&")) {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}

	private static String generateSign(Map<String, String> paramMap)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Map<String, Object> result = new LinkedHashMap<>();
		paramMap.entrySet().stream().sorted(Map.Entry.<String, String> comparingByKey())
				.forEachOrdered(x -> result.put(x.getKey(), x.getValue()));

		String toEnctryStr = SECRET;
		for (String key : result.keySet()) {
			toEnctryStr += key + result.get(key).toString();
		}
		toEnctryStr += SECRET;
		return encoderByMd5(toEnctryStr).toUpperCase();// 加密的字符串
	}

	/**
	 * 利用MD5进行加密
	 * 
	 * @param str
	 *            待加密的字符串
	 * @return 加密后的字符串
	 * @throws NoSuchAlgorithmException
	 *             没有这种产生消息摘要的算法
	 * @throws UnsupportedEncodingException
	 */
	public static String encoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return MD5Util.MD5Encode(str, MD5Util.CHARSET_UTF_8);
	}

	public static String doPost(String url, Map<String, String> params, String charset, boolean pretty) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		HttpMethod method = new PostMethod(url);
		// 设置Http Post数据
		if (params != null) {
			HttpMethodParams p = new HttpMethodParams();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				p.setParameter(entry.getKey(), entry.getValue());
			}
			method.setParams(p);
		}
		try {
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(), charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (IOException e) {
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	@Override
	public void pushPddLogistic(LogisticsEvent event) {
		try {
			List<Integer> shopIds = Lists.transform(event.getSbs(), sb -> sb.getShopId());
			Map<Integer, JsonNode> shops = Maps.newHashMap();
			// 查询所有店铺
			for (Integer shop : Sets.newHashSet(shopIds)) {
				shops.put(shop, httpService.getShopInfo(shop));
			}
			Map<String, SaleMain> salesMap = Maps.uniqueIndex(event.getSms(), sale -> sale.getSalesOrderNo());
			Map<String, OrderPack> packs = Maps.uniqueIndex(event.getOps(), op -> op.getCordernumber());
			Map<Integer, SaleBase> baseMap = Maps.uniqueIndex(event.getSbs(), sale -> sale.getSalesOrderId());
			SaleMain main = null;
			SaleBase base = null;
			OrderPack pack = null;
			JsonNode shopNode = null;
			for (String orderNo : salesMap.keySet()) {
				try {
					main = salesMap.get(orderNo);
					base = baseMap.get(main.getId());
					pack = packs.get(orderNo);
					shopNode = shops.get(base.getShopId());
					if (shopNode == null) {
						Logger.info("查询不到店铺信息【{}】", orderNo);
						continue;
					}
					SECRET = JsonCaseUtil.jsonToString(shopNode.get("clientsecret"));
					String clientid = shopNode.get("clientid").asText();
					if(base.getPlatformType() == 14){
						pushLogistic(clientid, orderNo, base, pack);
					}else if(base.getPlatformType() == 13){
						yzService.pushLogistic(SECRET,clientid,orderNo,base,pack);
					}
				} catch (Exception e) {
					Logger.error("同步PDD物流失败【{}】,{}", orderNo, e);
				}
			}
			// 将返回结果转换为json对象
		} catch (Exception e) {
			Logger.error("同步失败", e);
		}
	}
	
	public void pushLogistic(String clientid,String orderNo,SaleBase base,OrderPack pack) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		Long timeStamp = System.currentTimeMillis();
		Map<String, String> paramMap = Maps.newHashMap();
		paramMap.put("type", LOGISTICS);
		paramMap.put("mall_id", clientid);
		paramMap.put("data_type", DATA_TYPE);
		paramMap.put("order_sn", base.getPlatformOrderNo());

		if (cache.getIfPresent(base.getLogisticsMode()) == null) {
			PddLogistics pddLogisticsByCompanyName = pddLogisticsMapper.findPddLogisticsByCompanyName(base.getLogisticsMode());
			if (pddLogisticsByCompanyName == null) {
				Logger.error("---------------------------------->找不到拼多多对应的快递公司:logisticsMode:【{}】,tracking_number:【{}】", base.getLogisticsMode(), pack.getCtrackingnumber());
				return;
			}
			cache.put(base.getLogisticsMode(), String.valueOf(pddLogisticsByCompanyName.getLogisticsId()));
		}

		paramMap.put("logistics_id", cache.getIfPresent(base.getLogisticsMode()));
		paramMap.put("tracking_number", pack.getCtrackingnumber());
		paramMap.put("timestamp", timeStamp.toString());
		paramMap.put("sign", generateSign(paramMap));
		String url = generateUrl(paramMap);
		Logger.info("【{}】推送物流信息到拼多多平台订单.", orderNo);
		String respone = doPost(url, paramMap, MD5Util.CHARSET_UTF_8, true);
		Logger.info("推送物流信息到拼多多平台订单返回值【{}】", respone);
	}
}
