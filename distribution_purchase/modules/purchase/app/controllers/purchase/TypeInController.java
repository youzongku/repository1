package controllers.purchase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.purchase.IHttpService;
import services.purchase.IPurchaseOrderTypeInService;
import services.purchase.IUserService;
import utils.purchase.StringUtils;
import annotation.ALogin;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.purchase.PurchaseOrderInputDto;
import entity.purchase.PurchaseOrderInput;
import entity.purchase.PurchaseOrderInputGift;
import entity.purchase.PurchaseOrderInputPro;
import forms.purchase.GetProductsParams;

public class TypeInController extends Controller {
	@Inject	private IPurchaseOrderTypeInService typeInService;
	@Inject	private IUserService userService;
	@Inject	private IHttpService httpService;
	
	/**
	 * 添加/修改主表数据
	 * @return
	 */
	@ALogin
	public Result addOrUpdateMain(){
		JsonNode node = request().body().asJson();
		
		PurchaseOrderInput orderInput = new PurchaseOrderInput();
		orderInput.setDisAccount(node.get("disAccount").asText());
		orderInput.setInputUser(userService.getAdminAccount());
		orderInput.setInputType(PurchaseOrderInput.INPUT_TYPE_TYPE_IN);
		orderInput.setDisMode(node.get("disMode").asInt());// 分销商模式
		orderInput.setDisType(node.get("comsumerType").asInt());// 分销商类型
		if(node.has("inputId")){
			orderInput.setId(node.get("inputId").asInt());
		}else{
			orderInput.setId(null);
		}
		// 返回一个inputId
		typeInService.addOrUpdateMain(orderInput);
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("inputId", orderInput.getId());
		return ok(Json.toJson(map));
	}
	
	/**
	 * 是否需要选择到期日期
	 * 
	 * @param node
	 * @return
	 */
	private boolean needExpirationDate(JsonNode node){
		if(node==null || !node.has("needExpirationDate")) 
			return false;
		
		return node.get("needExpirationDate").asBoolean();
	}
	
	/**
	 * 将符合条件的商品添加为正价商品
	 * @return
	 */
	@ALogin
	public Result addAllMatched2Prdoucts(){
		HashMap<Object,Object> map = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		int inputId = node.get("inputId").asInt();
		PurchaseOrderInput input = typeInService.getPurchaseOrderInput(inputId);
		if(input==null){
			map.put("suc", false);
			map.put("msg", "不存在采购单录入信息");
			return ok(Json.toJson(map));
		}
		Logger.info("添加匹配条件的商品作为正价商品-参数：{}",node);
		
		boolean needExpirationDate = needExpirationDate(node);
		GetProductsParams getProductsParams = new GetProductsParams();
		getProductsParams.setModel(input.getDisMode());
		getProductsParams.setEmail(input.getDisAccount());
		getProductsParams.setWarehouseId(node.has("warehouseId")?node.get("warehouseId").asInt():null);
		getProductsParams.setCategoryId(node.has("categoryId")?node.get("categoryId").asInt():null);
		getProductsParams.setTitle(node.has("title")?node.get("title").asText():null);
		if(node.has("skuList")&&StringUtils.isNotBlankOrNull(node.get("skuList").asText())){
			String[] skus = node.get("skuList").asText().split(",");
			List<String> skuList = Lists.newArrayList();
			for(String sku : skus){
				skuList.add(sku.trim());
			}
			getProductsParams.setSkuList(skuList);
		}
		JsonNode productsJson = null;
		try {
			productsJson = httpService.getProducts(getProductsParams);
		} catch (Exception e) {
			Logger.info("添加匹配条件的商品作为正价商品失败,{}",e);
			map.put("suc", false);
			map.put("msg", "添加匹配条件的商品作为正价商品失败");
			return ok(Json.toJson(map));
		}
		
		List<PurchaseOrderInputPro> inputProList = new ArrayList<>();
		for(Iterator<JsonNode> it = productsJson.get("data").get("result").iterator();it.hasNext();){
			JsonNode eachProductNode = it.next();
			PurchaseOrderInputPro inputPro = new PurchaseOrderInputPro();
			inputPro.setSku(eachProductNode.get("csku").asText());
			inputPro.setWarehouseId(eachProductNode.get("warehouseId").asInt());
			inputPro.setQty(eachProductNode.get("batchNumber").asInt());
			inputPro.setUnitType(PurchaseOrderInputPro.UNIT_TYPE_SINGLE);
			inputPro.setChecked(true);
			inputProList.add(inputPro);
		}
		Logger.info("添加匹配条件的商品作为正价商品：input={},正价商品={},是否要选择到期日期={}", inputId,
				inputProList, needExpirationDate);
		// 添加正价商品
		typeInService.addProducts(inputId, needExpirationDate, inputProList);
		map.put("suc", true);
		map.put("msg", "添加匹配条件的商品作为正价商品成功");
		return ok(Json.toJson(map));
	}
	
