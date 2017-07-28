package mapper.sales;

import entity.sales.SaleBase;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SaleBaseMapper extends BaseMapper<SaleBase> {
	SaleBase selectByOrderId(Integer orderId);
	List<SaleBase> selectByOrderIdList(List<Integer> orderIdList);

	/**
	 * 通过销售订单编号查询定购人信息
	 * @param salesOrderNo
	 * @return
	 */
	Map getOrderer(@Param("salesOrderNo")String salesOrderNo);
	
	SaleBase getSaleBaseByOrderNo(@Param("salesOrderNo")String salesOrderNo);
	
	List<SaleBase> selectByOrderIdAndWarehouseId(String platformOrderNo, Integer warehouseId);

	List<SaleBase> selectBases(@Param("list")List<Integer> mainIds);
}