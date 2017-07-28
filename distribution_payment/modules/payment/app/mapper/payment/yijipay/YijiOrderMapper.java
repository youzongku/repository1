package mapper.payment.yijipay;

import entity.payment.yijipay.YijiOrder;
import mapper.payment.BaseMapper;

public interface YijiOrderMapper extends BaseMapper<YijiOrder> {

    /**
     * 根据条件查询易极付下单记录
     * @return 单个下单记录
     */
    YijiOrder getYijiOrderByCondition(YijiOrder record);

}