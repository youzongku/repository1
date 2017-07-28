package services.payment.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.tempuri.AccountTypes;
import org.tempuri.ArrayOfKvp;
import org.tempuri.CurrencyType;
import org.tempuri.Kvp;
import org.tempuri.Payer;
import org.tempuri.TransReqItem;
import org.tempuri.TransRespItem;
import org.tempuri.TransSoap;
import org.tempuri.TransferRequest;
import org.tempuri.TransferResponse2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;

import entity.payment.enums.SFTEnum;
import entity.payment.shengpay.ShengAccountBalance;
import entity.payment.shengpay.ShengConfig;
import entity.payment.shengpay.ShengOrder;
import entity.payment.shengpay.ShengResult;
import mapper.payment.shengpay.ShengAccountBalanceMapper;
import mapper.payment.shengpay.ShengConfigMapper;
import mapper.payment.shengpay.ShengOrderMapper;
import mapper.payment.shengpay.ShengResultMapper;
import play.Logger;
import play.libs.Json;
import services.payment.IShengPayService;
import utils.payment.SFTUtil;

/**
 * Created by LSL on 2016/4/30.
 */
public class ShengPayService implements IShengPayService {

    @Inject
    private ShengConfigMapper shengConfigMapper;

    @Inject
    private ShengOrderMapper shengOrderMapper;

    @Inject
    private ShengResultMapper shengResultMapper;

    @Inject
    private ShengAccountBalanceMapper shengAccountBalanceMapper;

    @Override
    public ShengConfig getShengConfig() {
        Logger.debug("==========获取盛付通支付配置信息==========");
        return shengConfigMapper.selectByPrimaryKey(1);
    }

    /**
     * 盛付通支付申请(代扣业务)
     * {
     *     "orderNo": "",//订单号
     *     "amount": "",//交易金额
     *     "IDCard": "",//订购人证件号
     *     "realName": "",//订购人真实姓名
     *     "cellphone": "",//订购人联系方式，可空
     *     "currency": "",//交易币种，可空
     *     "sku": "",//SKU，可空
     *     "remark": ""//商品描述，可空
     * }
     */
    @Override
    public JsonNode applyPay(JsonNode param) {
        ObjectNode res = Json.newObject();
        ShengConfig config = this.getShengConfig();
        TransSoap transSoap = SFTUtil.getTransSoap(config.getWsUrl());
//        if (this.authentication(param)) {
            //实名认证通过
            ((ObjectNode) param).put("interfaceType", SFTEnum.Billing.name());
            TransferRequest request = this.buildTransferRequest(param, config);
            //保存申请记录
            this.saveShengOrder(request, param);
            TransferResponse2 response = transSoap.transfer(request);
            Logger.debug("applyPay    response----->" + Json.toJson(response).toString());
            //保存结果记录
            this.saveShengResult(response, param);
            if (response.getCode() == 0) {
                //支付成功
                Logger.debug("applyPay    [success message]----->" + response.getMessage());
            } else {
                //支付失败
                Logger.debug("applyPay    [fail message]----->" + response.getMessage());
            }
//        } else {
//            //实名认证不通过
//            Logger.debug("applyPay    [authentication failed]");
//        }
        return res;
    }

    /**
     * 盛付通转账申请(代发业务)
     * {
     *     "orderNo": "",//订单号
     *     "amount": "",//交易金额
     *     "IDCard": "",//订购人证件号
     *     "realName": "",//订购人真实姓名
     *     "cellphone": "",//订购人联系方式，可空
     *     "currency": "",//交易币种，可空
     *     "sku": "",//SKU，可空
     *     "remark": ""//商品描述，可空
     * }
     */
    @Override
    public JsonNode applyTransfer(JsonNode param) {
        ObjectNode res = Json.newObject();
        ShengConfig config = this.getShengConfig();
        TransSoap transSoap = SFTUtil.getTransSoap(config.getWsUrl());
//        if (this.authentication(param)) {
            //实名认证通过
            ((ObjectNode) param).put("interfaceType", SFTEnum.Transfer.name());
            TransferRequest request = this.buildTransferRequest(param, config);
            //保存申请记录
            this.saveShengOrder(request, param);
            TransferResponse2 response = transSoap.transfer(request);
            Logger.debug("applyTransfer    response----->" + Json.toJson(response).toString());
            //保存结果记录
            this.saveShengResult(response, param);
            if (response.getCode() == 0) {
                //转账成功
                Logger.debug("applyTransfer    [success message]----->" + response.getMessage());
            } else {
                //转账失败
                Logger.debug("applyTransfer    [fail message]----->" + response.getMessage());
            }
//        } else {
//            //实名认证不通过
//            Logger.debug("applyTransfer    [authentication failed]");
//        }
        return res;
    }

