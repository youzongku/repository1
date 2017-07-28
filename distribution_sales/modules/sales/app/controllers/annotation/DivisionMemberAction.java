package controllers.annotation;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import play.Logger;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import session.ISessionService;
import util.sales.HttpUtil;
import util.sales.JsonCaseUtil;

public class DivisionMemberAction extends Action<DivisionMember>{

	@Inject
	private ISessionService sessionService;
	@Override
	public Promise<Result> call(Context context) throws Throwable {
		try {
			JsonNode asJson = context.request().body().asJson();
			if(asJson != null && asJson.has("email")) {
				sessionService.remove("account");
				return delegate.call(context);
			}
			String response = HttpUtil.post(Json.toJson(Maps.newHashMap()).toString(),
					HttpUtil.B2BBASEURL + "/member/relatedMember", context);
			JsonNode node = Json.parse(response);
			//业务员关联分销商品
			if(node.has("mark") && node.get("mark").asInt() == 3) {
				String accounts = "change_email,";//初始化值
				JsonNode members = node.get("data").get("list");
				for (JsonNode member : members) {
					if (!JsonCaseUtil.isJsonEmpty(member)) {
						accounts += JsonCaseUtil.getStringValue(member, "email") + ",";			
					}
				}
				sessionService.set("account", accounts.substring(0, accounts.length()-1));					
			} else {
				sessionService.remove("account");
			}
		} catch (Exception e) {
			Logger.error("业务员查询失败，" + e);
		}
		return delegate.call(context);
	}

}
