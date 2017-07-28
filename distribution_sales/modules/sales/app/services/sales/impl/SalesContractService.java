package services.sales.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.JsonResult;
import dto.sales.ContractFeeSearch;
import dto.sales.ContractFeeTypeResult;
import dto.sales.FeeColumnDto;
import dto.sales.FeeItem;
import dto.sales.FeeValue;
import entity.sales.SaleContractFee;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;
import mapper.sales.SaleContractFeeMapper;
import mapper.sales.SaleDetailMapper;
import mapper.sales.SaleMainMapper;
import pager.sales.Pager;
import play.Logger;
import play.libs.Json;
import services.base.utils.JsonFormatUtils;
import services.sales.IHttpService;
import services.sales.ISalesContractService;
import util.sales.Constant;
import util.sales.DateUtils;

/**
 * @author zbc
 * 2017年5月12日 上午10:30:02
 */
public class SalesContractService implements ISalesContractService {
	
	@Inject
	private SaleDetailMapper detailMapper;
	
	@Inject
	private SaleContractFeeMapper feeMapper;
	
	@Inject
	private SaleMainMapper mainMapper;
	
	@Inject
	private IHttpService httpService;

	@Override
	public JsonResult<?> caculate(Integer sid) {
		try {
			SaleMain main = mainMapper.selectByPrimaryKey(sid);
			String sno = main.getSalesOrderNo();
			List<SaleDetail> historys = detailMapper.selectHistoryByOrderId(main.getId()).stream().filter(his->his.getContractNo() != null).collect(Collectors.toList());
			//判断是否包含合同
			if(historys.size()>0){
				Map<String, Object> postMap = getPostList(main,historys);
				List<SaleContractFee> contractFees = Lists.newArrayList();
				// 用于统计整个订单的合同费用
				Map<String, BigDecimal> orderFeeMap = Maps.newHashMap();
				orderFeeMap.put("orderEstimatedTotal", null);
				orderFeeMap.put("orderActualTotal", null);
				JsonNode jsonRes = httpService.getContractFeeItems(postMap);
				if (jsonRes.get("suc").asBoolean()) {
					ObjectMapper map = new ObjectMapper();
					List<ContractFeeTypeResult> feeTypes = map.readValue(jsonRes.get("result").toString(),
							new TypeReference<List<ContractFeeTypeResult>>() {
							});
					// 过滤掉费用项为0的合同
					feeTypes = feeTypes.stream().filter(f -> f.getFeeItems() != null && f.getFeeItems().size() > 0)
							.collect(Collectors.toCollection(ArrayList::new));
					if (feeTypes.size() > 0) {
						// 根据合同号分组
						Map<String, List<SaleDetail>> historyMap = historys.stream()
								.collect(Collectors.groupingBy(h -> h.getContractNo()));
						// 遍历合同
						for (ContractFeeTypeResult feeType : feeTypes) {
							// 遍历费用项 计算费用
							caculateFee(orderFeeMap, contractFees, feeType.getContractNo(), sno,
									historyMap.get(feeType.getContractNo()), feeType.getFeeItems());
						}
						contractFees.forEach(f -> {
							if (Constant.CONTRACT_ORDER_ACTUAL_COST_TOTAL.equals(f.getAttrKey())) {
								f.setValue(orderFeeMap.get("orderActualTotal") != null
										? orderFeeMap.get("orderActualTotal").toString() : null);
							} else if (Constant.CONTRACT_ORDER_ESTIMATED_COST_TOTAL.equals(f.getAttrKey())) {
								f.setValue(orderFeeMap.get("orderEstimatedTotal") != null
										? orderFeeMap.get("orderEstimatedTotal").toString() : null);
							}
						});
					}
					//防止更新到状态
					main.setStatus(null);
					//设置订单预估费用，实际费用
					main.setEstimatedCatfeeInOrder(orderFeeMap.get("orderEstimatedTotal")!=null?orderFeeMap.get("orderEstimatedTotal").doubleValue():null);
					main.setRealCatfeeInOrder(orderFeeMap.get("orderActualTotal")!=null?orderFeeMap.get("orderActualTotal").doubleValue():null);
					mainMapper.updateByPrimaryKeySelective(main);
					//查询所有费用
					feeMapper.selectByParams(sno,null).forEach(f->{
						f.setUpdateTime(new Date());
						f.setIsDelete(true);
						feeMapper.updateByPrimaryKeySelective(f);
					});
					//插入费用
					contractFees.forEach(f->{
						feeMapper.insertSelective(f);
					});
				}
			}
			return JsonResult.newIns().result(true).msg("计算合同费成功").data(mainMapper.selectByPrimaryKey(sid));
		} catch (Exception e) {
			Logger.info("计算合同费用异常:{}",e);
			return JsonResult.newIns().result(false).msg("计算合同费用异常").data(mainMapper.selectByPrimaryKey(sid));
		}
	}
	
