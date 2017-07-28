package services.payment.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import dto.payment.ReturnMess;
import dto.payment.alipay.PayIterm;
import dto.payment.alipay.PayParam;
import dto.payment.alipay.ReqParam;
import entity.payment.alipay.AlipayConfig;
import entity.payment.alipay.AlipayOrder;
import entity.payment.alipay.AlipayRefund;
import entity.payment.alipay.AlipayResult;
import entity.payment.alipay.enums.Status;
import mapper.payment.alipay.AlipayConfigMapper;
import mapper.payment.alipay.AlipayOrderMapper;
import mapper.payment.alipay.AlipayRefundMapper;
import mapper.payment.alipay.AlipayResultMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import play.Logger;
import play.libs.Json;
import services.payment.IAlipayService;
import utils.payment.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付宝支付
 * @author luwj
 *
 */
public class AlipayService implements IAlipayService {

	public static final Integer TYPES_1 = 1;

	public static final Integer TYPES_2 = 2;

	@Inject
	AlipayConfigMapper iAlipayConfigMapper;

	@Inject
	AlipayOrderMapper iAlipayOrderMapper;

	@Inject
	AlipayResultMapper iAlipayResultMapper;

	@Inject
	AlipayRefundMapper iAlipayRefundMapper;

	/**
	 * 支付宝网关支付
	 * @param node json
	 * @return PayIterm
	 */
	@Override
	public PayIterm alipayGateway(JsonNode node) {
		Logger.info(">>>>alipayGateway>>>>param>>>" + node.toString());
		PayParam payParam = new PayParam();
		ReturnMess returnMess = new ReturnMess("0","");
		try{
			ObjectMapper obj = new ObjectMapper();
			ReqParam reqParam = obj.readValue(node.toString(), ReqParam.class);
			AlipayConfig config =  iAlipayConfigMapper.getAlipayConfig(TYPES_2);
			reqParam.setCurrency(config.getExchangeRate().trim());
			String postflag = node.has("postflag")? node.get("postflag").asText():"";
			String sid = node.get("order_id").asText();
			String outTradeNo = reqParam.getOut_trade_no();
			if("true".equals(postflag)){
				reqParam.setOut_trade_no(outTradeNo+"sid"+sid);
			}
			if(StringUtils.isNotBlank(reqParam.getTotal_fee())){//支付人民币金额
				reqParam.setService("create_direct_pay_by_user");
				reqParam.setSign_type(AlipayUtil.SIGN_TYPE);
				reqParam.setPayment_type("1");
				reqParam.set_input_charset(AlipayUtil.INPUT_CHARSET);
				reqParam.setPartner(config.getPartner().trim());
				reqParam.setSeller_id(config.getPartner().trim());
				reqParam.setNotify_url(config.getNotifyUrl().trim());
				reqParam.setBody(reqParam.getSubject());
				reqParam.setReturn_url(AlipayUtil.HTTP_ADD + reqParam.getVhost() + config.getReturnUrl().trim());
//              reqParam.setReturn_url(config.getReturnUrl().trim());	//测试时的配置,上线要改掉
//				reqParam.setSupplier(AlipayUtil.SUPPLIER);
				String inputCharset = AlipayUtil.INPUT_CHARSET;
				String key = config.getKey().trim();//
				Map<String, String> sParaTemp = new HashMap<String, String>();
				sParaTemp.put("service", reqParam.getService());
		        sParaTemp.put("partner", reqParam.getPartner());
		        sParaTemp.put("_input_charset", inputCharset);
				sParaTemp.put("sign_type", reqParam.getSign_type());
				sParaTemp.put("return_url", reqParam.getReturn_url());
				sParaTemp.put("notify_url", reqParam.getNotify_url());
				sParaTemp.put("out_trade_no", reqParam.getOut_trade_no());
				sParaTemp.put("subject", reqParam.getSubject());
				sParaTemp.put("payment_type", reqParam.getPayment_type());
				sParaTemp.put("total_fee", reqParam.getTotal_fee());
				sParaTemp.put("seller_id", reqParam.getSeller_id());
				sParaTemp.put("body", reqParam.getSubject());
//				sParaTemp.put("currency", reqParam.getCurrency());
//				sParaTemp.put("supplier", reqParam.getSupplier());
				Logger.info(">>>>alipayGateway>>>sParaTemp>>" + sParaTemp.toString());

				sParaTemp = AlipayUtil.paraFilter(sParaTemp);
				String buildStr = AlipayUtil.createLinkString(sParaTemp) + key;//拼装签名字符窜
				String sign = MD5Util.MD5Encode(buildStr, inputCharset);//签名
				Logger.info(">>>>alipayGateway>>>>>sign>>" + sign);

				BeanUtils.copyProperties(payParam, reqParam);
				payParam.setAction_url(config.getGatewayUrl().trim() + "?_input_charset=utf-8");
				payParam.setSign(sign);

				AlipayOrder alipayOrder = new AlipayOrder();
				alipayOrder.setOrderId(reqParam.getOrder_id());
				alipayOrder.setOrderNo(outTradeNo);
				alipayOrder.setRmbFee(reqParam.getRmb_fee());
				alipayOrder.setCurrency(reqParam.getCurrency());
				alipayOrder.setCreateDate(new Date());
				int result = iAlipayOrderMapper.insertSelective(alipayOrder);
				Logger.info(">>>>>>alipayGateway>>order>>result>>" + result);
			}
		}catch(Exception e){
			returnMess = new ReturnMess("1","异常：throw Exception!");
			Logger.error("Exception ",e);
			e.printStackTrace();
		}
		PayIterm payIterm = new PayIterm(payParam,returnMess);
		return payIterm;
	}

