package mapper.payment.yijipay;

import entity.payment.yijipay.YijiResult;
import mapper.payment.BaseMapper;

public interface YijiResultMapper extends BaseMapper<YijiResult> {

    /**
     * 根据条件查询易极付支付结果
     * @return 单个支付结果
     */
    YijiResult getYijiResultByCondition(YijiResult record);

}