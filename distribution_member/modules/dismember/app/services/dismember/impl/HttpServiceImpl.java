package services.dismember.impl;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

import play.Logger;
import play.libs.Json;
import services.dismember.IHttpService;
import utils.dismember.HttpUtil;

public class HttpServiceImpl implements IHttpService {

	@Override
	public JsonNode getPurchaseOrder(String purchaseOrderNo) throws JsonProcessingException, IOException {
		Map<String, Object> params = Maps.newHashMap();
		params.put("purchaseOrderNo", purchaseOrderNo);
		Logger.info("getPurchaseOrder post_string--->"
				+ Json.toJson(params));
		String resultString = HttpUtil.httpPost(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/purchase/simpleInfo");
		Logger.info("getPurchaseOrder result--->" + resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode getSalesOrder(String salesOrderNo) throws JsonProcessingException, IOException {
		Map<String, Object> params = Maps.newHashMap();
		params.put("salesOrderNo", salesOrderNo);
		Logger.info("getSalesOrder post_string--->"
				+ Json.toJson(params));
		String resultString = HttpUtil.httpPost(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/sales/manager/getSaleOrderByNo");
		Logger.info("getSalesOrder result--->" + resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode getCombinedSalesOrder(String hbNo) throws JsonProcessingException, IOException {
		Map<String, Object> params = Maps.newHashMap();
		params.put("hbNo", hbNo);
		Logger.info("getCombinedSalesOrder post_string--->"
				+ Json.toJson(params));
		String resultString = HttpUtil.httpPost(Json.toJson(params).toString(),
				HttpUtil.B2BBASEURL + "/sales/hb/prodetails");
		Logger.info("getCombinedSalesOrder result--->" + resultString);
		return parseString(resultString);
	}
	
	
}
