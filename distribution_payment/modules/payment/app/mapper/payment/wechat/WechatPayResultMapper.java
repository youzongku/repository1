package mapper.payment.wechat;

import entity.payment.wechat.WechatPayResult;
import mapper.payment.BaseMapper;

import java.util.List;
import java.util.Map;

public interface WechatPayResultMapper extends BaseMapper<WechatPayResult> {

    /**
     * 单条件或多条件查询微信支付结果
     */
    List<WechatPayResult> findWPRsByMap(Map<String, Object> map);

}