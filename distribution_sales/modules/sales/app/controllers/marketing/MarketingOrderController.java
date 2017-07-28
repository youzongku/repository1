package controllers.marketing;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.ALogin;
import controllers.annotation.DivisionMember;
import dto.marketing.AuditParams;
import entity.marketing.MarketingOrderAuditLog;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.marketing.IMarketingOrderService;
import services.sales.IProductExpirationDateService;
import services.sales.IUserService;
import util.sales.DateUtils;
import util.sales.JsonCaseUtil;
import util.sales.StringUtils;

@Api(value="/营销单",description="Market Order Module")
public class MarketingOrderController extends Controller {
	@Inject private IMarketingOrderService moService;
	@Inject	private IUserService userService;
	@Inject private IProductExpirationDateService productExpirationDateService;
	
	/**
	 * 设置营销单录入商品到期日期
	 * 
	 * @return
	 */
	@ApiOperation(value="设置营销单录入商品到期日期",notes="<b>修改接口如下</b><br>新增参数：email(分销商账号),其余参数不变<br/>返回值:<br/>成功:{\"suc\":true,\"result\":{}}<br/>失败:{\"suc\":false,\"msg\":\"提示消息\"}<br/>其中result内容不变",httpMethod="POST",produces="application/json;charset=utf-8;")
	@ApiImplicitParams(
		{
		@ApiImplicitParam(name="body",required=true,paramType="body",dataType="string",
				defaultValue="{\n"
						+ "\"email\":\"zhengbc@qq.com\",\n"
						+ "\"selectedProducts\":[\n"
						+ "   {\n"
						+ "    \"title\": \"越南wismo番茄饼干棒22克\",\n"
						+ "    \"interBarCode\": \"8936047445493\",\n"
						+ "    \"imgUrl\": \"https://static.tomtop.com.cn/images/I/1/IF942-1/IF942-1-1-80e0-4rJF.jpg\",\n"
						+ "    \"sku\": \"IF942-1\",\n"
						+ "    \"qty\": 1,\n"
						+ "    \"warehouseId\": 2024,\n"
						+ "    \"warehouseName\": \"深圳仓\",\n"
						+ "    \"price\": 0.77,\n"
						+ "    \"stock\": 155148,\n"
						+ "    \"subStock\":155148,\n"
						+ "    \"microStock\": 0,\n"
						+ "    \"batchNumber\": 1,\n"
						+ "    \"categoryId\": 4706,\n"
						+ "    \"categoryName\": \"进口食品\",\n"
						+ "    \"marketPrice\": 15.26\n"
						+ "    }\n"
						+ "]\n"
						+ "}")}
	)
	@BodyParser.Of(BodyParser.Json.class)
	public Result setMOSelectedProductsExpirationDates(){
		JsonNode main = request().body().asJson();
		Logger.info("获取营销单商品到期日期，参数为：{}",main);
		if(JsonCaseUtil.checkParam(main,"email","selectedProducts")){
			return ok(Json.toJson(productExpirationDateService.setMOSelectedProductsExpirationDates(main)));
		}
		return ok(Json.newObject().put("suc",false).put("msg", "参数错误"));
	}
	
	/**
	 * 录入营销单
	 * @return
	 */
	@ALogin
	@DivisionMember
	@BodyParser.Of(BodyParser.Json.class)
	public Result inputMarketingOrder(){
		// {"data":{"email":"","details":[{"sku":"", "product_name":"xxxx"},{},{}]}}
		JsonNode main = request().body().asJson();
    	if (main == null) {
			return internalServerError("Expecting Json data");
		}
    	
    	String mainStr = main.toString();
    	String loginAccount = userService.getAdminAccount();
		return ok(Json.toJson(moService.createMarketingOrder(mainStr,loginAccount)));
	}

	
	/**
	 * 获取营销单列表
	 * @return
	 */
	@ALogin
	@DivisionMember
	public Result getMoList(){
		Map<String, String[]> map = request().body().asFormUrlEncoded();
		return ok(Json.toJson(moService.query(map)));
	}
	
