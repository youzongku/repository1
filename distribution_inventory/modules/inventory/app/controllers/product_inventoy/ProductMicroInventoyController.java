package controllers.product_inventoy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import constant.InventoryConstant;
import controllers.annotation.ALogin;
import controllers.annotation.GetRequestIp;
import controllers.annotation.Login;
import entity.product_inventory.ProductMicroInventoryDetail;
import entity.product_inventory.ProductMicroInventoryInRecord;
import dto.product_inventory.*;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.inventory.IUserService;
import services.product_inventory.IOrderService;
import services.product_inventory.IProductCloudInventoryService;
import services.product_inventory.IProductInventoryOrderLockService;
import services.product_inventory.IProductInventoryService;
import services.product_inventory.IProductMicroInventoryService;
import util.warehousing.DataUtil;
import utils.inventory.DateUtils;
import vo.inventory.Page;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 *
 * 商品库，微仓控制器
 * @author duyuntao
 *
 */
@GetRequestIp
@Api(value="/微仓模块",description="MicroInventory Module")
public class ProductMicroInventoyController extends Controller {

	@Inject
	IOrderService orderService;

	@Inject
	IProductInventoryService productInventoryService;

	@Inject
	IProductMicroInventoryService microInventoryService;

	@Inject
	IProductInventoryOrderLockService productInventoryOrderLockService;

	@Inject
	IProductCloudInventoryService productCloudInventoryService;

	@Inject
	IProductMicroInventoryService productMicroInventoryService;

	@Inject
	IUserService userService;

	/**
	 * 查询一组SKU的微仓商品数量
	 * @param ProductMicroInventoryDetailSearchDto
	 * @return
	 */
	public Result microList() {
		JsonNode node = request().body().asJson();
		ProductMicroInventoryDetailSearchDto reqParam=null;
		try {
			reqParam=DataUtil.fromJson(ProductMicroInventoryDetailSearchDto.class, node.toString());
		} catch (IOException e) {
			Logger.error(">>>>>>>>>>>>>>>>>>>>>microList error:{}", e);
			Map<String,Object> result=Maps.newHashMap();
			result.put("result", false);
			result.put("msg", "获取查询参数发生异常!");
			return ok(Json.toJson(result));
		}
		List<MicroInventoryQueryResult> resultList=microInventoryService.selectByParams(reqParam);
		if(resultList !=null && resultList.size()>0){
			Logger.debug("microList-->result[{}]",resultList.toString());
		}
		return ok(Json.toJson(resultList));
	}

	/**
	 * 查询一组SKU的微仓商品详情
	 * @param ProductMicroInventoryDetailSearchDto
	 * @return
	 */
	public Result microDetailList() {
		JsonNode node = request().body().asJson();
		ProductMicroInventoryDetailSearchDto reqParam=null;
		try {
			reqParam=DataUtil.fromJson(ProductMicroInventoryDetailSearchDto.class, node.toString());
		} catch (IOException e) {
			Logger.error(">>>>>>>>>>>>>>>>>>>>>microList error:{}", e);
			Map<String,Object> result=Maps.newHashMap();
			result.put("result", false);
			result.put("msg", "获取查询参数发生异常!");
			return ok(Json.toJson(result));
		}
		List<ProductMicroInventoryDetail> resultList=microInventoryService.selectMicroDetailByParams(reqParam);
		return ok(Json.toJson(resultList));
	}

	/**
	 * 查询一组(SKU,warehoueId)的微仓商品详情
	 * @param ProductMicroInventoryDetailSearchDto
	 * @return
	 */
	public Result microDetailWithExpirdateList() {
		JsonNode node = request().body().asJson();
		ProductMicroInventoryDetailSearchDto reqParam=new ProductMicroInventoryDetailSearchDto();
		String account = node.get("account").asText();
		reqParam.setAccount(account);
		List<ProductMicroInventoryDetail> skuAndWarehouseIds=Lists.newArrayList();
		JsonNode jsonNode = node.get("skuWarehouseIdArray");
		for(JsonNode skuWarehouseId:jsonNode){
			ProductMicroInventoryDetail detail=new ProductMicroInventoryDetail();
			String sku = skuWarehouseId.get("sku").asText();
			int warehouseId = skuWarehouseId.get("warehouseId").asInt();
			detail.setSku(sku);
			detail.setWarehouseId(warehouseId);
			skuAndWarehouseIds.add(detail);
		}
		reqParam.setSkuAndWarehouseIds(skuAndWarehouseIds);
		List<ProductMicroInventoryDetail> resultList=microInventoryService.selectMicroDetailBySkuAndWareId(reqParam);
		return ok(Json.toJson(resultList));
	}

