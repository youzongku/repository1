package controllers.category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.base.utils.JsonFormatUtils;
import services.product.IProductVcategoryMapService;
import services.product.IVirtualCategoryService;
import session.ISessionService;

import com.fasterxml.jackson.databind.JsonNode;

import dto.category.CategorySearchParamDto;
import dto.product.ProductSearchParamDto;
import dxo.category.CategorySearchParamDxo;
import entity.category.CategoryBase;
import forms.category.VirtualCategoryForm;

public class CategoryController extends Controller{
	
	@Inject
	private IVirtualCategoryService vcService;
	
	@Inject
	private IProductVcategoryMapService vmapService;
	
	@Inject
	private ISessionService sessionService;
	
	/**
	 * 虚拟类目查询
	 * 
	 * @return
	 * @author
	 * @since 2015年12月8日 下午6:34:38
	 */
	public Result virtualCategoryQuery(){
		JsonNode main = request().body().asJson();
		if(main == null ){
			return internalServerError("Expecting Json data");
		}
		CategorySearchParamDto dto = JsonFormatUtils.jsonToBean(main.toString(),CategorySearchParamDto.class);
		List<VirtualCategoryForm> vcList = vcService.query(dto);
		return ok(Json.toJson(vcList));
	}
	
	/**
	 * 所有虚拟类目查询
	 * 
	 * @return
	 * @author
	 * @since 2015年12月8日 下午6:34:38
	 */
	public Result virtualCategoryQueryAll(){
		JsonNode main = request().body().asJson();
		if(main == null ){
			return internalServerError("Expecting Json data");
		}
		CategorySearchParamDto dto = JsonFormatUtils.jsonToBean(main.toString(),CategorySearchParamDto.class);
		List<VirtualCategoryForm> vcList = vcService.queryAll(dto);
		return ok(Json.toJson(vcList));
	}
	
	/**
	 * 获取所谓上级虚拟类目
	 * 
	 * @return
	 * @author
	 * @since 2015年12月8日 下午6:34:38
	 */
	public Result queryVirtualParent(Integer vcId){
		List<VirtualCategoryForm> vcate = vcService.queryParent(vcId);
		return ok(Json.toJson(vcate));
	}
	
	/**
	 * 真实类目查询
	 * 
	 * @return
	 * @author
	 * @since 2015年12月8日 下午6:34:38
	 */
	public Result realCategoryQuery(Integer level){
		CategorySearchParamDto dto = new CategorySearchParamDto();
		dto.setLevel(level);
		List<CategoryBase> vcList = vcService.realCateQuery(dto);
		return ok(Json.toJson(vcList));
	}
	
	/**
	 * 根据虚拟类目id拿到产品列表
	 * 
	 * @return
	 * @author 
	 * @since 2015年12月16日 下午5:52:14
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getProductsByVrtCtgId(){
		JsonNode json = request().body().asJson();
		CategorySearchParamDto dxo = CategorySearchParamDxo.json2ParamDto(json);
		Set<Integer> all = new HashSet<>();
		vmapService.queryChild(dxo.getCatIds(),all);
		dxo.setCatIds(new ArrayList<>(all));
		return ok(Json.toJson(vmapService.getProductList(dxo)));
	}
	
	/**
	 * 根据虚拟类目id拿到产品列表（前台使用）
	 * 
	 * @return
	 * @author 
	 * @since 2015年12月16日 下午5:52:14
	 */
	public Result getSkuListByVrtCtId(){
		JsonNode json=request().body().asJson();
		ProductSearchParamDto searchDto = JsonFormatUtils.jsonToBean(json.toString(),ProductSearchParamDto.class);
		// 只能查询可以卖的商品
		searchDto.setSalable(1);
		//change by zbc 虚拟类目下所有商品查询
		searchDto.setModel(sessionService.get("model") != null ? Integer.parseInt(sessionService.get("model").toString()) : null);
		return ok(Json.toJson(vcService.getSkuLists(searchDto)));
	} 
	/**
	 * 根据参数查询虚拟类目
	 * 如 虚拟类目名称 name bbc
	 * 根据虚拟类目 id 查询所有对应的商品
	 * 
	 * @return
	 * @author 
	 * @since 2015年12月16日 下午5:52:14
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getAllSkuListByParam(){
		JsonNode main = request().body().asJson();
		if(main == null ){
			return internalServerError("Expecting Json data");
		}
		//获取对应的类目
		CategorySearchParamDto cateDto = JsonFormatUtils.jsonToBean(main.toString(),CategorySearchParamDto.class);
		cateDto.setModel(sessionService.get("model") != null ? Integer.parseInt(sessionService.get("model").toString()) : null);
		//返回一个list
		return ok(Json.toJson(vcService.getProInfo(cateDto)));
	}
	
	/**
	 * 根据类目Id查询类目详情
	 * @param vcId
	 * @return
	 */
	public Result queryCateDetail(Integer vcId) {
		if(null == vcId) {
			return ok("");
		}
		return ok(Json.toJson(vcService.categoryDetail(vcId)));
	}
}
