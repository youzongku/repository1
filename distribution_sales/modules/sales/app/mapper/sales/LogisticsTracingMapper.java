package mapper.sales;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.sales.LogisticsTracing;

public interface LogisticsTracingMapper {
	
    public int insert(LogisticsTracing record);

    public int insertSelective(LogisticsTracing record);
    
    public void deleteByShipperCodeAndLogisticCode(@Param("shipperCode") String shipperCode,@Param("logisticCode") String logisticCode);
    
    public int batchInsert(@Param("list")List<LogisticsTracing> LogisticsTracings);
  
    public List<LogisticsTracing> getLogisticsTracings(@Param("logisticCode") String logisticCode);

    
}