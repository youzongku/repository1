package services.sales.impl;


import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import entity.sales.SaleDetail;
import mapper.sales.SaleDetailMapper;
import services.sales.ISaleDetailsService;

public class SaleDetailsService implements ISaleDetailsService {

	@Inject private SaleDetailMapper saleDetailMapper;
	
	@Override
	public boolean saveBatchSaleDetails(List<SaleDetail> saleDetails) {
		return saleDetailMapper.batchInsert(saleDetails)>0;
	}

	@Override
	public boolean updateBatchSaleDetail(List<SaleDetail> salesDetails) {
		return saleDetailMapper.batchUpdate(salesDetails)>0;
	}

	@Override
	public List<SaleDetail> getAllSaleDetailsByOrderId(Integer orderId) {
		return saleDetailMapper.selectByOrderId(orderId);
	}
	
	public Double jsonToDouble(JsonNode node){
		return node == null?null:("null".equals(node.asText())?null:node.asDouble());
	}
	public String jsonToString(JsonNode node){
		return node == null?null:("null".equals(node.asText())?null:node.asText());
	}
	public Integer jsonToInt(JsonNode node){
		return node == null?null:("null".equals(node.asText())?null:node.asInt());
	}
	public Boolean jsonToBoolean(JsonNode node){
		return node == null?null:("null".equals(node.asText())?null:node.asBoolean());
	}
	
	@Override
	public int updateIsAfterSaleTo1(String saleNo, String sku) {
		return saleDetailMapper.updateIsAfterSaleTo1(saleNo, sku);
	}
	
}
