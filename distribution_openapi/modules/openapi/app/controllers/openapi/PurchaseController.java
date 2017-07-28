package controllers.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import annotation.Login;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.openapi.IPurchaseService;
import utils.response.ResponseResultUtil;

/**
 * 采购订单控制类
 * @author zbc
 * 2016年8月26日 上午11:36:12
 */
public class PurchaseController extends Controller {
	
	@Inject
	IPurchaseService purchaseService;

	/**
	 * * add by zbc
	 * 下单流程
     * [{
     * 	"sku":"IM180",
     *  ware_id:2024,
     *  qty:5
     * },
     * {
     * 	"sku":"IM180",
     *  ware_id:2024,
     *  qty:5
     * }]
	 * code{
	 *  100 正常
	 *  101 参数错误
	 *  102 未查询到商品信息
	 *  103 商品编码_仓库编码 库存不足无法下单
	 *  104 某商品 不存在，无法下单 
	 *  105 下单异常
	 * }
	 * @author zbc
	 * @since 2016年8月26日 上午11:05:16
	 */
	@Login
	public Result order(){
		JsonNode node = request().body().asJson();
		if(node == null || !node.has("details")){
			return ResponseResultUtil.newErrorJson(101,"参数错误");
		}
		return purchaseService.order(node,Context.current());
	}
	
	/** 
	 * {
	 * "pageSize":10, 
	 * "pageCount":1, 当前页 可不传 默认为1
	 * "title":"",
	 * "status": "WAIT_PAY":(未付款) "CANCEL":(已取消) "PAY_SUCCESS": (已付款)INVALID："已失效"
	 * "title":模糊搜索（采购单号，商品名称）
	 * }
	 * code {
	 *   100 正常
	 *   101 参数错误 
	 *   110 查询异常
	 * }
	 * 
	 */
	@Login
	public Result getOrderPage(){
		JsonNode node = request().body().asJson();
		if(node == null){
			return ResponseResultUtil.newErrorJson(101,"参数错误");
		}
		return purchaseService.getOrderPage(node,Context.current());
	}
	
}
