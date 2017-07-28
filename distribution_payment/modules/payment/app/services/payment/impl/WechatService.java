package services.payment.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.payment.wechat.WechatPayParamsDTO;
import entity.payment.wechat.WechatConfig;
import entity.payment.wechat.WechatOrder;
import entity.payment.wechat.WechatPayResult;
import entity.payment.wechat.WechatRefundResult;
import entity.payment.wechat.xml.WOrderXml;
import entity.payment.wechat.xml.WQueryReturnXml;
import entity.payment.wechat.xml.WQueryXml;
import mapper.payment.wechat.WechatConfigMapper;
import mapper.payment.wechat.WechatOrderMapper;
import mapper.payment.wechat.WechatPayResultMapper;
import mapper.payment.wechat.WechatRefundResultMapper;
import play.Logger;
import play.libs.Json;
import services.payment.IDealInventoryService;
import services.payment.IWechatService;
import utils.payment.BuildXmlUtils;
import utils.payment.MD5Util;
import utils.payment.PayUtil;
import utils.payment.WechatUtil;

/**
 * Created by LSL on 2016/3/14.
 */
public class WechatService implements IWechatService {

	@Inject
    private IDealInventoryService inventoryService;

    @Inject
    private WechatConfigMapper wechatConfigMapper;

    @Inject
    private WechatOrderMapper wechatOrderMapper;

    @Inject
    private WechatPayResultMapper wechatPayResultMapper;

    @Inject
    private WechatRefundResultMapper wechatRefundResultMapper;
    

    /**
     * 统一支付接口_1_调用微信下单
     */
    @Override
    public WechatPayParamsDTO unifiedorder(String paramStr){
        String req = "0";
        JsonNode param = Json.parse(paramStr);
        WechatPayParamsDTO paramsDTO = new WechatPayParamsDTO();
        String out_trade_no = param.get("orderNo").asText();  //商品订单号
        Map<String, Object> tempParam = Maps.newHashMap();
        tempParam.put("orderNo", out_trade_no);
        List<WechatOrder> lists = wechatOrderMapper.findWOsByMap(tempParam);
        if(lists != null && lists.size() > 0){
            for(WechatOrder wechatOrder : lists){
                Date createDate = wechatOrder.getCreateDate();
                if(StringUtils.isNotEmpty(wechatOrder.getCodeUrl()) && createDate != null &&
                        (new Date().getTime() - createDate.getTime()) < 2 * 60 * 60 * 1000){//该订单号已经下单且两小时内有效
                    req = "1";
                    paramsDTO.setCodeUrl(wechatOrder.getCodeUrl());
                    paramsDTO.setPrepayId(wechatOrder.getPrepayId());
                    paramsDTO.setfProposalCode(wechatOrder.getOrderNo());
                    paramsDTO.setTotalFee(wechatOrder.getTotalFee());
                    paramsDTO.setErrorCode("0");
                    paramsDTO.setErrorInfo("订单"+out_trade_no+"已下单");
                    Logger.info("订单" + out_trade_no + "已下单");
                    return paramsDTO;
                }
            }
        }
        if("0".equals(req)){//未有下单成功记录
            String flag = "100";
            WechatConfig config = this.getWechatConfig();
            String url = config.getUnifiedOrderUrl();//下单请求地址
            String appid = config.getAppid();  //公众账号ID
            String mch_id= config.getMchid();   //商户号
            String key = config.getKey();	//API密钥
            String notify_url = config.getNotifyUrl(); //通知地址 （接收微信支付成功通知）
            Logger.info("notifyUrl:" + notify_url);
            String sign = ""; //签名
            String device_info = param.get("device_info").asText();//终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
            String nonce_str = WechatUtil.create_nonce_str(); //随机字符串
            String body = param.get("orderDes").asText();  //商品描述
            //附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
            String attach = String.valueOf(param.get("id").asInt()/*订单表t_order iid*/);
            String ftotalprices = String.valueOf(param.get("totalPrice").asDouble());
            String total_fee = WechatUtil.moneyUnitConversion(ftotalprices, WechatUtil.CONVESION_0);//总金额 单位为分 不带小数点
            String spbill_create_ip= config.getSpbillCreateIp();  //终端IP  订单生成的机器 IP （需修改）
            String signType = config.getSignType();
            String trade_type = param.get("tradeType").asText();  //JSAPI、NATIVE、APP
            String product_id = param.get("orderNo").asText();//此id为二维码中包含的商品ID

            String prepay_id = "";//预支付交易会话标识
            String openid = "" ;   //用户标示
            String code_url = "";//二维码链接

            SortedMap<Object,Object> params = new TreeMap<Object,Object>();
            params.put("appid", appid);
            params.put("attach", attach);
            params.put("body", body);
            params.put("device_info", device_info);
            params.put("mch_id", mch_id);
            params.put("nonce_str", nonce_str);
            params.put("notify_url", notify_url);
            params.put("out_trade_no", out_trade_no);
            params.put("spbill_create_ip", spbill_create_ip);
            params.put("total_fee", total_fee);
            params.put("trade_type", trade_type);
            params.put("product_id", product_id);
            sign = this.createSign("utf-8", params, key);//生成签名
            params.put("sign", sign);
            //请求下单报文
//	        StringBuffer xmlTemp = new StringBuffer();
//			xmlTemp.append("<xml>");
//			xmlTemp.append( "<appid>"	+ appid + "</appid>");
//			xmlTemp.append( "<attach><![CDATA["+ attach + "]]></attach>");
//			xmlTemp.append( "<body><![CDATA["  + body + "]]></body>");
//			xmlTemp.append( "<device_info>" + device_info + "</device_info>");
//			xmlTemp.append( "<mch_id>"  + mch_id + "</mch_id>");
//			xmlTemp.append( "<nonce_str>" + nonce_str + "</nonce_str>");
//			xmlTemp.append( "<notify_url>" + notify_url + "</notify_url>");
//			xmlTemp.append( "<out_trade_no>" + out_trade_no + "</out_trade_no>");
//			xmlTemp.append( "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>");
//			xmlTemp.append( "<total_fee>" + total_fee + "</total_fee>");
//			xmlTemp.append( "<trade_type>" + trade_type	+ "</trade_type>");
//			xmlTemp.append( "<product_id>" + product_id	+ "</product_id>");
//			xmlTemp.append( "<sign><![CDATA[" + sign + "]]></sign>");
//			xmlTemp.append( "</xml> ");
//			Logger.info(">>>>>>微信支付请求报文>>>>>>："+xmlTemp.toString());
//			//调用微信下单接口
//			String return_info = payUnifiedorder( url , xmlTemp.toString()) ;
            Logger.info(">>>>>>微信支付请求报文>>>>>>：" + Json.toJson(params).toString());
            String return_info = this.payUnifiedorder(url, this.buildWechatOrder(params));
            Logger.info(">>>>>>返回报文>>>:"+return_info);
            String jsonString = "";
            //转换成元为单位
            total_fee = WechatUtil.moneyUnitConversion(total_fee, WechatUtil.CONVESION_1);
            if(return_info!=null && !"".equals(return_info)){
                jsonString = this.makePrepayIdXML(return_info, out_trade_no, total_fee, key);
                Logger.info("******jsonString*****"+jsonString);
            }
            JsonNode jsonObject = Json.parse(jsonString);
            String tErrorCode = jsonObject.get("errorCode").asText();
            if("0".equals(tErrorCode)){
                //返回前台报文
                JsonNode responseJson = jsonObject.get("response");
                flag =  responseJson.get("flag").asText();
                if("0".equals(flag)){//保存预支付订单相关信息
                    prepay_id  = responseJson.get("prepayId").asText();
                    paramsDTO.setAppId(appid);
                    paramsDTO.setfProposalCode(out_trade_no);
                    paramsDTO.setPrepayId(prepay_id);
                    paramsDTO.setMchId(mch_id);
                    paramsDTO.setDeviceInfo(device_info);
                    paramsDTO.setOpenId(openid);
                    paramsDTO.setTradetype(trade_type);
                    paramsDTO.setTotalFee(total_fee);
                    paramsDTO.setSignType(signType);
                    paramsDTO.setIid(Integer.parseInt(attach));//订单iid
                    code_url = responseJson.get("code_url").asText();
                    paramsDTO.setCodeUrl(code_url);
                    paramsDTO.setErrorCode("0");
                    paramsDTO.setErrorInfo("");
                }else{
                    paramsDTO.setErrorCode("1");
                    paramsDTO.setErrorInfo(jsonObject.get("failReason").asText());
                }
            }else{
                paramsDTO.setErrorCode(tErrorCode);
                paramsDTO.setErrorInfo(jsonObject.get("errorInfo").asText());
            }
        }
        return paramsDTO;
    }

