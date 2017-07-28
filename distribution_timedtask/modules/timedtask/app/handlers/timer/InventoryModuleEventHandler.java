package handlers.timer;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import mapper.timer.ProductInventoryOrderLockMapper;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import service.timer.IProductCloudInventoryService;
import service.timer.IProductInventoryBatchDetailService;
import service.timer.IWarehInvenService;
import service.timer.IinventorySyncRecordService;
import util.timer.DateUtils;
import util.timer.HttpUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

import entity.timer.InventorySyncRecord;
import entity.timer.ProductInventoryBatchDetail;
import entity.timer.ProductInventoryDetail;
import entity.timer.ProductInventoryOrderLock;
import entity.timer.Warehouse;
import events.timer.ExternalWarehouseInventorySynchroizationEvent;
import events.timer.InventorySynchronizationEvent;
import events.timer.ProductReleaseCloudLockEvent;

public class InventoryModuleEventHandler {

	@Inject
	private IProductCloudInventoryService productCloudInventoryService;

	@Inject
	private IWarehInvenService warehInvenService;

	@Inject
	private IinventorySyncRecordService inventorySyncRecordService;

	@Inject
	private IProductInventoryBatchDetailService productInventoryBatchDetailService;

	@Inject
	private ProductInventoryOrderLockMapper productInventoryOrderLockMapper;

	private static String ERP_API_KEY = "";
	private static String ERP_HOST = "";
	private static String STOCK_IN_API = "";
	private static String BOX_INFO_API = "";
	private static String EXTERNAL_WAREHOUSE_API = "";
	private static String JSONPARSE_EXCEPTION_BEGINTIME = "";
	public static int EXTERNAL_WAREHOUSE_STOCK=-1;
	// ERP出入库记录类型
	private static String PRODUCT_INSTOCK = "";// 入仓
	private static String PRODUCT_CHANGE_BOX = "";// 换箱
	private static String PRODUCT_CHECK_COUNT = "";// 盘点
	private static String PRODUCT_OUTSTOCK = "";// 出仓

	static {
		if (ERP_API_KEY == "") {
			Configuration config = Play.application().configuration()
					.getConfig("erp");
			ERP_API_KEY = config.getString("apiKey");
		}
		if (EXTERNAL_WAREHOUSE_API == "") {
			Configuration config = Play.application().configuration()
					.getConfig("erp");
			EXTERNAL_WAREHOUSE_API = config.getString("externalWarehouseApi");
		}
		if(EXTERNAL_WAREHOUSE_STOCK==-1){
			Configuration config = Play.application().configuration().getConfig("erp");
			EXTERNAL_WAREHOUSE_STOCK = config.getInt("externalWarehouseStock")==null?-1:config.getInt("externalWarehouseStock");
		}
		if (ERP_HOST == "") {
			Configuration config = Play.application().configuration()
					.getConfig("erp");
			ERP_HOST = config.getString("host");
		}
		if (STOCK_IN_API == "") {
			Configuration config = Play.application().configuration()
					.getConfig("erp");
			STOCK_IN_API = config.getString("stockInApi");
			BOX_INFO_API = config.getString("boxInfo");
			PRODUCT_INSTOCK = config.getString("productInstock");// 暂未使用该常量，只是维护起来，方便了解类型
			PRODUCT_CHANGE_BOX = config.getString("productChangeBox");// 暂未使用该常量，只是维护起来，方便了解类型
			PRODUCT_CHECK_COUNT = config.getString("productCheckCount");
			PRODUCT_OUTSTOCK = config.getString("productOutstock");
		}

		if (ERP_API_KEY == "") {
			Configuration config = Play.application().configuration()
					.getConfig("erp");
			ERP_API_KEY = config.getString("apiKey");
		}
		if (EXTERNAL_WAREHOUSE_API == "") {
			Configuration config = Play.application().configuration()
					.getConfig("erp");
			EXTERNAL_WAREHOUSE_API = config.getString("externalWarehouseApi");
		}
		if (ERP_HOST == "") {
			Configuration config = Play.application().configuration()
					.getConfig("erp");
			ERP_HOST = config.getString("host");
		}
	}

