package controllers.product;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.collect.Lists;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import dxo.category.SkuWarehouse2Qty;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.product.IProductDispriceService;

/**
 * 定价设控制类
 * @author zbc
 * 2016年7月29日 下午12:17:35
 */
/**
 * @author zbc 2016年8月19日 下午7:53:26
 */
@Api(value="/productPrice",description="查询、设置商品价格")
public class ProductDispriceContoller extends Controller {

	@Inject
	private IProductDispriceService productDispriceService;
	
	public Result batchGetArriveWarePrice(){
		// [{sku_warehouseId:qty,sku_warehouseId:qty:sku_warehouseId:qty,...}]
		JsonNode node = request().body().asJson();
		JsonNode skuWarehouse2QtyListNode = node.get("data").get(
				"skuWarehouse2QtyList");
		List<SkuWarehouse2Qty> skuWarehouse2QtyList = Lists.newArrayList();
		for (Iterator<JsonNode> skuWarehouse2QtyIterator = skuWarehouse2QtyListNode.iterator(); skuWarehouse2QtyIterator.hasNext();) {
			JsonNode skuWarehouse2QtyNode = skuWarehouse2QtyIterator.next();
			skuWarehouse2QtyList.add(new SkuWarehouse2Qty(skuWarehouse2QtyNode.get("sku").asText(),
					skuWarehouse2QtyNode.get("warehouseId").asInt()));
		}
		
		HashMap<Object, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("suc", true);
		newHashMap.put("result", productDispriceService.batchGetArriveWarePrice(skuWarehouse2QtyList));
		Logger.info("批量获取到仓价的结果为：{}", newHashMap);
		return ok(Json.toJson(newHashMap));
	}

	/**
	 * 获取总的到仓价
	 * 
	 * @return
	 */
	public Result getTotalArriveWarePrice() {
		/*
		 {
		 	data: {
		 		skuWarehouse2QtyList: [{sku_warehouseId:qty,sku_warehouseId:qty:sku_warehouseId:qty,...}],
		 		useCostIfAbsent: true/false(此字段useCostIfAbsent可有可无)
		 	}
		 }
		 */
		JsonNode node = request().body().asJson();
		JsonNode dataNode = node.get("data");
		JsonNode skuWarehouse2QtyListNode = dataNode.get("skuWarehouse2QtyList");
		boolean useCostIfAbsent = false;
		if (dataNode.has("useCostIfAbsent")) {
			useCostIfAbsent = dataNode.get("useCostIfAbsent").asBoolean();
		}
		
		List<SkuWarehouse2Qty> skuWarehouse2QtyList = Lists.newArrayList();
		for (Iterator<JsonNode> skuWarehouse2QtyIterator = skuWarehouse2QtyListNode
				.iterator(); skuWarehouse2QtyIterator.hasNext();) {
			JsonNode skuWarehouse2QtyNode = skuWarehouse2QtyIterator.next();
			
			skuWarehouse2QtyList.add(new SkuWarehouse2Qty(skuWarehouse2QtyNode.get("sku").asText(),
					skuWarehouse2QtyNode.get("warehouseId").asInt(),
					skuWarehouse2QtyNode.get("qty").asInt()));
		}
		

		HashMap<Object, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("suc", true);
		newHashMap.put("result", productDispriceService.calculateTotalArriveWarePrice(skuWarehouse2QtyList, useCostIfAbsent));
		Logger.info("计算总到仓价的结果为：{}", newHashMap);
		return ok(Json.toJson(newHashMap));
	}

	/**
	 * 获取定价规则列表
	 * 
	 * @author zbc
	 * @since 2016年7月29日 下午12:22:48
	 */
	public Result getPriceRule() {
		productDispriceService.readrule();
		return ok(Json.toJson(productDispriceService.readrule()));
	}

	/**
	 * 设置价格
	 * 
	 * @author zbc
	 * @since 2016年7月30日 下午2:06:11
	 */
	public Result setDisPrice(String type) {
		JsonNode node = request().body().asJson();
		Logger.info("修改(" + (type != null ? type : "TOTAL") + ")价格参数:[{}]",
				node);
		if (node == null || !node.has("priceIid") || !node.has("sku")
				|| !node.has("productTitle") || !node.has("categoryName")
				|| !node.has("categoryId") || !node.has("operator")
				|| !node.has("changeMap") || !node.has("warehouseId")
				|| !node.has("warehouseName")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(productDispriceService.setDisprice(node, type)));
	}