    /**
     * 统一支付接口_2_调用微信下单
     */
    @SuppressWarnings("deprecation")
    private  String payUnifiedorder( String url , String request_xml){
        String return_xml ="";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(url);
        post.setRequestBody( request_xml );
        post.getParams().setContentCharset("utf-8");
        // 发送http请求
        try {
            httpClient.executeMethod(post);
            //打印返回的信息
            return_xml = post.getResponseBodyAsString();
            Logger.info(">>>>payUnifiedorder>>return_xml>>"+return_xml);
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            //释放连接
            post.releaseConnection();
        }
        return return_xml ;
    }


    /**
     * 统一支付接口_3_返回报文 组织返回前端信息
     */
    private String makePrepayIdXML(String return_xml , String orderId , String total_fee , String key){
        ObjectNode paraJson = JsonNodeFactory.instance.objectNode();
        ObjectNode responsejson = JsonNodeFactory.instance.objectNode();
        String errorCode = "0";
        String errorInfo = "";
        try {
            Element root = this.getRootElementByString(return_xml); //解析返回xml报文
            String return_code = WechatUtil.resovleXml(root, "return_code");//返回状态码
            String return_msg = WechatUtil.resovleXml(root, "return_msg"); //返回信息
            if("SUCCESS".equals(return_code)){
                String appid =  WechatUtil.resovleXml(root, "appid");
                String mch_id = WechatUtil.resovleXml(root, "mch_id");
                String device_info = WechatUtil.resovleXml(root, "device_info");
                String nonce_str = WechatUtil.resovleXml(root, "nonce_str");
                String sign = WechatUtil.resovleXml(root, "sign");
                String result_code = WechatUtil.resovleXml(root, "result_code");
                String err_code = WechatUtil.resovleXml(root, "err_code"); //错误代码
                String err_code_des = WechatUtil.resovleXml(root, "err_code_des");
                String trade_type = "";//交易类型
                String prepay_id = "";//预支付交易会话标识
                String code_url = "";//二维码链接
                if("SUCCESS".equals(result_code)){
                    trade_type = WechatUtil.resovleXml(root, "trade_type");
                    code_url = WechatUtil.resovleXml(root, "code_url");
                    prepay_id = WechatUtil.resovleXml(root, "prepay_id");
                    SortedMap<Object,Object> params = new TreeMap<Object,Object>();
                    params.put("return_code", return_code);
                    params.put("return_msg", return_msg);
                    params.put("appid", appid);
                    params.put("mch_id", mch_id);
                    params.put("device_info", device_info);
                    params.put("nonce_str", nonce_str);
                    params.put("result_code", result_code);
                    params.put("err_code", err_code);
                    params.put("err_code_des", err_code_des);
                    params.put("trade_type", trade_type);
                    params.put("prepay_id", prepay_id);
                    params.put("code_url", code_url);
                    Logger.info(">>>makePrepayIdXML>>params>>>>>>"+params);
                    String createdSign = this.createSign("utf-8", params, key);//生成签名
                    Logger.info(">>makePrepayIdXML>>>createdSign>>"+createdSign);
                    if(sign.equals(createdSign)){
                        Logger.info(">>>>>total_fee>>>>>>>>"+total_fee);
                        WechatOrder order = new WechatOrder();
                        order.setSign(sign);
                        order.setAppid(appid);
                        order.setDeviceInfo(device_info);
                        order.setErrCode(err_code);
                        order.setErrCodeDes(err_code_des);
                        order.setReturnCode(return_code);
                        order.setReturnMsg(return_msg);
                        order.setMchId(mch_id);
                        order.setNonceStr(nonce_str);
                        order.setResultCode(result_code);
                        order.setTradeType(trade_type);
                        order.setOrderNo(orderId);
                        order.setPrepayId(prepay_id);
                        order.setCodeUrl(code_url);
                        order.setCreateDate(new Date());
                        order.setTotalFee(total_fee);
                        int result = wechatOrderMapper.insertSelective(order);
                        Logger.info("保存下单返回结果："+result);
                        responsejson.put("flag", "0");
                        responsejson.put("prepayId", prepay_id);
                        responsejson.put("code_url", code_url);
                    }else{
                        errorCode = "1";
                        errorInfo = "校验签名失败";
                    }
                }else{
                    errorCode = "1";
                    errorInfo = err_code_des;
                }
            }else{
                errorCode = "1";
                errorInfo = return_msg;
            }
        } catch (Exception e){
            errorCode = "1";
            errorInfo = "下单返回报文解析异常";
            e.printStackTrace();
        }
        paraJson.set("response", responsejson);
        paraJson.put("errorCode", errorCode);
        paraJson.put("errorInfo", errorInfo);
        Logger.info(">>>makePrepayIdXML>>>>paraJson>>"+paraJson.toString());
        return paraJson.toString();
    }

    /**
     * 微信支付下单
     */
    private String buildWechatOrder(SortedMap<Object,Object> maps){
        WOrderXml wechat = new WOrderXml();
        wechat.setAppid((String)maps.get("appid"));
        wechat.setAttach((String)maps.get("attach"));
        wechat.setBody((String)maps.get("body"));
        wechat.setDeviceInfo((String)maps.get("device_info"));
        wechat.setMchId((String)maps.get("mch_id"));
        wechat.setNonceStr((String)maps.get("nonce_str"));
        wechat.setNotifyUrl((String)maps.get("notify_url"));
        wechat.setOutTradeNo((String)maps.get("out_trade_no"));
        wechat.setSpbillCreateIp((String)maps.get("spbill_create_ip"));
        wechat.setTotalFee((String)maps.get("total_fee"));
        wechat.setTradeType((String)maps.get("trade_type"));
        wechat.setSign((String)maps.get("sign"));
        wechat.setProductId((String)maps.get("product_id"));
        BuildXmlUtils<WOrderXml> xmlUtils = new BuildXmlUtils<WOrderXml>(){};
        String xml = xmlUtils.bean2xml(wechat);
        Logger.debug(">>微信支付请求报文>>xml>>>>>"+xml);
        return xml;
    }

