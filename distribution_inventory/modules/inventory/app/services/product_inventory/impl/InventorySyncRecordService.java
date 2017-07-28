package services.product_inventory.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.product_inventory.InitProductInventoryResult;
import dto.product_inventory.InventoryCommonResult;
import dto.product_inventory.ProductMicroInventoyResult;
import entity.product_inventory.InventorySyncRecord;
import entity.product_inventory.OrderDetail;
import entity.product_inventory.OrderMicroInventoryDeductRecord;
import entity.product_inventory.ProductInventoryDetail;
import entity.product_inventory.ProductInventoryOrderLock;
import entity.product_inventory.ProductInventoryTotal;
import entity.product_inventory.ProductMicroInventoryInRecord;
import mapper.product_inventory.InventorySyncRecordMapper;
import mapper.product_inventory.OrderDetailMapper;
import mapper.product_inventory.OrderMicroInventoryDeductRecordMapper;
import mapper.product_inventory.ProductInventoryDetailMapper;
import mapper.product_inventory.ProductInventoryOrderLockMapper;
import mapper.product_inventory.ProductInventoryTotalMapper;
import mapper.product_inventory.ProductMicroInventoryInRecordMapper;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import services.product_inventory.IinventorySyncRecordService;
import utils.inventory.DateUtils;
import utils.inventory.ErpApiUtil;
import utils.inventory.HttpUtil;

public class InventorySyncRecordService implements IinventorySyncRecordService {
	@Inject
	private InventorySyncRecordMapper inventorySyncRecordMapper;
	
	@Inject
	private ProductMicroInventoryInRecordMapper microInventoryInRecordMapper;
	
	@Inject
	private OrderDetailMapper orderDetailMapper;
	
	@Inject
	private ProductInventoryDetailMapper cloudInventoryDetailMapper;
	
	@Inject
	private ProductInventoryTotalMapper cloudInventoryTotalMapper;
	
	@Inject 
	private OrderMicroInventoryDeductRecordMapper microDeductRecordMapper;
	
	@Inject
	private ProductInventoryOrderLockMapper inventoryOrderLack;
	
	@Inject
	private InventorySyncRecordMapper syncRecordMapper;
	
	@Override
	public InventorySyncRecord selectBySkuAndWarehouseId(String sku,Integer warehouseId) {
		return inventorySyncRecordMapper.selectBySkuAndWarehouseId(sku,warehouseId);
	}

	@Override
	public int insertSelective(InventorySyncRecord record) {
		return inventorySyncRecordMapper.insertSelective(record);
	}

