/**
 * 
 */
package controllers.openapi;


import javax.inject.Inject;

import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.openapi.IProductService;
import utils.Page;
import utils.StringUtils;
import utils.response.ResponseResultUtil;
import annotation.Login;

import com.fasterxml.jackson.databind.JsonNode;

import dto.openapi.ProductLite;

/**
 * @author Lzl
 *
 */
public class ProductController extends Controller{

	@Inject
	IProductService productService;

		/**	
		 * 查询指定商品
		 * {
		    "data": {
		        "pageSize": "10",
		        "currPage": "1",
		        "istatus": 1,
		        "skuList": [],
		        "title": "IF639",
		        "categoryId":4695,
		        "model":1
		    }
		}
		*/
	@SuppressWarnings("unchecked")
	@Login
	public Result products(){
		JsonNode node = request().body().asJson();
		if(node == null || node.size() == 0 || !node.has("data")) {
			return ResponseResultUtil.newErrorJson(101, "参数错误");
		}
		Page<ProductLite> result = productService.getProducts(node,Context.current());
		if (result == null) {
			return ResponseResultUtil.newErrorJson(105, "查询商品出错");
		}
		return ResponseResultUtil.newSuccessJson(result);
	}
	
	/**
	 * 查询商品详情
	 * @return
	 */
	@Login
	public Result getProductsDetail(){
		JsonNode node = request().body().asJson();
		if(node == null || node.size() == 0 || 
				!node.has("sku") || !node.has("warehouseId") || !node.has("ltc")) {
			return ResponseResultUtil.newErrorJson(101, "参数错误");
		}
		
		if(!node.get("warehouseId").canConvertToInt()){
			return ResponseResultUtil.newErrorJson(101, "参数错误");
		}
		
		String productsDetail = productService.getProductsDetail(node,Context.current());
		
		if (StringUtils.isBlankOrNull(productsDetail)) {
			return ResponseResultUtil.newErrorJson(105, "查询不到商品详情");
		}
		
		return ResponseResultUtil.newSuccessJson(productsDetail);
	}

	@Login
	public Result queryCategorys(){
		JsonNode node = request().body().asJson();
		if(node == null || node.size() == 0 || !node.has("ltc")) {
			return ResponseResultUtil.newErrorJson(101, "参数错误");
		}
		return ResponseResultUtil.newSuccessJson(productService.queryCategorys(1));
	}

}