	/**
	 * 组织post 参数
	 * @author zbc
	 * @return 
	 * @since 2017年5月12日 上午11:41:55
	 */
	public static Map<String, Object> getPostList(SaleMain main,List<SaleDetail> historys){
		historys = historys.stream().filter(de -> de.getContractNo() != null)
				.collect(Collectors.toCollection(ArrayList::new));
		Map<String, List<ObjectNode>> param = Maps.newHashMap();
		for (SaleDetail his : historys) {
			if (param.get(his.getContractNo()) == null) {
				param.put(his.getContractNo(), Lists.newArrayList());
			}
			param.get(his.getContractNo())
					.add(Json.newObject().put("sku", his.getSku()).put("warehouseId", his.getWarehouseId()));
		}
		List<Map<String, Object>> postList = Lists.newArrayList();
		Map<String, Object> postMap = null;
		for (Map.Entry<String, List<ObjectNode>> entrySet : param.entrySet()) {
			postMap = Maps.newHashMap();
			postMap.put("contractNo", entrySet.getKey());
			postMap.put("pros", entrySet.getValue());
			postList.add(postMap);
		}
		postMap =  Maps.newHashMap();
		postMap.put("payDate", main.getPurchasePayDateStr());
		postMap.put("contracts", postList);
		return postMap;
	}

	private  void caculateFee(Map<String,BigDecimal> orderFeeMap,List<SaleContractFee> contractFees,String contractNo,String orderNo,List<SaleDetail> list, List<FeeItem> feeItems) {
		List<SaleDetail> calList = null;
		SaleContractFee contractFee = null;
		BigDecimal quoteTotal = null;//报价总价
		FeeValue feeValue = null;
		//报价
		SaleContractFee quotedFee = new SaleContractFee(orderNo, contractNo, null,Constant.CONTRACT_QUOTED);
		quotedFee.setValue(new BigDecimal(list.stream().mapToDouble(de -> {
			return de.getQty() * de.getPurchasePrice();
		}).sum()).setScale(4, BigDecimal.ROUND_HALF_UP).toString());
		contractFees.add(quotedFee);
		//合同内预估总计
		SaleContractFee  estimatedTotalFee = new SaleContractFee(orderNo, contractNo,null,Constant.CONTRACT_ESTIMATED_COST_TOTAL);  
		//合同内实际总价
		SaleContractFee  actualTotalFee = new SaleContractFee(orderNo, contractNo,null,Constant.CONTRACT_ACTUAL_COST_TOTAL);  
		String key = null;
		for (FeeItem item : feeItems) {
			calList = list.stream().filter(d -> {
				return Lists.transform(item.getRelatedSkus(), s -> s.getSku() + "_" + s.getWarehouseId())
						.indexOf(d.getSku() + "_" + d.getWarehouseId()) > -1;
			}).collect(Collectors.toCollection(ArrayList::new));
			if(calList.size()>0){
				quoteTotal = new BigDecimal(calList.stream().mapToDouble(de -> {
					return de.getQty() * de.getPurchasePrice();
				}).sum());
				contractFee = new SaleContractFee(orderNo,contractNo);
				feeValue = item.getContractFeeValue();
				if(feeValue != null){
					if (item.getFeeType() == 1) {// 费用值
						key = Constant.CONTRACT_ESTIMATED_COST;
						//合同内预估均摊费用
						contractFee.setAttrName(getName(item.getFeeTypeName(),key));
						//change by zbc 使用费用自增长id 可以确定唯一性，用于后续的更新操作
						contractFee.setAttrKey(getKey(item.getId(), key));
						contractFee.setFeeId(item.getFeeTypeId());
						//预估费用
						contractFee.setValue(rateCaculate(feeValue.getEstimatedRate(),quoteTotal));
						//预估合计
						innerSum(estimatedTotalFee, contractFee);
						contractFees.add(contractFee);
						//合同内实际均摊费用
						contractFee = new SaleContractFee(orderNo,contractNo);
						key = Constant.CONTRACT_ACTUAL_COST;
						contractFee.setValue(rateCaculate(feeValue.getRealRate(),quoteTotal));
					} else if (item.getFeeType() == 2) {// 费用率
						key = Constant.CONTRACT_FEE_RATE;
						contractFee.setValue(rateCaculate(feeValue.getFeeRate(),quoteTotal));
					    //预估合计
						innerSum(estimatedTotalFee, contractFee);
					}
					contractFee.setFeeId(item.getFeeTypeId());
					contractFee.setAttrName(getName(item.getFeeTypeName(),key));
					//change by zbc 使用费用自增长id 可以确定唯一性，用于后续的更新操作
					contractFee.setAttrKey(getKey(item.getId(), key));
					innerSum(actualTotalFee, contractFee);
					contractFees.add(contractFee);
				}
			}
		}
		contractFees.add(estimatedTotalFee);
		orderFeeMap.put("orderEstimatedTotal",orderSum(orderFeeMap.get("orderEstimatedTotal"), estimatedTotalFee));
		//初始化订单预估总计
		contractFees.add(new SaleContractFee(orderNo, contractNo,null, Constant.CONTRACT_ORDER_ESTIMATED_COST_TOTAL));
		contractFees.add(actualTotalFee);
		orderFeeMap.put("orderActualTotal",orderSum(orderFeeMap.get("orderActualTotal"), actualTotalFee));
		//初始化订单实际总计
		contractFees.add(new SaleContractFee(orderNo, contractNo, null,Constant.CONTRACT_ORDER_ACTUAL_COST_TOTAL));
	}

