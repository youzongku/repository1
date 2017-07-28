package controllers.marketing.promotion;

import java.util.List;

import org.springframework.util.StringUtils;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.marketing.promotion.IPromotionTypeService;
import services.marketing.promotion.IUserService;
import util.marketing.promotion.CondtUtil;
import util.marketing.promotion.PageInfo;
import util.marketing.promotion.PromotionAttrEnum;
import util.marketing.promotion.ResponseObject;
import annotation.ALogin;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSortedMap;
import com.google.inject.Inject;

import dto.marketing.promotion.FullProTypeDto;
import entity.marketing.promotion.PromotionCondition;
import entity.marketing.promotion.PromotionPrivilege;
import entity.marketing.promotion.PromotionType;
import forms.marketing.promotion.PromotionTypeForm;
import forms.marketing.promotion.PromotionTypeSearchForm;
/**
 * 促销模板
 * @author huangjc
 * @date 2016年7月25日
 */
public class PromotionTypeController extends Controller {

	@Inject
	private IPromotionTypeService promotionTypeService;
	@Inject
	private IUserService userService;
	private static final ImmutableSortedMap<Integer,String> attrMap;
	
	/** 初始化促销类型属性：商品、购物车、用户 */
	static{
		attrMap = ImmutableSortedMap.of(
				PromotionAttrEnum.GOODS.getValue(), 
				PromotionAttrEnum.GOODS.getName(), 
				PromotionAttrEnum.SHOPPING_CART.getValue(), 
				PromotionAttrEnum.SHOPPING_CART.getName(), 
				PromotionAttrEnum.USER.getValue(), 
				PromotionAttrEnum.USER.getName());
	}
	
