package controllers.dismember;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import entity.dismember.DisProvince;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IDisAreaService;
import services.dismember.IDisCityService;
import services.dismember.IDisProvinceService;
import utils.dismember.StringUtils;

/**
 * Created by LSL on 2015/12/21.
 */
public class ProvinceController extends Controller {

    @Inject
    private IDisProvinceService disProvinceService;
    @Inject
    private IDisCityService disCityService;
    @Inject
    private IDisAreaService disAreaService;

    /**
     * 获取所有省份
     * @return
     */
    public Result getAllProvinces() {
        return ok(Json.toJson(disProvinceService.getAllProvinces()));
    }

    /**
     * 获取指定省份下的城市
     * @return
     */
    public Result getCitiesByProvince() {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, String> params = Form.form().bindFromRequest().data();
        if (params == null || !params.containsKey("proId")) {
            result.put("success", false);
            result.put("message", "请求参数不存在");
            return ok(Json.toJson(result));
        }
      
        result.put("success", true);
        result.put("cities", disCityService.getCitiesByProvince(Integer.valueOf(params.get("proId"))));
        return ok(Json.toJson(result));
    }

    /**
     *
     *获取所有地区
     * @return
     */
    public Result getAllAreas(){
        return ok(Json.toJson(disAreaService.getAllAreas()));
    }
    
    /**
     * 获取所有城市
     */
    public Result getAllCities(){
    	return ok(Json.toJson(disCityService.getAllCities()));
    }
    
    /**
     *  获取指定城市下的区/县/县级市
     * 
     * @return
     */
    public Result getAreasByCity() {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, String> params = Form.form().bindFromRequest().data();
        if (params == null || !params.containsKey("cityId")) {
            result.put("success", false);
            result.put("message", "请求参数不存在");
            return ok(Json.toJson(result));
        }
        
        result.put("success", true);
        result.put("areas", disAreaService.getAreasByCity(Integer.valueOf(params.get("cityId"))));
        return ok(Json.toJson(result));
    }
    
    /**
     * 模糊查询省ID
     * @return
     */
    public Result getProvinces(String key) {
    	if(StringUtils.isBlankOrNull(key)){
    		return ok("");
    	}
    	DisProvince province = disProvinceService.getProvinces(key);
    	if(province == null ){
    		return ok("");
    	}
        return ok(Json.toJson(province));
    }
    
    public Result addarea(String name,Integer cityId) {
    	if(StringUtils.isBlankOrNull(name) || StringUtils.isBlankOrNull(cityId)) {
    		return ok("false");
    	}

    	disAreaService.addArea(name,cityId);
    	return ok("success");
    }
    
    public Result getChinaArea() {
    	JsonNode node = request().body().asJson();
    	if (node == null || node.size() == 0) {    		
    		ObjectNode result = Json.newObject();
    		result.put("suc", false);
    		result.put("msg", "参数错误");
    		return ok(result.toString());
    	}
    	
    	String param = node.toString();
    	return ok(disProvinceService.getChinaArea(param));
    }

}
