package services.product_inventory.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import dto.inventory.ProductCloudInventoryResult;
import dto.product_inventory.HistoryDateOrderDetail;
import dto.product_inventory.HistoryOrderData;

import org.joda.time.LocalDate;
import org.mybatis.guice.transactional.Transactional;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import entity.product_inventory.Order;
import entity.product_inventory.OrderDetail;
import entity.product_inventory.OrderInventoryDeductRecord;
import entity.product_inventory.OrderMicroInventoryDeductRecord;
import entity.product_inventory.ProductInventoryDetail;
import entity.product_inventory.ProductInventoryOrderLock;
import entity.product_inventory.ProductInventoryTotal;
import entity.product_inventory.ProductMicroInventoryDetail;
import entity.product_inventory.ProductMicroInventoryInRecord;
import entity.product_inventory.ProductMicroInventoryOrderLock;
import entity.product_inventory.ProductMicroInventoryTotal;
import mapper.product_inventory.OrderDetailMapper;
import mapper.product_inventory.OrderInventoryDeductRecordMapper;
import mapper.product_inventory.OrderMapper;
import mapper.product_inventory.OrderMicroInventoryDeductRecordMapper;
import mapper.product_inventory.OrderStatusChangeRecordMapper;
import mapper.product_inventory.ProductInventoryDetailMapper;
import mapper.product_inventory.ProductInventoryOrderLockMapper;
import mapper.product_inventory.ProductInventoryTotalMapper;
import mapper.product_inventory.ProductMicroInventoryDetailMapper;
import mapper.product_inventory.ProductMicroInventoryInRecordMapper;
import mapper.product_inventory.ProductMicroInventoryOrderLockMapper;
import mapper.product_inventory.ProductMicroInventoryTotalMapper;
import play.Logger;
import play.libs.Json;
import services.product_inventory.IOrderService;
import services.product_inventory.IProductMicroInventoryService;

public class OrderService implements IOrderService {

	@Inject
	OrderMapper orderMapper;

	@Inject
	OrderDetailMapper orderDetailMapper;
	
	@Inject
	OrderStatusChangeRecordMapper orderStatusChangeRecordMapper;
	
	@Inject
	ProductInventoryOrderLockMapper productInventoryOrderLockMapper;
	
	@Inject
	ProductInventoryDetailMapper productInventoryDetailMapper;
	
	@Inject
	ProductInventoryTotalMapper productInventoryTotalMapper;
	
	@Inject
	OrderInventoryDeductRecordMapper orderInventoryDeductRecordMapper;
	
	@Inject
	IProductMicroInventoryService productMicroInventoryService;
	
	@Inject
	ProductMicroInventoryDetailMapper microInventoryDetailMapper;
	
	@Inject
	ProductMicroInventoryInRecordMapper productMicroInventoryInRecordMapper;
	
	@Inject
	ProductMicroInventoryTotalMapper productMicroInventoryTotalMapper;

	@Inject
	ProductMicroInventoryOrderLockMapper productMicroInventoryOrderLockMapper;
	
	@Inject
	OrderMicroInventoryDeductRecordMapper orderMicroInventoryDeductRecordMapper;
	
	private final Object monitor = new Object();
	
	private final Date historyOrderExpirationDate = new LocalDate(2099,1,1).toDate();
	
	@Override
	public Order getOrder(Order order) {
		return orderMapper.selectOrderByParams(order);
	}

	@Override
	public void saveOrder(Order order) {
		orderMapper.insertSelective(order);
		
	}

	@Override
	public void saveOrderDetail(OrderDetail orderDetail) {
		orderDetailMapper.insertSelective(orderDetail);
		
	}

	@Override
	public OrderDetail selectByParam(OrderDetail orderDetail) {
		return orderDetailMapper.selectByParam(orderDetail);
	}
	
	@Override
	public List<OrderDetail> getOrderListForPurchase(OrderDetail orderDetailParam) {
		String orderNo = orderDetailParam.getOrderNo();
		List<OrderDetail> purchaseOrderDetailList = orderDetailMapper.selectOrderDetailListByOrderNo(orderNo);
		return purchaseOrderDetailList;
	}

	@Override
	public List<ProductMicroInventoryOrderLock> selectMicroLockListForMicroOut(
			ProductMicroInventoryOrderLock microLockParam) {
		List<ProductMicroInventoryOrderLock> microLockListForMicroOut = productMicroInventoryOrderLockMapper.selectListsByParams(microLockParam);
		return microLockListForMicroOut;
	}
	
	@Override
	public List<ProductMicroInventoryInRecord> selectMicroInRecordForMicroOut(
			ProductMicroInventoryInRecord microInRecordParam) {
		List<ProductMicroInventoryInRecord> microInListForMicroOut = productMicroInventoryInRecordMapper.selectMicroInRecordListByParam(microInRecordParam);
		return microInListForMicroOut;
	}
	
	@Override
	public void deleteAllOrderDetailDateByOrderNo(OrderDetail orderDetailParam) {
		orderDetailMapper.deleteDateByOrderNo(orderDetailParam);
	}


	
	/**
	 * 采购订单未支付操作：订单信息，订单详情信息保存；云仓库存锁定。
	 */
	@Override
	@Transactional
	public ProductCloudInventoryResult orderLockCloudDispose(Order order, List<OrderDetail> orderDetailList) {
		Order orderCheck=orderMapper.selectOrderByParams(order);
		if(orderCheck !=null){//已存在采购单支付进行支付前库存校验
			String orderNo = orderCheck.getOrderNo();
			//使用已存储的订单信息进行库存校验
			orderDetailList=orderDetailMapper.selectOrderDetailListByOrderNo(orderNo);
			if(orderDetailList.isEmpty()){
				return new ProductCloudInventoryResult(false,"重新检查库存查询不到订单详情信息！",null,null);
			}
			ProductInventoryOrderLock inventoryLockParam=new ProductInventoryOrderLock();
			inventoryLockParam.setOrderNo(orderNo);
			List<ProductInventoryOrderLock> inventoryLockList=productInventoryOrderLockMapper.selectInventoryLockListByParams(inventoryLockParam);
			int lackStatus = inventoryLockList.get(0).getIsEffective().intValue();
			if(lackStatus==0){//锁已失效
				//重新检查库存
				List<Object> inventoryList=Lists.newArrayList();
				for (OrderDetail orderDetail : orderDetailList) {
					ProductCloudInventoryResult result = this.inventoryCheckByOrder(order,orderDetail);
					
					if(!result.isResult()){//库存不足
						inventoryList.add(result.getObj());
						//生成临时锁定剩余库存的记录
						Order tempOrder=new Order();
						String tempOrderNo="LS"+order.getOrderNo();
						tempOrder.setOrderNo(tempOrderNo);
						this.tempLockRemainInventoy(tempOrder,orderDetail);
					}else{
						//生成临时锁定库存记录
						Order tempOrder=new Order();
						String tempOrderNo="LS"+order.getOrderNo();
						tempOrder.setOrderNo(tempOrderNo);
						this.tempLockInventoy(tempOrder,orderDetail);
					}
				}
				//删除所有临时锁定
				productInventoryOrderLockMapper.deleteByOrderNo("LS"+order.getOrderNo());
				if(inventoryList.size()>0){//有商品库存不足
					return new ProductCloudInventoryResult(false,"存在商品库存不足！",inventoryList,null);
				}else{//重新使锁定生效
					for (ProductInventoryOrderLock inventoryLock : inventoryLockList) {
						inventoryLock.setIsEffective((short) 1);
						inventoryLock.setLastCheckTime(new Date());
						productInventoryOrderLockMapper.updateByPrimaryKeySelective(inventoryLock);
						//扣减总仓可用数
						//this.deductCloudTotalResidueNumByLockInfo(inventoryLock);
					}
					return new ProductCloudInventoryResult(true,"锁定失效，校验库存通过后重新锁定！");
				}
			}else if(lackStatus==1){//重新锁定规定的时间
				
				for (ProductInventoryOrderLock productInventoryOrderLock : inventoryLockList) {
					productInventoryOrderLock.setLastCheckTime(new Date());
					productInventoryOrderLockMapper.updateByPrimaryKeySelective(productInventoryOrderLock);
				}
				return new ProductCloudInventoryResult(true,"锁定未失效，已重新锁定生成锁定时间！");
			}
		}
		//是否缺货采购单
		if(order.getSaleOrderNo()!=null){//缺货采购单直接将锁库记录的销售单号更改为采购单号
			//查询以该销售单号锁定的库存
			ProductInventoryOrderLock inventoryOrderLackParam=new ProductInventoryOrderLock();
			inventoryOrderLackParam.setOrderNo(order.getSaleOrderNo());
			List<ProductInventoryOrderLock> inventoryOrderLackList = productInventoryOrderLockMapper.selectInventoryLockListByParams(inventoryOrderLackParam);
			if(inventoryOrderLackList==null || inventoryOrderLackList.size()<=0){
				return new ProductCloudInventoryResult(false,"云仓锁定不存在"); 
			}
			for (ProductInventoryOrderLock productInventoryOrderLock : inventoryOrderLackList) {
				productInventoryOrderLock.setOrderNo(order.getOrderNo());
				productInventoryOrderLockMapper.updateByPrimaryKeySelective(productInventoryOrderLock);
			}
			//保存订单信息
			order.setCreateTime(new Date());
			order.setStatus(0);
			orderMapper.insertSelective(order);
			for (OrderDetail orderDetail : orderDetailList) {
				orderDetail.setCreateTime(new Date());
				orderDetailMapper.insertSelective(orderDetail);
			}
			return new ProductCloudInventoryResult(true,"云仓锁定成功！"); 
		}
		//检查所有商品数量是否满足锁库
		List<Object> InventoryList=Lists.newArrayList();
		for (OrderDetail orderDetail : orderDetailList) {
			ProductCloudInventoryResult result = this.inventoryCheckByOrder(order,orderDetail);
			if(!result.isResult()){
				InventoryList.add(result.getObj());
				//临时锁定剩余的所有库存
				Order tempOrder=new Order();
				tempOrder.setOrderNo("LS"+order.getOrderNo());
				this.tempLockRemainInventoy(tempOrder,orderDetail);
				
			}else{//临时锁定库存校验通过的库存
				Order tempOrder=new Order();
				tempOrder.setOrderNo("LS"+order.getOrderNo());
				this.tempLockInventoy(tempOrder,orderDetail);
			}
		}
		
		//删除临时库存锁定记录
		productInventoryOrderLockMapper.deleteByOrderNo("LS"+order.getOrderNo());
		if(InventoryList.size()>0){//有商品库存不足
			return new ProductCloudInventoryResult(false,"存在商品库存不足！",InventoryList,null);
		}
		
		//保存订单信息
		order.setCreateTime(new Date());
		order.setStatus(0);
		orderMapper.insertSelective(order);
		for (OrderDetail orderDetail : orderDetailList) {
			//锁定库存
			this.inventoryLockByOrder(order,orderDetail);
			//新建 订单详情表： [{sku，qty，purchasePrice，isGift,warehouseId,warehouseName,imgurl,producttitle,orderNo},...]
			orderDetail.setCreateTime(new Date());
			orderDetailMapper.insertSelective(orderDetail);
		}
		return new ProductCloudInventoryResult(true,"已锁定商品库存");
	}

