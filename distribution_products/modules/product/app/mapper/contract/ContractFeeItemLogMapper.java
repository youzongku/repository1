package mapper.contract;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.contract.ContractFeeItemLog;

public interface ContractFeeItemLogMapper {
	
	List<ContractFeeItemLog> selectByFeeItemId(@Param("feeItemId") Integer feeItemId);
	
    int insert(ContractFeeItemLog record);
    
    int insertBatch(List<ContractFeeItemLog> logs);
}