	/**
	 * 促销类型名称是否可以使用
	 * @return true可以使用，false不可以使用
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result canUseThisName(){
		JsonNode node = request().body().asJson();
		if(!node.has("proTypeName") || !node.has("id")){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("缺少参数")));
		}
		String proTypeName = node.get("proTypeName").asText();
		int id = node.get("id").asInt();
		Logger.info("促销类型名称----------proTypeName-------"+proTypeName);
		PromotionTypeForm proTypeForm = new PromotionTypeForm();
		proTypeForm.setName(proTypeName);
		proTypeForm.setId(id);
		boolean canUseThisName = promotionTypeService.canUseThisName(proTypeForm);
		if(!canUseThisName){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("此名称已被占用")));
		}
		return ok(Json.toJson(ResponseObject.newSuccessResponseObject("此名称可以使用")));
	}
	
	/**
	 * 添加促销类型
	 * @return
	 */
	@ALogin
	public Result addProType(){
		
		Form<PromotionTypeForm> form = Form.form(PromotionTypeForm.class).bindFromRequest();
		PromotionTypeForm proTypeForm = form.get();
		
		Logger.info("添加促销类型----------proTypeForm-------"+proTypeForm);
		
		// 验证
		Result validateResult = validateProType(proTypeForm);
		if(validateResult!=null){
			return validateResult;
		}
		String createUser = userService.getAdminAccount();
		proTypeForm.setCreateUser(createUser);
		
		boolean result = promotionTypeService.insertPromotionTypeDto(proTypeForm);
		if(!result){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("添加失败")));
		}
		
		return ok(Json.toJson(ResponseObject.newSuccessResponseObject("添加成功")));
	}

	// 验证提交的数据
	private Result validateProType(PromotionTypeForm proTypeForm) {
		if(StringUtils.isEmpty(proTypeForm.getName())){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("请填写促销类型名称")));
		}
		
		if(proTypeForm.getAttr()==null){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("请选择促销属性")));
		}
		
		if(!CondtUtil.isAttrRange(proTypeForm.getAttr())){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("促销属性范围在1~3")));
		}
		
		if(StringUtils.isEmpty(proTypeForm.getCondtIds())){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("促销类型的条件不能为空")));
		}
		
		if(StringUtils.isEmpty(proTypeForm.getPvlgIds())){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("促销类型的优惠不能为空")));
		}
		
		boolean canUseThisName = promotionTypeService.canUseThisName(proTypeForm);
		if(!canUseThisName){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("此名称已被占用")));
		}
		
		return null;
	}
	
	/**
	 * 更新
	 * @return
	 */
	@ALogin
	public Result updateProType(){
		Form<PromotionTypeForm> form = Form.form(PromotionTypeForm.class).bindFromRequest();
		PromotionTypeForm proTypeForm = form.get();
		
		Logger.info("更新促销类型----------proTypeForm-------"+proTypeForm);
		
		// 验证
		if(proTypeForm.getId()==null){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("id不能为空")));
		}
		Result validateResult = validateProType(proTypeForm);
		if(validateResult!=null){
			return validateResult;
		}
		String lastUpdateUser = userService.getAdminAccount();
		proTypeForm.setLastUpdateUser(lastUpdateUser);
		
		boolean result = promotionTypeService.updatePromotionTypeDto(proTypeForm);
		if(!result){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("更新失败")));
		}
		
		return ok(Json.toJson(ResponseObject.newSuccessResponseObject("更新成功")));
	}
	
	/**
	 * 复制促销类型
	 * @param id
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result copy(){
		JsonNode node = request().body().asJson();
		if(!node.has("id") || !node.has("newProTypeName")){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("缺少参数")));
		}
		int id = node.get("id").asInt();
		String newProTypeName = node.get("newProTypeName").asText();
		Logger.info("复制促销类型----------id-------"+id+"------------newProTypeName---------"+newProTypeName);
		
		PromotionTypeForm proTypeForm = new PromotionTypeForm();
		proTypeForm.setName(newProTypeName);
		boolean canUseThisName = promotionTypeService.canUseThisName(proTypeForm);
		if(!canUseThisName){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("此名称已被占用")));
		}
		
		FullProTypeDto typeDtoExists = promotionTypeService.getFullProTypeDto(id);
		if(typeDtoExists==null){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("不存在此促销类型")));
		}
		
		promotionTypeService.copy(typeDtoExists,newProTypeName);
		
		return ok(Json.toJson(ResponseObject.newSuccessResponseObject("复制成功")));
	}
	
	/**
	 * 删除促销类型
	 * @param id
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result deleteProTypeById(){
		JsonNode node = request().body().asJson();
		if(!node.has("id")){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("缺少参数")));
		}
		int id = node.get("id").asInt();
		Logger.info("删除促销类型----------id-------"+id);
		
		boolean result = promotionTypeService.deleteProTypeById(id);
		if(!result){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("删除失败")));
		}
		return ok(Json.toJson(ResponseObject.newSuccessResponseObject("删除成功")));
	}
	
	/**
	 * 获取单个促销类型
	 * @param id
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result loadOneProTypeDto(){
		JsonNode node = request().body().asJson();
		if(!node.has("id")){
			return ok(Json.toJson(ResponseObject.newErrorResponseObject("缺少参数")));
		}
		int id = node.get("id").asInt();
		Logger.info("获取单个促销类型----------id-------"+id);
		
		FullProTypeDto typeDto = promotionTypeService.getFullProTypeDto(id);
		return ok(Json.toJson(typeDto));
	}
	
	/**
	 * 分页查询
	 * @param form
	 * @return
	 */
	public Result loadAllProTypePage(){
		Form<PromotionTypeSearchForm> form = Form.form(PromotionTypeSearchForm.class).bindFromRequest();
		PromotionTypeSearchForm typeForm = form.get();
		Logger.info("促销类型分页查询----------参数-------"+typeForm);
		PageInfo<PromotionType> pageInfo = promotionTypeService.getPromotionTypePage(typeForm);
		return ok(Json.toJson(pageInfo));
	}
	
	/**
	 * 获取促销条件列表
	 * @return
	 */
	public Result loadCondtsByAttr(){
		String attrStr = request().getQueryString("attr");
		Logger.info("获取促销条件----------attr-------"+attrStr);
		short attr = Short.valueOf(attrStr);
		List<PromotionCondition> condtList = promotionTypeService.getProCondtListByAttr(attr);
		return ok(Json.toJson(condtList));
	}
	
	/**
	 * 获取促销优惠列表
	 * @return
	 */
	public Result loadPvlgsByAttr(){
		String attrStr = request().getQueryString("attr");
		Logger.info("获取促销优惠----------attr-------"+attrStr);
		short attr = Short.valueOf(attrStr);
		List<PromotionPrivilege> pvlgList = promotionTypeService.getProPvlgListByAttr(attr);
		return ok(Json.toJson(pvlgList));
	}
	
	/**
	 * 获取促销类型属性：商品、购物车、用户
	 * @return
	 */
	public Result loadProTypeAttrMap(){
		return ok(Json.toJson(attrMap));
	}
	
}
