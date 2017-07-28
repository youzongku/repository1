package mapper.dismember;

import entity.dismember.DisWithdrawLimit;

public interface DisWithdrawLimitMapper extends BaseMapper<DisWithdrawLimit> {

    /**
     * 查询通用提现限制
     * @Author LSL on 2016-09-22 17:06:39
     */
    DisWithdrawLimit getCommonWithdrawLimit();

}