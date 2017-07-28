package services.sales.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.eventbus.EventBus;
import events.sales.ImportOrderSyncEvent;
import org.apache.http.HttpResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.youzan.open.sdk.client.auth.Sign;
import com.youzan.open.sdk.client.core.DefaultYZClient;
import com.youzan.open.sdk.client.core.YZClient;
import com.youzan.open.sdk.gen.v3_0_0.api.YouzanLogisticsExpressGet;
import com.youzan.open.sdk.gen.v3_0_0.api.YouzanLogisticsOnlineConfirm;
import com.youzan.open.sdk.gen.v3_0_0.api.YouzanTradesSoldOuterGet;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanLogisticsExpressGetParams;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanLogisticsExpressGetResult;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanLogisticsExpressGetResult.LogisticsExpressOpenApiModel;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanLogisticsOnlineConfirmParams;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanLogisticsOnlineConfirmResult;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanLogisticsOnlineConfirmResult.Shipping;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanTradesSoldOuterGetParams;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanTradesSoldOuterGetResult;
import com.youzan.open.sdk.gen.v3_0_0.model.YouzanTradesSoldOuterGetResult.TradeDetail;

import entity.platform.order.template.TaoBaoOrder;
import entity.platform.order.template.TaoBaoOrderGoods;
import entity.sales.OrderPack;
import entity.sales.SaleBase;
import mapper.sales.TaoBaoOrderGoodsMapper;
import mapper.sales.TaoBaoOrderMapper;
import play.Logger;
import play.libs.Json;
import services.sales.IJdService;
import services.sales.IYZService;
import util.sales.Constant;
import util.sales.DateUtils;
import util.sales.JsonCaseUtil;
import util.sales.KdtApiClient;

/**
 * @author zbc
 *
 */
public class YZService implements IYZService {
	@Inject private TaoBaoOrderMapper taoBaoOrderMapper;
	@Inject private TaoBaoOrderGoodsMapper taoBaoOrderGoodsMapper;
	@Inject private IJdService jdService;

	@Inject
	private EventBus eventBus;

	private static Integer PAGESIZE = 100;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> pullOrder(JsonNode main,String email) {
		Map<String, Object> result = Maps.newHashMap();
		Integer shopId = main.get("shopId").asInt();
		JsonNode shopNode = jdService.checkShop(shopId);
		if (!jdService.isRightful(shopNode, "shopAccount", "clientid", "clientsecret")) {
			Logger.info("YZ店铺信息【{}】", shopNode);
			result.put("suc", false);
			result.put("msg", "店铺信息不完整，请重新确认。");
			return result;
		}
		if (email == null) {
			email = JsonCaseUtil.jsonToString(shopNode.get("email"));
		}
		boolean isSync = jdService.syncOrder(result, shopId, email);
		if(isSync){
			result.put("suc",false);
			result.put("msg", "该店铺正在同步订单");
			return result;
		}
		List<TaoBaoOrder> orderList = Lists.newArrayList();
		List<TaoBaoOrderGoods> goodList = Lists.newArrayList();
		Map<String, Object> orderListMap = getOrderList(main,shopNode);
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
		taoBaoOrderMapper.batchInsert(orderList);
		// 批量插入商品
		taoBaoOrderGoodsMapper.batchInsert(goodList);

		//补全信息
		eventBus.post(new ImportOrderSyncEvent(email));

		result.put("suc", true);
		result.put("msg", "拉取有赞订单成功,共插入【" + orderList.size() + "】条");
		jdService.cancelSync(shopId);
		return result;
	}

