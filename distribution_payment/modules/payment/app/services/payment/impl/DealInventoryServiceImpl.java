package services.payment.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import entity.payment.enums.YijifuMark;
import entity.payment.yijipay.YijiConfig;
import entity.payment.yijipay.YijiResult;
import mapper.payment.alipay.PaymentRecordMapper;
import mapper.payment.yijipay.YijiConfigMapper;
import mapper.payment.yijipay.YijiResultMapper;
import play.Logger;
import play.libs.Json;
import services.payment.IDealInventoryService;
import utils.payment.HttpUtil;
import utils.payment.PayUtil;
import utils.payment.YjfUtil;
import vo.payment.PaymentRecord;
import vo.payment.YijiResultVO;

/**
 * 
 * 支付成功后回调处理
 * 
 * @author xuse
 *
 */
public class DealInventoryServiceImpl implements IDealInventoryService {

	@Inject
	private PaymentRecordMapper recordMapper;
	
	@Inject
    private YijiConfigMapper yijiConfigMapper;
	
	@Inject
    private YijiResultMapper yijiResultMapper;
	
	@Inject
	private IDealInventoryService inventoryService;
	
	
	private static final byte[] synclock = new byte[0];

	@Override
	public String cancelPurchase(String purchaseNo, Double total, String flag,Map<String,String> payMap) {
		ObjectNode node = Json.newObject();
		node.put("purchaseNo", purchaseNo);
		node.put("flag", flag);
		node.put("actualAmount", total);
		node.put("payType", payMap.get("payType"));
		node.put("payDate", payMap.get("payDate"));
		node.put("tradeNo", payMap.get("tradeNo"));
		Logger.info("cancelPurchase     post_string--->" + node.toString());
		String response_string = HttpUtil.post(node.toString(), "/purchase/cancel");
		Logger.info("cancelPurchase     response_string--->" + response_string);
		return response_string;
	}

	@Override
	public String getPurchase(String purchaseOrderNo, String flag) {
		ObjectNode node = Json.newObject();
		node.put("purchaseOrderNo", purchaseOrderNo);
		node.put("flag", flag);
		Logger.info("syncPayInfoToPurchaseOrder     post_string--->" + node.toString());
		String response_string = HttpUtil.post(node.toString(), "/purchase/getByNo");
		Logger.info("syncPayInfoToPurchaseOrder     response_string--->" + response_string);
		return response_string;
	}

	@Override
	public String finishSaleOrder(Integer sid,Double actualPay,boolean isComplete,String payDate,String payNo,String payType) throws JsonProcessingException, IOException{
		Map<String,Object> param = Maps.newHashMap();
		param.put("id", sid);
		param.put("actualPay", actualPay);
		param.put("isComplete", isComplete);
		param.put("payDate", payDate);
		param.put("payNo", payNo);
		param.put("payType", payType);
		Logger.info("完成发货单参数:{}",param);
		String response_string = HttpUtil.post(Json.toJson(param)
				.toString(), "/sales/finishSaleOrder");
		Logger.info("完成发货单结果:{}",response_string);
		return response_string;
	} 
	
	@Override
	public String createBillRecord(Map<String, Object> params) {
		Logger.info("createBillRecord     post_string--->" + Json.toJson(params));
		String response_string = HttpUtil.post(Json.toJson(params).toString(), "/member/createBill");
		Logger.info("createBillRecord     response_string--->" + response_string);
		return response_string;
	}
	
	@Override
	public String getMemberInfo(String email) {
		Map<String, String> param = Maps.newHashMap();
		param.put("email", email);
		String resultString = HttpUtil.get(param, "/member/infor");
		Logger.info("getMemberInfo:" + resultString);
		return resultString;
	}

