package controllers.product_inventoy;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import controllers.annotation.GetRequestIp;
import dto.inventory.ProductCloudInventoryResult;
import dto.product_inventory.HistoryOrderData;
import dto.product_inventory.ProductMicroDeductDetail;
import dto.product_inventory.PurchaseOrderRequestParam;
import dto.product_inventory.PurchaseOrderResult;
import entity.product_inventory.Order;
import entity.product_inventory.OrderDetail;
import entity.product_inventory.ProductMicroInventoryInRecord;
import entity.product_inventory.ProductMicroInventoryOrderLock;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.product_inventory.IOrderService;
import util.warehousing.DataUtil;
import utils.inventory.JsonCaseUtil;
import utils.inventory.Types;

@GetRequestIp
public class ProductInventoryOrderController extends Controller{

	@Inject
	IOrderService orderService;
	
	/**
	 * 采购单锁定库存:
	 * 普通采购单初次调用：检查库存是否足够，足够则进行库存锁定。返回锁定成功。否则返回库存缺少的数量
	 * 	 重复调用添加商品：逻辑同初次调用，在锁定成功的情况修改订单内容（删除原有的，重新保存）
	 * 	重复调用：场景为支付前校验锁定是否还有效，无效重新校验库存，有效则重新锁定时间
	 * @return
	 */
	public Result purchaseOrderLockCloudStock(){
		
		try {
			Map<String,Object> result=Maps.newHashMap();
			JsonNode reqOfPurchaseOrder = request().body().asJson();
			if(reqOfPurchaseOrder==null || reqOfPurchaseOrder.size()==0){
				result.put("result", false);
				result.put("msg", "采购订单参数不正确！");
				return ok(Json.toJson(result));
			}
			Logger.info("purchaseOrderLockCloudStock-->reqParam[{}]",reqOfPurchaseOrder);
			if(reqOfPurchaseOrder.get("change")!=null&&reqOfPurchaseOrder.get("change").size()>0){//修改订单详情
				Order orderParam=new Order();
				orderParam.setOrderNo(reqOfPurchaseOrder.get("orderNo").asText());
				Order order=orderService.getOrder(orderParam);
				
				//查询云仓库存是否满足增量
				JsonNode changeNodes=reqOfPurchaseOrder.get("change");
				List<OrderDetail> orderDetailList=Lists.newArrayList();
				OrderDetail orderDetail=null;
				for(JsonNode changeNode:changeNodes){
					orderDetail=new OrderDetail();
					orderDetail.setOrderNo(reqOfPurchaseOrder.get("orderNo").asText());
					orderDetail.setSku(changeNode.get("sku").asText());
					if(changeNode.get("expirationDate")!=null && !"null".equals(changeNode.get("expirationDate").asText())){
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
						Date expirationDate = sdf.parse(changeNode.get("expirationDate").asText());
						orderDetail.setExpirationDate(expirationDate);
					}
					orderDetail.setQty(changeNode.get("qty").asInt());
					orderDetail.setPurchasePrice((float) changeNode.get("purchasePrice").asDouble());
					orderDetail.setIsGift((short) changeNode.get("isGift").asInt());
					orderDetail.setWarehouseId(changeNode.get("warehouseId").asInt());
					orderDetail.setWarehouseName(changeNode.get("warehouseName").asText());
					orderDetail.setCategoryId(changeNode.get("categoryId").asInt());
					orderDetail.setCategoryName(changeNode.get("categoryName").asText());
					orderDetailList.add(orderDetail);
				}
				List<Object> checkInventoryTotalNum = orderService.checkInventoryTotalNum(order,orderDetailList);
				if(checkInventoryTotalNum.size()>0){//存在商品不满足
					ProductCloudInventoryResult checkResult=new ProductCloudInventoryResult(false,"存在商品不足",checkInventoryTotalNum,null);
					return ok(Json.toJson(checkResult));
				}
				
				//删除该订单号下所有订单详情数据 ，重新生成订单详情信息
				OrderDetail orderDetailParam=new OrderDetail();
				orderDetailParam.setOrderNo(reqOfPurchaseOrder.get("orderNo").asText());
				orderService.deleteAllOrderDetailDateByOrderNo(orderDetailParam);
				
				JsonNode nodes=reqOfPurchaseOrder.get("pros");
				List<OrderDetail> orderDetailReList=Lists.newArrayList();
				OrderDetail orderDetailRe=null;
				for(JsonNode node:nodes){
					orderDetailRe=new OrderDetail();
					orderDetailRe.setOrderNo(reqOfPurchaseOrder.get("orderNo").asText());
					orderDetailRe.setSku(node.get("sku").asText());
					if(node.get("expirationDate")!=null && !"null".equals(node.get("expirationDate").asText())){
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
						Date expirationDate = sdf.parse(node.get("expirationDate").asText());
						orderDetailRe.setExpirationDate(expirationDate);
					}
					orderDetailRe.setProductTitle(node.get("productTitle").asText());
					orderDetailRe.setImgUrl(node.get("imgUrl").asText());
					orderDetailRe.setQty(node.get("qty").asInt());
					orderDetailRe.setPurchasePrice((float) node.get("purchasePrice").asDouble());
					orderDetailRe.setCapfee( (float) node.get("capfee").asDouble());
					orderDetailRe.setArriveWarePrice((float) node.get("arriveWarePrice").asDouble());
					orderDetailRe.setIsGift((short) node.get("isGift").asInt());
					orderDetailRe.setWarehouseId(node.get("warehouseId").asInt());
					orderDetailRe.setWarehouseName(node.get("warehouseName").asText());
					orderDetail.setCategoryId(node.get("categoryId").asInt());
					orderDetail.setCategoryName(node.get("categoryName").asText());
					orderDetail.setContractNo(JsonCaseUtil.jsonCase(node, "contractNo", Types.STR));
					orderDetail.setClearancePrice(JsonCaseUtil.jsonCase(node, "clearancePrice", Types.DOU));
					orderDetailReList.add(orderDetailRe);
				}
				Logger.info("orderDetailList[{}]", orderDetailList.toString());
				ProductCloudInventoryResult orderDetailReSave = orderService.reSaveOrderDetail(order,orderDetailReList);
				return ok(Json.toJson(orderDetailReSave).toString());
			}
			//新采购单或者对采购单进行支付前库存校验
			Order order=new Order();
			order.setOrderNo(reqOfPurchaseOrder.get("orderNo").asText());
			order.setAccount(reqOfPurchaseOrder.get("account").asText());
			order.setAccountName(JsonCaseUtil.jsonCase(reqOfPurchaseOrder,"accountName",Types.STR));
			order.setSaleOrderNo(JsonCaseUtil.jsonCase(reqOfPurchaseOrder, "saleOrderNo", Types.STR));
			
			JsonNode nodes=reqOfPurchaseOrder.get("pros");
			List<OrderDetail> orderDetailList=Lists.newArrayList();
			OrderDetail orderDetail=null;
			for(JsonNode node:nodes){
				orderDetail=new OrderDetail();
				orderDetail.setOrderNo(reqOfPurchaseOrder.get("orderNo").asText());
				orderDetail.setSku(node.get("sku").asText());
				if(node.get("expirationDate")!=null && !"null".equals(node.get("expirationDate").asText())){
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
					Date expirationDate = sdf.parse(node.get("expirationDate").asText());
					orderDetail.setExpirationDate(expirationDate);
				}
				orderDetail.setProductTitle(node.get("productTitle").asText());
				orderDetail.setImgUrl(node.get("imgUrl").asText());
				orderDetail.setQty(node.get("qty").asInt());
				orderDetail.setPurchasePrice((float) node.get("purchasePrice").asDouble());
				orderDetail.setCapfee((float) node.get("capfee").asDouble());
				orderDetail.setArriveWarePrice((float) node.get("arriveWarePrice").asDouble());
				orderDetail.setIsGift((short) node.get("isGift").asInt());
				orderDetail.setWarehouseId(node.get("warehouseId").asInt());
				orderDetail.setWarehouseName(node.get("warehouseName").asText());
				orderDetail.setCategoryId(node.get("categoryId").asInt());
				orderDetail.setCategoryName(node.get("categoryName").asText());
				orderDetail.setContractNo(JsonCaseUtil.jsonCase(node, "contractNo", Types.STR));
				orderDetail.setClearancePrice(JsonCaseUtil.jsonCase(node, "clearancePrice", Types.DOU));
				orderDetailList.add(orderDetail);
			}
			if(order==null || orderDetailList==null){
				return ok(Json.toJson(new ProductCloudInventoryResult(false,"订单信息为空",null,null)));
			}
			Logger.info("purchaseOrderInfo[{}],orderDetail[{}]", order.toString(),orderDetailList.toString());
			ProductCloudInventoryResult orderDisposeResult = orderService.orderLockCloudDispose(order,orderDetailList);
			Logger.info("purchaseOrderLockStockResult[{}]", orderDisposeResult.toString());
			return ok(Json.toJson(orderDisposeResult));
		} catch (Exception e) {
			Logger.info("purchaseOrderLockCloudStockError[{}]",e);
			return ok(Json.toJson(new ProductCloudInventoryResult(false,"系统异常",null,null)));
		}
		
	}
	
