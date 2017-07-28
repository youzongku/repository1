package controllers.sales;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.Login;
import dto.sales.TaoBaoGoodsSearchDto;
import dto.sales.TaoBaoOrderForm;
import entity.platform.order.template.TaoBaoOrder;
import entity.platform.order.template.TaoBaoOrderGoods;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.base.utils.JsonFormatUtils;
import services.sales.IImportOrderService;
import services.sales.ISaleLockService;
import services.sales.ITaoBaoOrderGoodsService;
import services.sales.ITaoBaoOrderService;
import services.sales.IUserService;
import util.sales.HttpUtil;

/**
 * 手动导入订单
 * 
 * @author mjx
 *
 */
@Api(value="/订单导入",description="Import Order")
public class HandOrderImportController extends Controller {

	@Inject private	ITaoBaoOrderService iTaoBaoOrderService;
	@Inject private	ITaoBaoOrderGoodsService iTaoBaoOrderGoodsService;
	@Inject private	IImportOrderService importOrderService;
	
	@Inject private	IUserService userService;
	@Inject private	ISaleLockService lockService;

	/**
	 * 描述：线下店铺订单及商品导入（以及淘宝订单和商品） 2016年5月17日
	 * 
	 * @return
	 * @throws IOException
	 */
	public Result importOrder() throws IOException {
		MultipartFormData formData = request().body().asMultipartFormData();
		if (formData == null) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("flag", false);
			resultMap.put("msg", "系统错误，上传失败！");
			return ok(Json.toJson(resultMap));
		}

