package services.sales.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import entity.sales.OrderPack;
import entity.sales.SaleBase;
import entity.sales.SaleMain;
import events.sales.JdLogisticsEvent;
import events.sales.LogisticsEvent;
import mapper.sales.OrderPackMapper;
import mapper.sales.SaleBaseMapper;
import mapper.sales.SaleMainMapper;
import play.Logger;
import services.sales.IOrderPackService;
import services.sales.ISaleMainService;

/**
 * Created by LSL on 2016/1/20.
 */
public class OrderPackService implements IOrderPackService {

	@Inject private OrderPackMapper orderPackMapper;
	@Inject private SaleMainMapper saleMainMapper;
	@Inject private SaleBaseMapper saleBaseMapper;
	@Inject private ISaleMainService saleMainService;
    @Inject private EventBus ebus;

    @Override
    public boolean batchInsert(List<OrderPack> orderPacks) {
        return orderPackMapper.batchInsert(orderPacks) == orderPacks.size();
    }

    @Override
    public boolean batchAdd(List<OrderPack> orderPacks) {
        int lines = 0;
        List<OrderPack> postListMSite = Lists.newArrayList();
        List<SaleBase> sbs = Lists.newArrayList();
        List<SaleMain> sms = Lists.newArrayList();
        List<OrderPack> ops = Lists.newArrayList();
        String salesOrderNo = "";
        SaleMain sale = null;
        SaleBase base = null;
        for (OrderPack orderPack : orderPacks) {
            OrderPack op = orderPackMapper.getOrderPackByOrderNumberAndTrackNumber(
                orderPack.getCordernumber(), orderPack.getCtrackingnumber()
            );
            if (op == null) {
                lines += orderPackMapper.insertSelective(orderPack);
                //新增操作给更新状态
                //更新销售单主表状态为“待收货”  就是“已发货”状态 
                salesOrderNo = orderPack.getCordernumber();
                sale = saleMainMapper.selectByOrderNo(salesOrderNo);
                if(null != sale) {
                	base = saleBaseMapper.selectByOrderId(sale.getId());
                	sale.setStatus(9);
                	sale.setOrderSendDate(new Date());
                	saleMainService.updateByPrimaryKeySelective(sale);
                	if(salesOrderNo.startsWith("MS-")){
                		postListMSite.add(orderPack);
                	}
                	if(null != base.getPlatformType()&&(base.getPlatformType() == 14||base.getPlatformType()== 13 || base.getPlatformType() == 4)) {
            			sms.add(sale);
                		sbs.add(base);
                		ops.add(orderPack);
                	}
//                	saleService.syncLogs(sale, Constant.UPDATE);                	
                }
            } else {
                orderPack.setIid(op.getIid());
                lines += orderPackMapper.updateByPrimaryKeySelective(orderPack);
            }
        }
        if(CollectionUtils.isNotEmpty(sms)) {
        	ebus.post(new LogisticsEvent(sbs, sms, ops));

            //处理京东的单
            ebus.post(new JdLogisticsEvent(sbs, sms, ops));
        }
        //M站订单操作
        if(postListMSite.size() >0){
        	saleMainService.batchPushStatusToMSite(postListMSite);
        }
        
        return lines > 0;
    }
    
    @Override
    public boolean batchUpdate(JsonNode node) {
        Integer trackcode = node.get("isTrackCode").asInt();
        ArrayNode temps = (ArrayNode) node.get("products");
        int lines = 0;
        for (JsonNode temp : temps) {
            List<OrderPack> orderPacks = orderPackMapper.getOrderPackByOrderNumberAndSKU(
                temp.get("cordernumber").asText(), temp.get("sku").asText()
            );
            if (orderPacks == null || orderPacks.size() != 1) continue;
            OrderPack orderPack = orderPacks.get(0);
            if (trackcode == 1) {
                orderPack.setCtrackingnumber(node.get("trackNumber").asText());
            } else {
                orderPack.setClocaltracknumber(node.get("localTrackNumber").asText());
            }
            orderPack.setIisregister(trackcode);
            //更新销售单物流信息
            lines += orderPackMapper.updateByPrimaryKeySelective(orderPack);
            
            //更新销售单主表状态为“待收货”  就是“已发货”状态 
            SaleMain sale = saleMainMapper.selectByOrderNo(orderPack.getCordernumber());
            if (sale != null) {
                sale.setStatus(9);
                saleMainService.updateByPrimaryKeySelective(sale);
                Logger.info("待收货-->" + sale.getSalesOrderNo());
            }
        }
        Logger.info("products-->" + temps.size() + "    lines-->" + lines);
        return lines > 0 ? true : false;
    }

    @Override
    public List<OrderPack> getOrderPacksByCondition(JsonNode node) {
        String orderNumber = node.has("orderNo") ? node.get("orderNo").asText() : null;
        String sku = (node.has("sku") && !node.get("sku").asText().isEmpty()) ? node.get("sku").asText() : null;
        return orderPackMapper.getOrderPackByOrderNumberAndSKU(orderNumber, sku);
    }
}
