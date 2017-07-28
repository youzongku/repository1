package controllers.dismember;

import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.Maps;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.ILoginService;
import services.dismember.IMenuService;
import vo.dismember.LoginContext;

public class MenuTreeControllers extends Controller{
	
	@Inject
	private IMenuService menuService;
	
	@Inject
	private ILoginService loginService;
	
	/**
	 * 查询菜单栏目节点
	 * @return
	 */
	public Result initTree(Integer roleId){
		Map<String, Object> result = Maps.newHashMap();
		LoginContext lc = loginService.getLoginContext(2);
		if(lc == null){
			//return internalServerError("用户未登陆。");
			result.put("success", false);
			result.put("code", "2");
			result.put("msg", "用户未登陆");
			return ok(Json.toJson(result));
		}
		
        Integer userId = Integer.valueOf(lc.getUserID());
        result.put("success", true);
        result.put("list", menuService.getChildNodes(null,roleId, userId));
		return ok(Json.toJson(result));
	}
	
	/**
	 * 配置权限
	 * @return
	 */
	public Result configure(){
		Map<String, Object> result = Maps.newHashMap();
		if(!loginService.isLogin(2)){
			result.put("success", false);
			return ok(Json.toJson(result));
		}
		
		Map<String, String[]> json = request().body().asFormUrlEncoded();
		return ok(Json.toJson(menuService.dealConfigure(json)));
	}
	
}