	private  BigDecimal orderSum(BigDecimal orderEstimatedTotal, SaleContractFee estimatedTotalFee) {
		return orderEstimatedTotal != null
				? orderEstimatedTotal.add(getValue(estimatedTotalFee.getValue()))
				: estimatedTotalFee.getValue() != null ? new BigDecimal(estimatedTotalFee.getValue()) : null;
	}
	/**
	 * 合同内 费用计算
	 * @author zbc
	 * @since 2017年5月15日 上午9:47:55
	 * @param totalFee
	 * @param fee
	 */
	public  void innerSum(SaleContractFee totalFee,SaleContractFee fee){
		BigDecimal total = totalFee.getValue() != null ? getValue(totalFee.getValue()).add(getValue(fee.getValue()))
				: fee.getValue() != null ? new BigDecimal(fee.getValue()) : null;
		totalFee.setValue(total != null?total.toString():null);		
	}
	
	
	/**
	 * @author zbc
	 * @since 2017年5月15日 上午9:48:17
	 * @param value
	 * @return
	 */
	public  BigDecimal getValue(String value){
		return value != null?new BigDecimal(value):BigDecimal.ZERO;
	}
	/**
	 * 费用计算
	 * @author zbc
	 * @since 2017年5月12日 下午5:20:04
	 * @return
	 */
	public  String rateCaculate(BigDecimal rate,BigDecimal quoteTotal){
			return rate == null?null:rate.multiply(quoteTotal).setScale(4, BigDecimal.ROUND_HALF_UP).toString();
	}

	@Override
	public List<FeeColumnDto> fieldNames(String cno) {
		List<FeeColumnDto> fields = Lists.newArrayList();
		fields.add(new FeeColumnDto( "订单号", "orderNo","orderNo"));
		fields.add(new FeeColumnDto( "下单时间", "orderingDate","orderingDate"));
		fields.addAll(feeMapper.getFields(cno));
		return fields;
	}

	@Override
	public JsonResult<?> pages(String string) {
		ContractFeeSearch search  = JsonFormatUtils.jsonToBean(string, ContractFeeSearch.class);
		Logger.info(Json.toJson(search).toString());
		//分页与排序
		List<SaleMain> list = feeMapper.pageSearch(search);
		Map<String, List<SaleContractFee>> feesMap = feeMapper.selectByParams(null, search.getContractNo()).stream()
				.collect(Collectors.groupingBy(f -> f.getSalesOrderNo()));
		List<Map<String,Object>> mapList = Lists.newArrayList();
		Map<String,Object> map = null;
		for(SaleMain sm:list){
			map = Maps.newHashMap();
			map.put("orderNo", sm.getSalesOrderNo());
			map.put("orderingDate", DateUtils.date2string(sm.getOrderingDate(), DateUtils.FORMAT_FULL_DATETIME));
			for(SaleContractFee f:feesMap.get(sm.getSalesOrderNo())){
				map.put(f.getAttrKey(), f.getValue() != null?new BigDecimal(f.getValue()) :null);
			}
			mapList.add(map);
		}
		return JsonResult.newIns().result(true).data(new Pager<>(mapList, search.getPage(), search.getRows(), feeMapper.pageCount(search)));
	}
	
	public String getKey(Integer typeId,String typeKey){
		return typeKey+typeId;
	}

	public String getName(String typeName,String key){
		StringBuilder build = new StringBuilder();
		if(typeName != null){
			build.append(typeName);
		}
		String keyName = Constant.CONTRACT_FEE_MAP.get(key);
		if(keyName != null){
			build.append(keyName);
		}else{
			build.append("（元）");
		}
		return build.toString();
	}

