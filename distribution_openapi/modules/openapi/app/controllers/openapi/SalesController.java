package controllers.openapi;

import java.util.Objects;

import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.openapi.ISaleService;
import utils.SalesStatusUtil;
import utils.StringUtils;
import utils.response.ResponseResultUtil;
import annotation.Login;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

public class SalesController extends Controller {

	@Inject
	private ISaleService saleService;
	
	/**
	 * 分页查询客户订单列表
	 * 
	 * @return
	 */
	 @Login
	@BodyParser.Of(BodyParser.Json.class)
	public Result getCustomerOrderPage() {
		JsonNode main = request().body().asJson();
		// {"status":1,"desc":"","email":"779740645@qq.com","currPage":1,"pageSize":1}
		// 参数校验
		if (Objects.isNull(main)) {
			return ResponseResultUtil.newErrorJson(101, "缺少参数");
		}
		
		if(main.has("status")){
			if(StringUtils.isNotBlankOrNull(main.get("status").asText())){
				if(!SalesStatusUtil.contains(main.get("status").asText())){
					return ResponseResultUtil.newErrorJson(102, "非法的订单状态");
				}
			}
		}
		
		String postResult = saleService.getCustomerOrderPage(main,Context.current());
		
		return ResponseResultUtil.newSuccessJson(postResult);
	}

	/**
	 * 创建一个新的订单
	 * 
	 * @return
	 */
	@Login
	@BodyParser.Of(BodyParser.Json.class)
	public Result createNewOrder() {
		// POST sales/postOrder SalesController.postOrder4OpenApi()
		// 参数校验
		JsonNode main = request().body().asJson();
		if (Objects.isNull(main) || !main.has("shopName") || !main.has("warehouseId") || !main.has("skuList")) {
			return ResponseResultUtil.newErrorJson(101, "参数错误");
		}
		Result result = saleService.createNewOrder(main,Context.current());
		Logger.info("下单result===="+result);
		return result;
	}


}
