package services.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entity.payment.yijipay.YijiConfig;
import entity.payment.yijipay.YijiOrder;
import entity.payment.yijipay.YijiResult;

import java.util.Map;

/**
 * Created by LSL on 2016/4/14.
 */
public interface IYijipayService {

    /**
     * 获取易极付支付配置信息
     */
    YijiConfig getYijiConfig(String mark);

    /**
     * 新增易极付下单记录
     */
    int insertYijiOrderSelective(YijiOrder record);

    /**
     * 根据条件获取易极付下单记录
     * @return 单个下单记录
     */
    YijiOrder getYijiOrderByCondition(YijiOrder record);

    /**
     * 新增易极付支付结果
     */
    int insertYijiResultSelective(YijiResult record);

    /**
     * 更新易极付支付结果
     */
    int updateYijiResultSelective(YijiResult record);

    /**
     * 根据条件获取易极付支付结果
     * @return 单个支付结果
     */
    YijiResult getYijiResultByCondition(YijiResult record);

    /**
     * 合并支付
     * @return
     */
    public String unionCashierWebPay(JsonNode node);

    /**
     * 获取易极付支付请求参数
     */
    ObjectNode getYijiPayParam(JsonNode params);

    /**
     * 接收易极付支付同步返回信息
     */
    void receiveSyncReturn(Map<String, String[]> params, ObjectNode result);

    /**
     * 接收易极付支付异步通知信息
     */
    void receiveAsynNotify(Map<String, String> params, ObjectNode result);


    public String getSalesOrderer(String outOrderNo);

    /**
     * 实名认证接口
     * @param node
     * @return
     */
    public String realNameQuery(JsonNode node);

	ObjectNode purchaseParam(JsonNode params);

    /**
     * 易极付-微信扫码支付
     * @return
     */
    public String yjfWxTrade(JsonNode node);
}
