package service.discart.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.discart.CartInfoDto;
import dto.discart.CartSaleDto;
import dto.discart.DeliveryInfoDto;
import dto.discart.DisCartDto;
import dto.discart.JsonResult;
import dto.discart.OrderDetail;
import dto.discart.ProSearch;
import dto.discart.PurchaseSaleDetailDto;
import dto.discart.PurchaseSaleDto;
import dto.discart.SkuWarehouseIdQty;
import entity.discart.DisCart;
import entity.discart.DisCartItem;
import mapper.discart.DisCartItemMapper;
import mapper.discart.DisCartMapper;
import play.Logger;
import play.libs.Json;
import service.discart.IDisCartItemService;
import service.discart.IDisCartService;
import service.discart.IHttpService;
import services.base.utils.JsonFormatUtils;
import session.ISessionService;
import utils.discart.IDUtils;
import utils.discart.JsonCaseUtil;
import utils.discart.KeyUtil;
import utils.discart.PriceFormatUtil;
import utils.discart.Types;

/**
 * Created by LSL on 2015/12/7.
 */
public class DisCartService implements IDisCartService {

    @Inject
    private DisCartMapper disCartMapper;
    
    @Inject 
    private DisCartItemMapper cartItemMapper;
    
    @Inject
    private IHttpService httpService;
    
    @Inject
    private IDisCartItemService cartItemService;
    
    @Inject
    private ISessionService sessionService;

    @Override
    public DisCart getUsableDisCart(String email) {
        DisCart cart = disCartMapper.getUsableDisCart(email);
        if (cart == null) {
            return createUsableDisCart(email);
        }
        return cart;
    }

    @Override
    public DisCart createUsableDisCart(String email) {
        DisCart cart = new DisCart();
        cart.setEmail(email);
        cart.setUseable(1);
        int line = disCartMapper.insert(cart);
        Logger.debug("createUsableDisCart line-->" + line);
        return line == 1 ? cart : null;
    }

