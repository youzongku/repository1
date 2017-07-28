package mapper.product_inventory;

import entity.product_inventory.OrderDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderDetail record);

    int insertSelective(OrderDetail record);

    OrderDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderDetail record);

    int updateByPrimaryKey(OrderDetail record);

	OrderDetail selectByParam(@Param("orderDetail") OrderDetail orderDetail);

	List<OrderDetail> selectSalesOrderDetailListByParam(OrderDetail orderDetail);

	List<OrderDetail> selectOrderDetailListByOrderNo(@Param("orderNo") String orderNo);

	int deleteDateByOrderNo(OrderDetail orderDetailParam);

    List<OrderDetail> getOrderDetailBySkuAndSaleOrderNo(@Param("saleOrderNo") String saleOrderNo, @Param("sku") String sku);

    /**
	 * 根据条件查询是否有已经微仓出库但未流转至HK的订单
	 * @param sku
	 * @param warehouseId
	 * @return
	 */
    List<OrderDetail> selectOrderBySkuAndStatus(@Param("sku")String sku, @Param("warehouseId")int warehouseId);
}