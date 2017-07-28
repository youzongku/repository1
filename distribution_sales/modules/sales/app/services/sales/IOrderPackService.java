package services.sales;

import com.fasterxml.jackson.databind.JsonNode;
import entity.sales.OrderPack;

import java.util.List;

/**
 * Created by LSL on 2016/1/20.
 */
public interface IOrderPackService {

    /**
     * 批量插入物流信息
     */
    boolean batchInsert(List<OrderPack> orderPacks);

    /**
     * 批量新增物流信息，会自动过滤已存在的物流信息
     */
    boolean batchAdd(List<OrderPack> orderPacks);

    /**
     * 批量更新物流信息
     */
    boolean batchUpdate(JsonNode node);

    /**
     * 根据条件获取物流信息
     */
    List<OrderPack> getOrderPacksByCondition(JsonNode node);

}
