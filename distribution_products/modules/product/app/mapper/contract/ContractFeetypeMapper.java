package mapper.contract;

import entity.contract.ContractFeetype;

import java.util.List;
import java.util.Map;

public interface ContractFeetypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ContractFeetype record);

    int insertSelective(ContractFeetype record);

    ContractFeetype selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ContractFeetype record);

    int updateByPrimaryKey(ContractFeetype record);

    List<ContractFeetype> getContractFeetypesByPage(Map<String, Object> params);

    int getCountByPage(Map<String, Object> params);

    List<ContractFeetype> getAllContractFeetype();

    int countByParam(Map<String, Object> param);
}