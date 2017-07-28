package services.warehousing.impl;

import java.util.Date;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import dto.warehousing.ReturnMess;
import entity.warehousing.ErpPushInvenRecode;
import mapper.warehousing.ErpPushInvenDetailMapper;
import mapper.warehousing.ErpPushInvenRecodeMapper;
import mapper.warehousing.GoodsInventoryMapper;
import mapper.warehousing.InventoryChangeHistoryMapper;
import mapper.warehousing.WarehouseMapper;
import services.warehousing.IWarehInvenService;

/**
 * Created by luwj on 2016/1/6.
 */
public class WarehInvenService implements IWarehInvenService {

	@Inject
	WarehouseMapper warehouseMapper;

	@Inject
	ErpPushInvenRecodeMapper erpPushInvenRecode;

	@Inject
	ErpPushInvenDetailMapper detailMapper;

	@Inject
	GoodsInventoryMapper goodsInventoryMapper;

	@Inject
	InventoryChangeHistoryMapper inventoryHistoryMapper;

	@Inject
	EventBus eventBus;

	/**
	 * 存储仓库信息（全量接口）
	 * 
	 * @param node
	 * @return
	 */
	@Override
	public ReturnMess saveWarehouse(JsonNode node) {
		/*Logger.debug(">>saveWarehouse>>>node>>>" + node.toString());
		ReturnMess returnMess = new ReturnMess("0", "");
		try {
			if (node.has("totalCount") && node.has("warehouse")) {
				ObjectMapper mapper = new ObjectMapper();
				TypeReference<List<Warehouse>> typeRef = new TypeReference<List<Warehouse>>() {
				};
				List<Warehouse> lists = mapper.readValue(node.get("warehouse")
						.toString(), typeRef);
				String batchNo = DateTimeUtils.date2string(new Date(),
						DateTimeUtils.FULL_DATE_FORMAT);
				List<Warehouse> postLists = Lists.newArrayList();
				for (int i = 0; i < lists.size(); i++) {
					Warehouse warehouse = lists.get(i);
					try {
						boolean flag = true;
						if (StringUtils.isNotBlank(warehouse.getWarehouseId())) {
							Warehouse ware = new Warehouse();
							ware.setWarehouseId(warehouse.getWarehouseId());
							ware = warehouseMapper.selectByPrimaryKey(ware);
							if (ware != null) {
								ware.setRemarks("");
								String hisRe = Json.toJson(ware).toString();
								BeanUtils.copyProperties(warehouse, ware,
										new String[] { "id" });
								Logger.debug(">>>ware>>>22>>"
										+ Json.toJson(ware).toString());
								 TO DO 目前只做了保存上一次记录的操作 待后续完善+ 
								ware.setRemarks("");
								ware.setRemarks("原记录：" + hisRe);
								ware.setBatchNo(batchNo);
								ware.setLastUpdated(new Date());

								warehouseMapper
										.updateByPrimaryKeySelective(ware);
								postLists.add(ware);
								flag = false;
							}
						}
						if (flag) {
							warehouse.setCreated(new Date());
							warehouse.setStatus(1);// 启用
							warehouse.setBatchNo(batchNo);
							postLists.add(warehouse);
							warehouseMapper.insertSelective(warehouse);
						}
					} catch (Exception e) {
						Logger.error(
								"erp推送仓库数据保存未能成功：" + Json.toJson(warehouse), e);
						returnMess = new ReturnMess("1", "异常情况");
					}
				}
				// add by xu_shengen
				if (postLists.size() > 0) {
					eventBus.post(new PostInventory2B2BEvent(null, postLists));
				}
				// 把erp未推送的仓库的状态修改为禁用
				List<Warehouse> wares = warehouseMapper
						.getWaresByBatch(batchNo);
				for (Warehouse w : wares) {
					w.setStatus(0);
					w.setLastUpdated(new Date());
					w.setRemarks("");
					w.setRemarks("系统修改，原记录：" + Json.toJson(w));
					warehouseMapper.updateByPrimaryKey(w);
				}
			} else {
				returnMess = new ReturnMess("1", "input error!参数错误");
			}
		} catch (Exception e) {
			Logger.error("erp推送仓库数据保存未能成功", e);
			returnMess = new ReturnMess("1", "异常情况");
		}
		return returnMess;*/
		return null;
	}

