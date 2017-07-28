package mapper.sales;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.sales.TaoBaoOrderForm;
import entity.platform.order.template.TaoBaoOrder;

public interface TaoBaoOrderMapper {
    int insert(TaoBaoOrder record);
    int insertSelective(TaoBaoOrder record);
    List<TaoBaoOrder> selectByOrderNoAndEmail(@Param("orderNo")String orderNo,@Param("email")String email);
    List<TaoBaoOrder> getAllOrders(@Param("paramDto") TaoBaoOrderForm form);
    int getTotal(@Param("paramDto") TaoBaoOrderForm form);
    int deleteOrder(String orderNo);//物理删除
    int deleteLogicOrder(@Param("orderNo") String orderNo,@Param("email") String email);//逻辑删除
    int batchDeleteOrder(@Param("paramDto") TaoBaoOrderForm form);//批量物理删除
    int saveOrder(@Param("paramDao") TaoBaoOrderForm form);
    TaoBaoOrder selectBygroube(@Param("paramDto") TaoBaoOrder appointOrder);
    int batchInsert(List<TaoBaoOrder> orders);

    int updateByPrimaryKeySelective(TaoBaoOrder taoBaoOrder);
}