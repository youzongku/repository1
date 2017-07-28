package controllers.warehousing;

import com.google.inject.Inject;

import controllers.annotation.ALogin;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.warehousing.IMicroWarehouseService;

public class ManMicroWarehouseController extends Controller {
	
	@Inject
	IMicroWarehouseService microWarehouseService;
	
	@ALogin
	public Result queryMicroWarehouse(String email){
		return ok(Json.toJson(microWarehouseService.queryMicroWarehouse(email)));
	}
	
}
