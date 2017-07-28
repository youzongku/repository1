package services.payment;

import com.fasterxml.jackson.databind.JsonNode;

import entity.payment.shengpay.ShengConfig;

/**
 * Created by LSL on 2016/4/30.
 */
public interface IShengPayService {

    /**
     * 获取盛付通支付配置信息
     */
    ShengConfig getShengConfig();

    /**
     * 盛付通支付申请(代扣业务)
     */
    JsonNode applyPay(JsonNode param);

    /**
     * 盛付通转账申请(代发业务)
     */
    JsonNode applyTransfer(JsonNode param);

    /**
     * 盛付通实名认证
     */
    boolean authentication(JsonNode param);

    /**
     * 调用盛付通账户余额查询接口查询余额
     * @param merchantNo 商户号
     * @return 账户余额信息
     */
    JsonNode remoteQueryBalance(String merchantNo);

    /**
     * 根据指定商户号获取账户余额信息，先查询本地数据库，若无记录再调盛付通接口查询
     * @return 账户余额信息
     */
    JsonNode queryBalanceRecord(String merchantNo);

}
