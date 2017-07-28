package controllers.dismember;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import entity.dismember.DisMember;
import entity.dismember.FindPasswordRecord;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IFindPasswordByEmailService;
import utils.dismember.HttpUtil;
import utils.dismember.IDUtils;

/**
 * 描述：找回密码控制类
 * 
 * @author hanfs
 */
public class FindPasswordByEmailController extends Controller {

	@Inject
	private IFindPasswordByEmailService findPasswordByEmailService;

	/**
	 * 描述：检查邮箱是否存在
	 * 
	 * @param email
	 * @return
	 */
	public Result checkEmail(String email) {
		boolean flag = false;
		if (email != null && !"".equals(email)) {
			flag = findPasswordByEmailService.checkEmail(email);
		}
		return ok(Json.toJson(flag));
	}

	/**
	 * 描述：显示重置密码页面
	 * 
	 * @param email
	 * @param decode
	 * @return
	 */
	public Result showResetPasswordPage(String id, String email, String decode) {
		if (id != null && !id.equals("")) {
			DisMember dis = new DisMember();
			dis.setId(Integer.valueOf(id));
			return ok("");
		}
		
		Map<String, Object> resultMap = findPasswordByEmailService.checkEmailAndDecode(email, decode);
		// 当前邮箱对应的分销商信息
		DisMember disMember = (DisMember) resultMap.get("disMember");
		// 如果当天找回密码次数大于3次则跳转到当天不能进行找回密码操作页面
		// 当天找回密码次数
		int recordCount = (int) resultMap.get("recordCount");
		if (recordCount > 3) {
			boolean overCount = true;
			// nginx远程调用返回
			if ("post".equalsIgnoreCase(request().method().toString())) {
				return ok(Json.toJson(overCount));
			}
			return redirect(HttpUtil.getHostUrl());
		}
		
		// 邮箱验证成功跳转到重置密码页面，否则跳转到验证失败页面
		// 验证是否通过
		boolean isRight = (boolean) resultMap.get("isRight");
		if (isRight) {
			// nginx远程调用返回
			if ("post".equalsIgnoreCase(request().method().toString())) {
				return ok(Json.toJson(disMember));
			}
			return redirect(HttpUtil.getUrl() + "/personal/find_password_reset.html?d=" + decode + "&m=" + email);// 跳转到重置密码页面
		}
		
		// nginx远程调用返回
		if ("post".equalsIgnoreCase(request().method().toString())) {
			return ok(Json.toJson(isRight));
		}
		return redirect(HttpUtil.getHostUrl());// 跳转到验证失败页面
	}

	/**
	 * 描述：修改密码
	 * 
	 * @param {id:分销商id,password:密码}
	 * @return 是否成功修改（false:失败，true:成功）
	 */
	public Result resetPassword() {
		DynamicForm dForm = Form.form().bindFromRequest();
		Integer id = null;
		String email = null;
		if (dForm.get("id") != null) {
			id = Integer.valueOf(dForm.get("id").toString());
		}
		if (dForm.get("email") != null) {
			email = dForm.get("email").toString();
		}
		String password = dForm.get("password").toString();
		Map<String, Object> res = findPasswordByEmailService.resetPassword(email, id,
				utils.dismember.MD5Util.MD5Encode(password, "utf-8"));
		if ("post".equalsIgnoreCase(request().method().toString())) {
			return ok(Json.toJson(res));
		}
		return ok("");
	}

	/**
	 * 描述：发送找回密码邮件,发送成功之后跳转到重置密码页面，否则仍然处于当前页面并给出提示信息
	 * 
	 * @return
	 */
	public Result sendEmailForFindPassword() {
		if ("post".equalsIgnoreCase(request().method().toString())) {
			JsonNode json = request().body().asJson();
			String email = json.path("email").asText();
			List<FindPasswordRecord> allRecordOfToday = findPasswordByEmailService.getAllRecordOfToday(email);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			if (allRecordOfToday != null && allRecordOfToday.size() > 3) {
				resultMap.put("recordOverflag", true);
				return ok(Json.toJson(resultMap));
			}
			
			String key = IDUtils.getUUID();
			String decode = utils.dismember.MD5Util.MD5Encode(email + key, "utf-8");
			String path = "/member/showResetPasswordPage?email=" + email + "&decode=" + decode + "&id=";
			String url = HttpUtil.getHostUrl() + path;
			Logger.info("find password url : " + url);
			boolean sendEmailFlag = findPasswordByEmailService.sendEmail(email, key, decode, url);
			resultMap.put("recordOverflag", false);
			resultMap.put("url", url);
			resultMap.put("sendEmailFlag", sendEmailFlag);
			return ok(Json.toJson(resultMap));
		}
		
		DynamicForm df = Form.form().bindFromRequest();
		String email = df.get("email").toString();
		String key = IDUtils.getUUID();
		String decode = utils.dismember.MD5Util.MD5Encode(email + key, "utf-8");
		String path = "/member/showResetPasswordPage?email=" + email + "&decode=" + decode + "&id=";
		String url = HttpUtil.getHostUrl() + path;
		Logger.info("find password url : " + url);
		findPasswordByEmailService.sendEmail(email, key, decode, url);
		return ok("");
	}

	/**
	 * 根据decode获取找回密码记录
	 * 
	 * @return
	 */
	public Result getRecord() {
		Map<String, String> params = Form.form().bindFromRequest().data();
		return ok(Json.toJson(findPasswordByEmailService.getResetRecord(params)));
	}
}
