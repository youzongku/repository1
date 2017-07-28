package controllers.payment;

import java.util.Map;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import entity.payment.yijipay.YijiResult;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.payment.IYijipayService;
import utils.payment.HttpUtil;

/**
 * Created by LSL on 2016/4/14.
 */
public class Yijipay extends Controller {

    @Inject
    private IYijipayService yijipayService;
    

    /**
     * 获取易极付支付请求参数
     * 输入:
     * {
     *     "id": "",//订单ID
     *     "outOrderNo": "",//订单号
     *     "tradeAmount": "",//交易金额
     *     "orderDetail": [
     *         {
     *             "name": ""//商品名称
     *         },......,
     *         {
     *             "name": ""//商品名称
     *         }
     *     ]//订单详情，一个或多个商品信息
     * }
     * 输出:
     * {
     *     "service": "",//服务代码，接口服务代码，commonTradePay。
     *     "version": "",//服务版本，2.0
     *     "partnerId": "",//商户ID
     *     "signType": "",//签名方式
     *     "notifyUrl": "",//异步通知URL
     *     "returnUrl": "",//同步返回URL
     *     "sellerUserId": "",//卖家ID
     *     "goodsClauses": "",//商品条款信息
     *     "tradeAmount": "",//交易金额
     *     "outOrderNo": "",//订单号
     *     "orderNo": "",//支付请求号
     *     "sign": "",//签名
     *     "actionUrl": ""//易极付支付网关URL
     * }
     */
    public Result gainYijiPayParam() {
        ObjectNode result = Json.newObject();
        JsonNode params = request().body().asJson();
        Logger.info(">>>>>gainYijiPayParam>>params>>"+ Json.stringify(params));
        if (params == null || params.size() < 4) {
            result.put("suc", false);
            result.put("msg", "请求参数不存在或格式错误");
        } else {
            try {
                Logger.debug("gainYijiPayParam    params--->" + params.toString());
                String outOrderNo = params.get("outOrderNo").asText();
                //add by xuse 易极付支付采购单
                String flag = params.has("postflag")?params.get("postflag").asText():"";
                
    			if(outOrderNo.toUpperCase().startsWith("CG")||outOrderNo.toUpperCase().startsWith("CZ")||"true".equals(flag)) {
            		Logger.info("易极付支付订单：" + outOrderNo);
            		result = yijipayService.purchaseParam(params);   
            	} else {
            		result = yijipayService.getYijiPayParam(params);            		
            	}
            } catch (Exception e) {
                e.printStackTrace();
                Logger.debug("gainYijiPayParam    Exception--->" + e.toString());
                result.put("suc", false);
                result.put("msg", "处理易极付支付请求参数发生异常");
            }
        }
        return ok(result);
    }

