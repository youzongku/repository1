package utils.payment; /**
 * www.yiji.com Inc.
 * Copyright (c) 2012 All Rights Reserved.
 */

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SingTest {

    public static String orderNo = "";
    public static String BatchNo = "yjf20150923111634165";//跨境汇款批次号，，全局唯一
    private static final String gatewayUrl = "https://openapi.yijifu.net/gateway.html";

    //跨境YHPAY、支付单、实名认证
//	 private static final String partnerId = "20140926020000058373";
//	 private static final String securityCheckKey ="2af0376a5dc1695aa1ab889384a8ade9";

    //国内支付
    private static final String partnerId = "20140411020055684571";
    private static final String securityCheckKey = "c9cef22553af973d4b04a012f9cb8ea8";

//	 private static final String partnerId = "20160216020000703053";
//	 private static final String securityCheckKey = "04d57ae4cf5fd19c3f1639bce5515471";


    private static final String signType = "MD5";

    private static final String returnUrl = "http://www.baidu.com";
    //	private static final String returnUrl = "http://127.0.0.1:20101/gateway/notify";
    private static final String notifyUrl = "http://openapi.yiji.com/notify.jsp";

    public static void main(String[] args) {

        // 获取到Yjf工具类
        YjfUtil yjf = new YjfUtil(gatewayUrl, securityCheckKey);
        // 声明一个MAP
        Map<String, String> data = new HashMap<String, String>();

        //加上公共请求头部
        data.put("partnerId", partnerId);
		data.put("signType", signType);
		data.put("returnUrl", returnUrl);
        data.put("notifyUrl", notifyUrl);
        // 加上公共请求头结束

        // 用时分秒生成一个20-40位的orderNo，推荐8位日期+8-12位业务序号
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssssSSSS");
        orderNo = df.format(new Date());
        data.put("orderNo", orderNo);

//        data = commonTradePay(data);//跳转收银台
//        data = CommonWchatTradeRedirect(data);//网关支付

        data = unionCashierWebPay(data);//<========================调用服务API相对应的方法

        String signString = yjf.signString(data);// <========生成待签名字符串

        String sign = YjfUtil.MD5(signString);// <=======================签名
        data.put("sign", sign);
        yjf.forLink(data);// <=======生成链接
        System.out.println("签名结果:>>>>>>>>>>>>>>sign=" + sign);
        System.out.print("remittranceBatchNo===" + BatchNo + "\n");
        System.out.println("orderNo=====>" + orderNo);

       yjf.form(data);//========================打印form表单
       yjf.postString(data); //<=================================提交from表单
//       yjf.httpclient(gatewayUrl, data);
        /**
         * 直接代签名字符串签名
         */
//        String str1 = "";
//        System.out.println("直接签名结果========" + yjf.MD5(str1));
    }
    
    /**
     * 请求报文处理方法
     *
     * @return
     */
    private static String stringBiz() {
        String strUrl = "returnUrl=http://mpay.yiji.com:8004/help/notifyUrl.html, verifyCode=063869, payPassword=, tradeNo=20150109000024743589, signType=MD5, partnerId=20141226020000099880, bankAccountNo=6222023100043100960, imageVersion=1.1, cardType=DEBIT_CARD, bankCode=ICBC, sign=c4738492afd6bd8bace7260fb00ed793, orderNo=20150109111821420AND, validDate=, certNo=500106199203248325, service=mobilePay, realName=王凤娇, smi=8986003131AA81919542|460021119169542, notifyUrl=http://mpay.yiji.com:8004/help/notifyUrl.html, cvv2=, mobileNo=13271966474, deviceId=864502023201163, isBind=0";
        String[] strArray = strUrl.split(",");
        Map<String, String> map = new HashMap<String, String>();
        for (String s : strArray) {
            String[] subStr = s.split("=");
            map.put(subStr[0], subStr[1]);
        }

        StringBuilder sb = new StringBuilder("?");

        for (Entry<String, String> entry : map.entrySet()) {
            try {
                sb.append(String.format("%s=%s,", entry.getKey(),
                        URLEncoder.encode(entry.getValue(), "UTF-8")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 去掉最后一个字符"&"
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

//
//    private static Map<String, String> test(Map<String, String> data) {
//        data.put("executeStatus", "true");
//        data.put("notifyTime", "2016-02-23 09:39:44");
//        data.put("orderNo", "20160223093542497AND");
//        data.put("payTime", "2016-02-23 09:39:41");
//        data.put("signType", "MD5");
//        data.put("tradeAction", "pay");
//        data.put("tradeNo","20160223000068095612");
//        return data;
//    }
    /*
     * 账户分页查询
     */
    private static Map<String, String> accountPageQuery(Map<String, String> data) {
        data.put("limit", "1");
        data.put("protocol", "httpPost");
        data.put("signType", "MD5");
        data.put("cardNo", "20131115020055387244");
        data.put("endCreateTime", "2015-10-28 23:59:59");
        data.put("service", "accountPageQuery");
        data.put("beginCreateTime", "2015-10-27 00:00:00");
        data.put("outBizNo", "201510271447455314767");
        return data;
    }

    /*
     * 支付单上传1.0(重庆&深圳海关)
     */
    private static Map<String, String> yhtMultiPaymentBillUpload(Map<String, String> data) {
        data.put("service", "yhtMultiPaymentBillUpload");
        data.put("eshopType", "A");
        data.put("eshopEntCode", "123456");
        data.put("eshopEntName", "易极付");
        data.put("vosList", "[{\"paymentBillAmount\":39,\"payerName\":\"易极付\",\"payerId\":\"61050219800304064X\",\"customsCode\":5349,\"outOrderNo\":\"155612348\"}]");
        return data;
    }

    /*
     * 支付单上传2.0
     */
    private static Map<String, String> paymentBillV2Order(Map<String, String> data) {
        data.put("service", "paymentBillV2Order");
        data.put("customsCode", "5100");
        data.put("outOrderNo", orderNo);
        data.put("eshopEntCode", "123456");
        data.put("eshopEntName", "易极付");
        data.put("payerName", "123");
        data.put("payerDocType", "01");
        data.put("payerId", "500234199203105555");
        data.put("goodsCurrency", "142");
        data.put("goodsAmount", "666.66");
        data.put("taxAmount", "20");
        data.put("taxCurrency", "142");
        data.put("freightCurrency", "142");
        data.put("freightAmount", "10");
        data.put("paymentType", "ALIPAY");
        data.put("tradeNo", "201512131738111");
        return data;
    }

    /*
     * 支付单上传3.0
     */
    private static Map<String, String> singlePaymentUpload(Map<String, String> data) {
        data.put("service", "singlePaymentUpload");
        data.put("customsCode", "GZ_5100");
        data.put("outOrderNo", orderNo);
        data.put("eshopEntCode", "123456");
        data.put("eshopEntName", "易极付");
        data.put("paymentType", "ALIPAY");//支付方式
        data.put("tradeNo", "[\"20151209100256666666\"]");
        data.put("payerDocType", "Identity_Card");
        data.put("payerId", "500234199203105555");
        data.put("payerName", "拓跋");
        data.put("goodsCurrency", "CNY");
        data.put("goodsAmount", "666.66");
        data.put("taxCurrency", "CNY");
        data.put("taxAmount", "20");
        data.put("freightCurrency", "CNY");
        data.put("freightAmount", "10");
        data.put("bizTransType", "OTHER_PORT");//广州海关必填
        return data;
    }

    /**
     * 易汇通实名查询
     *
     * @param data
     * @return
     */
    private static Map<String, String> realNameQuery(Map<String, String> data) {

        data.put("service", "realNameQuery");
        data.put("realName", "拓跋");
        data.put("certNo", "500234199303107254");
        return data;
    }

    /*
     * 商户特别转账
     */
    private static Map<String, String> commonUniqueTransfer(
            Map<String, String> data) {

        data.put("service", "commonUniqueTransfer");
        data.put("amount", "1000");
        data.put("transferBankOrder", "{\"bankName\":\"建设银行\",\"bankCode\": \"CCB\",\"bankAccountNo\":\"6227003766470039521\",\"bankAccountName\":\"拓跋\",\"publicTag\": \"N\"}");
        return data;
    }

    /**
     * 统一收银台 网关支付
     *
     * @return
     */
    private static Map<String, String> commonGatewayTradePay(
            Map<String, String> data) {

        data.put("service", "commonGatewayTradePay");
        data.put("sellerUserId", "20140411020055684571");
        data.put("tradeAmount", "100");
//		data.put("gatewayType", "QUICK");
        data.put("outOrderNo", orderNo);
        data.put("debitCreditType", "DEBIT");
        data.put("version", "2.0");
        data.put("bankCode", "BOC");
        data.put("buyerMobile", "18589010349");
        data.put("personalCorporateType", "PERSONAL");
        data.put("goodsClauses",
                "[{\"name\":\"测试\",\"price\":\"100\",\"quantity\":\"1\"}]");
        return data;
    }

    /**
     * mPay跳转移动收银台
     * 支持的scheme为:HTTPS
     *
     * @return
     */
    private static Map<String, String> createTradeOrder(Map<String, String> data) {

        //data.put("requestType","3");
        data.put("sellerUserId", "20160120020000660372");
        data.put("tradeAmount", "22");
        data.put("tradeName", "01452583162858090783");
        data.put("service", "createTradeOrder");
        data.put("outOrderNo", "201602230000");
        data.put("partnerUserId", "1234567891");
        return data;
    }


    /**
     * 跳转收银台付款
     *
     * @return
     */
    private static Map<String, String> commonTradePay(Map<String, String> data) {
        data.put("tradeName", "跳转收银台支付");
        data.put("service", "commonTradePay");
        data.put("tradeAmount", "105");
        data.put("outOrderNo", orderNo);
        data.put("version", "2.0");
        data.put("sellerUserId", "20140411020055684571");
        data.put("goodsClauses", "[{\"name\":\"跳转收银台测试\"}]");
        return data;
    }

    /**
     * 合并付款（网页）
     *
     * @return
     */
    private static Map<String, String> unionCashierWebPay(Map<String, String> data) {
        data.put("service", "unionCashierWebPay");
        data.put("prodInfoList", "[{'outOrderNo':'00000001000142406','tradeName':'无所谓','tradeAmount':'1.23','sellerUserId':'20140411020055684571','goodsClauses':[{'name':'红酒','price':'123.00','quantity':'1','outId':'22','otherFee':0}]},{'outOrderNo':'E91370513126147554222','tradeName':'不重要','tradeAmount':'1316.53','sellerUserId':'20140411020055684571','goodsClauses':[{'name':'白粉','price':'453.00','quantity':'1','outId':'34','otherFee':0}]}]");
        return data;
    }

    /*
     * 跨境订单信息同步
     */
    private static Map<String, String> corderRemittanceSynOrder(Map<String, String> data) {
        data.put("remittranceBatchNo", BatchNo);
        data.put("service", "corderRemittanceSynOrder");
        data.put("details", "[{\"detailOrderSerialNo\":\"PMPO242260119216032311\",\"tradeCode\":\"122030\",\"tradeType\":\"GOODS_TRADE\",\"detailOrderAmount\":465,\"detailOrderCurrency\":\"CNY\",\"verificationFlag\":\"Y\",\"buyerRealName\":\"徐建信\",\"buyerIDNumber\":\"330324198206050012\",\"logisticInfo\":{\"logisticsCompany\":\"zhongtong\",\"transportNumber\":\"359666719410\",\"outOrderNo\":\"\",\"consigneeName\":\"徐建信\",\"consigneeAddress\":\"浙江省温州市鹿城区鹿城区 浙江温州市鹿城区炬光园中路128号\",\"consigneeContact\":\"13588914426\"},\"goodsClause\":{\"outId\":\"\",\"name\":\"德国Aptamil爱他美奶粉3段(10-12个月宝宝) 800g\",\"memo\":\"\",\"price\":155,\"quantity\":3,\"otherFee\":0,\"unit\":\"\",\"detailUrl\":\"\",\"referUrl\":\"\",\"category\":\"0104\",\"detailOrderCurrency\":\"CNY\"}}]");
        return data;
    }

    /*
     * 跨境汇款申请
     */
    private static Map<String, String> applyRemittranceWithSynOrder(Map<String, String> data) {
        data.put("remittranceBatchNo", BatchNo);
        data.put("service", "applyRemittranceWithSynOrder");
        data.put("outOrderNo", orderNo);
        data.put("payAmount", "465");
        data.put("payCurrency", "CNY");
        data.put("withdrawCurrency", "CNY");
        data.put("payMemo", "汇款申请");
        data.put("toCountryCode", "CHN");
        data.put("tradeUseCode", "326");
        data.put("payeeName", "A");
        data.put("payeeAddress", "重庆");
        data.put("payeeBankName", "中国建设银行");
        data.put("payeeBankAddress", "重庆");
        data.put("payeeBankSwiftCode", "CNAPS CODE");
        data.put("payeeBankNo", "6227003766170093562");
        data.put("tradeUseCode", "326");
        return data;
    }

    /*
     * 微信跳转支付
     */
    private static Map<String, String> CommonWchatTradeRedirect(Map<String, String> data) {
        data.put("outOrderNo", orderNo);
        data.put("service", "CommonWchatTradeRedirect");
        data.put("sellerUserId", "20140411020055684571");
        data.put("tradeAmount", "666.66");
        data.put("goodsClauses", "[{\"name\":\"微信跳转支付测试\"}]");
        return data;
    }

    /*
     * 微信二维码支付
     */
    private static Map<String, String> commonWchatTrade(Map<String, String> data) {
        data.put("service", "commonWchatTrade");
        data.put("outOrderNo", orderNo);
        data.put("sellerUserId", "20140411020055684571");
        data.put("tradeAmount", "666.66");
        data.put("userEndIp", "192.168.1.1");
        data.put("goodsClauses", "[{\"name\":\"微信跳转支付测试\"}]");
        return data;
    }

        /*
     * 微信公众号支付
     */
    private static Map<String, String> commonWchatPublicPay(Map<String, String> data) {
        data.put("service", "commonWchatPublicPay");
        data.put("outOrderNo", orderNo);
        data.put("sellerUserId", "20140411020055684571");
        data.put("tradeAmount", "666.66");
        data.put("userEndIp", "192.168.1.1");
        data.put("openid", "123456789");
        data.put("goodsClauses", "[{\"name\":\"微信跳转支付测试\"}]");
        return data;
    }

    /*
    企富通综合支付
     */
    private static Map<String, String> qftIntegratedPayment(Map<String, String> data) {
        data.put("service", "qftIntegratedPayment");
        data.put("outOrderNo", orderNo);
        data.put("tradeChannel", "CASHIER_MOBILE");
        data.put("money", "666.66");
        data.put("moneyReal", "666.66");
        data.put("outPayeeShopId", "123456");
        data.put("payeeUserId", "20151126020009395480");
        data.put("moneyReal", "666.66");
        data.put("goodList", "[{\"name\":\"微信跳转支付测试\"}]");
        return data;
    }
    /**
     *移动支付提现
     */
    private static Map<String, String> mpayGotoWithdraw(Map<String, String> data) {
        data.put("service", "mpayGotoWithdraw");
        data.put("outOrderNo", orderNo);
        data.put("title", "1");
        data.put("userId", "20141215010000097062");
        return data;
    }
}