package services.sales.impl;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;
import events.sales.OrderOnlineEvent;
import mapper.sales.SaleBaseMapper;
import mapper.sales.SaleDetailMapper;
import mapper.sales.SaleMainMapper;
import org.apache.commons.collections.CollectionUtils;
import services.sales.IKdnService;

import java.util.List;

/**
 * @author longhuashen
 * @since 2017/5/20
 */
public class KdnService implements IKdnService {


    @Inject
    private SaleBaseMapper saleBaseMapper;

    @Inject
    private SaleDetailMapper saleDetailMapper;

    @Inject
    private SaleMainMapper saleMainMapper;

    @Inject
    private EventBus eventBus;

    @Override
    public void requestOrderOnline(SaleMain saleMain) {
        //生成电子面单 2017-5-17
        SaleBase saleBase = saleBaseMapper.selectByOrderId(saleMain.getId());
        List<SaleDetail> saleDetails = saleDetailMapper.selectByOrderId(saleMain.getId());
        if (saleBase != null && !CollectionUtils.isEmpty(saleDetails)) {
            eventBus.post(new OrderOnlineEvent(saleBase, saleMain, saleDetails));
        } else {
            saleMain.setIsPushed(0);
            saleMainMapper.updateByPrimaryKeySelective(saleMain);
        }
    }
}
