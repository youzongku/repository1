package mapper.sales;

import entity.sales.ShLog;

import java.util.List;

public interface ShLogMapper {
    int insert(ShLog record);

    int insertSelective(ShLog record);

    List<ShLog> getShLogListByShOrderId(int id);
}