	@Override
	public JsonResult<?> refresh(String string) {
		try {
			// 更新合同费用逻辑修改
			FeeItem feeItem = JsonFormatUtils.jsonToBean(string, FeeItem.class);
			if (feeItem.getFeeType() != 1) {// 费用值
				return JsonResult.newIns().result(true).msg("该类型费用项不能修改合同费用");
			}
			Map<String,List<SaleContractFee>> feesMap = feeMapper.selectByParams(null, feeItem.getContractNo()).stream().collect(Collectors.groupingBy(fee->fee.getSalesOrderNo()));
			String orderNo = null;
			List<SaleContractFee> fees = null;
			for(Entry<String, List<SaleContractFee>> entry:feesMap.entrySet() ){
				orderNo = entry.getKey();
				fees = entry.getValue();
				//处理更新合同费用逻辑
				dealRefresh(feeItem,orderNo,fees);
			}
			return JsonResult.newIns().result(true).msg("刷新费用项成功");
		} catch (Exception e) {
			Logger.info("刷新费用项异常{}",e);
			return JsonResult.newIns().result(false).msg("刷新费用项异常");
		}
	}

	private void dealRefresh(FeeItem feeItem, String orderNo, List<SaleContractFee> fees) {
		Map<String,SaleContractFee> feeMap = Maps.uniqueIndex(fees, fee->fee.getAttrKey());
		SaleContractFee fee = feeMap.get(getKey(feeItem.getId(), Constant.CONTRACT_ACTUAL_COST));
		//判断该费用那个是否存在,若是，更新费用值
		if(fee != null){
			SaleMain sm = mainMapper.selectByOrderNo(orderNo);
			List<SaleDetail> details = detailMapper.getHistoryByOrderId(sm.getId());
			List<SaleDetail> calList = details.stream().filter(d -> {
				return Lists.transform(feeItem.getRelatedSkus(), s -> s.getSku() + "_" + s.getWarehouseId())
						.indexOf(d.getSku() + "_" + d.getWarehouseId()) > -1;
			}).collect(Collectors.toCollection(ArrayList::new));
			if(calList.size() > 0){
				BigDecimal oldValue = getValue(fee.getValue()); 
				BigDecimal quoteTotal =	new BigDecimal(calList.stream().mapToDouble(de -> {
					return de.getQty() * de.getPurchasePrice();
				}).sum());
				fee.setValue(rateCaculate(feeItem.getContractFeeValue().getRealRate(),quoteTotal));
				fee.setUpdateTime(new Date());
				feeMapper.updateByPrimaryKeySelective(fee);
				BigDecimal fixValue = getValue(fee.getValue()).subtract(oldValue);
				//调整金额（差值）
				// 更新合同内总计，订单内总计 所有总计加上调整金额，就是更新后的总计
				for(SaleContractFee f:feeMapper.selectByParams(orderNo, null)){
					if(f.getAttrKey().equals(Constant.CONTRACT_ACTUAL_COST_TOTAL)&&f.getContractNo().equals(feeItem.getContractNo())){
						updateValue(fee, fixValue, f);
					}else if(f.getAttrKey().equals(Constant.CONTRACT_ORDER_ACTUAL_COST_TOTAL)){
						updateValue(fee, fixValue, f);
						//更新订单实际费用
						sm.setRealCatfeeInOrder(new Double(f.getValue()));
						mainMapper.updateByPrimaryKeySelective(sm);
					}
				}
			}
		}
	}

	private void updateValue(SaleContractFee fee, BigDecimal fixValue, SaleContractFee f) {
		if(f.getValue() == null){
			f.setValue(fee.getValue());
		}else{
			f.setValue(getValue(f.getValue()).add(fixValue).setScale(4, BigDecimal.ROUND_HALF_UP).toString());
		}
		f.setUpdateTime(new Date());
		feeMapper.updateByPrimaryKeySelective(f);
	}

	@Override
	public Map<String, Object> contractFee(String cno) {
		//合同实际费用总计
		BigDecimal contractActualTotal = null;
		//合同预估费用总计
		BigDecimal contractEstimatedTotal = null;
		for(SaleContractFee f:feeMapper.selectByParams(null, cno)){
			if(f.getAttrKey().equals(Constant.CONTRACT_ACTUAL_COST_TOTAL)){
				contractActualTotal = f.getValue() != null ? contractActualTotal != null
						? contractActualTotal.add(getValue(f.getValue())) : getValue(f.getValue())
						: contractActualTotal;
			}else if(f.getAttrKey().equals(Constant.CONTRACT_ESTIMATED_COST_TOTAL)){
				contractEstimatedTotal = f.getValue() != null ? contractEstimatedTotal != null
						? contractEstimatedTotal.add(getValue(f.getValue())) : getValue(f.getValue())
						: contractEstimatedTotal;
			}
		}
		Map<String,Object> res = Maps.newHashMap();
		res.put("contractActualTotal", contractActualTotal);
		res.put("contractEstimatedTotal", contractEstimatedTotal);
		return res;
	}
}

