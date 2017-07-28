package controllers.payment;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import dto.payment.ReturnMess;
import dto.payment.alipay.PayIterm;
import entity.payment.alipay.AlipayOrder;
import entity.payment.alipay.AlipayResult;
import mapper.payment.alipay.AlipayConfigMapper;
import mapper.payment.alipay.AlipayOrderMapper;
import mapper.payment.alipay.AlipayResultMapper;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.payment.IAlipayService;
import services.payment.IDealInventoryService;
import utils.payment.AlipayUtil;
import utils.payment.DateTimeUtils;
import utils.payment.HttpUtil;
import utils.payment.PayUtil;

/**
 * 接收支付宝同步、异步返回
 * @author luwj
 *
 */
public class Alipay extends Controller {

	@Inject
	IAlipayService iAlipayService;

	@Inject
	AlipayConfigMapper alipayConfigMapper;

	@Inject
	AlipayResultMapper alipayResultMapper;

	@Inject
	AlipayOrderMapper alipayOrderMapper;
	
	@Inject
    private IDealInventoryService inventoryService;

	/**
	 * 接收支付宝异步支付结果
	 * @return
	 */
	public Result receiveAlipayAsy() {
		Logger.info(">>>>>>异步>>>>>" + request().body().toString());
		Map<String, String[]> body= request().body().asFormUrlEncoded();
		String notifyId = AlipayUtil.checkNotifyParam(body.get("notify_id"));
//		String bool = iAlipayService.verifyNotify(notifyId);
		//校验通知是否合法
		boolean isSignOk = iAlipayService.isRightfulNotify(body);
		if(isSignOk){
			String notifyType = AlipayUtil.checkNotifyParam(body.get("notify_type"));
			String notifyTime = AlipayUtil.checkNotifyParam(body.get("notify_time"));
			Logger.info(">>>>notifyTime>>>>>>>" + notifyTime);
			String sign = AlipayUtil.checkNotifyParam(body.get("sign"));
			String signType = AlipayUtil.checkNotifyParam(body.get("sign_type"));
			String outTradeNo = AlipayUtil.checkNotifyParam(body.get("out_trade_no"));
			String tradeStatus = AlipayUtil.checkNotifyParam(body.get("trade_status"));
			Logger.info(">>>>>tradeStatus>>>>" + tradeStatus);
			String tradeNo = AlipayUtil.checkNotifyParam(body.get("trade_no"));
			String currency = AlipayUtil.checkNotifyParam(body.get("currency"));
			String totalFee = AlipayUtil.checkNotifyParam(body.get("total_fee"));
			
			boolean flag = outTradeNo.contains("sid");//判断是否包含单sid 包含说明是客户订单
			String sid = "";
			if(flag){
				sid = outTradeNo.substring(outTradeNo.lastIndexOf("d")+1);
				outTradeNo = outTradeNo.substring(0,outTradeNo.indexOf("s"));
			}
			AlipayOrder alipayOrder = new AlipayOrder();
			alipayOrder.setOrderNo(outTradeNo);
			//查询用户最近下的订单
			List<AlipayOrder> alipayOrders = alipayOrderMapper.getAlipayOrders(alipayOrder);
			Logger.info(">>>>>>>>>>>" + (alipayOrders!=null?alipayOrders.size():0));
			String orderId="";
			String rmbFee = "";
			if(alipayOrders != null && alipayOrders.size() > 0){
				orderId = alipayOrders.get(0).getOrderId();
				rmbFee = alipayOrders.get(0).getRmbFee();
			}
			if(AlipayUtil.TRADE_STATUS.equals(tradeStatus)){
					//add by xuse 记录显示支付成功，同步相关信息到采购订单
					if(flag){
						inventoryService.callback2(outTradeNo,sid,totalFee,notifyTime,tradeNo,"alipay");
					}else{
						if(outTradeNo.toUpperCase().startsWith("CG")) {
							Map<String,String> payMap = Maps.newHashMap();
							try {
		            			payMap.put("tradeNo",tradeNo);
								payMap.put("payDate", notifyTime);
								payMap.put("payType","alipay");
							} catch (Exception e) {
								e.printStackTrace();
							}
		            		Logger.info("支付宝支付订单：" + outTradeNo);
		            		inventoryService.callback(outTradeNo,totalFee,"支付宝支付",payMap);
		            	} 
						else if(outTradeNo.toUpperCase().startsWith("CZ")){
							//add by duyt
							//CZ开头的交易号意为“在线充值单单号”
							inventoryService.onlinePaySuccessCallback(outTradeNo, tradeNo, "alipay");
						}else {
		            		//支付成功  同步交易号到销售订单
		            		PayUtil.syncPayInfoToSaleOrder(outTradeNo,
		            				new DateTime().toString("yyyy-MM-dd HH:mm:ss"),
		            				tradeNo, "alipay", "CNY", "6", "", "");            		
		            	}
					}
				
			}
			AlipayResult alipayResult = new AlipayResult();
			alipayResult.setOutTradeNo(outTradeNo);
			List<AlipayResult> lists = alipayResultMapper.getAlipayResults(alipayResult);
			Logger.info(">>>>receiveAlipayAsy>>>>lists>" + (lists!=null?lists.size():0));
			if(lists == null || lists.size() <= 0){
				alipayResult.setNotifyId(notifyId);
				alipayResult.setNotifyType(notifyType);
				alipayResult.setNotifyTime(notifyTime);
				alipayResult.setSign(sign);
				alipayResult.setSignType(signType);
				alipayResult.setTradeNo(tradeNo);
				alipayResult.setTradeStatus(tradeStatus);
				alipayResult.setCurrency(currency);
				alipayResult.setTotalFee(totalFee);
				alipayResult.setOrderId(orderId);
				alipayResult.setRmbFee(rmbFee);
				alipayResult.setCreateDate(new Date());
				int saveResult = alipayResultMapper.insertSelective(alipayResult);
				Logger.info(">>>>>>>saveResult>>>>" + saveResult);
			}else{
				alipayResult = lists.get(0);
				if(!AlipayUtil.TRADE_STATUS.equals(alipayResult.getTradeStatus())){
					alipayResult.setNotifyId(notifyId);
					alipayResult.setNotifyType(notifyType);
					alipayResult.setNotifyTime(notifyTime);
					alipayResult.setSign(sign);
					alipayResult.setSignType(signType);
					alipayResult.setTradeNo(tradeNo);
					alipayResult.setTradeStatus(tradeStatus);
					alipayResult.setCurrency(currency);
					alipayResult.setTotalFee(totalFee);
					alipayResult.setOrderId(orderId);
					alipayResult.setRmbFee(rmbFee);
					alipayResult.setLastUpdateDate(new Date());
					int saveResult = alipayResultMapper.updateByPrimaryKeySelective(alipayResult);
					Logger.info(">>>>>>>saveResult>>>>" + saveResult);
				}
			}
			return ok("SUCCESS");
		}
		return ok("FAIL");
	}

