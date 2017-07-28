package mapper.payment.shengpay;

import entity.payment.shengpay.ShengResult;
import mapper.payment.BaseMapper;

public interface ShengResultMapper extends BaseMapper<ShengResult> {

    /**
     * 根据条件查询盛付通支付/转账(代扣/代发)结果记录
     */
    ShengResult getShengResultByCondition(ShengResult sr);

}