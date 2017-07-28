package handlers.sales;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import dto.sales.TaoBaoOrderForm;
import entity.platform.order.template.TaoBaoOrder;
import entity.platform.order.template.TaoBaoOrderGoods;
import events.sales.ImportOrderSyncEvent;
import events.sales.SalesVolumeCountEvent;
import events.sales.SupplementImportOrderInfoEvent;
import mapper.sales.SaleMainMapper;
import mapper.sales.TaoBaoOrderGoodsMapper;
import mapper.sales.TaoBaoOrderMapper;
import org.apache.commons.collections.CollectionUtils;
import play.Logger;
import services.sales.IHttpService;
import services.sales.ISaleReceiverService;
import services.sales.impl.ImportOrderService;
import util.sales.AddressUtils;
import util.sales.JsonCaseUtil;
import util.sales.StringUtils;

public class SalesVolumeCountHandler {

    @Inject
    private SaleMainMapper saleMainMapper;

    @Inject
    private TaoBaoOrderMapper taoBaoOrderMapper;

    @Inject
    private TaoBaoOrderGoodsMapper taoBaoOrderGoodsMapper;

    @Inject
    private IHttpService httpService;

    @Inject
    private ISaleReceiverService saleReceiverService;

    @Inject
    private ImportOrderService importOrderService;