	/**
	 * 临时锁定库存中剩余的数量
	 * @param order
	 * @param orderDetail
	 * @return
	 */
	private ProductCloudInventoryResult tempLockRemainInventoy(Order order, OrderDetail orderDetail) {
		//先查总表数量
		ProductInventoryTotal inventoryTotalParam=new ProductInventoryTotal();
		inventoryTotalParam.setSku(orderDetail.getSku());
		inventoryTotalParam.setWarehouseId(orderDetail.getWarehouseId());
		ProductInventoryTotal inventoryTotalResult = productInventoryTotalMapper.selectByParam(inventoryTotalParam);
		if(inventoryTotalResult==null){
			inventoryTotalParam.setStock(orderDetail.getQty());
			return new ProductCloudInventoryResult(true,null);
		}
		int inventoryTotailNum = inventoryTotalResult.getStock().intValue();
		
		//有效被锁的数量
		ProductInventoryOrderLock inventoryOrderLockParam=new ProductInventoryOrderLock();
		inventoryOrderLockParam.setSku(orderDetail.getSku());
		inventoryOrderLockParam.setWarehouseId(orderDetail.getWarehouseId());
		inventoryOrderLockParam.setIsEffective((short) 1);//查询时为1  或者 -1
		List<ProductInventoryOrderLock> inventoryOrderLockList =  productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
		int inventoryLockNum=0;
		if(inventoryOrderLockList !=null && inventoryOrderLockList.size()>0){
			for(ProductInventoryOrderLock inventoryOrderLock:inventoryOrderLockList){
				inventoryLockNum+=inventoryOrderLock.getStockLocked();
			}
		}
		
		if(inventoryTotailNum-inventoryLockNum < orderDetail.getQty()){//云仓总可用量不够
			int remain=inventoryTotailNum-inventoryLockNum;
			ProductInventoryOrderLock inventoryOrderLock=new ProductInventoryOrderLock();
			inventoryOrderLock.setSku(orderDetail.getSku());
			inventoryOrderLock.setWarehouseId(orderDetail.getWarehouseId());
			inventoryOrderLock.setWarehouseName(orderDetail.getWarehouseName());
			inventoryOrderLock.setStockLocked(remain);
			inventoryOrderLock.setOrderNo(order.getOrderNo());
			inventoryOrderLock.setIsEffective((short) 1);
			inventoryOrderLock.setCreateTime(new Date());
			productInventoryOrderLockMapper.insertSelective(inventoryOrderLock);
			//更新总仓可用量
			//this.deductCloudTotalResidueNumByLockInfo(inventoryOrderLock);
			return new ProductCloudInventoryResult(true,null);
		}
		if(orderDetail.getExpirationDate() !=null){//订单指定了过期日期
			ProductInventoryDetail param=new ProductInventoryDetail();
			param.setSku(orderDetail.getSku());
			param.setWarehouseId(orderDetail.getWarehouseId());
			param.setExpirationDate(orderDetail.getExpirationDate());
			
			ProductInventoryDetail inventoryDetail = productInventoryDetailMapper.selectByParam(param);
			if(inventoryDetail==null || inventoryDetail.getStock()<=0){
				ProductInventoryDetail resultProductInventoryDetail = new ProductInventoryDetail();
				resultProductInventoryDetail.setStock(orderDetail.getQty());
				resultProductInventoryDetail.setSku(orderDetail.getSku());
				resultProductInventoryDetail.setWarehouseId(orderDetail.getWarehouseId());
				resultProductInventoryDetail.setWarehouseName(orderDetail.getWarehouseName());
				return new ProductCloudInventoryResult(false,null,null,resultProductInventoryDetail);
			}
			Integer expriationTotalStock = inventoryDetail.getStock();
			//查询该过期日期锁定的数量
			inventoryOrderLockParam.setExpirationDate(orderDetail.getExpirationDate());
			List<ProductInventoryOrderLock> inventoryExpirationOrderLockList =  productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
			int inventoryExpirationLockNum=0;
			if(inventoryExpirationOrderLockList !=null && inventoryExpirationOrderLockList.size()>0){
				for(ProductInventoryOrderLock inventoryExpriationOrderLock:inventoryExpirationOrderLockList){
					inventoryExpirationLockNum+=inventoryExpriationOrderLock.getStockLocked();
				}
			}
			if(expriationTotalStock-inventoryExpirationLockNum<orderDetail.getQty()){
				int remain=expriationTotalStock-inventoryExpirationLockNum;
				
				ProductInventoryOrderLock inventoryOrderLock=new ProductInventoryOrderLock();
				inventoryOrderLock.setSku(orderDetail.getSku());
				inventoryOrderLock.setWarehouseId(orderDetail.getWarehouseId());
				inventoryOrderLock.setWarehouseName(orderDetail.getWarehouseName());
				inventoryOrderLock.setStockLocked(remain);
				inventoryOrderLock.setOrderNo(order.getOrderNo());
				inventoryOrderLock.setIsEffective((short) 1);
				inventoryOrderLock.setCreateTime(new Date());
				productInventoryOrderLockMapper.insertSelective(inventoryOrderLock);
				//更新总仓可用量
				//this.deductCloudTotalResidueNumByLockInfo(inventoryOrderLock);
				return new ProductCloudInventoryResult(true,null);
			}
		}
		
		return new ProductCloudInventoryResult(true,null,null,null);
		
	}

	/**
	 * 临时锁定库存校验通过的订单
	 * @param order
	 * @param orderDetail
	 * @return
	 */
	private ProductCloudInventoryResult tempLockInventoy(Order order, OrderDetail orderDetail) {
		
		ProductInventoryOrderLock inventoryOrderLock=new ProductInventoryOrderLock();
		inventoryOrderLock.setSku(orderDetail.getSku());
		inventoryOrderLock.setWarehouseId(orderDetail.getWarehouseId());
		if(orderDetail.getExpirationDate() != null){
			inventoryOrderLock.setExpirationDate(orderDetail.getExpirationDate());
		}
		inventoryOrderLock.setWarehouseName(orderDetail.getWarehouseName());
		inventoryOrderLock.setStockLocked(orderDetail.getQty());
		inventoryOrderLock.setOrderNo(order.getOrderNo());
		inventoryOrderLock.setIsEffective((short) 1);
		inventoryOrderLock.setCreateTime(new Date());
		productInventoryOrderLockMapper.insertSelective(inventoryOrderLock);
		//更新总仓可用量
		//this.deductCloudTotalResidueNumByLockInfo(inventoryOrderLock);
		return new ProductCloudInventoryResult(true,"采购单云仓锁库成功！");
	}

	@Override
	public ProductCloudInventoryResult reSaveOrderDetail(Order order, List<OrderDetail> orderDetailReList) {
		try {
			for (OrderDetail orderDetail : orderDetailReList) {
				orderDetail.setCreateTime(new Date());
				orderDetailMapper.insertSelective(orderDetail);	
			}
			return new ProductCloudInventoryResult(true,"采购订单["+order.getOrderNo()+"]新增商品锁库成功");
		} catch (Exception e) {
			Logger.info("reSaveOrderDetailError[{}]", e);
			return new ProductCloudInventoryResult(false,"采购订单["+order.getOrderNo()+"]订单详情更新发生异常！");
		}
	}
	
	/**
	 * 将采购订单锁更新为不可失效
	 */
	@Override
	public ProductCloudInventoryResult changeLockToEffective(Order orderResult) {
		Order orderCheck=orderMapper.selectOrderByParams(orderResult);
		if(orderCheck==null){
			return new ProductCloudInventoryResult(false,"该订单不存在！");
		}
		String orderNo = orderCheck.getOrderNo();
		ProductInventoryOrderLock inventoryLockParam=new ProductInventoryOrderLock();
		inventoryLockParam.setOrderNo(orderNo);
		List<ProductInventoryOrderLock> inventoryLockList=productInventoryOrderLockMapper.selectInventoryLockListByParams(inventoryLockParam);
		if(inventoryLockList==null ||inventoryLockList.size()<=0){
			return new ProductCloudInventoryResult(false,"该订单无锁定记录");
		}
		for (ProductInventoryOrderLock productInventoryOrderLock : inventoryLockList) {
			productInventoryOrderLock.setIsEffective((short) -1);
			productInventoryOrderLock.setUpdateTime(new Date());
			productInventoryOrderLockMapper.updateByPrimaryKeySelective(productInventoryOrderLock);
		}
		return new ProductCloudInventoryResult(true,"订单锁已更新为不可失效");
	}
	
	/**
	 * 采购订单取消
	 */
	@Override
	public ProductCloudInventoryResult orderCanceled(Order order, List<OrderDetail> orderDetailList) {
		if(order ==null){
			return new ProductCloudInventoryResult(false,"请检查订单是否存在");
		}	
		ProductInventoryOrderLock inventoryLockParam=new ProductInventoryOrderLock();
		inventoryLockParam.setOrderNo(order.getOrderNo());
		List<ProductInventoryOrderLock> inventoryLockList = productInventoryOrderLockMapper.selectInventoryLockListByParams(inventoryLockParam);
		if(inventoryLockList==null || inventoryLockList.size()<=0){
			return new ProductCloudInventoryResult(true,"订单号["+order.getOrderNo()+"]无有效商品锁库记录！");
		}
		for (ProductInventoryOrderLock productInventoryOrderLock : inventoryLockList) {
			productInventoryOrderLock.setIsEffective((short) 0);
			productInventoryOrderLock.setUpdateTime(new Date());
			productInventoryOrderLockMapper.updateByPrimaryKeySelective(productInventoryOrderLock);
			//云仓总仓可用数量增加
			//this.addCloudTotalResidueNumByLockInfo(productInventoryOrderLock);
		}
		return new ProductCloudInventoryResult(true,"库存释放成功");
	}
	
