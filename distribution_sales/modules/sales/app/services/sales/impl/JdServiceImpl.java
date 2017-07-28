package services.sales.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import dto.sales.JdLogisticSendRequest;
import dto.sales.JdLogisticSendResponse;
import entity.sales.OrderPack;
import entity.sales.SaleBase;
import entity.sales.SaleMain;
import events.sales.ImportOrderSyncEvent;
import events.sales.JdLogisticsEvent;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.domain.order.ItemInfo;
import com.jd.open.api.sdk.domain.order.OrderResult;
import com.jd.open.api.sdk.domain.order.OrderSearchInfo;
import com.jd.open.api.sdk.domain.order.UserInfo;
import com.jd.open.api.sdk.domain.ware.Sku;
import com.jd.open.api.sdk.domain.ware.Ware;
import com.jd.open.api.sdk.request.order.OrderSearchRequest;
import com.jd.open.api.sdk.request.ware.WareListRequest;
import com.jd.open.api.sdk.request.ware.WareListingGetRequest;
import com.jd.open.api.sdk.response.order.OrderSearchResponse;
import com.jd.open.api.sdk.response.ware.WareListResponse;
import com.jd.open.api.sdk.response.ware.WareListingGetResponse;

import entity.platform.order.template.TaoBaoOrder;
import entity.platform.order.template.TaoBaoOrderGoods;
import entity.sales.OrderTimeConfig;
import mapper.sales.OrderTimeConfigMapper;
import mapper.sales.TaoBaoOrderGoodsMapper;
import mapper.sales.TaoBaoOrderMapper;
import play.Logger;
import play.libs.Json;
import services.sales.IHttpService;
import services.sales.IJdService;
import util.sales.Constant;
import util.sales.DateUtils;
import util.sales.JsonCaseUtil;

public class JdServiceImpl implements IJdService {
	
	@Inject	private IHttpService httpService;
	@Inject	private TaoBaoOrderMapper taoBaoOrderMapper;
	@Inject	private TaoBaoOrderGoodsMapper taoBaoOrderGoodsMapper; 
	@Inject	private OrderTimeConfigMapper  orderTimeConfigMapper;

	@Inject
	private EventBus eventBus;
	