	/**
	 * 添加正价商品
	 * @return
	 */
	@ALogin
	public Result addProducts(){
		/*
		{
			inputId:12,
			needExpirationDate:true/false,
            products:[
            	{
                	sku:xxx,
                    warehouseId:xxx,
                    unitType:xxx, // 单位类型（1 为单个商品，2 为整箱商品）
                    checked:false/true
                    }
            ]
		}
		 */
		JsonNode node = request().body().asJson();
		int inputId = node.get("inputId").asInt();
		boolean needExpirationDate = needExpirationDate(node);
		// 正价商品
		List<PurchaseOrderInputPro> inputProList = new ArrayList<>();
		for (Iterator<JsonNode> iterator = node.get("products").iterator();iterator.hasNext();) {
			JsonNode inputProNode = iterator.next();
			PurchaseOrderInputPro inputPro = new PurchaseOrderInputPro();
			inputPro.setSku(inputProNode.get("sku").asText());
			inputPro.setWarehouseId(inputProNode.get("warehouseId").asInt());
			inputPro.setUnitType(inputProNode.get("unitType").asInt());
			inputPro.setQty(inputProNode.get("qty").asInt());
			if(inputPro.getQty()<=0){
				inputPro.setQty(1);
			}
			inputPro.setChecked(true);
			inputProList.add(inputPro);
		}
		Logger.info("手动录入正价商品：input={},正价商品={},是否要选择到期日期={}", inputId,
				inputProList, needExpirationDate);
		// 添加正价商品
		typeInService.addProducts(inputId, needExpirationDate, inputProList);
		
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("suc", true);
		return ok(Json.toJson(map));
	}
	