	/**
	 * 采购单支付成功处理：
	 * 普通采购单：云仓库存，云仓总仓扣减，微仓入库，微仓总仓入库，入库记录
	 * 缺货采购单：云仓库存，云仓总仓扣减，微仓入库，微仓总仓入库，入库记录
	 * 	       微仓库存扣减，微仓总仓扣减，出库记录。入库记录剩余量修改。
	 */
	@Override
	@Transactional
	public ProductCloudInventoryResult updateStockByPurchaseOrder(Order order, List<OrderDetail> orderDetailList) {
		if(order==null || orderDetailList.size()<=0){
			return new ProductCloudInventoryResult(false,"请检查订单号是否正确。");
		}
		if(order.getStatus()!=null && order.getStatus()!=0){
			return new ProductCloudInventoryResult(false,"订单已付款或已取消");
		}
		//采购入库
		ProductInventoryOrderLock inventoryOrderLockParam=new ProductInventoryOrderLock();
		inventoryOrderLockParam.setOrderNo(order.getOrderNo());
		inventoryOrderLockParam.setIsEffective((short) 1);
		List<ProductInventoryOrderLock> inventoryOrderLackList=productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
		if(inventoryOrderLackList==null ||inventoryOrderLackList.size()<=0){
			return new ProductCloudInventoryResult(false,"该订单无有效锁库记录！",null,null);
		}
		for (OrderDetail orderDetail : orderDetailList) {
			ProductCloudInventoryResult cloudDeductByLockRecordResult = this.cloudDeductByLockRecord(orderDetail);
			if(!cloudDeductByLockRecordResult.isResult()){
				Logger.info("云仓出库失败:{}",cloudDeductByLockRecordResult.toString());
				//将已出库至微仓的库存还原至云仓库存
				this.restoreCloudInventory(order);
				return new ProductCloudInventoryResult(false,"订单["+orderDetail.getOrderNo()+"]中sku["+orderDetail.getSku()+"]云仓库存不足");
			}
		}
		//使锁库失效
		for (ProductInventoryOrderLock productInventoryOrderLock : inventoryOrderLackList) {
			productInventoryOrderLock.setIsEffective((short) 0);
			productInventoryOrderLock.setUpdateTime(new Date());
			productInventoryOrderLockMapper.updateByPrimaryKeySelective(productInventoryOrderLock);
		}	
		order.setStatus(1);
		order.setUpdateTime(new Date());
		orderMapper.updateByPrimaryKeySelective(order);
		return new ProductCloudInventoryResult(true,"采购订单["+order.getOrderNo()+"]库存变更完成.");
	}
	
	/**
	 * 缺货采购进行微仓出库
	 */
	@Override
	public ProductCloudInventoryResult microOutOfSalesOrder(List<ProductMicroInventoryOrderLock> microLackList,
			List<ProductMicroInventoryInRecord> microInRecordList,String saleOrderNo) {
		if(microLackList!=null && microLackList.size()>0){
			for (ProductMicroInventoryOrderLock microLock : microLackList) {
				//出微仓锁定的
				ProductCloudInventoryResult microLockOutResult=  this.microOutFromMicroLock(microLock);
				if(!microLockOutResult.isResult()){
					return microLockOutResult;
				}
				//标记锁定失效
				microLock.setIsEffective((short) 0);
				microLock.setUpdateTime(new Date());
				productMicroInventoryOrderLockMapper.updateByPrimaryKeySelective(microLock);
			}
		}
		for (ProductMicroInventoryInRecord microInRecord : microInRecordList) {
			//出缺货采购单入库的商品
			ProductCloudInventoryResult microInRecordOutResult = this.microOutFromMicroInRecord(microInRecord,saleOrderNo);
			if(!microInRecordOutResult.isResult()){
				return microInRecordOutResult;
			}
		}
		//标记销售单出货成功
		Order order=orderMapper.selectOrderByParams(new Order(saleOrderNo));
		order.setStatus(4);
		order.setUpdateTime(new Date());
		orderMapper.updateByPrimaryKeySelective(order);
		return new ProductCloudInventoryResult(true,"缺货采购单["+saleOrderNo+"]库存扣减完成");
	}
	
	@Override
	public List<Object> checkInventoryTotalNum(Order order, List<OrderDetail> orderDetailList) {
		List<Object> inventoryList=Lists.newArrayList();
		for (OrderDetail orderDetail : orderDetailList) {
			ProductInventoryTotal inventoryTotalParam=new ProductInventoryTotal();
			inventoryTotalParam.setWarehouseId(orderDetail.getWarehouseId());
			inventoryTotalParam.setSku(orderDetail.getSku());
			
			ProductInventoryTotal inventoryTotalResult = productInventoryTotalMapper.selectByParam(inventoryTotalParam);
			if(inventoryTotalResult==null){
				inventoryTotalParam.setStock(orderDetail.getQty());
				inventoryList.add(inventoryTotalParam);
			}else{
				//有效被锁的数量
				ProductInventoryOrderLock inventoryOrderLockParam=new ProductInventoryOrderLock();
				inventoryOrderLockParam.setSku(orderDetail.getSku());
				inventoryOrderLockParam.setWarehouseId(orderDetail.getWarehouseId());
				inventoryOrderLockParam.setIsEffective((short) 1);
				List<ProductInventoryOrderLock> inventoryOrderLockList =  productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
				int inventoryLockNum=0;
				if(inventoryOrderLockList !=null && inventoryOrderLockList.size()>0){
					for(ProductInventoryOrderLock inventoryOrderLock:inventoryOrderLockList){
						inventoryLockNum+=inventoryOrderLock.getStockLocked();
					}
				}
				
				int availableNum= inventoryTotalResult.getStock()-inventoryLockNum;
				if(availableNum-orderDetail.getQty()<0){//总量不足
					inventoryTotalResult.setStock(orderDetail.getQty()-availableNum);
					inventoryList.add(inventoryTotalResult);
					//将剩余的库存临时锁定
					Order tempOrder = new Order();
					tempOrder.setOrderNo("LS"+order.getOrderNo());
					this.tempLockRemainInventoy(tempOrder,orderDetail);
					
					continue;
				}else if(orderDetail.getExpirationDate() !=null){
					ProductInventoryDetail param=new ProductInventoryDetail();
					param.setSku(orderDetail.getSku());
					param.setWarehouseId(orderDetail.getWarehouseId());
					param.setExpirationDate(orderDetail.getExpirationDate());
					
					ProductInventoryDetail inventoryDetail = productInventoryDetailMapper.selectByParam(param);
					if(inventoryDetail==null || inventoryDetail.getStock()<=0){
						ProductInventoryDetail tempDetail=new ProductInventoryDetail();
						tempDetail.setSku(orderDetail.getSku());
						tempDetail.setWarehouseId(orderDetail.getWarehouseId());
						tempDetail.setExpirationDate(orderDetail.getExpirationDate());
						tempDetail.setStock(orderDetail.getQty());
						inventoryList.add(tempDetail);
						continue;
					}
					Integer expriationTotalStock = inventoryDetail.getStock();
					//查询该过期日期锁定的数量
					inventoryOrderLockParam.setExpirationDate(orderDetail.getExpirationDate());
					List<ProductInventoryOrderLock> inventoryExpirationOrderLockList =  productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
					int inventoryExpirationLockNum=0;
					if(inventoryExpirationOrderLockList !=null && inventoryExpirationOrderLockList.size()>0){
						for(ProductInventoryOrderLock inventoryExpriationOrderLock:inventoryExpirationOrderLockList){
							inventoryExpirationLockNum+=inventoryExpriationOrderLock.getStockLocked();
						}
					}
					if(expriationTotalStock-inventoryExpirationLockNum<orderDetail.getQty()){
						int num=orderDetail.getQty()+inventoryExpirationLockNum-expriationTotalStock;
						inventoryDetail.setStock(num);
						inventoryList.add(inventoryDetail);
						
						//将剩余的库存临时锁定
						Order tempOrder = new Order();
						tempOrder.setOrderNo("LS"+order.getOrderNo());
						this.tempLockRemainInventoy(tempOrder,orderDetail);
						
					}else {
						//将库存临时锁定
						Order tempOrder = new Order();
						tempOrder.setOrderNo("LS"+order.getOrderNo());
						this.tempLockInventoy(tempOrder,orderDetail);
					}
				}else {
					//将库存临时锁定
					Order tempOrder = new Order();
					tempOrder.setOrderNo("LS"+order.getOrderNo());
					this.tempLockInventoy(tempOrder,orderDetail);
				}
			}
		}
		//删除临时锁定的库存
		productInventoryOrderLockMapper.deleteByOrderNo("LS"+order.getOrderNo());
		if(inventoryList.size()>0){
			return inventoryList;
		}
		
		//锁定增量
		for (OrderDetail orderDetail : orderDetailList) {
			ProductInventoryOrderLock inventoryOrderLock=new ProductInventoryOrderLock();
			inventoryOrderLock.setSku(orderDetail.getSku());
			inventoryOrderLock.setWarehouseId(orderDetail.getWarehouseId());
			if(orderDetail.getExpirationDate()!=null){
				inventoryOrderLock.setExpirationDate(orderDetail.getExpirationDate());
			}
			inventoryOrderLock.setWarehouseName(orderDetail.getWarehouseName());
			inventoryOrderLock.setStockLocked(orderDetail.getQty());
			inventoryOrderLock.setOrderNo(order.getOrderNo());
			inventoryOrderLock.setIsEffective((short) 1);
			inventoryOrderLock.setCreateTime(new Date());
			productInventoryOrderLockMapper.insertSelective(inventoryOrderLock);
			//扣减云仓总仓可用量
			//this.deductCloudTotalResidueNumByLockInfo(inventoryOrderLock);
		}
		
		return inventoryList;
	}
	
