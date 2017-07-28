package services.payment.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dto.payment.ReturnMess;
import dto.payment.yijipay.YijiPayParamsDTO;
import dto.payment.yijipay.YijiPayTradeInfoDTO;
import dto.payment.yijipay.YijiPayUnionDTO;
import dto.payment.yijipay.YijiPayWechatDTO;
import entity.payment.enums.PayType;
import entity.payment.enums.YijifuMark;
import entity.payment.yijipay.*;
import mapper.payment.yijipay.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import services.base.utils.JsonFormatUtils;
import services.payment.IYijipayService;
import utils.payment.*;
import vo.payment.YijiResultVO;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by LSL on 2016/4/14.
 */
public class YijipayService implements IYijipayService {

    @Inject
    private YijiConfigMapper yijiConfigMapper;

    @Inject
    private YijiOrderMapper yijiOrderMapper;

    @Inject
    private YijiResultMapper yijiResultMapper;

    @Inject
    MergeResultMapper mergeResultMapper;

    @Inject
    CheckedRealNameMapper checkedRealNameMapper;

    @Override
    public YijiConfig getYijiConfig(String mark) {
        Logger.debug("==========获取易极付支付配置信息==========");
        return yijiConfigMapper.getYijiConfig(mark);
    }

    @Override
    public int insertYijiOrderSelective(YijiOrder record) {
        return yijiOrderMapper.insertSelective(record);
    }

    @Override
    public YijiOrder getYijiOrderByCondition(YijiOrder record) {
        return yijiOrderMapper.getYijiOrderByCondition(record);
    }

    @Override
    public int insertYijiResultSelective(YijiResult record) {
        return yijiResultMapper.insertSelective(record);
    }