	@Override
	public void callback(String orderNo,String total,String desc,Map<String,String> payMap) {
		synchronized (synclock) {
			try {
				PaymentRecord old = recordMapper.select(orderNo);
				if (null == old) {
					String tradeNo = payMap.get("tradeNo");
					PaymentRecord record = new PaymentRecord(orderNo);
					recordMapper.insertSelective(record);
					Logger.info("生成支付记录" + record.toString());
					// 1、更新采购单状态
					String firstRes = cancelPurchase(orderNo, Double.valueOf(total), "PAY_SUCCESS",payMap);
					JsonNode firstResNode = parseJsonNode(firstRes);
					if ("0".equals(firstResNode.get("errorCode").asText())) {
						String secondRes = getPurchase(orderNo, "SUCCESS");
						JsonNode secondResNode = parseJsonNode(secondRes);
						//change by zbc 支付成功 回调，删掉活动标记
						if ("0".equals(secondResNode.get("returnMess").get("errorCode").asText())) {
							//创建交易记录
							Map<String, Object> billMap = Maps.newHashMap();
							billMap.put("serialNumber", orderNo);
							billMap.put("purpose", "3");
							billMap.put("sources", 3);
							billMap.put("amount", Double.valueOf(total));
							billMap.put("balance", 0);
							billMap.put("paymentType", desc);
							billMap.put("tradeNo",tradeNo);
							createBillRecord(billMap);
						}
					} else {
						Logger.info("更新采购订单失败");
					}
				} else {
					Logger.info("该订单已经被处理或者正在处理：" + orderNo);
				}
			} catch (Exception e) {
				Logger.error("处理订单状态失败：" + e);
			}
		}
	}
	
	private JsonNode parseJsonNode(String firstRes) throws JsonProcessingException, IOException {
		ObjectMapper obj = new ObjectMapper();
		return obj.readTree(firstRes);
	}
	
	public YijiConfig getYijiConfig(String mark) {
        Logger.debug("==========获取易极付支付配置信息==========");
        return yijiConfigMapper.getYijiConfig(mark);
    }

