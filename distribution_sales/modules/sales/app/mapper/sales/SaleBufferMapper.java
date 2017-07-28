package mapper.sales;

import java.util.List;

import entity.sales.SaleBuffer;

public interface SaleBufferMapper extends BaseMapper<SaleBuffer> {
    int deleteByPrimaryKey(Integer id);

    int insert(SaleBuffer record);

    int insertSelective(SaleBuffer record);

    SaleBuffer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SaleBuffer record);

    int updateByPrimaryKey(SaleBuffer record);
    
    int deleteByEmail(SaleBuffer record);
    
    List<SaleBuffer> selectByEmail(String email);
}