    /**
     * 根据订单号调用微信支付结果查询接口查询支付结果
     */
    @Override
    public String qryWechatPayResult(String orderNo){
        String errorCode = "0";
        String errorInfo = "";
        ObjectNode responseJson = JsonNodeFactory.instance.objectNode();
        ObjectNode resultJson = JsonNodeFactory.instance.objectNode();
        WechatConfig wechatConfig = this.getWechatConfig();
        String appid = wechatConfig.getAppid();  //公众账号ID
        String mchId= wechatConfig.getMchid();   //商户号
        String nonceStr = WechatUtil.create_nonce_str();
        String key = wechatConfig.getKey();
        SortedMap<Object,Object> params = new TreeMap<Object,Object>();
        params.put("appid", appid);
        params.put("mch_id", mchId);
        params.put("out_trade_no", orderNo);
        params.put("nonce_str", nonceStr);
        String signReq = this.createSign("utf-8", params, key);
        String url = wechatConfig.getOrderQueryUrl();
        //拼装查询报文
        WQueryXml wQueryXml = new WQueryXml();
        wQueryXml.setAppid(appid);
        wQueryXml.setMchId(mchId);
        wQueryXml.setNonceStr(nonceStr);
        wQueryXml.setOutTradeNo(orderNo);
        wQueryXml.setSign(signReq);
        BuildXmlUtils<WQueryXml> xmlUtils = new BuildXmlUtils<WQueryXml>(){};
        String reqXml = xmlUtils.bean2xml(wQueryXml);
        Logger.info(">>>qryWechatPayResult>>reqXml>>"+reqXml);
        try{
            String returnMess = this.payUnifiedorder(url, reqXml);
            Logger.info(">>>>查询返回报文>>>returnMess>>>>"+returnMess);
            //Element root = getRootElementByString(returnMess);
            String result = this.test(returnMess, key);//resovleQryXml(root, key);
            Logger.info(">>qryWechatPayResult>>result>>"+result);
            return result;
        } catch (Exception e) {
            errorCode = "1";
            errorInfo = "解析报文异常";
            e.printStackTrace();
        }
        resultJson.put("errorCode", errorCode);
        resultJson.put("errorInfo", errorInfo);
        resultJson.set("response", responseJson);
        Logger.info(">>>qryWechatPayResult()>>resultJson>>>>"+resultJson);
        return resultJson.toString();
    }

    /**
     * 解析查询返回报文
     * @return
     */
    private String test(String resXml, String key){
        BuildXmlUtils<WQueryReturnXml> xmlUtils = new BuildXmlUtils<WQueryReturnXml>(){};
        WQueryReturnXml wQueryRXml = xmlUtils.xml2bean(resXml);
        String errorCode = "0";
        String errorInfo = "";
        ObjectNode responseJson = JsonNodeFactory.instance.objectNode();
        ObjectNode resultJson = JsonNodeFactory.instance.objectNode();
        String returnCode = wQueryRXml.getResultCode();
        String returnMsg = wQueryRXml.getReturnMsg();
        Logger.info(">>>>>returnCode>>>>>>"+returnCode);
        String resultCode = wQueryRXml.getResultCode();
        SortedMap<Object,Object> map = new TreeMap<Object,Object>();
//	    try{
//	    	map = BeanMapUtils.bean2sortedmap(wQueryRXml);
//	    	Logger.info(">>map>>>>"+map.toString());
//	    }catch(Exception e){
//	    	Logger.error(">>>test>>>bean2sortedmap exception>>>");
//	    	e.printStackTrace();
//	    }
        map = WechatUtil.putInMap("return_code", returnCode, map);
        map = WechatUtil.putInMap("return_msg", returnMsg, map);
        map = WechatUtil.putInMap("result_code", resultCode, map);
        if(WechatUtil.RETURN_CODE_SUCCESS.equals(returnCode)){
            String appid = wQueryRXml.getAppid();
            map = WechatUtil.putInMap("appid", appid, map);

            String mch_id = wQueryRXml.getMchId();
            map = WechatUtil.putInMap("mch_id", mch_id, map);

            String attach = wQueryRXml.getAttach();// 商家数据包（原样返回）
            map = WechatUtil.putInMap("attach", attach, map);

            String bankype = wQueryRXml.getBankType();// 付款银行
            map = WechatUtil.putInMap("bank_type", bankype, map);

            String deviceInfo = wQueryRXml.getDeviceInfo();// 微信支付分配的终端设备号 (扫码:WEB)
            map = WechatUtil.putInMap("device_info", deviceInfo, map);

            String feeType = wQueryRXml.getFeeType();// 货币种类
            map = WechatUtil.putInMap("fee_type", feeType, map);

            String isSubscribe = wQueryRXml.getIsSubscribe();// 是否关注公众账号
            map = WechatUtil.putInMap("is_subscribe", isSubscribe, map);

            String nonceStr = wQueryRXml.getNonceStr();// 随机窜
            map = WechatUtil.putInMap("nonce_str", nonceStr, map);

            String openid = wQueryRXml.getOpenid();// 用户标识
            map = WechatUtil.putInMap("openid", openid, map);

            String signRes = wQueryRXml.getSign();// 签名
            Logger.info(">>PayControl()>>>>>sign>>>>>"+signRes);

            String timeEnd = wQueryRXml.getTimeEnd();// 支付完成时间
            map = WechatUtil.putInMap("time_end", timeEnd, map);

            String totalFee = wQueryRXml.getTotalFee();// 订单总金额，单位为分
            map = WechatUtil.putInMap("total_fee", totalFee, map);
            totalFee = WechatUtil.moneyUnitConversion(totalFee, WechatUtil.CONVESION_1);// 换算成元为单位

            String tradeType = wQueryRXml.getTradeType();// 交易类型
            map = WechatUtil.putInMap("trade_type", tradeType, map);

            String transactionId = wQueryRXml.getTransactionId();// 微信支付订单号
            map = WechatUtil.putInMap("transaction_id", transactionId, map);

            String outTradeNo  = wQueryRXml.getOutTradeNo();//订单号
            map = WechatUtil.putInMap("out_trade_no", outTradeNo, map);

            String tradeState = wQueryRXml.getTradeState();//交易状态
            map = WechatUtil.putInMap("trade_state", tradeState, map);

            String tradeStateDesc = wQueryRXml.getTradeStateDesc();//交易状态描述
            map = WechatUtil.putInMap("trade_state_desc", tradeStateDesc, map);

            String cashFee = wQueryRXml.getCashFee();
            map = WechatUtil.putInMap("cash_fee", cashFee, map);
            Logger.info(">>PayControl()>>map>>>>>>>"+map);
            String createSign = this.createSign("utf-8", map, key);
            Logger.info(">>>>>PayControl()>>>createSign>>>"+createSign);
            if(signRes.equals(createSign)){
                WechatPayResult proposal = new WechatPayResult();
                Map<String, Object> tempParam = Maps.newHashMap();
                tempParam.put("outTradeNo", outTradeNo);
                List<WechatPayResult> proposals = wechatPayResultMapper.findWPRsByMap(tempParam);
                if(proposals != null && proposals.size() > 0){//查询订单存在
                    proposal = proposals.get(0);
                    //支付未成功的订单更新支付状态
                    if(proposal.getTradeState() == null || !(WechatUtil.TRADE_STATE_SUCCESS.equals(proposal.getTradeState()))){
                        proposal.setAppid(appid);
                        proposal.setAttach(attach);
                        proposal.setBankype(bankype);
                        proposal.setDeviceInfo(deviceInfo);
                        proposal.setFeeType(feeType);
                        proposal.setIsSubscribe(isSubscribe);
                        proposal.setMchId(mch_id);
                        proposal.setOpenid(openid);
                        proposal.setResultCode(resultCode);
                        proposal.setReturnCode(returnCode);
                        proposal.setSign(signRes);
                        proposal.setTimeEnd(timeEnd);
                        proposal.setTotalFee(totalFee);
                        proposal.setCashFee(cashFee);
                        proposal.setTradeType(tradeType);
                        proposal.setTransactionId(transactionId);
                        proposal.setNonceStr(nonceStr);
                        proposal.setTradeState(tradeState);
                        proposal.setTradeStateDesc(tradeStateDesc);
                        proposal.setLastUpdateDate(new Date());
                        int updateResult = wechatPayResultMapper.updateByPrimaryKeySelective(proposal);
                        Logger.info(">>>>>updateResult>>>>"+updateResult);
                    }
                }else{
                    proposal.setOutTradeNo(outTradeNo);
                    proposal.setAppid(appid);
                    proposal.setAttach(attach);
                    proposal.setBankype(bankype);
                    proposal.setDeviceInfo(deviceInfo);
                    proposal.setFeeType(feeType);
                    proposal.setIsSubscribe(isSubscribe);
                    proposal.setMchId(mch_id);
                    proposal.setOpenid(openid);
                    proposal.setResultCode(resultCode);
                    proposal.setReturnCode(returnCode);
                    proposal.setTotalFee(totalFee);
                    proposal.setCashFee(cashFee);
                    proposal.setSign(signRes);
                    proposal.setTimeEnd(timeEnd);
                    proposal.setTradeType(tradeType);
                    proposal.setTransactionId(transactionId);
                    proposal.setNonceStr(nonceStr);
                    proposal.setTradeState(tradeState);
                    proposal.setTradeStateDesc(tradeStateDesc);
                    proposal.setCreateDate(new Date());
                    int result = wechatPayResultMapper.insertSelective(proposal);
                    Logger.info(">>>>> save result>>>>"+result);
                }
                responseJson.put("outTradeNo", outTradeNo);
                responseJson.put("tradeState", tradeState);
            }else{
                errorCode = "1";
                errorInfo = "校验签名失败";
            }
        } else if("SUCCESS".equals(returnCode) && "FAIL".equals(resultCode)){
            errorCode = "1";
            errorInfo = wQueryRXml.getErrCodeDes();
        }else{
            errorCode = "1";
            errorInfo = wQueryRXml.getReturnMsg();
        }
        resultJson.set("response", responseJson);
        resultJson.put("errorCode", errorCode);
        resultJson.put("errorInfo", errorInfo);
        return resultJson.toString();
    }

