package handlers.inventory;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dto.product_inventory.ErpStockChangeQueryDto;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import entity.product_inventory.InventorySyncRecord;
import entity.product_inventory.ProductInventoryBatchDetail;
import entity.warehousing.Warehouse;
import events.product_inventory.InventorySynchronizationEvent;
import services.inventory.IWarehInvenService;
import services.product_inventory.IProductCloudInventoryService;
import services.product_inventory.IProductInventoryBatchDetailService;
import services.product_inventory.IinventorySyncRecordService;
import utils.inventory.DateUtils;
import utils.inventory.HttpUtil;

public class InventorySynchronizingHandler {

	@Inject
	private IProductCloudInventoryService productCloudInventoryService;

	@Inject
	private IWarehInvenService warehInvenService;
	
	@Inject
	private IinventorySyncRecordService inventorySyncRecordService;
	
	@Inject
	private IProductInventoryBatchDetailService productInventoryBatchDetailService;
	
	private static String ERP_API_KEY = "";
	private static String ERP_HOST = "";
	private static String STOCK_IN_API = "";
	private static String BOX_INFO_API = "";
	private static String EXTERNAL_WAREHOUSE_API="";
	private static String SYNC_EXCEPTION_BEGINTIME="";
	//ERP出入库记录类型
	private static String PRODUCT_INSTOCK = "";//入仓
	private static String PRODUCT_CHANGE_BOX = "";//换箱
	private static String PRODUCT_CHECK_COUNT = "";//盘点
	private static String PRODUCT_OUTSTOCK = "";//出仓
	
	
	static {
		if (ERP_API_KEY.equals("")) {
			Configuration config = Play.application().configuration()
					.getConfig("erp");
			ERP_API_KEY = config.getString("apiKey");
		}
		if(EXTERNAL_WAREHOUSE_API.equals("")){
			Configuration config = Play.application().configuration().getConfig("erp");
			EXTERNAL_WAREHOUSE_API = config.getString("externalWarehouseApi");
		}
		if (ERP_HOST.equals("")) {
			Configuration config = Play.application().configuration()
					.getConfig("erp");
			ERP_HOST = config.getString("host");
		}
		if (STOCK_IN_API.equals("")) {
			Configuration config = Play.application().configuration()
					.getConfig("erp");
			STOCK_IN_API = config.getString("stockInApi");
			BOX_INFO_API = config.getString("boxInfo");
			PRODUCT_INSTOCK = config.getString("productInstock");//暂未使用该常量，只是维护起来，方便了解类型
			PRODUCT_CHANGE_BOX = config.getString("productChangeBox");//暂未使用该常量，只是维护起来，方便了解类型
			PRODUCT_CHECK_COUNT = config.getString("productCheckCount");
			PRODUCT_OUTSTOCK = config.getString("productOutstock");
		}

	}

	/**
	 * 同步ERP入仓记录
	 * 
	 * @param event
	 * @throws ParseException 
	 */
	@Subscribe
	public void executeInventorySynchronization(InventorySynchronizationEvent event) throws ParseException {
		
		// 获取每个可用进口仓库的库位入库记录
		List<Warehouse> validableWarehouses = warehInvenService
				.queryWarehouse(null,true,null);

		// 组织时间参数
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("page_index", 1);
		paramMap.put("page_size", 10000);
		paramMap.put("timestamp", 0);

		// 时间参数范围，当前时间点，往前推6分钟，然后取2分钟的时间区间
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -10);
		String beginTime = DateUtils.date2string(calendar.getTime(), DateUtils.FORMAT_FULL_DATETIME);
		if("".equals(SYNC_EXCEPTION_BEGINTIME)){
			paramMap.put("updated_begin", beginTime);
		}else{
			paramMap.put("updated_begin", SYNC_EXCEPTION_BEGINTIME);
		}
		calendar.add(Calendar.MINUTE,2);
		paramMap.put("updated_end", DateUtils.date2string(calendar.getTime(), DateUtils.FORMAT_FULL_DATETIME));

		Logger.info("============获取时间段位于：[{}]~[{}]出入仓记录============",paramMap.get("updated_begin"),paramMap.get("updated_end"));
		
