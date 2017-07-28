package services.sales.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.inject.Inject;

import dto.sales.ShOrderDto;
import dto.sales.ShOrderInfo;
import dto.sales.ShOrderStatus;
import entity.sales.SaleDetail;
import entity.sales.ShAttachment;
import entity.sales.ShLog;
import entity.sales.ShOrder;
import entity.sales.ShOrderDetail;
import mapper.sales.ShAttachmentMapper;
import mapper.sales.ShLogMapper;
import mapper.sales.ShOrderDetailMapper;
import mapper.sales.ShOrderMapper;
import pager.sales.Pager;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Http;
import services.sales.IHttpService;
import services.sales.ISaleAfterService;
import services.sales.ISaleService;
import services.sales.ISequenceService;
import services.sales.IUserService;
import util.sales.HttpUtil;
import util.sales.IDUtils;
import util.sales.JsonCaseUtil;
import util.sales.PageUtil;
import util.sales.PriceFormatUtil;
import util.sales.StringUtils;

/**
 * @author longhuashen
 * @since 2017/4/8
 */
public class SaleAfterService implements ISaleAfterService {
    @Inject
    private ShOrderMapper shOrderMapper;
    @Inject
    private ShAttachmentMapper shAttachmentMapper;
    @Inject
    private ShOrderDetailMapper shOrderDetailMapper;
    @Inject
    private ShLogMapper shLogMapper;
    @Inject
    private IUserService userService;
    @Inject
    private ISequenceService sequenceService;
    @Inject
    private ISaleService saleService;
    @Inject
    private IHttpService httpService;
    
    private static String filePath = "";
    
    
    
    @Override
    public Map<String, Object> getReturnAmountCapfee4Sku(String purchaseOrderNo) {
    	Map<String, Object> result = Maps.newHashMap();
    	List<ShOrderDetail> allDetails = shOrderDetailMapper.selectByPurchaseOrderNo(purchaseOrderNo);
    	if (CollectionUtils.isEmpty(allDetails)) {
        	result.put("suc", false);
        	result.put("msg", "指定的采购单没有对应的发货售后");
			return result;
		}
    	
    	// 查询采购单对应的有效的售后
    	// 这里的售后id对应的售后单可能不是有效的
    	List<Integer> shOrderIdList = Lists.transform(allDetails, d->d.getShOrderId());
    	Map<String, Object> paramMap = Maps.newHashMap();
    	paramMap.put("status", ShOrderStatus.SH_FINISH);// 已完成的才要
    	paramMap.put("shOrderIdList", shOrderIdList);
    	// 售后单
    	List<ShOrder> shOrderList = shOrderMapper.selectAllAfterSaleOrder(paramMap);
    	
    	// 这里的售后id对应的售后单都是有效的
    	shOrderIdList = Lists.transform(shOrderList, shOrder->shOrder.getId());
    	// 获取售后详情
    	List<ShOrderDetail> shOrderDetailList = shOrderDetailMapper.getShOrderDetailListByShOrderIdList(shOrderIdList);
    	// 售后单id = 售后详情集合
    	Map<Integer, List<ShOrderDetail>> shOrderId2DetailList = shOrderDetailList.stream().collect(Collectors.groupingBy(e->e.getShOrderId()));
    	List<ShOrderInfo> shOrderInfoList = Lists.newArrayListWithCapacity(shOrderList.size());
    	// 为每个商品进行实际退款均摊计算
    	for (ShOrder shOrder : shOrderList) {
    		Integer shOrderId = shOrder.getId();
    		// 售后单对应的详情
    		List<ShOrderDetail> shDetailList = shOrderId2DetailList.get(shOrderId);
    		// 均摊价总计
    		BigDecimal subtotalCapfee = shDetailList.stream()
        			.map(e->new BigDecimal(e.getQty()).multiply(new BigDecimal(e.getCapfee())))
        			.reduce(new BigDecimal(0), (x,y)->x.add(y));
    		
    		// 实际退款金额
    		BigDecimal actualAmount = new BigDecimal(shOrder.getActualAmount());
    		// 采购单所属的采购单要跟传入的参数一致，这才是要的数据，不是的就过滤了
    		List<ShOrderDetail> shDetailListNeeded = shDetailList.stream()
    				.filter(e->Objects.equal(e.getPurchaseOrderNo(), purchaseOrderNo))
    				.collect(Collectors.toList());
    		// 一个sku均摊的实际退款金额：商品均摊价小计/均摊价总计*实际退款金额
    		shDetailListNeeded.forEach(shOrderDetail->{
    			BigDecimal capfeeSubtotal = shOrderDetail.capfeeSubtotal();
				BigDecimal actualAmountCapfee4ASku = capfeeSubtotal.divide(subtotalCapfee, 2, BigDecimal.ROUND_HALF_UP)
						.multiply(actualAmount);
    			shOrderDetail.setActualAmountCapfee(PriceFormatUtil.toFix2(actualAmountCapfee4ASku));
    		});
    		
    		ShOrderInfo shOrderInfo = new ShOrderInfo(shOrder, shDetailListNeeded);
    		shOrderInfoList.add(shOrderInfo);
		}
    	
    	// 到这里，售后单的每个详情都进行了实际退款均摊
    	result.put("suc", true);
    	result.put("shOrderInfoList", shOrderInfoList);
    	return result;
    }

