package services.marketing.promotion.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import play.libs.Json;
import services.marketing.promotion.IHttpService;
import util.marketing.promotion.HttpUtil;

public class HttpServiceImpl implements IHttpService {

	@Override
	public JsonNode getProducts(List<String> skus, Integer warehouseId,Integer categoryId,Integer model,String email) throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("skuList", skus);
		param.put("warehouseId", warehouseId);
		param.put("categoryId", categoryId);
		param.put("model", model);
		param.put("email", email);
		param.put("istatus", 1);
		Map<String, Object> params = Maps.newHashMap();
		params.put("data", param);
		String resultString = HttpUtil.post(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/product/api/getProducts");
		return parseString(resultString);

	}
	private JsonNode parseString(String str) throws JsonProcessingException, IOException {
		ObjectMapper obj = new ObjectMapper();
		return obj.readTree(str);
	}

}