	@Override
	public int updateSelective(InventorySyncRecord record) {
		return inventorySyncRecordMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public List<InventorySyncRecord> selectBySku(String sku) {
		
		return inventorySyncRecordMapper.selectBySku(sku);
	}
	
	@Override
	public List<OrderMicroInventoryDeductRecord> getMicroDeductRecordByOrderNoAndSku(List<String> orderNoLists, String sku,Date expirationDate) {
		
		return microDeductRecordMapper.selectByOrderNoAndSku(orderNoLists,sku,expirationDate);
	}

	@Override
	public ProductInventoryDetail getInventoryDetailByParam(ProductInventoryDetail paramDetail) {
		return cloudInventoryDetailMapper.selectByParam(paramDetail);
	}
	

	@Override
	public int getMicroStockpileTotal(String sku, Integer warehouseId) {
		return microInventoryInRecordMapper.getTotalMicroInventoryStockpile(warehouseId, sku);
	}

	@Override
	public int getMicroStockpileTotalWithExpirdationDate(ProductInventoryDetail paramDetail) {
		return microInventoryInRecordMapper.getTotalMicroInventoryStockpileByExpiration(paramDetail);
	}
	
	@Override
	public List<ProductInventoryTotal> getCloudInventoryByWarehouseId(Integer warehouseId) {
		
		return cloudInventoryTotalMapper.selectByWarehouseId(warehouseId);
	}
	
	/**
	 * 根据sku，仓库id获取微仓各个到期日期的囤货数量，然后和erp库存信息进行比较
	 */
	@Override
	public InitProductInventoryResult checkMicroInventoryStockpile(String s_skuStock,Integer warehouseId) {
		JsonNode skuStock=Json.parse(s_skuStock);
		JsonNode stockByExpirationDates = skuStock.get("expiration");
		Map<String,Integer> microExpirationWithNum=Maps.newHashMap();
		Map<String,Integer> erpExpirationWithNum=Maps.newHashMap();
		ProductMicroInventoryInRecord inRecord=new ProductMicroInventoryInRecord();
		inRecord.setSku(skuStock.get("sku").asText());
		inRecord.setWarehouseId(warehouseId);
		List<ProductMicroInventoryInRecord> inRecordLists= microInventoryInRecordMapper.getMicroInventoryStockpile(inRecord);
		if(inRecordLists.isEmpty()){
			return new InitProductInventoryResult(true,null,microExpirationWithNum);
		}
		Logger.info("初始化商品库存---->erp库存信息{}，微仓囤货:{}",s_skuStock,inRecordLists.toString());
		
		//erp信息补全(erp数据可能有数据的到期日期格式异常)
		for (JsonNode stockByDate : stockByExpirationDates) {
			int erpStock = stockByDate.get("leftCount").asInt();
			 //到期日期校验
            if(StringUtils.isNotBlank(stockByDate.get("productionDate").asText())){
        		try {
        			inRecord.setExpirationDate(DateUtils.string2date(stockByDate.get("productionDate").asText(), DateUtils.FORMAT_DATE_PAGE));
            	} catch (ParseException e) {
            		inRecord.setExpirationDate(ErpApiUtil.EXPIRATION_DATE);
				}
            }else{
            	inRecord.setExpirationDate(ErpApiUtil.EXPIRATION_DATE);
            }
            erpExpirationWithNum.put(DateUtils.date2string(inRecord.getExpirationDate(), "yyyy-MM-dd"), erpStock);
		}
		
		//库存比较
		for(ProductMicroInventoryInRecord tempInRecords: inRecordLists){
			String expirationKey=DateUtils.date2string(tempInRecords.getExpirationDate(), "yyyy-MM-dd");
			int microStockpile = tempInRecords.getQty().intValue();
			if(microStockpile<=0){
				continue;
			}
			if(microStockpile>0 && erpExpirationWithNum.get(expirationKey)==null){
				return new InitProductInventoryResult(false,"仓库["+inRecord.getWarehouseId()+"]中到期日期为"+expirationKey+"的商品["+inRecord.getSku()+"]在微仓中的囤货数量大于erp现有库存数量");
			}
			int erpStock = erpExpirationWithNum.get(expirationKey).intValue();
			
			if(erpStock<microStockpile){
				return new InitProductInventoryResult(false,"仓库["+inRecord.getWarehouseId()+"]中到期日期为"+DateUtils.date2string(inRecord.getExpirationDate(), DateUtils.FORMAT_DATE_PAGE)+"的商品["+inRecord.getSku()+"]在微仓中的囤货数量大于erp现有库存数量");
			}else{
				microExpirationWithNum.put(expirationKey, microStockpile);
			}
		}
		return new InitProductInventoryResult(true,null,microExpirationWithNum);
	}
	@Override
	public InitProductInventoryResult checkOrderExistHKBySku(String sku, int warehouseId) {
		try {
			Configuration config = Play.application().configuration().getConfig("b2c");
			String baseurl = config.getString("b2cBaseUrl");
			String url=baseurl+"/checkout/queryExistOrder";
			List<OrderDetail> deductInventoryOrderDetail=  orderDetailMapper.selectOrderBySkuAndStatus(sku,warehouseId);
			if(deductInventoryOrderDetail.isEmpty()){
				return new InitProductInventoryResult(true, null);
			}
			List<String> orderNoList=Lists.newArrayList();
			for(OrderDetail tempOrderDetail:deductInventoryOrderDetail){
				orderNoList.add(tempOrderDetail.getOrderNo());
			}
			String existOrderNoStr = HttpUtil.post(Json.toJson(orderNoList).toString(), url);
			JsonNode existOrderNoNode = Json.parse(existOrderNoStr);
			if(existOrderNoNode==null){
				return new InitProductInventoryResult(false, "该商品存在处于流转状态中的订单！");
			}
			for(JsonNode tempNode:existOrderNoNode){
				String existHkOrderNo = tempNode.asText();
				orderNoList=orderNoList.stream()
				.filter(e->!e.equals(existHkOrderNo))
				.collect(Collectors.toList());
			}
			if(!orderNoList.isEmpty()){
				return new InitProductInventoryResult(false, "该商品存在处于流转状态中的订单！");
			}
			return new InitProductInventoryResult(true, null);
		} catch (Exception e) {
			Logger.info("云仓库存初始化校验包含初始化商品的订单流转状态发生异常{}", e);
			return new InitProductInventoryResult(false, "云仓库存初始化校验包含初始化商品的订单流转状态发生异常！");
		}
	}

	@Override
	public String cloudInventoryCheck() {
		Map<String,Object> result=Maps.newHashMap();
		//云仓库存小于0的
		List<ProductInventoryDetail> cloudInventoryStockMinus=cloudInventoryDetailMapper.selectCloudInventoryStockMinus();
		//云仓总仓和云仓明细数据不一致的
		List<Map> cloudInventoryTotalUnequalDetail=cloudInventoryDetailMapper.selectCloudInventoryTotalUnequalDetail();
		result.put("cloudStockMinus", cloudInventoryStockMinus);
		result.put("cloudTotalUnequalDetail", cloudInventoryTotalUnequalDetail);
		return Json.toJson(result).toString();
	}

	@Override
	public ProductMicroInventoyResult getOrderOccupyNum(ProductInventoryDetail paramDetail) {
		String sku=paramDetail.getSku();
		Integer warehouseId = paramDetail.getWarehouseId();
    	try {
			Configuration config = Play.application().configuration().getConfig("b2c");
			String baseurl = config.getString("b2cBaseUrl");
			String url=baseurl+"/checkout/queryExistOrder";
			List<OrderMicroInventoryDeductRecord> microDeductList= microDeductRecordMapper.selectRecordBySkuAndExpirationDate(paramDetail);
			if(microDeductList.isEmpty()){
				return new ProductMicroInventoyResult(true,null,0,null);
			}
			List<String> orderNoList=Lists.newArrayList();
			int totailNum=0;
			for(OrderMicroInventoryDeductRecord tmepDeductRecord: microDeductList){
				totailNum+=tmepDeductRecord.getQty();
				orderNoList.add(tmepDeductRecord.getOrderNo());
			}
			String orderNoExistHkStr = HttpUtil.post(Json.toJson(orderNoList).toString(), url);
			JsonNode orderNoExistHkNode = Json.parse(orderNoExistHkStr);
			if(orderNoExistHkNode==null || orderNoExistHkNode.size()<=0){
				return new ProductMicroInventoyResult(true,null,totailNum,orderNoList);
			}
			for(JsonNode tempOrderNoNode: orderNoExistHkNode){
				String orderNoHKExist=tempOrderNoNode.asText();
				microDeductList= microDeductList.stream()
				.filter(e->!e.getOrderNo().equals(orderNoHKExist))
				.collect(Collectors.toList());
			}
			int orderExistFilterTotalNum=0;
			List<String> orderLists=Lists.newArrayList();
			for(OrderMicroInventoryDeductRecord tmepDeductRecord: microDeductList){
				orderExistFilterTotalNum+=tmepDeductRecord.getQty();
				orderLists.add(tmepDeductRecord.getOrderNo());
			}
			return new ProductMicroInventoyResult(true,null,orderExistFilterTotalNum,orderLists);
			
		} catch (Exception e) {
			Logger.info("获取商品处于流转中的订单发生异常{}", e);
			Map<String,Object> tempMap=Maps.newHashMap();
			tempMap.put("sku", sku);
			tempMap.put("warehouseId", warehouseId);
			return new ProductMicroInventoyResult(false,"sku="+paramDetail.getSku()+
			",warehouseId="+paramDetail.getWarehouseId()+
			",expirationDate="+DateUtils.date2string(paramDetail.getExpirationDate(), DateUtils.FORMAT_DATE_PAGE)+
			",获取商品处于流转中的订单发生异常",tempMap,null);
		}
		
	}

	@Override
	public ProductMicroInventoyResult updateCloudInventory(List inventoryDetailDataLists) {
		ProductInventoryTotal totalResult=new ProductInventoryTotal();
		InventorySyncRecord syncRecord=new InventorySyncRecord();//同步记录
		for (Object tempInventoryDetail : inventoryDetailDataLists) {
			ProductInventoryDetail erpDataDetail=(ProductInventoryDetail)tempInventoryDetail;
			String sku=erpDataDetail.getSku();
			Integer warehouseId = erpDataDetail.getWarehouseId();
			Map<String,Object> tempMap=Maps.newHashMap();
			tempMap.put("sku", sku);
			tempMap.put("warehouseId", warehouseId);
			//如果有不可失效锁的sku则不能进行库存更改操作
			List<ProductInventoryOrderLock> orderPermanentLockLists = inventoryOrderLack.getOrderLackBySkuAndWarhouseId(sku,warehouseId,-1);
			if(!orderPermanentLockLists.isEmpty()){
				return new ProductMicroInventoyResult(false,"sku="+sku+
						",warehouseId="+warehouseId+
						",BBC存在云仓锁库不可失效的订单",tempMap,null);
			}
			
			//云仓 总仓无该sku，warehouseId则不能进行库存处理
			totalResult = cloudInventoryTotalMapper.selectByParam(new ProductInventoryTotal(sku,warehouseId));
			if(totalResult==null){
				return new ProductMicroInventoyResult(false,"sku="+sku+
						",warehouseId="+warehouseId+
						",云仓总仓无该商品数据",tempMap,null);
			}
			//使可失效的锁失效
			List<ProductInventoryOrderLock> orderLockLists = inventoryOrderLack.getOrderLackBySkuAndWarhouseId(erpDataDetail.getSku(),erpDataDetail.getWarehouseId(),1);
			for(ProductInventoryOrderLock tempLock: orderLockLists){
				tempLock.setIsEffective((short) 0);
				tempLock.setUpdateTime(new Date());
				inventoryOrderLack.updateByPrimaryKeySelective(tempLock);
			}
			//该sku，warhouseId的所有到期日期库存设置为0
			List<ProductInventoryDetail> BBCInventoryDetailLists = cloudInventoryDetailMapper.selectInventoryDetailBySkuAndWarehouseId(erpDataDetail.getSku(), erpDataDetail.getWarehouseId());
			for(ProductInventoryDetail tempBBCInventoryDetail:BBCInventoryDetailLists){
				Logger.info("将sku={},warehouseId={},到期日期={},库存={}设置为0", tempBBCInventoryDetail.getSku(),tempBBCInventoryDetail.getWarehouseId(),tempBBCInventoryDetail.getExpirationDate(),tempBBCInventoryDetail.getStock());
				tempBBCInventoryDetail.setStock(0);
				tempBBCInventoryDetail.setUpdateTime(new Date());
				cloudInventoryDetailMapper.updateByPrimaryKeySelective(tempBBCInventoryDetail);
			}
			break;
		}
		int stockTotal=0;
		for (Object tempInventoryDetail : inventoryDetailDataLists) {
			
			ProductInventoryDetail erpDataDetail=(ProductInventoryDetail)tempInventoryDetail;
			String sku=erpDataDetail.getSku();
			Integer warehouseId = erpDataDetail.getWarehouseId();
			syncRecord.setSku(sku);
			syncRecord.setWarehouseId(warehouseId);
			//将erp数据更新至云仓库存
			ProductInventoryDetail paramDetail=new ProductInventoryDetail(sku, warehouseId, erpDataDetail.getExpirationDate());
			ProductInventoryDetail resultParam = cloudInventoryDetailMapper.selectByParam(paramDetail);
			if(resultParam!=null){
				resultParam.setStock(erpDataDetail.getStock());
				resultParam.setUpdateTime(new Date());
				cloudInventoryDetailMapper.updateByPrimaryKeySelective(resultParam);
			}else{
				erpDataDetail.setCreateTime(new Date());
				cloudInventoryDetailMapper.insertSelective(erpDataDetail);
			}
			stockTotal+=erpDataDetail.getStock();
		}
		
		totalResult.setStock(stockTotal);
		totalResult.setUpdateTime(new Date());
		cloudInventoryTotalMapper.updateByPrimaryKeySelective(totalResult);
		//同步记录
		InventorySyncRecord syncRecordResult = syncRecordMapper.selectBySkuAndWarehouseId(syncRecord.getSku(), syncRecord.getWarehouseId());
		if(syncRecordResult!=null){
			syncRecordResult.setLastSyncingTime(new Date());
			syncRecordResult.setSyncingNum(stockTotal);
			syncRecordResult.setOperator("系统同步ERP库存");
			syncRecordMapper.updateByPrimaryKeySelective(syncRecordResult);
		}else{
			syncRecord.setLastSyncingTime(new Date());
			syncRecord.setOperator("系统同步ERP库存");
			syncRecord.setSyncingNum(stockTotal);
			syncRecordMapper.insertSelective(syncRecord);
		}
		
		return new ProductMicroInventoyResult(true,"sku="+totalResult.getSku()+
				",warehouseId="+totalResult.getWarehouseId()+
				",库存["+stockTotal+"]已同步",null,null);
		
	}

	@Override
	public ProductMicroInventoyResult getOrderOccupyNumBySkuAndWarehouseId(String sku, Integer warehouseId) {
		try {
			Configuration config = Play.application().configuration().getConfig("b2c");
			String baseurl = config.getString("b2cBaseUrl");
			String url=baseurl+"/checkout/queryExistOrder";
			ProductInventoryDetail paramDetail=new ProductInventoryDetail(sku,warehouseId,null);
			List<OrderMicroInventoryDeductRecord> microDeductList= microDeductRecordMapper.selectRecordBySkuAndExpirationDate(paramDetail);
			if(microDeductList.isEmpty()){
				return new ProductMicroInventoyResult(true,null,0,null);
			}
			List<String> orderNoList=Lists.newArrayList();
			int totailNum=0;
			for(OrderMicroInventoryDeductRecord tmepDeductRecord: microDeductList){
				totailNum+=tmepDeductRecord.getQty();
				orderNoList.add(tmepDeductRecord.getOrderNo());
			}
			String orderNoExistHkStr = HttpUtil.post(Json.toJson(orderNoList).toString(), url);
			JsonNode orderNoExistHkNode = Json.parse(orderNoExistHkStr);
			if(orderNoExistHkNode==null || orderNoExistHkNode.size()<=0){
				return new ProductMicroInventoyResult(true,null,totailNum,null);
			}
			for(JsonNode tempOrderNoNode: orderNoExistHkNode){
				String orderNoHKExist=tempOrderNoNode.asText();
				microDeductList= microDeductList.stream()
				.filter(e->!e.getOrderNo().equals(orderNoHKExist))
				.collect(Collectors.toList());
			}
			int orderExistFilterTotalNum=0;
			for(OrderMicroInventoryDeductRecord tmepDeductRecord: microDeductList){
				orderExistFilterTotalNum+=tmepDeductRecord.getQty();
			}
			return new ProductMicroInventoyResult(true,null,orderExistFilterTotalNum,null);
			
		} catch (Exception e) {
			Logger.info("获取商品处于流转中的订单发生异常{}", e);
			Map<String,Object> tempMap=Maps.newHashMap();
			tempMap.put("sku", sku);
			tempMap.put("warehouseId", warehouseId);
			return new ProductMicroInventoyResult(false,"sku="+sku+
			",warehouseId="+warehouseId+
			",获取商品处于流转中的订单发生异常",tempMap,null);
		}
	}

	@Override
	public String setCloudInventory(String reqStr) {
		Map<String,Object> result=Maps.newHashMap();
		List<Map<String,Object>> failedList=Lists.newArrayList();
		List<Map<String,Object>> successList=Lists.newArrayList();
		JsonNode paramNodes = Json.parse(reqStr);
		for(JsonNode tempNode:paramNodes){
			String sku = tempNode.get("sku").asText();
			int warehouseId = tempNode.get("warehouseId").asInt();
			Map<String,Object> tempMap=Maps.newHashMap();
			tempMap.put("sku", sku);
			tempMap.put("warehouseId", warehouseId);
			//查询是否存在不可失效的库存锁定
			List<ProductInventoryOrderLock> orderPermanentLockLists = inventoryOrderLack.getOrderLackBySkuAndWarhouseId(sku,warehouseId,-1);
			if(!orderPermanentLockLists.isEmpty()){
				tempMap.put("msg", "该sku存在不可失效的云仓采购订单锁库！");
				failedList.add(tempMap);
				continue;
			}
			//使可失效的锁失效
			List<ProductInventoryOrderLock> orderLockLists = inventoryOrderLack.getOrderLackBySkuAndWarhouseId(sku,warehouseId,1);
			for(ProductInventoryOrderLock tempLock: orderLockLists){
				tempLock.setIsEffective((short) 0);
				tempLock.setUpdateTime(new Date());
				inventoryOrderLack.updateByPrimaryKeySelective(tempLock);
			}
			//该sku，warhouseId的所有到期日期库存设置为0
			List<ProductInventoryDetail> BBCInventoryDetailLists = cloudInventoryDetailMapper.selectInventoryDetailBySkuAndWarehouseId(sku, warehouseId);
			for(ProductInventoryDetail tempBBCInventoryDetail:BBCInventoryDetailLists){
				Logger.info("将sku={},warehouseId={},到期日期={},库存={}设置为0", tempBBCInventoryDetail.getSku(),tempBBCInventoryDetail.getWarehouseId(),tempBBCInventoryDetail.getExpirationDate(),tempBBCInventoryDetail.getStock());
				tempBBCInventoryDetail.setStock(0);
				tempBBCInventoryDetail.setUpdateTime(new Date());
				cloudInventoryDetailMapper.updateByPrimaryKeySelective(tempBBCInventoryDetail);
			}
			//将云仓总仓设置为0
			ProductInventoryTotal cloudTotal = cloudInventoryTotalMapper.selectBySkuAndWarehouseId(sku, warehouseId);
			cloudTotal.setStock(0);
			cloudTotal.setUpdateTime(new Date());
			Logger.info("将sku={},warehouseId={},库存={}设置为0", cloudTotal.getSku(),cloudTotal.getWarehouseId(),cloudTotal.getStock());
			cloudInventoryTotalMapper.updateByPrimaryKeySelective(cloudTotal);
			successList.add(tempMap);
		}
		result.put("failedList", failedList);
		result.put("successList", successList);
		return Json.toJson(result).toString();
	}

	@Override
	public InventoryCommonResult<JsonNode> getErpExpirationInventoryData(String sku, int warehouseId, int type) {
		InventoryCommonResult<JsonNode> result=new InventoryCommonResult<JsonNode>();
		try{
			Map<String, Object> paramMap = Maps.newHashMap();
			List<String> skus=Lists.newArrayList();
			skus.add(sku);
			paramMap.put("skus", skus);
			paramMap.put("stock_id", warehouseId);
			paramMap.put("timestamp", 0);
			String resultStrBoxInfo = HttpUtil.post(
					Json.toJson(paramMap).toString(),
					ErpApiUtil.ERP_HOST + ErpApiUtil.STOCK_INIT_API + "?api_key="
							+ ErpApiUtil.ERP_API_KEY);
			Logger.info("查询erp商品到期日期库存接口参数[sku={},warehouseId={}],返回结果:{}", sku,warehouseId,resultStrBoxInfo);
			JsonNode resultNode = Json.parse(resultStrBoxInfo);
			if(resultNode.get("result")==null||resultNode.get("result").size()<=0){//查询到期日期接口没有该sku数据
				if(type==0){//当type为1时不查商品接口
					result.setResultCode(1);
					result.setMsg("erp查询不到商品到期日期库存明细数据！");
				}else{
					result = this.getErpInventoryData(sku, warehouseId);
				}
			}else{
				result.setResultCode(0);
				result.setData(resultNode);
			}
		}catch (Exception e) {
			Logger.info("查询erp商品到期日期库存明细参数[sku={},warehouseId={}]发生异常{}", sku,warehouseId);
			result.setResultCode(1);
			result.setMsg("查询erp商品到期日期库存明细发生异常！");
		}
		return result;
	}

	@Override
	public InventoryCommonResult<JsonNode> getErpInventoryData(String sku, int warehouseId) {
		InventoryCommonResult<JsonNode> result=new InventoryCommonResult<JsonNode>();
		Map<String, Object> paramMap =Maps.newHashMap();
		paramMap.put("sku", sku);
		paramMap.put("stock_id", warehouseId);
		paramMap.put("timestamp", 0);
		String resultStrBoxInfo = HttpUtil.post(
				Json.toJson(paramMap).toString(),
				ErpApiUtil.ERP_HOST + ErpApiUtil.EXTERNAL_WAREHOUSE_API + "?api_key="
						+ ErpApiUtil.ERP_API_KEY);
		Logger.info("查询erp商品列表接口参数[sku={},warehouseId={}],返回结果:{}", sku,warehouseId,resultStrBoxInfo);
		try {
			JsonNode resultNode = Json.parse(resultStrBoxInfo);
			if(resultNode.get("result").size() > 0){
				JsonNode skuStock = resultNode.get("result").get(0);
				int wareId = skuStock.get("stock_id").asInt();
				Map<String,Object> expirationAndStock=Maps.newHashMap();
				List<Map<String,Object>> expirationDatas=Lists.newArrayList();
				expirationAndStock.put("productionDate", ErpApiUtil.EXPIRATION_STR);
				if(wareId==2012){
					if(ErpApiUtil.EXTERNAL_WAREHOUSE_STOCK!=-1){//有配置杭州仓2012的初始化数量
						expirationAndStock.put("leftCount", ErpApiUtil.EXTERNAL_WAREHOUSE_STOCK);
					}else{//没有配置杭州仓2012的初始化数量
						if(skuStock.get("available_stock_count")!=null){
							expirationAndStock.put("leftCount", skuStock.get("available_stock_count").asInt());
						}else{
							expirationAndStock.put("leftCount", 0);
						}
					}
				}else if(wareId==2062){
					if(skuStock.get("available_stock_count")!=null){
						expirationAndStock.put("leftCount", skuStock.get("available_stock_count").asInt());
					}else{
						expirationAndStock.put("leftCount", 0);
					}
				}else{
					expirationAndStock.put("leftCount", skuStock.get("available_stock_count").asInt());
				}
				expirationDatas.add(expirationAndStock);
				
				Map<String,Object> tempResultMap=Maps.newHashMap();
				List<Map<String,Object>> tempList=Lists.newArrayList();
				Map<String,Object> tempDate=Maps.newHashMap();
				tempDate.put("wait_delivery_count", 0);	
				tempDate.put("itemIds", Lists.newArrayList());
				tempDate.put("sku", skuStock.get("sku").asText());
				tempDate.put("expiration", expirationDatas);
				tempList.add(tempDate);
				tempResultMap.put("result", tempList);
				JsonNode erpInventoryData = Json.toJson(tempResultMap);
				result.setResultCode(0);
				result.setData(erpInventoryData);
			}else{
				result.setResultCode(1);
				result.setMsg("查询erp商品列表接口获取不到商品信息!");
			}
		} catch (Exception e) {
			Logger.info("查询erp商品列表接口发生异常:{}", e);
			result.setResultCode(1);
			result.setMsg("查询erp商品列表接口发生异常!");
		}
		return result;
	}

	@Override
	public InventoryCommonResult<List<ProductInventoryDetail>> formatErpInventoryData(String tempErpDataStr,String sku, Integer warehouseId) {
		JsonNode erpDataNode = Json.parse(tempErpDataStr);
		JsonNode resultNode = erpDataNode.get("result").get(0);
		//待发数目
		int waitDeliveryCount = resultNode.get("wait_delivery_count").asInt(0);

		//带到期日期商品数量
		JsonNode expirationDataNode = resultNode.get("expiration");
		if(expirationDataNode==null ||expirationDataNode.size()<=0){
			return new InventoryCommonResult<>(1,"erp查询不到商品到期日期库存信息");
		}
		//erp总库存
		int erpTotalInventory=0;
		//erp库存明细数据
		List<ProductInventoryDetail> erpDetailList=Lists.newArrayList();
		for(JsonNode tempExpirationNode:expirationDataNode){
			ProductInventoryDetail inventoryDetail=new ProductInventoryDetail();
			String expirationStr = tempExpirationNode.get("productionDate").asText(ErpApiUtil.EXPIRATION_STR);
			int leftCount = tempExpirationNode.get("leftCount").asInt(0);
			Date expirationDate=null;
			try {
				expirationDate=DateUtils.string2date(expirationStr, DateUtils.FORMAT_DATE_PAGE);
			} catch (ParseException e) {
				Logger.info("============SKU[{}],含有异常格式到期日期的库存！设置2116-02-07过期！============", sku);
				expirationDate=ErpApiUtil.EXPIRATION_DATE;
			}
			inventoryDetail.setSku(sku);
			inventoryDetail.setWarehouseId(warehouseId);
			inventoryDetail.setExpirationDate(expirationDate);
			inventoryDetail.setStock(leftCount);
			erpTotalInventory+=leftCount;
			erpDetailList.add(inventoryDetail);
		}
		
		//微仓出库记录到期日期对应的erp待发数
		Map<Date,Integer> waitDetailMap=Maps.newHashMap();
		
		if(waitDeliveryCount>0){//有代发数
			
			if(waitDeliveryCount>erpTotalInventory){//待发数大于erp总库存数
				return new InventoryCommonResult<>(1,"erp待发数大于erp库存总量");
			}
			JsonNode orderNoNodes = resultNode.get("itemIds");
			//商品待发数相关订单
			List<String> orderNoLists=Lists.newArrayList();
			if(orderNoNodes==null || orderNoNodes.size()<=0){//erp缺少待发订单信息
				return new InventoryCommonResult<>(1,"erp缺少商品待发数相关的订单信息");
			}else{
				for(JsonNode tempOrderNode:orderNoNodes){
					String orderNo = tempOrderNode.asText();
					orderNoLists.add(orderNo);
				}
			}
			List<OrderMicroInventoryDeductRecord> microDeductRecordLists = this.getMicroDeductRecordByOrderNoAndSku(orderNoLists, sku,null);
			
			//微仓出库总数量
			int countNum=0;
			if(microDeductRecordLists==null || microDeductRecordLists.size()<=0){
				return new InventoryCommonResult<>(1,"微仓出库记录查询不到待发数相关的订单信息");
			}else{
				for(OrderMicroInventoryDeductRecord tempRecord:microDeductRecordLists){
					countNum+=tempRecord.getQty();
				}
			}
			if(countNum != waitDeliveryCount ){//微仓出库数量与代发数不相等
				return new InventoryCommonResult<>(1,"微仓出库记录订单数量与待发数不匹配");
			}
			//将微仓出库订单包含的商品数量按到期日期分组统计
			if(!microDeductRecordLists.isEmpty()){
				for(OrderMicroInventoryDeductRecord tempRecord:microDeductRecordLists){
					Date dateKey = tempRecord.getExpirationDate();
					Integer qty = tempRecord.getQty();
					if(waitDetailMap.get(dateKey)!=null){
						waitDetailMap.put(dateKey, waitDetailMap.get(dateKey)+qty);
					}else{
						waitDetailMap.put(dateKey, qty);
					}
				}
			}
		}
		//将erp库存由少到多排序
		erpDetailList.sort((a,b) -> a.getStock()-b.getStock());
		
		//订单指定 到期日期数量大于erp现有库存数量，从其他到期日期扣减
		int waitErpDeductNum=0;
		for(ProductInventoryDetail tempErpDetail:erpDetailList){
			Date dateKey = tempErpDetail.getExpirationDate();
			int orderSalesNum=waitErpDeductNum;
			if(waitDetailMap.get(dateKey)!=null){//该到期日期待发数量+其他到期日期剩余数量
				orderSalesNum+=waitDetailMap.get(dateKey);
			}
			if(tempErpDetail.getStock()<orderSalesNum){//erp该到期日期数量数量不足待发数扣减
				waitErpDeductNum=orderSalesNum-tempErpDetail.getStock();
				tempErpDetail.setStock(0);
			}else{
				tempErpDetail.setStock(tempErpDetail.getStock()-orderSalesNum);
				waitErpDeductNum=0;
			}
		}
		if(waitErpDeductNum>0){
			return new InventoryCommonResult<>(1,"待发数无法分配到erp库存数据");
		}
		return new InventoryCommonResult<List<ProductInventoryDetail>>(0,erpDetailList);
	}

	@Override
	public InventoryCommonResult<String> downLoadPhysicalResult(String time) {
		InventoryCommonResult<String> result = new InventoryCommonResult<>();
		Configuration conf = Play.application().configuration()
				.getConfig("inventory");
		String path = conf.getString("stockCheckFilePath");
		if(time==null){
			time=LocalDate.now().toString();
		}
		String fileName=time+"库存清点结果.json";
		File file=new File(path+File.separator+fileName);
		BufferedReader reader = null; 
		String tempString=null;
		try{
			reader = new BufferedReader(new FileReader(file));
			tempString=reader.readLine();
			reader.close();
		}catch (Exception e) {
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException x) {
					
				}
			}
			Logger.info("库存盘点结果文件读取发生异常{}", e);
			result.setResultCode(1);
			result.setMsg("库存盘点结果文件读取发生异常！");
			return result;
		}
		if(tempString!=null){
			result.setResultCode(0);
			result.setData(tempString);
			return result;
		}else{
			result.setResultCode(1);
			result.setMsg("库存盘点结果为空！");
			return result;
		}
	}

	@Override
	public byte[] downLoadCheckResult(String time) throws IOException {
		Configuration conf = Play.application().configuration()
				.getConfig("inventory");
		String path = conf.getString("stockCheckFilePath");
		if(time==null){
			time=LocalDate.now().toString();
		}
		String fileName=time+"库存清点结果.xls";
		File file=new File(path+File.separator+fileName);
		InputStream fis = new BufferedInputStream(new FileInputStream(file));
		byte[] buffer = new byte[fis.available()];
		fis.read(buffer);
		fis.close();
		return buffer;
	}
}