	/**
	 * 后台查询微仓中商品信息
	 * 返回结果为微仓中所有商品数量，包含处于锁定中的微仓商品数量。
	 * @return
	 */
	public Result microDetailData(){
		ProductMicroInventoryDetailSearchDto reqParam = new ProductMicroInventoryDetailSearchDto();
		Map<String, String[]> map = request().body().asFormUrlEncoded();
		try {
			reqParam.setKey(map.get("key")[0]);
			reqParam.setAccount(map.get("account")[0]);
			if (!StringUtils.isEmpty(map.get("expirationDate")[0])) {
				reqParam.setExpirationDate(DateUtils.string2date(map.get("expirationDate")[0], "yyyy-MM-dd"));
			}
			//分页参数
			reqParam.setPageSize(Integer.valueOf(map.get("rows")[0]));
			reqParam.setCurrPage(Integer.valueOf(map.get("page")[0]));

			//排序
			reqParam.setSidx(map.get("sidx")[0]);
			reqParam.setSord(map.get("sord")[0]);
		} catch (Exception e) {
			Logger.error(">>>>>>>>>>>>>>>>>>>>>microList error:{}", e);
			Map<String,Object> result=Maps.newHashMap();
			result.put("result", false);
			result.put("msg", "获取查询参数发生异常!");
			return ok(Json.toJson(result));
		}
		Page<ProductMicroInventoryDetail> pageDae= microInventoryService.selectMicroDetailByParam(reqParam);
		return ok(Json.toJson(pageDae));
	}