	@Override
	public Map<String, Object> saveActiveInfo(JsonNode node,String email) {
		Map<String, Object> res = Maps.newHashMap();
		DisCart disCart = disCartMapper.getUsableDisCart(email);
		if(disCart != null) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("cartid", disCart.getId());
			Map<String, Object> exsit = disCartMapper.getActiveInfo(disCart.getId());
			map.put("activePlvg", node.get("activePlvg").asText());
			if(exsit != null) {
				disCartMapper.updateActiveInfo(map);
			} else {
				disCartMapper.insertActiveInfo(map);
			}
			res.put("suc", true);
		}
		return res;
	}

	@Override
	public Map<String, Object> getActive(Integer id) {
		Map<String, Object> result = disCartMapper.getActiveInfo(id);
		return result;
	}

	@Override
	public int deleteActiveInfo(String email) {
		if(StringUtils.isEmpty(email)) {
			return 0;
		}
		return disCartMapper.deleteActiveInfo(email);
	}
	
	/**
	 * 获取部分数据
	 * @param productNode ProductLite对象的json串
	 * @return {productName:xxx,sku:xxx,warehouseId:1,warehouseName:深圳仓}
	 */
	private ObjectNode getProductSimpleInfo(JsonNode productNode){
		ObjectNode newObject = Json.newObject();
		newObject.put("productName", productNode.get("ctitle").asText());
		newObject.put("sku", productNode.get("csku").asText());
		newObject.put("warehouseId", productNode.get("warehouseId").asInt());
		newObject.put("warehouseName", productNode.get("warehouseName").asText());
		return newObject;
	}
	
	@Override
	public Map<String, Object> pushCart(String email, Integer distributionMode, JsonNode proArrayNode) {
		// 查询是否存在购物车，没有则创建
		DisCart cart = getUsableDisCart(email);
		
		// 参数解析
		List<SkuWarehouseIdQty> proList = Lists.newArrayList();
		Map<String, Integer> skuWarehouseId2Qty = Maps.newHashMap();
		for (Iterator<JsonNode> it = proArrayNode.get("proArray").iterator(); it.hasNext();) {
			JsonNode proNode = it.next();
			proList.add(new SkuWarehouseIdQty(proNode.get("sku").asText(), proNode.get("warehouseId").asInt(),
					proNode.get("pQty").asInt()));
			skuWarehouseId2Qty.put(KeyUtil.getKey(proNode.get("sku").asText(), proNode.get("warehouseId").asInt()),
					proNode.get("pQty").asInt());
		}
		
		// 根据仓库分组
		List<ProSearch> proSearchList = Lists.newArrayList();
		Map<Integer, List<SkuWarehouseIdQty>> proListByWarehouseId = proList.stream().collect(Collectors.groupingBy(SkuWarehouseIdQty::getWarehouseId));
		for (Map.Entry<Integer, List<SkuWarehouseIdQty>> entry : proListByWarehouseId.entrySet()) {
			Integer warehouseId = entry.getKey();
			List<SkuWarehouseIdQty> tempProList = entry.getValue();
			List<String> skuList = Lists.transform(tempProList, pro -> pro.getSku());
			proSearchList.add(new ProSearch(skuList, warehouseId, distributionMode, email));
		}
		Logger.info("添加商品至购物车：解析出来的参数为proSearchList===>{}",proSearchList);
		
		// 循环多个查询条件，来查询商品
		Map<String, JsonNode> skuWarehouseId2ProductNode = Maps.newHashMap();
		try {
			for (ProSearch search : proSearchList) {
				JsonNode productsNode = httpService.fgetProducts(search);
				if(productsNode == null || productsNode.get("data").get("result").size()==0){
					return pushCartResult(false, "添加商品失败", null);
				}
				
				// 对查询结果拆分开来处理：sku_warehouseId = productNode
				JsonNode resultNode = productsNode.get("data").get("result");
				for(Iterator<JsonNode> it = resultNode.iterator();it.hasNext();){
					JsonNode productNode = it.next();
					skuWarehouseId2ProductNode.put(KeyUtil.getKey(productNode.get("csku").asText(), productNode.get("warehouseId").asInt()), productNode);
				}
			}
		}catch (Exception e) {
			Logger.info("添加商品至购物车：查询商品异常");
			e.printStackTrace();
			return pushCartResult(false, "添加商品失败", null);
		}
		
		// 商品不存在
		if (skuWarehouseId2ProductNode.size()==0) {
			Logger.info("添加商品至购物车：商品不存在");
			return pushCartResult(false, "添加商品失败，商品不存在", null);
		}
		
		// 对商品一起处理
		// 不够库存的商品
		List<ObjectNode> notEnoughStockProductList = Lists.newArrayList();
		// 待购买的
		List<DisCartItem> toPurchaseList = Lists.newArrayList();
		for (Map.Entry<String, JsonNode> entry : skuWarehouseId2ProductNode.entrySet()) {
			JsonNode productNode = entry.getValue();
			
			// 购买数量
			Integer pQty = skuWarehouseId2Qty.get(KeyUtil.getKey(productNode.get("csku").asText(), productNode.get("warehouseId").asInt()));
			// 起批量
			int batchNumber = productNode.get("batchNumber").asInt();
			batchNumber = batchNumber > 0 ? batchNumber : 1;	
			// 购买数量不小于起批量
			pQty = (pQty==null || pQty<batchNumber) ? batchNumber : pQty;
			// 云仓库存
			int stock = productNode.has("stock") ? productNode.get("stock").asInt() : 0;
			stock = stock < 0 ? 0 : stock;
			
			// 库存不足
			if (stock == 0 || pQty > stock) {
				ObjectNode productSimpleInfo = getProductSimpleInfo(productNode);
				notEnoughStockProductList.add(productSimpleInfo);
				Logger.info("此商品库存不足，不能添加至购物车：{}", productSimpleInfo);
				continue;
			}
			
			DisCartItem item = new DisCartItem();
			item.setCartId(cart.getId());// 购物车主表id
			item.setCsku(JsonCaseUtil.getStringValue(productNode, "csku"));// 商品csku
			item.setIqty(pQty);// 商品数量
			item.setWarehouseId(JsonCaseUtil.getIntegerValue(productNode, "warehouseId"));// 仓库id
			item.setWarehouseName(JsonCaseUtil.getStringValue(productNode, "warehouseName"));// 仓库名称
			item.setTitle(JsonCaseUtil.getStringValue(productNode, "ctitle"));// 商品标题
			item.setPublicImg(JsonCaseUtil.getStringValue(productNode, "imageUrl"));// 商品图片
			item.setIsOrder(0);// 是否提交订单(0:未提交订单，1：已提交订单)
			item.setIsRemove(0);// 是否从购物车删除商品（0：未删除商品，1：已删除商品）
			item.setCreateTime(new Date());// 商品添加时间
			item.setCategoryName(JsonCaseUtil.getStringValue(productNode, "cname"));
			item.setCategoryId(JsonCaseUtil.getIntegerValue(productNode, "categoryId"));// 所属类目ID
			item.setInterBarCode(JsonCaseUtil.getStringValue(productNode, "interBarCode"));// 国际条码
			
			toPurchaseList.add(item);
		}
		
		if (CollectionUtils.isEmpty(toPurchaseList)) {
			Logger.info("添加商品至购物车：要购买的商品不存在/库存不足");
			return pushCartResult(false, "要购买的商品不存在/库存不足", notEnoughStockProductList);
		}
		
		// 待新增的商品
		List<DisCartItem> toInsert = Lists.newArrayList();
		// 待更新的商品
		List<DisCartItem> toUpdate = Lists.newArrayList();
		
		// 当前购物车里有的商品
		List<DisCartItem> allItemsInCartCurrently = cartItemMapper.getDisCartItemsByCartId(cart.getId());
		// 如果购物车为空，直接加入购物车，否则循环判断是否存在
		if (CollectionUtils.isEmpty(allItemsInCartCurrently)) {
			toInsert.addAll(toPurchaseList);
		} else {
			Map<String, DisCartItem> skuWarehouseId2CartItem = allItemsInCartCurrently.stream().collect(Collectors
					.toMap(item -> KeyUtil.getKey(item.getCsku(), item.getWarehouseId()), Function.identity()));
			
			// 检查商品是否在购物车中存在
			for (DisCartItem item : toPurchaseList) {
				DisCartItem itemExists = skuWarehouseId2CartItem
						.get(KeyUtil.getKey(item.getCsku(), item.getWarehouseId()));
				if (itemExists != null) {
					// 存在的话就数量加1
					int newQty = itemExists.getIqty() + 1;
					itemExists.setIqty(newQty);
					toUpdate.add(itemExists);
				} else {
					toInsert.add(item);
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(toInsert)) {
			cartItemMapper.batchInsert(toInsert);
		}
		
		if (CollectionUtils.isNotEmpty(toUpdate)) {
			cartItemMapper.batchUpdateByPrimaryKeySelective(toUpdate);
		}
		
		return pushCartResult(true, "添加商品成功", notEnoughStockProductList);
	}
	
	private Map<String, Object> pushCartResult(boolean suc, String msg, List<ObjectNode> notEnoughStockProductList){
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("suc", suc);
		resultMap.put("msg", msg);
		if (CollectionUtils.isNotEmpty(notEnoughStockProductList)) {
			resultMap.put("notEnoughStockProductList", notEnoughStockProductList);
		}
		return resultMap;
	}

	/**
	 * TODO  前台备注信息 
	 *       优惠码信息
	 * 1.购物车数据信息
	 * 2.获取赠品信息 满赠满减信息，
	 * 3.优惠码信息
	 * 
	 *   var orderData = {
            "email": email,
            "orderDetail": orderDetail.concat(gift),
            "totalPrice": response.totalPrice,
            "distributorType":comsumerType,
            "deductionAmount" : 0
        };
	 */
	@Override
	public Map<String, Object> order(String data, String dismember) {
		Map<String,Object> res = Maps.newHashMap();
		boolean suc = false;
		String msg = null;
		try {
			JsonNode login = Json.parse(dismember);
			// 购物车数据
			CartInfoDto info = cartItemService.getDisCartData(dismember);
			
			if (!info.getResult()) {
				res.put("suc",false);
				res.put("msg",info.getMsg());
				return res;
			}
			
			// 购物车没有商品
			if (CollectionUtils.isEmpty(info.getCartData())) {
				res.put("suc",false);
				res.put("msg","请选择商品");
				return res;
			}
			
			List<Integer> itemIdList = Lists.newArrayList();
			// 拿到选中的商品
			List<DisCartDto> selected = info.getCartData().stream().collect(Collectors.partitioningBy(e -> e.getSelected())).get(true);
			// 购物车有商品，但没有选中要下单的商品
			if (CollectionUtils.isEmpty(selected)) {
				res.put("suc",false);
				res.put("msg","请选择商品");
				return res;
			}
			
			// 查看是否有非卖状态的商品
			boolean hasNotSalable = false;
			List<DisCartDto> notSalablePros = Lists.newArrayList();
			for(DisCartDto disCartDto : selected){
				if(disCartDto.getSalable()==0){
					hasNotSalable = true;
					notSalablePros.add(disCartDto);
				}
			}
			// 有非卖的商品
			if (hasNotSalable) {
				Logger.info("购物车下单，试图下单非卖品，非卖品为：{}",notSalablePros);
				res.put("suc",false);
				res.put("msg","购物车存在非卖品，请核对");
				res.put("notSalablePros", notSalablePros);
				return res;
			}
			
			List<OrderDetail> details = Lists.transform(selected, e -> {
				itemIdList.add(e.getItemId());
				return new OrderDetail(e);
			});
			details = Lists.newArrayList(details);
			excuteActive(info, details,null);
			String email = login.get("email").asText();
			Integer distributorType = login.get("comsumerType").asInt();
			JsonNode json = Json.parse(data);
			String remarks = JsonCaseUtil.jsonCase(json, "remarks", Types.STR);
			String couponsCode = JsonCaseUtil.jsonCase(json, "couponsCode", Types.STR);
			Double couponsAmount = JsonCaseUtil.jsonCase(json, "couponsAmount", Types.DOU);
			Integer mode = login.get("distributionMode").asInt();
			// 下采购单
			JsonNode postOrder = httpService.postPurchase(
					distributorType, 
					email, details, info.getTotalPrice(),
					couponsAmount, couponsCode, remarks, mode == 1,
					null,null,null,false);
			boolean flag = !"1".equals(postOrder.get("errorCode").asText());
			res.put("suc", flag);
			res.put("code", postOrder.get("errorCode"));
			res.put("msg", postOrder.get("errorInfo"));
			if (flag) {
				// 如果生成订单成功，删除购物车数据
				cartItemMapper.deleteByIdList(itemIdList);
				deleteActiveInfo(email);
			}
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			msg = "生成订单异常";
		}
		res.put("suc",suc);
		res.put("msg",msg);
		return res;
	}

	/**
	 * 构造促销信息
	 * @author zbc
	 * @since 2017年5月26日 上午10:10:48
	 * @param info
	 * @param details
	 */
	private void excuteActive(CartInfoDto info, List<OrderDetail> details,DeliveryInfoDto deliveryInfo) {
		Double totalPrice = info.getTotalPrice();
		Double activityPrice = null;
		Map<String, Object> act = info.getActiveInfo();
		if (act != null && act.get("activePlvg") != null) {
			JsonNode pri = Json.parse(act.get("activePlvg") + "");
			Double plvgSum = pri.get("plvg").get("sum").asDouble();
			if (!plvgSum.equals(totalPrice)) {
				activityPrice = totalPrice - plvgSum;
				info.setTotalPrice(plvgSum);
			}
			JsonNode gift = pri.get("plvg").get("gift");
			OrderDetail d = null;
			for (JsonNode g : gift) {
				d = new OrderDetail();
				d.setTitle(JsonCaseUtil.jsonCase(g, "cTitle", Types.STR));
				d.setPrice(0.0);
				d.setMarketPrice(JsonCaseUtil.jsonCase(g, "marketPrice", Types.DOU));
				d.setRealPrice(JsonCaseUtil.jsonCase(g, "realPrice", Types.DOU));
				d.setQty(JsonCaseUtil.jsonCase(g, "num", Types.INT));
				d.setWarehouseId(JsonCaseUtil.jsonCase(g, "warehouseId", Types.INT));
				d.setWarehouseName(JsonCaseUtil.jsonCase(g, "warehouseName", Types.STR));
				d.setSumPrice(0.00);
				d.setPublicImg(JsonCaseUtil.jsonCase(g, "imgUrl", Types.STR));
				d.setSku(JsonCaseUtil.jsonCase(g, "sku", Types.STR));
				d.setCategoryId(JsonCaseUtil.jsonCase(g, "categoryId", Types.INT));
				d.setCategoryName(JsonCaseUtil.jsonCase(g, "categoryName", Types.STR));
				d.setClearancePrice(JsonCaseUtil.jsonCase(g, "clearancePrice", Types.DOU));
				d.setIsgift(true);
				details.add(d);
			}
		}
		if(deliveryInfo != null){
			deliveryInfo.setGoodsTotalPrice(totalPrice);
			deliveryInfo.setActivityPrice(activityPrice);
		}
	}

	@Override
	public JsonResult<?> delivery(String string, String dismember) {
		try {
			boolean flag = false;
			JsonNode login = Json.parse(dismember);
			String account = login.get("email").asText();
			CartSaleDto cartSale = JsonFormatUtils.jsonToBean(string, CartSaleDto.class);
			String uid = cartSale.getUid();
			if(sessionService.get(uid) == null){
				return JsonResult.newIns().result(false).msg("未查询到发货信息");
			}
			DeliveryInfoDto delivery = JsonFormatUtils.jsonToBean(sessionService.get(uid).toString(), DeliveryInfoDto.class);
			if(!delivery.getValidOrder()){
				return JsonResult.newIns().result(false).msg("信息不全无法下单");
			}
			//构造采购单下单数据
			Integer distributorType = login.get("comsumerType").asInt();
			Integer mode = login.get("distributionMode").asInt();
			// 下采购单  生成采购单不需要审核
			JsonNode postOrder = httpService.postPurchase(distributorType, 
					account,delivery.getGoods(),
					delivery.getTotalPrice(),
					delivery.getCouponsAmount(), delivery.getCouponsCode(), 
					cartSale.getRemark(), mode == 1,delivery.getLogisticsMode(),
					delivery.getBbcPostage(),delivery.getLogisticsTypeCode(),true);
			flag = !"1".equals(postOrder.get("errorCode").asText());
			String pno = null;//采购单号
			// 下单成功
			if(flag){
				pno = postOrder.get("errorInfo").asText();
				cartItemMapper.deleteByIdList(delivery.getItemIdList());
				deleteActiveInfo(account);
				sessionService.remove(delivery.getUid());
				// 构造发货单下单数据
				PurchaseSaleDto purSale = new PurchaseSaleDto(pno,delivery,cartSale,account,Lists.transform(delivery.getGoods(), de->{
					return new PurchaseSaleDetailDto(de);
				}));
				// 如果生成订单成功，删除购物车数据
				//保存发货单信息
				httpService.savePurchaseSaleOut(pno, Json.toJson(purSale).toString());
			}
			return JsonResult.newIns().result(flag).msg(pno);
		} catch (Exception e) {
			Logger.info("购物车发货异常:{}",e);
			return  JsonResult.newIns().result(false).msg("购物车发货异常");
		}
	}


	@Override
	public JsonResult<?> getDeliveryInfo(String dismember) {
		try {
			//查询购物车商品
			CartInfoDto info = cartItemService.getDisCartData(dismember);
			if(!info.getResult()){
				return JsonResult.newIns().result(false).msg(info.getMsg());
			}
			// 校验是否同仓
			List<DisCartDto> cartDtos  =  info.getCartData().stream().filter(ele->{return ele.getSelected();}).collect(Collectors.toList());
			//所选商品必须大于0
			if(cartDtos.size() <= 0){
				return JsonResult.newIns().result(false).msg("请选择商品");
			}
			List<Integer> itemIdList = Lists.newArrayList();
			List<OrderDetail> details = Lists.newArrayList(Lists.transform(cartDtos, e -> {
				itemIdList.add(e.getItemId());
				return new OrderDetail(e);
			}));
			JsonNode member = Json.parse(dismember);
			//发货信息
			DeliveryInfoDto delivery = new DeliveryInfoDto(IDUtils.getUUID());
			excuteActive(info, details,delivery);
			delivery.setGoods(details);
			delivery.setItemIdList(itemIdList);
			delivery.setIsPackageMail(JsonCaseUtil.jsonCase(member, "isPackageMail", Types.INT));
			//是否跨仓标识
			boolean isAcrossWare = details.stream().map(ele->ele.getWarehouseId()).collect(Collectors.toSet()).size()>1;
			if(isAcrossWare){
				return JsonResult.newIns().result(false).msg("不能跨仓发货,请重新选择商品或赠品");
			}
			//仓库id
			Integer warehouseId = details.get(0).getWarehouseId();
			//仓库名称
			String warehouseName = details.get(0).getWarehouseName();
			delivery.setWarehouseId(warehouseId);
			delivery.setWarehouseName(warehouseName);
			delivery.setTotalQty(details.stream().mapToInt(de->de.getQty()).sum());
			//保存发货信息
			sessionService.set(delivery.getUid(), Json.toJson(delivery).toString());
			return JsonResult.newIns().result(true).data(delivery);
		} catch (Exception e) {
			Logger.info("获取购物车发货信息异常,{}",e);
			return JsonResult.newIns().result(false).msg("获取购物车发货信息异常");
		}
	}

	@Override
	public JsonResult<?> getDeliveryFee(String string) {
		try {
			JsonResult<?> resObject = null;
			JsonNode json = Json.parse(string);
			String uid = JsonCaseUtil.jsonCase(json, "uid", Types.STR);
			if(sessionService.get(uid) == null){
				return JsonResult.newIns().result(false).msg("未查询到发货信息");
			}
			DeliveryInfoDto delivery = JsonFormatUtils.jsonToBean(sessionService.get(uid).toString(), DeliveryInfoDto.class);
			//补全费用信息
			JsonResult<?> freightRes = calFreight(json, delivery);
			if(!freightRes.getResult()){
				resObject = freightRes;
			}else{
				//计算优惠金额
				JsonResult<?> couponsRes = calCouponsAmount(json,delivery);
				if(!couponsRes.getResult()){
					resObject =  couponsRes;
				}else{
					resObject = JsonResult.newIns().result(true).data(delivery);
					delivery.setValidOrder(true);
				}
			}
			//保存发货信息
			sessionService.set(delivery.getUid(), Json.toJson(delivery).toString());
			return resObject;
		} catch (Exception e) {
			Logger.info("购物车发货费用信息获取异常{}",e);
			return JsonResult.newIns().result(false).msg("购物车发货费用信息获取异常");
		}
		
	}

	private JsonResult<?> calCouponsAmount(JsonNode json, DeliveryInfoDto delivery) throws JsonProcessingException, IOException {
		Double couponsAmount = null;
		String couponsCode =null;
		JsonResult<?> res = JsonResult.newIns().result(true);
		//如果包含优惠码
		if(JsonCaseUtil.isNotJsonEmpty(json, "couponsNo")){
			JsonNode couponsRes = httpService.getCouponsInfo(delivery.getPurchaseTotalPrice(), JsonCaseUtil.jsonCase(json, "couponsNo",Types.STR));
			if(couponsRes.get("suc").asBoolean()){
				couponsCode = JsonCaseUtil.jsonCase(json, "couponsNo",Types.STR);
				couponsAmount = PriceFormatUtil.toFix2(new BigDecimal(couponsRes.get("active").get("couponsCost").asText()));
			}else{
				delivery.setValidOrder(false);
				res = res.result(false).msg(couponsRes.get("info").asText());
			}
		}else{
			delivery.setValidOrder(true);
		}
		delivery.setCouponsAmount(couponsAmount);
		delivery.setCouponsCode(couponsCode);
		return res;
	}

	private JsonResult<?> calFreight(JsonNode json, DeliveryInfoDto delivery) throws JsonProcessingException, IOException {
		String logisticsTypeCode = JsonCaseUtil.jsonCase(json, "logisticsTypeCode", Types.STR);
		String logisticsMode = JsonCaseUtil.jsonCase(json, "logisticsMode", Types.STR);
		Integer provinceId = JsonCaseUtil.jsonCase(json, "provinceId", Types.INT);
		Integer cityId = JsonCaseUtil.jsonCase(json, "cityId", Types.INT);
		if(logisticsTypeCode != null&& provinceId!= null&&cityId!= null){
			List<Map<String,Object>> skuObj= Lists.newArrayList();
			Map<String,Object> skuMap = null;
			for(OrderDetail de:delivery.getGoods()){
				skuMap = Maps.newHashMap();
				skuMap.put("sku", de.getSku());
				skuMap.put("num", de.getQty());
				skuObj.add(skuMap);
			}
			JsonNode freightRes = httpService.getFreight(delivery.getWarehouseId(), logisticsTypeCode, Json.toJson(skuObj), provinceId, cityId);
			if(freightRes.get("result").asBoolean()){
				delivery.setBbcPostage(JsonCaseUtil.jsonCase(freightRes, "msg", Types.DOU));
			}else{
				delivery.setValidOrder(false);
				return JsonResult.newIns().result(false).msg(JsonCaseUtil.jsonCase(freightRes, "msg", Types.STR));
			}
		}
		delivery.setLogisticsMode(logisticsMode);
		delivery.setLogisticsTypeCode(logisticsTypeCode);
		return JsonResult.newIns().result(true);
	}

	@Override
	public JsonResult<?> delSelected(String account) {
		try {
			DisCart disCart = getUsableDisCart(account);
			List<Integer> itemsIdList = 
					cartItemMapper.getDisCartItemsByCartId(disCart.getId()).stream().filter(item->item.getBselected()).map(item->item.getId()).collect(Collectors.toList());
			if(itemsIdList.size()>0){
				cartItemMapper.deleteByIdList(itemsIdList);
			}
			return JsonResult.newIns().result(true).msg("删除购物车商品"+itemsIdList.size()+"个成功");
		} catch (Exception e) {
			Logger.info("删除异常:{}",e);
			return JsonResult.newIns().result(true).msg("购物车商品异常");
		}
	}
}