	/**
	 * 转换为订单 与商品集合
	 * @param orderList
	 * @param goodList
	 * @param shopName
	 * @param email
	 * @param trades
	 */
	private void parseList(List<TaoBaoOrder> orderList, List<TaoBaoOrderGoods> goodList, String shopName, String email,
						   JsonNode trades) {
		for (JsonNode trade : trades) {
			TaoBaoOrder order = new TaoBaoOrder();
			// 分销商账号
			order.setEmail(email);
			// 订单编号
			order.setOrderNo(trade.get("tid").asText());
			TaoBaoOrder exitOrder = taoBaoOrderMapper.selectBygroube(order);
			if (exitOrder != null) {
				Logger.info("该订单:[{}]已经存在", order.getOrderNo());
				// 跳过循环
				continue;
			}
			order.setShopName(shopName);
			order.setOrderStatus(Constant.YZ_ORDER_STATE.get(trade.get("status").asText()));
			order.setLogisticsCost(trade.get("post_fee").asDouble());
			order.setOrderTotal(trade.get("payment").asDouble());
			order.setReceiverName(trade.get("receiver_name").asText());
			// 省市县地区要空格隔开，否则推送订单会失败
			order.setAddress(trade.get("receiver_state").asText() + " " + trade.get("receiver_city").asText() + " "
					+ trade.get("receiver_district").asText() + " " + trade.get("receiver_address").asText());
			order.setPostCode(trade.get("receiver_zip").asText());
			order.setReceiverPhone(trade.get("receiver_mobile").asText());
			order.setBuyerMessage(trade.get("buyer_message").asText());
			//TODO 缺少支付流水号
			// 下单账号
			order.setBuyerAccount(trade.get("buyer_nick").asText());
			try {
				// 下单时间
				order.setPaymentDate(
						DateUtils.string2date(trade.get("created").asText(), DateUtils.FORMAT_FULL_DATETIME));
			} catch (ParseException e) {
				Logger.info("时间格式转换异常", e);
			}
			order.setUpdateDate(new Date());
			orderList.add(order);
			JsonNode pros = trade.get("orders");
			for (JsonNode pro : pros) {
				TaoBaoOrderGoods good = new TaoBaoOrderGoods();
				good.setSku(pro.get("outer_item_id").asText());
				good.setGoodsTitle(pro.get("title").asText());
				good.setOrderNo(order.getOrderNo());
				good.setEmail(email);
				good.setAmount(pro.get("num").asInt());
				good.setPrice(pro.get("price").asDouble());;
				goodList.add(good);
			}
		}
	}

	public Map<String, Object> getOrderList(JsonNode main, JsonNode shopNode) {
		Map<String, Object> result = Maps.newHashMap();
		List<TaoBaoOrder> orderList = Lists.newArrayList();
		List<TaoBaoOrderGoods> goodList = Lists.newArrayList();
		try {
			String outer_user_id = JsonCaseUtil.jsonToString(shopNode.get("shopAccount"));
			String clientid = JsonCaseUtil.jsonToString(shopNode.get("clientid"));
			String clientsecret = JsonCaseUtil.jsonToString(shopNode.get("clientsecret"));
			String shopName = JsonCaseUtil.jsonToString(shopNode.get("shopName"));
			String email = JsonCaseUtil.jsonToString(shopNode.get("email"));

			Integer pageNo = 1;
			Map<String, String> params = Maps.newHashMap();
			// params.put("fields", "has_next");
			params.put("outer_user_id", outer_user_id);// 店铺账号
			params.put("outer_type", "");// 三方用户ua,该字段必传，暂时设置为空
			params.put("status", "WAIT_SELLER_SEND_GOODS");
			params.put("start_created", main.get("start_created").asText());
			params.put("end_created", main.get("end_created").asText());
			params.put("page_no", String.valueOf(pageNo));
			params.put("page_size", String.valueOf(PAGESIZE));

			Map<String,Object> resMap = responseJson(result, clientid, clientsecret, params);
			if(!(boolean)resMap.get("suc")){
				return resMap;
			}
			JsonNode resposeJson = Json.toJson(resMap.get("respose"));
			Integer total_results = resposeJson.get("response").get("total_results").asInt();
			Logger.info("获取订单总数:【" + total_results + "】");
			JsonNode trades = resposeJson.get("response").get("trades");
			parseList(orderList, goodList, shopName, email, trades);
			//循环请求获取订单数据
			while(total_results > pageNo*PAGESIZE){
				pageNo += 1;
				params.put("page_no",pageNo + "");
				resMap =  responseJson(result, clientid, clientsecret, params);
				resposeJson = Json.toJson(resMap.get("respose"));
				trades = resposeJson.get("response").get("trades");
				parseList(orderList, goodList, shopName, email, trades);
			}
			result.put("suc", true);
			result.put("orderList",orderList);
			result.put("good",goodList);
		} catch (Exception e) {
			result.put("suc", false);
			result.put("msg", "获取有赞订单异常");
			Logger.error("获取有赞订单异常", e);
		}
		return result;
	}