    @Override
    public Pager<ShOrder> selectShSaleOrderList(JsonNode main, String email) {
        Logger.info("selectShSaleOrderList查询参数：{}", main);
        int pageSize = main.get("pageSize").asInt();
        int currPage = main.get("currPage").asInt();

        Map<String,Object> map = Maps.newHashMap();
        map.put("email", email);
        map.put("pageSize", pageSize);
        map.put("currPage", currPage);
        map.put("shOrderNo", main.get("shOrderNo").asText());
        List<ShOrder> shOrderList = shOrderMapper.selectShSaleOrderList(map);
        int totalCount = shOrderMapper.selectShSaleOrderListCount(map);

        int totalPage = PageUtil.calculateTotalPage(totalCount, pageSize);
        return new Pager<ShOrder>(shOrderList, currPage,pageSize, totalPage);
    }

    @Override
    public ShOrderDto getAfterSaleOrderDtoById(int orderId) {
        return shOrderMapper.getAfterSaleOrderDtoById(orderId);
    }

    @Override
    public Map<String, Object> saleOrderRefundsApply(Map<String, String[]> params, List<Http.MultipartFormData.FilePart> files) {
        Logger.info("=======================>saleOrderRefundsApply:{}, files:{}", params.toString(), files);
        //生成售后单
        String xsOrderNo = setString(params.get("xsOrderNo"));
        int qty = Integer.parseInt(setString(params.get("qty")));
        double money = Double.parseDouble(setString(params.get("money")));
        String desc = setString(params.get("desc"));
        String email = setString(params.get("email"));
        String sku = setString(params.get("sku"));
        int warehouseId = Integer.parseInt(setString(params.get("warehouseId")));
        String warehouseName = setString(params.get("warehouseName"));
        String productImg = setString(params.get("productImg"));
        String productName = setString(params.get("productName"));
        int detailOrderId = Integer.parseInt(setString(params.get("detailOrderId")));

        int orderId = Integer.parseInt(setString(params.get("orderId")));

        String user = userService.getDismember();
        JsonNode userNode = Json.parse(user);
        String nickName = JsonCaseUtil.getStringValue(userNode,"nickName");
        String salesManErp = JsonCaseUtil.getStringValue(userNode,"salesmanErp");
        Integer distributionMode = userNode.get("distributionMode").asInt();

        Logger.info("xsOrderNo:{},qty:{},money:{}, desc:{}, email:{}", xsOrderNo, qty, money, desc, email );
        Map<String, Object> map = Maps.newHashMap();
        try {
            if (StringUtils.isBlankOrNull(filePath)) {
                Configuration config = Play.application().configuration().getConfig("b2bSPA");
                filePath = config.getString("imagePath");
            }
            //生成售后单
            ShOrder shOrder = new ShOrder();
            shOrder.setXsOrderNo(xsOrderNo);
            String shOrderNo = IDUtils.getXsshSaleOrderCode(sequenceService.selectNextValue("AFTER_SALE_NO"));
            shOrder.setShOrderNo(shOrderNo);
            shOrder.setEmail(email);
            Date date = new Date();
            shOrder.setCreateTime(date);
            shOrder.setUpdateTime(date);
            shOrder.setDemandAmount(money);
            shOrder.setStatus(1);
            shOrder.setQaDesc(desc);
            shOrder.setDemandQty(qty);
            shOrder.setWarehouseId(warehouseId);
            shOrder.setWarehouseName(warehouseName);
            shOrder.setProductImg(productImg);
            shOrder.setProductName(productName);
            shOrder.setSku(sku);
            shOrder.setDetailOrderId(detailOrderId);
            shOrder.setBusinessErp(salesManErp);
            shOrder.setDisName(nickName);
            shOrder.setDisMode(distributionMode);
            shOrderMapper.insertSelective(shOrder);


            //生成售后单详情
            Map<String, Object> purchaseInfo = saleService.getPurchaseInfo(orderId);
            List<SaleDetail> historySaleDetailList = (List<SaleDetail>) purchaseInfo.get("historySaleDetailList");
            List<SaleDetail> saleDetailList = historySaleDetailList.stream().filter(a -> a.getSku().equals(sku)).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(saleDetailList)) {
                for(SaleDetail saleDetail : saleDetailList) {
                    ShOrderDetail shOrderDetail = new ShOrderDetail();
                    shOrderDetail.setShOrderId(shOrder.getId());
                    shOrderDetail.setShOrderNo(shOrder.getShOrderNo());
                    shOrderDetail.setSku(sku);
                    shOrderDetail.setQty(saleDetail.getQty());
                    shOrderDetail.setWarehoseid(warehouseId);
                    shOrderDetail.setWarehouseName(warehouseName);
                    shOrderDetail.setPurchaseOrderNo(saleDetail.getPurchaseOrderNo());
                    shOrderDetail.setPurchasePrice(saleDetail.getPurchasePrice());
                    shOrderDetail.setProductImg(productImg);
                    shOrderDetail.setProductName(productName);
                    shOrderDetail.setArriveWarePrice(saleDetail.getArriveWarePrice());
                    shOrderDetail.setCapfee(saleDetail.getCapFee());
                    shOrderDetail.setExpirationDate(saleDetail.getExpirationDate());
                    Date now = new Date();
                    shOrderDetail.setCreateTime(now);
                    shOrderDetail.setUpdateTime(now);
                    shOrderDetail.setInterBarCode(saleDetail.getInterBarCode());
                    shOrderDetailMapper.insertSelective(shOrderDetail);
                }
            }


            File origin, folder, target;
            String fileName;
            for (Http.MultipartFormData.FilePart part : files) {
                ShAttachment shAttachment = new ShAttachment();
                shAttachment.setCreateTime(new Date());
                shAttachment.setAttachmentName(part.getFilename());
                shAttachment.setShOrderId(shOrder.getId());

                fileName = part.getFilename();
                target = new File(filePath + File.separator + shOrderNo + File.separator + fileName);
                folder = new File(filePath + File.separator +shOrderNo);

                if (!folder.exists()) {
                    folder.mkdirs();
                }
                origin = part.getFile();
                target.createNewFile();
                Files.copy(origin, target);
                shAttachment.setAttachmentPath(target.getAbsolutePath());
                shAttachmentMapper.insertSelective(shAttachment);
            }

            map.put("success", true);
            map.put("id", shOrder.getId());
            return map;
        } catch (Exception e) {
            Logger.error("==========>saleOrderRefundsApply 申请售后退款出错！{}", e);
            map.put("success", false);
            map.put("msg", "申请售后出错！");
            return map;
        }
    }


    @Override
    public Map<String, Object> selectSaleOrderRefundsListOfBackstage(JsonNode json, List<String> accounts) {
        Logger.info("selectShSaleOrderList查询参数：{}",json);

        Map<String,Object> resultMap = new HashMap<String,Object>();
        Map<String,Object> paramMap = Maps.newHashMap();
        int currPage = 1;
        int pageSize = 10;
        if (json.get("pageSize")!=null && json.get("pageCount")!=null) {
            currPage = json.get("pageCount").asInt();
            pageSize = json.get("pageSize").asInt();
        }
        paramMap.put("currPage", currPage);
        paramMap.put("pageSize", pageSize);
        paramMap.put("orderStartDate", StringUtils.getStringBlank(json.path("orderStartDate").asText(), true));
        paramMap.put("orderEndDate", StringUtils.getStringBlank(json.path("orderEndDate").asText(), true));
        paramMap.put("status", json.path("status").asInt());
        paramMap.put("searchSpan", StringUtils.getStringBlank(json.path("searchSpan").asText(), true));
        paramMap.put("isProductReturn", json.path("isProductReturn").asInt());


        List<ShOrder> shOrderList = shOrderMapper.selectAllAfterSaleOrder(paramMap);
        int totalCount = shOrderMapper.selectAllAfterSaleOrderCount(paramMap);

        int totalPage = PageUtil.calculateTotalPage(totalCount, pageSize);
        resultMap.put("result", true);
        resultMap.put("total", totalCount);
        resultMap.put("pages",totalPage);
        resultMap.put("pageCount", currPage);
        resultMap.put("saleOrderRefundInfos", shOrderList);
        return resultMap;
    }

    @Override
    public ShOrder getSalesOrderRefundsById(JsonNode json) {
        Integer id = json.get("id").asInt();
        return shOrderMapper.getSalesOrderRefundsById(id);
    }

    @Override
    public List<ShAttachment> getShAttachmentListByShOrderId(JsonNode json) {
        Integer id = json.get("id").asInt();
        return shAttachmentMapper.getShAttachmentListByShOrderId(id);
    }

    @Override
    public File getShAttachmentImg(Integer id) {
        return new File(shAttachmentMapper.getImg(id));
    }

    @Override
    public List<ShOrderDetail> getShOrderDetailListByShOrderId(JsonNode json) {
        Integer id = json.get("id").asInt();
        return shOrderDetailMapper.getShOrderDetailListByShOrderId(id);
    }

    @Override
    public Map<String, Object> shAudit(JsonNode json, String account) {
        Integer type = json.get("type").asInt();
        Double demandAmount = json.get("demandAmount").asDouble();
        Integer isProductReturn = json.get("isProductReturn").asInt();
        String remark = json.get("remark").asText();
        Integer shOrderId = json.get("shOrderId").asInt();
        Integer isConfirm = json.get("isConfirm").asInt();

        Logger.info("==================>shAudit:{}", json.toString());

        ShOrder shOrder = shOrderMapper.getSalesOrderRefundsById(shOrderId);
        Map<String, Object> map = Maps.newHashMap();
        switch (type) {
            case ShOrderStatus.CUSTOMMER_CONFIRM:// 财务确认

                //生成操作日志
                ShLog shLog = new ShLog();
                shLog.setShOrderId(shOrderId);
                shLog.setType(type);
                shLog.setIsProductReturn(isProductReturn);
                shLog.setRemark(remark);
                shLog.setCreateTime(new Date());
                shLog.setOperator(account);
                shLog.setResult(isConfirm);
                shLogMapper.insertSelective(shLog);

                if (isConfirm.intValue() == ShOrderStatus.SH_CONFIRM_YES) {
                    shOrder.setStatus(ShOrderStatus.FINANCE_CONFIRM);
                    shOrder.setActualAmount(demandAmount);
                } else {
                    shOrder.setStatus(ShOrderStatus.SH_CLOSED);
                }
                shOrder.setActualAmount(demandAmount);
                shOrder.setIsProductReturn(isProductReturn);
                shOrder.setUpdateTime(new Date());
                map.put("result", shOrderMapper.updateSelective(shOrder) > 0 ? true : false);
                break;
            case ShOrderStatus.FINANCE_CONFIRM: //客服确认
                //生成操作日志
                ShLog financeShLog = new ShLog();
                financeShLog.setShOrderId(shOrderId);
                financeShLog.setType(type);
                financeShLog.setIsProductReturn(isProductReturn);
                financeShLog.setRemark(remark);
                financeShLog.setCreateTime(new Date());
                financeShLog.setOperator(account);
                financeShLog.setResult(isConfirm);
                shLogMapper.insertSelective(financeShLog);

                if (isConfirm.intValue() == ShOrderStatus.SH_CONFIRM_YES) {
                    if (isProductReturn.intValue() == 1) {//需要寄回商品
                        shOrder.setStatus(ShOrderStatus.SEND_PRODUCT_BACK);
                    } else {//不用寄回商品
                        shOrder.setStatus(ShOrderStatus.SH_FINISH);
                        Date now = new Date();
                        shOrder.setReceivedProductTime(now);
                        Logger.info("{}售后单退款",shOrder.getShOrderNo());
                        try {
                        	//退款至余额
							httpService.refund(shOrder.getEmail(), demandAmount, shOrder.getShOrderNo());
						} catch (Exception e) {
							Logger.info("售后退款异常:{}",e);
						} 
                    }
                } else {
                    shOrder.setStatus(ShOrderStatus.CUSTOMMER_CONFIRM);
                }
                shOrder.setFinanceConfirmTime(new Date());
                shOrder.setActualAmount(demandAmount);
                shOrder.setIsProductReturn(isProductReturn);
                shOrder.setUpdateTime(new Date());
                map.put("result", shOrderMapper.updateSelective(shOrder) > 0 ? true : false);
                break;
            case ShOrderStatus.CONFIRM_RECEIPT: //确认收货
                //生成操作日志
                ShLog confirmReceiptShLog = new ShLog();
                confirmReceiptShLog.setShOrderId(shOrderId);
                confirmReceiptShLog.setType(type);
                confirmReceiptShLog.setIsProductReturn(shOrder.getIsProductReturn());
                confirmReceiptShLog.setRemark(remark);
                confirmReceiptShLog.setCreateTime(new Date());
                confirmReceiptShLog.setOperator(account);
                confirmReceiptShLog.setResult(isConfirm);
                shLogMapper.insertSelective(confirmReceiptShLog);

                if (isConfirm.intValue() == ShOrderStatus.SH_CONFIRM_YES) {
                    shOrder.setStatus(ShOrderStatus.SH_FINISH);
                    shOrder.setReceivedProductTime(new Date());
                    Logger.info("[{}]售后单退款",shOrder.getShOrderNo());
                    try {
                    	//退款至余额
						httpService.refund(shOrder.getEmail(), shOrder.getActualAmount(), shOrder.getShOrderNo());
					} catch (Exception e) {
						Logger.info("售后单退款异常:{}",e);
					} 
                } else {
                    shOrder.setStatus(ShOrderStatus.SH_CLOSED);
                }
                shOrder.setUpdateTime(new Date());
                map.put("result", shOrderMapper.updateSelective(shOrder) > 0 ? true : false);
                break;
        }
        return map;
    }

    @Override
    public List<ShLog> getShLogListByShOrderId(JsonNode json) {
        Integer id = json.get("id").asInt();
        return shLogMapper.getShLogListByShOrderId(id);
    }

    @Override
    public Map<String, Object> cancleSaleOrderRefundsApply(JsonNode json) {
        Map<String, Object> map = Maps.newHashMap();
        Integer id = json.get("id").asInt();
        ShOrder shOrder = shOrderMapper.getSalesOrderRefundsById(id);
        if(shOrder != null && shOrder.getStatus() < ShOrderStatus.SH_FINISH) {
            shOrder.setUpdateTime(new Date());
            shOrder.setStatus(ShOrderStatus.SH_CLOSED);
            shOrderMapper.updateSelective(shOrder);
            //生成操作日志
            ShLog confirmReceiptShLog = new ShLog();
            confirmReceiptShLog.setShOrderId(shOrder.getId());
            confirmReceiptShLog.setType(4);
            confirmReceiptShLog.setIsProductReturn(shOrder.getIsProductReturn());
            confirmReceiptShLog.setCreateTime(new Date());
            confirmReceiptShLog.setOperator(shOrder.getEmail());
            confirmReceiptShLog.setResult(2);
            shLogMapper.insertSelective(confirmReceiptShLog);
            map.put("result", true);
        } else {
            map.put("result", false);
            map.put("msg", "售后单状态有误，请检查！");
        }
        return map;
    }

    @Override
    public Map<String, Object> saleOrderRefundsApplyLogistics(JsonNode json) {
        Map<String, Object> map = Maps.newHashMap();
        Integer id = json.get("id").asInt();
        String company = json.get("company").asText();
        String expressCode = json.get("expressCode").asText();
        ShOrder shOrder = shOrderMapper.getSalesOrderRefundsById(id);
        if(shOrder != null && shOrder.getStatus() == ShOrderStatus.SEND_PRODUCT_BACK) {
            shOrder.setUpdateTime(new Date());
            shOrder.setStatus(ShOrderStatus.PLATFORM_RECEIPT);
            shOrder.setCompany(company);
            shOrder.setExpressCode(expressCode);
            shOrder.setSendProductTime(new Date());
            shOrderMapper.updateSelective(shOrder);
            map.put("result", true);
        } else {
            map.put("result", false);
            map.put("msg", "售后单状态有误，请检查！");
        }
        return map;
    }

    @Override
    public Map<String, Object> selectEffectiveShOrderCount(JsonNode json) {
        Map<String, Object> map = Maps.newHashMap();
        String sku = json.get("sku").asText();
        String xsOrderNo = json.get("xsOrderNo").asText();
        ShOrder shOrder = shOrderMapper.selectEffectiveShOrder(sku, xsOrderNo);
        if(shOrder != null) {
            map.put("count", 1);
            map.put("status", shOrder.getStatus());
            map.put("id", shOrder.getId());
        } else {
            map.put("count", 0);
        }
        return map;
    }

    @Override
    public Map<String, Object> selectEffectiveShOrderByDetailOrderId(JsonNode json) {
        Map<String, Object> map = Maps.newHashMap();
        int detailOrderId = json.get("detailOrderId").asInt();
        ShOrder shOrder = shOrderMapper.selectEffectiveShOrderByDetailOrderId(detailOrderId);
        if(shOrder != null) {
            map.put("count", 1);
            map.put("status", shOrder.getStatus());
            map.put("id", shOrder.getId());
        } else {
            map.put("count", 0);
        }
        return map;
    }

    private String setString(String[] strings) {
        return strings != null && strings.length > 0 ? "".equals(strings[0])?null:strings[0] : null;
    }
}
