package services.sales.impl;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import dto.sales.LogisticsTracingDto;
import entity.sales.LogisticsTracing;
import entity.sales.OrderPack;
import mapper.sales.LogisticsTracingMapper;
import mapper.sales.OrderPackMapper;
import services.sales.ILogisticsTracingService;

public class LogisticsTracingService implements ILogisticsTracingService {

	@Inject private LogisticsTracingMapper logisticsTracingMapper;
	@Inject private OrderPackMapper orderPachMapper;
	
	@Override
	public int insert(LogisticsTracing record) {
		return logisticsTracingMapper.insert(record);
	}

	@Override
	public int insertSelective(LogisticsTracing record) {
		return logisticsTracingMapper.insertSelective(record);
	}

	@Override
	public void deleteByShipperCodeAndLogisticCode(String shipperCode,
			String logisticCode) {
		logisticsTracingMapper.deleteByShipperCodeAndLogisticCode(shipperCode, logisticCode);
	}

	@Override
	public int batchInsert(List<LogisticsTracing> LogisticsTracings) {
		return logisticsTracingMapper.batchInsert(LogisticsTracings);
	}

	@Override
	public List<LogisticsTracingDto> getLogisticsTracings(String orderNo) {
		List<LogisticsTracingDto> resultLists=Lists.newArrayList();
		List<OrderPack> opLists = orderPachMapper.getOrderPackByOrderNumberAndSKU(orderNo, null);
		if(opLists.isEmpty()){
			return resultLists;
		}
		for(OrderPack op:opLists){
			String[] ctrackingnumberArray = op.getCtrackingnumber().split("，");
			for(String ctrackingnumber: ctrackingnumberArray){
				List<LogisticsTracing> logisticsTracings = logisticsTracingMapper.getLogisticsTracings(ctrackingnumber);
				if(!logisticsTracings.isEmpty()){
					for(LogisticsTracing tempLt: logisticsTracings){
						LogisticsTracingDto ltDto=new LogisticsTracingDto();
						ltDto.setAcceptStation(tempLt.getAcceptStation());
						ltDto.setAcceptTime(tempLt.getAcceptTime());
						ltDto.setAcceptTimeStr(tempLt.getAcceptTimeStr());
						ltDto.setId(tempLt.getId());
						ltDto.setCtrackingnumber(tempLt.getLogisticCode());
						ltDto.setLogisticCode(tempLt.getLogisticCode());
						ltDto.setRemark(tempLt.getRemark());
						ltDto.setShipperCode(tempLt.getShipperCode());
						ltDto.setLogisticName(op.getCshippingname());
						ltDto.setCordernumber(op.getCordernumber());
						resultLists.add(ltDto);
					}
				}else{
					LogisticsTracingDto ltDto=new LogisticsTracingDto();
					ltDto.setLogisticName(op.getCshippingname());
					ltDto.setCordernumber(op.getCordernumber());
					ltDto.setCtrackingnumber(op.getCtrackingnumber());
					resultLists.add(ltDto);
				}
			}
		}
		return resultLists;
	}

}
