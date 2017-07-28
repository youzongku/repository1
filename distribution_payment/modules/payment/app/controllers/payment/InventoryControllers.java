package controllers.payment;

import java.util.Map;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.base.utils.StringUtils;
import services.payment.IDealInventoryService;
import utils.payment.HttpUtil;

public class InventoryControllers extends Controller{
	
	
	@Inject
    private IDealInventoryService inventoryService;
	
	public Result callback(String no,Double amount,String flag,String sid) {
		Logger.info("易极付支付订单成功回调：" + no);
		ObjectNode result = Json.newObject();
		String url =  "";
        Map<String, String[]> params = request().queryString();
        if (params == null || params.size() == 0) {
            result.put("suc", false);
            result.put("msg", "请求参数不存在或格式错误");
        } else {
            try {
                Logger.debug("==============易极付同步返回==============");
                Logger.debug("receiveSyncNotify    params--->" + Json.toJson(params).toString());
                
                Map<String,Object> resMap = inventoryService.receiveSyncReturn(params, result,no,amount,flag,sid);
                Boolean isok = result.get("isok").asBoolean();
                if(resMap.get("frontPay")!=null&&(Boolean)resMap.get("frontPay")){
                	url = HttpUtil.BBC_HOST+"/product/pay-success.html?isok="+isok+"&od="+no;
                	if(isok){
                		url += "&transamount="+amount;
                	}
                }else{
            	   url =  HttpUtil.BBC_HOST + "/backstage/pay_success.html?isok="+isok;
                   if(isok) {
                	   url += "&transamount="+amount;
                   }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.debug("receiveSyncNotify    处理同步返回参数发生异常", e);
                result.put("suc", false);
            }
        }
		return redirect(url);
	}
	 
	/**
	 * 缺货采购支付成功后续操作
	 * @param orderNo
	 * @return
	 */
	public Result call(String orderNo,String total) {
		Logger.info("处理单号为：" + orderNo);
		if(StringUtils.isEmpty(orderNo)) {
			return ok("fail");
		}
		inventoryService.callback(orderNo,total,"支付",null);
		return ok("success");
	}

}
