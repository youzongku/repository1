package controllers.dismember;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.dismember.MemberForm;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IDisMemberService;
import services.dismember.IDisRankService;
import services.dismember.ILoginService;

/**
 * Created by LSL on 2016/2/13.
 */
public class RankController extends Controller {

    @Inject
    private IDisRankService disRankService;
    @Inject
    private ILoginService loginService;
    @Inject
    private IDisMemberService memberService;

    /**
     * 获取所有等级数据
     * @return
     */
    public Result gainAllRanks() {
        Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(2)) {
            Logger.info("当前用户未登录或登录超时");
            result.put("suc", false);
            result.put("code", 1);
            return ok(Json.toJson(result));
        }

        result.put("suc", true);
        result.put("data", disRankService.getAllRanks());
        return ok(Json.toJson(result));
    }

    /**
     * 分页获取等级数据
     * @return
     */
    public Result getRanksByPage() {
        Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(2)) {
            Logger.info("当前用户未登录或登录超时");
            result.put("suc", false);
            result.put("code", 1);
            return ok(Json.toJson(result));
        }
        Map<String, String> params = Form.form().bindFromRequest().data();
        if (params == null || !params.containsKey("currPage") || !params.containsKey("pageSize")) {
            Logger.info("请求参数不存在或格式错误");
            result.put("suc", false);
            result.put("code", 2);
            return ok(Json.toJson(result));
        }
        
        Map<String, Object> temp = Maps.newHashMap();
        Integer currPage = Strings.isNullOrEmpty(params.get("currPage")) ? 1 : Integer.valueOf(params.get("currPage"));
        Integer pageSize = Strings.isNullOrEmpty(params.get("pageSize")) ? 10 : Integer.valueOf(params.get("pageSize"));
        temp.put("currPage", currPage);
        temp.put("startNum", (currPage - 1) * pageSize);
        temp.put("pageSize", pageSize);
        result.put("suc", true);
        result.put("page", disRankService.getRanksByPage(temp));
        return ok(Json.toJson(result));
    }

    /**
     * 新增或更新等级数据
     * sign:1——>新增，sign:2——>更新
     * @return
     */
    public Result addOrUpdateRank() {
        Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(2)) {
            Logger.info("当前用户未登录或登录超时");
            result.put("suc", false);
            result.put("code", 1);
            return ok(Json.toJson(result));
        }
        Map<String, String> params = Form.form().bindFromRequest().data();
        if (params == null || !params.containsKey("sign")) {
            Logger.info("请求参数不存在或格式错误");
            result.put("suc", false);
            result.put("code", 2);
            return ok(Json.toJson(result));
        }
        
        params.put("user", loginService.getLoginContext(2).getUsername());
        int flag = disRankService.addOrUpdateRank(params);
        if (flag == 1) {
            result.put("suc", true);
            return ok(Json.toJson(result));
        } 
        
        if (flag == 2) {
            Logger.info("新增或更新等级数据失败");
            result.put("suc", false);
            result.put("code", 3);
            return ok(Json.toJson(result));
        } 
        
        if (flag == 3) {
            Logger.info("等级或折扣已存在");
            result.put("suc", false);
            result.put("code", 4);
            return ok(Json.toJson(result));
        }
        
        return ok(Json.toJson(result));
    }

    /**
     * 删除指定等级数据
     * @return
     */
    public Result deleteRankData() {
        Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(2)) {
            Logger.info("当前用户未登录或登录超时");
            result.put("suc", false);
            result.put("code", 1);
            return ok(Json.toJson(result));
        }
        Map<String, String> params = Form.form().bindFromRequest().data();
        if (params == null || !params.containsKey("rid")) {
            Logger.info("请求参数不存在或格式错误");
            result.put("suc", false);
            result.put("code", 2);
            return ok(Json.toJson(result));
        }
        
        Logger.info("deleteRank params--->" + params.toString());
        Integer id = Strings.isNullOrEmpty(params.get("rid")) ? null : Integer.valueOf(params.get("rid"));
        if (!disRankService.deleteRankById(id)) {
        	Logger.info("删除指定等级数据失败");
            result.put("suc", false);
            result.put("code", 3);
            return ok(Json.toJson(result));        	
        }
        
        result.put("suc", true);
        return ok(Json.toJson(result));
    }

    /**
     * 更新指定用户的等级
     * @return
     */
    @BodyParser.Of(BodyParser.Json.class)
    public Result updateUserRank() {
        Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(2)) {
            Logger.info("当前用户未登录或登录超时");
            result.put("suc", false);
            result.put("code", 1);
            return ok(Json.toJson(result));
        }
        JsonNode node = request().body().asJson();
        if (node == null || !node.has("rid") || !node.has("uids")) {
            Logger.info("请求参数不存在或格式错误");
            result.put("suc", false);
            result.put("code", 2);
            return ok(Json.toJson(result));
        }
        
        //更新用户自身邀请码
        Integer userId = node.get("uids").findValues("uid").get(0).asInt();
        JsonNode inviteCodeJson = node.get("registerInviteCode");
        JsonNode erpAccountJson = node.get("erpAccount");
        //如果用id以及注册邀请码不为空则更新用户邀请码信息
        if (userId!=null) {
        	MemberForm memberForm = new MemberForm();
        	memberForm.setId(userId);
        	memberForm.setRegisterInviteCode(inviteCodeJson.asText());
        	memberForm.setErpAccount(erpAccountJson.asText());
        	JsonNode resultJson = memberService.updateInfo(memberForm);
        	Logger.info("update inviteCode result:"+resultJson.toString());
		}
        boolean isCustomized = node.get("isCustomized").asBoolean();
        //如果定制折扣，则校验是否有和定制的折扣值相等的等级折扣，有则返回
        if (isCustomized) {
        	Integer customizeDiscount = node.get("customizeDiscount").asInt();
			result.put("suc", true);
			Map<String, Object> dataMap = disRankService.checkIsExistOfRank(0, null, customizeDiscount);
			boolean isFlag = (boolean) dataMap.get("isFlag");
			if (isFlag) {
				result.put("isFlag", isFlag);
				result.put("data", dataMap);
				return ok(Json.toJson(result));
			}
		}
        
        ((ObjectNode) node).put("operator", loginService.getLoginContext(2).getEmail());
        if (!disRankService.updateUserRank(node)) {
        	Logger.info("更新指定用户的等级失败");
            result.put("suc", false);
            result.put("code", 3);
            return ok(Json.toJson(result));
        }
        
        result.put("suc", true);
        result.put("isFlag", false);
        return ok(Json.toJson(result));
    }

    /**
     * 获取指定用户的等级变更历史
     * @return
     */
    public Result gainUserRankHistory() {
        Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(2)) {
            Logger.info("当前用户未登录或登录超时");
            result.put("suc", false);
            result.put("code", 1);
            return ok(Json.toJson(result));
        }
        Map<String, String> params = Form.form().bindFromRequest().data();
        if (params == null || !params.containsKey("email")) {
            Logger.info("请求参数不存在或格式错误");
            result.put("suc", false);
            result.put("code", 2);
            return ok(Json.toJson(result));
        }
        
        Logger.info("gainUserRankHistory params--->" + params.toString());
        result.put("suc", true);
        result.put("data", disRankService.getURHsByEmail(params.get("email")));
        return ok(Json.toJson(result));
    }
    
    /**
     * 描述：校验等级名称，等级折扣并返回校验结果以及相关等级信息
     * @return
     */
    public Result checkRank(){
    	Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(2)) {
            result.put("suc", false);
            result.put("code", "当前用户未登录或登录超时");
            return ok(Json.toJson(result));
        }
        
        JsonNode json = request().body().asJson();
        String rankName = json.path("rankName").asText();
        Integer discount = json.path("discount").asInt();
        Integer id = json.path("rid").asInt();
        result.put("suc", true);
        result.put("data", disRankService.checkIsExistOfRank(id,rankName, discount));
        return ok(Json.toJson(result));
    }

}
