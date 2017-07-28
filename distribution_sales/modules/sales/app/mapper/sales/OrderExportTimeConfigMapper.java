package mapper.sales;

import entity.sales.OrderExportTimeConfig;

/**
 * @author zbc
 * 2017年6月26日 下午3:08:08
 */
public interface OrderExportTimeConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderExportTimeConfig record);

    int insertSelective(OrderExportTimeConfig record);

    OrderExportTimeConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderExportTimeConfig record);

    int updateByPrimaryKey(OrderExportTimeConfig record);

	OrderExportTimeConfig selectByParmas(String operator, String exportType);
}