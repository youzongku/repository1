package mapper.contract;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import entity.contract.ContractQuotations;

public interface ContractQuotationsMapper {
	/**
	 * 批量设置合同报价商品的商品分类
	 * @param list
	 * @return
	 */
	int batchUpdateCategoryId(List<ContractQuotations> list);
	
    int deleteByPrimaryKey(Integer id);

    int insertSelective(ContractQuotations record);

    ContractQuotations selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ContractQuotations record);

	List<ContractQuotations> select(Map<String, Object> param);
	
	List<ContractQuotations> productSearch(Map<String, Object> param);

	Integer selectCount(Map<String, Object> param);

	Integer updateNotStartQuoted();

	Integer updateEndedQuoted();
	
	List<ContractQuotations> selectByContractNo(@Param("cno")String cno, @Param("status")Integer status);

}