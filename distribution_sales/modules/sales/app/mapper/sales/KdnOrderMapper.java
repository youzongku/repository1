package mapper.sales;

import entity.sales.KdnOrder;

public interface KdnOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(KdnOrder record);

    int insertSelective(KdnOrder record);

    KdnOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(KdnOrder record);

    int updateByPrimaryKey(KdnOrder record);

    KdnOrder selectBySalesOrderNo(String salesOrderNo);

    int countByOrderNo(String salesOrderNo);
}