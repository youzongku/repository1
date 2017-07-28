package mapper.dismember;

import entity.dismember.OrderConfig;

public interface OrderConfigMapper extends BaseMapper<OrderConfig> {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderConfig record);

    int insertSelective(OrderConfig record);

    OrderConfig select(OrderConfig config);

    int updateByPrimaryKeySelective(OrderConfig record);

    int updateByPrimaryKey(OrderConfig record);
}