    /**
     * 构建代发/代扣申请
     * {
     *     "orderNo": "",//订单号
     *     "interfaceType": "",//接口类型：Transfer-转账 Billing-代扣
     *     "amount": "",//交易金额
     *     "IDCard": "",//订购人证件号，可空
     *     "realName": "",//订购人真实姓名，可空
     *     "cellphone": "",//订购人联系方式，可空
     *     "currency": "",//交易币种，可空
     *     "sku": "",//SKU，可空
     *     "remark": ""//商品描述，可空
     * }
     */
    private TransferRequest buildTransferRequest(JsonNode param, ShengConfig config) {
        Logger.debug("buildTransferRequest    param----->" + param.toString());
        TransferRequest request = new TransferRequest();
        request.setVersion(config.getVersion());//接口版本号,传1.0
        request.setInterfaceType(param.get("interfaceType").asText());//接口类型：Transfer-转账 Billing-代扣
        if (SFTEnum.Billing.name().equals(request.getInterfaceType())) {
            //代扣商户号
            request.setMerchantNo(config.getMerchantNo());//商户号
        } else {
            //代发商户号
            request.setMerchantNo(config.getPayer());//商户号
        }

        request.setAppId("");//接口编号（保留字段，默认不用填写）
        request.setRemark1("");
        request.setRemark2("");
        request.setExt(new ArrayOfKvp());

        TransReqItem reqBody = new TransReqItem();
        reqBody.setAmount(new BigDecimal("1.00"));//金额
        reqBody.setCurrency(CurrencyType.RMB);//货币类型：枚举
        reqBody.setMerchantOrderId(param.get("orderNo").asText());//订单号.唯一

        reqBody.setNotifyUrl("");
        reqBody.setNotifyUrlType("");
        reqBody.setProductNo("");
        reqBody.setProductDesc("");//产品备注,如不为空，会做为钱包帐户明细备注

        Payer payer = new Payer();//付款人
        if (SFTEnum.Billing.name().equals(request.getInterfaceType())) {
            payer.setMemberId(config.getPayer());//盛大通行证或钱包账户
        } else {
            payer.setMemberId(config.getReceipt());//盛大通行证或钱包账户
        }
        payer.setMemberIdType(AccountTypes.PT_ID);//MemberId的类型，枚举：通行证或账户
        reqBody.setPayer(payer);

        Payer toPayer = new Payer();//收款方
        if (SFTEnum.Billing.name().equals(request.getInterfaceType())) {
            toPayer.setMemberId(config.getReceipt());//盛大通行证或钱包账户
        } else {
            toPayer.setMemberId(config.getPayer());//盛大通行证或钱包账户
        }
        toPayer.setMemberIdType(AccountTypes.PT_ID);//MemberId的类型，枚举：通行证或账户
        reqBody.setToPayer(toPayer);

        ArrayOfKvp kvpArray = new ArrayOfKvp();
        List<Kvp> kvpList = kvpArray.getKvp();
        kvpList.add(SFTUtil.getKvp("invokeIp", config.getInvokeIp()));
        kvpList.add(SFTUtil.getKvp("idNo", param.get("IDCard").asText()));
        kvpList.add(SFTUtil.getKvp("realName", param.get("realName").asText()));
        kvpList.add(SFTUtil.getKvp("mobile", param.get("cellphone").asText()));
        reqBody.setExt(kvpArray);//说明：如果为外币转账或者跨境商户转账，需要在ReqBody的扩展字段中传入实名信息，字段分别是：realName, idNo, mobile，invokeIp
        request.setReqBody(reqBody);

        request.setMachineName(config.getMachineName());
        request.setSignType(Integer.parseInt(config.getSignType()));//签名类型 1-rsa 2-md5
        request.setSummary("摘要信息");//摘要信息

        //待签名串
        String preSign = request.getVersion() + "|" +
                request.getInterfaceType() + "|" +
                request.getMerchantNo() + "|" +
                request.getAppId() + "|" +
                reqBody.getAmount().toString() + "|" +
                reqBody.getCurrency().value().toUpperCase() + "|" +
                reqBody.getMerchantOrderId() + "|" +
                payer.getMemberId() + "|" +
                payer.getMemberIdType().value().toUpperCase() + "|" +
                toPayer.getMemberId() + "|" +
                toPayer.getMemberIdType().value().toUpperCase() + "|" +
                request.getMachineName() + "|" +
                request.getSummary();
        Logger.debug("buildTransferRequest    preSign----->" + preSign);

        String signed = SFTUtil.sign(preSign, config.getCkey(), config.getSignCharset());
        Logger.debug("buildTransferRequest    signed----->" + signed);
        request.setMac(signed);//签名信息
        Logger.debug("buildTransferRequest    request----->" + Json.toJson(request).toString());
        return request;
    }

