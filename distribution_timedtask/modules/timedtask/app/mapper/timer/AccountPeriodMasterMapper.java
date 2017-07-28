package mapper.timer;

import java.util.List;

import entity.timer.AccountPeriodMaster;
import entity.timer.Search;

public interface AccountPeriodMasterMapper {
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