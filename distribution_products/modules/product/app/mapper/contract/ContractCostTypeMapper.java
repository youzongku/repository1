package mapper.contract;

import java.util.List;

import entity.contract.ContractCostType;

public interface ContractCostTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ContractCostType record);

    int insertSelective(ContractCostType record);

    ContractCostType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ContractCostType record);

    int updateByPrimaryKey(ContractCostType record);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月27日 上午10:11:04
	 */
	List<ContractCostType> select();
}