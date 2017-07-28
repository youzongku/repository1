package mapper.payment.wechat;

import entity.payment.wechat.WechatRefundResult;
import mapper.payment.BaseMapper;

import java.util.List;
import java.util.Map;

public interface WechatRefundResultMapper extends BaseMapper<WechatRefundResult> {

    /**
     * 单条件或多条件查询微信退款结果
     */
    List<WechatRefundResult> findWRRsByMap(Map<String, Object> map);

}