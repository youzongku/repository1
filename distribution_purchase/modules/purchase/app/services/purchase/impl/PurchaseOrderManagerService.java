package services.purchase.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import dto.purchase.ViewPurchaseDetail;
import dto.purchase.returnod.ReturnOrderInfo;
import entity.purchase.PurchaseOrder;
import entity.purchase.PurchaseOrderDetail;
import entity.purchase.returnod.ReturnOrder;
import entity.purchase.returnod.ReturnOrderDetail;
import mapper.purchase.PurchaseOrderDetailMapper;
import mapper.purchase.PurchaseOrderMapper;
import mapper.purchase.returnod.ReturnOrderDetailMapper;
import mapper.purchase.returnod.ReturnOrderMapper;
import play.Logger;
import services.purchase.IHttpService;
import services.purchase.IPurchaseOrderManagerService;
import utils.purchase.JsonCaseUtil;
import utils.purchase.PriceFormatUtil;
import utils.purchase.ReturnOrderStatus;
import utils.purchase.StringUtils;

public class PurchaseOrderManagerService implements IPurchaseOrderManagerService {
	@Inject	private ReturnOrderMapper returnOrderMapper;
	@Inject	private ReturnOrderDetailMapper returnOrderDetailMapper;
	@Inject	private IHttpService httpService;
	@Inject	private PurchaseOrderMapper purchaseOrderMapper;
	@Inject	private PurchaseOrderDetailMapper purchaseOrderDetailMapper;
	
	@Override
	public Map<String, Object> getReturnInfo4PurchaseOrder(String returnOrderNo) {
		Map<String, Object> result = Maps.newHashMap();
		// 获取退货单
		ReturnOrder ro = returnOrderMapper.selectByReturnOrderNo(returnOrderNo);
		if (ro == null) {
			result.put("suc", false);
			result.put("msg", "退货单不存在");
			return result;
		}

		// 退货单详情
		List<ReturnOrderDetail> details = returnOrderDetailMapper.selectByRoIdList(Lists.newArrayList(ro.getId()));
		String purchaseOrderNo = details.get(0).getPurchaseOrderNo();

		// 采购单信息
		PurchaseOrder queryPoParam = new PurchaseOrder();
		queryPoParam.setPurchaseOrderNo(purchaseOrderNo);
		PurchaseOrder purchaseOrder = purchaseOrderMapper.selectOrder(queryPoParam);
		List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailMapper
				.getAlldetailsByPurNo(purchaseOrderNo);
		List<ViewPurchaseDetail> viewPurchaseDetailList = Lists.newArrayListWithCapacity(purchaseOrderDetails.size());
		// 进行转换
		for (PurchaseOrderDetail aDetail : purchaseOrderDetails) {
			ViewPurchaseDetail viewPurchaseDetail = new ViewPurchaseDetail();
			BeanUtils.copyProperties(aDetail, viewPurchaseDetail);
			viewPurchaseDetail.setTotalReturnAmount(0.00);
			viewPurchaseDetail.setTotalReturnQties(0);
			viewPurchaseDetailList.add(viewPurchaseDetail);
		}
		
		Logger.info("采购单详情，viewPurchaseDetail = {}",viewPurchaseDetailList);

		viewPurchaseDetailList = setPurchaseOrderDetailExpirationDate(purchaseOrderNo, viewPurchaseDetailList);
		setReturnOrderData(purchaseOrderNo, viewPurchaseDetailList);
		setSalesOrderAfterSaleData(purchaseOrderNo, viewPurchaseDetailList);

		result.put("suc", true);
		result.put("purchaseOrder", purchaseOrder);
		result.put("poInfoList", viewPurchaseDetailList);
		return result;
	}