	/**
	 * 获取支付宝汇率文件接口
	 * @param currency 币种
	 * @param config 支付宝配置信息
	 * @return
	 */
	@Override
	public String getExchangeRate(String currency , AlipayConfig config){
		if(currency==null||currency.trim().toLowerCase().equals("cny")){
			return "1";
		}
		String exchangeRate = "";
		try{
			String key = config.getKey().trim();//密钥
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("service", AlipayUtil.FOREX_RATE_SERVICE);
	        sParaTemp.put("partner", config.getPartner().trim());
			sParaTemp.put("sign_type", AlipayUtil.SIGN_TYPE);
			Map<String, String> sParaTemps = AlipayUtil.paraFilter(sParaTemp);
			String buildStr = AlipayUtil.createLinkString(sParaTemps) + key;//拼装签名字符窜
			Logger.info(">>>>>buildStr>>>>>>>" + buildStr);
			String sign = MD5Util.MD5Encode(buildStr, AlipayUtil.INPUT_CHARSET);//签名
			sParaTemp.put("sign", sign);
			Logger.info(">>>>sParaTemp>>>>>" + sParaTemp);
			NameValuePair[] nameValuePair = AlipayUtil.generatNameValuePair(sParaTemp);
			String url = config.getGatewayUrl().trim();
			HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
			String returnStr = httpProtocolHandler.execute(AlipayUtil.METHOD_POST, AlipayUtil.INPUT_CHARSET, url, nameValuePair);
			Logger.info(">>>>getExchangeRate>>returnStr>>>" + returnStr);
			if(StringUtils.isNotBlank(returnStr) && returnStr.contains(currency)){
				//截取
				returnStr = returnStr.substring(returnStr.lastIndexOf(currency)+4, returnStr.length());
				Logger.info(">>>>>>returnStr>>>>" + returnStr);
				if(returnStr.contains("|"))
					exchangeRate = returnStr.substring(0,returnStr.indexOf("|"));
				else
					exchangeRate = returnStr;
			}
		}catch(Exception e){
			Logger.error(">>>>>AlipayService.getExchangeRate>>Exception>>", e);
		}
		return exchangeRate;
	}

	/**
	 * 校验通知合法性
	 * @param notifyId 通知验证 ID
	 * @return
	 */
	@Override
	public String verifyNotify(String notifyId){
		AlipayConfig config =  iAlipayConfigMapper.getAlipayConfig(TYPES_2);
		String return_xml = "false";
		String service = AlipayUtil.VERIFY_SERVICE;
		String partner = config.getPartner().trim();
		String url = config.getGatewayUrl().trim()+"?service="+service+"&partner="+partner+"&notify_id="+notifyId;
		try{
			HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
			return_xml = httpProtocolHandler.execute(AlipayUtil.METHOD_GET, AlipayUtil.INPUT_CHARSET, url, null);
			Logger.info(">>>>>return_xml>>>>>>" + return_xml);
		}catch(Exception e){
			Logger.error("Exception>>>", e);
			e.printStackTrace();
		}
		return return_xml;
	}

