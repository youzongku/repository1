package mapper.payment.wechat;

import entity.payment.wechat.WechatConfig;
import mapper.payment.BaseMapper;

import java.util.List;

public interface WechatConfigMapper extends BaseMapper<WechatConfig> {

    /**
     * 查询所有微信支付配置信息(实际只有一条记录)
     */
    List<WechatConfig> findAllWCs();

}