    /**
     * 接收易极付同步返回结果信息
     * {
     *     "success": "",//成功标识，表示接口调用是否成功或通知是否成功。true：成功，false：失败
     *     "protocol": "",//协议，报文协议格式，httpGet、httpPost、httpJson。
     *     "service": "",//服务代码，接口服务代码，commonTradePay。
     *     "version": "",//服务版本，2.0
     *     "orderNo": "",//success=true表示易极付支付请求号，success=false表示商户订单号
     *     "signType": "",//签名方式，MD5，必须大写。
     *     "sign": "",//签名，
     *     "resultCode": "",//返回码，EXECUTE_SUCCESS：处理成功，EXECUTE_PROCESSING：交易处理中，
     *                        INTERNAL_ERROR：系统内部错误，SERVICE_NOT_FOUND_ERROR：服务不存在，
     *                        PARAMETER_ERROR：参数错误，PARAM_FORMAT_ERROR：参数格式错误，
     *                        UNAUTHENTICATED：认证(签名)错误，UNAUTHORIZED：未授权的服务，
     *                        ORDER_NO_NOT_UNIQUE：商户订单号不唯一，FIELD_NOT_UNIQUE：对象字段重复，
     *                        REDIRECT_URL_NOT_EXIST：重定向服务需设置redirectUrl，
     *                        PARTNER_NOT_REGISTER：合作伙伴没有注册，PARTNER_NOT_PRODUCT：商户没有配置产品。
     *     "resultMessage": "",//返回信息
     *     "tradeNo": "",//交易号
     *     "tradeStatus": "",//交易状态，wait_buyer_pay：等待买家付款，trade_finished：交易完成
     *     "notifyTime": "",//通知时间
     *     "notifyUrl": "",//异步通知URL
     *     "returnUrl": "",//同步返回URL
     *     "partnerId": "",//商户ID
     *     "inlet": ""//当success=false时，该参数存在。
     *
     *     --合并支付返回：
     *     "mergePayResult"：EXECUTE_SUCCESS
     *     "tradeDetail"=[{"tradeNo":"00z22kVSY0Fugk1Zry71","tradeStatus":"trade_finished","outOrderNo":"XS2016041915003400000098"}]
     * }
     */
    public Result receiveSyncNotify() {
        Logger.debug(">>>>>>>>>>>>>>>>>start>>>>>>>>>>>>>>>>>");
        ObjectNode result = Json.newObject();
        Map<String, String[]> params = request().queryString();
        if (params == null || params.size() == 0) {
            result.put("suc", false);
            result.put("msg", "请求参数不存在或格式错误");
        } else {
            try {
                Logger.debug("==============易极付同步返回==============");
                Logger.debug("receiveSyncNotify    params--->" + Json.toJson(params).toString());
                yijipayService.receiveSyncReturn(params, result);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.debug("receiveSyncNotify    处理同步返回参数发生异常", e);
                result.put("suc", false);
            }
        }
        Logger.debug("receiveSyncNotify    result--->" + result.toString());
        String url = HttpUtil.BBC_HOST + "/backstage/pay_success.html?isok=" + result.get("isok");
        Logger.debug("receiveSyncNotify    url--->" + url);
        return redirect(url);
    }

    /**
     * 接收易极付异步通知结果信息
     * {
     *     "success": "",//成功标识，表示接口调用是否成功，true：成功，false：失败
     *     "protocol": "",//协议，报文协议格式，httpGet、httpPost、httpJson。
     *     "service": "",//服务代码，接口服务代码，commonTradePay。
     *     "version": "",//服务版本，2.0
     *     "notifyTime": "",//通知时间，通知的发送时间，格式为yyyy-MM-dd HH:mm:ss
     *     "signType": "",//签名方式，MD5，必须大写。
     *     "sign": "",//签名，
     *     "resultCode": "",//返回码，EXECUTE_SUCCESS：处理成功，EXECUTE_PROCESSING：交易处理中，
     *                        INTERNAL_ERROR：系统内部错误，SERVICE_NOT_FOUND_ERROR：服务不存在，
     *                        PARAMETER_ERROR：参数错误，PARAM_FORMAT_ERROR：参数格式错误，
     *                        UNAUTHENTICATED：认证(签名)错误，UNAUTHORIZED：未授权的服务，
     *                        ORDER_NO_NOT_UNIQUE：商户订单号不唯一，FIELD_NOT_UNIQUE：对象字段重复，
     *                        REDIRECT_URL_NOT_EXIST：重定向服务需设置redirectUrl，
     *                        PARTNER_NOT_REGISTER：合作伙伴没有注册，PARTNER_NOT_PRODUCT：商户没有配置产品。
     *     "resultMessage": "",//返回信息
     *     "tradeNo": "",//交易号
     *     "tradeType": "",//交易类型，ESCROWTRADE:担保交易，FASTPAYTRADE:即时到账交易，POOL:集资借款，
     *                       POOL_REVERSE:集资还款，POOL_TOGETHER:给力式集资，TRANSFERTOBANK:转账到卡，
     *                       BANKCARD_TO_BANKCARD:卡到卡，DEDUCTDEPOISITAL:代扣充值，TRANSFER:站内转帐，
     *                       BEHALF_DEPOISIT:代充，OTHER:其他。
     *     "accountDay": "",//账期，YYYYHHMM
     *     "orderNo": "",//易极付支付请求号
     *     "outOrderNo": "",//商户订单号
     *     "partnerId": ""//商户ID
     * }
     * 只有支付成功才会有异步通知
     */
    public Result receiveAsynNotify() {
        ObjectNode result = Json.newObject();
        Map<String, String> params = Form.form().bindFromRequest().data();
        if (params == null || params.size() == 0) {
            result.put("suc", false);
            result.put("msg", "请求参数不存在或格式错误");
        } else {
            try {
                Logger.debug("==============易极付异步通知==============");
                Logger.debug("receiveAsynNotify    params--->" + params.toString());
                yijipayService.receiveAsynNotify(params, result);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.debug("receiveAsynNotify    处理异步通知参数发生异常", e);
                result.put("suc", false);
            }
        }
        Logger.debug("receiveAsynNotify    result--->" + result.toString());
        return ok(result.get("suc").asBoolean() ? "success" : "fail");
    }

