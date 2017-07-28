package controllers.dismember;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IDisRoleService;
import services.dismember.ILoginService;
import vo.dismember.LoginContext;

import java.util.Map;

/**
 * @Author LSL on 2016-09-14 15:32:57
 */
public class RoleController extends Controller {

    @Inject
    private IDisRoleService disRoleService;
    @Inject
    private ILoginService loginService;

    /**
     * 获取所有角色
     * @return
     */
    public Result getAllRoles() {
        LoginContext lc = loginService.getLoginContext(2);
        if (lc == null) {
        	Map<String, Object> result = Maps.newHashMap();
            Logger.info("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
        }
        JsonNode params = request().body().asJson();
        if (params == null) {
        	Map<String, Object> result = Maps.newHashMap();
            result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
        }
        
        JSONObject json = JSON.parseObject(params.toString());
        Logger.debug("getAllRoles    json----->" + json.toString());
        json.put("createUser", lc.getEmail());
        json.put("userId", lc.getUserID());
        return ok(disRoleService.getRolesByPage(json));
    }

    /**
     * 获取角色下拉值
     * @return
     */
    public Result getRoleNames(){
        LoginContext lc = loginService.getLoginContext(2);
        if (lc == null) {
        	Map<String, Object> result = Maps.newHashMap();
            Logger.info("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
        }
        
        JSONObject json = new JSONObject();
        json.put("createUser", lc.getEmail());
        json.put("userId", lc.getUserID());
    	return ok(disRoleService.getRolesByPage(json));
    }
    
    /**
     * 得到指定的角色
     */
    public Result getRoleById(){
    	if (!loginService.isLogin(2)){
    		Map<String, Object> result = Maps.newHashMap();
    		Logger.info("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
    		return ok(Json.toJson(result));
    	}
        JsonNode params = request().body().asJson();
    	if (params == null) {
    		Map<String, Object> result = Maps.newHashMap();
    		result.put("suc", false);
    		result.put("msg", "请求参数错误");
    		return ok(Json.toJson(result));
    	}
    	
        JSONObject json = JSON.parseObject(params.toString());
        Logger.debug("getRoleById    json----->" + json.toString());
    	return ok(disRoleService.getRoleById(json));
    }
    
    /**
     * 添加新角色
     * @return
     */
    public Result addNewRole() {
        LoginContext lc = loginService.getLoginContext(2);
        if (lc == null) {
        	Map<String, Object> result = Maps.newHashMap();
        	Logger.info("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
        }
        JsonNode params = request().body().asJson();
        if (params == null) {
        	Map<String, Object> result = Maps.newHashMap();
            result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
        }
        
        JSONObject json = JSON.parseObject(params.toString());
        json.put("createUser", lc.getEmail());
        return ok(disRoleService.addDisRole(json));
    }

    /**
     * 更新指定角色信息
     * @return
     */
    public Result updateRole() {
        if (!loginService.isLogin(2)) {
        	Map<String, Object> result = Maps.newHashMap();
        	Logger.debug("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
        }
        JsonNode params = request().body().asJson();
        if (params == null) {
        	Map<String, Object> result = Maps.newHashMap();
            result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
        }
        
        JSONObject json = JSON.parseObject(params.toString());
        return ok(disRoleService.updateDisRole(json));
    }

    /**
     * 删除指定角色
     * @return
     */
    public Result deleteRole(Integer rid) {
        LoginContext lc = loginService.getLoginContext(2);
        if (lc == null) {
        	Map<String, Object> result = Maps.newHashMap();
        	Logger.debug("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
        }
        
        Integer userId = Integer.valueOf(lc.getUserID());
        return ok(disRoleService.deleteDisRole(rid, userId));
    }

}
