package controllers.purchase.returnod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.purchase.IUserService;
import services.purchase.returnod.IReturnAmountCoefficientService;
import annotation.ALogin;
import annotation.DivisionMember;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.purchase.returnod.ReturnAmountCoefficientDto;
import entity.purchase.returnod.ReturnAmountCoefficient;
import entity.purchase.returnod.ReturnAmountCoefficientLog;
import forms.purchase.Page;

public class ReturnAmountCoefficientController extends Controller {
	
	@Inject private IReturnAmountCoefficientService returnAmountCoefficientService;
	@Inject private IUserService userService;
	
	/**
	 * 获取设置系数日志
	 * @return
	 */
	public Result getSetCoefficientLogs(){
		JsonNode node = request().body().asJson();
		Logger.info("获取系数日志参数：{}",node);
		List<ReturnAmountCoefficientLog> setCoefficientLogs = returnAmountCoefficientService.getSetCoefficientLogs(node.get("sku").asText(),node.get("warehouseId").asInt());
		Logger.info("获取系数日志结果：{}",setCoefficientLogs);
		HashMap<String, Object> result = Maps.newHashMap();
		result.put("setCoefficientLogs", setCoefficientLogs);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 获取一个商品退款系数
	 * @return
	 */
	public Result getProductCoefficients(){
		JsonNode node = request().body().asJson();
		ReturnAmountCoefficient rac = returnAmountCoefficientService.getProductCoefficients(node.get("sku").asText(), 
				node.get("warehouseId").asInt());
		Map<String,Object> result = Maps.newHashMap();
		result.put("suc", true);
		result.put("result", rac);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 获取商品退款系数
	 * @return
	 */
	@ALogin
	public Result getCoefficientsOfProducts() {
		JsonNode node = request().body().asJson();
		Map<String, Object> params = Maps.newHashMap();
		params.put("currPage", node.get("currPage").asInt());
		params.put("pageSize", node.get("pageSize").asInt());
		params.put("categoryId", node.has("categoryId")?node.get("categoryId").asInt():null);
		params.put("typeId", node.has("typeId")?node.get("typeId").asInt():null);
		params.put("warehouseId", node.has("warehouseId")?node.get("warehouseId").asInt():null);
		params.put("searchText", node.get("searchText").asText());
		Page<ReturnAmountCoefficientDto> page = returnAmountCoefficientService.getCoefficientsOfProducts(params);
		return ok(Json.toJson(page));
	}
	
	/**
	 * 设置商品退款系数
	 * @return
	 */
	@ALogin
	@DivisionMember
	public Result setProductsCoefficients(){
		JsonNode node = request().body().asJson();
		Logger.info("设置退货系数的参数：{}",node);
		List<ReturnAmountCoefficient> coefficientList = parseParameters(node);
		Logger.info("解析后的退货系数的参数：{}",coefficientList);
		Map<String, Object> result = returnAmountCoefficientService.setProductsCoefficients(coefficientList);
		return ok(Json.toJson(result));
	}
	
	private List<ReturnAmountCoefficient> parseParameters(JsonNode node){
		List<ReturnAmountCoefficient> coefficientList = Lists.newArrayList();
		String lastUpdateUser = userService.getAdminAccount();
		// 具体的退款系数
		String coefficientValue = node.get("coefficientValue").toString();
		// 迭代出sku & 仓库
		for(Iterator<JsonNode> it = node.get("skuAndWarehouseIdList").iterator();it.hasNext();){
			JsonNode nextNode = it.next();
			ReturnAmountCoefficient rac = new ReturnAmountCoefficient();
			rac.setId(nextNode.has("racId")?nextNode.get("racId").asInt():null);
			rac.setSku(nextNode.get("sku").asText());
			rac.setWarehouseId(nextNode.get("warehouseId").asInt());
			rac.setCoefficientValue(coefficientValue);
			if(rac.getId()==null){
				rac.setCreateUser(lastUpdateUser);
			}else{
				rac.setLastUpdateUser(lastUpdateUser);
			}
			coefficientList.add(rac);
		}
		
		return coefficientList;
	}
}
