package mapper.sales.hb;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.sales.hb.CombinedSalesPageQueryParam;
import entity.sales.hb.SalesHBDelivery;
/**
 * 合并发货mapper
 */
public interface SalesHBDeliveryMapper {

    int insert(SalesHBDelivery record);

    int insertSelective(SalesHBDelivery record);

    SalesHBDelivery selectByPrimaryKey(Integer id);

	SalesHBDelivery selectByHbNo(@Param("salesHbNo") String salesHbNo);

    /**
	 * 分页查询
	 * @param query
	 * @return
	 */
	int selectCountByQueryParam(CombinedSalesPageQueryParam query);
	/**
	 * 分页查询
	 * @param query
	 * @return
	 */
	List<SalesHBDelivery> selectByQueryParam(CombinedSalesPageQueryParam query);

	int updateById(SalesHBDelivery devivery);

}