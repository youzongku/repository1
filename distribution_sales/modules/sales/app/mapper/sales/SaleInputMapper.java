package mapper.sales;

import java.util.List;

import entity.sales.SaleInput;

public interface SaleInputMapper extends BaseMapper<SaleInput> {
    int deleteByPrimaryKey(Integer id);

    int insert(SaleInput record);

    int insertSelective(SaleInput record);

    SaleInput selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SaleInput record);

    int updateByPrimaryKey(SaleInput record);
    
    List<SaleInput> selectByEmail(String email);

	int batchInsert(List<SaleInput> saleInputs);

	int deleteByEmail(String email);

	int deleteByIds(List<Integer> proIds);
}