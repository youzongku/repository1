package services.sales;

import java.util.List;

import dto.sales.LogisticsTracingDto;
import entity.sales.LogisticsTracing;

public interface ILogisticsTracingService {
	
	public int insert(LogisticsTracing record);

    public int insertSelective(LogisticsTracing record);
	
	public void deleteByShipperCodeAndLogisticCode(String shipperCode,String logisticCode);
	
    public int batchInsert(List<LogisticsTracing> LogisticsTracings);
    
    public List<LogisticsTracingDto> getLogisticsTracings(String orderNo);


}
