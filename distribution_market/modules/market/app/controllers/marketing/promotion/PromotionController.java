package controllers.marketing.promotion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.marketing.promotion.ActInstWithProTypeDto;
import dto.marketing.promotion.FullActInstDto;
import dto.marketing.promotion.FullCondtInstDto;
import dto.marketing.promotion.FullProActDto;
import dto.marketing.promotion.ProActWithActInstsAndProTypesDto;
import dto.marketing.promotion.condt.value.BaseCondtValue;
import dto.marketing.promotion.condt.value.FinalCondtValue;
import dto.marketing.promotion.condt.value.ProductCategory;
import dto.marketing.promotion.condt.value.ProductCategory.SingleProductCategory;
import dto.marketing.promotion.condt.value.ProductTotalCount;
import dto.marketing.promotion.condt.value.ProductType;
import dto.marketing.promotion.condt.value.ShippingRegion;
import dto.marketing.promotion.condt.value.ShippingRegion.SingleShippingRegion;
import dto.marketing.promotion.condt.value.ShoppingCartTotalWeight;
import dto.marketing.promotion.condt.value.SpecifyAttrValue;
import dto.marketing.promotion.condt.value.SpecifyProduct;
import dto.marketing.promotion.condt.value.SpecifyProduct.SingleSpecifyProduct;
import dto.marketing.promotion.condt.value.SpecifyWarehouse;
import dto.marketing.promotion.condt.value.SpecifyWarehouse.SingleSpecifyWarehouse;
import dto.marketing.promotion.condt.value.Subtotal;
import dto.marketing.promotion.pvlg.value.BasePvlgValue;
import dto.marketing.promotion.pvlg.value.Donation;
import dto.marketing.promotion.pvlg.value.Donation.SingleDonation;
import dto.marketing.promotion.pvlg.value.FinalPvlgValue;
import dto.marketing.promotion.pvlg.value.ReduceMoney;
import dto.marketing.promotion.pvlg.value.ShoppingCartQuantityDiscount;
import entity.marketing.promotion.ConditionInstanceExt;
import entity.marketing.promotion.PromotionActivity;
import forms.marketing.promotion.ActivityInstanceSearchForm;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.marketing.promotion.IPromotionService;
import util.marketing.promotion.PageInfo;
import util.marketing.promotion.ResponseObject;

public class PromotionController extends Controller {
	@Inject
	private IPromotionService promotionService;
	
	public Result pausePromotionActivity(){
		JsonNode node = request().body().asJson();
		if (node == null) {
			return badRequest("Expecting Json data");
		}
		
		int proActId = node.get("proActId").asInt();
		boolean result = promotionService.pausePromotionActivity(proActId);
		HashMap<Object,Object> newHashMap = Maps.newHashMap();
		newHashMap.put("suc", result);
		return ok(Json.toJson(newHashMap));
	}
	
	public Result activateProActPaused(){
		JsonNode node = request().body().asJson();
		if (node == null) {
			return badRequest("Expecting Json data");
		}
		
		int proActId = node.get("proActId").asInt();
		boolean result = promotionService.activateProActPaused(proActId);
		HashMap<Object,Object> newHashMap = Maps.newHashMap();
		newHashMap.put("suc", result);
		return ok(Json.toJson(newHashMap));
	}

