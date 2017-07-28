package mapper.dismember;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.dismember.Search;
import entity.dismember.AccountPeriodSlave;

public interface AccountPeriodSlaveMapper {
	/**
	 * 当前时间大于等于账期开始时间且小于等于合同账期的
	 * @return
	 */
	List<AccountPeriodSlave> selectValid();
	
    int deleteByPrimaryKey(Integer id);

    int insert(AccountPeriodSlave record);

    int insertSelective(AccountPeriodSlave record);

    AccountPeriodSlave selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AccountPeriodSlave record);

    int updateByPrimaryKey(AccountPeriodSlave record);
    
    List<AccountPeriodSlave> getAccountPeriods(String email);
    
    List<AccountPeriodSlave> getAccountPeriodsByMasterId(Integer masterId);
    
    List<AccountPeriodSlave> pageSearch(Search search);
    
    Integer pageCount(Search search);
    
    int batchUpdate(List<AccountPeriodSlave> list);

    List<AccountPeriodSlave> getNeedHandleApByState(@Param("state")Integer state,@Param("now")Date now);

	/**
	 * @author zbc
	 * @since 2017年3月1日 下午5:54:16
	 */
    List<String> getStartDate(Integer id);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月7日 下午12:03:23
	 */
    AccountPeriodSlave getCurAccountPeriod(String email);

}