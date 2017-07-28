package mapper.payment.wechat;

import entity.payment.wechat.WechatOrder;
import mapper.payment.BaseMapper;

import java.util.List;
import java.util.Map;

public interface WechatOrderMapper extends BaseMapper<WechatOrder> {

    /**
     * 单条件或多条件查询微信下单记录
     */
    List<WechatOrder> findWOsByMap(Map<String, Object> map);

}