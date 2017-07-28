package services.payment;

import java.util.Map;

import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.node.ObjectNode;

import dto.payment.wechat.WechatPayParamsDTO;
import entity.payment.wechat.WechatConfig;

/**
 * Created by LSL on 2016/3/14.
 */
public interface IWechatService {

    /**
     * 调用微信支付统一下单支付接口
     * {
     *     "id": id,//订单ID
     *     "orderNo": orderNo,//订单号
     *     "orderDes": orderDes,//商品描述
     *     "totalPrice": totalPrice,//订单总金额
     *     "tradeType": WechatUtil.TRADE_TYPE_NATIVE),//JSAPI、NATIVE、APP，支付方式
     *     "device_info": WechatUtil.DEVICE_INFO_WEB)//终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
     * }
     */
    WechatPayParamsDTO unifiedorder(String paramStr);

    /**
     * 根据订单号调用微信支付结果查询接口查询支付结果
     */
    String qryWechatPayResult(String orderNo);

    /**
     * 根据订单号查询支付结果(先查结果记录是否支付成功，不存在或不成功则调微信的查询接口)
     */
    String queryWechatPayResult(String orderNo,String postFlag);

    /**
     * 根据订单号调用微信支付申请退款接口申请退款
     */
    Map<String, String> wechatRefund(String orderNo);

    /**
     * 获取微信支付配置信息
     */
    WechatConfig getWechatConfig();

    /**
     * 处理微信支付异步通知信息
     */
    String disposeWechatPayAsyncNotifi(Document body);

	void dealReturnFlag(String flag, String orderNo, String sid, String total, String postflag, ObjectNode result);

}
