package controllers.contract;

import java.util.Map;

import org.elasticsearch.common.collect.Maps;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.ALogin;
import dto.JsonResult;
import dto.product.result.QuotedResult;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Result;
import services.product.IQuotedService;
import services.product.IUserService;
import util.product.JsonCaseUtil;

/**
 * 报价管理
 * 
 * @author Administrator
 *
 */
@Api(value="/合同报价管理",description="Contract Quoted")
public class QuotedManagerController extends Controller {

	@Inject
	private IQuotedService quotedService;

	@Inject
	private IUserService userService;

	/**
	 * 添加报价
	 * 
	 * @return
	 */
	@ALogin
	public Result quotedAdd() {
		MultipartFormData formData = request().body().asMultipartFormData();
		Map<String, String[]> params = formData.asFormUrlEncoded();
		Map<String, Object> result = Maps.newHashMap();
		if (null == params || !params.containsKey("cid") || !params.containsKey("contractPrice")
				|| !params.containsKey("sku") || !params.containsKey("categoryId")) {
			result.put("suc", false);
			result.put("msg", "请检查参数是否正确。");
			return ok(Json.toJson(result));
		}
		String opUser = userService.getAdminAccount();
		return ok(Json.toJson(quotedService.addQuoted(params, opUser)));
	}

	/**
	 * 删除报价
	 * 
	 * @return
	 */
	@ALogin
	public Result quotedDelete(Integer qid) {
		Map<String, Object> result = Maps.newHashMap();
		if (qid == null) {
			result.put("suc", false);
			result.put("msg", "请检查参数是否正确。");
			return ok(Json.toJson(result));
		}
		String opUser = userService.getAdminAccount();
		return ok(Json.toJson(quotedService.deleteQuoted(qid, opUser)));
	}

	/**
	 * 修改报价 
	 * change by zbc
	 * @return
	 */
	/*@ALogin
	public Result quotedUpdate() {
		JsonNode node = request().body().asJson();
		Map<String, Object> result = Maps.newHashMap();
		if (null == node || !node.has("qid")) {
			result.put("suc", false);
			result.put("msg", "请检查参数是否正确。");
			return ok(Json.toJson(result));
		}
		String opUser = userService.getAdminAccount();
		return ok(Json.toJson(quotedService.updateQuoted(node, opUser)));
	}*/
	/**
	 * 修改报价
	 * @author zbc
	 * @since 2017年5月3日 下午4:35:03
	 * @return
	 */
	@ApiOperation(value="修改报价",httpMethod="POST",
			notes="<b>有后台登录校验，该接口是原有接口修改的</b><br/>未开始报价可以进行修改",
			produces="application/json;charset=utf-8;",
			response=JsonResult.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="param",value="新增报价json数据:<br/>qid:报价id，必传<br/>contractPrice:合同价,必传",dataType="string",paramType="body",
				defaultValue="{\n"
					    +"\"qid\": 64,\n"
					    +"\"contractPrice\": 20\n"
					    +"}"
		)
	})
	@ALogin
	public Result quotedUpdate() {
		JsonNode node = request().body().asJson();
		JsonResult<?> res = null;
		String operator = userService.getAdminAccount();
		Logger.info("[{}]更新报价参数[{}]",operator,node);
		if (JsonCaseUtil.checkParam(node, new String[]{"qid","contractPrice"})) {
			res =quotedService.updateQuoted(node.toString(), operator);
		}else{
			res = JsonResult.newIns().result(false).msg("请检查参数是否正确。");
		}
		return ok(Json.toJson(res));
	}

	/**
	 * 查询报价
	 * 
	 * @return
	 */
	@ALogin
	public Result quotedGet() {
		Map<String, Object> result = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		if (null == node || node.size() <= 0) {
			result.put("suc", false);
			result.put("msg", "请检查参数是否正确。");
			return ok(Json.toJson(result));
		}
		result.put("suc", true);
		String opUser = userService.getAdminAccount();
		result.put("data", quotedService.getQuoted(node, opUser));
		return ok(Json.toJson(result));
	}

	/**
	 * 查询操作日志
	 * 
	 * @param qid
	 * @return
	 */
	@ALogin
	public Result quotedOprecord(Integer qid) {
		return ok(Json.toJson(quotedService.getOprecord(qid)));
	}

	/**
	 * 手动触发定时任务
	 * @param qid
	 * @return
	 */
	public Result auto() {
		quotedService.autoOpenNotStartQuoted();
		return ok("OK");
	}
	
	/**
	 * 批量添加报价
	 * @author zbc
	 * @since 2017年5月2日 下午4:11:29
	 * @return
	 */
	@ApiOperation(value="添加报价",httpMethod="POST",
			notes="<b>有后台登录校验</b><br/>该接口替换原先/product/quoted/add接口, 可以同时添加多个sku的报价",
			produces="application/json;charset=utf-8;",
			response=JsonResult.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="param",value="新增报价json数据",dataType="dto.product.ContractQuotationsAddDto",paramType="body",
				defaultValue="{\n"
					    +"\"cid\": 231,\n"
					    +"\"pros\": [\n"
					    +"{\n"
					    +"\"warehouseId\": 2024,\n"
					    +"\"sku\": \"IF942-1\",\n"
					    +"\"categoryId\": 4680,\n"
					    +"\"contractPrice\": 20\n"
					    +"}\n"
					    +"]\n"
					    +"}"
		)
	})
	@ALogin
	public Result quotedBatchAdd(){
		JsonNode node = request().body().asJson();
		JsonResult<?> res = null;
		String operator = userService.getAdminAccount();
		Logger.info("[{}]批量添加报价参数[{}]",operator,node);
		if(JsonCaseUtil.checkParam(node, new String[]{"cid","pros"})){
			res = quotedService.batchAdd(node.toString(),operator);
		}else{
			res = JsonResult.newIns().result(false).msg("参数错误");
		}
		return ok(Json.toJson(res));
	}
	
	/**
	 * 提前结束
	 * @author zbc
	 * @since 2017年5月3日 下午12:27:29
	 * @return
	 */
	@ApiOperation(value="提前结束报价",httpMethod="GET",
			notes="<b>有后台登录校验</b><br/>合同报价提前结束(开始状态可以进行该操作)",
			produces="application/json;charset=utf-8;",
			response=JsonResult.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="qid",required=true,value="合同报价id",dataType="integer",
				paramType="path",defaultValue="64")
	})
	@ALogin
	public Result earlyTermination(Integer id){
		return ok(Json.toJson(quotedService.earlyTermination(id,userService.getAdminAccount())));
	}
	
	/**
	 * 查询报价信息
	 * @author zbc
	 * @since 2017年5月3日 下午3:05:21
	 * @param id
	 * @return
	 */
	@ApiOperation(value="查看报价信息",httpMethod="GET",
			notes="<b>有后台登录校验</b><br/>修改报价弹出框可调用该接口查询报价",
			produces="application/json;charset=utf-8;",
			response=QuotedResult.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="qid",required=true,value="合同报价id",dataType="integer",
				paramType="path",defaultValue="64")
	})
	@ALogin
	public Result quotedReadOne(Integer id){
		return ok(Json.toJson(quotedService.getQuoted(id)));
	}
	
	@ApiOperation(value="刷数据使用：设置合同报价商品的categoryId",httpMethod="GET",
			notes="",
			produces="application/json;charset=utf-8;",
			response=JsonResult.class)
	@ALogin
	public Result batchSetCategoryId(){
		Map<String,Object> result = quotedService.batchSetCategoryId();
		return ok(Json.toJson(result));
	}
}
