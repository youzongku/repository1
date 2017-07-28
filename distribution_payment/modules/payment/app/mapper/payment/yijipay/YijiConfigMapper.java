package mapper.payment.yijipay;

import entity.payment.yijipay.YijiConfig;
import mapper.payment.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface YijiConfigMapper extends BaseMapper<YijiConfig> {

    /**
     * 查询易极付支付配置信息
     */
    YijiConfig getYijiConfig(@Param("mark")String mark);
}