		boolean flag=false;//标志该时间段是否有仓库的库存发生变更
		List<String> warehouseLists=Lists.newArrayList();//日志内容优化，将库存没有变动的仓库统一打印日志
		for (Warehouse warehouse : validableWarehouses) {
			JsonNode inRecordDatas=null;
			try {
				paramMap.put("stock_id", warehouse.getWarehouseId());
				String resultStrRecordIn = HttpUtil.post(Json.toJson(paramMap)
						.toString(), ERP_HOST + STOCK_IN_API + "?api_key="
						+ ERP_API_KEY);

				JsonNode resultNode = Json.parse(resultStrRecordIn);
				
				// 该时间段如有入仓记录
				inRecordDatas = resultNode.get("result");
				if(!"".equals(SYNC_EXCEPTION_BEGINTIME)){
					SYNC_EXCEPTION_BEGINTIME="";
					Logger.info("=========时间[{}]系统恢复，累计时间清除===", beginTime);
				}
			} catch (Exception e) {
				Logger.info("异常信息：{}", e);
				if("".equals(SYNC_EXCEPTION_BEGINTIME)){
					SYNC_EXCEPTION_BEGINTIME=beginTime;
					Logger.info("========时间[{}]发生异常，进行同步时间累计===", beginTime);
				}
				break;
			}
			if (inRecordDatas!=null && inRecordDatas.size() != 0) {
				flag=true;//库存发生过变更
				
				Logger.info("======时间[{}]~[{}],仓库[{}]有出入仓记录======出入仓信息数量[{}]",paramMap.get("updated_begin"),paramMap.get("updated_end"),warehouse.getWarehouseId(),inRecordDatas.size());

				// 箱号
				String boxNo = "";
				// SKU
				String sku = "";
				// 到期日期字符串，yyyy-dd-MM
				String productionDate = "";

				// 遍历入仓记录，获取箱号入仓日期
				List<String> skuNotInitList=Lists.newArrayList();
				for (JsonNode in_record : inRecordDatas) {
					
					sku = in_record.get("sku").asText();
					boxNo = in_record.get("box_no").asText();
					
					//校验当前入仓的SKU是否已经初始化，如果没有初始化，那么跳过。直到SKU初始化完成后，才执行云仓入库操作
					InventorySyncRecord isr = inventorySyncRecordService.selectBySkuAndWarehouseId(sku,Integer.valueOf(warehouse.getWarehouseId()));
					
					//同步记录不存在，需要初始化记录
					if(isr == null || (isr != null && isr.getIsActive() == 0)){
						skuNotInitList.add(sku);
						continue;
					}
					
					//记录去重复
					ErpStockChangeQueryDto dto = new ErpStockChangeQueryDto();
					dto.setIdentifier(in_record.get("_id").asText());
					int count = productInventoryBatchDetailService.getSyncRecordTotalCountByParam(dto);
					//根据唯一标识符判断记录唯一性，忽略重复记录
					if(count > 0){
						Logger.info("============SKU为[{}]，标识符为[{}]的入仓记录已经添加过，该记录将被系统忽略============", sku,in_record.get("_id").asText());
						continue;
					}
					
					//如果当前出入库记录的时间在该SKU库存初始化之前，那么也要跳过，只同步初始化时间点之后的出入仓记录
					if(isr.getLastSyncingTime().getTime() 
							> 
						DateUtils.string2date(in_record.get("updated_time").asText(), DateUtils.FORMAT_FULL_DATETIME_MI).getTime()){
						continue;
					}
					
					//筛选该入库记录，是否需要变动库存
					String stockType = in_record.get("stock_type").asText();
					
					boolean isStockOut = false;
					
					//“盘点”类型的入仓记录，需要手动校验是出仓还是入仓
					if(PRODUCT_CHECK_COUNT.equals(stockType)){
						//确认一下是盘亏还是盘盈
						int box_before_change_count = in_record.get("box_before_change_count").asInt();
						int box_after_change_count  = in_record.get("box_after_change_count").asInt();
						
						//盘亏，意味着出库
						if(box_before_change_count - box_after_change_count > 0){
							isStockOut = true;
						}
						
					}
					
					//出库
					if(PRODUCT_OUTSTOCK.equals(stockType)){
						isStockOut = true;
						//忽略两种情况下的出库
						
						//订单自动出库
						if(70 == in_record.get("record_type").asInt()){
							Logger.info("============出入库记录ID[{}]为自动出库，忽略，但是记录该记录，不进行库存变化============",in_record.get("_id").asText());
							ProductInventoryBatchDetail record = new ProductInventoryBatchDetail();
							try {
								record.setSku(in_record.get("sku").asText());
								record.setProductName(in_record.get("sku_name_cn").asText());
								record.setContainerStockChange(-Integer.parseInt(in_record.get("change_count").asText()));
								record.setRemark(in_record.get("remark").asText());
								record.setUpdateDate(DateUtils.string2date(in_record.get("updated_time").asText(), DateUtils.FORMAT_FULL_DATETIME_MI));
								record.setContainerNumber(in_record.get("box_no").asText());
								record.setStorageLocation(in_record.get("shelf_code").asText());
								record.setWarehouseName(warehouse.getWarehouseName());
								record.setWarehouseId(Integer.valueOf(warehouse.getWarehouseId()));
								record.setCreateTime(new Date());
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
														"productionDate").asText("2116-02-07");
											}
										}
									}
								}
								
								record.setIdentifier(in_record.get("_id").asText());
								record.setExpirationDate(DateUtils.string2date(productionDate, DateUtils.FORMAT_DATE_PAGE) );
							} catch (ParseException e) {
								e.printStackTrace();
								Logger.warn("============出入库记录ID[{}]为自动出库，但是日期格式化异常，记录保存失败============",in_record.get("_id").asText());
							}
							productInventoryBatchDetailService.insertSelective(record);
							continue;
						}
						
						//进口保税订单出库
						if(80 == in_record.get("record_type").asInt()){
							Logger.info("============出入库记录ID[{}]进口保税订单出库，忽略，但是记录该记录，不进行库存变化============",in_record.get("_id").asText());
							
							ProductInventoryBatchDetail record = new ProductInventoryBatchDetail();
							try {
								
								record.setSku(in_record.get("sku").asText());
								record.setProductName(in_record.get("sku_name_cn").asText());
								record.setContainerStockChange(-Integer.valueOf(in_record.get("change_count").asText()));
								record.setRemark(in_record.get("remark").asText());
								record.setUpdateDate(DateUtils.string2date(in_record.get("updated_time").asText(), DateUtils.FORMAT_FULL_DATETIME_MI));
								record.setContainerNumber(in_record.get("box_no").asText());
								record.setStorageLocation(in_record.get("shelf_code").asText());
								record.setWarehouseName(warehouse.getWarehouseName());
								record.setWarehouseId(Integer.valueOf(warehouse.getWarehouseId()));
								
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
														"productionDate").asText("2116-02-07");
											}
										}
									}
								}
								
								record.setIdentifier(in_record.get("_id").asText());
								record.setExpirationDate(DateUtils.string2date(productionDate, DateUtils.FORMAT_DATE_PAGE) );
							} catch (ParseException e) {
								e.printStackTrace();
								Logger.warn("============出入库记录ID[{}]进口保税订单出库，但是日期格式化异常，记录保存失败============",in_record.get("_id").asText());
							}
							productInventoryBatchDetailService.insertSelective(record);
							continue;
						}
						//其他类型的出库都需要扣减
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
											"productionDate").asText("2116-02-07");
								}
							}
						}

						// 入仓明细数据构造
						Map<String, String> inRecordParam = new HashMap<String, String>();

						inRecordParam.put("sku", in_record.get("sku")
								.asText());
						inRecordParam.put("productName",
								in_record.get("sku_name_cn").asText());
						
						//出入仓数值变更
						if(isStockOut){
							inRecordParam.put("containerStockChange", "-" + in_record
									.get("change_count").asText());
						}else{
							inRecordParam.put("containerStockChange", in_record
									.get("change_count").asText());
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
						inRecordParam.put("expirationDate", productionDate==null?"2116-02-07":productionDate);
						inRecordParam.put("identifier", in_record.get("_id").asText());

						ProductInventoryBatchDetail productInventoryBatchDetail = Json
								.fromJson(Json.toJson(inRecordParam),
										ProductInventoryBatchDetail.class);

						Logger.info("新增详情参数[{}]",productInventoryBatchDetail.toString());
						
						try {
							Json.toJson(productCloudInventoryService.erpStockInDetail(Lists
									.newArrayList(productInventoryBatchDetail)));
						} catch (Exception e) {
							Logger.info("库存同步发生异常,参数{},异常信息{}", productInventoryBatchDetail.toString(),e);
							continue;
						}
					}

				}
				if(!skuNotInitList.isEmpty()){
					Logger.info("库存同步定时任务：[{}]未初始化库存，不进行入库记录的保存============", skuNotInitList.toString());
				}
			} else {
				warehouseLists.add(warehouse.getWarehouseId());
			}
		}
		if(!flag){//所有仓库库存都未发生变动，统一打印日期
			Logger.info("======时间[{}]~[{}],所有仓库无出入仓记录======",paramMap.get("updated_begin"),paramMap.get("updated_end"));
		}else{//有仓库库存发生了变更，其他仓库使用该日志
			Logger.info("库存同步定时任务：时间[{}]~[{}],仓库[{}]无库存变更", paramMap.get("updated_begin"),paramMap.get("updated_end"),warehouseLists.toString());
		}
	}
}
