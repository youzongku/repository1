package mapper.timer;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.timer.AccountPeriodSlave;

public interface AccountPeriodSlaveMapper {

    int batchUpdate(List<AccountPeriodSlave> list);

    List<AccountPeriodSlave> getNeedHandleApByState(@Param("state")Integer state);
    
    int updateByPrimaryKeySelective(AccountPeriodSlave record);
    
    List<AccountPeriodSlave> getAccountPeriodsByMasterId(Integer masterId);
    
    AccountPeriodSlave selectByPrimaryKey(Integer id);
    
    int insertSelective(AccountPeriodSlave record);

}