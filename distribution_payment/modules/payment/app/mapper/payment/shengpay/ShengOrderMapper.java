package mapper.payment.shengpay;

import entity.payment.shengpay.ShengOrder;
import mapper.payment.BaseMapper;

public interface ShengOrderMapper extends BaseMapper<ShengOrder> {

    /**
     * 根据条件查询盛付通支付/转账(代扣/代发)申请记录
     */
    ShengOrder getShengOrderByCondition(ShengOrder so);

}