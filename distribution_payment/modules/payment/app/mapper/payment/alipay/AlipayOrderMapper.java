package mapper.payment.alipay;

import entity.payment.alipay.AlipayOrder;
import mapper.payment.BaseMapper;

import java.util.List;

/**
 * 支付宝下单Mapper
 * @author luwj
 *
 */
public interface AlipayOrderMapper extends BaseMapper<AlipayOrder> {

    public List<AlipayOrder> getAlipayOrders(AlipayOrder alipayOrder);
}