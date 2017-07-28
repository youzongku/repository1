package mapper.payment.alipay;

import entity.payment.alipay.AlipayResult;
import mapper.payment.BaseMapper;

import java.util.List;

/**
 * 支付宝支付结果Mapper
 * @author luwj
 *
 */
public interface AlipayResultMapper extends BaseMapper<AlipayResult> {

    public List<AlipayResult> getAlipayResults(AlipayResult alipayResult);
}