    @Override
    public int updateYijiResultSelective(YijiResult record) {
        return yijiResultMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public YijiResult getYijiResultByCondition(YijiResult record) {
        return yijiResultMapper.getYijiResultByCondition(record);
    }


    /**
     * 合并支付
     *
     * @return
     */
    public String unionCashierWebPay(JsonNode node) {
        YijiPayUnionDTO dto = new YijiPayUnionDTO();
        ReturnMess returnMess = new ReturnMess("0", "");
        try {
            if (node.isNull()) {
                returnMess = new ReturnMess("1", "请选择您要支付的销售订单!");
            } else {
                String id = node.get("id").asText();
                String outOrderNo = node.get("outOrderNo").asText();
                if(!checkOrderer(id)){
                    returnMess = new ReturnMess("1", "订单" + outOrderNo + "的订购人身份信息校验未通过");
                }else {
                    String tradeAmount = node.get("tradeAmount").asText();
                    String userTerminalType = node.get("userTerminalType").asText();
                    String goodsName = node.get("goodsName").asText();
                    String paymentType = node.get("paymentType").asText();
                    YijiConfig config = getYijiConfig(YijifuMark.yjf_payee.name());
                    YijiConfig payer = getYijiConfig(YijifuMark.yjf_payer.name());
                    String notifyUrl = config.getNotifyUrl();
                    String returnUrl = config.getReturnUrl();
                    String orderNo = PayUtil.buildOrderNo();
                    String service = config.getTradeService();
                    String version = config.getVersion();
                    String partnerId = config.getPartnerId();
                    String signType = config.getSignType();
                    String key = config.getSecretKey();

                    List<YijiPayTradeInfoDTO> lists = Lists.newArrayList();
                    YijiPayTradeInfoDTO tradeInfo = new YijiPayTradeInfoDTO();
                    tradeInfo.setMerchOrderNo(outOrderNo);
                    tradeInfo.setSellerUserId(partnerId);
                    tradeInfo.setTradeAmount(tradeAmount);
                    tradeInfo.setCurrency("CNY");
                    tradeInfo.setGoodsName(goodsName);
                    lists.add(tradeInfo);

                    Map<String, String> iterm = Maps.newHashMap();
                    iterm.put("orderNo", orderNo);
                    iterm.put("service", service);
                    iterm.put("version", version);
                    iterm.put("partnerId", partnerId);
                    iterm.put("signType", signType);
                    iterm.put("merchOrderNo", outOrderNo);
                    iterm.put("notifyUrl", notifyUrl);
                    iterm.put("returnUrl", returnUrl);
                    iterm.put("buyerUserId", payer.getPartnerId());
                    iterm.put("tradeInfo", Json.toJson(lists).toString());
                    iterm.put("paymentType", paymentType);
                    iterm.put("userTerminalType", userTerminalType);
                    Logger.debug(">>>>>iterm>>" + Json.toJson(iterm).toString());

                    YjfUtil yjf = new YjfUtil(config.getYijiUrl(), key);
                    String signString = yjf.signString(iterm);
                    String sign = YjfUtil.MD5(signString);
                    Logger.debug(">>>sign>>" + sign);

                    dto.setActionUrl(config.getYijiUrl());
                    dto.setOrderNo(orderNo);
                    dto.setService(service);
                    dto.setVersion(version);
                    dto.setPartnerId(partnerId);
                    dto.setSignType(signType);
                    dto.setMerchOrderNo(outOrderNo);
                    dto.setNotifyUrl(notifyUrl);
                    dto.setReturnUrl(returnUrl);
                    dto.setBuyerUserId(payer.getPartnerId());
                    dto.setTradeInfo(lists);
                    dto.setPaymentType(paymentType);
                    dto.setUserTerminalType(userTerminalType);
                    dto.setSign(sign);
                }
            }
        } catch (Exception e) {
            Logger.error(">>>>unionCashierWebPay>>Exception>>>",e);
            returnMess = new ReturnMess("1", "系统异常");
        }
        dto.setReturnMess(returnMess);
        Logger.debug(">>>dto>>>>" + Json.toJson(dto).toString());
        return Json.toJson(dto).toString();
    }

    /**
     * 校验订购人信息:
     * 1.调用本地数据表查询已经认证通过的实名信息
     * 2.1未查询到再调用易极付实名查询接口
     * @param id : 订单id
     * @return : boolean
     */
    private boolean checkOrderer(String id){
        boolean isOk = false;
        Map header = Maps.newHashMap();
        header.put("token", HttpUtil.getToken());
        header.put("content-type", "application/json;charset=utf-8");
        String url = HttpUtil.getHostUrl() + "/sales/getBase";
        String response = HttpUtil.httpsPost("{\"orderId\":" + id + "}", header, url);
        if (!Strings.isNullOrEmpty(response)) {
            JsonNode realJson = Json.parse(response);
            String realName = realJson.get("orderer").isNull() ? null : realJson.get("orderer").asText();
            String certNo = realJson.get("ordererIDCard").isNull() ? null : realJson.get("ordererIDCard").asText();
            Logger.debug(realName + "<-----realName    certNo----->" + certNo);
            if (!Strings.isNullOrEmpty(realName) && !Strings.isNullOrEmpty(certNo)) {
                ObjectNode temp = Json.newObject();
                temp.put("realName", realName);
                temp.put("certNo", certNo);
                Map map = JsonFormatUtils.jsonToBean(temp.toString(), Map.class);
                map.put("idCard", certNo);
                CheckedRealName checked = checkedRealNameMapper.getByNameIdCard(map);
                if(checked != null){
                    isOk = true;
                }else {
                    JsonNode json = Json.parse(this.realNameQuery(temp));
                    if ("0".equals(json.get("errorCode").asText())) {//订购人身份信息校验通过
                        isOk = true;
                    }
                }
            }
        }
        return isOk;
    }

    /**
     * 易极付-微信扫码支付
     *
     * @return
     */
    public String yjfWxTrade(JsonNode node) {
        ObjectNode result = Json.newObject();
        YijiPayWechatDTO dto = new YijiPayWechatDTO();
        boolean suc = true;
        try {
            if (node.isNull()) {
                suc = false;
                result.put("info", "请选择您要支付的订单");
            } else {
                YijiConfig config = getYijiConfig(YijifuMark.yjf_wx.name());
                String service = config.getTradeService(); //服务代码 必
                String version = config.getVersion(); //服务版本 非
                String partnerId = config.getPartnerId(); //商户ID 必
                String outOrderNo = node.get("outOrderNo").asText(); //外部订单号 必
                String orderNo = IDUtils.buildOrderNo("YJF_WX_"); //请求订单号 必
                String signType = config.getSignType(); //签名方式 必
                //TODO:金额从前端页面传输，未校验
                String tradeAmount = deleteZero(node.get("tradeAmount").asText()); //交易额 必
                String currency = "CNY"; //币种 非
                String uiStyle = "PC_NORMAL"; //页面风格 PC_NORMAL:PC标准版 ZBJ:猪八戒版 MOBILE_WEB:手机web版
                String returnUrl = config.getReturnUrl(); //页面跳转返回URL 非
                String notifyUrl = config.getNotifyUrl(); //异步通知URL 非
                //商品条款 必
                String goodsClauses = node.get("orderDetail").toString();
                Map<String, String> params = Maps.newHashMap();
                params.put("protocol", config.getPostProtocol());
                params.put("service", service);
                params.put("version", version);
                params.put("partnerId", partnerId);
                params.put("orderNo", orderNo);
                params.put("signType", signType);
                params.put("outOrderNo", outOrderNo);
                params.put("sellerUserId", partnerId);//卖家Id 必
                params.put("tradeAmount", tradeAmount);
                params.put("uiStyle", uiStyle);
                params.put("goodsClauses", goodsClauses);
                params.put("currency", currency);
                params.put("returnUrl", returnUrl);
                params.put("notifyUrl", notifyUrl);

                YjfUtil util = new YjfUtil(config.getYijiUrl(), config.getSecretKey());
                String prepareSign = util.signString(params);//生成待签名字符串
                String sign = YjfUtil.MD5(prepareSign);

                dto.setGoodsClauses(goodsClauses);
                dto.setOrderNo(orderNo);
                dto.setOutOrderNo(outOrderNo);
                dto.setPartnerId(partnerId);
                dto.setSellerUserId(partnerId);
                dto.setProtocol(config.getPostProtocol());
                dto.setService(service);
                dto.setSign(sign);
                dto.setSignType(signType);
                dto.setTradeAmount(tradeAmount);
                dto.setUiStyle(uiStyle);
                dto.setActionUrl(config.getYijiUrl());
                dto.setVersion(version);
                dto.setCurrency(currency);
                dto.setReturnUrl(returnUrl);
                dto.setNotifyUrl(notifyUrl);

                YijiOrder yo = new YijiOrder();
                yo.setOrderNo(orderNo);
                yo.setOutOrderId(node.get("id").asText());
                yo.setOutOrderNo(outOrderNo);
                yo.setTotalFee(tradeAmount);
                yo.setCurrency(currency);
                yo.setCreateDate(new Date());
                this.insertYijiOrderSelective(yo);
                result.set("info", Json.toJson(dto));
            }
        } catch (Exception e) {
            suc = false;
            result.put("info", "系统异常");
            e.printStackTrace();
        }
        result.put("suc", suc);
        Logger.debug(">>yjfWxTrade>>>result>>>>" + Json.toJson(result).toString());
        return result.toString();
    }


    @Override
    public ObjectNode getYijiPayParam(JsonNode params) {
        ObjectNode result = Json.newObject();
        YijiConfig config = this.getYijiConfig(YijifuMark.pay.name());
        String id = params.get("id").asText();
        String outOrderNo = params.get("outOrderNo").asText();
        String tradeAmount = params.get("tradeAmount").asText();
        String userTerminalType = params.get("userTerminalType").asText();
        String goodsName = params.get("goodsName").asText();
        String paymentType = params.get("paymentType").asText();
        //校验订购人身份信息
        if (checkOrderer(id)) {//订购人身份信息校验通过
            String orderNo = PayUtil.buildOrderNo();//支付请求号
            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put("orderNo", orderNo);
            paramMap.put("merchOrderNo", outOrderNo);
            paramMap.put("service", config.getSinglePayService());
            paramMap.put("version", config.getSinglePayVersion());
            paramMap.put("partnerId", config.getPartnerId());
            paramMap.put("signType", config.getSignType());
            paramMap.put("notifyUrl", config.getNotifyUrl());
            paramMap.put("returnUrl", config.getReturnUrl());
            paramMap.put("sellerUserId", config.getPartnerId());
            paramMap.put("tradeAmount", tradeAmount);
            paramMap.put("userTerminalType", userTerminalType);
            paramMap.put("goodsName", goodsName);
            paramMap.put("paymentType", paymentType);
            Logger.debug("gainYijiPayParam    paramMap--->" + paramMap.toString());

            YjfUtil util = new YjfUtil(config.getYijiUrl(), config.getSecretKey());
            String prepareSign = util.signString(paramMap);//生成待签名字符串
            Logger.debug("getYijiPayParam    prepareSign--->" + prepareSign);
            String sign = YjfUtil.MD5(prepareSign);//签名
            Logger.debug("getYijiPayParam    sign--->" + sign);

            YijiPayParamsDTO dto = new YijiPayParamsDTO();
            dto.setActionUrl(config.getYijiUrl());
            dto.setOrderNo(orderNo);
            dto.setMerchOrderNo(outOrderNo);
            dto.setService(config.getSinglePayService());
            dto.setVersion(config.getSinglePayVersion());
            dto.setPartnerId(config.getPartnerId());
            dto.setSignType(config.getSignType());
            dto.setSign(sign);
            dto.setNotifyUrl(config.getNotifyUrl());
            dto.setReturnUrl(config.getReturnUrl());
            dto.setUserTerminalType(userTerminalType);
            dto.setGoodsName(goodsName);
            dto.setSellerUserId(config.getPartnerId());
            dto.setTradeAmount(Double.valueOf(tradeAmount));
            dto.setPaymentType(paymentType);
            dto.setMemo(outOrderNo);
            Logger.debug("getYijiPayParam    dto--->" + Json.toJson(dto).toString());

            YijiOrder yo = new YijiOrder();
            yo.setOrderNo(orderNo);
            yo.setOutOrderId(id);
            yo.setOutOrderNo(outOrderNo);
            yo.setTotalFee(tradeAmount);
            yo.setCurrency("CNY");
            yo.setCreateDate(new Date());
            int line = this.insertYijiOrderSelective(yo);
            Logger.debug("getYijiPayParam    insertYijiOrderSelective  line--->" + line);
            result.put("suc", true);
            result.set("info", Json.toJson(dto));
        } else {
            //订购人身份信息校验未通过
            result.put("suc", false);
            result.put("msg", "订单" + outOrderNo + "的订购人身份信息校验未通过");
        }
        return result;
    }

    @Override
    public void receiveSyncReturn(Map<String, String[]> params, ObjectNode result) {
        result.put("isok", false);
        try {
            Map map = signIsOk(params);
            if ((Boolean) map.get("signIsOk")) {//签名校验通过
                Map<String, String> paramMap = (Map<String, String>)map.get("paramMap");
                String orderNo = paramMap.get("orderNo");
                String creatResult = paramMap.get("creatResult");//合并支付结果
                if (paramMap.containsKey("creatResult")) {//合并回执
                    MergeResult merge = new MergeResult();
                    merge.setOrderNo(orderNo);
                    merge = mergeResultMapper.getResult(merge);
                    MergeResult mergeResult = Json.fromJson(Json.toJson(paramMap), MergeResult.class);
                    mergeResult.setCreateDate(new Date());
                    if (merge == null) {
                        mergeResultMapper.saveResult(mergeResult);
                    } else if (!"EXECUTE_SUCCESS".equals(merge.getMergePayResult())) {
                        mergeResult.setId(merge.getId());
                        mergeResult.setOrderNo(merge.getOrderNo());
                        mergeResultMapper.upResult(mergeResult);
                    }
                    ObjectMapper obj = new ObjectMapper();
                    ArrayNode node = (ArrayNode)obj.readTree(paramMap.get("creatTradeResult"));
                    Logger.debug(">>>>creatTradeResult>>>>"+node.toString());
                    for(int i = 0;i<node.size();i++){
                        paramMap.put("tradeNo", node.get(i).get("tradeNo").asText());
                        paramMap.put("tradeStatus", node.get(i).get("creatResult").asText());
                        paramMap.put("outOrderNo", node.get(i).get("merchOrderNo").asText());
                        paramMap.put("tradeAmount", node.get(i).get("tradeAmount").asText());
                        paramMap.remove("creatResult");
                        paramMap.remove("creatTradeResult");
                        Logger.debug(">>>>>paramMap>>>>"+paramMap.toString());
                        this.dealResultInformation(paramMap);
                    }
                    //用于结果页面
                    if ("EXECUTE_SUCCESS".equals(paramMap.get("resultCode"))
                        && "FINISHED".equals(creatResult)) {
                        result.put("isok", true);
                    }
                } else {
                    //单个支付同步返回
                    YijiOrder yo = new YijiOrder();
                    yo.setOrderNo(orderNo);
                    yo = this.getYijiOrderByCondition(yo);
                    if (yo != null) {
                        paramMap.put("tradeNo", paramMap.get("bizNo"));
                        paramMap.put("tradeStatus", paramMap.get("fastPayStatus"));
                        paramMap.put("outOrderNo", yo.getOutOrderNo());
                        this.dealResultInformation(paramMap);
                    } else {
                        Logger.debug("receiveSyncReturn    没有该支付请求号\"" + orderNo + "\"的下单记录");
                    }
                    //用于结果页面
                    if ("EXECUTE_SUCCESS".equals(paramMap.get("resultCode"))
                        && "FINISHED".equals(paramMap.get("fastPayStatus"))) {
                        result.put("isok", true);
                    }
                }
            } else {
                Logger.debug("receiveSyncReturn    参数签名校验未通过");
            }
        } catch (Exception e) {
            Logger.error("同步  Exception : " + e);
            e.printStackTrace();
        }
    }

    /**
     * 验证同步返回签名
     * @param params : 返回参数
     * @return : Map
     */
    private Map signIsOk(Map<String, String[]> params){
        Map map = Maps.newHashMap();
        Map<String, String> paramMap = Maps.newHashMap();
        String success = params.containsKey("success") ? params.get("success")[0] : null;
        String orderNo = params.containsKey("orderNo") ? params.get("orderNo")[0] : null;
        String service = params.containsKey("service") ? params.get("service")[0] : null;
        String protocol = params.containsKey("protocol") ? params.get("protocol")[0] : null;
        String version = params.containsKey("version") ? params.get("version")[0] : null;
        String partnerId = params.containsKey("partnerId") ? params.get("partnerId")[0] : null;
        String sign = params.containsKey("sign") ? params.get("sign")[0] : null;
        String signType = params.containsKey("signType") ? params.get("signType")[0] : null;
        String merchOrderNo = params.containsKey("merchOrderNo") ? params.get("merchOrderNo")[0] : null;
        String context = params.containsKey("context") ? params.get("context")[0] : null;
        String resultCode = params.containsKey("resultCode") ? params.get("resultCode")[0] : null;
        String resultMessage = params.containsKey("resultMessage") ? params.get("resultMessage")[0] : null;
        String notifyUrl = params.containsKey("notifyUrl") ? params.get("notifyUrl")[0] : null;
        String returnUrl = params.containsKey("returnUrl") ? params.get("returnUrl")[0] : null;
        String tradeAmount = params.containsKey("tradeAmount") ? params.get("tradeAmount")[0] : null;
        String creatTradeResult = params.containsKey("creatTradeResult") ? params.get("creatTradeResult")[0] : null;
        String notifyTime = params.containsKey("notifyTime") ? params.get("notifyTime")[0] : null;
        YijiConfig config;
        if (params.containsKey("creatResult")) {//合并付款同步返回
            config = this.getYijiConfig(YijifuMark.yjf_payee.name());
            String tradeNo = params.containsKey("tradeNo") ? params.get("tradeNo")[0] : null;
            String creatResult = params.containsKey("creatResult") ? params.get("creatResult")[0] : null;
            paramMap.put("tradeNo", tradeNo);
            paramMap.put("creatResult", creatResult);
        } else {
            config = this.getYijiConfig(YijifuMark.pay.name());
            String fastPayStatus = params.containsKey("fastPayStatus") ? params.get("fastPayStatus")[0] : null;
            String bizNo = params.containsKey("bizNo") ? params.get("bizNo")[0] : null;
            String merchantOrderNo = params.containsKey("merchantOrderNo") ? params.get("merchantOrderNo")[0] : null;
            paramMap.put("fastPayStatus", fastPayStatus);
            paramMap.put("bizNo", bizNo);
            paramMap.put("merchantOrderNo", merchantOrderNo);
        }
        paramMap.put("success", success);
        paramMap.put("orderNo", orderNo);
        paramMap.put("protocol", protocol);
        paramMap.put("service", service);
        paramMap.put("version", version);
        paramMap.put("partnerId", partnerId);
        paramMap.put("signType", signType);
        paramMap.put("merchOrderNo", merchOrderNo);
        paramMap.put("context", context);
        paramMap.put("resultCode", resultCode);
        paramMap.put("resultMessage", resultMessage);
        paramMap.put("notifyUrl", notifyUrl);
        paramMap.put("returnUrl", returnUrl);
        paramMap.put("tradeAmount", tradeAmount);
        paramMap.put("creatTradeResult", creatTradeResult);
        paramMap.put("notifyTime", notifyTime);

        YjfUtil util = new YjfUtil(config.getYijiUrl(), config.getSecretKey());
        String prepareSign = util.signString(paramMap);//生成待签名字符串
        Logger.debug("receiveSyncReturn    prepareSign--->" + prepareSign);
        String toBeVerifiedSign = YjfUtil.MD5(prepareSign);//待验证签名
        Logger.debug("receiveSyncReturn    toBeVerifiedSign--->" + toBeVerifiedSign);

        if (StringUtils.isNotEmpty(sign) && sign.equals(toBeVerifiedSign)) {//签名校验通过
            paramMap.put("sign", sign);
            map.put("signIsOk", true);
        }else {
            map.put("signIsOk", false);
        }
        map.put("paramMap", paramMap);
        return map;
    }

    @Override
    public void receiveAsynNotify(Map<String, String> params, ObjectNode result) {
        String success = params.get("success");
        String orderNo = params.get("orderNo");
        String protocol = params.get("protocol");
        String service = params.get("service");
        String version = params.get("version");
        String partnerId = params.get("partnerId");
        String sign = params.get("sign");
        String signType = params.get("signType");
        String merchOrderNo = params.get("merchOrderNo");
        String context = params.get("context");
        String resultCode = params.get("resultCode");
        String resultMessage = params.get("resultMessage");
        String notifyTime = params.get("notifyTime");
        String returnUrl = params.get("returnUrl");
        String creatTradeResult = params.get("creatTradeResult");
        String tradeAmount = params.get("tradeAmount");
        String fastPayStatus = params.get("fastPayStatus");
        String bizNo = params.get("fastPayStatus");
        String notifyUrl = params.get("notifyUrl");
        String merchantOrderNo = params.get("merchantOrderNo");
        String tradeNo = params.get("tradeNo");
        String outOrderNo = params.get("outOrderNo");
        String tradeType = params.get("tradeType");
        String accountDay = params.get("accountDay");

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("success", success);
        paramMap.put("protocol", protocol);
        paramMap.put("service", service);
        paramMap.put("version", version);
        paramMap.put("notifyTime", notifyTime);
        paramMap.put("signType", signType);
        paramMap.put("resultCode", resultCode);
        paramMap.put("resultMessage", resultMessage);
        paramMap.put("tradeNo", tradeNo);
        paramMap.put("tradeType", tradeType);
        paramMap.put("accountDay", accountDay);
        paramMap.put("orderNo", orderNo);
        paramMap.put("outOrderNo", outOrderNo);
        paramMap.put("partnerId", partnerId);
        paramMap.put("tradeAmount", tradeAmount);
        paramMap.put("merchOrderNo", merchOrderNo);
        paramMap.put("context", context);
        paramMap.put("returnUrl", returnUrl);
        paramMap.put("creatTradeResult", creatTradeResult);
        paramMap.put("fastPayStatus", fastPayStatus);
        paramMap.put("bizNo", bizNo);
        paramMap.put("notifyUrl", notifyUrl);
        paramMap.put("merchantOrderNo", merchantOrderNo);
        if (params.containsKey("merchOrderNo"))
            paramMap.put("merchOrderNo", params.get("merchOrderNo"));

        YijiConfig config = this.getYijiConfig(YijifuMark.pay.name());
        YjfUtil util = new YjfUtil(config.getYijiUrl(), config.getSecretKey());
        String prepareSign = util.signString(paramMap);//生成待签名字符串
        Logger.debug("receiveAsynNotify    prepareSign--->" + prepareSign);
        String toBeVerifiedSign = YjfUtil.MD5(prepareSign);//待验证签名
        Logger.debug("receiveAsynNotify    toBeVerifiedSign--->" + toBeVerifiedSign);

        if (sign.equals(toBeVerifiedSign)) {
            //签名校验通过
            paramMap.put("sign", sign);
            //支付接口调用成功
            this.dealResultInformation(paramMap);
        } else {
            Logger.debug("receiveAsynNotify    参数签名校验未通过");
        }
        result.put("suc", true);
    }

    /**
     * 处理支付结果信息
     */
    private void dealResultInformation(Map<String, String> paramMap) {
        String outOrderNo = paramMap.get("merchOrderNo");
        String resultCode = paramMap.get("resultCode");
        String tradeStatus = paramMap.get("tradeStatus");
        String tradeNo = paramMap.get("tradeNo");
        YijiResult yr = new YijiResult();
        yr.setOutOrderNo(outOrderNo);
        yr = this.getYijiResultByCondition(yr);
        if (yr != null) {
            //已有结果记录
            if ("EXECUTE_SUCCESS".equals(yr.getResultCode()) &&
                    (Strings.isNullOrEmpty(yr.getTradeStatus()) || "FINISHED".equals(tradeStatus))) {
                //结果记录显示支付成功，不做处理
                Logger.debug("dealResultInformation    订单\"" + outOrderNo + "\"支付成功的结果记录已存在");
            } else {
                //结果记录显示未支付成功，更新结果记录
                YijiResultVO vo = Json.fromJson(Json.toJson(paramMap), YijiResultVO.class);
                YijiResult yjResult = new YijiResult();
                BeanUtils.copyProperties(vo, yjResult);
                yjResult.setId(yr.getId());
                int line = this.updateYijiResultSelective(yjResult);
                Logger.debug("dealResultInformation    updateYijiResultSelective  line--->" + line);
            }
        } else {
            //没有结果记录
            YijiResultVO vo = Json.fromJson(Json.toJson(paramMap), YijiResultVO.class);
            YijiResult yjResult = new YijiResult();
            BeanUtils.copyProperties(vo, yjResult);
            yjResult.setCreateDate(new Date());
            int line = this.insertYijiResultSelective(yjResult);
            Logger.debug("dealResultInformation    insertYijiResultSelective  line--->" + line);
        }
        if ("EXECUTE_SUCCESS".equals(resultCode) && "FINISHED".equals(tradeStatus)) {
            //获取订购人信息
            String str = getSalesOrderer(outOrderNo);
            String payer = "";
            String payIdCard = "";
            if (StringUtils.isNotBlank(str)) {
                JsonNode node = Json.parse(str);
                payer = node.get("orderer") == null ? "" : node.get("orderer").asText();
                payIdCard = node.get("orderer_idcard") == null ? "" : node.get("orderer_idcard").asText();
            }
            Logger.debug(">>>payer>" + payer + ">>>payIdCard>>" + payIdCard);
            String payType = PayType.yijifu.name();

            //交易完成(支付成功)，更改订单状态
            PayUtil.syncPayInfoToSaleOrder(outOrderNo,
                    new DateTime().toString("yyyy-MM-dd HH:mm:ss"),
                    tradeNo, payType, "CNY", "6", payer, payIdCard);
        } else {
            //交易处理中或交易出错
            Logger.debug("dealResultInformation    交易处理中或交易出错");
        }
    }


    /**
     * 获取销售订单订购人信息
     * {
     * "orderer_tel":"13542424211",
     * "orderer_idcard":"445221198210024212",
     * "orderer":"dfsf",
     * "sales_order_no":"XS2016041317533100000029"
     * }
     *
     * @return
     */
    public String getSalesOrderer(String outOrderNo) {
        String returnStr = "";
        try {
            String url = "/sales/getOrderer?salesOrderNo=" + outOrderNo;
            url = HttpUtil.getHostUrl() + url;
            String token = HttpUtil.getToken();
            Map header = Maps.newHashMap();
            header.put("token", token);
            header.put("content-type", "application/json;charset=utf-8");
            returnStr = HttpUtil.httpsGet(header, url);
            Logger.debug(">getSalesOrderer>>>returnStr>>>>" + returnStr);
        } catch (Exception e) {
            Logger.debug(">>getSalesOrderer>>Exception>",e);
            e.printStackTrace();
        }
        return returnStr;
    }

    /**
     * 实名查询
     *
     * @return
     */
    public String realNameQuery(JsonNode node) {
        ReturnMess returnMess = new ReturnMess();
        boolean isOk = false;
        try {
            YijiConfig config = getYijiConfig(YijifuMark.realname.name());
            String service = config.getTradeService().trim();
            String version = config.getVersion().trim();
            String partnerId = config.getPartnerId().trim();
            String orderNo = IDUtils.buildOrderNo("RL_");
            String signType = config.getSignType().trim();
            String realName = "";
            String certNo = "";
            if (node.get("realName") != null && node.get("certNo") != null) {
                realName = node.get("realName").asText();
                certNo = node.get("certNo").asText();
            }
            String key = config.getSecretKey();
            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put("service", service);
            paramMap.put("version", version);
            paramMap.put("partnerId", partnerId);
            paramMap.put("orderNo", orderNo);
            paramMap.put("signType", signType);
            paramMap.put("realName", realName);
            paramMap.put("certNo", certNo);
            paramMap.put("protocol", config.getPostProtocol().trim());
            Logger.debug(">>>>>paramMap>>>>" + paramMap.toString());
            YjfUtil yjfUtil = new YjfUtil(config.getYijiUrl(), key);
            String toSign = yjfUtil.signString(paramMap);
            String sign = YjfUtil.MD5(toSign);//待验证签名
            paramMap.put("sign", sign);
            String resStr = yjfUtil.postString(paramMap); //<=================================提交from表单
            Logger.debug(">>realNameQuery>>>resStr>>>" + resStr);
            isOk = resoveRealRes(resStr);
            if (isOk) {
                returnMess = new ReturnMess("0", partnerId);
                //保存已经校验的实名信息
                saveRealName(realName, certNo);
            }else {
                returnMess = new ReturnMess("1", "实名校验失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.debug(">>Exception>>" + e);
            returnMess = new ReturnMess("1", "Exception:系统异常");
        }
        Logger.debug(">>realNameQuery>>returnMess>>" + Json.toJson(returnMess).toString());
        return Json.toJson(returnMess).toString();
    }

    /**
     * 保存已经校验的实名信息
     * @param realName : 姓名
     * @param idCard : 身份证号
     */
    private void saveRealName(String realName, String idCard){
        Map map = Maps.newHashMap();
        map.put("realName", realName);
        map.put("idCard", idCard);
        CheckedRealName checked = checkedRealNameMapper.getByNameIdCard(map);
        if(checked == null) {
            checked = new CheckedRealName();
            checked.setRealName(realName);
            checked.setIdCard(idCard);
            checked.setCreateDate(new Date());
            int line = checkedRealNameMapper.save(checked);
            Logger.info(">>>realNameQuery>>>save real name>>" + line);
        }
    }

    /**
     * 解析实名查询回执
     *
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean resoveRealRes(String resStr) {
        boolean isOk = false;
        ObjectMapper obj = new ObjectMapper();
        try {
            YijiConfig config = getYijiConfig(YijifuMark.realname.name());
            Map map = obj.readValue(resStr, Map.class);
            if (map.get("success") instanceof Boolean) {
                if ((Boolean) map.get("success"))
                    map.put("success", "true");
                else
                    map.put("success", "false");
            }
            YjfUtil yjfUtil = new YjfUtil(config.getYijiUrl(), config.getSecretKey());
            String toSign = yjfUtil.signString(map);
            Logger.debug(">>>>resoveRealRes>>>>toSign>>" + toSign);
            String sign = YjfUtil.MD5(toSign);//待验证签名
            Logger.debug(">>>>resoveRealRes>>>>sign>>" + sign);
            if (sign.equals(map.get("sign").toString())) {
                if (map.containsKey("resultCode") && "EXECUTE_SUCCESS".equals(map.get("resultCode").toString())) {
                    if (!map.containsKey("realNameQueryResult") || (map.containsKey("realNameQueryResult") &&
                            "pass".equals(map.get("realNameQueryResult").toString()))) {
                        isOk = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.debug(">Exception>" + e);
        }
        return isOk;
    }

    public static void main(String[] args) {
        boolean isOk = false;
        try {
//            String protocol = "httpPost";
//            String service = "realNameQuery";
//            String version = "1.0";
//            String partnerId = "20160427020011206988";
////            String partnerId = "20160427020011207606";
//            String orderNo = "RL_20160804142413000491315";
//            String signType = "MD5";
//            String realName = "咎召云";
//            String certNo = "310104199408133136";
//            String key = "ac230fc0132c1fc1f2d993296be4f5b0";
////            String key = "4f864e41d0c2dd937bcca6d168f07f92";
//            Map<String, String> paramMap = Maps.newHashMap();
//            paramMap.put("service", service);
//            paramMap.put("version", version);
//            paramMap.put("partnerId", partnerId);
//            paramMap.put("orderNo", orderNo);
//            paramMap.put("signType", signType);
//            paramMap.put("realName", realName);
//            paramMap.put("certNo", certNo);
//            paramMap.put("protocol", protocol);
//            YjfUtil yjfUtil = new YjfUtil("https://api.yiji.com/gateway.html", key);
//            String toSign = yjfUtil.signString(paramMap);
////            String toSign = "notifyTime=2016-08-04 15:34:22&notifyUrl=http://tomtopx.com.cn/payment/yijipay/asynnotify&orderNo=YJF_WX_20160804153335000990656&partnerId=20160427020011207606&protocol=httpGet&resultCode=EXECUTE_SUCCESS&resultMessage=成功&returnUrl=http://tomtopx.com.cn/payment/yijipay/syncnotify&service=commonWchatTradeRedirect&signType=MD5&success=true&tradeNo=000g00401g48gsh2xk06&tradeStatus=trade_finished&version=1.0c9cef22553af973d4b04a012f9cb8ea8";
//            String sign = yjfUtil.MD5(toSign);//待验证签名
//            System.out.println(">>>>>paramMap>>>>"+paramMap.toString());
//            System.out.println(">>realNameQuery>>>sign>>>" + sign);
//            paramMap.put("sign", sign);
//            String resStr = yjfUtil.postString(paramMap);
//            System.out.println(">>realNameQuery>>>resStr>>>" + resStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(">>>isOk>>>>>" + isOk);
    }

    @Override
    public ObjectNode purchaseParam(JsonNode params) {
        Logger.debug(">>>>>params>>" + params.toString());
        ObjectNode result = Json.newObject();
        YijiConfig config = this.getYijiConfig(YijifuMark.pay.name());
        String id = params.get("id").asText();
        String outOrderNo = params.get("outOrderNo").asText();
        String tradeAmount = deleteZero(params.get("tradeAmount").asText());
        String userTerminalType = params.get("userTerminalType").asText();
        String goodsName = params.get("goodsName").asText();
        String paymentType = params.get("paymentType").asText();
        String flag = params.has("postflag") ? params.get("postflag").asText() : "";
        String service = config.getSinglePayService();
        String version = config.getSinglePayVersion();
        if (params.has("service")) {
            version = "1.0";
            service = params.get("service").asText();
        }
        String url = HttpUtil.getHostUrl() + "/payment/callback?no=" + outOrderNo + "&amount=" + tradeAmount;
        if ("true".equals(flag)) {
            url += "&flag=" + flag + "&sid=" + id;
        }
        // 支付请求号
        String orderNo = PayUtil.buildOrderNo();
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("orderNo", orderNo);
        paramMap.put("merchOrderNo", outOrderNo);
        paramMap.put("service", service);
        paramMap.put("version", version);
        paramMap.put("partnerId", config.getPartnerId());
        paramMap.put("signType", config.getSignType());
        paramMap.put("notifyUrl", url);//
        paramMap.put("returnUrl", url);//
        paramMap.put("userTerminalType", userTerminalType);//终端类型
        paramMap.put("goodsName", goodsName);//商品名称(0-64)
        paramMap.put("sellerUserId", config.getPartnerId());
        paramMap.put("tradeAmount", tradeAmount);
        paramMap.put("paymentType", paymentType);//支付方式
        Logger.debug("gainYijiPayParam    paramMap--->" + paramMap.toString());

        YjfUtil util = new YjfUtil(config.getYijiUrl(), config.getSecretKey());
        String prepareSign = util.signString(paramMap);// 生成待签名字符串
        Logger.debug("getYijiPayParam    prepareSign--->" + prepareSign);
        String sign = YjfUtil.MD5(prepareSign);// 签名
        Logger.debug("getYijiPayParam    sign--->" + sign);

        YijiPayParamsDTO dto = new YijiPayParamsDTO();
        dto.setActionUrl(config.getYijiUrl());
        dto.setOrderNo(orderNo);
        dto.setMerchOrderNo(outOrderNo);
        dto.setService(service);
        dto.setVersion(version);
        dto.setPartnerId(config.getPartnerId());
        dto.setSignType(config.getSignType());
        dto.setSign(sign);
        dto.setNotifyUrl(url);//
        dto.setReturnUrl(url);//
        dto.setUserTerminalType(userTerminalType);
        dto.setGoodsName(goodsName);
        dto.setSellerUserId(config.getPartnerId());
        dto.setTradeAmount(Double.valueOf(tradeAmount));
        dto.setPaymentType(paymentType);
        Logger.debug("getYijiPayParam    dto--->" + Json.toJson(dto).toString());

        YijiOrder yo = new YijiOrder();
        yo.setOrderNo(orderNo);
        yo.setOutOrderId(id);
        yo.setOutOrderNo(outOrderNo);
        yo.setTotalFee(tradeAmount);
        yo.setCurrency("CNY");
        yo.setCreateDate(new Date());
        int line = this.insertYijiOrderSelective(yo);
        Logger.debug("getYijiPayParam    insertYijiOrderSelective  line--->" + line);
        result.put("suc", true);
        result.set("info", Json.toJson(dto));
        return result;
    }

    /**
     * 去除交易金额小数后末尾的0
     *
     * @param str
     * @return
     */
    private String deleteZero(String str) {
        if (str.indexOf(".") > 0) {
            str = str.replaceAll("0+?$", "");
            str = str.replaceAll("[.]$", "");
        }
        return str;
    }

}


