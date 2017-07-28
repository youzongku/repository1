package utils.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;

import play.Logger;
import play.libs.Json;

import java.util.Date;
import java.util.Map;

/**
 * Created by LSL on 2016/4/18.
 */
public class PayUtil {

	/**
	 * 后台审核支付 同步相关支付信息到销售订单
	 *
	 * @param saleOrderNo
	 *            销售订单号
	 * @param payDate
	 *            支付完成时间
	 * @param payNo
	 *            支付交易号
	 * @param payType
	 *            支付方式
	 * @param currency
	 *            支付币种
	 * @param status
	 *            销售订单状态
	 * @param paryer
	 *            支付人
	 * @param paryerIdcard
	 *            支付人身份证
	 */
	public static void syncPayInfoToSaleOrder(String saleOrderNo,
			String payDate, String payNo, String payType, String currency,
			String status, String paryer, String paryerIdcard) {
		ObjectNode node = Json.newObject();
		node.put("saleOrderNo", saleOrderNo);
		node.put("payDate", payDate);
		node.put("payNo", payNo);
		node.put("payType", payType);
		node.put("currency", currency);
		node.put("status", status);// 6，销售单状态变为已审核
		node.put("payer", paryer);
		node.put("paryerIdcard", paryerIdcard);
		switch (payType) {
		case "alipay":
			Logger.info("同步支付宝支付相关信息到销售订单");
			break;
		case "wechatpay":
			Logger.info("同步微信支付相关信息到销售订单");
			break;
		case "yijifu":
			Logger.info("同步易极付支付相关信息到销售订单");
			break;
		default:
			Logger.info("同步支付相关信息到销售订单");
			break;
		}
		Logger.info("syncPayInfoToSaleOrder     post_string--->"
				+ node.toString());
		String response_string = HttpUtil.post(node.toString(),
				"/sales/updPayInfo");
		Logger.info("syncPayInfoToSaleOrder     response_string--->"
				+ response_string);
	}

	/**
	 * 生成易极付请求交易号
	 *
	 * @return
	 */
	public static String buildOrderNo() {
		String orderNo = "YJF_O_";
		orderNo += DateTimeUtils.date2string(new Date(),
				DateTimeUtils.FORMAT_DATETIME_BACKEND);
		int x = (int) (Math.random() * 1000);

		String str0 = "";
		switch (String.valueOf(x).length()) {
		case 1:
			str0 = "00" + x;
			break;
		case 2:
			str0 = "0" + x;
			break;
		default:
			str0 = String.valueOf(x);
		}

		int y = (int) (Math.random() * 1000);
		String str1 = "";
		switch (String.valueOf(y).length()) {
		case 1:
			str1 = "00" + y;
			break;
		case 2:
			str1 = "0" + y;
			break;
		default:
			str1 = String.valueOf(y);
		}
		return orderNo + "000" + str0 + str1;
	}

	/**
	 * 在线充值成功后的回调函数
	 *
	 * @param orderNo
	 * @param tradeNo
	 */
	public static void onlinePaySuccessCallback(String orderNo, String tradeNo,String payType) {
		try {

			// 更新在线充值申请的状态
			Map<String, Object> params = Maps.newHashMap();
			params.put("onlineApplyNo", orderNo);
			params.put("transferNumber", tradeNo);

			Logger.info("=======在线充值成功后的回调函数=======");
			String returnStr = HttpUtil.post(Json.toJson(params).toString(),
					"/member/onlinePaySuccessCallback");
			JsonNode result = new ObjectMapper().readTree(returnStr);

			if (result.has("success") && !result.get("success").asBoolean()) {
				Logger.info("["+payType+"]在线充值订单异常：单号[" + orderNo + "]，交易号["
						+ tradeNo + "]，充值后[更新余额]未返回正确数据");
			} else {
				Logger.info("["+payType+"]在线充值订单正常：" + orderNo + "");
			}

		} catch (Exception e) {
			Logger.info("["+payType+"]在线充值订单异常：单号[" + orderNo + "]，交易号[" + tradeNo
					+ "]异常信息：" + e.getMessage());
			e.printStackTrace();
		}
	}

}
