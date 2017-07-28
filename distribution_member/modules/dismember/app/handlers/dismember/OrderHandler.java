package handlers.dismember;



import com.google.common.eventbus.Subscribe;

import entity.dismember.DisMember;
import play.Logger;
import play.libs.Json;
import utils.dismember.HttpUtil;

public class OrderHandler {

	/**
	 * 修改昵称同步到销售单采购单的昵称
	 * @author lzl
	 * @since 2016年12月22日下午6:22:03
	 */
	@Subscribe
	public void execute(DisMember member){
		String res = "";
		try {
			res = HttpUtil.httpPost(Json.toJson(member).toString(), HttpUtil.getHostUrl()+"/purchase/changeNickName");
			Logger.info("changeNickName(purchase) ----->" + Json.toJson(res).toString());
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		try {
			res = HttpUtil.httpPost(Json.toJson(member).toString(), HttpUtil.getHostUrl()+"/sales/changeNickName");
			Logger.info("changeNickName(sales) ----->" + Json.toJson(res).toString());
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	}
}