	/**
	 * 修改订单详情中价格信息
	 * @return
	 */
	public Result changeOrderDetailInfo(){
		Map<String,Object> result=Maps.newHashMap();
		JsonNode reqOfPurchaseOrder = request().body().asJson();
		PurchaseOrderRequestParam reqParam=new PurchaseOrderRequestParam();
		Logger.info("修改订单详情中价格信息参数:{}",reqOfPurchaseOrder);
		try {
			reqParam=DataUtil.fromJson(PurchaseOrderRequestParam.class, reqOfPurchaseOrder.toString());
		} catch (IOException e) {
			Logger.info("changeOrderDetailInfoError[{}]",e);
			result.put("result", false);
			result.put("msg", "采购单获取参数发生异常");
			return ok(Json.toJson(result));
		}
		Order orderParam = reqParam.getOrder();
		String saleOrderNo = orderParam.getSaleOrderNo();
		if(null==saleOrderNo || "".equals(saleOrderNo) || "null".equals(saleOrderNo)){
			orderParam.setSaleOrderNo(null);
		}
		
		//查看采购单是否存在
		Order order = orderService.getOrder(orderParam);
		if(order==null){
			result.put("result", false);
			result.put("msg", "采购单不存在");
			return ok(Json.toJson(result));
		}
		List<OrderDetail> orderDetailList=null;
		try {
			orderDetailList = reqParam.getOrderDetailList();
		} catch (ParseException e) {
			Logger.info("修改订单信息获取参数异常：{}", e);
			result.put("result", false);
			result.put("msg", "修改订单信息获取参数异常");
			return ok(Json.toJson(result));
		}
		if(order==null || orderDetailList==null ||orderDetailList.size()<=0){
			result.put("result", false);
			result.put("msg", "订单信息为空");
			return ok(Json.toJson(result));
		}
		ProductCloudInventoryResult reSaveOrderDetailResult = orderService.changeOrderDetailInfo(order, orderDetailList);
		result.put("result", reSaveOrderDetailResult.isResult());
		result.put("msg", reSaveOrderDetailResult.getMsg());
		return ok(Json.toJson(result));
	}
	
