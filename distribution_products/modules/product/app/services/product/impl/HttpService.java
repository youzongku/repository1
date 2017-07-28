package services.product.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;

import dto.contract.fee.ContractFeeItemDto;
import dto.product.inventory.InventoryCloudLockDto;
import dto.product.inventory.SaleLockDto;
import play.Logger;
import play.libs.Json;
import services.product.IHttpService;
import util.product.HttpUtil;

public class HttpService implements IHttpService {
	
	@Override
	public JsonNode notifyCalContractFee(ContractFeeItemDto feeItem) throws JsonProcessingException,
	IOException{
		JsonNode paramsNode = Json.toJson(feeItem);
		Logger.info("通知sales模块去计算合同费用-参数:[{}]", paramsNode);
		String resultString = HttpUtil.post(paramsNode.toString(),
				HttpUtil.B2BBASEURL + "/sales/contract/refresh");
		Logger.info("通知sales模块去计算合同费用-结果:{}", resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode getCloudProductsExpirationDate(List<ObjectNode> skuWarehouseIdNodeList) throws JsonProcessingException,
	IOException{
		Map<String, Object> map = Maps.newHashMap();
		map.put("skuWarehouseIdArray", skuWarehouseIdNodeList);
		JsonNode paramsNode = Json.toJson(map);
		Logger.info("获取云仓商品到期日期-参数:[{}]", paramsNode);
		String resultString = HttpUtil.post(paramsNode.toString(),
				HttpUtil.B2BBASEURL + "/inventory/cloud/getCloudDetail");
		Logger.info("获取云仓商品到期日期-结果:{}", resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode getProStock(String email,List<String> skus) throws Exception {
		HashMap<String,Object> param = Maps.newHashMap();
		if(email != null){
			param.put("account", email);
		}
		if(skus!= null){
			param.put("skus", skus);
		}
		Logger.info("查询库存参数:[{}]",Json.toJson(param));
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/inventory/cloud/searchProductCloudAndMicroInventory");
		Logger.info("查询库存结果:[{}]",resultString);
		return parseString(resultString);
	}
	
	@Override
	public JsonNode getMriStock(String email,List<String> skus,Integer warehouseId ) throws Exception {
		HashMap<String,Object> param = Maps.newHashMap();
		if(email != null){
			param.put("account", email);
		}
		if(skus!= null){
			param.put("skus", skus);
		}
		if(warehouseId != null){
			param.put("warehouseId", warehouseId);
		}
		Logger.info("查询库存参数:[{}]",param);
		String resultString = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/inventory/micro/getList");
		Logger.info("查询库存结果:[{}]",resultString);
		return parseString(resultString);
	}
	
	
	@Override
	public JsonNode getMemberInfo(String email) throws JsonProcessingException, IOException {
		Map<String, Object> params = Maps.newHashMap();
		params.put("disEmail", email);
		String resultString = HttpUtil.post(Json.toJson(params).toString(), HttpUtil.B2BBASEURL + "/member/dismember");
		Logger.info("getDismemberByEmail的结果    --->" + resultString);
		return parseString(resultString);
	}

	@Override
	public JsonNode cloudLock(InventoryCloudLockDto cloudLock) throws JsonProcessingException, IOException {
		Logger.info("云仓锁库参数:[{}]",Json.toJson(cloudLock));
		String resultString = HttpUtil.post(Json.toJson(cloudLock).toString(), HttpUtil.B2BBASEURL+"/inventory/inventoryorder/lockcloudstock");
		Logger.info("云仓锁库结果:{}",resultString);
		return parseString(resultString);
	}
	@Override
	public JsonNode getOrderDetails(String orderNo) throws JsonProcessingException, IOException {
		Map<String,String> params = Maps.newHashMap();
		params.put("orderNo", orderNo);
		String resultString = HttpUtil.get(params,HttpUtil.B2BBASEURL+"/inventory/inventoryorder/getOrderDetails");
		return parseString(resultString);
	}
	
	@Override
	public JsonNode microLock(SaleLockDto param) throws JsonProcessingException, IOException {
		Logger.info("微仓锁库参数:[{}]",Json.toJson(param));
		String res = HttpUtil.post(Json.toJson(param).toString(),
				HttpUtil.B2BBASEURL + "/inventory/micro/createSaleOrderWithInventory");
		Logger.info("微仓锁库结果:[{}]",res);
		return parseString(res);
	}
	
	
	@Override
	public JsonNode getMicroProductsExpirationDate(String email, List<ObjectNode> skuWarehouseIdNodeList) throws JsonProcessingException,
	IOException {
		Map<String,Object> map = Maps.newHashMap();
		map.put("account", email);
		map.put("skuWarehouseIdArray", skuWarehouseIdNodeList);
		JsonNode paramsNode = Json.toJson(map);
		Logger.info("获取微仓商品到期日期-参数:[{}]",paramsNode);
		String resultString = HttpUtil.post(paramsNode.toString(),
				HttpUtil.B2BBASEURL
						+ "/inventory/micro/getMicroDetailBySkuAndWareId");
		Logger.info("获取微仓商品到期日期-结果:{}",resultString);
		return parseString(resultString);
	}
	
	private JsonNode parseString(String str) throws JsonProcessingException, IOException {
		ObjectMapper obj = new ObjectMapper();
		return obj.readTree(str);
	}

}
