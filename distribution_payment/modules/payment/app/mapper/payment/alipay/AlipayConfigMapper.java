package mapper.payment.alipay;

import entity.payment.alipay.AlipayConfig;
import mapper.payment.BaseMapper;

/**
 * 支付宝支付配置Mapper
 * @author luwj
 *
 */
public interface AlipayConfigMapper extends BaseMapper<AlipayConfig> {

    /**
     * 获取支付宝配置信息实体
     * @param types 类型,1:国际支付宝,2:国内支付宝
     */
    public AlipayConfig getAlipayConfig(Integer types);
}