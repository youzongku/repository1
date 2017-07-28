package services.sales.impl;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;
import events.sales.CalculateOptFeeEvent;
import mapper.sales.SaleBaseMapper;
import mapper.sales.SaleDetailMapper;
import mapper.sales.SaleMainMapper;
import play.Logger;
import services.sales.IHttpService;
import services.sales.IMSiteSalesService;
import util.sales.JsonCaseUtil;
import util.sales.StringUtils;

public class MSiteSalesService implements IMSiteSalesService {
    
    @Inject private SaleMainMapper saleMainMapper;
    @Inject private SaleBaseMapper saleBaseMapper;
    @Inject private SaleDetailMapper saleDetailMapper;
    @Inject private EventBus ebus;
    @Inject private IHttpService httpService;

    @Override
    public Map<String, Object> order(JsonNode orderList) {
        Map<String,Object> result = Maps.newHashMap();
        try {
            int count = 0;
            SaleMain main = null;
            SaleBase base = null;
            String saleOrderNo = null;
            List<SaleDetail> sds = null;
            List<String> orderNos = Lists.newArrayList();
            for (JsonNode order : orderList) {
                sds = Lists.newArrayList();
                main = new SaleMain();
                base = new SaleBase();
                saleOrderNo = JsonCaseUtil.jsonToString(order.get("cordernumber"));
                SaleMain sm = saleMainMapper.selectByOrderNo(saleOrderNo);
                if(sm != null){
                    Logger.info("订单重复：[{}]",saleOrderNo);
                    continue;
                }
                if(order.get("platformAmount") == null){
                    Logger.info("平台收入为空：[{}]",saleOrderNo);
                    continue;
                }
                String host = JsonCaseUtil.jsonToString(order.get("host"));
                // 客户账号 
                String email = order.get("disAccount") != null ? order.get("disAccount").asText() : host;
                main.setEmail(email);
                main.setHost(host);
                try {
                    JsonNode resJson = httpService.getDismemberByEmail(email);
                    JsonNode memberNode = resJson.get("result");
                    if (null != memberNode) {
                        String erp = JsonCaseUtil.jsonToString(memberNode.get("salesmanErp"));
                        if (StringUtils.isBlankOrNull(erp)) {
                            Logger.info("未关联客服账号：[{}]", saleOrderNo);
                            continue;
                        }
                        main.setNickName(JsonCaseUtil.jsonToString(order.get("nickName")));
                        base.setCustomerservice(erp);
                        main.setDistributorType(JsonCaseUtil.jsonToInteger(memberNode.get("comsumerType")));
                        main.setDisMode(JsonCaseUtil.jsonToInteger(memberNode.get("distributionMode")));
                    } else {
                        Logger.info("未查询到用户信息:[{}]", saleOrderNo);
                        continue;
                    }
                } catch (Exception e) {
                    Logger.info("获取用户信息异常 ", e);
                    continue;
                }
                
                // 订单补差
                main.setCompensationPaymentNo(JsonCaseUtil.jsonToString(order.get("compensationTradeNo")));// 补差支付交易号
                main.setCompensationPaymentType(JsonCaseUtil.jsonToString(order.get("compensationPayType")));// 补差支付类型
                main.setCompensationPayDate(JsonCaseUtil.jsonToDate(order.get("compensationPayTime")));// 补差支付时间
                main.setCompensationAmount(JsonCaseUtil.jsonToDouble(order.get("compensationAmount")));// 补差支付金额
                
                main.setDisPrimeCost(JsonCaseUtil.jsonToDouble(order.get("disPrimeCost")));// 分销总成本
                
                main.setHostOrigin(JsonCaseUtil.jsonToInteger(order.get("hostOrigin")));
                // 单号
                main.setSalesOrderNo(saleOrderNo);
                // 平台类型
                base.setPlatformType(JsonCaseUtil.jsonToInteger(order.get("platformtype")));
                // 平台名称
                base.setPlatformName(JsonCaseUtil.jsonToString(order.get("plattypename")));
                // 收货人姓名
                base.setReceiver(JsonCaseUtil.jsonToString(order.get("cfirstname")));
                // 收货人手机
                base.setTel(JsonCaseUtil.jsonToString(order.get("ctelephone")));
                // 收货人邮编
                base.setPostCode(JsonCaseUtil.jsonToString(order.get("cpostalcode")));
                // 基本价格
                base.setOrderTotalAmount(JsonCaseUtil.jsonToDouble(order.get("fordersubtotal")));
                // 结算价格
                base.setOrderActualAmount(JsonCaseUtil.jsonToDouble(order.get("fgrandtotal")));
                // 支付方式
                main.setPaymentType(JsonCaseUtil.jsonToString(order.get("cpaymentid")));
                // 第三方交易号
                main.setPaymentNo(JsonCaseUtil.jsonToString(order.get("ctransactionid")));
                // 下单时间
                main.setOrderingDate(JsonCaseUtil.jsonToDate(order.get("dcreatedate")));
                // 付款时间
                main.setPayDate(JsonCaseUtil.jsonToDate(order.get("dpaymentdate")));
                //平台收入  
                main.setPlatformAmount(JsonCaseUtil.jsonToDouble(order.get("platformAmount")));
                //报关金额
                main.setClearanceAmount(JsonCaseUtil.jsonToDouble(order.get("fgrandtotal")));
                // 买家身份证
                base.setOrdererIDCard(JsonCaseUtil.jsonToString(order.get("idcard")));
                //来源m站 --MSITE-BRAND/品牌M站   MSITE/普通M站
                main.setSource(order.has("source") ? JsonCaseUtil.jsonToString(order.get("source")) : "MSITE");
                // 买家姓名
                base.setOrderer(JsonCaseUtil.jsonToString(order.get("payman")));
                String address = JsonCaseUtil.jsonToString(order.get("cprovince")) + " " + JsonCaseUtil.jsonToString(order.get("ccity")) + 
                        (!JsonCaseUtil.isJsonEmpty(order.get("carea"))?" "+order.get("carea").asText():"")+ 
                        (!JsonCaseUtil.isJsonEmpty(order.get("cstreetaddress"))?" "+order.get("cstreetaddress").asText():"")+
                        (!JsonCaseUtil.isJsonEmpty(order.get("receiveraddr"))?" "+order.get("receiveraddr").asText():"");
                // 收货地址
                base.setAddress(address);
                // 收货人身份证
                base.setIdcard(JsonCaseUtil.jsonToString(order.get("receiveridcard")));
                //买家手机号
                base.setOrdererTel(JsonCaseUtil.jsonToString(order.get("buyerCellphone")));
                // 买家id
                base.setBuyerID(JsonCaseUtil.jsonToString(order.get("buyerid")));
                //收款账号
                base.setCollectAccount(JsonCaseUtil.jsonToString(order.get("creceiveraccount")));
                // 备注
                base.setRemark(JsonCaseUtil.jsonToString(order.get("cremark")));
                // 物流代码
                base.setLogisticsTypeCode(JsonCaseUtil.jsonToString(order.get("cshippingcode")));
                // m站 运费包含在商品中  不需要 物流费用
                //base.setBbcPostage(jsonToDouble(order.get("fshippingprice")));
                // 仓库id
                main.setWarehouseId(JsonCaseUtil.jsonToInteger(order.get("istorageid")));
                // 仓库名称
                main.setWarehouseName(JsonCaseUtil.jsonToString(order.get("cstoragename")));
                // 订单状态 ？？ 默认审核通过
                main.setStatus(6);
                //订单渠道
                main.setOrderchannel(2);
                saleMainMapper.insertSelective(main);
                count++; 
                base.setSalesOrderId(main.getId());
                saleBaseMapper.insertSelective(base);
                JsonNode pros = order.get("details");
                for (JsonNode pro : pros) {
                    SaleDetail detail = new SaleDetail();
                    detail.setSalesOrderId(main.getId());
                    detail.setProductName(JsonCaseUtil.jsonToString(pro.get("ctitle")));
                    detail.setSku(JsonCaseUtil.jsonToString(pro.get("csku")));
                    detail.setPurchasePrice(JsonCaseUtil.jsonToDouble(pro.get("foriginalprice")));//采购价
                    detail.setQty(JsonCaseUtil.jsonToInteger(pro.get("iqty")));
                    detail.setWarehouseId(JsonCaseUtil.jsonToInteger(pro.get("warehouseId")));
                    detail.setWarehouseName(JsonCaseUtil.jsonToString(pro.get("warehousename")));
                    detail.setSalesOrderNo(JsonCaseUtil.jsonToString(pro.get("cordernumber")));
                    detail.setProductImg(JsonCaseUtil.jsonToString(pro.get("productImg")));
                    detail.setInterBarCode(JsonCaseUtil.jsonToString(pro.get("interBarCode")));
                    detail.setSalesOrderNo(saleOrderNo);
                    // 分销价格体系
                    detail.setDisPrice(JsonCaseUtil.jsonToDouble(pro.get("disPrice")));
                    detail.setFinalSellingPrice(JsonCaseUtil.jsonToDouble(pro.get("price")));
                    sds.add(detail);
                    saleDetailMapper.insertSelective(detail);
                }
                if(main!=null){
                    // 计算税金
                    ebus.post(main);
                    //记录日志
//                  saleService.syncLogs(main, Constant.CREATE);
                    //计算操作费
                    ebus.post(new CalculateOptFeeEvent(main, base, sds));
                }
                orderNos.add(main.getSalesOrderNo());
            }
            result.put("suc", true);
            result.put("msg", "保存成功，共["+count+"]条订单");
            result.put("saveOrders", orderNos);
        } catch (Exception e) {
            result.put("suc", false);
            result.put("msg", "保存M站订单异常");
            Logger.error("保存M站订单异常",e);
        }
        return result;
    }
    
