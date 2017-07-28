package services.product_inventory.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import constant.InventoryConstant;
import dto.product_inventory.*;
import dto.warehousing.MicroRealWarehouseDto;
import entity.product.ProductDisprice;
import entity.product_inventory.*;
import entity.warehousing.InventoryChangeHistory;
import entity.warehousing.MicroGoodsInventory;
import forms.warehousing.MicroGoodsInventoryForm;
import mapper.product.ProductDispriceMapper;
import mapper.product_inventory.*;
import mapper.warehousing.InventoryChangeHistoryMapper;
import mapper.warehousing.MicroGoodsInventoryMapper;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.mybatis.guice.transactional.Transactional;
import org.springframework.util.CollectionUtils;
import play.Logger;
import play.libs.Json;
import services.product_inventory.IProductMicroInventoryService;
import utils.inventory.DateUtils;
import utils.inventory.JsonCaseUtil;
import vo.inventory.Page;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class ProductMicroInventoryService implements IProductMicroInventoryService {

    @Inject
    ProductMicroInventoryDetailMapper productMicroInventoryDetailMapper;

    @Inject
    OrderMapper orderMapper;

    @Inject
    ProductMicroInventoryDetailMapper microInventoryDetailMapper;

    @Inject
    ProductMicroInventoryInRecordMapper productMicroInventoryInRecordMapper;

    @Inject
    ProductInventoryOrderLockMapper productInventoryOrderLockMapper;

    @Inject
    OrderMicroInventoryDeductRecordMapper orderMicroInventoryDeductRecordMapper;

    @Inject
    ProductMicroInventoryOrderLockMapper productMicroInventoryOrderLockMapper;

    @Inject
    ProductMicroInventoryTotalMapper productMicroInventoryTotalMapper;

	@Inject
	OrderDetailMapper orderDetailMapper;

	@Inject
	ProductInventoryDetailMapper productInventoryDetailMapper;

	@Inject
	ProductInventoryTotalMapper productInventoryTotalMapper;

	@Inject
	ProductInventoryInRecordMapper productInventoryInRecordMapper;

	@Inject
	OrderInventoryDeductRecordMapper orderInventoryDeductRecordMapper;


	private final Object monitor=new Object();

	@Inject
	InventoryChangeHistoryMapper inventoryChangeHistoryMapper;

	@Inject
	MicroGoodsInventoryMapper microGoodsInventoryMapper;

	@Inject
	ProductDispriceMapper productDispriceMapper;

	@Inject
	InventorySyncRecordMapper inventorySyncRecordMapper;
	
    /**
     * 创建终端订单
	 *
	 * {
	 *     		"account":"xxx",
	 *     		"warehouseId":"xxx",
	 *     		"warehouseId":"深圳仓",
	 *     		"pros":[
	 *     			{
	 *     				"sku":"xxx",
	 *     				"qty":"x",
	 *     				"isGift":1
	 *     			},...
	 *     		  ]
	 * }
     *
     * @param jsonDataStr
     * @return
     */
    @Override
    @Transactional
    public synchronized CreateSaleOrderResult createSaleOrderWithInventory(String jsonDataStr) {
		Logger.info(">>>>>>>>>>>>>>>>>>>createSaleOrderWithInventory>>postData:{}" ,jsonDataStr);
		CreateSaleOrderResult createSaleOrderResult = new CreateSaleOrderResult();
        JsonNode jsonNode = Json.parse(jsonDataStr);
		if (JsonCaseUtil.isNotEmpty(jsonNode, "purchaseNo")) {//判断是否包含营销单号
			String purchaseNo = jsonNode.get("purchaseNo").asText();//营销单号
			String account = jsonNode.get("account").asText();
			int warehouseId = jsonNode.get("warehouseId").asInt();
			String warehouseName = jsonNode.get("warehouseName").asText();
			String orderNo = jsonNode.get("orderNo").asText();
			Logger.info(">>>>>>>>>微仓出库根据营销单号出：marketingNo:{}", purchaseNo);

			if (StringUtils.isEmpty(purchaseNo)) {
				throw new RuntimeException("营销单号不能为空！");
			}

			Order orderParam = new Order();
			orderParam.setOrderNo(purchaseNo);
			Order order = orderMapper.selectOrderByParams(orderParam);
			if (order == null) {
				throw new RuntimeException("该营销单号没有对应的订单记录！");
			}

			if(order.getStatus() != 1) {//可能 4 或 5
				throw new RuntimeException("该营销单号已出库或出库后取消！");
			}

			//根据营销单号查找微仓入仓记录
			ProductMicroInventoryInRecord productMicroInventoryInRecordParam = new ProductMicroInventoryInRecord();
			productMicroInventoryInRecordParam.setOrderNo(purchaseNo);
			List<ProductMicroInventoryInRecord> productMicroInventoryInRecordList = productMicroInventoryInRecordMapper.selectMicroInRecordListByParam(productMicroInventoryInRecordParam);
			List<ShipingDto> shipingDtoList = Lists.newArrayList();

			if (productMicroInventoryInRecordList != null && productMicroInventoryInRecordList.size() > 0) {
				//生成订单记录
				this.generateOrder(orderNo, warehouseId, warehouseName, account, InventoryConstant.INVENTORY_ORDER_STATUS_SUCCESS);//status=4 正常出库
				for (ProductMicroInventoryInRecord productMicroInventoryInRecord : productMicroInventoryInRecordList) {
					//查询微仓明细数据
					ProductMicroInventoryDetail productMicroDetailParam = new ProductMicroInventoryDetail();
					productMicroDetailParam.setAccount(productMicroInventoryInRecord.getAccount());
					productMicroDetailParam.setExpirationDate(productMicroInventoryInRecord.getExpirationDate());
					productMicroDetailParam.setSku(productMicroInventoryInRecord.getSku());
					productMicroDetailParam.setWarehouseId(productMicroInventoryInRecord.getWarehouseId());
					productMicroDetailParam.setPurchasePrice(productMicroInventoryInRecord.getPurchasePrice());
					productMicroDetailParam.setCapfee(productMicroInventoryInRecord.getCapfee());
					productMicroDetailParam.setIsGift(productMicroInventoryInRecord.getIsGift());

					ProductMicroInventoryDetail productMicroInventoryDetail = productMicroInventoryDetailMapper.selectByParam(productMicroDetailParam);

					if(productMicroInventoryDetail == null) {
						throw new RuntimeException("营销单出库时查询微仓明细异常！");
					}

					if (productMicroInventoryInRecord.getResidueNum() > 0) {
						//生成微仓出库记录
						this.generateOrderMicroInventoryDeductRecord(productMicroInventoryInRecord.getWarehouseId(), productMicroInventoryInRecord.getWarehouseName(), orderNo, productMicroInventoryInRecord.getSku(), productMicroInventoryInRecord.getResidueNum(), productMicroInventoryInRecord.getExpirationDate(), productMicroInventoryInRecord.getAccount(), productMicroInventoryInRecord.getPurchasePrice(), productMicroInventoryInRecord.getId(), productMicroInventoryInRecord.getIsGift());
						shipingDtoList.add(new ShipingDto(productMicroInventoryInRecord, productMicroInventoryInRecord.getResidueNum()));
						//微仓明细减
						productMicroInventoryDetail.setStock(productMicroInventoryDetail.getStock() - productMicroInventoryInRecord.getResidueNum());
						productMicroInventoryDetail.setUpdateTime(new Date());
						productMicroInventoryDetailMapper.updateByPrimaryKeySelective(productMicroInventoryDetail);

						//微仓总计表扣减
						ProductMicroInventoryTotal productMicroInventoryTotalParam = new ProductMicroInventoryTotal();
						productMicroInventoryTotalParam.setWarehouseId(warehouseId);
						productMicroInventoryTotalParam.setSku(productMicroInventoryInRecord.getSku());
						productMicroInventoryTotalParam.setAccount(account);

						ProductMicroInventoryTotal productMicroInventoryTotal = productMicroInventoryTotalMapper.selectByParam(productMicroInventoryTotalParam);

						if (productMicroInventoryTotal == null) {
							throw new RuntimeException("营销单出库时查询微仓总仓异常！");
						}
						int reduceAfterNum = productMicroInventoryTotal.getStock() - productMicroInventoryInRecord.getResidueNum();
						productMicroInventoryTotal.setStock(reduceAfterNum);
						productMicroInventoryTotal.setUpdateTime(new Date());
//						productMicroInventoryTotal.setResidueNum(productMicroInventoryTotal.getResidueNum() - productMicroInventoryInRecord.getResidueNum());
						productMicroInventoryTotalMapper.updateByPrimaryKeySelective(productMicroInventoryTotal);

						productMicroInventoryInRecord.setResidueNum(0);
						productMicroInventoryInRecord.setUpdateTime(new Date());
						productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(productMicroInventoryInRecord);
					}
				}
			} else {
				throw new RuntimeException("该营销单号没有对应的微仓入仓记录！");
			}

			order.setStatus(4);//完成出库
			order.setUpdateTime(new Date());
			orderMapper.updateByPrimaryKeySelective(order);
			createSaleOrderResult.setType(InventoryConstant.SALE_ORDER_SUCCESS);//库存扣减成功
			createSaleOrderResult.setSuccessOrLocks(shipingDtoList);
			return createSaleOrderResult;
		}

		//是否直接去锁云仓
		if (jsonNode.has("lockCloud") && jsonNode.get("lockCloud").asBoolean()) {
			JsonNode details = jsonNode.get("pros");

			String account = jsonNode.get("account").asText();
			int warehouseId = jsonNode.get("warehouseId").asInt();
			String warehouseName = jsonNode.get("warehouseName").asText();
			String orderNo = jsonNode.get("orderNo").asText();

			int cloudMissingNum = 0;
			for (JsonNode detail : details) {
				String sku = detail.get("sku").asText();
				int shipmentNum = detail.get("qty").asInt();//数量

				if (detail.has("expirationDate") && !(detail.get("expirationDate") instanceof NullNode)) {
					Date expiredDate;
					try {
						expiredDate = DateUtils.string2date(detail.get("expirationDate").asText(), DateUtils.FORMAT_DATE_PAGE);
					} catch (ParseException e) {
						throw new RuntimeException("createSaleOrderWithInventory-------->日期格式错误！");
					}

					ProductInventoryDetail param = new ProductInventoryDetail();
					param.setSku(sku);
					param.setWarehouseId(warehouseId);
					param.setExpirationDate(expiredDate);

					ProductInventoryDetail inventoryDetail = productInventoryDetailMapper.selectByParam(param);
					if(inventoryDetail == null || inventoryDetail.getStock()<=0){
						cloudMissingNum++;
					}
					if(inventoryDetail !=null){
						Integer expriationTotalStock = inventoryDetail.getStock();
						//查询该过期日期锁定的数量
						//有效被锁的数量
						ProductInventoryOrderLock inventoryOrderLockParam = new ProductInventoryOrderLock();
						inventoryOrderLockParam.setSku(sku);
						inventoryOrderLockParam.setWarehouseId(warehouseId);
						inventoryOrderLockParam.setIsEffective((short) 1);//查询时为1  或者 -1
						inventoryOrderLockParam.setExpirationDate(expiredDate);
						List<ProductInventoryOrderLock> inventoryExpirationOrderLockList =  productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
						int inventoryExpirationLockNum = 0;
						if(inventoryExpirationOrderLockList !=null && inventoryExpirationOrderLockList.size()>0){
							for(ProductInventoryOrderLock inventoryExpriationOrderLock:inventoryExpirationOrderLockList){
								inventoryExpirationLockNum+=inventoryExpriationOrderLock.getStockLocked();
							}
						}
						if(expriationTotalStock - inventoryExpirationLockNum < shipmentNum){
							cloudMissingNum++;
						}
					}
				} else {
					//云仓
					ProductInventoryTotal productInventoryTotal = this.getProductInventoryTotal(warehouseId, sku);
					if(productInventoryTotal != null) {
						ProductInventoryDetail inventoryDetailParam = new ProductInventoryDetail();
						inventoryDetailParam.setSku(sku);
						inventoryDetailParam.setWarehouseId(warehouseId);

						List<ProductInventoryDetail> inventoryDetailListResult = productInventoryDetailMapper.selectinventoryDetailListByParam(inventoryDetailParam);
						if(inventoryDetailListResult == null ||inventoryDetailListResult.size()<= 0){
							throw new RuntimeException("执行云仓锁库请检查sku["+sku+"]在云仓中的数量");
						}

						//有效被锁的数量
						ProductInventoryOrderLock inventoryOrderLockParam = new ProductInventoryOrderLock();
						inventoryOrderLockParam.setSku(sku);
						inventoryOrderLockParam.setWarehouseId(warehouseId);
						inventoryOrderLockParam.setIsEffective((short) 1);//查询时为1  或者 -1
						inventoryOrderLockParam.setExpirationDate(inventoryDetailListResult.get(0).getExpirationDate());
						List<ProductInventoryOrderLock> inventoryOrderLockList = productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
						int inventoryLockNum = 0;
						if(inventoryOrderLockList != null && inventoryOrderLockList.size() > 0){
							for(ProductInventoryOrderLock inventoryOrderLock:inventoryOrderLockList){
								inventoryLockNum += inventoryOrderLock.getStockLocked();
							}
						}

						//云仓有效库存
						int effectiveNum = productInventoryTotal.getStock() - inventoryLockNum;

						if(effectiveNum < shipmentNum){
							cloudMissingNum++;
						}
					} else {
						cloudMissingNum++;
					}
				}
			}

			if (cloudMissingNum > 0) {
				createSaleOrderResult.setType(InventoryConstant.SALE_ORDER_OUT_OF_CLOUD_STOCK);//云仓也没货
				return createSaleOrderResult;
			} else {
				this.generateOrder(orderNo,warehouseId,warehouseName,account, InventoryConstant.INVENTORY_ORDER_STATUS_SALE_ORDER_TO_PURCHASE);//status=3 缺货
				for (JsonNode detail : details) {
					Date expiredDate = null;
					if (detail.has("expirationDate") && !(detail.get("expirationDate") instanceof NullNode)) {
						try {
							expiredDate = DateUtils.string2date(detail.get("expirationDate").asText(), DateUtils.FORMAT_DATE_PAGE);
						} catch (ParseException e) {
							throw new RuntimeException("createSaleOrderWithInventory-------->日期格式错误！");
						}
					}

					String sku = detail.get("sku").asText();
					int shipmentNum = detail.get("qty").asInt();//数量
					this.generateOrderDetail(orderNo, warehouseId, warehouseName, shipmentNum, sku, null, null, expiredDate);
					this.generateOrderLock(sku, warehouseId, warehouseName, shipmentNum, orderNo, expiredDate);//orderNo 销售单号
				}
			}

			createSaleOrderResult.setType(InventoryConstant.SALE_ORDER_LOCK_CLOUD_SUCCESS);//成功锁住云仓(只锁云仓)
			return createSaleOrderResult;
		}



        JsonNode details = jsonNode.get("pros");

		String account = jsonNode.get("account").asText();
		int warehouseId = jsonNode.get("warehouseId").asInt();
		String warehouseName = jsonNode.get("warehouseName").asText();
		String orderNo = jsonNode.get("orderNo").asText();


		List<ProductMicroAndCloudLockDto> productMicroAndCloudLockDtoList = Lists.newArrayList();//  出库/锁库 信息列表
		//检查锁库

		List<ProductMicroAndCloudLockDto> checkProductMicroAndCloudLockDtoList = Lists.newArrayList();
		for (JsonNode detail : details) {
			String sku = detail.get("sku").asText();
			int shipmentNum = detail.get("qty").asInt();//数量
			Date expiredDate = null;//过期日期
			if (detail.has("expirationDate") && !(detail.get("expirationDate") instanceof NullNode)) {//指定过期日期
				try {
					expiredDate = DateUtils.string2date(detail.get("expirationDate").asText(), DateUtils.FORMAT_DATE_PAGE);
				} catch (ParseException e) {
					throw new RuntimeException("createSaleOrderWithInventory-------->日期格式错误！");
				}
			}
			ProductMicroAndCloudLockDto productMicroAndCloudLockDto = new ProductMicroAndCloudLockDto();
			productMicroAndCloudLockDto.setSku(sku);
			productMicroAndCloudLockDto.setLockMicroStock(shipmentNum);
			productMicroAndCloudLockDto.setExpiredDate(expiredDate);
			checkProductMicroAndCloudLockDtoList.add(productMicroAndCloudLockDto);
		}

		Map<String, List<ProductMicroAndCloudLockDto>> groupBySkuMap = checkProductMicroAndCloudLockDtoList.stream().collect(groupingBy(ProductMicroAndCloudLockDto::getSku));
		for (Map.Entry<String, List<ProductMicroAndCloudLockDto>> skuMapEntry : groupBySkuMap.entrySet()) {
			String sku = skuMapEntry.getKey();
			List<ProductMicroAndCloudLockDto> productMicroAndCloudLockDtos = skuMapEntry.getValue().stream()
					.sorted(Comparator.comparing(ProductMicroAndCloudLockDto::getExpiredDate, Comparator.nullsLast(Comparator.naturalOrder()))).collect(toList());

			//微仓明细
			List<ProductMicroInventoryDetail> detailListGroupBySku = productMicroInventoryDetailMapper.selectProductMicroInventoryDetailsGroupByDate(account, sku);

			for (ProductMicroAndCloudLockDto productMicroAndCloudLockDto : productMicroAndCloudLockDtos) {
				boolean flag = false;
				if (productMicroAndCloudLockDto.getExpiredDate() != null) {
					for (ProductMicroInventoryDetail productMicroInventoryDetail : detailListGroupBySku) {
						if (isSameDate(productMicroAndCloudLockDto.getExpiredDate(),productMicroInventoryDetail.getExpirationDate())) {
							flag = true;
							break;
						}
					}

					if(flag) {
						for (ProductMicroInventoryDetail productMicroInventoryDetail: detailListGroupBySku) {
							if (productMicroInventoryDetail.getExpirationDate().equals(productMicroAndCloudLockDto.getExpiredDate())) {
								int reduce = productMicroInventoryDetail.getStock();//指定日期下微仓库存
								if (reduce == 0) {
									ProductMicroAndCloudLockDto toLockProductMicroAndCloudLockDto = new ProductMicroAndCloudLockDto();
									toLockProductMicroAndCloudLockDto.setSku(sku);
									toLockProductMicroAndCloudLockDto.setLockMicroStock(0);
									toLockProductMicroAndCloudLockDto.setLockCloudStock(productMicroAndCloudLockDto.getLockMicroStock());
									toLockProductMicroAndCloudLockDto.setExpiredDate(productMicroAndCloudLockDto.getExpiredDate());
									productMicroInventoryDetail.setStock(0);
									productMicroAndCloudLockDtoList.add(toLockProductMicroAndCloudLockDto);
								}
								if (productMicroAndCloudLockDto.getLockMicroStock() > reduce && reduce > 0) {
									ProductMicroAndCloudLockDto toLockProductMicroAndCloudLockDto = new ProductMicroAndCloudLockDto();
									toLockProductMicroAndCloudLockDto.setSku(sku);
									toLockProductMicroAndCloudLockDto.setLockMicroStock(reduce);
									toLockProductMicroAndCloudLockDto.setLockCloudStock(productMicroAndCloudLockDto.getLockMicroStock() - reduce);
									toLockProductMicroAndCloudLockDto.setExpiredDate(productMicroAndCloudLockDto.getExpiredDate());
									productMicroInventoryDetail.setStock(0);
									productMicroAndCloudLockDtoList.add(toLockProductMicroAndCloudLockDto);
								}

								if(productMicroAndCloudLockDto.getLockMicroStock() <= reduce && reduce > 0) {
									ProductMicroAndCloudLockDto toLockProductMicroAndCloudLockDto = new ProductMicroAndCloudLockDto();
									toLockProductMicroAndCloudLockDto.setSku(sku);
									toLockProductMicroAndCloudLockDto.setLockMicroStock(productMicroAndCloudLockDto.getLockMicroStock());
									toLockProductMicroAndCloudLockDto.setLockCloudStock(0);
									toLockProductMicroAndCloudLockDto.setExpiredDate(productMicroAndCloudLockDto.getExpiredDate());
									productMicroInventoryDetail.setStock(productMicroInventoryDetail.getStock() - productMicroAndCloudLockDto.getLockMicroStock());
									productMicroAndCloudLockDtoList.add(toLockProductMicroAndCloudLockDto);
								}
							}
						}
					}else {
						ProductMicroAndCloudLockDto toLockProductMicroAndCloudLockDto = new ProductMicroAndCloudLockDto();
						toLockProductMicroAndCloudLockDto.setSku(sku);
						toLockProductMicroAndCloudLockDto.setLockMicroStock(0);
						toLockProductMicroAndCloudLockDto.setLockCloudStock(productMicroAndCloudLockDto.getLockMicroStock());
						toLockProductMicroAndCloudLockDto.setExpiredDate(productMicroAndCloudLockDto.getExpiredDate());
						productMicroAndCloudLockDtoList.add(toLockProductMicroAndCloudLockDto);
					}
				} else {
					int num = detailListGroupBySku.stream().map(d -> d.getStock()).reduce(0, (n1,n2) -> (n1 + n2));
					if (num >= productMicroAndCloudLockDto.getLockMicroStock()) {//微仓够
						int reduceNum = productMicroAndCloudLockDto.getLockMicroStock();
						for (ProductMicroInventoryDetail productMicroInventoryDetail: detailListGroupBySku) {
							if(productMicroInventoryDetail.getStock() > 0) {
								if (productMicroInventoryDetail.getStock() >= reduceNum) {//日期维度下数量够
									ProductMicroAndCloudLockDto toLockProductMicroAndCloudLockDto = new ProductMicroAndCloudLockDto();
									toLockProductMicroAndCloudLockDto.setSku(sku);
									toLockProductMicroAndCloudLockDto.setLockMicroStock(reduceNum);
									toLockProductMicroAndCloudLockDto.setLockCloudStock(0);
									toLockProductMicroAndCloudLockDto.setExpiredDate(productMicroInventoryDetail.getExpirationDate());
									productMicroAndCloudLockDtoList.add(toLockProductMicroAndCloudLockDto);
									productMicroInventoryDetail.setStock(productMicroInventoryDetail.getStock() - reduceNum);
									break;
								} else {
									ProductMicroAndCloudLockDto toLockProductMicroAndCloudLockDto = new ProductMicroAndCloudLockDto();
									toLockProductMicroAndCloudLockDto.setSku(sku);
									toLockProductMicroAndCloudLockDto.setLockMicroStock(productMicroInventoryDetail.getStock());
									toLockProductMicroAndCloudLockDto.setLockCloudStock(0);
									toLockProductMicroAndCloudLockDto.setExpiredDate(productMicroInventoryDetail.getExpirationDate());
									productMicroAndCloudLockDtoList.add(toLockProductMicroAndCloudLockDto);
									reduceNum = reduceNum - productMicroInventoryDetail.getStock();
									productMicroInventoryDetail.setStock(0);
								}
							}
						}
					} else {//微仓不够
						for (ProductMicroInventoryDetail productMicroInventoryDetail: detailListGroupBySku) {
							if(productMicroInventoryDetail.getStock() > 0) {
								ProductMicroAndCloudLockDto toLockProductMicroAndCloudLockDto = new ProductMicroAndCloudLockDto();
								toLockProductMicroAndCloudLockDto.setSku(sku);
								toLockProductMicroAndCloudLockDto.setLockMicroStock(productMicroInventoryDetail.getStock());
								toLockProductMicroAndCloudLockDto.setLockCloudStock(0);
								toLockProductMicroAndCloudLockDto.setExpiredDate(productMicroInventoryDetail.getExpirationDate());
								productMicroAndCloudLockDtoList.add(toLockProductMicroAndCloudLockDto);
							}
						}
						ProductMicroAndCloudLockDto toLockProductMicroAndCloudLockDto = new ProductMicroAndCloudLockDto();
						toLockProductMicroAndCloudLockDto.setSku(sku);
						toLockProductMicroAndCloudLockDto.setLockMicroStock(0);
						toLockProductMicroAndCloudLockDto.setLockCloudStock(productMicroAndCloudLockDto.getLockMicroStock() - num);
						toLockProductMicroAndCloudLockDto.setExpiredDate(productMicroAndCloudLockDto.getExpiredDate());
						productMicroAndCloudLockDtoList.add(toLockProductMicroAndCloudLockDto);

					}
				}
			}
		}

		Logger.info("------------->info:{}", productMicroAndCloudLockDtoList);

		if (CollectionUtils.isEmpty(productMicroAndCloudLockDtoList)) {
			Logger.error(">>>>>>>>>>>>>>>>>>>>>>>>>>库存数据异常");
			createSaleOrderResult.setType(InventoryConstant.SALE_ORDER_ERROR);
			return createSaleOrderResult;
		}

		if (productMicroAndCloudLockDtoList.stream().filter(d -> d.getLockCloudStock() > 0).count() < 1) {//正常出库
			this.generateOrder(orderNo, warehouseId, warehouseName, account, InventoryConstant.INVENTORY_ORDER_STATUS_SUCCESS);//status=4 正常出库

			List<ShipingDto> shipingDtoList = Lists.newArrayList();
			List<ProductMicroAndCloudLockDto> productMicroAndCloudLockDtos = productMicroAndCloudLockDtoList.stream().filter(d -> d.getLockMicroStock() > 0).collect(Collectors.toList());
			for (ProductMicroAndCloudLockDto productMicroAndCloudLockDto : productMicroAndCloudLockDtos) {
				int shipmentNum = productMicroAndCloudLockDto.getLockMicroStock();//商品发货总数
				String sku = productMicroAndCloudLockDto.getSku();
				String productTitle = productMicroAndCloudLockDto.getProductTitle();
				String imgUrl = productMicroAndCloudLockDto.getImgUrl();
				//订单详情
				this.generateOrderDetail(orderNo, warehouseId, warehouseName, shipmentNum, sku, productTitle, imgUrl, null);

				if(productMicroAndCloudLockDto.getExpiredDate() != null) {//带过期日期

					List<ProductMicroInventoryDetail> productMicroInventoryDetails = this.getProductMicroInventoryDetails(account, warehouseId, sku, productMicroAndCloudLockDto.getExpiredDate());
					shipingDtoList.addAll(microStockOutByProductMicroInventoryDetails(account, sku, warehouseId, warehouseName, orderNo, productMicroInventoryDetails, shipmentNum));

					//微仓总计表扣减
					ProductMicroInventoryTotal productMicroInventoryTotalParam = new ProductMicroInventoryTotal();
					productMicroInventoryTotalParam.setWarehouseId(warehouseId);
					productMicroInventoryTotalParam.setSku(sku);
					productMicroInventoryTotalParam.setAccount(account);

					ProductMicroInventoryTotal productMicroInventoryTotal = productMicroInventoryTotalMapper.selectByParam(productMicroInventoryTotalParam);
					int reduceAfterNum = productMicroInventoryTotal.getStock() - shipmentNum;
					productMicroInventoryTotal.setStock(reduceAfterNum);
					productMicroInventoryTotal.setUpdateTime(new Date());
					productMicroInventoryTotalMapper.updateByPrimaryKeySelective(productMicroInventoryTotal);
				} else {//不带过期日期
					//查询微仓明细
					List<ProductMicroInventoryDetail> productMicroInventoryDetails = this.getProductMicroInventoryDetails(account, warehouseId, sku, null);

					shipingDtoList.addAll(microStockOutByProductMicroInventoryDetails(account, sku, warehouseId, warehouseName, orderNo, productMicroInventoryDetails, shipmentNum));

					//微仓总计表扣减
					ProductMicroInventoryTotal productMicroInventoryTotalParam = new ProductMicroInventoryTotal();
					productMicroInventoryTotalParam.setWarehouseId(warehouseId);
					productMicroInventoryTotalParam.setSku(sku);
					productMicroInventoryTotalParam.setAccount(account);

					ProductMicroInventoryTotal productMicroInventoryTotal = productMicroInventoryTotalMapper.selectByParam(productMicroInventoryTotalParam);
					int reduceAfterNum = productMicroInventoryTotal.getStock() - shipmentNum;
					productMicroInventoryTotal.setStock(reduceAfterNum);
					productMicroInventoryTotal.setUpdateTime(new Date());
					productMicroInventoryTotalMapper.updateByPrimaryKeySelective(productMicroInventoryTotal);
				}
			}
			createSaleOrderResult.setType(InventoryConstant.SALE_ORDER_SUCCESS);//销售单库存扣减成功
			createSaleOrderResult.setSuccessOrLocks(shipingDtoList);
			return createSaleOrderResult;
		}

		//检查锁库
		List<ProductMicroAndCloudLockDto> collect = productMicroAndCloudLockDtoList.stream().filter(d -> d.getLockCloudStock() > 0).collect(toList());
		int newCloudMissingNum = 0;
		if (collect != null && collect.size() > 0) {
			for (ProductMicroAndCloudLockDto productMicroAndCloudLockDto : collect) {
				String sku = productMicroAndCloudLockDto.getSku();
				int shipmentNum = productMicroAndCloudLockDto.getLockCloudStock();

				if(productMicroAndCloudLockDto.getExpiredDate() != null) {
					//锁云仓
					ProductInventoryTotal inventoryTotalResult = this.getProductInventoryTotal(warehouseId, productMicroAndCloudLockDto.getSku());
					if(inventoryTotalResult == null){
						newCloudMissingNum++;
					} else {
						if (productMicroAndCloudLockDto.getExpiredDate() == null) {
							//有效被锁的数量
							ProductInventoryOrderLock inventoryOrderLockParam = new ProductInventoryOrderLock();
							inventoryOrderLockParam.setSku(productMicroAndCloudLockDto.getSku());
							inventoryOrderLockParam.setWarehouseId(warehouseId);
							inventoryOrderLockParam.setIsEffective((short) 1);//查询时为1  或者 -1
							List<ProductInventoryOrderLock> inventoryOrderLockList = productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
							int inventoryLockNum = 0;
							if(inventoryOrderLockList !=null && inventoryOrderLockList.size()>0){
								for(ProductInventoryOrderLock inventoryOrderLock:inventoryOrderLockList){
									inventoryLockNum += inventoryOrderLock.getStockLocked();
								}
							}

							//云仓有效库存
							int effectiveNum = inventoryTotalResult.getStock() - inventoryLockNum;

							if(effectiveNum < shipmentNum){
								newCloudMissingNum++;
							}
						} else {
							ProductInventoryDetail param = new ProductInventoryDetail();
							param.setSku(sku);
							param.setWarehouseId(warehouseId);
							param.setExpirationDate(productMicroAndCloudLockDto.getExpiredDate());

							ProductInventoryDetail inventoryDetail = productInventoryDetailMapper.selectByParam(param);
							if(inventoryDetail == null || inventoryDetail.getStock()<=0){
								newCloudMissingNum++;
							}
							if(inventoryDetail!=null){
								Integer expriationTotalStock = inventoryDetail.getStock();
								//查询该过期日期锁定的数量
								//有效被锁的数量
								ProductInventoryOrderLock inventoryOrderLockParam = new ProductInventoryOrderLock();
								inventoryOrderLockParam.setSku(sku);
								inventoryOrderLockParam.setWarehouseId(warehouseId);
								inventoryOrderLockParam.setIsEffective((short) 1);//查询时为1  或者 -1
								inventoryOrderLockParam.setExpirationDate(productMicroAndCloudLockDto.getExpiredDate());
								List<ProductInventoryOrderLock> inventoryExpirationOrderLockList =  productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
								int inventoryExpirationLockNum = 0;
								if(inventoryExpirationOrderLockList !=null && inventoryExpirationOrderLockList.size()>0){
									for(ProductInventoryOrderLock inventoryExpriationOrderLock:inventoryExpirationOrderLockList){
										inventoryExpirationLockNum+=inventoryExpriationOrderLock.getStockLocked();
									}
								}
								if(expriationTotalStock - inventoryExpirationLockNum < productMicroAndCloudLockDto.getLockCloudStock()){
									newCloudMissingNum++;
								}
							}
						}
				}
			} else {
					//有效被锁的数量
					ProductInventoryTotal inventoryTotalResult = this.getProductInventoryTotal(warehouseId, sku);
					ProductInventoryOrderLock inventoryOrderLockParam = new ProductInventoryOrderLock();
					inventoryOrderLockParam.setSku(sku);
					inventoryOrderLockParam.setWarehouseId(warehouseId);
					inventoryOrderLockParam.setIsEffective((short) 1);//查询时为1  或者 -1
					List<ProductInventoryOrderLock> inventoryOrderLockList = productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
					int inventoryLockNum = 0;
					if(inventoryOrderLockList !=null && inventoryOrderLockList.size()>0){
						for(ProductInventoryOrderLock inventoryOrderLock:inventoryOrderLockList){
							inventoryLockNum += inventoryOrderLock.getStockLocked();
						}
					}

					//云仓有效库存
					int effectiveNum = inventoryTotalResult.getStock() - inventoryLockNum;

					if(effectiveNum < shipmentNum){
						newCloudMissingNum++;
					}
				}
		}
		}

		if (newCloudMissingNum > 0) {//云仓也锁不住
			List<PurchaseDto> purchaseDtoList = Lists.newArrayList();
			createSaleOrderResult.setType(InventoryConstant.SALE_ORDER_OUT_OF_CLOUD_STOCK);
			for (ProductMicroAndCloudLockDto productMicroAndCloudLockDto : collect) {
				PurchaseDto purchaseDto = new PurchaseDto(productMicroAndCloudLockDto.getSku(), productMicroAndCloudLockDto.getLockCloudStock());
				purchaseDto.setExpirationDate(productMicroAndCloudLockDto.getExpiredDate());
				purchaseDto.setWarehouseId(warehouseId);
				purchaseDtoList.add(purchaseDto);
			}
			createSaleOrderResult.setPurchases(purchaseDtoList);
			return createSaleOrderResult;
		} else {
			//可以锁库
			this.generateOrder(orderNo, warehouseId, warehouseName, account, InventoryConstant.INVENTORY_ORDER_STATUS_SALE_ORDER_TO_PURCHASE);//status=3 缺货
			List<ShipingDto> shipingDtoList = Lists.newArrayList();
			List<PurchaseDto> purchaseDtoList = Lists.newArrayList();
			for (ProductMicroAndCloudLockDto productMicroAndCloudLockDto : productMicroAndCloudLockDtoList) {
				int shipmentNum = productMicroAndCloudLockDto.getLockMicroStock() + productMicroAndCloudLockDto.getLockCloudStock();//商品发货总数
				String sku = productMicroAndCloudLockDto.getSku();
				String productTitle = productMicroAndCloudLockDto.getProductTitle();
				String imgUrl = productMicroAndCloudLockDto.getImgUrl();

				//订单详情
				this.generateOrderDetail(orderNo, warehouseId, warehouseName, shipmentNum, sku, productTitle, imgUrl, null);

				if (productMicroAndCloudLockDto.getLockCloudStock() > 0) {//锁云仓
					PurchaseDto purchaseDto = new PurchaseDto(sku, productMicroAndCloudLockDto.getLockCloudStock());
					purchaseDto.setExpirationDate(productMicroAndCloudLockDto.getExpiredDate());
					purchaseDto.setWarehouseId(warehouseId);
					purchaseDtoList.add(purchaseDto);
					//锁云仓
					this.generateOrderLock(sku, warehouseId, warehouseName, productMicroAndCloudLockDto.getLockCloudStock(), orderNo, productMicroAndCloudLockDto.getExpiredDate());//orderNo 销售单号
				}

				if (productMicroAndCloudLockDto.getLockMicroStock() > 0) {//锁微仓
					List<ProductMicroInventoryDetail> productMicroInventoryDetails = this.getProductMicroInventoryDetails(account, warehouseId, sku, productMicroAndCloudLockDto.getExpiredDate());
					shipingDtoList.addAll(this.generateMicroOrderLockByMicroInventoryInRecord(productMicroInventoryDetails, account, warehouseId, sku, productMicroAndCloudLockDto.getLockMicroStock(), orderNo));
				}
			}

			createSaleOrderResult.setType(InventoryConstant.SALE_ORDER_OUT_OF_STOCK);
			createSaleOrderResult.setSuccessOrLocks(shipingDtoList);
			createSaleOrderResult.setPurchases(purchaseDtoList);
			return createSaleOrderResult;
		}
	}

	private static boolean isSameDate(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
				.get(Calendar.YEAR);
		boolean isSameMonth = isSameYear
				&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
		boolean isSameDate = isSameMonth
				&& cal1.get(Calendar.DAY_OF_MONTH) == cal2
				.get(Calendar.DAY_OF_MONTH);

		return isSameDate;
	}

	/**
	 * 根据微仓明细出库
	 *
	 * @param account
	 * @param sku
	 * @param warehouseId
	 * @param warehouseName
	 * @param orderNo
	 * @param productMicroInventoryDetails
	 * @param shipmentNum
	 * @return
	 */
	private List<ShipingDto> microStockOutByProductMicroInventoryDetails(String account, String sku, int warehouseId,String warehouseName, String orderNo,List<ProductMicroInventoryDetail> productMicroInventoryDetails,int shipmentNum) {
		int reduceNum = shipmentNum;

		List<ShipingDto> shipingDtoList = Lists.newArrayList();
		for (int i = 0; i < productMicroInventoryDetails.size(); i++) {
			ProductMicroInventoryDetail productMicroInventoryDetail = productMicroInventoryDetails.get(i);
			int microEffectiveStock = productMicroInventoryDetail.getStock() - productMicroInventoryDetail.getLockStock();

			if (microEffectiveStock > 0) {
				if (microEffectiveStock >= reduceNum) {
					//查询微仓入仓记录
					List<ProductMicroInventoryInRecord> productMicroInventoryInRecordList = this.getProductMicroInventoryInRecords(account, warehouseId, sku, productMicroInventoryDetail);

					int tmpStock = reduceNum;
					for (int j = 0; j < productMicroInventoryInRecordList.size(); j++) {
						ProductMicroInventoryInRecord productMicroInventoryInRecord = productMicroInventoryInRecordList.get(j);
						//根据入仓主键查询已锁定数
						int lockSum = this.getLockSumById(productMicroInventoryInRecord.getId());
						int canOutSum = productMicroInventoryInRecord.getResidueNum() - lockSum;//可扣减数量
						if (canOutSum >= tmpStock) {
							productMicroInventoryInRecord.setResidueNum(productMicroInventoryInRecord.getResidueNum() - tmpStock);
							productMicroInventoryInRecord.setUpdateTime(new Date());
							productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(productMicroInventoryInRecord);
							//微仓出仓记录
							this.generateOrderMicroInventoryDeductRecord(warehouseId, warehouseName, orderNo, sku, tmpStock, productMicroInventoryDetail.getExpirationDate(), account, productMicroInventoryInRecord.getPurchasePrice(), productMicroInventoryInRecord.getId(), productMicroInventoryInRecord.getIsGift());
							shipingDtoList.add(new ShipingDto(productMicroInventoryInRecord, tmpStock));
							break;
						} else {
							if (canOutSum > 0) {
								tmpStock = tmpStock - canOutSum;
								productMicroInventoryInRecord.setResidueNum(productMicroInventoryInRecord.getResidueNum() - canOutSum);
								productMicroInventoryInRecord.setUpdateTime(new Date());
								productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(productMicroInventoryInRecord);
								//微仓出仓记录
								this.generateOrderMicroInventoryDeductRecord(warehouseId, warehouseName, orderNo, sku, canOutSum, productMicroInventoryDetail.getExpirationDate(), account, productMicroInventoryInRecord.getPurchasePrice(), productMicroInventoryInRecord.getId(), productMicroInventoryInRecord.getIsGift());
								shipingDtoList.add(new ShipingDto(productMicroInventoryInRecord, canOutSum));
							}

						}
					}


					//微仓明细表扣减
					productMicroInventoryDetail.setStock(productMicroInventoryDetail.getStock() - reduceNum);
					productMicroInventoryDetail.setUpdateTime(new Date());
					productMicroInventoryDetailMapper.updateByPrimaryKeySelective(productMicroInventoryDetail);
					break;
				} else {
					List<ProductMicroInventoryInRecord> productMicroInventoryInRecordList = this.getProductMicroInventoryInRecords(account, warehouseId, sku, productMicroInventoryDetail);

					int tmpStock = microEffectiveStock;
					for (int j = 0; j < productMicroInventoryInRecordList.size(); j++) {
						ProductMicroInventoryInRecord productMicroInventoryInRecord = productMicroInventoryInRecordList.get(j);
						//根据入仓主键查询已锁定数
						int lockSum = this.getLockSumById(productMicroInventoryInRecord.getId());
						int canOutSum = productMicroInventoryInRecord.getResidueNum() - lockSum;//可扣减数量
						if (canOutSum >= tmpStock) {
							productMicroInventoryInRecord.setResidueNum(productMicroInventoryInRecord.getResidueNum() - tmpStock);
							productMicroInventoryInRecord.setUpdateTime(new Date());
							productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(productMicroInventoryInRecord);
							//微仓出仓记录
							this.generateOrderMicroInventoryDeductRecord(warehouseId, warehouseName, orderNo, sku, tmpStock, productMicroInventoryDetail.getExpirationDate(), account, productMicroInventoryInRecord.getPurchasePrice(), productMicroInventoryInRecord.getId(), productMicroInventoryInRecord.getIsGift());

							shipingDtoList.add(
									new ShipingDto(productMicroInventoryInRecord, tmpStock));
							break;
						} else {
							if (canOutSum > 0) {
								tmpStock = tmpStock - productMicroInventoryInRecord.getResidueNum();
								productMicroInventoryInRecord.setResidueNum(productMicroInventoryInRecord.getResidueNum() - canOutSum);
								productMicroInventoryInRecord.setUpdateTime(new Date());
								productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(productMicroInventoryInRecord);
								//微仓出仓记录
								this.generateOrderMicroInventoryDeductRecord(warehouseId, warehouseName, orderNo, sku, canOutSum, productMicroInventoryDetail.getExpirationDate(), account, productMicroInventoryInRecord.getPurchasePrice(), productMicroInventoryInRecord.getId(), productMicroInventoryInRecord.getIsGift());
								shipingDtoList.add(
										new ShipingDto(productMicroInventoryInRecord, canOutSum));

							}
						}
					}
					reduceNum = reduceNum - microEffectiveStock;
					//微仓明细表扣减
					productMicroInventoryDetail.setStock(productMicroInventoryDetail.getStock() - microEffectiveStock);
					productMicroInventoryDetail.setUpdateTime(new Date());
					productMicroInventoryDetailMapper.updateByPrimaryKeySelective(productMicroInventoryDetail);
				}
			}
		}

		return shipingDtoList;
	}

	/**
	 * 生成云仓锁库记录
	 *
	 * @param sku
	 * @param warehouseId
	 * @param warehouseName
	 * @param qty
	 * @param orderNo
	 */
	private void generateOrderLock(String sku,int warehouseId, String warehouseName, int qty, String orderNo, Date expirateDate) {
		ProductInventoryOrderLock inventoryOrderLock = new ProductInventoryOrderLock();
		inventoryOrderLock.setSku(sku);
		inventoryOrderLock.setWarehouseId(warehouseId);
		inventoryOrderLock.setWarehouseName(warehouseName);
		inventoryOrderLock.setStockLocked(qty);
		inventoryOrderLock.setOrderNo(orderNo);
		inventoryOrderLock.setIsEffective((short) 1);
		inventoryOrderLock.setLastCheckTime(new Date());
		inventoryOrderLock.setCreateTime(new Date());
		inventoryOrderLock.setUpdateTime(new Date());
		inventoryOrderLock.setExpirationDate(expirateDate);
		productInventoryOrderLockMapper.insertSelective(inventoryOrderLock);
	}

	@Override
	@Transactional
	public synchronized CancleSaleOrderResult cancleSaleOrder(String jsonStr) {
		Logger.info(">>>>>>>>>>>>>>>>>>>cancleSaleOrder：{}", jsonStr);
		JsonNode jsonNode = Json.parse(jsonStr);
		String orderNo = jsonNode.get("orderNo").asText();

		CancleSaleOrderResult cancleSaleOrderResult = new CancleSaleOrderResult();

		Order orderParam = new Order();
		orderParam.setOrderNo(orderNo);
		Order order = orderMapper.selectOrderByParams(orderParam);
		if (order == null) {
			cancleSaleOrderResult.setResult(false);
			cancleSaleOrderResult.setMsg("没有对应的订单记录，数据异常！");
			return cancleSaleOrderResult;
		}

		if (order.getStatus() == 5) {//该单号已取消
			cancleSaleOrderResult.setResult(false);
			cancleSaleOrderResult.setMsg("该单号已取消！");
			return cancleSaleOrderResult;
		}

		List<OrderMicroInventoryDeductRecord> orderMicroInventoryDeductRecords = orderMicroInventoryDeductRecordMapper.listByOrderNo(orderNo);

		if (orderMicroInventoryDeductRecords != null && orderMicroInventoryDeductRecords.size() > 0) {//检查微仓出仓记录
			for (OrderMicroInventoryDeductRecord orderMicroInventoryDeductRecord : orderMicroInventoryDeductRecords) {
				//查询微仓明细数据
				ProductMicroInventoryDetail productMicroDetailParam = new ProductMicroInventoryDetail();
				productMicroDetailParam.setAccount(orderMicroInventoryDeductRecord.getAccount());
				productMicroDetailParam.setExpirationDate(orderMicroInventoryDeductRecord.getExpirationDate());
				productMicroDetailParam.setSku(orderMicroInventoryDeductRecord.getSku());
				productMicroDetailParam.setIsGift(orderMicroInventoryDeductRecord.getIsGift());
				productMicroDetailParam.setWarehouseId(orderMicroInventoryDeductRecord.getWarehouseId());

				//查询入仓记录
				ProductMicroInventoryInRecord productMicroInventoryInRecord = productMicroInventoryInRecordMapper.selectByPrimaryKey(orderMicroInventoryDeductRecord.getMicroInRecordId());
				if (productMicroInventoryInRecord == null) {
					cancleSaleOrderResult.setResult(false);
					cancleSaleOrderResult.setMsg("没有对应的入仓记录，数据异常！");
					return cancleSaleOrderResult;
				}

				productMicroDetailParam.setCapfee(productMicroInventoryInRecord.getCapfee());
				productMicroDetailParam.setPurchasePrice(productMicroInventoryInRecord.getPurchasePrice());

				ProductMicroInventoryDetail productMicroInventoryDetail = productMicroInventoryDetailMapper.selectByParam(productMicroDetailParam);
				if (productMicroInventoryDetail != null) {
					int num = productMicroInventoryDetail.getStock() + orderMicroInventoryDeductRecord.getQty();
					productMicroInventoryDetail.setStock(num);
					productMicroInventoryDetail.setUpdateTime(new Date());
					productMicroInventoryDetailMapper.updateByPrimaryKeySelective(productMicroInventoryDetail);
				} else {
					cancleSaleOrderResult.setResult(false);
					cancleSaleOrderResult.setMsg("没有对应的微仓明细记录，数据异常！");
					return cancleSaleOrderResult;
				}

				int returnSum = orderMicroInventoryDeductRecord.getQty();

				//微仓入仓记录还原(可用数还原)
				productMicroInventoryInRecord.setResidueNum(productMicroInventoryInRecord.getResidueNum() + returnSum);
				productMicroInventoryInRecord.setUpdateTime(new Date());
				productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(productMicroInventoryInRecord);



				//微仓总仓还原
				ProductMicroInventoryTotal productMicroInventoryTotalParam = new ProductMicroInventoryTotal();
				productMicroInventoryTotalParam.setAccount(orderMicroInventoryDeductRecord.getAccount());
				productMicroInventoryTotalParam.setSku(orderMicroInventoryDeductRecord.getSku());
				productMicroInventoryTotalParam.setWarehouseId(orderMicroInventoryDeductRecord.getWarehouseId());
				ProductMicroInventoryTotal productMicroInventoryTotal = productMicroInventoryTotalMapper.selectByParam(productMicroInventoryTotalParam);
				if (productMicroInventoryTotal != null) {
					productMicroInventoryTotal.setStock(productMicroInventoryTotal.getStock() + returnSum);
					productMicroInventoryTotal.setUpdateTime(new Date());
//					productMicroInventoryTotal.setResidueNum(productMicroInventoryTotal.getResidueNum() + returnSum);
					productMicroInventoryTotalMapper.updateByPrimaryKeySelective(productMicroInventoryTotal);
				} else {
					cancleSaleOrderResult.setResult(false);
					cancleSaleOrderResult.setMsg("没有对应的微仓总仓记录，数据异常！");
					return cancleSaleOrderResult;
				}
			}
			cancleSaleOrderResult.setResult(true);

		} else {//查询微仓锁定记录
			ProductMicroInventoryOrderLock productMicroInventoryOrderLockParam = new ProductMicroInventoryOrderLock();
			productMicroInventoryOrderLockParam.setOrderNo(orderNo);
			productMicroInventoryOrderLockParam.setIsEffective((short) -1);
			List<ProductMicroInventoryOrderLock> productMicroInventoryOrderLockList = productMicroInventoryOrderLockMapper.selectListsByParams(productMicroInventoryOrderLockParam);
			if (productMicroInventoryOrderLockList != null && productMicroInventoryOrderLockList.size() > 0) {
				Logger.info(">>>>>>>>>>>>>>>>>>>>微仓还原对应的锁库记录;{}", productMicroInventoryOrderLockList);
				for (ProductMicroInventoryOrderLock productMicroInventoryOrderLock : productMicroInventoryOrderLockList) {
					//查询微仓明细数据
					ProductMicroInventoryDetail productMicroDetailParam = new ProductMicroInventoryDetail();
					productMicroDetailParam.setAccount(productMicroInventoryOrderLock.getAccount());
					productMicroDetailParam.setExpirationDate(productMicroInventoryOrderLock.getExpirationDate());
					productMicroDetailParam.setSku(productMicroInventoryOrderLock.getSku());
					productMicroDetailParam.setWarehouseId(productMicroInventoryOrderLock.getWarehouseId());
					productMicroDetailParam.setPurchasePrice(productMicroInventoryOrderLock.getPurchasePrice());
					productMicroDetailParam.setCapfee(productMicroInventoryOrderLock.getCapfee());
					productMicroDetailParam.setIsGift(productMicroInventoryOrderLock.getIsGift());

					ProductMicroInventoryDetail productMicroInventoryDetail = productMicroInventoryDetailMapper.selectByParam(productMicroDetailParam);
					if (productMicroInventoryDetail != null) {
						int num = productMicroInventoryDetail.getLockStock() - productMicroInventoryOrderLock.getStockLocked();
						productMicroInventoryDetail.setLockStock(num);
						productMicroInventoryDetail.setUpdateTime(new Date());
						productMicroInventoryDetailMapper.updateByPrimaryKeySelective(productMicroInventoryDetail);
					} else {
						cancleSaleOrderResult.setResult(false);
						cancleSaleOrderResult.setMsg("没有对应的微仓明细记录，数据异常！");
						return cancleSaleOrderResult;
					}
					//释放锁
					productMicroInventoryOrderLock.setIsEffective((short) 0);
					productMicroInventoryOrderLock.setUpdateTime(new Date());
					productMicroInventoryOrderLockMapper.updateByPrimaryKeySelective(productMicroInventoryOrderLock);
				}
				cancleSaleOrderResult.setResult(true);
			} else {
				cancleSaleOrderResult.setResult(false);
				cancleSaleOrderResult.setMsg("该单号没有对应的出库记录、锁库记录,数据异常！");
			}
		}

		order.setStatus(5);//标记该订单已取消
		order.setUpdateTime(new Date());
		orderMapper.updateByPrimaryKeySelective(order);
		return cancleSaleOrderResult;
	}

	/**
	 * 查询微仓锁定记录
	 *
	 * @param account
	 * @param sku
	 * @param warehouseId
	 * @return
	 */
	private List<ProductMicroInventoryOrderLock> getProductMicroInventoryOrderLocks(String account, String sku, int warehouseId) {
		ProductMicroInventoryOrderLockDto productMicroInventoryOrderLockDto = new ProductMicroInventoryOrderLockDto();
		productMicroInventoryOrderLockDto.setAccount(account);
		productMicroInventoryOrderLockDto.setSku(sku);
		productMicroInventoryOrderLockDto.setWarehouseId(warehouseId);
		productMicroInventoryOrderLockDto.setIsEffective(-1);//锁定的记录
		return productMicroInventoryOrderLockMapper.query(productMicroInventoryOrderLockDto);
	}

	/**
	 * 查询微仓明细
	 *
	 * @param account
	 * @param warehouseId
	 * @param sku
	 * @return
	 */
	private List<ProductMicroInventoryDetail> getProductMicroInventoryDetails(String account, int warehouseId, String sku, Date expirationDate) {
		//查询微仓明细
		ProductMicroInventoryDetailDto productMicroInventoryDetailDto = new ProductMicroInventoryDetailDto();
		productMicroInventoryDetailDto.setAccount(account);
		productMicroInventoryDetailDto.setSku(sku);
		productMicroInventoryDetailDto.setWarehouseId(warehouseId);
//		productMicroInventoryDetailDto.setMoreThan(1);
		productMicroInventoryDetailDto.setExpirationDate(expirationDate);
		productMicroInventoryDetailDto.setExpirationDateSort(0);
		List<ProductMicroInventoryDetail> productMicroInventoryDetails = productMicroInventoryDetailMapper.selectByParamDto(productMicroInventoryDetailDto);
		if (productMicroInventoryDetails == null || productMicroInventoryDetails.size()<=0){
			//手动异常
			throw new RuntimeException("查询微仓明细数据异常");
		}

		return productMicroInventoryDetails;
	}


	/**
	 * 根据微仓明细生成微仓锁定库存
	 *
	 * @param lockNum 要锁定的库存数量
	 */
	public List<ShipingDto> generateMicroOrderLockByMicroInventoryInRecord(List<ProductMicroInventoryDetail> productMicroInventoryDetailList, String account, Integer warehouseId, String sku, int lockNum, String orderNo) {
		Logger.info(">>>>>>>>>>>>>>>>>>>执行微仓锁库：productMicroInventoryDetailList：{}, account:{},lockNum:{}, orderNo:{}", productMicroInventoryDetailList, account, lockNum, orderNo);
		int reduceNum = lockNum;
		List<ShipingDto> shipingDtoList = Lists.newArrayList();
		for (int i = 0; i < productMicroInventoryDetailList.size(); i++) {
			ProductMicroInventoryDetail productMicroInventoryDetail = productMicroInventoryDetailList.get(i);
			int microDetailStock = productMicroInventoryDetail.getStock() - productMicroInventoryDetail.getLockStock();
			if (microDetailStock > 0) {
				if (microDetailStock >= reduceNum) {
					//查询微仓入仓记录
					List<ProductMicroInventoryInRecord> productMicroInventoryInRecordList = this.getProductMicroInventoryInRecords(account, warehouseId, sku, productMicroInventoryDetail);

					//锁库
					shipingDtoList.addAll(this.generateMicroInventoryLockDetail(productMicroInventoryInRecordList, reduceNum, sku, warehouseId, account, orderNo));

					//更新微仓明细锁定数量
					productMicroInventoryDetail.setLockStock(productMicroInventoryDetail.getLockStock() + reduceNum);
					productMicroInventoryDetail.setUpdateTime(new Date());
					productMicroInventoryDetailMapper.updateByPrimaryKeySelective(productMicroInventoryDetail);
					break;
				} else {
					List<ProductMicroInventoryInRecord> productMicroInventoryInRecordList = this.getProductMicroInventoryInRecords(account, warehouseId, sku, productMicroInventoryDetail);

					//锁库
					shipingDtoList.addAll(this.generateMicroInventoryLockDetail(productMicroInventoryInRecordList, microDetailStock, sku, warehouseId, account, orderNo));

					//更新微仓明细锁定数量
					productMicroInventoryDetail.setLockStock(productMicroInventoryDetail.getStock());
					productMicroInventoryDetail.setUpdateTime(new Date());
					productMicroInventoryDetailMapper.updateByPrimaryKeySelective(productMicroInventoryDetail);
					reduceNum = reduceNum - microDetailStock;
				}
			}
		}
		return shipingDtoList;
	}

	/**
	 * 具体锁库逻辑
	 */
	private List<ShipingDto> generateMicroInventoryLockDetail(List<ProductMicroInventoryInRecord> productMicroInventoryInRecordList, int tmpStock, String sku, int warehouseId, String account, String orderNo) {
		List<ShipingDto> shipingDtoList = Lists.newArrayList();
		for (int j = 0; j < productMicroInventoryInRecordList.size(); j++) {
			ProductMicroInventoryInRecord productMicroInventoryInRecord = productMicroInventoryInRecordList.get(j);
			//根据入仓主键查询已锁定数
			int lockSum = this.getLockSumById(productMicroInventoryInRecord.getId());
			//可锁定的库存数
			int canLocSum = productMicroInventoryInRecord.getResidueNum() - lockSum;
			if (canLocSum > 0) {
				if (canLocSum >= tmpStock) {
					//生成微仓锁定记录
					this.generateProductMicroInventoryOrderLock(warehouseId, productMicroInventoryInRecord.getWarehouseName(), orderNo, sku, tmpStock, (short) -1, account, productMicroInventoryInRecord.getId(), productMicroInventoryInRecord.getPurchasePrice(), productMicroInventoryInRecord.getCapfee(), productMicroInventoryInRecord.getExpirationDate(), productMicroInventoryInRecord.getIsGift());
					shipingDtoList.add(new ShipingDto(productMicroInventoryInRecord,tmpStock));
					break;
				} else {
					//生成微仓锁定记录
					this.generateProductMicroInventoryOrderLock(warehouseId, productMicroInventoryInRecord.getWarehouseName(), orderNo, sku, canLocSum, (short) -1, account, productMicroInventoryInRecord.getId(), productMicroInventoryInRecord.getPurchasePrice(), productMicroInventoryInRecord.getCapfee(), productMicroInventoryInRecord.getExpirationDate(), productMicroInventoryInRecord.getIsGift());
					shipingDtoList.add(new ShipingDto(productMicroInventoryInRecord,canLocSum));
					tmpStock = tmpStock - canLocSum;
				}
			}
		}
		return shipingDtoList;
	}

	/**
	 * 检查微仓总表
	 *
	 * @param account
	 * @param warehouseId
	 * @param sku
	 * @return
	 */
	private ProductMicroInventoryTotal getProductMicroInventoryTotal(String account, int warehouseId, String sku) {
		ProductMicroInventoryTotal productMicroInventoryTotalParam = new ProductMicroInventoryTotal();
		productMicroInventoryTotalParam.setSku(sku);
		productMicroInventoryTotalParam.setWarehouseId(warehouseId);
		productMicroInventoryTotalParam.setAccount(account);
		return productMicroInventoryTotalMapper.selectByParam(productMicroInventoryTotalParam);
	}

	/**
	 * 微仓入仓记录
	 *
	 * @param account
	 * @param warehouseId
	 * @param sku
	 * @param productMicroInventoryDetail
	 * @return
	 */
	private List<ProductMicroInventoryInRecord> getProductMicroInventoryInRecords(String account, int warehouseId, String sku,ProductMicroInventoryDetail productMicroInventoryDetail) {
		//查询微仓入仓记录
		ProductMicroInventoryInRecordDto productMicroInventoryInRecordDto = new ProductMicroInventoryInRecordDto();
		productMicroInventoryInRecordDto.setAccount(account);
		productMicroInventoryInRecordDto.setSku(sku);
		productMicroInventoryInRecordDto.setWarehouseId(warehouseId);
		productMicroInventoryInRecordDto.setExpirationDate(productMicroInventoryDetail.getExpirationDate());

		productMicroInventoryInRecordDto.setCapfee(productMicroInventoryDetail.getCapfee());
		productMicroInventoryInRecordDto.setPurchasePrice(productMicroInventoryDetail.getPurchasePrice());
		productMicroInventoryInRecordDto.setIsGift(Integer.valueOf(productMicroInventoryDetail.getIsGift()));

		productMicroInventoryInRecordDto.setPurchaseTimeSort(0);//升序排
		productMicroInventoryInRecordDto.setMoreThan(1);//剩余数大于0
		List<ProductMicroInventoryInRecord> productMicroInventoryInRecordList = productMicroInventoryInRecordMapper.listMicroInRecordList(productMicroInventoryInRecordDto);
		if(productMicroInventoryInRecordList == null || productMicroInventoryInRecordList.size() <= 0){
            //手动异常
			Logger.error("查询微仓入仓明细记录数据,sku:{}, expirationDate:{}", sku, productMicroInventoryDetail.getExpirationDate());
            throw new RuntimeException("微仓入仓明细记录数据异常");
        }
		return productMicroInventoryInRecordList;
	}



	/**
	 * 生成微仓锁定记录
	 *
	 * @param warehouseId
	 * @param warehouseName
	 * @param orderNo
	 * @param
	 */
	private void generateProductMicroInventoryOrderLock(int warehouseId, String warehouseName, String orderNo, String sku, int stockLocked, short isEffective, String account, Integer microInRecordId, double purchasePrice, Float capfee, Date expirationDate, Short isGift) {
		ProductMicroInventoryOrderLock productMicroInventoryOrderLock = new ProductMicroInventoryOrderLock();
		productMicroInventoryOrderLock.setOrderNo(orderNo);
		productMicroInventoryOrderLock.setSku(sku);
		productMicroInventoryOrderLock.setStockLocked(stockLocked);
		productMicroInventoryOrderLock.setWarehouseId(warehouseId);
		productMicroInventoryOrderLock.setWarehouseName(warehouseName);
		productMicroInventoryOrderLock.setIsEffective(isEffective);
		productMicroInventoryOrderLock.setMicroInRecordId(microInRecordId);
		productMicroInventoryOrderLock.setPurchasePrice((float) purchasePrice);
		productMicroInventoryOrderLock.setCapfee(capfee);
		productMicroInventoryOrderLock.setExpirationDate(expirationDate);
		Date now = new Date();
		productMicroInventoryOrderLock.setCreateTime(now);
		productMicroInventoryOrderLock.setUpdateTime(now);
		productMicroInventoryOrderLock.setAccount(account);
		productMicroInventoryOrderLock.setIsGift(isGift);
		productMicroInventoryOrderLockMapper.insertSelective(productMicroInventoryOrderLock);
	}

	/**
	 * 生成微仓出仓记录
	 *
	 * @param warehouseId
	 * @param warehouseName
	 * @param orderNo
	 * @param sku
	 * @param qty
	 * @param expirationDate
	 * @param purchasePrice 采购价
	 */
	private void generateOrderMicroInventoryDeductRecord(int warehouseId, String warehouseName, String orderNo, String sku, int qty, Date expirationDate, String account, float purchasePrice,Integer microInRecordId, Short isGift) {
		OrderMicroInventoryDeductRecord orderMicroInventoryDeductRecord = new OrderMicroInventoryDeductRecord();
		orderMicroInventoryDeductRecord.setSku(sku);
		orderMicroInventoryDeductRecord.setQty(qty);
		orderMicroInventoryDeductRecord.setWarehouseId(warehouseId);
		orderMicroInventoryDeductRecord.setWarehouseName(warehouseName);
		orderMicroInventoryDeductRecord.setExpirationDate(expirationDate);
		orderMicroInventoryDeductRecord.setPurchasePrice(purchasePrice);
		Date now = new Date();
		orderMicroInventoryDeductRecord.setCreateTime(now);
		orderMicroInventoryDeductRecord.setUpdateTime(now);
		orderMicroInventoryDeductRecord.setAccount(account);
		orderMicroInventoryDeductRecord.setOrderNo(orderNo);
		orderMicroInventoryDeductRecord.setMicroInRecordId(microInRecordId);
		orderMicroInventoryDeductRecord.setIsGift(isGift);
		orderMicroInventoryDeductRecordMapper.insertSelective(orderMicroInventoryDeductRecord);
	}

	/**
	 * 创建订单
	 *
	 * @param orderNo
	 * @param warehouseId
	 * @param warehouseName
	 * @param account
	 * @param status
	 */
	private void generateOrder(String orderNo, int warehouseId, String warehouseName, String account, int status) {
		Order order = new Order();
		order.setOrderNo(orderNo);
		order.setWarehouseId(warehouseId);
		order.setWarehouseName(warehouseName);
		order.setAccount(account);
		order.setStatus(status);//订单状态
		Date now = new Date();
		order.setCreateTime(now);
		order.setUpdateTime(now);
		orderMapper.insertSelective(order);
	}

	/**
	 * 创建订单详情
	 *
	 * @param orderNo
	 * @param warehouseId
	 * @param warehouseName
	 * @param shipmentNum
	 * @param sku
	 * @param productTitle
	 * @param imgUrl
	 */
	private void generateOrderDetail(String orderNo, int warehouseId, String warehouseName,Integer shipmentNum, String sku, String productTitle, String imgUrl, Date expirationDate) {
		//订单详情
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setOrderNo(orderNo);
		orderDetail.setSku(sku);
		orderDetail.setQty(shipmentNum);
		orderDetail.setWarehouseId(warehouseId);
		orderDetail.setWarehouseName(warehouseName);
		orderDetail.setProductTitle(productTitle);
		orderDetail.setImgUrl(imgUrl);
		Date now = new Date();
		orderDetail.setUpdateTime(now);
		orderDetail.setCreateTime(now);
		orderDetail.setExpirationDate(expirationDate);
		orderDetailMapper.insertSelective(orderDetail);
	}



	/**
	 * 根据入仓主键查询已锁定数
	 *
	 * @param primaryKeyId
	 * @return
	 */
	private int getLockSumById(int primaryKeyId) {
		//根据入仓主键查询已锁定数
		ProductMicroInventoryOrderLockDto productMicroInventoryOrderLockDto = new ProductMicroInventoryOrderLockDto();
		productMicroInventoryOrderLockDto.setMicroInRecordId(primaryKeyId);
		productMicroInventoryOrderLockDto.setIsEffective(-1);
		List<ProductMicroInventoryOrderLock> productMicroInventoryOrderLockList = productMicroInventoryOrderLockMapper.query(productMicroInventoryOrderLockDto);
		int lockSum = 0;
		if (productMicroInventoryOrderLockList != null && productMicroInventoryOrderLockList.size() > 0) {
			for (ProductMicroInventoryOrderLock productMicroInventoryOrderLock : productMicroInventoryOrderLockList) {
				lockSum += productMicroInventoryOrderLock.getStockLocked();
			}
		}
		return lockSum;
	}


	/*private ShipingDto generateShipingDto(String account, String sku, String productTitle,
			String imgUrl, int qty, Float purchasePrice, Float capfee, 
			int warehouseId, String warehouseName, Date expirationDate, 
			String purchaseNo, Short isGift, Float arriveWarePrice,String contractNo) {
		ShipingDto shipingDto = new ShipingDto();
		shipingDto.setAccount(account);
		shipingDto.setSku(sku);
		shipingDto.setProductTitle(productTitle);
		shipingDto.setImgUrl(imgUrl);
		shipingDto.setQty(qty);
		shipingDto.setPurchasePrice(purchasePrice);
		shipingDto.setCapfee(capfee);
		shipingDto.setWarehouseId(warehouseId);
		shipingDto.setWarehouseName(warehouseName);
		shipingDto.setExpirationDate(expirationDate);
		shipingDto.setPurchaseNo(purchaseNo);
		shipingDto.setIsGift(isGift);
		shipingDto.setArriveWarePrice(arriveWarePrice);
		shipingDto.setContractNo(contractNo);
		return shipingDto;
	}*/

	/**
	 * 查询云仓
	 *
	 * @param warehouseId
	 * @param sku
	 * @return
	 */
	private ProductInventoryTotal getProductInventoryTotal(int warehouseId, String sku) {
		ProductInventoryTotal inventoryTotalParam = new ProductInventoryTotal();
		inventoryTotalParam.setSku(sku);
		inventoryTotalParam.setWarehouseId(warehouseId);
		return productInventoryTotalMapper.selectByParam(inventoryTotalParam);
	}

	@Override
	public List<MicroInventoryQueryResult> selectByParams(ProductMicroInventoryDetailSearchDto parms) {
		List<ProductMicroInventoryTotal> microTotalList=productMicroInventoryTotalMapper.selectMicroTotalListByParam(parms);
		List<MicroInventoryQueryResult> microQueryList = Lists.newArrayList();
		for (ProductMicroInventoryTotal microTotal : microTotalList) {
			//查询sku warhouseId account  有效的锁库记录
			MicroInventoryQueryResult microQuery=new MicroInventoryQueryResult(microTotal,0);
			ProductMicroInventoryOrderLock microLocakParam=new ProductMicroInventoryOrderLock();
			microLocakParam.setSku(microTotal.getSku());
			microLocakParam.setAccount(microTotal.getAccount());
			microLocakParam.setWarehouseId(microTotal.getWarehouseId());
			microLocakParam.setIsEffective((short) -1);
			List<ProductMicroInventoryOrderLock> microLockList = productMicroInventoryOrderLockMapper.selectListsByParams(microLocakParam);
			if(null != microLockList && microLockList.size()>0){
				for (ProductMicroInventoryOrderLock productMicroInventoryOrderLock : microLockList) {
					microQuery.setLockNum(microQuery.getLockNum() + productMicroInventoryOrderLock.getStockLocked());
					microQuery.setStock(microQuery.getStock()-productMicroInventoryOrderLock.getStockLocked());
				}
			}
			if(microQuery.getStock()<=0 && (parms.getAvaliableStock()!=null && parms.getAvaliableStock()==1)){//查询有库存的
				continue;
			}
			if(microQuery.getStock()>0 && (parms.getAvaliableStock()!=null && parms.getAvaliableStock()==0)){//查可用量为0
				continue;
			}
			microQueryList.add(microQuery);
		}
		return microQueryList;
	}

	@Override
	public ProductMicroInventoyResult microInventoryInSingle(ProductMicroInventoryDetail microInventory){
		if(microInventory==null){
			return new ProductMicroInventoyResult(false,"微仓入库参数格式不正确",microInventory,null);
		}
		//先根据账户  是不是赠品  sku 到期时间 确定是否存在
		ProductMicroInventoryDetail param=new ProductMicroInventoryDetail();
		
		param.setSku(microInventory.getSku());
		param.setAccount(microInventory.getAccount());
		param.setWarehouseId(microInventory.getWarehouseId());
		param.setIsGift(microInventory.getIsGift());
		param.setPurchasePrice(microInventory.getPurchasePrice());
		param.setExpirationDate(microInventory.getExpirationDate());
		param.setContainerNumber(microInventory.getContainerNumber());
		param.setStorageLocation(microInventory.getStorageLocation());
		
		param=microInventoryDetailMapper.selectByParam(param);
		Logger.info("入库信息查询结果[{}]", param.toString());
		if(param.getId()!=null){
			param.setStock(param.getStock()+microInventory.getStock());
			param.setUpdateTime(new Date());
			microInventoryDetailMapper.updateByPrimaryKey(param);
		}else{
			microInventory.setCreateTime(new Date());
			microInventoryDetailMapper.insert(microInventory);
		}
		return new ProductMicroInventoyResult(true,null,microInventory,null);
	}

	@Override
	public ProductMicroInventoyResult microTotalInventoryAdd(ProductMicroInventoryTotal microInventoryTotal) {
		
		ProductMicroInventoyResult microTotalResult=new ProductMicroInventoyResult();			
		try {
			ProductMicroInventoryTotal param=new ProductMicroInventoryTotal();
			param.setSku(microInventoryTotal.getSku());
			param.setProductTitle(microInventoryTotal.getProductTitle());
			param.setAccount(microInventoryTotal.getAccount());
			param.setWarehouseId(microInventoryTotal.getWarehouseId());
			param.setWarehouseName(microInventoryTotal.getWarehouseName());
			ProductMicroInventoryTotal resultMicoTotal = productMicroInventoryTotalMapper.selectByParam(microInventoryTotal);
			if(resultMicoTotal != null){
				resultMicoTotal.setStock(resultMicoTotal.getStock() + microInventoryTotal.getStock());
				resultMicoTotal.setUpdateTime(new Date());
				productMicroInventoryTotalMapper.updateByPrimaryKeySelective(param);
			}else{
				microInventoryTotal.setCreateTime(new Date());
				productMicroInventoryTotalMapper.insertSelective(microInventoryTotal);
			}
			microTotalResult.setResult(true);
		
			return microTotalResult;
		} catch (Exception e) {

			e.printStackTrace();
			microTotalResult.setResult(false);
			microTotalResult.setMsg("microInventoryTotal增加发生异常！");
			return microTotalResult;
		}
	}

	@Override
	public ProductMicroInventoryDetail selectByMicroInventoryDetail(ProductMicroInventoryDetail microInventoryDetail) {
		ProductMicroInventoryDetail resultMicroInventoryDetail = microInventoryDetailMapper.selectByParam(microInventoryDetail);
		return resultMicroInventoryDetail;
	}
	
	@Override
	public ProductMicroInventoyResult restoreCloudFormMicroInventory(String orderNo) {
		Order order=orderMapper.selectOrderByParams(new Order(orderNo));
		if (order == null ||(order.getStatus()!=null && order.getStatus()==2)){
			return new ProductMicroInventoyResult(false,"采购单["+orderNo+"]不存在或已取消",null,null);
		}
		try {
			ProductMicroInventoryInRecord microInRecordParam=new ProductMicroInventoryInRecord();
			microInRecordParam.setOrderNo(orderNo);
			List<ProductMicroInventoryInRecord> microInList = productMicroInventoryInRecordMapper.selectMicroInRecordListByParam(microInRecordParam);
			if(microInList==null || microInList.size()<=0){
				return new ProductMicroInventoyResult(false,"该采购单号无微仓入库记录",null,null);
			}
			for (ProductMicroInventoryInRecord microInRecord : microInList) {
				ProductMicroInventoryDetail microDetailParam = new ProductMicroInventoryDetail();
				microDetailParam.setAccount(microInRecord.getAccount());
				microDetailParam.setSku(microInRecord.getSku());
				microDetailParam.setExpirationDate(microInRecord.getExpirationDate());
				microDetailParam.setWarehouseId(microInRecord.getWarehouseId());
				microDetailParam.setIsGift(microInRecord.getIsGift());
				microDetailParam.setPurchasePrice(microInRecord.getPurchasePrice());
				microDetailParam.setCapfee(microInRecord.getCapfee());
				ProductMicroInventoryDetail microDetail = microInventoryDetailMapper.selectByParam(microDetailParam);
				
				microDetail.setStock(microDetail.getStock()-microInRecord.getQty());
				microDetail.setUpdateTime(new Date());
				microInventoryDetailMapper.updateByPrimaryKeySelective(microDetail);
				microInRecord.setResidueNum(0);
				microInRecord.setUpdateTime(new Date());
				productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(microInRecord);
				
				ProductMicroInventoryTotal microTotalParam=new ProductMicroInventoryTotal();
				microTotalParam.setSku(microInRecord.getSku());
				microTotalParam.setAccount(microInRecord.getAccount());
				microTotalParam.setWarehouseId(microInRecord.getWarehouseId());
				ProductMicroInventoryTotal resultMicoTotal = productMicroInventoryTotalMapper.selectByParam(microTotalParam);
				resultMicoTotal.setStock(resultMicoTotal.getStock()-microInRecord.getQty());
				resultMicoTotal.setUpdateTime(new Date());
				productMicroInventoryTotalMapper.updateByPrimaryKeySelective(resultMicoTotal);
				
				synchronized (monitor) {
					//还原云仓
					ProductInventoryDetail inventoryDetailParam = new ProductInventoryDetail();
					inventoryDetailParam.setSku(microInRecord.getSku());
					inventoryDetailParam.setWarehouseId(microInRecord.getWarehouseId());
					inventoryDetailParam.setExpirationDate(microInRecord.getExpirationDate());
					ProductInventoryDetail inventoryDetailResult = productInventoryDetailMapper
							.selectByParam(inventoryDetailParam);
					inventoryDetailResult.setStock(inventoryDetailResult.getStock() + microInRecord.getQty());
					inventoryDetailResult.setUpdateTime(new Date());
					productInventoryDetailMapper.updateByPrimaryKeySelective(inventoryDetailResult);
					ProductInventoryTotal inventoryTotalParam = new ProductInventoryTotal();
					inventoryTotalParam.setSku(microInRecord.getSku());
					inventoryTotalParam.setWarehouseId(microInRecord.getWarehouseId());
					ProductInventoryTotal inventoryTotal = productInventoryTotalMapper
							.selectByParam(inventoryTotalParam);
					inventoryTotal.setStock(inventoryTotal.getStock() + microInRecord.getQty());
					//inventoryTotal.setResidueNum(inventoryTotal.getResidueNum()+microInRecord.getQty());
					inventoryTotal.setUpdateTime(new Date());
					productInventoryTotalMapper.updateByPrimaryKeySelective(inventoryTotal);
					ProductInventoryInRecord inventoryInRecord = new ProductInventoryInRecord();
					inventoryInRecord.setOrderNo(microInRecord.getOrderNo());
					inventoryInRecord.setExpirationDate(microInRecord.getExpirationDate());
					inventoryInRecord.setSku(microInRecord.getSku());
					inventoryInRecord.setWarehouseId(microInRecord.getWarehouseId());
					inventoryInRecord.setWarehouseName(microInRecord.getWarehouseName());
					inventoryInRecord.setQty(microInRecord.getQty());
					inventoryInRecord.setCreateTime(new Date());
					productInventoryInRecordMapper.insertSelective(inventoryInRecord);
				}
			}
			
			order.setStatus(2);
			order.setUpdateTime(new Date());
			orderMapper.updateByPrimaryKeySelective(order);
			return new ProductMicroInventoyResult(true,"云仓还原成功",null,null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ProductMicroInventoyResult(false,"云仓还原发生异常。",null,null);
		}
	}

	@Override
	public synchronized MsiteStockOutResult msiteStockOut(String jsonDataStr) {
		Logger.info(">>>>>>>>>>>>>>>>>>>msiteStockOut：{}", jsonDataStr);

		MsiteStockOutResult msiteStockOutResult = new MsiteStockOutResult();
		JsonNode jsonNode = Json.parse(jsonDataStr);
		JsonNode details = jsonNode.get("pros");

		String account = jsonNode.get("account").asText();
		int warehouseId = jsonNode.get("warehouseId").asInt();
		String warehouseName = jsonNode.get("warehouseName").asText();
		String orderNo = jsonNode.get("orderNo").asText();

		int lostNum = 0;

		Map map = Maps.newLinkedHashMap();
		for (JsonNode detail : details) {
			String sku = detail.get("sku").asText(); //sku
			int shipmentNum = detail.get("qty").asInt();//购买数量

			//检查微仓有效库存
			ProductMicroInventoryTotal productMicroInventoryTotal = this.getProductMicroInventoryTotal(account, warehouseId, sku);
			if (productMicroInventoryTotal != null) {
				//微仓锁定总数
				List<ProductMicroInventoryOrderLock> productMicroInventoryOrderLockList = this.getProductMicroInventoryOrderLocks(account, sku, warehouseId);
				int lockSum = 0;
				if (productMicroInventoryOrderLockList != null && productMicroInventoryOrderLockList.size() > 0) {
					for (ProductMicroInventoryOrderLock productMicroInventoryOrderLock : productMicroInventoryOrderLockList) {
						lockSum += productMicroInventoryOrderLock.getStockLocked();
					}
				}
				//微仓有效库存
				int microEffectiveStock = productMicroInventoryTotal.getStock() - lockSum;
				if (microEffectiveStock < shipmentNum) {//微仓不足
					ProductInventoryTotal productInventoryTotal = this.getProductInventoryTotal(warehouseId, sku);
					if (productInventoryTotal == null) {
						lostNum++;
					}
				}
			} else {
				ProductInventoryTotal productInventoryTotal = this.getProductInventoryTotal(warehouseId, sku);
				if (productInventoryTotal == null) {
					lostNum++;
				}
			}
		}

		if (lostNum > 0) {//云仓不存在
			msiteStockOutResult.setType(2);
		} else {//执行扣减

			//订单总表
			this.generateOrder(orderNo, warehouseId, warehouseName, account, InventoryConstant.INVENTORY_ORDER_STATUS_SUCCESS);

			for (JsonNode detail : details) {
				String sku = detail.get("sku").asText(); //sku
				int shipmentNum = detail.get("qty").asInt();//购买数量
				String productTitle = detail.get("productTitle").asText();
				String imgUrl = detail.get("imgUrl").asText();
				double purchasePrice = detail.get("purchasePrice").asDouble();//云仓价格

				//订单详情
				this.generateOrderDetail(orderNo, warehouseId, warehouseName,shipmentNum, sku, productTitle, imgUrl, null);

				//检查微仓有效库存
				ProductMicroInventoryTotal productMicroInventoryTotal = this.getProductMicroInventoryTotal(account, warehouseId, sku);
				if (productMicroInventoryTotal != null) {
					//微仓锁定总数
					List<ProductMicroInventoryOrderLock> productMicroInventoryOrderLockList = this.getProductMicroInventoryOrderLocks(account, sku, warehouseId);
					int lockSum = 0;
					if (productMicroInventoryOrderLockList != null && productMicroInventoryOrderLockList.size() > 0) {
						for (ProductMicroInventoryOrderLock productMicroInventoryOrderLock : productMicroInventoryOrderLockList) {
							lockSum += productMicroInventoryOrderLock.getStockLocked();
						}
					}
					//微仓有效库存
					int microEffectiveStock = productMicroInventoryTotal.getStock() - lockSum;

					if (microEffectiveStock == 0) {//出云仓

						//云仓出仓记录
						ProductInventoryDetail inventoryDetailParam = new ProductInventoryDetail();
						inventoryDetailParam.setSku(sku);
						inventoryDetailParam.setWarehouseId(warehouseId);
						List<ProductInventoryDetail> productInventoryDetailList = productInventoryDetailMapper.selectinventoryDetailListByParam(inventoryDetailParam);
						this.generateOrderInventoryDeductRecord(productInventoryDetailList, shipmentNum, orderNo, warehouseId);

						ProductInventoryTotal productInventoryTotal = this.getProductInventoryTotal(warehouseId, sku);
						if(productInventoryTotal.getStock() < shipmentNum) {//云仓库存不足
							for (ProductInventoryDetail productInventoryDetail : productInventoryDetailList) {
								productInventoryDetail.setStock(0);
								productInventoryDetail.setUpdateTime(new Date());
								productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetail);
							}
						}

						MsiteStockOutDetail msiteStockOutDetail = this.getMsiteStockOutDetail(warehouseId, warehouseName,shipmentNum, purchasePrice, 1);
						Map detailMap = Maps.newHashMap();
						detailMap.put(sku, msiteStockOutDetail);
						map.putAll(detailMap);
					}

					if (microEffectiveStock < shipmentNum && microEffectiveStock > 0) {//微仓不足
						//出微仓
						//查询微仓明细
						List<ProductMicroInventoryDetail> productMicroInventoryDetailList = this.getProductMicroInventoryDetails(account, warehouseId, sku, null);


						List<MsiteStockOutDetail> msiteMicroStockOutDetailList = this.generateMsiteOrderMicroInventoryDeductRecord(productMicroInventoryDetailList, microEffectiveStock, orderNo, account, sku, warehouseId, warehouseName);

						//出云仓
						int requireCloudNum = shipmentNum - microEffectiveStock;
						ProductInventoryDetail inventoryDetailParam = new ProductInventoryDetail();
						inventoryDetailParam.setSku(sku);
						inventoryDetailParam.setWarehouseId(warehouseId);
						List<ProductInventoryDetail> productInventoryDetailList = productInventoryDetailMapper.selectinventoryDetailListByParam(inventoryDetailParam);
						this.generateOrderInventoryDeductRecord(productInventoryDetailList, requireCloudNum, orderNo, warehouseId);


						ProductInventoryTotal productInventoryTotal = this.getProductInventoryTotal(warehouseId, sku);
						if(productInventoryTotal.getStock() < requireCloudNum) {//云仓库存不足
							for (ProductInventoryDetail productInventoryDetail : productInventoryDetailList) {
								productInventoryDetail.setStock(0);
								productInventoryDetail.setUpdateTime(new Date());
								productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetail);
							}
						}

						MsiteStockOutDetail msiteStockOutDetail = this.getMsiteStockOutDetail(warehouseId, warehouseName,requireCloudNum, purchasePrice, 1);
						msiteMicroStockOutDetailList.add(msiteStockOutDetail);

						Map detailMap = Maps.newHashMap();
						detailMap.put(sku, msiteMicroStockOutDetailList);
						map.putAll(detailMap);
					}

					if (microEffectiveStock >= shipmentNum) {//微仓充足
						List<ProductMicroInventoryDetail> productMicroInventoryDetailList = this.getProductMicroInventoryDetails(account, warehouseId, sku, null);

						Map detailMap = Maps.newHashMap();
						detailMap.put(sku, this.generateMsiteOrderMicroInventoryDeductRecord(productMicroInventoryDetailList, shipmentNum, orderNo, account, sku, warehouseId, warehouseName));
						map.putAll(detailMap);
					}
				} else {//出云仓
					ProductInventoryTotal productInventoryTotal = this.getProductInventoryTotal(warehouseId, sku);

					//云仓出仓记录
					ProductInventoryDetail inventoryDetailParam = new ProductInventoryDetail();
					inventoryDetailParam.setSku(sku);
					inventoryDetailParam.setWarehouseId(warehouseId);
					List<ProductInventoryDetail> productInventoryDetailList = productInventoryDetailMapper.selectinventoryDetailListByParam(inventoryDetailParam);

					this.generateOrderInventoryDeductRecord(productInventoryDetailList, shipmentNum, orderNo, warehouseId);

					if(productInventoryTotal.getStock() < shipmentNum) {//云仓库存不足
						for (ProductInventoryDetail productInventoryDetail : productInventoryDetailList) {
							productInventoryDetail.setStock(0);
							productInventoryDetail.setUpdateTime(new Date());
							productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetail);
						}
					}


					MsiteStockOutDetail msiteStockOutDetail = this.getMsiteStockOutDetail(warehouseId, warehouseName,shipmentNum, purchasePrice, 1);

					Map detailMap = Maps.newHashMap();
					detailMap.put(sku, msiteStockOutDetail);
					map.putAll(detailMap);
				}
			}
			msiteStockOutResult.setType(1);
			msiteStockOutResult.setDetails(map);
		}
		return msiteStockOutResult;
	}
	
	@Override
	public int total(MicroGoodsInventoryForm mInventoryForm) {
		ProductMicroInventoryDetailSearchDto parms =new ProductMicroInventoryDetailSearchDto();
		parms.setAccount(mInventoryForm.getDistributorEmail());
		parms.setCurrPage(mInventoryForm.getPageNo());
		parms.setPageSize(mInventoryForm.getPageSize());
		parms.setWarehouseId(mInventoryForm.getWarehouseId());
		parms.setTitle(mInventoryForm.getProductTitle());
		parms.setAvaliableStock(mInventoryForm.getAvaliableStock());
		parms.setKey(mInventoryForm.getKey());
		int total = productMicroInventoryTotalMapper.getTotal(parms);
		return total;
	}

	@Override
	public List<MicroGoodsInventory> queryByParams(MicroGoodsInventoryForm mInventoryForm) {
		
		ProductMicroInventoryDetailSearchDto parms =new ProductMicroInventoryDetailSearchDto();
		parms.setAccount(mInventoryForm.getDistributorEmail());
		parms.setCurrPage(mInventoryForm.getPageNo());
		parms.setPageSize(mInventoryForm.getPageSize());
		parms.setWarehouseId(mInventoryForm.getWarehouseId());
		parms.setTitle(mInventoryForm.getProductTitle());
		parms.setAvaliableStock(mInventoryForm.getAvaliableStock());
		parms.setKey(mInventoryForm.getKey());
		parms.setCategoryId(mInventoryForm.getProductCategoryId());
		List<MicroInventoryQueryResult> selectByParams = productMicroInventoryTotalMapper.selectForMemberViewByParam(parms);
		//List<MicroInventoryQueryResult> selectByParams = this.selectByParams(parms);
		List<MicroGoodsInventory> resList=this.microInventoryQueryResultListToMicroGoodsInventoryList(selectByParams);
		
		//List<MicroGoodsInventory> resList = mInventoryMapper.query(mInventory, pageNum, length);
		return resList;
		
	}

	/**
	 * 构造前台查看微仓某account详情返回结果
	 * @param selectByParams
	 * @return
	 */
	private List<MicroGoodsInventory> microInventoryQueryResultListToMicroGoodsInventoryList(
			List<MicroInventoryQueryResult> selectByParams) {
		List<MicroGoodsInventory>  resList=Lists.newArrayList();
		MicroGoodsInventory microGoodsInventory=null;
		for (MicroInventoryQueryResult result : selectByParams) {
			microGoodsInventory=new MicroGoodsInventory();
			microGoodsInventory.setAvaliableStock(result.getStock());
			microGoodsInventory.setFrozenStock(result.getLockNum());
			microGoodsInventory.setProductTitle(result.getProductTitle());
			microGoodsInventory.setSku(result.getSku());
			microGoodsInventory.setWarehouseId(result.getWarehouseId());
			microGoodsInventory.setWarehouseName(result.getWarehouseName());
			microGoodsInventory.setMwarehouseName(result.getAccount());
			microGoodsInventory.setTotalStock(result.getStock()+result.getLockNum());
			resList.add(microGoodsInventory);
		}
		return resList;
	}

	@Override
	public List<ProductMicroInventoryInRecord> getProductMicroInventoryDetail(String s_main, String email) {
		JsonNode jsonNode = Json.parse(s_main);
		String sku = jsonNode.get("sku").asText();
		Integer warehouseId = jsonNode.get("warehouseId").asInt();
		ProductMicroInventoryInRecord microInRecordParam=new ProductMicroInventoryInRecord();
		microInRecordParam.setSku(sku);
		microInRecordParam.setAccount(email);
		microInRecordParam.setWarehouseId(warehouseId);
		List<ProductMicroInventoryInRecord> inRecordList = productMicroInventoryInRecordMapper.selectMicroInRecordListForRelease(microInRecordParam);
		
		//减去有效锁库记录锁定的数量
		Iterator<ProductMicroInventoryInRecord> inRecordListIterator=inRecordList.iterator();
		while(inRecordListIterator.hasNext()){
			ProductMicroInventoryInRecord inRecord = inRecordListIterator.next();
			ProductMicroInventoryOrderLockDto lockDto=new ProductMicroInventoryOrderLockDto();
			lockDto.setMicroInRecordId(inRecord.getId());
			lockDto.setIsEffective(-1);
			int lockNum=0;
			List<ProductMicroInventoryOrderLock> lockList = productMicroInventoryOrderLockMapper.query(lockDto);
			if(lockList!=null && lockList.size()>0){
				for (ProductMicroInventoryOrderLock productMicroInventoryOrderLock : lockList) {
					lockNum+=productMicroInventoryOrderLock.getStockLocked();
				}
			}
			if(inRecord.getResidueNum()<=lockNum){
				inRecordListIterator.remove();
			}else {
				inRecord.setResidueNum(inRecord.getResidueNum()-lockNum);
			}
		}	
		return inRecordList;
	}

	@Override
	public synchronized CancleSaleOrderResult cancleMsiteSaleOrderWithInventory(String jsonData) {
		Logger.info(">>>>>>>>>>>>>>>>>>>cancleMsiteSaleOrderWithInventor>>postDate:{}",jsonData);
		JsonNode jsonNode = Json.parse(jsonData);
		String orderNo = jsonNode.get("orderNo").asText();

		CancleSaleOrderResult cancleSaleOrderResult = new CancleSaleOrderResult();
		if (orderNo == null || "".equals(orderNo)) {
			cancleSaleOrderResult.setResult(false);
			cancleSaleOrderResult.setMsg("单号不能为空！");
		}

		Order orderParam = new Order();
		orderParam.setOrderNo(orderNo);
		Order order = orderMapper.selectOrderByParams(orderParam);
		if (order == null) {
			cancleSaleOrderResult.setResult(false);
			cancleSaleOrderResult.setMsg("没有对应的订单记录，数据异常！");
			return cancleSaleOrderResult;
		}

		if (order.getStatus() == 5) {//该单号已取消
			cancleSaleOrderResult.setResult(false);
			cancleSaleOrderResult.setMsg("该单号已取消！");
			return cancleSaleOrderResult;
		}

		//还原微仓出库部分
		List<OrderMicroInventoryDeductRecord> orderMicroInventoryDeductRecords = orderMicroInventoryDeductRecordMapper.listByOrderNo(orderNo);
		if (orderMicroInventoryDeductRecords != null && orderMicroInventoryDeductRecords.size() > 0) {//检查微仓出仓记录
			for (OrderMicroInventoryDeductRecord orderMicroInventoryDeductRecord : orderMicroInventoryDeductRecords) {
				//查询微仓明细数据
				ProductMicroInventoryDetail productMicroDetailParam = new ProductMicroInventoryDetail();
				productMicroDetailParam.setAccount(orderMicroInventoryDeductRecord.getAccount());
				productMicroDetailParam.setExpirationDate(orderMicroInventoryDeductRecord.getExpirationDate());
				productMicroDetailParam.setSku(orderMicroInventoryDeductRecord.getSku());
				productMicroDetailParam.setIsGift(orderMicroInventoryDeductRecord.getIsGift());
				productMicroDetailParam.setWarehouseId(orderMicroInventoryDeductRecord.getWarehouseId());

				//查询入仓记录
				ProductMicroInventoryInRecord productMicroInventoryInRecord = productMicroInventoryInRecordMapper.selectByPrimaryKey(orderMicroInventoryDeductRecord.getMicroInRecordId());
				if (productMicroInventoryInRecord == null) {
					cancleSaleOrderResult.setResult(false);
					cancleSaleOrderResult.setMsg("没有对应的入仓记录，数据异常！");
					return cancleSaleOrderResult;
				}

				productMicroDetailParam.setCapfee(productMicroInventoryInRecord.getCapfee());
				productMicroDetailParam.setPurchasePrice(productMicroInventoryInRecord.getPurchasePrice());

				ProductMicroInventoryDetail productMicroInventoryDetail = productMicroInventoryDetailMapper.selectByParam(productMicroDetailParam);
				if (productMicroInventoryDetail != null) {
					int num = productMicroInventoryDetail.getStock() + orderMicroInventoryDeductRecord.getQty();
					productMicroInventoryDetail.setStock(num);
					productMicroInventoryDetail.setUpdateTime(new Date());
					productMicroInventoryDetailMapper.updateByPrimaryKeySelective(productMicroInventoryDetail);
				} else {
					cancleSaleOrderResult.setResult(false);
					cancleSaleOrderResult.setMsg("没有对应的微仓明细记录，数据异常！");
					return cancleSaleOrderResult;
				}

				int returnSum = orderMicroInventoryDeductRecord.getQty();

				//微仓入仓记录还原(可用数还原)
				productMicroInventoryInRecord.setResidueNum(productMicroInventoryInRecord.getResidueNum() + returnSum);
				productMicroInventoryInRecord.setUpdateTime(new Date());
				productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(productMicroInventoryInRecord);



				//微仓总仓还原
				ProductMicroInventoryTotal productMicroInventoryTotalParam = new ProductMicroInventoryTotal();
				productMicroInventoryTotalParam.setAccount(orderMicroInventoryDeductRecord.getAccount());
				productMicroInventoryTotalParam.setSku(orderMicroInventoryDeductRecord.getSku());
				productMicroInventoryTotalParam.setWarehouseId(orderMicroInventoryDeductRecord.getWarehouseId());
				ProductMicroInventoryTotal productMicroInventoryTotal = productMicroInventoryTotalMapper.selectByParam(productMicroInventoryTotalParam);
				if (productMicroInventoryTotal != null) {
					productMicroInventoryTotal.setStock(productMicroInventoryTotal.getStock() + returnSum);
					productMicroInventoryTotal.setUpdateTime(new Date());
					productMicroInventoryTotalMapper.updateByPrimaryKeySelective(productMicroInventoryTotal);
				} else {
					cancleSaleOrderResult.setResult(false);
					cancleSaleOrderResult.setMsg("没有对应的微仓总仓记录，数据异常！");
					return cancleSaleOrderResult;
				}
			}
		}

		//还原云仓出库部分
		List<OrderInventoryDeductRecord> orderInventoryDeductRecordList = orderInventoryDeductRecordMapper.listByOrderNo(orderNo);
		if (orderInventoryDeductRecordList != null && orderInventoryDeductRecordList.size() > 0) {
			for (OrderInventoryDeductRecord orderInventoryDeductRecord : orderInventoryDeductRecordList) {
				//云仓入仓记录
				this.generateOrderInventoryInRecord(orderNo, orderInventoryDeductRecord.getExpirationDate(), orderInventoryDeductRecord.getSku(), orderInventoryDeductRecord.getWarehouseId(), orderInventoryDeductRecord.getWarehouseName(), orderInventoryDeductRecord.getQty());

				//查询云仓明细
				ProductInventoryDetail productInventoryDetailParam = new ProductInventoryDetail();
				productInventoryDetailParam.setSku(orderInventoryDeductRecord.getSku());
				productInventoryDetailParam.setWarehouseId(orderInventoryDeductRecord.getWarehouseId());
				productInventoryDetailParam.setExpirationDate(orderInventoryDeductRecord.getExpirationDate());
				ProductInventoryDetail productInventoryDetail = productInventoryDetailMapper.selectByParam(productInventoryDetailParam);
				if (productInventoryDetail == null) {
					cancleSaleOrderResult.setResult(false);
					cancleSaleOrderResult.setMsg("没有对应的云仓明细，数据异常！");
					return cancleSaleOrderResult;
				}
				productInventoryDetail.setStock(productInventoryDetail.getStock() + orderInventoryDeductRecord.getQty());
				productInventoryDetail.setUpdateTime(new Date());
				productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetail);
				//云仓总仓还原
				ProductInventoryTotal productInventoryTotal = this.getProductInventoryTotal(orderInventoryDeductRecord.getWarehouseId(), orderInventoryDeductRecord.getSku());
				if (productInventoryTotal == null) {
					cancleSaleOrderResult.setResult(false);
					cancleSaleOrderResult.setMsg("没有对应的云仓记录，数据异常！");
					return cancleSaleOrderResult;
				}

				productInventoryTotal.setStock(productInventoryTotal.getStock() + orderInventoryDeductRecord.getQty());
				productInventoryTotal.setUpdateTime(new Date());
				productInventoryTotalMapper.updateByPrimaryKeySelective(productInventoryTotal);
			}
		}

		order.setStatus(5);//标记该订单已取消
		order.setUpdateTime(new Date());
		orderMapper.updateByPrimaryKeySelective(order);
		cancleSaleOrderResult.setResult(true);
		return cancleSaleOrderResult;
	}

	/**
	 * 生成云仓入仓记录
	 *
	 * @param orderNo
	 * @param expirationDate
	 * @param sku
	 * @param warehouseId
	 * @param warehouseName
	 * @param qty
	 */
	private void generateOrderInventoryInRecord(String orderNo, Date expirationDate, String sku, int warehouseId, String warehouseName, int qty) {
		ProductInventoryInRecord productInventoryInRecord = new ProductInventoryInRecord();
		productInventoryInRecord.setOrderNo(orderNo);
		productInventoryInRecord.setExpirationDate(expirationDate);
		productInventoryInRecord.setSku(sku);
		productInventoryInRecord.setWarehouseId(warehouseId);
		productInventoryInRecord.setWarehouseName(warehouseName);
		productInventoryInRecord.setQty(qty);
		productInventoryInRecord.setCreateTime(new Date());
		productInventoryInRecordMapper.insertSelective(productInventoryInRecord);
	}

	private MsiteStockOutDetail getMsiteStockOutDetail(int warehouseId, String warehouseName,int shipmentNum, double purchasePrice, Integer type) {
		MsiteStockOutDetail msiteStockOutDetail = new MsiteStockOutDetail();
		msiteStockOutDetail.setQty(shipmentNum);
		msiteStockOutDetail.setWarehouseId(warehouseId);
		msiteStockOutDetail.setWarehouseName(warehouseName);
		msiteStockOutDetail.setPurchasePrice(purchasePrice);
		msiteStockOutDetail.setType(type);
		return msiteStockOutDetail;
	}

	/**
	 * 执行M站云仓出库
	 *
	 * @param productInventoryDetailList
	 * @param locNum
	 * @param orderNo
	 */
	private void generateOrderInventoryDeductRecord(List<ProductInventoryDetail> productInventoryDetailList, int locNum, String orderNo, int warehouseId) {
		for (ProductInventoryDetail productInventoryDetail : productInventoryDetailList) {
			int inventoryDetailStockNum = productInventoryDetail.getStock();
			if(inventoryDetailStockNum >= locNum){
				//云仓详情扣减
				productInventoryDetail.setStock(inventoryDetailStockNum - locNum);
				productInventoryDetail.setUpdateTime(new Date());
				productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetail);

				//总仓查询后扣减
				ProductInventoryTotal productInventoryTotal = this.getProductInventoryTotal(warehouseId, productInventoryDetail.getSku());
				productInventoryTotal.setStock(productInventoryTotal.getStock() - locNum);
				productInventoryTotal.setUpdateTime(new Date());
				productInventoryTotalMapper.updateByPrimaryKeySelective(productInventoryTotal);

				//generateOrderInventoryDeductRecord
				this.generateOrderInventoryDeductRecord(orderNo, productInventoryDetail, locNum);
				break;
			}else{
				productInventoryDetail.setStock(0);
				productInventoryDetail.setUpdateTime(new Date());
				productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetail);

				//总仓查询后扣减
				ProductInventoryTotal productInventoryTotal = this.getProductInventoryTotal(warehouseId, productInventoryDetail.getSku());
				productInventoryTotal.setStock(productInventoryTotal.getStock() - locNum);
				productInventoryTotal.setUpdateTime(new Date());
				productInventoryTotalMapper.updateByPrimaryKeySelective(productInventoryTotal);

				//generateOrderInventoryDeductRecord
				this.generateOrderInventoryDeductRecord(orderNo, productInventoryDetail, inventoryDetailStockNum);
				locNum -= inventoryDetailStockNum;
			}
		}
	}


	/**
	 * 执行M站微仓出库
	 *  @param
	 * @param shipmentNum
	 * @param orderNo
	 */
	private List<MsiteStockOutDetail> generateMsiteOrderMicroInventoryDeductRecord(List<ProductMicroInventoryDetail> productMicroInventoryDetailList, int shipmentNum, String orderNo, String account, String sku, int warehouseId, String warehouseName) {
		List<MsiteStockOutDetail> msiteStockOutDetailList = Lists.newArrayList();
		int reduceNum = shipmentNum;
		for (ProductMicroInventoryDetail productMicroInventoryDetail : productMicroInventoryDetailList) {
			int microEffectiveStock = productMicroInventoryDetail.getStock() - productMicroInventoryDetail.getLockStock();
			if (microEffectiveStock > 0) {
				if (microEffectiveStock >= reduceNum) {
					//查询微仓入仓记录
					List<ProductMicroInventoryInRecord> productMicroInventoryInRecordList = this.getProductMicroInventoryInRecords(account, warehouseId, sku, productMicroInventoryDetail);

					int tmpStock = reduceNum;
					for (int j = 0; j < productMicroInventoryInRecordList.size(); j++) {
						ProductMicroInventoryInRecord productMicroInventoryInRecord = productMicroInventoryInRecordList.get(j);
						//根据入仓主键查询已锁定数
						int lockSum = this.getLockSumById(productMicroInventoryInRecord.getId());
						int canOutSum = productMicroInventoryInRecord.getResidueNum() - lockSum;//可扣减数量
						if (canOutSum >= tmpStock) {
							productMicroInventoryInRecord.setResidueNum(productMicroInventoryInRecord.getResidueNum() - tmpStock);
							productMicroInventoryInRecord.setUpdateTime(new Date());
							productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(productMicroInventoryInRecord);
							//微仓出仓记录
							this.generateOrderMicroInventoryDeductRecord(warehouseId, warehouseName, orderNo, sku, tmpStock, productMicroInventoryDetail.getExpirationDate(), account, productMicroInventoryInRecord.getPurchasePrice(), productMicroInventoryInRecord.getId(), productMicroInventoryInRecord.getIsGift());

							BigDecimal purchasePrice = new BigDecimal(String.valueOf(productMicroInventoryInRecord.getPurchasePrice()));
							MsiteStockOutDetail msiteStockOutDetail = this.getMsiteStockOutDetail(warehouseId, warehouseName,tmpStock, purchasePrice.doubleValue(), 0);
							msiteStockOutDetailList.add(msiteStockOutDetail);

							break;
						} else {
							if (canOutSum > 0) {
								tmpStock = tmpStock - canOutSum;
								productMicroInventoryInRecord.setResidueNum(productMicroInventoryInRecord.getResidueNum() - canOutSum);
								productMicroInventoryInRecord.setUpdateTime(new Date());
								productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(productMicroInventoryInRecord);
								//微仓出仓记录
								this.generateOrderMicroInventoryDeductRecord(warehouseId, warehouseName, orderNo, sku, canOutSum, productMicroInventoryDetail.getExpirationDate(), account, productMicroInventoryInRecord.getPurchasePrice(), productMicroInventoryInRecord.getId(), productMicroInventoryInRecord.getIsGift());

								BigDecimal purchasePrice = new BigDecimal(String.valueOf(productMicroInventoryInRecord.getPurchasePrice()));
								MsiteStockOutDetail msiteStockOutDetail = this.getMsiteStockOutDetail(warehouseId, warehouseName,canOutSum, purchasePrice.doubleValue(), 0);
								msiteStockOutDetailList.add(msiteStockOutDetail);

							}

						}
					}

					//微仓明细表扣减
					productMicroInventoryDetail.setStock(productMicroInventoryDetail.getStock() - reduceNum);
					productMicroInventoryDetail.setUpdateTime(new Date());
					productMicroInventoryDetailMapper.updateByPrimaryKeySelective(productMicroInventoryDetail);
					break;
				} else {
					List<ProductMicroInventoryInRecord> productMicroInventoryInRecordList = this.getProductMicroInventoryInRecords(account, warehouseId, sku, productMicroInventoryDetail);

					int tmpStock = microEffectiveStock;
					for (int j = 0; j < productMicroInventoryInRecordList.size(); j++) {
						ProductMicroInventoryInRecord productMicroInventoryInRecord = productMicroInventoryInRecordList.get(j);
						//根据入仓主键查询已锁定数
						int lockSum = this.getLockSumById(productMicroInventoryInRecord.getId());
						int canOutSum = productMicroInventoryInRecord.getResidueNum() - lockSum;//可扣减数量
						if (canOutSum >= tmpStock) {
							productMicroInventoryInRecord.setResidueNum(productMicroInventoryInRecord.getResidueNum() - tmpStock);
							productMicroInventoryInRecord.setUpdateTime(new Date());
							productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(productMicroInventoryInRecord);
							//微仓出仓记录
							this.generateOrderMicroInventoryDeductRecord(warehouseId, warehouseName, orderNo, sku, tmpStock, productMicroInventoryDetail.getExpirationDate(), account, productMicroInventoryInRecord.getPurchasePrice(), productMicroInventoryInRecord.getId(), productMicroInventoryInRecord.getIsGift());

							BigDecimal purchasePrice = new BigDecimal(String.valueOf(productMicroInventoryInRecord.getPurchasePrice()));
							MsiteStockOutDetail msiteStockOutDetail = this.getMsiteStockOutDetail(warehouseId, warehouseName, tmpStock, purchasePrice.doubleValue(), 0);
							msiteStockOutDetailList.add(msiteStockOutDetail);

							break;
						} else {
							if (canOutSum > 0) {
								tmpStock = tmpStock - productMicroInventoryInRecord.getResidueNum();
								productMicroInventoryInRecord.setResidueNum(productMicroInventoryInRecord.getResidueNum() - canOutSum);
								productMicroInventoryInRecord.setUpdateTime(new Date());
								productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(productMicroInventoryInRecord);
								//微仓出仓记录
								this.generateOrderMicroInventoryDeductRecord(warehouseId, warehouseName, orderNo, sku, canOutSum, productMicroInventoryDetail.getExpirationDate(), account, productMicroInventoryInRecord.getPurchasePrice(), productMicroInventoryInRecord.getId(), productMicroInventoryInRecord.getIsGift());


								BigDecimal purchasePrice = new BigDecimal(String.valueOf(productMicroInventoryInRecord.getPurchasePrice()));
								MsiteStockOutDetail msiteStockOutDetail = this.getMsiteStockOutDetail(warehouseId, warehouseName,canOutSum, purchasePrice.doubleValue(), 0);
								msiteStockOutDetailList.add(msiteStockOutDetail);
							}
						}
					}
					reduceNum = reduceNum - microEffectiveStock;
					//微仓明细表扣减
					productMicroInventoryDetail.setStock(productMicroInventoryDetail.getStock() - microEffectiveStock);
					productMicroInventoryDetail.setUpdateTime(new Date());
					productMicroInventoryDetailMapper.updateByPrimaryKeySelective(productMicroInventoryDetail);
				}
			}
		}

		//微仓总计表扣减
		ProductMicroInventoryTotal productMicroInventoryTotalParam = new ProductMicroInventoryTotal();
		productMicroInventoryTotalParam.setWarehouseId(warehouseId);
		productMicroInventoryTotalParam.setSku(sku);
		productMicroInventoryTotalParam.setAccount(account);

		ProductMicroInventoryTotal productMicroInventoryTotal = productMicroInventoryTotalMapper.selectByParam(productMicroInventoryTotalParam);
		int reduceAfterNum = productMicroInventoryTotal.getStock() - shipmentNum;
		productMicroInventoryTotal.setStock(reduceAfterNum);
		productMicroInventoryTotal.setUpdateTime(new Date());
//		productMicroInventoryTotal.setResidueNum(productMicroInventoryTotal.getResidueNum() - shipmentNum);
		productMicroInventoryTotalMapper.updateByPrimaryKeySelective(productMicroInventoryTotal);

		return msiteStockOutDetailList;
	}


	/**
	 * 生成云仓出库记录
	 *
	 * @param orderNo
	 * @param productInventoryDetail
	 * @param inventoryDetailStockNum
	 */
	private void generateOrderInventoryDeductRecord(String orderNo, ProductInventoryDetail productInventoryDetail, int inventoryDetailStockNum) {
		OrderInventoryDeductRecord inventoryDeductRecord = new OrderInventoryDeductRecord();
		inventoryDeductRecord.setSku(productInventoryDetail.getSku());
		inventoryDeductRecord.setWarehouseId(productInventoryDetail.getWarehouseId());
		inventoryDeductRecord.setWarehouseName(productInventoryDetail.getWarehouseName());
		inventoryDeductRecord.setExpirationDate(productInventoryDetail.getExpirationDate());
		inventoryDeductRecord.setOrderNo(orderNo);
		inventoryDeductRecord.setQty(inventoryDetailStockNum);
		inventoryDeductRecord.setCreateTime(new Date());
		inventoryDeductRecord.setUpdateTime(new Date());
		orderInventoryDeductRecordMapper.insertSelective(inventoryDeductRecord);
	}

	
	@Override
	public Page<ProductMicroInventoryDetail> selectMicroDetailByParam(ProductMicroInventoryDetailSearchDto reqParam) {
		if(reqParam.getExpirationDate()!=null){
			Calendar cal = Calendar.getInstance();  
			cal.setTime(reqParam.getExpirationDate());
			cal.add(Calendar.MONTH, 1);
			reqParam.setExpirationDateEnd(cal.getTime());
		}
		List<ProductMicroInventoryDetail> resultList= microInventoryDetailMapper.selectBySerarchDto(reqParam);
		int total=microInventoryDetailMapper.selectTotalBySerarchDto(reqParam);
		return new Page<ProductMicroInventoryDetail>(reqParam.getCurrPage(),reqParam.getPageSize(),total,resultList);
	}

	@Override
	public List<ProductMicroInventoryInRecord> getPurchaseDetail(Integer id) {
		ProductMicroInventoryDetail microDetail = microInventoryDetailMapper.selectByPrimaryKey(id);
		if(microDetail==null){
			return null;
		}
		ProductMicroInventoryInRecordDto param=new ProductMicroInventoryInRecordDto();
		param.setAccount(microDetail.getAccount());
		param.setSku(microDetail.getSku());
		param.setWarehouseId(microDetail.getWarehouseId());
		int isGift =(int) microDetail.getIsGift();
		param.setIsGift(isGift);
		param.setCapfee(microDetail.getCapfee());
		param.setPurchasePrice(microDetail.getPurchasePrice());
		param.setExpirationDate(microDetail.getExpirationDate());
		param.setMoreThan(1);
		List<ProductMicroInventoryInRecord> result=productMicroInventoryInRecordMapper.listMicroInRecordList(param);
		Collections.sort(result, new Comparator<ProductMicroInventoryInRecord>() {

			@Override
			public int compare(ProductMicroInventoryInRecord record1, ProductMicroInventoryInRecord record2) {//排序剩余量从大到小
				if(record2.getResidueNum()-record1.getResidueNum()>0){
					return 1;
				}
				if(record2.getResidueNum()-record1.getResidueNum()==0){
					return 0;
				}
				return -1;
			}
		});
		return result;
	}

	@Override
	public List<MicroRealWarehouseDto> queryMicroWarehouse(String email) {
		return productMicroInventoryTotalMapper.selectbyemail(email);
	}

	@Override
	@Transactional
	public ProductMicroInventoyResult releaseMicroStockToCloud(ProductMicroInventoryInRecord microInRecordParam,String operator) {
		//查询入仓记录
		List<ProductMicroInventoryInRecord> microInRecordResult=productMicroInventoryInRecordMapper.selectMicroInRecordListForRelease(microInRecordParam);
		if(microInRecordResult==null || microInRecordResult.size()<=0){
			return new ProductMicroInventoyResult(false,"无入库记录或入库库存为0",null,null);
		}
		for (ProductMicroInventoryInRecord productMicroInventoryInRecord : microInRecordResult) {
			Integer microInRecordId = productMicroInventoryInRecord.getId();
			//查询该入仓记录是否有生效中的销售订单锁定库存
			ProductMicroInventoryOrderLock microLockParam=new ProductMicroInventoryOrderLock();
			microLockParam.setMicroInRecordId(microInRecordId);
			microLockParam.setIsEffective((short) -1);
			List<ProductMicroInventoryOrderLock> microLockResult=productMicroInventoryOrderLockMapper.selectListsByParams(microLockParam);
			if(microLockResult!=null && microLockResult.size()>0){
				return new ProductMicroInventoyResult(false,"该sku库存已被销售订单锁定",null,null);
			}
		}
		
		for (ProductMicroInventoryInRecord microInRecord : microInRecordResult) {
			Integer residueNum = microInRecord.getResidueNum();
			
			ProductMicroInventoryDetail microDetailParam = new ProductMicroInventoryDetail();
			microDetailParam.setAccount(microInRecord.getAccount());
			microDetailParam.setSku(microInRecord.getSku());
			microDetailParam.setExpirationDate(microInRecord.getExpirationDate());
			microDetailParam.setWarehouseId(microInRecord.getWarehouseId());
			microDetailParam.setIsGift(microInRecord.getIsGift());
			microDetailParam.setPurchasePrice(microInRecord.getPurchasePrice());
			microDetailParam.setCapfee(microInRecord.getCapfee());
			ProductMicroInventoryDetail microDetail = microInventoryDetailMapper.selectByParam(microDetailParam);
			
			microDetail.setStock(microDetail.getStock()-residueNum);
			microDetail.setUpdateTime(new Date());
			microInventoryDetailMapper.updateByPrimaryKeySelective(microDetail);
			microInRecord.setResidueNum(0);
			microInRecord.setUpdateTime(new Date());
			productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(microInRecord);
			String orderNo = operator + System.currentTimeMillis();//特殊的单号
			//微仓出仓记录
			this.generateOrderMicroInventoryDeductRecord(microInRecord.getWarehouseId(), microInRecord.getWarehouseName(), orderNo, 
					microInRecord.getSku(), residueNum, microInRecord.getExpirationDate(), microInRecord.getAccount(), microInRecord.getPurchasePrice(), microInRecord.getId(), microInRecord.getIsGift());
			
			ProductMicroInventoryTotal microTotalParam=new ProductMicroInventoryTotal();
			microTotalParam.setSku(microInRecord.getSku());
			microTotalParam.setAccount(microInRecord.getAccount());
			microTotalParam.setWarehouseId(microInRecord.getWarehouseId());
			ProductMicroInventoryTotal resultMicoTotal = productMicroInventoryTotalMapper.selectByParam(microTotalParam);
			resultMicoTotal.setStock(resultMicoTotal.getStock()-residueNum);
//			resultMicoTotal.setResidueNum(resultMicoTotal.getResidueNum()-residueNum);
			resultMicoTotal.setUpdateTime(new Date());
			productMicroInventoryTotalMapper.updateByPrimaryKeySelective(resultMicoTotal);
			
			synchronized (monitor) {
				//还原云仓
				ProductInventoryDetail inventoryDetailParam = new ProductInventoryDetail();
				inventoryDetailParam.setSku(microInRecord.getSku());
				inventoryDetailParam.setWarehouseId(microInRecord.getWarehouseId());
				inventoryDetailParam.setExpirationDate(microInRecord.getExpirationDate());
				ProductInventoryDetail inventoryDetailResult = productInventoryDetailMapper
						.selectByParam(inventoryDetailParam);
				if(inventoryDetailResult==null){
					return new ProductMicroInventoyResult(false,"云仓无该商品",null,null);
				}
				inventoryDetailResult.setStock(inventoryDetailResult.getStock() + residueNum);
				inventoryDetailResult.setUpdateTime(new Date());
				productInventoryDetailMapper.updateByPrimaryKeySelective(inventoryDetailResult);
				
				ProductInventoryTotal inventoryTotalParam = new ProductInventoryTotal();
				inventoryTotalParam.setSku(microInRecord.getSku());
				inventoryTotalParam.setWarehouseId(microInRecord.getWarehouseId());
				ProductInventoryTotal inventoryTotal = productInventoryTotalMapper
						.selectByParam(inventoryTotalParam);
				inventoryTotal.setStock(inventoryTotal.getStock() + residueNum);
				//inventoryTotal.setResidueNum(inventoryTotal.getResidueNum()+residueNum);
				inventoryTotal.setUpdateTime(new Date());
				productInventoryTotalMapper.updateByPrimaryKeySelective(inventoryTotal);
				
				
				ProductInventoryInRecord inventoryInRecord = new ProductInventoryInRecord();
				inventoryInRecord.setOrderNo(orderNo);
				inventoryInRecord.setExpirationDate(microInRecord.getExpirationDate());
				inventoryInRecord.setSku(microInRecord.getSku());
				inventoryInRecord.setWarehouseId(microInRecord.getWarehouseId());
				inventoryInRecord.setWarehouseName(microInRecord.getWarehouseName());
				inventoryInRecord.setQty(residueNum);
				inventoryInRecord.setCreateTime(new Date());
				productInventoryInRecordMapper.insertSelective(inventoryInRecord);
			}
		}
		
		return new ProductMicroInventoyResult(true,"库存释放成功!",null,null);
	}

	@Override
	public void asyncMicroInventory() {
		List<MicroGoodsInventory> microGoodsInventories = microGoodsInventoryMapper.queryByList();
		if (microGoodsInventories != null && microGoodsInventories.size() > 0) {
			Logger.info(">>>>>>>>>>>>>>>asyncMicroInventory 要同步的数据总数：{}", microGoodsInventories.size());
			int successNum = 0;
			List<String> failDetail = new ArrayList<String>();
			Set<String> unsyncedStockDetail = new HashSet<String>();
			for (MicroGoodsInventory microGoodsInventory : microGoodsInventories) {
				//查找微仓库存变化表
				String sku = microGoodsInventory.getSku();
				int stock = microGoodsInventory.getAvaliableStock();//剩余库存
			
				String account = microGoodsInventory.getMwarehouseName();
				Logger.info(">>>>>>>>>>>>>>>asyncMicroInventory sku:{}, stock:{}, account:{}", sku, stock,account);
				List<InventoryChangeHistory> changeHistories = inventoryChangeHistoryMapper.query(microGoodsInventory.getWarehouseId(),microGoodsInventory.getSku(), account);
				if (changeHistories != null && changeHistories.size() > 0) {
					int inRecordSum = 0;//采购历史总数
					for (InventoryChangeHistory inventoryChangeHistory : changeHistories) {
						inRecordSum += inventoryChangeHistory.getNum();
					}
					if (inRecordSum < stock) {
						Logger.info(">>>>>>>>>>>>>>>asyncMicroInventory 账户[{}]下sku:[{}]凑不够[{}]个", account,sku, stock);
						failDetail.add("account:" + account + " sku:" + sku + " stock:" + stock);
					} else {
						successNum++;
						int reduceNum = stock;
						for (InventoryChangeHistory inventoryChangeHistory : changeHistories) {
							Logger.info(">>>>>>>>>>>>>>>inventoryChangeHistory[{}]", inventoryChangeHistory);
							if (inventoryChangeHistory.getNum() >= reduceNum) {

								//查看微仓入仓记录该采购单号是否已存在
								ProductMicroInventoryInRecordDto productMicroInventoryInRecordDto = new ProductMicroInventoryInRecordDto();
								productMicroInventoryInRecordDto.setOrderNo(inventoryChangeHistory.getOrderNo());
								productMicroInventoryInRecordDto.setAccount(account);
								productMicroInventoryInRecordDto.setWarehouseId(inventoryChangeHistory.getWarehouseId());
								productMicroInventoryInRecordDto.setIsGift(inventoryChangeHistory.getIsgift() == true ? 1 : 0);
								List<ProductMicroInventoryInRecord> productMicroInventoryInRecordList = productMicroInventoryInRecordMapper.listMicroInRecordList(productMicroInventoryInRecordDto);
								if (productMicroInventoryInRecordList != null && productMicroInventoryInRecordList.size() == 0) {
									//云仓出库
									ProductInventoryDetail inventoryDetailParam = new ProductInventoryDetail();
									inventoryDetailParam.setSku(sku);
									inventoryDetailParam.setWarehouseId(microGoodsInventory.getWarehouseId());
									List<ProductInventoryDetail> inventoryDetailListResult = productInventoryDetailMapper.selectinventoryDetailListByParam(inventoryDetailParam);
									if(inventoryDetailListResult == null ||inventoryDetailListResult.size()<=0){
										Logger.info("同步旧微仓数据到新库存系统失败，可能未初始化新库存系统[{}]的库存信息，检查sku[{}]在云仓中的数量",sku,sku);
										unsyncedStockDetail.add("account:" + account + " sku:" + sku + " stock:" + stock);
										break;
									}

									//微仓入仓记录
									ProductMicroInventoryInRecord microInventoryInRecord = new ProductMicroInventoryInRecord();
									microInventoryInRecord.setAccount(account);
									microInventoryInRecord.setSku(sku);

									//设置图片
									List<Map<String, String>> imgUrls = microGoodsInventoryMapper.queryImage(Arrays.asList(sku));
									if(!CollectionUtils.isEmpty(imgUrls)) {
										Iterator<String> key = null;
										Map<String, String> imgs = Maps.newHashMap();
										for (Map<String, String> map : imgUrls) {
											key = map.values().iterator();
											imgs.put(key.next(), key.next());
										}
										microInventoryInRecord.setImgUrl(imgs.get(sku));
									}

									microInventoryInRecord.setProductTitle(inventoryChangeHistory.getProductName());
									//过期时间
									LocalDate localDate = new LocalDate(2099, 1,1);
									microInventoryInRecord.setExpirationDate(localDate.toDate());
									microInventoryInRecord.setPurchaseTime(new Date());
									microInventoryInRecord.setResidueNum(reduceNum);


									microInventoryInRecord.setOrderNo(inventoryChangeHistory.getOrderNo());
									microInventoryInRecord.setIsGift(microGoodsInventory.isGift() == true ? (short) 1 : (short) 0);
									microInventoryInRecord.setWarehouseId(microGoodsInventory.getWarehouseId());
									microInventoryInRecord.setWarehouseName(microGoodsInventory.getWarehouseName());
									microInventoryInRecord.setQty(inventoryChangeHistory.getNum());

									//价格相关
									ProductDisprice productDisprice = productDispriceMapper.selectByParam(sku, microGoodsInventory.getWarehouseId());

									if(productDisprice == null){
										Logger.info("product_disprice表中不含有该组合[{}],[{}]",sku,microGoodsInventory.getWarehouseId());
										continue;
									}

									BigDecimal price = new BigDecimal(Double.toString(productDisprice.getDistributorPrice() == null ? 0 : productDisprice.getDistributorPrice()));
									BigDecimal arrivePrice = new BigDecimal(Double.toString(productDisprice.getArriveWarePrice() == null ? 0 : productDisprice.getArriveWarePrice()));
									microInventoryInRecord.setPurchasePrice(inventoryChangeHistory.getPurchasePrice() == null ? price.floatValue() : inventoryChangeHistory.getPurchasePrice().floatValue());
									microInventoryInRecord.setCapfee(inventoryChangeHistory.getCapFee() == null ? price.floatValue() : inventoryChangeHistory.getCapFee().floatValue());
									microInventoryInRecord.setArriveWarePrice(inventoryChangeHistory.getArriveWarePrice() == null ? arrivePrice.floatValue() : inventoryChangeHistory.getArriveWarePrice().floatValue());

									microInventoryInRecord.setCreateTime(new Date());
									microInventoryInRecord.setUpdateTime(new Date());
									productMicroInventoryInRecordMapper.insertSelective(microInventoryInRecord);

									//生成微仓明细
									ProductMicroInventoryDetail microInventoryDetailParam = new ProductMicroInventoryDetail();
									microInventoryDetailParam.setSku(sku);
									microInventoryDetailParam.setAccount(account);
									microInventoryDetailParam.setExpirationDate(microInventoryInRecord.getExpirationDate());
									microInventoryDetailParam.setIsGift(microInventoryInRecord.getIsGift());
									microInventoryDetailParam.setPurchasePrice(microInventoryInRecord.getPurchasePrice());
									microInventoryDetailParam.setCapfee(microInventoryInRecord.getCapfee());
									microInventoryDetailParam.setWarehouseId(microInventoryInRecord.getWarehouseId());

									ProductMicroInventoryDetail productMicroInventoryDetail = microInventoryDetailMapper.selectByParam(microInventoryDetailParam);
									if (productMicroInventoryDetail != null) {
										productMicroInventoryDetail.setUpdateTime(new Date());
										productMicroInventoryDetail.setStock(productMicroInventoryDetail.getStock() + microInventoryInRecord.getQty());
										microInventoryDetailMapper.updateByPrimaryKeySelective(productMicroInventoryDetail);
									} else {
										ProductMicroInventoryDetail newProductMicroInventoryDetail = new ProductMicroInventoryDetail();
										newProductMicroInventoryDetail.setSku(sku);
										newProductMicroInventoryDetail.setAccount(account);
										newProductMicroInventoryDetail.setStock(reduceNum);
										newProductMicroInventoryDetail.setExpirationDate(localDate.toDate());
										newProductMicroInventoryDetail.setIsGift(microGoodsInventory.isGift() == true ? (short) 1 : (short) 0);
										newProductMicroInventoryDetail.setPurchasePrice(microInventoryInRecord.getPurchasePrice());
										newProductMicroInventoryDetail.setCapfee(microInventoryInRecord.getCapfee());
										newProductMicroInventoryDetail.setWarehouseId(microGoodsInventory.getWarehouseId());
										newProductMicroInventoryDetail.setWarehouseName(microGoodsInventory.getWarehouseName());
										newProductMicroInventoryDetail.setProductTitle(microGoodsInventory.getProductTitle());
										Date now = new Date();
										newProductMicroInventoryDetail.setCreateTime(now);
										newProductMicroInventoryDetail.setUpdateTime(now);
										microInventoryDetailMapper.insertSelective(newProductMicroInventoryDetail);
									}


									//微仓总仓+
									ProductMicroInventoryTotal microInventoryTotalParam = new ProductMicroInventoryTotal();
									microInventoryTotalParam.setSku(sku);
									microInventoryTotalParam.setAccount(account);
									microInventoryTotalParam.setWarehouseId(microGoodsInventory.getWarehouseId());
									ProductMicroInventoryTotal inventoryTotalResult = productMicroInventoryTotalMapper.selectByParam(microInventoryTotalParam);
									if(inventoryTotalResult != null){
										inventoryTotalResult.setStock(inventoryTotalResult.getStock()+reduceNum);
										inventoryTotalResult.setUpdateTime(new Date());
										productMicroInventoryTotalMapper.updateByPrimaryKeySelective(inventoryTotalResult);
									}else{
										microInventoryTotalParam.setProductTitle(microGoodsInventory.getProductTitle());
										microInventoryTotalParam.setStock(reduceNum);
										microInventoryTotalParam.setWarehouseName(microGoodsInventory.getWarehouseName());
										microInventoryTotalParam.setCategoryId(microGoodsInventory.getProductCategory());
										microInventoryTotalParam.setCategoryName(microGoodsInventory.getProductCategoryName());
										microInventoryTotalParam.setCreateTime(new Date());
										productMicroInventoryTotalMapper.insertSelective(microInventoryTotalParam);
									}

									int qty = reduceNum;
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
											inventoryDeductRecord.setWarehouseName(productInventoryDetail.getWarehouseName());
											inventoryDeductRecord.setExpirationDate(productInventoryDetail.getExpirationDate());
											inventoryDeductRecord.setOrderNo(inventoryChangeHistory.getOrderNo());
											inventoryDeductRecord.setQty(qty);
											inventoryDeductRecord.setCreateTime(new Date());
											orderInventoryDeductRecordMapper.insertSelective(inventoryDeductRecord);

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
											OrderInventoryDeductRecord inventoryDeductRecord=new OrderInventoryDeductRecord();
											inventoryDeductRecord.setSku(productInventoryDetail.getSku());
											inventoryDeductRecord.setWarehouseId(productInventoryDetail.getWarehouseId());
											inventoryDeductRecord.setWarehouseName(productInventoryDetail.getWarehouseName());
											inventoryDeductRecord.setExpirationDate(productInventoryDetail.getExpirationDate());
											inventoryDeductRecord.setOrderNo(inventoryChangeHistory.getOrderNo());
											inventoryDeductRecord.setQty(inventoryDetailStockNum);
											inventoryDeductRecord.setCreateTime(new Date());
											orderInventoryDeductRecordMapper.insertSelective(inventoryDeductRecord);
											qty -= inventoryDetailStockNum;
										}
									}
								}

								break;
							} else {
								reduceNum = reduceNum - inventoryChangeHistory.getNum();

								ProductMicroInventoryInRecordDto productMicroInventoryInRecordDto = new ProductMicroInventoryInRecordDto();
								productMicroInventoryInRecordDto.setOrderNo(inventoryChangeHistory.getOrderNo());
								productMicroInventoryInRecordDto.setAccount(account);
								productMicroInventoryInRecordDto.setWarehouseId(inventoryChangeHistory.getWarehouseId());
								productMicroInventoryInRecordDto.setIsGift(inventoryChangeHistory.getIsgift() == true ?  1 :  0);
								List<ProductMicroInventoryInRecord> productMicroInventoryInRecordList = productMicroInventoryInRecordMapper.listMicroInRecordList(productMicroInventoryInRecordDto);
								if (productMicroInventoryInRecordList != null && productMicroInventoryInRecordList.size() == 0) {
									//云仓出库
									ProductInventoryDetail inventoryDetailParam = new ProductInventoryDetail();
									inventoryDetailParam.setSku(sku);
									inventoryDetailParam.setWarehouseId(microGoodsInventory.getWarehouseId());
									List<ProductInventoryDetail> inventoryDetailListResult = productInventoryDetailMapper.selectinventoryDetailListByParam(inventoryDetailParam);
									if(inventoryDetailListResult == null ||inventoryDetailListResult.size()<=0){
										Logger.info("同步旧微仓数据到新库存系统失败，可能未初始化新库存系统[{}]的库存信息，检查sku[{}]在云仓中的数量",sku,sku);
										unsyncedStockDetail.add("account:" + account + " sku:" + sku + " stock:" + stock);
										break;
									}

									//微仓入仓记录
									ProductMicroInventoryInRecord microInventoryInRecord = new ProductMicroInventoryInRecord();
									microInventoryInRecord.setAccount(account);
									microInventoryInRecord.setSku(sku);
									List<Map<String, String>> imgUrls = microGoodsInventoryMapper.queryImage(Arrays.asList(sku));
									if(!CollectionUtils.isEmpty(imgUrls)) {
										Iterator<String> key = null;
										Map<String, String> imgs = Maps.newHashMap();
										for (Map<String, String> map : imgUrls) {
											key = map.values().iterator();
											imgs.put(key.next(), key.next());
										}
										microInventoryInRecord.setImgUrl(imgs.get(sku));
									}
									microInventoryInRecord.setProductTitle(inventoryChangeHistory.getProductName());
									//过期时间
									LocalDate localDate = new LocalDate(2099, 1,1);
									microInventoryInRecord.setExpirationDate(localDate.toDate());
									microInventoryInRecord.setPurchaseTime(new Date());
									microInventoryInRecord.setResidueNum(inventoryChangeHistory.getNum());

									microInventoryInRecord.setOrderNo(inventoryChangeHistory.getOrderNo());
									microInventoryInRecord.setIsGift(inventoryChangeHistory.getIsgift() == true ? (short) 1 : (short) 0);
									microInventoryInRecord.setWarehouseId(microGoodsInventory.getWarehouseId());
									microInventoryInRecord.setWarehouseName(microGoodsInventory.getWarehouseName());
									microInventoryInRecord.setQty(inventoryChangeHistory.getNum());

									//价格相关
									ProductDisprice productDisprice = productDispriceMapper.selectByParam(sku, microGoodsInventory.getWarehouseId());

									if(productDisprice == null){
										Logger.info("product_disprice表中不含有该组合[{}],[{}]",sku,microGoodsInventory.getWarehouseId());
										continue;
									}

									BigDecimal price = new BigDecimal(Double.toString(productDisprice.getDisPrice()));
									BigDecimal arrivePrice = new BigDecimal(Double.toString(productDisprice.getArriveWarePrice()));
									microInventoryInRecord.setPurchasePrice(inventoryChangeHistory.getPurchasePrice() == null ? price.floatValue() : inventoryChangeHistory.getPurchasePrice().floatValue());
									microInventoryInRecord.setCapfee(inventoryChangeHistory.getCapFee() == null ? price.floatValue() : inventoryChangeHistory.getCapFee().floatValue());
									microInventoryInRecord.setArriveWarePrice(inventoryChangeHistory.getArriveWarePrice() == null ? arrivePrice.floatValue() : inventoryChangeHistory.getArriveWarePrice().floatValue());

									microInventoryInRecord.setCreateTime(new Date());
									microInventoryInRecord.setUpdateTime(new Date());
									productMicroInventoryInRecordMapper.insertSelective(microInventoryInRecord);

									//生成微仓明细
									ProductMicroInventoryDetail microInventoryDetailParam=new ProductMicroInventoryDetail();
									microInventoryDetailParam.setSku(sku);
									microInventoryDetailParam.setAccount(account);
									microInventoryDetailParam.setExpirationDate(microInventoryInRecord.getExpirationDate());
									microInventoryDetailParam.setIsGift(microInventoryInRecord.getIsGift());
									microInventoryDetailParam.setPurchasePrice(microInventoryInRecord.getPurchasePrice());
									microInventoryDetailParam.setCapfee(microInventoryInRecord.getCapfee());
									microInventoryDetailParam.setWarehouseId(microInventoryInRecord.getWarehouseId());
									ProductMicroInventoryDetail productMicroInventoryDetail = microInventoryDetailMapper.selectByParam(microInventoryDetailParam);
									if (productMicroInventoryDetail != null) {
										productMicroInventoryDetail.setUpdateTime(new Date());
										productMicroInventoryDetail.setStock(productMicroInventoryDetail.getStock() + microInventoryInRecord.getQty());
										microInventoryDetailMapper.updateByPrimaryKeySelective(productMicroInventoryDetail);
									} else {
										ProductMicroInventoryDetail newProductMicroInventoryDetail = new ProductMicroInventoryDetail();
										newProductMicroInventoryDetail.setSku(sku);
										newProductMicroInventoryDetail.setAccount(account);
										newProductMicroInventoryDetail.setStock(inventoryChangeHistory.getNum());
										newProductMicroInventoryDetail.setExpirationDate(localDate.toDate());
										newProductMicroInventoryDetail.setIsGift(inventoryChangeHistory.getIsgift() == true ? (short) 1 : (short) 0);
										newProductMicroInventoryDetail.setPurchasePrice(microInventoryInRecord.getPurchasePrice());
										newProductMicroInventoryDetail.setCapfee(microInventoryInRecord.getCapfee());
										newProductMicroInventoryDetail.setWarehouseId(microGoodsInventory.getWarehouseId());
										newProductMicroInventoryDetail.setWarehouseName(microGoodsInventory.getWarehouseName());
										newProductMicroInventoryDetail.setProductTitle(microGoodsInventory.getProductTitle());
										Date now = new Date();
										newProductMicroInventoryDetail.setCreateTime(now);
										newProductMicroInventoryDetail.setUpdateTime(now);
										microInventoryDetailMapper.insertSelective(newProductMicroInventoryDetail);
									}


									ProductMicroInventoryTotal microInventoryTotalParam = new ProductMicroInventoryTotal();
									microInventoryTotalParam.setSku(sku);
									microInventoryTotalParam.setAccount(account);
									microInventoryTotalParam.setWarehouseId(microGoodsInventory.getWarehouseId());
									ProductMicroInventoryTotal inventoryTotalResult = productMicroInventoryTotalMapper.selectByParam(microInventoryTotalParam);
									if(inventoryTotalResult != null){
										inventoryTotalResult.setStock(inventoryTotalResult.getStock()+inventoryChangeHistory.getNum());
										inventoryTotalResult.setUpdateTime(new Date());
										productMicroInventoryTotalMapper.updateByPrimaryKeySelective(inventoryTotalResult);
									}else{
										microInventoryTotalParam.setProductTitle(microGoodsInventory.getProductTitle());
										microInventoryTotalParam.setStock(reduceNum);
										microInventoryTotalParam.setWarehouseName(microGoodsInventory.getWarehouseName());
										microInventoryTotalParam.setCategoryId(microGoodsInventory.getProductCategory());
										microInventoryTotalParam.setCategoryName(microGoodsInventory.getProductCategoryName());
										microInventoryTotalParam.setCreateTime(new Date());
										productMicroInventoryTotalMapper.insertSelective(microInventoryTotalParam);
									}



									int qty = inventoryChangeHistory.getNum();
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
											inventoryDeductRecord.setWarehouseName(productInventoryDetail.getWarehouseName());
											inventoryDeductRecord.setExpirationDate(productInventoryDetail.getExpirationDate());
											inventoryDeductRecord.setOrderNo(inventoryChangeHistory.getOrderNo());
											inventoryDeductRecord.setQty(qty);
											inventoryDeductRecord.setCreateTime(new Date());
											orderInventoryDeductRecordMapper.insertSelective(inventoryDeductRecord);

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
											OrderInventoryDeductRecord inventoryDeductRecord=new OrderInventoryDeductRecord();
											inventoryDeductRecord.setSku(productInventoryDetail.getSku());
											inventoryDeductRecord.setWarehouseId(productInventoryDetail.getWarehouseId());
											inventoryDeductRecord.setWarehouseName(productInventoryDetail.getWarehouseName());
											inventoryDeductRecord.setExpirationDate(productInventoryDetail.getExpirationDate());
											inventoryDeductRecord.setOrderNo(inventoryChangeHistory.getOrderNo());
											inventoryDeductRecord.setQty(inventoryDetailStockNum);
											inventoryDeductRecord.setCreateTime(new Date());
											orderInventoryDeductRecordMapper.insertSelective(inventoryDeductRecord);
											qty -= inventoryDetailStockNum;
										}
									}
								}
							}
						}
					}
				} else {
					Logger.info(">>>>>>>>>>>>>>>asyncMicroInventory 异常数据(没有对应的库存变化记录)：sku:{}, stock:{}, account:{}",microGoodsInventory.getSku(), microGoodsInventory.getAvaliableStock(), microGoodsInventory.getMwarehouseName());
					failDetail.add("account:" + account + " sku:" + sku + " stock:" + stock);
				}
			}
			Logger.info(">>>>>>>>>>>>>>>asyncMicroInventory 要同步的数据总数：{},成功数：{}, 失败数:{},失败详细：{},因未同步库存而跳过的SKU数:{}，跳过详细:{}", microGoodsInventories.size(), successNum,failDetail.size(),Json.toJson(failDetail).toString(),unsyncedStockDetail.size(),Json.toJson(unsyncedStockDetail).toString());
		}
	}

	@Override
	public PurchaseOrderResult returnProductLockMicroInventory(ReturnProductData returnParam) {
		PurchaseOrderResult result=new PurchaseOrderResult();
		if(returnParam==null){
			result.setResult(false);
			result.setMsg("退货信息不正确！");
			return result;
		}
		//查询该账户sku所对应的入仓记录是否存在有效的sku锁库记录
		ProductMicroInventoryOrderLock reqParam=new ProductMicroInventoryOrderLock();
		reqParam.setAccount(returnParam.getAccount());;
		reqParam.setSku(returnParam.getSku());
		reqParam.setMicroInRecordId(returnParam.getInRecordId());
		reqParam.setIsEffective((short) -1);
		List<ProductMicroInventoryOrderLock> resultLockRecords = productMicroInventoryOrderLockMapper.selectListsByParams(reqParam);
		int lockNum=0;
		if(resultLockRecords!=null&&resultLockRecords.size()>0){
			for (ProductMicroInventoryOrderLock lockRecord : resultLockRecords) {
				lockNum+=lockRecord.getStockLocked();
			}
		}
		//查询退货信息对应的入库记录
		ProductMicroInventoryInRecord inRecordParam=new ProductMicroInventoryInRecord();
		inRecordParam.setId(returnParam.getInRecordId());
		inRecordParam.setAccount(returnParam.getAccount());
		inRecordParam.setSku(returnParam.getSku());
		inRecordParam.setWarehouseId(returnParam.getWarehouseId());
		inRecordParam.setOrderNo(returnParam.getPurchaseOrderNo());
		List<ProductMicroInventoryInRecord> resultDatas = productMicroInventoryInRecordMapper.selectMicroInRecordListForRelease(inRecordParam);
		if(resultDatas==null || resultDatas.size()!=1){
			result.setResult(false);
			result.setMsg("缺少退货信息！");
			return result;
		}
		ProductMicroInventoryInRecord InRecord = resultDatas.get(0);
		if(returnParam.getReturnQty()>InRecord.getResidueNum()-lockNum){
			result.setResult(false);
			result.setMsg("该退货信息库存数量不足，请检微仓查库存信息！");
			return result;
		}
		//生产退货锁库记录
		this.generateProductMicroInventoryOrderLock(InRecord.getWarehouseId(), InRecord.getWarehouseName(), returnParam.getReturnOrderNo(), 
											returnParam.getSku(), returnParam.getReturnQty(), (short) -1, InRecord.getAccount(), InRecord.getId(), InRecord.getPurchasePrice(),
											InRecord.getCapfee(), InRecord.getExpirationDate(), InRecord.getIsGift());
		//更新微仓明细锁库数量字段
		ProductMicroInventoryDetail microDetialParam=new ProductMicroInventoryDetail();
		microDetialParam.setSku(InRecord.getSku());
		microDetialParam.setAccount(InRecord.getAccount());
		microDetialParam.setCapfee(InRecord.getCapfee());
		microDetialParam.setExpirationDate(InRecord.getExpirationDate());
		microDetialParam.setPurchasePrice(InRecord.getPurchasePrice());
		microDetialParam.setWarehouseId(InRecord.getWarehouseId());
		microDetialParam.setIsGift(InRecord.getIsGift());
		
		ProductMicroInventoryDetail selectByParam = productMicroInventoryDetailMapper.selectByParam(microDetialParam);
		selectByParam.setLockStock(selectByParam.getLockStock()+returnParam.getReturnQty());
		selectByParam.setUpdateTime(new Date());
		productMicroInventoryDetailMapper.updateByPrimaryKeySelective(selectByParam);
		
		result.setResult(true);
		result.setMsg("退货锁定库存成功");
		return result;
	}

	@Override
	public PurchaseOrderResult returnProductSuccess(String returnOrderNo) {
		PurchaseOrderResult result=new PurchaseOrderResult();
		
		ProductMicroInventoryOrderLock lockParam=new ProductMicroInventoryOrderLock();
		lockParam.setOrderNo(returnOrderNo);
		lockParam.setIsEffective((short) -1);
		List<ProductMicroInventoryOrderLock> reusltLockRecords = productMicroInventoryOrderLockMapper.selectListsByParams(lockParam);
		if(reusltLockRecords==null || reusltLockRecords.size()!=1){
			result.setResult(false);
			result.setMsg("请检查退货单号是否正确！");
			return result;
		}
		ProductMicroInventoryOrderLock returnLockRecord = reusltLockRecords.get(0);
		Integer microInRecordId = returnLockRecord.getMicroInRecordId();
		ProductMicroInventoryInRecord microInRecord = productMicroInventoryInRecordMapper.selectByPrimaryKey(microInRecordId);

		//修改微仓库存数量（总仓，明细，入库记录剩余）
		Integer returnLockNum = returnLockRecord.getStockLocked();
		
		ProductMicroInventoryDetail microDetailParam = new ProductMicroInventoryDetail();
		microDetailParam.setAccount(microInRecord.getAccount());
		microDetailParam.setSku(microInRecord.getSku());
		microDetailParam.setExpirationDate(microInRecord.getExpirationDate());
		microDetailParam.setWarehouseId(microInRecord.getWarehouseId());
		microDetailParam.setIsGift(microInRecord.getIsGift());
		microDetailParam.setPurchasePrice(microInRecord.getPurchasePrice());
		microDetailParam.setCapfee(microInRecord.getCapfee());
		ProductMicroInventoryDetail microDetail = microInventoryDetailMapper.selectByParam(microDetailParam);
		
		microDetail.setStock(microDetail.getStock()-returnLockNum);
		microDetail.setLockStock(microDetail.getLockStock()-returnLockNum);
		microInventoryDetailMapper.updateByPrimaryKeySelective(microDetail);
		
		microInRecord.setResidueNum(microInRecord.getResidueNum()-returnLockNum);
		microInRecord.setUpdateTime(new Date());
		productMicroInventoryInRecordMapper.updateByPrimaryKeySelective(microInRecord);
		
		String orderNo =returnLockRecord.getOrderNo() ;//退货单号
		//微仓出仓记录
		this.generateOrderMicroInventoryDeductRecord(microInRecord.getWarehouseId(), microInRecord.getWarehouseName(), orderNo, 
				microInRecord.getSku(), returnLockNum, microInRecord.getExpirationDate(), microInRecord.getAccount(), microInRecord.getPurchasePrice(), microInRecord.getId(), microInRecord.getIsGift());
		
		ProductMicroInventoryTotal microTotalParam=new ProductMicroInventoryTotal();
		microTotalParam.setSku(microInRecord.getSku());
		microTotalParam.setAccount(microInRecord.getAccount());
		microTotalParam.setWarehouseId(microInRecord.getWarehouseId());
		ProductMicroInventoryTotal resultMicoTotal = productMicroInventoryTotalMapper.selectByParam(microTotalParam);
		resultMicoTotal.setStock(resultMicoTotal.getStock()-returnLockNum);
//			resultMicoTotal.setResidueNum(resultMicoTotal.getResidueNum()-returnLockNum);
		resultMicoTotal.setUpdateTime(new Date());
		productMicroInventoryTotalMapper.updateByPrimaryKeySelective(resultMicoTotal);
			//还原云仓
		ProductInventoryDetail inventoryDetailParam = new ProductInventoryDetail();
		inventoryDetailParam.setSku(microInRecord.getSku());
		inventoryDetailParam.setWarehouseId(microInRecord.getWarehouseId());
		inventoryDetailParam.setExpirationDate(microInRecord.getExpirationDate());
		ProductInventoryDetail inventoryDetailResult = productInventoryDetailMapper
				.selectByParam(inventoryDetailParam);
		if(inventoryDetailResult==null){
			//可能因为云仓初始化时删除存在信息导致商品信息丢失，所以逻辑调整为云仓查询不到就新建一个
			inventoryDetailParam.setCreateTime(new Date());
			inventoryDetailParam.setWarehouseName(microInRecord.getWarehouseName());
			inventoryDetailParam.setStock(returnLockNum);
			Logger.info("微仓退货云仓明细查询为空,新建云仓明细信息：{}", inventoryDetailParam.toString());
			productInventoryDetailMapper.insertSelective(inventoryDetailParam);
		}else{
			inventoryDetailResult.setStock(inventoryDetailResult.getStock() + returnLockNum);
			inventoryDetailResult.setUpdateTime(new Date());
			productInventoryDetailMapper.updateByPrimaryKeySelective(inventoryDetailResult);
		}
		
		
		ProductInventoryTotal inventoryTotalParam = new ProductInventoryTotal();
		inventoryTotalParam.setSku(microInRecord.getSku());
		inventoryTotalParam.setWarehouseId(microInRecord.getWarehouseId());
		ProductInventoryTotal inventoryTotalResult = productInventoryTotalMapper
				.selectByParam(inventoryTotalParam);
		if(inventoryTotalResult==null){//
			inventoryTotalParam.setCreateTime(new Date());
			inventoryTotalParam.setProductName(microInRecord.getProductTitle());
			inventoryTotalParam.setStock(returnLockNum);
			//inventoryTotalParam.setResidueNum(returnLockNum);
			productInventoryTotalMapper.insertSelective(inventoryTotalParam);
			//新建一条云仓初始记录
			
			InventorySyncRecord record = new InventorySyncRecord();
			record.setIsActive(1);
			record.setSku(microInRecord.getSku());
			record.setWarehouseId(microInRecord.getWarehouseId());
			record.setLastSyncingTime(new Date());
			record.setOperator("微仓退货查询不到云仓库存新建云仓信息");
			record.setSyncingNum(returnLockNum);
			Logger.info("微仓退货查询不到云仓库存信息，新建一条云仓信息{}", record);
			inventorySyncRecordMapper.insertSelective(record);
		}else{
			inventoryTotalResult.setStock(inventoryTotalResult.getStock() + returnLockNum);
			//inventoryTotal.setResidueNum(inventoryTotal.getResidueNum()+residueNum);
			inventoryTotalResult.setUpdateTime(new Date());
			productInventoryTotalMapper.updateByPrimaryKeySelective(inventoryTotalResult);
		}
		ProductInventoryInRecord inventoryInRecord = new ProductInventoryInRecord();
		inventoryInRecord.setOrderNo(orderNo);
		inventoryInRecord.setExpirationDate(microInRecord.getExpirationDate());
		inventoryInRecord.setSku(microInRecord.getSku());
		inventoryInRecord.setWarehouseId(microInRecord.getWarehouseId());
		inventoryInRecord.setWarehouseName(microInRecord.getWarehouseName());
		inventoryInRecord.setQty(returnLockNum);
		inventoryInRecord.setCreateTime(new Date());
		productInventoryInRecordMapper.insertSelective(inventoryInRecord);
	
		//锁库失效
		returnLockRecord.setIsEffective((short) 0);
		returnLockRecord.setUpdateTime(new Date());
		productMicroInventoryOrderLockMapper.updateByPrimaryKeySelective(returnLockRecord);
		result.setResult(true);
		result.setMsg("微仓库存退货成功");
		return result;
	}

	@Override
	public PurchaseOrderResult updateReturnLockRecordEffective(String returnOrderNo) {
		PurchaseOrderResult result=new PurchaseOrderResult();
		
		ProductMicroInventoryOrderLock lockParam=new ProductMicroInventoryOrderLock();
		lockParam.setOrderNo(returnOrderNo);
		lockParam.setIsEffective((short) -1);
		List<ProductMicroInventoryOrderLock> reusltLockRecords = productMicroInventoryOrderLockMapper.selectListsByParams(lockParam);
		if(reusltLockRecords==null || reusltLockRecords.size()!=1){
			result.setResult(false);
			result.setMsg("请检查退货单号是否正确！");
			return result;
		}
		ProductMicroInventoryOrderLock returnLockRecord = reusltLockRecords.get(0);
		returnLockRecord.setIsEffective((short) 0);
		returnLockRecord.setUpdateTime(new Date());
		productMicroInventoryOrderLockMapper.updateByPrimaryKeySelective(returnLockRecord);
		
		ProductMicroInventoryInRecord InRecord = productMicroInventoryInRecordMapper.selectByPrimaryKey(returnLockRecord.getMicroInRecordId());
		
		//更新微仓明细表中LockStock字段
		ProductMicroInventoryDetail microDetialParam=new ProductMicroInventoryDetail();
		microDetialParam.setSku(InRecord.getSku());
		microDetialParam.setAccount(InRecord.getAccount());
		microDetialParam.setCapfee(InRecord.getCapfee());
		microDetialParam.setExpirationDate(InRecord.getExpirationDate());
		microDetialParam.setPurchasePrice(InRecord.getPurchasePrice());
		microDetialParam.setWarehouseId(InRecord.getWarehouseId());
		microDetialParam.setIsGift(InRecord.getIsGift());
		
		ProductMicroInventoryDetail selectByParam = productMicroInventoryDetailMapper.selectByParam(microDetialParam);
		selectByParam.setLockStock(selectByParam.getLockStock()-returnLockRecord.getStockLocked());
		selectByParam.setUpdateTime(new Date());
		productMicroInventoryDetailMapper.updateByPrimaryKeySelective(selectByParam);
		
		result.setResult(true);
		result.setMsg("释放退货锁库成功！");
		return result;
	}

	@Override
	public List<ProductMicroInventoryDetail> selectMicroDetailByParams(ProductMicroInventoryDetailSearchDto parms) {
		
		List<ProductMicroInventoryDetail> microDetailList = productMicroInventoryDetailMapper.selectByParams(parms);
		if(microDetailList!=null && microDetailList.size()>0){
			Iterator<ProductMicroInventoryDetail> iterator = microDetailList.iterator();
			while(iterator.hasNext()){
				ProductMicroInventoryDetail microDetial = iterator.next();
				if(microDetial.getStock().intValue()==microDetial.getLockStock().intValue()){
					iterator.remove();
				}else{
					microDetial.setStock(microDetial.getStock()-microDetial.getLockStock());
				}
			}
		}
		return microDetailList;
	}

	@Override
	public List<ProductMicroInventoryDetail> selectMicroDetailBySkuAndWareId(
			ProductMicroInventoryDetailSearchDto reqParam) {
		List<ProductMicroInventoryDetail> microDetailList=Lists.newArrayList();
		List<ProductMicroInventoryDetail> skuAndWarehouseIds = reqParam.getSkuAndWarehouseIds();
		for (ProductMicroInventoryDetail microDetailParam : skuAndWarehouseIds) {
			reqParam.setSkus(Lists.newArrayList(microDetailParam.getSku()));
			reqParam.setWarehouseId(microDetailParam.getWarehouseId());
			microDetailParam.setAccount(reqParam.getAccount());
			//获取所有过期日期
			List<Date> expirdateLists = productMicroInventoryDetailMapper.getMicroDetailExpirdateDates(microDetailParam);
			if(expirdateLists==null||expirdateLists.size()<=0){
				continue;
			}
			
			for(Date expirdateDate: expirdateLists){
				ProductMicroInventoryDetail resultMicroDetail=new ProductMicroInventoryDetail();
				resultMicroDetail.setSku(microDetailParam.getSku());
				resultMicroDetail.setAccount(reqParam.getAccount());
				resultMicroDetail.setWarehouseId(microDetailParam.getWarehouseId());
				
				resultMicroDetail.setExpirationDate(expirdateDate);
				//针对每个过期日期获取库存信息
				ProductMicroInventoryDetail microDetialStockInfoResult = productMicroInventoryDetailMapper.selectMicroDetailStockByExpirdate(resultMicroDetail);
				resultMicroDetail.setExpirationDate(expirdateDate);
				resultMicroDetail.setStock(microDetialStockInfoResult.getStock());
				resultMicroDetail.setLockStock(microDetialStockInfoResult.getLockStock());
				microDetailList.add(resultMicroDetail);
			}
		}
		//移除锁定量大于等于库存的
		if(microDetailList!=null && microDetailList.size()>0){
			Iterator<ProductMicroInventoryDetail> iterator = microDetailList.iterator();
			while(iterator.hasNext()){
				ProductMicroInventoryDetail microDetial = iterator.next();
				if(microDetial.getStock().intValue()==microDetial.getLockStock().intValue()){
					iterator.remove();
				}else{
					microDetial.setStock(microDetial.getStock()-microDetial.getLockStock());
				}
			}
		}
		return microDetailList;
	}

	@Override
	public List<ProductMicroInventoryInRecord> getMicroInventoryInRecordByParam(ProductMicroInventoryInRecord microInRecordParam) {
		List<ProductMicroInventoryInRecord> microInRecordsLists = productMicroInventoryInRecordMapper.selectMicroInRecordListByParam(microInRecordParam);
		return microInRecordsLists;
	}
	
}