	/**
	 * 后台查询微仓详情对应的采购详情
	 * @param id
	 * @return
	 */
	public Result getMicroPurchaseDetail(Integer id){
		if(id==null){
			Map<String,Object> result=Maps.newHashMap();
			result.put("result", false);
			result.put("msg", "参数不正确");
			return ok(Json.toJson(result));
		}
		List<ProductMicroInventoryInRecord> result= microInventoryService.getPurchaseDetail(id);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 根据订单号查询微仓入仓记录
	 * @return
	 */
	public Result getMicroInventoryInRecordByOrderNo(){
		Map<String,Object> result=Maps.newHashMap();
		JsonNode reqParam = request().body().asJson();
		if(reqParam==null||!reqParam.has("purchaseOrderNo")){
			result.put("suc", false);
			result.put("msg", "参数不正确");
			return ok(Json.toJson(result));
		}
		String purchaseOrderNo = reqParam.get("purchaseOrderNo").asText();
		ProductMicroInventoryInRecord microInRecordParam=new ProductMicroInventoryInRecord();
		microInRecordParam.setOrderNo(purchaseOrderNo);
		List<ProductMicroInventoryInRecord> resultLists=microInventoryService.getMicroInventoryInRecordByParam(microInRecordParam);
		if(resultLists.isEmpty()){
			result.put("suc", false);
			result.put("msg", "采购单号"+purchaseOrderNo+"查询不到相关入库信息！");
			return ok(Json.toJson(result));
		}
		result.put("suc", true);
		result.put("result", resultLists);
		ObjectMapper objectMapper = new ObjectMapper();
		//设置日期格式
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		objectMapper.setDateFormat(fmt);
		String str = "";
		try {
			str = objectMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			Logger.error("入仓记录查询结果日期格式化发生异常{}", e);
			result.put("suc", false);
			result.put("msg", "入仓记录查询结果日期格式化发生异常");
			return ok(Json.toJson(result));
		}
		return ok(Json.parse(str));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Result saleOrderWithInventory() {
		JsonNode main = request().body().asJson();

		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
		String jsonDataStr = main.toString();

		CreateSaleOrderResult createSaleOrderResult = new CreateSaleOrderResult();
		try {
			createSaleOrderResult = productMicroInventoryService.createSaleOrderWithInventory(jsonDataStr);
		} catch (Exception e) {
			createSaleOrderResult.setType(InventoryConstant.SALE_ORDER_ERROR);//错误返回码：4
			Logger.error(">>>>>>>>>>>>>>>>>>>>>saleOrderWithInventory error:{}", e);
		}

		ObjectMapper objectMapper = new ObjectMapper();
		//设置日期格式
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		objectMapper.setDateFormat(fmt);
		String str = "";
		try {
			str = objectMapper.writeValueAsString(createSaleOrderResult);
		} catch (JsonProcessingException e) {
			Logger.error(">>>>>>>>>>>>>>>>>>>>>saleOrderWithInventory writeValueAsString error:{}", e);
			e.printStackTrace();
		}
		Logger.info(">>>>>>>>>>>>>>>>>>>>>saleOrderWithInventory result{}", str);
		return ok(Json.toJson(Json.parse(str)));
	}

	/**
	 * 退货锁库
	 * @return
	 */
	public Result returnProductLockMicroInventory(){
		JsonNode node = request().body().asJson();
		Map<String,Object> result=Maps.newHashMap();
		try {
			Logger.info("returnProductLockMicroInventory---------->{}", node);
			ReturnProductData returnParam=null;
			try {
				returnParam=DataUtil.fromJson(ReturnProductData.class, node.toString());
			} catch (IOException e) {
				Logger.info("退货参数获取异常------{}", e);
				result.put("result", 1);
				result.put("msg", "退货参数获取异常");
			}
			PurchaseOrderResult returnLockResult= productMicroInventoryService.returnProductLockMicroInventory(returnParam);
			if(returnLockResult.isResult()){
				result.put("result", 0);
				result.put("msg", returnLockResult.getMsg());
			}else{
				result.put("result", 1);
				result.put("msg", returnLockResult.getMsg());
			}
		} catch (Exception e) {
			Logger.info("退货发生异常------>{}", e);
			result.put("result", 1);
			result.put("msg", "退货发生异常");
		}
		return ok(Json.toJson(result));
	}

	/**
	 * 退货成功释放微仓库存至云仓
	 * @param returnOrderNo
	 * @return
	 */
	public Result returnProductSuccess(){
		Map<String,Object> result=Maps.newHashMap();
		JsonNode reqParamJson = request().body().asJson();
		List<String> returnResult=Lists.newArrayList();
		try {
			if(reqParamJson==null||!reqParamJson.has("returnOrderNoArray")){
				result.put("result", 1);
				result.put("msg","参数为空！");
				return ok(Json.toJson(result));
			}
			Logger.info("微仓退货至云仓参数：{}", reqParamJson.toString());
			JsonNode jsonNode = reqParamJson.get("returnOrderNoArray");
			for(JsonNode node:jsonNode){
				String returnOrderNo = node.asText();
				PurchaseOrderResult returnLockResult = productMicroInventoryService.returnProductSuccess(returnOrderNo);
				if(!returnLockResult.isResult()){
					Logger.info("退货库存还回云仓失败，退货单号{}，信息{}", returnOrderNo,returnLockResult.getMsg());
					returnResult.add(returnOrderNo);
				}
			}
			if(returnResult.size()>0){
				result.put("result", 1);
				result.put("returnFailList",returnResult);
			}else{
				result.put("result", 0);
				result.put("msg","微仓库存退货成功");
			}
			return ok(Json.toJson(result));
		} catch (Exception e) {
			Logger.info("returnProductSuccessError----------->",e);
			result.put("result", 1);
			result.put("msg","退货释放微仓库存发生异常！");
			return ok(Json.toJson(result));
		}
	}
	/**
	 * 更新退货锁库为失效
	 * @param returnOrderNo
	 * @return
	 */
	public Result updateReturnLockRecordEffective(){
		Map<String,Object> result=Maps.newHashMap();
		JsonNode reqParamJson = request().body().asJson();
		List<String> releaseResultList=Lists.newArrayList();
		try {
			if(reqParamJson==null||!reqParamJson.has("returnOrderNoArray")){
				result.put("result", 1);
				result.put("msg","参数为空！");
				return ok(Json.toJson(result));
			}
			Logger.info("释放退货锁库参数:{}", reqParamJson.toString());
			JsonNode jsonNode = reqParamJson.get("returnOrderNoArray");
			for(JsonNode node:jsonNode){
				String returnOrderNo = node.asText();
				PurchaseOrderResult releaseLockResult = productMicroInventoryService.updateReturnLockRecordEffective(returnOrderNo);
				if(!releaseLockResult.isResult()){
					Logger.info("释放退货锁库失败，退货单号{}，信息{}", returnOrderNo,releaseLockResult.getMsg());
					releaseResultList.add(returnOrderNo);
				}
			}
			if(releaseResultList.size()>0){
				result.put("result", 1);
				result.put("releaseFailList",releaseResultList);
			}else{
				result.put("result", 0);
				result.put("msg","释放退货锁库成功！");
			}
			return ok(Json.toJson(result));
		} catch (Exception e) {
			Logger.info("updateReturnLockRecordEffective----------->",e);
			result.put("result", 1);
			result.put("msg","释放退货锁库发生异常！");
			return ok(Json.toJson(result));
		}
	}

	/**
	 * 根据采购单号将微仓商品还原到云仓
	 * @param orderNo
	 * @return
	 */
	public Result restoreCloudFormMicro(String orderNo) {
		if(orderNo==null){
			return ok("采购单号不正确");
		}
		ProductMicroInventoyResult restoreCloudFormMicroInventory = productMicroInventoryService.restoreCloudFormMicroInventory(orderNo);
		return ok(Json.toJson(restoreCloudFormMicroInventory));
	}

	/**
	 * 根据入库记录将微仓中商品释放
	 * @return
	 */
	public Result releaseMicroStockToCloud(){
		JsonNode node = request().body().asJson();
		ProductMicroInventoryInRecord microInRecordParam=null;
		String account = userService.getAdminAccoun();

		try {
			microInRecordParam=DataUtil.fromJson(ProductMicroInventoryInRecord.class, node.toString());
		} catch (IOException e) {
			Logger.info("releaseMicroStockToCloudError{}", e);
			return ok("微仓库存释放参数获取异常");
		}
		ProductMicroInventoyResult releaseMicroResult=productMicroInventoryService.releaseMicroStockToCloud(microInRecordParam, account);
		return ok(Json.toJson(releaseMicroResult));
	}

	/**
	 * 取消销售单
	 *
	 * @return
	 */
	public Result cancleSaleOrder() {
		JsonNode main = request().body().asJson();

		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}

		CancleSaleOrderResult cancleSaleOrderResult = new CancleSaleOrderResult();
		try {
			String jsonDataStr = main.toString();
			cancleSaleOrderResult = productMicroInventoryService.cancleSaleOrder(jsonDataStr);
			return  ok(Json.toJson(cancleSaleOrderResult));
		} catch (Exception e) {
			Logger.error(">>>>>>>>>>>>>>>>>>>>>cancleSaleOrder error:{}", e);
			cancleSaleOrderResult.setResult(false);
			cancleSaleOrderResult.setMsg("系统出错！");
			return ok(Json.toJson(cancleSaleOrderResult));
		}
	}

	/**
	 * M站出库(先微仓后云仓)
	 *
	 * @return
	 */
	public Result msiteStockOut(){
		JsonNode main = request().body().asJson();

		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}

		String jsonDataStr = main.toString();

		MsiteStockOutResult msiteStockOutResult = new MsiteStockOutResult();
		try {
			return ok(Json.toJson(productMicroInventoryService.msiteStockOut(jsonDataStr)));
		} catch (Exception e) {
			msiteStockOutResult.setType(3);//错误返回码：3
			Logger.error(">>>>>>>>>>>>>>>>>>>>>msiteStockOut error:{}", e);
			return ok(Json.toJson(msiteStockOutResult));
		}
	}


