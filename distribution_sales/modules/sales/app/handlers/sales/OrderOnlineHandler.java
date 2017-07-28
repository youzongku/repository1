package handlers.sales;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import entity.sales.*;
import events.sales.OrderOnlineEvent;
import mapper.sales.*;
import org.apache.commons.collections.CollectionUtils;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import services.sales.IJdService;
import services.sales.IKdnService;
import util.sales.HttpUtil;
import util.sales.KdniaoSubscribeAPI;
import util.sales.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author longhuashen
 * @since 2017/5/17
 */
public class OrderOnlineHandler {

/*    String requestData= "{'OrderCode': '012657700387'," +
            "'ShipperCode':'SF'," +
            "'PayType':1," +
            "'ExpType':1," +
            "'Cost':1.0," +
            "'OtherCost':1.0," +
            "'Sender':" +
            "{" +
            "'Company':'LV','Name':'Taylor','Mobile':'15018442396','ProvinceName':'上海','CityName':'上海','ExpAreaName':'青浦区','Address':'明珠路73号'}," +
            "'Receiver':" +
            "{" +
            "'Company':'GCCUI','Name':'Yann','Mobile':'15018442396','ProvinceName':'北京','CityName':'北京','ExpAreaName':'朝阳区','Address':'三里屯街道雅秀大厦'}," +
            "'Commodity':" +
            "[{" +
            "'GoodsName':'鞋子','Goodsquantity':1,'GoodsWeight':1.0}]," +
            "'Weight':1.0," +
            "'Quantity':1," +
            "'Volume':0.0," +
            "'Remark':'小心轻放'," +
            "'IsReturnPrintTemplate':1}";*/

    @Inject
    private IJdService jdService;

    @Inject
    private OrderPackMapper orderPackMapper;

    @Inject
    private SaleMainMapper saleMainMapper;

    @Inject
    private KdnOrderMapper kdnOrderMapper;

    @Inject
    private PlatformConfigMapper platformConfigMapper;

    @Inject
    private IKdnService kdnService;

    private static Map<String, Integer> map = Maps.newHashMap();

