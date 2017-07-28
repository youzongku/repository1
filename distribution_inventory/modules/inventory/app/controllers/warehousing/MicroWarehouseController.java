package controllers.warehousing;

import com.google.inject.Inject;

import controllers.annotation.Login;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.inventory.IUserService;
import services.warehousing.IMicroWarehouseService;

public class MicroWarehouseController extends Controller {
	
	@Inject
	IMicroWarehouseService microWarehouseService;
	
	@Inject
	IUserService userService;
	
	@Login
	public Result queryMicroWarehouse(String email){
		String mail = userService.getDisAccount();
		if(mail != null){
			email = mail;
		}
		return ok(Json.toJson(microWarehouseService.queryMicroWarehouse(email)));
	}
	
}
