package mapper.contract;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dto.product.ContractCostDto;
import entity.contract.ContractCost;

public interface ContractCostMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ContractCost record);

    int insertSelective(ContractCost record);

    ContractCost selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ContractCost record);

    int updateByPrimaryKey(ContractCost record);
    
    List<ContractCostDto> pageSearch(@Param("param")Map<String,Object> param);
    
    Integer pageCount(@Param("param")Map<String,Object> param);
    
    ContractCost matchCost(@Param("param")Map<String,Object> param);
    
    List<ContractCost> selectByContractNo(@Param("cno")String cno);
    
    int updateNotStartCost();
    
    int updateEndedCost();
}