    @Subscribe
    public void execute(OrderOnlineEvent event) {
        SaleMain saleMain = event.getSaleMain();
        SaleBase saleBase = event.getSaleBase();
        List<SaleDetail> saleDetails = event.getSaleDetails();

        Logger.info("生成电子面单---【{}】, saleId:【{}】, orderNo:【{}】", new Date(), saleMain.getId(), saleMain.getSalesOrderNo());

        String commoditys = getCommoditys(saleDetails);//物品详情

        String address = saleBase.getAddress();
        String receiverProvinceName = "";//收件人省
        String receiverCityName = "";//收件人市
        String receiverAreaName = "";//收件人区
        String receiverDetailAddress = "";//收件人详细地址
        if (StringUtils.isNotBlankOrNull(address)) {
            String[] split = address.split(" ");
            if (split.length >= 4) {
                receiverProvinceName = split[0];
                receiverCityName = split[1];
                receiverAreaName = split[2];
                receiverDetailAddress = split[3];
            }
        }


        //获取寄件人地址信息
        String sendProvinceName = "";//寄件人省
        String sendCityName = "";//寄件人市
        String sendAreaName = "";//寄件人区
        String sendDetailAddress = "";//寄件人详细地址
        String sender = "";//寄件人姓名
        String senderPhone = "";//寄件人手机号

        if (saleBase.getShopId() == null) {
            sendProvinceName = "广东省";
            sendCityName = "深圳市";
            sendAreaName = "龙岗区";
            sendDetailAddress = "平湖镇平湖街道平安大道乾隆物流园2期3楼";
            sender = "唐义和";
            senderPhone = "13689528832";
        } else {
            JsonNode shopNode = jdService.checkShop(saleBase.getShopId());
            Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>：【{}】", shopNode);

            if (shopNode.get("provinceId") instanceof NullNode || shopNode.get("cityId") instanceof NullNode || shopNode.get("areaId") instanceof NullNode) {
                sendProvinceName = "广东省";
                sendCityName = "深圳市";
                sendAreaName = "龙岗区";
                sendDetailAddress = "平湖镇平湖街道平安大道乾隆物流园2期3楼";
                sender = "唐义和";
                senderPhone = "13689528832";
            } else {

                sendDetailAddress = shopNode.get("addr").asText();
                sender = shopNode.get("keeperName").asText();
                senderPhone = shopNode.get("telphone").asText();

                Map<String, String> proParams = Maps.newHashMap();
                String provincesRes = HttpUtil.get(proParams, HttpUtil.B2BBASEURL + "/member/getprovs");
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode provinceNode = mapper.readTree(provincesRes);
                    if (provinceNode.isArray()) {
                        Iterator<JsonNode> iterator = provinceNode.iterator();
                        while (iterator.hasNext()) {
                            JsonNode next = iterator.next();
                            if (next.get("id").asInt() == shopNode.get("provinceId").asInt()) {
                                sendProvinceName = next.get("provinceName").asText();
                            }
                        }
                    }
                } catch (IOException e) {
                    Logger.error("获取省出错:{}", e);
                }

                String cityRes = HttpUtil.get(proParams, HttpUtil.B2BBASEURL + "/member/getAllCities");

                try {
                    JsonNode cityNode = mapper.readTree(cityRes);
                    if (cityNode.isArray()) {
                        Iterator<JsonNode> iterator = cityNode.iterator();
                        while (iterator.hasNext()) {
                            JsonNode next = iterator.next();
                            if (next.get("id").asInt() == shopNode.get("cityId").asInt()) {
                                sendCityName = next.get("cityName").asText();
                            }
                        }
                    }
                } catch (IOException e) {
                    Logger.error("获取市出错:{}", e);
                }


                String areaRes = HttpUtil.get(proParams, HttpUtil.B2BBASEURL + "/member/getAllAreas");
                try {
                    JsonNode areaNode = mapper.readTree(areaRes);
                    if (areaNode.isArray()) {
                        Iterator<JsonNode> iterator = areaNode.iterator();
                        while (iterator.hasNext()) {
                            JsonNode next = iterator.next();
                            if (next.get("id").asInt() == shopNode.get("areaId").asInt()) {
                                sendAreaName = next.get("areaName").asText();
                            }
                        }
                    }
                } catch (IOException e) {
                    Logger.error("获取区域出错:{}", e);
                }
            }
        }

        Logger.info("-------->sendProvinceName:{}, sendCityName:{}, sendAreaName:{}, sendDetailAddress:{}", sendProvinceName, sendCityName, sendAreaName, sendDetailAddress);

        String companyCode = "";
        //将快递公司编码转成快递鸟平台的
        Configuration config = Play.application().configuration().getConfig("sales");

        if (StringUtils.isNotBlankOrNull(saleBase.getLogisticsTypeCode())) {
            String url = config.getString("2Bbc") + "/sysadmin/order/shippinginfo/" + saleBase.getLogisticsTypeCode();
            String resultStr = HttpUtil.get(Maps.newHashMap(), url);
            Logger.info("请求hk获取对应的快递公司编码结果：【{}】", resultStr);
            JsonNode kdnNode = Json.parse(resultStr);
            if (kdnNode.isArray() && kdnNode.size() == 1) {
                JsonNode childNode = kdnNode.get(0);
                companyCode = childNode.get("externalCode").asText();
                Logger.info("-------------------快递鸟对应的快递公司编码-------------------->【{}】", companyCode);
            }
        }

