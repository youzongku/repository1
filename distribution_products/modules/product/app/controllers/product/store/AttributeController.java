package controllers.product.store;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import entity.product.store.BbcAttribute;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.base.utils.JsonFormatUtils;
import services.product.IAttributeService;

/**
 * 属性控制类（主要维护属性相关信息与属性可选值信息）
 * TODO
 * 1.初始化属性类型  
 * 2.新增属性
 * @author xuse
 * 2016年11月28日
 */
public class AttributeController extends Controller {
	
	
	@Inject
	private IAttributeService attributeService;
	/**
	 * 新增
	 * <b>
	 * {
	 * "attrName":"库存",
	 * "attrKey":"stock",
	 * "attrDesc":"商品库存字段",
	 * "status":1,
	 * "attrType":"radio",
	 * "typeId":1,
	 * "isNull":true,
	 * "isShow":true,
	 * "createUser":"admin"
	 * }
	 * </b>
	 * 
	 * 属性类型（文本:text、单选:radio、多选:checkbox、日期类型）
	 * TODO createUser 在后台获取 目前用前台
	 * 疑问：校验属性是否重复 "attrKey":"stock" ?exits?
	 * TODO 待处理 备选值插入，t_attr_multivalue 
	 * @author zbc
	 * @since 2016年11月29日 下午6:13:40
	 */
	public Result create(){
		Map<String,Object> res = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		Logger.info("新增属性参数:[{}]",node);
		BbcAttribute sku = null;
		try {
			sku = JsonFormatUtils.jsonToBean(node.toString(), BbcAttribute.class);
		} catch (Exception e) {
			Logger.info("参数错误",e);
		}
		if(sku == null){
			res.put("suc", false);
			res.put("msg", "参数错误");
		}else{
			res = attributeService.create(sku);
		}
		return ok(Json.toJson(res));
	}
	
	/**
	 * {
	 * "key":"stock",
	 * "pageSize":10,
	 * "currPage":1,
	 * "status":1
	 * }
	 * 查看
	 * @author zbc
	 * @since 2016年11月29日 下午6:14:22
	 */
	public Result read(){
		JsonNode node = request().body().asJson();
		if(node == null){
			return internalServerError("参数错误");
		}
		return ok(Json.toJson(attributeService.search(node.toString())));
	}
	
	/**
	 * 更新
	 * @author zbc
	 * @since 2016年11月29日 下午6:14:56
	 */
	public Result update(){
//		SkuEntity sku = new SkuEntity();
		return null;
	}
	
	/**
	 * 删除
	 * @author zbc
	 * @since 2016年11月29日 下午6:15:33
	 */
	public Result delete(){
		return null;
	}
	
	/**
	 * 查询所有属性类型，用于新增属性
	 * @author zbc
	 * @since 2016年12月1日 下午5:01:43
	 */
	public Result readTypes(){
		return ok(Json.toJson(attributeService.selectAttrTypes()));
	}
	
	
}
