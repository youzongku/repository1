package controllers.dismember;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import constant.dismember.Constant;
import dto.dismember.ShopDto;
import entity.dismember.DisShop;
import entity.dismember.DisShopDpLog;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IDisShopService;
import services.dismember.ILoginService;
import utils.dismember.JsonCaseUtil;
import utils.dismember.StringUtils;
import vo.dismember.LoginContext;
import vo.dismember.Page;

/**
 * Created by LSL on 2015/12/16.
 */
public class ShopController extends Controller {

    @Inject
    private IDisShopService disShopService;

    @Inject
    private ILoginService loginService;
    
    /**
     * 分页获取当前用户的店铺信息
     * @return
     */
    public Result getAllShops() {
        Map<String, Object> result = Maps.newHashMap();
        JsonNode json = request().body().asJson();
        if (!loginService.isLogin(1)&&(json == null||!json.has("remoteFlag"))) {
            Logger.debug("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
        }
        
        Map<String, String> params = Form.form().bindFromRequest().data();
        params = params == null ? Maps.newHashMap() : params;
        Integer currPage = params.containsKey("currPage") ? Integer.valueOf(params.get("currPage")) : 1;
        Integer pageSize = params.containsKey("pageSize") ? Integer.valueOf(params.get("pageSize")) : 10;
        Integer parentId = params.containsKey("parentId") ? Integer.valueOf(params.get("parentId")) : null;
        Integer type = params.containsKey("type") ? Integer.valueOf(params.get("type")) : null;
        String shopName = params.containsKey("shopName") ? params.get("shopName") : null;
        Map<String, Object> map = Maps.newHashMap();
        map.put("email", loginService.isLogin(1)?loginService.getLoginContext(1).getEmail():json.get("email").asText());
        map.put("pageSize", pageSize);
        map.put("startNum", (currPage - 1) * pageSize);
        map.put("type", type);
        map.put("shopName", shopName);
        if (parentId != null) {
        	map.put("parentId", parentId);
        }
        List<ShopDto> shops = disShopService.getShopsByCondition(map);
        int rows = disShopService.getCountByCondition(map);
        result.put("suc", true);
        result.put("page", new Page<ShopDto>(currPage, pageSize, rows, shops));
        return ok(Json.toJson(result));
    }
    
    /**
     * 获取设置店铺扣点的日志
     * {shopId:1, email: "123@qq.com"}
     * @return
     */
    public Result getDisShopDpLogs(){
    	JsonNode json = request().body().asJson();
    	Map<String, Object> result = Maps.newHashMap();
    	if (json == null 
				|| !json.has("email") 
//				|| !json.has("shopId")
				|| JsonCaseUtil.isJsonEmpty(json.get("email"))
//				|| JsonCaseUtil.isJsonEmpty(json.get("shopId"))
    			) {
			result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
		}

        Logger.info("------------------------>{}", json.get("email"));
		List<DisShopDpLog> logs = disShopService.getDisShopDpLogs(json.has("shopId") ? json.get("shopId").asInt() : -1,
				JsonCaseUtil.jsonToString(json.get("email")));
    	
		result.put("suc", true);
        result.put("result", logs);
        return ok(Json.toJson(result));
    }
    
    /**
     * 设置店铺扣点
     {
     	email:"123@qq.com",
     	shopDps:[
     		{shopId:1, dp: 0.1}, 
     		{shopId:2, dp: 0.2}
     	]
     }
     * @return
     */
    public Result setShopDeductionPoints(){
    	Map<String, Object> result = Maps.newHashMap();
    	JsonNode json = request().body().asJson();
		Logger.info("=====后台设置店铺扣点，参数=========>{}", json);
		if (json == null 
				|| !json.has("email") 
				|| !json.has("shopDps") 
				|| JsonCaseUtil.isJsonEmpty(json.get("email"))) {
			result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
		}
		
		// 店铺id对应店铺扣点
		Map<Integer, Double> shopId2Dp = Maps.newHashMap();
		try {
			for(Iterator<JsonNode> it = json.get("shopDps").iterator(); it.hasNext(); ){
				JsonNode shopIdDpNode = it.next();
				double deductionPoints = shopIdDpNode.get("dp").asDouble();
				if(deductionPoints<0 || deductionPoints>1){
					result.put("suc", false);
					result.put("msg", "店铺扣点数据设置有误");
					return ok(Json.toJson(result));
				}
				shopId2Dp.put(shopIdDpNode.get("shopId").asInt(), deductionPoints);
			}
		} catch (Exception e) {
			result.put("suc", false);
            result.put("msg", "请求参数错误");
            return ok(Json.toJson(result));
		}
		if(shopId2Dp.size()==0){
			result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
		}
		
		LoginContext loginContext = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK);
		if(loginContext==null){
			Logger.debug("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
		}
		String email = JsonCaseUtil.jsonToString(json.get("email"));
		String createUser = loginContext.getEmail();
		result = disShopService.setShopDeductionPoints(email, shopId2Dp, createUser);
		return ok(Json.toJson(result));
    }

    /**
     * 添加新店铺信息
     * @return
     */
    public Result addNewShop() {
        Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(1)) {
            Logger.debug("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
        }
        Map<String, String> params = Form.form().bindFromRequest().data();
        if (params == null) {
            result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
        }
        
        LoginContext context = loginService.getLoginContext(1);
        params.put("email", context.getEmail());
        DisShop shop = disShopService.addNewShop(params,context.getDistributionType());
        if (shop == null) {
            result.put("suc", false);
            result.put("msg", "添加店铺失败");
            return ok(Json.toJson(result));
        }
        
        result.put("suc", true);
        return ok(Json.toJson(result));
    }

    /**
     * 更新指定店铺信息
     * @return
     */
    public Result updateShop() {
        Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(1)) {
            Logger.debug("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
        }
        Map<String, String> params = Form.form().bindFromRequest().data();
        if (params == null) {
            result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
        }
        
        LoginContext context = loginService.getLoginContext(1);
        params.put("email", context.getEmail());
        if (!disShopService.updateShop(params,context.getDistributionType())) {
        	result.put("suc", false);
            result.put("msg", "更新店铺信息失败");
            return ok(Json.toJson(result));
        }
        
        result.put("suc", true);
        return ok(Json.toJson(result));
    }

    /**
     * 删除指定店铺信息
     * @return
     */
    public Result deleteShop() {
        Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(1)) {
            Logger.debug("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
        }
        Map<String, String> params = Form.form().bindFromRequest().data();
        if (params == null || !params.containsKey("sid")) {
            result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
        }
        
        params.put("email", loginService.getLoginContext(1).getEmail());
        if (!disShopService.deleteShop(params)) {
        	result.put("suc", false);
            result.put("msg", "获取店铺信息失败");
            return ok(Json.toJson(result));
        }
        
        result.put("suc", true);
        return ok(Json.toJson(result));
    }
    
    /**
     * 获取指定店铺信息
     * @return
     */
    public Result getShop() {
        Map<String, String> params = Form.form().bindFromRequest().data();
        DisShop shop = disShopService.getShop(params);
        return ok(Json.toJson(shop==null?new DisShop():shop));
    }

    /**
     * 获取所有店铺平台
     * (淘宝、天猫、京东、亚马逊......)
     * @return
     */
    public Result getAllShopPlatforms() {
        return ok(Json.toJson(disShopService.getAllShopPlatforms()));
    }

    /**
     * 获取所有店铺类型
     * (B2B/B2C/C2C/O2O......)
     * @return
     */
    public Result getAllShopCategorys() {
        return ok(Json.toJson(disShopService.getAllShopCategorys()));
    }
    
    /**
     * 根据名称获取店铺对象
     * @param name
     * @return
     */
    public Result shop(String name) {
    	if(StringUtils.isBlankOrNull(name)) {
    		return ok("");
    	}
        if (!loginService.isLogin(1)) {
        	Map<String, Object> result = Maps.newHashMap();
            Logger.debug("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
        }
    	
        return ok(Json.toJson(disShopService.shop(name,loginService.getLoginContext(1).getEmail())));
    }
    
    public Result getAllUserShop(){
    	Map<String, Object> result = Maps.newHashMap();
    	if (!loginService.isLogin(1)) {
            Logger.debug("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
        }
    
    	result.put("email", loginService.getLoginContext(1).getEmail());
    	return ok(Json.toJson(disShopService.getShopsByCondition(result)));
    }
    
    /**
     * 当前用户，检查在相同的平台上是否有相同的店铺名
     * @return
     */
    public Result checkShopName(){
    	JsonNode json = request().body().asJson();
        if (!loginService.isLogin(1)&&(json == null||!json.has("remoteFlag"))) {
            Logger.debug("当前用户未登录");
            Map<String, Object> result = Maps.newHashMap();
            result.put("suc", false);
            result.put("msg", "当前用户未登录");
            return ok(Json.toJson(result));
        }
    	 Map<String, String> params = Form.form().bindFromRequest().data();
         if (params == null || !params.containsKey("type") || !params.containsKey("shopName")) {
        	 Map<String, Object> result = Maps.newHashMap();
        	 result.put("suc", false);
             result.put("msg", "请求参数不存在");
             return ok(Json.toJson(result));
         }
         
		Map<String, Object> param = Maps.newHashMap();
		params.put("email", loginService.isLogin(1) ? loginService.getLoginContext(1).getEmail() : json.get("email").asText());
		param.putAll(params);
		return ok(Json.toJson(disShopService.checkShopName(param)));
    }
    
    /**
     * 查询所有店铺，不带任何条件
     * @return
     */
    public Result getAllStore(String email){
    	return ok(Json.toJson(disShopService.getAllShop(email)));
    }
}
