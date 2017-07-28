package controllers.sales;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.ALogin;
import controllers.annotation.Login;
import dto.JsonResult;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.sales.ISaleReceiverService;
import services.sales.IUserService;
import util.sales.JsonCaseUtil;

@Api(value = "/收件人地址", description = "Receiver Address")
public class ReceiverAddressController extends Controller {
	@Inject
	private ISaleReceiverService saleReceiverService;
	@Inject
	private IUserService userService;

	/**
	 * 删除指定收件人
	 */
	@ApiOperation(value = "删除收货人地址", notes = "rid:收件人的id", nickname = "", httpMethod = "GET",
			produces = "text/plain")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "rid", value = "收件人的id", required = true, paramType = "path", dataType = "Integer") })
	@Login
	public Result delReceiver(Integer rid) {
		if (rid == null || rid <= 0) {
			return ok(Json.toJson(JsonResult.newIns().result(false).msg("无效参数")));
		}
		String account = userService.getDisAccount();
		Logger.info("删除分销商【{}】的收货人地址信息，id为{}", account, rid);
		return ok(Json.toJson(saleReceiverService.deleteReceiver(rid, account)));
	}

	/**
	 * 获取收件人列表
	 * @return
	 */
	@ApiOperation(value = "获取收件人列表", notes = "<b>获取收件人列表</b>", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", value = "searchText：搜索条件",
					required = true, paramType = "body", dataType = "body",
					defaultValue = "{\n" +
							"\"searchText\":\"\"\n" +
							"}"
			)
	})
	@Login
	public Result getReceivers() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			return internalServerError("Expecting Json data");
		}
		String account = userService.getDisAccount();
		Logger.info("获取收货人信息，参数：account={}，json={}", account, json);
		String searchText = JsonCaseUtil.getStringValue(json, "searchText");
		return ok(Json.toJson(saleReceiverService.query(account, searchText)));
	}

	/**
	 * 获取收件人列表
	 * @return
	 */
	@ApiOperation(value = "获取收件人列表-后台使用", notes = "<b>获取收件人列表</b>", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", value = "searchText：搜索条件",
					required = true, paramType = "body", dataType = "body",
					defaultValue = "{\n" +
							"\"searchText\":\"\",\n" +
							"\"account\":\"zhengbc@qq.com\"\n" +
							"}"
			)
	})
	@ALogin
	public Result getReceivers4Backstage() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			return internalServerError("Expecting Json data");
		}
		String account = JsonCaseUtil.getStringValue(json, "account");
		Logger.info("获取收货人信息，参数：account={}，json={}", account, json);
		String searchText = JsonCaseUtil.getStringValue(json, "searchText");
		return ok(Json.toJson(saleReceiverService.query(account, searchText)));
	}
}