	/**
	 * 将符合条件的商品添加为赠品
	 * @return
	 */
	@ALogin
	public Result addAllMatched2Gifts(){
		HashMap<Object,Object> map = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		int inputId = node.get("inputId").asInt();
		PurchaseOrderInput input = typeInService.getPurchaseOrderInput(inputId);
		if(input==null){
			map.put("suc", false);
			map.put("msg", "不存在采购单录入信息");
			return ok(Json.toJson(map));
		}
		Logger.info("添加匹配条件的商品作为赠品-参数：{}",node);
		
		boolean needExpirationDate = needExpirationDate(node);
		GetProductsParams getProductsParams = new GetProductsParams();
		getProductsParams.setModel(input.getDisMode());
		getProductsParams.setEmail(input.getDisAccount());
		getProductsParams.setWarehouseId(node.has("warehouseId")?node.get("warehouseId").asInt():null);
		getProductsParams.setCategoryId(node.has("categoryId")?node.get("categoryId").asInt():null);
		getProductsParams.setTitle(node.has("title")?node.get("title").asText():null);
		if(node.has("skuList")&&StringUtils.isNotBlankOrNull(node.get("skuList").asText())){
			String[] skus = node.get("skuList").asText().split(",");
			List<String> skuList = Lists.newArrayList();
			for(String sku : skus){
				skuList.add(sku.trim());
			}
			getProductsParams.setSkuList(skuList);
		}
		
		JsonNode productsJson = null;
		try {
			productsJson = httpService.getProducts(getProductsParams);
		} catch (Exception e) {
			Logger.info("添加匹配条件的商品作为赠品失败,{}",e);
			map.put("suc", false);
			map.put("msg", "添加匹配条件的商品作为赠品失败");
			return ok(Json.toJson(map));
		}
		
		List<PurchaseOrderInputGift> inputGiftList = new ArrayList<>();
		for(Iterator<JsonNode> it = productsJson.get("data").get("result").iterator();it.hasNext();){
			JsonNode eachProductNode = it.next();
			PurchaseOrderInputGift inputGift = new PurchaseOrderInputGift();
			inputGift.setSku(eachProductNode.get("csku").asText());
			inputGift.setWarehouseId(eachProductNode.get("warehouseId").asInt());
			inputGift.setQty(1);// 默认为1
			inputGift.setUnitType(PurchaseOrderInputGift.UNIT_TYPE_SINGLE);
			inputGiftList.add(inputGift);
		}
		Logger.info("添加匹配条件的商品作为赠品：input={},赠品={},是否要选择到期日期={}", inputId,
				inputGiftList, needExpirationDate);
		// 添加正价商品
		typeInService.addGifts(inputId, needExpirationDate, inputGiftList);
		map.put("suc", true);
		map.put("msg", "添加匹配条件的商品作为赠品成功");
		return ok(Json.toJson(map));
	}
	
	/**
	 * 添加赠品
	 * @return
	 */
	@ALogin
	public Result addGifts(){
		/*
        {
			inputId:123,
            gifts:[
				{
					sku:xxx,
                    warehouseId:xxx,
                    unitType:1, // 单位类型（1 为单个商品，2 为整箱商品）
                    qty:1, // 数量
                }
            ]
        }
        */
		JsonNode node = request().body().asJson();
		int inputId = node.get("inputId").asInt();
		boolean needExpirationDate = needExpirationDate(node);
		// 正价商品
		List<PurchaseOrderInputGift> inputGiftList = new ArrayList<>();
		for (Iterator<JsonNode> iterator = node.get("gifts").iterator();iterator.hasNext();) {
			JsonNode inputGiftNode = iterator.next();
			PurchaseOrderInputGift inputGift = new PurchaseOrderInputGift();
			inputGift.setSku(inputGiftNode.get("sku").asText());
			inputGift.setWarehouseId(inputGiftNode.get("warehouseId").asInt());
			inputGift.setUnitType(inputGiftNode.get("unitType").asInt());
			int qty = 1;
			if(inputGiftNode.has("qty")){
				qty = inputGiftNode.get("qty").asInt();
			}
			inputGift.setQty(qty);
			inputGiftList.add(inputGift);
		}
		Logger.info("手动录入赠品：input={},赠品={},是否要选择到期日期={}", inputId,
				inputGiftList, needExpirationDate);
		
		typeInService.addGifts(inputId, needExpirationDate, inputGiftList);
		
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("suc", true);
		return ok(Json.toJson(map));
	}
	
	/**
	 * 根据inputId获取录入的数据
	 * @return
	 */
	@ALogin
	public Result getPurchaseOrderInputDtoByInputId() {
		JsonNode node = request().body().asJson();
		int inputId = node.get("inputId").asInt();
		Logger.info("通过id来查询录入的单======inputId======"+inputId);
//		ProsGiftsMapping inputDto = typeInService.getProsGiftsMapping(inputId);
		PurchaseOrderInputDto inputDto = typeInService.getPurchaseOrderInputDto(inputId);
		Logger.info("通过id来查询录入的单======inputDto======"+inputDto);
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("inputDto", inputDto);
		map.put("suc", true);
		return ok(Json.toJson(map));
	}
	