	// 服务地址
	private static String SERVER_URL = "https://api.jd.com/routerjson";
	// 订单状态 等待境外出库
//	private static String ORDER_STATE = "WAIT_SELLER_DELIVERY";
	//TODO 上线要替换
	private static String ORDER_STATE = "WAIT_SELLER_STOCK_OUT";
	// pageSize
	private static Integer PAGESIZE = 100;
	//获取买家ID参数
	private static String GET_BUYER = "pin";
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Object> pullOrder(JsonNode main,String email) {
		Map<String,Object> res = Maps.newHashMap();
		Integer shopId = main.get("shopId").asInt();
		JsonNode shopNode = checkShop(shopId);
		if (!isRightful(shopNode, "accesstoken", "clientid", "clientsecret")) {
			Logger.info("JD店铺信息【{}】", shopNode);
			res.put("suc", false);
			res.put("msg", "店铺信息不完整，请重新确认。");
			return res;
		}
		if(email == null){
			email = JsonCaseUtil.jsonToString(shopNode.get("email"));
		}
		String shopName = JsonCaseUtil.jsonToString(shopNode.get("shopName"));
		boolean isSync = syncOrder(res, shopId, email);
		if(isSync){
			res.put("suc",false);
			res.put("msg", "该店铺正在同步订单");
			return res;
		}
		Map<String,Object> orderInfosMap = getOrderInfo(main,shopNode,null);
		List<OrderSearchInfo>  orderInfos = Lists.newArrayList();
		if(!(boolean)orderInfosMap.get("suc")){
			cancelSync(shopId);
			return orderInfosMap;
		}
		
		orderInfos = (List<OrderSearchInfo>)orderInfosMap.get("list");
		if(orderInfos.size() == 0){
			res.put("suc",false);
			res.put("msg", "没有拉取到订单数据");
			//取消同步
			cancelSync(shopId);
			return res;
		}
		List<Sku> skus = this.getSkus(shopNode, orderInfos);
		if(skus.size() == 0){
			res.put("suc",false);
			res.put("msg", "没有拉去到商品数据");
			cancelSync(shopId);
			return res;
		}
		Map<Long, String> skusMap = Maps.newHashMap();
		for(Sku sku:skus){
			skusMap.put(sku.getSkuId(), sku.getOuterId());
		}
		Map<String,Object> buyerIdMap = this.getOrderInfo(main,shopNode,GET_BUYER);
		List<OrderSearchInfo> buyerIds   = (List<OrderSearchInfo>)buyerIdMap.get("list");
		Map<String, String> buyerMap =  buyerIds.stream().collect(
	            Collectors.toMap(OrderSearchInfo::getOrderId, OrderSearchInfo::getPin)); 
		List<TaoBaoOrder> orderList = Lists.newArrayList();
		List<TaoBaoOrderGoods> goodList = Lists.newArrayList();
		//循环订单数据
		for(OrderSearchInfo orderInfo:orderInfos){
			TaoBaoOrder order = new TaoBaoOrder();
			// 分销商账号
			order.setEmail(email);
			// 订单编号
			order.setOrderNo(orderInfo.getOrderId());
			TaoBaoOrder exitOrder = taoBaoOrderMapper.selectBygroube(order);
			if (exitOrder != null) {
				Logger.info("该订单:[{}]已经存在", order.getOrderNo());
				// 跳过循环
				continue;
			}
			// 店铺名称
			order.setShopName(shopName);

			//平台类型 2017-5-16
			order.setPlateform(4);

			/************************ 收货人信息 ******************************/
			// 收货信息
			UserInfo receiverInfo = orderInfo.getConsigneeInfo();
			// 收货人姓名
			order.setReceiverName(receiverInfo.getFullname());
			// 收货人电话
			order.setReceiverTelephone(receiverInfo.getTelephone());
			// 收货人手机号码
			order.setReceiverPhone(receiverInfo.getMobile());
			// 收货人地址，记得省 市 地区 详细地址隔开，否则订单推送会失败
			order.setAddress(receiverInfo.getProvince() + " "
					+ receiverInfo.getCity() + " " + receiverInfo.getCounty()
					+ " " + receiverInfo.getFullAddress());
			/************************* 订单信息 ************************************/
			// 订单金额
			order.setOrderTotal(Double.valueOf(orderInfo.getOrderPayment()));
			// 下单账号
			order.setBuyerAccount(buyerMap.get(order.getOrderNo()));
			// 订单状态
			order.setOrderStatus(Constant.JD_ORDER_STATE.get(orderInfo
					.getOrderState()));
			// 订单备注
			order.setBuyerMessage(orderInfo.getOrderRemark());
			// 运费金额
			order.setLogisticsCost(Double.valueOf(orderInfo.getFreightPrice()));
			try {
				// 下单时间
				order.setPaymentDate(orderInfo.getOrderStartTime() != null ? DateUtils
						.string2date(orderInfo.getOrderStartTime(),
								DateUtils.FORMAT_FULL_DATETIME) : null);
			} catch (ParseException e) {
				Logger.info("时间格式转换异常", e);
			}
			order.setUpdateDate(new Date());
			// 判断是否缺少sku
			boolean shortSku = false;
			// 插入商品数据
			List<ItemInfo> itemInfos = orderInfo.getItemInfoList();
			List<TaoBaoOrderGoods> itemGoodList = Lists.newArrayList();
			for (ItemInfo item : itemInfos) {
				TaoBaoOrderGoods good = new TaoBaoOrderGoods();
				if (StringUtils.isEmpty(skusMap.get(Long.valueOf(item
						.getSkuId())))) {
					shortSku = true;
					Logger.info(item.getSkuName() + "缺少外部sku:"
							+ item.getSkuId());
					continue;
				}
				good.setSku(skusMap.get(Long.valueOf(item.getSkuId())));
				good.setGoodsTitle(item.getSkuName());
				good.setOrderNo(order.getOrderNo());
				good.setAmount(Integer.valueOf(item.getItemTotal()));
				good.setPrice(Double.valueOf(item.getJdPrice()));
				good.setEmail(email);
				itemGoodList.add(good);
			}
			if (!shortSku) {
				goodList.addAll(itemGoodList);
				orderList.add(order);
			}
		}
		//批量插入订单
		taoBaoOrderMapper.batchInsert(orderList);
		//批量插入商品
		taoBaoOrderGoodsMapper.batchInsert(goodList);

		//补全信息
		eventBus.post(new ImportOrderSyncEvent(email));

		res.put("suc",true);
		res.put("msg","拉取订单成功,共插入【"+orderList.size()+"】条");
		cancelSync(shopId);
		return res;
	}
	
	/**
	 * 取消同步
	 * @param shopId
	 */
	@Override
	public void cancelSync(Integer shopId) {
		OrderTimeConfig cfg;
		cfg = orderTimeConfigMapper.selectByShopId(shopId);
		cfg.setIsSync(false);
		cfg.setEndTime(new Date());
		orderTimeConfigMapper.updateByParam(cfg);
	}
	
