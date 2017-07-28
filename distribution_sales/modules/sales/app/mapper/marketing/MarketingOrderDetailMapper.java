package mapper.marketing;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.marketing.MarketingOrderDetail;

public interface MarketingOrderDetailMapper {

	int insert(MarketingOrderDetail record);

	int insertSelective(MarketingOrderDetail record);

	MarketingOrderDetail selectByPrimaryKey(Integer id);
	List<MarketingOrderDetail> selectByMoIdList(@Param("moIdList")List<Integer> moIdList);

	int updateByPrimaryKeySelective(MarketingOrderDetail record);

	int insertBatch(List<MarketingOrderDetail> detailList);
	
	List<MarketingOrderDetail>  getDetailsByNo(@Param("orderNo")String orderNo);

}