    /**
     * 根据订单号查询支付结果(先查结果记录是否支付成功，不存在或不成功则调微信的查询接口)
     */
    @Override
    public String queryWechatPayResult(String orderNo,String postFlag) {
        Map<String, Object> tempParam = Maps.newHashMap();
        tempParam.put("outTradeNo", orderNo);
        List<WechatPayResult> proposals = wechatPayResultMapper.findWPRsByMap(tempParam);
        if (proposals != null && proposals.size() > 0) {
            //支付记录存在
            WechatPayResult proposal = proposals.get(0);
            if (proposal.getTradeState() != null &&
                    WechatUtil.TRADE_STATE_SUCCESS.equals(proposal.getTradeState())) {
                try {
                	//add by xuse 记录显示支付成功，同步相关信息到采购订单
                	if(proposal.getOutTradeNo().toUpperCase().startsWith("CG") || "true".equals(postFlag)) {
                		return "success";
                	}
                	//add by duyt 记录显示支付成功，同步相关信息到在线充值记录
                	if(proposal.getOutTradeNo().toUpperCase().startsWith("CZ")) {
                		return "cz_success";
                	}
                	//记录显示支付成功，同步相关支付信息到销售订单
                    PayUtil.syncPayInfoToSaleOrder(proposal.getOutTradeNo(),
                            new DateTime(DateUtils.parseDate(proposal.getTimeEnd(), "yyyyMMddHHmmss")).toString("yyyy-MM-dd HH:mm:ss"),
                            proposal.getTransactionId(), "wechatpay", "CNY", "6", "", "");
                } catch (ParseException e) {
                    e.printStackTrace();
                    return "false";
                }
                return "true";
            } else {
                //记录显示支付失败，调微信查询接口确认
                JsonNode json = Json.parse(this.qryWechatPayResult(orderNo));
                if("0".equals(json.get("errorCode").asText())) {
                    JsonNode obj = json.get("response");
                    String tradeState = obj.has("tradeState") ? obj.get("tradeState").asText() : "";
                    if (WechatUtil.TRADE_STATE_SUCCESS.equals(tradeState)) {
                    	//add by xuse 记录显示支付成功，同步相关信息到采购订单
                    	String outTradeNo = proposal.getOutTradeNo();
                    	return getFlag(outTradeNo, postFlag);
                		
                    }
                }
            }
        } else {
            //支付记录不存在，调微信查询接口查询
            JsonNode json = Json.parse(this.qryWechatPayResult(orderNo));
            if("0".equals(json.get("errorCode").asText())) {
                JsonNode obj = json.get("response");
                String tradeState = obj.has("tradeState") ? obj.get("tradeState").asText() : "";
                if (WechatUtil.TRADE_STATE_SUCCESS.equals(tradeState)) {
                	//add by xuse 记录显示支付成功，同步相关信息到采购订单
                	return getFlag(orderNo, postFlag);
                }
            }
        }
        return "false";
    }


