package controllers.dismember;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import entity.dismember.DisAddress;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IDisAddressService;
import services.dismember.ILoginService;

import java.util.List;
import java.util.Map;

/**
 * Created by LSL on 2015/12/25.
 */
public class AddressController extends Controller {

    @Inject
    private ILoginService loginService;

    @Inject
    private IDisAddressService disAddressService;

    /**
     * 根据条件获取地址信息
     * @return
     */
    public Result getAddresses() {
        Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(1)) {
            Logger.info("当前用户未登录");
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
        
        params.put("email", loginService.getLoginContext(1).getEmail());
        List<DisAddress> addresses = disAddressService.getAddresses(params);
        if (addresses == null || addresses.size() == 0) {
            result.put("suc", false);
            result.put("msg", "暂无地址信息");
            return ok(Json.toJson(result));
        }
        
        result.put("suc", true);
        result.put("adds", addresses);
        return ok(Json.toJson(result));
    }

    /**
     * 新增地址信息
     * @return
     */
    public Result addAddress() {
        Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(1)) {
            Logger.info("当前用户未登录");
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
        
        params.put("email", loginService.getLoginContext(1).getEmail());
        DisAddress address = disAddressService.addAddress(params);
        if (address == null) {
            result.put("suc", false);
            result.put("msg", "新增地址信息失败");
            return ok(Json.toJson(result));
        }
        
        result.put("suc", true);
        result.put("adds", address);
        return ok(Json.toJson(result));
    }

    /**
     * 更新指定地址信息
     * @return
     */
    public Result updateAddress() {
        Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(1)) {
            Logger.info("当前用户未登录");
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
        
        params.put("email", loginService.getLoginContext(1).getEmail());
        boolean updateResult = disAddressService.updateAddress(params);
        if (!updateResult) {
        	result.put("suc", false);
            result.put("msg", "更新指定地址信息失败");
            return ok(Json.toJson(result));
        }
        
        result.put("suc", true);
        return ok(Json.toJson(result));
    }

    /**
     * 删除指定地址信息
     * @return
     */
    public Result deleteAddress() {
        Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(1)) {
            Logger.info("当前用户未登录");
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
        
        Integer id = params.containsKey("aid") ? Integer.valueOf(params.get("aid")) : null;
        boolean deleteResult = disAddressService.deleteAddress(id);
        if (!deleteResult) {
        	result.put("suc", false);
            result.put("msg", "删除指定地址信息失败");
            return ok(Json.toJson(result));
        }
        
        result.put("suc", true);
        return ok(Json.toJson(result));
    }

}