    /**
     * 保存支付/转账申请记录
     */
    private void saveShengOrder(TransferRequest request, JsonNode param) {
        ShengOrder so = new ShengOrder();
        so.setOrderNo(param.get("orderNo").asText());
        so.setJsonString(Json.toJson(request).toString());
        so.setAppId(request.getAppId());
        so.setVersion(request.getVersion());
        so.setInterfaceType(request.getInterfaceType());
        so.setSignType(String.valueOf(request.getSignType()));
        so.setMac(request.getMac());
        so.setMachineName(request.getMachineName());
        so.setMerchantNo(request.getMerchantNo());
        so.setSummary(request.getSummary());

        TransReqItem item = request.getReqBody();
        so.setAmount(item.getAmount().toString());
        so.setCurrency(item.getCurrency().value().toUpperCase());
        so.setMerchantOrderId(item.getMerchantOrderId());
        so.setNotifyUrl(item.getNotifyUrl());
        so.setNotifyUrlType(item.getNotifyUrlType());
        so.setPayer(item.getPayer().getMemberId());
        so.setToPayer(item.getToPayer().getMemberId());
        so.setProductNo(item.getProductNo());
        so.setProductDesc(item.getProductDesc());
        int line = shengOrderMapper.insertSelective(so);
        Logger.debug("saveShengOrder    [insert ShengOrder]line----->" + line);
    }

    /**
     * 保存支付/转账结果记录
     */
    private void saveShengResult(TransferResponse2 response, JsonNode param) {
        String orderNo = param.get("orderNo").asText();
        String interfaceType = param.get("interfaceType").asText();
        ShengResult sr = new ShengResult();
        sr.setOrderNo(orderNo);
        sr.setInterfaceType(interfaceType);
        sr = shengResultMapper.getShengResultByCondition(sr);
        //结果记录不存在则先插入新纪录
        if (sr == null) {
            sr = new ShengResult();
            sr.setOrderNo(orderNo);
            sr.setInterfaceType(interfaceType);
            int line = shengResultMapper.insertSelective(sr);
            Logger.debug("saveShengResult    [insert ShengResult]line----->" + line);
        }
        sr.setJsonString(Json.toJson(response).toString());
        sr.setCcode(String.valueOf(response.getCode()));
        sr.setCmessage(response.getMessage());
        sr.setMerchantId(response.getMerchantId());

        TransRespItem item = response.getRespBody();
        if (item != null) {
            sr.setPayTransNo(item.getSerialNo());
            sr.setSerialNo(item.getSerialNo());
            sr.setAmount(item.getAmount().toString());
            sr.setCurrencyType(item.getCurrencyType().value());
            sr.setMerchantOrderId(item.getMerchantOrderId());
            sr.setPayer(item.getPayer().getMemberId());
            sr.setToPayer(item.getToPayer().getMemberId());
            sr.setPayTime(item.getPayTime().toString());
            sr.setPayChannel(item.getPayChannel());
        }
        int line = shengResultMapper.updateByPrimaryKeySelective(sr);
        Logger.debug("saveShengResult    [update ShengResult]line----->" + line);
    }