	/**
     * 解析微信查询返回
     */
    @SuppressWarnings("unused")
	private String resovleQryXml(Element root, String key){
        String errorCode = "0";
        String errorInfo = "";
        ObjectNode responseJson = JsonNodeFactory.instance.objectNode();
        ObjectNode resultJson = JsonNodeFactory.instance.objectNode();
        String returnCode = WechatUtil.resovleXml(root, "return_code");
        String returnMsg = WechatUtil.resovleXml(root, "return_msg");
        Logger.info(">>>>>returnCode>>>>>>"+returnCode);
        String resultCode = WechatUtil.resovleXml(root, "result_code");
        Logger.info(">>>>>resultCode>>>>>>"+resultCode);
        SortedMap<Object,Object> map = new TreeMap<Object,Object>();
        map = WechatUtil.putInMap("return_code", returnCode, map);
        map = WechatUtil.putInMap("return_msg", returnMsg, map);
        map = WechatUtil.putInMap("result_code", resultCode, map);
        if(WechatUtil.RETURN_CODE_SUCCESS.equals(returnCode) && WechatUtil.RETURN_CODE_SUCCESS.equals(resultCode)){
            String appid = WechatUtil.resovleXml(root, "appid");
            map = WechatUtil.putInMap("appid", appid, map);

            String mch_id = WechatUtil.resovleXml(root, "mch_id");
            map = WechatUtil.putInMap("mch_id", mch_id, map);

            String attach = WechatUtil.resovleXml(root, "attach");// 商家数据包（原样返回）
            map = WechatUtil.putInMap("attach", attach, map);

            String bankype = WechatUtil.resovleXml(root, "bank_type");// 付款银行
            map = WechatUtil.putInMap("bank_type", bankype, map);

            String deviceInfo = WechatUtil.resovleXml(root, "device_info");// 微信支付分配的终端设备号 (扫码:WEB)
            map = WechatUtil.putInMap("device_info", deviceInfo, map);

            String feeType = WechatUtil.resovleXml(root, "fee_type");// 货币种类
            map = WechatUtil.putInMap("fee_type", feeType, map);

            String isSubscribe = WechatUtil.resovleXml(root, "is_subscribe");// 是否关注公众账号
            map = WechatUtil.putInMap("is_subscribe", isSubscribe, map);

            String nonceStr = WechatUtil.resovleXml(root, "nonce_str");// 随机窜
            map = WechatUtil.putInMap("nonce_str", nonceStr, map);

            String openid = WechatUtil.resovleXml(root, "openid");// 用户标识
            map = WechatUtil.putInMap("openid", openid, map);

            String signRes = WechatUtil.resovleXml(root, "sign");// 签名
            Logger.info(">>PayControl()>>>>>sign>>>>>"+signRes);

            String timeEnd = WechatUtil.resovleXml(root, "time_end");// 支付完成时间
            map = WechatUtil.putInMap("time_end", timeEnd, map);

            String totalFee = WechatUtil.resovleXml(root, "total_fee");// 订单总金额，单位为分
            map = WechatUtil.putInMap("total_fee", totalFee, map);
            totalFee = WechatUtil.moneyUnitConversion(totalFee, WechatUtil.CONVESION_1);// 换算成元为单位

            String tradeType = WechatUtil.resovleXml(root, "trade_type");// 交易类型
            map = WechatUtil.putInMap("trade_type", tradeType, map);

            String transactionId = WechatUtil.resovleXml(root, "transaction_id");// 微信支付订单号
            map = WechatUtil.putInMap("transaction_id", transactionId, map);

            String outTradeNo  = WechatUtil.resovleXml(root, "out_trade_no");//订单号
            map = WechatUtil.putInMap("out_trade_no", outTradeNo, map);

            String tradeState = WechatUtil.resovleXml(root, "trade_state");//交易状态
            map = WechatUtil.putInMap("trade_state", tradeState, map);

            String tradeStateDesc = WechatUtil.resovleXml(root, "trade_state_desc");//交易状态描述
            map = WechatUtil.putInMap("trade_state_desc", tradeStateDesc, map);

            String cashFee = WechatUtil.resovleXml(root, "cash_fee");
            map = WechatUtil.putInMap("cash_fee", cashFee, map);
            Logger.info(">>PayControl()>>map>>>>>>>"+map);
            String createSign = this.createSign("utf-8", map, key);
            Logger.info(">>>>>PayControl()>>>createSign>>>"+createSign);
            if(signRes.equals(createSign)){
                WechatPayResult proposal = new WechatPayResult();
                Map<String, Object> tempParam = Maps.newHashMap();
                tempParam.put("outTradeNo", outTradeNo);
                List<WechatPayResult> proposals = wechatPayResultMapper.findWPRsByMap(tempParam);
                if(proposals != null && proposals.size() > 0){//查询订单存在
                    proposal = proposals.get(0);
                    //支付未成功的订单更新支付状态
                    if(proposal.getTradeState() == null || !(WechatUtil.TRADE_STATE_SUCCESS.equals(proposal.getTradeState()))){
                        proposal.setAppid(appid);
                        proposal.setAttach(attach);
                        proposal.setBankype(bankype);
                        proposal.setDeviceInfo(deviceInfo);
                        proposal.setFeeType(feeType);
                        proposal.setIsSubscribe(isSubscribe);
                        proposal.setMchId(mch_id);
                        proposal.setOpenid(openid);
                        proposal.setResultCode(resultCode);
                        proposal.setReturnCode(returnCode);
                        proposal.setSign(signRes);
                        proposal.setTimeEnd(timeEnd);
                        proposal.setTotalFee(totalFee);
                        proposal.setCashFee(cashFee);
                        proposal.setTradeType(tradeType);
                        proposal.setTransactionId(transactionId);
                        proposal.setNonceStr(nonceStr);
                        proposal.setTradeState(tradeState);
                        proposal.setTradeStateDesc(tradeStateDesc);
                        proposal.setLastUpdateDate(new Date());
                        int updateResult = wechatPayResultMapper.insertSelective(proposal);
                        Logger.info(">>>>>updateResult>>>>"+updateResult);
                    }
                }else{
                    proposal.setOutTradeNo(outTradeNo);
                    proposal.setAppid(appid);
                    proposal.setAttach(attach);
                    proposal.setBankype(bankype);
                    proposal.setDeviceInfo(deviceInfo);
                    proposal.setFeeType(feeType);
                    proposal.setIsSubscribe(isSubscribe);
                    proposal.setMchId(mch_id);
                    proposal.setOpenid(openid);
                    proposal.setResultCode(resultCode);
                    proposal.setReturnCode(returnCode);
                    proposal.setTotalFee(totalFee);
                    proposal.setCashFee(cashFee);
                    proposal.setSign(signRes);
                    proposal.setTimeEnd(timeEnd);
                    proposal.setTradeType(tradeType);
                    proposal.setTransactionId(transactionId);
                    proposal.setNonceStr(nonceStr);
                    proposal.setTradeState(tradeState);
                    proposal.setTradeStateDesc(tradeStateDesc);
                    proposal.setCreateDate(new Date());
                    int result = wechatPayResultMapper.insertSelective(proposal);
                    Logger.info(">>>>> save result>>>>"+result);
                }
                responseJson.put("outTradeNo", outTradeNo);
                responseJson.put("tradeState", tradeState);
            }else{
                errorCode = "1";
                errorInfo = "校验签名失败";
            }
        } else if("SUCCESS".equals(returnCode) && "FAIL".equals(resultCode)){
            errorCode = "1";
            errorInfo = WechatUtil.resovleXml(root, "err_code_des");
        }else{
            errorCode = "1";
            errorInfo = WechatUtil.resovleXml(root, "return_msg");
        }
        resultJson.set("response", responseJson);
        resultJson.put("errorCode", errorCode);
        resultJson.put("errorInfo", errorInfo);
        return resultJson.toString();
    }


    /**
     * 微信支付申请退款接口
     * @return
     */
    @Override
    public Map<String,String> wechatRefund(String orderNo){
        String key = this.getWechatConfig().getKey();
        String errorCode = "0";
        String errorInfo = "";
        Map<String,String> resultJson = Maps.newHashMap();
        WechatPayResult proposal = new WechatPayResult();
        Map<String, Object> tempParam = Maps.newHashMap();
        tempParam.put("outTradeNo", orderNo);
        List<WechatPayResult> lists = wechatPayResultMapper.findWPRsByMap(tempParam);
        if(lists != null && lists.size() > 0){
            proposal = lists.get(0);
            String appid = proposal.getAppid();//公众账号ID
            String mchId = proposal.getMchId();//商户号
            String deviceInfo = proposal.getDeviceInfo();//终端设备号
            String nonceStr = WechatUtil.create_nonce_str();//随机字符串
            String transactionId = proposal.getTransactionId();//微信订单号
            String outTradeNo = proposal.getOutTradeNo();//商户订单号
            String outRefundNo = "RE_WECHAT_" + proposal.getOutTradeNo();//商户退款单号
            String totalFee = proposal.getTotalFee();//总金额
            totalFee = WechatUtil.moneyUnitConversion(totalFee, WechatUtil.CONVESION_0);//金额转成分为单位
            String refundFee = totalFee;//退款金额----应由前端传入，根据商城规则，计算退款金额
            String refundFeeType = proposal.getFeeType();//货币种类
            String opUserId = proposal.getMchId();//操作员帐号, 默认为商户号

            SortedMap<Object,Object> params = new TreeMap<Object,Object>();
            params.put("appid", appid);
            params.put("mch_id", mchId);
            params.put("device_info", deviceInfo);
            params.put("nonce_str", nonceStr);
            params.put("transaction_id", transactionId);
            params.put("out_trade_no", outTradeNo);
            params.put("out_refund_no", outRefundNo);
            params.put("total_fee", totalFee);
            params.put("refund_fee", refundFee);
            params.put("refund_fee_type", refundFeeType);
            params.put("op_user_id", opUserId);
            Logger.info(">>>>params>>>>.."+params);
            String sign = this.createSign("utf-8", params, key);//生成签名
            StringBuffer xmlTemp = new StringBuffer();
            xmlTemp.append("<xml>");
            xmlTemp.append( "<appid>"	+ appid + "</appid>");
            xmlTemp.append( "<mch_id>"	+ mchId + "</mch_id>");
            xmlTemp.append( "<device_info>" +deviceInfo+ "</device_info>");
            xmlTemp.append( "<nonce_str>"	+ nonceStr + "</nonce_str>");
            xmlTemp.append( "<transaction_id>"	+ transactionId + "</transaction_id>");
            xmlTemp.append( "<out_trade_no>" +outTradeNo+ "</out_trade_no>");
            xmlTemp.append( "<out_refund_no>"	+ outRefundNo + "</out_refund_no>");
            xmlTemp.append( "<total_fee>" +totalFee+ "</total_fee>");
            xmlTemp.append( "<refund_fee>"	+ refundFee + "</refund_fee>");
            xmlTemp.append( "<refund_fee_type>"	+ refundFeeType + "</refund_fee_type>");
            xmlTemp.append( "<op_user_id>"	+ opUserId + "</op_user_id>");
            xmlTemp.append( "<sign>"	+ sign + "</sign>");
            xmlTemp.append( "</xml>");
            Logger.info(">>>查询请求报文>>xmlTemp>>>"+xmlTemp);
            String returnMess = "";
            try {
                returnMess = this.getResponse(xmlTemp.toString());
                Logger.info(">>>returnMess>>>>>>"+returnMess);
                Element root = this.getRootElementByString(returnMess);
                Map<String,String> resovleMess = this.resovleRefundResponse(root, proposal);
                Logger.info(">>>>>resovleMess>>>>>"+resovleMess);
                return resovleMess;
            } catch (Exception e) {
                errorCode = "1";
                errorInfo = "请求微信服务器异常";
                Logger.error(errorInfo, e);
            }
        }else{
            errorCode = "1";
            errorInfo = "查询退款订单记录";
        }
        resultJson.put("errorCode", errorCode);
        resultJson.put("errorInfo", errorInfo);
        return resultJson;
    }