	/**
	 * 将订单关联的库存锁定记录状态更改为不可失效
	 * @param orderNo
	 * @return
	 */
	public Result changeLockStatus(String orderNo){
		Map<String,Object> result=Maps.newHashMap();
		if(orderNo==null){
			result.put("result", false);
			result.put("msg", Json.toJson(result));
		}
		Logger.info("changeLockStatus-->reqParam[{}]",orderNo);
		Order orderParam=new Order();
		orderParam.setOrderNo(orderNo);

		ProductCloudInventoryResult orderDisposeResult = orderService.changeLockToEffective(orderParam);
		return ok(Json.toJson(orderDisposeResult));
	}
	
	/**
	 * 采购单取消
	 * @return
	 */
	public Result orderCanceled(String orderNo){

		Map<String,Object> result=Maps.newHashMap();
		if(orderNo==null){
			result.put("result", false);
			result.put("msg", Json.toJson(result));
			return ok(Json.toJson(result));
		}
		Logger.info("orderCanceled-->reqParam[{}]",orderNo);
		OrderDetail orderDetailParam=new OrderDetail();
		orderDetailParam.setOrderNo(orderNo);
		List<OrderDetail> orderDetailListForPurchase = orderService.getOrderListForPurchase(orderDetailParam);
		
		Order orderParam=new Order();
		orderParam.setOrderNo(orderNo);
		Order purchaseOrder=orderService.getOrder(orderParam);
		if(purchaseOrder==null || orderDetailListForPurchase==null || orderDetailListForPurchase.size()<=0){
			result.put("result", false);
			result.put("msg", "查询不到相关订单详细");
			return ok(Json.toJson(result));
		}
		Logger.info("canceledPurchaseOrderInfo[{}],orderDetail[{}]", purchaseOrder.toString(), orderDetailListForPurchase.toString());
		ProductCloudInventoryResult orderDisposeResult = orderService.orderCanceled(purchaseOrder, orderDetailListForPurchase);
		Logger.info("canceledPurchaseOrderResult[{}]", orderDisposeResult.toString());
		return ok(Json.toJson(orderDisposeResult));
	}
	