    /**
     * 盛付通实名认证
     * {
     *     "orderNo": "",//订单号
     *     "IDCard": "",//订购人证件号
     *     "realName": ""//订购人真实姓名
     * }
     */
    @Override
    public boolean authentication(JsonNode param) {
        ShengConfig config = this.getShengConfig();
        ObjectNode node = Json.newObject();
        node.put("merchantOrderNo", param.get("orderNo").asText());
        node.put("realName", param.get("realName").asText());
        node.put("idNo", param.get("IDCard").asText());
        node.put("requestTime", DateTime.now().toString("yyyyMMddHHmmss"));
        node.put("merchantNo", config.getMerchantNo());
        node.put("charset", config.getVerifyCharset());
        node.put("userIp", config.getUserIp());
        node.put("exts", "");
        String response_str = SFTUtil.requestPOST(node.toString(), config);
        if (!Strings.isNullOrEmpty(response_str)) {
            JsonNode responseBody = Json.parse(response_str);
            String response_data = responseBody.get("response_data").asText();
            String header_data = responseBody.get("header_data").asText();
            if (!Strings.isNullOrEmpty(header_data) &&
                    !Strings.isNullOrEmpty(response_data)) {
                JsonNode sign = Json.parse(response_str);
//                String signType = sign.get("signType").asText();
                String signMsg = sign.get("signMsg").asText();
                if (SFTUtil.verify(response_data, signMsg, config.getCkey(), config.getSignCharset())) {
                    //签名验证通过
                    JsonNode response = Json.parse(response_data);
                    if (response.get("verifyFlag").asBoolean()) {
                        //实名认证通过，网纹照Base64字符串
//                        String wangwenBase64Str = response.get("wangwenBase64Str").asText();
//                        byte[] bytes = Base64.decodeBase64(wangwenBase64Str);
                        return true;
                    } else {
                        //实名认证不通过
                        return false;
                    }
                } else {
                    //签名验证不通过
                    return false;
                }
            } else {
                //签名类型和签名串不存在或响应返回数据不存在
                return false;
            }
        } else {
            //实名认证请求出错
            return false;
        }
    }

    /**
     * 调用盛付通账户余额查询接口查询余额
     * @param merchantNo 商户号
     * @return 账户余额信息
     */
    @Override
    public JsonNode remoteQueryBalance(String merchantNo) {
        ObjectNode node = Json.newObject();
        ShengConfig config = this.getShengConfig();
        String request_url = config.getBalanceQueryUrl() + "?merchantNo=" + merchantNo;
        String response_data = SFTUtil.requestGET(request_url);
        if (!Strings.isNullOrEmpty(response_data)) {
            JsonNode response = Json.parse(response_data);
            if ("".equals(response.get("errorCode").asText())) {
                ShengAccountBalance sab = new ShengAccountBalance();
                sab.setMerchantNo(merchantNo);
                sab = shengAccountBalanceMapper.getBalanceByCondition(sab);
                if (sab == null) {
                    //账号余额记录不存在
                    sab = new ShengAccountBalance();
                    sab.setJsonString(response_data);
                    sab.setMerchantNo(merchantNo);
                    sab.setTotalBalance(response.get("balance").asText());
                    sab.setAvailableBalance(response.get("availableBalance").asText());
                    sab.setStatus(response.get("status").asText());
                    sab.setTdate(response.get("date").asText());
                    int line = shengAccountBalanceMapper.insertSelective(sab);
                    Logger.debug("remoteQueryBalance    [insert Balance]line----->" + line);
                } else {
                    //账号余额记录存在
                    sab.setJsonString(response_data);
                    sab.setTotalBalance(response.get("balance").asText());
                    sab.setAvailableBalance(response.get("availableBalance").asText());
                    sab.setStatus(response.get("status").asText());
                    sab.setTdate(response.get("date").asText());
                    sab.setUpdateTime(new Date());
                    int line = shengAccountBalanceMapper.updateByPrimaryKeySelective(sab);
                    Logger.debug("remoteQueryBalance    [update Balance]line----->" + line);
                }
                node.put("suc", true);
                node.set("msg", Json.toJson(sab));
            } else {
                node.put("suc", false);
                node.put("msg", response.get("errorMessage").asText());
            }
        } else {
            node.put("suc", false);
            node.put("msg", "盛付通账户余额查询出错");
        }
        return node;
    }

    /**
     * 根据指定商户号获取账户余额信息，先查询本地数据库，若无记录再调盛付通接口查询
     * @return 账户余额信息
     */
    @Override
    public JsonNode queryBalanceRecord(String merchantNo) {
        ObjectNode node = Json.newObject();
        ShengAccountBalance sab = new ShengAccountBalance();
        sab.setMerchantNo(merchantNo);
        sab = shengAccountBalanceMapper.getBalanceByCondition(sab);
        if (sab == null) {
            //账号余额记录不存在
            node = (ObjectNode) this.remoteQueryBalance(merchantNo);
        } else {
            //账号余额记录存在
            node.put("suc", true);
            node.set("msg", Json.toJson(sab));
        }
        return node;
    }

}