    @Override
    public Map<String, Object> storeOrder(JsonNode orderList) {
        Map<String, Object> result = Maps.newHashMap();
        SaleMain main = null;
        SaleBase base = null;
        String saleOrderNo = null;
        String email = null;
        List<SaleDetail> sds = null;
        // 成功
        List<String> success = Lists.newArrayList();
        // 失败
        List<String> failed = Lists.newArrayList();
        for (JsonNode order : orderList) {
            try {
                sds = Lists.newArrayList();
                main = new SaleMain();
                base = new SaleBase();
                saleOrderNo = JsonCaseUtil.jsonToString(order.get("cordernumber"));
                SaleMain sm = saleMainMapper.selectByOrderNo(saleOrderNo);
                if (sm != null) {
                    Logger.info("订单重复：[{}]", saleOrderNo);
                    failed.add(saleOrderNo);
                    continue;
                }
                if (order.get("platformAmount") == null) {
                    Logger.info("平台收入为空：[{}]", saleOrderNo);
                    failed.add(saleOrderNo);
                    continue;
                }
                email = JsonCaseUtil.jsonToString(order.get("disAccount"));
                if (StringUtils.isBlankOrNull(email)) {
                    Logger.info("用户账号为空：[{}]", email);
                    failed.add(saleOrderNo);
                    continue;
                }
                // 客户账号
                main.setEmail(email);
                try {
                    JsonNode resJson = httpService.getDismemberByEmail(email);
                    JsonNode memberNode = resJson.get("result");
                    if (null != memberNode) {
                        String erp = JsonCaseUtil.jsonToString(memberNode.get("salesmanErp"));
                        if (StringUtils.isBlankOrNull(erp)) {
                            Logger.info("未关联客服账号：[{}]", saleOrderNo);
                            failed.add(saleOrderNo);
                            continue;
                        }
                        main.setNickName(JsonCaseUtil.jsonToString(memberNode.get("nickName")));
                        base.setCustomerservice(erp);
                        main.setDistributorType(JsonCaseUtil.jsonToInteger(memberNode.get("comsumerType")));
                        main.setDisMode(JsonCaseUtil.jsonToInteger(memberNode.get("distributionMode")));
                    } else {
                        Logger.info("未查询到用户信息:[{}]", saleOrderNo);
                        failed.add(saleOrderNo);
                        continue;
                    }
                } catch (Exception e) {
                    Logger.info("获取用户信息异常 ", e);
                    failed.add(saleOrderNo);
                    continue;
                }
                // 单号
                main.setSalesOrderNo(saleOrderNo);
                // 平台类型
                base.setPlatformType(JsonCaseUtil.jsonToInteger(order.get("platformtype")));
                // 平台名称
                base.setPlatformName(JsonCaseUtil.jsonToString(order.get("plattypename")));
                // 收货人姓名
                base.setReceiver(JsonCaseUtil.jsonToString(order.get("cfirstname")));
                //店铺ID
                base.setShopId(JsonCaseUtil.jsonToInteger(order.get("shopId")));
                // 收货人手机
                base.setTel(JsonCaseUtil.jsonToString(order.get("ctelephone")));
                // 收货人邮编
                base.setPostCode(JsonCaseUtil.jsonToString(order.get("cpostalcode")));
                // 基本价格 (售价*数量)
                base.setOrderTotalAmount(JsonCaseUtil.jsonToDouble(order.get("fordersubtotal")));
                // 结算价格 (售价*数量)
                base.setOrderActualAmount(JsonCaseUtil.jsonToDouble(order.get("fgrandtotal")));
                // 支付方式
                main.setPaymentType(JsonCaseUtil.jsonToString(order.get("cpaymentid")));
                // 第三方交易号
                main.setPaymentNo(JsonCaseUtil.jsonToString(order.get("ctransactionid")));
                //
                main.setTradeNo(JsonCaseUtil.jsonToString(order.get("ctransactionid")));
                //平台单号
                base.setPlatformOrderNo(saleOrderNo);
                // 下单时间
                main.setOrderingDate(JsonCaseUtil.jsonToDate(order.get("dcreatedate")));
                //ordering_date
                base.setOrderingDate(JsonCaseUtil.jsonToDate(order.get("dcreatedate")));
                // 付款时间
                main.setPayDate(JsonCaseUtil.jsonToDate(order.get("dpaymentdate")));
                // 平台收入 (售价*数量)
                main.setPlatformAmount(JsonCaseUtil.jsonToDouble(order.get("platformAmount")));
                // 报关金额 (售价*数量)
                main.setClearanceAmount(JsonCaseUtil.jsonToDouble(order.get("fgrandtotal")));
                // 买家身份证
                base.setOrdererIDCard(JsonCaseUtil.jsonToString(order.get("idcard")));
                // 来源门店
                main.setSource(JsonCaseUtil.jsonToString(order.get("source")));
                // 买家姓名
                base.setOrderer(JsonCaseUtil.jsonToString(order.get("payman")));
                // 省 市 区 街道 详细地址
                String address = JsonCaseUtil.jsonToString(order.get("cprovince")) + " " + JsonCaseUtil.jsonToString(order.get("ccity"))
                        + (!JsonCaseUtil.isJsonEmpty(order.get("carea")) ? " " + order.get("carea").asText() : "")
                        + (!JsonCaseUtil.isJsonEmpty(order.get("cstreetaddress")) ? " " + order.get("cstreetaddress").asText() : "")
                        + (!JsonCaseUtil.isJsonEmpty(order.get("receiveraddr")) ? " " + order.get("receiveraddr").asText() : "");
                // 收货地址
                base.setAddress(address);
                // 收货人身份证
                base.setIdcard(JsonCaseUtil.jsonToString(order.get("receiveridcard")));
                // 买家手机号
                base.setOrdererTel(JsonCaseUtil.jsonToString(order.get("buyerCellphone")));
                // 买家id
                base.setBuyerID(JsonCaseUtil.jsonToString(order.get("buyerid")));
                // 收款账号
                base.setCollectAccount(JsonCaseUtil.jsonToString(order.get("creceiveraccount")));
                // 备注
                base.setRemark(JsonCaseUtil.jsonToString(order.get("cremark")));
                // 物流代码
                base.setLogisticsTypeCode(JsonCaseUtil.jsonToString(order.get("cshippingcode")));
                // 物流中文名称
                base.setLogisticsMode(JsonCaseUtil.jsonToString(order.get("logisticsMode")));
                // 创建人
                base.setCreateUser(email);
                // 运费 自提就传0
                base.setBbcPostage(JsonCaseUtil.jsonToDouble(order.get("fshippingprice")));
                // 仓库id 
                main.setWarehouseId(JsonCaseUtil.jsonToInteger(order.get("istorageid")));
                // 仓库名称
                main.setWarehouseName(JsonCaseUtil.jsonToString(order.get("cstoragename")));
                // 订单状态 ？？ 默认审核通过
                main.setStatus(6);
                //订单渠道
                main.setOrderchannel(3);
                saleMainMapper.insertSelective(main);
                success.add(saleOrderNo);
                base.setSalesOrderId(main.getId());
                saleBaseMapper.insertSelective(base);
                JsonNode pros = order.get("details");
                for (JsonNode pro : pros) {
                    SaleDetail detail = new SaleDetail();
                    detail.setSalesOrderId(main.getId());
                    detail.setProductName(JsonCaseUtil.jsonToString(pro.get("ctitle")));
                    detail.setSku(JsonCaseUtil.jsonToString(pro.get("csku")));
                    // 采购价
                    detail.setPurchasePrice(JsonCaseUtil.jsonToDouble(pro.get("foriginalprice")));
                    detail.setQty(JsonCaseUtil.jsonToInteger(pro.get("iqty")));
                    detail.setWarehouseId(JsonCaseUtil.jsonToInteger(pro.get("warehouseId")));
                    detail.setWarehouseName(JsonCaseUtil.jsonToString(pro.get("warehousename")));
                    detail.setSalesOrderNo(JsonCaseUtil.jsonToString(pro.get("cordernumber")));
                    detail.setProductImg(JsonCaseUtil.jsonToString(pro.get("productImg")));
                    detail.setInterBarCode(JsonCaseUtil.jsonToString(pro.get("interBarCode")));
                    detail.setSalesOrderNo(saleOrderNo);
                    // 最终售价
                    detail.setFinalSellingPrice(JsonCaseUtil.jsonToDouble(pro.get("price")));
                    sds.add(detail);
                    saleDetailMapper.insertSelective(detail);
                }
                if (main != null) {
                    // 计算税金
                    ebus.post(main);
                    // 记录日志
                    // saleService.syncLogs(main, Constant.CREATE);
                    // 计算操作费
                    ebus.post(new CalculateOptFeeEvent(main, base, sds));
                }
            } catch (Exception e) {
                Logger.error("保存门店零售订单异常[{}],[{}]",saleOrderNo, e);
                failed.add(saleOrderNo);
                continue;
            }
        }
        result.put("success", success);
        result.put("failed", failed);
        return result;
    }
}