	@Override
	public Map<String,Object> receiveSyncReturn(Map<String, String[]> params, ObjectNode result,String orderno, Double amount,String flag,String sid) {

        result.put("isok", false);
        Map<String, String> paramMap = Maps.newHashMap();
        Map<String,Object> dealResMap = Maps.newHashMap();
        try {
			String success = params.containsKey("success") ? params.get("success")[0] : null;
			String orderNo = params.containsKey("orderNo") ? params.get("orderNo")[0] : null;
			String protocol = params.containsKey("protocol") ? params.get("protocol")[0] : null;
			String service = params.containsKey("service") ? params.get("service")[0] : null;
			String version = params.containsKey("version") ? params.get("version")[0] : null;
			String partnerId = params.containsKey("partnerId") ? params.get("partnerId")[0] : null;
			String sign = params.containsKey("sign") ? params.get("sign")[0] : null;
			String signType = params.containsKey("signType") ? params.get("signType")[0] : null;
			String merchOrderNo = params.containsKey("merchOrderNo") ? params.get("merchOrderNo")[0] : null;
			String context = params.containsKey("context") ? params.get("context")[0] : null;
			String resultCode = params.containsKey("resultCode") ? params.get("resultCode")[0] : null;
			String resultMessage = params.containsKey("resultMessage") ? params.get("resultMessage")[0] : null;
			String notifyTime = params.containsKey("notifyTime") ? params.get("notifyTime")[0] : null;
            if (params.containsKey("mergePayResult")) {//合并付款同步返回
                String mergePayResult = params.get("mergePayResult")[0];
                String tradeDetail = params.get("tradeDetail")[0];
                paramMap.put("mergePayResult", mergePayResult);
                paramMap.put("tradeDetail", tradeDetail);
            } else {
				String returnUrl = params.containsKey("returnUrl") ? params.get("returnUrl")[0] : null;
				String creatTradeResult = params.containsKey("creatTradeResult") ? params.get("creatTradeResult")[0] : null;
				String tradeAmount = params.containsKey("tradeAmount") ? params.get("tradeAmount")[0] : null;
				String fastPayStatus = params.containsKey("fastPayStatus") ? params.get("fastPayStatus")[0] : null;
				String bizNo = params.containsKey("bizNo") ? params.get("bizNo")[0] : null;
				String notifyUrl = params.containsKey("notifyUrl") ? params.get("notifyUrl")[0] : null;
				String merchantOrderNo = params.containsKey("merchantOrderNo") ? params.get("merchantOrderNo")[0] : null;
				paramMap.put("returnUrl", returnUrl);
				paramMap.put("creatTradeResult", creatTradeResult);
				paramMap.put("tradeAmount", tradeAmount);
				paramMap.put("fastPayStatus", fastPayStatus);
				paramMap.put("bizNo", bizNo);
				paramMap.put("notifyUrl", notifyUrl);
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
            paramMap.put("notifyTime", notifyTime);
            YijiConfig config = getYijiConfig(YijifuMark.pay.name());
            YjfUtil util = new YjfUtil(config.getYijiUrl(), config.getSecretKey());
            String prepareSign = util.signString(paramMap);//生成待签名字符串
            Logger.debug("receiveSyncReturn    prepareSign--->" + prepareSign);
            String toBeVerifiedSign = YjfUtil.MD5(prepareSign);//待验证签名
            Logger.debug("receiveSyncReturn    toBeVerifiedSign--->" + toBeVerifiedSign);

			if (StringUtils.isNotEmpty(sign)&&sign.equals(toBeVerifiedSign)) {// 签名校验通过
				paramMap.put("sign", sign);
				// 单个支付同步返回
				paramMap.put("outOrderNo", merchOrderNo);
				if("true".equals(flag)){
					paramMap.put("flag", flag);
					paramMap.put("sid",sid);
				}
				dealResMap = dealResultInformation(paramMap,amount);
				// 用于结果页面
				if ("EXECUTE_SUCCESS".equals(paramMap.get("resultCode"))
						&& "FINISHED".equals(paramMap.get("fastPayStatus"))) {
					result.put("isok", true);
				}

            } else {
                Logger.debug("receiveSyncReturn    参数签名校验未通过");
            }
        }catch (Exception e){
            Logger.error("同步  Exception : "+e);
            e.printStackTrace();
        }
        return dealResMap;
	}
	
	/**
     * 处理支付结果信息
     */
	private Map<String,Object> dealResultInformation(Map<String, String> paramMap, Double amount) {
		//add by zbc
		boolean frontPay = false;//前台支付
		Map<String,Object> map = Maps.newHashMap();
		String outOrderNo = paramMap.get("outOrderNo");
		String resultCode = paramMap.get("resultCode");
		String tradeStatus = paramMap.get("tradeStatus");
		String flag = paramMap.containsKey("flag") ? paramMap.get("flag") : "";
		String sid = paramMap.get("sid");
		if ("true".equals(flag)) {// 取出参数 然后移除
			paramMap.remove("flag");
			paramMap.remove("sid");
		}
		YijiResult yr = new YijiResult();
		yr.setOutOrderNo(outOrderNo);
		yr = yijiResultMapper.getYijiResultByCondition(yr);
		if (yr != null) {
			// 已有结果记录
			if ("EXECUTE_SUCCESS".equals(yr.getResultCode())
					&& (Strings.isNullOrEmpty(yr.getTradeStatus()) || "trade_finished".equals(yr.getTradeStatus()))) {
				// 结果记录显示支付成功，不做处理
				Logger.debug("dealResultInformation    订单\"" + outOrderNo + "\"支付成功的结果记录已存在");
			} else {
				// 结果记录显示未支付成功，更新结果记录
				YijiResultVO vo = Json.fromJson(Json.toJson(paramMap), YijiResultVO.class);
				YijiResult yjResult = new YijiResult();
				BeanUtils.copyProperties(vo, yjResult);
				yjResult.setId(yr.getId());
				yjResult.setOutOrderNo(vo.getMerchOrderNo());
				yjResult.setTradeNo(vo.getBizNo());
				yjResult.setTradeStatus(vo.getFastPayStatus());
				int line = yijiResultMapper.updateByPrimaryKeySelective(yjResult);
				Logger.debug("dealResultInformation    updateYijiResultSelective  line--->" + line);
			}
		} else {
			// 没有结果记录
			YijiResultVO vo = Json.fromJson(Json.toJson(paramMap), YijiResultVO.class);
			YijiResult yjResult = new YijiResult();
			BeanUtils.copyProperties(vo, yjResult);
			yjResult.setCreateDate(new Date());
			yjResult.setOutOrderNo(vo.getMerchOrderNo());
			yjResult.setTradeNo(vo.getBizNo());
			yjResult.setTradeStatus(vo.getFastPayStatus());
			int line = yijiResultMapper.insertSelective(yjResult);
			Logger.debug("dealResultInformation    insertYijiResultSelective  line--->" + line);
		}
		if ("EXECUTE_SUCCESS".equals(resultCode)) {
			// 交易处理成功
			if (!paramMap.containsKey("fastPayStatus") || (paramMap.containsKey("fastPayStatus") && "FINISHED".equals(tradeStatus))) {
				// add by duyt
				// CZ开头的交易号意为“在线充值单单号”
				if (outOrderNo.toUpperCase().startsWith("CZ")) {
					inventoryService.onlinePaySuccessCallback(outOrderNo,  paramMap.get("bizNo"), "ijipay");
					return map;
				}
				yr = yijiResultMapper.getYijiResultByCondition(yr);
				if ("true".equals(flag)) {
					frontPay = true;
					callback2(outOrderNo, sid, amount + "",new DateTime().toString("yyyy-MM-dd HH:mm:ss"), yr.getTradeNo(),"yijifu");
				} else {
					Map<String,String> payMap = Maps.newHashMap();
            		try {
            			payMap.put("tradeNo",yr.getTradeNo());
						payMap.put("payDate", new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
						payMap.put("payType","yijifu");
					} catch (Exception e) {
						e.printStackTrace();
					}
					callback(outOrderNo, amount + "", "易极付支付",payMap);
					frontPay = true;
				}
			} else {
				// 交易待付款
				Logger.debug("dealResultInformation    交易待付款");
			}

		} else {
			// 交易处理中或交易出错
			Logger.debug("dealResultInformation    交易处理中或交易出错");
		}
		map.put("frontPay", frontPay);
		map.put("od",outOrderNo);
		return map;
	}
	
    public String getSaleMain(String orderNo) {
		ObjectNode node = Json.newObject();
		node.put("orderNo", orderNo);
		Logger.info("getSaleMain     post_string--->" + node.toString());
		String response_string = HttpUtil.post(node.toString(), "/sales/getMain");
		Logger.info("getSaleMain     response_string--->" + response_string);
		return response_string;
	}

	@Override
	public void callback2(String orderNo,String sid,String total,String payDate, String tradeNo, String payType) {
		synchronized (synclock) {
			try {
				PaymentRecord old = recordMapper.select(orderNo);
				if (null == old) {
					PaymentRecord record = new PaymentRecord(orderNo);
					recordMapper.insertSelective(record);
					Logger.info("生成支付记录" + record.toString());
					if(StringUtils.isEmpty(sid)){
						JsonNode main = Json.parse(getSaleMain(orderNo));
						sid = main.get("id").asText();
					}
					JsonNode res = Json.parse(finishSaleOrder(Integer.valueOf(sid), Double.valueOf(total), true,payDate,tradeNo,payType));
					if ("false".equals(res.get("result").asText())) {
						Logger.info("更新客户订单失败");
					} else {
						//创建交易记录
						Map<String, Object> billMap = Maps.newHashMap();
						billMap.put("serialNumber", orderNo);
						billMap.put("purpose", "5");
						billMap.put("sources", 3);
						billMap.put("amount", Double.valueOf(total));
						billMap.put("balance", 0);
						billMap.put("paymentType", getPayTypeDesc(payType));
						createBillRecord(billMap);
					}
				} else {
					Logger.info("该订单已经被处理或者正在处理：" + orderNo);
				}
			} catch (Exception e) {
				Logger.error("处理订单状态失败：" + e);
			}
		}
	}
	
	/**
	 * 判断是否为销售单
	 * @param outOrderNo
	 * @return
	 */
	@Override
	public boolean isSaleOrder(String outOrderNo) {
		return StringUtils.isNotEmpty(outOrderNo)
				&& (outOrderNo.toUpperCase().startsWith("XS") || outOrderNo.toUpperCase().startsWith("MD"));
	}

	@Override
	public void onlinePaySuccessCallback(String orderNo, String tradeNo, String payType) {
		synchronized (synclock) {
			PaymentRecord old = recordMapper.select(orderNo);
			if (null == old) {
				PaymentRecord record = new PaymentRecord(orderNo);
				recordMapper.insertSelective(record);
				Logger.info("生成支付记录" + record.toString());
				PayUtil.onlinePaySuccessCallback(orderNo, tradeNo, payType);
			} else {
				Logger.info("该订单已经被处理或者正在处理：" + orderNo);
			}
		}
	}
	
	

	private String getPayTypeDesc(String payType){
		if(payType == null ){
			return null;
		}
		switch (payType) {
		case "wechatpay":
			return "微信支付";
		case "yijifu":
			return "易极付支付";
		case "alipay":
			return "支付宝支付";
		default:
			break;
		}
		return null;
	}
}
