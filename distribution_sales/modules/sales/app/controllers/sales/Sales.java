package controllers.sales;

import play.mvc.Controller;
import play.mvc.Result;
import services.sales.ISalesPushToB2CService;

import com.google.inject.Inject;

import entity.sales.enums.TimerExceType;

public class Sales extends Controller{

	@Inject private	ISalesPushToB2CService iSalesPushToB2CService;
	
	/**
	 * 预留接口，测试bbc销售订单推送到b2c
	 * @return
	 */
	public Result pushSalesToB2C(){
		iSalesPushToB2CService.pushSales(TimerExceType.B2B_SALES_2_B2C.name());
		return ok("SUCESS");
	}
	
}