    /**
     * 获取易极付支付结果信息
     * {"orderNo": ""}//订单号
     */
    public Result gainYijiPayResult() {
        ObjectNode result = Json.newObject();
        Map<String, String> params = Form.form().bindFromRequest().data();
        if (params == null || !params.containsKey("orderNo") ||
                Strings.isNullOrEmpty(params.get("orderNo"))) {
            result.put("suc", false);
            result.put("msg", "请求参数不存在或格式错误");
        } else {
            Logger.debug("gainYijiPayResult    params--->" + params.toString());
            YijiResult yr = new YijiResult();
            yr.setOutOrderNo(params.get("orderNo"));
            yr = yijipayService.getYijiResultByCondition(yr);
            if (yr != null) {
                if ("EXECUTE_SUCCESS".equals(yr.getResultCode()) &&
                        (Strings.isNullOrEmpty(yr.getTradeStatus()) || "FINISHED".equals(yr.getTradeStatus()))) {
                    //结果记录显示支付成功
                    result.put("suc", true);
                } else {
                    result.put("suc", false);
                    result.put("msg", "未完成支付");
                }
            } else {
                result.put("suc", false);
                result.put("msg", "暂无支付结果信息");
            }
        }
        return ok(result);
    }


    /**
     * 合并付款
     * @return
     */
    public Result unionPay(){
        JsonNode reqBody = request().body().asJson();
        Logger.debug(">>>reqBody>>>>"+Json.toJson(reqBody).toString());
        return ok(yijipayService.unionCashierWebPay(reqBody));
    }

    /**
     * 实名查询
     * @return
     */
    public Result realNameQuery(){
        JsonNode reqBody = request().body().asJson();
        Logger.debug(">realNameQuery>>reqBody>>"+Json.toJson(reqBody).toString());
        return ok(yijipayService.realNameQuery(reqBody));
    }

    /**
     * 易极付-微信扫码支付
     * @return
     */
    public Result yjfWxTrade(){
        JsonNode reqBody = request().body().asJson();
        Logger.debug(">yjfWxTrade>>reqBody>>"+Json.toJson(reqBody).toString());
        if (reqBody.has("service"))
            return ok(yijipayService.purchaseParam(reqBody));
        else
            return ok(yijipayService.yjfWxTrade(reqBody));
    }

    /**
     * 测试
     * @return
     */
    public Result test(){
        Map<String,Object> map = Maps.newHashMap();
        map.put("realName","有道");
        map.put("certNo","511702197409284963");
        yijipayService.realNameQuery(Json.toJson(map));
        return ok("");
    }
}