	/**
	 * 为采购详情设置到期日期
	 * @param viewPurchaseDetailList
	 */
	private List<ViewPurchaseDetail> setPurchaseOrderDetailExpirationDate(String purchaseOrderNo, List<ViewPurchaseDetail> viewPurchaseDetailList) {
		// 一个商品可能有多个到期日期，所以要进行拆分
		long count = viewPurchaseDetailList.stream().filter(e->StringUtils.isBlankOrNull(e.getExpirationDate())).count();
		if (count<1) {
			return viewPurchaseDetailList;
		}
		
		JsonNode inRecordNode = null;
		try {
			inRecordNode = httpService.getMicroInRecord(purchaseOrderNo);
		} catch (IOException e) {
			Logger.info("获取采购单[{}]的入库记录异常：{}", purchaseOrderNo, e);
			return viewPurchaseDetailList;
		}
		
		if (inRecordNode==null || !inRecordNode.get("suc").asBoolean()) {
			Logger.info("获取采购单[{}]的入库记录失败", purchaseOrderNo);
			return viewPurchaseDetailList;
		}
		
		List<JsonNode> nodeList = Lists.newArrayList();
		for (Iterator<JsonNode> it = inRecordNode.get("result").iterator(); it.hasNext();) {
			nodeList.add(it.next());
		}
		// 入库记录进行转换
		Map<String, List<JsonNode>> skuWarehouseId2Node = nodeList.stream().collect(Collectors.groupingBy(e->{
			return getKey(e.get("sku").asText(), e.get("warehouseId").asInt(), null);
		}));
		
		/*
		 * TODO 有这些情况
		 * 1、全都是没有到期日期的
		 * 2、之前有选到期日期的需求，所以，部分有到期日期，部分没有到期日期
		 * 3、全都是由到期日期的
		 * 这3种情况都统一处理，获取到入仓记录，重新设置到期日期，因为不涉及到价格之类的，所以这里只设置到期日期即可
		 */
		// 去重
		Set<ViewPurchaseDetail> vpDetaiListWithOutExpirationDate = Sets.newHashSet(viewPurchaseDetailList);
		
		List<ViewPurchaseDetail> newViewPurchaseDetailList = Lists.newArrayList();
		// 获取入库记录，查询到期日期
		for (ViewPurchaseDetail viewPurchaseDetail : vpDetaiListWithOutExpirationDate) {
			String key = getKey(viewPurchaseDetail.getSku(), viewPurchaseDetail.getWarehouseId(), null);
			// 拿到到期日期
			List<JsonNode> list = skuWarehouseId2Node.get(key);
			if (CollectionUtils.isNotEmpty(list)) {
				for (Iterator<JsonNode> it = list.iterator(); it.hasNext(); ) {
					JsonNode expirationDateNode = it.next();
					String expirationDate = JsonCaseUtil.jsonToString(expirationDateNode.get("expirationDate"));
					ViewPurchaseDetail newViewPurchaseDetail = new ViewPurchaseDetail();
					BeanUtils.copyProperties(viewPurchaseDetail, newViewPurchaseDetail);
					newViewPurchaseDetail.setExpirationDate(expirationDate);// 重新设置到期日期
					newViewPurchaseDetail.setQty(JsonCaseUtil.jsonToInteger(expirationDateNode.get("qty")));// 数量
					newViewPurchaseDetail.setCapFee(JsonCaseUtil.jsonToDouble(expirationDateNode.get("capfee")));// 均摊价capfee
					// 是否是赠品（0：不是，1：是）is_gift
					boolean isgift = JsonCaseUtil.jsonToInteger(expirationDateNode.get("isGift"))==1;
					newViewPurchaseDetail.setIsgift(isgift);
					newViewPurchaseDetailList.add(newViewPurchaseDetail);
				}
			}
		}
		
		return newViewPurchaseDetailList;
	}
	