	/**
	 * 设置价格 基础价格
	 * 
	 * @author zbc
	 * @since 2016年7月30日 下午2:06:11
	 */
	public Result setBasePrice() {
		return setDisPrice("BASE");
	}

	/**
	 * 设置价格 经销商供货价
	 * 
	 * @author zbc
	 * @since 2016年7月30日 下午2:06:11
	 */
	public Result setDistributorPrice() {
		return setDisPrice("DIS");
	}

	/**
	 * 设置价格 自贸区经销价格
	 * 
	 * @author zbc
	 * @since 2016年7月30日 下午2:06:11
	 */
	public Result setFtzPrice() {
		return setDisPrice("FTZ");
	}

	/**
	 * 设置价格 电商供货价
	 * 
	 * @author zbc
	 * @since 2016年7月30日 下午2:06:11
	 */
	public Result setElePrice() {
		return setDisPrice("ELE");
	}

	/**
	 * 设置价格 KA直营供货价
	 * 
	 * @author zbc
	 * @since 2016年7月30日 下午2:06:11
	 */
	public Result setSupPrice() {
		return setDisPrice("SUP");
	}
	
	/**
	 * 设置价格 vip价格
	 * @author zbc
	 * @since 2016年12月13日 下午2:56:00
	 */
	public Result setVipPrice() {
		return setDisPrice("VIP");
	}

	/**
	 * 获取商品价格操作日志
	 * 
	 * @author zbc
	 * @since 2016年7月30日 下午4:18:34
	 */
	public Result readPriceLog(Integer priceIid, String type) {
		return ok(Json.toJson(productDispriceService.readPriceLog(priceIid,
				type)));
	}

	/**
	 * 默认价格设置 操作记录
	 * 
	 * @author zbc
	 * @since 2016年8月4日 下午2:36:00
	 */
	public Result readRuleLog() {
		JsonNode node = request().body().asJson();
		Map<String, Object> result = Maps.newHashMap();
		if (node == null) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		result = productDispriceService.readRuleLog(node);
		return ok(Json.toJson(result));
	}

	/**
	 * 分页查询价格 系数表
	 * 
	 * @author zbc
	 * @since 2016年8月19日 下午7:52:48
	 */
	public Result readPriceFactor() {
		JsonNode node = request().body().asJson();
		Map<String, Object> result = Maps.newHashMap();
		if (node == null) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		result = productDispriceService.getPriceFactorList(node);
		return ok(Json.toJson(result));
	}

	/**
	 * 初始化价格系数表信息
	 * 
	 * @author zbc
	 * @since 2016年8月19日 下午7:53:29
	 */
	public Result initCateData() {
		return ok(Json.toJson(productDispriceService.initCateData()));
	}

	/**
	 * 分页查询清货价
	 * @author zbc
	 * @since 2017年4月11日 上午11:34:20
	 *   "\n\" arrive_ware_price ","clearance_rate","clearance_price"）
	 */
	@ApiOperation(value="分页查询清货价",httpMethod="POST",notes="该接口值查询清货状态的商品，以下参数带*为必填" ,produces="application/json")
	@ApiImplicitParams({@ApiImplicitParam(name="body",
	required=true,value="可用参数：pageNo:*页码,"
			+ "\npageSize:*页长,"
			+ "\ntypeId:商品类别id,"
			+ "\nkey:模糊搜索,"
			+ "\nsort:排序字段(可选值：\"arrive_ware_price \",\"clearance_rate\",\"clearance_price\")"
			+ "\nfilter:排序方式(可选值:\"asc\",\"desc\")",
			paramType="body",dataType="json",
	defaultValue = "{" + 
			"\n\"pageNo\": 1," + 
			"\n\"pageSize\": 10," + 
			"\n\"typeId\": 1,"+
			"\n\"categoryId\": 4680," + 
			"\n\"key\": \"IM18\"," + 
			"\n\"sort\":\" arrive_ware_price \","+
			 "\n\"filter\":\"asc\"" + 
			"\n}")})
	public Result clearancePrice(){
		JsonNode node = request().body().asJson();
		if(node == null){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));	
		}
		return ok(Json.toJson(productDispriceService.pageSearchClearancePrice(node.toString())));
	}
	
}
