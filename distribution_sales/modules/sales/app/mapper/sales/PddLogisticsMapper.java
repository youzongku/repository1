package mapper.sales;

import entity.sales.PddLogistics;

public interface PddLogisticsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PddLogistics record);

    int insertSelective(PddLogistics record);

    PddLogistics selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PddLogistics record);

    int updateByPrimaryKey(PddLogistics record);

    PddLogistics findPddLogisticsByCompanyName(String companyName);
}