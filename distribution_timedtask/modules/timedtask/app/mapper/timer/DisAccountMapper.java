package mapper.timer;

import org.apache.ibatis.annotations.Param;

import entity.timer.DisAccount;


public interface DisAccountMapper extends BaseMapper<DisAccount> {

    /**
     * 根据用户邮箱获取资金账户
     * @param email
     * @return
     */
    DisAccount getDisAccountByEmail(@Param("email")String email);

}