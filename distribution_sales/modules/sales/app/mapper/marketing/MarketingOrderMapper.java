package mapper.marketing;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.marketing.QueryMarketingOrderParams;
import entity.marketing.MarketingOrder;

public interface MarketingOrderMapper {
	
	List<MarketingOrder> getMarketingOrderList(QueryMarketingOrderParams param);
	int getMarketingOrderCount(QueryMarketingOrderParams param);

	int updateStatusById(@Param("id") Integer id, @Param("status") int status,
			@Param("lastUpdateUser") String lastUpdateUser);

	int insert(MarketingOrder record);

	int insertSelective(MarketingOrder record);

	MarketingOrder selectByPrimaryKey(Integer id);
	MarketingOrder selectByMarketingOrderNo(@Param("marketingOrderNo")String marketingOrderNo);

	int updateByPrimaryKeySelective(MarketingOrder record);
	
	MarketingOrder getBySno(@Param("sno")String saleOrderNo);
	
}