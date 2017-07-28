package controllers.product;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.ALogin;
import dto.JsonResult;
import dto.product.result.InventoryLockDePageResult;
import dto.product.result.InventoryLockPageResult;
import dto.product.result.IvyOptDetailPageResult;
import dto.product.result.IvyRecordPageResult;
import dto.product.result.ProductLiteResult;
import entity.product.InventoryLock;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.product.IInventoryLockService;
import services.product.IUserService;
import util.product.JsonCaseUtil;

/**
 * @author zbc
 * 2017年4月18日 下午3:17:55
 */
@Api(value="/KA锁库",description="KA Inventory lock")
public class InventoryLockController extends Controller {

	@Inject
	private IInventoryLockService lockService;
	@Inject
	private IUserService userService;
	
	@ALogin
	@ApiOperation(value="新增锁库API",notes="该接口有后台登录校验<br/><b>details 新增参数:expirationDate(yyyy-MM-dd、非必传)</b>",httpMethod="POST",produces="application/json; charset=utf-8",response=JsonResult.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="body",paramType="body",dataType="dto.product.PostInventoryLockDto",
			value="<b>estimatedShippingTime:必填格式必须为YYYY-MM-DD<br/> account:必填 <br/> "
					+ "details:<br/>{sku:必传<br/>num:必传<br/>warehouseId:必传<br/>expirationDate:yyyy-MM-dd非必传<br/>}</b>" ,
			defaultValue= "{\n"+
					  "\"account\": \"zhengbc@qq.com\",\n"+
					  "\"remark\": \"KA锁库\",\n"+
					  "\"estimatedShippingTime\": \"2017-05-18\",\n"+
					  "\"details\": [\n"+
					  "{\n"+
					      "\"sku\": \"IF639\",\n"+
					      "\"num\": 20,\n"+
					      "\"warehouseId\": 2024\n"+
					    "}\n"+
					  "]\n"+
					"}"
		)
	})
	public Result create(){
		JsonNode json = request().body().asJson();
		Logger.info("创建KA锁库参数:[{}]",json);
		if(JsonCaseUtil.checkParam(json,new String[]{"estimatedShippingTime","account","details"})){
			return ok(Json.toJson(lockService.create(json.toString(),userService.getAdminAccount())));
		}
		return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
	}
	
	@ApiOperation(value="查询锁库信息",notes="result:true(取data中的数据),false(msg提示信息)",httpMethod="GET",response=InventoryLock.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="id",value="锁库id",required=true,dataType="integer",paramType="path")
	})
	public Result get(Integer id){
		return ok(Json.toJson(lockService.get(id)));
	}
	
	@ApiOperation(value="释放锁库",httpMethod="GET",response=JsonResult.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="id",value="锁库id",required=true,dataType="integer",paramType="path")
	})
	public Result release(Integer id){
		return ok(Json.toJson(lockService.release(id)));
	}
	
	@ApiOperation(
	value="分页查询锁库详情",httpMethod="POST",notes="分页查询",produces="application/json; charset=utf-8",response=InventoryLockDePageResult.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="body",paramType="body",dataType="dto.product.search.InventoryLockDeSearch",
			value="<b>lockId:必填<br/> page:必填 <br/> "
					+ "page:必填<br/>sidx:(可选值:expiration_date,left_num,lock_num)<br/>sord:(可选值:asc,desc)</b>" ,
			defaultValue= 
				"{\n"+
				  "\"page\": 1,\n"+
				  "\"rows\": 10,\n"+
				  "\"lockId\": 5\n"+
				"}"
		)
	})
	public Result detailPages(){
		JsonNode node = request().body().asJson();
		Logger.info("锁库详情分页查询参数:[{}]",node);
		if(JsonCaseUtil.checkParam(node, new String[]{"lockId","page","rows"})){
			return ok(Json.toJson(lockService.detailPages(node.toString())));
		}
		return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
	}
	
	@ApiOperation(
			value="分页查询锁库记录",httpMethod="POST",notes="分页查询",produces="application/json; charset=utf-8",response=InventoryLockPageResult.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="body",paramType="body",dataType="dto.product.search.InventoryLockSearch",
			value="<b> page:必填 <br/> "
					+ "page:必填<br/>sidx:(可选值:create_date,estimated_shipping_time)<br/>sord:(可选值:asc,desc)</b>" ,
			defaultValue= 
				"{\n"+
				  "\"page\": 1,\n"+
				  "\"rows\": 10\n"+
				"}"
		)
	})
	public Result page(){
		JsonNode node = request().body().asJson();
		Logger.info("锁库信息分页查询参数:[{}]",node);
		if(JsonCaseUtil.checkParam(node, new String[]{"page","rows"})){
			return ok(Json.toJson(lockService.page(node.toString())));
		}
		return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
	}
	
	/**
	 * 根据分销商账号
	 * 商品信息获取到期日期库存数据
	 * @author zbc
	 * @since 2017年4月21日 上午10:58:24
	 */
	public Result cloud(){
		JsonNode node = request().body().asJson();
		if(JsonCaseUtil.checkParam(node, new String[]{"email","skuWarehouseIdArray"})){
			return ok(Json.toJson(lockService.dealCloud(node.toString())));
		}
		return internalServerError("参数错误");
	}
	
	/**
	 * ＫＡ　云仓锁库
	 * @author zbc
	 * @since 2017年4月21日 下午3:51:32
	 */
	public Result cloudlock(){
		JsonNode node = request().body().asJson();
		Logger.info("KA云仓锁库参数:[{}]",node);
		return ok(Json.toJson(lockService.cloudlock(node.toString())));
	}
	
	/**
	 * KA微仓锁库
	 * @author zbc
	 * @since 2017年4月22日 下午5:07:27
	 */
	public Result microlock(){
		JsonNode node = request().body().asJson();
		Logger.info("KA微仓锁库参数:[{}]",node);
		return ok(Json.toJson(lockService.microlock(node.toString())));
	}
	
	public Result stock(String sku,Integer  wareId){
		Logger.info("获取KA锁库数{},{}",sku,wareId);
		return ok(Json.toJson(lockService.stock(sku,wareId)));
	}
	
	/**
	 * 获取各到期日期的库存
	 * @author zbc
	 * @since 2017年5月8日 下午3:40:44
	 * @return
	 */
	@ApiOperation(value="获取个到期日期的商品信息",httpMethod="POST",notes="该接口用于创建锁库记录时，选择到期日期时返回各个到期日期的库存数,data数据和商品查询返回值相同",response=ProductLiteResult.class)
	@ApiImplicitParams(
		{
			@ApiImplicitParam(name="body",required=true,dataType="string",paramType="body",
					value="email:分销商账号,必传<br>pros:<br/>[<br/>{sku:字符串，必传<br/>warehouseId:正整数，必传<br>}<br>]",
					defaultValue="{\n"
							+ "\"email\":\"zhengbc@qq.com\",\n"
							+ "\"pros\":\n"
							+"[\n"
							+ "{\n"
							+ "\"sku\":\"IF639\",\n"
							+ "\"warehouseId\":2024\n"
							+ "}\n"
							+ "]\n"
							+"}"
					)
		}
	)
	public Result cloudSelectedExpirationDates(){
		JsonNode main = request().body().asJson();
		Logger.info("KA锁库，获取到期日期商品，参数为：{}",main);
		if(JsonCaseUtil.checkParam(main, new String[]{"email","pros"})){
			return ok(Json.toJson(lockService.cloudSelectedExpirationDates(main.toString())));
		}
		return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
	}
	
	/**
	 * 
	 	{
		  "lockId": 0, 
		  "details": [
		    {
		      "detailId": 0, 
		      "num": 0
		    }
		  ], 
		  "remark": "XXX"
		}
	 * 释放锁库(后续加上全部释放逻辑)
	 * @author zbc
	 * @since 2017年5月8日 下午5:43:13
	 * @return
	 */
	@ApiOperation(value="释放锁库",httpMethod="POST",notes="<b>后台登录校验</b><br/>释放锁库接口，lockId,remark必传，details 与 isAll:true 选传",
			response=JsonResult.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="body",value="",paramType="body",required=true,dataType="dto.product.inventory.LockResetDto",
				defaultValue="{\n"+
						  "\"lockId\": 63,\n"+
						  "\"remark\": \"备注备注\",\n"+
						  "\"isAll\": true\n"+
						"}")
	})
	@ALogin
	public Result reSetLock(){
		JsonNode node = request().body().asJson();
		Logger.info("KA释放锁库{}",node);
		if(JsonCaseUtil.checkParam(node, new String[]{"lockId","remark"})){
			return ok(Json.toJson(lockService.reSetInventoryLock(node.toString(),userService.getAdminAccount())));
		}
		return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
	}
	
	/**
	 * 分页查询释放锁库日志
	 * @author zbc
	 * @since 2017年5月9日 下午5:29:55
	 * @return
	 */
	@ApiOperation(
			value="释放锁库记录分页查询",httpMethod="POST",notes="分页查询",produces="application/json; charset=utf-8",response=IvyRecordPageResult.class)
			@ApiImplicitParams({
				@ApiImplicitParam(name="body",paramType="body",dataType="dto.product.search.InventoryLockDeSearch",
					value="<b>lockId:必填<br/> page:必填 <br/> "
							+ "page:必填<br/>sidx:(可选值:opdate)<br/>sord:(可选值:asc,desc)</b>" ,
					defaultValue= 
						"{\n"+
						  "\"page\": 1,\n"+
						  "\"rows\": 10,\n"+
						  "\"lockId\": 64\n"+
						"}"
				)
			})
	@ALogin
	public Result records(){
		JsonNode node = request().body().asJson();
		if(JsonCaseUtil.checkParam(node, new String[]{"lockId","page","rows"})){
			return ok(Json.toJson(lockService.getRecords(node.toString())));
		}
		return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
	}
	
	/**
	 * 释放锁库明细分页查询
	 * @author zbc
	 * @since 2017年5月10日 上午9:06:12
	 * @return
	 */
	@ApiOperation(
			value="释放锁库明细分页查询",httpMethod="POST",notes="分页查询",produces="application/json; charset=utf-8",response=IvyOptDetailPageResult.class)
			@ApiImplicitParams({
				@ApiImplicitParam(name="body",paramType="body",dataType="dto.product.search.InventoryLockDeSearch",
					value="<b>recordId:必填<br/> page:必填 <br/> "
							+ "page:必填<br/>sidx:(可选值:expiration_date,num)<br/>sord:(可选值:asc,desc)</b>" ,
					defaultValue= 
						"{\n"+
						  "\"page\": 1,\n"+
						  "\"rows\": 10,\n"+
						  "\"recordId\": 2\n"+
						"}"
				)
			})
	public Result resetDetails(){
		JsonNode node = request().body().asJson();
		if(JsonCaseUtil.checkParam(node, new String[]{"recordId","page","rows"})){
			return ok(Json.toJson(lockService.getResetDetails(node.toString())));
		}
		return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
	}
	
	
}
