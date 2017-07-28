package services.openapi.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import dto.openapi.Warehouse;
import play.Logger;
import play.libs.Json;
import play.mvc.Http.Context;
import services.base.utils.JsonFormatUtils;
import services.openapi.IInventoryService;
import services.openapi.ILoginService;
import utils.HttpUtil;
import utils.Page;
import utils.StringUtils;

public class InventoryService implements IInventoryService{
	
	@Inject
	ILoginService loginService;

	@SuppressWarnings("unchecked")
	@Override
	public Page getMicroStorage(JsonNode node,Context context) {
		Map<String,Object> param = new HashMap<String,Object>();
		JsonNode login = loginService.currentUser(node.get("ltc").asText());
		JsonNode result = null;
		Logger.info("登录信息[{}]",login);
		String email = login.get("email").asText();
		param.put("currPage", node.get("currPage") == null ? 1 : node.get("currPage").asInt());
		param.put("email", email);
		param.put("pageSize", node.get("pageSize") == null ? 10 : node.get("pageSize").asInt());
		if(node.has("warehouseId") && !"".equals(node.get("warehouseId").textValue())){
			param.put("warehouseId",node.get("warehouseId").textValue());
		}
		
		if(node.has("productTitle") && !"".equals(node.get("productTitle").textValue())){
			param.put("productTitle",node.get("productTitle").textValue());
		}
		
		if(node.has("productCategoryId") && !"".equals(node.get("productCategoryId").textValue())){
			param.put("productCategoryId",node.get("productCategoryId").textValue());
		}
		
		if(node.has("avaliableStock") && !"".equals(node.get("avaliableStock").textValue())){
			param.put("avaliableStock",node.get("avaliableStock").textValue());
		}
		if(node.has("sku") && !"".equals(node.get("sku").textValue())){
			param.put("sku",node.get("sku").textValue());
		}
		if(node.has("warehouseId") && !"".equals(node.get("warehouseId").textValue())){
			param.put("warehouseId",node.get("warehouseId").textValue());
		}
		try {
			String resMsg = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL+"/inventory/getIvysAndStorage",null);
			result = Json.parse(Json.parse(resMsg).asText()).get("data");
			List list = JsonFormatUtils.jsonToBean(result.get("list").toString(),  List.class);
			int currPage = result.get("pageNo") == null ? 1 : result.get("pageNo").asInt();
			int pageSize = result.get("pageSize") == null ? 10 : (result.get("pageSize").asInt() > 100 ? 100 : result.get("pageSize").asInt());
			int totalCount = result.get("totalCount") == null ? 0 : result.get("totalCount").asInt();
			return new Page(currPage, pageSize,totalCount, list);
		} catch (Exception e) {
			Logger.info("查询微仓出错");
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page getCloudStorage(String sku, Integer wid) {
		Map<String,String> param = new HashMap<String,String>();
		if (sku != null) {
			param.put("sku", sku);
		}
		if (wid != null) {
			param.put("warehouseId", wid+"");
		}
		try {
			String resMsg = HttpUtil.get(param, HttpUtil.B2BBASEURL+"/inventory/warehousing/cloud-inventory",null,null);
			JsonNode result = Json.parse(resMsg);
			List list = JsonFormatUtils.jsonToBean(result.get("list").toString(),  List.class);
			int currPage = result.get("pageNo") == null ? 1 : result.get("pageNo").asInt();
			int pageSize = result.get("pageSize") == null ? 10 : (result.get("pageSize").asInt() > 100 ? 100 :result.get("pageSize").asInt());
			int totalCount = result.get("totalCount") == null ? 0 : result.get("totalCount").asInt();
			return new Page(currPage, pageSize, totalCount,list);
		} catch (Exception e) {
			Logger.info("查询云仓出错");
			return null;
		}
	}

	@Override
	public List<Warehouse> queryWarehouse(String wid) {
		List<Warehouse> list = Lists.newArrayList();
		Map<String,String> param = new HashMap<String,String>();
		if (StringUtils.isNotBlankOrNull(wid)) {
			param.put("wid", wid);
		}
		String resMsg = HttpUtil.get(param, HttpUtil.B2BBASEURL+"/inventory/queryWarehouse",null,null);
		JsonNode result = Json.parse(resMsg);
		if(result != null && result.size() > 0) {
			Warehouse ware = null;
			for (JsonNode node : result) {
				ware = new Warehouse();
				ware.setId(node.get("id").asInt());
				ware.setWarehouseId(node.get("warehouseId").asText());
				ware.setWarehouseName(node.get("warehouseName").asText());
				list.add(ware);
			}
		}
		return list;
	}
}