	/**
	 * 对缺货采购单对应的入仓记录进行出库
	 * @param microInRecord
	 * @param saleOrderNo 
	 * @return
	 */
	private ProductCloudInventoryResult microOutFromMicroInRecord(ProductMicroInventoryInRecord microInRecord, String saleOrderNo) {
		ProductMicroInventoryDetail microDetailParam=new ProductMicroInventoryDetail();
		microDetailParam.setSku(microInRecord.getSku());
		microDetailParam.setAccount(microInRecord.getAccount());
		microDetailParam.setWarehouseId(microInRecord.getWarehouseId());
		microDetailParam.setPurchasePrice(microInRecord.getPurchasePrice());
		microDetailParam.setCapfee(microInRecord.getCapfee());
		microDetailParam.setIsGift(microInRecord.getIsGift());
		microDetailParam.setExpirationDate(microInRecord.getExpirationDate());
		ProductMicroInventoryDetail microDetailForDeduct = microInventoryDetailMapper.selectByParam(microDetailParam);
		if(microDetailForDeduct==null || microDetailForDeduct.getStock()<0){
			return new ProductCloudInventoryResult(false,"请检查sku["+microInRecord.getSku()+"]在微仓详情中的数量");
		}
		
		microDetailForDeduct.setStock(microDetailForDeduct.getStock()-microInRecord.getQty());
		microDetailForDeduct.setUpdateTime(new Date());
		microInventoryDetailMapper.updateByPrimaryKeySelective(microDetailForDeduct);
		
		//微仓出仓记录
		OrderMicroInventoryDeductRecord microInventoryDeductRecord=new OrderMicroInventoryDeductRecord();
		microInventoryDeductRecord.setSku(microDetailForDeduct.getSku());
		microInventoryDeductRecord.setExpirationDate(microDetailForDeduct.getExpirationDate());
		microInventoryDeductRecord.setWarehouseId(microDetailForDeduct.getWarehouseId());
		microInventoryDeductRecord.setWarehouseName(microDetailForDeduct.getWarehouseName());
		microInventoryDeductRecord.setPurchasePrice(microDetailForDeduct.getPurchasePrice());
		microInventoryDeductRecord.setIsGift(microDetailForDeduct.getIsGift());
		microInventoryDeductRecord.setQty(microInRecord.getQty());
		microInventoryDeductRecord.setOrderNo(saleOrderNo);
		microInventoryDeductRecord.setAccount(microInRecord.getAccount());
		microInventoryDeductRecord.setCreateTime(new Date());
		microInventoryDeductRecord.setMicroInRecordId(microInRecord.getId());
		orderMicroInventoryDeductRecordMapper.insertSelective(microInventoryDeductRecord);
		
		//扣减微仓总仓
		ProductMicroInventoryTotal microTotalParam=new ProductMicroInventoryTotal();
		microTotalParam.setSku(microInRecord.getSku());
		//microTotal.setProductName(microInventory.getProductName());
		microTotalParam.setAccount(microInRecord.getAccount());
		microTotalParam.setWarehouseId(microInRecord.getWarehouseId());
		
		ProductMicroInventoryTotal resultMicroTotal = productMicroInventoryTotalMapper.selectByParam(microTotalParam);
		if(resultMicroTotal==null || resultMicroTotal.getStock()<0){
			//手动异常
			return new ProductCloudInventoryResult(false,"请检查sku["+microInRecord.getSku()+"]在账户["+microInRecord.getAccount()+"]微仓总仓中的数量");
		}
		resultMicroTotal.setStock(resultMicroTotal.getStock()-microInRecord.getQty());
//		resultMicroTotal.setResidueNum(resultMicroTotal.getResidueNum()-microInRecord.getQty());
		resultMicroTotal.setUpdateTime(new Date());
		productMicroInventoryTotalMapper.updateByPrimaryKeySelective(resultMicroTotal);
		
		//修改入仓记录中的剩余量
		microInRecord.setResidueNum(0);
		microInRecord.setUpdateTime(new Date());
		productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(microInRecord);
		return new ProductCloudInventoryResult(true,null);
	}

	
	/**
	 * 对微仓锁定的库存进行出库
	 * @param microInventoryForOut
	 * @param microLock
	 */
	private ProductCloudInventoryResult microOutFromMicroLock(ProductMicroInventoryOrderLock microLock) {
		
		ProductMicroInventoryDetail microDetailParam=new ProductMicroInventoryDetail();
		microDetailParam.setSku(microLock.getSku());
		microDetailParam.setAccount(microLock.getAccount());
		microDetailParam.setWarehouseId(microLock.getWarehouseId());
		microDetailParam.setPurchasePrice(microLock.getPurchasePrice());
		microDetailParam.setCapfee(microLock.getCapfee());
		microDetailParam.setIsGift(microLock.getIsGift());
		microDetailParam.setExpirationDate(microLock.getExpirationDate());
		
		ProductMicroInventoryDetail microDetailForDeduct = microInventoryDetailMapper.selectByParam(microDetailParam);
		if(microDetailForDeduct==null || microDetailForDeduct.getStock()<=0){
			return new ProductCloudInventoryResult(false,"请检查sku["+microLock.getSku()+"]在微仓详情中的剩余量");
		}
		microDetailForDeduct.setStock(microDetailForDeduct.getStock()-microLock.getStockLocked());
		microDetailForDeduct.setLockStock(microDetailForDeduct.getLockStock()-microLock.getStockLocked());
		microDetailForDeduct.setUpdateTime(new Date());
		microInventoryDetailMapper.updateByPrimaryKeySelective(microDetailForDeduct);
		
		
		//微仓出仓记录
		OrderMicroInventoryDeductRecord microInventoryDeductRecord=new OrderMicroInventoryDeductRecord();
		microInventoryDeductRecord.setSku(microDetailForDeduct.getSku());
		microInventoryDeductRecord.setExpirationDate(microDetailForDeduct.getExpirationDate());
		microInventoryDeductRecord.setWarehouseId(microDetailForDeduct.getWarehouseId());
		microInventoryDeductRecord.setWarehouseName(microDetailForDeduct.getWarehouseName());
		microInventoryDeductRecord.setPurchasePrice(microDetailForDeduct.getPurchasePrice());
		microInventoryDeductRecord.setIsGift(microDetailForDeduct.getIsGift());
		microInventoryDeductRecord.setQty(microLock.getStockLocked());
		microInventoryDeductRecord.setOrderNo(microLock.getOrderNo());
		microInventoryDeductRecord.setAccount(microLock.getAccount());
		microInventoryDeductRecord.setMicroInRecordId(microLock.getMicroInRecordId());
		microInventoryDeductRecord.setCreateTime(new Date());
		orderMicroInventoryDeductRecordMapper.insertSelective(microInventoryDeductRecord);
		
		//扣减微仓总仓
		ProductMicroInventoryTotal microTotalParam=new ProductMicroInventoryTotal();
		microTotalParam.setSku(microDetailForDeduct.getSku());
		//microTotal.setProductName(microInventory.getProductName());
		microTotalParam.setAccount(microDetailForDeduct.getAccount());
		microTotalParam.setWarehouseId(microDetailForDeduct.getWarehouseId());
		
		ProductMicroInventoryTotal resultMicoTotal = productMicroInventoryTotalMapper.selectByParam(microTotalParam);
		if(resultMicoTotal==null || resultMicoTotal.getStock()<0){
			//手动异常
			return new ProductCloudInventoryResult(false,"请检查sku["+microLock.getSku()+"]在微仓总中的剩余量");
		}
		resultMicoTotal.setStock(resultMicoTotal.getStock()-microLock.getStockLocked());
		resultMicoTotal.setUpdateTime(new Date());
		productMicroInventoryTotalMapper.updateByPrimaryKeySelective(resultMicoTotal);
		
		//修改入仓记录中的剩余量
		ProductMicroInventoryInRecord microInRecordResult= productMicroInventoryInRecordMapper.selectByPrimaryKey(microLock.getMicroInRecordId());
		if(microInRecordResult==null||microInRecordResult.getResidueNum()<microLock.getStockLocked()){
			return new ProductCloudInventoryResult(false,"请检查微仓入库记录id["+microLock.getMicroInRecordId()+"]中可锁定的数量");
		}
		microInRecordResult.setResidueNum(microInRecordResult.getResidueNum()-microLock.getStockLocked());
		microInRecordResult.setUpdateTime(new Date());
		productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(microInRecordResult);
		return new ProductCloudInventoryResult(true,"请检查sku["+microLock.getSku()+"]在微仓总中的剩余量");
	}

