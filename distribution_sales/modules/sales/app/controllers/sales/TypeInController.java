package controllers.sales;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.annotation.ALogin;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.sales.ISaleInputService;

/**
 * 录入发货单
 * @author huangjc
 * @date 2017年3月24日
 */
public class TypeInController extends Controller{
	@Inject private	ISaleInputService saleInputService;
	
	/**
	 * H5录单时临时选中添加待生成的商品信息
	 * @return
	 */
	@ALogin
	public Result addProducts(){
		JsonNode json = request().body().asJson();
		if (json == null || json.size() == 0){
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("suc", false);
			resultMap.put("msg", "数据格式错误");
			return ok(Json.toJson(resultMap.toString()));
		}
		
		return ok(Json.toJson(saleInputService.addProducts(json.toString())));
	}
	
	/**
	 * 根据email得到指定的待生成的商品信息
	 * @param email
	 * @return
	 */
	@ALogin
	public Result getInfor(String email){
		if (StringUtils.isEmpty(email)) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("suc",false);
			resultMap.put("msg", "数据格式错误");
			return ok(Json.toJson(resultMap.toString()));
		}
		
		return ok(Json.toJson(saleInputService.getCheckedProducts(email)));
	}
	
	/**
	 * 清空此分销商之前保存的待生成的商品信息
	 * @param email
	 * @return
	 */
	@ALogin
	public Result refreshProducts(String email){
		if (StringUtils.isEmpty(email)) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("suc",false);
			resultMap.put("msg", "数据格式错误");
			return ok(Json.toJson(resultMap.toString()));
		}
		
		return ok(Json.toJson(saleInputService.refreshProducts(email)));
	}
	
	/**
	 * 更新指定待生成的商品信息
	 * @return
	 */
	@ALogin
	public Result updateInfo(){
		JsonNode json = request().body().asJson();
		if (json == null || json.size() == 0 || !json.has("id")){
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("suc", false);
			resultMap.put("msg", "数据格式错误");
			return ok(Json.toJson(resultMap.toString()));
		}
		
		return ok(Json.toJson(saleInputService.updateInfo(json.toString())));
	}
	
	/**
	 * 删除勾选的商品
	 * @return
	 */
	@ALogin
	public Result deleteProducts(){
		JsonNode json = request().body().asJson();
		if (json == null || json.size() == 0 || !json.has("ids")){
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("suc", false);
			resultMap.put("msg", "数据格式错误");
			return ok(Json.toJson(resultMap.toString()));
		}
		
		return ok(Json.toJson(saleInputService.batchDelete(json.toString())));
	}
}
