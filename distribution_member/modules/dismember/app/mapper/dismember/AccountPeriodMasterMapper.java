package mapper.dismember;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.dismember.Search;
import entity.dismember.AccountPeriodMaster;

public interface AccountPeriodMasterMapper {
	@Deprecated
	List<AccountPeriodMaster> selectByAccountList(@Param("accountList")List<String> accountList);
	
	List<AccountPeriodMaster> selectByIds(@Param("idList")List<Integer> idList);
	
    int deleteByPrimaryKey(Integer id);

    int insert(AccountPeriodMaster record);

    int insertSelective(AccountPeriodMaster record);

    AccountPeriodMaster selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AccountPeriodMaster record);

    int updateByPrimaryKey(AccountPeriodMaster record);
    
    List<AccountPeriodMaster> pageSearch(Search search);
    
    Integer pageCount(Search search);
    
    AccountPeriodMaster getValidAp(String account);
    
    List<AccountPeriodMaster> getUsedAps(String account);
}