	/**
	 * 采购单进行云仓库存扣减，微仓库存增加
	 * @return
	 */
	public Result updateStockByPurchaseOrder(String orderNo){
		
		Map<String,Object> result=Maps.newHashMap();
		if(orderNo==null){
			result.put("result", false);
			result.put("msg", "采购单号为空");
			return ok(Json.toJson(result));
		}
		Logger.info("updateStockByPurchaseOrder-->reqParam[{}]",orderNo);
		Order orderParam=new Order();
		orderParam.setOrderNo(orderNo);
		Order orderResult=null;
		try {
			orderResult = orderService.getOrder(orderParam);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", false);
			result.put("msg", "获取采购单信息发生异常！");
			return ok(Json.toJson(result));
		}
		
		OrderDetail orderDetailParam=new OrderDetail();
		orderDetailParam.setOrderNo(orderNo);
		List<OrderDetail> orderDetailList=orderService.getOrderListForPurchase(orderDetailParam);
		
		ProductCloudInventoryResult orderDisposeResult = orderService.updateStockByPurchaseOrder(orderResult,orderDetailList);
		Logger.info("采购单进行库存变更结果：pruchaseOrder[{}],updateStockResuslt[{}]",orderNo,orderDisposeResult.toString());
		if(!orderDisposeResult.isResult()){//采购单云仓扣减、微仓增加失败
			return ok(Json.toJson(orderDisposeResult));
		}
		String saleOrderNo = orderResult.getSaleOrderNo();
		if(saleOrderNo==null){//普通采购单
			return ok(Json.toJson(orderDisposeResult));
		}
		
		//缺货采购单进行出库操作
		//查询该采购单对应的销售单锁定微仓库存记录
		ProductMicroInventoryOrderLock microLockParam=new ProductMicroInventoryOrderLock();
		microLockParam.setIsEffective((short) -1);
		microLockParam.setOrderNo(saleOrderNo);
		List<ProductMicroInventoryOrderLock> microLackList=orderService.selectMicroLockListForMicroOut(microLockParam);
		
		//该采购单对应的微仓入库记录
		ProductMicroInventoryInRecord microInRecordParam=new ProductMicroInventoryInRecord();
		microInRecordParam.setOrderNo(orderNo);
		List<ProductMicroInventoryInRecord> microInRecordList=orderService.selectMicroInRecordForMicroOut(microInRecordParam);
		
		ProductCloudInventoryResult microOutResult =orderService.microOutOfSalesOrder(microLackList,microInRecordList,saleOrderNo);
		if(!microOutResult.isResult()){
			return ok(Json.toJson(microOutResult));
		}
		//根据入仓记录构造返回结果
		List<ProductMicroDeductDetail> microDeductDetialList=Lists.newArrayList();
		ProductMicroDeductDetail microDeductDetial = null;
		for (ProductMicroInventoryInRecord microInRecord : microInRecordList) {
			microDeductDetial=new ProductMicroDeductDetail();
			microDeductDetial.setAccount(microInRecord.getAccount());
			microDeductDetial.setSku(microInRecord.getSku());
			microDeductDetial.setExpirationDate(microInRecord.getExpirationDate());
			microDeductDetial.setQty(microInRecord.getQty());
			microDeductDetial.setTitle(microInRecord.getProductTitle());
			microDeductDetial.setImgUrl(microInRecord.getImgUrl());
			microDeductDetial.setPurchasePrice(microInRecord.getPurchasePrice().toString());
			microDeductDetial.setCapfee(microInRecord.getCapfee().toString());
			microDeductDetial.setArriveWarePrice(microInRecord.getArriveWarePrice().toString());
			microDeductDetial.setWarehouseId(microInRecord.getWarehouseId());
			microDeductDetial.setWarehouseName(microInRecord.getWarehouseName());
			microDeductDetial.setPurchaseNo(microInRecord.getOrderNo());
			microDeductDetial.setSaleOrderNo(saleOrderNo);
			microDeductDetial.setIsGift(microInRecord.getIsGift());
			microDeductDetial.setContractNo(microInRecord.getContractNo());
			microDeductDetial.setClearancePrice(microInRecord.getClearancePrice());
			microDeductDetialList.add(microDeductDetial);
		}
		PurchaseOrderResult purchaseOrderResult=new PurchaseOrderResult();
		purchaseOrderResult.setResult(microOutResult.isResult());
		purchaseOrderResult.setMsg(microOutResult.getMsg());
		purchaseOrderResult.setMicroOutList(microDeductDetialList);
		
		Logger.info("库存变更出微仓库存结果：updateStockResuslt[{}]", purchaseOrderResult.toString());
		return ok(Json.toJson(purchaseOrderResult));
	}
	
