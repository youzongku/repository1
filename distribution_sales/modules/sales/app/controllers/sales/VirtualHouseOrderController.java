package controllers.sales;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.ALogin;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.sales.ISaleLockService;
import services.sales.ISaleService;
import util.sales.StringUtils;

@Api(value="/virtualWarehouseOrder",description="虚拟仓发货")
public class VirtualHouseOrderController extends Controller {

	@Inject private ISaleService saleService;
	@Inject private ISaleLockService lockService;
	
	@ApiOperation(value = "虚拟仓发货", notes = "", nickname = "", httpMethod = "POST")
	@ApiImplicitParams({
	@ApiImplicitParam(name = "body", value = "", required = true, dataType = "", paramType = "body" 
		,defaultValue = 
		"{"+
			  "\n\"email\": \"779740645@qq.com\", "+
			  "\n\"LogisticsTypeCode\":\"X2\", "+
			  "\n\"address\":\"北京市 北京市 东城区 12345678\", "+
			  "\n\"createUser\":\"superadmin\", "+
			  "\n\"email\":\"779740645@qq.com\", "+
			  "\n\"isBack\":true, "+
			  "\n\"isPay\":true, "+
			  "\n\"logisticsMode\":\"顾客自提\", "+
			  "\n\"postCode\":\"100000\", "+
			  "\n\"provinceId\":1, "+
			  "\n\"receiver\":\"wujiao\", "+
			  "\n\"remark\":\"\", "+
			  "\n\"skuList\":[{ "+
				  "\n\"expirationDate\":\"\", "+
				  "\n\"finalSellingPrice\":1.7800, "+
				  "\n\"interBarCode\":\"8936047445493\", "+
				  "\n\"isgift\":false, "+
				  "\n\"key\":\"A001_2100_false\", "+
				  "\n\"num\":1, "+
				  "\n\"sku\":\"A001\", "+
				  "\n\"warehouseId\":2100 "+
					  "\n}], "+
			"\n\"telphone\":\"15220086703\", "+
			"\n\"warehouseId\":2100, "+
			"\n\"warehouseName\":\"家乐福天津仓\" "+
			"\n}"
			) })
	@ALogin
	public Result virtualOrder() {
		JsonNode main = request().body().asJson();
		Logger.info("虚拟仓发货下单，参数：{}",main);
		Map<String, Object> result = Maps.newHashMap();
		if (main == null || !main.has("email") || !main.has("warehouseId")
				|| !main.has("skuList")) {
			result.put("code", 101);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		String tradeNo = main.has("tradeNo") ? main.get("tradeNo").textValue(): null;
		// 检查交易号
		if (StringUtils.isNotBlankOrNull(tradeNo)
				&& saleService.checkTradeNo(tradeNo.trim())) {
			result.put("code", 101);
			result.put("msg", "订单交易号重复，请重新输入");
			return ok(Json.toJson(result));
		}
		
		((ObjectNode)main).put("isVirtualWarehouse", true);
		Logger.info("虚拟仓发货下单接口请求数据：[{}]", main);
		// 修改逻辑 锁库方法
		return ok(Json.toJson(lockService.cOrder(main)));
	}
	
}
