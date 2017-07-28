package mapper.dismember;

import java.util.List;

import entity.dismember.DisHeaderSalesman;

public interface DisHeaderSalesmanMapper extends BaseMapper<DisHeaderSalesman> {
    int deleteByPrimaryKey(Integer id);

    int insert(DisHeaderSalesman record);

    int insertSelective(DisHeaderSalesman record);

    List<DisHeaderSalesman> getHeaderSalesmanMapper();

    int updateByPrimaryKeySelective(DisHeaderSalesman record);

    int updateByPrimaryKey(DisHeaderSalesman record);
    
    int deleteByConditon(DisHeaderSalesman record);
    
    DisHeaderSalesman selectBySalesmanId(Integer salesmanId);
}