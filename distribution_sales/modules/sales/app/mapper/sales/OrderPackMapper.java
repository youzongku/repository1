package mapper.sales;

import entity.sales.OrderPack;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderPackMapper extends BaseMapper<OrderPack> {

    /**
     * 批量插入物流信息
     */
    int batchInsert(@Param("list")List<OrderPack> orderPacks);

    /**
     * 根据订单号和商品SKU获取物流信息
     */
    List<OrderPack> getOrderPackByOrderNumberAndSKU(@Param("ordernumber")String ordernumber, @Param("sku")String sku);

    /**
     * 根据订单号和物流跟踪号获取物流信息
     */
    OrderPack getOrderPackByOrderNumberAndTrackNumber(@Param("ordernumber")String ordernumber, @Param("tracknumber")String tracknumber);

}