    /**
     * 加载证书调用微信退款接口
     * @param xml
     * @return
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    private String getResponse(String xml) throws Exception{
        WechatConfig config = this.getWechatConfig();
        String body = "";
        //指定读取证书格式为PKCS12
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        //读取本机存放的PKCS12证书文件
        InputStream instream = WechatService.class.getResourceAsStream("/apiclient_cert.p12");
        try {
            //指定PKCS12的密码(商户ID)
            keyStore.load(instream, config.getMchid().toCharArray());
        } catch(Exception e){
            e.printStackTrace();
        }finally {
            instream.close();
        }
        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, config.getMchid().toCharArray()).build();
        // Allow TLSv1 protocol only
        //指定TLS版本
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        try {
            //指定request参数、参数类型、编码方式
            HttpEntity httpEntity = new StringEntity(xml, "text/xml",  "utf-8");
            HttpPost httppost = new HttpPost(WechatUtil.REFUND_URL);
            httppost.setEntity(httpEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                Logger.info("----------------------------------------");
                Logger.info(response.getStatusLine().toString());
                if (entity != null) {
                    body = EntityUtils.toString(entity, "utf-8");
                }
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        return body;
    }

    /**
     * 解析微信退款报文
     * @return
     */
    private Map<String,String> resovleRefundResponse(Element root, WechatPayResult proposal){
        String errorCode = "0";
        String errorInfo = "";
        Map<String,String> responseJson = Maps.newHashMap();
        Map<String,String> resultJson = Maps.newHashMap();
        String key = this.getWechatConfig().getKey();
        SortedMap<Object,Object> map = new TreeMap<Object,Object>();
        String return_code = WechatUtil.resovleXml(root, "return_code");//返回状态码
        map = WechatUtil.putInMap("return_code", return_code, map);

        String return_msg = WechatUtil.resovleXml(root, "return_msg");//返回信息
        map = WechatUtil.putInMap("return_msg", return_msg, map);
        if(WechatUtil.SUCCESS.equals(return_code)){
            String result_code = WechatUtil.resovleXml(root, "result_code");//业务结果
            map = WechatUtil.putInMap("result_code", result_code, map);

            String err_code = WechatUtil.resovleXml(root, "err_code");//错误代码
            map = WechatUtil.putInMap("err_code", err_code, map);

            String err_code_des = WechatUtil.resovleXml(root, "err_code_des");//错误代码描述
            map = WechatUtil.putInMap("err_code_des", err_code_des, map);

            String appid = WechatUtil.resovleXml(root, "appid");//公众账号ID
            map = WechatUtil.putInMap("appid", appid, map);

            String mch_id = WechatUtil.resovleXml(root, "mch_id");//商户号
            map = WechatUtil.putInMap("mch_id", mch_id, map);

            String device_info = WechatUtil.resovleXml(root, "device_info");//设备号
            map = WechatUtil.putInMap("device_info", device_info, map);

            String nonce_str = WechatUtil.resovleXml(root, "nonce_str");//随机字符串
            map = WechatUtil.putInMap("nonce_str", nonce_str, map);

            String sign = WechatUtil.resovleXml(root, "sign");//签名
            Logger.info(">>>resovleRefundResponse>>>>>sign>>>>>"+sign);

            String transaction_id = WechatUtil.resovleXml(root, "transaction_id");//微信订单号
            map = WechatUtil.putInMap("transaction_id", transaction_id, map);

            String out_trade_no = WechatUtil.resovleXml(root, "out_trade_no");//商户订单号
            map = WechatUtil.putInMap("out_trade_no", out_trade_no, map);

            String out_refund_no = WechatUtil.resovleXml(root, "out_refund_no");//商户退款单号
            map = WechatUtil.putInMap("out_refund_no", out_refund_no, map);

            String refund_id = WechatUtil.resovleXml(root, "refund_id");//微信退款单号
            map = WechatUtil.putInMap("refund_id", refund_id, map);

            String refund_channel = WechatUtil.resovleXml(root, "refund_channel");//退款渠道
            map = WechatUtil.putInMap("refund_channel", refund_channel, map);

            String refund_fee = WechatUtil.resovleXml(root, "refund_fee");//退款金额
            map = WechatUtil.putInMap("refund_fee", refund_fee, map);

            String total_fee = WechatUtil.resovleXml(root, "total_fee");//订单总金额
            map = WechatUtil.putInMap("total_fee", total_fee, map);

            String fee_type = WechatUtil.resovleXml(root, "fee_type");//订单金额货币种类
            map = WechatUtil.putInMap("fee_type", fee_type, map);

            String cash_fee = WechatUtil.resovleXml(root, "cash_fee");//现金支付金额
            map = WechatUtil.putInMap("cash_fee", cash_fee, map);

            String cash_refund_fee = WechatUtil.resovleXml(root, "cash_refund_fee");//现金退款金额
            map = WechatUtil.putInMap("cash_refund_fee", cash_refund_fee, map);

            String coupon_refund_fee = WechatUtil.resovleXml(root, "coupon_refund_fee");//代金券或立减优惠退款金额
            map = WechatUtil.putInMap("coupon_refund_fee", coupon_refund_fee, map);

            String coupon_refund_count = WechatUtil.resovleXml(root, "coupon_refund_count");//代金券或立减优惠使用数量
            map = WechatUtil.putInMap("coupon_refund_count", coupon_refund_count, map);

            String coupon_refund_id = WechatUtil.resovleXml(root, "coupon_refund_id");//代金券或立减优惠ID
            map = WechatUtil.putInMap("coupon_refund_id", coupon_refund_id, map);
            Logger.info(">>resovleRefundResponse()>>map>>>>>>>"+map);
            String createSign = this.createSign("utf-8", map, key);
            Logger.info(">>>>>resovleRefundResponse()>>>createSign>>>"+createSign);
            if(sign.equals(createSign)){
                responseJson.put("refundFee", refund_fee);
                responseJson.put("outTradeNo", out_trade_no);
                responseJson.put("resultCode", result_code);
                WechatRefundResult refund = new WechatRefundResult();
                refund.setAppid(appid);
                Logger.info(">>>cash_fee>>>>>>"+cash_fee);
                if(StringUtils.isNotEmpty(cash_fee))
                    refund.setCashFee(Integer.parseInt(cash_fee));
                if(StringUtils.isNotEmpty(cash_refund_fee))
                    refund.setCashRefundFee(Integer.parseInt(cash_refund_fee));
                if(StringUtils.isNotEmpty(coupon_refund_count))
                    refund.setCouponRefundCount(Integer.parseInt(coupon_refund_count));
                if(StringUtils.isNotEmpty(coupon_refund_fee))
                    refund.setCouponRefundFee(Integer.parseInt(coupon_refund_fee));
                refund.setCouponRefundId(coupon_refund_id);
                refund.setDeviceInfo(device_info);
                refund.setErrCode(err_code);
                refund.setErrCodeDes(err_code_des);
                refund.setFeeType(fee_type);
                refund.setMchId(mch_id);
                refund.setNonceStr(nonce_str);
                refund.setOutRefundNo(out_refund_no);
                refund.setOutTradeNo(out_trade_no);
                refund.setRefundChannel(refund_channel);
                if(StringUtils.isNotEmpty(refund_fee))
                    refund.setRefundFee(Integer.parseInt(refund_fee));
                refund.setRefundId(refund_id);
                refund.setResultCode(result_code);
                refund.setReturnCode(return_code);
                refund.setReturnMsg(return_msg);
                refund.setSign(sign);
                if(StringUtils.isNotEmpty(total_fee))
                    refund.setTotalFee(Integer.parseInt(total_fee));
                refund.setTransactionId(transaction_id);
                refund.setCreateDate(new Date());
                int result = wechatRefundResultMapper.insertSelective(refund);

                Logger.info(">>>>>resovleRefundResponse>>>>save result>>"+result);

                //if(WechatUtil.SUCCESS.equals(result_code))//更新订单状态
                    //iPayBusiness.execute(Integer.parseInt(proposal.getAttach()), proposal.getOutTradeNo(), IOrderStatusService.REFUNDED,null);//更新订单状态
            }else{
                errorCode = "1";
                errorInfo = "校验签名失败";
            }
        }else{
            errorCode = "1";
            errorInfo = return_msg;
        }
        resultJson.put("errorCode", errorCode);
        resultJson.put("errorInfo", errorInfo);
        return resultJson;
    }