	/**
	 * 存储商品库存信息（增量接口）
	 * 
	 * @param node
	 * @return
	 */
	@Override
	public ReturnMess saveInvenInfo(JsonNode node) {
		/*long a = new Date().getTime();
		Logger.debug(">>saveInvenInfo>>>node>>>" + node.toString());
		ReturnMess returnMess = new ReturnMess("0", "");
		try {
			returnMess = checkParams(node);
			if ("0".equals(returnMess.getErrorCode())) {
				// 该批次的库存变化是否已经经过处理
				ErpPushInvenRecode record = erpPushInvenRecode
						.selectByUniqueId(node.get("uniqueId").asInt());
				if (record != null) {
					// 查得到结果说明已经库存增量已经实施
					Logger.error("erp推送仓库数据重复，不进行库存变化");
					returnMess = new ReturnMess("1", "库存增量唯一标识符重复："
							+ node.get("uniqueId").asInt());
					return returnMess;
				}

				// 库存批次唯一标识符不重复再进行库存的变化
				int id = saveRecode(node);
				ObjectMapper mapper = new ObjectMapper();
				TypeReference<List<ErpPushInvenDetail>> typeRef = new TypeReference<List<ErpPushInvenDetail>>() {
				};
				List<ErpPushInvenDetail> lists = mapper.readValue(
						node.get("invent").toString(), typeRef);
				Logger.debug(">>saveInvenInfo>>>lists>>>"
						+ Json.toJson(lists).toString());
				List<GoodsInventory> postList = Lists.newArrayList();
				for (int i = 0; i < lists.size(); i++) {
					// erp推送明细
					ErpPushInvenDetail detail = lists.get(i);
					Warehouse ware = new Warehouse();
					ware.setWarehouseId(String.valueOf(detail.getWarehouseId()));
					ware = warehouseMapper.selectByPrimaryKey(ware);
					if (ware != null) {
						detail.setWarehouseId(detail.getWarehouseId());
						detail.setCreated(new Date());
						detail.setPushId(id);
						Logger.debug(">>>>detail>>>"
								+ Json.toJson(detail).toString());
						try {
							detailMapper.insertSelective(detail);
							// 更新库存
							GoodsInventory goodsInventory = new GoodsInventory();
							goodsInventory.setSku(detail.getSku());
							goodsInventory.setWarehouseId(detail
									.getWarehouseId());
							goodsInventory = goodsInventoryMapper
									.selectByPrimaryKey(goodsInventory);
							if (goodsInventory == null) {
								goodsInventory = new GoodsInventory();
								goodsInventory.setSku(detail.getSku());
								goodsInventory.setWarehouseId(detail
										.getWarehouseId());
								goodsInventory.setWarehouseName(ware
										.getWarehouseName());
								goodsInventory.setTotalStock(detail.getStock());
								goodsInventory.setFrozenStock(0);
								goodsInventory.setAvailableStock(detail
										.getStock());
								goodsInventory.setCostprice(detail
										.getCostPrice());
								goodsInventory.setLastUpdated(new Date());
							
								goodsInventoryMapper
										.insertSelective(goodsInventory);
							} else {
								if (detail.getType() == 0) {// 库存同步—商品在该仓库的全部库存数量（重新初始化库存数量）
									// 2016-07-04
									// 库存初始化操作（一次性功能，本日生产环境使用完毕之后即下线。初始化的同时还需要扣除出仓记录）

									// 获取该SKU的出仓记录总数
//									int stockOutTotal = inventoryHistoryMapper
//											.getStockOutTotalQty(
//													detail.getSku(),
//													detail.getWarehouseId());
									
									//真实入仓数
									//int actualStockInt = detail.getStock() - stockOutTotal;
//									if (actualStockInt < 0) {
//										Logger.error(
//												"===========================注意：[{}(id:{})]的[{}]，初始化库存后，库存为负数({})",
//												ware.getWarehouseName(),
//												detail.getWarehouseId(),
//												detail.getSku(),
//												actualStockInt);
//									}
//									if (actualStockInt == 0) {
//										Logger.error(
//												"===========================注意：[{}(id:{})]的[{}]，初始化库存后，库存为0",
//												ware.getWarehouseName(),
//												detail.getWarehouseId(),
//												detail.getSku());
//									}

									goodsInventory.setTotalStock(detail.getStock());
									goodsInventory.setAvailableStock(detail.getStock());
								} else {// 1.盘点，2.采购入库，3.其他入库
									goodsInventory.setTotalStock(goodsInventory
											.getTotalStock()
											+ detail.getStock());
									goodsInventory
											.setAvailableStock(goodsInventory
													.getAvailableStock()
													+ detail.getStock());
								}
								goodsInventory.setCostprice(detail
										.getCostPrice());
								goodsInventory.setLastUpdated(new Date());
								goodsInventoryMapper
										.updateByPrimaryKeySelective(goodsInventory);
							}
							postList.add(goodsInventory);
						} catch (Exception e) {
							Logger.error("erp推送仓库数据保存不成功:"
									+ Json.toJson(detail).toString(), e);
							returnMess = new ReturnMess("1", "内部异常:" + e);
						}
					} else {
						returnMess = new ReturnMess("1", "仓库id："
								+ detail.getWarehouseId() + "不存在");
					}
				}
				// add by xu_shengen
				if (postList.size() > 0) {
					eventBus.post(new PostInventory2B2BEvent(postList, null));
				}
			}
		} catch (Exception e) {
			Logger.error("erp推送仓库数据保存未能成功", e);
			returnMess = new ReturnMess("1", "异常情况：" + e);
		}
		Logger.debug(">>>>time>>>>>" + (new Date().getTime() - a));
		Logger.debug(">>>saveInvenInfo>>returnMess>>"
				+ Json.toJson(returnMess).toString());
		return returnMess;*/
		return null;
	}

