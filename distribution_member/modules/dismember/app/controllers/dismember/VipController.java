package controllers.dismember;

import com.google.inject.Inject;

import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IVipService;

/**
 * vip邀请码控制类
 * @author zbc
 * 2016年12月17日 上午9:51:07
 */
public class VipController extends Controller {
	
	@Inject
	private IVipService vipService;

	/**
	 * 创建vip邀请码
	 * @author zbc
	 * @since 2016年12月17日 上午10:23:52
	 */
	public Result create(Integer num){
		return ok(vipService.create(num));
	}
	
}