	/**
	 * 同步ERP入仓记录
	 * 
	 * @param event
	 * @throws ParseException
	 */
	@Subscribe
	public void executeInventorySynchronization(
			InventorySynchronizationEvent event) {
		if (SystemEventHandler.run_timed_task) {
			// 获取每个可用进口仓库的库位入库记录
			List<Warehouse> validableWarehouses = warehInvenService
					.queryWarehouse(null);

			// 组织时间参数
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("page_index", 1);
			paramMap.put("page_size", 10000);
			paramMap.put("timestamp", 0);

			// 时间参数范围，当前时间点，往前推5分钟，然后取2分钟的时间区间
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, -5);
			String beginTime = DateUtils.date2string(calendar.getTime(),
					DateUtils.FORMAT_FULL_DATETIME);
			if ("".equals(JSONPARSE_EXCEPTION_BEGINTIME)) {
				paramMap.put("updated_begin", beginTime);
			} else {
				paramMap.put("updated_begin", JSONPARSE_EXCEPTION_BEGINTIME);
			}
			calendar.add(Calendar.MINUTE, 2);
			paramMap.put("updated_end", DateUtils.date2string(
					calendar.getTime(), DateUtils.FORMAT_FULL_DATETIME));

			Logger.info(
					"[inventoryEvent]========	获取时间段位于：[{}]~[{}]入仓记录	========[inventoryEvent]",
					paramMap.get("updated_begin"), paramMap.get("updated_end"));

			for (Warehouse warehouse : validableWarehouses) {
				JsonNode inRecordDatas = null;
				try {
					paramMap.put("stock_id", warehouse.getWarehouseId());
					String resultStrRecordIn = HttpUtil.post(
							Json.toJson(paramMap).toString(), ERP_HOST
									+ STOCK_IN_API + "?api_key=" + ERP_API_KEY);

					// String -> JsonNode
					Logger.info(
							"[inventoryEvent]========	时间[{}]~[{}],仓库[{}]入仓记录同步返回信息[{}]	========[inventoryEvent]",
							paramMap.get("updated_begin"),
							paramMap.get("updated_end"),
							warehouse.getWarehouseId(), resultStrRecordIn);
					JsonNode resultNode = Json.parse(resultStrRecordIn);

					// 该时间段如有入仓记录
					inRecordDatas = resultNode.get("result");
					if (!"".equals(JSONPARSE_EXCEPTION_BEGINTIME)) {
						JSONPARSE_EXCEPTION_BEGINTIME = "";
					}
				} catch (Exception e) {
					if ("".equals(JSONPARSE_EXCEPTION_BEGINTIME)) {
						JSONPARSE_EXCEPTION_BEGINTIME = beginTime;
					}
					break;
				}
				if (inRecordDatas != null && inRecordDatas.size() != 0) {

					Logger.info(
							"[inventoryEvent]========	时间[{}]~[{}],仓库[{}]有入仓记录，入仓信息数量[{}]	========[inventoryEvent]",
							paramMap.get("updated_begin"),
							paramMap.get("updated_end"),
							warehouse.getWarehouseId(), inRecordDatas.size());

					// 箱号
					String boxNo = "";
					// SKU
					String sku = "";
					// 到期日期字符串，yyyy-dd-MM
					String productionDate = "";

					// 遍历入仓记录，获取箱号入仓日期
					for (JsonNode in_record : inRecordDatas) {

						sku = in_record.get("sku").asText();
						boxNo = in_record.get("box_no").asText();

						// 校验当前入仓的SKU是否已经初始化，如果没有初始化，那么跳过。直到SKU初始化完成后，才执行云仓入库操作
						InventorySyncRecord isr = inventorySyncRecordService
								.selectBySkuAndWarehouseId(sku, Integer
										.valueOf(warehouse.getWarehouseId()));

						// 同步记录不存在，需要初始化记录
						if (isr == null
								|| (isr != null && isr.getIsActive() == 0)) {
							Logger.info(
									"[inventoryEvent]========	[{}]未初始化库存，不进行入库记录的保存	========[inventoryEvent]",
									sku);
							continue;
						}

						// 记录去重复
						ProductInventoryBatchDetail detail = productInventoryBatchDetailService
								.getBatchDetailByIdentifier(in_record
										.get("_id").asText());
						// 根据唯一标识符判断记录唯一性，忽略重复记录
						if (detail != null) {
							Logger.info(
									"[inventoryEvent]========	SKU为[{}]，标识符为[{}]的入仓记录已经添加过，该记录将被系统忽略	========[inventoryEvent]",
									sku, in_record.get("_id").asText());
							continue;
						}

						try {
							// 如果当前出入库记录的时间在该SKU库存初始化之前，那么也要跳过，只同步初始化时间点之后的出入仓记录
							if (isr.getLastSyncingTime().getTime() > DateUtils
									.string2date(
											in_record.get("updated_time")
													.asText(),
											DateUtils.FORMAT_FULL_DATETIME_MI)
									.getTime()) {
								continue;
							}
						} catch (ParseException e) {
							Logger.info(
									"[inventoryEvent]========	ID为[{}]的ERP出入仓记录，[操作时间]解析异常，无法判定是否是发生在同步后的出入仓记录，该记录将被系统忽略	========[inventoryEvent]",
									sku, in_record.get("_id").asText());
						}

						// 筛选该入库记录，是否需要变动库存
						String stockType = in_record.get("stock_type").asText();

						boolean isStockOut = false;

						// “盘点”类型的入仓记录，需要手动校验是出仓还是入仓
						if (PRODUCT_CHECK_COUNT.equals(stockType)) {
							// 确认一下是盘亏还是盘盈
							int box_before_change_count = in_record.get(
									"box_before_change_count").asInt();
							int box_after_change_count = in_record.get(
									"box_after_change_count").asInt();

							// 盘亏，意味着出库
							if (box_before_change_count
									- box_after_change_count > 0) {
								isStockOut = true;
							}

						}

						// 出库
						if (PRODUCT_OUTSTOCK.equals(stockType)) {
							isStockOut = true;
							// 忽略两种情况下的出库

							// 订单自动出库
							if (70 == in_record.get("record_type").asInt()) {
								Logger.info(
										"[inventoryEvent]========	出入库记录ID[{}]为自动出库，忽略，但是记录该记录，不进行库存变化	========[inventoryEvent]",
										in_record.get("_id").asText());
								ProductInventoryBatchDetail record = new ProductInventoryBatchDetail();
								try {
									record.setSku(in_record.get("sku").asText());
									record.setProductName(in_record.get(
											"sku_name_cn").asText());
									record.setContainerStockChange(-Integer
											.valueOf(in_record.get(
													"change_count").asText()));
									record.setRemark(in_record.get("remark")
											.asText());
									record.setUpdateDate(DateUtils.string2date(
											in_record.get("updated_time")
													.asText(),
											DateUtils.FORMAT_FULL_DATETIME_MI));
									record.setContainerNumber(in_record.get(
											"box_no").asText());
									record.setStorageLocation(in_record.get(
											"shelf_code").asText());
									record.setWarehouseName(warehouse
											.getWarehouseName());
									record.setWarehouseId(Integer
											.valueOf(warehouse.getWarehouseId()));

									// 请求货箱数据
									Map<String, Object> paramMapBoxInfo = new HashMap<String, Object>();
									paramMapBoxInfo.put("box_no", boxNo);
									paramMapBoxInfo.put("timestamp", 0);
									String resultStrBoxInfo = HttpUtil
											.post(Json.toJson(paramMapBoxInfo)
													.toString(), ERP_HOST
													+ BOX_INFO_API
													+ "?api_key=" + ERP_API_KEY);

									// String -> JsonNode
									JsonNode resultNodeBoxInfo = Json
											.parse(resultStrBoxInfo);

									// 有具体货箱数据进行入仓处理
									JsonNode boxProductsDatas = resultNodeBoxInfo
											.get("result");
									if (boxProductsDatas.size() != 0) {

										for (JsonNode boxInfo : boxProductsDatas) {
											JsonNode boxInfoDetails = boxInfo
													.get("product_shelf_batchBox_products");

											// 遍历这个货箱中的商品数据，获得对应SKU的到期日期
											for (JsonNode boxInfoDetail : boxInfoDetails) {
												if (sku.equals(boxInfoDetail
														.get("sku").asText())) {
													productionDate = boxInfoDetail
															.get("productionDate")
															.asText("2099-01-01");
												}
											}
										}
									}
									record.setIdentifier(in_record.get("_id")
											.asText());
									record.setExpirationDate(DateUtils
											.string2date(productionDate,
													DateUtils.FORMAT_DATE_PAGE));
								} catch (ParseException e) {
									e.printStackTrace();
									Logger.warn(
											"[inventoryEvent]========	出入库记录ID[{}]为自动出库，但是日期格式化异常	========[inventoryEvent]",
											in_record.get("_id").asText());
								}
								productInventoryBatchDetailService
										.insertSelective(record);
								continue;
							}

							// 进口保税订单出库
							if (80 == in_record.get("record_type").asInt()) {
								Logger.info(
										"[inventoryEvent]========	出入库记录ID[{}]进口保税订单出库，忽略，但是记录该记录，不进行库存变化	========[inventoryEvent]",
										in_record.get("_id").asText());

								ProductInventoryBatchDetail record = new ProductInventoryBatchDetail();
								try {

									record.setSku(in_record.get("sku").asText());
									record.setProductName(in_record.get(
											"sku_name_cn").asText());
									record.setContainerStockChange(-Integer
											.valueOf(in_record.get(
													"change_count").asText()));
									record.setRemark(in_record.get("remark")
											.asText());
									record.setUpdateDate(DateUtils.string2date(
											in_record.get("updated_time")
													.asText(),
											DateUtils.FORMAT_FULL_DATETIME_MI));
									record.setContainerNumber(in_record.get(
											"box_no").asText());
									record.setStorageLocation(in_record.get(
											"shelf_code").asText());
									record.setWarehouseName(warehouse
											.getWarehouseName());
									record.setWarehouseId(Integer
											.valueOf(warehouse.getWarehouseId()));

									// 请求货箱数据
									Map<String, Object> paramMapBoxInfo = new HashMap<String, Object>();
									paramMapBoxInfo.put("box_no", boxNo);
									paramMapBoxInfo.put("timestamp", 0);
									String resultStrBoxInfo = HttpUtil
											.post(Json.toJson(paramMapBoxInfo)
													.toString(), ERP_HOST
													+ BOX_INFO_API
													+ "?api_key=" + ERP_API_KEY);

									// String -> JsonNode
									JsonNode resultNodeBoxInfo = Json
											.parse(resultStrBoxInfo);

									// 有具体货箱数据进行入仓处理
									JsonNode boxProductsDatas = resultNodeBoxInfo
											.get("result");
									if (boxProductsDatas.size() != 0) {

										for (JsonNode boxInfo : boxProductsDatas) {
											JsonNode boxInfoDetails = boxInfo
													.get("product_shelf_batchBox_products");

											// 遍历这个货箱中的商品数据，获得对应SKU的到期日期
											for (JsonNode boxInfoDetail : boxInfoDetails) {
												if (sku.equals(boxInfoDetail
														.get("sku").asText())) {
													productionDate = boxInfoDetail
															.get("productionDate")
															.asText("2099-01-01");
												}
											}
										}
									}
									record.setIdentifier(in_record.get("_id")
											.asText());
									record.setExpirationDate(DateUtils
											.string2date(productionDate,
													DateUtils.FORMAT_DATE_PAGE));

								} catch (ParseException e) {
									e.printStackTrace();
									Logger.warn(
											"[inventoryEvent]========	出入库记录ID[{}]进口保税订单出库，但是日期格式化异常	========[inventoryEvent]",
											in_record.get("_id").asText());
								}
								productInventoryBatchDetailService
										.insertSelective(record);
								continue;
							}
							// 其他类型的出库都需要扣减
						}

						// 请求货箱数据
						Map<String, Object> paramMapBoxInfo = new HashMap<String, Object>();
						paramMapBoxInfo.put("box_no", boxNo);
						paramMapBoxInfo.put("timestamp", 0);
						String resultStrBoxInfo = HttpUtil.post(
								Json.toJson(paramMapBoxInfo).toString(),
								ERP_HOST + BOX_INFO_API + "?api_key="
										+ ERP_API_KEY);

						// String -> JsonNode
						JsonNode resultNodeBoxInfo = Json
								.parse(resultStrBoxInfo);

						// 有具体货箱数据进行入仓处理
						JsonNode boxProductsDatas = resultNodeBoxInfo
								.get("result");
						if (boxProductsDatas.size() != 0) {

							for (JsonNode boxInfo : boxProductsDatas) {
								JsonNode boxInfoDetails = boxInfo
										.get("product_shelf_batchBox_products");

								// 遍历这个货箱中的商品数据，获得对应SKU的到期日期
								for (JsonNode boxInfoDetail : boxInfoDetails) {
									if (sku.equals(boxInfoDetail.get("sku")
											.asText())) {
										productionDate = boxInfoDetail.get(
												"productionDate").asText(
												"2099-01-01");
									}
								}
							}

							// 入仓明细数据构造
							Map<String, String> inRecordParam = new HashMap<String, String>();

							inRecordParam.put("sku", in_record.get("sku")
									.asText());
							inRecordParam.put("productName",
									in_record.get("sku_name_cn").asText());

							// 出入仓数值变更
							if (isStockOut) {
								inRecordParam.put("containerStockChange", "-"
										+ in_record.get("change_count")
												.asText());
							} else {
								inRecordParam.put("containerStockChange",
										in_record.get("change_count").asText());
							}

							inRecordParam.put("remark", in_record.get("remark")
									.asText());
							inRecordParam.put("updateDate",
									in_record.get("updated_time").asText());
							inRecordParam.put("containerNumber",
									in_record.get("box_no").asText());
							inRecordParam.put("storageLocation",
									in_record.get("shelf_code").asText());
							inRecordParam.put("warehouseName",
									warehouse.getWarehouseName());
							inRecordParam.put("warehouseId",
									warehouse.getWarehouseId());
							inRecordParam.put("expirationDate", productionDate);
							inRecordParam.put("identifier", in_record
									.get("_id").asText());

							ProductInventoryBatchDetail productInventoryBatchDetail = Json
									.fromJson(Json.toJson(inRecordParam),
											ProductInventoryBatchDetail.class);

							Logger.info("新增详情参数[{}]",
									productInventoryBatchDetail.toString());

							Json.toJson(productCloudInventoryService.erpStockInDetail(Lists
									.newArrayList(productInventoryBatchDetail)));
						}

					}
				} else {
					// 当前仓库无入仓记录，跳过
					Logger.info(
							"[inventoryEvent]========	时间[{}]~[{}],仓库[{}]无入仓记录	========[inventoryEvent]",
							paramMap.get("updated_begin"),
							paramMap.get("updated_end"),
							warehouse.getWarehouseId());
					continue;
				}
			}
		}
	}

	/**
	 * 外部仓库(t_warehouse表中type=40)库存获取
	 * 
	 * @param event
	 */
	@Subscribe
	public void executeExternalWarehouseInventorySynchroization(
			ExternalWarehouseInventorySynchroizationEvent event) {
		if (SystemEventHandler.run_timed_task) {
			List<Map<String,Object>> dataUpdateForLog=Lists.newArrayList();
			//查询启用中、type=40的仓库
			//在总仓明细中获取当前福州1仓的商品数据
			List<Warehouse> validableWarehouses = warehInvenService
					.queryWarehouse(null);
			for(Warehouse warehouse: validableWarehouses){
				if(Integer.valueOf(warehouse.getType())!=40){
					continue;
				}
				Integer warehouseId=Integer.valueOf(warehouse.getWarehouseId());
				List<ProductInventoryDetail> externalInventoryDatas=  productCloudInventoryService.getExternalWarehouseInventoryDetail(warehouseId);
				if(externalInventoryDatas==null || externalInventoryDatas.size()<=0){
					continue;
				}
				for(int i=0;i<externalInventoryDatas.size();i++){
					ProductInventoryDetail productInventoryDetailParam = externalInventoryDatas.get(i);
					Map<String,Object> reqParam=Maps.newHashMap();
					reqParam.put("sku", productInventoryDetailParam.getSku());
					reqParam.put("stock_id", productInventoryDetailParam.getWarehouseId());
					reqParam.put("timestamp", 0);
					String resultStrBoxInfo = HttpUtil.post(Json.toJson(reqParam).toString(),
							ERP_HOST + EXTERNAL_WAREHOUSE_API + "?api_key="
									+ ERP_API_KEY);
					JsonNode resultData=null;
					try {
						JsonNode externalWarehouseDataNode = Json.parse(resultStrBoxInfo);
						resultData = externalWarehouseDataNode.get("result");
					} catch (Exception e) {
						Logger.info("外部仓---库存同步发生异常======时间[{}]", DateUtils.date2string(new Date(), DateUtils.FORMAT_FULL_DATETIME_MI));
						Logger.info("异常信息：{}",e);
					}
					if(resultData==null || resultData.size()<=0){
						continue;
					}
					JsonNode productInventoryData = resultData.get(0);
					if(!productInventoryData.has("available_stock_count") || null==productInventoryData.get("available_stock_count") 
							|| "".equals(productInventoryData.get("available_stock_count"))){
						continue;
					}
					int availableStockNum = productInventoryData.get("available_stock_count").asInt();
					
					if(warehouseId==2012){//杭州仓库存设置
						if(EXTERNAL_WAREHOUSE_STOCK!=-1){//配置文件中有值
							if(productInventoryDetailParam.getStock() !=EXTERNAL_WAREHOUSE_STOCK){//当前库存不等于配置文件中库存数量
								Map<String,Object> dataForLog=Maps.newHashMap();
								dataForLog.put("expirationDate", DateUtils.date2string(productInventoryDetailParam.getExpirationDate(), "yyyy-MM-dd"));
								dataForLog.put("sku", productInventoryDetailParam.getSku());
								dataForLog.put("warehouseId", warehouseId);
								dataForLog.put("oldstockNum", productInventoryDetailParam.getStock());
								
								productInventoryDetailParam.setStock(EXTERNAL_WAREHOUSE_STOCK);
								productInventoryDetailParam.setUpdateTime(new Date());
								int j= productCloudInventoryService.updateExternalWearhouseProductInventory(productInventoryDetailParam);
								dataForLog.put("nowStockNum", EXTERNAL_WAREHOUSE_STOCK);
								dataUpdateForLog.add(dataForLog);
							}
						}else{//配置文件中没有值设置为从erp获取到的值
							Map<String,Object> dataForLog=Maps.newHashMap();
							dataForLog.put("expirationDate", DateUtils.date2string(productInventoryDetailParam.getExpirationDate(), "yyyy-MM-dd"));
							dataForLog.put("sku", productInventoryDetailParam.getSku());
							dataForLog.put("warehouseId", warehouseId);
							dataForLog.put("oldstockNum", productInventoryDetailParam.getStock());
							
							productInventoryDetailParam.setStock(availableStockNum);
							productInventoryDetailParam.setUpdateTime(new Date());
							int j= productCloudInventoryService.updateExternalWearhouseProductInventory(productInventoryDetailParam);
							dataForLog.put("nowStockNum", availableStockNum);
							dataUpdateForLog.add(dataForLog);
						}
						continue;
					}
					
					if(availableStockNum!=productInventoryDetailParam.getStock()){
						Map<String,Object> dataForLog=Maps.newHashMap();
						dataForLog.put("expirationDate", DateUtils.date2string(productInventoryDetailParam.getExpirationDate(), "yyyy-MM-dd"));
						dataForLog.put("sku", productInventoryDetailParam.getSku());
						dataForLog.put("warehouseId", warehouseId);
						dataForLog.put("oldstockNum", productInventoryDetailParam.getStock());
						
						productInventoryDetailParam.setStock(availableStockNum);
						int j= productCloudInventoryService.updateExternalWearhouseProductInventory(productInventoryDetailParam);
						dataForLog.put("nowStockNum", availableStockNum);
						dataUpdateForLog.add(dataForLog);
					}
				}
			}
			if (dataUpdateForLog.size() > 0) {
				Logger.info(
						"[inventoryEvent]========	外部仓库库存变更前后信息[{}]	========[inventoryEvent]",
						dataUpdateForLog.toString());
			}
		}
	}

	@Subscribe
	public void realeaseProductCloudInventoryLockJob(
			ProductReleaseCloudLockEvent event) {
		if (SystemEventHandler.run_timed_task) {
			// 释放云仓锁定
			ProductInventoryOrderLock productInventoryOrderLockParam = new ProductInventoryOrderLock();
			productInventoryOrderLockParam.setIsEffective((short) 1);// 获取临时锁
			List<ProductInventoryOrderLock> productInventoryOrderLockList = productInventoryOrderLockMapper
					.selectInventoryLockListByParams(productInventoryOrderLockParam);

			if (productInventoryOrderLockList != null
					&& productInventoryOrderLockList.size() > 0) {
				for (ProductInventoryOrderLock productInventoryOrderLock : productInventoryOrderLockList) {
					Configuration config = Play.application().configuration()
							.getConfig("cloudLockInventory");
					String cloudLockInventoryEffectiveMinute = config
							.getString("effectiveMinute");
					DateTime dateTimeNow = new DateTime(new Date());
					int differMinutes = Minutes.minutesBetween(
							new DateTime(
									productInventoryOrderLock.getCreateTime()),
							dateTimeNow).getMinutes();// 相差分钟
					if (differMinutes > Integer
							.valueOf(cloudLockInventoryEffectiveMinute)) {
						Logger.info(
								"[inventoryEvent]========	单号[{}]，sku[{}],于[{}]释放锁定的云仓	========[inventoryEvent]",
								productInventoryOrderLock.getOrderNo(),
								productInventoryOrderLock.getSku(), DateUtils
										.date2string(new Date(),
												DateUtils.FORMAT_FULL_DATETIME));
						productInventoryOrderLock.setIsEffective((short) 0);
						productInventoryOrderLock.setUpdateTime(new Date());
						productInventoryOrderLockMapper
								.updateByPrimaryKeySelective(productInventoryOrderLock);
					}
				}
			}
		}
	}
}
