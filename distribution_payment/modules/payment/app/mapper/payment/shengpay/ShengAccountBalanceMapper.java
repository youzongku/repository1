package mapper.payment.shengpay;

import entity.payment.shengpay.ShengAccountBalance;
import mapper.payment.BaseMapper;

public interface ShengAccountBalanceMapper extends BaseMapper<ShengAccountBalance> {

    /**
     * 根据条件查询盛付通账户余额记录
     */
    ShengAccountBalance getBalanceByCondition(ShengAccountBalance record);

}