	/**
	 * 设置发货单售后数据
	 * 
	 * @param purchaseOrderNo
	 * @param viewPurchaseDetailList
	 */
	private void setSalesOrderAfterSaleData(String purchaseOrderNo, List<ViewPurchaseDetail> viewPurchaseDetailList) {
		
		// 获取发货单售后单
		JsonNode resultNode = null;
		try {
			resultNode = httpService.getReturnAmountCapfee4Sku(purchaseOrderNo);
		} catch (IOException e) {
			Logger.info("获取发货单售后退款均摊异常，{}", e);
			return;
		}

		if (resultNode == null || !resultNode.get("suc").asBoolean()) {
			Logger.info("获取发货单售后退款均摊为空");
			return;
		}

		List<JsonNode> shOrderDetailNodeList = Lists.newArrayList();
		for (Iterator<JsonNode> it = resultNode.get("shOrderInfoList").iterator(); it.hasNext();) {
			JsonNode shOrderInfoNode = it.next();
			for (Iterator<JsonNode> shOrderDetailList = shOrderInfoNode.get("shOrderDetailList")
					.iterator(); shOrderDetailList.hasNext();) {
				shOrderDetailNodeList.add(shOrderDetailList.next());
			}
		}

		Map<String, List<JsonNode>> key2ShOrderDetailList = shOrderDetailNodeList.stream()
				.collect(Collectors.groupingBy(node -> {
					return getKey(JsonCaseUtil.jsonToString(node.get("purchaseOrderNo")),
							JsonCaseUtil.jsonToString(node.get("sku")),
							JsonCaseUtil.jsonToInteger(node.get("warehoseid")),
							JsonCaseUtil.jsonToString(node.get("expirationDateStr")));
				}));

		// 为每个采购单详情设置售后数据
		for (ViewPurchaseDetail viewPurchaseDetail : viewPurchaseDetailList) {
			// 拿到商品对应的退货详情
			List<JsonNode> shOrderDetailList = key2ShOrderDetailList.get(getKey(purchaseOrderNo, viewPurchaseDetail.getSku(),
					viewPurchaseDetail.getWarehouseId(), viewPurchaseDetail.getExpirationDate()));
			if (CollectionUtils.isNotEmpty(shOrderDetailList)) {
				// 一个商品的总退货数量
				int totalReturnQties = Lists.transform(shOrderDetailList, n -> n.get("qty").asInt()).stream().reduce(0,
						(x, y) -> x + y);
				// 一个商品的总退货金额
				BigDecimal totalReturnAmount = shOrderDetailList.stream()
						.map(n -> new BigDecimal(n.get("actualAmountCapfee").asDouble()))
						.reduce(new BigDecimal(0), (x, y) -> x.add(y));
				// 原有的加上新的
				viewPurchaseDetail.setTotalReturnAmount(
						PriceFormatUtil.toFix2(new BigDecimal(viewPurchaseDetail.getTotalReturnAmount()).add(totalReturnAmount)));
				viewPurchaseDetail.setTotalReturnQties(viewPurchaseDetail.getTotalReturnQties() + totalReturnQties);
			}
		}
	}

	/**
	 * 设置微仓退货情况
	 * 
	 * @param purchaseOrderNo
	 * @param viewPurchaseDetailList
	 */
	private void setReturnOrderData(String purchaseOrderNo, List<ViewPurchaseDetail> viewPurchaseDetailList) {
		// 采购单的退货情况
		List<ReturnOrderInfo> roInfoList = returnOrderMapper.selectReturnOrderInfo(purchaseOrderNo,
				ReturnOrderStatus.AUDIT_PASSED);
		
		Logger.info("采购单的退货情况：{}",roInfoList);

		// sku_warehouseId_expirationDate = 退货详情集合
		Map<String, List<ReturnOrderInfo>> key2RoInfos = roInfoList.stream().collect(Collectors.groupingBy(d -> {
			return getKey(d.getSku(), d.getWarehouseId(), d.getExpirationDate());
		}));
		
		Logger.info("key2RoInfos = {}",key2RoInfos);

		// 为每个采购单详情设置退货数据
		for (ViewPurchaseDetail viewPurchaseDetail : viewPurchaseDetailList) {
			Logger.info("key是{}",getKey(viewPurchaseDetail.getSku(), viewPurchaseDetail.getWarehouseId(), viewPurchaseDetail.getExpirationDate()));
			// 拿到商品对应的退货详情
			List<ReturnOrderInfo> roInfos = key2RoInfos
					.get(getKey(viewPurchaseDetail.getSku(), viewPurchaseDetail.getWarehouseId(), viewPurchaseDetail.getExpirationDate()));
			if (CollectionUtils.isNotEmpty(roInfos)) {
				// 一个商品的总退货数量
				int totalReturnQties = roInfos.stream().map(roInfo -> roInfo.getReturnQty()).reduce(0, (x, y) -> x + y);
				Logger.info("totalReturnQties="+totalReturnQties);
				// 一个商品的总退货金额
				BigDecimal totalReturnAmount = roInfos.stream()
						.map(roInfo -> new BigDecimal(roInfo.getActualTotalReturnAmount()))
						.reduce(new BigDecimal(0), (x, y) -> x.add(y));
				// 原有的加上新的
				viewPurchaseDetail.setTotalReturnAmount(
						PriceFormatUtil.toFix2(new BigDecimal(viewPurchaseDetail.getTotalReturnAmount()).add(totalReturnAmount)));
				viewPurchaseDetail.setTotalReturnQties(viewPurchaseDetail.getTotalReturnQties() + totalReturnQties);
			}
		}
	}

	private String getKey(String purchaseOrderNo, String sku, int warehouseId, String expirationDate) {
		return purchaseOrderNo + "_" + getKey(sku, warehouseId, expirationDate);
	}

	private String getKey(String sku, Integer warehouseId, String expirationDate) {
		String key = sku + "_" + warehouseId;
		if (StringUtils.isNotBlankOrNull(expirationDate)) {
			key += "_" + expirationDate;
		}
		return key.toString();
	}

}