	/**
	 * 根据用户获取录入的数据
	 * @return
	 * 
	 */
	@ALogin
	public Result getPurchaseOrderInputDtoByInputUser() {
		String inputUser = userService.getAdminAccount();
		
		Logger.info("通过用户【"+inputUser+"】来查询录入的单");
		PurchaseOrderInputDto inputDto = typeInService.getPurchaseOrderInputDto(inputUser);
		Logger.info("通过用户来查询录入的单======inputDto======"+inputDto);
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("inputDto", inputDto);
		map.put("suc", true);
		return ok(Json.toJson(map));
	}
	
	@ALogin
	public Result getPurchaseOrderInputByInputUser() {
		String inputUser = userService.getAdminAccount();
		
		Logger.info("通过用户来查询录入的单======inputUser======"+inputUser);
		PurchaseOrderInput input = typeInService.getPurchaseOrderInput(inputUser);
		Logger.info("通过用户来查询录入的单======input======"+input);
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("input", input);
		map.put("suc", true);
		return ok(Json.toJson(map));
	}
	
	/**
	 * 更新正价商品数量
	 * @return
	 */
	@ALogin
	public Result updateProQty(){
		JsonNode node = request().body().asJson();
		int proId = node.get("proId").asInt();
		int qty = node.get("qty").asInt();
		Logger.info("===========更新正价商品=="+proId+"===的数量为=="+qty);
		typeInService.updateProQty(proId, qty);
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("suc", true);
		return ok(Json.toJson(map));
	}
	
	/**
	 * 更新赠品数量
	 * @return
	 */
	@ALogin
	public Result updateGiftQty(){
		JsonNode node = request().body().asJson();
		int giftId = node.get("giftId").asInt();
		int qty = node.get("qty").asInt();
		Logger.info("===========更新赠品=="+giftId+"===的数量为=="+qty);
		typeInService.updateGiftQty(giftId, qty);
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("suc", true);
		return ok(Json.toJson(map));
	}
	
	public Result updateChecked(){
		JsonNode node = request().body().asJson();
		int inputId = node.get("inputId").asInt();
		String proIds = node.get("proIds").asText();
		typeInService.updateChecked(inputId,proIds);
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("suc", true);
		return ok(Json.toJson(map));
	}
	
	@ALogin
	public Result updateCheckecUnchecked(){
		JsonNode node = request().body().asJson();
		int inputId = node.get("inputId").asInt();
		typeInService.updateChecked(inputId,null);
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("suc", true);
		return ok(Json.toJson(map));
	}
	
	/**
	 * 根据ids批量删除正价商品
	 * @return
	 */
	@ALogin
	public Result deleteProsByIds(){
		JsonNode node = request().body().asJson();
		String proIds = node.get("proIds").asText();
		Logger.info("===========删除正价商品=="+proIds);
		typeInService.deleteProducts(proIds);
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("suc", true);
		return ok(Json.toJson(map));
	}
	
	/**
	 * 根据ids批量删除赠品
	 * @return
	 */
	@ALogin
	public Result deleteGiftsByIds(){
		JsonNode node = request().body().asJson();
		String giftIds = node.get("giftIds").asText();
		Logger.info("===========删除赠品=="+giftIds);
		typeInService.deleteGifts(giftIds);
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("suc", true);
		return ok(Json.toJson(map));
	}
	
	@ALogin
	public Result deleteAllProsAndGiftsByInputId(){
		JsonNode node = request().body().asJson();
		int inputId = node.get("inputId").asInt();
		Logger.info("===========删除正价商品和赠品==inputId={}",inputId);
		typeInService.deleteAllProsAndGiftsByInputId(inputId);
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("suc", true);
		return ok(Json.toJson(map));
	}
	
	/**
	 * 获取选中的
	 * @return
	 */
	@ALogin
	public Result getCheckedInputPros(){
		JsonNode node = request().body().asJson();
		String inputId = node.get("inputId").asText();
		List<PurchaseOrderInputPro> checkedInputPros = typeInService.getCheckedInputPros(Integer.valueOf(inputId));
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("suc", true);
		map.put("checkedInputPros", checkedInputPros);
		return ok(Json.toJson(map));
	}
}