        if (StringUtils.isNotBlankOrNull(companyCode)) {
            List<PlatformConfig> platformConfigList = platformConfigMapper.findPlatformConfigsByCode(companyCode);
            if (CollectionUtils.isEmpty(platformConfigList)) {
                Logger.error("未配置快递鸟系统请求参数，请检查!");

                //使订单信息能推送到hk->erp
                saleMain.setIsPushed(0);
                saleMainMapper.updateByPrimaryKeySelective(saleMain);
                return;
            }

            Optional<PlatformConfig> businessIdAny = platformConfigList.stream().filter(d -> d.getPlatformKey().equals(KdniaoSubscribeAPI.EBUSINESSID)).findAny();
            Optional<PlatformConfig> appKeyAny = platformConfigList.stream().filter(d -> d.getPlatformKey().equals(KdniaoSubscribeAPI.APPKEY)).findAny();
            Optional<PlatformConfig> orderOnlineAny = platformConfigList.stream().filter(d -> d.getPlatformKey().equals(KdniaoSubscribeAPI.ORDER_ONLINE)).findAny();
            Optional<PlatformConfig> customerNameAny = platformConfigList.stream().filter(d -> d.getPlatformKey().equals(KdniaoSubscribeAPI.CUSTOMER_NAME)).findAny();
            Optional<PlatformConfig> customerPwdAny = platformConfigList.stream().filter(d -> d.getPlatformKey().equals(KdniaoSubscribeAPI.CUSTOMER_PWD)).findAny();
            Optional<PlatformConfig> monthCodeAny = platformConfigList.stream().filter(d -> d.getPlatformKey().equals(KdniaoSubscribeAPI.MONTH_CODE)).findAny();

            String businessId = businessIdAny.isPresent() ? businessIdAny.get().getPlatformValue() : "";
            String appKey = appKeyAny.isPresent() ? appKeyAny.get().getPlatformValue() : "";
            String orderOnlineUrl = orderOnlineAny.isPresent() ? orderOnlineAny.get().getPlatformValue() : "";
            String customerName = customerNameAny.isPresent() ? customerNameAny.get().getPlatformValue() : "";
            String customerPwd = customerPwdAny.isPresent() ? customerPwdAny.get().getPlatformValue() : "";
            String monthCode = monthCodeAny.isPresent() ? monthCodeAny.get().getPlatformValue() : "";

            Logger.info("快递鸟相关配置参数: EBusinessId:【{}】, appKey:【{}】, orderOnlineUrl:【{}】, customerName:【{}】, customerPwd:【{}】, monthCode【{}】",
                    businessId, appKey, orderOnlineUrl, customerName, customerPwd, monthCode);


            String requestData = "{'OrderCode': '" + saleMain.getSalesOrderNo() + "'," +
                    "'ShipperCode':'" + companyCode + "'," +
                    "'PayType':1," +
                    "'ExpType':1," +
                    "'Cost':" + saleBase.getBbcPostage() + "," +
                    "'OtherCost':1.0," +
                    "'Sender':" +
                    "{" +
                    "'Company':'LV','Name':'" + sender + "','Mobile':'" + senderPhone + "','ProvinceName':'" + sendProvinceName + "','CityName':'" + sendCityName + "','ExpAreaName':'" + sendAreaName + "','Address':'" + sendDetailAddress + "'}," +
                    "'Receiver':" +
                    "{" +
                    "'Company':'GCCUI','Name':'" + saleBase.getReceiver() + "','Mobile':'" + saleBase.getTel() + "','ProvinceName':'" + receiverProvinceName + "','CityName':'" + receiverCityName + "','ExpAreaName':'" + receiverAreaName + "','Address':'" + receiverDetailAddress + "'}," +
                    "'Commodity':"
                    + commoditys +
                    "," +
                    "'Weight':1.0," +
                    "'Quantity':1," +
                    "'Volume':0.0," +
                    "'Remark':'小心轻放'," +
                    "'IsReturnPrintTemplate':1," +
                    "'MonthCode':'" + monthCode + "'," +
                    "'CustomerName':'" + customerName + "'," +
                    "'CustomerPwd':'" + customerPwd + "'"
                    + "}";
            try {
                Logger.info("请求快递鸟电子面单api json参数:【{}】", requestData);

                String resultStr = KdniaoSubscribeAPI.orderOnlineByJson(requestData, businessId, appKey, orderOnlineUrl);
                Logger.info("请求快递鸟电子面单api结果:【{}】", resultStr);
                JsonNode jsonNode = Json.parse(resultStr);
                //调用成功
                if (jsonNode.has("ResultCode") && jsonNode.get("ResultCode").asText().equals("100")) {
                    //获取快递单号
                    JsonNode orderNode = jsonNode.findValue("Order");
                    String logisticCode = orderNode.get("LogisticCode").asText();
                    Logger.info("==============================>快递单号：【{}】", logisticCode);

                    OrderPack orderPackByOrderNumberAndTrackNumber = orderPackMapper.getOrderPackByOrderNumberAndTrackNumber(saleMain.getSalesOrderNo(), logisticCode);
                    if (orderPackByOrderNumberAndTrackNumber == null) {
                        //生成 OrderPack
                        OrderPack orderPack = new OrderPack();
                        orderPack.setCordernumber(saleMain.getSalesOrderNo());
                        orderPack.setCsku(saleDetails.get(0).getSku());
                        orderPack.setCshippingtype(orderNode.get("ShipperCode").asText());
                        orderPack.setFshippingprice(saleBase.getBbcPostage());
                        orderPack.setCtrackingnumber(logisticCode);
                        orderPack.setDcreatedate(new Date());
                        orderPack.setCshippingname(saleBase.getLogisticsMode());
                        orderPackMapper.insertSelective(orderPack);
                    }


                    //电子面单(html)
                    //Logger.info("==============================>电子面单内容：【{}】", jsonNode.get("PrintTemplate").asText());
                    //保存电子面单内容

                    int num = kdnOrderMapper.countByOrderNo(saleMain.getSalesOrderNo());
                    if (num < 1) {
                        KdnOrder kdnOrder = new KdnOrder();
                        kdnOrder.setSalesOrderNo(saleMain.getSalesOrderNo());
                        kdnOrder.setPrintTemplate(jsonNode.get("PrintTemplate").asText());
                        kdnOrder.setLogisticCode(logisticCode);
                        kdnOrder.setCreateDate(new Date());
                        kdnOrderMapper.insertSelective(kdnOrder);
                    }

                    if (map.containsKey(saleMain.getSalesOrderNo())) {
                        map.remove(saleMain.getSalesOrderNo());
                    }
                } else {
                    if (map.containsKey(saleMain.getSalesOrderNo())) {
                        Integer num = map.get(saleMain.getSalesOrderNo());
                        map.put(saleMain.getSalesOrderNo(), ++num);
                    } else {
                        map.put(saleMain.getSalesOrderNo(), 1);
                    }
                }
            } catch (Exception e) {
                Logger.error("请求快递鸟电子面单api出错:【{}】", e);
            } finally {
                //使订单信息能推送到hk->erp
                saleMain.setIsPushed(0);
                saleMainMapper.updateByPrimaryKeySelective(saleMain);
            }

            if (map.containsKey(saleMain.getSalesOrderNo()) && map.get(saleMain.getSalesOrderNo()) <= 3) {
                Logger.info("------------>{}", map);
                kdnService.requestOrderOnline(event.getSaleMain());
            }
        } else {
            Logger.error("获取不到快递公司编码");

            //使订单信息能推送到hk->erp
            saleMain.setIsPushed(0);
            saleMainMapper.updateByPrimaryKeySelective(saleMain);
        }
    }

    /**
     * 要寄的物品详情
     *
     * @param saleDetails
     * @return
     */
    private String getCommoditys(List<SaleDetail> saleDetails) {
        List<String> resultStr = Lists.newArrayList();
        for (SaleDetail saleDetail : saleDetails) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"GoodsName\":");
            sb.append("\""+ saleDetail.getProductName().replace("'", " " )
                    .replace("\"", " ").replace("#"," ").replace("&", " ").replace("+", " ")
                    .replace("<", "" ).replace(">", " ")+"\",");
            sb.append("\"Goodsquantity\":");
            sb.append(saleDetail.getQty());
            sb.append("}");
            resultStr.add(sb.toString());
        }
        return resultStr.toString();
    }
}