	/**
	 * 批量付款
	 * @return
	 */
	public String batchPayment(){
//		String service = AlipayUtil.BATCH_TRANS_NOTIFY;
//		AlipayConfig config =  iAlipayConfigMapper.getAlipayConfig(TYPES_2);
//		String partner = config.getPartner();
//		String inputCharset = AlipayUtil.INPUT_CHARSET;
//		String signType = AlipayUtil.SIGN_TYPE;
//		String notifyUrl = "";
//		String accountName = "";//付款账号名
//		String detailData = "";//付款的详细数据，最多支持1000笔。 格式为：流水号1^收款方账号1^收款账号姓名1^付款金额1^备注说明1|流水号2^收款方账号2^收款账号姓名2^付款金额2^备注说明2。 每条记录以“|”间隔。
//		String batchNo = "";//批量付款批次号
//		String batchNum = "";//付款总笔数
//		String batchFee = "";//付款总金额
//		String email = "";//付款账号
//		String pay_date = "";//支付日期20080107
//		String buyerAccountName = "";//付款账号别名
//		String extendParam = "";//业务扩展参数

		return "";
	}

	/**
	 * 国际支付宝退款
	 */
	@Override
	public Map<String,String> forexRefund(JsonNode json){
		Logger.info(">>>>>>>>>>>"+json.toString());
		String orderNo = json.get("orderNo").asText();
		String reson = json.get("reason").asText();
		//String iid = json.get("orderId").asText();
		int count = 0;
		String errorCode = "0";
		String errorInfo = "";
		Map<String,String> map = Maps.newHashMap();
		AlipayResult alipayResult = new AlipayResult();
		alipayResult.setOutTradeNo(orderNo);
		try{
			List<AlipayResult> lists = iAlipayResultMapper.getAlipayResults(alipayResult);
			Logger.info(">>>>>>lists>>>>>"+(lists!=null?lists.size():0));
			if(lists != null && lists.size() > 0){
				for(AlipayResult sult : lists){
					if(StringUtils.isNotBlank(sult.getTradeStatus()) && sult.getTradeStatus().equals(AlipayUtil.TRADE_STATUS)){
						alipayResult = sult;
						count = 1;
					}
				}
			}
			if(count > 0){
				//访问退款接口
				String returnMess = callRefund(alipayResult,reson);
				Logger.info(">>>>>forexRefund>>>resultStr>"+returnMess);
				JsonNode reJson = Json.parse(returnMess);
				String isSuccess = reJson.get("isSuccess").asText();
				if(Status.F.name().equals(isSuccess)){
					errorCode = "1";
					errorInfo = reJson.get("error").asText();
				}else{
					//更新订单状态
					//iPayBusiness.execute(Integer.parseInt(iid), orderNo, IOrderStatusService.REFUNDED,null);
				}
			}else{
				errorCode = "1";
				errorInfo = "无该订单支付成功记录";
			}
		}catch(Exception e){
			e.printStackTrace();
			errorCode = "1";
			errorInfo = "系统异常";
		}
		map.put("errorCode", errorCode);
		map.put("errorInfo", errorInfo);
		return map;
	}

