package mapper.contract;

import java.util.List;

import entity.contract.ContractOprecord;

public interface ContractOprecordMapper {

    int insertSelective(ContractOprecord record);
    
    List<ContractOprecord> getRecord(String cno);
}