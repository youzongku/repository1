package controllers.dismember;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import entity.dismember.DisActive;
import entity.dismember.DisCoupons;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.base.utils.JsonFormatUtils;
import services.dismember.impl.ActiveService;
import services.dismember.impl.LoginService;

/**
 * 
 * Created by zbc on 2016/5/27.
 *
 */
/**
 * @author Administrator
 *
 */
public class ActiveController extends Controller {
	
	@Inject
	private ActiveService activeService;
	
	@Inject
	private LoginService loginService;
	
	/**
	 * 新增优惠活动
	 * @param 参数格式
	 * {
	 *  "couponsName":"端午节优惠活动",
	 *  "couponsCost":50,
	 *  "publishQty":1250,
	 *  "thresholdPrice":0,
	 *   "validDateEndtStr": "2016-05-27 12:00:00",
	 *   "validDateStartStr":"2016-05-30 12:00:00",
	 *   "couponsLenght":8
	 * }
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result addActive(){
		Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(2)) {//后台登录
            Logger.info("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
        }

        JsonNode main = request().body().asJson();
		if (main == null) {
			return internalServerError("Expecting Json data");
		}
		
		DisActive acitive = JsonFormatUtils.jsonToBean(main.toString(), DisActive.class);
		acitive.setCreater(loginService.getLoginContext(2).getEmail());//创建人
		result = activeService.saveActive(acitive);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 分页查询优惠活动
	 * {
	 * "pageSize":10,
	 * "currPage":1,
	 * "seachSpan":"",
	 * "createStartDate":"",
	 * "createEndDate":"",
	 *  "status":
	 * }
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result queryActive(){
		
		Map<String, Object> result = Maps.newHashMap();
        if (!loginService.isLogin(2)) {//后台登录
            Logger.info("当前用户未登录");
            result.put("suc", false);
            result.put("code", "2");
            return ok(Json.toJson(result));
        }
       
		JsonNode main = request().body().asJson();
		if (main == null) {
			return internalServerError("Expecting Json data");
		}

		result.put("suc",true);
		result.put("data",activeService.queryPageActive(main));
		return ok(Json.toJson(result));
	}
	
	/**
	 * 分页查询优惠码
	 * {
	 * "activeId":3
	 * "pageSize":10,
	 * "currPage":1,
	 * "seachSpan":"",
	 * "usedStartDate":"",
	 * "usedEndDate":"",
	 *  "status":
	 * }
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result queryCoupons(){
		Map<String, Object> result = Maps.newHashMap();
       
		JsonNode main = request().body().asJson();
		if (main == null) {
			return internalServerError("Expecting Json data");
		}

		result.put("suc",true);
		result.put("data",activeService.queryPageCoupons(main));
		return ok(Json.toJson(result));
	}
	
	/**
	 * 更新优惠码状态
	 * TODO
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result updateCoupons(){
		JsonNode main = request().body().asJson();
		if (main == null) {
			return internalServerError("Expecting Json data");
		}

		DisCoupons coupons = JsonFormatUtils.jsonToBean(main.toString(), DisCoupons.class);
		Logger.info("更新优惠码信息参数:"+coupons.toString());
		Map<String, Object> result = activeService.updateCoupons(coupons);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 验证优惠码有效性,是否过期，是否已使用过，返回该优惠吗数据
	 * TODO
	 * param  couponsNo
	 * @return
	 */
	public Result getCouponsInfo(String couponsNo,Double orderAmount){
		Map<String, Object> result = activeService.getCouponsInfo(couponsNo,orderAmount);
		return ok(Json.toJson(result));
	}
}
