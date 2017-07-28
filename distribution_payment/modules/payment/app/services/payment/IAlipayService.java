package services.payment;

import com.fasterxml.jackson.databind.JsonNode;
import dto.payment.alipay.PayIterm;
import entity.payment.alipay.AlipayConfig;

import java.util.Map;

/**
 *
 * @author luwj
 *
 */
public interface IAlipayService {

	/**
	 * 支付宝网关支付
	 * @param node
	 * @return
	 */
	public PayIterm alipayGateway(JsonNode node);

	/**
	 * 获取支付宝汇率文件接口
	 * @param currency
	 * @param config
	 * @return
	 */
	public String getExchangeRate(String currency, AlipayConfig config);

	/**
	 * 校验通知合法性
	 * @param notifyId
	 * @return
	 */
	public String verifyNotify(String notifyId);

	/**
	 * 国际支付宝退款
	 * @param json
	 * {
	 *     orderNo: orderNo,
	 *     reason: reason,
	 *     //orderId: orderId
	 * }
	 * @return
	 */
	public Map<String, String> forexRefund(JsonNode json);

	/**
	 * 校验回调参数是否合法
	 * @param body : 参数
	 * @return : boolean
	 */
	public boolean isRightfulNotify(Map<String, String[]> body);
}