	/**
	 * 调用支付宝退款接口
	 */
	public String callRefund(AlipayResult alipayResult,String reson) throws Exception{
		AlipayConfig config = iAlipayConfigMapper.getAlipayConfig(TYPES_2);
		String key = config.getKey().trim();
		String returnStr = "";
		String outReturnNo = IDUtils.buildRefundNo();//退款单号(自行生成，唯一)
		String outTradeNo = alipayResult.getOutTradeNo();
		String returnAmount = alipayResult.getTotalFee();//退款金额（外币）
		String returnRmbAmount = alipayResult.getRmbFee();//人民币金额
		String currency = alipayResult.getCurrency();//币种
		if(StringUtils.isBlank(returnRmbAmount)){
			AlipayOrder alipayOrder = new AlipayOrder();
			alipayOrder.setOrderNo(outTradeNo);
			List<AlipayOrder> alipayOrders = iAlipayOrderMapper.getAlipayOrders(alipayOrder);
			if(alipayOrders != null && alipayOrders.size() > 0){
				returnRmbAmount = alipayOrders.get(0).getRmbFee();
				Logger.info(">>>>>>>"+returnRmbAmount);
			}
		}
		String gmtReturn = DateTimeUtils.date2string(new Date(), DateTimeUtils.FORMAT_DATETIME_BACKEND);//退款时间

		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", AlipayUtil.FOREX_REFUND.trim());
		sParaTemp.put("partner", config.getPartner().trim());//商户在支付宝的用户 ID
		sParaTemp.put("_input_charset", AlipayUtil.INPUT_CHARSET);
		sParaTemp.put("sign_type", AlipayUtil.SIGN_TYPE);
		sParaTemp.put("out_return_no", outReturnNo);
		sParaTemp.put("out_trade_no", outTradeNo);
		sParaTemp.put("return_rmb_amount", returnRmbAmount);
		//sParaTemp.put("return_amount", returnAmount);
		sParaTemp.put("currency", currency);
		sParaTemp.put("gmt_return", gmtReturn);
		sParaTemp.put("reason", new String(reson.trim().getBytes(),AlipayUtil.INPUT_ISO));//退款原因
		Map<String, String> sParaTemps = AlipayUtil.paraFilter(sParaTemp);
		String buildStr = AlipayUtil.createLinkString(sParaTemps) + key;//拼装签名字符窜
		String sign = MD5Util.MD5Encode(buildStr, AlipayUtil.INPUT_CHARSET);//签名
		sParaTemp.put("sign", sign);
		Logger.info(">>callRefund>>sParaTemp>>>>>"+sParaTemp);
		NameValuePair[] nameValuePair = AlipayUtil.generatNameValuePair(sParaTemp);
		Logger.info(">>callRefund>>>>nameValuePair>>>>>"+Json.toJson(nameValuePair));
		String url = config.getGatewayUrl().trim();
		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
		returnStr = httpProtocolHandler.execute(AlipayUtil.METHOD_POST, AlipayUtil.INPUT_CHARSET, url, nameValuePair);
		Logger.info(">>>>returnStr>>>>>>>>"+returnStr);

		//解析返回值
		Element root = AlipayUtil.getRootElementByString(returnStr);
		String isSuccess = WechatUtil.resovleXml(root, "is_success");
		ObjectNode obj = JsonNodeFactory.instance.objectNode();
		String error = "";
		if(Status.F.name().equals(isSuccess)){
			error = WechatUtil.resovleXml(root, "error");
		}
		obj.put("error", error);
		obj.put("isSuccess", isSuccess);

		//保存退款记录
		AlipayRefund alipayRefund = new AlipayRefund();
		alipayRefund.setOutReturnNo(outReturnNo);
		alipayRefund.setOutTradeNo(outTradeNo);
		alipayRefund.setReturnAmount(returnAmount);
		alipayRefund.setReturnRmbAmount(returnRmbAmount);
		alipayRefund.setCurrency(currency);
		alipayRefund.setGmtReturn(gmtReturn);
		alipayRefund.setReason(reson);
		alipayRefund.setIsSuccess(isSuccess);
		alipayRefund.setError(error);
		alipayRefund.setCreateDate(new Date());
		int result = iAlipayRefundMapper.insertSelective(alipayRefund);
		Logger.info(">>save alipay refund result>>result>>"+result);
		return obj.toString();
	}