    /**
     * 统计当天商品销量
     *
     * @param event
     */
    @Subscribe
    public void executeSalesVolumeCount(SalesVolumeCountEvent event) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar time = Calendar.getInstance();
            int year = time.get(Calendar.YEAR);
            int month = time.get(Calendar.MONTH);
            int day = time.get(Calendar.DAY_OF_MONTH);
            time.set(year, month, day, 00, 00, 00);
            Date benginTime = time.getTime();
            String begin = sdf.format(benginTime);
            time.set(year, month, day, 23, 59, 00);
            Date endTime = time.getTime();
            String end = sdf.format(endTime);
            Logger.info("执行销量统计函数参数{},{}", begin, end);
            int i = saleMainMapper.executSalesVolumeCountFunction(begin, end);
            Logger.info("执行销量统计定时任务返回结果{}", i);
        } catch (Exception e) {
            Logger.info("执行销量统计定时任务发生异常{}", e);
        }
    }

    @Subscribe
    public void executeSupplementImportOrderInfo(SupplementImportOrderInfoEvent event) {
        Logger.info(">>>>>>>>>>>>>>>>>>执行系统智能补充符合条件下订单的商品信息");
        TaoBaoOrderForm taoBaoOrderForm = new TaoBaoOrderForm();
        taoBaoOrderForm.setIsComplete(2);
        // 查询订单
        List<TaoBaoOrder> allOrders = taoBaoOrderMapper.getAllOrders(taoBaoOrderForm);
        // 根据订单查询商品
        if (CollectionUtils.isNotEmpty(allOrders)) {

            //按email分组
            Map<String, List<TaoBaoOrder>> groupByEmail = allOrders.stream().collect(Collectors.groupingBy(d -> d.getEmail()));

            Set<Map.Entry<String, List<TaoBaoOrder>>> entries = groupByEmail.entrySet();
            Iterator<Map.Entry<String, List<TaoBaoOrder>>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<TaoBaoOrder>> next = iterator.next();
                String email = next.getKey();
                List<TaoBaoOrder> list = next.getValue();

                JsonNode memberNode = null;
                try {
                    // 获取用户详情
                    memberNode = httpService.getMemberInfo(email);
                    if (memberNode == null || !memberNode.has("comsumerType") || !memberNode.has("distributionMode")) {
                        continue;
                    }
                    Integer distributionMode = memberNode.get("distributionMode").asInt();

                    for (TaoBaoOrder taoBaoOrder : list) {
                        List<TaoBaoOrderGoods> allGoods = taoBaoOrderGoodsMapper.goodsLists(Lists.newArrayList(taoBaoOrder.getOrderNo()), email);
                        if (CollectionUtils.isNotEmpty(allGoods)) {
                            Set<String> skus = Sets.newHashSet();
                            skus.addAll(Lists.transform(allGoods, good -> good.getSku()));
                            JsonNode productStrNode = null;
                            JsonNode result = null;
                            if (skus.size() > 0) {
                                try {
                                    productStrNode = httpService.getProducts(taoBaoOrderForm.getEmail(), Lists.newArrayList(skus), null,
                                            distributionMode);
                                    if (productStrNode.get("data") != null && productStrNode.get("data").get("result") != null) {
                                        result = productStrNode.get("data").get("result");
                                    }
                                } catch (Exception e) {
							        Logger.error("getProducts:" + e);
                                }
                            }

                            List<Map<String, Object>> warehouseNameIds = null;
                            Map<String, Object> warehouseNameId = null;
                            for (TaoBaoOrderGoods good : allGoods) {
                                warehouseNameIds = new ArrayList<Map<String, Object>>();
                                for (JsonNode product : result) {
                                    warehouseNameId = Maps.newHashMap();
                                    if (good.getSku().equals(JsonCaseUtil.getStringValue(product, "csku"))) {
                                        warehouseNameId.put("warehouseId", product.get("warehouseId").asText());
                                        warehouseNameId.put("warehouseName", product.get("warehouseName").asText());
                                        good.setCtitle(product.get("ctitle").asText());
                                        good.setImageUrl(product.get("imageUrl").asText());
                                        good.setBatchNumber(product.get("batchNumber").asText());
                                        warehouseNameIds.add(warehouseNameId);
                                    }
                                }
                                good.setWarehouseNameId(warehouseNameIds);
                            }
                            // key = email_orderno value = List<TaoBaoOrderGoods>
                            Map<String, List<TaoBaoOrderGoods>> ordersMap = allGoods.stream().collect(Collectors.groupingBy(e -> {
                                return e.getEmail() + "_" + e.getOrderNo();
                            }));
                            for (TaoBaoOrder order : list) {
                                order.setGoods(ordersMap.get(order.getEmail() + "_" + order.getOrderNo()));
                            }
                        }

                        List<TaoBaoOrderGoods> targetTaobaoOrderGoods = taoBaoOrder.getGoods();
                        if (CollectionUtils.isNotEmpty(targetTaobaoOrderGoods)) {

                            int size = targetTaobaoOrderGoods.size();
                            int count = (int) targetTaobaoOrderGoods.stream().filter(d -> d.getWarehouseNameId().size() == 1).count();

                            boolean flag = true;
                            if (size == count) {
                                int warehouseId = Integer.parseInt(String.valueOf(taoBaoOrder.getGoods().get(0).getWarehouseNameId().get(0).get("warehouseId")));
                                String warehouseName = String.valueOf(taoBaoOrder.getGoods().get(0).getWarehouseNameId().get(0).get("warehouseName"));
                                for (int i = 0; i < targetTaobaoOrderGoods.size(); i++) {

                                    if (i == 0) {
                                        continue;
                                    }
                                    TaoBaoOrderGoods taoBaoOrderGoods = targetTaobaoOrderGoods.get(i);

                                    int otherWarehouseId = Integer.parseInt(String.valueOf(taoBaoOrderGoods.getWarehouseNameId().get(0).get("warehouseId")));
                                    if (otherWarehouseId != warehouseId) {
                                        flag = false;
                                    }
                                }

                                if (flag) {//表明只能是属于某个个仓库的
                                    for (TaoBaoOrderGoods taoBaoOrderGoods : targetTaobaoOrderGoods) {
                                        if (StringUtils.isBlankOrNull(taoBaoOrderGoods.getWarehouseId())) {
                                            taoBaoOrderGoods.setWarehouseId(String.valueOf(warehouseId));
                                            taoBaoOrderGoods.setWarehouseName(warehouseName);
                                            taoBaoOrderGoodsMapper.saveGoodsInfo(taoBaoOrderGoods);
                                        }
                                    }

                                    if (taoBaoOrder.getIsComplete() == 2) {
                                        taoBaoOrder.setIsComplete(1);
                                        taoBaoOrder.setUpdateDate(new Date());


                                        if (StringUtils.isBlankOrNull(taoBaoOrder.getLogisticsTypeCode())) {

                                            JsonNode node = getShippingMethod(warehouseId);
                                            if (node != null) {
                                                String code = node.get("methodCode").asText();
                                                String methodName = node.get("methodName").asText();

                                                taoBaoOrder.setLogisticsTypeCode(code);
                                                taoBaoOrder.setLogisticsTypeName(methodName);
                                            }


                                            //省-市-区是否匹配
                                            boolean addressFlag = AddressUtils.isAdjustAddress(taoBaoOrder.getAddress());

                                            if (addressFlag) {
                                                Logger.info(">>>>>>>>>>>executeSupplementImportOrderInfo 补全订单信息：{}", taoBaoOrder.toString());
                                                taoBaoOrderMapper.updateByPrimaryKeySelective(taoBaoOrder);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.error(">>>>>>>>>>>>executeSupplementImportOrderInfo getMemberInfo:" + e);
                }
            }
        }
    }


    /**
     * 获取默认物流方式
     *
     * @param warehouseId
     * @return
     */
    private JsonNode getShippingMethod(int warehouseId) {
        //获取物流方式
        JsonNode methodNode = null;
        try {
            methodNode = httpService.getShoppingMethod(warehouseId);
        } catch (IOException e) {
            Logger.error(">>>>>>>>>>>>>>>>>>>获取物流方式错误：{}", e);
        }
        if (methodNode == null) {
            return null;
        }

        Iterator<JsonNode> it = methodNode.iterator();
        JsonNode node = null;
        while (it.hasNext()) {
            node = (JsonNode) it.next();
            if (node.get("default").asBoolean()) {
                break;
            }
        }

        return node == null ? (methodNode.get(0)) : node;
    }

    @Subscribe
    public void completionOrderInfoSync(ImportOrderSyncEvent event) {
        Logger.info("补全订单信息>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        importOrderService.completionOrderInfo(event.getEmail());
    }
}
