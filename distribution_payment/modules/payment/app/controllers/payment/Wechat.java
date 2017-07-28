package controllers.payment;

import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.inject.Inject;

import dto.payment.wechat.WechatPayParamsDTO;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.payment.IWechatService;
import utils.payment.BuildXmlUtils;
import utils.payment.WechatUtil;

/**
 * Created by LSL on 2016/3/14.
 */
public class Wechat extends Controller {

    @Inject
    private IWechatService wechatService;
    
    
    /**
     * 获取微信支付所需参数(用于页面生成微信支付二维码)
     * {
     *     "id": id,//订单ID
     *     "orderNo": orderNo,//订单号
     *     "orderDes": orderDes,//商品描述
     *     "totalPrice": totalPrice//订单总金额
     * }
     */
    @BodyParser.Of(BodyParser.Json.class)
    public Result gainWechatPayParam() {
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        JsonNode node = request().body().asJson();
        if (node == null || !node.has("id") || !node.has("orderNo") ||
                !node.has("orderDes") || !node.has("totalPrice")) {
            Logger.info("gainWechatPayParam：请求参数不存在或格式错误");
            result.put("suc", false);
            result.put("msg", "请求参数不存在或格式错误");
        } else {
            ((ObjectNode) node).put("tradeType", WechatUtil.TRADE_TYPE_NATIVE);
            ((ObjectNode) node).put("device_info", WechatUtil.DEVICE_INFO_WEB);
            Logger.info("gainWechatPayParam：params--->" + node.toString());
            WechatPayParamsDTO dto = wechatService.unifiedorder(node.toString());
            Logger.info("gainWechatPayParam：WechatPayParam--->" + Json.toJson(dto).toString());
            result.put("suc", true);
            result.set("info", Json.toJson(dto));
        }
        return ok(result);
    }

    /**
     * 获取微信支付结果
     * @return
     */
    public Result gainWechatPayResult() {
        Logger.info("gainWechatPayResult    params--->" + Json.toJson(request().queryString()).toString());
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        String orderNo = request().getQueryString("orderNo");
        String postflag = request().getQueryString("postflag");
        String sid = request().getQueryString("sid");
        String total = request().getQueryString("total");
        if (Strings.isNullOrEmpty(orderNo)) {
            Logger.info("gainWechatPayResult：请求参数不存在或格式错误");
            result.put("suc", false);
            result.put("msg", "请求参数不存在或格式错误");
        } else {
            String flag = wechatService.queryWechatPayResult(orderNo,postflag);
            Logger.info("支付校验返回值：" + flag);
            result.put("suc", flag);
            wechatService.dealReturnFlag(flag,orderNo,sid,total,postflag,result);
        }
        Logger.info("gainWechatPayResult：result--->" + result.toString());
        return ok(result);
    }

    /**
     * 被动接收微信支付异步通知信息
     */
    @BodyParser.Of(BodyParser.Xml.class)
    public Result receiveWechatPayAsyncNotifi() {
        Document body = request().body().asXml();
        Logger.info("微信异步Body:{}",BuildXmlUtils.transformXMLToString(body));
        StringBuffer xmlStr = new StringBuffer();
        if (body == null) {
            xmlStr.append("<xml>");
            xmlStr.append("<return_code><![CDATA[FAIL]]></return_code>");
            xmlStr.append("<return_msg><![CDATA[请求参数不存在]]></return_msg>");
            xmlStr.append("</xml>");
        } else {
            xmlStr.append(wechatService.disposeWechatPayAsyncNotifi(body));
        }
        return ok(xmlStr.toString());
    }

    /**
     * 申请微信支付退款
     */
    @BodyParser.Of(BodyParser.Json.class)
    public Result applyWechatPayRefund() {
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        JsonNode node = request().body().asJson();
        if (node == null || !node.has("orderNo")) {
            Logger.info("applyWechatPayRefund：请求参数不存在或格式错误");
            result.put("errorCode", "1");
            result.put("errorInfo", "请求参数不存在或格式错误");
        } else {
            result = (ObjectNode) Json.toJson(wechatService.wechatRefund(node.get("orderNo").asText()));
        }
        return ok(result);
    }

}
