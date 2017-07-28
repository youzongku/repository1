package services.sales;

import entity.sales.SaleMain;

/**
 * 快递鸟Service
 *
 * @author longhuashen
 * @since 2017/5/20
 */
public interface IKdnService {

    /**
     * 请求快递鸟电子面单API
     *
     * @param saleMain 订单主体
     */
    void requestOrderOnline(SaleMain saleMain);
}
