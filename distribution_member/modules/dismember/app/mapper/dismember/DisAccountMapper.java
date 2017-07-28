package mapper.dismember;

import entity.dismember.DisAccount;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dto.dismember.AccountDto;

/**
 * 
 * @author luwj
 *
 */
public interface DisAccountMapper extends BaseMapper<DisAccount> {

    /**
     * 根据用户邮箱获取资金账户
     * @param email
     * @return
     */
    DisAccount getDisAccountByEmail(@Param("email")String email);
    
    /**
     * 更新支付密码的输入错误时间
     * @param disAccount
     * @return
     * @author huchuyin
     * @date 2016年10月7日 下午7:51:13
     */
    int updateInputErrorTimes(DisAccount disAccount);
    
    /**
     * 根据传入参数查询账户
     * @param map
     * @author lzl
     * @since 2016年12月7日下午2:20:00
     */
    List<AccountDto> getDisAccountByCondition(Map<String, Object> map);
    
    int getCountByCondition(Map<String, Object> map);

	List<AccountDto> getAccounts(Map<String, Object> userMap);

	Integer getAccountsCount(Map<String, Object> userMap);

	int updatePeriodFrozen(DisAccount account);

}