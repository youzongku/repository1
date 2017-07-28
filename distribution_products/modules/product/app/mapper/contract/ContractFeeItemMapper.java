package mapper.contract;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dto.contract.fee.ContractFeeItemPageQeuryParam;
import entity.contract.ContractFeeItem;

public interface ContractFeeItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ContractFeeItem record);
    
    int insertSelective(ContractFeeItem record);
    
    int selectCount(@Param("contractNo")String contractNo, @Param("feeTypeId")Integer feeTypeId);
    
    List<ContractFeeItem> select(@Param("contractNo")String contractNo, @Param("feeTypeId")Integer feeTypeId);

    ContractFeeItem selectByPrimaryKey(Integer id);

    List<ContractFeeItem> selectByConditions(Map<String, Object> map);
    
    int updateByPrimaryKeySelective(ContractFeeItem record);

	List<ContractFeeItem> selectByPage(ContractFeeItemPageQeuryParam param);
	int selectCountByPage(ContractFeeItemPageQeuryParam param);
}