	/**
	 * 版本对接：历史订单关闭构造数据
	 * @return
	 */
	public Result historyOrderClosed(){
		Map<String,Object> result=Maps.newHashMap();
		JsonNode historyOrderDataNode = request().body().asJson();
		HistoryOrderData historyOrderData =new HistoryOrderData();
		if(historyOrderDataNode==null){
			result.put("result", false);
			result.put("msg", "订单信息为空");
			return ok(Json.toJson(result));
		}
		try {
			historyOrderData=DataUtil.fromJson(HistoryOrderData.class, historyOrderDataNode.toString());
		} catch (IOException e) {
			Logger.info("historyOrderClosed[{}]",e);
			result.put("result", false);
			result.put("msg", "历史订单数据获取异常");
			return ok(Json.toJson(result));
		}
		Logger.info("historyOrderData[{}]", historyOrderData.toString());
		ProductCloudInventoryResult restoreResult= orderService.historyOrderDataClosed(historyOrderData);
		return ok(Json.toJson(restoreResult));
	}
	
	/*public Result initResidueNum(){
		try {
			orderService.initResidueNum();
			return ok("云仓总仓剩余初始化成功");
		} catch (Exception e) {
			Logger.info("云仓总仓剩余初始化失败", e);
			return ok("云仓总仓剩余初始化失败");
		}
	}*/

	public Result getOrderDetailBySkuAndSaleOrderNo() {
		JsonNode main = request().body().asJson();

		Logger.info("根据销售单号和sku查询采购历史:[{}]",main);

		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}

		String s_main = main.toString();
		return ok(Json.toJson(orderService.getOrderDetailBySkuAndSaleOrderNo(s_main)));
	}

	public Result getPurchaseByPurchaseOrderNo() {
		JsonNode main = request().body().asJson();

		Logger.info("根据采购单号获取采购时间:[{}]",main);

		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}

		String s_main = main.toString();

		ObjectMapper objectMapper = new ObjectMapper();
		//设置日期格式
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		objectMapper.setDateFormat(fmt);
		String str = "";
		try {
			str = objectMapper.writeValueAsString(orderService.getPurchaseByPurchaseOrderNo(s_main));
		} catch (JsonProcessingException e) {
			Logger.error(">>>>>>>>>>>>>>>>>>>>>getPurchaseByPurchaseOrderNo writeValueAsString error:{}", e);
			e.printStackTrace();
		}
		return ok(Json.parse(str));
	}
	
	/**
	 * 查询订单详情接口
	 * @author zbc
	 * @since 2017年4月22日 上午10:53:22
	 */
	public Result getOrderDetails(String orderNo){
		return ok(Json.toJson(orderService.getOrderDetails(orderNo)));
	}
	public static void main(String[] args) {
		System.out.println(Json.toJson(Lists.newArrayList()).size());
	}
}