	/**
	 * 分页查询促销活动
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getProActivitiesPage() {
		Form<ActivityInstanceSearchForm> form = Form.form(
				ActivityInstanceSearchForm.class).bindFromRequest();
		ActivityInstanceSearchForm activityPageForm = form.get();
		Logger.info("活动实例分页查询----------参数-------" + activityPageForm);
		PageInfo<PromotionActivity> pageInfo = promotionService
				.getAllActivity(activityPageForm);
		return ok(Json.toJson(pageInfo));
	}

	/**
	 * 添加活动基本信息
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result addPromotionActivity() {
		JsonNode json = request().body().asJson();
		PromotionActivity proAct = null;
		try {
			proAct = parseJsonNode2ProAct(json);
		} catch (ParseException e) {
			e.printStackTrace();
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("时间格式错误，格式应为：1990-10-1 12:01:12")));
		}
		
		boolean canUseThisName = this.promotionService.canUseThisName(null, proAct.getName());
		Logger.info("创建活动-----name---"+proAct.getName()+"---名字是否可以使用----"+canUseThisName);
		if(!canUseThisName){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("此促销活动名称已被占用")));
		}
		boolean result = promotionService.addPromotionActivity(proAct);
		
		if (result) {
			// 添加成功，要返回id值
			return ok(Json.toJson(ResponseObject
					.newSuccessResponseObject(proAct.getId())));
		}
		
		return ok(Json.toJson(ResponseObject
				.newErrorResponseObject("创建失败")));
	}
	
	// 更新促销活动基础信息
	@BodyParser.Of(BodyParser.Json.class)
	public Result updatePromotionActivity() {
		JsonNode json = request().body().asJson();
		PromotionActivity proAct = null;
		try {
			proAct = parseJsonNode2ProAct(json);
		} catch (ParseException e) {
			e.printStackTrace();
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("时间格式错误，格式应为：1990-10-1 12:01:12")));
		}
		
		Logger.info("修改活动基本信息----------参数-------" + proAct);
		
		boolean canUseThisName = this.promotionService.canUseThisName(proAct.getId(), proAct.getName());
		Logger.info("编辑活动--id-"+proAct.getId()+"---name-"+proAct.getName()+"---名字是否可以使用----"+canUseThisName);
		if(!canUseThisName){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("此促销活动名称已被占用")));
		}
		boolean result = promotionService.updatePromotionActivity(proAct);
		
		if (result) {
			// 添加成功，要返回id值
			return ok(Json.toJson(ResponseObject
					.newSuccessResponseObject("更新促销活动信息成功")));
		}
		
		return ok(Json.toJson(ResponseObject
				.newErrorResponseObject("更新促销活动信息失败")));
	}
	/**
	 * 将json解析成一个活动对象
	 * @param json
	 * @return
	 * @throws ParseException
	 */
	private PromotionActivity parseJsonNode2ProAct(JsonNode json) throws ParseException {
		PromotionActivity proAct = new PromotionActivity();
		if(json.has("id")){
			proAct.setId(json.get("id").asInt());
		}
		if(json.has("name")){
			proAct.setName(json.get("name").asText());
		}
		if(json.has("description")){
			proAct.setDescription(json.get("description").asText());
		}
		if(json.has("modeIds")){
			proAct.setModeIds(json.get("modeIds").asText());
		}
		if(json.has("modeNames")){
			proAct.setModeNames(json.get("modeNames").asText());
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(json.has("startTime")){
			proAct.setStartTime(df.parse(json.get("startTime").asText()));
		}
		if(json.has("endTime")){
			proAct.setEndTime(df.parse(json.get("endTime").asText()));
		}
		if(json.has("status")){
			proAct.setStatus(json.get("status").asInt());
		}
		if(json.has("createUser")){
			proAct.setCreateUser(json.get("createUser").asText());
		}
		if(json.has("lastUpdateUser")){
			proAct.setLastUpdateUser(json.get("lastUpdateUser").asText());
		}
		return proAct;
	}

	/**
	 * 添加活动实例
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result addActivityInstance() {
		JsonNode json = request().body().asJson();
		if(!json.has("proActId") || !json.has("proTypeId")){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("缺少参数")));
		}
		int proActId = json.get("proActId").asInt();
		int proTypeId = json.get("proTypeId").asInt();
		Logger.info("活动实例添加活动----------proActId=" + proActId+"--------------proTypeId="+proTypeId);
		
		boolean result = promotionService.addActivityInstance(proActId, proTypeId);
		
		if (result) {
			return ok(Json.toJson(ResponseObject.newSuccessResponseObject("添加成功")));
		}

		return ok(Json.toJson(ResponseObject.newErrorResponseObject("添加失败！")));
	}

	/**
	 * 修改活动结束时间
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result updateEndTime() {
		JsonNode node = request().body().asJson();
		if (node == null) {
			return badRequest("Expecting Json data");
		}
		
		String endTime = node.findPath("endTime").textValue();
		if("".equals(endTime)|| endTime == null){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("时间不能为空！")));
		}
		Integer id = Integer.valueOf(node.findPath("id").textValue());
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = null;
		try {
			date = sim.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Logger.info("修改活动结束时间----------参数-------endDate:" + endTime
				+ "    id:" + id);
		if (promotionService.updateEndTime(date, id, null)) {
			return ok(Json.toJson(ResponseObject
					.newSuccessResponseObject("更改成功！")));
		}
		
		return ok(Json.toJson(ResponseObject.newErrorResponseObject("更改失败！")));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result getPromotionActivityById() {
		JsonNode node = request().body().asJson();
		if(!node.has("proActId")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少活动id")));
		}
		int proActId = node.get("proActId").asInt();
		PromotionActivity promotionActivity = promotionService.getPromotionActivityById(proActId);
		return ok(Json.toJson(ResponseObject.newSuccessResponseObject(promotionActivity))); 
	}
	
	/**
	 * 获取活动实例及其促销类型信息
	 * @param id 活动实例id
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getProActWithActInstsAndProTypesDto() {
		JsonNode node = request().body().asJson();
		if(!node.has("proActId")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少活动id")));
		}
		int proActId = node.get("proActId").asInt();
		ProActWithActInstsAndProTypesDto proActWithActInstsAndProTypesDto = promotionService.getProActWithActInstsAndProTypesDto(proActId);
		return ok(Json.toJson(proActWithActInstsAndProTypesDto));
	}
	
	/**
	 * 获取活动实例及其模板，进行了分组操作
	 * @param id 活动实例id
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getActivityInstanceAndTypeByGroup() {
		JsonNode node = request().body().asJson();
		if(!node.has("proActId")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少活动id")));
		}
		int proActId = node.get("proActId").asInt();
		Map<Integer, List<ActInstWithProTypeDto>> groupByProTypeId = promotionService.getActInstWithProTypeDtoListByGroup(proActId);
		return ok(Json.toJson(groupByProTypeId));
	}
	
	/**
	 * 根据活动id获取活动所有数据
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getFullProActDtoByProActId(){
		JsonNode node = request().body().asJson();
		if(!node.has("proActId")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少活动proActId")));
		}
		int proActId = node.get("proActId").asInt();
		FullProActDto dto = promotionService.getFullProActDtoByProActId(proActId);
		return ok(Json.toJson(dto));
	}
	
	/**
	 * 根据活动实例id获取实例的活动所有数据
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getFullActInstDtoByActInstId(){
		JsonNode node = request().body().asJson();
		if(!node.has("actInstId")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少活动实例id")));
		}
		int actInstId = node.get("actInstId").asInt();
		FullActInstDto fullActInstDto = promotionService.getFullActInstDtoByActInstId(actInstId);
		return ok(Json.toJson(fullActInstDto));
	}
	
	/**
	 * 根据活动实例id获取实例的活动所有数据
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getFullCondtInstDtoByCondtInstId(){
		JsonNode node = request().body().asJson();
		if(!node.has("condtInstId")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少条件实例id")));
		}
		int condtInstId = node.get("condtInstId").asInt();
		FullCondtInstDto fullCondtInstDto = promotionService.getFullCondtInstDtoByCondtInstId(condtInstId);
		return ok(Json.toJson(fullCondtInstDto));
	}
	
	/**
	 * 批量删除活动实例
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result deleteActInstByIds(){
		JsonNode node = request().body().asJson();
		if(!node.has("actInstIds")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少活动实例id：actInstIds")));
		}
		
		String actInstIds = node.get("actInstIds").asText();
		promotionService.deleteActInstByIds(actInstIds);
		
		return ok(Json.toJson(ResponseObject.newSuccessResponseObject("删除成功")));
	}
	
	/**
	 * 获取未设置值的活动实例个数
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getUnSetVActInstCount(){
		JsonNode node = request().body().asJson();
		if(!node.has("proActId")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少活动id")));
		}
		int proActId = node.get("proActId").asInt();
		int count = promotionService.getProActUnSetVCondtInstCount(proActId);
		ObjectNode newObject = Json.newObject();
		newObject.put("count", count);
		return ok(Json.toJson(newObject));
	}
	
	/**
	 * 获取促销活动的活动实例个数
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getActInstCountByProActId(){
		JsonNode node = request().body().asJson();
		if(!node.has("proActId")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少活动id")));
		}
		int proActId = node.get("proActId").asInt();
		int count = promotionService.getActInstCountByProActId(proActId);
		ObjectNode newObject = Json.newObject();
		newObject.put("count", count);
		return ok(Json.toJson(newObject));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result updatePriorityByCondtInstId(){
		JsonNode node = request().body().asJson();
		if(!node.has("condtInstId")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少参数condtInstId")));
		}
		if(!node.has("priority")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少参数priority")));
		}
		
		int condtInstId = node.get("condtInstId").asInt();
		int priority = node.get("priority").asInt();
		Logger.info("更新条件实例优先级priority======condtInstId="+condtInstId+"=====priority="+priority);
		
		promotionService.updatePriorityByCondtInstId(condtInstId,priority);
		
		return ok(Json.toJson(ResponseObject.newSuccessResponseObject("更新优先级成功")));
	}
	
	/**
	 * 更新条件&优惠的值
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result updateProActNotDelete(){
		JsonNode node = request().body().asJson();
		
		if(!node.has("proActId")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少参数proActId")));
		}
		if(!node.has("proActName")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少参数proActName")));
		}
		int proActId = node.get("proActId").asInt();
		String proActName = node.get("proActName").asText();
		
		Logger.info("更新活动状态======proActId="+proActId+"=====proActName="+proActName);
		// 判断促销活动是否有活动实例
		int count = promotionService.getActInstCountByProActId(proActId);
		if(count<1){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("此促销活动促销类型个数为0，请添加促销类型")));
		}
		
		// 判断是否有活动实例没有设置参数
		int unSetVCount = promotionService.getProActUnSetVCondtInstCount(proActId);
		if (unSetVCount > 0) {
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("还有促销类型没有设置参数，请设置参数")));
		}
		
		promotionService.updateProActNotDelete(proActId, proActName);
		
		return ok(Json.toJson(ResponseObject.newSuccessResponseObject("促销活动创建成功")));
	}
	
	/**
	 * 更新条件实例和优惠实例的值
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result updateCondtInstsAndPvlgInstsValue(){
		JsonNode node = request().body().asJson();
		
		Result validateResult = validateCondtInstsAndPvlgInstsValue(node);
		if(Objects.nonNull(validateResult)){
			return validateResult;
		}
		
//		Integer actInstId = null, matchType = ActivityInstance.MATCH_TYPE_ALL;
//		if(node.has("actInstId")){
//			actInstId = node.get("actInstId").asInt();
//		}
//		if(node.has("matchType")){
//			matchType = node.get("matchType").asInt();
//		}
//		Logger.info("===========更新条件优惠值====actInstId="+actInstId+"====matchType="+matchType);
		
		// ********************条件*********************
		List<FinalCondtValue> finalCondtValueList = parseCondtValues(node);
		Logger.info("===========finalCondtValueList======"+finalCondtValueList);

		// ********************指定属性*********************
		ConditionInstanceExt condtInstExt = parseSpecifyAttrValues(node);
		Logger.info("===========condtInstExt======"+condtInstExt);
		
		// ********************优惠*********************
		List<FinalPvlgValue> finalPvlgValueList = parsePvlgValues(node);
		Logger.info("===========finalPvlgValueList======"+finalPvlgValueList);
		
		
		promotionService.updateCondtInstsAndPvlgInstsValue(null, null, finalCondtValueList, condtInstExt, finalPvlgValueList);
		return ok(Json.toJson(ResponseObject.newSuccessResponseObject("设置参数成功")));
	}
	
	/**
	 * 解析条件json
	 * @param node
	 * @return
	 */
	private List<FinalCondtValue> parseCondtValues(JsonNode node) {
		List<FinalCondtValue> finalCondtValueList = new ArrayList<FinalCondtValue>();
		
		JsonNode condtValueDatas = node.get("condtValueDatas");
		String[] cTypeArr = {null,"1","2","3","4","5","6","7","8"};
		if(condtValueDatas.has(cTypeArr[1])){// 商品分类
			// condtInstId:1,jType:"lt",jdmntTypeId:1,categoryIds:"1,2,3",categoryNames:"121,1232"
			JsonNode productCategoryNode = condtValueDatas.get(cTypeArr[1]);
			int condtInstId = productCategoryNode.get("condtInstId").asInt();// 条件实例id
			String jType = productCategoryNode.get("jType").asText(); 		// 条件判断类型
			int jdmntTypeId = productCategoryNode.get("jdmntTypeId").asInt();// 条件判断类型id
			boolean combined = productCategoryNode.get("combined").asBoolean();// 是否组合
			
			// 组织json数据
			ProductCategory sp = new ProductCategory();
			sp.setCombined(combined);
			JsonNode eachNode;
			for(Iterator<JsonNode> it = productCategoryNode.get("productCategories").elements();it.hasNext();){
				eachNode = it.next();
				SingleProductCategory spc = new SingleProductCategory();
				spc.setCategoryId(eachNode.get("id").asInt());
				spc.setCategoryName(eachNode.get("name").asText());
				sp.getProductCategories().add(spc);
			}
			
			// 封装更新对象
			FinalCondtValue fcv = newFinalCondtValueInstance(condtInstId, jType, jdmntTypeId, sp);
			finalCondtValueList.add(fcv);
			Logger.info("=====商品分类====="+fcv);
		}
		
		if(condtValueDatas.has(cTypeArr[2])){// 指定商品
			// condtInstId:1,jType:"lt",jdmntTypeId:1,skuWarehouseIds:"1:2024,2:2025",num:2
			JsonNode specifyProductNode = condtValueDatas.get(cTypeArr[2]);
			int condtInstId = specifyProductNode.get("condtInstId").asInt();// 条件实例id
			String jType = specifyProductNode.get("jType").asText(); 		// 条件判断类型
			int jdmntTypeId = specifyProductNode.get("jdmntTypeId").asInt();// 条件判断类型id
			String unit = "";
			boolean combined = specifyProductNode.get("combined").asBoolean();// 是否组合
			if("y".equals(jType)){
				// 只有在是的条件下，才有unit
				unit = specifyProductNode.get("unit").asText();// 单位：箱/件
			}
			
			// 组织json数据
			// specifyProductList:[{sku:xxx,warehouseId:yyyy,warehouseName:yyyy,cTitle:zzzz,imgUrl:xxx}]
			SpecifyProduct sp = new SpecifyProduct();
			sp.setUnit(unit);// 箱规
			sp.setCombined(combined);
			JsonNode eachNode;
			for(Iterator<JsonNode> it = specifyProductNode.get("specifyProductList").elements();it.hasNext();){
				eachNode = it.next();
				SingleSpecifyProduct ssp = new SingleSpecifyProduct();
				ssp.setSku(eachNode.get("sku").asText());
				ssp.setWarehouseId(eachNode.get("warehouseId").asInt());
				ssp.setWarehouseName(eachNode.get("warehouseName").asText());
				ssp.setcTitle(eachNode.get("cTitle").asText());
				ssp.setImgUrl(eachNode.get("imgUrl").asText());
//				ssp.setNum(num);
				ssp.setUnit(unit);								// 箱规
				// 如果是件，设置unitNum为0
				if("件".equals(unit)){
					ssp.setUnitNum(0);
				}else{
					ssp.setUnitNum(eachNode.get("unitNum").asInt());// 每箱数量
				}
				sp.getSpecifyProductList().add(ssp);
			}
			
			// 封装更新对象
			FinalCondtValue fcv = newFinalCondtValueInstance(condtInstId, jType, jdmntTypeId, sp);
			finalCondtValueList.add(fcv);
			Logger.info("=====指定商品====="+fcv);
		}
		
		if(condtValueDatas.has(cTypeArr[3])){// 指定仓库 ======not supported yet
			// condtInstId:1,jType:"lt",jdmntTypeId:1,warehouseIds:"1,2",warehouseNames:"afa,zvzcx"
			JsonNode specifyWarehousesNode = condtValueDatas.get(cTypeArr[3]);
			int condtInstId = specifyWarehousesNode.get("condtInstId").asInt();// 条件实例id
			String jType = specifyWarehousesNode.get("jType").asText(); 		// 条件判断类型
			int jdmntTypeId = specifyWarehousesNode.get("jdmntTypeId").asInt();// 条件判断类型id
			String warehouseIds = specifyWarehousesNode.get("warehouseIds").asText();// 仓库ids
			String warehouseNames = specifyWarehousesNode.get("warehouseNames").asText();// 仓库名称
			
			// 组织json数据
			String[] cityIdArr = warehouseIds.split(",");
			String[] cityNameArr = warehouseNames.split(",");
			SpecifyWarehouse sw = new SpecifyWarehouse();
			for (int i = 0; i < cityIdArr.length; i++) {
				SingleSpecifyWarehouse ssw = new SingleSpecifyWarehouse();
				ssw.setWarehouseId(Integer.valueOf(cityIdArr[i]));
				ssw.setWarehouseName(cityNameArr[i]);
				sw.getSpecifyWarehouses().add(ssw);
			}

			// 封装更新对象
			FinalCondtValue fcv = newFinalCondtValueInstance(condtInstId, jType, jdmntTypeId, sw);
			finalCondtValueList.add(fcv);
			Logger.info("=====指定仓库====="+fcv);
		}
		
		if(condtValueDatas.has(cTypeArr[4])){// 商品类型 ======not supported yet
			// condtInstId:1,jType:"lt",jdmntTypeId:1,productType:1
			JsonNode specifyProductNode = condtValueDatas.get(cTypeArr[4]);
			int condtInstId = specifyProductNode.get("condtInstId").asInt();// 条件实例id
			String jType = specifyProductNode.get("jType").asText(); 		// 条件判断类型
			int jdmntTypeId = specifyProductNode.get("jdmntTypeId").asInt();// 条件判断类型id
			int productType = specifyProductNode.get("productType").asInt();// 商品类型
			
			// 组织json数据
			ProductType pt = new ProductType();
			pt.setProductType(productType);
			
			// 封装更新对象
			FinalCondtValue fcv = newFinalCondtValueInstance(condtInstId, jType, jdmntTypeId, pt);
			finalCondtValueList.add(fcv);
			Logger.info("=====商品类型====="+fcv);
		}
		
		if(condtValueDatas.has(cTypeArr[5])){// 小计金额
			// condtInstId:1,jType:"lt",jdmntTypeId:1,minPrice:1,maxPrice:4
			JsonNode specifyProductNode = condtValueDatas.get(cTypeArr[5]);
			int condtInstId = specifyProductNode.get("condtInstId").asInt();// 条件实例id
			String jType = specifyProductNode.get("jType").asText(); 		// 条件判断类型
			int jdmntTypeId = specifyProductNode.get("jdmntTypeId").asInt();// 条件判断类型id
			Double minPrice = specifyProductNode.get("minPrice").asDouble();// 低价格
			Double maxPrice = 0.0;
			if("ltv".equals(jType)){
				maxPrice = specifyProductNode.get("maxPrice").asDouble();// 高价格
			}
			
			// 组织json数据
			Subtotal subTotal = new Subtotal();
			subTotal.setMaxPrice(maxPrice);
			subTotal.setMinPrice(minPrice);

			// 封装更新对象
			FinalCondtValue fcv = newFinalCondtValueInstance(condtInstId, jType, jdmntTypeId, subTotal);
			finalCondtValueList.add(fcv);
			Logger.info("=====小计金额====="+fcv);
		}
		
		if(condtValueDatas.has(cTypeArr[6])){// 总商品数量
			// condtInstId:1,jType:"lt",jdmntTypeId:1,productTotalCount:1
			JsonNode specifyProductNode = condtValueDatas.get(cTypeArr[6]);
			int condtInstId = specifyProductNode.get("condtInstId").asInt();// 条件实例id
			String jType = specifyProductNode.get("jType").asText(); 		// 条件判断类型
			int jdmntTypeId = specifyProductNode.get("jdmntTypeId").asInt();// 条件判断类型id
			int productTotalCount = specifyProductNode.get("productTotalCount").asInt();// 总商品数量
			
			// 组织json数据
			ProductTotalCount ptc = new ProductTotalCount();
			ptc.setMinTotalCount(productTotalCount);

			// 封装更新对象
			FinalCondtValue fcv = newFinalCondtValueInstance(condtInstId, jType, jdmntTypeId, ptc);
			finalCondtValueList.add(fcv);
			Logger.info("=====商品总数量====="+fcv);
		}
		
		if(condtValueDatas.has(cTypeArr[7])){// 购物车总重量 ======not supported yet
			// condtInstId:1,jType:"lt",jdmntTypeId:1,minWeight:1,maxWeight:5
			JsonNode specifyProductNode = condtValueDatas.get(cTypeArr[7]);
			int condtInstId = specifyProductNode.get("condtInstId").asInt();// 条件实例id
			String jType = specifyProductNode.get("jType").asText(); 		// 条件判断类型
			int jdmntTypeId = specifyProductNode.get("jdmntTypeId").asInt();// 条件判断类型id
			Double minWeight = specifyProductNode.get("minWeight").asDouble();// 最小重量
			Double maxWeight = 0.0;
			if("ltv".equals(jType)){
				maxWeight = specifyProductNode.get("maxWeight").asDouble();// 最大重量
			}
			
			// 组织json数据
			ShoppingCartTotalWeight sctw = new ShoppingCartTotalWeight();
			sctw.setMaxWeight(maxWeight);
			sctw.setMinWeight(minWeight);

			// 封装更新对象
			FinalCondtValue fcv = newFinalCondtValueInstance(condtInstId, jType, jdmntTypeId, sctw);
			finalCondtValueList.add(fcv);
			Logger.info("=====总重量====="+fcv);
		}
		
		if(condtValueDatas.has(cTypeArr[8])){// 运送地区 ======not supported yet
			// condtInstId:1,jType:"lt",jdmntTypeId:1,cityIds:"1,2,3",cityNames:"1"
			JsonNode specifyProductNode = condtValueDatas.get(cTypeArr[8]);
			int condtInstId = specifyProductNode.get("condtInstId").asInt();// 条件实例id
			String jType = specifyProductNode.get("jType").asText(); 		// 条件判断类型
			int jdmntTypeId = specifyProductNode.get("jdmntTypeId").asInt();// 条件判断类型id
			String cityIds = specifyProductNode.get("cityIds").asText();    // 城市ids
			String cityNames = specifyProductNode.get("cityNames").asText();// 城市名称
			
			// 组织json数据
			String[] cityIdArr = cityIds.split(",");
			String[] cityNameArr = cityNames.split(",");
			ShippingRegion sr = new ShippingRegion();
			for (int i = 0; i < cityIdArr.length; i++) {
				SingleShippingRegion ssr = new SingleShippingRegion();
				ssr.setCityId(Integer.valueOf(cityIdArr[i]));
				ssr.setCityName(cityNameArr[i]);
				sr.getShippingRegions().add(ssr);
			}

			// 封装更新对象
			FinalCondtValue fcv = newFinalCondtValueInstance(condtInstId, jType, jdmntTypeId, sr);
			finalCondtValueList.add(fcv);
			Logger.info("=====运送地区====="+fcv);
		}
		return finalCondtValueList;
	}

	/**
	 * 解析优惠json
	 * @param node
	 * @return
	 */
	private List<FinalPvlgValue> parsePvlgValues(JsonNode node) {
		List<FinalPvlgValue> finalPvlgValueList = new ArrayList<FinalPvlgValue>();
		JsonNode pvlgValueDatas = node.get("pvlgValueDatas");
		String[] pTypeArr = {null,"1","2","3","4","5","6"};
		if(pvlgValueDatas.has(pTypeArr[1])){// 满赠
			// {donations:[{cTitle:"可比克零食",sku:"IM27","wareHouseId":2024,warehouseName:xx,imgUrl:xx,"num":1}],"num":1}
			JsonNode donationNode = pvlgValueDatas.get(pTypeArr[1]);
			int pvlgInstId = donationNode.get("pvlgInstId").asInt();
			int num = donationNode.get("num").asInt();				// 可能是件数或是箱数，由unit来决定
			String unit = donationNode.get("unit").asText();// 箱规：件/箱

			Donation donation = new Donation();
			donation.setNum(num);
			donation.setUnit(unit);     		// 箱规
			if(donationNode.has("donations")){// 不一定有赠品数量
				JsonNode eachNode;
				for(Iterator<JsonNode> it = donationNode.get("donations").elements();it.hasNext();){
					eachNode = it.next();
					SingleDonation sd = new SingleDonation();
					sd.setSku(eachNode.get("sku").asText());
					sd.setWarehouseId(eachNode.get("warehouseId").asInt());
					sd.setWarehouseName(eachNode.get("warehouseName").asText());
					sd.setcTitle(eachNode.get("cTitle").asText());
					sd.setImgUrl(eachNode.get("imgUrl").asText());
					sd.setNum(num);
					sd.setUnit(unit);				// 箱规
					// 如果是件，设置unitNum为0
					if("件".equals(unit)){
						sd.setUnitNum(0);
					}else{
						sd.setUnitNum(eachNode.get("unitNum").asInt());// 每箱多少，如果是件，则为0
					}
					donation.getDonations().add(sd);
				}
			}
			
			// 组织json数据
			// 封装更新对象
			FinalPvlgValue fpv = newFinalPvlgValueInstance(pvlgInstId, donation);
			finalPvlgValueList.add(fpv);
			Logger.info("=====购买X即可免费获得Y（赠品）====="+fpv);
		}else if(pvlgValueDatas.has(pTypeArr[2])){// 满减金额
			// {"pvlgInstId":1,"reduceMoney":1.0}
			JsonNode reduceMoneyNode = pvlgValueDatas.get(pTypeArr[2]);
			int pvlgInstId = reduceMoneyNode.get("pvlgInstId").asInt();
			Double reduceMoney = reduceMoneyNode.get("reduceMoney").asDouble();
			
			// 组织json数据
			ReduceMoney rm = new ReduceMoney();
			rm.setMoneyReduce(reduceMoney);
			
			// 封装更新对象
			FinalPvlgValue fpv = newFinalPvlgValueInstance(pvlgInstId, rm);
			finalPvlgValueList.add(fpv);
			Logger.info("=====满减金额====="+fpv);
		}else if(pvlgValueDatas.has(pTypeArr[3])){// 整个购物车的定额折扣
			// {"pvlgInstId":1,"discountNum":12}
			JsonNode shoppingCartQuantityDiscountNode = pvlgValueDatas.get(pTypeArr[3]);
			int pvlgInstId = shoppingCartQuantityDiscountNode.get("pvlgInstId").asInt();
			Double discountNum = shoppingCartQuantityDiscountNode.get("discountNum").asDouble();
			
			// 组织json数据
			ShoppingCartQuantityDiscount scqd = new ShoppingCartQuantityDiscount();
			scqd.setNum(discountNum);
			
			// 封装更新对象
			FinalPvlgValue fpv = newFinalPvlgValueInstance(pvlgInstId, scqd);
			finalPvlgValueList.add(fpv);
			Logger.info("=====整个购物车的定额折扣====="+fpv);
		}if(pvlgValueDatas.has(pTypeArr[4])){// 购物车满赠
			// {donations:[{cTitle:"可比克零食",sku:"IM27","wareHouseId":2024,warehouseName:xx,imgUrl:xx,"num":1}],"num":1}
			JsonNode donationNode = pvlgValueDatas.get(pTypeArr[4]);
			int pvlgInstId = donationNode.get("pvlgInstId").asInt();
			int num = donationNode.get("num").asInt();				// 可能是件数或是箱数，由unit来决定
			String unit = donationNode.get("unit").asText();// 箱规：件/箱

			Donation donation = new Donation();
			donation.setNum(num);
			donation.setUnit(unit);     		// 箱规
			if(donationNode.has("donations")){// 不一定有赠品数量
				JsonNode eachNode;
				for(Iterator<JsonNode> it = donationNode.get("donations").elements();it.hasNext();){
					eachNode = it.next();
					SingleDonation sd = new SingleDonation();
					sd.setSku(eachNode.get("sku").asText());
					sd.setWarehouseId(eachNode.get("warehouseId").asInt());
					sd.setWarehouseName(eachNode.get("warehouseName").asText());
					sd.setcTitle(eachNode.get("cTitle").asText());
					sd.setImgUrl(eachNode.get("imgUrl").asText());
					sd.setNum(num);
					sd.setUnit(unit);				// 箱规
					// 如果是件，设置unitNum为0
					if("件".equals(unit)){
						sd.setUnitNum(0);
					}else{
						sd.setUnitNum(eachNode.get("unitNum").asInt());// 每箱多少，如果是件，则为0
					}
					donation.getDonations().add(sd);
				}
			}
			
			// 组织json数据
			// 封装更新对象
			FinalPvlgValue fpv = newFinalPvlgValueInstance(pvlgInstId, donation);
			finalPvlgValueList.add(fpv);
			Logger.info("=====购买X即可免费获得Y（赠品）====="+fpv);
		}else if(pvlgValueDatas.has(pTypeArr[5])){// 购物车满减
			// {"pvlgInstId":1,"reduceMoney":1.0}
			JsonNode reduceMoneyNode = pvlgValueDatas.get(pTypeArr[5]);
			int pvlgInstId = reduceMoneyNode.get("pvlgInstId").asInt();
			Double reduceMoney = reduceMoneyNode.get("reduceMoney").asDouble();
			
			// 组织json数据
			ReduceMoney rm = new ReduceMoney();
			rm.setMoneyReduce(reduceMoney);
			
			// 封装更新对象
			FinalPvlgValue fpv = newFinalPvlgValueInstance(pvlgInstId, rm);
			finalPvlgValueList.add(fpv);
			Logger.info("=====满减金额====="+fpv);
		}else if(pvlgValueDatas.has(pTypeArr[6])){// 整个购物车的定额折扣
			// {"pvlgInstId":1,"discountNum":12}
			JsonNode shoppingCartQuantityDiscountNode = pvlgValueDatas.get(pTypeArr[6]);
			int pvlgInstId = shoppingCartQuantityDiscountNode.get("pvlgInstId").asInt();
			Double discountNum = shoppingCartQuantityDiscountNode.get("discountNum").asDouble();
			
			// 组织json数据
			ShoppingCartQuantityDiscount scqd = new ShoppingCartQuantityDiscount();
			scqd.setNum(discountNum);
			
			// 封装更新对象
			FinalPvlgValue fpv = newFinalPvlgValueInstance(pvlgInstId, scqd);
			finalPvlgValueList.add(fpv);
			Logger.info("=====整个购物车的定额折扣====="+fpv);
		}
		return finalPvlgValueList;
	}

	private ConditionInstanceExt parseSpecifyAttrValues(JsonNode node) {
		
		if(node.has("specifyAttrValueDatas")){
			JsonNode specifyAttrValueDatas = node.get("specifyAttrValueDatas");
			
			ConditionInstanceExt condtInstExt = new ConditionInstanceExt();
			condtInstExt.setId(specifyAttrValueDatas.get("condtInstExtId").asInt());
			// TODO 暂时屏蔽可阶梯
			condtInstExt.setStepped(false);
//			condtInstExt.setStepped(specifyAttrValueDatas.get("stepped").asBoolean());
			condtInstExt.setDoubleUp(specifyAttrValueDatas.get("doubleUp").asBoolean());
			
			// 以下的是json数据
			if(specifyAttrValueDatas.has("attrType")){
				int attrType = specifyAttrValueDatas.get("attrType").asInt();
				String jType = specifyAttrValueDatas.get("jType").asText();
				
				SpecifyAttrValue specifyAttrValue = new SpecifyAttrValue();
				specifyAttrValue.setAttrType(attrType);
				specifyAttrValue.setjType(jType);
				
				// gt/gteq/ltv
				if("ltv".equals(jType)){
					JsonNode ltvValNode = specifyAttrValueDatas.get("ltvVal");
					SpecifyAttrValue.LtvVal ltvVal = new SpecifyAttrValue.LtvVal();
					ltvVal.setMinJType(ltvValNode.get("minJType").asText());// gt/gteq
					ltvVal.setMinValue(ltvValNode.get("minValue").asDouble());
					ltvVal.setMaxJType(ltvValNode.get("maxJType").asText());// gt/gteq
					ltvVal.setMaxValue(ltvValNode.get("maxValue").asDouble());
					specifyAttrValue.setLtvVal(ltvVal);				
				}else if("gt".equals(jType) || "gteq".equals(jType)){
					specifyAttrValue.setSingleVal(specifyAttrValueDatas.get("singleVal").asDouble());
				}else{
					Logger.info("指定属性，未知的判断类型，jType="+jType);
					throw new RuntimeException("指定属性，未知的判断类型，jType="+jType);
				}
				
				JsonNode jsonNode = Json.toJson(specifyAttrValue);
				String jsonValue = jsonNode.toString();
				condtInstExt.setSpecifyAttrValue(jsonValue);
			}
			
			return condtInstExt;
		}
		
		return null;
	}
	
	private FinalCondtValue newFinalCondtValueInstance(int condtInstId, String jType, int jdmntTypeId, BaseCondtValue bcv) {
		FinalCondtValue fcv = new FinalCondtValue();
		fcv.setCondtInstId(condtInstId);
		fcv.setjType(jType);
		fcv.setJdmntTypeId(jdmntTypeId);
		JsonNode jsonNode = Json.toJson(bcv);
		String jsonValue = jsonNode.toString();
		fcv.setJsonValue(jsonValue);
		return fcv;
	}
	
	private FinalPvlgValue newFinalPvlgValueInstance(int pvlgInstId, BasePvlgValue bpv) {
		FinalPvlgValue fpv = new FinalPvlgValue();
		fpv.setPvlgInstId(pvlgInstId);
		JsonNode jsonNode = Json.toJson(bpv);
		String jsonValue = jsonNode.toString();
		fpv.setJsonValue(jsonValue);
		return fpv;
	}

	/**
	 * 通过验证，返回null
	 * @param node
	 * @return
	 */
	private Result validateCondtInstsAndPvlgInstsValue(JsonNode node) {
//		if(!node.has("actInstId")){
//			return ok(Json.toJson(ResponseObject
//					.newErrorResponseObject("缺少参数：actInstId")));
//		}
		// 条件
		if(!node.has("condtValueDatas")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少条件的参数：condtValueDatas")));
		}
		// 优惠
		if(!node.has("pvlgValueDatas")){
			return ok(Json.toJson(ResponseObject
					.newErrorResponseObject("缺少优惠的参数：pvlgValueDatas")));
		}
		
		return null;
	}
	
}