	/**
	 * 校验erp推送商品库存信息格式
	 * 
	 * @param node
	 * @return
	 */
	private ReturnMess checkParams(JsonNode node) {
		ReturnMess returnMess = new ReturnMess("0", "");
		if (node.has("totalCount") && node.has("invent")) {
			try {
				JsonNode invents = node.get("invent");
				for (JsonNode invent : invents) {
					if (!invent.has("sku") || invent.get("sku") == null
							|| invent.get("sku").asText().equals("") ) {
						returnMess = new ReturnMess("1", "错误的条目-1："
								+ invent.toString());
					}
					if (!invent.has("warehouseId")
							|| invent.get("warehouseId") == null
							|| "".equals(invent.get("warehouseId").asText())) {
						returnMess = new ReturnMess("1", "错误的条目-2："
								+ invent.toString());
					}
					if (!invent.has("stock") || invent.get("stock") == null
							||"".equals(invent.get("stock").asText())) {
						returnMess = new ReturnMess("1", "错误的条目-3："
								+ invent.toString());
					}
					if (!invent.has("costPrice")
							|| invent.get("costPrice") == null
							|| "".equals(invent.get("costPrice").asText())) {
						returnMess = new ReturnMess("1", "错误的条目-4："
								+ invent.toString());
					}
					if (!invent.has("type") || invent.get("type") == null
							|| "".equals(invent.get("type").asText())) {
						returnMess = new ReturnMess("1", "错误的条目-5："
								+ invent.toString());
					}
				}
			} catch (Exception e) {
				returnMess = new ReturnMess("1", "input error!参数错误");
			}
		} else {
			returnMess = new ReturnMess("1", "input error!参数错误");
		}
		return returnMess;
	}

	/**
	 * 保存erp推送记录记录
	 * 
	 * @param node
	 * @return id 记录Id
	 */
	private int saveRecode(JsonNode node) {
		ErpPushInvenRecode recode = new ErpPushInvenRecode();
		recode.setContents(node.toString());
		recode.setCreated(new Date());
		recode.setUniqueId(node.get("uniqueId").asInt());
		erpPushInvenRecode.insertSelective(recode);
		return recode.getId();
	}
}
