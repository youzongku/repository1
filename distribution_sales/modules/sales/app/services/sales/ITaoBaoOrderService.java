package services.sales;


import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.fasterxml.jackson.databind.JsonNode;

import dto.sales.TaoBaoOrderForm;
import entity.platform.order.template.TaoBaoOrder;
import pager.sales.Pager;

public interface ITaoBaoOrderService {

	int insert(TaoBaoOrder record);
    int insertSelective(TaoBaoOrder record);
    TaoBaoOrder selectByOrderNoAndEmail(String orderNo,String email);
    Pager<TaoBaoOrder> getAllOrders(@Param("paramDto") TaoBaoOrderForm form);
    int deleteOrder(String orderNo);//物理删除
    int deleteLogicOrder(String orderNo,String email);//逻辑删除
    int batchDeleteOrder(@Param("paramDto") TaoBaoOrderForm form);//批量逻辑删除
    int saveOrder(@Param("paramDto") TaoBaoOrderForm form);
	TaoBaoOrder selectBygroube(TaoBaoOrder appointOrder);
	Map<String, Object> checkByOrderNoAndWarehouseId(JsonNode node);
	
	/**
	 * 查询订单信息
	 * @param taoBaoOrderForm
	 * @param model
	 * @return
	 */
	Map<String, Object> queryOrders(TaoBaoOrderForm taoBaoOrderForm, Integer model);
	
}
