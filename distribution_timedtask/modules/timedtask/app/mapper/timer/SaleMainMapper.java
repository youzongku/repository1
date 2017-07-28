package mapper.timer;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import entity.timer.SaleMain;
import entity.timer.SalesToB2cIterm;

public interface SaleMainMapper extends BaseMapper<SaleMain> {

	public int updateTaxFee(SaleMain sm);
	
	public SaleMain selectSaleMain(@Param("id")Integer id,@Param("salesOrderNo")String salesOrderNo);
	
	public SaleMain selectByOrderNo(@Param("orderNo") String orderNo);
	
	/**
	 * 查询销售订单信息
	 * @param map
	 */
	public List<SalesToB2cIterm> getSalesInfo(Map map);

	/**
	 * 通过Id查询销售订单信息
	 * @param id
	 * @return
	 */
	public Map<String,String> getSalesById(@Param("id") Integer id);
	
	/**
	 * 根据支付时间查询订单
	 * @param paystr
	 * @return
	 */
	public List<SaleMain> getAutoConfirmOrders(@Param("paystr")String paystr);
}