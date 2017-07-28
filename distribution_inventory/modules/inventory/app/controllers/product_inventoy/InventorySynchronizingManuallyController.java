package controllers.product_inventoy;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import controllers.annotation.GetRequestIp;
import dto.inventory.ProductCloudInventoryResult;
import dto.product_inventory.ErpStockChangeQueryDto;
import entity.product_inventory.InventorySyncRecord;
import entity.product_inventory.ProductInventoryBatchDetail;
import entity.warehousing.Warehouse;
import services.inventory.IWarehInvenService;
import services.product_inventory.IProductCloudInventoryService;
import services.product_inventory.IProductInventoryBatchDetailService;
import services.product_inventory.IinventorySyncRecordService;
import utils.inventory.DateUtils;
import utils.inventory.HttpUtil;
import vo.inventory.Page;

@GetRequestIp
public class InventorySynchronizingManuallyController extends Controller{

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
	 * 根据指定的时间范围获取ERP的出入仓记录
	 * 
	 * @param event
	 */
	public Result executeInventorySynchronizationManually() {
			Map<String,Object> result=Maps.newHashMap();
			//返回页面的结果
			List<ProductInventoryBatchDetail> dataModel=Lists.newArrayList();
			String updated_begin = request().getQueryString("updated_begin");
			String updated_end = request().getQueryString("updated_end");
		
			// 获取每个可用进口仓库的库位入库记录
			List<Warehouse> validableWarehouses = warehInvenService
					.queryWarehouse(null,true,null);

			// 组织时间参数
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("page_index", 1);
			paramMap.put("page_size", 10000);
			paramMap.put("timestamp", 0);

			// 时间
			paramMap.put("updated_begin", updated_begin);
			paramMap.put("updated_end", updated_end);
			
			Logger.info("============获取时间段位于：[{}]~[{}]出入仓记录============",paramMap.get("updated_begin"),paramMap.get("updated_end"));

			for (Warehouse warehouse : validableWarehouses) {
				paramMap.put("stock_id", warehouse.getWarehouseId());
				String resultStrRecordIn = HttpUtil.post(Json.toJson(paramMap)
						.toString(), ERP_HOST + STOCK_IN_API + "?api_key="
						+ ERP_API_KEY);

				// String -> JsonNode
				JsonNode resultNode = Json.parse(resultStrRecordIn);

				// 该时间段如有出入仓记录
				JsonNode inRecordDatas = resultNode.get("result");
				if (inRecordDatas.size() != 0) {
					
					Logger.info("======时间[{}]~[{}],仓库[{}]有出入仓记录======",paramMap.get("updated_begin"),paramMap.get("updated_end"),warehouse.getWarehouseId());

					
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
						
						//筛选该入库记录，是否需要接收
						String stockType = in_record.get("stock_type").asText();
						
						//标识为true，意味着该记录是出仓记录
						boolean isStockOut = false;
						
						//标识为true，意为着该记录只是同步到BBC，不仅行库存变更
						boolean isJustRecord = false;
						
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
							//以下两种出库记录只是单纯记录，不进行库存变更
							
							//订单自动出库
							if(70 == in_record.get("record_type").asInt()){
								Logger.info("============出入库记录ID[{}]为自动出库，忽略，但是记录该记录，不进行库存变化============",in_record.get("_id").asText());
								isJustRecord = true;
							}
							
							//进口保税订单出库
							if(80 == in_record.get("record_type").asInt()){
								Logger.info("============出入库记录ID[{}]进口保税订单出库，忽略，但是记录该记录，不进行库存变化============",in_record.get("_id").asText());
								isJustRecord = true;
							}
							
							//其他类型的出库都需要扣减
						}
						
						
						//校验当前入仓的SKU是否已经初始化，如果没有初始化，那么跳过。直到SKU初始化完成后，才执行云仓入库操作
						InventorySyncRecord isr = inventorySyncRecordService.selectBySkuAndWarehouseId(sku,Integer.valueOf(warehouse.getWarehouseId()));
						
						//同步记录不存在，需要初始化记录
						if(isr == null || (isr != null && isr.getIsActive() == 0)){
							Logger.info("============[{}]未初始化库存，不进行入库记录的保存============", sku);
							continue;
						}
						
						//比较初始化时间和erp出入库时间
						Date lastSyncingTime = isr.getLastSyncingTime();
						Date erpInventoryChangeDate=null;
						try {
							erpInventoryChangeDate = DateUtils.string2date(in_record.get("updated_time").asText(), DateUtils.FORMAT_FULL_DATETIME_MI);
						} catch (ParseException error) {
							Logger.info("手动同步erp库存变更记录,获取erp库存变更时间发生异常{}", error);
							continue;
						}
						if(erpInventoryChangeDate==null){
							continue;
						}
						if(erpInventoryChangeDate.getTime()<lastSyncingTime.getTime()){
							Logger.info("============[{}]库存变更时间小于初始化时间，不进行入库记录的保存============", sku);
							continue;
						}
						
						//记录去重复
						ProductInventoryBatchDetail detail = productInventoryBatchDetailService.getBatchDetailByIdentifier(in_record.get("_id").asText());
						//根据唯一标识符判断记录唯一性，忽略重复记录
						if(detail != null){
							Logger.info("============SKU为[{}]，标识符为[{}]的入仓记录已经添加过，该记录将被系统忽略============", sku,in_record.get("_id").asText());
							continue;
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
							inRecordParam.put("expirationDate", productionDate);
							inRecordParam.put("identifier", in_record.get("_id").asText());

							ProductInventoryBatchDetail productInventoryBatchDetail = null;
							try {
								productInventoryBatchDetail = Json
										.fromJson(Json.toJson(inRecordParam),
												ProductInventoryBatchDetail.class);
							} catch (Exception e) {
								Logger.info(e.getMessage());
							}

							//是否是单纯记录
							if(isJustRecord){
								
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
									record.setExpirationDate(DateUtils.string2date(productionDate, DateUtils.FORMAT_DATE_PAGE) );
									record.setIdentifier(in_record.get("_id").asText());
								} catch (ParseException e) {
									e.printStackTrace();
									Logger.warn("============出入库记录ID[{}]进口保税订单出库，但是日期格式化异常，记录保存失败============",in_record.get("_id").asText());
								}
								productInventoryBatchDetailService.insertSelective(record);
								dataModel.add(record);
								//单纯记录将不再继续后面的保存逻辑
								continue;
								
							}
							Logger.info("库存发生改变的记录信息：{}",productInventoryBatchDetail.toString());
							dataModel.add(productInventoryBatchDetail);
							Json.toJson(productCloudInventoryService.erpStockInDetail(Lists
									.newArrayList(productInventoryBatchDetail)));
						}

					}
				} else {
					// 当前仓库无入仓记录，跳过
					Logger.info("======时间[{}]~[{}],仓库[{}]无入仓记录======",paramMap.get("updated_begin"),paramMap.get("updated_end"),warehouse.getWarehouseId());
					continue;
				}
			}
			result.put("result", 0);
			result.put("syncinfo", dataModel);
			result.put("msg", "erp库存变更同步完成！");
			return ok(Json.toJson(result));
		}
	/**
	 * 获取指定sku在指定时间段内的erp出入库同步记录
	 * @return
	 */
	public Result checkInventoySyncRecord(){
		Map<String,Object> result =Maps.newHashMap();
		Map<String, String[]> map = request().body().asFormUrlEncoded();
		ErpStockChangeQueryDto queryParam=new ErpStockChangeQueryDto();
		try {
			
			
			if(!StringUtils.isEmpty(map.get("updated_begin")[0])){
				queryParam.setBegin(DateUtils.string2date(map.get("updated_begin")[0], DateUtils.FORMAT_DATE_PAGE));
			}
			if(!StringUtils.isEmpty(map.get("updated_end")[0])){
				queryParam.setEnd(DateUtils.string2date(map.get("updated_end")[0], DateUtils.FORMAT_DATE_PAGE));
			}
			
			if (!StringUtils.isEmpty(map.get("sku")[0])) {
				queryParam.setSku(map.get("sku")[0]);
			}
			//分页参数
			queryParam.setRows(Integer.valueOf(map.get("rows")[0]));
			queryParam.setPage(Integer.valueOf(map.get("page")[0]));
		} catch (Exception e) {
			Logger.error(">>>>>>>>>>>>>>>>>>>>>microList error:{}", e);
			result.put("result", false);
			result.put("msg", "获取查询参数发生异常!");
			return ok(Json.toJson(result));
		}
		
		List<ProductInventoryBatchDetail> syncRecordList= productInventoryBatchDetailService.getSyncRecordByParam(queryParam);
		int totalCount=productInventoryBatchDetailService.getSyncRecordTotalCountByParam(queryParam);
		Page<ProductInventoryBatchDetail> page=new Page<>(queryParam.getPage(),queryParam.getRows(),totalCount,syncRecordList);
		return ok(Json.toJson(page));
	}
	
	/**
	 * 修改海豚仓(2012杭州仓库存数据)
	 * @return
	 */
	public Result changeStock(){
		JsonNode reqParam = request().body().asJson();
		if(!reqParam.has("type")){
			return ok("参数有误！");
		}
		Logger.info("changeStock---->reqParam", reqParam);
		String param = reqParam.toString();
		ProductCloudInventoryResult result= productCloudInventoryService.setCloudStockByWarehouseId(param);
		return ok(Json.toJson(result));
	}
}