	/**
	 * 判断是否同步中
	 * @param res
	 * @param shopId
	 * @param email
	 * @param cfg
	 * @return
	 */
	@Override
	public boolean syncOrder(Map<String, Object> res, Integer shopId, String email) {
		OrderTimeConfig cfg = orderTimeConfigMapper.selectByShopId(shopId);
		if(cfg == null ){
			//创建为同步状态
			OrderTimeConfig newCongfig = new OrderTimeConfig();
			newCongfig.setEmail(email);
			newCongfig.setShopId(shopId);
			newCongfig.setIsSync(true);
			newCongfig.setOrderStatus(ORDER_STATE);
			//开始时间 
			newCongfig.setStartTime(new Date());
			orderTimeConfigMapper.insertSelective(newCongfig);
		}else{
			if(cfg.getIsSync()){
				return true;
			}else{
				//更新为同步
				OrderTimeConfig modCongfig = new OrderTimeConfig();
				modCongfig.setShopId(shopId);
				modCongfig.setIsSync(true);
				modCongfig.setStartTime(new Date());
				modCongfig.setOrderStatus(ORDER_STATE);
				orderTimeConfigMapper.updateByParam(modCongfig);
			}
		}
		return false;
	}
	
	@Override
	public Map<String,Object> getOrderInfo(JsonNode main,JsonNode shopNode,String optionalFields){
		Map<String,Object> resMap = Maps.newHashMap();
		//授权码
		String access_token = JsonCaseUtil.jsonToString(shopNode.get("accesstoken"));
		//应用 app_key
		String app_key = JsonCaseUtil.jsonToString(shopNode.get("clientid"));
		//应用app_secret
		String app_secret = JsonCaseUtil.jsonToString(shopNode.get("clientsecret"));
		//开始时间 
		String startDate = JsonCaseUtil.jsonToString(main.get("start_date"));
		//结束时间
		String endDate = JsonCaseUtil.jsonToString(main.get("end_date"));
		Integer page = 1;
		List<OrderSearchInfo> orderInfo = Lists.newArrayList();
		try {
			JdClient client = new DefaultJdClient(SERVER_URL,access_token,app_key,app_secret);
			OrderSearchRequest request = new OrderSearchRequest();
			request.setStartDate(startDate);
			request.setEndDate(endDate);
			request.setOrderState(ORDER_STATE);
			request.setPage(page + "");
			request.setPageSize(PAGESIZE + "");
			request.setDateType("1");
			//需返回的字段列表
			if(optionalFields != null){
				request.setOptionalFields(optionalFields);
			}
			OrderSearchResponse response = client.execute(request);
			OrderResult result = response.getOrderInfoResult();
			//access_token 已过期
			if(!"0".equals(response.getCode()) ){
				resMap.put("suc", false);
				resMap.put("msg", response.getZhDesc());
				Logger.info(response.getZhDesc());
				return resMap;
			}
			Integer total = result.getOrderTotal();
			orderInfo.addAll(result.getOrderInfoList());
			//根据总数 循环查询
			while(total > page*PAGESIZE){
				page += 1;
				request.setPage(page + "");
				request.setPageSize(PAGESIZE + "");
				OrderSearchResponse res = client.execute(request);
				result = res.getOrderInfoResult();
				orderInfo.addAll(result.getOrderInfoList()) ;
			}
			Logger.info("返回订单:" + Json.toJson(orderInfo));
		} catch (JdException e) {
			Logger.info("拉取订单失败：" + e);
		}
		resMap.put("suc", true);
		resMap.put("list", orderInfo);
		return resMap;
	}
	
	@Override
	public List<Ware> pullProduct(JsonNode main) {
		//授权吗
		String access_token = main.get("access_token").asText();
		//应用 app_key
		String app_key = main.get("app_key").asText();
		//应用app_secret
		String app_secret = main.get("app_secret").asText();
		List<Ware> wares = Lists.newArrayList();
		try {
			Integer page = 1;
			JdClient client=new DefaultJdClient(SERVER_URL,access_token,app_key, app_secret); 
			WareListingGetRequest request = new WareListingGetRequest();
			request.setPage(page + "");
			request.setPageSize(PAGESIZE + "");
			WareListingGetResponse response=client.execute(request);
			//access_token 已过期
			if(!"0".equals(response.getCode()) ){
				return wares;
			}
			wares.addAll(response.getWareInfos());
			Integer total = response.getTotal();
			//根据总数 循环查询
			while(total > page*PAGESIZE){
				page += 1;
				request.setPage(page + "");
				request.setPageSize(PAGESIZE + "");
				WareListingGetResponse res=client.execute(request);
				List<Ware>  ware = res.getWareInfos();
				wares.addAll(ware);
			}
//			Logger.info("查询到商品的数据:" + Json.toJson(wares)); 
		} catch (JdException e) {
			Logger.info("拉取商品失败：" + e);
		}
		return wares;
	}
	
