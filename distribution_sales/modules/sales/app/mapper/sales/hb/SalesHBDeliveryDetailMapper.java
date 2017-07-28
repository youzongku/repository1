package mapper.sales.hb;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.sales.hb.SalesHBDeliveryDetail;
/**
 * 合并发货详情mapper
 */
public interface SalesHBDeliveryDetailMapper {

    int insert(SalesHBDeliveryDetail record);

    int insertSelective(SalesHBDeliveryDetail record);

    SalesHBDeliveryDetail selectByPrimaryKey(Integer id);

    List<SalesHBDeliveryDetail> selectByHbId(@Param("hbId")Integer hbId);
    
    List<SalesHBDeliveryDetail> selectByHbNo(@Param("hbNo")String hbNo);
}