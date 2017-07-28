package mapper.sales;

import dto.sales.ShOrderDto;
import entity.sales.ShOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ShOrderMapper {
    int insert(ShOrder record);

    int insertSelective(ShOrder record);

    List<ShOrder> selectShSaleOrderList(Map<String, Object> map);

    int selectShSaleOrderListCount(Map<String, Object> map);

    List<ShOrder> selectAllAfterSaleOrder(Map<String, Object> paramMap);

    int selectAllAfterSaleOrderCount(Map<String, Object> paramMap);

    ShOrderDto getAfterSaleOrderDtoById(int orderId);

    ShOrder getSalesOrderRefundsById(Integer id);

    int updateSelective(ShOrder shOrder);

    ShOrder selectEffectiveShOrder(@Param("sku") String sku, @Param("xsOrderNo") String xsOrderNo);

    ShOrder selectEffectiveShOrderByDetailOrderId(Integer id);
}