	/**
	 * 导出营销单
	 * @return
	 */
	@ALogin
	@DivisionMember
	public Result exportMoList(){
		Map<String, String[]> map = request().queryString();
		Logger.info("导出查询参数：{}",map);
		// 只能查看关联的分销商的营销单
		// 导出
		String filename = "样品登记表"+DateUtils.nowStr()+".xlsx";
		try {
			response().setHeader("Content-disposition", "attachment;filename="+new String(filename.getBytes("utf-8"),"ISO8859_1"));
		} catch (UnsupportedEncodingException e) {
			Logger.info("营销单导出的excel文件不支持中文名");
			e.printStackTrace();
		}
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		
		return ok(moService.exportMoList(filename,map));
	}
	
	/**
	 * 初审
	 * @return
	 */
	@ALogin
	@BodyParser.Of(BodyParser.Json.class)
	public Result auditFirstly(){
		JsonNode main = request().body().asJson();
    	if (main == null) {
			return internalServerError("Expecting Json data");
		}
    	
    	AuditParams params = buildParam(main,1);
		Map<String, Object> result = moService.audit(params);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 复审
	 * @return
	 */
	@ALogin
	@BodyParser.Of(BodyParser.Json.class)
	public Result auditSecondly(){
		JsonNode main = request().body().asJson();
    	if (main == null) {
			return internalServerError("Expecting Json data");
		}
		
    	AuditParams params = buildParam(main,2);
		Map<String, Object> result = moService.audit(params);
		return ok(Json.toJson(result));
	}

	private AuditParams buildParam(JsonNode main, int i) {
		AuditParams params = new AuditParams();
		params.setMarketingOrderNo(main.get("marketingOrderNo").asText());
		params.setPassed(main.get("passed").asInt());
		params.setRemarks(main.get("remarks").asText());
		params.setAuditUser(userService.getAdminAccount());
		params.setType(i);
		return params;
	}

	/**
	 * 获取审核日志
	 * @param marketingOrderNo
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getMoAuditLogs(){
		JsonNode main = request().body().asJson();
		if (main == null) {
			return internalServerError("Expecting Json data");
		}
		
		String marketingOrderNo = main.get("marketingOrderNo").asText();
		Map<String, Object> result = Maps.newHashMap();
		if(StringUtils.isBlankOrNull(marketingOrderNo)){
			result.put("suc", false);
			result.put("msg", "营销单单号不能为空");
			return ok(Json.toJson(result));
		}
		
		List<MarketingOrderAuditLog> logs = moService.getAllAuditLogs4AMo(marketingOrderNo);
		result.put("suc", true);
		result.put("logs", logs);
		return ok(Json.toJson(result));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result getAuditFirstlyLatestLog(){
		JsonNode main = request().body().asJson();
		if (main == null) {
			return internalServerError("Expecting Json data");
		}
		
		String marketingOrderNo = main.get("marketingOrderNo").asText();
		Map<String, Object> result = Maps.newHashMap();
		if(StringUtils.isBlankOrNull(marketingOrderNo)){
			result.put("suc", false);
			result.put("msg", "营销单单号不能为空");
			return ok(Json.toJson(result));
		}
		
		MarketingOrderAuditLog log = moService.getAuditFirstlyLatestLog(marketingOrderNo);
		result.put("suc", true);
		result.put("log", log);
		return ok(Json.toJson(result));
	}
	
	@ALogin
	public Result smMoList(){
		Map<String, String[]> map = request().body().asFormUrlEncoded();
		// 只能查看关联的分销商的营销单
		return ok(Json.toJson(moService.smMoList(map)));
	}
	
	public Result smExportMoList(){
		// 导出
		String filename = "营销单据导出样表-业务"+DateUtils.nowStr()+".xlsx";
		Map<String, String[]> map = request().queryString();
		Logger.info("导出查询参数：{}",map);
		try {
			response().setHeader("Content-disposition", "attachment;filename="+new String(filename.getBytes("utf-8"),"ISO8859_1"));
		} catch (UnsupportedEncodingException e) {
			Logger.info("营销单导出的excel文件不支持中文名");
			e.printStackTrace();
		}
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		return ok(moService.smExport(filename,map));
	}
	
	
}
