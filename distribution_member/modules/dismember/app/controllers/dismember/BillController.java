package controllers.dismember;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import constant.dismember.Constant;
import controllers.annotation.ALogin;
import entity.dismember.DisBill;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IDisBillService;
import services.dismember.IDisSalesmanService;
import services.dismember.ILoginService;

/**
 * Created by LSL on 2016/1/5.
 */
public class BillController extends Controller {

	@Inject
	private IDisBillService billService;

	@Inject
	private ILoginService loginService;
	
	@Inject
	private IDisSalesmanService salesManService;

	/**
	 * 获取用户交易记录
	 *
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result queryPagedBills() {
		JsonNode node = request().body().asJson(); 
		Logger.info("交易记录查询参数:{}", node);
		if (!loginService.isLogin(1) || node == null) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);// 未登陆
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(billService.getPagedBills(node.toString(),
				loginService.getLoginContext(1).getEmail(),null)));
	}

	/**
	 * 创建交易记录
	 *
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result createBill() {
		Form<DisBill> form = Form.form(DisBill.class).bindFromRequest();
		DisBill bill = form.get();
		if (null == bill) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "获取参数失败");
			return ok(Json.toJson(result));
		}
		
		Logger.info("createBill ---> " + bill.toString());
		return ok(Json.toJson(billService.createBill(bill)));
	}

	public Result getBill(Integer id){
		return ok(Json.toJson(billService.getBill(id)));
	}
	
	
	@ALogin
	public Result backQueryBills(){
		Map<String, Object> result = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		Logger.info("交易记录查询参数:{}", node);
		if (node == null) {
			result.put("success", false);// 未登陆
			return ok(Json.toJson(result));
		}

		result = billService.getPagedBills(node.toString(), null,
				salesManService.relateAccounts(loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK).getEmail()));
		return ok(Json.toJson(result));
	}
	
}
