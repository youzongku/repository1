package events.sales;

import entity.sales.OrderPack;
import entity.sales.SaleBase;
import entity.sales.SaleMain;

import java.util.List;

/**
 * @author longhuashen
 * @since 2017/5/16
 */
public class JdLogisticsEvent extends LogisticsEvent {

    public JdLogisticsEvent(List<SaleBase> sbs, List<SaleMain> sms, List<OrderPack> ops) {
        super(sbs, sms, ops);
    }
}
