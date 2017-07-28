package controllers.dismember;

import java.util.Map;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.base.utils.JsonFormatUtils;
import services.dismember.IDisMemberService;
import services.dismember.ILoginService;
import services.dismember.IPaymentMethodService;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.dismember.PaymentMethodDto;

/**
 * @author xuse
 * 2016年12月6日
 */
public class PaymentMethodController extends Controller{
	
	@Inject
	private IPaymentMethodService paymentMethodService;
	@Inject
	private ILoginService loginService;
	@Inject
	private IDisMemberService disMemberService;
	
	/**
	 * 前台获取支付方式
	 * 参数：登陆用户获得：模式、分销商类型--传入：用途（1充值，2采购，3销售）   foreground字段为true
	 * xuse
	 * 2016年12月6日
	 * @return
	 */
	public Result foregroundMethod(Integer purpose) {
		if(!loginService.isLogin(1)){
			Map<String,Object> res = Maps.newHashMap();
			res.put("suc", false);
			res.put("msg", "用户未登录");
			return ok(Json.toJson(res));
		}
		
		return ok(Json.toJson(paymentMethodService.read(purpose,
				disMemberService.getMember(loginService.getLoginContext(1)
						.getEmail()), true)));
	}

	/**
	 * 后台获取支付方式
	 * 参数：分销商账号、用途（1充值，2采购，3销售）、backstage字段为true
	 * xuse
	 * 2016年12月6日
	 * @return
	 */
	public Result backgroundMethod(Integer purpose,String email) {
		if(!loginService.isLogin(1)){
			Map<String,Object> res = Maps.newHashMap();
			res.put("suc", false);
			res.put("msg", "用户未登录");
		}
		
		return ok(Json.toJson(paymentMethodService.read(purpose,
				disMemberService.getMember(email), false)));
	}
	/**
	 * 新增支付方式映射
	 * 参数：模式、分销商类型、用途（1充值，2采购，3销售）、backstage、foreground、支付方式id
	 * {
	 	"model":1,
	 	"disType":1,
	 	"backstage":true,
	 	"foreground":true,
	 	"purpose":1,
	 	"methodids":[1,2,3,4]
	 	}
	 * 
	 * xuse
	 * 2016年12月6日
	 * @return
	 */
	public Result addMapping() {
		if(!loginService.isLogin(2)){
			return internalServerError("用户未登录");
		}
		JsonNode node = request().body().asJson();
		Logger.info("新增/更新支付方式映射参数[{}]",node);
		PaymentMethodDto condit = null;
		try {
			if(node == null){
				throw new RuntimeException("参数为空");
			}else if(!node.has("model") ||!node.has("disType")||!node.has("backstage")
					||!node.has("foreground") || !node.has("purpose")||!node.has("methodids")){
				throw new RuntimeException("参数缺失");
			}else{
				condit = JsonFormatUtils.jsonToBean(node.toString(), PaymentMethodDto.class);
			}
		} catch (Exception e) {
			Logger.info("参数错误",e);
		}
		if(condit == null){
			return internalServerError("参数错误");
		}
		
		return ok(Json.toJson(paymentMethodService.create(condit,loginService.getLoginContext(2).getEmail())));
	}
	
	/**
	 * 该接口不开放外部调用，仅用于查看数据
	 * TODO 查看所有支付方式 
	 * @author zbc
	 * @since 2016年12月6日 上午11:35:08
	 */
	public Result readAllMethod(){
		return ok(Json.toJson(paymentMethodService.readAllMethod()));
	}
}