	/**
	 * 接收支付宝同步支付结果
	 * @return
	 */
	public Result receiveAlipaySyn(){
		Logger.info(">>>>>>同步>>>>>" + Json.toJson(request().queryString()).toString());
		String sign = request().getQueryString("sign");
		String tradeNo = request().getQueryString("trade_no");
		String totalFee = request().getQueryString("total_fee");
		String signType = request().getQueryString("sign_type");
		String outTradeNo = request().getQueryString("out_trade_no");
		String tradeStatus = request().getQueryString("trade_status");
		String notifyTime = request().getQueryString("notify_time");
		// add by zbc 
		String notifyId = request().getQueryString("notify_id");
		String notifyType = request().getQueryString("notify_type");
		
		boolean flag = outTradeNo.contains("sid");//判断是否包含单sid 包含说明是客户订单
		String sid = "";
		if(flag){
			sid = outTradeNo.substring(outTradeNo.lastIndexOf("d")+1);
			outTradeNo = outTradeNo.substring(0,outTradeNo.indexOf("s"));
		}
		Logger.debug(">>>>tradeStatus>>>>>"+tradeStatus);
		String currency = request().getQueryString("currency");
		AlipayResult alipayResult = new AlipayResult();
		alipayResult.setOutTradeNo(outTradeNo);
		List<AlipayResult> lists = alipayResultMapper.getAlipayResults(alipayResult);
		Logger.info(">>>>receiveAlipaySyn>>>>lists>" + (lists!=null?lists.size():0));
		AlipayOrder alipayOrder = new AlipayOrder();
		alipayOrder.setOrderNo(outTradeNo);
		//查询用户最近下的订单
		List<AlipayOrder> alipayOrders = alipayOrderMapper.getAlipayOrders(alipayOrder);
		Logger.info(">>>>>>>>>>>" + (alipayOrders!=null?alipayOrders.size():0));
		String orderId="";
		String rmbFee = "";
		if(alipayOrders != null && alipayOrders.size() > 0){
			orderId = alipayOrders.get(0).getOrderId();
			rmbFee = alipayOrders.get(0).getRmbFee() != null?alipayOrders.get(0).getRmbFee():"" ;
		}
		boolean isok=false;
		if(lists != null && lists.size() > 0){
			alipayResult = lists.get(0);
			if(!AlipayUtil.TRADE_STATUS.equals(alipayResult.getTradeStatus())){
				alipayResult.setSign(sign);
				alipayResult.setSignType(signType);
				alipayResult.setTradeNo(tradeNo);
				alipayResult.setTradeStatus(tradeStatus);
				alipayResult.setCurrency(currency);
				alipayResult.setTotalFee(totalFee);
				alipayResult.setLastUpdateDate(new Date());
				int saveResult = alipayResultMapper.updateByPrimaryKeySelective(alipayResult);
				Logger.info(">>>>>>>saveResult>>>>" + saveResult);
			}
		}else{
			//add by zbc 同步回调保存支付结果
			alipayResult.setNotifyId(notifyId);
			alipayResult.setNotifyType(notifyType);
			alipayResult.setNotifyTime(notifyTime);
			alipayResult.setSign(sign);
			alipayResult.setSignType(signType);
			alipayResult.setTradeNo(tradeNo);
			alipayResult.setTradeStatus(tradeStatus);
			alipayResult.setCurrency(currency);
			alipayResult.setTotalFee(totalFee);
			alipayResult.setOrderId(orderId);
			alipayResult.setRmbFee(rmbFee);
			alipayResult.setCreateDate(new Date());
			int saveResult = alipayResultMapper.insertSelective(alipayResult);
			Logger.info(">>>>>>>saveResult>>>>" + saveResult);
		}
		//前台支付判断
		boolean frontPay = false;
		if (AlipayUtil.TRADE_STATUS.equals(tradeStatus)) {
			isok = true;
			//add by xuse 
			Logger.info("支付宝支付订单：" + outTradeNo);
			if(flag){
				inventoryService.callback2(outTradeNo,sid,totalFee,notifyTime,tradeNo,"alipay");
				frontPay = true;
			}else{
				if(outTradeNo.toUpperCase().startsWith("CG")) {
					frontPay = true;
					Map<String,String> payMap = Maps.newHashMap();
					try {
            			payMap.put("tradeNo",tradeNo);
						payMap.put("payDate", notifyTime);
						payMap.put("payType","alipay");
					} catch (Exception e) {
						e.printStackTrace();
					}
            		Logger.info("支付宝支付订单：" + outTradeNo);
            		inventoryService.callback(outTradeNo,totalFee,"支付宝支付",payMap);
            	} 
				else if(outTradeNo.toUpperCase().startsWith("CZ")){
					//add by duyt
					//CZ开头的交易号意为“在线充值单单号”
					inventoryService.onlinePaySuccessCallback(outTradeNo, tradeNo, "alipay");
				}
				else {
            		//支付成功  同步交易号到销售订单
            		PayUtil.syncPayInfoToSaleOrder(outTradeNo,
            				new DateTime().toString("yyyy-MM-dd HH:mm:ss"),
            				tradeNo, "alipay", "CNY", "6", "", "");            		
            	}
			}
		}
		String host = HttpUtil.BBC_HOST;
		if(frontPay){
			if(isok){
				host += "/product/pay-success.html?transamount="+totalFee+"&isok="+isok+"&od="+outTradeNo;
			}else{
				host += "/product/pay-success.html?isok="+isok+"&od="+outTradeNo;
			}
		}else{
			if(isok)
				host += "/backstage/pay_success.html?isok="+isok+"&transamount="+rmbFee;
			else
				host += "/backstage/pay_success.html?isok="+isok;
		}
		Logger.debug(">>>>>host>>>>>"+host);
		return  redirect(host);
	}

