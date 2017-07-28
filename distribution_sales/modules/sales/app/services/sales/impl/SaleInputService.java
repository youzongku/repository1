package services.sales.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import mapper.sales.SaleInputMapper;
import play.Logger;
import play.libs.Json;
import services.sales.ISaleInputService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import entity.sales.SaleInput;

public class SaleInputService implements ISaleInputService{

	@Inject	private SaleInputMapper saleInputMapper;
	
	@Override
	public String addProducts(String param) {
		ObjectNode  resultMap = Json.newObject();
		JsonNode json = Json.parse(param);
		ObjectMapper obj = new ObjectMapper();
		try {
			List<SaleInput> saleInputs = obj.readValue(json.toString(), new TypeReference<List<SaleInput>>(){});
			String email = saleInputs.get(0).getEmail();
			//先清空此分销商之前选择的商品信息
			saleInputMapper.deleteByEmail(email);
			int flag = saleInputMapper.batchInsert(saleInputs);
			if (flag > 0){
				resultMap.put("suc", true);
				resultMap.put("msg", "保存成功");
				return resultMap.toString();
			}
			resultMap.put("suc", false);
			resultMap.put("msg", "保存失败");
			return resultMap.toString();
		} catch (IOException e) {
			Logger.info("保存商品信息异常" + e);
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String getCheckedProducts(String email) {
		ObjectNode  resultMap = Json.newObject();
		List<SaleInput> list = new ArrayList<SaleInput>();
	    list = saleInputMapper.selectByEmail(email);
	    resultMap.put("suc", true);
	    resultMap.put("data", Json.toJson(list));
		return resultMap.toString();
	}
	
	@Override
	public String refreshProducts(String email) {
		ObjectNode resultMap = Json.newObject();
		int flag = saleInputMapper.deleteByEmail(email);
		if (flag == 0) {
			resultMap.put("suc", false);
			resultMap.put("msg", "删除失败");
			return resultMap.toString();
		}
		
		resultMap.put("suc", true);
		resultMap.put("msg", "删除成功");
		return resultMap.toString();
	}
	
	@Override
	public String updateInfo(String param) {
		ObjectNode  resultMap = Json.newObject();
		JsonNode json = Json.parse(param);
		SaleInput saleInput = new SaleInput();
		saleInput.setId(json.get("id").asInt());
		saleInput.setQty(json.get("qty") == null ? null : json.get("qty").asInt());
		int flag = saleInputMapper.updateByPrimaryKeySelective(saleInput);
		if (flag == 0) {
			resultMap.put("suc", false);
			resultMap.put("msg", "更新失败");
			return resultMap.toString();
		}
		
		resultMap.put("suc", true);
		resultMap.put("msg", "更新成功");
		return resultMap.toString();
	}
	
	@Override
	public String batchDelete(String param) {
		ObjectNode  resultMap = Json.newObject();
		JsonNode json = Json.parse(param);
		List<Integer> proIds = new ArrayList<Integer>();
		for (JsonNode node : json.get("ids")){
			proIds.add(node.asInt());
		}
		int flag = saleInputMapper.deleteByIds(proIds);
		if (flag == 0) {
			resultMap.put("suc", false);
			resultMap.put("msg", "删除失败");
			return resultMap.toString();
		}
		resultMap.put("suc", true);
		resultMap.put("msg", "删除成功");
		return resultMap.toString();
	}
}