	/**
	 * 根据订单详情数量进行云仓库存扣减
	 * @param inventoryOrderLackResult
	 * @param orderDetail2 
	 */
	private ProductCloudInventoryResult cloudDeductByLockRecord(OrderDetail orderDetail) {
		int qty = orderDetail.getQty().intValue();
		
		String orderNo = orderDetail.getOrderNo();
		Order orderParam=new Order();
		orderParam.setOrderNo(orderNo);
		Order orderResult=orderMapper.selectOrderByParams(orderParam);
		
		List<ProductMicroInventoryDetail> microInventoryDetailList=Lists.newArrayList();
	
		ProductInventoryDetail inventoryDetailParam=new ProductInventoryDetail();
		inventoryDetailParam.setSku(orderDetail.getSku());
		inventoryDetailParam.setWarehouseId(orderDetail.getWarehouseId());
		if(orderDetail.getExpirationDate()!=null){
			inventoryDetailParam.setExpirationDate(orderDetail.getExpirationDate());
		}
		//云仓详情中库存大于0并且按过期时间升序排序
		synchronized (monitor) {
			List<ProductInventoryDetail> inventoryDetailListResult = productInventoryDetailMapper.selectinventoryDetailListByParam(inventoryDetailParam);
			if(inventoryDetailListResult==null ||inventoryDetailListResult.size()<=0){
				return new ProductCloudInventoryResult(false,"请检查sku["+orderDetail.getSku()+"]在云仓中的数量");
			}
			
			for (ProductInventoryDetail productInventoryDetail : inventoryDetailListResult) {
				int otherOrderLockExpirationNum=0;
				if(orderDetail.getExpirationDate() ==null){
					//查询除这个订单以外的该过期日期的锁定数量,当订单详情未指定到期日期，则云仓扣减时需要保证该到期日期剩余量满足其他指定到期日期的订单详情数量
					ProductInventoryOrderLock inventoryExpirationOrderLockParam=new ProductInventoryOrderLock();
					inventoryExpirationOrderLockParam.setOrderNo(orderDetail.getOrderNo());
					inventoryExpirationOrderLockParam.setSku(orderDetail.getSku());
					inventoryExpirationOrderLockParam.setWarehouseId(orderDetail.getWarehouseId());
					inventoryExpirationOrderLockParam.setIsEffective((short) 1);
					inventoryExpirationOrderLockParam.setExpirationDate(productInventoryDetail.getExpirationDate());
					List<ProductInventoryOrderLock> inventoryOrderLockList =  productInventoryOrderLockMapper.selectOtherOrderInventoryEffectiveLockListByParams(inventoryExpirationOrderLockParam);
					if(inventoryOrderLockList!=null&&inventoryOrderLockList.size()>0){
						for (ProductInventoryOrderLock lockRecord : inventoryOrderLockList) {
							otherOrderLockExpirationNum=lockRecord.getStockLocked();
						}
					}
				}
				int inventoryDetailStock = productInventoryDetail.getStock().intValue();
				//扣减完处于锁定中的商品后还剩余的可用量
				int tempInventoryDetailStock = productInventoryDetail.getStock().intValue()-otherOrderLockExpirationNum;
				if(tempInventoryDetailStock >= qty){
					//云仓详情扣减
					productInventoryDetail.setStock(inventoryDetailStock-qty);
					productInventoryDetail.setUpdateTime(new Date());
					productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetail);
					
					//总仓查询后扣减
					ProductInventoryTotal inventoryTotalDeductParam=new ProductInventoryTotal();
					inventoryTotalDeductParam.setSku(productInventoryDetail.getSku());
					//inventoryTotalDeductParam.setWarehouseName(productInventoryDetail.getWarehouseName());
					inventoryTotalDeductParam.setWarehouseId(productInventoryDetail.getWarehouseId());
					
					ProductInventoryTotal inventoryTotalDeductResult = productInventoryTotalMapper.selectByParam(inventoryTotalDeductParam);
					inventoryTotalDeductResult.setStock(inventoryTotalDeductResult.getStock()-qty);
					inventoryTotalDeductResult.setUpdateTime(new Date());
					productInventoryTotalMapper.updateByPrimaryKeySelective(inventoryTotalDeductResult);
					
					//出库记录
					OrderInventoryDeductRecord inventoryDeductRecord=new OrderInventoryDeductRecord();
					inventoryDeductRecord.setSku(productInventoryDetail.getSku());
					inventoryDeductRecord.setWarehouseId(productInventoryDetail.getWarehouseId());
					inventoryDeductRecord.setWarehouseName(productInventoryDetail.getWarehouseName());
					inventoryDeductRecord.setExpirationDate(productInventoryDetail.getExpirationDate());
					inventoryDeductRecord.setOrderNo(orderDetail.getOrderNo());
					inventoryDeductRecord.setQty(qty);
					inventoryDeductRecord.setCreateTime(new Date());
					orderInventoryDeductRecordMapper.insertSelective(inventoryDeductRecord);
					
					//微仓入库信息
					ProductMicroInventoryDetail microInventoryDetialForIn=new ProductMicroInventoryDetail();
					microInventoryDetialForIn.setSku(productInventoryDetail.getSku());
					microInventoryDetialForIn.setProductTitle(orderDetail.getProductTitle());
					microInventoryDetialForIn.setWarehouseId(productInventoryDetail.getWarehouseId());
					microInventoryDetialForIn.setWarehouseName(productInventoryDetail.getWarehouseName());
					microInventoryDetialForIn.setAccount(orderResult.getAccount());
					microInventoryDetialForIn.setIsGift(orderDetail.getIsGift());
					microInventoryDetialForIn.setPurchasePrice(orderDetail.getPurchasePrice());
					microInventoryDetialForIn.setCapfee(orderDetail.getCapfee());
					microInventoryDetialForIn.setExpirationDate(productInventoryDetail.getExpirationDate());
					microInventoryDetialForIn.setStock(qty);
					microInventoryDetialForIn.setAccountName(orderResult.getAccountName());
					microInventoryDetailList.add(microInventoryDetialForIn);
					break;
				}else{
					if(orderDetail.getExpirationDate()!=null){
						return new ProductCloudInventoryResult(false,"云仓出库失败,请检查sku["+orderDetail.getSku()+"],expirationDate["+orderDetail.getExpirationDate()+"]在云仓中的数量");
					}
					//
					productInventoryDetail.setStock(otherOrderLockExpirationNum);
					productInventoryDetail.setUpdateTime(new Date());
					productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetail);
					
					//总仓查询后扣减
					ProductInventoryTotal inventoryTotalDeductParam=new ProductInventoryTotal();
					inventoryTotalDeductParam.setSku(productInventoryDetail.getSku());
					inventoryTotalDeductParam.setWarehouseName(productInventoryDetail.getWarehouseName());
					
					ProductInventoryTotal inventoryTotalDeductResult = productInventoryTotalMapper.selectByParam(inventoryTotalDeductParam);
					inventoryTotalDeductResult.setStock(inventoryTotalDeductResult.getStock()-tempInventoryDetailStock);
					inventoryTotalDeductResult.setUpdateTime(new Date());
					productInventoryTotalMapper.updateByPrimaryKeySelective(inventoryTotalDeductResult);
					
					//微仓入库信息
					ProductMicroInventoryDetail microInventoryDetialForIn=new ProductMicroInventoryDetail();
					microInventoryDetialForIn.setSku(productInventoryDetail.getSku());
					microInventoryDetialForIn.setProductTitle(orderDetail.getProductTitle());
					microInventoryDetialForIn.setWarehouseId(productInventoryDetail.getWarehouseId());
					microInventoryDetialForIn.setWarehouseName(productInventoryDetail.getWarehouseName());
					microInventoryDetialForIn.setAccount(orderResult.getAccount());
					microInventoryDetialForIn.setIsGift(orderDetail.getIsGift());
					microInventoryDetialForIn.setPurchasePrice(orderDetail.getPurchasePrice());
					microInventoryDetialForIn.setCapfee(orderDetail.getCapfee());
					microInventoryDetialForIn.setExpirationDate(productInventoryDetail.getExpirationDate());
					microInventoryDetialForIn.setStock(tempInventoryDetailStock);
					microInventoryDetialForIn.setAccountName(orderResult.getAccountName());
					microInventoryDetailList.add(microInventoryDetialForIn);
					
					//云出库记录
					OrderInventoryDeductRecord inventoryDeductRecord=new OrderInventoryDeductRecord();
					inventoryDeductRecord.setSku(productInventoryDetail.getSku());
					inventoryDeductRecord.setWarehouseId(productInventoryDetail.getWarehouseId());
					inventoryDeductRecord.setWarehouseName(productInventoryDetail.getWarehouseName());
					inventoryDeductRecord.setExpirationDate(productInventoryDetail.getExpirationDate());
					inventoryDeductRecord.setOrderNo(orderDetail.getOrderNo());
					inventoryDeductRecord.setQty(tempInventoryDetailStock);
					inventoryDeductRecord.setCreateTime(new Date());
					orderInventoryDeductRecordMapper.insertSelective(inventoryDeductRecord);
					qty-=tempInventoryDetailStock;
				}
			}
		}
		// 入微仓
		ProductCloudInventoryResult flag= this.microInByPurchaseOrder(orderDetail,microInventoryDetailList);
		return flag;
	}

	
	/**
	 * 采购单微仓入库操作
	 * @param orderDetail  提供采购单号
	 * @param microInventoryDetailList  需要入库的商品信息
	 * @return
	 */
	private ProductCloudInventoryResult microInByPurchaseOrder(OrderDetail orderDetail,List<ProductMicroInventoryDetail> microInventoryDetailList) {
		String orderNo = orderDetail.getOrderNo();
		Order orderParam=new Order();
		orderParam.setOrderNo(orderNo);
		Order orderResult=orderMapper.selectOrderByParams(orderParam);
		for (ProductMicroInventoryDetail productMicroInventoryDetail : microInventoryDetailList) {
			ProductMicroInventoryDetail microInventoryDetailParam=new ProductMicroInventoryDetail();
			microInventoryDetailParam.setSku(productMicroInventoryDetail.getSku());
			microInventoryDetailParam.setAccount(orderResult.getAccount());
			microInventoryDetailParam.setExpirationDate(productMicroInventoryDetail.getExpirationDate());
			microInventoryDetailParam.setIsGift(productMicroInventoryDetail.getIsGift());
			microInventoryDetailParam.setPurchasePrice(productMicroInventoryDetail.getPurchasePrice());
			microInventoryDetailParam.setCapfee(productMicroInventoryDetail.getCapfee());
			microInventoryDetailParam.setWarehouseId(productMicroInventoryDetail.getWarehouseId());
			Logger.info("微仓详情查询条件{}", microInventoryDetailParam.toString());
			ProductMicroInventoryDetail microInventoryDetailResult = microInventoryDetailMapper.selectByParam(microInventoryDetailParam);
			synchronized (monitor) {
				if(microInventoryDetailResult!=null){
					microInventoryDetailResult.setStock(microInventoryDetailResult.getStock()+productMicroInventoryDetail.getStock());
					microInventoryDetailResult.setUpdateTime(new Date());
					microInventoryDetailMapper.updateByPrimaryKeySelective(microInventoryDetailResult);
					//微仓总仓+
					ProductMicroInventoryTotal microInventoryTotal=new ProductMicroInventoryTotal();
					microInventoryTotal.setSku(productMicroInventoryDetail.getSku());
					microInventoryTotal.setAccount(productMicroInventoryDetail.getAccount());
					microInventoryTotal.setWarehouseId(productMicroInventoryDetail.getWarehouseId());
					microInventoryTotal.setStock(productMicroInventoryDetail.getStock());
//					microInventoryTotal.setResidueNum(productMicroInventoryDetail.getStock());
					microInventoryTotal.setCategoryId(orderDetail.getCategoryId());
					microInventoryTotal.setCategoryName(orderDetail.getCategoryName());
					microInventoryTotal.setUpdateTime(new Date());
					productMicroInventoryTotalMapper.updateMicroTotalStockNum(microInventoryTotal);
					
					//入仓记录
					ProductMicroInventoryInRecord microInventoryInRecord=new ProductMicroInventoryInRecord();
					microInventoryInRecord.setAccount(productMicroInventoryDetail.getAccount());
					microInventoryInRecord.setSku(productMicroInventoryDetail.getSku());
					microInventoryInRecord.setImgUrl(orderDetail.getImgUrl());
					microInventoryInRecord.setProductTitle(orderDetail.getProductTitle());
					microInventoryInRecord.setExpirationDate(productMicroInventoryDetail.getExpirationDate());
					microInventoryInRecord.setPurchaseTime(new Date());
					microInventoryInRecord.setResidueNum(productMicroInventoryDetail.getStock());
					microInventoryInRecord.setOrderNo(orderNo);
					microInventoryInRecord.setIsGift(productMicroInventoryDetail.getIsGift());
					microInventoryInRecord.setWarehouseId(productMicroInventoryDetail.getWarehouseId());
					microInventoryInRecord.setWarehouseName(productMicroInventoryDetail.getWarehouseName());
					microInventoryInRecord.setQty(productMicroInventoryDetail.getStock());
					microInventoryInRecord.setPurchasePrice(productMicroInventoryDetail.getPurchasePrice());
					microInventoryInRecord.setCapfee(orderDetail.getCapfee());
					microInventoryInRecord.setArriveWarePrice(orderDetail.getArriveWarePrice());
					microInventoryInRecord.setContractNo(orderDetail.getContractNo());
					microInventoryInRecord.setClearancePrice(orderDetail.getClearancePrice());
					microInventoryInRecord.setCreateTime(new Date());
					productMicroInventoryInRecordMapper.insertSelective(microInventoryInRecord);
				}else{
					productMicroInventoryDetail.setCreateTime(new Date());
					microInventoryDetailMapper.insertSelective(productMicroInventoryDetail);
					
					//微仓总仓+
					ProductMicroInventoryTotal microInventoryTotalParam=new ProductMicroInventoryTotal();
					microInventoryTotalParam.setSku(productMicroInventoryDetail.getSku());
					microInventoryTotalParam.setAccount(productMicroInventoryDetail.getAccount());
					microInventoryTotalParam.setWarehouseId(productMicroInventoryDetail.getWarehouseId());
	
					
					ProductMicroInventoryTotal inventoryTotalResult = productMicroInventoryTotalMapper.selectByParam(microInventoryTotalParam);
					if(inventoryTotalResult !=null){
						inventoryTotalResult.setStock(inventoryTotalResult.getStock()+productMicroInventoryDetail.getStock());
//						inventoryTotalResult.setResidueNum(inventoryTotalResult.getResidueNum()+productMicroInventoryDetail.getStock());
						inventoryTotalResult.setUpdateTime(new Date());
						productMicroInventoryTotalMapper.updateByPrimaryKeySelective(inventoryTotalResult);
					}else{
						microInventoryTotalParam.setProductTitle(productMicroInventoryDetail.getProductTitle());
						microInventoryTotalParam.setStock(productMicroInventoryDetail.getStock());
//						microInventoryTotalParam.setResidueNum(productMicroInventoryDetail.getStock());
						microInventoryTotalParam.setWarehouseName(productMicroInventoryDetail.getWarehouseName());
						microInventoryTotalParam.setCategoryId(orderDetail.getCategoryId());
						microInventoryTotalParam.setCategoryName(orderDetail.getCategoryName());
						microInventoryTotalParam.setCreateTime(new Date());
						productMicroInventoryTotalMapper.insertSelective(microInventoryTotalParam);
					}
					//入仓记录
					ProductMicroInventoryInRecord microInventoryInRecord=new ProductMicroInventoryInRecord();
					microInventoryInRecord.setAccount(productMicroInventoryDetail.getAccount());
					microInventoryInRecord.setSku(productMicroInventoryDetail.getSku());
					microInventoryInRecord.setImgUrl(orderDetail.getImgUrl());
					microInventoryInRecord.setProductTitle(orderDetail.getProductTitle());
					microInventoryInRecord.setExpirationDate(productMicroInventoryDetail.getExpirationDate());
					microInventoryInRecord.setPurchaseTime(new Date());
					microInventoryInRecord.setResidueNum(productMicroInventoryDetail.getStock());
					microInventoryInRecord.setOrderNo(orderNo);
					microInventoryInRecord.setIsGift(productMicroInventoryDetail.getIsGift());
					microInventoryInRecord.setWarehouseId(productMicroInventoryDetail.getWarehouseId());
					microInventoryInRecord.setWarehouseName(productMicroInventoryDetail.getWarehouseName());
					microInventoryInRecord.setQty(productMicroInventoryDetail.getStock());
					microInventoryInRecord.setPurchasePrice(productMicroInventoryDetail.getPurchasePrice());
					microInventoryInRecord.setCapfee(orderDetail.getCapfee());
					microInventoryInRecord.setArriveWarePrice(orderDetail.getArriveWarePrice());
					microInventoryInRecord.setContractNo(orderDetail.getContractNo());
					microInventoryInRecord.setClearancePrice(orderDetail.getClearancePrice());
					microInventoryInRecord.setCreateTime(new Date());
					productMicroInventoryInRecordMapper.insertSelective(microInventoryInRecord);
				}
			}
		}
		return new ProductCloudInventoryResult(true,"sku["+orderDetail.getSku()+"]已入微仓account["+orderDetail.getQty()+"]个");
	}
	
	/**
	 * 订单进行云仓出库到微仓库存失败（有商品库存不足）
	 * 将已出库到微仓的商品还原至云仓
	 * 删除该订单生成的微仓入库记录
	 * @param order
	 */
	private void restoreCloudInventory(Order order) {
		if(order.getOrderNo()==null){
			return;
		}
		ProductMicroInventoryInRecord microInRecordParam=new ProductMicroInventoryInRecord();
		microInRecordParam.setOrderNo(order.getOrderNo());
		List<ProductMicroInventoryInRecord> microInRecordResultLists = productMicroInventoryInRecordMapper.selectMicroInRecordListByParam(microInRecordParam);
		if(microInRecordResultLists.isEmpty()){
			return;
		}
		for(ProductMicroInventoryInRecord tempMicroInRecord:microInRecordResultLists){
			int temMicroInRecordNum = tempMicroInRecord.getQty().intValue();
			//修改微仓明细数量
			ProductMicroInventoryDetail microDetailParam= new ProductMicroInventoryDetail(tempMicroInRecord);
			ProductMicroInventoryDetail microDetailResult = microInventoryDetailMapper.selectByParam(microDetailParam);
			int tempmicroDetailStock = microDetailResult.getStock().intValue();
			if(tempmicroDetailStock>=temMicroInRecordNum){
				microDetailResult.setStock(tempmicroDetailStock-temMicroInRecordNum);
			}else{
				Logger.error("采购单库存变更失败,删除微仓入库记录存在异常,订单[{}},异常微仓明细数据[{}]", order.toString(),microDetailResult.toString());
				microDetailResult.setStock(0);
			}
			microInventoryDetailMapper.updateByPrimaryKeySelective(microDetailResult);
			//修改微仓总仓数量
			ProductMicroInventoryTotal microTotalParam=new ProductMicroInventoryTotal(tempMicroInRecord);
			ProductMicroInventoryTotal microTotalResult = productMicroInventoryTotalMapper.selectByParam(microTotalParam);
			int tempMicroTotalStock = microTotalResult.getStock().intValue();
			if(tempMicroTotalStock>=temMicroInRecordNum){
				microTotalResult.setStock(tempMicroTotalStock-temMicroInRecordNum);
			}else{
				Logger.error("采购单库存变更失败,删除微仓入库记录存在异常,订单[{}},异常微仓总仓数据[{}]", order.toString(),microTotalResult.toString());
				microTotalResult.setStock(0);
			}
			productMicroInventoryTotalMapper.updateByPrimaryKeySelective(microTotalResult);
			//修改云仓明细数
			ProductInventoryDetail cloudDetailParam=new ProductInventoryDetail(tempMicroInRecord);
			ProductInventoryDetail cloudDetailResult = productInventoryDetailMapper.selectByParam(cloudDetailParam);
			cloudDetailResult.setStock(cloudDetailResult.getStock()+temMicroInRecordNum);
			productInventoryDetailMapper.updateByPrimaryKeySelective(cloudDetailResult);
			//修改云仓总仓数量
			ProductInventoryTotal cloudTotalParam=new ProductInventoryTotal(tempMicroInRecord);
			ProductInventoryTotal cloudTotalResult = productInventoryTotalMapper.selectByParam(cloudTotalParam);
			cloudTotalResult.setStock(cloudTotalResult.getStock()+temMicroInRecordNum);
			productInventoryTotalMapper.updateByPrimaryKeySelective(cloudTotalResult);
			//删除微仓入库记录
			productMicroInventoryInRecordMapper.deleteByPrimaryKey(tempMicroInRecord.getId());
		}
		//删除该订单产生的云仓出库记录
		orderInventoryDeductRecordMapper.deleteByOrderNo(order.getOrderNo());
	}

	/**
	 * 根据订单查询云仓总仓库存是否满足
	 * @param orderDetail
	 * @return
	 * @throws ParseException 
	 */
	private ProductCloudInventoryResult inventoryCheckByOrder(Order order,OrderDetail orderDetail){
		//先查总表数量
		ProductInventoryTotal inventoryTotalParam=new ProductInventoryTotal();
		inventoryTotalParam.setSku(orderDetail.getSku());
		inventoryTotalParam.setWarehouseId(orderDetail.getWarehouseId());
		ProductInventoryTotal inventoryTotalResult = productInventoryTotalMapper.selectByParam(inventoryTotalParam);
		if(inventoryTotalResult==null){
			inventoryTotalParam.setStock(orderDetail.getQty());
			return new ProductCloudInventoryResult(false,null,null,inventoryTotalParam);
		}
		int inventoryTotailNum = inventoryTotalResult.getStock().intValue();
		
		//有效被锁的数量
		ProductInventoryOrderLock inventoryOrderLockParam=new ProductInventoryOrderLock();
		inventoryOrderLockParam.setSku(orderDetail.getSku());
		inventoryOrderLockParam.setWarehouseId(orderDetail.getWarehouseId());
		inventoryOrderLockParam.setIsEffective((short) 1);//查询时为1  或者 -1
		List<ProductInventoryOrderLock> inventoryOrderLockList =  productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
		int inventoryLockNum=0;
		if(inventoryOrderLockList !=null && inventoryOrderLockList.size()>0){
			for(ProductInventoryOrderLock inventoryOrderLock:inventoryOrderLockList){
				inventoryLockNum+=inventoryOrderLock.getStockLocked();
			}
		}
		
		if(inventoryTotailNum-inventoryLockNum < orderDetail.getQty()){//云仓总可用量不够
			int num=orderDetail.getQty()-inventoryTotailNum+inventoryLockNum;
			inventoryTotalResult.setStock(num);
			return new ProductCloudInventoryResult(false,null,null,inventoryTotalResult);
		}
		
		if(orderDetail.getExpirationDate() !=null){//订单指定了过期日期
			ProductInventoryDetail param=new ProductInventoryDetail();
			param.setSku(orderDetail.getSku());
			param.setWarehouseId(orderDetail.getWarehouseId());
			param.setExpirationDate(orderDetail.getExpirationDate());
			
			ProductInventoryDetail inventoryDetail = productInventoryDetailMapper.selectByParam(param);
			if(inventoryDetail==null || inventoryDetail.getStock()<=0){
				ProductInventoryDetail tempDetail=new ProductInventoryDetail();
				tempDetail.setWarehouseId(orderDetail.getWarehouseId());
				tempDetail.setSku(orderDetail.getSku());
				tempDetail.setExpirationDate(orderDetail.getExpirationDate());
				tempDetail.setStock(orderDetail.getQty());
				return new ProductCloudInventoryResult(false,null,null,tempDetail);
			}
			Integer expriationTotalStock = inventoryDetail.getStock();
			//查询该过期日期锁定的数量
			inventoryOrderLockParam.setExpirationDate(orderDetail.getExpirationDate());
			List<ProductInventoryOrderLock> inventoryExpirationOrderLockList =  productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
			int inventoryExpirationLockNum=0;
			if(inventoryExpirationOrderLockList !=null && inventoryExpirationOrderLockList.size()>0){
				for(ProductInventoryOrderLock inventoryExpriationOrderLock:inventoryExpirationOrderLockList){
					inventoryExpirationLockNum+=inventoryExpriationOrderLock.getStockLocked();
				}
			}
			if(expriationTotalStock-inventoryExpirationLockNum<orderDetail.getQty()){
				int num=orderDetail.getQty()+inventoryExpirationLockNum-expriationTotalStock;
				inventoryDetail.setStock(num);
				return new ProductCloudInventoryResult(false,null,null,inventoryDetail);
			}
		}
		
		return new ProductCloudInventoryResult(true,null,null,null);
	}
	
	/**
	 * 锁定云仓库存
	 * @param order
	 * @param orderDetail
	 * @return
	 */
	private ProductCloudInventoryResult inventoryLockByOrder(Order order, OrderDetail orderDetail) {
		

		ProductInventoryOrderLock inventoryOrderLock=new ProductInventoryOrderLock();
		inventoryOrderLock.setSku(orderDetail.getSku());
		inventoryOrderLock.setWarehouseId(orderDetail.getWarehouseId());
		if(orderDetail.getExpirationDate() != null){
			inventoryOrderLock.setExpirationDate(orderDetail.getExpirationDate());
		}
		inventoryOrderLock.setWarehouseName(orderDetail.getWarehouseName());
		inventoryOrderLock.setStockLocked(orderDetail.getQty());
		inventoryOrderLock.setOrderNo(order.getOrderNo());
		inventoryOrderLock.setIsEffective((short) 1);
		inventoryOrderLock.setLastCheckTime(new Date());
		inventoryOrderLock.setCreateTime(new Date());
		productInventoryOrderLockMapper.insertSelective(inventoryOrderLock);
		//更新总仓可用量
		//this.deductCloudTotalResidueNumByLockInfo(inventoryOrderLock);
		return new ProductCloudInventoryResult(true,"采购单云仓锁库成功！");
	}

	@Override
	public ProductCloudInventoryResult historyOrderDataClosed(HistoryOrderData historyOrderData) {
		List<HistoryDateOrderDetail> orderDetailList = historyOrderData.getOrderList();
		if(orderDetailList==null || orderDetailList.size()<=0){
			return new ProductCloudInventoryResult(false,"历史采购数据为空");
		}
		for (HistoryDateOrderDetail historyDateOrderDetail : orderDetailList) {
			//构造微仓入库记录
			ProductMicroInventoryInRecord inRecordParam=new ProductMicroInventoryInRecord();
			inRecordParam.setAccount(historyOrderData.getAccount());
			inRecordParam.setSku(historyDateOrderDetail.getSku());
			inRecordParam.setWarehouseId(historyDateOrderDetail.getWarehouseId());
			inRecordParam.setWarehouseName(historyDateOrderDetail.getWarehouseName());
			inRecordParam.setProductTitle(historyDateOrderDetail.getProductTitle());
			inRecordParam.setIsGift(historyDateOrderDetail.getIsGift());
			inRecordParam.setExpirationDate(historyOrderExpirationDate);
			inRecordParam.setArriveWarePrice(historyDateOrderDetail.getArriveWarePrice());
			inRecordParam.setCapfee(historyDateOrderDetail.getCapfee());
			inRecordParam.setPurchasePrice(historyDateOrderDetail.getPurchasePrice());
			inRecordParam.setOrderNo(historyDateOrderDetail.getOrderNo());
			ProductMicroInventoryInRecord inRecordResult= productMicroInventoryInRecordMapper.selectInRecordForHistoryOrderClosed(inRecordParam);
			if(inRecordResult!=null){
				inRecordResult.setQty(inRecordResult.getQty()+historyDateOrderDetail.getQty());
				inRecordResult.setResidueNum(inRecordResult.getResidueNum()+historyDateOrderDetail.getQty());
				inRecordResult.setUpdateTime(new Date());
				productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(inRecordResult);
				this.updateMicroInventoryByHistoryOrderClosed(historyDateOrderDetail,historyOrderData);
				this.updateCloudInventoryByHistoryOrderClosed(historyDateOrderDetail,historyOrderData);
			}else{
				inRecordParam.setQty(historyDateOrderDetail.getQty());
				inRecordParam.setImgUrl(historyDateOrderDetail.getImgUrl());
				inRecordParam.setResidueNum(historyDateOrderDetail.getQty());
				inRecordParam.setCreateTime(new Date());
				productMicroInventoryInRecordMapper.insertSelective(inRecordParam);
				this.updateMicroInventoryByHistoryOrderClosed(historyDateOrderDetail,historyOrderData);
				this.updateCloudInventoryByHistoryOrderClosed(historyDateOrderDetail,historyOrderData);
			}
		}
		return new ProductCloudInventoryResult(true,"");
	}

	/**
	 * 历史订单数据关闭扣减云仓
	 * @param historyDateOrderDetail
	 * @param historyOrderData
	 */
	private ProductCloudInventoryResult updateCloudInventoryByHistoryOrderClosed(HistoryDateOrderDetail historyDateOrderDetail,
			HistoryOrderData historyOrderData) {
		int qty=historyDateOrderDetail.getQty();
		ProductInventoryDetail inventoryDetailParam=new ProductInventoryDetail();
		inventoryDetailParam.setSku(historyDateOrderDetail.getSku());
		inventoryDetailParam.setWarehouseId(historyDateOrderDetail.getWarehouseId());
		//云仓详情中库存大于0并且按过期时间升序排序
		synchronized (monitor) {
			List<ProductInventoryDetail> inventoryDetailListResult = productInventoryDetailMapper.selectinventoryDetailListByParam(inventoryDetailParam);
			if(inventoryDetailListResult==null ||inventoryDetailListResult.size()<=0){
				return new ProductCloudInventoryResult(false,"请检查sku["+historyDateOrderDetail.getSku()+"]在云仓中的数量");
			}
			
			for (ProductInventoryDetail productInventoryDetail : inventoryDetailListResult) {
				int inventoryDetailStockNum = productInventoryDetail.getStock().intValue();
				if(inventoryDetailStockNum >= qty){
					//云仓详情扣减
					productInventoryDetail.setStock(inventoryDetailStockNum-qty);
					productInventoryDetail.setUpdateTime(new Date());
					productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetail);
					
					//总仓查询后扣减
					ProductInventoryTotal inventoryTotalDeductParam=new ProductInventoryTotal();
					inventoryTotalDeductParam.setSku(productInventoryDetail.getSku());
					inventoryTotalDeductParam.setWarehouseName(productInventoryDetail.getWarehouseName());
					
					ProductInventoryTotal inventoryTotalDeductResult = productInventoryTotalMapper.selectByParam(inventoryTotalDeductParam);
					inventoryTotalDeductResult.setStock(inventoryTotalDeductResult.getStock()-qty);
					inventoryTotalDeductResult.setUpdateTime(new Date());
					productInventoryTotalMapper.updateByPrimaryKeySelective(inventoryTotalDeductResult);
					//出库记录
					OrderInventoryDeductRecord inventoryDeductRecord=new OrderInventoryDeductRecord();
					inventoryDeductRecord.setSku(productInventoryDetail.getSku());
					inventoryDeductRecord.setWarehouseId(productInventoryDetail.getWarehouseId());
					inventoryDeductRecord.setExpirationDate(productInventoryDetail.getExpirationDate());
					inventoryDeductRecord.setOrderNo(historyDateOrderDetail.getOrderNo());
					OrderInventoryDeductRecord inventoryDeductRecordResult=orderInventoryDeductRecordMapper.selectForDeductByHistoryOrderClosed(inventoryDeductRecord);
					if(inventoryDeductRecordResult!=null){
						inventoryDeductRecordResult.setQty(inventoryDeductRecordResult.getQty()+qty);
						inventoryDeductRecordResult.setUpdateTime(new Date());
						orderInventoryDeductRecordMapper.updateByPrimaryKeySelective(inventoryDeductRecordResult);
					}else{
						inventoryDeductRecord.setQty(qty);
						inventoryDeductRecord.setWarehouseName(productInventoryDetail.getWarehouseName());
						inventoryDeductRecord.setCreateTime(new Date());
						orderInventoryDeductRecordMapper.insertSelective(inventoryDeductRecord);
					}
					
					break;
				}else{
					productInventoryDetail.setStock(0);
					productInventoryDetail.setUpdateTime(new Date());
					productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetail);
					
					//总仓查询后扣减
					ProductInventoryTotal inventoryTotalDeductParam=new ProductInventoryTotal();
					inventoryTotalDeductParam.setSku(productInventoryDetail.getSku());
					inventoryTotalDeductParam.setWarehouseName(productInventoryDetail.getWarehouseName());
					
					ProductInventoryTotal inventoryTotalDeductResult = productInventoryTotalMapper.selectByParam(inventoryTotalDeductParam);
					inventoryTotalDeductResult.setStock(inventoryTotalDeductResult.getStock()-inventoryDetailStockNum);
					inventoryTotalDeductResult.setUpdateTime(new Date());
					productInventoryTotalMapper.updateByPrimaryKeySelective(inventoryTotalDeductResult);
					
					//云出库记录
					//出库记录
					OrderInventoryDeductRecord inventoryDeductRecord=new OrderInventoryDeductRecord();
					inventoryDeductRecord.setSku(productInventoryDetail.getSku());
					inventoryDeductRecord.setWarehouseId(productInventoryDetail.getWarehouseId());
					
					inventoryDeductRecord.setExpirationDate(productInventoryDetail.getExpirationDate());
					inventoryDeductRecord.setOrderNo(historyDateOrderDetail.getOrderNo());
					
					
					OrderInventoryDeductRecord inventoryDeductRecordResult=orderInventoryDeductRecordMapper.selectForDeductByHistoryOrderClosed(inventoryDeductRecord);
					if(inventoryDeductRecordResult!=null){
						inventoryDeductRecordResult.setQty(inventoryDeductRecordResult.getQty()+qty);
						inventoryDeductRecordResult.setUpdateTime(new Date());
						orderInventoryDeductRecordMapper.updateByPrimaryKeySelective(inventoryDeductRecordResult);
					}else{
						inventoryDeductRecord.setQty(qty);
						inventoryDeductRecord.setWarehouseName(productInventoryDetail.getWarehouseName());
						inventoryDeductRecord.setCreateTime(new Date());
						orderInventoryDeductRecordMapper.insertSelective(inventoryDeductRecord);
					}
					qty-=inventoryDetailStockNum;
				}
			}
		}
		return new ProductCloudInventoryResult(true,"",null,null);
	}

	/**
	 * 历史订单数据关闭增加微仓
	 * @param historyDateOrderDetail
	 * @param historyOrderData
	 */
	private void updateMicroInventoryByHistoryOrderClosed(HistoryDateOrderDetail historyDateOrderDetail,
			HistoryOrderData historyOrderData) {
		//查询微仓明细
		ProductMicroInventoryDetail param=new ProductMicroInventoryDetail();
		param.setAccount(historyOrderData.getAccount());
		param.setWarehouseId(historyDateOrderDetail.getWarehouseId());
		param.setSku(historyDateOrderDetail.getSku());
		param.setExpirationDate(historyOrderExpirationDate);
		param.setIsGift(historyDateOrderDetail.getIsGift());
		param.setCapfee(historyDateOrderDetail.getCapfee());
		param.setPurchasePrice(historyDateOrderDetail.getPurchasePrice());
		
		ProductMicroInventoryDetail result = microInventoryDetailMapper.selectByParam(param);
		if(result!=null){
			result.setStock(result.getStock()+historyDateOrderDetail.getQty());
			result.setUpdateTime(new Date());
			microInventoryDetailMapper.updateByPrimaryKeySelective(result);
			
			ProductMicroInventoryTotal microInventoryTotal=new ProductMicroInventoryTotal();
			microInventoryTotal.setSku(historyDateOrderDetail.getSku());
			microInventoryTotal.setAccount(historyOrderData.getAccount());
			microInventoryTotal.setWarehouseId(historyDateOrderDetail.getWarehouseId());
			microInventoryTotal.setStock(historyDateOrderDetail.getQty());
//			microInventoryTotal.setResidueNum(productMicroInventoryDetail.getStock());
			microInventoryTotal.setUpdateTime(new Date());
			productMicroInventoryTotalMapper.updateMicroTotalStockNum(microInventoryTotal);
		}else{
			param.setStock(historyDateOrderDetail.getQty());
			param.setWarehouseName(historyDateOrderDetail.getWarehouseName());
			param.setProductTitle(historyDateOrderDetail.getProductTitle());
			param.setLockStock(0);
			param.setCreateTime(new Date());
			microInventoryDetailMapper.insertSelective(param);
			
			ProductMicroInventoryTotal microInventoryTotalParam=new ProductMicroInventoryTotal();
			microInventoryTotalParam.setSku(historyDateOrderDetail.getSku());
			microInventoryTotalParam.setAccount(historyOrderData.getAccount());
			microInventoryTotalParam.setWarehouseId(historyDateOrderDetail.getWarehouseId());

			ProductMicroInventoryTotal inventoryTotalResult = productMicroInventoryTotalMapper.selectByParam(microInventoryTotalParam);
			if(inventoryTotalResult !=null){
				inventoryTotalResult.setStock(inventoryTotalResult.getStock()+historyDateOrderDetail.getQty());
//				inventoryTotalResult.setResidueNum(inventoryTotalResult.getResidueNum()+productMicroInventoryDetail.getStock());
				inventoryTotalResult.setUpdateTime(new Date());
				productMicroInventoryTotalMapper.updateByPrimaryKeySelective(inventoryTotalResult);
			}else{
				microInventoryTotalParam.setProductTitle(historyDateOrderDetail.getProductTitle());
				microInventoryTotalParam.setStock(historyDateOrderDetail.getQty());
//				microInventoryTotalParam.setResidueNum(productMicroInventoryDetail.getStock());
				microInventoryTotalParam.setWarehouseName(historyDateOrderDetail.getWarehouseName());
				microInventoryTotalParam.setCreateTime(new Date());
				productMicroInventoryTotalMapper.insertSelective(microInventoryTotalParam);
			}
		}
		
	}

	@Override
	public ProductCloudInventoryResult changeOrderDetailInfo(Order order, List<OrderDetail> orderDetailList) {
		List<OrderDetail> oldOrderDetailList=orderDetailMapper.selectOrderDetailListByOrderNo(order.getOrderNo());
		if(oldOrderDetailList!=null && oldOrderDetailList.size()>0){
			Logger.info("修改订单详情信息删除已有数据{}", oldOrderDetailList.toString());
			OrderDetail orderDetailParam = new OrderDetail();
			orderDetailParam.setOrderNo(order.getOrderNo());
			this.deleteAllOrderDetailDateByOrderNo(orderDetailParam);
		}else{
			return new ProductCloudInventoryResult(false,"采购订单{"+order.getOrderNo()+"}详情不存在！");
		}
		ProductCloudInventoryResult reSaveOrderDetail = this.reSaveOrderDetail(order, orderDetailList);
		if(reSaveOrderDetail.isResult()){
			return new ProductCloudInventoryResult(true,"采购订单{"+order.getOrderNo()+"}详情修改成功");
		}else{
			return new ProductCloudInventoryResult(false,"采购订单{"+order.getOrderNo()+"}发生异常");
		}
	}

	@Override
	public List<OrderDetail> getOrderDetailBySkuAndSaleOrderNo(String main) {
		JsonNode jsonNode = Json.parse(main);
		String sku = jsonNode.get("sku").asText();
		String saleOrderNo = jsonNode.get("saleOrderNo").asText();
		return orderDetailMapper.getOrderDetailBySkuAndSaleOrderNo(saleOrderNo, sku);
	}

	@Override
	public ProductMicroInventoryInRecord getPurchaseByPurchaseOrderNo(String s_main) {
		JsonNode jsonNode = Json.parse(s_main);
		String orderNo = jsonNode.get("orderNo").asText();
		ProductMicroInventoryInRecord productMicroInventoryInRecord = new ProductMicroInventoryInRecord();
		productMicroInventoryInRecord.setOrderNo(orderNo);
		Optional<ProductMicroInventoryInRecord> inventoryInRecordOptional = productMicroInventoryInRecordMapper.selectMicroInRecordListByParam(productMicroInventoryInRecord).stream().findAny();
		if(inventoryInRecordOptional.isPresent()) {
			return inventoryInRecordOptional.get();
		}
		return new ProductMicroInventoryInRecord();
	}

	@Override
	public List<OrderDetail> getOrderDetails(String orderNo) {
		return orderDetailMapper.selectOrderDetailListByOrderNo(orderNo);
	}

	/**
	 * 根据锁库信息扣减云仓总仓可用量
	 * @param inventoryLock
	 */
	/*private void deductCloudTotalResidueNumByLockInfo(ProductInventoryOrderLock inventoryLock) {
		ProductInventoryTotal param=new ProductInventoryTotal();
		param.setSku(inventoryLock.getSku());
		param.setWarehouseId(inventoryLock.getWarehouseId());
		ProductInventoryTotal result = productInventoryTotalMapper.selectByParam(param);
		result.setResidueNum(result.getResidueNum()-inventoryLock.getStockLocked());
		result.setUpdateTime(new Date());
		productInventoryTotalMapper.updateByPrimaryKeySelective(result);
	}*/
	
	/**
	 * 根据锁库信息增加云仓总仓可用量
	 * @param inventoryLock
	 */
	/*private void addCloudTotalResidueNumByLockInfo(ProductInventoryOrderLock inventoryLock) {
		ProductInventoryTotal param=new ProductInventoryTotal();
		param.setSku(inventoryLock.getSku());
		param.setWarehouseId(inventoryLock.getWarehouseId());
		ProductInventoryTotal result = productInventoryTotalMapper.selectByParam(param);
		result.setResidueNum(result.getResidueNum()+inventoryLock.getStockLocked());
		result.setUpdateTime(new Date());
		productInventoryTotalMapper.updateByPrimaryKeySelective(result);
	}*/

	/*@Override
	public void initResidueNum() {
		List<ProductInventoryTotal> list= productInventoryTotalMapper.getAll();
		if(list!=null){
			for (ProductInventoryTotal productInventoryTotal : list) {
				ProductInventoryOrderLock param=new ProductInventoryOrderLock();
				param.setWarehouseId(productInventoryTotal.getWarehouseId());
				param.setSku(productInventoryTotal.getSku());
				param.setIsEffective((short) 1);
				List<ProductInventoryOrderLock> result = productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(param);
				int lockNum=0;
				if(result != null || result.size()>0){
					for (ProductInventoryOrderLock productInventoryOrderLock : result) {
						lockNum+=productInventoryOrderLock.getStockLocked();
					}
				}
				productInventoryTotal.setResidueNum(productInventoryTotal.getStock()-lockNum);
				productInventoryTotal.setUpdateTime(new Date());
				productInventoryTotalMapper.updateByPrimaryKeySelective(productInventoryTotal);
			} 
		}
	}*/
	
}