	/**
	 * 查询支付宝支付结果
	 * @return
	 */
	public Result queryAlipayResult(){
		Logger.info("queryAlipayResult    params--->" + Json.toJson(request().queryString()).toString());
		ObjectNode result = JsonNodeFactory.instance.objectNode();
		String orderNo = request().getQueryString("orderNo");
		if (Strings.isNullOrEmpty(orderNo)) {
			Logger.info("gainWechatPayResult：请求参数不存在或格式错误");
			result.put("suc", false);
		} else {
			AlipayResult alipayResult = new AlipayResult();
			alipayResult.setOutTradeNo(orderNo);
			List<AlipayResult> lists = alipayResultMapper.getAlipayResults(alipayResult);
			result.put("suc", false);
			if (lists != null && lists.size() > 0) {
				alipayResult = lists.get(0);
				String tradeStatus = alipayResult.getTradeStatus();
				if(tradeStatus != null && AlipayUtil.TRADE_STATUS.equals(tradeStatus)){
					result.put("suc", true);
				} else {
					result.put("suc", false);
				}
			} else {
				result.put("suc", false);
			}
		}
		Logger.info("queryAlipayResult    result--->" + result.toString());
		return ok(result);
	}

	/**
	 * 支付
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result alipayGateway(){
		ReturnMess re = new ReturnMess("0","");
		PayIterm iterm = new PayIterm();
		try {
			JsonNode node = request().body().asJson();
			((ObjectNode)node).put("vhost",request().host());
			iterm = iAlipayService.alipayGateway(node);
		}catch (Exception e){
			Logger.debug("Exception :" , e);
			re = new ReturnMess("1","异常情况");
			iterm = new PayIterm(null,re);
		}
		return ok(Json.toJson(iterm));
	}

	public Result test(){
		ObjectMapper obj = new ObjectMapper();
		ObjectNode node = obj.createObjectNode();
		node.put("saleOrderNo","XS201601272051255");
		node.put("payDate", DateTimeUtils.date2string(new Date(),DateTimeUtils.FORMAT_FULL_DATETIME));
		node.put("payNo","tb_3234seewr");
		node.put("payType","alipay");//支付方式
		node.put("currency","CNY");//支付币种
		HttpUtil.post(node.toString(), "/sales/updPayInfo");
		return ok("ok");
	}

	/**
	 * 申请支付宝退款
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result applyAlipayRefund() {
		ObjectNode result = JsonNodeFactory.instance.objectNode();
		JsonNode node = request().body().asJson();
		if (node == null || !node.has("orderNo") || !node.has("reason")) {
			Logger.info("applyAlipayRefund：请求参数不存在或格式错误");
			result.put("errorCode", "1");
			result.put("errorInfo", "请求参数不存在或格式错误");
		} else {
			result = (ObjectNode) Json.toJson(iAlipayService.forexRefund(node));
		}
		return ok(result);
	}
	
}