    /**
     * String转Xml
     * @param returnMess
     * @return
     * @throws Exception
     */
    private Element getRootElementByString(String returnMess) throws Exception{
        InputStream inputStream = null;
        Element root = null;
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //解析返回xml报文
            DocumentBuilder builder = dbf.newDocumentBuilder();
            inputStream = new ByteArrayInputStream(returnMess.trim().getBytes("UTF-8"));
            Document doc = builder.parse(inputStream);
            root = doc.getDocumentElement(); // 获取根元素
        } finally{
            if(inputStream != null)
                inputStream.close();
        }
        return root;
    }

    /**
     * 获取微信支付配置文件信息
     * @return
     */
    @Override
    public WechatConfig getWechatConfig(){
        List<WechatConfig> configs = wechatConfigMapper.findAllWCs();
        WechatConfig wechatConfig = new WechatConfig();
        if(configs != null && configs.size() >0){
            wechatConfig = configs.get(0);
            Logger.info("获取微信支付配置信息");
        }else{
//			String appid = WechatUtil.APPID;  //微信appid
//			String appSecret = WechatUtil.APPSECRET;//微信secret
//	    	String mch_id = WechatUtil.MCH_ID;//商户号
//	    	String key = WechatUtil.KEY;//API密钥
//	    	String spbill_create_ip= WechatUtil.SPBILl_CREATE_IP;  //终端IP  订单生成的机器 IP （需修改）
//			String signType = WechatUtil.SIGN_TYPE;
//			//通知地址 （接收微信支付成功通知）
//			String notify_url = WechatUtil.NOTIFY_URL;
//	    	//统一下单地址
//	    	String unified_order_url = WechatUtil.UNIFIED_ORDER_URL;
//	    	//长链接转短链接地址
//	    	String long_short_url = WechatUtil.LONG_SHORT_URL;
//	    	//获取access_token地址
//	    	String get_access_token = WechatUtil.GET_ACCESS_TOKEN;
//	    	//微信支付结果查询地址
//	    	String order_query_url = WechatUtil.ORDER_QUERY_URL;
//
//	    	wechatConfig.setAppid(appid);
//	    	wechatConfig.setAppsecret(appSecret);
//	    	wechatConfig.setMchId(mch_id);
//	    	wechatConfig.setKey(key);
//	    	wechatConfig.setUnifiedOrderUrl(unified_order_url);
//	    	wechatConfig.setLongToShort(long_short_url);
//	    	wechatConfig.setGetTokenUrl(get_access_token);
//	    	wechatConfig.setSignType(signType);
//	    	wechatConfig.setSpbillCreateIp(spbill_create_ip);
//	    	wechatConfig.setNotifyUrl(notify_url);
//	    	wechatConfig.setOrderQueryUrl(order_query_url);
        }
        return wechatConfig;
    }

    /**
     * 生成签名
     * @param characterEncoding
     * @param parameters
     * @param key
     * @return
     * @author luwj
     *2015年7月9日下午7:31:39
     */
    @SuppressWarnings("rawtypes")
    private  String createSign(String characterEncoding,SortedMap<Object,Object> parameters , String key){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while(it.hasNext()) {  //遍历
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        if(StringUtils.isNotBlank(key)){
            sb.append("key=" + key);
        }
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();  //MD5加密
        return sign;
    }

    @Override
    public String disposeWechatPayAsyncNotifi(Document body) {
        StringBuffer str = new StringBuffer();
        String return_code = "SUCCESS";
        String return_msg = "OK";
        try {
            Element root = body.getDocumentElement(); // 获取根元素
            String returnCode = WechatUtil.resovleXml(root, "return_code");// 此字段是通信标识，非交易标识
            String returnMsg = WechatUtil.resovleXml(root, "return_msg");//返回信息
            if (WechatUtil.RETURN_CODE_SUCCESS.equals(returnCode)) {//returnCode为SUCCESS的时候有返回
                // 商户订单号
                String outTradeNo = WechatUtil.resovleXml(root, "out_trade_no");
                Logger.info(">>PayControl()>>>>>outTradeNo>>>>>"+outTradeNo);
                String appid = WechatUtil.resovleXml(root, "appid");// 微信appid
                String mchId = WechatUtil.resovleXml(root, "mch_id");// 商户号
                String deviceInfo = WechatUtil.resovleXml(root, "device_info");// 微信支付分配的终端设备号 (扫码:WEB)
                String nonceStr = WechatUtil.resovleXml(root, "nonce_str");// 随机窜
                String sign = WechatUtil.resovleXml(root, "sign");// 签名
                Logger.info(">>PayControl()>>>>>sign>>>>>"+sign);
                String resultCode = WechatUtil.resovleXml(root, "result_code");// 业务结果
                String errCode = WechatUtil.resovleXml(root, "err_code");//错误代码
                String errCodeDes = WechatUtil.resovleXml(root, "err_code_des");//错误代码描述
                String openid = WechatUtil.resovleXml(root, "openid");// 用户标识
                String isSubscribe = WechatUtil.resovleXml(root, "is_subscribe");// 是否关注公众账号
                String tradeType = WechatUtil.resovleXml(root, "trade_type");// 交易类型
                String bankype = WechatUtil.resovleXml(root, "bank_type");// 付款银行
                String totalFee = WechatUtil.resovleXml(root, "total_fee");// 订单总金额，单位为分
                String feeType = WechatUtil.resovleXml(root, "fee_type");// 货币种类
                String cashfee = WechatUtil.resovleXml(root, "cash_fee");// 现金支付金额
                String cashFeeType = WechatUtil.resovleXml(root, "cash_fee_type");//现金支付货币类型
                String couponFee = WechatUtil.resovleXml(root, "coupon_fee");//代金券或立减优惠金额
                String couponCount = WechatUtil.resovleXml(root, "coupon_count");//代金券或立减优惠使用数量
                String transactionId = WechatUtil.resovleXml(root, "transaction_id");// 微信支付订单号
                String attach = WechatUtil.resovleXml(root, "attach");// 商家数据包（原样返回）
                String timeEnd = WechatUtil.resovleXml(root, "time_end");// 支付完成时间
                //add by zbc 微信异步回调新增参数,解决签名错误bug
                String couponIdZero = WechatUtil.resovleXml(root, "coupon_id_0");
                String couponFeeZero =  WechatUtil.resovleXml(root, "coupon_fee_0");
                SortedMap<Object, Object> params = new TreeMap<Object, Object>();
                params.put("coupon_id_0", couponIdZero);
                params.put("coupon_fee_0", couponFeeZero);
                params.put("out_trade_no", outTradeNo);
                params.put("appid", appid);
                params.put("attach", attach);
                params.put("bank_type", bankype);
                params.put("cash_fee", cashfee);
                params.put("device_info", deviceInfo);
                params.put("fee_type", feeType);
                params.put("is_subscribe", isSubscribe);
                params.put("mch_id", mchId);
                params.put("nonce_str", nonceStr);
                params.put("openid", openid);
                params.put("result_code", resultCode);
                params.put("return_code", returnCode);
                params.put("return_msg", returnMsg);
                params.put("time_end", timeEnd);
                params.put("total_fee", totalFee);
                params.put("trade_type", tradeType);
                params.put("transaction_id", transactionId);
                params.put("err_code", errCode);
                params.put("err_code_des", errCodeDes);
                params.put("cash_fee_type", cashFeeType);
                params.put("coupon_fee", couponFee);
                params.put("coupon_count", couponCount);
                Logger.info(">>>>>params>>>>>>"+params.toString());
                String signCreated = this.createSign("utf-8", params, this.getWechatConfig().getKey());//生成签名
                Logger.info(">>PayControl()>>>>>signCreated>>>>>"+signCreated);
                // 换算成元为单位
                totalFee = new BigDecimal(totalFee).divide(new BigDecimal(100.00)).toString();
                if (!(signCreated.equals(sign))) {//校验签名
                    return_code = WechatUtil.RETURN_CODE_FAIL;
                    return_msg = "签名失败";
                } else {
                    WechatPayResult proposal = new WechatPayResult();
                    Map<String, Object> tempParam = Maps.newHashMap();
                    tempParam.put("outTradeNo", outTradeNo);
                    List<WechatPayResult> proposals = wechatPayResultMapper.findWPRsByMap(tempParam);
                    if (proposals != null && proposals.size() > 0) {
                        proposal = proposals.get(0);
                        if (proposal.getTradeState() == null ||
                                !(WechatUtil.TRADE_STATE_SUCCESS.equals(proposal.getTradeState()))) {// 未记录支付结果的订单
                            proposal.setAppid(appid);
                            proposal.setAttach(attach);
                            proposal.setBankype(bankype);
                            proposal.setCashFee(cashfee);
                            proposal.setDeviceInfo(deviceInfo);
                            proposal.setFeeType(feeType);
                            proposal.setIsSubscribe(isSubscribe);
                            proposal.setMchId(mchId);
                            proposal.setNonceStr(nonceStr);
                            proposal.setOpenid(openid);
                            proposal.setResultCode(resultCode);
                            proposal.setReturnCode(returnCode);
                            proposal.setSign(sign);
                            proposal.setTimeEnd(timeEnd);
                            proposal.setTotalFee(totalFee);
                            proposal.setTradeType(tradeType);
                            proposal.setTransactionId(transactionId);
                            proposal.setLastUpdateDate(new Date());
                            proposal.setTradeState(resultCode);
                            proposal.setTradeStateDesc("");
                            int upResult = wechatPayResultMapper.updateByPrimaryKeySelective(proposal);
                            Logger.info(">>>>>>upResult>>>>>>"+upResult);
                        }
                    } else {
                        proposal.setOutTradeNo(outTradeNo);
                        proposal.setAppid(appid);
                        proposal.setAttach(attach);
                        proposal.setBankype(bankype);
                        proposal.setCashFee(cashfee);
                        proposal.setDeviceInfo(deviceInfo);
                        proposal.setFeeType(feeType);
                        proposal.setIsSubscribe(isSubscribe);
                        proposal.setMchId(mchId);
                        proposal.setNonceStr(nonceStr);
                        proposal.setOpenid(openid);
                        proposal.setResultCode(resultCode);
                        proposal.setReturnCode(returnCode);
                        proposal.setSign(sign);
                        proposal.setTimeEnd(timeEnd);
                        proposal.setTotalFee(totalFee);
                        proposal.setTradeType(tradeType);
                        proposal.setTransactionId(transactionId);
                        proposal.setCreateDate(new Date());
                        proposal.setTradeState(resultCode);
                        int saveResult = wechatPayResultMapper.insertSelective(proposal);
                        Logger.info(">>>>>>saveResult>>>"+saveResult);
                    }
                }
                //change by zbc 微信异步回调bug，参数未插入就进行同步，导致数据不全而同步异常
                if (resultCode != null &&
                        WechatUtil.TRADE_STATE_SUCCESS.equals(resultCode)) {
                	String postflag = "";
                	if(inventoryService.isSaleOrder(outTradeNo)) {
                		postflag = "true";
                	}
                	String flag = getFlag(outTradeNo,postflag);
                	dealReturnFlag(flag, outTradeNo, null, totalFee, postflag, JsonNodeFactory.instance.objectNode());
                }
            } else {
                return_code = "FAIL";
                return_msg = "参数格式校验错误";
            }
        } catch (Exception e) {
            Logger.error("解析微信支付返回结果报文异常");
            e.printStackTrace();
            return_code = "FAIL";
            return_msg = "服务器异常";
        }
        str.append("<xml>");
        str.append("<return_code><![CDATA[" + return_code + "]]></return_code>");
        str.append("<return_msg><![CDATA[" + return_msg + "]]></return_msg>");
        str.append("</xml>");
        Logger.info("disposeWechatPayAsyncNotifi：return_xml--->" + str.toString());
        return str.toString();
    }

	private String getFlag(String outTradeNo, String postflag) {
		if (outTradeNo.toUpperCase().startsWith("CG") || "true".equals(postflag)) {
			return "success";
		}
		// add by duyt 记录显示支付成功，同步相关信息到在线充值记录
		if (outTradeNo.toUpperCase().startsWith("CZ")) {
			return "cz_success";
		}
		// 微信查询接口确认支付成功
		return "true";
	}

	@Override
	public void dealReturnFlag(String flag, String orderNo, String sid, String total,String postflag, ObjectNode result) {
		if ("false".equals(flag)) {
			result.put("msg", "该订单待支付或支付失败");
		} else if ("cz_success".equals(flag)) {// 在线充值标识
			// add by duyt
			// CZ开头的交易号意为“在线充值单单号”
			Logger.error("在线充值[微信]支付");
			WechatPayResult proposal = null;
			try {
				Map<String, Object> tempParam = Maps.newHashMap();
				tempParam.put("outTradeNo", orderNo);
				List<WechatPayResult> proposals = wechatPayResultMapper.findWPRsByMap(tempParam);
				if (proposals != null && proposals.size() > 0) {// 查询订单存在
					proposal = proposals.get(0);
					Logger.error("在线充值[微信]支付，回调前标识");
					inventoryService.onlinePaySuccessCallback(proposal.getOutTradeNo(), proposal.getTransactionId(), "weixin");
					
//					PayUtil.onlinePaySuccessCallback(proposal.getOutTradeNo(), proposal.getTransactionId(), "weixin");
				}
			} catch (Exception e) {
				if (proposal != null) {
					Logger.info("[weixin]在线充值订单异常：" + proposal.getOutTradeNo() + "，异常信息：" + e.getMessage());
				}
				e.printStackTrace();
			}

		} else if ("success".equals(flag)) {
			Logger.info("微信支付订单：" + orderNo);
			WechatPayResult proposal = null;
			try {
				Map<String, Object> tempParam = Maps.newHashMap();
				tempParam.put("outTradeNo", orderNo);
				List<WechatPayResult> proposals = wechatPayResultMapper.findWPRsByMap(tempParam);
				if (proposals != null && proposals.size() > 0) {// 查询订单存在
					proposal = proposals.get(0);
				}
				if ("true".equals(postflag)) {
					inventoryService.callback2(orderNo, sid, total,
							 new DateTime(DateUtils.parseDate(proposal.getTimeEnd(), "yyyyMMddHHmmss"))
								.toString("yyyy-MM-dd HH:mm:ss"), proposal.getTransactionId(), "wechatpay");
				} else {
					Map<String, String> payMap = Maps.newHashMap();
						payMap.put("tradeNo", proposal.getTransactionId());
						payMap.put("payDate", new DateTime(DateUtils.parseDate(proposal.getTimeEnd(), "yyyyMMddHHmmss"))
								.toString("yyyy-MM-dd HH:mm:ss"));
						payMap.put("payType", "wechatpay");
					inventoryService.callback(orderNo, total, "微信支付", payMap);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

}
