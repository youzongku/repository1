/**
 * 
 */
package services.openapi.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.openapi.Category;
import dto.openapi.Product;
import dto.openapi.ProductLite;
import play.Logger;
import play.libs.Json;
import play.mvc.Http.Context;
import services.openapi.ILoginService;
import services.openapi.IProductService;
import utils.HttpUtil;
import utils.Page;

/**
 * @author Lzl
 *
 */
public class ProductService implements IProductService{

	@Inject
	ILoginService loginService;

	@Override
	public Page<Product> getProducts(JsonNode node,Context context) {
		Map<String,Object> param = Maps.newHashMap();
		JsonNode result = null;
		JsonNode login = loginService.currentUser(node.get("ltc").asText());
		String email = login.get("email").asText();
		Integer mode = login.get("distributionMode").asInt();
		JsonNode detailParam = Json.parse(node.get("data").toString()) ;
		param.put("pageSize", detailParam.get("pageSize") == null ? 10 : detailParam.get("pageSize").asInt());
		param.put("currPage", detailParam.get("currPage") == null ? 1 : detailParam.get("currPage").asInt());
		param.put("email", email);
		param.put("istatus", detailParam.get("istatus") == null ? null : detailParam.get("istatus").asInt());
		param.put("title", detailParam.get("title") == null ? null : detailParam.get("title").asText());
		param.put("categoryId", detailParam.get("categoryId") == null ? null : detailParam.get("categoryId").asInt());
		param.put("model", mode);
		param.put("warehouseId", detailParam.get("warehouseId") == null ? null : detailParam.get("warehouseId").asInt());
		List<String> skus = new ArrayList<String>(); 
		if (detailParam.get("skuList") != null) {
			for (JsonNode objNode : detailParam.get("skuList")){
				skus.add(objNode.asText());
			}
		}
		param.put("skuList", skus);
		try {
			String resMsg = HttpUtil.post("{\"data\":"+Json.toJson(param).toString()+"}", HttpUtil.B2BBASEURL+"/product/api/fgetProducts",null);
			result = Json.parse(resMsg).get("data");
			ObjectMapper obj = new ObjectMapper();
			List<ProductLite> products = obj.readValue(result.get("result").toString(), new TypeReference<List<ProductLite>>(){});
			int currPage = result.get("currPage") == null ? 1 : result.get("currPage").asInt();
			int pageSize = result.get("pageSize") == null ? 10 : (result.get("pageSize").asInt() > 100 ? 100 : detailParam.get("pageSize").asInt());
			int totalCount = result.get("rows") == null ? 0 : result.get("rows").asInt();
			List<Product> pros = Lists.newArrayList();
			Product p = null;
			if(products != null && products.size() > 0) {
				for (ProductLite product : products) {
					p = new Product();
					BeanUtils.copyProperties(product, p);
					pros.add(p);
				}
			}
			return  new Page<Product>(currPage, pageSize, totalCount, pros);
		} catch (Exception e) {
			Logger.info("查询商品异常" + e);
			return null;
		}
	}

	@Override
	public String getProductsDetail(JsonNode node,Context context){
		ObjectNode resultNode = Json.newObject();
		String sku = node.get("sku").asText();
		int warehouseId = node.get("warehouseId").asInt();
		
		Logger.info("查询商品详情==sku="+sku+"==warehouseId="+warehouseId);
		
		JsonNode login = loginService.currentUser(node.get("ltc").asText());
		Integer mode = login.get("distributionMode").asInt();
		
		String url = HttpUtil.B2BBASEURL + "/product/api/productDetail";
		Map<String,String> params = Maps.newHashMap();
		params.put("sku", sku);
		params.put("wd", String.valueOf(warehouseId));
		params.put("md", String.valueOf(mode));
		String result = HttpUtil.get(params, url, Context.current(), null);
		JsonNode jsonNode = Json.parse(result);
		ObjectNode dataNode = (ObjectNode) jsonNode.get("data");
		if (!dataNode.has("base")) {
			return "";
		}
		ProductLite productLite = Json.fromJson(dataNode.get("base"), ProductLite.class);
		
		Product product = new Product();
		BeanUtils.copyProperties(productLite, product);
		Double disPrice = 0.0;
		if(mode == 1) {
			disPrice = productLite.getElectricityPrices();
		} else if(mode == 2) {
			disPrice = productLite.getDistributorPrice();
		} else if (mode == 3) {
			disPrice = productLite.getSupermarketPrice();
		} else if (mode == 4) {
			disPrice = productLite.getFtzPrice();
		} else {
			disPrice = product.getProposalRetailPrice();
		}
		product.setDisPrice(disPrice);
		dataNode.put("base", Json.toJson(product));
		
		resultNode.put("data", dataNode);
		
		return resultNode.toString();
	}

	@Override
	public List<Category> queryCategorys(Integer level) {
		String url = HttpUtil.B2BBASEURL + "/product/api/realCateQuery?level=" + level;
		Map<String,String> params = Maps.newHashMap();
		params.put("level", String.valueOf(level));
		String resMsg = HttpUtil.get(params, url, Context.current(), null);
		JsonNode result = Json.parse(resMsg);
		List<Category> cates = Lists.newArrayList();
		if(result != null && result.size() > 0) {
			Category cate = null;
			for (JsonNode node : result) {
				cate = new Category();
				cate.setId(node.get("iid").asInt());
				cate.setCname(node.get("cname").asText());
				cates.add(cate);
			}
		}
		return cates;
	}

}