	/**
	 * 获取订单数据
	 * @param result
	 * @param clientid
	 * @param clientsecret
	 * @param params
	 * @return
	 * @throws Exception
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	private Map<String,Object> responseJson(Map<String, Object> result, String clientid, String clientsecret,
											Map<String, String> params) throws Exception, IOException, JsonProcessingException {
		KdtApiClient client;

		client = new KdtApiClient(clientid, clientsecret);

		HttpResponse response = client.get("kdt.trades.sold.getforouter", params);
		Logger.info("有赞Response Code :" + response.getStatusLine().getStatusCode());
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));
		StringBuffer res = new StringBuffer();
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			res.append(line);
		}
		String respose = res.toString();
		ObjectMapper map = new ObjectMapper();
		// 将返回结果转换为json对象
		JsonNode responseJson = map.readTree(respose);
		if (responseJson.has("error_response")) {
			JsonNode errorJson = responseJson.get("error_response");
			Logger.info("有赞订单拉取出错:[{}]", errorJson);
			result.put("suc", false);
			result.put("msg", errorJson.get("msg"));
			return result;
		}
		result.put("suc", true);
		result.put("respose",responseJson);
		return result;
	}
	
	
	public static void main(String[] args) {
		/*String appKey = "3264f64c0a6f2653bb";
		String appSecret = "07c3226e9d001ebe197d61f89502e06b";
		YZClient client = new  DefaultYZClient(new Sign(appKey,appSecret)); 
		Shipping logisticsOnlineConfirm = logisticsOnlineConfirm(client, "E20170615094205078949869", Constant.YZ_LOGISTICS.get("JYT"), "885426703873973402");
		System.out.println(Json.toJson(logisticsOnlineConfirm));*/
	}
	
	/**
	 * 确认收货
	 * @author zbc
	 * @since 2017年5月10日 下午5:07:01
	 */
	/**
	 * @author zbc
	 * @since 2017年5月10日 下午6:29:38
	 * @param tid       交易订单号
	 * @param outStype  物流公司id
	 * @param outSid    快递单号
	 * @return
	 */
	public static Shipping logisticsOnlineConfirm(YZClient client,String tid,String outStype,String outSid){
		YouzanLogisticsOnlineConfirmParams youzanLogisticsOnlineConfirmParams = new YouzanLogisticsOnlineConfirmParams();
		youzanLogisticsOnlineConfirmParams.setTid(tid); //交易订单号
		youzanLogisticsOnlineConfirmParams.setOutStype(outStype);//物流公司id，通过youzan.logistics.express.get此接口可以拿到
		youzanLogisticsOnlineConfirmParams.setOutSid(outSid);//快递单号（具体一个物流公司的真实快递单号）
		YouzanLogisticsOnlineConfirm youzanLogisticsOnlineConfirm = new YouzanLogisticsOnlineConfirm();
		youzanLogisticsOnlineConfirm.setAPIParams(youzanLogisticsOnlineConfirmParams);
		YouzanLogisticsOnlineConfirmResult result = client.invoke(youzanLogisticsOnlineConfirm);
		Logger.info("推送物流信息到拼多多平台订单返回值【{}】", Json.toJson(result));
		return result.getShipping();
	}
	
	/**
	 * 获取所有物流方式测试
	 * @author zbc
	 * @since 2017年5月10日 下午5:06:24
	 * @param client
	 * @return
	 */
	public static LogisticsExpressOpenApiModel[] getAllExpress(YZClient client ){
		YouzanLogisticsExpressGetParams youzanLogisticsExpressGetParams  = new YouzanLogisticsExpressGetParams();
		YouzanLogisticsExpressGet youzanLogisticsExpressGet = new YouzanLogisticsExpressGet();
		youzanLogisticsExpressGet.setAPIParams(youzanLogisticsExpressGetParams);
		YouzanLogisticsExpressGetResult result = client.invoke(youzanLogisticsExpressGet);
		return result.getAllExpress();
	}
	
	/**
	 * 获取订单测试
	 * @author zbc
	 * @since 2017年5月10日 下午5:03:11
	 * @param client
	 * @return
	 */
	public static TradeDetail[] getOrders(YZClient client){
		YouzanTradesSoldOuterGetParams youzanTradesSoldOuterGetParams = new YouzanTradesSoldOuterGetParams();
		youzanTradesSoldOuterGetParams.setPageNo((long)1);
		youzanTradesSoldOuterGetParams.setPageSize((long)10);
		youzanTradesSoldOuterGetParams.setOuterUserId("18676697260");
		youzanTradesSoldOuterGetParams.setOuterType("");
		YouzanTradesSoldOuterGet youzanTradesSoldOuterGet = new YouzanTradesSoldOuterGet();
		youzanTradesSoldOuterGet.setAPIParams(youzanTradesSoldOuterGetParams);
		YouzanTradesSoldOuterGetResult result = client.invoke(youzanTradesSoldOuterGet);
		return result.getTrades();
	}

	public void pushLogistic(String secret, String clientid, String orderNo, SaleBase base, OrderPack pack) {
		YZClient client = new  DefaultYZClient(new Sign(clientid,secret)); 
		Logger.info("【{}】推送物流信息到有赞平台订单", orderNo);
		logisticsOnlineConfirm(client, base.getPlatformOrderNo(), Constant.YZ_LOGISTICS.get(base.getLogisticsTypeCode()), pack.getCtrackingnumber());
	}
}
