package mapper.sales;

import org.apache.ibatis.annotations.Param;

import entity.sales.OrderTimeConfig;

public interface OrderTimeConfigMapper extends BaseMapper<OrderTimeConfig> {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderTimeConfig record);

    int insertSelective(OrderTimeConfig record);

    OrderTimeConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderTimeConfig record);

    int updateByPrimaryKey(OrderTimeConfig record);
    
    OrderTimeConfig selectByShopId(@Param("shopId")Integer shopId);
    
    int updateByParam(OrderTimeConfig record);
    
}