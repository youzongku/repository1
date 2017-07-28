package controllers.dismember;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.dismember.RoleMenuDto;
import entity.dismember.DisMember;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IDisMemberService;
import services.dismember.ILoginService;
import services.dismember.IMenuService;
import vo.dismember.LoginContext;

/**
 * @author Administrator
 *
 */
public class MenuController extends Controller {
	
	@Inject
	private IMenuService menuService;
	@Inject
	private ILoginService loginService;
	@Inject
	private IDisMemberService memberService;
	
	/**
	 * @param ishfive 是否为h5 菜单
     * 获取用户所在角色的功能菜单
     * @return
     */
    public Result getRoleMenuOfUser(boolean ishfive,Integer menuId) {
    	Map<String, Object> result = Maps.newHashMap();
        LoginContext lc = loginService.getLoginContext(2);
        if(lc == null){
            Logger.info("当前用户未登录");
            result.put("success", false);
            result.put("code", 2);
            return ok(Json.toJson(result));
        }

        String email = lc.getEmail();
        DisMember member = memberService.getMember(email);
        if(null != member && member.getRoleId() != 2){
            List<RoleMenuDto> menus = menuService.getMenuByParam(member, ishfive, menuId);
            result.put("success", true);
            result.put("roleMenus", menus);
            return ok(Json.toJson(result));
        }
        
        Logger.info("该用户未设置角色或为非后台用户：" + email);
        result.put("success", false);
        result.put("msg", "当前用户未设置角色或为非后台用户");
        return ok(Json.toJson(result));
    }
    
    /**
     * 检查用户是否有该菜单的操作权限
     * @return
     */
    public Result checkMenuAuthority(){
    	Map<String, Object> result = Maps.newHashMap();
		LoginContext lc = loginService.getLoginContext(2);
		if(lc == null){
			result.put("success", false);
			return ok(Json.toJson(result));
		}
		
		//获取菜单位置进行校验
		JsonNode json = request().body().asJson().path("position");
		Integer position = json!=null?json.asInt():null;
        return ok(menuService.checkMenuAuthority(lc, position));
    }
    
    
    /**
     * 检查用户是否有该菜单的操作权限
     * @return
     */
	public Result createMenu() {
		if (!loginService.isLogin(2)) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("msg", "用户未登陆");
			return ok(Json.toJson(result));
		}
		JsonNode node = request().body().asJson();
		if (null == node || !node.has("parentId") || !node.has("menuName") || !node.has("position")
				|| !node.has("isParent") || !node.has("level")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("msg", "参数不正确");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(menuService.createMenu(node)));
	}
	
	/**
	 * 根据用户与角色ID获取栏目列表
	 * @param roleId
	 * @return
	 * @author huchuyin
	 * @date 2016年9月13日 下午6:02:54
	 */
	public Result getLoginMemMenuList() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			result.put("success", false);
			result.put("code", "2");
			result.put("msg", "用户未登陆");
			return ok(Json.toJson(result));
		}
		JsonNode node = request().body().asJson();
		Logger.info(this.getClass().getName()+" getLoginMemMenuList node==="+node);
		if (null == node || !node.has("roleId")) {
			result.put("success", false);
			result.put("msg", "参数不正确");
			return ok(Json.toJson(result));
		}
		
		result.put("success", true);
		result.put("list", menuService.getLoginMemMenuList(node.get("roleId").asInt()));
		return ok(Json.toJson(result));
	}
	
	/**
	 * 根据用户ID获取用户所关联的栏目信息
	 * @param memberId
	 * @return
	 * @author huchuyin
	 * @date 2016年9月13日 下午7:31:42
	 */
	public Result getMemberMenuList() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			result.put("success", false);
			result.put("code", "2");
			result.put("msg", "用户未登陆");
			return ok(Json.toJson(result));
		}
		JsonNode node = request().body().asJson();
		Logger.info(this.getClass().getName()+" getMemberMenuList node==="+node);
		if (null == node || !node.has("memberId")) {
			result.put("success", false);
			result.put("msg", "参数不正确");
			return ok(Json.toJson(result));
		}
		
		result.put("success", true);
		result.put("list", menuService.getMemberMenuList(node.get("memberId").asInt()));
		return ok(Json.toJson(result));
	}

}
