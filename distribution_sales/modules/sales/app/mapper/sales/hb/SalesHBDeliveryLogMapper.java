package mapper.sales.hb;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.sales.hb.SalesHBDeliveryLog;

public interface SalesHBDeliveryLogMapper {
    int insertSelective(SalesHBDeliveryLog record);

    SalesHBDeliveryLog selectByPrimaryKey(Integer id);
    
    List<SalesHBDeliveryLog> selectBySalesHbId(@Param("salesHbId")Integer salesHbId);
}