		Map<String, String[]> params = formData.asFormUrlEncoded();
		List<FilePart> files = formData.getFiles();
		return ok(Json.toJson(this.importOrderService.importOrder(files, params)));
	}

	/**
	 * 描述：其他订单导入（阿里巴巴，有赞，京东） 2016年5月23日
	 * 
	 * @return
	 * @throws IOException
	 */
	public Result importOtherOrder() throws IOException {
		MultipartFormData formData = request().body().asMultipartFormData();
		if (formData == null) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("flag", false);
			resultMap.put("msg", "系统错误，上传失败！");
			return ok(Json.toJson(resultMap));
		}
		
		Map<String, String[]> params = formData.asFormUrlEncoded();
		List<FilePart> files = formData.getFiles();
		return ok(Json.toJson(importOrderService.importOtherOrder(files,params)));
	}

	/**
	 * 通过邮箱获取当前用户订单
	 * 
	 * @return
	 */
	@ApiOperation(value="获取用户订单",httpMethod="POST",notes="获取用户订单，带*为必填" ,produces="application/json")
	@ApiImplicitParams({@ApiImplicitParam(name="body",
			required=true,value="可用参数："
			+ "\nemail:*用户账号，\n"
			+ "\npageNo:*页码，\n"
			+ "\npageSize:*页长",
			paramType="body",dataType="application/json",
			defaultValue = "{\n\"email\":\"003003@qq.com\",\n\"pageNo\":1,\n\"pageSize\":10\n}")})
	@Login
	public Result uploadOrderList() {
		String email = "";
		Integer model = 0;
		String dis = userService.getDismember();
		if (dis != null) {
			JsonNode login = Json.parse(dis);
			email = login.get("email").asText();
			model = login.get("distributionMode").asInt();
		}
		Form<TaoBaoOrderForm> form = Form.form(TaoBaoOrderForm.class)
				.bindFromRequest();
		TaoBaoOrderForm taoBaoOrderForm = form.get();
		if (!email.equals("")) {
			taoBaoOrderForm.setEmail(email);
		}
		if (taoBaoOrderForm.getPageNo() == null) {
			taoBaoOrderForm.setPageNo(1);
			taoBaoOrderForm.setPageSize(10);
		}
		return ok(Json.toJson(iTaoBaoOrderService.queryOrders(taoBaoOrderForm,model)));
	}

	@Login
	public Result getGoodsByOrderNo() {
		Map<String, String> params = Form.form().bindFromRequest().data();
		String email = userService.getDisAccount();
		if (email == null) {
			email = params.get("email");
		}
		List<TaoBaoOrderGoods> goodsByOrderNo = iTaoBaoOrderGoodsService
				.getGoodsByOrderNoAndEmail(params.get("orderNo"), email);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("data", goodsByOrderNo);
		return ok(Json.toJson(resultMap));
	}

	/**
	 * 通过订单编号订单详细
	 * 
	 * @return
	 */
	@Login
	public Result getOrderDetails() {
		Map<String, Object> map = Maps.newHashMap();
		JsonNode asJson = request().body().asJson();
		if (asJson.get("orderNo") == null || asJson.get("email") == null) {
			map.put("data", null);
			return ok(Json.toJson(map));
		}
		
		String orderNo = asJson.get("orderNo").asText();
		String email = userService.getDisAccount();
		if (email == null) {
			email = asJson.get("email").asText();
		}
		TaoBaoOrder order = iTaoBaoOrderService.selectByOrderNoAndEmail(orderNo, email);
		map.put("data", order);
		return ok(Json.toJson(map));
	}

	/**
	 * 通过订单编号逻辑删除(含批量)
	 * 
	 * @return
	 */
	@Login
	public Result deleteOrder() {
		Form<TaoBaoOrderForm> form = Form.form(TaoBaoOrderForm.class)
				.bindFromRequest();
		TaoBaoOrderForm taoBaoOrderForm = form.get();
		int num = iTaoBaoOrderService.batchDeleteOrder(taoBaoOrderForm);
		TaoBaoGoodsSearchDto dto = new TaoBaoGoodsSearchDto();
		dto.setOrderNoList(taoBaoOrderForm.getOrderList());
		dto.setEmail(taoBaoOrderForm.getEmail());
		iTaoBaoOrderGoodsService.batchDeleteOrderGoods(dto);
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("data", num);
		return ok(Json.toJson(map));
	}

	/**
	 * 更新订单信息
	 * 
	 * @return
	 */
	@Login
	public Result updateImportItemQty() {
		Map<String, Object> result = Maps.newHashMap();

		// String token = cookie.value();
		JsonNode params = request().body().asJson();
		if (params == null || (!params.has("goodId") && !params.has("qty"))) {
			result.put("result", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		Logger.debug("updateDisCartItemQty params-->" + params.toString());
		String goodId = params.get("goodId").asText();
		Integer qty = params.get("qty").asInt();
		boolean flag = iTaoBaoOrderGoodsService.updateImportItemQty(goodId, qty);
		
		result.put("result", flag);
		if (!flag) {
			result.put("msg", "更新商品购买数量失败");
		}
		return ok(Json.toJson(result));
	}

	/**
	 * 保存订单及商品仓库信息
	 * 
	 * @return
	 */
	@Login
	public Result saveOrderInfo() {
		JsonNode json = request().body().asJson();
		TaoBaoOrderForm taoBaoOrderForm = JsonFormatUtils.jsonToBean(
				json.get("data").toString(), TaoBaoOrderForm.class);

		TaoBaoOrderGoods good = null;
		for (Iterator<JsonNode> iterator = json.get("warehouse").iterator();iterator.hasNext();) {
			good = JsonFormatUtils.jsonToBean(iterator.next().toString(),
					TaoBaoOrderGoods.class);
			iTaoBaoOrderGoodsService.saveGoodsInfo(good);
		}
		
		taoBaoOrderForm.setUpdateDate(new Date());
		int saveOrder = iTaoBaoOrderService.saveOrder(taoBaoOrderForm);
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("data", saveOrder);
		return ok(Json.toJson(map));
	}

	/**
	 * 订单和商品模板下载
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public Result exportMould() throws UnsupportedEncodingException {
		String filename = Form.form().bindFromRequest().data().get("name");
		response().setHeader("Content-disposition",
						"attachment;filename=" + new String(filename.getBytes(), "ISO8859-1") + ".xls");
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		return ok(importOrderService.getExportModel(filename));
	}

	/**
	 * 生成订单操作
	 * 
	 * @author ouyangyaxiong
	 * @date 2016年4月8日 下午5:28:31
	 * @return
	 */
	@Login
	public Result saveOrder() {
		JsonNode main = request().body().asJson();
		if (main.size() == 0) {
			ObjectNode result = Json.newObject();
			result.put("suc", false);
			result.put("msg", "生成订单失败，参数错误");
			return ok(result.toString());
		}
		
		return ok(Json.toJson(lockService.saveSaleOrder(main)));
	}


	/**
	 * 取淘宝订单的商品信息
	 * 
	 * @author ouyangyaxiong
	 * @date 2016年4月9日 下午3:04:03
	 * @return
	 */
	@Login
	public Result getGoodsInfo() {
		Form<TaoBaoGoodsSearchDto> form = Form.form(TaoBaoGoodsSearchDto.class)
				.bindFromRequest();
		TaoBaoGoodsSearchDto dto = form.get();

		List<TaoBaoOrderGoods> list = iTaoBaoOrderGoodsService
				.getGoodsByParam(dto);
		return ok(Json.toJson(list));
	}

	@Login
	public Result deleteTaobaoOrder() {
		JsonNode node = request().body().asJson();
		TaoBaoGoodsSearchDto dto = JsonFormatUtils.jsonToBean(node.toString(),
				TaoBaoGoodsSearchDto.class);
		Logger.info("deleteTaobaoOrder---dto={}", dto.toString());
		int num = iTaoBaoOrderGoodsService.batchDeleteOrderGoods(dto);

		Map<String, Object> res = Maps.newHashMap();
		res.put("result", (num > 0));
		return ok(Json.toJson(res));
	}

	@Login
	public Result deleteTaobaoGood(Integer goodId) {
		int num = iTaoBaoOrderGoodsService.deletOrderGoodsById(goodId);

		Map<String, Object> res = Maps.newHashMap();
		res.put("suc", (num > 0));
		return ok(Json.toJson(res));
	}

	/**
	 * 描述:通过仓库id获得仓库对应的所有物流方式 2016年5月9日
	 * 
	 * @return
	 */
	@Login
	public Result getLogisticsByWarehouseId() {
		DynamicForm df = Form.form().bindFromRequest();
		JsonNode logisticsInfoJson = null;
		if (df != null && !Strings.isNullOrEmpty(df.get("wd"))) {
			String resultString = HttpUtil.get(new HashMap<String, String>(),
					HttpUtil.B2CBASEURL + "/sysadmin/warehouse/" + df.get("wd")
							+ "/shippings");
			if (resultString != null && !"".equals(resultString)) {
				logisticsInfoJson = Json.parse(resultString);
				return ok(Json.toJson(logisticsInfoJson));
			}
		}
		return ok(logisticsInfoJson);
	}

	/**
	 * 描述：根据仓库id和导入进来的订单编号判断此订单在销售订单系统中是否已经存在
	 * 
	 * @return
	 */
	@Login
	public Result checkByOrderNoAndWarehouseId() {
		JsonNode node = request().body().asJson();
		Logger.info("node--------->" + Json.toJson(node));
		if (node == null || !node.has("platformOrderNo")
				|| !node.has("warehouseId") || !node.has("list")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(iTaoBaoOrderService
				.checkByOrderNoAndWarehouseId(node)));
	}
}
