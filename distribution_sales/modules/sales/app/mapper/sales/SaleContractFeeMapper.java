package mapper.sales;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.sales.ContractFeeSearch;
import dto.sales.FeeColumnDto;
import entity.sales.SaleContractFee;
import entity.sales.SaleMain;

public interface SaleContractFeeMapper {
	
    int deleteByPrimaryKey(String uid);

    int insert(SaleContractFee record);

    int insertSelective(SaleContractFee record);

    SaleContractFee selectByPrimaryKey(String uid);

    int updateByPrimaryKeySelective(SaleContractFee record);

    int updateByPrimaryKey(SaleContractFee record);

	List<FeeColumnDto> getFields(String cno);
	
	List<SaleContractFee> selectByParams(@Param("sno")String sno,@Param("cno")String cno);	
	
	List<SaleMain> pageSearch(ContractFeeSearch search);
	
	Integer pageCount(ContractFeeSearch search);
}