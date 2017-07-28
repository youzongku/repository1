package mapper.contract;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import entity.contract.ContractFeeItemRelatedSku;

public interface ContractFeeItemRelatedSkuMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByFeeItemId(@Param("feeItemId")Integer feeItemId);

    int insert(ContractFeeItemRelatedSku record);

    int insertBatch(List<ContractFeeItemRelatedSku> records);

    int insertSelective(ContractFeeItemRelatedSku record);

    ContractFeeItemRelatedSku selectByPrimaryKey(Integer id);

	List<ContractFeeItemRelatedSku> selectByConditions(Map<String, Object> map);
}