	@Override
	public List<Sku> getSkus(JsonNode shopNode,List<OrderSearchInfo> orderInfos){
		// 授权码
		String access_token = JsonCaseUtil.jsonToString(shopNode.get("accesstoken"));
		// 应用 app_key
		String app_key = JsonCaseUtil.jsonToString(shopNode.get("clientid"));
		// 应用app_secret
		String app_secret = JsonCaseUtil.jsonToString(shopNode.get("clientsecret"));
		List<ItemInfo> items = Lists.newArrayList();
		for(OrderSearchInfo info:orderInfos ){
			items.addAll(info.getItemInfoList());
		}
		List<Ware> wares = Lists.newArrayList();
		List<Sku> skus = Lists.newArrayList(); 
		try {
			JdClient client = new DefaultJdClient(SERVER_URL,access_token,app_key, app_secret); 
			List<String> waresIdsList = Lists.newArrayList();
			String wareIds = ""; 
			int count = 0;
			for(ItemInfo item:items){
				wareIds +=  item.getWareId()+","; 
				count += 1;
				//多个wareId拼接 可多个请求，可减少请求次数，节省时间
				if(count%10 == 0 || count == items.size()){
					waresIdsList.add(wareIds);
					wareIds = "";
				}
			}
			for(String ids :waresIdsList){
				WareListRequest request=new WareListRequest();
				request.setWareIds(ids);
				WareListResponse  response=client.execute(request);
				if(!"0".equals(response.getCode())){
					continue;
				}else{
					wares.addAll(response.getWareList());
				}
			}
			for(Ware w:wares){
				skus.addAll(w.getSkus());
			}
		} catch (JdException e) {
			Logger.info("获取京东skus失败",e);
		}
		Logger.info("获取京东skus数据:"+Json.toJson(skus));
		return skus;
	}

	@Override
	public JsonNode checkShop(Integer shopId) {
		try {
			return httpService.getShopInfo(shopId);
		} catch (Exception e) {
			Logger.error("查询店铺失败：",e);
			return null;
		}
	}

	@Override
	public boolean isRightful(JsonNode shopNode, String... field) {
		if (null == shopNode || field == null) {
			return false;
		}
		boolean flag = true;
		for (String str : field) {
			if (JsonCaseUtil.isJsonEmpty(shopNode.get(str))) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	@Override
	public void pushLogistic(JdLogisticsEvent jdLogisticsEvent) {
		try {
			List<Integer> shopIds = Lists.transform(jdLogisticsEvent.getSbs(), sb -> sb.getShopId());
			Map<Integer, JsonNode> shops = Maps.newHashMap();
			// 查询所有店铺
			for (Integer shop : Sets.newHashSet(shopIds)) {
				shops.put(shop, httpService.getShopInfo(shop));
			}
			Map<String, SaleMain> salesMap = Maps.uniqueIndex(jdLogisticsEvent.getSms(), sale -> sale.getSalesOrderNo());
			Map<String, OrderPack> packs = Maps.uniqueIndex(jdLogisticsEvent.getOps(), op -> op.getCordernumber());
			Map<Integer, SaleBase> baseMap = Maps.uniqueIndex(jdLogisticsEvent.getSbs(), sale -> sale.getSalesOrderId());
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

					//授权码
					String access_token = JsonCaseUtil.jsonToString(shopNode.get("accesstoken"));

					//应用 app_key
					String app_key = JsonCaseUtil.jsonToString(shopNode.get("clientid"));

					//应用app_secret
					String app_secret = JsonCaseUtil.jsonToString(shopNode.get("clientsecret"));

					if(base.getPlatformType().intValue() == 4){
						pushJdLogistic(access_token, app_key, app_secret,pack);
					}
				} catch (Exception e) {
					Logger.error("同步京东物流失败【{}】,{}", orderNo, e);
				}
			}
			// 将返回结果转换为json对象
		} catch (Exception e) {
			Logger.error("同步失败", e);
		}
	}

	private void pushJdLogistic(String access_token, String app_key, String app_secret, OrderPack pack) {
		JdClient client = new DefaultJdClient(SERVER_URL,access_token,app_key,app_secret);

		JdLogisticSendRequest request = new JdLogisticSendRequest();
		request.setShipmentNumber(pack.getCordernumber());

		try {
			JdLogisticSendResponse response = client.execute(request);
			Logger.info("推送物流到京东平台返回结果：【{}】", response);
		} catch (JdException e) {
			Logger.error("推送物流到京东平台发生错误：【{}】", e);
		}
	}
}
