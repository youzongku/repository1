package services.sales.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import dto.sales.TaoBaoOrderForm;
import entity.platform.order.template.TaoBaoOrder;
import entity.platform.order.template.TaoBaoOrderGoods;
import entity.sales.SaleDetail;
import mapper.sales.SaleDetailMapper;
import mapper.sales.TaoBaoOrderGoodsMapper;
import mapper.sales.TaoBaoOrderMapper;
import pager.sales.Pager;
import play.Logger;
import services.sales.IHttpService;
import services.sales.ITaoBaoOrderService;
import util.sales.JsonCaseUtil;

public class TaoBaoOrderService implements ITaoBaoOrderService {
	@Inject private	SaleDetailMapper saleDetailMapper;
	@Inject private	TaoBaoOrderMapper taoBaoOrderMapper;
	@Inject private TaoBaoOrderGoodsMapper taoBaoOrderGoodsMapper;
	@Inject private	IHttpService httpService;

	@Override
	public int insert(TaoBaoOrder record) {
		return taoBaoOrderMapper.insert(record);
	}

	@Override
	public int insertSelective(TaoBaoOrder record) {
		return taoBaoOrderMapper.insertSelective(record);
	}

	@Override
	public TaoBaoOrder selectByOrderNoAndEmail(String orderNo,String email) {
		List<TaoBaoOrder> orders = this.taoBaoOrderMapper.selectByOrderNoAndEmail(orderNo, email);;
		if (orders!=null && orders.size()>0) {
			return orders.get(0);
		}
		return null;
	}

	@Override
	public Pager<TaoBaoOrder> getAllOrders(TaoBaoOrderForm form) {
		List<TaoBaoOrder> allOrders = taoBaoOrderMapper.getAllOrders(form);
		Pager<TaoBaoOrder> pager = new Pager<TaoBaoOrder>(allOrders, form.getPageNo(), form.getPageSize(),taoBaoOrderMapper.getTotal(form));
		return pager;
	}

	@Override
	public int deleteOrder(String orderNo) {
		return taoBaoOrderMapper.deleteOrder(orderNo);
	}

	@Override
	public int deleteLogicOrder(String orderNo,String email) {
		return taoBaoOrderMapper.deleteLogicOrder(orderNo,email);
	}

	public int saveOrder(TaoBaoOrderForm form) {
		return taoBaoOrderMapper.saveOrder(form);
	}

	@Override
	public int batchDeleteOrder(TaoBaoOrderForm form) {
		return taoBaoOrderMapper.batchDeleteOrder(form);
	}

	@Override
	public TaoBaoOrder selectBygroube(TaoBaoOrder appointOrder) {
		return taoBaoOrderMapper.selectBygroube(appointOrder);
	}

	@Override
	public Map<String, Object> checkByOrderNoAndWarehouseId(JsonNode node ) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("suc", false);
		Integer warehouseId = node.get("warehouseId").asInt();
		
		List<String> existsku = Lists.newArrayList();
		for (Iterator<JsonNode> it = node.get("list").iterator();it.hasNext();) {
			existsku.add(it.next().asText());
		}
		
		List<SaleDetail> SaleDetails = saleDetailMapper.selectByOrderIdAndWarehouseIdAndSku(node.get("platformOrderNo").asText(), 
				warehouseId,existsku);
		for (SaleDetail detail : SaleDetails) {
			existsku.remove(detail.getSku());
		}
		
		if(existsku.size() > 0) {
			result.put("suc", true);
			result.put("msg", existsku);
		} else {
			result.put("msg", "订单重复，该待生成信息无法生成订单");
		}
		
		return result;
	}

	@Override
	public Map<String, Object> queryOrders(TaoBaoOrderForm taoBaoOrderForm, Integer model) {
		Map<String, Object> response = Maps.newHashMap();
		int total = taoBaoOrderMapper.getTotal(taoBaoOrderForm);
		if (total <= 0) {
			response.put("suc", true);
			response.put("data", new Pager<TaoBaoOrder>(Lists.newArrayList(), taoBaoOrderForm.getPageNo(),
					taoBaoOrderForm.getPageSize(), 0));
			return response;
		}
		String email = taoBaoOrderForm.getEmail();
		// 分页订单
		List<TaoBaoOrder> allOrders = taoBaoOrderMapper.getAllOrders(taoBaoOrderForm);
		List<String> orderNos = Lists.transform(allOrders, order -> order.getOrderNo());
		// 根据订单查询商品
		List<TaoBaoOrderGoods> allGoods = taoBaoOrderGoodsMapper.goodsLists(orderNos, email);
		if (CollectionUtils.isNotEmpty(allGoods)) {
			Set<String> skus = Sets.newHashSet();
			skus.addAll(Lists.transform(allGoods, good -> good.getSku()));
			JsonNode productStrNode = null;
			JsonNode result = null;
			if (skus.size() > 0) {
				try {
					productStrNode = httpService.getProducts(taoBaoOrderForm.getEmail(), Lists.newArrayList(skus), null,
							model);
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
			for (TaoBaoOrder order : allOrders) {
				order.setGoods(ordersMap.get(order.getEmail() + "_" + order.getOrderNo()));
			}
		}
		response.put("suc", true);
		response.put("data",
				new Pager<TaoBaoOrder>(allOrders, taoBaoOrderForm.getPageNo(), taoBaoOrderForm.getPageSize(), total));
		return response;
	}
	
}
