package mapper.contract;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import entity.contract.Contract;

public interface ContractMapper {

	int insertSelective(Contract record);

	Contract selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Contract record);

	List<Contract> select(@Param("param") Map<String, Object> param);

	Integer selectCount(@Param("param") Map<String, Object> param);
	
	Contract selectByCno(@Param("cno") String cno);
}