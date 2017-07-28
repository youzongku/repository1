package services.purchase.returnod.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import mapper.purchase.returnod.ReturnAmountCoefficientLogMapper;
import mapper.purchase.returnod.ReturnAmountCoefficientMapper;
import play.Logger;
import services.purchase.IHttpService;
import services.purchase.returnod.IReturnAmountCoefficientService;
import utils.purchase.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.purchase.returnod.ReturnAmountCoefficientDto;
import entity.purchase.returnod.ReturnAmountCoefficient;
import entity.purchase.returnod.ReturnAmountCoefficientLog;
import forms.purchase.GetProductsParams;
import forms.purchase.Page;
/**
 * 退款系数服务
 * @author Administrator
 *
 */
public class ReturnAmountCoefficientService implements
		IReturnAmountCoefficientService {
	@Inject private ReturnAmountCoefficientLogMapper returnAmountCoefficientLogMapper;
	@Inject private ReturnAmountCoefficientMapper returnAmountCoefficientMapper;
	@Inject private IHttpService httpService;
	
	@Override
	public Page<ReturnAmountCoefficientDto> getCoefficientsOfProducts(
			Map<String, Object> params) {
		JsonNode productsNode = null;
		GetProductsParams getProductsParams = new GetProductsParams();
		getProductsParams.setPageSize((Integer) params.get("pageSize"));
		getProductsParams.setCurrPage((Integer) params.get("currPage"));
		getProductsParams.setCategoryId((Integer) params.get("categoryId"));
		getProductsParams.setTypeId((Integer) params.get("typeId"));
		getProductsParams.setWarehouseId((Integer) params.get("warehouseId"));
		String searchText = (String)params.get("searchText");
		if (StringUtils.isNotBlankOrNull(searchText)) {
	        if (searchText.indexOf(",") == -1) {
	            getProductsParams.setTitle(searchText);
	        } else {
	            getProductsParams.setSkuList(Arrays.asList(searchText.split(",")));
	        }
	    }
		Logger.info("获取商品系数参数：{}",getProductsParams);
		try {
			productsNode = httpService.getProducts(getProductsParams);
		} catch (IOException e) {
			Logger.info("getCoefficientsOfProducts：获取商品数据失败");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		// 重新封装商品结果
		ArrayList<ReturnAmountCoefficientDto> coefficientDtoList  = Lists.newArrayList();
		JsonNode dataNode = productsNode.get("data");
		JsonNode resultNode = dataNode.get("result");
		for(Iterator<JsonNode> it = resultNode.iterator();it.hasNext();){
			JsonNode productNode = it.next();
			coefficientDtoList.add(new ReturnAmountCoefficientDto(productNode,
					returnAmountCoefficientMapper.selectBySkuWarehouseId(
							productNode.get("csku").asText(),
							productNode.get("warehouseId").asInt())));
		}

		Page<ReturnAmountCoefficientDto> page = new Page<>(dataNode.get("currPage").asInt(), dataNode.get("pageSize").asInt(), dataNode.get("rows").asInt(), coefficientDtoList);
		return page;
	}
	
	@Override
	public ReturnAmountCoefficient getProductCoefficients(String sku, Integer warehouseId) {
		ReturnAmountCoefficient rac = returnAmountCoefficientMapper.selectBySkuWarehouseId(sku, warehouseId);
		return rac;
	}
	
	@Override
	public Map<String,Object> setProductsCoefficients(List<ReturnAmountCoefficient> coefficientList) {
		// 区分哪些要更新，哪些要新增
		Map<Boolean, List<ReturnAmountCoefficient>> map = coefficientList
				.stream().collect(
						Collectors.partitioningBy(rac -> rac.getId() == null));
		List<ReturnAmountCoefficient> toUpdateList = map.get(Boolean.FALSE);
		List<ReturnAmountCoefficient> toInsertList = map.get(Boolean.TRUE);
		
		Logger.info("要更新的系数：{}",toUpdateList);
		Logger.info("要新增的系数：{}",toInsertList);
		if(toUpdateList!=null && toUpdateList.size()>0){
			returnAmountCoefficientMapper.batchUpdateByConditions(toUpdateList);
		}
		if(toInsertList!=null && toInsertList.size()>0){
			returnAmountCoefficientMapper.batchInsert(toInsertList);
		}
		
		// 记录日志
		addLogs(coefficientList);
		
		Map<String,Object> result = Maps.newHashMap();
		result.put("suc", true);
		result.put("msg", "设置商品退款系数成功");
		return result;
	}
	
	private void addLogs(List<ReturnAmountCoefficient> coefficientList){
		if(coefficientList.size()>0){
			List<ReturnAmountCoefficientLog> logs = Lists.newArrayList();
			for(ReturnAmountCoefficient rac : coefficientList){
				// TODO 因为mybatis版本原因，会导致批量查询的对象拿不到id，所以导致日志表里的系数id为空
				logs.add(new ReturnAmountCoefficientLog(rac.getId(), rac.getSku(),
						rac.getWarehouseId(), rac.getCoefficientValue(), rac
								.getLastUpdateUser()));
			}
			returnAmountCoefficientLogMapper.batchInsert(logs);
		}
	}

	@Override
	public List<ReturnAmountCoefficientLog> getSetCoefficientLogs(String sku, int warehouseId) {
		return returnAmountCoefficientLogMapper.selectBySkuAndWarehouseId(sku, warehouseId);
	}

}