	/**
	 * 校验回调参数是否合法
	 * @param body : 参数
     * @return : boolean
     */
	public boolean isRightfulNotify(Map<String, String[]> body){
		boolean isOk = false;
		Map<String, String> sParaTemp = new HashMap<String, String>();
		buildSparamTemp(body, "notify_time", sParaTemp);		//通知时间
		buildSparamTemp(body, "notify_type", sParaTemp);		//通知类型
		buildSparamTemp(body, "notify_id", sParaTemp);			//通知校验ID
		buildSparamTemp(body, "sign_type", sParaTemp);			//签名方式
		buildSparamTemp(body, "out_trade_no", sParaTemp);		//商户网站唯一订单号
		buildSparamTemp(body, "subject", sParaTemp);			//商品名称
		buildSparamTemp(body, "payment_type", sParaTemp);		//支付类型
		buildSparamTemp(body, "trade_no", sParaTemp);			//支付宝交易号
		buildSparamTemp(body, "trade_status", sParaTemp);		//交易状态
		buildSparamTemp(body, "gmt_create", sParaTemp);			//交易创建时间
		buildSparamTemp(body, "gmt_payment", sParaTemp);		//交易付款时间
		buildSparamTemp(body, "gmt_close", sParaTemp);			//交易关闭时间
		buildSparamTemp(body, "refund_status", sParaTemp);		//退款状态
		buildSparamTemp(body, "gmt_refund", sParaTemp);			//退款时间
		buildSparamTemp(body, "seller_email", sParaTemp);		//卖家支付宝账号
		buildSparamTemp(body, "buyer_email", sParaTemp);		//买家支付宝账号
		buildSparamTemp(body, "seller_id", sParaTemp);			//卖家支付宝账户号
		buildSparamTemp(body, "buyer_id", sParaTemp);			//买家支付宝账户号
		buildSparamTemp(body, "price", sParaTemp);				//商品单价
		buildSparamTemp(body, "total_fee", sParaTemp);			//交易金额
		buildSparamTemp(body, "quantity", sParaTemp);			//购买数量
		buildSparamTemp(body, "body", sParaTemp);				//商品描述
		buildSparamTemp(body, "discount", sParaTemp);			//折扣
		buildSparamTemp(body, "is_total_fee_adjust", sParaTemp);//是否调整总价
		buildSparamTemp(body, "use_coupon", sParaTemp);			//是否使用红包买家
		buildSparamTemp(body, "extra_common_param", sParaTemp);	//公用回传参数
		buildSparamTemp(body, "business_scene", sParaTemp);		//是否扫码支付
		Map<String, String> sParaTemps = AlipayUtil.paraFilter(sParaTemp);
		AlipayConfig config =  iAlipayConfigMapper.getAlipayConfig(TYPES_2);
		String buildStr = AlipayUtil.createLinkString(sParaTemps) + config.getKey();//拼装签名字符窜f
		String sign = MD5Util.MD5Encode(buildStr, AlipayUtil.INPUT_CHARSET);//签名
		String _sign = AlipayUtil.checkNotifyParam(body.get("sign"));
		if(sign.equals(_sign))
			isOk = true;
		return isOk;
	}

	/**
	 * 构建签名参数map
	 * @param body : map
	 * @param name : 参数名
     */
	private void buildSparamTemp(Map<String, String[]> body, String name, Map<String, String> sParaTemp){
		if(body.containsKey(name))//是否扫码支付
			sParaTemp.put(name, AlipayUtil.checkNotifyParam(body.get(name)));
	}

	public static void main(String[] args) {
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "single_trade_query");
		sParaTemp.put("partner", "2088021169465822");
		sParaTemp.put("_input_charset", "utf-8");
		sParaTemp.put("sign_type", "MD5");
		sParaTemp.put("out_trade_no", "XS2016092316070900028780");
		Map<String, String> sParaTemps = AlipayUtil.paraFilter(sParaTemp);
		String buildStr = AlipayUtil.createLinkString(sParaTemps) + "zaqzdlcllk5buw0g0j2jwyf0t2z4uzmh";//拼装签名字符窜f
		String sign = MD5Util.MD5Encode(buildStr, AlipayUtil.INPUT_CHARSET);//签名
		sParaTemp.put("sign", sign);

		try {
			String retStr = HttpProtocolHandler.getInstance().execute("POST",
					"utf-8", "https://mapi.alipay.com/gateway.do", generatNameValuePair(sParaTemp));
			System.out.println(">>>retStr>>>>"+retStr);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * MAP类型数组转换成NameValuePair类型
	 * @param properties  MAP类型数组
	 * @return NameValuePair类型数组
	 */
	private static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
		NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
		int i = 0;
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
		}

		return nameValuePair;
	}
}