	/**
	 * 取消M站的单
	 *
	 * @return
	 */
	public Result cancleMsiteSaleOrderWithInventory() {
		JsonNode main = request().body().asJson();

		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}

		CancleSaleOrderResult cancleSaleOrderResult = new CancleSaleOrderResult();
		try {
			String jsonDataStr = main.toString();
			cancleSaleOrderResult = productMicroInventoryService.cancleMsiteSaleOrderWithInventory(jsonDataStr);
			return ok(Json.toJson(cancleSaleOrderResult));
		} catch (Exception e) {
			Logger.error(">>>>>>>>>>>>>>>>>>>>>cancleMsiteSaleOrderWithInventory error:{}", e);
			cancleSaleOrderResult.setResult(false);
			cancleSaleOrderResult.setMsg("系统出错！");
			return ok(Json.toJson(cancleSaleOrderResult));
		}
	}

	@ApiOperation(value = "根据分销商账号查询微仓", notes = "", nickname = "", httpMethod = "GET")
	@Login
	public Result queryMicroWarehouse(String email){
		String mail = userService.getDisAccount();
		if(mail != null){
			email = mail;
		}
		return ok(Json.toJson(productMicroInventoryService.queryMicroWarehouse(email)));
	}

	@ALogin
	public Result manQueryMicroWarehouse(String email){
		return ok(Json.toJson(productMicroInventoryService.queryMicroWarehouse(email)));
	}


	public Result asyncMicroInventory() {
		try {
			productMicroInventoryService.asyncMicroInventory();
			return ok(Json.toJson(true));
		} catch (Exception e) {
			Logger.error(">>>>>>>>>>>>>>>>>>>>>asyncMicroInventory error:{}", e);
			return ok(Json.toJson(false));
		}
	}

}
