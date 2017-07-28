package services.payment.impl;

import java.util.Map;

import com.google.common.collect.Maps;

import utils.payment.YjfUtil;

/**
 * Created by luwj on 2016/7/5.
 */
public class TestService {

    public static void main(String[] args) {
//        TestService ws = new TestService();
        String url = "https://openapi.yijifu.net/gateway.html";//下单请求地址
        String protocol = "httpPost"; //协议类型 非
        String service = "commonWchatTradeRedirect"; //服务代码 必
        String version = "1.0"; //服务版本 非
        String partnerId = "20140411020055684571"; //商户ID 必
        String orderNo = "yjf_wx_20160803102120345678"; //请求订单号 必
//        String merchOrderNo = "";  //外部订单号 非
        String signType = "MD5"; //签名方式 必
//        String returnUrl = "http://baidu.com"; //页面跳转返回URL 非
//        String notifyUrl = ""; //异步通知URL 非
//        String tradeName = ""; //交易名称 非
        String outOrderNo = "WS20160803102120345678"; //外部订单号 必
//        String sellerUserId = ""; //卖家Id 必
//        String sellerCardNo = ""; //卖家卡号 非
//        String tradeMemo = ""; //交易备注 非
        String tradeAmount = "0.01"; //交易额 必
//        String currency = "CNY"; //币种 非
//        String payFrom = "NORMAL"; //付款类型 非
        String uiStyle = "PC_NORMAL"; //页面风格 PC_NORMAL:PC标准版 ZBJ:猪八戒版 MOBILE_WEB:手机web版
//        String mutableType = ""; //特殊渠道标示 非
//        String chargeExtend = ""; //收费扩展字段 非
        //商品条款 必
        String goodsClauses = "[{" +
                "\"outId\":\"\""+ //商品的外部ID 非
                "\"name\":\"笔记本电脑\""+ //商品名称 必
                "\"memo\":\"\""+ //商品详情 否
                "\"price\":\"0.01\""+ //商品单价 非
                "\"quantity\":\"1\""+ //商品数量 非
                "}]";
//        String profitClauses = "[{}]"; //分润项 非

        Map<String, String> params = Maps.newHashMap();
        params.put("protocol", protocol);
        params.put("service", service);
        params.put("version", version);
        params.put("partnerId", partnerId);
        params.put("orderNo", orderNo);
        params.put("signType", signType);
        params.put("outOrderNo", outOrderNo);
        params.put("sellerUserId", partnerId);
        params.put("tradeAmount", tradeAmount);
        params.put("uiStyle", uiStyle);
        params.put("goodsClauses", goodsClauses);

        YjfUtil util = new YjfUtil(url, "c9cef22553af973d4b04a012f9cb8ea8");
        String prepareSign = util.signString(params);//生成待签名字符串
        String sign = YjfUtil.MD5(prepareSign);
        System.out.print(">>>>>>>>>" +sign);
    }

//    private String buildWechatOrder(SortedMap<Object,Object> maps){
//        WOrderXml wechat = new WOrderXml();
//        wechat.setAppid((String)maps.get("appid"));
//        wechat.setAttach((String)maps.get("attach"));
//        wechat.setBody((String)maps.get("body"));
//        wechat.setDeviceInfo((String)maps.get("device_info"));
//        wechat.setMchId((String)maps.get("mch_id"));
//        wechat.setNonceStr((String)maps.get("nonce_str"));
//        wechat.setNotifyUrl((String)maps.get("notify_url"));
//        wechat.setOutTradeNo((String)maps.get("out_trade_no"));
//        wechat.setSpbillCreateIp((String)maps.get("spbill_create_ip"));
//        wechat.setTotalFee((String)maps.get("total_fee"));
//        wechat.setTradeType((String)maps.get("trade_type"));
//        wechat.setSign((String)maps.get("sign"));
//        wechat.setProductId((String)maps.get("product_id"));
//        BuildXmlUtils<WOrderXml> xmlUtils = new BuildXmlUtils<WOrderXml>(){};
//        String xml = xmlUtils.bean2xml(wechat);
//        Logger.debug(">>微信支付请求报文>>xml>>>>>"+xml);
//        return xml;
//    }


//    private String createSign(String characterEncoding,SortedMap<Object,Object> parameters , String key){
//        StringBuffer sb = new StringBuffer();
//        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
//        Iterator it = es.iterator();
//        while(it.hasNext()) {  //遍历
//            Map.Entry entry = (Map.Entry)it.next();
//            String k = (String)entry.getKey();
//            Object v = entry.getValue();
//            if(null != v && !"".equals(v)
//                    && !"sign".equals(k) && !"key".equals(k)) {
//                sb.append(k + "=" + v + "&");
//            }
//        }
//        if(StringUtils.isNotBlank(key)){
//            sb.append("key=" + key);
//        }
//        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();  //MD5加密
//        return sign;
//    }

//    private  String payUnifiedorder( String url , String request_xml){
//        String return_xml ="";
//        HttpClient httpClient = new HttpClient();
//        PostMethod post = new PostMethod(url);
//        post.setRequestBody( request_xml );
//        post.getParams().setContentCharset("utf-8");
//        // 发送http请求
//        try {
//            httpClient.executeMethod(post);
//            //打印返回的信息
//            return_xml = post.getResponseBodyAsString();
//            Logger.info(">>>>payUnifiedorder>>return_xml>>"+return_xml);
//        } catch (HttpException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally{
//            //释放连接
//            post.releaseConnection();
//        }
//        return return_xml ;
//    }
}
