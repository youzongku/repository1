package controllers.dismember;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import entity.dismember.CommonField;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.ICommonFieldService;
import services.dismember.ILoginService;

/**
 * 全局变量控制类
 *
 */
public class CommonFieldController extends Controller {
	@Inject
	private ICommonFieldService commonFieldService;

	@Inject
	private ILoginService loginService;

	/**
	 * 增加或更新全局变量
	 */
	public Result saveOrUpdate() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!loginService.isLogin(2)) {
			resultMap.put("success", false);
			return ok(Json.toJson(resultMap));
		}
		
		JsonNode json = request().body().asJson();
		CommonField commonField = Json.fromJson(json, CommonField.class);
		boolean flag = commonFieldService.saveOrUpdateField(commonField);
		resultMap.put("success", true);
		resultMap.put("flag", flag);
		return ok(Json.toJson(resultMap));
	}

	/**
	 * 通过id或变量名称获得全局变量信息
	 * 
	 * @return
	 */
	public Result getCommonField() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!loginService.isLogin(2)) {
			resultMap.put("success", false);
			return ok(Json.toJson(resultMap));
		}
		
		JsonNode json = request().body().asJson();
		JsonNode idJson = json.path("id");
		if (idJson != null && !"".equals(idJson.asText())) {
			resultMap.put("commonField", commonFieldService.getCommonFieldById(idJson.asInt()));
			resultMap.put("success", true);
			return ok(Json.toJson(resultMap));
		}
		
		String name = json.path("name").asText();
		CommonField commonField = commonFieldService.getCommonFieldByName(name);
		if (commonField == null) {
			commonField = new CommonField();
			commonField.setName(name);
			commonFieldService.saveOrUpdateField(commonField);
			commonField = commonFieldService.getCommonFieldByName(name);
		}
		resultMap.put("commonField", commonField);
		resultMap.put("success", true);
		return ok(Json.toJson(resultMap));
	}
}
